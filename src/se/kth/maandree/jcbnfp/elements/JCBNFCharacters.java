/**
 * jcbnfp — A parser for JCBNF (Jacky's Compilable BNF)
 * 
 * Copyright (C) 2012  Mattias Andrée <maandree@kth.se>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */
package se.kth.maandree.jcbnfp.elements;
import se.kth.maandree.jcbnfp.*;

import java.util.*;


/**
 * JCBNF grammar element: characters
 * 
 * @author  Mattias Andrée, <a href="mailto:maandree@kth.se">maandree@kth.se</a>
 */
public abstract class JCBNFCharacters implements GrammarElement
{
    /**
     * All exception to the sets definition
     */
    public final Vector<JCBNFCharacters> exceptions = new Vector<JCBNFCharacters>();
    
    
    
    /**
     * Tests whether a character is contains by the set
     * 
     * @param   character  The character
     * @return             Whether a character is contains by the set
     */
    public boolean contains(final int character)
    {
	for (final JCBNFCharacters exception : this.exceptions)
	    if (exception.contains(character))
		return false;
	
	return true;
    }
    
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void printGrammar(final String indent)
    {
	for (final JCBNFCharacters exception : this.exceptions)
	    exception.printGrammar(indent + "  ");
    }
    
     
    /**
     * {@inheritDoc}
     */
    public String toString()
    {
	final StringBuilder rc = new StringBuilder();
	for (final JCBNFCharacters exception : this.exceptions)
	{
	    rc.append(" ^");
	    rc.append(exception.toString());
	}
	return rc.toString();
    }
    
    
    /**
     * JCBNF grammar element: atomary character
     * 
     * @author  Mattias Andrée, <a href="mailto:maandree@kth.se">maandree@kth.se</a>
     */
    public static class JCBNFCharacter extends JCBNFCharacters
    {
	/**
	 * Constructor
	 * 
	 * @param  character  The character
	 */
	public JCBNFCharacter(final int character)
	{
	    this.character = character;
	}
	
	
	
	/**
	 * The character
	 */
	private final int character;
	
	
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean contains(final int character)
	{
	    boolean rc = this.character == character;
	    
	    return rc ? super.contains(character) : false;
	}
    
        
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void printGrammar(final String indent)
	{
	    System.out.print(indent + '\'');
	    try
	    {
		System.out.write(Escaper.escape(new int[] {this.character}));
	    }
	    catch (final Throwable err)
	    {
		//Will not happen
	    }
	    System.out.println('\'');
	    super.printGrammar(indent);
	}
	
	
	/**
	 * {@inheritDoc}
	 */
	public String toString()
	{
	    try
	    {
		final String chr = new String(Escaper.escape(new int[] {this.character}), "UTF-8");
		return (chr.charAt(0) == '\\' ? chr : ("'" + chr + "'")) + super.toString();
	    }
	    catch (final java.io.UnsupportedEncodingException err)
	    {
		throw new Error(err);
	    }
	}
    }
    
    /**
     * JCBNF grammar element: character class
     * 
     * @author  Mattias Andrée, <a href="mailto:maandree@kth.se">maandree@kth.se</a>
     */
    public static class JCBNFCharacterClass extends JCBNFCharacters
    {
	/**
	 * Constructor
	 * 
	 * @param  set  The character class
	 */
	public JCBNFCharacterClass(final Set set)
	{
	    this.set = set;
	}
	
	
	
	/**
	 * The character class
	 */
	private final Set set;
	
	
	
	/**
	 * Character class enum
	 * 
	 * @author  Mattias Andrée, <a href="mailto:maandree@kth.se">maandree@kth.se</a>
	 */
	public static enum Set
	{
	    /**
	     * Character class: any character (universe)
	     */
	    ANY
	    {
		/**
		 * {@inheritDoc}
		 */
		public boolean contains(final int character)
		{
		    return true;
		}
	    },
	    
	    /**
	     * Character class: subscript digit
	     */
	    SUB
	    {
		/**
		 * {@inheritDoc}
		 */
		public boolean contains(final int character)
		{
		    return (0x2080 <= character) && (character <= 0x02089);
	        }
	    },
	   
	    /**
	     * Character class: superscript digit
	     */ 
	    SUP
	    {
		/**
		 * {@inheritDoc}
		 */
		public boolean contains(final int character)
		{
		    return (character == 0x2070) || (character == 0x00B9) ||
			   (character == 0x00B2) || (character == 0x00B3) ||
			   ((0x2070 <= character) && (character <= 0x2079));
		}
	    },
	   
	    /**
	     * Character class: letter
	     */ 
	    LETTER
	    {
		/**
		 * {@inheritDoc}
		 */
		public boolean contains(final int character)
		{
		    //Selected among [Letter] &a from http://www.unicode.org/reports/tr49/Categories.txt
		    //No notice have been taken to unassigned characters
		    
		    //The use of the letters should decrease somewhat with ordinal, most significantly at the beginning,
		    //so it is better to do a linear search than a binary or interpolation search.
		    
		    final int[] groups = {0x0041, 0x005A,  0x0061, 0x007A,  0x00C0, 0x00D6,  0x00D8, 0x00F6,  0x00F8, 0x02C6,  0x02C8, 0x02D7,
		                          0x02DE, 0x034E,  0x0350, 0x0373,  0x0376, 0x0377,  0x0386, 0x0386,  0x0388, 0x03FC,  0x0400, 0x0481,
		                          0x0483, 0x0559,  0x0561, 0x0587,  0x0591, 0x05BD,  0x05BF, 0x05BF,  0x05C1, 0x05C2,  0x05C4, 0x05C5,
		                          0x05C7, 0x05F2,  0x0610, 0x061A,  0x0620, 0x063F,  0x0641, 0x065F,  0x066E, 0x06D3,  0x06D3, 0x06EF,
		                          0x06FA, 0x06FC,  0x06FF, 0x06FF,  0x0710, 0x07B1,  0x07CA, 0x07F5,  0x0800, 0x082D,  0x0840, 0x085B,
		                          0x0904, 0x094C,  0x094E, 0x094F,  0x0951, 0x0963,  0x0972, 0x097F,  0x0985, 0x09CC,  0x09CE, 0x09E3,
		                          0x09F0, 0x09F1,  0x0A05, 0x0A4C,  0x0A59, 0x0A5E,  0x0A72, 0x0A73,  0x0A85, 0x0AE3,  0x0B05, 0x0B63,  
					  0x0B71, 0x0B71,  0x0B83, 0x0BCD,  0x0BD7, 0x0BD7,  0x0C05, 0x0C63,  0x0C85, 0x0CE3,  0x0CF1, 0x0CF2,
		                          0x0D05, 0x0D63,  0x0D7A, 0x0D7F,  0x0D85, 0x0DF3,  0x0E01, 0x0E2E,  0x0E30, 0x0E3A,  0x0E40, 0x0E4B,
		                          0x0E81, 0x0EAE,  0x0EB0, 0x0ECB,  0x0EDC, 0x0EDD,  0x0F39, 0x0F39,  0x0F40, 0x0F7D,  0x0F80, 0x0F81,
		                          0x0F84, 0x0F85,  0x0F88, 0x0FBC,  0x0FC0, 0x0FC3,  0x1000, 0x1035,  0x1037, 0x1037,  0x1039, 0x103F,
		                          0x1050, 0x108F,  0x109A, 0x109D,  0x10A0, 0x10FA,  0x10FC, 0x10FC,  0x1100, 0x135F,  0x1380, 0x13F4,
		                          0x1401, 0x166C,  0x166F, 0x167F,  0x1681, 0x169A,  0x16A0, 0x16EA,  0x1700, 0x1734,  0x1740, 0x17C5,
		                          0x17C9, 0x17D2,  0x17DC, 0x17DD,  0x1820, 0x1877,  0x1887, 0x18A8,  0x18AA, 0x193B,  0x1950, 0x19C9,
		                          0x1A00, 0x1A1B,  0x1A20, 0x1A7F,  0x1B05, 0x1B4B,  0x1B83, 0x1BAF,  0x1BC0, 0x1BF3,  0x1C00, 0x1C37,
		                          0x1C4D, 0x1C4F,  0x1C5A, 0x1C7D,  0x1CD0, 0x1FFE,  0x2071, 0x2071,  0x207F, 0x207F,  0x2090, 0x209C,
		                          0x20D0, 0x20F0,  0x2102, 0x2102,  0x2107, 0x2107,  0x210A, 0x2113,  0x2115, 0x2115,  0x2118, 0x211D,
					  0x2124, 0x2124,  0x2126, 0x2126,  0x2128, 0x2128,  0x212A, 0x212D,  0x212F, 0x213F,  0x2141, 0x2149,
					  0x214E, 0x214E,  0x249C, 0x24E9,  0x2C00, 0x2CE3,  0x2CEB, 0x2CF1,  0x2D00, 0x2D6F,  0x2D80, 0x2DFF,
		                          0x2E80, 0x2FD5,  0x302A, 0x302F,  0x3031, 0x3035,  0x303C, 0x303C,  0x3041, 0x3247,  0x3260, 0x327E,
		                          0x3280, 0x32B0,  0x32C0, 0x3357,  0x337B, 0x337F,  0x4DC0, 0x9FCB,  0xA000, 0xA4FD,  0xA500, 0xA60C,
		                          0xA610, 0xA612,  0xA640, 0xA8C4,  0xA8E0, 0xA8F7,  0xA8FB, 0xA8FB,  0xA90A, 0xA92D,  0xA930, 0xA953,
		                          0xA960, 0xA97C,  0xA984, 0xA9C0,  0xA9CF, 0xA9CF,  0xAA00, 0xAA4D,  0xAA60, 0xAA73,  0xAA7A, 0xAAC2,
		                          0xAADD, 0xAADD,  0xAB01, 0xABEA,  0xAC00, 0xD7A3,  0xD7B0, 0xD7FB,  0xF900, 0xFD3D,  0xFD50, 0xFDFB,
		                          0xFDFD, 0xFDFD,  0xFE20, 0xFE26,  0xFE70, 0xFEFC,  0xFF21, 0xFF3A,  0xFF41, 0xFF5A,  0xFF66, 0xFFDC,
		                          
		                          0x10000, 0x100FA,  0x101D0, 0x1039D,  0x103A0, 0x103C3,  0x10400, 0x1049D,  0x10800, 0x10855,
		                          0x10900, 0x10915,  0x10920, 0x10939,  0x10A00, 0x10A3F,  0x10A60, 0x10A7C,  0x10B00, 0x10B35,
		                          0x10B40, 0x10B55,  0x10B60, 0x10B72,  0x10C00, 0x10C48,  0x11003, 0x11046,  0x11083, 0x110BA, 
					  0x13000, 0x1342E,  0x16800, 0x16A38,  0x1B000, 0x1B001,  0x1D300, 0x1D356,  0x1D400, 0x1D6C0,
		                          0x1D6C2, 0x1D6DA,  0x1D6DC, 0x1D6FA,  0x1D6FC, 0x1D714,  0x1D716, 0x1D734,  0x1D736, 0x1D74E,
		                          0x1D750, 0x1D76E,  0x1D770, 0x1D770,  0x1D78A, 0x1D7A8,  0x1D7AA, 0x1D7C2,  0x1D7C4, 0x1D7CB,
		                          0x1F110, 0x1F4FC,  0x20000, 0x2B81D,  0x2F800, 0x2FA1D,  };
		    
		    for (int i = 0, n = groups.length; i < n; i += 2)
		    {
			if (character < groups[i])
			    return false;
			
			if (character <= groups[i + 1])
			    return true;
		    }
		    
		    return false;
		}
	    },
		
	    /**
	     * Character class: letter-like
	     */ 
	    LETTEROID
	    {
		/**
		 * {@inheritDoc}
		 */
		public boolean contains(final int character)
		{
		    final int[] groups = {0x0024, 0x0024,  0x00A2, 0x00A3,  0x00A5, 0x00A5,  0x00A9, 0x00A9,  0x00AE, 0x00AE,  0x00B0, 0x00B0,
		                          0x00B5, 0x00B5,  0x00BA, 0x00BA,  0x060B, 0x060B,  0x09F2, 0x09F3,  0x09FB, 0x09FB,  0x0BF3, 0x0BFA,
		                          0x0D79, 0x0D79,  0x0E3F, 0x0E3F,  0x0F15, 0x0F1F,  0x0F3E, 0x0F3E,  0x0FCE, 0x0FCF,  0x17DB, 0x17DB,
		                          0x1946, 0x194F,  0x19E0, 0x19FF,  0x20A0, 0x20B9,  0x2100, 0x2101,  0x2103, 0x2106,  0x2108, 0x2109,
		                          0x2114, 0x2114,  0x2116, 0x2117,  0x211E, 0x2123,  0x2125, 0x2125,  0x2127, 0x2127,  0x2129, 0x2129,
		                          0x212E, 0x212E,  0x214A, 0x214A,  0x214C, 0x214C,  0x214F, 0x214F,  0x2160, 0x2188,  0x2400, 0x2426,
		                          0x3358, 0x337A,  0x3380, 0x33FF,  0xFDFC, 0xFDFC,  0xFF04, 0xFF04,  0xFFE0, 0xFFE1,  0xFFE5, 0xFFE6,
		                          
		                          0x10137, 0x1013F,  0x10140, 0x10174,  0x10179, 0x10189,  0x10190, 0x1019B,  0x12000, 0x1236E,
		                          0x1D6C1, 0x1D6C1,  0x1D6DB, 0x1D6DB,  0x1D715, 0x1D715,  0x1D735, 0x1D735,  0x1D74F, 0x1D74F,
		                          0x1D76F, 0x1D76F,  0x1D789, 0x1D789,  0x1D789, 0x1D789,  0x1D7A9, 0x1D7A9,  0x1D7C3, 0x1D7C3,
		                          0x1D7CE, 0x1D7FF,  0x1F100, 0x1F10A,  };
			
		    for (int i = 0, n = groups.length; i < n; i += 2)
		    {
			if (character < groups[i])
			    return false;
			
			if (character <= groups[i + 1])
			    return true;
		    }
		    
		    return false;
		}
	    },
		
	    /**
	     * Character class: digits and numbers, but not fractions
	     */ 
	    DIGIT
	    {
		/**
		 * {@inheritDoc}
		 */
		public boolean contains(final int character)
		{
		    final int[] groups = {0x0030, 0x0039,  0x00B2, 0x00B3,  0x00B9, 0x00B9,  0x0482, 0x0482,  0x0488, 0x0489,  0x0660, 0x0669,
		                          0x06F0, 0x06F9,  0x07C0, 0x07C9,  0x0966, 0x096F,  0x09E6, 0x09EF,  0x0A66, 0x0A6F,  0x0AE6, 0x0AEF,
		                          0x0B66, 0x0B6F,  0x0BE6, 0x0BF2,  0x0C66, 0x0C6F,  0x0CE6, 0x0CEF,  0x0D66, 0x0D72,  0x0E50, 0x0E59,
		                          0x0ED0, 0x0ED9,  0x0F20, 0x0F33,  0x1040, 0x1049,  0x1090, 0x1099,  0x1369, 0x137C,  0x17E0, 0x17E9,
		                          0x1810, 0x1819,  0x19D0, 0x19DA,  0x1A80, 0x1A89,  0x1A90, 0x1A99,  0x1B50, 0x1B59,  0x1BB0, 0x1BB9,
		                          0x1C40, 0x1C49,  0x1C50, 0x1C59,  0x2070, 0x2070,  0x2074, 0x2079,  0x2080, 0x2089,  0x2460, 0x249B,
		                          0x24EA, 0x24FF,  0x2776, 0x2793,  0x3007, 0x3007,  0x3021, 0x3029,  0x3038, 0x303A,  0x3248, 0x324F,
		                          0x3251, 0x325F,  0x32B1, 0x32BF,  0xA620, 0xA629,  0xA620, 0xA629,  0xA8D0, 0xA8D9,  0xA900, 0xA909,
		                          0xA9D0, 0xA9D9,  0xAA50, 0xAA59,  0xABF0, 0xABF9,  0xFF10, 0xFF19,  
		    
		                          0x10107, 0x10133,  0x1018A, 0x1018A,  0x103D1, 0x103D5,  0x104A0, 0x104A9,  0x10858, 0x1085F,
		                          0x10916, 0x1091B,  0x10A40, 0x10A47,  0x10A7D, 0x10A7E,  0x10B58, 0x10B5F,  0x10B78, 0x10B7F,
					  0x10E60, 0x10E7A,  0x11052, 0x1106F,  0x12400, 0x12459,  0x1D360, 0x1D371,  };
		    
		    for (int i = 0, n = groups.length; i < n; i += 2)
		    {
			if (character < groups[i])
			    return false;
			
			if (character <= groups[i + 1])
			    return true;
		    }
		    
		    return false;
		}
	    },
		
	    ;
	    
	    
	    /**
	     * Tests whether a character is contains by the set
	     * 
	     * @param   character  The character
	     * @return             Whether a character is contains by the set
	     */
	    public boolean contains(final int character)
	    {
		assert false : "Not implemented";
		return true;
	    }
	}
	
	
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean contains(final int character)
	{
	    return this.set.contains(character) ? super.contains(character) : false;
	}
    
        
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void printGrammar(final String indent)
	{
	    System.out.print(indent + "$");
	    switch (this.set)
	    {
		case ANY:        System.out.println("any");  break;
		case SUB:        System.out.println("sub");  break;
		case SUP:        System.out.println("sup");  break;
		case LETTER:     System.out.println("letter");  break;
		case LETTEROID:  System.out.println("letteroid");  break;
		case DIGIT:      System.out.println("digit");  break;
		default:
		    System.out.println("?");
		    break;
	    }
	    super.printGrammar(indent);
	}
	
	
	/**
	 * {@inheritDoc}
	 */
	public String toString()
	{
	    switch (this.set)
	    {
		case ANY:        return "$any" + super.toString();
		case SUB:        return "$sub" + super.toString();
		case SUP:        return "$sup" + super.toString();
		case LETTER:     return "$letter" + super.toString();
		case LETTEROID:  return "$letteroid" + super.toString();
		case DIGIT:      return "$digit" + super.toString();
		default:
		    return "$?" + super.toString();
	    }
	}
	
    }
    

    /**
     * JCBNF grammar element: character group
     * 
     * @author  Mattias Andrée, <a href="mailto:maandree@kth.se">maandree@kth.se</a>
     */
    public static class JCBNFCharacterGroup extends JCBNFCharacters
    {
	/**
	 * Constructor
	 * 
	 * @param  characters  The characters
	 */
	public JCBNFCharacterGroup(final int[] characters)
	{
	    this.characters = characters;
	}
	
	
	
	/**
	 * The characters
	 */
	private final int[] characters;
	
	
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean contains(final int character)
	{
	    boolean rc = false;
	    for (final int c : characters)
		if (c == character)
		{
		    rc = true;
		    break;
		}
	    
	    return rc ? super.contains(character) : false;
	}
    
        
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void printGrammar(final String indent)
	{
	    System.out.print(indent + "@\"");
	    try
	    {
		System.out.write(Escaper.escape(this.characters));
	    }
	    catch (final Throwable err)
	    {
		//Will not happen
	    }
	    System.out.println('"');
	    super.printGrammar(indent);
	}
	
	
	/**
	 * {@inheritDoc}
	 */
	public String toString()
	{
	    return "@\"" + Util.intArrayToString(this.characters) + '"' + super.toString();
	}
    }
    
    /**
     * JCBNF grammar element: character range
     * 
     * @author  Mattias Andrée, <a href="mailto:maandree@kth.se">maandree@kth.se</a>
     */
    public static class JCBNFCharacterRange extends JCBNFCharacters
    {
	/**
	 * Constructor
	 * 
	 * @param  min  The min character
	 * @param  max  The max character
	 */
	public JCBNFCharacterRange(final int min, final int max)
	{
	    this.min = min < max ? min : max;
	    this.max = min > max ? min : max;
	}
	
	
	
	/**
	 * The min character
	 */
	private final int min;
	
	/**
	 * The max character
	 */
	private final int max;
	
	
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean contains(final int character)
	{
	    boolean rc = (this.min <= character) && (character <= this.max);
	    
	    return rc ? super.contains(character) : false;
	}
    
        
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void printGrammar(final String indent)
	{
	    System.out.print(indent + '\'');
	    try
	    {
		System.out.write(Escaper.escape(this.min));
		System.out.print("'..'");
		System.out.write(Escaper.escape(this.max));
	    }
	    catch (final Throwable err)
	    {
		//Will not happen
	    }
	    System.out.println('\'');
	    super.printGrammar(indent);
	}
	
	
	/**
	 * {@inheritDoc}
	 */
	public String toString()
	{
	    return "'" + Util.intArrayToString(this.min) + "'..'" + Util.intArrayToString(this.max) + "'" + super.toString();
	}
    }
    
}


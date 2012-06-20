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
package se.kth.maandree.jcbnfp;


/**
 * JCBNF grammar element: bounded repeation
 * 
 * @author  Mattias Andrée, <a href="mailto:maandree@kth.se">maandree@kth.se</a>
 */
public class JCBNFBoundedRepeation implements GrammarElement
{
    /**
     * Constructor
     * 
     * @param  minCount  The minimum repeation count
     * @param  maxCount  The maximum repeation count
     */
    @SuppressWarnings("hiding")
    public JCBNFBoundedRepeation(final int minCount, final int maxCount)
    {
	this.minCount = minCount;
	this.maxCount = maxCount;
    }
    
    
    
    /**
     * The minimum repeation count
     */
    public final int minCount;
    
    /**
     * The maximum repeation count
     */
    public final int maxCount;
    
    /**
     * The option
     */
    public GrammarElement option = null;
    
    /**
     * The repeation element
     */
    public GrammarElement element = null;
    
    
    
    /**
     * Prints out the element
     * 
     * @param  indent  The current indent
     */
    @Override
    public void printGrammar(final String indent)
    {
	System.out.print(indent);
	System.out.println("{" + this.minCount + ".." + (this.maxCount < 0 ? "." : String.valueOf(this.maxCount)) + "}");
	if (this.option != null)
	{
	    System.out.println(indent + "  OPTION");
	    this.option.printGrammar(indent + "    ");
	    System.out.println(indent + "  REPEAT");
	    this.element.printGrammar(indent + "    ");
	}
	else
	    this.element.printGrammar(indent + "  ");
    }
    
}


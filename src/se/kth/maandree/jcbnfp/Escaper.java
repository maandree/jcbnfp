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
 * Class used for escaping strings do they can be displayed
 * 
 * @author  Mattias Andrée, <a href="mailto:maandree@kth.se">maandree@kth.se</a>
 */
public class Escaper
{
    /**
     * Forbidden constructor
     */
    private Escaper()
    {
	assert false : "You may not create instances of this class.";
    }
    
    
    
    /**
     * Escapes special characters in a string so it may be viewed better
     * 
     * @param   plain  The string to escape
     * @return         The string escaped
     */
    public static String escape(final String plain)
    {
	final char[] cs = new char[plain.length() << 1];
	int ptr = 0;
	
	for (int i = 0, n = plain.length(); i < n; i++)
	{
	    final char c = plain.charAt(i);
	    char cc = c;
	    
	    switch (c)
	    {
		case '\007':  cc = 'a';  break;
		case '\b':    cc = 'b';  break;
		case '\033':  cc = 'e';  break;
		case '\f':    cc = 'f';  break;
		case '\n':    cc = 'n';  break;
		case '\r':    cc = 'r';  break;
		case '\t':    cc = 't';  break;
		case 11:      cc = 'v';  break;
		case '\0':    cc = '0';  break;
	    }
	    
	    if ((cc != c) || (c == '\\') || (c == '\'') || (c == '\"'))
		cs[ptr++] = '\\';
	    cs[ptr++] = cc;
	}
	
	return new String(cs, 0, ptr);
    }


    /**
     * Escapes special characters in a string so it may be viewed better
     * 
     * @param   plain  The string to escape
     * @param   off    The offset of {@code plain}'s actual content
     * @param   len    The length of {@code plain}'s actual content
     * @return         The string escaped
     */
    public static byte[] escape(final int[] plain, final int off, final int len)
    {
	final int[] data = new int[len];
	System.arraycopy(plain, off, data, 0, len);
	return escape(data);
    }
    
    
    /**
     * Escapes special characters in a string so it may be viewed better
     * 
     * @param   plain  The string to escape
     * @return         The string escaped
     */
    public static byte[] escape(final int... plain)
    {
	final int[] buf = new int[6];
	byte[] prc = new byte[plain.length << 2];
	int rcp = 0;

	for (final int c : plain)
	    if (c < 128)
	    {
		int cc = c;
		
		switch ((char)c)
		{
		    case '\007':  cc = 'a';  break;
		    case '\b':    cc = 'b';  break;
		    case '\033':  cc = 'e';  break;
		    case '\f':    cc = 'f';  break;
		    case '\n':    cc = 'n';  break;
		    case '\r':    cc = 'r';  break;
		    case '\t':    cc = 't';  break;
		    case 11:      cc = 'v';  break;
		    case '\0':    cc = '0';  break;
		}
		
		if ((cc != c) || (c == '\\') || (c == '\'') || (c == '\"'))
		{
		    prc[rcp++] = (byte)'\\';
		    if (rcp == prc.length)
		    {
			final byte[] nrc = new byte[prc.length + plain.length];
			System.arraycopy(prc, 0, nrc, 0, rcp);
			prc = nrc;
		    }
		}
		
		prc[rcp++] = (byte)cc;
		if (rcp == prc.length)
		{
		    final byte[] nrc = new byte[prc.length + plain.length];
		    System.arraycopy(prc, 0, nrc, 0, rcp);
		    prc = nrc;
		}
	    }
	    else
	    {
		int ptr = 0;
		int cc = c;
		while (cc >= 0x40)
		{
		    buf[ptr++] = (cc & 0x3F) | 128;
		    cc >>>= 6;
		}
		ptr++;
		buf[ptr - 1] = cc | (((1 << ptr) - 1) << (8 - ptr));
		while (--ptr >= 0)
		{
		    prc[rcp++] = (byte)(buf[ptr]);
		    if (rcp == prc.length)
		    {
			final byte[] nrc = new byte[prc.length + plain.length];
			System.arraycopy(prc, 0, nrc, 0, rcp);
			prc = nrc;
		    }
		}
	    }
	
	final byte[] rc = new byte[rcp];
	System.arraycopy(prc, 0, rc, 0, rcp);
	return rc;
    }

    
}


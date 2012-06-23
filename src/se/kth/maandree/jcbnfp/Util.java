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
 * Small utilities
 * 
 * @author  Mattias Andrée, <a href="mailto:maandree@kth.se">maandree@kth.se</a>
 */
public class Util
{
    /**
     * Forbidden constructor
     */
    private Util()
    {
	assert false : "You may not create instances of this class.";
    }
    
    
    
    /**
     * Creates an UTF-32 integer array from an UTF-16 string
     * 
     * @param   The string
     * @return  The integer array
     */
    public static int[] stringToIntArray(final String string)
    {
	final int[] rcc = new int[string.length()];
	int ptr = 0;
	
	for (int i = 0, n = string.length(); i < n; i++)
	{
	    final char c = string.charAt(i);
	    if ((0xD800 <= c) && (c < 0xDC00))
	    {
		final char cc = string.charAt(++i);
		final int hi = c  & 0x3FF;
		final int lo = cc & 0x3FF;
		rcc[ptr++] = ((hi << 10) | lo) + 0x10000;
	    }
	    else if ((0xDC00 <= c) && (c < 0xE000))
	    {
		final char cc = string.charAt(++i);
		final int lo = c  & 0x3FF;
		final int hi = cc & 0x3FF;
		rcc[ptr++] = ((hi << 10) | lo) + 0x10000;
	    }
	    else
		rcc[ptr++] = c;
	}
	
	final int[] rc = new int[ptr];
	System.arraycopy(rcc, 0, rc, 0, ptr);
	return rc;
    }
}


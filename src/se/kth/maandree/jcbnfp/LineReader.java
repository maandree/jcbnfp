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

import java.io.*;


/**
 * This is the main entry point of the program
 * 
 * @author  Mattias Andrée, <a href="mailto:maandree@kth.se">maandree@kth.se</a>
 */
public class LineReader
{
    /**
     * The default size of the buffer
     */
    private static final int BUF_SIZE = 128;
    
    
    
    //Has default constructor
    
    
    
    /**
     * The index of the current line
     */
    private int lineIndex = 0;
    
    /**
     * The last read character
     */
    private int last = 0;
    
    /**
     * The buffer
     */
    private int[] buf = new int[BUF_SIZE];
    
    /**
     * The pointer in the buffer
     */
    private int ptr;
    
    
    
    /**
     * Gets the index of the last retrieved line
     * 
     * @return  The index of the last retrieved line
     */
    public int getLineIndex()
    {
	return this.lineIndex;
    }
    
    
    /**
     * Gets the next line from an stream
     * 
     * @param   is  The stream to read from
     * @return      The next, non whitespace-only line, <code>null</code> if no line left
     * 
     * @throws  IOException  On I/O exception
     */
    public int[] getNextLine(final InputStream is) throws IOException
    {
	for (int d, c; (d = is.read()) != -1; )
	{
	    if (((c = getNextChar(d, is)) == -1) || ((last == '\r') && (c == '\n')))
	    {
		last = c;
		continue;
	    }
	    
	    if ((last == '\n') || (last == '\r') || (last == '\f'))
		this.lineIndex++;
	    
	    if ((c == '\n') || (c == '\r') || (c == '\f'))
	    {
		int off = 0;
		while ((off < ptr) && ((buf[off] == ' ') || (buf[off] == '\t')))
		    off++;
		
		if (off < ptr)
		{
		    int len = ptr - off;
		    int[] rc = new int[len];
		    System.arraycopy(buf, off, rc, 0, len);
		    ptr = 0;
		    last = c;
		    return rc;
		}
		
		ptr = 0;
	    }
	    else
	    {
		buf[ptr++] = c;
		if (ptr == buf.length)
		{
		    final int[] nbuf = new int[ptr + BUF_SIZE];
		    System.arraycopy(buf, 0, nbuf, 0, ptr);
		    buf = nbuf;
		}
	    }
	    
	    last = c;
	}
	
        if ((last != '\n') && (last != '\r') && (last != '\f'))
	{
	    last = '\n';
	    
	    int off = 0;
	    while ((off < ptr) && ((buf[off] == ' ') || (buf[off] == '\t')))
		off++;
	    
	    if (off < ptr)
	    {
		int len = ptr - off;
		int[] rc = new int[len];
		System.arraycopy(buf, off, rc, 0, len);
		ptr = 0;
		return rc;
	    }
	    
	    ptr = 0;
	}
	
	return null;
    }
    
    
    /**
     * Gets next character from a stream
     *
     * @param   d   An already read byte
     * @param   is  Stream to poll additional bytes from if needed
     * @return      The ordinal value of the character, <code>-1</code> if not a character
     *
     * @throws  IOException  On I/O exception
     */
    private static int getNextChar(final int d, final InputStream is) throws IOException
    {
	if (d == -1)
	    return -1;
	
	int c;
	if ((d & 128) == 0)
	    c = d;
	else
	{
	    int n = 0;
	    int b = d;
	    while ((b & 128) != 0)
	    {
		n++;
		b <<= 1;
	    }
	    if (n == 1)
		return -1; //non-character
	    
	    c = (b & 0xFF) >>> n;
	    
	    for (int i = 1; i < n; i++)
	    {
		int p = is.read();
		if ((p == -1) || ((p >>> 6) != 2))
		    return c;
		c = (c << 6) | (p ^ 128);
	    }
	}
	return c;
    }
    
}


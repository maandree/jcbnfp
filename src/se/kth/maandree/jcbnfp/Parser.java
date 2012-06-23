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

import java.util.*;
import java.io.*;


/**
 * Code parser class using parsed syntax
 * 
 * @author  Mattias Andrée, <a href="mailto:maandree@kth.se">maandree@kth.se</a>
 */
public class Parser
{
    /**
     * Constructor
     * 
     * @param  definitions  Definition map
     * @param  main         The main definition, normally the title of the JCBNF file
     */
    public Parser(final HashMap<String, Definition> definitions, final String main)
    {
	this.definitions = definitions;
	this.main = main;
    }
    
    
    
    /**
     * Definition map
     */
    final HashMap<String, Definition> definitions;
    
    /**
     * The main definition
     */
    private final String main;
    
    
    
    /**
     * Parses a stream and builds a tree of the result
     * 
     * @param   is  The data stream to parse
     * @return      The tree with the result, describing the data
     * 
     * @throws  IOException  On I/O exception
     */
    public ParseTree parse(final InputStream is) throws IOException
    {
	final int BUF_SIZE = 2048;
	final ArrayList<int[]> bufs = new ArrayList<int[]>();
	int[] buf = new int[BUF_SIZE];
	int ptr = 0;
	
	for (int d; (d = is.read()) != -1;)
	{
	    if (d < 0x80)
		buf[ptr++] = d;
	    else if ((d & 0xC0) != 0x80)
	    {
		int n = 0;
		while ((d & 0x80) != 0)
		{
		    n++;
		    d <<= 1;
		}
		d = (d & 255) >> n;
		for (int i = 0; i < n; i++)
		{
		    final int v = is.read();
		    if ((v & 0xC0) != 0x80)
			break;
		    d = (d << 6) | (d & 0x3F);
		}
	    }
	    
	    if (ptr == BUF_SIZE)
	    {
		bufs.add(buf);
		ptr = 0;
		buf = new int[BUF_SIZE];
	    }
	}
	
	final int[] text = new int[bufs.size() * BUF_SIZE + ptr];
	int p = 0;
	for (final int[] b : bufs)
	{
	    System.arraycopy(b, 0, text, p, BUF_SIZE);
	    p += BUF_SIZE;
	}
	bufs.clear();
	System.arraycopy(buf, 0, text, p, ptr);
	
	
	final Definition root = this.definitions.get(this.main);
	final ParseTree tree = new ParseTree(null, root, this.definitions);
	tree.parse(text, 0);
	return tree;
    }
    
}


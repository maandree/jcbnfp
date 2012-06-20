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
 * This is the main entry point of the program
 * 
 * @author   Mattias Andrée, <a href="mailto:maandree@kth.se">maandree@kth.se</a>
 * @version  1.0
 */
public class Program
{
    /**
     * Forbidden constructor
     */
    private Program()
    {
	assert false : "You may not create instances of this class.";
    }
    
    
    
    /**
     * This is the main entry point of the program
     * 
     * @param  args  Command line arguments
     */
    public static void main(final String... args)
    {
	final String jcbnfFile = args[0];
	final String parseFile = args[1];
	
	InputStream pis = null;
	try
	{
	    pis = new BufferedInputStream(new FileInputStream(new File(jcbnfFile)));
	    final HashMap<String, Definition> defs = GrammarParser.parseGrammar(pis);
	    for (final Definition def : defs.values())
	    {
		printGrammar(def);
		System.out.println();
		System.out.println();
	    }
	}
	catch (final SyntaxFileError err)
	{
	    System.err.println("ERROR: " + err.getMessage());
	}
	catch (final RuntimeException err)
	{
	    System.err.print("ERROR: ");
	    err.printStackTrace(System.err);
	}
	catch (final Throwable err)
	{
	    System.err.println("---SYSTEM ERROR---");
	    err.printStackTrace(System.err);
	}
	finally
	{
	    if (pis != null)
		try
		{
		    pis.close();
		}
		catch (final Throwable err)
		{
		    //Ignore
		}
	}
    }
    
    
    
    /**
     * Prints out a definition
     * 
     * @param  definition  The definition
     */
    @SuppressWarnings("unchecked")
    public static void printGrammar(final Definition definition)
    {
	System.out.println("(:: " + definition.name + " ::)");
	
	{
	    final int[] buf = new int[6];
	    final String[] preerr = {"<==", "<--", "w==", "w--", };
	    final ArrayList[] errses = {definition.panics, definition.oopses, definition.uniques, definition.warnings, };
	    
	    for (final ArrayList<int[]> errs : errses)
		for (final int[] err : errs)
		{
		    System.out.print(preerr + " ");
		    for (final int c : err)
			if (c < 128)
			    System.out.write(c);
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
				System.out.write(buf[ptr]);
			}
		    System.out.println();
		}
	}
	
	if (definition.definition != null)
	    definition.definition.printGrammar("::= ");
	
	if (definition.compiles != null)
	    definition.compiles.printGrammar("==> ");
    }
    
}


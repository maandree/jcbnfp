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
 * This is the main entry point of the JCBNF highlighter program
 * 
 * @author   Mattias Andrée, <a href="mailto:maandree@kth.se">maandree@kth.se</a>
 */
public class Highlighter
{
    /**
     * Forbidden constructor
     */
    private Highlighter()
    {
	assert false : "You may not create instances of this class.";
    }
    
    
    
    /**
     * This is the main entry point of the JCBNF highlighter program
     * 
     * @param  args  Command line arguments (unused)
     */
    public static void main(final String... args)
    {
	final PrintStream stderr = System.err;
	final PrintStream stdout = System.out;
	final PrintStream devNull = new PrintStream(new OutputStream() { public void write(int b) { /* do nothing */ } });
	
	final String parseFile = args[0];
	
	InputStream gis = null, fis = null;
	try
	{
	    System.setOut(devNull);
	    System.setErr(devNull);
	    
	    gis = new BufferedInputStream(new FileInputStream(new File("./JCBNF/jcbnf")));
	    final HashMap<String, Definition> defs = GrammarParser.parseGrammar(gis);
	    
	    final Parser parser = new Parser(defs, "jcbnf");
	    fis = new BufferedInputStream(new FileInputStream(new File(parseFile)));
	    final ParseTree tree = parser.parse(fis);
	    
	    System.setOut(stdout);
	    System.setErr(stderr);
	    
	    if (tree == null)
		System.out.println("===### Grammar did not match ###===\n\n");
	    else
		print(tree, parser.data, new ArrayDeque<String>());
	    System.out.flush();
	}
	catch (final SyntaxFileError err)
	{
	    System.setOut(stdout);
	    System.setErr(stderr);
	    System.err.println("ERROR: " + err.getMessage());
	    if (err.getCause() != null)
		err.getCause().printStackTrace(System.err);
	}
	catch (final UndefiniedDefinitionException err)
	{
	    System.setOut(stdout);
	    System.setErr(stderr);
	    System.err.println("ERROR: " + err.getMessage());
	}
	catch (final RuntimeException err)
	{
	    System.setOut(stdout);
	    System.setErr(stderr);
	    System.err.print("ERROR: ");
	    err.printStackTrace(System.err);
	}
	catch (final Throwable err)
	{
	    System.setOut(stdout);
	    System.setErr(stderr);
	    System.err.println("---SYSTEM ERROR---");
	    err.printStackTrace(System.err);
	}
	finally
	{
	    if (gis != null)
		try
		{   gis.close();
		}
		catch (final Throwable err)
		{   //Ignore
		}
	    if (fis != null)
		try
		{   fis.close();
		}
		catch (final Throwable err)
		{   //Ignore
		}
	}
    }
    
    
    public static void print(final ParseTree node, final int[] data, final ArrayDeque<String> colours)
    {
	final String n = node.definition.name;
	
	if      (n.equals("shebang"))   colours.offerLast("\033[36m");
	else if (n.equals("comment"))   colours.offerLast("\033[32m");
	else if (n.equals("name"))      colours.offerLast("\033[33m");
	else if (n.equals("juxta"))     colours.offerLast("\033[34m");
	else if (n.equals("altern"))    colours.offerLast("\033[01m");
	else
	    colours.offerLast("");
	
	System.out.print(colours.peekLast());
	
	{
	    int s = node.intervalStart;
	    for (final ParseTree child : node.children)
	    {
		if (s < child.intervalStart)
		{
		    final int[] dat = new int[child.intervalStart - s];
		    System.arraycopy(data, s, dat, 0, dat.length);
		    System.out.print(Util.intArrayToString(dat));
		    //System.err.println("[" + s + ", " + (s + dat.length - 1) + "]");
		}
		
		s = child.intervalEnd;
		print(child, data, colours);
	    }
	    if (s < node.intervalEnd)
	    {
		final int[] dat = new int[node.intervalEnd - s];
		System.arraycopy(data, s, dat, 0, dat.length);
		System.out.print(Util.intArrayToString(dat));
		//System.err.println("[" + s + ", " + (s + dat.length - 1) + "]");
	    }
	}
	
	System.out.print("\033[0m");
	colours.pollLast();
	if (colours.isEmpty() == false)
	    for (final String c : colours)
		System.out.print(c);
	else
	{
	    System.out.print("\033[1;30m");
	    final int[] dat = new int[data.length - node.intervalEnd];
	    System.arraycopy(data, node.intervalEnd, dat, 0, dat.length);
	    System.out.print(Util.intArrayToString(dat));
	    //System.err.println("(" + node.intervalEnd + ", " + (node.intervalEnd + dat.length - 1) + ")");
	    System.out.print("\033[21;39m");
	}
    }
    
}


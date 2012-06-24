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
	final String main      = args[2];
	
	InputStream gis = null, fis = null;
	try
	{
	    System.out.println("--- Parsing Syntax ---\n\n");
	    
	    gis = new BufferedInputStream(new FileInputStream(new File(jcbnfFile)));
	    final HashMap<String, Definition> defs = GrammarParser.parseGrammar(gis);
	    for (final Definition def : defs.values())
	    {
		printGrammar(def);
		System.out.println("\n");
	    }
	    
	    System.out.println("--- Parsing code ---\n\n");
	    
	    final Parser parser = new Parser(defs, main);
	    fis = new BufferedInputStream(new FileInputStream(new File(parseFile)));
	    final ParseTree tree = parser.parse(fis);
	    System.out.println("\n");
	    
	    System.out.println("--- Parsed code ---\n\n");
	    printTree(tree, parser.data);
	    System.out.println("\n");
	}
	catch (final SyntaxFileError err)
	{
	    System.err.println("ERROR: " + err.getMessage());
	    if (err.getCause() != null)
		err.getCause().printStackTrace(System.err);
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
    
    
    /**
     * Prints out a parsed tree
     * 
     * @param  tree  The tree
     * @param  data  The parsed data
     * 
     * @throws  Exception  Yay!
     */
    public static void printTree(final ParseTree tree, final int[] data) throws Exception
    {
	final ArrayDeque<ParseTree> nodes = new ArrayDeque<ParseTree>();
	final ArrayDeque<String> indents = new ArrayDeque<String>();
	
	nodes.add(tree);
	indents.add("");
	
	ParseTree node;
	while ((node = nodes.pollLast()) != null)
	{
	    String indent = indents.pollLast();
	    
	    System.out.print(indent);
	    System.out.print(node.definition.definition);
	    System.out.print(" :: (");
	    System.out.print(node.intervalStart);
	    System.out.print(", ");
	    System.out.print(node.intervalEnd - node.intervalStart);
	    System.out.print(", ");
	    System.out.print(node.intervalEnd);
	    System.out.print(")  \"\033[32m");
	    System.out.write(Escaper.escape(data, node.intervalStart, node.intervalEnd - node.intervalStart));
	    System.out.println("\033[39m\"\033[35m");
	    System.out.println(indent + "(:: " + node.definition.name + " ::)");
	    node.definition.definition.printGrammar(indent + "::= ");
	    System.out.print("\033[39m");
	    
	    indent += "    ";
	    
	    for (int i = node.children.size() - 1; i >= 0; i--)
	    {
		nodes.offerLast(node.children.get(i));
		indents.offerLast(indent);
	    }
	}
    }
    
    
    /**
     * Prints out a definition
     * 
     * @param  definition  The definition
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static void printGrammar(final Definition definition)
    {
	System.err.print(definition.toString());
	
	System.out.println("(:: " + definition.name + " ::)");
	
	if (definition.definition != null)
	    definition.definition.printGrammar("::= ");
	
	if (definition.compiles != null)
	    definition.compiles.printGrammar("==> ");
    }
    
}


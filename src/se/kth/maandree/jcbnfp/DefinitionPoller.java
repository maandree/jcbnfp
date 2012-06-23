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
 * Class for polling definitions from a stream
 * 
 * @author  Mattias Andrée, <a href="mailto:maandree@kth.se">maandree@kth.se</a>
 */
class DefinitionPoller
{
    //Has default constructor
    
    
    
    /**
     * A map from line index to line strings
     */
    public final HashMap<Integer, String> lineMap = new HashMap<Integer, String>();
    
    /**
     * The object's line reader
     */
    private final LineReader lr = new LineReader();
    
    /**
     * The name of the definition
     */
    private int[] name = null;
    
    /**
     * The line with the name of the definition
     */
    private int nameLine = 0;
    
    /**
     * The parts of the definition pattern
     */
    private ArrayList<int[]> definition = new ArrayList<int[]>();
    
    /**
     * The lines with the parts of the definition pattern
     */
    private ArrayList<Integer> definitionLines = new ArrayList<Integer>();
    
    /**
     * The parts of the code to what the definition compiles
     */
    private ArrayList<int[]> compiles = new ArrayList<int[]>();
    
    /**
     * The lines with the parts of the code to what the definition compiles
     */
    private ArrayList<Integer> compilesLines = new ArrayList<Integer>();
    
    /**
     * All oopses, non-stopping errors
     */
    private ArrayList<int[]> oopses = new ArrayList<int[]>();
    
    /**
     * The lines with the oopses, non-stopping errors
     */
    private ArrayList<Integer> oopsLines = new ArrayList<Integer>();
    
    /**
     * All panics, stopping errors
     */
    private ArrayList<int[]> panics = new ArrayList<int[]>();
    
    /**
     * The lines with the panics, stopping errors
     */
    private ArrayList<Integer> panicLines = new ArrayList<Integer>();
    
    /**
     * All warnings
     */
    private ArrayList<int[]> warnings = new ArrayList<int[]>();
    
    /**
     * The lines with the warnings
     */
    private ArrayList<Integer> warningLines = new ArrayList<Integer>();
    
    /**
     * All unique warnings
     */
    private ArrayList<int[]> uniques = new ArrayList<int[]>();
    
    /**
     * The lines with the unique warnings
     */
    private ArrayList<Integer> uniqueLines = new ArrayList<Integer>();
    
    /**
     * The next line
     */
    private int[] nextLine = null;
    
    
    
    /**
     * Definition data class
     * 
     * @author  Mattias Andrée, <a href="mailto:maandree@kth.se">maandree@kth.se</a>
     */
    public static class DefinitionSpace
    {
	/**
	 * Constructor
	 * 
	 * @param  name        The name of the definition
	 * @param  nameLine    The line with the name of the definition
	 * @param  definition  The parts of the definition pattern
	 * @param  compiles    The parts of the code to what the definition compiles
	 * @param  oopses      All oopses, non-stopping errors
	 * @param  panics      All panics, stopping errors
	 * @param  warnings    All warnings
	 * @param  uniques     All unique warnings
	 */
	public DefinitionSpace(final int[] name,                  final int nameLine,
			       final ArrayList<int[]> definition, final ArrayList<Integer> definitionLines,
			       final ArrayList<int[]> compiles,   final ArrayList<Integer> compilesLines,
			       final ArrayList<int[]> oopses,     final ArrayList<Integer> oopsLines,
			       final ArrayList<int[]> panics,     final ArrayList<Integer> panicLines,
			       final ArrayList<int[]> warnings,   final ArrayList<Integer> warningLines,
			       final ArrayList<int[]> uniques,    final ArrayList<Integer> uniqueLines)
	{
	    this.name            = name;
	    this.nameLine        = nameLine;
	    this.definition      = definition;
	    this.definitionLines = definitionLines;
	    this.compiles        = compiles;
	    this.compilesLines   = compilesLines;
	    this.oopses          = oopses;
	    this.oopsLines       = oopsLines;
	    this.panics          = panics;
	    this.panicLines      = panicLines;
	    this.warnings        = warnings;
	    this.warningLines    = warningLines;
	    this.uniques         = uniques;
	    this.uniqueLines     = uniqueLines;
	}
	
	
	
	/**
	 * The name of the definition
	 */
	public final int[] name;
	
	/**
	 * The line with the name of the definition
	 */
	public final int nameLine;
	
	/**
	 * The name of the definition
	 */
	public final ArrayList<int[]> definition;
	
	/**
	 * The lines with the name of the definition
	 */
	public final ArrayList<Integer> definitionLines;
	
	/**
	 * The parts of the code to what the definition compiles
	 */
	public final ArrayList<int[]> compiles;
	
	/**
	 * The lines with the parts of the code to what the definition compiles
	 */
	public final ArrayList<Integer> compilesLines;
	
	/**
	 * All oopses, non-stopping errors
	 */
	public final ArrayList<int[]> oopses;
	
	/**
	 * The lines with the oopses, non-stopping errors
	 */
	public final ArrayList<Integer> oopsLines;
	
	/**
	 * All panics, stopping errors
	 */
	public final ArrayList<int[]> panics;
	
	/**
	 * The lines with the panics, stopping errors
	 */
	public final ArrayList<Integer> panicLines;
	
	/**
	 * All warnings
	 */
	public final ArrayList<int[]> warnings;
	
	/**
	 * The lines with the warnings
	 */
	public final ArrayList<Integer> warningLines;
	
	/**
	 * All unique warnings
	 */
	public final ArrayList<int[]> uniques;
	
	/**
	 * The lines with the unique warnings
	 */
	public final ArrayList<Integer> uniqueLines;
	
    }
    
    
    
    /**
     * Gets the next definition from a stream
     *
     * @param   is  The stream
     * @return      The raw data in the next definition, <code>null</code> is there are no more definitions
     * 
     * @throws  SyntaxFileError  If there is something wrong with the JCBNF file
     * @throws  IOException      On I/O exception
     */
    public DefinitionSpace getNextDefinition(final InputStream is) throws SyntaxFileError, IOException
    {
	final int DEFINITION     = 0;
	final int DEFINITION_CON = 1;
	final int COMPILES       = 2;
	final int COMPILES_CON   = 3;
	final int OOPS           = 4;
	final int PANIC          = 5;
	final int WARNING        = 6;
	final int WARNING_UNIQUE = 7;
	
	int c;
	int lastStmt = -1;
	for (int[] line;;)
	{
	    if (this.nextLine != null)
	    {
		line = this.nextLine;
		this.nextLine = null;
	    }
	    else if ((line = lr.getNextLine(is)) == null)
		break;
	    
	    
	    final int len = line.length;
	    final int lineIndex = lr.getLineIndex();
	    
	    assert len > 0 : "Line should not be empty here";
	    
	    if ((len >= 3) && (line[0] == ':') && (line[1] == ':') && (line[2] == '='))
		continue; //comment
	    if ((len >= 2) && (line[0] == '#') && (line[1] == '!') && (lineIndex == 0))
		continue; //shebang
	    
	    final Integer iline = new Integer(lineIndex);
	    
	    this.lineMap.put(iline, intArrayToString(line));
	    
	    int namelen = 0;
	    while (((c = line[namelen]) == '_') || (c == '@') || ('0' <= c && c <= '9') || ('a' <= c && c <= 'z') || ('A' <= c && c <= 'Z'))
		namelen++;
	    
	    if ((namelen > 0) && (name != null))
	    {
		this.nextLine = line;
		final DefinitionSpace rc = new DefinitionSpace(name,       nameLine,
							       definition, definitionLines,
							       compiles,   compilesLines,
							       oopses,     oopsLines,
							       panics,     panicLines,
							       warnings,   warningLines,
							       uniques,    uniqueLines);
		
		definition = new ArrayList<int[]>();  definitionLines = new ArrayList<Integer>();
		compiles   = new ArrayList<int[]>();  compilesLines   = new ArrayList<Integer>();
		oopses     = new ArrayList<int[]>();  oopsLines       = new ArrayList<Integer>();
		panics     = new ArrayList<int[]>();  panicLines      = new ArrayList<Integer>();
		warnings   = new ArrayList<int[]>();  warningLines    = new ArrayList<Integer>();
		uniques    = new ArrayList<int[]>();  uniqueLines     = new ArrayList<Integer>();
		
		name = null;
		
		return rc;
	    }
	    
	    if ((name == null) && (namelen == 0))
		throw new SyntaxFileError("Name missing", lineIndex, intArrayToString(line));
	    
	    if (namelen > 0)
	    {
		lastStmt = -1;
		name = new int[namelen];
		nameLine = lineIndex;
		System.arraycopy(line, 0, name, 0, namelen);
	    }
	    
	    int off = namelen;
	    while (((c = line[off]) == ' ') || (c == '\t'))
		off++;
	    
	    if (off + 3 >= len)
		throw new SyntaxFileError("Missing statement symbol (::=, ::-, ==>, -->, <--, <==, w-- or w==)", lineIndex, intArrayToString(line));
	    
	    final int stmt;
	    if      ((line[off + 0] == ':') && (line[off + 1] == ':') && (line[off + 2] == '=')) stmt = DEFINITION;
	    else if ((line[off + 0] == ':') && (line[off + 1] == ':') && (line[off + 2] == '-')) stmt = DEFINITION_CON;
	    else if ((line[off + 0] == '=') && (line[off + 1] == '=') && (line[off + 2] == '>')) stmt = COMPILES;
	    else if ((line[off + 0] == '-') && (line[off + 1] == '-') && (line[off + 2] == '>')) stmt = COMPILES_CON;
	    else if ((line[off + 0] == '<') && (line[off + 1] == '-') && (line[off + 2] == '-')) stmt = OOPS;
	    else if ((line[off + 0] == '<') && (line[off + 1] == '=') && (line[off + 2] == '=')) stmt = PANIC;
	    else if ((line[off + 0] == 'w') && (line[off + 1] == '-') && (line[off + 2] == '-')) stmt = WARNING;
	    else if ((line[off + 0] == 'w') && (line[off + 1] == '=') && (line[off + 2] == '=')) stmt = WARNING_UNIQUE;
	    else
		throw new SyntaxFileError("Unrecognised statement symbol", lineIndex, intArrayToString(line));
	    
	    if ((stmt == DEFINITION_CON) && (lastStmt != DEFINITION) && (lastStmt != DEFINITION_CON))
		throw new SyntaxFileError("There is nothing to continue", lineIndex, intArrayToString(line));
	    
	    if ((stmt == COMPILES_CON) && (lastStmt != COMPILES) && (lastStmt != COMPILES_CON))
		throw new SyntaxFileError("There is nothing to continue", lineIndex, intArrayToString(line));
	    
	    lastStmt = stmt;
	    off += 3;
	    
	    final int[] mline = new int[1 + len - off];
	    mline[0] = ' ';
	    System.arraycopy(line, off, mline, 1, len - off);
	    
	    switch (stmt)
	    {
		case DEFINITION:
	        case DEFINITION_CON:  definition.add(mline);  definitionLines.add(iline);  break;
		case COMPILES:
	        case COMPILES_CON:    compiles.add(mline);    compilesLines.add(iline);    break;
		case OOPS:            oopses.add(mline);      oopsLines.add(iline);        break;
		case PANIC:           panics.add(mline);      panicLines.add(iline);       break;
		case WARNING:         warnings.add(mline);    warningLines.add(iline);     break;
		case WARNING_UNIQUE:  uniques.add(mline);     uniqueLines.add(iline);      break;
	    }
	}
	
	if (name != null)
	    try
	    {
		return new DefinitionSpace(name, nameLine, definition, definitionLines, compiles, compilesLines, oopses, 
					   oopsLines, panics, panicLines, warnings, warningLines, uniques, uniqueLines);
	    }
	    finally
	    {
		name = null;
	    }
	
	return null;
    }
    
    
    /**
     * Converts an integer array to a string with only 16-bit charaters
     * 
     * @param   ints  The int array
     * @return        The string
     */
    public static String intArrayToString(final int[] ints)
    {
	int len = ints.length;
	for (final int i : ints)
	    if (i > 0xFFFF)
		len++;
	    else if (i > 0x10FFFF)
		throw new RuntimeException("Be serious, there is no character above plane 16.");
	
	final char[] chars = new char[len];
	int ptr = 0;
	
	for (final int i : ints)
	    if (i <= 0xFFFF)
		chars[ptr++] = (char)i;
	    else
	    {
		//0x10000 + (H - 0xD800) * 0x400 + (L - 0xDC00)
		
		int c = i - 0x10000;
		int L = (c % 0x400) + 0xDC00;
		int H = (c / 0x400) + 0xD800;
		
		chars[ptr++] = (char)H;
		chars[ptr++] = (char)L;
	    }
	
	return new String(chars);
    }
    
}


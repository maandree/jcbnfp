ol/**
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
 * Class for parsing the grammar of a JCBNF file
 * 
 * @author  Mattias Andrée, <a href="mailto:maandree@kth.se">maandree@kth.se</a>
 */
public class GrammarParser
{
    /**
     * Forbidden constructor
     */
    private GrammarParser()
    {
	assert false : "You may not create instances of this class.";
    }
    
    
    
    /**
     * Retrieves all definitions from a JCBNF data stream
     * 
     * @param  is  The JCBNF data stream
     * 
     * @throws  SyntaxFileError  If there is something wrong with the JCBNF file
     * @throws  IOException      On I/O exception
     */
    public static HashMap<String, Definition> parseGrammar(final InputStream is) throws SyntaxFileError, IOException
    {
	final HashMap<String, Definition> definitions = new HashMap<String, Definition>();
	
	DefinitionPuller.DefinitionSpace defspace;
	final DefinitionPuller puller = new DefinitionPuller();
	
	while ((defspace = puller.getNextDefinition(is)) != null)
	{
	    final char[] name = new char[defspace.name.length];
	    final int nameLine = defspace.nameLine;
	    final String lineName = puller.lineMap.get(new Integer(nameLine));
	    puller.lineMap.remove(new Integer(nameLine));
	    try
	    {
		if ((name.length == 2) && (name[0] == '@') && (name[name.length - 1] == '@'))
		    throw new SyntaxFileError("Invalid definition name", nameLine, lineName);
		
		final int PREPENDIX = 1;
		final int APPENDIX = 2;
		int ats = 0;
		
		for (int i = 0, n = name.length; i < n; i++)
		{
		    final int c = (int)(name[i] = (char)(defspace.name[i]));
		    if ((('a' > c) || (c > 'z')) && (('A' > c) || (c > 'Z')) && (c != '_'))
		    {
			boolean ok = false;
			
			if ((c == '@') && ((i == 0) || (i + 1 == name.length)))
			{
			    ok = true;
			    ats |= i == 0 ? PREPENDIX : APPENDIX;
			    if (n == 1)
				ats = 0;
			}
		    
			if (('0' <= c) && (c <= '9'))
			    if (i > (name[0] == '@' ? 2 : 1))
				ok = true;
		    
			if (ok == false)
			    throw new SyntaxFileError("Invalid definition name", nameLine, lineName);
		    }
		}
	    
		final String zName = new String(name);
		final String zzName = zName.substring(ats & PREPENDIX, zName.length() - ((ats & APPENDIX) >> 1));
		if (definitions.get(zzName) != null)
		    throw new SyntaxFileError("Already definied", nameLine, lineName);
		
		for (final Integer line : defspace.oopsLines)     puller.lineMap.remove(line);
		for (final Integer line : defspace.panicLines)    puller.lineMap.remove(line);
		for (final Integer line : defspace.warningLines)  puller.lineMap.remove(line);
		for (final Integer line : defspace.uniqueLines)   puller.lineMap.remove(line);
	    
		int defsize = 0;
		for (final int[] pdef : defspace.definition)
		    defsize += pdef.length;
	    
		int compsize = 0;
		for (final int[] pcomp : defspace.compiles)
		    compsize += pcomp.length;
	    
		final int[] def = new int[defsize];
		final int[] comp = new int[compsize];
		int ptr;
	    
		ptr = 0;
		for (final int[] pdef : defspace.definition)
		{
		    System.arraycopy(pdef, 0, def, ptr, pdef.length);
		    ptr += pdef.length;
		}
		
		ptr = 0;
		for (final int[] pcomp : defspace.compiles)
		{
		    System.arraycopy(pcomp, 0, comp, ptr, pcomp.length);
		    ptr += pcomp.length;
		}
		
		defspace.definitionLines.clear();
		defspace.compilesLines.clear();
		
		definitions.put(zzName, new Definition(zName, defspace.definition.size() == 0 ? null : def,
						       defspace.compiles.size() == 0 ? null : comp,
						       defspace.oopses, defspace.panics, defspace.warnings, defspace.uniques));
		
		defspace.definition.clear();
		defspace.compiles.clear();
	    }
	    catch (final SyntaxFileError err)
	    {
		throw err; //initial cause
	    }
	    catch (final Throwable err)
	    {
		throw new SyntaxFileError("Unknown exception", nameLine, lineName, err);
	    }
	}
	
	return definitions;
    }
    
}


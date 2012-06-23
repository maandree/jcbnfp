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
 * This exception is thrown if the JCBNF file cannot be parsed
 * 
 * @author  Mattias Andrée, <a href="mailto:maandree@kth.se">maandree@kth.se</a>
 */
@SuppressWarnings("serial")
public class SyntaxFileError extends Exception
{
    /**
     * Constructor
     *
     * @param  description  A description on the error
     * @param  lineIndex    The zero-based index of the line where the exception occured
     * @param  line         The content of the line where the exception occured
     */
    public SyntaxFileError(final String description, final int lineIndex, final String line)
    {
	super(description + " @ line " + (lineIndex + 1) + ": " + line);
	
	this.description = description;
	this.lineIndex   = lineIndex;
	this.line        = line;
    }
    
    
    
    /**
     * A description on the error
     */
    public final String description;
    
    /**
     * The zero-based index of the line where the exception occured
     */
    public final int lineIndex;
    
    /**
     * The content of the line where the exception occured
     */
    public final String line;
    
}


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
package se.kth.maandree.jcbnfp.elements;
import se.kth.maandree.jcbnfp.*;

import java.util.*;


/**
 * JCBNF grammar element: juxtaposition
 * 
 * @author  Mattias Andrée, <a href="mailto:maandree@kth.se">maandree@kth.se</a>
 */
public class JCBNFJuxtaposition implements GrammarElement
{
    //Has default contructor
    
    
    
    /**
     * The elements
     */
    public final Vector<GrammarElement> elements = new Vector<GrammarElement>();
    
    
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void printGrammar(final String indent)
    {
	System.out.print(indent);
	System.out.println(".juxta.");
	for (final GrammarElement e : this.elements)
	    e.printGrammar(indent + "  ");
    }
    
}


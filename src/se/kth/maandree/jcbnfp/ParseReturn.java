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


/**
 * Return data class for some parsing methods
 * 
 * @author  Mattias Andrée, <a href="mailto:maandree@kth.se">maandree@kth.se</a>
 */
class ParseReturn
{
    //Has default constructor
    
    
    
    /**
     * The subtree's named capture storage, may be <code>null</code>
     */
    public HashMap<String, ArrayDeque<int[]>> storage = null;
    
    /**
     * The number of read elements in the named capture storage for a given name
     */
    public final HashMap<String, int[]> storageRead = new HashMap<String, int[]>();
    
    /**
     * The amount of read data
     */
    public int read = 0;
    
    
    
    /**
     * Concatenates data from another instance
     * 
     * @param  obj  The object with which to concatenate
     */
    protected void cat(final ParseReturn obj)
    {
	{
	    HashMap<String, ArrayDeque<int[]>> xx = this.storage;
	    final HashMap<String, ArrayDeque<int[]>> xy = obj.storage;
	    
	    if (xx == null)
		this.storage = xy;
	    else if (xy != null)
		for (final Map.Entry<String, ArrayDeque<int[]>> entry : xy.entrySet())
		{
		    final String key = entry.getKey();
		    final ArrayDeque<int[]> values = entry.getValue();
		    final ArrayDeque<int[]> vs;
		    
		    if ((vs = xx.get(key)) != null)
			vs.addAll(values);
		    else
			xx.put(key, values);
		}
	}
	{
	    final HashMap<String, int[]> xx = this.storageRead;
	    final HashMap<String, int[]> xy = obj.storageRead;
	    
	    for (final Map.Entry<String, int[]> entry : xy.entrySet())
	    {
		final String key = entry.getKey();
		final int[] value = entry.getValue();
		final int[] v;
		
		if ((v = xx.get(key)) != null)
		    v[0] += value[0];
		else
		    xx.put(key, value);
	    }
	}
    }
}


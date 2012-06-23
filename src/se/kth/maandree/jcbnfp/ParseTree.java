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
import se.kth.maandree.jcbnfp.elements.*;

import java.util.*;


/**
 * Tree describing parsed data, as well as parsing it
 * 
 * @author  Mattias Andrée, <a href="mailto:maandree@kth.se">maandree@kth.se</a>
 */
public class ParseTree
{
    /**
     * Inside a bounded repeat clause, which can be assembled from repeat clauses
     */
    private static final byte REPEAT = 1;
    
    /**
     * Inside a optional clause, which is actually assembled to an [0..1] bounded repeat clause
     */
    private static final byte OPTION = 2;
    
    
    
    /**
     * Constructor
     * 
     * @param  parent       The parent node, <code>null</code> if none
     * @param  definition   The current definition, includes name, grammar &amp;c
     * @param  definitions  Definition map
     */
    public ParseTree(final ParseTree parent, final Definition definition, final HashMap<String, Definition> definitions)
    {
	this.parent = parent;
	this.definition = definition;
	this.definitions = definitions;
    }
    
    
    
    /**
     * The parent node, <code>null</code> if none
     */
    public final ParseTree parent;
    
    /**
     * The current definition, includes name, grammar &amp;c
     */
    public final Definition definition;
    
    /**
     * The node's children
     */
    public final ArrayList<ParseTree> children = new ArrayList<ParseTree>();
    
    /**
     * The subtree's named capture storage, may be <code>null</code>
     */
    public HashMap<String, ArrayList<int[]>> storage = null;
    
    /**
     * Definition map
     */
    public final HashMap<String, Definition> definitions;
    
    
    
    /**
     * Return data class for some parsing methods
     * 
     * @author  Mattias Andrée, <a href="mailto:maandree@kth.se">maandree@kth.se</a>
     */
    static class Return
    {
	//Has default constructor
	
	
	
	/**
	 * The subtree's named capture storage, may be <code>null</code>
	 */
	public HashMap<String, ArrayList<int[]>> storage = null;
	
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
	protected void cat(final Return obj)
	{
	    {
		HashMap<String, ArrayList<int[]>> xx = this.storage;
		final HashMap<String, ArrayList<int[]>> xy = obj.storage;
		
		if (xx == null)
		    this.storage = xy;
		else
		    for (final Map.Entry<String, ArrayList<int[]>> entry : xy.entrySet())
		    {
			final String key = entry.getKey();
			final ArrayList<int[]> values = entry.getValue();
			final ArrayList<int[]> vs;
			    
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
    
    
    
    /**
     * Parses the tree and stores all data
     * 
     * @param   data  The data
     * @param   off   The offset in the data
     * @return        The amount of read data
     */
    public int parse(final int[] data, final int off)
    {
	@SuppressWarnings({"all", "unchecked", "rawtypes"}) // ecj finds [unchecked], openjdk finds [rawtypes] as well, [all] removed warning about "rawtypes" in ecj
	final HashMap<String, ArrayList<int[]>>[] storages = (HashMap<String, ArrayList<int[]>>[])(new HashMap[32]);
	@SuppressWarnings({"all", "unchecked", "rawtypes"})
	final HashMap<String, int[]>[] reads = (HashMap<String, int[]>[])(new HashMap[32]);
	final Return r = parse(data, off, this.definition.definition, storages, 0, reads, 0, (byte)0);
	this.storage = r.storage;
	return r == null ? -1 : r.read;
    }
    
    
    /**
     * Find a named capture
     * 
     * @param   name            The name of the capture
     * @param   storages        Named capture storage stack
     * @param   storagePtr      Named capture storage stack pointer
     * @param   reads           Named capture read stack
     * @param   readPtr         Named capture read stack pointer
     * @parma   elementalState  Grammar element state
     * @return                  The capture's span, <code>null</code> if not found, otherwise, {start, end}
     */
    private int[] backtrack(final String name, final HashMap<String, ArrayList<int[]>>[] storages, final int storagePtr,
			    final HashMap<String, int[]>[] reads, final int readPtr, final byte elementalState)
    {
	ParseTree tree = this;
	for (;;)
	{
	    if (tree == null)
		return null;
	    
	    final HashMap<String, ArrayList<int[]>> s = this.storage;
	    if (s == null)
	    {
		tree = tree.parent;
		break;
	    }
	    
	    final ArrayList<int[]> a = s.get(name);
	    if (a == null)
	    {
		tree = tree.parent;
		break;
	    }
	    
	    //TODO ########################################################################################
	}
	return null; // todo #####
    }
    
    
    /**
     * Parses a subtree
     * 
     * @param   data            The data
     * @param   off             The offset in the data
     * @param   def             The grammar element to parse
     * @param   storages        Named capture storage stack
     * @param   storagePtr      Named capture storage stack pointer
     * @param   reads           Named capture read stack
     * @param   readPtr         Named capture read stack pointer
     * @param   elementalState  Grammar element state
     * @return                  Parsing subtree data
     */
    private Return parse(final int[] data, final int off, final GrammarElement def, final HashMap<String, ArrayList<int[]>>[] storages,
			 final int storagePtr, final HashMap<String, int[]>[] reads, final int readPtr, final byte elementalState)
    {
	Return rc = new Return();
	final GrammarElement grammar = Parser.assemble(def);
	final int atom = Parser.passes(data, off, grammar);
	
	if (atom == -1) rc.read = -1;
	if (atom >= 0)  rc.read = off + atom;
	if (atom >= -1)
	    return rc;
	
	if (grammar instanceof JCBNFBacktrack)
	{
	    final String name = ((JCBNFBacktrack)grammar).name;
	    final int[] start_end = backtrack(name, storages, storagePtr, reads, readPtr, elementalState);
	    if (start_end == null)
		return null;
	    
	    final int start = start_end[0];
	    final int end = start_end[1];
	    
	    if (((JCBNFBacktrack)grammar).replacee != null)
		rc.read = Parser.passes(data, off, start, end);
	    else
	    {
		final int[] replacee = Util.stringToIntArray(((JCBNFBacktrack)grammar).replacee);
		final int[] replacer = Util.stringToIntArray(((JCBNFBacktrack)grammar).replacer);
		
		rc.read = Parser.passes(data, off, start, end, replacee, replacer);
	    }
	    
	    return rc.read < 0 ? null : rc;
	}
	if (grammar instanceof JCBNFStore)
	{
	    final String name = ((JCBNFStore)grammar).name;
	    final GrammarElement g = ((JCBNFStore)grammar).element;
	    final Return r = parse(data, off, g, storages, storagePtr, reads, readPtr, elementalState);
	    if (r.read < 0)
		return null;
	    
	    if (r.storage == null)
		r.storage = new HashMap<String, ArrayList<int[]>>();
	    
	    ArrayList<int[]> list = r.storage.get(name);
	    if (list == null)
		r.storage.put(name, list = new ArrayList<int[]>());
	    
	    //FIXME (start and ref[] are not definied)
	    //list.add(0, new int[] { start, start + ref[0] }); // I have arbitrarly choosen to add items in parse (definition) order,
	    //                                                  // rather than parsed (complete) order; however storing effected by
	    return r;                                         // this choice [for example <a=x <a=y> z>] is strongly disencouraged.
	}
	if (grammar instanceof JCBNFBoundedRepeation) //TODO ###################################################################################### reads
	{
	    Return r;
	    final int min = ((JCBNFBoundedRepeation)grammar).minCount;
	    final int max = ((JCBNFBoundedRepeation)grammar).maxCount;
	    final GrammarElement g = ((JCBNFBoundedRepeation)grammar).element;
	    HashMap<String, ArrayList<int[]>>[] nstorages = storages;
	    if (storagePtr == storages.length)
	    {
		@SuppressWarnings({"all", "unchecked", "rawtypes"})
		final HashMap<String, ArrayList<int[]>>[] nnstorages = (HashMap<String, ArrayList<int[]>>[])(new HashMap[storagePtr << 1]);
		System.arraycopy(storages, 0, nnstorages, 0, storagePtr);
		nstorages = nnstorages;
	    }
	    nstorages[storagePtr] = null;
	    
	    int es = elementalState;
	    es |= min == 0 ? OPTION : 0;
	    es |= max != 1 ? REPEAT : 0;
	    
	    int offset = off;
	    for (int i = 0; i < min; i++)
	    {
		r = parse(data, offset, g, nstorages, storagePtr + 1, reads, readPtr, (byte)es);
		if (r.read < 0)
		    return null;
		offset += r.read;
		rc.cat(r);
		if (nstorages[storagePtr] == null)
		    nstorages[storagePtr] = rc.storage;
	    }
	    for (int i = min; i != max; i++) //infinity is -1, so 'i < max' would fail
	    {
		r = parse(data, offset, g, nstorages, storagePtr + 1, reads, readPtr, (byte)es);
		if (r.read < 0)
		    break;
		offset += r.read;
		rc.cat(r);
		if (nstorages[storagePtr] == null)
		    nstorages[storagePtr] = rc.storage;
	    }
	    
	    rc.read = offset - off;
	    return rc;
	}
	if (grammar instanceof JCBNFJuxtaposition) //TODO ###################################################################################### reads
	{
	    Return r;
	    int offset = off;
	    HashMap<String, ArrayList<int[]>>[] nstorages = storages;
	    if (storagePtr == storages.length)
	    {
		@SuppressWarnings({"all", "unchecked", "rawtypes"})
	        final HashMap<String, ArrayList<int[]>>[] nnstorages = (HashMap<String, ArrayList<int[]>>[])(new HashMap[storagePtr << 1]);
		System.arraycopy(storages, 0, nnstorages, 0, storagePtr);
		nstorages = nnstorages;
	    }
	    nstorages[storagePtr] = null;
	    
	    
	    for (final GrammarElement g : ((JCBNFJuxtaposition)grammar).elements)
	    {
		r = parse(data, offset, g, nstorages, storagePtr + 1, reads, readPtr, elementalState);
		if (r.read < 0)
		    return null;
		offset += r.read;
		rc.cat(r);
		if (nstorages[storagePtr] == null)
		    nstorages[storagePtr] = rc.storage;
	    }
	    
	    rc.read = offset - off;
	    return rc;
	}
	if (grammar instanceof JCBNFAlternation)
	{
	    for (final GrammarElement g : ((JCBNFAlternation)grammar).elements)
	    {
		rc = parse(data, off, g, storages, storagePtr, reads, readPtr, elementalState);
		if (rc.read >= 0)
		    break;
		rc = null;
	    }
	    
	    return rc;
	}
	if (grammar instanceof JCBNFDefinition)
	{
	    final String name = ((JCBNFDefinition)grammar).name;
	    final ParseTree child = new ParseTree(this, this.definitions.get(name), this.definitions);
	    this.children.add(child);
	    final int r = child.parse(data, off);
	    rc.read = r;
	    return rc;
	}
	
	assert false : "Unrecognised grammar used!";
	return null;
    }
    
}


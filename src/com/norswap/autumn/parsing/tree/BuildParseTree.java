package com.norswap.autumn.parsing.tree;

import com.norswap.util.Array;

import java.util.HashSet;
import java.util.Set;

/**
 * A parse tree as built by captures.
 * <p>
 * This version of the parse tree is the one we construct during the parse and is optimized for that
 * use-case. When processing the parse tree, the user should work an instance {@link ParseTree}
 * obtained by calling {@link #build} method of this class.
 */
public final class BuildParseTree
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public String accessor;

    public Array<String> tags;

    public String value;

    public Array<BuildParseTree> children;

    public BuildParseTree wrappee;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public BuildParseTree() {}

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public int childrenCount()
    {
        return children == null ? 0 : children.size();
    }

    // ---------------------------------------------------------------------------------------------

    public void add(BuildParseTree child)
    {
        if (children == null)
        {
            children = new Array<>();
        }

        children.add(child);
    }

    // ---------------------------------------------------------------------------------------------

    public void addAll(Array<BuildParseTree> array)
    {
        if (children == null)
        {
            children = new Array<>();
        }

        children.addAll(array);
    }

    // ---------------------------------------------------------------------------------------------

    public ParseTree build()
    {
        return build(null, new HashSet<>());
    }

    // ---------------------------------------------------------------------------------------------

    private ParseTree build(String accessor, Set<String> tagSet)
    {
        if (accessor != null && this.accessor != null)
        {
            throw new RuntimeException(String.format(
                "Trying to override accessor \"%s\" with accessor \"%s\".",
                accessor, this.accessor));
        }

        accessor = accessor != null ? accessor : this.accessor;

        if (tags != null)
        {
            tagSet.addAll(tags);
        }

        return wrappee != null
            ? wrappee.build(accessor, tagSet)
            : new ParseTree(accessor, value, tagSet, children.map(BuildParseTree::build));
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
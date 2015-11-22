package com.norswap.autumn.parsing.tree;

import com.norswap.util.Array;
import com.norswap.util.annotations.NonNull;

import java.util.Collections;
import java.util.LinkedHashSet;

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

    public String kind;

    public String value;

    public Array<String> tags;

    private final @NonNull Array<BuildParseTree> children;

    public BuildParseTree wrappee;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public BuildParseTree()
    {
        this.children = new Array<>();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // CHILDREN DELEGATION

    public int childrenCount()
    {
        return children == null ? 0 : children.size();
    }

    // ---------------------------------------------------------------------------------------------

    public void add(BuildParseTree child)
    {
        children.add(child);
    }

    // ---------------------------------------------------------------------------------------------

    public void addAll(Array<BuildParseTree> array)
    {
        children.addAll(array);
    }

    // ---------------------------------------------------------------------------------------------

    public BuildParseTree child(int i)
    {
        return children.get(i);
    }

    // ---------------------------------------------------------------------------------------------

    public void setChild(int i, BuildParseTree tree)
    {
        children.set(i, tree);
    }

    // ---------------------------------------------------------------------------------------------

    public void truncate(int size)
    {
        children.truncate(size);
    }

    // ---------------------------------------------------------------------------------------------

    public Array<BuildParseTree> childrenFromIndex(int i)
    {
        return children.copyFromIndex(i);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public ParseTree build()
    {
        return build(null, new Array<>());
    }

    // ---------------------------------------------------------------------------------------------

    private ParseTree build(String accessor, Array<String> tagSet)
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
            : new ParseTree(
                accessor,
                value,
                null, // TODO kind
                tagSet == null ? Collections.emptySet() : new LinkedHashSet<>(tagSet),
                children == null ? Array.empty() : children.map(BuildParseTree::build));
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
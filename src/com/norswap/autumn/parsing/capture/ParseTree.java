package com.norswap.autumn.parsing.capture;

import com.norswap.util.Array;
import com.norswap.util.Strings;

import java.util.Collections;
import java.util.Iterator;
import java.util.Set;

import static com.norswap.util.JObjects.hash;
import static com.norswap.util.JObjects.same;

public final class ParseTree  implements Iterable<ParseTree>
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public final String accessor;
    public final String value;
    public final String kind;
    private final Set<String> tags;
    private final Array<ParseTree> children;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public ParseTree(
        String accessor,
        String value,
        String kind,
        Set<String> tags,
        Array<ParseTree> children)
    {
        this.accessor = accessor;
        this.value = value;
        this.kind = kind;
        this.tags = tags;
        this.children = children;
    }

    // ---------------------------------------------------------------------------------------------

    public ParseTree(ParseTreeTransient transientTree)
    {
        this.accessor = transientTree.accessor;
        this.value = transientTree.value;
        this.kind = transientTree.kind;
        this.tags = transientTree.tags;
        this.children = transientTree.children;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // TAGS

    /**
     * Does this node have the given tag?
     */
    public boolean hasTag(String tag)
    {
        return tags != null && tags.contains(tag);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // CHILDREN: ACCESSORS

    /**
     * Does the tree have a child with the given accessor?
     */
    public boolean has(String accessor)
    {
        return get(accessor) != null;
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Returns the child with the given accessor, or null.
     */
    public ParseTree get(String accessor)
    {
        return children == null
            ? null
            : children.first(x -> x.accessor.equals(accessor));
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Returns the value of the child with the given accessor, or null.
     */
    public String value(String accessor)
    {
        ParseTree child = get(accessor);
        return child == null ? null : child.value;
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Returns the group with the given accessor.
     */
    public Array<ParseTree> group(String accessor)
    {
        return children == null
            ? null
            : children.filter(x -> x.accessor.equals(accessor));
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // CHILDREN: ALL

    /**
     * Returns all the children.
     */
    public Array<ParseTree> children()
    {
        return children == null
            ? Array.empty()
            : children;
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public Iterator<ParseTree> iterator()
    {
        return children == null
            ? Collections.emptyIterator()
            : children.iterator();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // CHILDREN: POSITIONAL

    /**
     * Return the first child, or null.
     */
    public ParseTree child()
    {
        return children == null
            ? null
            : children.size() > 0 ? children.get(0) : null;
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Return the child at the given index, or null.
     */
    public ParseTree child(int i)
    {
        return children != null && i >= 0 && i < children.size() ? children.get(i) : null;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // CHILDREN: TAGS

    /**
     * Return all nodes that have the given tag).
     */
    public Array<ParseTree> tagged(String tag)
    {
        return children == null
            ? Array.empty()
            : children.filter(x -> x.hasTag(tag));
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // TO STRING

    public String nodeToString()
    {
        StringBuilder builder = new StringBuilder();
        nodeToString(builder);
        return builder.toString();
    }

    // ---------------------------------------------------------------------------------------------

    public void nodeToString(StringBuilder builder)
    {
        if (accessor == null && tags == null && value == null)
        {
            builder.append("--");
            return;
        }

        if (accessor != null)
        {
            builder.append(accessor);

            if (tags == null && value != null)
            {
                builder.append(" - ");
            }
            else if (tags != null)
            {
                builder.append(" ");
            }
        }

        if (tags != null)
        {
            builder.append(tags);

            if (value != null)
            {
                builder.append(" - ");
            }
        }

        if (value != null)
        {
            builder.append("\"");
            builder.append(value);
            builder.append("\"");
        }
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        toString(builder, 0);
        return builder.toString();
    }

    // ---------------------------------------------------------------------------------------------

    public void toString(StringBuilder builder, int depth)
    {
        builder.append(Strings.times(depth, "-|"));
        nodeToString(builder);
        builder.append("\n");

        if (children != null)
        for (ParseTree child: children)
        {
            child.toString(builder, depth + 1);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (!(o instanceof ParseTree)) return false;

        ParseTree that = (ParseTree) o;

        return
           same(accessor, that.accessor)
        && same(value,    that.value)
        && same(kind,     that.kind)
        && same(tags,      that.tags)
        && same(children,  that.children);
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public int hashCode()
    {
        int result = hash(accessor);
        result = 31 * result + hash(value);
        result = 31 * result + hash(kind);
        result = 31 * result + hash(tags);
        result = 31 * result + hash(children);
        return result;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}

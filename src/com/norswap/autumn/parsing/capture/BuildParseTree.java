package com.norswap.autumn.parsing.capture;

import com.norswap.util.Array;
import com.norswap.util.Strings;

public final class BuildParseTree
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public final boolean capture;

    private Array<Decoration> decorations;
    private Array<BuildParseTree> children;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public BuildParseTree(boolean capture, Array<Decoration> decorations)
    {
        this.capture = capture;
        this.decorations = decorations;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public void addChild(BuildParseTree child)
    {
        if (children == null) children = new Array<>();
        children.add(child);
    }

    // ---------------------------------------------------------------------------------------------

    public void addChildren(Array<BuildParseTree> array)
    {
        if (children == null) children = new Array<>();
        children.addAll(array);
    }

    // ---------------------------------------------------------------------------------------------

    public void addDecoration(Decoration decoration)
    {
        if (decorations == null) decorations = new Array<>(1);
        decorations.add(decoration);
    }

    // ---------------------------------------------------------------------------------------------

    public void truncate(int size)
    {
        if (children == null) return;
        children.truncate(size);
    }

    // ---------------------------------------------------------------------------------------------

    public int childrenCount()
    {
        return children == null ? 0 : children.size();
    }

    // ---------------------------------------------------------------------------------------------

    public Array<BuildParseTree> childrenFromIndex(int i)
    {
        return children != null
            ? children.copyFromIndex(i)
            : null;
    }

    // ---------------------------------------------------------------------------------------------

    public Array<ParseTreeTransient> build()
    {
        Array<ParseTreeTransient> concatenatedChildren =
            children == null
                ? null
                : Array.concat(children.map(BuildParseTree::build));

        Array<ParseTreeTransient> out =
            capture
                ? new Array<>(new ParseTreeTransient())
                : concatenatedChildren;

        if (capture && children != null)
            out.first().children = concatenatedChildren.map(ParseTree::new);

        if (out != null && decorations != null)
            for (ParseTreeTransient node: out)
                for (Decoration deco: decorations)
                    deco.decorate(node);

        return out != null ? out : Array.empty();
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
        builder.append(capture + " " + decorations);
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
            for (BuildParseTree child: children)
            {
                child.toString(builder, depth + 1);
            }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
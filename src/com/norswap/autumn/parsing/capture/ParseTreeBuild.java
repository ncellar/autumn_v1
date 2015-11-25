package com.norswap.autumn.parsing.capture;

import com.norswap.util.Array;
import com.norswap.util.JArrays;
import com.norswap.util.Strings;
import com.norswap.util.annotations.NonNull;
import java.util.Arrays;

public final class ParseTreeBuild
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public final boolean capture;

    private final @NonNull Decoration[] decorations;
    private @NonNull Array<ParseTreeBuild> children = EMPTY_BUILD;
    public String value;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private static final ParseTree[]            EMPTY_TREE      = new ParseTree[0];
    private static final Array<ParseTreeBuild>  EMPTY_BUILD     = Array.empty();
    private static final ParseTreeTransient[]   EMPTY_TRANSIENT = new ParseTreeTransient[0];

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public ParseTreeBuild(boolean capture, @NonNull Decoration[] decorations)
    {
        this.capture = capture;
        this.decorations = decorations;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public void addChild(ParseTreeBuild child)
    {
        if (children == EMPTY_BUILD) children = new Array<>();
        children.add(child);
    }

    // ---------------------------------------------------------------------------------------------

    public void addChildren(Array<ParseTreeBuild> array)
    {
        if (children == EMPTY_BUILD) children = new Array<>();
        children.addAll(array);
    }

    // ---------------------------------------------------------------------------------------------

    public void truncate(int size)
    {
        if (children == EMPTY_BUILD) return;
        children.truncate(size);
    }

    // ---------------------------------------------------------------------------------------------

    public int childrenCount()
    {
        return children.size();
    }

    // ---------------------------------------------------------------------------------------------

    public Array<ParseTreeBuild> childrenFromIndex(int i)
    {
        return children != EMPTY_BUILD
            ? children.copyFromIndex(i)
            : null;
    }

    // ---------------------------------------------------------------------------------------------


    public @NonNull ParseTreeTransient[] build()
    {
        ParseTreeTransient[] concatenatedChildren = children == EMPTY_BUILD
            ? EMPTY_TRANSIENT
            : JArrays.concat(
                ParseTreeTransient[]::new,
                children.mapToArray(
                    ParseTreeBuild::build,
                    ParseTreeTransient[][]::new));

        ParseTreeTransient[] out;

        if (capture)
        {
            ParseTreeTransient tree = new ParseTreeTransient();
            tree.value = value;

            tree.children = children == EMPTY_BUILD
                ? EMPTY_TREE
                : JArrays.map(
                    concatenatedChildren,
                    new ParseTree[concatenatedChildren.length],
                    ParseTree::new);

            out = new ParseTreeTransient[]{tree};
        }
        else
        {
            out = concatenatedChildren;
        }

        for (ParseTreeTransient node: out)
            for (Decoration deco: decorations)
                deco.decorate(node);

        return out;
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
        builder.append(capture + " " + Arrays.toString(decorations));
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

        for (ParseTreeBuild child: children)
            child.toString(builder, depth + 1);

    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
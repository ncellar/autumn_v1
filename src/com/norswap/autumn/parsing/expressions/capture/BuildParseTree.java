package com.norswap.autumn.parsing.expressions.capture;

import com.norswap.util.Array;

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

    void addChild(BuildParseTree child)
    {
        if (children == null) children = new Array<>();
        children.add(child);
    }

    // ---------------------------------------------------------------------------------------------

    void addDecorations(Decoration[] decos)
    {
        if (decorations == null) decorations = new Array<>();
        this.decorations.addAll(decos);
    }

    // ---------------------------------------------------------------------------------------------

    void addDecoration(Decoration decoration)
    {
        if (decorations == null) decorations = new Array<>();
        decorations.add(decoration);
    }

    // ---------------------------------------------------------------------------------------------

    Array<ParseTreeTransient> build()
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

        if (decorations != null)
            out.forEach(node -> decorations.forEach(deco -> deco.decorate(node)));

        return out;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
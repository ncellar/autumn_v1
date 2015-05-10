package com.norswap.autumn.parsing3;

import com.norswap.autumn.util.Array;

public final class OutputChanges
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public int position;
    public int blackPosition;
    public ParseResult tree;
    public Array<String> cuts;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public static OutputChanges failure()
    {
        OutputChanges changes = new OutputChanges();
        changes.position = -1;
        changes.blackPosition = -1;
        return changes;
    }

    // ---------------------------------------------------------------------------------------------

    private OutputChanges()
    {
    }

    // ---------------------------------------------------------------------------------------------

    public OutputChanges(ParseInput input)
    {
        this.position = input.output.position;
        this.blackPosition = input.output.blackPosition;
        this.tree = new ParseResult();
        this.cuts = new Array<>();

        for (int i = input.resultChildrenCount; i < input.result.childrenCount(); ++i)
        {
            this.tree.add(input.result.children.get(i));
        }

        for (int i = input.cutsCount; i < input.cuts.size(); ++i)
        {
            this.cuts.add(input.cuts.get(i));
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public void mergeInto(ParseInput input)
    {
        input.output.position = position;
        input.output.blackPosition = blackPosition;

        if (tree != null)
        {
            input.result.add(tree);
        }

        if (cuts != null)
        {
            input.cuts.addAll(cuts);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}

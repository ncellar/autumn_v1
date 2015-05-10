package com.norswap.autumn.parsing3;

import com.norswap.autumn.util.Array;

public final class OutputChanges
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public int end;
    public int blackEnd;
    public ParseResult tree;
    public Array<String> cuts;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public static OutputChanges failure()
    {
        OutputChanges changes = new OutputChanges();
        changes.end = -1;
        changes.blackEnd = -1;
        return changes;
    }

    // ---------------------------------------------------------------------------------------------

    private OutputChanges()
    {
    }

    // ---------------------------------------------------------------------------------------------

    public OutputChanges(ParseInput input)
    {
        this.end = input.end;
        this.blackEnd = input.blackEnd;
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
        input.end = end;
        input.blackEnd = blackEnd;

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

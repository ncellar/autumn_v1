package com.norswap.autumn.parsing;

import com.norswap.autumn.util.Array;

public final class OutputChanges
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public int end;
    public int blackEnd;
    public ParseTree tree;
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
        this.tree = new ParseTree();
        this.cuts = new Array<>();

        for (int i = input.treeChildrenCount; i < input.tree.childrenCount(); ++i)
        {
            this.tree.add(input.tree.children.get(i));
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
            input.tree.add(tree);
        }

        if (cuts != null)
        {
            input.cuts.addAll(cuts);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public boolean succeeded()
    {
        return end != -1;
    }

    // ---------------------------------------------------------------------------------------------

    public boolean failed()
    {
        return end == -1;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}

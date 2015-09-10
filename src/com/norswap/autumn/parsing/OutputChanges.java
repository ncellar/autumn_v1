package com.norswap.autumn.parsing;

import com.norswap.util.Array;
import com.norswap.util.HandleMap;

/**
 * TODO
 */
public final class OutputChanges
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public final int end;
    public final int blackEnd;

    private Array<ParseTree> children;
    private HandleMap ext;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public static OutputChanges failure()
    {
        return new OutputChanges();
    }

    // ---------------------------------------------------------------------------------------------

    private OutputChanges()
    {
        this.end = -1;
        this.blackEnd = -1;
    }

    // ---------------------------------------------------------------------------------------------

    public OutputChanges(ParseState state)
    {
        this.end = state.end;
        this.blackEnd = state.blackEnd;
        this.children = new Array<>();

        for (int i = state.treeChildrenCount; i < state.tree.childrenCount(); ++i)
        {
            this.children.add(state.tree.children.get(i).unqualify(state));
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public void mergeInto(ParseState state)
    {
        state.end = end;
        state.blackEnd = blackEnd;

        if (children != null)
        {
            for (ParseTree child: children)
            {
                state.tree.add(new ParseTree(state, child));
            }
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

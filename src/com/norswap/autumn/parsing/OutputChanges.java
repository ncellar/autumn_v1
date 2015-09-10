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
        this.children = state.tree.unqualifiedAddedChildren(state);
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
                ParseTree qualified = child.qualify(state);
                state.tree.add(qualified);
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

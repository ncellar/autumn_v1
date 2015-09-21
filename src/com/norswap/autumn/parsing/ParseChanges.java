package com.norswap.autumn.parsing;

import com.norswap.autumn.parsing.tree.BuildParseTree;
import com.norswap.util.Array;

/**
 * TODO
 */
public final class ParseChanges
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public final int end;

    public final int blackEnd;

    public final Array<BuildParseTree> children;

    public final Object[] customChanges;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public ParseChanges(
        int end,
        int blackEnd,
        Array<BuildParseTree> children,
        Object[] customChanges)
    {
        this.end = end;
        this.blackEnd = blackEnd;
        this.children = children;
        this.customChanges = customChanges;
    }

    // ---------------------------------------------------------------------------------------------

    // TODO nullize
    public static ParseChanges failure()
    {
        return new ParseChanges(-1, -1, Array.empty(), new Object[0]);
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

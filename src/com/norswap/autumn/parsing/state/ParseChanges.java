package com.norswap.autumn.parsing.state;

import com.norswap.autumn.parsing.tree.BuildParseTree;
import com.norswap.util.Array;

/**
 * See {@link ParseState}, "Committed and Uncommitted State" section.
 */
public final class ParseChanges
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private static final CustomChanges[] NO_CHILDREN = new CustomChanges[0];

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public final int end;
    public final int blackEnd;
    public final Array<BuildParseTree> children;
    public final CustomChanges[] customChanges;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public ParseChanges(
        int end,
        int blackEnd,
        Array<BuildParseTree> children,
        CustomChanges[] customChanges)
    {
        this.end = end;
        this.blackEnd = blackEnd;
        this.children = children;
        this.customChanges = customChanges;
    }

    // ---------------------------------------------------------------------------------------------

    public static ParseChanges failure()
    {
        return new ParseChanges(-1, -1, Array.empty(), NO_CHILDREN);
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

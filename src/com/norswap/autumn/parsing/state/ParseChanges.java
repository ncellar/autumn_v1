package com.norswap.autumn.parsing.state;

import com.norswap.autumn.parsing.ParsingExpression;
import com.norswap.autumn.parsing.tree.BuildParseTree;
import com.norswap.util.Array;
import com.norswap.util.annotations.Nullable;

/**
 * See {@link ParseState}, "Committed and Uncommitted State" section.
 */
public final class ParseChanges
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public final int end;
    public final int blackEnd;
    public final ParsingExpression clusterAlternate;
    public final @Nullable Array<BuildParseTree> children;
    public final @Nullable CustomChanges[] customChanges;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public ParseChanges(
        int end,
        int blackEnd,
        Array<BuildParseTree> children,
        ParsingExpression clusterAlternate,
        CustomChanges[] customChanges)
    {
        this.end = end;
        this.blackEnd = blackEnd;
        this.children = children;
        this.clusterAlternate = clusterAlternate;
        this.customChanges = customChanges;
    }

    // ---------------------------------------------------------------------------------------------

    public static ParseChanges failure()
    {
        return new ParseChanges(-1, -1, null, null, null);
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

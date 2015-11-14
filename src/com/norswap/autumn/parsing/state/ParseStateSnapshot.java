package com.norswap.autumn.parsing.state;

import com.norswap.autumn.parsing.ParsingExpression;
import com.norswap.autumn.parsing.state.CustomState.Snapshot;

/**
 * See {@link ParseState}, "Snapshots" section.
 */
public final class ParseStateSnapshot
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public final int start;
    public final int blackStart;
    public final int end;
    public final int blackEnd;
    public final int treeChildrenCount;
    public final ParsingExpression clusterAlternate;
    public final Snapshot[] customSnapshots;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public ParseStateSnapshot(
        int start,
        int blackStart,
        int end,
        int blackEnd,
        int treeChildrenCount,
        ParsingExpression clusterAlternate,
        Snapshot[] customSnapshots)
    {
        this.start = start;
        this.blackStart = blackStart;
        this.end = end;
        this.blackEnd = blackEnd;
        this.treeChildrenCount = treeChildrenCount;
        this.clusterAlternate = clusterAlternate;
        this.customSnapshots = customSnapshots;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}

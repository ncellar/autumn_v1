package com.norswap.autumn.parsing;

import com.norswap.util.Array;

/**
 *
 */
public final class StandardStateSnapshot
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public final int start;
    public final int blackStart;
    public final int end;
    public final int blackEnd;
    public final int treeChildrenCount;
    public final int flags;
    public final Array<Seed> seeds;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public StandardStateSnapshot(int start, int blackStart, int end, int blackEnd, int treeChildrenCount, int flags, Array<Seed> seeds)
    {
        this.start = start;
        this.blackStart = blackStart;
        this.end = end;
        this.blackEnd = blackEnd;
        this.treeChildrenCount = treeChildrenCount;
        this.flags = flags;
        this.seeds = seeds;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}

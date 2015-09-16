package com.norswap.autumn.parsing.debugger;

import com.norswap.autumn.parsing.ParseState;
import com.norswap.autumn.parsing.Seed;
import com.norswap.util.Array;

public final class ExecutionNode
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    // Parse input

    public int start;
    public int blackStart;
    public int precedence;

    public int flags;
    public Array<Seed> seeds;

    public String accessor;
    public Array<String> tags;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public ExecutionNode(ParseState state)
    {
        this.start = state.start;
        this.blackStart = state.blackStart;
        this.precedence = state.precedence;
        this.flags = state.flags;
        this.seeds = state.seeds.clone();
        this.accessor = state.accessor;
        this.tags = state.tags.clone();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}

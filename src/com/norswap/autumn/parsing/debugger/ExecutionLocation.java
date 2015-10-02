package com.norswap.autumn.parsing.debugger;

import com.norswap.autumn.parsing.ParsingExpression;
import com.norswap.autumn.parsing.state.ParseState;
import com.norswap.util.Array;
import com.norswap.util.JArrays;
import com.norswap.util.JIntArrays;

/**
 *
 */
public final class ExecutionLocation implements ExecutionLocator
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public final ParsingExpression[] exprs;

    public final int[] invocationIndices;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public ExecutionLocation(ExecutionLocation parent, ParsingExpression expr, int index)
    {
        this.exprs = JArrays.concat(parent.exprs, expr);
        this.invocationIndices = JIntArrays.concat(parent.invocationIndices, index);
    }

    // ---------------------------------------------------------------------------------------------

    public ExecutionLocation(ParsingExpression root)
    {
        this.exprs = new ParsingExpression[]{ root };
        this.invocationIndices = new int[]{ 0 };
    }

    // ---------------------------------------------------------------------------------------------

    public ExecutionLocation(ParsingExpression[] exprs, int[] invocationIndices)
    {
        this.exprs = exprs;
        this.invocationIndices = invocationIndices;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public Match match(
        Object locatorState,
        ParseState parseState,
        Array<NodeInfo> spine,
        int index,
        ParsingExpression pe)
    {
        int depth = spine.size();

        return exprs[depth] == pe && invocationIndices[depth] == index
            ? exprs.length == depth + 1
                ? Match.MATCH
                : Match.PREFIX
            : Match.NONE;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}

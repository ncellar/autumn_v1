package com.norswap.autumn.parsing.debugger;

import com.norswap.autumn.parsing.expressions.common.ParsingExpression;
import com.norswap.util.JArrays;
import com.norswap.util.JIntArrays;

public final class ExecutionLocation
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
}

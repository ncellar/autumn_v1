package com.norswap.autumn.debugger;

import com.norswap.autumn.ParsingExpression;
import com.norswap.autumn.state.ParseInputs;
import com.norswap.util.annotations.Nullable;

/**
 *
 */
public final class NodeInfo
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public final ParsingExpression pe;

    public final int index;

    public final @Nullable ParseInputs inputs;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public NodeInfo(ParsingExpression pe, int index)
    {
        this.pe = pe;
        this.index = index;
        this.inputs = null;
    }

    // ---------------------------------------------------------------------------------------------

    public NodeInfo(ParsingExpression pe, int index, ParseInputs inputs)
    {
        this.pe = pe;
        this.index = index;
        this.inputs = inputs;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}

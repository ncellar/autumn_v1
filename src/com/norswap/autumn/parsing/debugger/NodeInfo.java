package com.norswap.autumn.parsing.debugger;

import com.norswap.autumn.parsing.ParsingExpression;
import com.norswap.autumn.parsing.state.ParseInputs;

/**
 *
 */
public final class NodeInfo
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public final ParsingExpression pe;

    public final int index;

    public final ParseInputs inputs;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    NodeInfo(ParsingExpression pe, int index, ParseInputs inputs)
    {
        this.pe = pe;
        this.index = index;
        this.inputs = inputs;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}

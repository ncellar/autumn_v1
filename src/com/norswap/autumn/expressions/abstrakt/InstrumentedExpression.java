package com.norswap.autumn.expressions.abstrakt;

import com.norswap.autumn.Parser;

public abstract class InstrumentedExpression extends UnaryParsingExpression
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public int parseDumb(Parser parser, int position)
    {
        return operand.parseDumb(parser, position);
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public boolean isPrintable()
    {
        return false;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}

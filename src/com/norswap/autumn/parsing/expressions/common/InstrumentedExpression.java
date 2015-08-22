package com.norswap.autumn.parsing.expressions.common;

import com.norswap.autumn.parsing.Parser;
import com.norswap.autumn.parsing.Registry;

public abstract class InstrumentedExpression extends UnaryParsingExpression
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    {
        setFlags(Registry.PEF_UNARY_INVISIBLE);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public int parseDumb(Parser parser, int position)
    {
        return operand.parseDumb(parser, position);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}

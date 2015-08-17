package com.norswap.autumn.parsing.expressions;

import com.norswap.autumn.parsing.ParseState;
import com.norswap.autumn.parsing.Parser;
import com.norswap.autumn.parsing.expressions.common.UnaryParsingExpression;

/**
 *
 */
public final class CaptureGroup extends UnaryParsingExpression
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public SetCaptureState setState;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void parse(Parser parser, ParseState state)
    {
        if (setState != null)
        {
            setState.set();
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}

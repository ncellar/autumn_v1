package com.norswap.autumn.expressions;

import com.norswap.autumn.state.ParseState;
import com.norswap.autumn.Parser;
import com.norswap.autumn.expressions.abstrakt.UnaryParsingExpression;

public final class Token extends UnaryParsingExpression
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void parse(Parser parser, ParseState state)
    {
        operand.parse(parser, state);

        if (state.failed())
        {
            state.fail(this);
            return;
        }

        int pos = parser.whitespace.parseDumb(parser, state.end);

        if (pos > 0)
            state.end = pos;
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public int parseDumb(Parser parser, int position)
    {
        position = operand.parseDumb(parser, position);

        if (position == -1)
            return -1;

        int pos = parser.whitespace.parseDumb(parser, position);
        return pos > 0 ? pos : position;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}

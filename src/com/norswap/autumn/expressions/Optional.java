package com.norswap.autumn.expressions;

import com.norswap.autumn.state.ParseState;
import com.norswap.autumn.Parser;
import com.norswap.autumn.expressions.abstrakt.UnaryParsingExpression;
import com.norswap.autumn.graph.Nullability;

/**
 * Invokes its operand on the input.
 *
 * Always succeeds.
 *
 * On success, its end position is the end position of it operand if it
 * succeeded, or its start position otherwise.
 */
public final class Optional extends UnaryParsingExpression
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void parse(Parser parser, ParseState state)
    {
        operand.parse(parser, state);

        if (state.failed())
        {
            state.discard();
        }
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public int parseDumb(Parser parser, int position)
    {
        int result = operand.parseDumb(parser, position);

        return result != -1
            ? result
            : position;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public Nullability nullability()
    {
        return Nullability.yes(this);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}

package com.norswap.autumn.expressions;

import com.norswap.autumn.state.ParseState;
import com.norswap.autumn.Parser;
import com.norswap.autumn.expressions.abstrakt.UnaryParsingExpression;
import com.norswap.autumn.graph.Nullability;

/**
 * Invokes its operand on the input, then resets the input to its initial position.
 *
 * Succeeds iff its operand succeeds.
 *
 * On success, its end position is its start position.
 */
public final class Not extends UnaryParsingExpression
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void parse(Parser parser, ParseState state)
    {
        state.recordErrors = false;

        operand.parse(parser, state);

        if (state.succeeded())
        {
            state.fail(this);
        }
        else
        {
            state.discard();
        }

        state.recordErrors = true;
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public int parseDumb(Parser parser, int position)
    {
        return operand.parseDumb(parser, position) == -1
            ? position
            : -1;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public Nullability nullability()
    {
        return Nullability.yes(this);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}

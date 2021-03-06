package com.norswap.autumn.expressions;

import com.norswap.autumn.expressions.abstrakt.NaryParsingExpression;
import com.norswap.autumn.state.ParseState;
import com.norswap.autumn.Parser;
import com.norswap.autumn.ParsingExpression;
import com.norswap.autumn.graph.Nullability;

import java.util.function.Predicate;

/**
 * Invokes all its operands at its initial input position, until one succeeds.
 *
 * Succeeds iff one operand succeeds.
 *
 * On success, its end position is that of the operand that succeeded.
 */
public final class Choice extends NaryParsingExpression
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void parse(Parser parser, ParseState state)
    {
        for (ParsingExpression operand : operands)
        {
            operand.parse(parser, state);

            if (state.succeeded())
            {
                return;
            }
            else
            {
                state.discard();
            }
        }

        state.fail(this);
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public int parseDumb(Parser parser, int position)
    {
        for (ParsingExpression operand : operands)
        {
            int result = operand.parseDumb(parser, position);

            if (result != - 1)
            {
                return result;
            }
        }

        return -1;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public ParsingExpression[] firsts(Predicate<ParsingExpression> nullability)
    {
        return operands;
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public Nullability nullability()
    {
        return Nullability.any(this, operands);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}

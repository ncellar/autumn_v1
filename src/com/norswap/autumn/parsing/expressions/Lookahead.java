package com.norswap.autumn.parsing.expressions;

import com.norswap.autumn.parsing.Grammar;
import com.norswap.autumn.parsing.state.ParseState;
import com.norswap.autumn.parsing.Parser;
import com.norswap.autumn.parsing.expressions.abstrakt.UnaryParsingExpression;
import com.norswap.autumn.parsing.graph.Nullability;

/**
 * Invokes its operand on the input, then resets the input to its initial position.
 *
 * Succeeds iff its operand succeeds.
 *
 * On success, its end position is its start position.
 */
public final class Lookahead extends UnaryParsingExpression
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void parse(Parser parser, ParseState state)
    {
        operand.parse(parser, state);

        if (state.succeeded())
        {
            state.discard();
        }
        else
        {
            state.fail(this);
        }
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public int parseDumb(Parser parser, int position)
    {
        return operand.parseDumb(parser, position) == -1
            ? -1
            : position;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public Nullability nullability(Grammar grammar)
    {
        return Nullability.yes(this);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}

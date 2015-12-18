package com.norswap.autumn.expressions;

import com.norswap.autumn.state.ParseChanges;
import com.norswap.autumn.state.ParseState;
import com.norswap.autumn.Parser;
import com.norswap.autumn.expressions.abstrakt.UnaryParsingExpression;

public final class Memo extends UnaryParsingExpression
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void parse(Parser parser, ParseState state)
    {
        ParseChanges changes = state.memo.get(operand, state);

        if (changes != null)
        {
            state.merge(changes);
            return;
        }

        operand.parse(parser, state);
        state.memo.memoize(operand, state, state.extract());
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}

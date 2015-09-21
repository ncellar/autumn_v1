package com.norswap.autumn.parsing.expressions;

import com.norswap.autumn.parsing.ParseChanges;
import com.norswap.autumn.parsing.ParseState;
import com.norswap.autumn.parsing.Parser;
import com.norswap.autumn.parsing.expressions.common.UnaryParsingExpression;

public final class Memo extends UnaryParsingExpression
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void parse(Parser parser, ParseState state)
    {
        ParseChanges changes = parser.memoHandler.get(this, state);

        if (changes != null)
        {
            state.merge(changes);
            return;
        }

        operand.parse(parser, state);
        parser.memoHandler.memoize(operand, state, state.extract());
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}

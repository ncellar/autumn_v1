package com.norswap.autumn.parsing.expressions;

import com.norswap.autumn.parsing.state.BottomUpState;
import com.norswap.autumn.parsing.state.ParseState;
import com.norswap.autumn.parsing.Parser;
import com.norswap.autumn.parsing.expressions.abstrakt.UnaryParsingExpression;

public final class WithMinPrecedence extends UnaryParsingExpression
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public int minPrecedence;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void parse(Parser parser, ParseState state)
    {
        BottomUpState.Precedence precedence = state.bottomup.getCurrentPrecedence();
        int oldPrecedence = precedence.value;
        precedence.value = minPrecedence;
        operand.parse(parser, state);
        precedence.value = oldPrecedence;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public String ownDataString()
    {
        return "minPrecedence: " + minPrecedence;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}

package com.norswap.autumn.parsing.expressions;

import com.norswap.autumn.parsing.extensions.BottomupExtension;
import com.norswap.autumn.parsing.state.BottomUpState;
import com.norswap.autumn.parsing.state.ParseState;
import com.norswap.autumn.parsing.Parser;
import com.norswap.autumn.parsing.expressions.abstrakt.UnaryParsingExpression;

import static com.norswap.util.Caster.cast;

public final class WithMinPrecedence extends UnaryParsingExpression
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public int minPrecedence;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void parse(Parser parser, ParseState state)
    {
        BottomUpState bstate = cast(state.customStates[BottomupExtension.INDEX]);
        BottomUpState.Precedence precedence = bstate.getCurrentPrecedence();

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

package com.norswap.autumn.test.languages.clike;

import com.norswap.autumn.Parser;
import com.norswap.autumn.ParsingExpression;
import com.norswap.autumn.expressions.abstrakt.UnaryParsingExpression;
import com.norswap.autumn.state.ParseState;

public class TypeUse extends UnaryParsingExpression
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    TypeUse(ParsingExpression operand)
    {
        this.operand = operand;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void parse(Parser parser, ParseState state)
    {
        operand.parse(parser, state);

        if (state.failed())
        {
            state.fail();
            return;
        }

        CLikeState clstate = (CLikeState) state.customStates[CLikeExtension.INDEX];
        String typeName = state.tree.children().last().value;

        if (!clstate.items.contains(typeName))
        {
            state.fail();
            System.err.println(String.format("unsuccessful type use (%s) at: %s",
                typeName,
                parser.source.position(state.start)));
        }
        else
        {
            System.err.println(String.format("successful type use (%s) at: %s",
                typeName,
                parser.source.position(state.start)));
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}

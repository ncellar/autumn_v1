package com.norswap.autumn.test.languages;

import com.norswap.autumn.Parser;
import com.norswap.autumn.ParsingExpression;
import com.norswap.autumn.expressions.abstrakt.UnaryParsingExpression;
import com.norswap.autumn.state.ParseState;
import java.util.function.Function;

public final class Monadic extends UnaryParsingExpression
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public final Function<ParseState, ParsingExpression> f;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public Monadic(ParsingExpression operand, Function<ParseState, ParsingExpression> f)
    {
        this.operand = operand;
        this.f = f;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void parse(Parser parser, ParseState state)
    {
        operand.parse(parser, state);

        if (state.failed()) {
            state.fail();
            return;
        }

        f.apply(state).parse(parser, state);

        if (state.failed())
            state.fail();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}

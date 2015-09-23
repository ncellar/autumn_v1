package com.norswap.autumn.parsing.debug;

import com.norswap.autumn.parsing.state.ParseState;
import com.norswap.autumn.parsing.Parser;
import com.norswap.autumn.parsing.expressions.abstrakt.InstrumentedExpression;

import static com.norswap.autumn.parsing.debug.Debugger.DEBUGGER;

public class Breakpoint extends InstrumentedExpression
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void parse(Parser parser, ParseState state)
    {
        DEBUGGER.pushFrame(operand, state);
        DEBUGGER.suspend(this, state);

        operand.parse(parser, state);
        DEBUGGER.popFrame();
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public int parseDumb(Parser parser, int position)
    {
        return operand.parseDumb(parser, position);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}

package com.norswap.autumn.parsing.debugger;

import com.norswap.autumn.parsing.ParseState;
import com.norswap.autumn.parsing.Parser;
import com.norswap.autumn.parsing.expressions.common.InstrumentedExpression;

import static com.norswap.autumn.parsing.debugger.Debugger.DEBUGGER;

/**
 * While debugging, a breakpoint wraps all expressions in the original graph.
 */
public final class Breakpoint extends InstrumentedExpression
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void parse(Parser parser, ParseState state)
    {
        operand.parse(parser, state);
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public int parseDumb(Parser parser, int position)
    {
        return operand.parseDumb(parser, position);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}

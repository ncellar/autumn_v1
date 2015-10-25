package com.norswap.autumn.parsing.debugger.store;

import com.norswap.autumn.parsing.ParsingExpression;
import com.norswap.autumn.parsing.debugger.Debugger;
import com.norswap.autumn.parsing.state.ParseState;
import com.norswap.autumn.parsing.Parser;
import com.norswap.autumn.parsing.expressions.abstrakt.InstrumentedExpression;

/**
 * While debugging, a breakpoint wraps all expressions in the original graph.
 */
public final class Breakpoint extends InstrumentedExpression
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private final DebuggerStore store;
    private final Debugger debugger;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public Breakpoint(ParsingExpression operand, DebuggerStore store)
    {
        this.operand = operand;
        this.store = store;
        this.debugger = store.debugger;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void parse(Parser parser, ParseState state)
    {
        Object penultimate = store.before(operand, state);
        operand.parse(parser, state);
        store.after(penultimate, operand, state);
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public int parseDumb(Parser parser, int position)
    {
        return operand.parseDumb(parser, position);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}

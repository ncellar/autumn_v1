package com.norswap.autumn.parsing.debugger;

import com.norswap.autumn.parsing.ParseState;
import com.norswap.autumn.parsing.Parser;
import com.norswap.autumn.parsing.expressions.common.InstrumentedExpression;

/**
 * While debugging, a breakpoint wraps all expressions in the original graph.
 */
public final class Breakpoint extends InstrumentedExpression
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void parse(Parser parser, ParseState state)
    {
        WindowModel window = parser.get(Debugger.DEBUG_WINDOW);

        if (window.depth == 0)
        {

        }
        else if (window.depth == 1)
        {

        }

        ++window.depth;
        operand.parse(parser, state);
        --window.depth;
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public int parseDumb(Parser parser, int position)
    {
        return operand.parseDumb(parser, position);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}

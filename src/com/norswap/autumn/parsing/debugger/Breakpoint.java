package com.norswap.autumn.parsing.debugger;

import com.norswap.autumn.parsing.ParseResult;
import com.norswap.autumn.parsing.ParsingExpression;
import com.norswap.autumn.parsing.debugger.ExecutionLocator.Match;
import com.norswap.autumn.parsing.state.ParseInputs;
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
        Match last = store.last;

        if (last == Match.OVER)
        {
            operand.parse(parser, state);
            return;
        }

        Match match = store.match(operand, state);
        store.last = match;

        switch (match)
        {
            case MATCH:
                state.errors.requestErrorRecordPoint();
                store.spine.push(new NodeInfo(operand, store.index, state.inputs(operand)));
                break;

            case PREFIX:
                store.spine.push(new NodeInfo(operand, store.index, state.inputs(operand)));
                store.index = 0;
                break;

            case NONE:
            case CHILD:
                break;


            case OVER:
                return; // -> !!! <-
        }

        operand.parse(parser, state);

        switch (match)
        {
            case MATCH:
                store.targetInvocation = invocation(state);
                state.errors.dismissErrorRecordPoint();
                store.last = Match.OVER;
                return; // -> !!! <-

            case PREFIX:
                break;

            case NONE:
                if (last == Match.PREFIX) {
                    ++ store.index;
                }
                break;

            case CHILD:
                store.targetChildrenInvocation.add(invocation(state));
                break;
        }

        store.last = last;

    }

    // ---------------------------------------------------------------------------------------------

    private Invocation invocation(ParseState state)
    {
        ParseInputs inputs = state.inputs(operand);

        return new Invocation(
            inputs,
            new ParseResult(
                store.debugger.parser.source,
                inputs,
                state.extract(),
                state.errors.changes()));
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public int parseDumb(Parser parser, int position)
    {
        return operand.parseDumb(parser, position);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}

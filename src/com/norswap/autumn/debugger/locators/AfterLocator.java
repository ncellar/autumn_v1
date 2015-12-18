package com.norswap.autumn.debugger.locators;

import com.norswap.autumn.ParsingExpression;
import com.norswap.autumn.debugger.store.LocateStatus;
import com.norswap.autumn.debugger.NodeInfo;
import com.norswap.autumn.state.ParseState;
import com.norswap.util.Array;

/**
 *
 */
public final class AfterLocator implements ExecutionLocator
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public final ExecutionLocator beforeLocator;
    public final ExecutionLocator mainLocator;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public AfterLocator(ExecutionLocator beforeLocator, ExecutionLocator mainLocator)
    {
        this.beforeLocator = beforeLocator;
        this.mainLocator = mainLocator;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private static class State
    {
        Object operandState;
        boolean beforeMatched = false;
        boolean beforePromoted = false;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public State newState()
    {
        State out = new State();
        out.operandState = beforeLocator.newState();
        return out;
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public LocateStatus match(
        Object locatorState,
        ParseState parseState,
        Array<NodeInfo> spine,
        int index,
        ParsingExpression pe)
    {
        State state = (State) locatorState;

        if (state.beforeMatched)
        {
            return mainLocator.match(state.operandState, parseState, spine, index, pe);
        }
        else
        {
            LocateStatus status =
                beforeLocator.match(state.operandState, parseState, spine, index, pe);

            if (status == LocateStatus.MATCH)
            {
                state.operandState = mainLocator.newState();
                state.beforeMatched = true;
                return LocateStatus.DEAD_END;
            }
            else
            {
                return status;
            }
        }
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public boolean promote(
        LocateStatus oldStatus,
        Object locatorState,
        ParseState parseState,
        Array<NodeInfo> spine,
        int index,
        ParsingExpression pe)
    {
        State state = (State) locatorState;

        return state.beforeMatched && state.beforePromoted
            ? mainLocator.promote(oldStatus, state.operandState, parseState, spine, index, pe)
            : state.beforeMatched
                ? (state.beforePromoted = true) != true // false
                : false;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}

package com.norswap.autumn.parsing.debugger.locators;

import com.norswap.autumn.parsing.ParsingExpression;
import com.norswap.autumn.parsing.debugger.store.LocateStatus;
import com.norswap.autumn.parsing.debugger.NodeInfo;
import com.norswap.autumn.parsing.state.ParseState;
import com.norswap.util.Array;

public final class BeforeLocator implements ExecutionLocator
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public final ExecutionLocator mainLocator;
    public final ExecutionLocator afterLocator;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public BeforeLocator(ExecutionLocator afterLocator, ExecutionLocator mainLocator)
    {
        this.afterLocator = afterLocator;
        this.mainLocator = mainLocator;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private static class State
    {
        Object mainLocatorState;
        Object afterLocatorState;
        boolean afterMatched = false;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public State newState()
    {
        State out = new State();
        out.mainLocatorState = mainLocator.newState();
        out.afterLocatorState = afterLocator.newState();
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

        if (state.afterMatched) {
            return LocateStatus.DEAD_END;
        }

        LocateStatus status =
            afterLocator.match(state.afterLocatorState, parseState, spine, index, pe);

        if (status == LocateStatus.MATCH)
        {
            state.afterMatched = true;
            state.afterLocatorState = null;
            state.mainLocatorState = null;
            return LocateStatus.DEAD_END;
        }

        return mainLocator.match(state.mainLocatorState, parseState, spine, index, pe);
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

        return state.afterMatched
            ? false
            : mainLocator.promote(oldStatus, state.mainLocatorState, parseState, spine, index, pe);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}

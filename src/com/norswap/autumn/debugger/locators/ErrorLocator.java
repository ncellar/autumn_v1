package com.norswap.autumn.debugger.locators;

import com.norswap.autumn.ParsingExpression;
import com.norswap.autumn.debugger.NodeInfo;
import com.norswap.autumn.debugger.store.LocateStatus;
import com.norswap.autumn.state.ParseState;
import com.norswap.autumn.errors.ErrorLocation;
import com.norswap.util.Array;

/**
 *
 */
public final class ErrorLocator implements ExecutionLocator
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public final ErrorLocation location;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public ErrorLocator(ErrorLocation location)
    {
        this.location = location;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private static class State
    {
        int matched = 0;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public Object newState()
    {
        return new State();
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

        assert state.matched < index;

        return pe == location.pe
            && parseState.end >= location.position
            && ++ state.matched == location.position
                ? LocateStatus.MATCH
                : LocateStatus.POSSIBLE_PREFIX;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}

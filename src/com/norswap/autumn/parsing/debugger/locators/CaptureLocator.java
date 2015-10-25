package com.norswap.autumn.parsing.debugger.locators;

import com.norswap.autumn.parsing.ParsingExpression;
import com.norswap.autumn.parsing.debugger.store.LocateStatus;
import com.norswap.autumn.parsing.debugger.NodeInfo;
import com.norswap.autumn.parsing.expressions.Capture;
import com.norswap.autumn.parsing.state.ParseState;
import com.norswap.util.Array;

/**
 *
 */
public class CaptureLocator implements ExecutionLocator
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public final Capture capture;
    private final int index;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public CaptureLocator(Capture capture, int index)
    {
        this.capture = capture;
        this.index = index;
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

        return pe instanceof Capture
            ?  ++ state.matched == index
                ? LocateStatus.MATCH
                : ((Capture) pe).shouldCapture()
                    ? LocateStatus.DEAD_END
                    : LocateStatus.POSSIBLE_PREFIX
            : LocateStatus.POSSIBLE_PREFIX;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}

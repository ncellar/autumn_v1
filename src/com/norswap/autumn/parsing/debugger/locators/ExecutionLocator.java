package com.norswap.autumn.parsing.debugger.locators;

import com.norswap.autumn.parsing.ParsingExpression;
import com.norswap.autumn.parsing.debugger.store.LocateStatus;
import com.norswap.autumn.parsing.debugger.NodeInfo;
import com.norswap.autumn.parsing.state.ParseState;
import com.norswap.util.Array;

/**
 *
 */
public interface ExecutionLocator
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    default Object newState() { return null; }

    // ---------------------------------------------------------------------------------------------

    LocateStatus match(
        Object locatorState,
        ParseState parseState,
        Array<NodeInfo> spine,
        int index,
        ParsingExpression pe);

    // ---------------------------------------------------------------------------------------------

    default boolean promote(
        LocateStatus oldStatus,
        Object locatorState,
        ParseState parseState,
        Array<NodeInfo> spine,
        int index,
        ParsingExpression pe)
    {
        return false;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}

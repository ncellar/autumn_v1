package com.norswap.autumn.parsing.debugger;

import com.norswap.autumn.parsing.ParsingExpression;
import com.norswap.autumn.parsing.state.ParseState;
import com.norswap.util.Array;

/**
 *
 */
public interface ExecutionLocator
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    enum Match { MATCH, PREFIX, NONE, CHILD, OVER }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    default Object newState() { return null; }

    // ---------------------------------------------------------------------------------------------

    Match match(
        Object locatorState,
        ParseState parseState,
        Array<NodeInfo> spine,
        int index,
        ParsingExpression pe);

    ////////////////////////////////////////////////////////////////////////////////////////////////
}

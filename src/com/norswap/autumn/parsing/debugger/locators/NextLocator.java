package com.norswap.autumn.parsing.debugger.locators;

import com.norswap.autumn.parsing.ParsingExpression;
import com.norswap.autumn.parsing.debugger.store.LocateStatus;
import com.norswap.autumn.parsing.debugger.NodeInfo;
import com.norswap.autumn.parsing.state.ParseState;
import com.norswap.util.Array;
import com.norswap.util.annotations.Nullable;

/**
 *
 */
public final class NextLocator implements ExecutionLocator
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public final int position;
    public final @Nullable ParsingExpression pe;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public NextLocator(int position, ParsingExpression pe)
    {
        this.position = position;
        this.pe = pe;
    }

    // ---------------------------------------------------------------------------------------------

    public NextLocator(int position)
    {
        this.position = position;
        this.pe = null;
    }

    // ---------------------------------------------------------------------------------------------

    public NextLocator(ParsingExpression pe)
    {
        this.position = -1;
        this.pe = pe;
    }

    // ---------------------------------------------------------------------------------------------

    public NextLocator()
    {
        this.position = -1;
        this.pe = null;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public LocateStatus match(
        Object locatorState,
        ParseState parseState,
        Array<NodeInfo> spine,
        int index,
        ParsingExpression pe)
    {
        if (position >= 0 && parseState.end <= position) {
            return LocateStatus.DEAD_END;
        }

        if (this.pe != null && this.pe != pe) {
            return LocateStatus.POSSIBLE_PREFIX;
        }

        return LocateStatus.MATCH;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}

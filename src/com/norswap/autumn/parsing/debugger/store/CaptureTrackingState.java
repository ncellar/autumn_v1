package com.norswap.autumn.parsing.debugger.store;

import com.norswap.autumn.parsing.state.CustomState;
import com.norswap.autumn.parsing.state.ParseState;
import com.norswap.util.Array;

/**
 *
 */
public class CaptureTrackingState implements CustomState
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    Array<CaptureList> captures = new Array<>();

    int capturesCount = 0;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public Object inputs(ParseState state)
    {
        return null;
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public void load(Object inputs)
    {
        // TODO
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public Object snapshot(ParseState state)
    {
        return new Integer(capturesCount);
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public void restore(Object snapshot, ParseState state)
    {
        capturesCount = (int) snapshot;
        captures.truncate(capturesCount);
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public void uncommit(Object snapshot, ParseState state)
    {
        capturesCount = (int) snapshot;
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public void discard(ParseState state)
    {
        captures.truncate(capturesCount);
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public void commit(ParseState state)
    {
        capturesCount = captures.size();
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public Object extract(ParseState state)
    {
        return captures.copyFromIndex(capturesCount);
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    @SuppressWarnings("unchecked")
    public void merge(Object changes, ParseState state)
    {
        captures.addAll((Array<CaptureList>) changes);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}

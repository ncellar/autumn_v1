package com.norswap.autumn.parsing.debugger.store;

import com.norswap.autumn.parsing.state.CustomChanges;
import com.norswap.autumn.parsing.state.CustomState;
import com.norswap.util.Array;

/**
 *
 */
public class CaptureTrackingState implements CustomState
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    static class Changes implements CustomChanges
    {
        Array<CaptureList> added;

        @Override
        public Result result(CustomState.Inputs inputs)
        {
            Result result = new Result();
            result.captures = added;
            return result;
        }
    }

    // ---------------------------------------------------------------------------------------------

    static class Result implements CustomState.Result
    {
        Array<CaptureList> captures;
    }

    // ---------------------------------------------------------------------------------------------

    static class Snapshot implements CustomState.Snapshot
    {
        int count;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    Array<CaptureList> captures = new Array<>();

    int capturesCount = 0;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void commit()
    {
        capturesCount = captures.size();
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public void discard()
    {
        captures.truncate(capturesCount);
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public Changes extract()
    {
        Changes changes = new Changes();
        changes.added = captures.copyFromIndex(capturesCount);
        return changes;
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public void merge(CustomChanges changes)
    {
        Changes c = (Changes) changes;
        captures.addAll(c.added);
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public Snapshot snapshot()
    {
        Snapshot snapshot = new Snapshot();
        snapshot.count = capturesCount;
        return snapshot;
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public void restore(CustomState.Snapshot snapshot)
    {
        Snapshot s = (Snapshot) snapshot;
        capturesCount = s.count;
        captures.truncate(capturesCount);
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public void uncommit(CustomState.Snapshot snapshot)
    {
        Snapshot s = (Snapshot) snapshot;
        capturesCount = s.count;
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public Inputs inputs()
    {
        return null;
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public Result result()
    {
        Result result = new Result();
        result.captures = captures;
        return result;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}

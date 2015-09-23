package com.norswap.autumn.parsing;

import com.norswap.autumn.parsing.state.CustomState;

public final class DefaultErrorState implements ErrorState
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void commit()
    {

    }

    @Override
    public void discard()
    {

    }

    @Override
    public Changes extract()
    {
        return null;
    }

    @Override
    public void merge(Changes changes)
    {

    }

    @Override
    public Snapshot snapshot()
    {
        return null;
    }

    @Override
    public void restore(Snapshot snapshot)
    {

    }

    @Override
    public void uncommit(Snapshot snapshot)
    {

    }

    @Override
    public Inputs inputs()
    {
        return null;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}

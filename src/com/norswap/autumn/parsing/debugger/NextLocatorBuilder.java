package com.norswap.autumn.parsing.debugger;

import com.norswap.autumn.parsing.ParsingExpression;

/**
 * Builder pattern for {@link NextLocator}.
 */
public final class NextLocatorBuilder
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private ExecutionLocator after;
    private ExecutionLocator before;
    private int position = -1;
    private ParsingExpression pe;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public NextLocatorBuilder after(ExecutionLocator after)
    {
        this.after = after;
        return this;
    }

    // ---------------------------------------------------------------------------------------------

    public NextLocatorBuilder before(ExecutionLocator before)
    {
        this.before = before;
        return this;
    }

    // ---------------------------------------------------------------------------------------------

    public NextLocatorBuilder position(int position)
    {
        this.position = position;
        return this;
    }

    // ---------------------------------------------------------------------------------------------

    public NextLocatorBuilder pe(ParsingExpression pe)
    {
        this.pe = pe;
        return this;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public NextLocator build()
    {
        return new NextLocator(after, before, position, pe);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}

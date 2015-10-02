package com.norswap.autumn.parsing.debugger;

import com.norswap.autumn.parsing.Parser;

/**
 *
 */
public final class Debugger
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public final Parser parser;

    public final DebuggerStore store;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public Debugger(Parser parser, DebuggerStore store)
    {
        this.parser = parser;
        this.store = store;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}

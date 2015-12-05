package com.norswap.autumn.parsing.debugger;

import com.norswap.autumn.parsing.debugger.locators.AfterLocator;
import com.norswap.autumn.parsing.debugger.locators.BeforeLocator;
import com.norswap.autumn.parsing.debugger.locators.ErrorLocator;
import com.norswap.autumn.parsing.debugger.locators.ExecutionLocator;
import com.norswap.autumn.parsing.debugger.locators.NextLocator;
import com.norswap.autumn.parsing.debugger.store.DebuggerStore;
import com.norswap.autumn.parsing.errors.ErrorLocation;
import com.norswap.util.Array;

/**
 *
 */
public final class WindowModel
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public final Debugger debugger;
    public final ExecutionLocation location;
    public final Invocation invocation;
    public final Array<Invocation> childrenInvocation;
    public final Array<NodeInfo> spine;
    public final Array<CaptureInfo> captures;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public WindowModel(
        Debugger debugger,
        ExecutionLocation location,
        Invocation invocation,
        Array<Invocation> childrenInvocation,
        Array<NodeInfo> spine,
        Array<CaptureInfo> captures)
    {
        this.debugger = debugger;
        this.location = location;
        this.invocation = invocation;
        this.childrenInvocation = childrenInvocation;
        this.spine = spine;
        this.captures = captures;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public WindowModel goUpTree(ExecutionLocator locator)
    {
        DebuggerStore store = debugger.store;
        WindowModel out = null;

        for (int i = spine.size() - 1; out == null && i >= 0; -- i)
        {
            out = store.windowFor(locator, spine.copyOfPrefix(i), spine.get(i).inputs);
        }

        if (out == null) {
            throw new DebuggerException("Couldn't find the specified parsing expression invocation.");
        }

        return out;
    }

    // ---------------------------------------------------------------------------------------------

    public WindowModel stepOver()
    {
        return goUpTree(new AfterLocator(location, new NextLocator()));
    }

    // ---------------------------------------------------------------------------------------------

    public WindowModel stepBack()
    {
        return goUpTree(new BeforeLocator(location, new NextLocator()));
    }

    // ---------------------------------------------------------------------------------------------

    public WindowModel stepOver(int position)
    {
        return goUpTree(new AfterLocator(location, new NextLocator(position)));
    }

    // ---------------------------------------------------------------------------------------------

    public WindowModel stepBack(int position)
    {
        return goUpTree(new BeforeLocator(location, new NextLocator(position)));
    }

    // ---------------------------------------------------------------------------------------------

    public WindowModel stepInto()
    {
        assert !childrenInvocation.isEmpty();

        return debugger.store.windowFor(
            new NextLocator(),
            spine.clone(),
            invocation.inputs);
    }
    // ---------------------------------------------------------------------------------------------

    public WindowModel stepToError(ErrorLocation errorLocation)
    {
        return debugger.store.windowFor(
            new ErrorLocator(errorLocation),
            spine.clone(),
            invocation.inputs);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}

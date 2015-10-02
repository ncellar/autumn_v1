package com.norswap.autumn.parsing.debugger;

import com.norswap.autumn.parsing.state.ParseInputs;
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

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public WindowModel(
        Debugger debugger,
        ExecutionLocation location,
        Invocation invocation,
        Array<Invocation> childrenInvocation,
        Array<NodeInfo> spine)
    {
        this.debugger = debugger;
        this.location = location;
        this.invocation = invocation;
        this.childrenInvocation = childrenInvocation;
        this.spine = spine;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public WindowModel goUpTree(ExecutionLocator locator)
    {
        DebuggerStore store = debugger.store;
        store.targetInvocation = null;

        int i = spine.size() - 1;
        while (store.targetInvocation == null && i >= 0)
        {
            store.setTarget(locator, spine.copyOfPrefix(i));
            ParseInputs inputs = spine.get(i).inputs;
            debugger.parser.parse(inputs);
            -- i;
        }

        if (store.targetInvocation == null)
        {
            throw new DebuggerException("Couldn't find the specified parsing expression invocation.");
        }

        return windowFromStore();
    }

    // ---------------------------------------------------------------------------------------------

    public WindowModel stepOver()
    {
        return goUpTree(NextLocator.builder()
            .after(location)
            .build());
    }

    // ---------------------------------------------------------------------------------------------

    public WindowModel stepBack()
    {
        return goUpTree(NextLocator.builder()
            .before(location)
            .build());
    }

    // ---------------------------------------------------------------------------------------------

    public WindowModel stepOver(int position)
    {
        return goUpTree(NextLocator.builder()
            .position(position)
            .after(location)
            .build());
    }

    // ---------------------------------------------------------------------------------------------

    public WindowModel stepBack(int position)
    {
        return goUpTree(NextLocator.builder()
            .position(position)
            .before(location)
            .build());
    }

    // ---------------------------------------------------------------------------------------------

    public WindowModel stepInto()
    {
        assert !childrenInvocation.isEmpty();

        DebuggerStore store = debugger.store;
        store.setTarget(NextLocator.builder().build(), spine.clone());
        debugger.parser.parse(invocation.inputs);

        return windowFromStore();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    WindowModel windowFromStore()
    {
        DebuggerStore store = debugger.store;

        return new WindowModel(
            debugger,
            store.location(),
            store.targetInvocation,
            store.targetChildrenInvocation,
            store.spine);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}

package com.norswap.autumn.parsing.debugger;

public final class WindowModel
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public ExecutionLocation root;

    public int depth = 0;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public WindowModel(ExecutionLocation root)
    {
        this.root = root;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}

package com.norswap.autumn.parsing.debugger.store;

import com.norswap.autumn.parsing.expressions.Capture;
import com.norswap.util.annotations.Nullable;

public final class CaptureList
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public final Capture capture;
    public final int index;
    public final @Nullable CaptureList next;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    CaptureList(Capture capture, int index)
    {
        this.capture = capture;
        this.index = index;
        this.next = null;
    }

    // ---------------------------------------------------------------------------------------------

    CaptureList(Capture capture, int index, CaptureList next)
    {
        this.capture = capture;
        this.index = index;
        this.next = next;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}

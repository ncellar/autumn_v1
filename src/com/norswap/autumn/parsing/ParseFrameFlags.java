package com.norswap.autumn.parsing;

public class ParseFrameFlags
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private static int i = 0;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Indicates that we should not capture any parsing output.
     */
    public static int PFF_DONT_CAPTURE
        = 1 << i++;

    /**
     * Indicates that a cut operator has been encountered while parsing the children of the
     * parsing expression corresponding to this frame.
     */
    public static int PFF_CUT
        = 1 << i++;

    /**
     * Indicates that the expression owning this frame can be cut.
     */
    public static int PFF_CUTTABLE
        = 1 << i++;

    ////////////////////////////////////////////////////////////////////////////////////////////////
}

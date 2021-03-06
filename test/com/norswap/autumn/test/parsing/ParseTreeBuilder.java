package com.norswap.autumn.test.parsing;

import com.norswap.autumn.capture.ParseTree;

public class ParseTreeBuilder
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public static ParseTree $(ParseTree... children)
    {
        return new ParseTree(null, null, null, null, children);
    }

    // ---------------------------------------------------------------------------------------------

    public static ParseTree $(String accessor, ParseTree... children)
    {
        return new ParseTree(accessor, null, null, null, children);
    }

    // ---------------------------------------------------------------------------------------------

    public static ParseTree $(String accessor, String value, ParseTree... children)
    {
        return new ParseTree(accessor, value, null, null, children);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}

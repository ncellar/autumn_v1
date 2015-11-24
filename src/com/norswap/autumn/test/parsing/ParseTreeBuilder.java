package com.norswap.autumn.test.parsing;

import com.norswap.autumn.parsing.capture.ParseTree;
import com.norswap.util.Array;

public class ParseTreeBuilder
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public static ParseTree $(ParseTree... children)
    {
        return new ParseTree(null, null, null, null, nullOrArray(children));
    }

    // ---------------------------------------------------------------------------------------------

    public static ParseTree $(String accessor, ParseTree... children)
    {
        return new ParseTree(accessor, null, null, null, nullOrArray(children));
    }

    // ---------------------------------------------------------------------------------------------

    public static ParseTree $(String accessor, String value, ParseTree... children)
    {
        return new ParseTree(accessor, value, null, null, nullOrArray(children));
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private static Array<ParseTree> nullOrArray(ParseTree[] children)
    {
        return children == null || children.length == 0 ? null : new Array<>(children);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}

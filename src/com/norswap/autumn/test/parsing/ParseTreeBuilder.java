package com.norswap.autumn.test.parsing;

import com.norswap.autumn.parsing.ParseTree;
import com.norswap.util.Array;

public class ParseTreeBuilder
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public static ParseTree $(ParseTree... children)
    {
        ParseTree tree = new ParseTree(null, new Array<>(), false);
        tree.children = new Array<>(children);
        return tree;
    }

    // ---------------------------------------------------------------------------------------------

    public static ParseTree $(String accessor, ParseTree... children)
    {
        ParseTree tree = new ParseTree(accessor, new Array<>(), false);
        tree.children = new Array<>(children);
        return tree;
    }

    // ---------------------------------------------------------------------------------------------

    public static ParseTree $(String accessor, String value, ParseTree... children)
    {
        ParseTree tree = $(accessor, children);
        tree.value = value;
        return tree;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}

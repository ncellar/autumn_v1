package com.norswap.autumn.parsing.expressions.capture;

import com.norswap.util.Array;

import java.util.Set;

public final class ParseTreeTransient
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public String accessor;
    public String value;
    public String kind;
    public Set<String> tags;
    public Array<ParseTree> children;

    ////////////////////////////////////////////////////////////////////////////////////////////////
}

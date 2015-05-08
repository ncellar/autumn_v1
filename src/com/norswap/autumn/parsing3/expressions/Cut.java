package com.norswap.autumn.parsing3.expressions;

import com.norswap.autumn.parsing3.ParseInput;
import com.norswap.autumn.parsing3.Parser;
import com.norswap.autumn.parsing3.ParsingExpression;

public final class Cut extends ParsingExpression
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public String name;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void parse(Parser parser, ParseInput input)
    {
        input.cuts.add(name);
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public void appendTo(StringBuilder builder)
    {
        builder.append("cut(\"");
        builder.append(name);
        builder.append("\")");
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}

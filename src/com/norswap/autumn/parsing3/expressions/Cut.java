package com.norswap.autumn.parsing3.expressions;

import com.norswap.autumn.parsing3.ParseInput;
import com.norswap.autumn.parsing3.Parser;
import com.norswap.autumn.parsing3.ParsingExpression;


/**
 * Triggers a cut operation: signify that the remaining alternatives of the choice indicated by
 * the name (a node of type {@link Cuttable}) must not be tried.
 *
 * Always succeeds.
 *
 * On success, its end position is its start position.
 */
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

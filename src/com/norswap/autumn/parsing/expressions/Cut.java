package com.norswap.autumn.parsing.expressions;

import com.norswap.autumn.parsing.ParseInput;
import com.norswap.autumn.parsing.Parser;
import com.norswap.autumn.parsing.ParsingExpression;


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

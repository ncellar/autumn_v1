package com.norswap.autumn.parsing.expressions;

import com.norswap.autumn.parsing.ParseInput;
import com.norswap.autumn.parsing.Parser;
import com.norswap.autumn.parsing.ParsingExpression;
import com.norswap.autumn.parsing.SingleReferenceResolver;
import com.norswap.autumn.util.Array;


/**
 * A reference to another expression. A reference is a temporary operator that is meant to be
 * pruned from the expression graph via a resolution process.
 *
 * A reference holds the name of its target, which is the an expression with that name ({@link
 * ParsingExpression#name()}).
 */
public final class Reference extends ParsingExpression
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public String target;

    /** See {@link SingleReferenceResolver}. */
    public Array<ParsingExpression> nestedReferences;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void parse(Parser parser, ParseInput input)
    {
        throw new RuntimeException("Trying to parse an unresolved reference.");
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public void appendTo(StringBuilder builder)
    {
        builder.append(target);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}

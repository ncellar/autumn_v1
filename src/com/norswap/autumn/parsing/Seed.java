package com.norswap.autumn.parsing;

import com.norswap.autumn.parsing.expressions.common.ParsingExpression;

/**
 * A growing output change built-up by an expression cluster.
 * <p>
 * Seeds are immutable, so in practice growing the seed means replacing it in the {@link
 * ParseState}.
 */
public final class Seed
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public final ParsingExpression expression;
    public final OutputChanges changes;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public Seed(ParsingExpression expression, OutputChanges changes)
    {
        this.expression = expression;
        this.changes = changes;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}

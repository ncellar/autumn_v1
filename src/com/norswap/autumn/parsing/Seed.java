package com.norswap.autumn.parsing;

public final class Seed
{
    public final ParsingExpression expression;
    public OutputChanges changes;

    public Seed(ParsingExpression expression, OutputChanges changes)
    {
        this.expression = expression;
        this.changes = changes;
    }
}

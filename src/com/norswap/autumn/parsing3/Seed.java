package com.norswap.autumn.parsing3;

public final class Seed
{
    public final ParsingExpression expression;
    public final OutputChanges changes;

    public Seed(ParsingExpression expression, OutputChanges changes)
    {
        this.expression = expression;
        this.changes = changes;
    }
}

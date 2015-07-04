package com.norswap.autumn.parsing.graph;

import com.norswap.autumn.parsing.expressions.common.ParsingExpression;

import java.util.Collection;

/**
 * An expression graph transformer whose transformation function is passed as a lambda.
 */
public final class FunctionalTransformer extends ExpressionGraphTransformer
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    @FunctionalInterface
    public interface ExpressionTransformer
    {
        ParsingExpression transform(ParsingExpression pe);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public final ExpressionTransformer transformer;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public FunctionalTransformer(ExpressionTransformer transformer, boolean unique)
    {
        this.unique = unique;
        this.transformer = transformer;
    }

    // ---------------------------------------------------------------------------------------------

    public FunctionalTransformer(ExpressionTransformer transformer)
    {
        this.unique = true;
        this.transformer = transformer;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public ParsingExpression doTransform(ParsingExpression pe)
    {
        return transformer.transform(pe);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}

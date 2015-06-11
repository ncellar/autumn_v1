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
        super(unique);
        this.transformer = transformer;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public static ParsingExpression apply(
        ParsingExpression pe, ExpressionTransformer transformer, boolean unique)
    {
        return new FunctionalTransformer(transformer, unique).apply(pe);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public ParsingExpression transform(ParsingExpression pe)
    {
        return transformer.transform(pe);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected Collection<ParsingExpression> apply(Iterable<ParsingExpression> exprs)
    {
        return super.apply(exprs);
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    protected ParsingExpression[] apply(ParsingExpression[] exprs)
    {
        return super.apply(exprs);
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    protected ParsingExpression apply(ParsingExpression pe)
    {
        return super.apply(pe);
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
}

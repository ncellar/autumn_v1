package com.norswap.autumn.extensions.cluster;

import com.norswap.autumn.ParsingExpression;
import static com.norswap.autumn.ParsingExpressionFactory.sequence;
import com.norswap.autumn.extensions.cluster.expressions.Filter;
import com.norswap.autumn.extensions.cluster.expressions.WithMinPrecedence;

public final class ClusterExpressionFactory
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public static WithMinPrecedence exprDropPrecedence(ParsingExpression operand)
    {
        return exprWithMinPrecedence(0, operand);
    }

    // ---------------------------------------------------------------------------------------------

    public static WithMinPrecedence exprDropPrecedence(ParsingExpression... seq)
    {
        return exprWithMinPrecedence(0, sequence(seq));
    }

    // ---------------------------------------------------------------------------------------------

    public static WithMinPrecedence exprWithMinPrecedence(int minPrecedence, ParsingExpression operand)
    {
        WithMinPrecedence result = new WithMinPrecedence();
        result.operand = operand;
        result.minPrecedence = minPrecedence;
        return result;
    }

    // ---------------------------------------------------------------------------------------------

    public static WithMinPrecedence exprWithMinPrecedence(int minPrecedence, ParsingExpression... seq)
    {
        return exprWithMinPrecedence(minPrecedence, sequence(seq));
    }

    // ---------------------------------------------------------------------------------------------

    public static ParsingExpression filter(
        ParsingExpression pe,
        String[] allowed,
        String[] forbidden)
    {
        if ((allowed == null   || allowed.length == 0)
            &&  (forbidden == null || forbidden.length == 0))
        {
            return pe;
        }

        return new Filter(
            pe,
            allowed != null ? allowed : EMPTY_STRINGS,
            forbidden != null ? forbidden : EMPTY_STRINGS);
    }

    private static final String[] EMPTY_STRINGS = new String[0];

    // ---------------------------------------------------------------------------------------------

    public static ParsingExpression allow(ParsingExpression pe, String... allowed)
    {
        return filter(pe, allowed, null);
    }

    // ---------------------------------------------------------------------------------------------

    public static ParsingExpression forbid(ParsingExpression pe, String... forbidden)
    {
        return filter(pe, null, forbidden);
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Use to create the allowed and forbidden parameters to {@link #filter}.
     */
    public static ParsingExpression[] $(ParsingExpression... exprs)
    {
        return exprs;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}

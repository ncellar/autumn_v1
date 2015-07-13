package com.norswap.autumn.parsing.graph;

import com.norswap.autumn.parsing.Grammar;
import com.norswap.autumn.parsing.expressions.LeftRecursive;
import com.norswap.autumn.parsing.expressions.common.ParsingExpression;

import java.util.HashMap;

import static com.norswap.autumn.parsing.ParsingExpressionFactory.leftRecursive;

/**
 * This break left-recursive cycles detected by a {@link LeftRecursionDetector} by wrapping every
 * occurrence of expressions that the detector has recorded in a {@link LeftRecursive} expression.
 *
 * To do so, the breaker starts by creating the LeftRecursive replacement for each node recorded
 * by the detector. It then walks the graph and records the location (a {@link ChildSlot}) where each
 * recorded node occur. We can't replace nodes during the walk as that would break the walking
 * algorithm. Finally, it replaces each recorded location by its proper replacement.
 */
public final class LeftRecursionBreaker extends ExpressionGraphTransformer
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private HashMap<ParsingExpression, LeftRecursive> replacements;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public LeftRecursionBreaker(Grammar grammar)
    {
        LeftRecursionDetector detector = new LeftRecursionDetector(grammar);
        grammar.walk(detector);

        replacements = new HashMap<>();

        for (ParsingExpression pe : detector.leftRecursives)
        {
            replacements.put(pe, leftRecursive(pe));
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected ParsingExpression doTransform(ParsingExpression pe)
    {
        LeftRecursive replacement = replacements.get(pe);
        return replacement != null ? replacement : pe;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}

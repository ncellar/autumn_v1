package com.norswap.autumn.extensions.cluster.syntax;

import com.norswap.autumn.capture.ParseTree;
import com.norswap.autumn.extensions.SyntaxExtension;
import com.norswap.autumn.extensions.cluster.ClusterSyntax;
import com.norswap.autumn.extensions.cluster.expressions.Filter;
import com.norswap.autumn.support.GrammarCompiler;
import com.norswap.autumn.support.MetaGrammar;
import com.norswap.util.Array;

import static com.norswap.autumn.extensions.cluster.ClusterExpressionFactory.filter;
import static com.norswap.autumn.ParsingExpressionFactory.reference;

/**
 * Describes the syntactic extension for cluster expression filters (see {@link Filter}).
 * <p>
 * Examples:
 * <pre>{@code
 * myRule1 = `filter { myClusterRule; allowed: myArrow1, myArrow2; }
 * myRule2 = `filter { myClusterRule; forbidden: myArrow1, myArrow2; }
 * }</pre>
 */
public final class SyntaxFilter extends SyntaxExtension
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public SyntaxFilter()
    {
        super(Type.EXPRESSION, "filter", ClusterSyntax.filter);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public Object compile(
        GrammarCompiler compiler,
        ParseTree filter)
    {
        String ref = filter.value("ref");

        Array<ParseTree> allowed   = filter.group("allowed");
        Array<ParseTree> forbidden = filter.group("forbidden");

        return filter(
            reference(ref),
            allowed   .mapToArray(t -> t.value, String[]::new),
            forbidden .mapToArray(t -> t.value, String[]::new));
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}

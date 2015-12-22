package com.norswap.autumn.extensions.cluster;

import com.norswap.autumn.ParsingExpression;

import static com.norswap.autumn.ParsingExpressionFactory.*;
import com.norswap.autumn.extensions.cluster.syntax.SyntaxCluster;
import com.norswap.autumn.extensions.cluster.syntax.SyntaxFilter;
import static com.norswap.autumn.support.MetaGrammar.*;

/**
 * Parsing expressions used to define the syntax of {@link SyntaxCluster} and
 * {@link SyntaxFilter}.
 */
public final class ClusterSyntax
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public final static ParsingExpression

    clusterArrow =
        namekind("clusterArrow", sequence(
            ntoken("->"),
            optional(lhs),
            capture("expr",
                parsingExpression))),

    clusterDirective =
        namekindText("clusterDirective", choice(
            nKeyword("@+"),
            nKeyword("@+_left_assoc"),
            nKeyword("@+_left_recur"))),

    exprCluster =
        namekind("exprCluster", group("entries", oneMore(choice(clusterArrow, clusterDirective)))),

    filter =
        sequence(
            captureText("ref", name),
            semi,
            optional(group("allowed", sequence(
                nKeyword("allow"),
                colon,
                aloSeparated(captureText(name), comma),
                semi))),
            optional(group("forbidden", sequence(
                nKeyword("forbid"),
                colon,
                aloSeparated(captureText(name), comma),
                semi))));

    ////////////////////////////////////////////////////////////////////////////////////////////////
}

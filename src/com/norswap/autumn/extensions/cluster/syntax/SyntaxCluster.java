package com.norswap.autumn.extensions.cluster.syntax;

import com.norswap.autumn.ParsingExpression;
import com.norswap.autumn.capture.ParseTree;
import com.norswap.autumn.extensions.SyntaxExtension;
import com.norswap.autumn.extensions.cluster.ClusterExtension;
import com.norswap.autumn.extensions.cluster.ClusterSyntax;
import com.norswap.autumn.extensions.cluster.expressions.ExpressionCluster;
import com.norswap.autumn.support.GrammarCompiler;
import com.norswap.util.Array;

import static com.norswap.autumn.ParsingExpressionFactory.group;

/**
 * Describes the syntactic extension for expression clusters (see {@link ExpressionCluster}).
 * <p>
 * Example (a very small subset of our Java 8 grammar):
 * <pre>{@code
 * E ~ expression = `expr {
 * &#64;+_left_assoc
 * -> '+' :~$ = E '+' E
 * -> '-' :~$ = E '-' E
 *
 * &#64;+_left_assoc
 * -> '*' :~$ = E '*' E
 * -> '/' :~$ = E '/' E
 * -> '%' :~$ = E '%' E
 * };
 * }</pre>
 */
public final class SyntaxCluster extends SyntaxExtension
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private final ClusterExtension cext;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public SyntaxCluster(ClusterExtension cext)
    {
        super(Type.EXPRESSION, "expr", ClusterSyntax.exprCluster);
        this.cext = cext;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public Object compile(GrammarCompiler compiler, ParseTree expression)
    {
        expression = expression.child();

        // Current precedence level, alternates being collected, and group where the alternates
        // must be added.
        int precedence = 1;
        Array<ParsingExpression> alts = new Array<>();
        Array<ExpressionCluster.Group> groups = new Array<>(group(1, false, false));

        for (ParseTree entry: expression.group("entries"))
        {
            if (entry.hasKind("clusterDirective"))
            {
                if (!alts.isEmpty())
                {
                    // Add the alternates to the current group and prepare for the next group.
                    groups.peek().operands = alts.toArray(ParsingExpression[]::new);
                    alts = new Array<>();
                    ++precedence;
                }
                else
                {
                    groups.pop();
                }

                switch (entry.value)
                {
                    case "@+":
                        groups.push(group(precedence, false, false));
                        break;

                    case "@+_left_recur":
                        groups.push(group(precedence, true, false));
                        break;

                    case "@+_left_assoc":
                        groups.push(group(precedence, true, true));
                        break;

                    default:
                        compiler.error("Unknown cluster directive: %s", entry);
                }
            }
            else if (entry.hasKind("clusterArrow"))
            {
                ParsingExpression pe = compiler.compilePE(entry.get("expr").child());

                if (entry.has("lhs"))
                    pe = compiler.decorateRule(entry.get("lhs"), pe);

                alts.push(pe);
            }
            else
            {
                compiler.error("Unknown cluster entry: %s", entry);
            }
        }

        if (!alts.isEmpty()) {
            groups.peek().operands = alts.toArray(ParsingExpression[]::new);
        }
        else {
            groups.pop();
        }

        return cext.cluster(groups.toArray(ExpressionCluster.Group[]::new));
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}

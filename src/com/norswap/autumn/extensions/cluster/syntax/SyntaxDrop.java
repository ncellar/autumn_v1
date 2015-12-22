package com.norswap.autumn.extensions.cluster.syntax;

import com.norswap.autumn.capture.ParseTree;
import com.norswap.autumn.extensions.SyntaxExtension;
import static com.norswap.autumn.extensions.cluster.ClusterExpressionFactory.exprDropPrecedence;
import com.norswap.autumn.extensions.cluster.expressions.WithMinPrecedence;
import com.norswap.autumn.support.GrammarCompiler;
import com.norswap.autumn.support.MetaGrammar;

/**
 * Describes the syntactic extension for cluster precedence drops (see {@link WithMinPrecedence}).
 * <p>
 * Examples:
 * <pre>{@code
 * MethodCall = Expression `drop { Arguments }
 * Parens = '(' `drop { Expression } ')'
 * }</pre>
 */
public final class SyntaxDrop extends SyntaxExtension
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public SyntaxDrop()
    {
        super(Type.EXPRESSION, "drop", MetaGrammar.expr);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public Object compile(GrammarCompiler compiler, ParseTree tree)
    {
        return exprDropPrecedence(compiler.compilePE(tree.child()));
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}

package com.norswap.autumn.extensions.bruteforcetree;

import com.norswap.autumn.GrammarBuilderExtensionView;
import static com.norswap.autumn.ParsingExpressionFactory.capture;

import com.norswap.autumn.ParsingExpression;
import com.norswap.autumn.expressions.Capture;
import com.norswap.autumn.expressions.Dumb;
import com.norswap.autumn.extensions.Extension;
import com.norswap.autumn.graph.Transformer;

/**
 * Wraps every parsing expression in the grammar in a parse tree.
 * <p>
 * Only nominally useful at present, when hacking {@link Capture} to support {@link
 * ParsingExpression#parseDumb}. Making this really useful will entail not wrapping {@link Dumb}
 * nodes and their children, and only wrapping nodes with a name.
 */
public final class BruteForceTreeExtension implements Extension
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void transform(GrammarBuilderExtensionView grammar)
    {
        grammar.transform(new Transformer(pe -> capture(pe.name, pe)));
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}

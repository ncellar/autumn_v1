package com.norswap.autumn.parsing.extensions.bruteforcetree;

import com.norswap.autumn.parsing.GrammarBuilderExtensionView;
import static com.norswap.autumn.parsing.ParsingExpressionFactory.capture;
import com.norswap.autumn.parsing.extensions.Extension;
import com.norswap.autumn.parsing.graph.Transformer;

public final class BruteForceTreeExtension implements Extension
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void transform(GrammarBuilderExtensionView grammar)
    {
        grammar.transform(new Transformer(pe -> capture(pe)));
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}

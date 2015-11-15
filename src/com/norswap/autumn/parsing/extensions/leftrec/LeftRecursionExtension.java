package com.norswap.autumn.parsing.extensions.leftrec;

import com.norswap.autumn.parsing.GrammarBuilderExtensionView;
import com.norswap.autumn.parsing.extensions.Extension;
import com.norswap.autumn.parsing.graph.LeftRecursionHandler;
import com.norswap.autumn.parsing.graph.NullabilityCalculator;
import com.norswap.autumn.parsing.state.CustomState;
import com.norswap.autumn.parsing.state.CustomStateIndex;

/**
 * Extension enabling left-recursion in the grammar. The extension automatically detect and breaks
 * left-recursive loops amongst rules (using {@link LeftRecursionHandler}.
 */
public final class LeftRecursionExtension implements Extension
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public static final int INDEX = CustomStateIndex.allocate();

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public CustomState customParseState()
    {
        return new LeftRecursionState();
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public void transform(GrammarBuilderExtensionView grammar)
    {
        NullabilityCalculator calc = new NullabilityCalculator();
        grammar.compute(calc);

        LeftRecursionHandler handler = new LeftRecursionHandler(true, calc);
        grammar.transform(handler);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}

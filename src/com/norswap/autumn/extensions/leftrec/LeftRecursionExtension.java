package com.norswap.autumn.extensions.leftrec;

import com.norswap.autumn.GrammarBuilderExtensionView;
import com.norswap.autumn.extensions.Extension;
import com.norswap.autumn.graph.NullabilityCalculator;
import com.norswap.autumn.state.CustomState;
import com.norswap.autumn.extensions.CustomStateIndex;

/**
 * Extension enabling left-recursion in the grammar. The extension automatically detect and breaks
 * left-recursive loops amongst rules (using {@link LeftRecursionVisitor}.
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

        LeftRecursionVisitor lrHandler = new LeftRecursionVisitor(true, calc);
        grammar.transform(lrHandler);
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public int stateIndex()
    {
        return INDEX;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}

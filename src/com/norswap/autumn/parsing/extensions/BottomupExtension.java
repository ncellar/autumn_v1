package com.norswap.autumn.parsing.extensions;

import com.norswap.autumn.parsing.GrammarBuilderExtensionView;
import com.norswap.autumn.parsing.graph.LeftRecursionHandler;
import com.norswap.autumn.parsing.graph.NullabilityCalculator;
import com.norswap.autumn.parsing.state.BottomUpState;
import com.norswap.autumn.parsing.state.CustomStateIndex;

public class BottomupExtension implements Extension
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public static final int INDEX = CustomStateIndex.allocate();

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public BottomUpState customParseState()
    {
        return new BottomUpState();
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

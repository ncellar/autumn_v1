package com.norswap.autumn.extensions.tracer;

import com.norswap.autumn.GrammarBuilderExtensionView;
import com.norswap.autumn.extensions.Extension;
import com.norswap.autumn.graph.Transformer;
import com.norswap.autumn.state.CustomState;
import com.norswap.autumn.extensions.CustomStateIndex;

/**
 * This extension wraps every rule in a {@link Trace} parsing expression that logs the parsing
 * expression whenever it is entered, excepted in dumb mode.
 */
public final class TracerExtension implements Extension
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public static final int INDEX = CustomStateIndex.allocate();

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public CustomState customParseState()
    {
        return new TraceState();
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public void transform(GrammarBuilderExtensionView grammar)
    {
        grammar.transform(new Transformer(pe -> new Trace(pe)));
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public int stateIndex()
    {
        return INDEX;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}

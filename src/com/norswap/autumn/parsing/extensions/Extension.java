package com.norswap.autumn.parsing.extensions;

import com.norswap.autumn.parsing.GrammarBuilderExtensionView;
import com.norswap.autumn.parsing.Parser;
import com.norswap.autumn.parsing.state.CustomState;

/**
 * Interfaces that extensions to the parser must implement.
 * <p>
 * An extension is made of an optional grammar transformation and an optional custom parse state.
 */
public interface Extension
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Returns a new instance of the custom parse state, if the extension requires one, null
     * otherwise (the default). Called once per parser invocation ({@link Parser#parseRoot} or
     * {@link Parser#parse}).
     */
    default CustomState customParseState()
    {
        return null;
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Applies transformation to the given grammar. See {@link GrammarBuilderExtensionView}.
     */
    default void transform(GrammarBuilderExtensionView grammar) {}

    ////////////////////////////////////////////////////////////////////////////////////////////////
}

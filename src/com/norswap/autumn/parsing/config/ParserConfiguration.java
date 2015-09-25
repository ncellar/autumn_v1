package com.norswap.autumn.parsing.config;

import com.norswap.autumn.parsing.ErrorState;
import com.norswap.autumn.parsing.state.CustomState;

/**
 * [Immutable] The parser configuration allows the user to configure operational details of the
 * parse, such as how errors and memoization are handled.
 */
public interface ParserConfiguration
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    static ParserConfiguration build()
    {
        return new ParserConfigurationBuilder().build();
    }

    // ---------------------------------------------------------------------------------------------

    static ParserConfigurationBuilder with()
    {
        return new ParserConfigurationBuilder();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    ErrorState errorState();

    MemoHandler memoHandler();

    Object[] scoped();

    CustomState[] customStates();

    ////////////////////////////////////////////////////////////////////////////////////////////////
}

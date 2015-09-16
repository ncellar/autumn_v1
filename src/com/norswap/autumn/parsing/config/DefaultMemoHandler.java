package com.norswap.autumn.parsing.config;

import com.norswap.autumn.parsing.OutputChanges;
import com.norswap.autumn.parsing.ParseInputs;
import com.norswap.autumn.parsing.ParseState;
import com.norswap.autumn.parsing.expressions.common.ParsingExpression;

import java.util.HashMap;

/**
 * The default memoization strategy memoizes every changeset that it is asked to.
 * <p>
 * It is implement by combining the parsing expression and all parse inputs from the parse state
 * as a single key {@link ParseInputs} in a traditional HashMap.
 */
public final class DefaultMemoHandler implements MemoHandler
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private HashMap<ParseInputs, OutputChanges> store;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void memoize(ParsingExpression pe, ParseState state, OutputChanges changeset)
    {
        store.put(state.inputs(pe), changeset);
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public OutputChanges get(ParsingExpression pe, ParseState state)
    {
        return store.get(state.inputs(pe));
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}

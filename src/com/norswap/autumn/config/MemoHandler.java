package com.norswap.autumn.config;

import com.norswap.autumn.state.ParseChanges;
import com.norswap.autumn.state.ParseState;
import com.norswap.autumn.ParsingExpression;

/**
 * The memoization handler is called each time we want to memoize the result of a parsing expression
 * invocation; or when we want to retrieve such a result.
 * <p>
 * An invocation is characterized by the parsing expression as well as the parsing state at the time
 * of invocation. Traditionally, memoization only takes the input position into account.
 * However, if some state is able to modify the result of the invocation, it must either be taken
 * into account by the memoization handler; or memoization must be disabled while this state is in
 * effect. Otherwise, correctness is no longer guaranteed.
 * <p>
 * The "results" we allude to are saved as changesets ({@link ParseChanges}) that describe the
 * difference in the parse state before/after the invocation.
 */
public interface MemoHandler
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Memoize a changeset for the given parsing expression and state.
     */
    void memoize(ParsingExpression pe, ParseState state, ParseChanges changeset);

    // ---------------------------------------------------------------------------------------------

    /**
     * Return a memoized changeset; or null if no such changeset has been memoized.
     */
    ParseChanges get(ParsingExpression pe, ParseState state);

    ////////////////////////////////////////////////////////////////////////////////////////////////
}

package com.norswap.autumn.parsing;

import com.norswap.autumn.parsing.expressions.common.ParsingExpression;
import com.norswap.util.Array;

/**
 * A growing output change built-up by an expression cluster.
 * <p>
 * Seeds are immutable, so in practice growing the seed means replacing it in the {@link
 * ParseState}.
 */
public final class Seed
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public final ParsingExpression expression;
    public final ParseChanges changes;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public Seed(ParsingExpression expression, ParseChanges changes)
    {
        this.expression = expression;
        this.changes = changes;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Get the seed stored in the parse state for the given expression, if any; otherwise null.
     */
    public static ParseChanges get(ParseState state, ParsingExpression pe)
    {
        if (state.seeds != null)
        {
            for (Seed seed : state.seeds)
            {
                if (seed.expression == pe)
                {
                    return seed.changes;
                }
            }
        }

        return null;
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Adds a seed in the parse state for the given expression.
     */
    public static void push(ParseState state, ParsingExpression pe, ParseChanges changes)
    {
        Array<Seed> seeds = state.seeds;

        if (seeds == null)
        {
            seeds = state.seeds = new Array<>();
        }

        seeds.push(new Seed(pe, changes));
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Sets the seed of the innermost left-recursive node or expression cluster being parsed.
     */
    public static void set(ParseState state, ParseChanges changes)
    {
        state.seeds.push(new Seed(state.seeds.pop().expression, changes));
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Removes the seed of the innermost left-recursive node or expression cluster being parsed.
     */
    public static ParseChanges pop(ParseState state)
    {
        return state.seeds.pop().changes;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}

package com.norswap.autumn.parsing.state;

import com.norswap.autumn.parsing.ParsingExpression;
import com.norswap.autumn.parsing.expressions.ExpressionCluster;
import com.norswap.autumn.parsing.expressions.LeftRecursive;
import com.norswap.util.Array;
import com.norswap.util.annotations.Nullable;

import java.util.HashMap;

/**
 * TODO (this was copy pasted)
 *
 * Holds a set of mapping between parsing expressions ({@link ExpressionCluster} and {@link
 * LeftRecursive} instances whose invocation is ongoing) and their seed (an instance of {@link
 * ParseChanges}).
 *
 * Holds a set of mapping between {@link ExpressionCluster} instances whose invocation is
 * ongoing and their current precedence level.
 */
public final class BottomUpState
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public static class Precedence
    {
        Precedence(int value)
        {
            this.value = value;
        }

        public int value;
        Array<Integer> history = new Array<>();

        public int oldPrecedence()
        {
            return history.peekOr(0);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Nullable Array<ParsingExpression> seeded;
    @Nullable Array<ParseChanges> seeds;
    private final HashMap<ParsingExpression, Precedence> precedences = new HashMap<>();
    private final Array<ParsingExpression> history = new Array<>();

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public ParseChanges getSeed(ParsingExpression pe)
    {
        if (seeded == null) return null;

        int size = seeded.size();

        for (int i = 0; i < size; ++i)
        {
            if (pe == seeded.get(i));
            {
                return seeds.get(i);
            }
        }

        return null;
    }

    // ---------------------------------------------------------------------------------------------

    public void setSeed(ParsingExpression pe, ParseChanges seed)
    {
        if (seeded == null)
        {
            seeded = new Array<>(pe);
            seeds = new Array<>(seed);
        }
        else if (seeded.peekOr(null) == pe)
        {
            seeds.setLast(seed);
        }
        else
        {
            seeded.push(pe);
            seeds.push(seed);
        }
    }

    // ---------------------------------------------------------------------------------------------

    public void removeSeed(ParsingExpression pe)
    {
        seeds.pop();
    }

    // ---------------------------------------------------------------------------------------------

    public Precedence getPrecedence(ParsingExpression pe)
    {
        history.push(pe);

        return precedences.compute(pe, (k, v) -> {
            if (v != null) {
                v.history.push(v.value);
                return v;
            }
            else {
                return new Precedence(0);
            }});
    }

    // ---------------------------------------------------------------------------------------------

    public void removePrecedence(ParsingExpression pe, Precedence precedence)
    {
        history.pop();

        if (precedence.history.isEmpty())
        {
            precedences.remove(pe);
        }
        else
        {
            precedence.value = precedence.history.pop();
        }
    }

    // ---------------------------------------------------------------------------------------------

    public Precedence getCurrentPrecedence()
    {
        if (history.isEmpty())
        {
            throw new Error(
                "Trying to retrieve a cluster precedence while none is currently parsing.");
        }

        return precedences.get(history.peek());
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}

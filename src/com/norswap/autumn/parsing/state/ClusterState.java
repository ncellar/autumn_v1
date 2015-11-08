package com.norswap.autumn.parsing.state;

import com.norswap.autumn.parsing.ParsingExpression;
import com.norswap.util.Array;
import com.norswap.util.annotations.Nullable;

import java.util.HashMap;

public final class ClusterState
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
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Nullable Array<ParsingExpression> seeded;
    @Nullable Array<ParseChanges> seeds;
    private HashMap<ParsingExpression, Precedence> precedences = new HashMap<>();

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
        if (precedence.history.isEmpty())
        {
            precedences.remove(pe);
        }
        else
        {
            precedence.value = precedence.history.pop();
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}

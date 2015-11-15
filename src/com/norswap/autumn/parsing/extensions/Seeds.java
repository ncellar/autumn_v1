package com.norswap.autumn.parsing.extensions;

import com.google.auto.value.AutoValue;
import com.norswap.autumn.parsing.ParsingExpression;
import com.norswap.autumn.parsing.extensions.cluster.ExpressionCluster;
import com.norswap.autumn.parsing.extensions.leftrec.LeftRecursive;
import com.norswap.autumn.parsing.extensions.leftrec.LeftRecursionState;
import com.norswap.autumn.parsing.state.CustomState;
import com.norswap.autumn.parsing.state.ParseChanges;
import com.norswap.autumn.parsing.state.ParseState;
import com.norswap.util.Array;
import com.norswap.util.Exceptions;
import com.norswap.util.annotations.Nullable;

/**
 * Holds a set of mapping between parsing expressions ({@link ExpressionCluster} and {@link
 * LeftRecursive} instances whose invocation is ongoing) and their seed (an instance of {@link
 * ParseChanges}).
 * <p>
 * Both {@link LeftRecursionState} and TODO require an instance of this.
 */
public final class Seeds implements CustomState, Cloneable
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * The position associated to the seeds.
     */
    private int position;

    /**
     * The set of all expressions for which we currently have a seed value.
     */
    private @Nullable Array<ParsingExpression> seeded;

    /**
     * The set of seeds matching the parsing expressions in {@link #seeded}.
     */
    private @Nullable Array<ParseChanges> seeds;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public ParseChanges get(ParsingExpression pe)
    {
        if (seeded == null) return null;

        int size = seeded.size();

        for (int i = 0; i < size; ++i)
        {
            if (pe == seeded.get(i))
            {
                return seeds.get(i);
            }
        }

        return null;
    }

    // ---------------------------------------------------------------------------------------------

    public void set(ParsingExpression pe, ParseChanges seed, int position)
    {
        if (seeded == null)
        {
            this.position = position;
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

    public void remove(ParsingExpression pe)
    {
        seeded.pop();
        seeds.pop();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public Inputs inputs(ParseState state)
    {
        return Inputs.create(
            position,
            seeded != null ? seeded.clone() : null,
            seeds  != null ? seeds .clone() : null);
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public void load(CustomState.Inputs inputs)
    {
        Inputs in = (Inputs) inputs;
        this.position = in.position();
        this.seeded = in.seeded() != null ? in.seeded().clone() : null;
        this.seeds  = in.seeds () != null ? in.seeds ().clone() : null;
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public Snapshot snapshot(ParseState state)
    {
        return seeded != null
            ? new Snapshot(position, seeded, seeds)
            : null;
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public void restore(CustomState.Snapshot snapshot, ParseState state)
    {
        if (snapshot != null)
        {
            Snapshot s = (Snapshot) snapshot;
            this.position = s.position;
            this.seeded = s.seeded;
            this.seeds = s.seeds;
        }
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public void uncommit(CustomState.Snapshot snapshot, ParseState state)
    {
        restore(snapshot, state);
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public void commit(ParseState state)
    {
        if (state.end > position)
        {
            position = 0;
            seeded = null;
            seeds = null;
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public final static class Snapshot implements CustomState.Snapshot
    {
        final int position;
        final Array<ParsingExpression> seeded;
        final Array<ParseChanges> seeds;

        Snapshot(
            int position,
            Array<ParsingExpression> seeded,
            Array<ParseChanges> seeds)
        {
            this.position = position;
            this.seeded = seeded;
            this.seeds = seeds;
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public Seeds clone()
    {
        Seeds out = (Seeds) Exceptions.swallow(() -> super.clone());

        if (seeded != null)
        {
            out.seeded = seeded.clone();
            out.seeds = seeds.clone();
        }

        return out;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @AutoValue
    public static abstract class Inputs implements CustomState.Inputs
    {
        public static Inputs create(
            int position,
            @Nullable Array<ParsingExpression> seeded,
            @Nullable Array<ParseChanges> seeds)
        {
            return new AutoValue_Seeds_Inputs(position, seeded, seeds);
        }

        abstract int position();
        abstract @Nullable Array<ParsingExpression> seeded();
        abstract @Nullable Array<ParseChanges> seeds();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}

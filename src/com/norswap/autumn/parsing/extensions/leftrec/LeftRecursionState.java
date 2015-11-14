package com.norswap.autumn.parsing.extensions.leftrec;

import com.google.auto.value.AutoValue;
import com.norswap.autumn.parsing.ParsingExpression;
import com.norswap.autumn.parsing.expressions.LeftRecursive;
import com.norswap.autumn.parsing.state.CustomChanges;
import com.norswap.autumn.parsing.state.CustomState;
import com.norswap.autumn.parsing.state.ParseChanges;
import com.norswap.autumn.parsing.state.ParseState;
import com.norswap.util.Array;
import com.norswap.util.annotations.Nullable;

import java.util.HashSet;

import static com.norswap.util.Caster.cast;

public final class LeftRecursionState implements CustomState
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

    /**
     * Set of expressions in which we can't recurse, in order to ensure left-associativity.
     */
    private HashSet<LeftRecursive> blocked = new HashSet<>();

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public LeftRecursionState() {}

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public ParseChanges getSeed(ParsingExpression pe)
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

    public void setSeed(ParsingExpression pe, ParseChanges seed, int position)
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

    public void removeSeed(ParsingExpression pe)
    {
        seeded.pop();
        seeds.pop();
    }

    // ---------------------------------------------------------------------------------------------

    public void block(LeftRecursive pe)
    {
        blocked.add(pe);
    }

    // ---------------------------------------------------------------------------------------------

    public void unblock(LeftRecursive pe)
    {
        blocked.remove(pe);
    }

    // ---------------------------------------------------------------------------------------------

    public boolean blocked(LeftRecursive pe)
    {
        return blocked.contains(pe);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void load(CustomState.Inputs inputs)
    {
        Inputs in = (Inputs) inputs;
        this.position = in.position();
        this.seeded = in.seeded() != null ? in.seeded().clone() : null;
        this.seeds  = in.seeds () != null ? in.seeds ().clone() : null;
        this.blocked = cast(in.blocked().clone());
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

    // ---------------------------------------------------------------------------------------------

    @Override
    public void discard(ParseState state) {}

    // ---------------------------------------------------------------------------------------------

    @Override
    public CustomChanges extract(ParseState state)
    {
        return null;
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public void merge(CustomChanges changes, ParseState state) {}

    // ---------------------------------------------------------------------------------------------

    @Override
    public CustomState.Snapshot snapshot(ParseState state)
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
    public Inputs inputs(ParseState state)
    {
        return Inputs.create(
            position,
            seeded != null ? seeded.clone() : null,
            seeds  != null ? seeds .clone() : null,
            cast(blocked.clone()));
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public Result result(ParseState state)
    {
        return null;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public final static class Snapshot implements CustomState.Snapshot
    {
        final int position;
        final Array<ParsingExpression> seeded;
        final Array<ParseChanges> seeds;

        public Snapshot(
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

    @AutoValue
    public static abstract class Inputs implements CustomState.Inputs
    {
        public static Inputs create(
            int position,
            @Nullable Array<ParsingExpression> seeded,
            @Nullable Array<ParseChanges> seeds,
            HashSet<ParsingExpression> blocked)
        {
            return new AutoValue_LeftRecursionState_Inputs(
                position,
                seeded,
                seeds,
                blocked);
        }

        abstract int position();
        abstract @Nullable Array<ParsingExpression> seeded();
        abstract @Nullable Array<ParseChanges> seeds();
        abstract HashSet<ParsingExpression> blocked();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}

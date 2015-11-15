package com.norswap.autumn.parsing.extensions;

import com.google.auto.value.AutoValue;
import com.norswap.autumn.parsing.ParsingExpression;
import com.norswap.autumn.parsing.extensions.cluster.ExpressionCluster;
import com.norswap.autumn.parsing.extensions.cluster.Filter;
import com.norswap.autumn.parsing.extensions.leftrec.LeftRecursive;
import com.norswap.autumn.parsing.extensions.cluster.WithMinPrecedence;
import com.norswap.autumn.parsing.state.CustomChanges;
import com.norswap.autumn.parsing.state.CustomState;
import com.norswap.autumn.parsing.state.ParseChanges;
import com.norswap.autumn.parsing.state.ParseState;
import com.norswap.autumn.parsing.state.patterns.Container;
import com.norswap.util.Array;
import com.norswap.util.annotations.Nullable;

import java.util.HashMap;
import java.util.HashSet;

import static com.norswap.util.Caster.cast;

public final class BottomupState implements CustomState
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public static class Precedence
    {
        Precedence(int value)
        {
            this.value = value;
        }

        // Value is above the history stack.

        public int value;
        Array<Integer> history = new Array<>();

        public int oldPrecedence()
        {
            return history.peekOr(0);
        }
    }

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
     * Maps from expression clusters to their current precedence.
     */
    private HashMap<ExpressionCluster, Precedence> precedences = new HashMap<>();

    /**
     * A stack of expressions with a precedence level that are currently being visited. These
     * expressions are the keys of {@link #precedences}.
     * <p>
     * This data structure is necessary for {@link #getCurrentPrecedence} (used by {@link
     * WithMinPrecedence}) to work.
     */
    private Array<ExpressionCluster> history = new Array<>();

    /**
     * Set of expressions in which we can't recurse, in order to ensure left-associativity.
     */
    private HashSet<LeftRecursive> blocked = new HashSet<>();

    /**
     * The current cluster alternate; set by {@link ExpressionCluster} and read by {@link Filter}.
     */
    public ParsingExpression committedAlternate;

    /**
     * The current cluster alternate; set by {@link ExpressionCluster} and read by {@link Filter}.
     */
    public ParsingExpression uncommittedAlternate;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public BottomupState() {}

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

    public Precedence getPrecedence(ExpressionCluster pe)
    {
        history.push(pe);

        return precedences.compute(pe, (k, v) -> {
            if (v != null)
            {
                v.history.push(v.value);
                return v;
            }
            else
            {
                return new Precedence(0);
            }
        });
    }

    // ---------------------------------------------------------------------------------------------

    public void removePrecedence(ExpressionCluster pe, Precedence precedence)
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
    public Inputs inputs(ParseState state)
    {
        return Inputs.create(
            position,
            seeded != null ? seeded.clone() : null,
            seeds != null ? seeds.clone() : null,
            cast(precedences.clone()),
            history.clone(),
            cast(blocked.clone()));
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public void load(CustomState.Inputs inputs)
    {
        Inputs in = (Inputs) inputs;
        this.position = in.position();
        this.seeded = in.seeded() != null ? in.seeded().clone() : null;
        this.seeds  = in.seeds () != null ? in.seeds ().clone() : null;
        this.precedences = cast(in.precedences().clone());
        this.history = in.history().clone();
        this.blocked = cast(in.blocked().clone());
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public CustomState.Snapshot snapshot(ParseState state)
    {
        return seeded != null
            ? new Snapshot(position, seeded, seeds, committedAlternate)
            : new Container<>(committedAlternate);
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    @SuppressWarnings("unchecked")
    public void restore(CustomState.Snapshot snapshot, ParseState state)
    {
        if (snapshot instanceof Snapshot)
        {
            Snapshot s = (Snapshot) snapshot;
            this.position = s.position;
            this.seeded = s.seeded;
            this.seeds = s.seeds;

            committedAlternate = s.committedAlternate;
            uncommittedAlternate = s.committedAlternate;
        }
        else
        {
            committedAlternate = ((Container<ParsingExpression>) snapshot).content;
            uncommittedAlternate = committedAlternate;
        }
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    @SuppressWarnings("unchecked")
    public void uncommit(CustomState.Snapshot snapshot, ParseState state)
    {
        if (snapshot instanceof Snapshot)
        {
            Snapshot s = (Snapshot) snapshot;
            this.position = s.position;
            this.seeded = s.seeded;
            this.seeds = s.seeds;

            committedAlternate = s.committedAlternate;
        }
        else
        {
            committedAlternate = ((Container<ParsingExpression>) snapshot).content;
        }
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

        // TODO got added after split
        committedAlternate = uncommittedAlternate;
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public void discard(ParseState state)
    {
        uncommittedAlternate = committedAlternate;
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public Container<ParsingExpression> extract(ParseState state)
    {
        return committedAlternate != uncommittedAlternate
            ? new Container<>(uncommittedAlternate)
            : null;
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    @SuppressWarnings("unchecked")
    public void merge(CustomChanges changes, ParseState state)
    {
        if (changes != null) {
            uncommittedAlternate = ((Container<ParsingExpression>)changes).content;
        }
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public Result result(ParseState state)
    {
        return null;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public final static class Changes implements CustomChanges
    {
        final ParsingExpression uncommittedAlternate;

        public Changes(ParsingExpression uncommittedAlternate)
        {
            this.uncommittedAlternate = uncommittedAlternate;
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public final static class Snapshot implements CustomState.Snapshot
    {
        final int position;
        final Array<ParsingExpression> seeded;
        final Array<ParseChanges> seeds;
        final ParsingExpression committedAlternate;

        public Snapshot(
            int position,
            Array<ParsingExpression> seeded,
            Array<ParseChanges> seeds,
            ParsingExpression committedAlternate)
        {
            this.position = position;
            this.seeded = seeded;
            this.seeds = seeds;
            this.committedAlternate = committedAlternate;
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
            HashMap<ParsingExpression, Precedence> precedences,
            Array<ExpressionCluster> history,
            HashSet<ParsingExpression> blocked)
        {
            return new AutoValue_BottomupState_Inputs(
                position,
                seeded,
                seeds,
                precedences,
                history,
                blocked);
        }

        abstract int position();
        abstract @Nullable Array<ParsingExpression> seeded();
        abstract @Nullable Array<ParseChanges> seeds();
        abstract HashMap<ParsingExpression, Precedence> precedences();
        abstract Array<ExpressionCluster> history();
        abstract HashSet<ParsingExpression> blocked();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}

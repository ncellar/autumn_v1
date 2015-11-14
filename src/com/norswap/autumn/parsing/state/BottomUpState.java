package com.norswap.autumn.parsing.state;

import com.norswap.autumn.parsing.ParsingExpression;
import com.norswap.autumn.parsing.expressions.ExpressionCluster;
import com.norswap.autumn.parsing.expressions.Filter;
import com.norswap.autumn.parsing.expressions.LeftRecursive;
import com.norswap.autumn.parsing.expressions.WithMinPrecedence;
import com.norswap.util.Array;
import com.norswap.util.annotations.Nullable;

import java.util.HashMap;
import java.util.HashSet;

import static com.norswap.util.Caster.cast;

/**
 * TODO (this was copy pasted)
 *
 * Holds a set of mapping between parsing expressions ({@link ExpressionCluster} and {@link
 * LeftRecursive} instances whose invocation is ongoing) and their seed (an instance of {@link
 * ParseChanges}).
 *
 * Holds a set of mapping between {@link ExpressionCluster} instances whose invocation is
 * ongoing and their current precedence level.
 *
 * A set of blocked {@link LeftRecursive} parsing expression. Invoking these expressions
 * will never succeed.
 */
public final class BottomUpState implements CustomState
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
    private HashMap<ParsingExpression, Precedence> precedences = new HashMap<>();

    /**
     * A stack of expressions with a precedence level that are currently being visited. These
     * expressions are the keys of {@link #precedences}.
     * <p>
     * This data structure is necessary for {@link #getCurrentPrecedence} (used by {@link
     * WithMinPrecedence}) to work.
     */
    private Array<ParsingExpression> history = new Array<>();

    /**
     * Set of expressions in which we can't recurse, in order to ensure left-associativity.
     */
    private HashSet<ParsingExpression> blocked = new HashSet<>();

    /**
     * The current cluster alternate; set by {@link ExpressionCluster} and read by {@link Filter}.
     */
    public ParsingExpression committedAlternate;

    /**
     * The current cluster alternate; set by {@link ExpressionCluster} and read by {@link Filter}.
     */
    public ParsingExpression uncommittedAlternate;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public BottomUpState() {}

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

    public void block(ParsingExpression pe)
    {
        blocked.add(pe);
    }

    // ---------------------------------------------------------------------------------------------

    public void unblock(ParsingExpression pe)
    {
        blocked.remove(pe);
    }

    // ---------------------------------------------------------------------------------------------

    public boolean blocked(ParsingExpression pe)
    {
        return blocked.contains(pe);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void load(CustomState.Inputs inputs)
    {
        Inputs in = (Inputs) inputs;
        this.seeded = in.seeded != null ? in.seeded.clone() : null;
        this.seeds   = in.seeds != null ? in.seeds .clone() : null;
        this.precedences = cast(in.precedences.clone());
        this.history = in.history.clone();
        this.blocked = cast(in.blocked.clone());
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public void commit(ParseState state)
    {
        if (state.end > state.start)
        {
            seeded = null;
            seeds = null;
        }
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public void discard(ParseState state)
    {
        uncommittedAlternate = null;
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public CustomChanges extract(ParseState state)
    {
        return new Changes(uncommittedAlternate);
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public void merge(CustomChanges changes, ParseState state)
    {
        uncommittedAlternate = ((Changes)changes).uncommittedAlternate;
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public Snapshot snapshot(ParseState state)
    {
        return seeded != null
            ? new Snapshot(seeded, seeds, committedAlternate)
            : null;
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public void restore(CustomState.Snapshot snapshot, ParseState state)
    {
        // TODO null check might be wrong with committedAlternate
        if (snapshot != null)
        {
            Snapshot s = (Snapshot) snapshot;
            this.seeded = s.seeded;
            this.seeds = s.seeds;

            committedAlternate = s.committedAlternate;
            uncommittedAlternate = s.committedAlternate;
        }
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public void uncommit(CustomState.Snapshot snapshot, ParseState state)
    {
        // TODO null check might be wrong with committedAlternate
        if (snapshot != null)
        {
            Snapshot s = (Snapshot) snapshot;
            this.seeded = s.seeded;
            this.seeds = s.seeds;

            committedAlternate = s.committedAlternate;
        }
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public Inputs inputs(ParseState state)
    {
        return new Inputs(
            seeded != null ? seeded.clone() : null,
            seeds  != null ? seeds .clone() : null,
            cast(precedences.clone()),
            history.clone(),
            cast(blocked.clone()));
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
        final Array<ParsingExpression> seeded;
        final Array<ParseChanges> seeds;
        final ParsingExpression committedAlternate;

        public Snapshot(
            Array<ParsingExpression> seeded,
            Array<ParseChanges> seeds,
            ParsingExpression committedAlternate)
        {
            this.seeded = seeded;
            this.seeds = seeds;
            this.committedAlternate = committedAlternate;
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public final static class Inputs implements CustomState.Inputs
    {
        final @Nullable Array<ParsingExpression> seeded;
        final @Nullable Array<ParseChanges> seeds;
        final HashMap<ParsingExpression, Precedence> precedences;
        final Array<ParsingExpression> history;
        final HashSet<ParsingExpression> blocked;

        public Inputs(
            @Nullable Array<ParsingExpression> seeded,
            @Nullable Array<ParseChanges> seeds,
            HashMap<ParsingExpression, Precedence> precedences,
            Array<ParsingExpression> history,
            HashSet<ParsingExpression> blocked)
        {
            this.seeded = seeded;
            this.seeds = seeds;
            this.precedences = precedences;
            this.history = history;
            this.blocked = blocked;
        }

        @Override
        public boolean equals(Object o)
        {
            if (this == o) return true;
            if (!(o instanceof Inputs)) return false;

            Inputs that = (Inputs) o;

            if (seeded != that.seeded)
            {
                if (seeded == null || that.seeded == null) return false;
                if (!seeded.equals(that.seeded)) return false;
                if (!seeds.equals(that.seeds)) return false;
            }

            if (seeds != that.seeds)
            {
                if (seeds == null || that.seeds == null) return false;
                if (!seeds.equals(that.seeds)) return false;
                if (!seeds.equals(that.seeds)) return false;
            }

            if (!precedences.equals(that.precedences)) return false;
            if (!history.equals(that.history)) return false;

            return blocked.equals(that.blocked);
        }

        @Override
        public int hashCode()
        {
            int result = 0;
            result = 31 * result + (seeded != null ? seeded.hashCode() : 0);
            result = 31 * result + (seeds  != null ? seeds .hashCode() : 0);
            result = 31 * result + precedences.hashCode();
            result = 31 * result + history.hashCode();
            result = 31 * result + blocked.hashCode();
            return result;
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}

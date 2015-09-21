package com.norswap.autumn.parsing;

import com.norswap.autumn.parsing.expressions.ExpressionCluster;
import com.norswap.autumn.parsing.expressions.ExpressionCluster.PrecedenceEntry;
import com.norswap.autumn.parsing.expressions.Filter;
import com.norswap.autumn.parsing.expressions.LeftRecursive;
import com.norswap.autumn.parsing.expressions.common.ParsingExpression;
import com.norswap.autumn.parsing.tree.BuildParseTree;
import com.norswap.util.Array;
import com.norswap.util.JArrays;

import static com.norswap.autumn.parsing.Registry.*; // PSF_*

/**
 * An instance of this class is passed to every parsing expression invocation
 * {@link ParsingExpression#parse}.
 * <p>
 * The parse state is the sole access point for all state (mutable data) that the expression
 * will manipulate during the parse.
 *
 * Its role is dual. On the one hand, it holds both the inputs for the invocation (things that will
 * influence the invocation, or the produced outputs). On the other hand, the parse state is also
 * where the produced outputs will be attached. As such, it allows parsing expressions to exchange
 * information on the way down (inputs) and on the way up (outputs).
 * <p>
 * Custom parse states can be manipulated via the {@link #customStates} field. You must index this
 * array with a handle you received from {@link Registry#CustomParseStateHandleFactory}.
 */
public final class ParseState
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * (MODIFIER) Position where the parsing expression will be invoked.
     */
    public int start;

    /**
     * The last non-whitespace input position preceding {@link #start}.
     * <p>
     * We keep this particular tidbit of information for a single purpose: restoring {@link
     * ParseState#blackEnd} (the last non-whitespace input position preceding {@link
     * ParseState#end}) with {@link ParseState#discard()}.
     * <p>
     * As such, this does not influence the parse.
     */
    public int blackStart;

    /**
     * The position of the end of the text matched by the parsing expression, or -1 if no match
     * could be made. Initially equal to {@link #start}.
     */
    public int end;

    /**
     * The position of the last non-whitespace character preceding {@link #end}. This is useful
     * to avoid including trailing whitespaces in captures.
     */
    public int blackEnd;

    /**
     * (MODIFIER) The current precedence level for {@link com.norswap.autumn.parsing.expressions.Precedence}
     * expressions.
     */
    public int precedence;

    /**
     * (MODIFIER) Holds a set of flags that can serve as additional parse input. Can be set both by
     * Autumn and the user, who can register his own flags with {@link
     * Registry#ParseStateFlagsFactory}.
     */
    public int flags;

    /**
     * (MODIFIER) Holds the seeds for all the expression clusters in the parsing expression stack (all
     * the parsing expressions whose invocation is ongoing).
     */
    public Array<Seed> seeds;

    /**
     * The parse tree which is to be the parent of parse trees produced by captures in
     * the parsing expression.
     */
    public BuildParseTree tree;

    /**
     * The number of children of {@link #tree} prior to invocation. Kept so that we can rollback
     * {@link #tree} to its original state if required.
     */
    public int treeChildrenCount;

    /**
     * The error information that result after attempting to parse the expression. Note that
     * we may record errors even when the expresison succeeds.
     */
    public ErrorState errors;

    /**
     * An array of additional user-defined parse state.
     */
    public CustomState[] customStates;

    /**
     * The current cluster alternate; set by {@link ExpressionCluster} and read by {@link Filter}.
     * <p>
     * TODO unsafe, but clunky to begin with; rework the filtering mechanism
     */
    public ParsingExpression clusterAlternate;

    /**
     *
     */
    public Array<LeftRecursive> blocked;

    /**
     *
     */
    public Array<PrecedenceEntry> minPrecedence;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Creates the parse state to be passed to the root parsing expression.
     */
    ParseState()
    {
        this.end = 0;
        this.blackEnd = 0;
        this.tree = new BuildParseTree();
        this.customStates = new CustomState[Registry.CustomParseStateHandleFactory.size()];
        // TODO
        this.errors = new DefaultErrorState();
        this.blocked = new Array<>();
        this.minPrecedence = new Array<>();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Advances the end and black end positions by n characters.
     */
    public void advance(int n)
    {
        end += n;
        blackEnd = end;
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Sets the end position to indicate that no match could be found.
     */
    public void fail()
    {
        this.end = -1;
        this.blackEnd = -1;
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Indicates whether the match was successful.
     */
    public boolean succeeded()
    {
        return end != -1;
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Indicates whether the match was unsuccessful.
     */
    public boolean failed()
    {
        return end == -1;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public ParseStateSnapshot snapshot()
    {
        return new ParseStateSnapshot(
            start,
            blackStart,
            end,
            blackEnd,
            treeChildrenCount,
            flags,
            seeds,
            JArrays.map(customStates, CustomState::snapshot));
    }

    // ---------------------------------------------------------------------------------------------

    public void restore(ParseStateSnapshot snapshot)
    {
        start               = snapshot.start;
        blackStart          = snapshot.blackStart;
        end                 = snapshot.end;
        blackEnd            = snapshot.blackEnd;
        treeChildrenCount   = snapshot.treeChildrenCount;
        flags               = snapshot.flags;
        seeds               = snapshot.seeds;

        tree.truncate(treeChildrenCount);

        for (int i = 0; i < customStates.length; i++)
        {
            customStates[i].restore(snapshot.customSnapshots[i]);
        }
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Merges the outputs of this parse state into its inputs.
     */
    public void commit()
    {
        if (end > start)
        {
            seeds = null;
        }

        start = end;
        blackStart = blackEnd;
        treeChildrenCount = tree.childrenCount();

        for (CustomState state: customStates)
        {
            state.commit();
        }
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Removes all of this parse state's output. This may imply reverting changes that were done to
     * data structures in order to attach the outputs.
     */
    public void discard()
    {
        end = start;
        blackEnd = blackStart;
        tree.truncate(treeChildrenCount);

        for (CustomState state: customStates)
        {
            state.discard();
        }
    }

    // ---------------------------------------------------------------------------------------------

    public ParseChanges extract()
    {
        return new ParseChanges(
            end,
            blackEnd,
            tree.children.copyFromIndex(treeChildrenCount),
            JArrays.map(customStates, CustomState::extract));
    }

    // ---------------------------------------------------------------------------------------------

    public void merge(ParseChanges changes)
    {
        end = changes.end;
        blackEnd = changes.blackEnd;
        tree.addAll(changes.children);

        for (int i = 0; i < customStates.length; ++i)
        {
            customStates[i].merge(changes.customChanges[i]);
        }
    }

    // ---------------------------------------------------------------------------------------------

    public void uncommit(ParseStateSnapshot snapshot)
    {
        start               = snapshot.start;
        blackStart          = snapshot.blackStart;
        treeChildrenCount   = snapshot.treeChildrenCount;
        flags               = snapshot.flags; // needed?
        seeds               = snapshot.seeds;

        for (int i = 0; i < customStates.length; ++i)
        {
            customStates[i].uncommit(snapshot.customSnapshots[i]);
        }
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Produce an object that combines all parse inputs of this state, fit to be used as
     * a memoization key, or to preserve the inputs for a later run.
     */
    public ParseInputs inputs(ParsingExpression pe)
    {
        return new ParseInputs(
            pe,
            start,
            blackStart,
            precedence,
            flags,
            seeds.clone(),
            blocked.clone(),
            minPrecedence.clone(),
            JArrays.map(customStates, CustomState::inputs));
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public String toString()
    {
        return String.format("(%X) [%d/%d - %d/%d[ tree(%d/%d) flags(%s)",
            hashCode(),
            start,
            blackStart,
            end,
            blackEnd,
            treeChildrenCount,
            tree.childrenCount(),
            Integer.toString(flags, 2));
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // FLAGS
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public void forbidErrorRecording()
    {
        flags |= PSF_DONT_RECORD_ERRORS;
    }

    // ---------------------------------------------------------------------------------------------

    public boolean isErrorRecordingForbidden()
    {
        return (flags & PSF_DONT_RECORD_ERRORS) != 0;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}

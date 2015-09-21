package com.norswap.autumn.parsing;

import com.norswap.autumn.parsing.expressions.common.ParsingExpression;
import com.norswap.autumn.parsing.tree.BuildParseTree;
import com.norswap.util.Array;
import com.norswap.util.DeepCopy;

import static com.norswap.autumn.parsing.Registry.*; // PSF_*

/**
 * An instance of this class is passed to every parsing expression invocation {@link
 * ParsingExpression#parse}.
 * <p>
 * Its role is dual. On the one hand, it holds both the inputs for the invocation (things that will
 * influence the invocation, or the produced outputs). On the other hand, the parse state is also
 * where the produced outputs will be attached. As such, it allows parsing expressions to exchange
 * information on the way down (inputs) and on the way up (outputs).
 * <p>
 * The standard Autumn inputs are described in {@link StandardParseInput}, while the outputs are
 * described in this file (single-inheritance meant they couldn't get their own file).
 * <p>
 * Custom parse inputs can be manipulated via the {@link #inputs} field. You must index this array
 * with a handle you received from {@link Registry#ParseInputHandleFactory}.
 * <p>
 * Custom parse outputs can be manipulated via the {@link #outputs} field. You must index this array
 * with a handle you received from {@link Registry#ParseOutputHandleFactory}.
 */
public final class ParseState extends StandardParseInput implements Cloneable
{
    ////////////////////////////////////////////////////////////////////////////////////////////////
    // OUTPUT

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
    public ErrorOutput errors;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * An array of additional user-defined parse inputs.
     */
    public ParseInput[] inputs;

    /**
     * An array of additional user-defined parse outputs.
     */
    public ParseOutput[] outputs;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Creates the parse state to be passed to the root parsing expression.
     */
    static ParseState root()
    {
        ParseState root = new ParseState();
        root.end = 0;
        root.blackEnd = 0;
        root.tree = new BuildParseTree();
        root.inputs = new ParseInput[Registry.ParseInputHandleFactory.size()];
        root.outputs = new ParseOutput[Registry.ParseOutputHandleFactory.size()];
        // TODO
        root.errors = new DefaultErrorOutput();
        return root;
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Creates a clone (not a deep copy) of the passed state.
     */
    public static ParseState from(ParseState state)
    {
        return state.clone();
    }

    // ---------------------------------------------------------------------------------------------

    private ParseState()
    {
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Gets the seed for the given expression, if any; otherwise returns null.
     */
    public OutputChanges getSeed(ParsingExpression pe)
    {
        if (seeds == null)
        {
            return null;
        }

        for (Seed seed: seeds)
        {
            if (seed.expression == pe)
            {
                return seed.changes;
            }
        }

        return null;
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Adds a seed for the given expression.
     */
    public void pushSheed(ParsingExpression pe, OutputChanges changes)
    {
        if (seeds == null)
        {
            seeds = new Array<>();
        }

        seeds.push(new Seed(pe, changes));
    }


    // ---------------------------------------------------------------------------------------------

    /**
     * Sets the seed of the innermost left-recursive node or expression cluster being parsed.
     */
    public void setSeed(OutputChanges changes)
    {
        seeds.push(new Seed(seeds.pop().expression, changes));
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Removes the seed of the innermost left-recursive node or expression cluster being parsed.
     */
    public OutputChanges popSeed()
    {
        return seeds.pop().changes;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Advances the end (and black end) position by n characters.
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

    // ---------------------------------------------------------------------------------------------

    /**
     * Merges the outputs of the child with this parse state.
     */
//    public void merge(ParseState child)
//    {
//        this.end = child.end;
//        this.blackEnd = child.blackEnd;
//
//        for (ParseOutput output: outputs)
//        {
//            if (output != null)
//            {
//                output.merge(this, child);
//            }
//        }
//    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Produce an object that combines all parse inputs of this state, fit to be used as
     * a memoization key, or to preserve the inputs for a later run.
     */
    public ParseInputs inputs(ParsingExpression pe)
    {
        StandardParseInput stdInput = new StandardParseInput(this);
        ParseInput[] inputs = DeepCopy.of(this.inputs, ParseInput[]::new);
        return new ParseInputs(pe, stdInput, inputs);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public StandardStateSnapshot snapshot()
    {
        return new StandardStateSnapshot(
            start,
            blackStart,
            end,
            blackEnd,
            treeChildrenCount,
            flags,
            seeds);
    }

    // ---------------------------------------------------------------------------------------------

    public void restore(StandardStateSnapshot snapshot)
    {
        start               = snapshot.start;
        blackStart          = snapshot.blackStart;
        end                 = snapshot.end;
        blackEnd            = snapshot.blackEnd;
        treeChildrenCount   = snapshot.treeChildrenCount;
        flags               = snapshot.flags;
        seeds               = snapshot.seeds;

        tree.truncate(treeChildrenCount);

        for (ParseOutput output: outputs)
        {
            if (output != null) output.reset(this);
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

        for (ParseOutput output: outputs)
        {
            if (output != null)
            {
                output.advance(this);
            }
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

        for (ParseOutput output: outputs)
        {
            if (output != null) output.reset(this);
        }
    }

    // ---------------------------------------------------------------------------------------------

    public OutputChanges extract()
    {
        return new OutputChanges(this);
    }

    // ---------------------------------------------------------------------------------------------

    public void merge(OutputChanges changes)
    {
        changes.mergeInto(this);
    }

    // ---------------------------------------------------------------------------------------------

    public void uncommit(StandardStateSnapshot snapshot)
    {
        start               = snapshot.start;
        blackStart          = snapshot.blackStart;
        treeChildrenCount   = snapshot.treeChildrenCount;
        flags               = snapshot.flags; // needed?
        seeds               = snapshot.seeds;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public String toString()
    {
        return String.format("(%X) [%d/%d - %d/%d[ tree(%d/%d) flags(%s)",
            hashCode(),
            start, blackStart,
            end, blackEnd,
            treeChildrenCount, tree.childrenCount(),
            Integer.toString(flags, 2));
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected ParseState clone()
    {
        try {
            return (ParseState) super.clone();
        }
        catch (CloneNotSupportedException e)
        {
            return null; // unreachable
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Standard Flags
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

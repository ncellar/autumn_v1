package com.norswap.autumn.parsing;

import java.util.ArrayDeque;
import java.util.Deque;

import static com.norswap.autumn.parsing.ParseFrameFlags.*; // PFF_*
import static com.norswap.autumn.parsing.Precedence.ESCAPE_PRECEDENCE;
import static com.norswap.autumn.parsing.Precedence.NO_PRECEDENCE;

/**
 * A parse frame is a snapshot of the parser's state.
 *
 * Each call to {@link Parser#parse(ParsingExpression, ParseFrame)} is passed a frame in
 * addition to the expression to invoke. The frame is tied to the parent expression of the passed
 * expression. We call this frame "the old frame".
 *
 * The old frame indicates the following:
 *
 * (-) the position at which to invoke the expression
 * (-) the current precedence level
 * (-) the seed values for the current position
 * (-) the left-associative expression we're currently parsing (those are blocked from recursing)
 * (-) whether output should be captured
 * (-) which choice to cut if a cut operator is encountered
 * (-) where to merge captured output
 *
 * Within the call the old frame is modified:
 *
 * (-) The position is set past the text matching the invoked expression, or to -1 if the
 *     expression didn't match.
 * (-) Seed values are replaced with an empty set if the position advances.
 * (-) The call can signal that the choice to which the frame is tied was cut.
 * (-) If the expression succeeds, captured output are merged into the indicated parent.
 *
 * Excepted for indicating a cut, all of these are performed by the `parse` function outside of the
 * operator-specific code.
 *
 * Inside the `parse` function, a new frame is created. This frame is tied to the expression to
 * invoke, and will be passed to the `parse` call for sub-expressions.
 *
 * Modifications made to the old frame by sub-expressions implement a sequential behaviour:
 * repeatedly calling successful sub-expressions with the new frame will match them to sequential
 * chunks of source text.
 *
 * Often, other behaviours are required, and it is then necessary to modify the frame between
 * the invocation of sub-expressions. Here are the means available:
 *
 * (-) explicitly setting the position
 * (-) calling {@link #resetPositionsAndSeeds}
 * (-) calling {@link #reset}
 * (-) calling {@link #unCut}
 *
 * (-) Prior to invoking the sub-expression, substituting the parse output by another returned
 *     by {@link ParseOutput#collect}. This allows you to discard the parse outputs or merge
 *     them at some later point. Don't forget to restore the original parse output afterwards.
 *
 * (-) Prior to invoking the sub-expression, calling {@link #forbidCapture}. This can be undone
 *     with {@link #allowCapture}.
 *
 * (-) Prior to invoking the sub-expression, calling {@link #isolateCuts}. This can be undone with
 *     {@link #connectCuts}.
 */
public final class ParseFrame
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private int position;

    private int blackPosition;

    public ParseOutput parseOutput;

    private int precedence;

    private int childrenSize;

    private ParseFrame parentCuttable;

    private Deque<ParseOutput> seeds;

    private Deque<ParsingExpression> leftAssociatives;

    private int flags;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public static ParseFrame rootFrame(ParsingExpression rootExpression)
    {
        ParseFrame result = new ParseFrame();

        result.position = 0;
        result.blackPosition = 0;
        result.parseOutput = new ParseOutput(rootExpression, 0);
        result.parseOutput.setCaptureName("root");
        result.childrenSize = 0;
        result.seeds = null;
        result.leftAssociatives = new ArrayDeque<>();

        return result;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private ParseFrame() {}

    public ParseFrame(ParsingExpression pe, ParseFrame oldFrame)
    {
        // --------------------------------------

        position = oldFrame.position;
        blackPosition = position;

        // --------------------------------------

        parseOutput =  pe.requiresCapture() && !oldFrame.isCaptureForbidden()
            || pe.requiresMemoization()
            || pe.isLeftRecursive()
            ? new ParseOutput(pe, position)
            : oldFrame.parseOutput;

        childrenSize = parseOutput.childrenSize();

        // --------------------------------------
        // PRECEDENCE

        precedence = pe.precedence();

        // NO_PRECEDENCE means current precedence isn't affected
        if (precedence == NO_PRECEDENCE)
        {
            precedence = oldFrame.precedence;
        }

        // ESCAPE_PRECEDENCE means reset the precedence to 0
        if (precedence == ESCAPE_PRECEDENCE)
        {
            precedence = NO_PRECEDENCE;
        }

        // --------------------------------------

        if (pe.isCuttable())
        {
            setFlags(PFF_CUTTABLE);
        }

        parentCuttable = oldFrame.isCuttable()
            ? oldFrame
            : oldFrame.parentCuttable;

        // --------------------------------------
        // LEFT-RECURSION & ASSOCIATIVITY

        seeds = oldFrame.seeds;

        // `leftAssociatives` never changes. We could make it static but it would be bad if we
        // ever wanted to run parses in parallel.

        leftAssociatives = oldFrame.leftAssociatives;

        if (pe.isLeftRecursive())
        {
            if (seeds == null)
            {
                // Necessary so that we can still access the seed stack after growing the seeds.
                // Not wrong either, since frame.position == oldFrame.position.
                seeds = oldFrame.seeds = new ArrayDeque<>(2);
            }

            pushSeed(ParseOutput.fail(pe, position));
        }

        if (pe.isLeftAssociative())
        {
            leftAssociatives.push(pe);
        }

        // --------------------------------------

        // Set DONT_CAPTURE if the old frame has them.
        setFlags(oldFrame.flags & PFF_DONT_CAPTURE);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public boolean failed()
    {
        return position == -1;
    }

    public int precedence()
    {
        return precedence;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    // CUT HANDLING

    // ---------------------------------------------------------------------------------------------

    /**
     * Is the frame tied to an expression which is a cuttable choice?
     */
    public boolean isCuttable()
    {
        return hasFlagsSet(PFF_CUTTABLE);
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Prevent cuts on this frame or frames tied to sub-expressions to cut enclosing choices.
     */
    public void isolateCuts()
    {
        // This causes cut() to always cut the current frame and never any of its cuttable
        // ancestors.
        parentCuttable = this;
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Lifts the effect of {@link #isolateCuts}.
     */
    public void connectCuts(ParseFrame oldFrame)
    {
        parentCuttable = oldFrame.isCuttable()
            ? oldFrame
            : oldFrame.parentCuttable;
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Mark the closest enclosing uncut choice as being cut.
     */
    public boolean cut()
    {
        if (isCuttable() && !isCut())
        {
            setFlags(PFF_CUT);
        }
        else if (parentCuttable != null)
        {
            parentCuttable.setFlags(PFF_CUT);
            parentCuttable = parentCuttable.parentCuttable;
        }

        return parentCuttable == null;
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Indicates if the choice tied to this frame has been cut.
     */
    public boolean isCut()
    {
        return hasFlagsSet(PFF_CUT);
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Mark the choice tied to this frame has uncut.
     */
    public void unCut()
    {
        clearFlags(PFF_CUT);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    // CAPTURE

    /**
     * Prevent sub-expressions from capturing any output.
     */
    public void forbidCapture()
    {
        setFlags(PFF_DONT_CAPTURE);
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Allow sub-expressions to capture output.
     */
    public void allowCapture()
    {
        clearFlags(PFF_DONT_CAPTURE);
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Are sub-expressions allowed to capture output?
     */
    public boolean isCaptureForbidden()
    {
        return hasFlagsSet(PFF_DONT_CAPTURE);
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////

    // CHANGING POSITION & RESETTING

    // ---------------------------------------------------------------------------------------------

    public int position()
    {
        return position;
    }

    // ---------------------------------------------------------------------------------------------

    public int blackPosition()
    {
        return blackPosition;
    }

    // ---------------------------------------------------------------------------------------------

    public void setPosition(int position)
    {
        this.position = position;
    }

    // ---------------------------------------------------------------------------------------------

    public void setBlackPosition(int beforeWhitespace)
    {
        this.blackPosition = beforeWhitespace;
    }

    // ---------------------------------------------------------------------------------------------

    public Positions positions()
    {
        return new Positions(position, blackPosition);
    }

    // ---------------------------------------------------------------------------------------------

    public void setPositions(int position)
    {
        this.position = position;
        this.blackPosition = position;
    }

    // ---------------------------------------------------------------------------------------------

    public void setPositions(Positions positions)
    {
        position = positions.position;
        blackPosition = positions.blackPosition;
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Reset the positions of this frame to the position of the old frame.
     *
     * It is an optimization of {@link #reset} when we know that the seeds weren't
     * modified and that the conditions listed in {@link #resetPositionsAndSeeds} hold.
     */
    public void resetPositions(ParseFrame oldFrame)
    {
        position = oldFrame.position;
        blackPosition = position;
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Reset the positions of this frame to the position of the frame. Also restore the seeds
     * associated to that position.
     *
     * It is an optimization of {@link #reset} to be used when we know that
     * the frame wasn't cut and that the parse output wasn't modified or saved anywhere.
     */
    public void resetPositionsAndSeeds(ParseFrame oldFrame)
    {
        position = oldFrame.position;
        blackPosition = position;
        seeds = oldFrame.seeds;

        // If the expression associated with the frame is left-recursive, it will have a seed
        // in the seed list at this point.
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Reset anything that might have changed in this frame to its initial value.
     */
    public void reset(ParseFrame oldFrame)
    {
        resetPositionsAndSeeds(oldFrame);
        unCut();

        if (parseOutput != oldFrame.parseOutput)
        {
            parseOutput = new ParseOutput(parseOutput.expression(), oldFrame.position);
        }
        else
        {
            // Discard unsaved children.
            oldFrame.childrenSize = oldFrame.parseOutput.childrenSize();
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    // FINALIZING AND MERGING

    // ---------------------------------------------------------------------------------------------

    /**
     * Add the missing info to the parse output, if required.
     */
    void finalizeOutput(ParseFrame oldFrame, CharSequence text)
    {
        // Only finalize if this frame has its own parse output, which can be captured or memoized.

        if (parseOutput != oldFrame.parseOutput)
        {
            parseOutput.setEndPosition(position, blackPosition, text);
        }
    }

    // ---------------------------------------------------------------------------------------------

    void fail()
    {
        position = -1;
    }

    // ---------------------------------------------------------------------------------------------

    void propagateFailure()
    {
        position = -1;

        // Discard unsaved children.
        parseOutput.truncateChildren(childrenSize);
    }

    // ---------------------------------------------------------------------------------------------

    void succeed(ParseFrame subFrame)
    {
        if (subFrame.position > this.position)
        {
            this.seeds = null;
        }

        this.position = subFrame.position;
        this.blackPosition = subFrame.blackPosition;

        // Save unsaved children.
        childrenSize = parseOutput.childrenSize();
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Merge the passed output into this frame's output, assuming the passed output is distinct
     * from the one it's to be merged into, that it is successful and that capture is allowed.
     */
    public void mergeOutput(ParseOutput parseOutput)
    {
        if (parseOutput!= this.parseOutput
                && parseOutput.endPosition() != -1
                && !isCaptureForbidden())
        {
            this.parseOutput.add(parseOutput);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    // LEFT-ASSOCIATIVES

    // ---------------------------------------------------------------------------------------------

    void popLeftAssociative()
    {
        leftAssociatives.pop();
    }

    // ---------------------------------------------------------------------------------------------

    boolean isLeftAssociative(ParsingExpression pe)
    {
        for (ParsingExpression e: leftAssociatives)
        {
            if (pe == e)
            {
                return true;
            }
        }

        return false;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    // SEEDS

    // ---------------------------------------------------------------------------------------------

    void pushSeed(ParseOutput output)
    {
        assert(seeds != null);

        seeds.push(output);
    }

    // ---------------------------------------------------------------------------------------------

    ParseOutput popSeed()
    {
        assert (seeds != null);

        return seeds.pop();
    }

    // ---------------------------------------------------------------------------------------------

    ParseOutput getSeed(ParsingExpression pe)
    {
        if (seeds == null)
        {
            return null;
        }

        for (ParseOutput seed: seeds)
        {
            if (seed.expression() == pe)
            {
                return seed;
            }
        }

        return null;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public boolean hasAnyFlagsSet(int flagsToCheck)
    {
        return (flags & flagsToCheck) != 0;
    }

    public boolean hasFlagsSet(int flagsToCheck)
    {
        return (flags & flagsToCheck) == flagsToCheck ;
    }

    public void setFlags(int flagsToAdd)
    {
        flags |= flagsToAdd;
    }

    public void clearFlags(int flagsToClear)
    {
        flags &= ~flagsToClear;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}

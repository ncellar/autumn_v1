package com.norswap.autumn.parsing2;

import com.norswap.autumn.parsing.ParsingExpression;
import com.norswap.autumn.parsing.Precedence;
import com.norswap.autumn.util.Array;

public class ParseInput
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    // FLAGS

    /**
     * Indicates we should not perform any capture on the sub-expressions of the expression
     * associated with this parse input.
     */
    private static int PFF_DONT_CAPTURE = 1;

    /**
     * Indicates that the expression associated to this parse input can be cut.
     */
    private static int PFF_CUTTABLE  = 2;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public int position;
    public int blackPosition;
    public int precedence;

    int resultChildrenCount;
    int flags;
    Array<ParseResult> seeds;
    Array<ParsingExpression> leftAssociatives;
    ParseInput parentCuttable;

    // output
    ParseOutput output;
    ParseResult result;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    static ParseInput root()
    {
        ParseInput result = new ParseInput();
        result.leftAssociatives = new Array<>();
        return result;
    }

    // ---------------------------------------------------------------------------------------------

    ParseInput(ParsingExpression pe, ParseInput parent)
    {
        this.position = parent.position;
        this.blackPosition = parent.blackPosition;
        this.precedence = Precedence.get(pe, parent.precedence);
        this.seeds = parent.seeds;
        this.leftAssociatives = parent.leftAssociatives;
        this.flags = parent.flags;
        this.output = new ParseOutput(parent);

        this.result = pe.requiresCapture() && !parent.isCaptureForbidden()
                || pe.requiresMemoization()
                || pe.isLeftRecursive()
            ? new ParseResult(pe, position)
            : parent.result;

        this.resultChildrenCount = result.childrenCount();

        this.parentCuttable = parent.isCuttable()
            ? parent
            : parent.parentCuttable;

        if (pe.isLeftRecursive())
        {
            if (seeds == null)
            {
                // Attach the seeds to {@code input} so that they are still accessible after
                // resetting this parse input.
                parent.seeds = seeds = new Array<>();
            }

            seeds.push(ParseResult.failure(pe, position));

            if (pe.isLeftAssociative())
            {
                leftAssociatives.push(pe);
            }
        }
    }

    // ---------------------------------------------------------------------------------------------

    private ParseInput()
    {
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Mostly resets this parse input to its initial state, as determined by its parent.
     *
     * However: (1) the parse result is left untouched but is replaced by a fresh parse result;
     * (2) the effect of any performed cut is kept.
     */
    void reset(ParsingExpression pe, ParseInput parent)
    {
        this.position = parent.position;
        this.blackPosition = parent.blackPosition;
        this.seeds = parent.seeds;
        this.flags = parent.flags;
        this.output.reset(parent);

        this.result = new ParseResult(pe, parent.position);
        this.resultChildrenCount = 0;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    ParseResult getSeed(ParsingExpression pe)
    {
        if (seeds == null)
        {
            return null;
        }

        for (ParseResult seed: seeds)
        {
            if (seed.expression() == pe)
            {
                return seed;
            }
        }

        return null;
    }

    // ---------------------------------------------------------------------------------------------

    boolean isLeftAssociative(ParsingExpression pe)
    {
        if (!pe.isLeftAssociative())
        {
            return false;
        }

        for (ParsingExpression e: leftAssociatives)
        {
            if (pe == e)
            {
                return true;
            }
        }

        return false;
    }

    // ---------------------------------------------------------------------------------------------

    public void advance(ParseOutput up)
    {
        if (up.position > this.position)
        {
            this.seeds = null;
        }

        this.position = up.position;
        this.blackPosition = up.blackPosition;
        this.resultChildrenCount = result.childrenCount();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public void forbidCapture()
    {
        setFlags(PFF_DONT_CAPTURE);
    }

    // ---------------------------------------------------------------------------------------------

    public boolean isCaptureForbidden()
    {
        return hasFlagsSet(PFF_DONT_CAPTURE);
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Prevent cuts on the output or child outputs to cut enclosing choices.
     */
    public void isolateCuts()
    {
        // This causes {@link ParseOutput#cut()} to always cut the current output and never
        // any of the ancestor cuttable outputs.
        this.parentCuttable = this;
    }

    // ---------------------------------------------------------------------------------------------

    public boolean isCuttable()
    {
        return hasFlagsSet(PFF_CUTTABLE);
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

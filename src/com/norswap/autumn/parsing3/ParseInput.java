package com.norswap.autumn.parsing3;

import com.norswap.autumn.util.Array;

import static com.norswap.autumn.parsing3.Registry.*; // PIF_*

public class ParseInput
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public int position;
    public int blackPosition;
    public int precedence;

    public int flags;
    public Array<Seed> seeds;

    public int resultChildrenCount;
    public int cutsCount;

    // output
    public ParseOutput output;
    public ParseResult result;
    public Array<String> cuts;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    static ParseInput root()
    {
        ParseInput input = new ParseInput();
        input.output = new ParseOutput(0, 0);
        input.cuts = new Array<>();
        return input;
    }

    // ---------------------------------------------------------------------------------------------

    private ParseInput()
    {
    }

    // ---------------------------------------------------------------------------------------------

    public ParseInput(ParseInput parent)
    {
        this.position = parent.position;
        this.blackPosition = parent.blackPosition;
        this.precedence = parent.precedence;
        this.seeds = parent.seeds;
        this.flags = parent.flags & (PIF_DONT_CAPTURE | PIF_DONT_MEMOIZE | PIF_DONT_RECORD_ERRORS);

        this.result = parent.result;
        this.resultChildrenCount = result.childrenCount();

        this.output = new ParseOutput(parent);
        this.cuts = parent.cuts;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public OutputChanges getSeedChanges(ParsingExpression pe)
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
     * Advance the position (and related fields) of this parse input up to position pointed to
     * by the passed output. Also updates the fields that records the number of children of the
     * result instance.
     */
    public void advance(ParseOutput up)
    {
        if (up.position > this.position)
        {
            this.seeds = null;
            this.clearFlags(PIF_DONT_MEMOIZE);
        }

        this.position = up.position;
        this.blackPosition = up.blackPosition;
        this.resultChildrenCount = result.childrenCount();
        this.cutsCount = cuts.size();
    }

    // ---------------------------------------------------------------------------------------------

    public void resetOutput()
    {
        output.reset(this);
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Discard captures in the result that are newer than the time where the field tracking the
     * number of children was last updated (most often at the time the frame was create, although
     * {@link #setResult} and {@link #advance} also modify that field).
     */
    public void resetResultChildren()
    {
        if (result.children != null)
        {
            result.children.truncate(resultChildrenCount);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Standard Flags
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public void forbidMemoization()
    {
        setFlags(PIF_DONT_MEMOIZE);
    }

    // ---------------------------------------------------------------------------------------------

    public boolean isMemoizationForbidden()
    {
        return hasFlagsSet(PIF_DONT_MEMOIZE);
    }

    // ---------------------------------------------------------------------------------------------

    public void forbidErrorRecording()
    {
        setFlags(PIF_DONT_RECORD_ERRORS);
    }

    // ---------------------------------------------------------------------------------------------

    public boolean isErrorRecordingForbidden()
    {
        return hasFlagsSet(PIF_DONT_RECORD_ERRORS);
    }

    // ---------------------------------------------------------------------------------------------

    public void forbidCapture()
    {
        setFlags(PIF_DONT_CAPTURE);
    }

    // ---------------------------------------------------------------------------------------------

    public boolean isCaptureForbidden()
    {
        return hasFlagsSet(PIF_DONT_CAPTURE);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Generic Flag Manipulation Functions
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

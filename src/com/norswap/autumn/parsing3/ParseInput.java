package com.norswap.autumn.parsing3;

import com.norswap.autumn.util.Array;

import static com.norswap.autumn.parsing3.Registry.*; // PIF_*

public class ParseInput
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public int start;
    public int blackStart;
    public int precedence;

    public int flags;
    public Array<Seed> seeds;

    // output
    public int end;
    public int blackEnd;
    public ParseResult result;
    public Array<String> cuts;

    public int resultChildrenCount;
    public int cutsCount;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    static ParseInput root()
    {
        ParseInput root = new ParseInput();
        root.end = 0;
        root.blackEnd = 0;
        root.cuts = new Array<>();
        return root;
    }

    // ---------------------------------------------------------------------------------------------

    private ParseInput()
    {
    }

    // ---------------------------------------------------------------------------------------------

    public ParseInput(ParseInput parent)
    {
        this.start = parent.start;
        this.blackStart = parent.blackStart;
        this.precedence = parent.precedence;
        this.seeds = parent.seeds;
        this.flags = parent.flags;
        this.end = parent.end;
        this.blackEnd = parent.blackEnd;
        this.result = parent.result;
        this.resultChildrenCount = parent.resultChildrenCount;
        this.cuts = parent.cuts;
        this.cutsCount = parent.cutsCount;
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

    public void advance()
    {
        if (end > start)
        {
            seeds = null;
            clearFlags(PIF_DONT_MEMOIZE);
        }

        start = end;
        blackStart = blackEnd;
        resultChildrenCount = result.childrenCount();
        cutsCount = cuts.size();
    }

    // ---------------------------------------------------------------------------------------------

    public void advance(int n)
    {
        if (n != 0)
        {
            end += n;
            blackEnd = end;
        }
    }

    // ---------------------------------------------------------------------------------------------

    public void resetOutput()
    {
        end = start;
        blackEnd = blackStart;

        if (result.children != null)
        {
            result.children.truncate(resultChildrenCount);
        }
    }

    // ---------------------------------------------------------------------------------------------

    public void resetAllOutput()
    {
        resetOutput();
        cuts.truncate(cutsCount);
    }

    // ---------------------------------------------------------------------------------------------

    public void fail()
    {
        this.end = -1;
        this.blackEnd = -1;
    }

    // ---------------------------------------------------------------------------------------------

    public boolean succeeded()
    {
        return end != -1;
    }

    // ---------------------------------------------------------------------------------------------

    public boolean failed()
    {
        return end == -1;
    }

    // ---------------------------------------------------------------------------------------------

    public void merge(ParseInput child)
    {
        this.end = child.end;
        this.blackEnd = child.blackEnd;
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

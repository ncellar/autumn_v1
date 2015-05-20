package com.norswap.autumn.parsing;

import com.norswap.autumn.util.Array;
import com.norswap.autumn.util.HandleMap;

import static com.norswap.autumn.parsing.Registry.*; // PIF_*

public final class ParseInput
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
    public ParseTree tree;
    public Array<String> cuts;

    public int treeChildrenCount;
    public int cutsCount;

    public HandleMap ext;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    static ParseInput root()
    {
        ParseInput root = new ParseInput();
        root.end = 0;
        root.blackEnd = 0;
        root.cuts = new Array<>();
        root.ext = new HandleMap();
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
        this.tree = parent.tree;
        this.treeChildrenCount = parent.treeChildrenCount;
        this.cuts = parent.cuts;
        this.cutsCount = parent.cutsCount;
        this.ext = parent.ext;
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
        treeChildrenCount = tree.childrenCount();
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

        if (tree.children != null)
        {
            tree.children.truncate(treeChildrenCount);
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

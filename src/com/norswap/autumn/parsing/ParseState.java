package com.norswap.autumn.parsing;

import com.norswap.autumn.parsing.expressions.common.ParsingExpression;
import com.norswap.util.Array;
import com.norswap.util.DeepCopy;
import com.norswap.util.HandleMap;

import static com.norswap.autumn.parsing.Registry.*; // PSF_*

/**
 * TODO
 */
public final class ParseState extends StandardParseInput implements Cloneable
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    // output
    public int end;
    public int blackEnd;
    public ParseTree tree;

    public int treeChildrenCount;

    public ParseInput[] inputs;
    public HandleMap ext;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    static ParseState root()
    {
        ParseState root = new ParseState();
        root.end = 0;
        root.blackEnd = 0;
        root.tree = new ParseTree(null, new Array<>(), false);
        root.tags = new Array<>();
        root.inputs = new ParseInput[Registry.ParseInputHandleFactory.size()];
        root.ext = new HandleMap();
        return root;
    }

    // ---------------------------------------------------------------------------------------------

    public static ParseState from(ParseState parent)
    {
        return parent.clone();
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
     * Sets the seed of the innermost left-recursion or exression cluster being parsed.
     */
    public void setSeed(OutputChanges changes)
    {
        seeds.push(new Seed(seeds.pop().expression, changes));
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Removes the seed of the innermost left-recursive or expression cluster being parsed.
     */
    public OutputChanges popSeed()
    {
        return seeds.pop().changes;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public void advance()
    {
        if (end > start)
        {
            seeds = null;
        }

        start = end;
        blackStart = blackEnd;
        treeChildrenCount = tree.childrenCount();
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
        tree.truncate(treeChildrenCount);
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

    public void merge(ParseState child)
    {
        this.end = child.end;
        this.blackEnd = child.blackEnd;
    }

    // ---------------------------------------------------------------------------------------------

    public ParseInputs inputs(ParsingExpression pe)
    {
        StandardParseInput stdInput = new StandardParseInput(this);
        ParseInput[] inputs = DeepCopy.of(this.inputs, ParseInput[]::new);
        return new ParseInputs(pe, stdInput, inputs);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public String toString()
    {
        return String.format("(%X) [%d/%d - %d/%d[ tree(%d/%d) acc(%s)%s tags(%d) flags(%s)",
            hashCode(), start, blackStart, end, blackEnd, treeChildrenCount, tree.childrenCount(),
            accessor, isCaptureGrouping() ? "(g)" : "", tags.size(), Integer.toString(flags, 2));
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

    public void forbidMemoization()
    {
        setFlags(PSF_DONT_MEMOIZE);
    }

    // ---------------------------------------------------------------------------------------------

    public boolean isMemoizationForbidden()
    {
        return hasFlagsSet(PSF_DONT_MEMOIZE);
    }

    // ---------------------------------------------------------------------------------------------

    boolean forbidErrorRecording = false;

    public void forbidErrorRecording()
    {
        setFlags(PSF_DONT_RECORD_ERRORS);
    }

    // ---------------------------------------------------------------------------------------------

    public boolean isErrorRecordingForbidden()
    {
        return hasFlagsSet(PSF_DONT_RECORD_ERRORS);
    }

    // ---------------------------------------------------------------------------------------------

    public void enableGroupingCapture()
    {
        setFlags(PSF_GROUPING_CAPTURE);
    }

    // ---------------------------------------------------------------------------------------------

    public void disableGroupingCapture()
    {
        clearFlags(PSF_GROUPING_CAPTURE);
    }

    // ---------------------------------------------------------------------------------------------

    public boolean isCaptureGrouping()
    {
        return hasFlagsSet(PSF_GROUPING_CAPTURE);
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

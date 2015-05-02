package com.norswap.autumn.parsing3;

public class ParseOutput
{
    ////////////////////////////////////////////////////////////////////////////////////////////////
    // FLAGS

    /**
     * Indicates that a cut operator has been encountered while parsing the children of the
     * parsing expression corresponding to this frame.
     */
    private static int PFF_CUT = 1;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public int position;
    public int blackPosition;
    private int flags;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    static ParseOutput failure()
    {
        ParseOutput output = new ParseOutput(-1, -1);
        return output;
    }

    // ---------------------------------------------------------------------------------------------

    ParseOutput(int position, int blackPosition)
    {
        this.position = position;
        this.blackPosition = blackPosition;
    }

    // ---------------------------------------------------------------------------------------------

    ParseOutput(ParseInput input)
    {
        reset(input);
    }

    // ---------------------------------------------------------------------------------------------

    ParseOutput(ParseOutput output)
    {
        become(output);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public void fail()
    {
        position = -1;
        blackPosition = -1;
    }

    // ---------------------------------------------------------------------------------------------

    public boolean failed()
    {
        return position == -1;
    }

    // ---------------------------------------------------------------------------------------------

    public boolean succeeded()
    {
        return position != -1;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    void advance(int n)
    {
        this.position += n;
        this.blackPosition += n;
    }

    // ---------------------------------------------------------------------------------------------

    public void become(ParseOutput output)
    {
        this.position = output.position;
        this.blackPosition = output.blackPosition;
        this.flags = output.flags;
    }

    // ---------------------------------------------------------------------------------------------

    void reset(ParseInput input)
    {
        this.position = input.position;
        this.blackPosition = input.blackPosition;
        this.flags = 0;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public boolean cut(ParseInput input)
    {
        if (input.isCuttable() && !isCut())
        {
            setFlags(PFF_CUT);
        }
        else if (input.parentCuttable != null)
        {
            input.parentCuttable.output.setFlags(PFF_CUT);
            input.parentCuttable = input.parentCuttable.parentCuttable;
        }

        return input.parentCuttable == null;
    }

    // ---------------------------------------------------------------------------------------------

    public void unCut()
    {
        clearFlags(PFF_CUT);
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Indicates if the choice tied to this frame has been cut.
     */
    public boolean isCut()
    {
        return hasFlagsSet(PFF_CUT);
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

package com.norswap.autumn.parsing3;

import static com.norswap.autumn.parsing3.Registry.POF_CUT;

public class ParseOutput
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public int position;
    public int blackPosition;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public static ParseOutput failure()
    {
        ParseOutput output = new ParseOutput(-1, -1);
        return output;
    }

    // ---------------------------------------------------------------------------------------------

    public ParseOutput(int position, int blackPosition)
    {
        this.position = position;
        this.blackPosition = blackPosition;
    }

    // ---------------------------------------------------------------------------------------------

    public ParseOutput(ParseInput input)
    {
        reset(input);
    }

    // ---------------------------------------------------------------------------------------------

    public ParseOutput(ParseOutput output)
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

    public void advance(int n)
    {
        if (n != 0)
        {
            this.position += n;
            this.blackPosition = this.position;
        }
    }

    // ---------------------------------------------------------------------------------------------

    public void become(ParseOutput output)
    {
        this.position = output.position;
        this.blackPosition = output.blackPosition;
    }

    // ---------------------------------------------------------------------------------------------

    public void reset(ParseInput input)
    {
        this.position = input.position;
        this.blackPosition = input.blackPosition;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}

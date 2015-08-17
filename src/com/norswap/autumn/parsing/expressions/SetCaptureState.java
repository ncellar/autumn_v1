package com.norswap.autumn.parsing.expressions;

import com.norswap.autumn.parsing.ParseState;
import com.norswap.autumn.parsing.Parser;
import com.norswap.autumn.parsing.expressions.common.UnaryParsingExpression;
import com.norswap.util.Array;

/**
 *
 */
public final class SetCaptureState extends UnaryParsingExpression
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private String name;
    private Array<String> tags;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public SetCaptureState(String name, Array<String> tags)
    {
        this.name = name;
        this.tags = tags;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public void set()
    {

    }

    // ---------------------------------------------------------------------------------------------

    public void unset()
    {

    }

    // ---------------------------------------------------------------------------------------------

    public SetCaptureState merge(SetCaptureState other)
    {
        if (other.name != null)
        {
            this.name = other.name;
        }

        // TODO should be a proper merge
        tags.addAll(other.tags);

        return this;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void parse(Parser parser, ParseState state)
    {
        set();
        operand.parse(parser, state);
        unset();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}

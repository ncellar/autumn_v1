package com.norswap.autumn.parsing;

import com.norswap.autumn.parsing.config.DefaultMemoHandler;
import com.norswap.autumn.parsing.expressions.common.ParsingExpression;

/**
 * A collection of parse inputs obtained from a {@link ParseState} along with the parsing expression
 * these inputs are used for. This is used as a memoization key in the {@link DefaultMemoHandler}.
 * It's {@code equals} and {@code hashCode} methods consider only type 1 input fields (see {@link
 * ParseInput}).
 */
public final class ParseInputs
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public ParsingExpression pe;

    public final StandardParseInput stdInput;

    public final ParseInput[] inputs;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public ParseInputs(ParsingExpression pe, StandardParseInput stdInput, ParseInput[] inputs)
    {
        this.pe = pe;
        this.stdInput = stdInput;
        this.inputs = inputs;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (!(o instanceof ParseInputs)) return false;

        ParseInputs that = (ParseInputs) o;

        if (!stdInput.inputEquals(that.stdInput)) return false;
        if (inputs == that.inputs) return true;
        if (inputs == null || that.inputs == null) return false;

        ParseInput[] inputs2 = that.inputs;

        if (inputs.length != inputs2.length) return false;

        for (int i = 0; i < inputs.length ; ++i)
        {
            ParseInput input1 = inputs[i];
            ParseInput input2 = inputs2[i];

            if (input1 == input2) continue;
            if (input1 == null || input2 == null) return false;
            if (!input1.inputEquals(input2)) return false;
        }

        return true;
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public int hashCode()
    {
        int result = stdInput.inputHashCode();

        for (ParseInput input: inputs)
        {
            result = input.inputHashCode(result);
        }

        return result;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}

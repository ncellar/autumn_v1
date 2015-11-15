package com.norswap.autumn.parsing.state;

import com.google.auto.value.AutoValue;
import com.norswap.autumn.parsing.ParsingExpression;
import com.norswap.util.Array;

/**
 * See {@link ParseState}, "Parse Inputs" section.
 */
@AutoValue
public abstract class ParseInputs
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public abstract ParsingExpression pe();
    public abstract int start();
    public abstract int blackStart();
    public abstract int precedence();
    public abstract boolean recordErrors();
    public abstract Array<Object> customInputs();

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public static ParseInputs create(
        ParsingExpression pe,
        int start,
        int blackStart,
        int precedence,
        boolean recordErrors,
        Array<Object> customInputs)
    {
        return new AutoValue_ParseInputs(pe, start, blackStart, precedence, recordErrors, customInputs);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}

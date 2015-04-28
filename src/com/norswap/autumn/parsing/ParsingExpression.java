package com.norswap.autumn.parsing;

import com.norswap.autumn.util.Caster;

import java.util.regex.Pattern;

import static com.norswap.autumn.parsing.ParsingExpressionFlags.*; // PEF_*

public final class ParsingExpression
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private final Operator operator;

    private long flags = 0;

    private int precedence;

    private String name;

    private String captureName;

    private Object ext;

    public int debugCount;

    // These fields form an union: one and only one is non-null.
    private ParsingExpression operand;
    private ParsingExpression[] operands;
    private String string;
    private Pattern pattern;
    private Object custom;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public ParsingExpression(Operator operator)
    {
        this.operator = operator;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public boolean hasAnyFlagsSet(long flagsToCheck)
    {
        return (flags & flagsToCheck) != 0;
    }

    public boolean hasFlagsSet(long flagsToCheck)
    {
        return (flags & flagsToCheck) == flagsToCheck ;
    }

    public void setFlags(long flagsToAdd)
    {
        flags |= flagsToAdd;
    }

    public void clearFlags(long flagsToClear)
    {
        flags &= ~flagsToClear;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public boolean isNamed()
    {
        return hasFlagsSet(PEF_NAMED);
    }

    public boolean isErrorRecording()
    {
        return hasFlagsSet(PEF_ERROR_RECORDING);
    }

    public boolean requiresSingleCapture()
    {
        return hasFlagsSet(PEF_SINGLE_CAPTURE);
    }

    public boolean requiresMultipleCapture()
    {
        return hasFlagsSet(PEF_MULTIPLE_CAPTURE);
    }

    public boolean requiresTextCapture()
    {
        return hasFlagsSet(PEF_TEXT_CAPTURE);
    }

    public boolean requiresCapture()
    {
        return hasAnyFlagsSet(PEF_SINGLE_CAPTURE | PEF_MULTIPLE_CAPTURE);
    }

    public boolean requiresMemoization()
    {
        return hasFlagsSet(PEF_MEMOIZE);
    }

    public boolean isCuttable()
    {
        return hasFlagsSet(PEF_CUTTABLE);
    }

    public boolean isLeftRecursive()
    {
        return hasFlagsSet(PEF_LEFT_RECURSIVE);
    }

    public boolean isLeftAssociative()
    {
        return hasFlagsSet(PEF_LEFT_ASSOCIATIVE);
    }


    public boolean isToken()
    {
        return hasFlagsSet(PEF_TOKEN);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public Operator operator()
    {
        return operator;
    }

    public ParsingExpression operand()
    {
        return operand;
    }

    public ParsingExpression[] operands()
    {
        return operands;
    }

    public String string()
    {
        return string;
    }

    public Pattern pattern()
    {
        return pattern;
    }

    public char rangeStart()
    {
        assert(operator == Operator.OP_CHAR_RANGE && string != null);
        return string.charAt(0);
    }

    public char rangeEnd()
    {
        assert (operator == Operator.OP_CHAR_RANGE && string != null);
        return string.charAt(1);
    }

    public String name()
    {
        return name;
    }

    public Object custom()
    {
        return custom;
    }

    public CustomParseOperator customParseFunction()
    {
        return Caster.cast(custom);
    }

    public CustomDumbParseOperator customDumbParseFunction()
    {
        return Caster.cast(custom);
    }

    @SuppressWarnings("unchecked")
    public <T> T ext()
    {
        return (T) ext;
    }

    public String captureName()
    {
        return captureName;
    }

    public int precedence()
    {
        return precedence;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    void setOperand(ParsingExpression operand)
    {
        this.operand = operand;
    }

    void setOperands(ParsingExpression operands[])
    {
        this.operands = operands;
    }

    void setString(String string)
    {
        this.string = string;
    }

    void setPattern(Pattern pattern)
    {
        this.pattern = pattern;
    }

    void setName(String name)
    {
        this.flags |= PEF_NAMED;
        this.name = name;
    }

    void setCustom(Object function)
    {
        this.custom = function;
    }

    void setSingleCapture(String captureName)
    {
        this.flags |= PEF_SINGLE_CAPTURE;
        this.captureName = captureName;
    }

    void setMultipleCapture(String captureName)
    {
        this.flags |= PEF_MULTIPLE_CAPTURE;
        this.captureName = captureName;
    }

    void setPrecedence(int precedence)
    {
        this.precedence = precedence;
    }

    public void setExt(Object ext)
    {
        this.ext = ext;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public String toString()
    {
        return ParsingExpressionPrinter.string(this);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}
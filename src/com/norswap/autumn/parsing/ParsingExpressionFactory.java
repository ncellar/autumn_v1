package com.norswap.autumn.parsing;

import com.norswap.autumn.parsing.expressions.*;
import com.norswap.autumn.parsing.expressions.Whitespace;

import static com.norswap.autumn.parsing.Registry.*; // PEF_*

public final class ParsingExpressionFactory
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public static Any any()
    {
        return new Any();
    }

    public static Capture capture(String captureName, ParsingExpression operand)
    {
        Capture result = new Capture();
        result.name = captureName;
        result.operand = operand;
        return result;
    }

    public static Capture captureGrouped(String captureName, ParsingExpression operand)
    {
        Capture result = capture(captureName, operand);
        result.flags |= PEF_CAPTURE_GROUPED;
        return result;
    }

    public static Capture captureText(String captureName, ParsingExpression operand)
    {
        Capture result = capture(captureName, operand);
        result.flags |= PEF_CAPTURE_TEXT;
        return result;
    }

    public static Capture captureTextGrouped(String captureName, ParsingExpression operand)
    {
        Capture result = capture(captureName, operand);
        result.flags |= PEF_CAPTURE_TEXT | PEF_CAPTURE_GROUPED;
        return result;
    }

    public static CharRange charRange(char start, char end)
    {
        CharRange result = new CharRange();
        result.start = start;
        result.end = end;
        return result;
    }

    public static CharSet charSet(char[] chars)
    {
        CharSet result = new CharSet();
        result.chars = chars;
        return result;
    }

    public static CharSet charSet(String chars)
    {
        return charSet(chars.toCharArray());
    }

    public static Choice choice(ParsingExpression... operands)
    {
        Choice result = new Choice();
        result.operands = operands;
        return result;
    }

    public static Cut cut(String cutName)
    {
        Cut result = new Cut();
        result.name = cutName;
        return result;
    }

    public static Cuttable cuttable(String name, ParsingExpression... operands)
    {
        Cuttable result = new Cuttable();
        result.name = name;
        result.operands = operands;
        return result;
    }

    public static Dumb dumb(ParsingExpression operand)
    {
        Dumb result = new Dumb();
        result.operand = operand;
        return result;
    }

    public static ParsingExpression dumb(ParsingExpression... seq)
    {
        return dumb(sequence(seq));
    }

    public static Literal literal(String string)
    {
        Literal result = new Literal();
        result.string = string;
        return result;
    }

    public static LeftRecursive leftAssociative(ParsingExpression operand)
    {
        LeftRecursive result = new LeftRecursive();
        result.operand = operand;
        result.leftAssociative = true;
        return result;
    }

    public static LeftRecursive leftAssociative(ParsingExpression... seq)
    {
        return leftAssociative(sequence(seq));
    }

    public static LeftRecursive leftRecursive(ParsingExpression operand)
    {
        LeftRecursive result = new LeftRecursive();
        result.operand = operand;
        return result;
    }

    public static LeftRecursive leftRecursive(ParsingExpression... seq)
    {
        return leftRecursive(sequence(seq));
    }

    public static LongestMatch longestMatch(ParsingExpression... operands)
    {
        LongestMatch result = new LongestMatch();
        result.operands = operands;
        return result;
    }

    public static Lookahead lookahead(ParsingExpression operand)
    {
        Lookahead result = new Lookahead();
        result.operand = operand;
        return result;
    }

    public static Lookahead lookahead(ParsingExpression... seq)
    {
        return lookahead(sequence(seq));
    }

    public static Memo memo(ParsingExpression operand)
    {
        Memo result = new Memo();
        result.operand = operand;
        return result;
    }

    public static ParsingExpression memo(ParsingExpression... seq)
    {
        return memo(sequence(seq));
    }

    public static Not not(ParsingExpression operand)
    {
        Not result = new Not();
        result.operand = operand;
        return result;
    }

    public static Not not(ParsingExpression... seq)
    {
        return not(sequence(seq));
    }

    public static Precedence noPrecedence(ParsingExpression operand)
    {
        Precedence result = new Precedence();
        result.precedence = Precedence.NONE;
        result.operand = operand;
        return result;
    }

    public static Precedence noPrecedence(ParsingExpression... seq)
    {
        return noPrecedence(sequence(seq));
    }

    public static OneMore oneMore(ParsingExpression operand)
    {
        OneMore result = new OneMore();
        result.operand = operand;
        return result;
    }

    public static OneMore oneMore(ParsingExpression... seq)
    {
        return oneMore(sequence(seq));
    }

    public static Optional optional(ParsingExpression operand)
    {
        Optional result = new Optional();
        result.operand = operand;
        return result;
    }

    public static Optional optional(ParsingExpression... seq)
    {
        return optional(sequence(seq));
    }

    public static Precedence precedence(int precedence, ParsingExpression operand)
    {
        Precedence result = new Precedence();
        result.precedence = precedence;
        result.operand = operand;
        return result;
    }

    public static Reference reference(String target)
    {
        Reference result = new Reference();
        result.target = target;
        return result;
    }

    public static Sequence sequence(ParsingExpression... operands)
    {
        Sequence result = new Sequence();
        result.operands = operands;
        return result;
    }

    public static Token token(ParsingExpression operand)
    {
        Token result = new Token();
        result.operand = operand;
        return result;
    }

    public static Token token(ParsingExpression... seq)
    {
        return token(sequence(seq));
    }

    public static Whitespace whitespace()
    {
        return new Whitespace();
    }

    public static ZeroMore zeroMore(ParsingExpression operand)
    {
        ZeroMore result = new ZeroMore();
        result.operand = operand;
        return result;
    }

    public static ZeroMore zeroMore(ParsingExpression... seq)
    {
        return zeroMore(sequence(seq));
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public static ParsingExpression notCharSet(String chars)
    {
        return sequence(not(charSet(chars)), any());
    }

    public static ParsingExpression until(ParsingExpression op1, ParsingExpression op2)
    {
        return sequence(zeroMore(not(op2), op1), op2);
    }

    public static ParsingExpression aloUntil(ParsingExpression op1, ParsingExpression op2)
    {
        return sequence(oneMore(not(op2), op1), op2);
    }

    public static ParsingExpression separated(ParsingExpression op, ParsingExpression sep)
    {
        return optional(op, zeroMore(sep, op));
    }

    public static ParsingExpression aloSeparated(ParsingExpression op, ParsingExpression sep)
    {
        return sequence(op, zeroMore(sep, op));
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public static ParsingExpression errorRecording$(String name, ParsingExpression pe)
    {
        pe.setFlags(Registry.PEF_ERROR_RECORDING);
        return pe;
    }

    public static ParsingExpression named$(String name, ParsingExpression pe)
    {
        pe.setName(name);
        return pe;
    }

    public static ParsingExpression recursive$(String name, ParsingExpression pe)
    {
        pe.setName(name);
        new IncrementalReferenceResolver(pe).walk(pe);
        return pe;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}

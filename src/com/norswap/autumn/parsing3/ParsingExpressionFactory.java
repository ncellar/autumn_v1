package com.norswap.autumn.parsing3;

import com.norswap.autumn.parsing.Operator;
import com.norswap.autumn.parsing.Precedence;

import static com.norswap.autumn.parsing.Operator.*;
import static com.norswap.autumn.parsing.ParsingExpressionFlags.*;

public final class ParsingExpressionFactory
{
    ////////////////////////////////////////////////////////////////////////////////////////////////
    
    private static ParsingExpression operands(Operator operator, ParsingExpression[] operands)
    {
        ParsingExpression result = new ParsingExpression(operator);
        result.setOperands(operands);
        return result;
    }

    private static ParsingExpression operand(Operator operator, ParsingExpression operand)
    {
        ParsingExpression result = new ParsingExpression(operator);
        result.setOperand(operand);
        return result;
    }

    private static ParsingExpression string(Operator operator, String string)
    {
        ParsingExpression result = new ParsingExpression(operator);
        result.setString(string);
        return result;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    // CREATE EXPRESSIONS

    public static ParsingExpression sequence(ParsingExpression... operands)
    {
        return operands(OP_SEQUENCE, operands);
    }

    public static ParsingExpression choice(ParsingExpression... operands)
    {
        return cuttable$(operands(OP_CHOICE, operands));
    }

    public static ParsingExpression lookahead(ParsingExpression operand)
    {
        return operand(OP_LOOKAHEAD, operand);
    }

    public static ParsingExpression not(ParsingExpression operand)
    {
        return operand(OP_NOT, operand);
    }

    public static ParsingExpression optional(ParsingExpression operand)
    {
        return cuttable$(operand(OP_OPTIONAL, operand));
    }

    public static ParsingExpression zeroMore(ParsingExpression operand)
    {
        return cuttable$(operand(OP_ZERO_MORE, operand));
    }

    public static ParsingExpression oneMore(ParsingExpression operand)
    {
        return cuttable$(operand(OP_ONE_MORE, operand));
    }

    public static ParsingExpression literal(String string)
    {
        return string(OP_LITERAL, string);
    }

    public static ParsingExpression charRange(char first, char last)
    {
        return string(OP_CHAR_RANGE, "" + first + last);
    }

    public static ParsingExpression charRange(String string)
    {
        if (string.length() != 2)
        {
            throw new RuntimeException(
                "charRange takes a string with the first and last characters only!");
        }

        return string(OP_CHAR_RANGE, string);
    }

    public static ParsingExpression charSet(String chars)
    {
        return string(OP_CHAR_SET, chars);
    }

    public static ParsingExpression any()
    {
        return new ParsingExpression(OP_ANY);
    }

    public static ParsingExpression longestMatch(ParsingExpression... operands)
    {
        return operands(OP_LONGEST_MATCH, operands);
    }

    public static ParsingExpression dumb(ParsingExpression operand)
    {
        return operand(OP_DUMB, operand);
    }

    public static ParsingExpression custom(CustomParseOperator function)
    {
        ParsingExpression result = new ParsingExpression(OP_CUSTOM);
        result.setCustom(function);
        return result;
    }

    public static ParsingExpression customDumb(CustomDumbParseOperator function)
    {
        ParsingExpression result = new ParsingExpression(OP_DUMB_CUSTOM);
        result.setCustom(function);
        return result;
    }

    public static ParsingExpression cut()
    {
        return new ParsingExpression(OP_CUT);
    }

    public static ParsingExpression ref(String name)
    {
        ParsingExpression result = new ParsingExpression(OP_REF);
        result.setString(name);
        return result;
    }

    public static ParsingExpression ref(ParsingExpression expr)
    {
        ParsingExpression result = new ParsingExpression(OP_REF);
        result.setOperand(expr);
        return result;
    }

    public static ParsingExpression whitespace()
    {
        return new ParsingExpression(OP_WHITESPACE);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    // CREATE EXPRESSIONS (SEQUENCE SYNTACTIC SUGAR)

    public static ParsingExpression lookahead(ParsingExpression... operands)
    {
        return lookahead(sequence(operands));
    }

    public static ParsingExpression not(ParsingExpression... operands)
    {
        return not(sequence(operands));
    }

    public static ParsingExpression optional(ParsingExpression... operands)
    {
        return optional(sequence(operands));
    }

    public static ParsingExpression zeroMore(ParsingExpression... operands)
    {
        return zeroMore(sequence(operands));
    }

    public static ParsingExpression oneMore(ParsingExpression... operands)
    {
        return oneMore(sequence(operands));
    }

    public static ParsingExpression dumb(ParsingExpression... operands)
    {
        return dumb(sequence(operands));
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    // CREATE EXPRESSIONS (FAKE OPERATORS)

    public static ParsingExpression notChars(String chars)
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

    // MUTATE EXPRESSIONS (indicated by $ suffix)

    public static ParsingExpression named$(String string, ParsingExpression pe)
    {
        pe.setName(string.intern());
        return pe;
    }

    public static ParsingExpression errorRecording$(ParsingExpression pe)
    {
        pe.setFlags(PEF_ERROR_RECORDING);
        return pe;
    }

    public static ParsingExpression memoized$(ParsingExpression pe)
    {
        pe.setFlags(PEF_MEMOIZE);
        return pe;
    }

    public static ParsingExpression cuttable$(ParsingExpression pe)
    {
        pe.setFlags(PEF_CUTTABLE);
        return pe;
    }

    public static ParsingExpression leftAssociative$(ParsingExpression pe)
    {
        pe.setFlags(PEF_LEFT_RECURSIVE | PEF_LEFT_ASSOCIATIVE);
        return pe;
    }

    public static ParsingExpression leftRecursive$(ParsingExpression pe)
    {
        pe.setFlags(PEF_LEFT_RECURSIVE);
        return pe;
    }

    public static ParsingExpression rightAssociative$(ParsingExpression pe)
    {
        // Not a mistake, right associativity is the default, we just need to allow left recursion.
        pe.setFlags(PEF_LEFT_RECURSIVE);
        return pe;
    }

    public static ParsingExpression leftRecursive$(String name, ParsingExpression pe)
    {
        pe.setFlags(PEF_LEFT_RECURSIVE);
        return recursive$(name, pe);
    }

    public static ParsingExpression delimited$(ParsingExpression pe)
    {
        pe.setPrecedence(Precedence.ESCAPE_PRECEDENCE);
        return pe;
    }

    public static ParsingExpression token$(ParsingExpression pe)
    {
        pe.setFlags(PEF_TOKEN);
        return pe;
    }

    public static ParsingExpression token(String literal)
    {
        return token$(literal(literal));
    }

    public static ParsingExpression token(ParsingExpression... operands)
    {
        return token$(sequence(operands));
    }

    public static ParsingExpression capture$(String captureName, ParsingExpression pe)
    {
        pe.setSingleCapture(captureName);
        return pe;
    }

    public static ParsingExpression captureText$(String captureName, ParsingExpression pe)
    {
        pe.setSingleCapture(captureName);
        pe.setFlags(PEF_TEXT_CAPTURE);
        return pe;
    }

    public static ParsingExpression captureMultiple$(String captureName, ParsingExpression pe)
    {
        pe.setMultipleCapture(captureName);
        return pe;
    }

    public static ParsingExpression captureTextMultiple$(String captureName, ParsingExpression pe)
    {
        pe.setMultipleCapture(captureName);
        pe.setFlags(PEF_TEXT_CAPTURE);
        return pe;
    }

    public static ParsingExpression withPrecedence$(int precedence, ParsingExpression pe)
    {
        pe.setPrecedence(precedence);
        return pe;
    }

    public static ParsingExpression recursive$(String name, ParsingExpression pe)
    {
        pe.setName(name);
        new RecursionResolver(pe).walk(pe);
        return pe;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    // CREATE & MUTATE REFERENCE TO EXPRESSION

    public static ParsingExpression capture(String captureName, ParsingExpression pe)
    {
        return capture$(captureName, ref(pe));
    }

    public static ParsingExpression captureText(String captureName, ParsingExpression pe)
    {
        return captureText$(captureName, ref(pe));
    }

    public static ParsingExpression captureMultiple(String captureName, ParsingExpression pe)
    {
        return captureMultiple$(captureName, ref(pe));
    }

    public static ParsingExpression captureTextMultiple(String captureName, ParsingExpression pe)
    {
        return captureTextMultiple$(captureName, ref(pe));
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}

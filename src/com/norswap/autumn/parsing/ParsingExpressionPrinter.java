package com.norswap.autumn.parsing;

import com.norswap.autumn.util.StringEscape;

/**
 * Static methods to produce string representations of parsing expressions that are similar to the
 * code from {@link ParsingExpressionFactory} that can be used to create them.
 */
public final class ParsingExpressionPrinter
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public static String string(ParsingExpression pe)
    {
        StringBuilder str = new StringBuilder();
        string(str, pe);
        return str.toString();
    }

    // ---------------------------------------------------------------------------------------------

    public static void string(StringBuilder str, ParsingExpression pe)
    {
        if (pe.isNamed())
        {
            str.append(pe.name());
            return;
        }

        switch(pe.operator())
        {
            case OP_SEQUENCE:
                operandsToString(str, pe, "sequence");
                break;

            case OP_CHOICE:
                operandsToString(str, pe, "choice");
                break;

            case OP_LOOKAHEAD:
                operandToString(str, pe, "lookahead");
                break;

            case OP_NOT:
                operandToString(str, pe, "not");
                break;

            case OP_OPTIONAL:
                operandToString(str, pe, "optional");
                break;

            case OP_ZERO_MORE:
                operandToString(str, pe, "zeroMore");
                break;

            case OP_ONE_MORE:
                operandToString(str, pe, "oneMore");
                break;

            case OP_LITERAL:
                stringToString(str, pe, "literal");
                break;

            case OP_CHAR_RANGE:
                stringToString(str, pe, "charRange");
                break;

            case OP_CHAR_SET:
                stringToString(str, pe, "charSet");
                break;

            case OP_ANY:
                str.append("any");
                break;

            case OP_LONGEST_MATCH:
                operandsToString(str, pe, "longestMatch");
                break;

            case OP_CUT:
                str.append("cut");
                break;

            case OP_DUMB:
                operandToString(str, pe, "dumb");
                break;

            case OP_CUSTOM:
                customToString(str, pe, "custom");
                break;

            case OP_DUMB_CUSTOM:
                customToString(str, pe, "customDumb");
                break;

            case OP_REF:
                operandToString(str, pe, "ref");
                break;

            case OP_BOGUS:
                str.append("bogus");
                break;
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public static void operandsToString(StringBuilder str, ParsingExpression pe, String prefix)
    {
        str.append(prefix);
        str.append("(");

        for (ParsingExpression op: pe.operands())
        {
            str.append(op);
            str.append(",");
        }

        str.deleteCharAt(str.length() - 1);
        str.append(")");
    }

    public static void operandToString(StringBuilder str, ParsingExpression pe, String prefix)
    {
        str.append(prefix);
        str.append("(");
        str.append(pe.operand());
        str.append(")");
    }

    public static void stringToString(StringBuilder str, ParsingExpression pe, String prefix)
    {
        str.append(prefix);
        str.append("(");
        str.append(StringEscape.escape(pe.string()));
        str.append(")");
    }

    public static void customToString(StringBuilder str, ParsingExpression pe, String prefix)
    {
        str.append(prefix);
        str.append("(");
        str.append(pe.custom());
        str.append(")");
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}

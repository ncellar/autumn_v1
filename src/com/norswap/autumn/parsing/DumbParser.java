package com.norswap.autumn.parsing;

public final class DumbParser
{
    /**
     * @see {@link Operator#OP_DUMB}
     */
    public static int parse(CharSequence text, ParsingExpression pe, int position)
    {
        int result = position;

        switch (pe.operator())
        {
            // ------------------------------------

            case OP_SEQUENCE:
            {
                for (ParsingExpression child : pe.operands())
                {
                    position = parse(text, child, position);

                    if (position == -1)
                    {
                        break;
                    }
                }

            } break;

            // ------------------------------------

            case OP_CHOICE:
                choice: {
                    for (ParsingExpression child : pe.operands())
                    {
                        result = parse(text, child, position);

                        if (result != - 1)
                        {
                            position = result;
                            break choice;
                        }
                    }

                    position = -1;

                } break;

                // ------------------------------------

            case OP_LONGEST_MATCH:
            {
                int farthestPosition = -1;

                for (ParsingExpression child : pe.operands())
                {
                    result = parse(text, child, position);

                    if (result > farthestPosition)
                    {
                        farthestPosition = result;
                    }
                }

                position = farthestPosition;
                break;
            }

            // ------------------------------------

            case OP_LOOKAHEAD:
            {
                result = parse(text, pe.operand(), result);

                if (result == -1)
                {
                    position = -1;
                }

                break;
            }

            // ------------------------------------

            case OP_NOT:
            {
                result = parse(text, pe.operand(), position);

                if (result != -1)
                {
                    position = -1;
                }

            } break;

            // ------------------------------------

            case OP_OPTIONAL:
            {
                result = parse(text, pe.operand(), position);

                if (result != -1)
                {
                    position = result;
                }

            } break;

            // ------------------------------------

            case OP_ZERO_MORE:
            {
                while ((result = parse(text, pe.operand(), position)) != -1)
                {
                    position = result;
                }

            } break;

            // ------------------------------------

            case OP_ONE_MORE:
            {
                position = parse(text, pe.operand(), position);

                if (position == -1)
                {
                    break;
                }

                while ((result = parse(text, pe.operand(), position)) != -1)
                {
                    position = result;
                }

            } break;

            // ------------------------------------

            case OP_LITERAL:
            {
                String string = pe.string();

                int index = 0;
                int len = string.length();

                while (index < len && text.charAt(result) == string.charAt(index))
                {
                    ++index;
                    ++result;
                }

                position = index != string.length()
                    ? -1
                    : result;

            } break;

            // ------------------------------------

            case OP_CHAR_RANGE:
            {
                char c = text.charAt(position);

                position = pe.rangeStart() <= c && c <= pe.rangeEnd()
                    ? position + 1
                    : -1;

            } break;
            // ------------------------------------

            case OP_CHAR_SET:
            {
                position = pe.string().indexOf(text.charAt(position)) != -1
                    ? position + 1
                    : -1;

            } break;

            // ------------------------------------

            case OP_ANY:
            {
                position = text.charAt(position) != 0
                    ? position + 1
                    : -1;

            } break;

            // ------------------------------------

            case OP_DUMB_CUSTOM:
            {
                position = pe.customDumbParseFunction().parse(pe, position);

            } break;

            // ------------------------------------

            case OP_REF:
            {
                position = parse(text, pe.operand(), position);

            } break;

            // ------------------------------------

            case OP_CUSTOM:
            case OP_CUT:
                throw new RuntimeException("Invalid operator in dumb mode: " + pe.operator());
        }

        return position;
    }
}

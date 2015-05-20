package com.norswap.autumn.parsing.expressions;

import com.norswap.autumn.parsing.ParseInput;
import com.norswap.autumn.parsing.ParseTree;
import com.norswap.autumn.parsing.Parser;
import com.norswap.autumn.parsing.ParsingExpression;

import static com.norswap.autumn.parsing.Registry.*; // PEF_*

public final class Capture extends ParsingExpression
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public String name;
    public ParsingExpression operand;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void parse(Parser parser, ParseInput input)
    {
        ParseTree oldTree = input.tree;
        ParseTree newTree = new ParseTree();
        newTree.name = name;
        int oldCount = input.treeChildrenCount;

        input.tree = newTree;
        input.treeChildrenCount = 0;

        operand.parse(parser, input);

        input.tree = oldTree;
        input.treeChildrenCount = oldCount;

        if (input.succeeded())
        {
            if (isCaptureGrouped())
            {
                input.tree.addGrouped(newTree);
            }
            else
            {
                input.tree.add(newTree);
            }

            if (shouldCaptureText())
            {
                int end = input.blackEnd;

                newTree.value = parser.text
                    .subSequence(input.start, end)
                    .toString();
            }
        }
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public void appendTo(StringBuilder builder)
    {
        builder.append("capture(");
        builder.append(name);
        builder.append(", ");
        operand.toString(builder);
        builder.append(")");
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public ParsingExpression[] children()
    {
        return new ParsingExpression[]{operand};
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public void setChild(int position, ParsingExpression expr)
    {
        operand = expr;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public boolean shouldCaptureText()
    {
        return (flags & PEF_CAPTURE_TEXT) != 0;
    }

    public boolean isCaptureGrouped()
    {
        return (flags & PEF_CAPTURE_GROUPED) != 0;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}

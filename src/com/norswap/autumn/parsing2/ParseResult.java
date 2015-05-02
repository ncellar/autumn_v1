package com.norswap.autumn.parsing2;

import com.norswap.autumn.parsing.ParsingExpression;
import com.norswap.autumn.util.Array;

public final class ParseResult
{
    ParsingExpression expression;
    String name;
    int position;
    ParseOutput output;
    Array<ParseResult> children;

    static ParseResult failure(ParsingExpression expression, int position)
    {
        ParseResult result = new ParseResult(expression, position);
        result.output = ParseOutput.failure();
        return result;
    }

    public static ParseResult container()
    {
        ParseResult result = new ParseResult();
        return result;
    }

    ParseResult(ParsingExpression expression, int position)
    {
        this.expression = expression;
        this.position = position;
    }

    private ParseResult()
    {
    }

    ParseOutput output()
    {
        return output;
    }

    public String name()
    {
        return name;
    }

    public ParsingExpression expression()
    {
        return expression;
    }

    public boolean isContainer()
    {
        return expression == null;
    }

    public int childrenCount()
    {
        return children.size();
    }

    public void add(ParseResult child)
    {
        if (children == null)
        {
            children = new Array<>();
        }

        ParsingExpression pe = child.expression();

        if (child.isContainer())
        {
            children.addAll(child.children);
        }
        else if (pe.requiresCapture())
        {
            if (pe.requiresMultipleCapture())
            {
                ParseResult container = get(pe.captureName());

                if (container == null)
                {
                    container = new ParseResult();
                    children.add(container);
                }

                container.children.add(child);
            }
            else
            {
                children.add(child);
            }
        }
    }

    public ParseResult get(String name)
    {
        if (children == null)
        {
            return null;
        }

        for (ParseResult child: children)
        {
            if (name.equals(child.name))
            {
                return child;
            }
        }

        return null;
    }

    void truncateChildren(int childrenCount)
    {
        children.truncate(childrenCount);
    }

    boolean failed()
    {
        return output.failed();
    }

    int endPosition()
    {
        return output.position;
    }

    int blackEndPosition()
    {
        return output.blackPosition;
    }
}

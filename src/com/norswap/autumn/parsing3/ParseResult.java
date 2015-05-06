package com.norswap.autumn.parsing3;

import com.norswap.autumn.util.Array;

public final class ParseResult
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public final ParsingExpression expression;
    public final int position;

    public String name;
    public ParseOutput output;
    public Array<ParseResult> children;
    public boolean grouped;
    public ParseResult next;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public static ParseResult container()
    {
        ParseResult result = new ParseResult(null, 0);
        return result;
    }

    public ParseResult(ParsingExpression expression, int position)
    {
        this.expression = expression;
        this.position = position;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public int endPosition()
    {
        return output.position;
    }

    int blackEndPosition()
    {
        return output.blackPosition;
    }

    boolean succeeded()
    {
        return output.succeeded();
    }

    boolean failed()
    {
        return output.failed();
    }

    public void finalize(ParseOutput output)
    {
        output.become(output);
    }

    public int childrenCount()
    {
        return children.size();
    }

    void truncateChildren(int childrenCount)
    {
        children.truncate(childrenCount);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public void add(ParseResult child)
    {
        if (children == null)
        {
            children = new Array<>();
        }

        if (name == null)
        {
            children.addAll(child.children);
        }
        else
        {
            if (child.grouped)
            {
                ParseResult container = get(child.name);

                if (container == null)
                {
                    container = container();
                    container.name = child.name;
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

    ////////////////////////////////////////////////////////////////////////////////////////////////
}

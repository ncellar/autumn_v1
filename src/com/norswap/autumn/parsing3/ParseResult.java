package com.norswap.autumn.parsing3;

import com.norswap.autumn.util.Array;

/**
 * - name != null && expression != null >> result of a capture
 * - name != null && expression == null >> grouped capture (also position == 0)
 * - name == null && expression != null >> root & seeds
 * - name == null && expression == null >> container (used to save results of sub-expressions)
 */
public final class ParseResult
{
    ////////////////////////////////////////////////////////////////////////////////////////////////
    
    public String name;
    public String value;
    public Array<ParseResult> children;
    public boolean grouped;
    public ParseResult next;

    public final ParsingExpression expression;
    public final int position;
    public ParseOutput output;

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

    public int blackEndPosition()
    {
        return output.blackPosition;
    }

    public boolean succeeded()
    {
        return output.succeeded();
    }

    public boolean failed()
    {
        return output.failed();
    }

    public void finalize(ParseOutput output)
    {
        this.output = new ParseOutput(output);
    }

    public int childrenCount()
    {
        return children == null
            ? 0
            : children.size();
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

        if (child.name == null)
        {
            if (child.children != null)
            {
                children.addAll(child.children);
            }
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
                    container.children = new Array<>();
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

    // ---------------------------------------------------------------------------------------------

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

    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();

        if (name != null)
        {
            builder.append(name);
            builder.append(": ");
        }

        if (children != null) // TODO && !children.isEmpty()
        {
            builder.append("[");

            for (ParseResult child: children)
            {
                builder.append(child);
                builder.append(", ");
            }

            if (children.size() > 0)
            {
                builder.setLength(builder.length() - 2);
            }

            builder.append("]");
        }
        else if (value != null)
        {
            builder.append(": \"");
            builder.append(value);
            builder.append("\"");
        }
        else if (name != null)
        {
            builder.setLength(builder.length() - 2);
        }

        return builder.toString();
    }

    // ---------------------------------------------------------------------------------------------

    public String toTreeString()
    {
        StringBuilder builder = new StringBuilder();
        toTreeString(builder, 0);
        return builder.toString();
    }

    // ---------------------------------------------------------------------------------------------

    public void toTreeString(StringBuilder builder, int depth)
    {
        builder.append(new String(new char[depth]).replace("\0", "-|"));
        builder.append(name != null ? name : "container");
        builder.append("\n");

        if (expression == null)
        {
            int i = 0;
            for (ParseResult child: children)
            {
                builder.append(new String(new char[depth + 1]).replace("\0", "-|"));
                builder.append(" ");
                builder.append(i);
                builder.append("\n");

                if (child.children != null)
                for (ParseResult grandChild: child.children)
                {
                    grandChild.toTreeString(builder, depth + 2);
                }

                ++i;
            }
        }
        else if (children != null)
        {
            for (ParseResult child: children)
            {
                child.toTreeString(builder, depth + 1);
            }
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}

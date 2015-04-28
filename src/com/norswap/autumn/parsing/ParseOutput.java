package com.norswap.autumn.parsing;

import com.norswap.autumn.util.Array;

import java.util.HashMap;
import java.util.Map;

public final class ParseOutput
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * This special value is used as the expression for parse outputs that are used to collect
     * outputs we do not want to add to the parent output object immediately.
     *
     * For instance, when using the longest match operator, we want to determine which alternative
     * is the longest before adding the outputs to the parent.
     */
    private static final ParsingExpression COLLECT = new ParsingExpression(Operator.OP_BOGUS);

    /**
     * This special value is used as the expression for parse outputs that are used to hold
     * the parse outputs captured by a multiple capture.
     *
     * Such a parse output is in essence a glorified array (only its children field interests us),
     * but wrapping the array inside a parse output makes the interface much more pleasant to
     * work with.
     */
    private static final ParsingExpression MULTIPLE = new ParsingExpression(Operator.OP_BOGUS);

    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Returns a failed parse output for the given expression at the given position.
     */
    public static ParseOutput fail(ParsingExpression pe, int position)
    {
        ParseOutput result = new ParseOutput(pe, position);
        result.endPosition = -1;
        return result;
    }

    /**
     * @see {@link #COLLECT}
     */
    public static ParseOutput collect(int position)
    {
        return new ParseOutput(COLLECT, position);
    }

    /**
     * @see {@link #MULTIPLE}
     */
    private static ParseOutput multiple(String captureName)
    {
        ParseOutput result = new ParseOutput(MULTIPLE, -1);
        result.endPosition = -1;
        result.children = new Array<>();
        result.captureName = captureName;
        return result;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private String captureName;

    private ParsingExpression expression;

    private int startPosition, endPosition, endBlackPosition;

    private Array<ParseOutput> children;

    private String value;

    public ParseOutput next;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public ParseOutput(ParsingExpression pe, int startPosition)
    {
        if (pe != null)
        {
            this.captureName = pe.captureName();
        }

        this.expression = pe;
        this.startPosition = startPosition;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public ParsingExpression expression()
    {
        return expression;
    }

    public int startPosition()
    {
        return startPosition;
    }

    public int endPosition()
    {
        return endPosition;
    }

    public int endBlackPosition()
    {
        return endBlackPosition;
    }

    public Positions endPositions()
    {
        return new Positions(endPosition, endBlackPosition);
    }

    public String value()
    {
        return value;
    }

    public boolean succeeded()
    {
        return endPosition != -1;
    }

    public boolean failed()
    {
        return endPosition == -1;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    void setEndPosition(int endPosition, int endBlackPosition, CharSequence text)
    {
        this.endPosition = endPosition;
        this.endBlackPosition = endBlackPosition;

        if (endPosition != -1 && expression.requiresTextCapture())
        {
            value = text.subSequence(startPosition, endBlackPosition).toString();
        }
    }

    // ---------------------------------------------------------------------------------------------

    void setCaptureName(String captureName)
    {
        this.captureName = captureName;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public int childrenSize()
    {
        return children == null
            ? 0
            : children.size();
    }

    // ---------------------------------------------------------------------------------------------

    public Array<ParseOutput> children()
    {
        return children;
    }

    // ---------------------------------------------------------------------------------------------

    public ParseOutput get(String name)
    {
        if (children == null)
        {
            return null;
        }

        for (int i = 0; i < children.size(); ++i)
        {
            ParseOutput child = children.get(i);

            if (name.equals(child.captureName))
            {
                return child;
            }
        }

        return null;
    }

    // ---------------------------------------------------------------------------------------------

    public ParseOutput getPath(String path)
    {
        String[] components = path.split("/");

        ParseOutput output = this;

        for (String component: components)
        {
           output = output.get(component);
        }

        return output;
    }

    // ---------------------------------------------------------------------------------------------

    public Map<String, Object> map()
    {
        if (children == null)
        {
            return new HashMap<>();
        }

        HashMap<String, Object> map = new HashMap<>();

        for (int i = 0; i < children.size(); ++i)
        {
            ParseOutput child = children.get(i);
            map.put(child.captureName, child);
        }

        return map;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    void truncateChildren(int size)
    {
        if (children != null)
        {
            children.truncate(size);
        }
    }

    // ---------------------------------------------------------------------------------------------

    void add(ParseOutput child)
    {
        if (children == null)
        {
            children = new Array<>();
        }

        ParsingExpression pe = child.expression();

        if (pe.requiresCapture())
        {
            if (pe.requiresMultipleCapture())
            {
                ParseOutput container = get(pe.captureName());

                if (container == null)
                {
                    container = multiple(pe.captureName());
                    children.add(container);
                }

                container.children.add(child);
            }
            else
            {
                children.add(child);
            }
        }
        else if (child.children != null)
        {
            children.addAll(child.children);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public String toString()
    {
        return toString(-1);
    }

    public String toString(int maxDepth)
    {
        StringBuilder str = new StringBuilder();
        toString(str, 0, maxDepth, -1);
        return str.toString();
    }

    private void toString(StringBuilder str, int depth, int maxDepth, int index)
    {
        str.append(new String(new char[depth]).replace("\0", "-|"));
        str.append(" ");

        str.append(index < 0 ? captureName : index);
        str.append(expression == null ? "[]" : "");
        str.append(value != null ? ("(" + value + ")") : "");
        str.append("\n");

        if (maxDepth == -1 || depth < maxDepth)
        {
            for (int i = 0; i < childrenSize(); ++i)
            {
                children.get(i).toString(str, depth + 1, maxDepth,
                   expression == null ? i : -1);
            }
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}

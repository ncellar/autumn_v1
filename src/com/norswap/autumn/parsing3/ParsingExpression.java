package com.norswap.autumn.parsing3;

import com.norswap.autumn.util.HandleMap;

/**
 * A parsing expression is matched to the source text by recursively invoking the {@link #parse}
 * method of its sub-expressions on the source text; in a manner defined by parsing expression
 * flavour.
 *
 * {@link #parse} takes two parameters: the parser itself which supplies global context and some
 * parse input. In particular the parse input includes the position in the source text at which
 * to attempt the match.
 */
public abstract class ParsingExpression
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public int flags;
    public HandleMap ext = new HandleMap();

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public abstract void parse(Parser parser, ParseInput input);

    // ---------------------------------------------------------------------------------------------

    public int parseDumb(CharSequence text, int position)
    {
        throw new UnsupportedOperationException(
            "Parsing expression class "
            + this.getClass().getSimpleName()
            + " doesn't support dumb parsing.");
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        toString(builder);
        return builder.toString();
    }

    // ---------------------------------------------------------------------------------------------

    public void toString(StringBuilder builder)
    {
        String name = name();

        if (name != null)
        {
            builder.append(name);
        }
        else
        {
            appendTo(builder);
        }
    }

    // ---------------------------------------------------------------------------------------------

    public abstract void appendTo(StringBuilder builder);

    // ---------------------------------------------------------------------------------------------

    public ParsingExpression[] children()
    {
        return new ParsingExpression[0];
    }

    // ---------------------------------------------------------------------------------------------

    public void setChild(int position, ParsingExpression expr)
    {
        throw new UnsupportedOperationException(
            "Parsing expression class "
            + this.getClass().getSimpleName()
            + " doesn't have children or doesn't support setting them.");
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public String name()
    {
        return ext.get(Registry.PEH_NAME);
    }

    // ---------------------------------------------------------------------------------------------

    public void setName(String name)
    {
        ext.set(Registry.PEH_NAME, name);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Generic Flag Manipulation Functions
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public boolean hasAnyFlagsSet(int flagsToCheck)
    {
        return (flags & flagsToCheck) != 0;
    }

    public boolean hasFlagsSet(int flagsToCheck)
    {
        return (flags & flagsToCheck) == flagsToCheck ;
    }

    public void setFlags(int flagsToAdd)
    {
        flags |= flagsToAdd;
    }

    public void clearFlags(int flagsToClear)
    {
        flags &= ~flagsToClear;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}

package com.norswap.autumn;

import com.norswap.autumn.graph.Copier;
import com.norswap.autumn.graph.Nullability;
import com.norswap.autumn.graph.Printer;
import com.norswap.autumn.state.ParseState;
import com.norswap.util.DeepCopy;
import static java.lang.String.format;
import java.util.function.Predicate;

/**
 * A parsing expression can be invoked on the input text (using the {@link #parse} method). It can
 * result in success (some input text was matched) or failure, and can additional modify the parse
 * state (see {@link ParseState}).
 * <p>
 * Parsing expression often have sub-expressions, which they invoke recursively.
 */
public abstract class ParsingExpression implements DeepCopy
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public String name;

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // PARSING

    // ---------------------------------------------------------------------------------------------

    public abstract void parse(Parser parser, ParseState state);

    // ---------------------------------------------------------------------------------------------

    public int parseDumb(Parser parser, int position)
    {
        throw new UnsupportedOperationException(
            "Parsing expression [" + this + "] doesn't support dumb parsing.");
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // GRAPH WALKING

    // ---------------------------------------------------------------------------------------------

    /**
     * Returns the sub-expressions of this parsing expression. The parsing expression can invoke
     * other parsing expression than those, but these will not be affected by grammar
     * transformations (including reference resolution and left-recursion elimination). The order
     * must match that used by {@link #setChild}.
     */
    public ParsingExpression[] children()
    {
        return new ParsingExpression[0];
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Sets the sub-expression at the given position. Used by grammar transformations.
     */
    public void setChild(int position, ParsingExpression pe)
    {
        throw new UnsupportedOperationException(
            "Parsing expression class "
            + this.getClass().getSimpleName()
            + " doesn't have children or doesn't support setting them.");
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // PROPERTIES

    public Nullability nullability()
    {
        return Nullability.no(this);
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Returns the subset of the sub-expressions that can be invoked at the same position than this
     * expression.
     */
    public ParsingExpression[] firsts(Predicate<ParsingExpression> nullability)
    {
        return new ParsingExpression[0];
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // COPYING

    /**
     * Parsing expression must use this method to perform a deep copy of their own data in place.
     * They must not copy their children. They must, however, copy any wrapper around their children
     * (e.g. an array), or the copy will overwrite the original parsing expression as well!
     * <p>
     * This is used by {@link #deepCopy()} and called right after a parsing expression was cloned.
     */
    public void copyOwnData() {}

    // ---------------------------------------------------------------------------------------------

    /**
     * Do not use; use {@link #deepCopy()} instead.
     */
    @Override
    public final ParsingExpression clone()
    {
        try {
            return (ParsingExpression) super.clone();
        }
        catch (CloneNotSupportedException e)
        {
            throw new Error(e); // shouldn't happen
        }
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Performs a deep copy of the parsing expression. This is aware of recursion, and parsing
     * expression identity: it will never make two copies of the same descendant of this parsing
     * expression.
     */
    @Override
    public final ParsingExpression deepCopy()
    {
        return new Copier().visit(this);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // STRING REPRESENTATION

    // ---------------------------------------------------------------------------------------------

    /**
     * Returns a string containing information about the expression that isn't contained in its
     * type or its children.
     */
    public String ownDataString()
    {
        return "";
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Returns a string containing information about a child expression that isn't contained the
     * child expression itself.
     */
    public String childDataString(int position)
    {
        return "";
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * @see {@link #toStringOneLine}
     */
    @Override
    public String toString()
    {
        return toStringShort();
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Returns a string containing the hashcode of the parsing expression (based on pointer
     * identity), as well as the name of the expression, if it has one.
     */
    public final String nameOrHashcode()
    {
        return name != null
            ? name + " - " + format("%X", hashCode())
            : format("%X", hashCode());
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Returns a string containing the simple (unqualified) name of the class of the expression, as
     * well as the contents of {@link #nameOrHashcode} and {@link #ownDataString}.
     */
    public final String toStringOneLine()
    {
        String data = ownDataString();
        return format("%s (%s)%s",
            getClass().getSimpleName(),
            nameOrHashcode(),
            data.isEmpty() ? "" : format(" [%s]", data));
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Returns a string containing a short representation of this expression's structure.
     * <p>
     * If it has the name, use this name; otherwise output its structure cutting off the recursion
     * as soon as named nodes are encountered.
     */
    public final String toStringShort()
    {
        return toString(true, true);
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Returns a string containing the structure of the expression, cutting off the recursion as
     * soon as named nodes are encountered. If the expression itself is named, its structure will be
     * shown regardless.
     */
    public final String toStringUnroll()
    {
        return toString(true, false);
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Returns a string containing expression in full, with all its descendants. Recursion is marked
     * as such. No expression is outputted more than once, references like
     * "visited(EXPRESSION_NAME)" are used after the first time.
     */
    public final String toStringFull()
    {
        return toString(false, false);
    }

    // ---------------------------------------------------------------------------------------------

    private String toString(boolean cutoffAtNames, boolean cutoffAtOwnName)
    {
        StringBuilder builder = new StringBuilder();
        new Printer(builder::append, cutoffAtNames, cutoffAtOwnName).visit(this);
        // delete trailing newline
        builder.deleteCharAt(builder.length() - 1);
        return builder.toString();
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Indicate whether the parsing expression node should be visible when printing a parsing
     * expression graph. An invisible expression will have its children appear as children of
     * its own parents.
     */
    public boolean isPrintable()
    {
        return true;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}

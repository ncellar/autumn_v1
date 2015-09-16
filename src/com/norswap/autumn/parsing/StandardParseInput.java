package com.norswap.autumn.parsing;

import com.norswap.util.Array;

/**
 * Holds the parse inputs required by Autumn's core. These inputs determine what will be parsed by a
 * parsing expression.
 * <p>
 * Note that this does not (currently) implement {@link ParseInput} since it will always be handled
 * sepately. However the same distinction between type 1 and type 2 fields applies (see {@link
 * ParseInput}).
 */
public class StandardParseInput
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * (TYPE 1) Position where the parsing expression will be invoked.
     */
    public int start;

    /**
     * (TYPE 3) The last non-whitespace input position preceding start.
     * <p>
     * We keep this particular tidbit of information for a single purpose: restoring {@link
     * ParseState#blackEnd} (the last non-whitespace input position preceding {@link
     * ParseState#end}) with {@link ParseState#resetOutput()}.
     * <p>
     * As such, this does not influence the parse.
     */
    public int blackStart;

    /**
     * (TYPE 1) The current precedence level for {@link com.norswap.autumn.parsing.expressions.Precedence}
     * expressions.
     */
    public int precedence;

    /**
     * (TYPE 1) Holds a set of flags that can serve as additional parse input. Can be set both by
     * Autumn and the user, who can register his own flags with {@link
     * Registry#ParseStateFlagsFactory}.
     */
    public int flags;

    /**
     * (TYPE 1) Holds the seeds for all the expression clusters in the parsing expression stack (all
     * the parsing expressions whose invocation is ongoing).
     */
    public Array<Seed> seeds;

    /**
     * (TYPE 2) The accessor to bestow upon newly captures.
     */
    public String accessor;

    /**
     * (TYPE 2) The tags to bestow upon newly created captures.
     */
    public Array<String> tags;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    StandardParseInput() {}

    // ---------------------------------------------------------------------------------------------

    StandardParseInput(StandardParseInput other)
    {
        copy(other);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Sets the fields of this object to a (deep) copy of that of the passed one.
     */
    public void copy(StandardParseInput other)
    {
        this.start = other.start;
        this.blackStart = other.blackStart;
        this.precedence = other.precedence;
        this.flags = other.flags;
        this.seeds = other.seeds.clone();
        this.accessor = other.accessor;
        this.tags = other.tags.clone();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Returns a hash code factoring in only this input's type 1 fields.
     */
    public int inputHashCode()
    {
        int result = start;
        result = 31 * result + precedence;
        result = 31 * result + flags;
        result = 31 * result + (seeds == null ? 0 : seeds.hashCode());
        return result;
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Indicate whether this input is equivalent to another, factoring in only this input's type 1
     * fields.
     */
    public boolean inputEquals(StandardParseInput o)
    {
        return start == o.start
            && precedence == o.precedence
            && flags == o.flags
            && (seeds == o.seeds || seeds != null && o.seeds != null && seeds.equals(o.seeds));
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}

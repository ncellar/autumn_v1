package com.norswap.autumn.parsing;

import com.norswap.util.Array;

/**
 * Holds the parse inputs required by Autumn's core. These inputs determine what will be parsed by a
 * parsing expression.
 * <p>
 * Note that this does not (currently) implement {@link ParseInput} since it will always be handled
 * separately. However the same distinction between modifier and other fields applies (see {@link
 * ParseInput}).
 */
public class StandardParseInput
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * (MODIFIER) Position where the parsing expression will be invoked.
     */
    public int start;

    /**
     * The last non-whitespace input position preceding {@link #start}.
     * <p>
     * We keep this particular tidbit of information for a single purpose: restoring {@link
     * ParseState#blackEnd} (the last non-whitespace input position preceding {@link
     * ParseState#end}) with {@link ParseState#discard()}.
     * <p>
     * As such, this does not influence the parse.
     */
    public int blackStart;

    /**
     * (MODIFIER) The current precedence level for {@link com.norswap.autumn.parsing.expressions.Precedence}
     * expressions.
     */
    public int precedence;

    /**
     * (MODIFIER) Holds a set of flags that can serve as additional parse input. Can be set both by
     * Autumn and the user, who can register his own flags with {@link
     * Registry#ParseStateFlagsFactory}.
     */
    public int flags;

    /**
     * (MODIFIER) Holds the seeds for all the expression clusters in the parsing expression stack (all
     * the parsing expressions whose invocation is ongoing).
     */
    public Array<Seed> seeds;

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
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Returns a hash code factoring in only this input's modifier fields.
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
     * Indicate whether this input is equivalent to another, factoring in only this input's modifier
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

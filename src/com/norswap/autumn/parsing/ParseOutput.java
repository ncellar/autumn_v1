package com.norswap.autumn.parsing;

/**
 * The interface to be implemented by user-defined classes to be used as additional output to the
 * parse.
 * <p>
 * The various methods in this interface are fairly lax in their requirements and are more lifecycle
 * callbacks called by corresponding methods from {@link ParseState}.
 * <p>
 * Parsing expressions are responsible for the manipulation of parse inputs: modifying them, and
 * adding/removing them from a {@link ParseState}. Many strategies are possible, including
 * repurposing the same ParseOutput object to be used many times.
 *
 * TODO more
 */
public interface ParseOutput
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Reset this parse output to its state before the parsing expression invocation.
     * <p>
     * Called by {@link ParseState#resetOutput}.
     */
    void reset(ParseState state);

    // ---------------------------------------------------------------------------------------------

    /**
     * Assimilate these outputs, making them non-resettable.
     * <p>
     * Called by {@link ParseState#advance}.
     */
    void advance(ParseState state);

    // ---------------------------------------------------------------------------------------------

    /**
     * Merge the output from the child with this output.
     * <p>
     * Called by {@link ParseState#merge}.
     */
    void merge(ParseState parent, ParseState child);

    // ---------------------------------------------------------------------------------------------

    /**
     * Return an object that represents the changes to this output compared to before the parsing
     * expression invocation. This object may later serve as parameter to {@link #merge}.
     */
    Object changes();

    // ---------------------------------------------------------------------------------------------

    /**
     * Merge in changes to this output (obtained by an earlier call to {@link #changes}).
     */
    void merge(Object changes);

    ////////////////////////////////////////////////////////////////////////////////////////////////
}

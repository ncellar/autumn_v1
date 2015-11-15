package com.norswap.autumn.parsing.state.patterns;

import com.norswap.autumn.parsing.state.CustomState;
import com.norswap.autumn.parsing.state.ParseState;

/**
 * This class implements a fundamental parse state pattern: the pure output. This is a piece of
 * state which is set by a parsing expression with the intent for it to be consumed by one of its
 * parent.
 * <p>
 * The value can be overwritten by other expressions, but if the expression is part of a failing
 * branch, the last succeeding value will ultimately prevail.
 */
public class Output<T> implements CustomState
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private T contentUncommitted;
    private T contentCommitted;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public void set(T content)
    {
        this.contentUncommitted = content;
    }

    // ---------------------------------------------------------------------------------------------

    public T get()
    {
        return contentUncommitted;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public Object snapshot(ParseState state)
    {
        return contentCommitted;
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    @SuppressWarnings("unchecked")
    public void restore(Object snapshot, ParseState state)
    {
        contentCommitted = (T) snapshot;
        contentUncommitted = contentCommitted;
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    @SuppressWarnings("unchecked")
    public void uncommit(Object snapshot, ParseState state)
    {
        contentCommitted = (T) snapshot;
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public void discard(ParseState state)
    {
        contentUncommitted = contentCommitted;
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public void commit(ParseState state)
    {
        contentCommitted = contentUncommitted;
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public Object extract(ParseState state)
    {
        return contentCommitted != contentUncommitted
            ? contentUncommitted
            : null;
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    @SuppressWarnings("unchecked")
    public void merge(Object changes, ParseState state)
    {
        if (changes != null) {
            contentUncommitted = (T) changes;
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}

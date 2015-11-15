package com.norswap.autumn.parsing.state.patterns;

import com.norswap.autumn.parsing.state.CustomChanges;
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
    public Container<T> snapshot(ParseState state)
    {
        return new Container<>(contentCommitted);
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    @SuppressWarnings("unchecked")
    public void restore(Snapshot snapshot, ParseState state)
    {
        contentCommitted = ((Container<T>) snapshot).content;
        contentUncommitted = contentCommitted;
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    @SuppressWarnings("unchecked")
    public void uncommit(Snapshot snapshot, ParseState state)
    {
        contentCommitted = ((Container<T>) snapshot).content;
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
    public Container<T> extract(ParseState state)
    {
        return contentCommitted != contentUncommitted
            ? new Container<>(contentUncommitted)
            : null;
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    @SuppressWarnings("unchecked")
    public void merge(CustomChanges changes, ParseState state)
    {
        if (changes != null) {
            contentUncommitted = ((Container<T>)changes).content;
        }
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public Container<T> result(ParseState state)
    {
        return new Container<>(contentUncommitted);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}

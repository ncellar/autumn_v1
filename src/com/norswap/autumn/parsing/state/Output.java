package com.norswap.autumn.parsing.state;

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
    public void load(Inputs inputs) {}

    // ---------------------------------------------------------------------------------------------

    @Override
    public void commit(ParseState state)
    {
        contentCommitted = contentUncommitted;
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public void discard(ParseState state)
    {
        contentUncommitted = contentCommitted;
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public CustomChanges extract(ParseState state)
    {
        return new Changes<>(contentUncommitted);
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    @SuppressWarnings("unchecked")
    public void merge(CustomChanges changes, ParseState state)
    {
        contentUncommitted = ((Changes<T>)changes).content;
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public Snapshot snapshot(ParseState state)
    {
        return new Snapshot(contentCommitted);
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    @SuppressWarnings("unchecked")
    public void restore(CustomState.Snapshot snapshot, ParseState state)
    {
        contentCommitted = ((Snapshot<T>) snapshot).content;
        contentUncommitted = contentCommitted;
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    @SuppressWarnings("unchecked")
    public void uncommit(CustomState.Snapshot snapshot, ParseState state)
    {
        contentCommitted = ((Snapshot<T>) snapshot).content;
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public Inputs inputs(ParseState state)
    {
        return null;
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public Result result(ParseState state)
    {
        return new Result(contentUncommitted);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public static class Changes<T> implements CustomChanges
    {
        T content;
        Changes(T content) { this.content = content; }

        @Override
        public Result result(Inputs inputs)
        {
            return new Result(content);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public static class Result<T> implements CustomState.Result
    {
        T content;
        Result(T content) { this.content = content; }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public static class Snapshot<T> implements CustomState.Snapshot
    {
        T content;
        Snapshot(T content) { this.content = content; }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}

package com.norswap.autumn.test.languages.python;

import com.norswap.autumn.parsing.state.ParseState;
import com.norswap.autumn.parsing.state.patterns.Duplex;

public final class PythonState extends Duplex<PythonState>
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public int indent, oldIndent, lineJoining = 1, newLinePos;
    boolean newLineEmmitted;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public PythonState()
    {
        super(new PythonState(null));
    }

    private PythonState(PythonState NULL)
    {
        super(NULL);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected PythonState copy()
    {
        PythonState out = new PythonState(null);
        out.copy(this);
        return out;
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    protected void copy(PythonState other)
    {
        indent          = other.indent;
        oldIndent       = other.indent;
        lineJoining     = other.lineJoining;
        newLineEmmitted = other.newLineEmmitted;
        newLinePos      = other.newLinePos;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public Object inputs(ParseState state)
    {
        return this;
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public Object extract(ParseState state)
    {
        return copy();
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public void merge(Object changes, ParseState state)
    {
        copy((PythonState) changes);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public boolean equals(Object o)
    {
        PythonState other;
        return this == o
            || o instanceof PythonState
            && (other = (PythonState) o) != null
            && indent           == other.indent
            && oldIndent        == other.oldIndent
            && lineJoining      == other.lineJoining
            && newLinePos       == other.newLinePos
            && newLineEmmitted  == other.newLineEmmitted;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public int hashCode()
    {
        int result = indent;
        result = 31 * result + oldIndent;
        result = 31 * result + lineJoining;
        result = 31 * result + newLinePos;
        result = 31 * result + (newLineEmmitted ? 1 : 0);
        return result;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}

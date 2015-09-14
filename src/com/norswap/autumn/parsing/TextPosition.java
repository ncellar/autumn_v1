package com.norswap.autumn.parsing;

/**
 * Denotes a position in a file by both its character position in the file and a (line, column)
 * pair.
 * <p>
 * Line numbers start at 0, while columns starts are determined by the {@link Source} object
 * that created this text position.
 */
public final class TextPosition
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public final int position;
    public final int line;
    public final int column;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public TextPosition(int position, int line, int column)
    {
        this.position = position;
        this.line = line;
        this.column = column;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public String toString()
    {
        return "line " + line + ", column " + column;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}

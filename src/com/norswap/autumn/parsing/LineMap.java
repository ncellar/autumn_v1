package com.norswap.autumn.parsing;

import java.util.Arrays;

/**
 * Maps line numbers to their file position and allows the reverse mapping in
 * O(log(number of lines)).
 *
 * Following the tradition, lines start at 1, while file positions and columns start at 0.
 */
public final class LineMap
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private int[] linePositions;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Return a position for the given offset, assuming that columns start at 0.
     */
    public TextPosition positionFromOffset(int offset)
    {
        return positionFromOffset(offset, 0);
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Return a position for the given offset, assuming that columns start at 0.
     */
    public TextPosition positionFromOffset(int offset, int lineStart)
    {
        int line = Arrays.binarySearch(linePositions, offset);

        if (line >= 0)
        {
            return new TextPosition(offset, line, 0);
        }
        else
        {
            line = -line - 2;

            return new TextPosition(offset, line, offset - linePositions[line] + lineStart);
        }
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Return a position for the (line, column) pair, assuming that columns start at 0.
     */
    public TextPosition position(int line, int column)
    {
        return new TextPosition(offset(line, column), line, column);
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Return a position for the (line, column) pair, assuming that columns start at {@code
     * lineStart}.
     */
    public TextPosition position(int line, int column, int lineStart)
    {
        return new TextPosition(offset(line, column - lineStart), line, column);
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Returns the file offset for the (line, column) pair, assuming that columns start at 0.
     */
    public int offset(int line, int column)
    {
        return linePositions[line] + column;
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Returns the file offset for the (line, column) pair, assuming that columns start at {@code
     * lineStart}.
     */
    public int offset(int line, int column, int lineStart)
    {
        return linePositions[line] + column - lineStart;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public LineMap(CharSequence seq)
    {
        linePositions = new int[128];
        linePositions[0] = -1;
        linePositions[1] = 0;
        int next = 2;

        for (int i = 0; i < seq.length(); ++i)
        {
            char c = seq.charAt(i);

            if (c == '\n')
            {
                if (next == linePositions.length)
                {
                    linePositions = Arrays.copyOf(linePositions, linePositions.length * 2);
                }

                linePositions[next++] = i + 1;
            }
        }

        linePositions = Arrays.copyOf(linePositions, next);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}

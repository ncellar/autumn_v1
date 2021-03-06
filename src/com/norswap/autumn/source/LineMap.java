package com.norswap.autumn.source;

import com.norswap.util.Array;

import java.util.Arrays;

/**
 * Maps line numbers to their file offsets and allows the reverse mapping in O(log(number of
 * lines)).
 * <p>
 * Lines start at index 1, while columns start at 0.
 */
public final class LineMap
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private int[] linePositions;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public LineMap(CharSequence seq)
    {
        Array<Integer> positions = new Array<>(-1, 0);

        for (int i = 0; i < seq.length(); ++i)
        {
            if (seq.charAt(i) == '\n') {
                positions.add(i + 1);
            }
        }

        linePositions = positions.stream().mapToInt(x -> x).toArray();
    }
    
    ////////////////////////////////////////////////////////////////////////////////////////////////

    int lineFromOffset(int offset)
    {
        int line = Arrays.binarySearch(linePositions, offset);

        return line >= 0
            ? line
            : -line - 2;
    }

    // ---------------------------------------------------------------------------------------------

    int linePosition(int line)
    {
        return linePositions[line];
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}

package com.norswap.autumn.parsing;

/**
 * Used to save and restore a position and the last non-whitespace (black) position.
 */
public class Positions
{
    public int position;
    public int blackPosition;

    public Positions(int position, int blackPosition)
    {
        this.position = position;
        this.blackPosition = blackPosition;
    }

    public String toString()
    {
        return "(" + position + ", " + blackPosition + ")";
    }
}

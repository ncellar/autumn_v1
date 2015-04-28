package com.norswap.autumn.parsing;

public class Precedence
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public static final int NO_PRECEDENCE = 0;

    public static final int ESCAPE_PRECEDENCE = -1;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public static boolean isLower(int a, int b)
    {
        return a > 0 && a < b;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}

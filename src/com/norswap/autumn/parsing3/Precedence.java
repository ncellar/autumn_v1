package com.norswap.autumn.parsing3;

public final class Precedence
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public static final int NO_PRECEDENCE = 0;

    public static final int ESCAPE_PRECEDENCE = -1;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public static boolean isLower(int a, int b)
    {
        return a > 0 && a < b;
    }

    public static int get(ParsingExpression pe, int currentLevel)
    {
        int precedence = pe.precedence();

        if (precedence == NO_PRECEDENCE)
        {
            precedence = currentLevel;
        }
        else if (precedence == ESCAPE_PRECEDENCE)
        {
            precedence = NO_PRECEDENCE;
        }

        return precedence;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}

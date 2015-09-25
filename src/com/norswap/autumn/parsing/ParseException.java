package com.norswap.autumn.parsing;

/**
 * TODO
 */
public final class ParseException extends RuntimeException
{
    public final ErrorReport error;

    public ParseException(ErrorReport error)
    {
        this.error = error;
    }

    @Override
    public String getMessage()
    {
        return error.message();
    }
}

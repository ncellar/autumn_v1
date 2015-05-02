package com.norswap.autumn.parsing3;

public final class ParserConfiguration
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * The configuration a Parser will use by default if a configuration is not supplied explicitly.
     */
    public static final ParserConfiguration DEFAULT = new ParserConfiguration();

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public ErrorHandler errorHandler = new DefaultErrorHandler();

    public ParsingExpression whitespace = Whitespace.whitespace;

    public MemoizationStrategy memoizationStrategy = new DefaultMemoizationStrategy();

    public Tracer tracer;

    public boolean debug = false;

    public boolean processLeadingWhitespace = true;

    public int debugCountThreshold = 1000000;

    ////////////////////////////////////////////////////////////////////////////////////////////////
}

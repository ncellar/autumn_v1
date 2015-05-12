package com.norswap.autumn.parsing;

public final class ParserConfiguration
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * The configuration a Parser will use by default if a configuration is not set explicitly.
     */
    public static final ParserConfiguration DEFAULT = new ParserConfiguration();

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public ErrorHandler errorHandler = new DefaultErrorHandler();

    public ParsingExpression whitespace = Whitespace.whitespace;

    public MemoizationStrategy memoizationStrategy = new DefaultMemoizationStrategy();

    public boolean processLeadingWhitespace = true;

    ////////////////////////////////////////////////////////////////////////////////////////////////
}

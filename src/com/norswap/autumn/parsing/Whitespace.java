package com.norswap.autumn.parsing;

import static com.norswap.autumn.parsing.ParsingExpressionFactory.*;

/**
 * This class exposes the default whitespace expression ({@link #whitespace}) as well as a few of
 * its sub-expressions that can be useful when building custom whitespace expressions.
 */
public class Whitespace
{
    public static final ParsingExpression lineComment =
        named$("lineComment", sequence(
            literal("//"),
            zeroMore(
                not(literal("\n")),
                any())));

    public static final ParsingExpression blockComment =
        recursive$("blockComment", sequence(
            literal("/*"),
            zeroMore(choice(
                reference("blockComment"),
                sequence(
                    not(literal("*/")),
                    any()))),
            literal("*/")));

    public static final ParsingExpression whitespaceChars =
        named$("whitespaceChars", charSet("  \n\t"));

    public static final ParsingExpression whitespace =
        zeroMore(choice(
            whitespaceChars,
            lineComment,
            blockComment));
}

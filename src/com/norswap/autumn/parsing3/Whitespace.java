package com.norswap.autumn.parsing3;

import static com.norswap.autumn.parsing3.ParsingExpressionFactory.*;

public class Whitespace
{
    public static final ParsingExpression lineComment =
        named$("lineComment", sequence(
            literal("//"),
            zeroMore(sequence(
                not(literal("\n")),
                any()))));

    public static final ParsingExpression blockComment =
        recursive$("blockComment", sequence(
            literal("/*"),
            zeroMore(choice(
                ref("blockComment"),
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

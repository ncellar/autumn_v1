package com.norswap.autumn.test.parsing;

import com.norswap.autumn.parsing.ParsingExpression;

/**
 * A grammar for the Java language.
 */
public final class OldJavaGrammar
{
//    ////////////////////////////////////////////////////////////////////////////////////////////////
//
//    private static ParsingExpression lit(String lit)
//    {
//        return token(lit);
//    }
//
//    private static ParsingExpression lit(String lit, String noFollow)
//    {
//        return token$(sequence(literal(lit), not(charSet(noFollow))));
//    }
//
//    private static ParsingExpression keyword(String keyword)
//    {
//        return token$(sequence(literal(keyword), not(letterOrDigit)));
//    }
//
//    ////////////////////////////////////////////////////////////////////////////////////////////////
//
//    public static ParsingExpression
//
//    zeroNine = charSet("09"),
//
//    letter = choice(
//        charRange("az"),
//        charRange("AZ"),
//        charSet("_$")),
//
//    letterOrDigit = choice(letter, zeroNine)
//
//    ;
//
//    ////////////////////////////////////////////////////////////////////////////////////////////////
//
//    //-------------------------------------------------------------------------
//    //  JLS 3.11-12  Separators, Operators
//    //-------------------------------------------------------------------------
//
//    public static ParsingExpression
//    at          = lit("@"),
//    and         = lit("&", "&="),
//    andAnd      = lit("&&"),
//    andEq       = lit("&="),
//    bang        = lit("!", "="),
//    bsr         = lit(">>>", "="),
//    bsrEq       = lit(">>>="),
//    colon       = lit(":"),
//    comma       = lit(","),
//    dec         = lit("--"),
//    div         = lit("/", "="),
//    divEq       = lit("/="),
//    dot         = lit("."),
//    ellipsis    = lit("..."),
//    eq          = lit("=", "="),
//    eqEq        = lit("=="),
//    ge          = lit(">="),
//    gt          = lit(">", ">="),
//    hat         = lit("^", "="),
//    hatEq       = lit("^="),
//    inc         = lit("++"),
//    lSqBrk      = lit("["),
//    le          = lit("<="),
//    lPar        = lit("("),
//    lAnBrk      = lit("<"), // uh?
//    lt          = lit("<", "<="),
//    lBrace      = lit("{"),
//    minus       = lit("-", "-="),
//    minusEq     = lit("-="),
//    mod         = lit("%", "="),
//    modEq       = lit("%="),
//    notEq       = lit("!="),
//    or          = lit("|", "|="),
//    orEq        = lit("|="),
//    orOr        = lit("||"),
//    plus        = lit("+", "+="),
//    plusEq      = lit("+="),
//    query       = lit("?"),
//    rSqBrk      = lit("]"),
//    rPar        = lit(")"),
//    rAnBrk      = lit(">"), // uh?
//    rBrace      = lit("}"),
//    semi        = lit(";"),
//    sl          = lit("<<", "="),
//    slEq        = lit("<<="),
//    sr          = lit(">>", ">="),
//    srEq        = lit(">>="),
//    star        = lit("*", "="),
//    starEq      = lit("*="),
//    tilda       = lit("~")
//    ;
//
//
//    //-------------------------------------------------------------------------
//    //  JLS 3.9  Keywords
//    //-------------------------------------------------------------------------
//
///*
//
//Keyword
//
//    = ( "abstract"
//      / "assert"
//      / "boolean"
//      / "break"
//      / "byte"
//      / "case"
//      / "catch"
//      / "char"
//      / "class"
//      / "const"
//      / "continue"
//      / "default"
//      / "double"
//      / "do"
//      / "else"
//      / "enum"
//      / "extends"
//      / "false"
//      / "finally"
//      / "final"
//      / "float"
//      / "for"
//      / "goto"
//      / "if"
//      / "implements"
//      / "import"
//      / "interface"
//      / "int"
//      / "instanceof"
//      / "long"
//      / "native"
//      / "new"
//      / "null"
//      / "package"
//      / "private"
//      / "protected"
//      / "public"
//      / "return"
//      / "short"
//      / "static"
//      / "strictfp"
//      / "super"
//      / "switch"
//      / "synchronized"
//      / "this"
//      / "throws"
//      / "throw"
//      / "transient"
//      / "true"
//      / "try"
//      / "void"
//      / "volatile"
//      / "while"
//      ) !LetterOrDigit
//    ;
//
//ASSERT       = "assert"       !LetterOrDigit Spacing ;
//BREAK        = "break"        !LetterOrDigit Spacing ;
//CASE         = "case"         !LetterOrDigit Spacing ;
//CATCH        = "catch"        !LetterOrDigit Spacing ;
//CLASS        = "class"        !LetterOrDigit Spacing ;
//CONTINUE     = "continue"     !LetterOrDigit Spacing ;
//DEFAULT      = "default"      !LetterOrDigit Spacing ;
//DO           = "do"           !LetterOrDigit Spacing ;
//ELSE         = "else"         !LetterOrDigit Spacing ;
//ENUM         = "enum"         !LetterOrDigit Spacing ;
//EXTENDS      = "extends"      !LetterOrDigit Spacing ;
//FINALLY      = "finally"      !LetterOrDigit Spacing ;
//FINAL        = "final"        !LetterOrDigit Spacing ;
//FOR          = "for"          !LetterOrDigit Spacing ;
//IF           = "if"           !LetterOrDigit Spacing ;
//IMPLEMENTS   = "implements"   !LetterOrDigit Spacing ;
//IMPORT       = "import"       !LetterOrDigit Spacing ;
//INTERFACE    = "interface"    !LetterOrDigit Spacing ;
//INSTANCEOF   = "instanceof"   !LetterOrDigit Spacing ;
//NEW          = "new"          !LetterOrDigit Spacing ;
//PACKAGE      = "package"      !LetterOrDigit Spacing ;
//RETURN       = "return"       !LetterOrDigit Spacing ;
//STATIC       = "static"       !LetterOrDigit Spacing ;
//SUPER        = "super"        !LetterOrDigit Spacing ;
//SWITCH       = "switch"       !LetterOrDigit Spacing ;
//SYNCHRONIZED = "synchronized" !LetterOrDigit Spacing ;
//THIS         = "this"         !LetterOrDigit Spacing ;
//THROWS       = "throws"       !LetterOrDigit Spacing ;
//THROW        = "throw"        !LetterOrDigit Spacing ;
//TRY          = "try"          !LetterOrDigit Spacing ;
//VOID         = "void"         !LetterOrDigit Spacing ;
//WHILE        = "while"        !LetterOrDigit Spacing ;
//
// */
//
//    //-------------------------------------------------------------------------
//    //  JLS 3.10  Literals
//    //-------------------------------------------------------------------------
//
//    public static ParsingExpression
//
//    hexDigit = choice(charRange("af"), charRange("AF"), charRange("09")),
//
//    unicodeEscape = sequence(oneMore(lit("u")), hexDigit, hexDigit, hexDigit, hexDigit),
//
//    zeroSeven = charRange("07"),
//
//    octalEscape = choice(
//        sequence(charRange("03"), zeroSeven, zeroSeven),
//        sequence(zeroSeven, zeroSeven),
//        sequence(zeroSeven)),
//
//    escape = sequence(literal("\\"), choice(
//        charSet("btnfr\"\'\\"),
//        octalEscape,
//        unicodeEscape)),
//
//    charLiteral = sequence(
//        literal("'"),
//        choice(
//            escape,
//            sequence(
//                not(charSet("'\\\n\r")),
//                any()))),
//
//    stringLiteral = sequence(
//        literal("\""),
//        zeroMore(choice(
//            escape,
//            sequence(
//                not(charSet("\\\n\r")),
//                any()))),
//        literal("\"")),
//
//    underscores = zeroMore(literal("_")),
//
//    hexDigits = sequence(
//        hexDigit,
//        zeroMore(
//            underscores,
//            hexDigit)),
//
//    hexNumeral = sequence(choice(literal("0x"), literal("0X")), hexDigits),
//
//    digits = sequence(
//        zeroNine,
//        zeroMore(underscores, zeroNine)),
//
//    binaryExponent = sequence(
//        charSet("pP"),
//        optional(charSet("+-")),
//        digits),
//
//    hexSignificand = choice(
//        sequence(
//            choice(literal("0x"), literal("0X")),
//            optional(hexDigits),
//            literal("."),
//            hexDigits),
//        sequence(hexNumeral, optional(literal(".")))),
//
//    floatSuffix = charSet("fFdD"),
//
//    optFloatSuffix = optional(floatSuffix),
//
//    hexFloat = sequence(hexSignificand, binaryExponent, optFloatSuffix),
//
//    exponent = sequence(charSet("eE"), optional(charSet("+-")), digits),
//
//    decimalFloat = choice(
//        sequence(digits, literal("."), optional(digits), optional(exponent), optFloatSuffix),
//        sequence(literal("."), digits, optional(exponent), optFloatSuffix),
//        sequence(digits, exponent, optFloatSuffix),
//        sequence(digits, optional(exponent), floatSuffix)),
//
//    floatLiteral = choice(hexFloat, decimalFloat),
//
//    octalNumeral = sequence(
//        literal("0"),
//        oneMore(underscores, zeroSeven)),
//
//    binaryNumeral = sequence(
//        choice(literal("0b"), literal("0B")),
//        charSet("01"),
//        zeroMore(underscores, charSet("01"))),
//
//    decimalNumeral = choice(
//        literal("0"),
//        sequence(
//            zeroNine,
//            zeroMore(underscores, zeroNine))),
//
//    integerLiteral = sequence(
//        choice(hexNumeral, binaryNumeral, octalNumeral, decimalNumeral),
//        optional(charSet("lL"))),
//
//    // TODO
//    _true,
//    _false,
//
//    literal = token$(choice(
//        floatLiteral,
//        integerLiteral,
//        charLiteral,
//        stringLiteral,
//        _true,
//        _false))
//
//    ;
//
    // TODO
    public static ParsingExpression root;
}

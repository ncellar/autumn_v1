package com.norswap.autumn.parsing.support;

import com.norswap.autumn.parsing.Grammar;
import com.norswap.autumn.parsing.ParsingExpression;
import static com.norswap.autumn.parsing.ParsingExpressionFactory.*;
import static com.norswap.autumn.parsing.extensions.GrammarSyntaxExtension.Type.DECLARATION;
import static com.norswap.autumn.parsing.extensions.GrammarSyntaxExtension.Type.EXPRESSION;

import com.norswap.autumn.parsing.support.dynext.DynExtExtension;
import com.norswap.autumn.parsing.support.dynext.DynExtReader;
import com.norswap.autumn.parsing.support.dynext.DynRef;
import com.norswap.autumn.parsing.support.dynext.DynRefReader;

public final class GrammarGrammar
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private static int i = 0;

    public static ParsingExpression

    // TOKENS

    and         = strtok("&"),
    bang        = strtok("!"),
    equal       = strtok("="),
    plus        = strtok("+"),
    qMark       = strtok("?"),
    colon       = strtok(":"),
    semi        = strtok(";"),
    slash       = strtok("/"),
    star        = strtok("*"),
    tilda       = strtok("~"),
    lBrace      = strtok("{"),
    rBrace      = strtok("}"),
    lParen      = strtok("("),
    rParen      = strtok(")"),
    underscore  = strtok("_"),
    starPlus    = strtok("*+"),
    plusPlus    = strtok("++"),
    arrow       = strtok("->"),
    lAnBra      = strtok("<"),
    rAnBra      = strtok(">"),
    comma       = strtok(","),
    commaPlus   = strtok(",+"),
    minus       = strtok("-"),
    hash        = strtok("#"),
    dollar      = strtok("$"),
    dot         = strtok("."),
    percent     = strtok("%"),
    hat         = strtok("^"),
    backquote   = strtok("`"),

    // NAMES AND LITERALS

    digit
        = charRange('0', '9'),

    hexDigit
        = choice(digit, charRange('a', 'f'), charRange('A', 'F')),

    letter
        = choice(charRange('a', 'z'), charRange('A', 'Z')),

    nameChar
        = choice(letter, digit, literal("_")),

    num
        = named$("num", token(oneMore(digit))),

    exprLit     = keyword("expr"),
    dropLit     = keyword("drop"),
    left_assoc  = keyword("left_assoc"),
    left_recur  = keyword("left_recur"),
    importLit   = keyword("import"),
    declLit     = keyword("decl"),

    reserved
        = choice(exprLit, dropLit, left_assoc, left_recur, importLit, declLit),

    unicodeEscape
        = sequence(literal("\\u"), hexDigit, hexDigit, hexDigit, hexDigit),

    tabOrLineEscape
        = sequence(literal("\\"), charSet("tn")),

    anyEscape
        = sequence(literal("\\"), not(literal("u")), any()),

    escape
        = named$("escape", choice(unicodeEscape, tabOrLineEscape, anyEscape)),

    character
        = named$("character", choice(escape, notCharSet("\n\\"))),

    identifier
        = sequence(not(reserved), letter, zeroMore(nameChar)),

    qualifiedIdentifier
        = aloSeparated(identifier, dot),

    escapedIdentifier
        = sequence(literal("'"), aloUntil(any(), literal("'"))),

    name
        = named$("name", token(choice(identifier, escapedIdentifier))),

    nameOrDollar
        = choice(captureText("name", name), capture("dollar", dollar)),

    // PARSING EXPRESSIONS

    range
        = named$("range", token(
            literal("["),
            captureText("first", character),
            literal("-"),
            captureText("last", character),
            literal("]"))),

    charSet
        = named$("charSet", token(
            literal("["),
            captureText("charSet", oneMore(
                not(literal("]")),
                character)),
            literal("]"))),

    notCharSet
        = named$("notCharSet", token(
            literal("^["),
            captureText("notCharSet", oneMore(not(literal("]"), character))),
            literal("]"))),

    stringLit
        = named$("stringLit", token(
            literal("\""),
            captureText("literal", zeroMore(not(literal("\"")), character)),
            literal("\""))),

    reference
        = sequence(
            captureText("name", name),
            optional(
                strtok("allow"),
                lBrace,
                aloSeparated(captureText($(group("allowed")), name), comma),
                rBrace),
            optional(
                strtok("forbid"),
                lBrace,
                aloSeparated(captureText($(group("forbidden")), name), comma),
                rBrace)),

    captureSuffix
        = capture($(group("captureSuffixes")), choice(
        capture("capture",
            token(sequence(literal(":"), optional(capture("captureText", literal("+")))))),
        capture("accessor",
            sequence(minus, nameOrDollar)),
        capture("group",
            sequence(hash, nameOrDollar)),
        capture("kind",
            sequence(tilda, nameOrDollar)))),

    expr = reference("parsingExpression"),

    // EXPRESSIONS

    parsingExpression
        = named$("parsingExpression", cluster(

        // NOTE(norswap)
        // Using left associativity for choice and sequence ensures that sub-expressions
        // must have higher precedence. This way, we avoid pesky choice of choices or
        // sequence of sequences.

        groupLeftAssoc(++i,
            named$("choice", capture("choice", aloSeparated(expr, slash)))),

        groupLeftAssoc(++i,
            capture("sequence", sequence(expr, oneMore(expr)))),

        groupLeftRec(++i, // binary & capture
            capture("until", sequence(expr, starPlus, expr)),
            capture("aloUntil", sequence(expr, plusPlus, expr)),
            capture("separated", sequence(expr, comma, expr)),
            capture("aloSeparated", sequence(expr, commaPlus, expr)),
            capture("capture", sequence(
                choice(expr, capture("marker", dot)),
                oneMore(captureSuffix)))),

        group(++i, // prefix
            capture("and", sequence(and, expr)),
            capture("not", sequence(bang, expr)),
            capture("token", sequence(percent, expr)),
            capture("dumb", sequence(hat, expr))),

        group(++i, // suffix
            capture("optional", sequence(expr, qMark)),
            capture("zeroMore", sequence(expr, star, not(plus))),
            capture("oneMore", sequence(expr, plus, not(plus)))),

        group(++i, // primary
            sequence(lParen, exprDropPrecedence(expr), rParen),
            capture("drop", sequence(dropLit, expr)),
            capture("ref", reference),
            capture("any", underscore),
            capture("charRange", range),
            captureText("stringLit", stringLit),
            captureText("charSet", charSet),
            captureText("notCharSet", notCharSet),
            capture("custom", sequence(
                backquote,
                new DynRefReader(EXPRESSION, captureText(identifier)),
                lBrace,
                new DynRef(),
                rBrace))))),

        // RULES & CLUSTERS

        lhs =
            named$("lhs", capture("lhs", sequence(
                captureText("ruleName", name),
                zeroMore(captureSuffix),
                optional(capture("dumb", hat)),
                optional(capture("token", percent)),
                equal))),

        clusterArrow =
            namekind("clusterArrow", sequence(
                arrow,
                optional(lhs),
                capture("expr", forbid$(parsingExpression, reference("choice"))))),

        clusterDirective =
            namekindText("clusterDirective", choice(
                    keyword("@+"),
                    keyword("@+_left_assoc"),
                    keyword("@+_left_recur"))),

        exprCluster =
            namekind("exprCluster", sequence(
                exprLit,
                group("entries", oneMore(choice(clusterArrow, clusterDirective))))),

        // TOP LEVEL DECLARATIONS

        rule =
            namekind("rule", sequence(
                lhs,
                accessor("rhs", choice(
                    exprCluster,
                    kind("parsingExpression", capture(parsingExpression)))),
                semi)),

        customDecl =
            namekind("customDecl", sequence(
                declLit,
                new DynRefReader(DECLARATION, captureText("declType", identifier)),
                capture("custom", new DynRef()),
                semi)),

        decl =
            named$("decl", choice(rule, customDecl)),

        innport =
            named$("import", sequence(
                importLit,
                new DynExtReader(captureText(qualifiedIdentifier)),
                semi)),

        root =
            named$("grammar", sequence(
                group("imports", zeroMore(innport)),
                group("decls", oneMore(decl))));

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private static ParsingExpression namekind(String string, ParsingExpression pe)
    {
        return named$(string, capture($(kind(string)), pe));
    }

    // ---------------------------------------------------------------------------------------------

    private static ParsingExpression namekindText(String string, ParsingExpression pe)
    {
        return named$(string, captureText($(kind(string)), pe));
    }

    // ---------------------------------------------------------------------------------------------

    private static ParsingExpression keyword(String string)
    {
        return named$(string, token(literal(string), not(nameChar)));
    }

    // ---------------------------------------------------------------------------------------------

    private static ParsingExpression strtok(String string)
    {
        return named$(string, token(string));
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public static final Grammar grammar = Grammar
        .fromRoot(root)
        .withExtension(new DynExtExtension())
        .build();

    ////////////////////////////////////////////////////////////////////////////////////////////////
}

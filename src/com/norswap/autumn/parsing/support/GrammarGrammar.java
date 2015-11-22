package com.norswap.autumn.parsing.support;

import com.norswap.autumn.parsing.Grammar;
import com.norswap.autumn.parsing.ParsingExpression;

import static com.norswap.autumn.parsing.ParsingExpressionFactory.*;

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

    reserved
        = choice(exprLit, dropLit, left_assoc, left_recur),

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
                aloSeparated(captureTextGrouped("allowed", name), comma),
                rBrace),
            optional(
                strtok("forbid"),
                lBrace,
                aloSeparated(captureTextGrouped("forbidden", name), comma),
                rBrace)),

    captureSuffix
        = group$("captureSuffixes", capture(choice(
        capture("capture",
            token(sequence(literal(":"), optional(capture("captureText", literal("+")))))),
        capture("accessor",
            sequence(minus, nameOrDollar)),
        capture("group",
            sequence(hash, nameOrDollar)),
        capture("tag",
            sequence(tilda, nameOrDollar))))),

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
            captureText("notCharSet", notCharSet)))),

        // RULES & CLUSTERS

        ruleLeftHandSide =
            named$("ruleLeftHandSide", capture(sequence(
                captureText("ruleName", name),
                zeroMore(captureSuffix),
                optional(capture("dumb", hat)),
                optional(capture("token", percent)),
                equal))),

        lhs =
            accessor$("lhs", ruleLeftHandSide),

        clusterArrow =
            nametag$("clusterArrow", capture(sequence(
                arrow,
                optional(lhs),
                capture("expr", forbid$(parsingExpression, reference("choice")))))),

        clusterDirective =
            nametag$("clusterDirective", captureText(choice(
                    keyword("@+"),
                    keyword("@+_left_assoc"),
                    keyword("@+_left_recur")))),

        exprCluster =
            nametag$("exprCluster", capture(
                exprLit,
                group$("entries", oneMore(choice(clusterArrow, clusterDirective))))),

        // SYNTAX EXTENSIONS

        syntaxRhs =
            null,

        // TOP LEVEL DECLARATIONS

        declSyntaxDef =
            nametag$("declSyntaxDef",
                sequence(keyword("decl"), keyword("syntax"), equal, qualifiedIdentifier)),

        exprSyntaxDef =
            nametag$("declSyntaxDef",
                sequence(keyword("expr"), keyword("syntax"), equal, qualifiedIdentifier)),

        rule =
            nametag$("rule", capture(
                lhs,
                accessor$("rhs", choice(
                    exprCluster,
                    tag$("parsingExpression", capture(parsingExpression)))))),

        decl =
            named$("decl", sequence(
                choice(rule, declSyntaxDef, exprSyntaxDef),
                semi)),

        root =
            named$("grammar", group$("decls", oneMore(decl)));

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private static ParsingExpression nametag$(String string, ParsingExpression pe)
    {
        return named$(string, tag$(string, pe));
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

    public static final Grammar grammar = Grammar.fromRoot(root).build();

    ////////////////////////////////////////////////////////////////////////////////////////////////
}

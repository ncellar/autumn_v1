package com.norswap.autumn.test.parsing;

import com.norswap.autumn.parsing.Source;
import com.norswap.autumn.parsing3.ParseResult;
import com.norswap.autumn.parsing3.Parser;
import com.norswap.autumn.parsing3.ParserConfiguration;
import com.norswap.autumn.parsing3.ParsingExpression;

import java.io.IOException;

import static com.norswap.autumn.parsing3.ParsingExpressionFactory.*;

public class MouseGrammar
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    /*
        The original Mouse grammar this is based on.

        Grammar   = Space (Rule/Skip)*+ EOT ;
        Rule      = Name EQUAL RuleRhs DiagName? SEMI ;
        Skip      = SEMI / _++ (SEMI/EOT) ;
        RuleRhs   = Sequence Actions (SLASH Sequence Actions)* ;
        Choice    = Sequence (SLASH Sequence)* ;
        Sequence  = Prefixed+ ;
        Prefixed  = PREFIX? Suffixed ;
        Suffixed  = Primary (UNTIL Primary / SUFFIX)? ;
        Primary   = Name
                  / LPAREN Choice RPAREN
                  / ANY
                  / StringLit
                  / Range
                  / CharClass ;
        Actions   = OnSucc OnFail ;
        OnSucc    = (LWING AND? Name? RWING)? ;
        OnFail    = (TILDA LWING Name? RWING)? ;
        Name      = Letter (Letter / Digit)* Space ;
        DiagName  = "<" Char++ ">" Space ;
        StringLit = ["] Char++ ["] Space ;
        CharClass = ("[" / "^[") Char++ "]" Space ;
        Range     = "[" Char "-" Char "]" Space ;
        Char      = Escape / ^[\r\n\\] ;
        Escape    = "\\u" HexDigit HexDigit HexDigit HexDigit
                  / "\\t"
                  / "\\n"
                  / "\\r"
                  / !"\\u""\\"_ ;
        Letter    = [a-z] / [A-Z] ;
        Digit     = [0-9] ;
        HexDigit  = [0-9] / [a-f] / [A-F] ;
        PREFIX    = [&!]  Space ;
        SUFFIX    = [?*+] Space ;
        UNTIL     = ("*+" / "++") Space ;
        EQUAL     = "=" Space ;
        SEMI      = ";" Space ;
        SLASH     = "/" Space ;
        AND       = "&" Space ;
        LPAREN    = "(" Space ;
        RPAREN    = ")" Space ;
        LWING     = "{" Space ;
        RWING     = "}" Space ;
        TILDA     = "~" Space ;
        ANY       = "_" Space ;
        Space     = ([ \r\n\t] / Comment)* ;
        Comment   = "//" _*+ EOL ;
        EOL       = [\r]? [\n] / !_  ;
        EOT       = !_  ;
    */

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public static ParsingExpression

    and         = token(literal("&")),
    bang        = token(literal("!")),
    equal       = token(literal("=")),
    plus        = token(literal("+")),
    qMark       = token(literal("?")),
    semi        = token(literal(";")),
    slash       = token(literal("/")),
    star        = token(literal("*")),
    tilda       = token(literal("_")),
    lBrace      = token(literal("{")),
    rBrace      = token(literal("}")),
    lParen      = token(literal("(")),
    rParen      = token(literal(")")),
    underscore  = token(literal("_")),
    until       = token(literal("*+")),
    aloUntil    = token(literal("++")),

    EOT         = named$("EOT", not(any())),
    EOL         = named$("EOL", choice(literal("\n"), EOT)),

    comment     = named$("comment", sequence(literal("//"), zeroMore(not(EOL), any()), EOL)),
    whitespace  = named$("whitespace", zeroMore(choice(charSet(" \n\t"), comment))),

    digit       = charRange('0', '9'),
    hexDigit    = choice(digit, charRange('a', 'f'), charRange('A', 'F')),
    letter      = choice(charRange('a', 'z'), charRange('A', 'Z')),

    escape = named$("escape", choice(
        sequence(literal("\\u"), hexDigit, hexDigit, hexDigit, hexDigit),
        sequence(literal("\\"), charSet("tnr")),
        sequence(not(literal("\\u")), literal("\\"), any()))),

    character = named$("character", choice(escape, notChars("\n\\"))),

    range = named$("range", token(
        literal("["),
        captureText("first", character),
        literal("-"),
        captureText("last", character),
        literal("]"))),

    charSet = named$("charSet", token(
        literal("["),
        capture("charSet", oneMore(not(literal("]")), character)),
        literal("]"))),

    notCharSet = named$("notCharSet", token(
        literal("^["),
        capture("notCharSet", oneMore(not(literal("]"), character))),
        literal("]"))),

    stringLit = named$("stringLit", token(
        literal("\""),
        capture("literal", oneMore(not(literal("\"")), character)),
        literal("\""))),

    diagName    = named$("diagName", token(literal("<"), until(character, literal(">")))),
    name        = named$("name", token(letter, zeroMore(choice(letter, digit)))),

    onFail = named$("onFail", capture("onFail", sequence(
        tilda,
        lBrace,
        optional(capture("name", name)),
        rBrace))),

    onSucc = named$("onSucc", capture("onSucc", sequence(
        lBrace,
        optional(capture("boolean", and)),
        optional(capture("name", name)),
        rBrace))),

    primary = named$("primary", choice(
        sequence(lParen, reference("choice"), rParen),
        captureText("ref", name),
        capture("any", underscore),
        capture("charRange", range),
        stringLit,
        charSet,
        notCharSet)),

    suffixed = named$("suffixed", choice(
        capture("until", sequence(primary, until, primary)),
        capture("aloUntil", sequence(primary, aloUntil, primary)),
        capture("optional", sequence(primary, qMark)),
        capture("zeroMore", sequence(primary, star)),
        capture("oneMore", sequence(primary, plus)),
        primary)),

    prefixed = named$("prefixed", choice(
        capture("and", sequence(and, suffixed)),
        capture("not", sequence(bang, suffixed)),
        suffixed)),

    sequence    = named$("sequence", capture("seq", oneMore(prefixed))),
    choice      = recursive$("choice", aloSeparated(sequence, slash)),

    ruleRhs = named$("ruleRhs", aloSeparated(
        captureGrouped("alts", sequence(sequence, optional(onSucc), optional(onFail))),
        slash)),

    rule = named$("rule",
        sequence(captureText("ruleName", name), equal, ruleRhs, optional(diagName), semi)),

    grammar = named$("grammar",
        sequence(whitespace(), aloUntil(captureGrouped("rules", rule), EOT)))

    ;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    static ParserConfiguration config = new ParserConfiguration();
    static {
        config.whitespace = whitespace;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    static void emitGrammar(ParseResult result)
    {
        System.out.println(result.toTreeString());
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public static void main(String[] args)
    {
        //String filename = args[0];
        String filename = "src/com/norswap/autumn/test/parsing/JavaGrammar.mouse";

        try
        {
            Source source = Source.fromFile(filename);
            Parser parser = new Parser(source, config);
            //grammar = InstrumentExpression.trace(grammar);
            parser.parse(grammar);
            parser.report();
            if (parser.succeeded())
            {
                emitGrammar(parser.result());
            }
        }
        catch (IOException e)
        {
            System.out.println("Could not read file: " + filename);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}

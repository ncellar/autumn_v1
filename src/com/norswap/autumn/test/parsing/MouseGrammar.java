package com.norswap.autumn.test.parsing;

import com.norswap.autumn.parsing.ParseOutput;
import com.norswap.autumn.parsing.Parser;
import com.norswap.autumn.parsing.ParserConfiguration;
import com.norswap.autumn.parsing.ParsingExpression;
import com.norswap.autumn.parsing.Source;
import com.norswap.autumn.parsing.Tracer;

import java.io.IOException;

import static com.norswap.autumn.parsing.ParsingExpressionFactory.*;

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

    and         = token("&"),
    bang        = token("!"),
    equal       = token("="),
    plus        = token("+"),
    qMark       = token("?"),
    semi        = token(";"),
    slash       = token("/"),
    star        = token("*"),
    tilda       = token("_"),
    lBrace      = token("{"),
    rBrace      = token("}"),
    lParen      = token("("),
    rParen      = token(")"),
    underscore  = token("_"),
    until       = token("*+"),
    aloUntil    = token("++"),

    EOT         = named$("EOT", not(any())),
    EOL         = named$("EOL", choice(literal("\n"), EOT)),

    comment     = named$("comment", sequence(literal("//"), zeroMore(not(EOL), any()), EOL)),
    whitespace  = named$("whitespace", zeroMore(choice(charSet(" \n\t"), comment))),

    digit       = charRange("09"),
    hexDigit    = choice(digit, charRange("af"), charRange("AF")),
    letter      = choice(charRange("az"), charRange("AZ")),

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
        sequence(lParen, ref("choice"), rParen),
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

    sequence    = named$("sequence", capture$("seq", oneMore(prefixed))),
    choice      = recursive$("choice", aloSeparated(sequence, slash)),

    ruleRhs = named$("ruleRhs", aloSeparated(
        captureMultiple("alts", sequence(sequence, optional(onSucc), optional(onFail))),
        slash)),

    rule = named$("rule",
        sequence(captureText("ruleName", name), equal, ruleRhs, optional(diagName), semi)),

    grammar = named$("grammar",
        sequence(whitespace(), aloUntil(captureMultiple("rules", rule), EOT)))

    ;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    static ParserConfiguration config = new ParserConfiguration();
    static {
        config.whitespace = whitespace;
        config.debug = true;
        config.tracer = null;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    static void emitGrammar(ParseOutput output)
    {
        System.out.println(output.toString());
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
            parser.parse(grammar);
            parser.reportErrors();
            emitGrammar(parser.result());
        }
        catch (IOException e)
        {
            System.out.println("Could not read file: " + filename);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}

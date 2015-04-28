package com.norswap.autumn.test.parsing;

import com.norswap.autumn.parsing.ParseOutput;
import com.norswap.autumn.parsing.Parser;
import com.norswap.autumn.parsing.ParsingExpression;
import com.norswap.autumn.test.Ensure;
import com.norswap.autumn.test.ParserProvider;
import com.norswap.autumn.test.TestRunner;
import com.norswap.autumn.util.Array;

import static com.norswap.autumn.parsing.ParsingExpressionFactory.*;

// TODO fix
public class FeatureTests
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    boolean stopOnFirst = true;

    Runnable[] tests = {
        this::testToken,
        this::testLeftRecursive,
        this::testLeftAssociative,
        this::testCapture,
        this::testMultipleCapture,
        this::testRightAssociativity,
        this::testLeftAssociativity,
        this::testPrecedence
    };

    ////////////////////////////////////////////////////////////////////////////////////////////////

    ParsingExpression num = charRange('1', '9');

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public static void run()
    {
        new FeatureTests().doRun();
        System.out.println("Feature tests succeeded.");
    }

    void doRun()
    {
        TestRunner runner = new TestRunner(tests);
        runner.run();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public void testToken()
    {
        ParsingExpression expr = oneMore(token$(literal("*")));

        Ensure.match("*", expr);
        Ensure.match("* \n\t", expr);
        Ensure.match("* \n\t*** * * \n\t", expr);
        Ensure.match("* // hello lol", expr);
        Ensure.match("* /* is diz real life? */", expr);
        Ensure.match("* /* nested /* amazing innit? */ lol */", expr);
        Ensure.fails(" ", expr);
        Ensure.fails(" *", expr);
    }

    // ---------------------------------------------------------------------------------------------

    public void testLeftRecursive()
    {
        ParsingExpression expr = recursive$("expr", choice(
            leftRecursive$(sequence(ref("expr"), literal("*"))),
            num));

        Ensure.match("1", expr);
        Ensure.match("1*", expr);
        Ensure.match("1***", expr);
    }

    // ---------------------------------------------------------------------------------------------

    public void testLeftAssociative()
    {
        ParsingExpression expr = recursive$("expr", choice(
            leftAssociative$(sequence(ref("expr"), literal("+"), ref("expr"))),
            leftAssociative$(sequence(ref("expr"), literal("*"), ref("expr"))),
            num));

        Ensure.match("1", expr);
        Ensure.match("1+1", expr);
        Ensure.match("1*1", expr);
        Ensure.match("1+1+1+1", expr);
        Ensure.match("1*1+1*1", expr);
    }


    // ---------------------------------------------------------------------------------------------

    public void testCapture()
    {
        ParsingExpression expr = sequence(
            captureText("a", oneMore(literal("a"))));

        Parser parser = ParserProvider.parser("aaa");
        parser.parse(expr);

        ParseOutput result = parser.result();
        ParseOutput aResult = result.get("a");

        Ensure.equals(aResult.startPosition(), 0);
        Ensure.equals(aResult.endPosition(), 3);
        Ensure.equals(aResult.value(), "aaa");
    }

    // ---------------------------------------------------------------------------------------------

    public void testMultipleCapture()
    {
        ParsingExpression expr = sequence(
            oneMore(captureTextMultiple("a", literal("a"))));

        Parser parser = ParserProvider.parser("aaa");
        parser.parse(expr);

        ParseOutput result = parser.result();
        Array<ParseOutput> aResults = result.get("a").children();

        Ensure.equals(aResults.size(), 3);

        for (int i = 0; i < 3; ++i)
        {
            Ensure.equals(aResults.get(i).startPosition(), i);
            Ensure.equals(aResults.get(i).endPosition(), i + 1);
            Ensure.equals(aResults.get(i).value(), "a");
        }
    }

    // ---------------------------------------------------------------------------------------------

    public void testRightAssociativity()
    {
        ParsingExpression expr = recursive$("expr", choice(
            capture("plus", rightAssociative$(sequence(
                capture$("left", ref("expr")),
                literal("+"),
                capture$("right", ref("expr"))))),
            capture("num", ref(num))));

        Parser parser = ParserProvider.parser("1+1+1");
        parser.parse(expr);
        Ensure.equals(parser.result().endPosition(), 5);

        ParseOutput result = parser.result();
        ParseOutput plus = result.get("plus");
        Ensure.different(plus.get("left").get("num"), null);
        Ensure.different(plus.get("right").get("plus").get("left").get("num"), null);
        Ensure.different(plus.get("right").get("plus").get("right").get("num"), null);
    }

    // ---------------------------------------------------------------------------------------------

    public void testLeftAssociativity()
    {
        ParsingExpression expr = recursive$("expr", choice(
            capture("plus", leftAssociative$(sequence(
                capture("left", ref("expr")),
                literal("+"),
                capture("right", ref("expr"))))),
            capture("num", ref(num))));

        Parser parser = ParserProvider.parser("1+1+1");
        parser.parse(expr);
        ParseOutput result = parser.result();
        Ensure.equals(result.endPosition(), 5);

        ParseOutput plus = result.get("plus");
        Ensure.different(plus.get("right").get("num"), null);
        Ensure.different(plus.get("left").get("plus").get("right").get("num"), null);
        Ensure.different(plus.get("left").get("plus").get("left").get("num"), null);
    }

    // ---------------------------------------------------------------------------------------------

    public void testPrecedence()
    {
        ParsingExpression expr = recursive$("expr", choice(

            withPrecedence$(1, capture("+", leftAssociative$(sequence(
                capture("left", ref("expr")),
                literal("+"),
                capture("right", ref("expr")))))),

            withPrecedence$(2, capture("*", leftAssociative$(sequence(
                capture("left", ref("expr")),
                literal("*"),
                capture("right", ref("expr")))))),

            withPrecedence$(3, capture("^", leftAssociative$(sequence(
                capture("left", ref("expr")),
                literal("^"),
                capture("right", ref("expr")))))),

            capture("num", ref(num))));

        Parser parser = ParserProvider.parser("1+1*1");
        parser.parse(expr);
        ParseOutput result = parser.result();
        Ensure.equals(result.endPosition(), 5);

        Ensure.different(result.get("+").get("left").get("num"), null);
        Ensure.different(result.get("+").get("right").get("*").get("left").get("num"), null);
        Ensure.different(result.get("+").get("right").get("*").get("right").get("num"), null);

        parser = ParserProvider.parser("1*1+1");
        parser.parse(expr);
        result = parser.result();
        Ensure.equals(result.endPosition(), 5);

        Ensure.different(result.get("+").get("right").get("num"), null);
        Ensure.different(result.get("+").get("left").get("*").get("left").get("num"), null);
        Ensure.different(result.get("+").get("left").get("*").get("right").get("num"), null);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}

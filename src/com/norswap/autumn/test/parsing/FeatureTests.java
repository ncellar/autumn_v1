package com.norswap.autumn.test.parsing;

import com.norswap.autumn.parsing3.ParseResult;
import com.norswap.autumn.parsing3.Parser;
import com.norswap.autumn.parsing3.ParsingExpression;
import com.norswap.autumn.test.Ensure;
import com.norswap.autumn.test.TestConfiguration;
import com.norswap.autumn.test.TestRunner;
import com.norswap.autumn.util.Array;

import static com.norswap.autumn.parsing3.ParsingExpressionFactory.*;

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

    public static void main(String[] args)
    {
        run();
    }

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
        ParsingExpression expr = oneMore(token(literal("*")));

        Ensure.match("*", expr);
        Ensure.match("* \n\t", expr);
        Ensure.match("* \n\t*** * * \n\t", expr);
        Ensure.match("* // hello lol", expr);
        Ensure.match("* /* is diz real life? */", expr);
        Ensure.match("* /* nested /* amazing innit? */ lol */", expr);
        Ensure.fails(" ", expr);
        Ensure.match(" *", expr);
    }

    // ---------------------------------------------------------------------------------------------

    public void testLeftRecursive()
    {
        ParsingExpression expr = recursive$("expr", choice(
            leftRecursive(sequence(reference("expr"), literal("*"))),
            num));

        Ensure.match("1", expr);
        Ensure.match("1*", expr);
        Ensure.match("1***", expr);
    }

    // ---------------------------------------------------------------------------------------------

    public void testLeftAssociative()
    {
        ParsingExpression expr = recursive$("expr", choice(
            leftAssociative(sequence(reference("expr"), literal("+"), reference("expr"))),
            leftAssociative(sequence(reference("expr"), literal("*"), reference("expr"))),
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
        ParsingExpression expr = captureText("a", oneMore(literal("a")));

        Parser parser = TestConfiguration.parser("aaa");
        parser.parse(expr);

        ParseResult result = parser.result();
        ParseResult aResult = result.get("a");

        Ensure.equals(aResult.position, 0);
        Ensure.equals(aResult.endPosition(), 3);
        Ensure.equals(aResult.value, "aaa");
    }

    // ---------------------------------------------------------------------------------------------

    public void testMultipleCapture()
    {
        ParsingExpression expr = sequence(
            oneMore(captureTextGrouped("a", literal("a"))));

        Parser parser = TestConfiguration.parser("aaa");
        parser.parse(expr);

        ParseResult result = parser.result();
        Array<ParseResult> aResults = result.get("a").children;

        Ensure.equals(aResults.size(), 3);

        for (int i = 0; i < 3; ++i)
        {
            Ensure.equals(aResults.get(i).position, i);
            Ensure.equals(aResults.get(i).endPosition(), i + 1);
            Ensure.equals(aResults.get(i).value, "a");
        }
    }

    // ---------------------------------------------------------------------------------------------

    public void testRightAssociativity()
    {
        ParsingExpression expr1 = recursive$("expr", choice(
            leftRecursive(capture("plus", sequence(
                capture("left", reference("expr")),
                literal("+"),
                capture("right", reference("expr"))))),
            capture("num", num)));

        ParsingExpression expr2 = recursive$("expr", leftRecursive(choice(
            capture("plus", sequence(
                capture("left", reference("expr")),
                literal("+"),
                capture("right", reference("expr")))),
            capture("num", num))));

        for (ParsingExpression expr: new ParsingExpression[]{expr1, expr2})
        {
            Parser parser = TestConfiguration.parser("1+1+1");
            parser.parse(expr);
            Ensure.equals(parser.result().endPosition(), 5);

            ParseResult result = parser.result();
            ParseResult plus = result.get("plus");

            Ensure.different(plus.get("left").get("num"), null);
            Ensure.different(plus.get("right").get("plus").get("left").get("num"), null);
            Ensure.different(plus.get("right").get("plus").get("right").get("num"), null);
        }
    }

    // ---------------------------------------------------------------------------------------------

    public void testLeftAssociativity()
    {
        ParsingExpression expr = recursive$("expr", choice(
            capture("plus", leftAssociative(sequence(
                capture("left", reference("expr")),
                literal("+"),
                capture("right", reference("expr"))))),
            capture("num", num)));

        Parser parser = TestConfiguration.parser("1+1+1");
        parser.parse(expr);
        ParseResult result = parser.result();
        Ensure.equals(result.endPosition(), 5);

        ParseResult plus = result.get("plus");
        Ensure.different(plus.get("right").get("num"), null);
        Ensure.different(plus.get("left").get("plus").get("right").get("num"), null);
        Ensure.different(plus.get("left").get("plus").get("left").get("num"), null);
    }

    // ---------------------------------------------------------------------------------------------

    public void testPrecedence()
    {
        ParsingExpression expr = recursive$("expr", choice(

            precedence(1, capture("+", leftAssociative(sequence(
                capture("left", reference("expr")),
                literal("+"),
                capture("right", reference("expr")))))),

            precedence(2, capture("*", leftAssociative(sequence(
                capture("left", reference("expr")),
                literal("*"),
                capture("right", reference("expr")))))),

            precedence(3, capture("^", leftAssociative(sequence(
                capture("left", reference("expr")),
                literal("^"),
                capture("right", reference("expr")))))),

            capture("num", num)));

        Parser parser = TestConfiguration.parser("1+1*1");
        parser.parse(expr);
        ParseResult result = parser.result();
        Ensure.equals(result.endPosition(), 5);

        Ensure.different(result.get("+").get("left").get("num"), null);
        Ensure.different(result.get("+").get("right").get("*").get("left").get("num"), null);
        Ensure.different(result.get("+").get("right").get("*").get("right").get("num"), null);

        parser = TestConfiguration.parser("1*1+1");
        parser.parse(expr);
        result = parser.result();
        Ensure.equals(result.endPosition(), 5);

        Ensure.different(result.get("+").get("right").get("num"), null);
        Ensure.different(result.get("+").get("left").get("*").get("left").get("num"), null);
        Ensure.different(result.get("+").get("left").get("*").get("right").get("num"), null);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}

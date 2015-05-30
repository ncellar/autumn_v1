package com.norswap.autumn.test.parsing;

import com.norswap.autumn.parsing.ParseTree;
import com.norswap.autumn.parsing.Parser;
import com.norswap.autumn.parsing.ParsingExpression;
import com.norswap.autumn.test.Ensure;
import com.norswap.autumn.test.TestConfiguration;
import com.norswap.autumn.test.TestRunner;
import com.norswap.autumn.util.Array;

import static com.norswap.autumn.parsing.ParsingExpressionFactory.*;
import static com.norswap.autumn.util.Pair.$;

public final class FeatureTests
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    Runnable[] tests = {
        this::testToken,
        this::testLeftRecursive,
        this::testLeftAssociative,
        this::testCapture,
        this::testMultipleCapture,
        this::testRightAssociativity,
        this::testLeftAssociativity,
        this::testPrecedence,
        this::testExpression,
        this::testExpression2,
        this::testExpression3
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
            leftRecursive(reference("expr"), literal("*")),
            num));

        Ensure.match("1", expr);
        Ensure.match("1*", expr);
        Ensure.match("1***", expr);
    }

    // ---------------------------------------------------------------------------------------------

    public void testLeftAssociative()
    {
        ParsingExpression expr = recursive$("expr", choice(
            leftAssociative(reference("expr"), literal("+"), reference("expr")),
            leftAssociative(reference("expr"), literal("*"), reference("expr")),
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

        ParseTree tree = parser.tree();
        ParseTree aTree = tree.get("a");

        Ensure.equals(aTree.value, "aaa");
    }

    // ---------------------------------------------------------------------------------------------

    public void testMultipleCapture()
    {
        ParsingExpression expr = sequence(
            oneMore(captureTextGrouped("a", literal("a"))));

        Parser parser = TestConfiguration.parser("aaa");
        parser.parse(expr);

        ParseTree tree = parser.tree();
        Array<ParseTree> aResults = tree.get("a").children;

        Ensure.equals(aResults.size(), 3);

        for (int i = 0; i < 3; ++i)
        {
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
            Ensure.equals(parser.endPosition(), 5);

            ParseTree tree = parser.tree();
            ParseTree plus = tree.get("plus");

            Ensure.equals(tree.childrenCount(), 1);
            Ensure.different(plus.get("left").get("num"), null);
            Ensure.different(plus.get("right").get("plus").get("left").get("num"), null);
            Ensure.different(plus.get("right").get("plus").get("right").get("num"), null);
        }
    }

    // ---------------------------------------------------------------------------------------------

    public void testLeftAssociativity()
    {
        ParsingExpression expr = recursive$("expr", choice(
            capture("plus", leftAssociative(
                capture("left", reference("expr")),
                literal("+"),
                capture("right", reference("expr")))),
            capture("num", num)));

        Parser parser = TestConfiguration.parser("1+1+1");
        parser.parse(expr);
        Ensure.ensure(parser.succeeded());
        ParseTree tree = parser.tree();

        ParseTree plus = tree.get("plus");
        Ensure.different(plus.get("right").get("num"), null);
        Ensure.different(plus.get("left").get("plus").get("right").get("num"), null);
        Ensure.different(plus.get("left").get("plus").get("left").get("num"), null);
    }

    // ---------------------------------------------------------------------------------------------

    public void testPrecedence()
    {
        ParsingExpression expr = recursive$("expr", choice(

            precedence(1, capture("+", leftAssociative(
                capture("left", reference("expr")),
                literal("+"),
                capture("right", reference("expr"))))),

            precedence(2, capture("*", leftAssociative(
                capture("left", reference("expr")),
                literal("*"),
                capture("right", reference("expr"))))),

            precedence(3, capture("^", leftAssociative(
                capture("left", reference("expr")),
                literal("^"),
                capture("right", reference("expr"))))),

            capture("num", num)));

        Parser parser = TestConfiguration.parser("1+1*1");
        parser.parse(expr);
        Ensure.ensure(parser.succeeded());
        ParseTree tree = parser.tree();

        Ensure.different(tree.get("+").get("left").get("num"), null);
        Ensure.different(tree.get("+").get("right").get("*").get("left").get("num"), null);
        Ensure.different(tree.get("+").get("right").get("*").get("right").get("num"), null);

        parser = TestConfiguration.parser("1*1+1");
        parser.parse(expr);
        Ensure.ensure(parser.succeeded());
        tree = parser.tree();

        Ensure.different(tree.get("+").get("right").get("num"), null);
        Ensure.different(tree.get("+").get("left").get("*").get("left").get("num"), null);
        Ensure.different(tree.get("+").get("left").get("*").get("right").get("num"), null);
    }

    // ---------------------------------------------------------------------------------------------

    public void testExpression()
    {
        // TODO: test deepCopy to simplify all these messes
        // TODO: write a way to build trees easily; then equals check to test easily?

        ParsingExpression plus = exprLeftAssociative$(capture("+", sequence(
            capture("left", reference("expr")),
            literal("+"),
            capture("right", reference("expr")))));

        ParsingExpression minus = exprLeftAssociative$(capture("-", sequence(
            capture("left", reference("expr")),
            literal("-"),
            capture("right", reference("expr")))));

        ParsingExpression mult = exprLeftAssociative$(capture("*", sequence(
            capture("left", reference("expr")),
            literal("*"),
            capture("right", reference("expr")))));

        ParsingExpression div = exprLeftAssociative$(capture("/", sequence(
            capture("left", reference("expr")),
            literal("/"),
            capture("right", reference("expr")))));

        ParsingExpression expr = recursive$("expr", expression(
            $(plus, 1),
            $(minus, 1),
            $(mult, 2),
            $(div, 2),
            $(captureText("num", num), 3)));

        Parser parser = TestConfiguration.parser("1+2-3+4*5/6*7+8");
        parser.parse(expr);
        Ensure.ensure(parser.succeeded());
        ParseTree tree = parser.tree();

        Ensure.equals(tree.get("+").get("right").value("num"), "8");

        ParseTree oneToThree = tree.get("+").get("left").get("+").get("left");

        Ensure.equals(oneToThree.get("-").get("right").value("num"), "3");
        Ensure.equals(oneToThree.get("-").get("left").get("+").get("right").value("num"), "2");
        Ensure.equals(oneToThree.get("-").get("left").get("+").get("left") .value("num"), "1");

        ParseTree fourToSeven = tree.get("+").get("left").get("+").get("right");

        Ensure.equals(fourToSeven.get("*").get("right").value("num"), "7");
        Ensure.equals(fourToSeven.get("*").get("left").get("/").get("right").value("num"), "6");
        Ensure.equals(fourToSeven.get("*").get("left").get("/").get("left").get("*")
            .get("right").value("num"), "5");
        Ensure.equals(fourToSeven.get("*").get("left").get("/").get("left").get("*")
            .get("left").value("num"), "4");
    }

    public void testExpression2()
    {
        // NOTE(norswap): Same as testExpression() but + and - are now right-associative.

        ParsingExpression plus = exprLeftRecursive$(capture("+", sequence(
            capture("left", reference("expr")),
            literal("+"),
            capture("right", reference("expr")))));

        ParsingExpression minus = exprLeftRecursive$(capture("-", sequence(
            capture("left", reference("expr")),
            literal("-"),
            capture("right", reference("expr")))));

        ParsingExpression mult = exprLeftAssociative$(capture("*", sequence(
            capture("left", reference("expr")),
            literal("*"),
            capture("right", reference("expr")))));

        ParsingExpression div = exprLeftAssociative$(capture("/", sequence(
            capture("left", reference("expr")),
            literal("/"),
            capture("right", reference("expr")))));

        ParsingExpression expr = recursive$("expr", expression(
            $(plus, 1),
            $(minus, 1),
            $(mult, 2),
            $(div, 2),
            $(captureText("num", num), 3)));

        Parser parser = TestConfiguration.parser("1+2-3+4*5/6*7+8");
        parser.parse(expr);
        Ensure.ensure(parser.succeeded());
        ParseTree tree = parser.tree();

        Ensure.equals(tree.get("+").get("left").value("num"), "1");
        Ensure.equals(tree.get("+").get("right").get("-").get("left").value("num"), "2");
        Ensure.equals(tree.get("+").get("right").get("-").get("right").get("+").get("left")
            .value("num"), "3");
        Ensure.equals(tree.get("+").get("right").get("-").get("right").get("+").get("right")
            .get("+").get("right").value("num"), "8");

        ParseTree fourToSeven = tree.get("+").get("right").get("-").get("right").get("+")
            .get("right").get("+").get("left");

        Ensure.equals(fourToSeven.get("*").get("right").value("num"), "7");
        Ensure.equals(fourToSeven.get("*").get("left").get("/").get("right").value("num"), "6");
        Ensure.equals(fourToSeven.get("*").get("left").get("/").get("left").get("*")
            .get("right").value("num"), "5");
        Ensure.equals(fourToSeven.get("*").get("left").get("/").get("left").get("*")
            .get("left").value("num"), "4");
    }

    public void testExpression3()
    {
        // NOTE(norswap): Same as testExpression() but * and / are now right-associative.

        ParsingExpression plus = exprLeftAssociative$(capture("+", sequence(
            capture("left", reference("expr")),
            literal("+"),
            capture("right", reference("expr")))));

        ParsingExpression minus = exprLeftAssociative$(capture("-", sequence(
            capture("left", reference("expr")),
            literal("-"),
            capture("right", reference("expr")))));

        ParsingExpression mult = exprLeftRecursive$(capture("*", sequence(
            capture("left", reference("expr")),
            literal("*"),
            capture("right", reference("expr")))));

        ParsingExpression div = exprLeftRecursive$(capture("/", sequence(
            capture("left", reference("expr")),
            literal("/"),
            capture("right", reference("expr")))));

        ParsingExpression expr = recursive$("expr", expression(
            $(plus, 1),
            $(minus, 1),
            $(mult, 2),
            $(div, 2),
            $(captureText("num", num), 3)));

        Parser parser = TestConfiguration.parser("1+2-3+4*5/6*7+8");
        parser.parse(expr);
        Ensure.ensure(parser.succeeded());
        ParseTree tree = parser.tree();

        Ensure.equals(tree.get("+").get("right").value("num"), "8");

        ParseTree oneToThree = tree.get("+").get("left").get("+").get("left");

        Ensure.equals(oneToThree.get("-").get("right").value("num"), "3");
        Ensure.equals(oneToThree.get("-").get("left").get("+").get("right").value("num"), "2");
        Ensure.equals(oneToThree.get("-").get("left").get("+").get("left") .value("num"), "1");

        ParseTree fourToSeven = tree.get("+").get("left").get("+").get("right");

        Ensure.equals(fourToSeven.get("*").get("left").value("num"), "4");
        Ensure.equals(fourToSeven.get("*").get("right").get("/").get("left").value("num"), "5");
        Ensure.equals(fourToSeven.get("*").get("right").get("/").get("right").get("*")
            .get("left").value("num"), "6");
        Ensure.equals(fourToSeven.get("*").get("right").get("/").get("right").get("*")
            .get("right").value("num"), "7");
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}

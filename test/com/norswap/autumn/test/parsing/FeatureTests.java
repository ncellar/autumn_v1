package com.norswap.autumn.test.parsing;

import com.norswap.autumn.ParsingExpression;
import com.norswap.autumn.capture.ParseTree;
import com.norswap.autumn.extensions.cluster.ClusterExtension;
import com.norswap.autumn.test.Ensure;
import com.norswap.autumn.test.TestRunner;

import java.util.List;

import static com.norswap.autumn.ParsingExpressionFactory.*;
import static com.norswap.autumn.test.parsing.ParseTreeBuilder.$;
import static com.norswap.autumn.ParsingExpressionFactory.$;

public final class FeatureTests
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    ParsingExpression pe;

    ParseTree tree, expected;

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

    ParsingExpression

    plus = capture("+", sequence(
        capture("left", reference("expr")),
        literal("+"),
        capture("right", reference("expr")))),

    minus = capture("-", sequence(
        capture("left", reference("expr")),
        literal("-"),
        capture("right", reference("expr")))),

    mult = capture("*", sequence(
        capture("left", reference("expr")),
        literal("*"),
        capture("right", reference("expr")))),

    div = capture("/", sequence(
        capture("left", reference("expr")),
        literal("/"),
        capture("right", reference("expr")))),

    times = capture("*", sequence(
        capture("left", reference("expr")),
        literal("*"),
        capture("right", reference("expr")))),

    exp = capture("^", sequence(
        capture("left", reference("expr")),
        literal("^"),
        capture("right", reference("expr")))),

    num = captureText("num", charRange('1', '9'));

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
        pe = oneMore(token(literal("*")));

        Common.ensureMatch(pe, "*");
        Common.ensureMatch(pe, "* \n\t");
        Common.ensureMatch(pe, "* \n\t*** * * \n\t");
        Common.ensureMatch(pe, "* // hello lol");
        Common.ensureMatch(pe, "* /* is diz real life? */");
        Common.ensureMatch(pe, "* /* nested /* amazing innit? */ lol */");
        Common.ensureFail(pe, " ");
        Common.ensureMatch(pe, " *");
    }

    // ---------------------------------------------------------------------------------------------

    public void testLeftRecursive()
    {
        pe = named$("expr", choice(
            leftRecursive(reference("expr"), literal("*")),
            num.deepCopy()));

        Common.ensureMatch(pe, "1");
        Common.ensureMatch(pe, "1*");
        Common.ensureMatch(pe, "1***");
    }

    // ---------------------------------------------------------------------------------------------

    public void testLeftAssociative()
    {
        pe = named$("expr", choice(
            leftAssociative(reference("expr"), literal("+"), reference("expr")),
            leftAssociative(reference("expr"), literal("*"), reference("expr")),
            num.deepCopy()));

        Common.ensureMatch(pe, "1");
        Common.ensureMatch(pe, "1+1");
        Common.ensureMatch(pe, "1*1");
        Common.ensureMatch(pe, "1+1+1+1");
        Common.ensureMatch(pe, "1*1+1*1");
    }


    // ---------------------------------------------------------------------------------------------

    public void testCapture()
    {
        tree = Common.tree(captureText("a", oneMore(literal("a"))), "aaa");
        Ensure.equals(tree.get("a").value, "aaa");
    }

    // ---------------------------------------------------------------------------------------------

    public void testMultipleCapture()
    {
        tree = Common.tree(sequence(oneMore(captureText($(group("a")), literal("a")))), "aaa");
        List<ParseTree> aResults = tree.group("a");

        Ensure.equals(aResults.size(), 3);

        for (int i = 0; i < 3; ++i)
        {
            Ensure.equals(aResults.get(i).value, "a");
        }
    }

    // ---------------------------------------------------------------------------------------------

    public void testRightAssociativity()
    {
        ParsingExpression expr1 = named$("expr", choice(
            leftRecursive(plus.deepCopy()),
            num.deepCopy()));

        ParsingExpression expr2 = named$("expr", leftRecursive(choice(
            plus.deepCopy(),
            num.deepCopy())));

        for (ParsingExpression expr: new ParsingExpression[]{expr1, expr2})
        {
            tree = Common.tree(expr, "1+2+3");

            expected = $($("+",
                $("left", $("num", "1")),
                $("right", $("+",
                    $("left", $("num", "2")),
                    $("right", $("num", "3"))))));

            Ensure.equals(tree, expected);
        }
    }

    // ---------------------------------------------------------------------------------------------

    public void testLeftAssociativity()
    {
        // NOTE(norswap): it also works if leftAssociative is nested inside the capture

        pe = named$("expr", choice(
            leftAssociative(plus.deepCopy()),
            num.deepCopy()));

        tree = Common.tree(pe, "1+2+3");

        expected = $($("+",
            $("left", $("+",
                $("left", $("num", "1")),
                $("right", $("num", "2")))),
            $("right", $("num", "3"))));

        Ensure.equals(tree, expected);
    }

    // ---------------------------------------------------------------------------------------------

    public void testPrecedence()
    {
        // NOTE(norswap): it also works if leftAssociative is nested inside the capture

        pe = named$("expr", choice(
            precedence(1, leftAssociative(plus.deepCopy())),
            precedence(2, leftAssociative(mult.deepCopy())),
            num.deepCopy()));

        tree = Common.tree(pe, "1+2*3");

        expected = $($("+",
            $("left", $("num", "1")),
            $("right", $("*",
                $("left", $("num", "2")),
                $("right", $("num", "3"))))));

        Ensure.equals(tree, expected);

        tree = Common.tree(pe, "1*2+3");

        expected = $($("+",
            $("left", $("*",
                $("left", $("num", "1")),
                $("right", $("num", "2")))),
            $("right", $("num", "3"))));

        Ensure.equals(tree, expected);
    }

    // ---------------------------------------------------------------------------------------------

    public void testExpression()
    {
        ClusterExtension cext = new ClusterExtension();

        pe = named$("expr", cext.cluster(
            groupLeftAssoc(1,
                plus.deepCopy(),
                minus.deepCopy()),
            groupLeftAssoc(2,
                mult.deepCopy(),
                div.deepCopy()),
            group(3,
                num.deepCopy())));

        tree = Common.tree(pe, "1+2-3+4*5/6*7+8", cext);

        expected = $($("+",
            $("left", $("+",
                    $("left", $("-",
                        $("left", $("+",
                            $("left", $("num", "1")),
                            $("right", $("num", "2")))),
                        $("right", $("num", "3")))),
                    $("right", $("*",
                        $("left", $("/",
                            $("left", $("*",
                                $("left", $("num", "4")),
                                $("right", $("num", "5")))),
                            $("right", $("num", "6")))),
                        $("right", $("num", "7")))))),
            $("right", $("num", "8"))));

        Ensure.equals(tree, expected);
    }

    public void testExpression2()
    {
        ClusterExtension cext = new ClusterExtension();

        // NOTE(norswap): Same as testExpression() but + and - are now right-associative.

        pe = named$("expr", cext.cluster(
            groupLeftRec(1,
                plus.deepCopy(),
                minus.deepCopy()),
            groupLeftAssoc(2,
                mult.deepCopy(),
                div.deepCopy()),
            group(3,
                num.deepCopy())));

        tree = Common.tree(pe, "1+2-3+4*5/6*7+8", cext);

        expected = $($("+",
            $("left", $("num", "1")),
            $("right", $("-",
                $("left", $("num", "2")),
                $("right", $("+",
                    $("left", $("num", "3")),
                    $("right", $("+",
                        $("left", $("*",
                            $("left", $("/",
                                $("left", $("*",
                                    $("left", $("num", "4")),
                                    $("right", $("num", "5")))),
                                $("right", $("num", "6")))),
                            $("right", $("num", "7")))),
                        $("right", $("num", "8"))))))))));

        Ensure.equals(tree, expected);
    }

    public void testExpression3()
    {
        ClusterExtension cext = new ClusterExtension();

        // NOTE(norswap): Same as testExpression() but * and / are now right-associative.

        pe = named$("expr", cext. cluster(
            groupLeftAssoc(1,
                plus.deepCopy(),
                minus.deepCopy()),
            groupLeftRec(2,
                mult.deepCopy(),
                div.deepCopy()),
            group(3,
                num.deepCopy())));

        tree = Common.tree(pe, "1+2-3+4*5/6*7+8", cext);

        expected = $($("+",
            $("left", $("+",
                $("left", $("-",
                    $("left", $("+",
                        $("left", $("num", "1")),
                        $("right", $("num", "2")))),
                    $("right", $("num", "3")))),
                $("right", $("*",
                    $("left", $("num", "4")),
                    $("right", $("/",
                        $("left", $("num", "5")),
                        $("right", $("*",
                            $("left", $("num", "6")),
                            $("right", $("num", "7")))))))))),
            $("right", $("num", "8"))));

        Ensure.equals(tree, expected);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}

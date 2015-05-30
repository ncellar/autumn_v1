package com.norswap.autumn.parsing.support;

import com.norswap.autumn.parsing.ParseTree;
import com.norswap.autumn.parsing.ParsingExpression;
import com.norswap.autumn.parsing.ParsingExpressionFactory;
import com.norswap.autumn.parsing.expressions.Expression;
import com.norswap.autumn.parsing.expressions.Expression.Operand;
import com.norswap.autumn.util.Pair;
import com.norswap.autumn.util.Streams;

import java.util.function.Function;

import static com.norswap.autumn.parsing.ParsingExpressionFactory.*;
import static com.norswap.autumn.parsing.Registry.*; // PEF_EXPR_*
import static com.norswap.autumn.util.StringEscape.unescape;

public final class GrammarCompiler
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    @FunctionalInterface
    private interface Compiler extends Function<ParseTree, ParsingExpression> {}

    @FunctionalInterface
    private interface Grouper extends Function<ParsingExpression[], ParsingExpression> {}

    private static ParsingExpressionFactory F = new ParsingExpressionFactory();

    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Takes the parse tree obtained from a grammar file, and return an array of parsing
     * expressions, corresponding to the grammar rules defined in the grammar.
     *
     * Note that the references inside these expressions are not resolved.
     */
    public ParsingExpression[] compile(ParseTree tree)
    {
        ParseTree rules = tree.group("rules");

        return Streams.from(rules)
            .map(this::compileRule)
            .toArray(ParsingExpression[]::new);
    }

    // ---------------------------------------------------------------------------------------------

    private ParsingExpression compileRule(ParseTree rule)
    {
        String ruleName = rule.value("ruleName");
        ParsingExpression topChoice = compileTopChoice(rule.group("alts"));

        if (rule.has("dumb"))
        {
            topChoice = dumb(topChoice);
        }

        if (rule.has("token"))
        {
            topChoice = token(topChoice);
        }

        return named$(ruleName, topChoice);
    }


    // ---------------------------------------------------------------------------------------------

    private ParsingExpression compileChoiceAlt(ParseTree alt)
    {
        return alt.name.equals("sequence")
            ? compileSequence(alt)
            : compileExpression(alt);
    }

    // ---------------------------------------------------------------------------------------------

    private ParsingExpression compileOneOrGroup(
        Compiler itemCompiler, Grouper grouper, ParseTree tree)
    {
        if (tree.childrenCount() == 1)
        {
            return itemCompiler.apply(tree.child());
        }
        else
        {
            return grouper.apply(Streams.from(tree)
                .map(itemCompiler)
                .toArray(ParsingExpression[]::new));
        }
    }

    // ---------------------------------------------------------------------------------------------

    private ParsingExpression compileTopChoice(ParseTree alts)
    {
        return compileOneOrGroup(
            t -> compileChoiceAlt(t.child(0)),
            exprs -> choice(exprs),
            alts);
    }

    // ---------------------------------------------------------------------------------------------

    private ParsingExpression compileChoice(ParseTree choice)
    {
        return compileOneOrGroup(
            t -> compileChoiceAlt(t),
            exprs -> choice(exprs),
            choice);
    }

    // ---------------------------------------------------------------------------------------------

    private ParsingExpression compileExpression(ParseTree expression)
    {
        class $ {
            int lastPrecedence = 1;
        }
        $ $ = new $();

        return expression(Streams.from(expression.group("alts"))
            .map(alt ->
            {
                ParsingExpression pe = compileSequence(alt.get("sequence"));
                Operand operand = new Operand();
                int precedence = -1;

                for (ParseTree annotation: alt.group("annotations"))
                {
                    // TODO(norswap): this is retarded
                    annotation = annotation.child(0);

                    switch (annotation.name)
                    {
                        case "precedence":
                            precedence = precedence == -1
                                ? Integer.parseInt(annotation.value)
                                : -2;
                            break;

                        case "increment":
                            precedence = precedence == -1
                                ? $.lastPrecedence + 1
                                : -2;
                            break;

                        case "same":
                            precedence = precedence == -1
                                ? $.lastPrecedence
                                : -2;
                            break;

                        case "left_assoc":
                            operand.leftRecursive = true;
                            operand.leftAssociative = true;
                            break;

                        case "left_recur":
                            operand.leftRecursive = true;
                            break;
                    }
                }

                if (precedence == -1)
                {
                    throw new RuntimeException(
                        "Expression alternate does not specify precedence.");
                }

                if (precedence == -2)
                {
                    throw new RuntimeException(
                        "Expression specifies precedence more than once.");
                }

                operand.operand = pe;
                operand.precedence = precedence;
                $.lastPrecedence = precedence;

                return operand;

            }).toArray(Operand[]::new));
    }

    // ---------------------------------------------------------------------------------------------

    private ParsingExpression compileSequence(ParseTree sequence)
    {
        return compileOneOrGroup(
            tree -> compilePrefixed(tree),
            exprs -> sequence(exprs),
            sequence);
    }

    // ---------------------------------------------------------------------------------------------

    private ParsingExpression compilePrefixed(ParseTree prefixed)
    {
        switch (prefixed.name)
        {
            case "and":
                return lookahead(compileSuffixed(prefixed.child()));

            case "not":
                return not(compileSuffixed((prefixed.child())));

            default:
                return compileSuffixed(prefixed);
        }
    }

    // ---------------------------------------------------------------------------------------------

    private ParsingExpression compileSuffixed(ParseTree suffixed)
    {
        switch (suffixed.name)
        {
            case "until":
                return until(
                    compilePrimary(suffixed.child(0)),
                    compilePrimary(suffixed.child(1)));

            case "aloUntil":
                return aloUntil(
                    compilePrimary(suffixed.child(0)),
                    compilePrimary(suffixed.child(1)));

            case "optional":
                return optional(compilePrimary(suffixed.child()));

            case "zeroMore":
                return zeroMore(compilePrimary(suffixed.child()));

            case "oneMore":
                return oneMore(compilePrimary(suffixed.child()));

            default:
                return compilePrimary(suffixed);
        }
    }

    // ---------------------------------------------------------------------------------------------

    private ParsingExpression compilePrimary(ParseTree primary)
    {
        switch (primary.name)
        {
            case "choice":
                return compileChoice(primary);

            case "ref":
                return reference(primary.value);

            case "any":
                return any();

            case "charRange":
                return charRange(
                    unescape(primary.value("first")).charAt(0),
                    unescape(primary.value("last")).charAt(0));

            case "charSet":
                return charSet(unescape(primary.value("charSet")));

            case "notCharSet":
                return notCharSet(unescape(primary.value("notCharSet")));

            case "stringLit":
                return literal(unescape(primary.value("literal")));

            case "drop":
                Expression.DropPrecedence out = new Expression.DropPrecedence();
                out.operand = compilePrimary(primary.child(0));
                return out;

            default:
                throw new RuntimeException("Primary expression with no name.");
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}

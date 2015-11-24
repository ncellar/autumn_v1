package com.norswap.autumn.parsing.support;

import com.norswap.autumn.parsing.Grammar;
import com.norswap.autumn.parsing.GrammarBuilder;
import com.norswap.autumn.parsing.Whitespace;
import com.norswap.autumn.parsing.expressions.Success;
import com.norswap.autumn.parsing.expressions.Capture;
import com.norswap.autumn.parsing.capture.Decoration;
import com.norswap.autumn.parsing.capture.ParseTree;
import com.norswap.autumn.parsing.extensions.cluster.ExpressionCluster.Group;
import com.norswap.autumn.parsing.extensions.cluster.Filter;
import com.norswap.autumn.parsing.ParsingExpression;
import com.norswap.autumn.parsing.expressions.Reference;
import com.norswap.util.Array;
import com.norswap.util.Streams;

import java.util.List;
import java.util.function.Function;

import static com.norswap.autumn.parsing.ParsingExpressionFactory.*;
import static com.norswap.util.StringEscape.unescape;

public final class GrammarCompiler
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    @FunctionalInterface
    private interface Compiler extends Function<ParseTree, ParsingExpression> {}

    @FunctionalInterface
    private interface Grouper extends Function<ParsingExpression[], ParsingExpression> {}

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private Array<ParsingExpression> rules = new Array<>();
    private Array<ParsingExpression> namedClusterAlternates = new Array<>();

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public static GrammarBuilder compile(ParseTree tree)
    {
        Array<ParsingExpression> exprs = new GrammarCompiler().run(tree);

        ParsingExpression whitespace = exprs.stream()
            .filter(rule -> "Spacing".equals(rule.name))
            .findFirst().orElse(Whitespace.DEFAULT());

        return Grammar.fromRoot(exprs.get(0))
            .rules(exprs)
            .whitespace(whitespace);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * TODO edit
     * Takes the parse tree obtained from a grammar file, and return an array of parsing
     * expressions, corresponding to the grammar rules defined in the grammar.
     * <p>
     * Note that the references inside these expressions are not resolved.
     */
    public Array<ParsingExpression> run(ParseTree tree)
    {
        tree.group("decls").forEach(this::compileDeclaration);
        // TODO ?
        rules.addAll(namedClusterAlternates);
        return rules;
    }

    // ---------------------------------------------------------------------------------------------

    private void compileDeclaration(ParseTree declaration)
    {
        if (declaration.hasTag("rule"))
        {
            compileRule(declaration);
        }
        else if (declaration.hasTag("declSyntaxDef"))
        {
            // TODO
        }
        else if (declaration.hasTag("exprSyntaxDef"))
        {
            // TODO
        }
        else
        {
            error("Unknown top-level declaration: %s", declaration);
        }
    }
    // ---------------------------------------------------------------------------------------------

    private void compileRule(ParseTree rule)
    {
        ParseTree lhs = rule.get("lhs");
        ParseTree rhs = rule.get("rhs");
        ParsingExpression pe;

        if (rhs.hasTag("parsingExpression"))
        {
            pe = compilePE(rhs.child());
            pe = decorateRule(lhs, pe);
            rules.add(pe);
        }
        else if (rhs.hasTag("exprCluster"))
        {
            Array<ParsingExpression> namedAlternates = new Array<>();

            pe = compileCluster(rhs, namedAlternates);
            pe = decorateRule(lhs, pe);

            for (ParsingExpression alt: namedAlternates)
            {
                alt = named$(pe.name + "." + alt.name, alt);
                namedClusterAlternates.push(alt);
            }

            rules.add(pe);
        }
        else
        {
            error("Unknown rule right-hand side: %s", rule);
        }
    }

    // ---------------------------------------------------------------------------------------------

    private ParsingExpression decorateRule(ParseTree lhs, ParsingExpression pe)
    {
        if (lhs.has("dumb"))
            pe = dumb(pe);

        if (lhs.has("token"))
            pe = token(pe);

        String ruleName = lhs.value("ruleName");
        List<ParseTree> captureSuffixes = lhs.group("captureSuffixes");

        return named$(ruleName,
            captureSuffixes == null || captureSuffixes.isEmpty()
                ? pe
                : compileCapture(ruleName, pe, captureSuffixes));
    }

    // ---------------------------------------------------------------------------------------------

    private ParsingExpression compileCluster(
        ParseTree expression,
        Array<ParsingExpression> outNamedAlternates)
    {
        // Current precedence level, alternates being collected, and group where the alternates
        // must be added.
        int precedence = 1;
        Array<ParsingExpression> alts = new Array<>();
        Array<Group> groups = new Array<>(group(1, false, false));

        for (ParseTree entry: expression.group("entries"))
        {
            if (entry.hasTag("clusterDirective"))
            {
                if (!alts.isEmpty())
                {
                    // Add the alternates to the current group and prepare for the next group.
                    groups.peek().operands = alts.toArray(ParsingExpression[]::new);
                    alts = new Array<>();
                    ++precedence;
                }
                else
                {
                    groups.pop();
                }

                switch (entry.value)
                {
                    case "@+":
                        groups.push(group(precedence, false, false));
                        break;

                    case "@+_left_recur":
                        groups.push(group(precedence, true, false));
                        break;

                    case "@+_left_assoc":
                        groups.push(group(precedence, true, true));
                        break;

                    default:
                        error("Unknown cluster directive: %s", entry);
                }
            }
            else if (entry.hasTag("clusterArrow"))
            {
                ParsingExpression pe = compilePE(entry.get("expr").child());

                if (entry.has("lhs"))
                {
                    pe = decorateRule(entry.get("lhs"), pe);
                    outNamedAlternates.push(pe);
                }

                alts.push(pe);
            }
            else
            {
                error("Unknown cluster entry: %s", entry);
            }
        }

        if (!alts.isEmpty()) {
            groups.peek().operands = alts.toArray(ParsingExpression[]::new);
        }
        else {
            groups.pop();
        }

        return cluster(groups.toArray(Group[]::new));
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private ParsingExpression compileRef(ParseTree tree)
    {
        Reference ref = reference(tree.value("name"));

        List<ParseTree> allowed = tree.group("allowed");
        List<ParseTree> forbidden = tree.group("forbidden");

        if (!allowed.isEmpty() || !forbidden.isEmpty())
        {
            return filter(
                ref,
                Streams.from(allowed)
                    .map(pe -> reference(ref.target + "." + pe.value))
                    .toArray(ParsingExpression[]::new),
                Streams.from(forbidden)
                    .map(pe -> reference(ref.target + "." + pe.value))
                    .toArray(ParsingExpression[]::new)
            );
        }
        else {
            return ref;
        }
    }

    // ---------------------------------------------------------------------------------------------

    private String name(String name, ParsingExpression expr, ParseTree suffix)
    {
        if (suffix.has("name"))
        {
            return suffix.value("name");
        }

        // Else there is a dollar instead of a name.
        // Either this qualifies a rule ...

        if (name != null)
        {
            return name;
        }

        // ... or a reference (possibly wrapped in a filter).

        if (expr == null)
        {
            error("Dollar ($) capture name used in conjuction with a marker.");
        }

        if (expr instanceof Filter)
        {
            expr = ((Filter) expr).operand;
        }

        if (!(expr instanceof Reference))
        {
            error("Dollar ($) capture name is a suffix of something which is not an identifier");
        }

        return ((Reference)expr).target;
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * The name field is for rule names, if this is a capture over a rule definition.
     * <p>
     * If the child field is null, this is a marker capture.
     */
    private ParsingExpression compileCapture(
        String ruleName,
        ParsingExpression child,
        List<ParseTree> suffixes)
    {
        // A marker implies capture!
        boolean capture = child == null;
        boolean captureText = false;

        int accessors = 0;
        boolean first = true;
        Array<Decoration> decorations = new Array<>();

        for (ParseTree suffix: suffixes)
        {
            suffix = suffix.child();

            switch (suffix.accessor)
            {
                case "capture":
                    if (!first)
                    {
                        error("Capture suffix (:) not appearing as first suffix.");
                    }
                    capture = true;
                    captureText = suffix.has("captureText");
                    break;

                case "accessor":
                    decorations.add(accessor(name(ruleName, child, suffix)));
                    ++accessors;
                    break;

                case "group":
                    decorations.add(group(name(ruleName, child, suffix)));
                    ++accessors;
                    break;

                case "tag":
                    decorations.add(tag(name(ruleName, child, suffix)));
                    break;

                default:
                    error("Unknown capture type: %s", suffix.accessor);
            }

            first = false;
        }

        if (accessors > 1)
            error("More than one accessor or group specification.");

        return new Capture(
            capture,
            captureText,
            child == null ? new Success() : child,
            decorations.toArray(Decoration[]::new));
    }

    // ---------------------------------------------------------------------------------------------

    private ParsingExpression[] compileChildren(ParseTree tree)
    {
        return tree.children().stream()
            .map(this::compilePE)
            .toArray(ParsingExpression[]::new);
    }

    // ---------------------------------------------------------------------------------------------

    private ParsingExpression compilePE(ParseTree tree)
    {
        ParseTree child;
        ParsingExpression childPE;

        switch (tree.accessor)
        {
            case "choice":
                return choice(compileChildren(tree));

            case "sequence":
                return sequence(compileChildren(tree));

            case "and":
                return lookahead(compilePE(tree.child()));

            case "not":
                return not(compilePE(tree.child()));

            case "token":
                return token(compilePE(tree.child()));

            case "dumb":
                return dumb(compilePE(tree.child()));

            case "until":
                return until(
                    compilePE(tree.child(0)),
                    compilePE(tree.child(1)));

            case "aloUntil":
                return aloUntil(
                    compilePE(tree.child(0)),
                    compilePE(tree.child(1)));

            case "separated":
                return separated(
                    compilePE(tree.child(0)),
                    compilePE(tree.child(1)));

            case "aloSeparated":
                return aloSeparated(
                    compilePE(tree.child(0)),
                    compilePE(tree.child(1)));

            case "optional":
                return optional(compilePE(tree.child()));

            case "zeroMore":
                return zeroMore(compilePE(tree.child()));

            case "oneMore":
                return oneMore(compilePE(tree.child()));

            case "capture":
                child = tree.child(0);
                childPE = child.accessor.equals("marker") ? null : compilePE(child);
                return compileCapture(null, childPE, tree.group("captureSuffixes"));

            case "drop":
                return exprDropPrecedence(compilePE(tree.child()));

            case "ref":
                return compileRef(tree);

            case "any":
                return any();

            case "charRange":
                return charRange(
                    unescape(tree.value("first")).charAt(0),
                    unescape(tree.value("last")).charAt(0));

            case "charSet":
                return charSet(unescape(tree.value("charSet")));

            case "notCharSet":
                return notCharSet(unescape(tree.value("notCharSet")));

            case "stringLit":
                return literal(unescape(tree.value("literal")));

            default:
                error("Parsing expression with unknown name: %s", tree.accessor);
                return null; // unreachable
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private void error(String format, Object... items)
    {
        for (int i = 0; i < items.length; ++i)
        {
            if (items[i] instanceof ParseTree)
                items[i] = ((ParseTree) items[i]).nodeToString();
        }

        throw new RuntimeException(String.format(format, items));
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}

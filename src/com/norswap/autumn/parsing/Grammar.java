package com.norswap.autumn.parsing;

import com.norswap.autumn.Autumn;
import com.norswap.autumn.parsing.graph.NullabilityCalculator;
import com.norswap.autumn.parsing.source.Source;
import com.norswap.autumn.parsing.support.GrammarCompiler;
import com.norswap.autumn.parsing.support.GrammarGrammar;
import com.norswap.util.annotations.Immutable;
import com.norswap.util.graph_visit.GraphVisitor;
import com.norswap.util.slot.Slot;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * A user-facing representation of the grammar, which is the union of a parsing expression and some
 * options.
 * <p>
 * Convenient factory methods are available in class {@link Autumn}.
 */
public final class Grammar
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * The parsing expression that defines the grammar.
     */
    public final ParsingExpression root;

    /**
     * The rules contained within the grammar. These usually have a name, and usually include the
     * root (and sometimes the whitespace); but those are not absolute requirements. This is never
     * null but can be empty. The iteration order must be consistent.
     * <p>
     * For grammar created from grammar files, these are all the rules defined within the grammar
     * file (so there won't be a rule for the whitespace if the default whitespace specification is
     * used).
     */
    public final @Immutable Collection<ParsingExpression> rules;

    /**
     * The parsing expression to use as whitespace (used for whitespace expressions, and after
     * token expressions).
     */
    public final ParsingExpression whitespace;

    /**
     * Whether leading whitespace should be skipped when parsing.
     */
    public final boolean processLeadingWhitespace;

    /**
     * TODO
     */
    public final @Immutable Map<String, String> options;

    private Map<String, ParsingExpression> rulesByName;

    private NullabilityCalculator nullabilityCalculator;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    Grammar(
        ParsingExpression root,
        Collection<ParsingExpression> rules,
        ParsingExpression whitespace,
        boolean processLeadingWhitespace,
        Map<String, String> options)
    {
        this.root = root;
        this.rules = rules;
        this.whitespace = whitespace;
        this.processLeadingWhitespace = processLeadingWhitespace;
        this.options = options;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    // TODO EXCEPTIONS
    public static GrammarBuilder fromSource(Source source)
    {
        ParseResult result = Parser.parse(GrammarGrammar.grammar, source);

        if (!result.matched)
        {
            throw new ParseException(result.error);
        }
        else
        {
            return GrammarCompiler.compile(result.tree);
        }
    }

    // ---------------------------------------------------------------------------------------------

    public static GrammarBuilder fromRoot(ParsingExpression root)
    {
        return new GrammarBuilder(root);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Returns the rule with the given name, if any, null otherwise.
     */
    public ParsingExpression getRule(String name)
    {
        if (rulesByName == null)
        {
            rulesByName = new HashMap<>();

            rules.forEach(
            pe -> {
                String key = pe.name;

                if (key != null)
                {
                    rulesByName.put(key, pe);
                }
            });
        }

        return rulesByName.get(name);
    }

    // ---------------------------------------------------------------------------------------------

    public Grammar transform(GraphVisitor<ParsingExpression> visitor)
    {
        Slot<ParsingExpression> root2 = visitor.partialVisit(root);
        Collection<ParsingExpression> rules2 = visitor.partialVisit(rules);
        Slot<ParsingExpression> whitespace2 = visitor.partialVisit(whitespace);

        visitor.conclude();

        boolean rulesChanged = false;
        Iterator<ParsingExpression> it1 = rules.iterator();
        Iterator<ParsingExpression> it2 = rules2.iterator();

        while (it1.hasNext())
        {
            ParsingExpression oldRule = it1.next();
            ParsingExpression newRule = it2.next();

            if (oldRule != newRule)
            {
                rulesChanged = true;
                newRule.name = oldRule.name;
                oldRule.name = null;
            }
        }

        ParsingExpression root3 = root2.get();
        ParsingExpression whitespace3 = whitespace2.get();

        Grammar out = new Grammar(root3, rules2, whitespace3, processLeadingWhitespace, options);

        if (!rulesChanged)
        {
            out.rulesByName = rulesByName;
        }

        if (!rulesChanged && root == root3 && whitespace == whitespace3)
        {
            out.nullabilityCalculator = nullabilityCalculator;
        }

        return out;
    }

    // ---------------------------------------------------------------------------------------------

    public void compute(GraphVisitor<ParsingExpression> visitor)
    {
        visitor.partialVisit(root);
        visitor.partialVisit(rules);
        visitor.partialVisit(whitespace);
        visitor.conclude();
    }

    // ---------------------------------------------------------------------------------------------

    public boolean isNullable(ParsingExpression pe)
    {
        if (nullabilityCalculator == null)
        {
            throw new IllegalStateException(
                "Must call Grammar#computeNullability() before using Grammar#IsNullable.");
        }

        return nullabilityCalculator.isNullable(pe);
    }

    // ---------------------------------------------------------------------------------------------

    public boolean nullabilityComputed()
    {
        return nullabilityCalculator != null;
    }

    // ---------------------------------------------------------------------------------------------

    public void computeNullability()
    {
        nullabilityCalculator = new NullabilityCalculator();
        compute(nullabilityCalculator);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}

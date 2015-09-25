package com.norswap.autumn.parsing;

import com.norswap.autumn.parsing.graph.LeftRecursionDetector;
import com.norswap.autumn.parsing.graph.ReferenceResolver;
import com.norswap.autumn.parsing.graph.Replacer;
import com.norswap.autumn.parsing.graph.Walks;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

/**
 * Builder pattern for {@link Grammar}.
 */
public final class GrammarBuilder
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private final ParsingExpression root;

    private Collection<ParsingExpression> rules;

    private ParsingExpression whitespace;

    private boolean processLeadingWhitespace = true;

    private Map<String, String> options;

    private boolean leftRecursionElimination = true;

    private boolean referenceResolution = true;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    GrammarBuilder(ParsingExpression root)
    {
        this.root = root;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public GrammarBuilder rules(Collection<ParsingExpression> rules)
    {
        this.rules = rules;
        return this;
    }

    // ---------------------------------------------------------------------------------------------

    public GrammarBuilder whitespace(ParsingExpression whitespace)
    {
        this.whitespace = whitespace;
        return this;
    }

    // ---------------------------------------------------------------------------------------------

    public GrammarBuilder processLeadingWhitespace(boolean processLeadingWhitespace)
    {
        this.processLeadingWhitespace = processLeadingWhitespace;
        return this;
    }

    // ---------------------------------------------------------------------------------------------

    public void leftRecursionElimination(boolean leftRecursionElimination)
    {
        this.leftRecursionElimination = leftRecursionElimination;
    }

    // ---------------------------------------------------------------------------------------------

    public void referenceResolution(boolean referenceResolution)
    {
        this.referenceResolution = referenceResolution;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public Grammar build()
    {
        Grammar out = new Grammar(
            root,
            rules != null ? rules : Collections.emptyList(),
            whitespace != null ? whitespace : Whitespace.DEFAULT(),
            processLeadingWhitespace,
            options != null ? options : Collections.emptyMap());

        if (referenceResolution)
        {
            if (rules != null)
            {
                out = out.transform(new ReferenceResolver(Walks.inPlace));

            }
            else
            {
                System.err.println(out.root.toStringFull());
                out = out.transform(new ReferenceResolver());
                System.err.println("================");
                System.err.println(out.root.toStringFull());
            }
        }

        if (leftRecursionElimination)
        {
            out.computeNullability();

            LeftRecursionDetector detector = new LeftRecursionDetector(out);
            out.transform(detector);
            out = out.transform(new Replacer(detector.leftRecursives));
        }

        return out;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}

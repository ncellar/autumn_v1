package com.norswap.autumn.parsing;

import com.norswap.autumn.parsing.extensions.Extension;
import com.norswap.autumn.parsing.extensions.cluster.ClusterExtension;
import com.norswap.autumn.parsing.extensions.leftrec.LeftRecursionExtension;
import com.norswap.autumn.parsing.graph.ReferenceResolver;
import com.norswap.util.Array;
import com.norswap.util.graph.GraphVisitor;
import com.norswap.util.graph.Slot;

import java.util.Collection;
import java.util.Collections;

/**
 * Builder pattern for {@link Grammar}.
 */
public final class GrammarBuilder implements GrammarBuilderExtensionView
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private ParsingExpression root;

    private Collection<ParsingExpression> rules;

    private ParsingExpression whitespace;

    private boolean processLeadingWhitespace = true;

    private boolean defaultExtensions = true;

    private boolean referenceResolution = true;

    private final Array<Extension> extensions = new Array<>();

    ////////////////////////////////////////////////////////////////////////////////////////////////

    GrammarBuilder(ParsingExpression root)
    {
        this.root = root;
    }

    // ---------------------------------------------------------------------------------------------

    GrammarBuilder(Grammar grammar)
    {
        this.root = grammar.root;
        this.rules = grammar.rules;
        this.whitespace = grammar.whitespace;
        this.processLeadingWhitespace = grammar.processLeadingWhitespace;
        this.defaultExtensions = false;
        this.referenceResolution = false;
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

    public GrammarBuilder withExtension(Extension extension)
    {
        extensions.add(extension);
        return this;
    }

    // ---------------------------------------------------------------------------------------------

    public GrammarBuilder defaultExtensions(boolean defaultExtensions)
    {
        this.defaultExtensions = defaultExtensions;
        return this;
    }

    // ---------------------------------------------------------------------------------------------

    public GrammarBuilder referenceResolution(boolean referenceResolution)
    {
        this.referenceResolution = referenceResolution;
        return this;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public Grammar build()
    {
        rules = rules != null
            ? rules
            : Collections.emptyList();

        whitespace = whitespace != null
            ? whitespace
            : Whitespace.DEFAULT();

        if (referenceResolution)
        {
            transform(new ReferenceResolver());
        }

        if (defaultExtensions)
        {
            withExtension(new LeftRecursionExtension());
            withExtension(new ClusterExtension());
        }

        for (Extension extension: extensions)
        {
            extension.transform(this);
        }

        return new Grammar(root, rules, whitespace, processLeadingWhitespace, extensions);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void transform(GraphVisitor<ParsingExpression> visitor)
    {
        Slot<ParsingExpression> root2 = visitor.partialVisit(root);
        Array<Slot<ParsingExpression>> rules2 = visitor.partialVisit(rules);
        Slot<ParsingExpression> whitespace2 = visitor.partialVisit(whitespace);
        visitor.conclude();

        root = root2.latest();
        whitespace = whitespace2.latest();
        rules = rules2.map(Slot::latest);
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public void compute(GraphVisitor<ParsingExpression> visitor)
    {
        visitor.partialVisit(root);
        visitor.partialVisit(rules);
        visitor.partialVisit(whitespace);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}

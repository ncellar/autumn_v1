package com.norswap.autumn.parsing.extensions;

import com.norswap.autumn.parsing.Grammar;
import com.norswap.autumn.parsing.ParsingExpression;
import com.norswap.autumn.parsing.capture.ParseTree;
import java.util.Map;

/**
 * Enables extensions to extend the syntax of grammar files.
 * <p>
 * Currently two types of extensions are supported. First, custom top-level declarations, which take
 * the following form:
 * <pre>{@code
 * decl <name> <custom_syntax> ;
 * }</pre>
 * Note that the custom syntax cannot contain semicolons.
 * <p>
 * Second, custom expressions, which take one the following two forms:
 * <pre>{@code
 * 1) `name
 * 2) `<name> { <custom_syntax> }
 * }</pre>
 * Note that the custom syntax cannot contain closing braces.
 * <p>
 * A syntactic extension must supply the type of extension, the name, a parsing expression defining
 * the custom syntax part, and a compiler that is called whenever these custom constructs are
 * encountered. For custom expression, it returns a {@link ParsingExpression}.
 */
public interface GrammarSyntaxExtension
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * See {@link GrammarSyntaxExtension}.
     */
    enum Type { DECLARATION, EXPRESSION }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Returns the type of syntactic extension described. See {@link GrammarSyntaxExtension}.
     */
    Type type();

    // ---------------------------------------------------------------------------------------------

    /**
     * Returns a parsing expression describing the syntax of extension.
     */
    ParsingExpression syntax();

    // ---------------------------------------------------------------------------------------------

    /**
     * Returns the syntactic extension's name, which is used to mark its occurences in grammar files
     * (see {@link GrammarSyntaxExtension}.
     */
    String name();

    // ---------------------------------------------------------------------------------------------

    /**
     * Compiles the syntactic extension. The result depends on the type of extension: for custom
     * parsing expressions it must be a {@link ParsingExpression}, for declarations it is ignored.
     * <p>
     * To context object can be used to read/write information local to the parse. It is shared
     * with other objects, so keys should be uniquely prefixed.
     */
    Object compile(ParseTree tree, Map<String, Object> context);

    ////////////////////////////////////////////////////////////////////////////////////////////////
}

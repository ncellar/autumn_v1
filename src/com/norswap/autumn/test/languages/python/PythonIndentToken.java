package com.norswap.autumn.test.languages.python;

import com.norswap.autumn.parsing.Parser;
import com.norswap.autumn.parsing.ParsingExpression;
import com.norswap.autumn.parsing.graph.Nullability;
import com.norswap.autumn.parsing.state.ParseState;

public class PythonIndentToken extends ParsingExpression
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public PythonIndentToken(String name)
    {
        this.name = name;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void parse(Parser parser, ParseState state)
    {
        PythonState pstate = (PythonState) state.customStates[PythonExtension.INDEX];

        switch (name)
        {
            case "INDENT":
                if ( !pstate.newLineEmmitted
                &&   pstate.indent > pstate.oldIndent
                &&   pstate.newLinePos + pstate.indent * parser.source.tabSize == state.start)
                    pstate.oldIndent = pstate.indent;
                else
                    state.fail();
                break;

            case "DEDENT":
                if ( !pstate.newLineEmmitted
                &&   pstate.indent < pstate.oldIndent
                &&   pstate.newLinePos + pstate.indent * parser.source.tabSize == state.start)
                    -- pstate.oldIndent;
                else
                    state.fail();
                break;

            case "NEWLINE":
                if (pstate.newLineEmmitted)
                    pstate.newLineEmmitted = false;
                else
                    state.fail();
                break;

            case "START_LINE_JOINING":
                ++ pstate.lineJoining;
                break;

            case "END_LINE_JOINING":
                -- pstate.lineJoining;
                break;
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public Nullability nullability()
    {
        return Nullability.yes(this);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}

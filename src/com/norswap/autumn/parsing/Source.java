package com.norswap.autumn.parsing;

import com.norswap.util.Encoding;
import com.norswap.util.Strings;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;

/**
 * Contains the source text and associated meta-data.
 * <p>
 * The source text is available as a string whose final final character is 0 (handy to detect EOF).
 * <p>
 * Each source has an optional string identifier which is used to refer to it in textual output.
 * <p>
 * Users can configure how wide a tab character should appear (default: 4), and at which character
 * lines start (default: 0). Lines always start at 1.
 */
public final class Source
{
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public final String text;

    public final String identifier;

    public final int lineStart;

    public final int tabSize;

    private LineMap lineMap;

    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * The length of the source text, excluding the NUL terminator.
     */
    public int length()
    {
        return text.length() - 1;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    // private because text needs to be 0-terminated!

    private Source(String text, String identifier, int lineStart, int tabSize)
    {
        this.text = text;
        this.identifier = identifier;
        this.lineStart = lineStart;
        this.tabSize = tabSize;
    }

    // ---------------------------------------------------------------------------------------------

    public static Source fromFile(String filename) throws IOException
    {
        return fromFile(filename, Encoding.UTF_8, 0, 4);
    }

    // ---------------------------------------------------------------------------------------------

    public static Source fromFile(String filename, int lineStart, int tabSize) throws IOException
    {
        return fromFile(filename, Encoding.UTF_8, lineStart, tabSize);
    }

    // ---------------------------------------------------------------------------------------------

    public static Source fromFile(String filename, Charset charset, int lineStart, int tabSize)
    throws IOException
    {
        File file = new File(filename);
        BufferedInputStream stream = new BufferedInputStream(new FileInputStream(file));

        byte[] terminator = "\0".getBytes(charset);
        byte[] data = new byte[(int) file.length() + terminator.length];

        stream.read(data);
        stream.close();

        // EOF terminator
        System.arraycopy(terminator, 0, data, data.length - terminator.length, terminator.length);

        String string = new String(data, charset);
        string = string.replaceAll("\t", Strings.times(tabSize, " "));

        return new Source(string, filename, lineStart, tabSize);
    }

    // ---------------------------------------------------------------------------------------------

    public static Source fromString(String string)
    {
        return fromString(string, null, 0, 4);
    }

    // ---------------------------------------------------------------------------------------------

    public static Source fromString(String string, String identifier)
    {
        return fromString(string, identifier, 0, 4);
    }

    // ---------------------------------------------------------------------------------------------

    public static Source fromString(String string, String identifier, int lineStart, int tabSize)
    {
        string = string.replaceAll("\t", Strings.times(tabSize, " "));
        return new Source(string + '\0', identifier, lineStart, tabSize);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    public TextPosition position(int fileOffset)
    {
        if (lineMap == null)
        {
            lineMap = new LineMap(text);
        }

        return lineMap.positionFromOffset(fileOffset);
    }

    // ---------------------------------------------------------------------------------------------

    public int fileOffset(TextPosition position)
    {
        return fileOffset(position.line, position.column);
    }

    // ---------------------------------------------------------------------------------------------

    public int fileOffset(int line, int column)
    {
        if (lineMap == null)
        {
            lineMap = new LineMap(text);
        }

        return lineMap.offset(line, column, lineStart);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Prints a position, prefixed by this source's identifier.
     */
    String posToString(TextPosition position)
    {
        return position.toString() + " in source \"" + identifier + "\"";
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}

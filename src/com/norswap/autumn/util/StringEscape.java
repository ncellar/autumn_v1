package com.norswap.autumn.util;

public final class StringEscape
{
    /**
     * Returns a copy of string in which the escapable java characters have been replaced by their
     * escaped representation (\t, \b, \n, \r, \f, \', \" and \\).
     *
     * This does not insert octal or hexadecimal escapes for other non-printable characters.
     */
    public static String escape(String string)
    {
        StringBuilder str = new StringBuilder(string.length());

        for (char c: string.toCharArray())
        {
            switch (c)
            {
                case '\t':
                    str.append("\\t");
                    break;

                case '\b':
                    str.append("\\b");
                    break;

                case '\n':
                    str.append("\\n");
                    break;

                case '\r':
                    str.append("\\r");
                    break;

                case '\f':
                    str.append("\\f");
                    break;

                case '\'':
                    str.append("\\\'");
                    break;

                case '\"':
                    str.append("\\\"");
                    break;

                case '\\':
                    str.append("\\\\");
                    break;

                default:
                    str.append(c);
            }
        }

        return str.toString();
    }
}

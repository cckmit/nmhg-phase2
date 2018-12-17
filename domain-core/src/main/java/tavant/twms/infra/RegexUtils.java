/*
 *   Copyright (c)2006 Tavant Technologies
 *   All Rights Reserved.
 *
 *   This software is furnished under a license and may be used and copied
 *   only  in  accordance  with  the  terms  of such  license and with the
 *   inclusion of the above copyright notice. This software or  any  other
 *   copies thereof may not be provided or otherwise made available to any
 *   other person. No title to and ownership of  the  software  is  hereby
 *   transferred.
 *
 *   The information in this software is subject to change without  notice
 *   and  should  not be  construed as a commitment  by Tavant Technologies.
 */

/**
 * User: <a href="mailto:vikas.sasidharan@tavant.com>Vikas Sasidharan</a>
 * Date: Jun 27, 2007
 * Time: 3:12:07 PM
 */

package tavant.twms.infra;

import org.springframework.util.StringUtils;

import java.util.HashSet;
import java.util.Set;

public class RegexUtils {

    public static final Set<Character> REGEX_CHARS = new HashSet<Character>(20);

    static {
        // grouping chars
        REGEX_CHARS.add('[');
        REGEX_CHARS.add(']');
        REGEX_CHARS.add('(');
        REGEX_CHARS.add(')');

        // count chars
        REGEX_CHARS.add('{');
        REGEX_CHARS.add('}');
        REGEX_CHARS.add('?');
        REGEX_CHARS.add('*');
        REGEX_CHARS.add('+');

        // range chars
        REGEX_CHARS.add(',');
        REGEX_CHARS.add('-');

        // logical chars
        REGEX_CHARS.add('|');
        REGEX_CHARS.add('&');

        // others
        REGEX_CHARS.add('^');
        REGEX_CHARS.add('$');
        REGEX_CHARS.add('\\');
        REGEX_CHARS.add('.');
    }

    public boolean isRegexChar(char ch) {
        return REGEX_CHARS.contains(ch);
    }

    public boolean isRegexChar(String str) {
        return REGEX_CHARS.contains(str.charAt(0));
    }

    public String escape(char ch) {

        if (isRegexChar(ch)) {
            return new StringBuffer(3)
                    .append("\\")
                    .append(ch)
                    .toString();
        } else {
            return String.valueOf(ch);
        }
    }

    public String escape(String pattern) {

        if(!StringUtils.hasText(pattern)) {
            return pattern;
        }

        StringBuffer escapedString = new StringBuffer(pattern.length() + 10);

        for(char ch : pattern.toCharArray()) {
            escapedString.append(escape(ch));
        }

        return escapedString.toString();
    }
}

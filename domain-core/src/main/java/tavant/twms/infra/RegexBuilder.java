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
 * Time: 3:52:05 PM
 */

package tavant.twms.infra;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.springframework.util.StringUtils;

import java.text.MessageFormat;
import java.util.regex.Pattern;

public class RegexBuilder {

    private StringBuffer buffer;
    private boolean matchWhole;
    private RegexUtils regexUtils = new RegexUtils();

    public RegexBuilder() {
        this(false);
    }

    public RegexBuilder(boolean matchWhole) {
        buffer = new StringBuffer(100);

        this.matchWhole = matchWhole;

        if(matchWhole) {
            buffer.append("^");
        }
    }

    public RegexBuilder numbers() {
        return appendPattern("\\d");
    }

    public RegexBuilder anyNumberOfChars() {
        return appendPattern(".*");
    }

    public RegexBuilder anyCharsOtherThan(String chars) {
        buffer.append("[^");
        buffer.append(regexUtils.escape(chars));
        buffer.append("]");

        return this;
    }

    public RegexBuilder oneOrMoreAlphabets() {
        return alphabets().occurOnceOrMore();    
    }

    public RegexBuilder oneOrMoreNumbers() {
        return numbers().occurOnceOrMore();
    }

    public RegexBuilder occurZeroOrMoreTimes() {
        return appendPattern("*");
    }

    public RegexBuilder occurOnce() {
        return occur(1);
    }

    public RegexBuilder occurOnceOrMore() {
        buffer.append("+");
        return this;
    }

    public RegexBuilder occurAtmostOnce() {
        buffer.append("?");
        return this;
    }

    public RegexBuilder occur(int count) {
        buffer.append("{");
        buffer.append(count);
        buffer.append("}");

        return this;
    }

    public RegexBuilder occurAtleast(int count) {
        buffer.append("{");
        buffer.append(count);
        buffer.append(",}");

        return this;
    }

    public RegexBuilder occurAtmost(int count) {
        buffer.append("{,");
        buffer.append(count);
        buffer.append("}");

        return this;
    }

    public RegexBuilder occurWithinRange(int min, int max) {
        buffer.append("{");
        buffer.append(min);
        buffer.append(",");
        buffer.append(max);
        buffer.append("}");

        return this;
    }

    public RegexBuilder lowerCaseAlphabets() {
        return appendPattern("[a-z]");
    }

    public RegexBuilder upperCaseAlphabets() {
        return appendPattern("[A-Z]");
    }

    public RegexBuilder alphabets() {
        return appendPattern("[a-zA-Z]");
    }

    public RegexBuilder alphaNumeric() {
        return appendPattern("[a-zA-Z0-9]");
    }

    public RegexBuilder whiteSpace() {
        return appendPattern("\\s");
    }

    public RegexBuilder pattern(String pattern) {
        return appendPattern(regexUtils.escape(pattern));
    }

    public RegexBuilder anyChar() {
        return appendPattern(".");
    }

    public RegexBuilder text(String text) {
        return appendPattern("\\Q" + text + "\\E");
    }

    public RegexBuilder startGroup() {
        return appendPattern("(");
    }

    public RegexBuilder endGroup() {
        return appendPattern(")");
    }

    public RegexBuilder or() {
        return appendPattern("|");
    }

    public RegexBuilder and() {
        return appendPattern("&");
    }

    protected RegexBuilder end() {
        if(matchWhole) {
            buffer.append("$");
        }

        return this;
    }

    public Pattern compile() {
        end();
        
        String pattern = buffer.toString();

        if (!StringUtils.hasText(pattern)) {
            throw new RuntimeException("Cannot compile blank pattern.");
        }

        validatePattern();

        return Pattern.compile(buffer.toString());
    }

    private void validatePattern() {
        String pattern = buffer.toString();
        char[] patternAsCharArray = pattern.toCharArray();

        int incompleteParantheses = 0;
        int incompleteCurlyBraces = 0;
        int incompleteSquareBrackets = 0;

        boolean withinQuotes = false;
        char lastSeenChar = '\0';

        for (char ch : patternAsCharArray) {
            boolean escapedChar = ('\\' == lastSeenChar);
            
            switch (ch) {
                case'Q':
                    withinQuotes = escapedChar;
                    break;
                case'E':
                    withinQuotes = !(withinQuotes && escapedChar);
                    break;
                case'(':
                    if (!(withinQuotes || escapedChar)) {
                        incompleteParantheses++;
                    }
                    break;
                case'{':
                    if (!(withinQuotes || escapedChar)) {
                        incompleteCurlyBraces++;
                    }
                    break;
                case'[':
                    if (!(withinQuotes || escapedChar)) {
                        incompleteSquareBrackets++;
                    }
                    break;
                case')':
                    if (!(withinQuotes || escapedChar)) {
                        incompleteParantheses--;
                    }
                    break;
                case'}':
                    if (!(withinQuotes || escapedChar)) {
                        incompleteCurlyBraces--;
                    }
                    break;
                case']':
                    if (!(withinQuotes || escapedChar)) {
                        incompleteSquareBrackets--;
                    }
                    break;
            }

            lastSeenChar = ch;
        }

        validateGrouping(pattern, incompleteParantheses, "parantheses");
        validateGrouping(pattern, incompleteCurlyBraces, "curly braces");
        validateGrouping(pattern, incompleteSquareBrackets, "square brackets");
    }

    private void validateGrouping(String pattern, int offset,
                                  String groupType) {

        if (offset == 0) {
            return;
        }

        String openOrClose = (offset > 0) ? "left" : "right";
        int absoluteOffset = Math.abs(offset);

        String errorMessage = "Invalid pattern. There are {0} unmatched " +
                "{1} " + groupType + " in the pattern : ";

        String formattedErrorMessage =
                MessageFormat.format(errorMessage, absoluteOffset, openOrClose);
        throw new RuntimeException(formattedErrorMessage + pattern);
    }

    protected RegexBuilder appendPattern(String pattern) {
        buffer.append(pattern);

        return this;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("pattern", buffer)
                .toString();
    }
}

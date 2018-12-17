package tavant.twms.infra;

import junit.framework.TestCase;

/**
 * RegexUtils Tester.
 *
 * @author <a href="mailto:vikas.sasidharan@tavant.com>Vikas Sasidharan</a>
 * @since <pre>06/27/2007</pre>
 * @version 1.0
 */
public class RegexUtilsTest extends TestCase {

    private RegexUtils fixture = new RegexUtils();

    public void testEscapeForNormalChar() {
        assertEquals("c", fixture.escape('c'));
    }

    public void testEscapeForSpecialChar() {
        assertEquals("\\\\", fixture.escape('\\'));
    }

    public void testIsRegexCharForAllRegexChars() {
        for(char ch : RegexUtils.REGEX_CHARS) {
            assertTrue(fixture.isRegexChar(ch));
        }
    }

    public void testIsRegexCharForNonRegexChars() {
        for(char ch = 'a'; ch <= 'z'; ch++) {
            assertFalse(fixture.isRegexChar(ch));
        }

        assertFalse(fixture.isRegexChar('0'));
        assertFalse(fixture.isRegexChar('9'));
    }

    public void testEscapeForStringContainingRegex() {
        assertEquals("\\$\\{foo\\.bar\\}", fixture.escape("${foo.bar}"));
    }

    public void testEscapeForStringNotContainingRegex() {
        String regex = "~!@#%=foo90";

        assertEquals(regex, fixture.escape(regex));
    }
}

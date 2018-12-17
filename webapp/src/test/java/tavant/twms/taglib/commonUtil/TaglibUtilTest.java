package tavant.twms.taglib.commonUtil;

import junit.framework.TestCase;
import tavant.twms.taglib.TaglibUtil;

public class TaglibUtilTest extends TestCase {

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testSeperateMarkupAndScriptWithOnlyMarkup() {
        String testInput = "<tr><td>foo</td></tr>";        
        String[] result = TaglibUtil.seperateMarkupAndScript(testInput,false);
        assertEquals(2, result.length);
        assertEquals(testInput, result[0]);
    }

    public void testSeperateMarkupAndScriptWithMarkupHavingSpaceAndNewLines() {
        String testInput = " <tr>\n\t" +
            " <td>\n\t\tSomeValue\n\t</td>\n\t" +
            " <td>\n\t\tSomeOtherValue\n\t</td>\n\n" +
            "</tr>";
        String expectedOutput = "<tr><td>SomeValue</td><td>SomeOtherValue</td></tr>";
        String[] result = TaglibUtil.seperateMarkupAndScript(testInput,false);
        assertEquals(expectedOutput, result[0]);
        assertEquals("", result[1]);
    }

    public void testSeperateMarkupAndScriptWithMarkupAndScript() {
        String testInput = "<tr><td colspan=\"2\">Foo</td></tr>" +
            "<script type=\"text/javascript\">function someName() {\n" +
            "var i = 0;\n alert(\"Foo\");\n" +
            "}</script>";
        String expectedMarkup = "<tr><td colspan=\\\"2\\\">Foo</td></tr>";
        String expectedScript = "function someName() {" +
            "var i = 0;alert(\\\"Foo\\\");}";
        String[] result = TaglibUtil.seperateMarkupAndScript(testInput,false);
        assertEquals(expectedMarkup, result[0]);
        assertEquals(expectedScript, result[1]);
    }

    public void testSeperateMarkupAndScriptWithMultipleMarkupAndScriptSegments() {
        String testInput = "<tr><td>Foo</td></tr>" +
            "<script type=\"text/javascript\">function someName() {\n" +
            "var i = 0;\n alert(\"Foo\");\n" +
            "}</script>\n" + 
            "<tr><td>Bar</td></tr>" + 
            "<script type=\"text/javascript\">function someName() {\n" +
            "var i = 0;\n alert(\"BAR\");\n" +
            "}</script>\n";
        String expectedMarkup = "<tr><td>Foo</td></tr><tr><td>Bar</td></tr>";
        String expectedScript = "function someName() {" +
            "var i = 0;alert(\\\"Foo\\\");}" + 
            "function someName() {var i = 0;alert(\\\"BAR\\\");}";
        String[] result = TaglibUtil.seperateMarkupAndScript(testInput,false);
        assertEquals(expectedMarkup, result[0]);
        assertEquals(expectedScript, result[1]);
    }

    public void testEscapeInvertedCommasAndNewLines() {
        String testInput = "function someName() {\n" +
            "\tvar i = 0;\n\t alert(\"BAR\");\n" +
            "}\n";
        String expectedScript = "function someName() {var i = 0;alert(\\\"BAR\\\");}";
        String result = TaglibUtil.escapeInvertedCommasAndNewLines(testInput);
        assertEquals(expectedScript, result);
    }
}

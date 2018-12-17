package tavant.twms.infra;

import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: vikas.sasidharan
 * Date: 22 May, 2008
 * Time: 8:57:19 PM
 */
public class OracleDateLikeSQLFunctionTest extends TestCase {

    private OracleDateLikeSQLFunction fixture = new OracleDateLikeSQLFunction("datelike");

    public void testRenderForPartialDay() {
        String queryString = fixture.render(getArgsArray("/7"), null);
        assertEquals("to_char(foo, 'dd') like '7%' and 9999", queryString);
    }

    public void testRenderForCompleteDay() {
        String queryString = fixture.render(getArgsArray("/17"), null);
        assertEquals("to_char(foo, 'dd') = '17' and 9999", queryString);

        queryString = fixture.render(getArgsArray("/1/"), null);
        assertEquals("to_char(foo, 'dd') = '1' and 9999", queryString);
    }

    public void testRenderForPartialMonth() {
        String queryString = fixture.render(getArgsArray("7"), null);
        assertEquals("to_char(foo, 'mm') like '7%' and 9999", queryString);
    }

    public void testRenderForCompleteMonth() {
        String queryString = fixture.render(getArgsArray("17"), null);
        assertEquals("to_char(foo, 'mm') = '17' and 9999", queryString);

        queryString = fixture.render(getArgsArray("1/"), null);
        assertEquals("to_char(foo, 'mm') = '1' and 9999", queryString);
    }

    public void testRenderForCompleteMonthAndPartialDay() {
        String queryString = fixture.render(getArgsArray("7/7"), null);
        assertEquals("to_char(foo, 'mm-dd') like '7-7%' and 9999", queryString);
    }

    public void testRenderForCompleteMonthAndDay() {
        String queryString = fixture.render(getArgsArray("7/17"), null);
        assertEquals("to_char(foo, 'mm-dd') = '7-17' and 9999", queryString);

        queryString = fixture.render(getArgsArray("7/1/"), null);
        assertEquals("to_char(foo, 'mm-dd') = '7-1' and 9999", queryString);
    }

    public void testRenderForPartialYear() {
        String queryString = fixture.render(getArgsArray("//200"), null);
        assertEquals("to_char(foo, 'yyyy') like '200%' and 9999", queryString);
    }

    public void testRenderForCompleteYear() {
        String queryString = fixture.render(getArgsArray("//2017"), null);
        assertEquals("to_char(foo, 'yyyy') = '2017' and 9999", queryString);
    }

    public void testRenderForCompleteMonthAndPartialYear() {
        String queryString = fixture.render(getArgsArray("7//20"), null);
        assertEquals("to_char(foo, 'mm-yyyy') like '7-20%' and 9999", queryString);
    }

    public void testRenderForCompleteMonthAndCompleteYear() {
        String queryString = fixture.render(getArgsArray("7//2017"), null);
        assertEquals("to_char(foo, 'mm-yyyy') = '7-2017' and 9999", queryString);
    }

    public void testRenderForCompleteDayAndPartialYear() {
        String queryString = fixture.render(getArgsArray("/7/20"), null);
        assertEquals("to_char(foo, 'dd-yyyy') like '7-20%' and 9999", queryString);
    }

    public void testRenderForCompleteDayAndCompleteYear() {
        String queryString = fixture.render(getArgsArray("/7/2017"), null);
        assertEquals("to_char(foo, 'dd-yyyy') = '7-2017' and 9999", queryString);
    }

    public void testRenderForCompleteDayCompleteMonthAndPartialYear() {
        String queryString = fixture.render(getArgsArray("7/7/20"), null);
        assertEquals("to_char(foo, 'mm-dd-yyyy') like '7-7-20%' and 9999", queryString);
    }

    public void testRenderForCompleteDayCompleteMonthAndCompleteYear() {
        String queryString = fixture.render(getArgsArray("7/7/2017"), null);
        assertEquals("to_char(foo, 'mm-dd-yyyy') = '7-7-2017' and 9999", queryString);
    }

    @SuppressWarnings("unchecked")
    public List getArgsArray(String dateInput) {
        List args = new ArrayList();
        args.add("foo");
        args.add(dateInput);

        return args;
    }
}
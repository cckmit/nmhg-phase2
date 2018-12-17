package tavant.twms.infra;

import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: vikas.sasidharan
 * Date: 22 May, 2008
 * Time: 8:58:01 PM
 */
public class MySQLDateLikeSQLFunctionTest extends TestCase {

    private MySQLDateLikeSQLFunction fixture = new MySQLDateLikeSQLFunction("datelike");

    public void testRenderForPartialDay() {
        String queryString = fixture.render(getArgsArray("/7"), null);
        assertEquals("day(foo) like '7%' and 9999", queryString);
    }

    public void testRenderForPartialStartsWithZeroDay() {
        String queryString = fixture.render(getArgsArray("/0"), null);
        assertEquals("length(day(foo)) = 1 and 9999", queryString);
    }

    public void testRenderForCompleteStartsWithZeroDay() {
        String queryString = fixture.render(getArgsArray("/07"), null);
        assertEquals("day(foo) = '7' and 9999", queryString);
    }

    public void testRenderForCompleteDay() {
        String queryString = fixture.render(getArgsArray("/17"), null);
        assertEquals("day(foo) = '17' and 9999", queryString);

        queryString = fixture.render(getArgsArray("/1/"), null);
        assertEquals("day(foo) = '1' and 9999", queryString);
    }

    public void testRenderForPartialMonth() {
        String queryString = fixture.render(getArgsArray("7"), null);
        assertEquals("month(foo) like '7%' and 9999", queryString);
    }

    public void testRenderForPartialStartsWithZeroMonth() {
        String queryString = fixture.render(getArgsArray("0"), null);
        assertEquals("length(month(foo)) = 1 and 9999", queryString);
    }

    public void testRenderForCompleteStartsWithZeroMonth() {
        String queryString = fixture.render(getArgsArray("07"), null);
        assertEquals("month(foo) = '7' and 9999", queryString);
    }

    public void testRenderForCompleteMonth() {
        String queryString = fixture.render(getArgsArray("17"), null);
        assertEquals("month(foo) = '17' and 9999", queryString);

        queryString = fixture.render(getArgsArray("1/"), null);
        assertEquals("month(foo) = '1' and 9999", queryString);
    }

    public void testRenderForCompleteMonthAndPartialDay() {
        String queryString = fixture.render(getArgsArray("7/7"), null);
        assertEquals("month(foo) = '7' and day(foo) like '7%' and 9999", queryString);
    }

    public void testRenderForCompleteMonthAndPartialStartsWithZeroDay() {
        String queryString = fixture.render(getArgsArray("7/0"), null);
        assertEquals("month(foo) = '7' and length(day(foo)) = 1 and 9999", queryString);
    }

    public void testRenderForCompleteMonthAndCompleteDay() {
        String queryString = fixture.render(getArgsArray("7/17"), null);
        assertEquals("month(foo) = '7' and day(foo) = '17' and 9999", queryString);

        queryString = fixture.render(getArgsArray("7/1/"), null);
        assertEquals("month(foo) = '7' and day(foo) = '1' and 9999", queryString);
    }

    public void testRenderForCompleteMonthAndCompleteStartsWithZeroDay() {
        String queryString = fixture.render(getArgsArray("7/07"), null);
        assertEquals("month(foo) = '7' and day(foo) = '7' and 9999", queryString);
    }

    public void testRenderForPartialYear() {
        String queryString = fixture.render(getArgsArray("//200"), null);
        assertEquals("year(foo) like '200%' and 9999", queryString);
    }

    public void testRenderForCompleteYear() {
        String queryString = fixture.render(getArgsArray("//2017"), null);
        assertEquals("year(foo) = '2017' and 9999", queryString);
    }

    public void testRenderForCompleteMonthAndPartialYear() {
        String queryString = fixture.render(getArgsArray("7//20"), null);
        assertEquals("month(foo) = '7' and year(foo) like '20%' and 9999", queryString);
    }

    public void testRenderForCompleteMonthAndCompleteYear() {
        String queryString = fixture.render(getArgsArray("7//2017"), null);
        assertEquals("month(foo) = '7' and year(foo) = '2017' and 9999", queryString);
    }

    public void testRenderForCompleteDayAndPartialYear() {
        String queryString = fixture.render(getArgsArray("/7/20"), null);
        assertEquals("day(foo) = '7' and year(foo) like '20%' and 9999", queryString);
    }

    public void testRenderForCompleteDayAndCompleteYear() {
        String queryString = fixture.render(getArgsArray("/7/2017"), null);
        assertEquals("day(foo) = '7' and year(foo) = '2017' and 9999", queryString);
    }

    public void testRenderForCompleteDayCompleteMonthAndPartialYear() {
        String queryString = fixture.render(getArgsArray("7/7/20"), null);
        assertEquals("month(foo) = '7' and day(foo) = '7' and year(foo) like '20%' and 9999", queryString);
    }

    public void testRenderForCompleteDayCompleteMonthAndCompleteYear() {
        String queryString = fixture.render(getArgsArray("7/7/2017"), null);
        assertEquals("month(foo) = '7' and day(foo) = '7' and year(foo) = '2017' and 9999", queryString);
    }

    @SuppressWarnings("unchecked")
    public List getArgsArray(String dateInput) {
        List args = new ArrayList();
        args.add("foo");
        args.add(dateInput);

        return args;
    }
}
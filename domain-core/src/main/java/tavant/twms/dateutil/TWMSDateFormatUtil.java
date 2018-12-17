package tavant.twms.dateutil;

import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Pattern;

import com.domainlanguage.time.CalendarDate;

import tavant.twms.security.authz.infra.SecurityHelper;

public class TWMSDateFormatUtil {

    public static final String DEFAULT_DATE_PATTERN = "MM/dd/yyyy";

    public static final String DATE_PATTERN_NL_IT_ES_GB = "dd/MM/yyyy";//Date pattern for Dutch,Italy,Spain,GB

    public static final String DATE_PATTERN_FR_DE = "dd.MM.yyyy";//Date pattern for French/German

    public static final String DATE_FORMAT_SQL_DD_MMM_YYYY = "'DD-Mon-YYYY'";

    public static final String DATE_FORMAT_CALENDAR_DD_MMM_YYYY = "dd-MMM-yyyy";

    private static String[] DATEFORMATCOMPONENTS_DEFAULT = new String[] { "mm",
            "dd", "yyyy" };

    private static String[] DATEFORMATCOMPONENTS_OTHERTHANUS = new String[] { "dd",
            "mm", "yyyy" };

    public static final Pattern EMPTY_COMPONENT_PADDER_PATTERN = Pattern
            .compile("[/\\.]");



    public static String getDateFormatForLoggedInUser() {
        Locale locale = new SecurityHelper().getLoggedInUser().getLocale();

        if (locale != null) {
            if (locale.toString().equalsIgnoreCase("it_IT")
                    || locale.toString().equalsIgnoreCase("nl_NL")
                    || locale.toString().equalsIgnoreCase("es_ES")
                    || locale.toString().equalsIgnoreCase("en_GB")) {
                return DATE_PATTERN_NL_IT_ES_GB;
            } else if (locale.toString().equalsIgnoreCase("fr_FR")
                    || locale.toString().equalsIgnoreCase("de_DE")) {
                return DATE_PATTERN_FR_DE;
            }

        }

        return DEFAULT_DATE_PATTERN;
    }

    public static String getJSCalendarDateFormatForLoggedInUser() {
        String jsDatePattern = getDateFormatForLoggedInUser();
        jsDatePattern=jsDatePattern.replaceAll("MM", "%m");
        jsDatePattern=jsDatePattern.replaceAll("dd", "%d");
        jsDatePattern=jsDatePattern.replaceAll("yyyy", "%Y");
        return jsDatePattern;
    }

    public static String[] getDateFormatComponentsForLoggedInUser() {
        Locale locale = new SecurityHelper().getLoggedInUser().getLocale();

        if (locale != null) {
            if (locale.toString().equalsIgnoreCase("en_US")) {
                return DATEFORMATCOMPONENTS_DEFAULT;
            }
        }

        return DATEFORMATCOMPONENTS_OTHERTHANUS;
    }

    public static String getDateSeparatorForLoggedInUser() {
        Locale locale = new SecurityHelper().getLoggedInUser().getLocale();

        if (locale != null) {
            if (locale.toString().equalsIgnoreCase("en_US")) {
                return "/";
            }
        }

        return "\\.";
    }

    public static String getDayNumberInYear() {

        String dayNumber = new Integer(
                new GregorianCalendar(TimeZone.getTimeZone("GMT+0"))
                        .get(GregorianCalendar.DAY_OF_YEAR)).toString();
        if (dayNumber.length() == 1) {
            return "00" + dayNumber;

        } else if (dayNumber.length() == 2) {
            return "0" + dayNumber;
        } else {
            return dayNumber;
        }
    }

    public static String formatCalendarDate(CalendarDate date, String dateFormat) {
    	return date.toString(dateFormat);
    }
}

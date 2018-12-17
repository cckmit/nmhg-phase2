package tavant.twms.taglib;

import com.opensymphony.xwork2.TextProvider;
import com.opensymphony.xwork2.util.ValueStack;
import org.apache.log4j.Logger;
import tavant.twms.action.ThemeProvider;
import static tavant.twms.action.ThemeProvider.DEFAULT_CSS_THEME_KEY;
import static tavant.twms.config.UiConfReader.getPropertyValue;
import tavant.twms.taglib.summaryTable.SummaryTableColumnData;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;

/**
 * This is a utility class for twms tag library.
 *
 * @author janmejay.singh
 */
public class TaglibUtil {

    public static final String TWMS_TAG_RECORDS = "TWMS_TAG_RECORDS";
    public static final String SESSION_LOCALE_ATTRIBUTE_NAME = "WW_TRANS_I18N_LOCALE";//FIXME: externalize this...
    public static final String TWMS_REPEAT_TABLE_ID = "TWMS_REPEAT_TABLE_ID";
    public static final String SUMMARY_TABLE = "SUMMARY_TABLE";
    public static final String SUMMARY_TABLE_COLUMN_LIST = "SUMMARY_TABLE_COLUMNS";
    public static final String BLANK_FTL_NAME = "twms_blank_template";

    private static final String CSS_THEME_BASE_DIR = "css.theme.baseDir";

    private static final String COMMON_CSS_BASE_DIR = "css.common.baseDir";

    //IE detection
    public static final String USER_AGENT = "User-Agent",
            IE_CONSTANT = "MSIE";

    public static Logger logger = Logger.getLogger(TaglibUtil.class);

    /**
     * Takes servlet request and tag component's class name, and returns true or false, depending on weather the
     * tag has been used before or not.
     *
     * @param request
     * @param tagComponent
     * @return boolean
     */
    @SuppressWarnings("unchecked")
    public static boolean isUsedBefore(HttpServletRequest request, Class tagComponent) {

        List<Class> tagsUsed = (List<Class>) request.getAttribute(TWMS_TAG_RECORDS);

        if(tagsUsed == null) {//It won't be there the first time... so adding a new one...
            tagsUsed = new ArrayList<Class>();
            request.setAttribute(TWMS_TAG_RECORDS, tagsUsed);
        }

        if(tagsUsed.contains(tagComponent)) {//it is there means tag was used before
            return true;
        }
        //else it wasn't used before
        tagsUsed.add(tagComponent);
        return false;
    }

    public static String getDojoLocale(HttpServletRequest request) {
        Locale locale = (Locale)request.getSession().getAttribute(SESSION_LOCALE_ATTRIBUTE_NAME);
        //IMP: i m not fully sure... if this thing will work in all the wierdo cases... (havn't tested it.. on different languages).
        return locale.toString().replace('_', '-').toLowerCase();//dojo locale uses - insteed of _ and uses lower case for country specifier.
    }

    public static boolean isIE(HttpServletRequest request) {
        boolean answer = Boolean.FALSE;
        if(request.getHeader(USER_AGENT).indexOf(IE_CONSTANT) > -1) {
            answer = Boolean.TRUE;
        }
        return answer;
    }

    public static List<String> splitBasedOnComma(String baseString) {
        List<String> list = new ArrayList<String>();
        String[] strings = baseString.split(",");
        for (String string : strings) {
            if (string != null) {
                list.add(string.trim());
            } else {
                throw new IllegalArgumentException("The value " + baseString +
                        " is not a legal comma seprated list of Ids.");
            }
        }
        return list;
    }

    public static String getRepeatTableId(HttpServletRequest request) {
        return (String)request.getAttribute(TWMS_REPEAT_TABLE_ID);
    }

    public static void setRepeatTableId(HttpServletRequest request, String id) {
        request.setAttribute(TWMS_REPEAT_TABLE_ID, id);
    }

    public static String[] seperateMarkupAndScript(String body, boolean replaceAddOnLoads) {
        body = body.trim();
        //
        // TODO: There are probably much more elegant methods with RegExs to get this done
        // Writing this way now to get going and not tussling with RegExs.
        //
        String scripts = "";
        String scriptStart = "<script type=\"text/javascript\">";
        String scriptEnd = "</script>";
        while (true) {
            int start = body.indexOf(scriptStart);
            if (start == -1) break;
            int end = body.indexOf(scriptEnd, start);
            if (end == -1) throw new IllegalArgumentException("<script> not properly closed in [" + body + "]");
            if (start > end) throw new IllegalArgumentException("incorrect nesting of <script> in [" + body + "]");

            String pre = body.substring(0, start);
            // Escape the "/" in the </script> of any dojo 1.x "dojo/..." scripts present inside the body.
            pre = pre.replaceAll("</script", "<\\\\/script");

            String script = body.substring(start + scriptStart.length(), end);
            if(replaceAddOnLoads) {
                script = script.replaceAll("dojo\\.addOnLoad", "twms\\.repeattable\\.addOnRowAdd");
            }
            
            scripts += script;
            String post = body.substring(end+scriptEnd.length());
            body = pre + post;
        }

        return new String[]{escapeInvertedCommasAndNewLines(body), escapeInvertedCommasAndNewLines(scripts)};
    }

    public static String escapeInvertedCommasAndNewLines(String source) {
        source = source.replaceAll("\"", "\\\\\"");
        StringTokenizer st = new StringTokenizer(source, "\n");
        String result = "";
        while (st.hasMoreTokens()) {
            result += st.nextToken().trim();
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    public static void setSummaryTableId(ValueStack stack, String summaryTableId) {
        stack.getContext().put(SUMMARY_TABLE, summaryTableId);
    }

    public static String getSummaryTableId(ValueStack stack) {
        return (String) stack.getContext().get(SUMMARY_TABLE);
    }

    @SuppressWarnings("unchecked")
    public static void addSummaryTableColumn(ValueStack stack, SummaryTableColumnData column) {
        List<SummaryTableColumnData> summaryTableColumns = (List<SummaryTableColumnData>) stack.getContext().get(SUMMARY_TABLE_COLUMN_LIST);
        if(summaryTableColumns == null) {
            summaryTableColumns = new ArrayList<SummaryTableColumnData>();
            stack.getContext().put(SUMMARY_TABLE_COLUMN_LIST, summaryTableColumns);
        }
        summaryTableColumns.add(column);
    }

    @SuppressWarnings("unchecked")
    public static List<SummaryTableColumnData> getSummaryTableColumns(ValueStack stack) {
        return (List<SummaryTableColumnData>) stack.getContext().get(SUMMARY_TABLE_COLUMN_LIST);
    }

    public static String geti18NVal(ValueStack stack, String key) {
        for(Object obj : stack.getRoot()) {
            if(obj instanceof TextProvider) {
                TextProvider tp = (TextProvider) obj;
                return tp.getText(key);
            }
        }
        return key;
    }

    public static int getInt(Object obj) {
        if(obj != null && obj instanceof Integer) {
            return (Integer)obj;
        }
        return 0;
    }

    public static double getDouble(Object obj) {
        if(obj != null && obj instanceof Double) {
            return (Double)obj;
        }
        return 0;
    }

    public static boolean getBoolean(Object obj) {
        return obj != null && obj instanceof Boolean && (Boolean) obj;
    }

    public static String getCssTheme(ValueStack stack) {
        Object actionBean = stack.peek();
        if(actionBean instanceof ThemeProvider) {
            return ((ThemeProvider)actionBean).getCssTheme();
        }
        return getPropertyValue(DEFAULT_CSS_THEME_KEY);
    }

    public static String getCssThemeBaseDir() {
        return getPropertyValue(CSS_THEME_BASE_DIR);
    }

    public static String getCssCommonDir() {
        return getPropertyValue(COMMON_CSS_BASE_DIR);
    }
}

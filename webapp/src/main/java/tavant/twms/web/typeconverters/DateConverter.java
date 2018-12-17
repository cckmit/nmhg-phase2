package tavant.twms.web.typeconverters;

import org.apache.struts2.util.StrutsTypeConverter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.ResourceBundle;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;
import tavant.twms.dateutil.TWMSDateFormatUtil;

/**
 * Created with IntelliJ IDEA.
 * User: deepak.patel
 * Date: 16/8/13
 * Time: 8:42 PM
 * To change this template use File | Settings | File Templates.
 */
public class DateConverter extends StrutsTypeConverter {

    private static Logger LOG = LoggerFactory.getLogger(DateConverter.class);

    @Override
    /**
     * Converts one or more String values to the specified class.
     *
     * @param context the action context
     * @param values  the String values to be converted, such as those submitted from an HTML form
     * @param toClass the class to convert to
     * @return the converted object
     */
    public Object convertFromString(Map context, String[] values, Class toClass) {

        Date returnObject = null;
        String value = values[0];
        String datePattern = TWMSDateFormatUtil.getDateFormatForLoggedInUser();
        SimpleDateFormat dateFormat = new SimpleDateFormat(datePattern);
        if (value != null && !value.trim().equals("")) {
            try {
                returnObject = dateFormat.parse(value);
            } catch (ParseException e) {
                // Just to ignore the parse exception
            }
        }
        return returnObject;
    }

    @Override
    /**
     * Converts the specified object to a String.
     *
     * @param context the action context
     * @param o       the object to be converted
     * @return the converted String
     */
    public String convertToString(Map context, Object o) {

        Date date = (Date) o;
        String datePattern = TWMSDateFormatUtil.getDateFormatForLoggedInUser();
        SimpleDateFormat dateFormat = new SimpleDateFormat(datePattern);
        String formatedDate = dateFormat.format(date);
        return formatedDate;
    }

   /* private String getDatePattern() {

        ResourceBundle bundle = ResourceBundle.getBundle("messages", ActionContext.getContext().getLocale());
        String pattern = bundle.getString("text.date.format");
        //LOG.info("current date pattern is:" + pattern);
        return pattern;
    }*/
}

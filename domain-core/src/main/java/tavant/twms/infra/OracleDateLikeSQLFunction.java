package tavant.twms.infra;

import java.util.List;
import java.util.regex.Pattern;

import org.hibernate.dialect.function.StandardSQLFunction;
import org.hibernate.engine.SessionFactoryImplementor;
import org.hibernate.type.Type;

import tavant.twms.dateutil.TWMSDateFormatUtil;
import static tavant.twms.dateutil.TWMSDateFormatUtil.EMPTY_COMPONENT_PADDER_PATTERN;
/**
 * Created by IntelliJ IDEA.
 * User: vikas.sasidharan
 * Date: 22 May, 2008
 * Time: 8:53:57 PM
 */
public class OracleDateLikeSQLFunction extends StandardSQLFunction {

    
    

    /**
     * Construct a standard SQL function definition with a variable return type;
     * the actual return type will depend on the types to which the function
     * is applied.
     * <p/>
     * Using this form, the return type is considered non-static and assumed
     * to be the type of the first argument.
     *
     * @param name The name of the function.
     */
    public OracleDateLikeSQLFunction(String name) {
        super(name);
    }

    /**
     * Construct a standard SQL function definition with a static return type.
     *
     * @param name The name of the function.
     * @param type The static return type.
     */
    public OracleDateLikeSQLFunction(String name, Type type) {
        super(name, type);
    }

    /**
     * {@inheritDoc}
     */
    public String render(List args, SessionFactoryImplementor factory) {
        String targetColumn = (String) args.get(0);
        String dateSeparator = TWMSDateFormatUtil.getDateSeparatorForLoggedInUser();
        // Escape single quotes around the pattern, that are put there automatically by hibernate.
        String datePattern = ((String) args.get(1)).replace('\'', '\0');
        // Pad the date separator with a leading and trailing space. This is to make sure the logic
        // doesn't bomb for incomplete patterns such as '/11/2009', '//2009' etc.
        String[] dateComponents = EMPTY_COMPONENT_PADDER_PATTERN.matcher(datePattern)
        	.replaceAll(" " + dateSeparator + " ").split(dateSeparator);
        String[] dateFormatComponents = TWMSDateFormatUtil.getDateFormatComponentsForLoggedInUser();
        StringBuffer sqlPart = new StringBuffer(100);
        StringBuffer dateFormat = new StringBuffer(20);
        StringBuffer dateToMatch = new StringBuffer(20);
        boolean isPartialSearch = false;

        sqlPart.append("to_char(");
        sqlPart.append(targetColumn);
        sqlPart.append(", '");

        for (int i = 0; i < dateComponents.length; i++) {

            String dateComponent = dateComponents[i].trim();
            int dateComponentLength = dateComponent.length();

            if(dateComponentLength == 0) {
                continue;
            }

            if(dateFormat.length() > 0) {
                dateFormat.append("-");
                dateToMatch.append("-");
            }

            dateFormat.append(dateFormatComponents[i]);

            dateToMatch.append(dateComponent);

            if(i == dateComponents.length - 1) { //last element
                // i == 0 => month, i == 1 => day, i == 2 => year
                if(((i == 0 || i == 1) && (dateComponentLength < 2)) || ( i == 2 && dateComponentLength < 4)) {
                    isPartialSearch = true;
                    dateToMatch.append("%");
                }
            }
        }

        sqlPart.append(dateFormat);
        sqlPart.append("') ");
        sqlPart.append(isPartialSearch ? "like" : "=");
        sqlPart.append(" '");
        sqlPart.append(dateToMatch);
        sqlPart.append("' and 9999");

        return sqlPart.toString();
    }
}

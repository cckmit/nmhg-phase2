package tavant.twms.infra;

import org.hibernate.dialect.function.SQLFunction;
import org.hibernate.dialect.function.StandardSQLFunction;
import org.hibernate.type.NullableType;
import org.hibernate.type.Type;
import org.hibernate.engine.SessionFactoryImplementor;

import java.util.regex.Pattern;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: vikas.sasidharan
 * Date: 22 May, 2008
 * Time: 8:55:28 PM
 */
public class MySQLDateLikeSQLFunction extends StandardSQLFunction {

    private static String[] dateFuncs = new String[] {"month", "day", "year"};
    private static final Pattern EMPTY_COMPONENT_PADDER_PATTERN = Pattern.compile("/");
    private static final Pattern QUOTE_REMOVER_PATTERN = Pattern.compile("'");

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
    public MySQLDateLikeSQLFunction(String name) {
        super(name);
    }

    /**
     * Construct a standard SQL function definition with a static return type.
     *
     * @param name The name of the function.
     * @param type The static return type.
     */
    public MySQLDateLikeSQLFunction(String name, Type type) {
        super(name, type);
    }

    /**
     * {@inheritDoc}
     */
    public String render(List args, SessionFactoryImplementor factory) {
        String targetColumn = (String) args.get(0);
        String datePattern = QUOTE_REMOVER_PATTERN.matcher(((String) args.get(1))).replaceAll("");
        String[] dateComponents = EMPTY_COMPONENT_PADDER_PATTERN.matcher(datePattern).replaceAll(" / ").split("/");
        StringBuffer sqlPart = new StringBuffer(100);

        for (int i = 0; i < dateComponents.length; i++) {

            String dateComponent = dateComponents[i].trim();
            int dateComponentLength = dateComponent.length();
            boolean isPartialInput = false;

            if(dateComponentLength == 0) {
                continue;
            }

            if(sqlPart.length() > 0) {
                sqlPart.append(" and ");
            }

            if(i == dateComponents.length - 1) { //last element
                // i == 0 => month, i == 1 => day, i == 2 => year
                isPartialInput = ((i == 0 || i == 1) && (dateComponentLength < 2)) ||
                        ( i == 2 && dateComponentLength < 4);
            }

            addSqlFuncCall(dateComponent, dateFuncs[i], targetColumn, sqlPart, isPartialInput);
        }

        if(sqlPart.length() > 0) {
            sqlPart.append(" and ");
        }

        sqlPart.append("9999");

        return sqlPart.toString();
    }

    private void addSqlFuncCall(String dateComponent, String componentFunc, String targetColumn, StringBuffer sqlPart,
                                boolean isPartialInput) {
        boolean isStartsWithZeroPattern = false;

        if(dateComponent.startsWith("0")) {
            if(isPartialInput) {
                isStartsWithZeroPattern = true;
                sqlPart.append("length(");
            } else {
                dateComponent = dateComponent.substring(1);
            }
        }

        sqlPart.append(componentFunc);
        sqlPart.append("(");
        sqlPart.append(targetColumn);
        sqlPart.append(")");

        if(isPartialInput && !isStartsWithZeroPattern) {
            sqlPart.append(" like '");
            sqlPart.append(dateComponent);
            sqlPart.append("%'");
        } else if(isStartsWithZeroPattern) {
            sqlPart.append(") = 1");
        } else {
            sqlPart.append(" = '");
            sqlPart.append(dateComponent);
            sqlPart.append("'");
        }
    }
}
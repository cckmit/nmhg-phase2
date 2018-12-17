package tavant.twms.infra.hibernate;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.CriteriaQuery;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.dialect.function.SQLFunction;
import org.hibernate.engine.SessionFactoryImplementor;
import org.hibernate.engine.TypedValue;

import java.util.List;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: vikas.sasidharan
 * Date: 16 Oct, 2008
 * Time: 3:16:22 PM
 */
public class DateLikeExpression implements Criterion {
    private final String propertyName;
	private final Object value;
    public static final String DATE_LIKE_SQL_FUNC = "datelike";

    public DateLikeExpression(String propertyName, String value) {
	    this.propertyName = propertyName;
        this.value = value;
    }

    /**
     * Render the SQL fragment
     *
     * @param criteria
     * @param criteriaQuery
     * @return String
     * @throws org.hibernate.HibernateException
     *
     */
    public String toSqlString(Criteria criteria, CriteriaQuery criteriaQuery) throws HibernateException {
        String[] columns = criteriaQuery.getColumnsUsingProjection( criteria, propertyName );
		if ( columns.length != 1 ) {
			throw new HibernateException( "DateLike may only be used with single-column properties" );
		}

        SessionFactoryImplementor sessionFactoryImplementor = criteriaQuery.getFactory();
        SQLFunction dateLikeSyntheticSQLFunction =
                (SQLFunction) sessionFactoryImplementor.getDialect().getFunctions().get(DATE_LIKE_SQL_FUNC);
        List<Object> args = new ArrayList<Object>(2);
        args.add(columns[0]);
        args.add(value);

        StringBuffer sqlString = new StringBuffer(dateLikeSyntheticSQLFunction.render(args, sessionFactoryImplementor));
        sqlString.append(" > 0 ");
		return sqlString.toString();
    }

    /**
     * Return typed values for all parameters in the rendered SQL fragment
     *
     * @param criteria
     * @param criteriaQuery
     * @return TypedValue[]
     * @throws org.hibernate.HibernateException
     *
     */
    public TypedValue[] getTypedValues(Criteria criteria, CriteriaQuery criteriaQuery) throws HibernateException {
        return new TypedValue[] {};
    }

    public String toString() {
		return toSqlString(null, null);
	}
}
package tavant.twms.infra;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.criterion.CriteriaQuery;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.MatchMode;
import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.PostgreSQLDialect;
import org.hibernate.engine.TypedValue;

/**
 * An Ilike Expression which uses upper function instead of lower to accomplish case-insensitive search
 */
public class IlikeExpression implements Criterion {

    private final String propertyName;
    private final Object value;

    protected IlikeExpression(String propertyName, Object value) {
        this.propertyName = propertyName;
        this.value = value;
    }

    protected IlikeExpression(String propertyName, String value, MatchMode matchMode) {
        this( propertyName, matchMode.toMatchString(value) );
    }

    public String toSqlString(Criteria criteria, CriteriaQuery criteriaQuery)
            throws HibernateException {
        Dialect dialect = criteriaQuery.getFactory().getDialect();
        String[] columns = criteriaQuery.getColumnsUsingProjection(criteria, propertyName);
        if (columns.length!=1) throw new HibernateException("ilike may only be used with single-column properties");
        if ( dialect instanceof PostgreSQLDialect) {
            return columns[0] + " ilike ?";
        }
        else {
            return "upper" + '(' + columns[0] + ") like ?";
        }

        //TODO: get SQL rendering out of this package!
    }

    public TypedValue[] getTypedValues(Criteria criteria, CriteriaQuery criteriaQuery)
            throws HibernateException {
        return new TypedValue[] { criteriaQuery.getTypedValue( criteria, propertyName, value.toString().toUpperCase() ) };
    }

    public String toString() {
        return propertyName + " ilike " + value;
    }

}

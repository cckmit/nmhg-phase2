/*
 *   Copyright (c)2006 Tavant Technologies
 *   All Rights Reserved.
 *
 *   This software is furnished under a license and may be used and copied
 *   only  in  accordance  with  the  terms  of such  license and with the
 *   inclusion of the above copyright notice. This software or  any  other
 *   copies thereof may not be provided or otherwise made available to any
 *   other person. No title to and ownership of  the  software  is  hereby
 *   transferred.
 *
 *   The information in this software is subject to change without  notice
 *   and  should  not be  construed as a commitment  by Tavant Technologies.
 */
package tavant.twms.infra;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import org.hibernate.HibernateException;
import org.hibernate.usertype.UserType;

import com.domainlanguage.time.TimePoint;
import java.math.BigDecimal;

/**
 * 
 * User type for Time point
 * 
 * TODO: I have just stored the milliSeconds as a BIGINT and have relied on
 * default time zone. This can be improved later
 * 
 * @author kannan.ekanath
 * 
 */
public class CalendarTimeUserType implements UserType {

	public Object assemble(Serializable cached, Object owner)
			throws HibernateException {
		return cached;
	}

	public Object deepCopy(Object value) throws HibernateException {
		return value;
	}

	public Serializable disassemble(Object value) throws HibernateException {
		return (Serializable) value;
	}

	public boolean equals(Object first, Object second)
			throws HibernateException {
		if (first == second) {
			return true;
		}
		if (first == null || second == null) {
			return false;
		}
		return first.equals(second);
	}

	public int hashCode(Object x) throws HibernateException {
		return x.hashCode();
	}

	public boolean isMutable() {
		return false;
	}

	public Object nullSafeGet(ResultSet rs, String[] names, Object owner)
			throws HibernateException, SQLException {
		Object o = rs.getObject(names[0]);
		if (o == null) {
			return null;
		}
		/**
		 * If database is oracle then object 'o' is of type BigDecimal. In this
		 * case we need to convert object 'o' to Long type.
		 */
		if (o instanceof BigDecimal) {
			BigDecimal bigDecimalObject = (BigDecimal) o;
			o = new Long(bigDecimalObject.longValue());
		}
		if (!(o instanceof Long)) {
			throw new HibernateException(
					"CalendarTimeUserType can be only used for "
							+ "Timestamp fields internally stored as long");
		}
		return TimePoint.from((Long) o);
	}

	public void nullSafeSet(PreparedStatement ps, Object value, int index)
			throws HibernateException, SQLException {
		if (value != null) {
			TimePoint valueAsTime = (TimePoint) value;
			ps.setLong(index, valueAsTime.asJavaUtilDate().getTime());
		} else {
			ps.setLong(index, 0);
		}
	}

	public Object replace(Object original, Object target, Object owner)
			throws HibernateException {
		return original;
	}

	public Class returnedClass() {
		return TimePoint.class;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.hibernate.usertype.UserType#sqlTypes()
	 */
	public int[] sqlTypes() {
		return new int[] { Types.BIGINT };
	}

}

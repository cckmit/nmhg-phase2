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

import com.domainlanguage.time.CalendarDate;
import com.domainlanguage.time.TimePoint;
import java.io.Serializable;
import java.sql.*;
import java.util.TimeZone;
import org.hibernate.HibernateException;
import org.hibernate.usertype.UserType;

/**
 * @author kamal.govindraj
 * 
 */
public class CalendarDateUserType implements UserType {

    /*
     * (non-Javadoc)
     * 
     * @see org.hibernate.usertype.UserType#assemble(java.io.Serializable,
     *      java.lang.Object)
     */
    public Object assemble(Serializable cached, Object owner) throws HibernateException {
        return cached;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.hibernate.usertype.UserType#deepCopy(java.lang.Object)
     */
    public Object deepCopy(Object original) throws HibernateException {
        return original;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.hibernate.usertype.UserType#disassemble(java.lang.Object)
     */
    public Serializable disassemble(Object value) throws HibernateException {
        return (Serializable) value;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.hibernate.usertype.UserType#equals(java.lang.Object,
     *      java.lang.Object)
     */
    public boolean equals(Object first, Object second) throws HibernateException {
        if (first == second) {
            return true;
        }
        if (first == null || second == null) {
            return false;
        }
        return first.equals(second);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.hibernate.usertype.UserType#hashCode(java.lang.Object)
     */
    public int hashCode(Object o) throws HibernateException {
        return o.hashCode();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.hibernate.usertype.UserType#isMutable()
     */
    public boolean isMutable() {
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.hibernate.usertype.UserType#nullSafeGet(java.sql.ResultSet,
     *      java.lang.String[], java.lang.Object)
     */
    public Object nullSafeGet(ResultSet rs, String[] names, Object owner) throws HibernateException,
            SQLException {
        Object o = rs.getObject(names[0]);
        oracle.sql.TIMESTAMP t=null;
        if (o == null) {
            return null;
        }
        if(o instanceof oracle.sql.TIMESTAMP)
        {
        	t=(oracle.sql.TIMESTAMP)o;
        	o=t.dateValue();
        	
        }
        if (!(o instanceof Date || o instanceof Timestamp || o instanceof oracle.sql.TIMESTAMP)) {
            throw new HibernateException("CalendarDateUserType can be only used for Date/Timestamp fields");
        }
        return TimePoint.from((java.util.Date) o).calendarDate(TimeZone.getDefault());
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.hibernate.usertype.UserType#nullSafeSet(java.sql.PreparedStatement,
     *      java.lang.Object, int)
     */
    public void nullSafeSet(PreparedStatement ps, Object value, int index) throws HibernateException,
            SQLException {
        if (value != null) {
            CalendarDate valueAsDate = (CalendarDate) value;
            ps.setDate(index, new java.sql.Date(valueAsDate.startAsTimePoint(TimeZone.getDefault())
                    .asJavaUtilDate().getTime()));
        } else {
            ps.setDate(index, null);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.hibernate.usertype.UserType#replace(java.lang.Object,
     *      java.lang.Object, java.lang.Object)
     */
    public Object replace(Object original, Object target, Object owner) throws HibernateException {
        return original;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.hibernate.usertype.UserType#returnedClass()
     */
    public Class returnedClass() {
        return CalendarDate.class;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.hibernate.usertype.UserType#sqlTypes()
     */
    public int[] sqlTypes() {
        return new int[] { Types.DATE };
    }

}

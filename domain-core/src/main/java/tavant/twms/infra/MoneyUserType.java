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
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Currency;

import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.engine.SessionImplementor;
import org.hibernate.type.Type;
import org.hibernate.usertype.CompositeUserType;

import com.domainlanguage.money.Money;

/**
 * Hibernate UserType class for persisting objects of Money class, currently
 * only the amount portion is being persisted the currency is assumed to be
 * dollars. 
 * 
 * @author kamal.govindraj
 * 
 */
public class MoneyUserType implements CompositeUserType {

    public Object assemble(Serializable cached, SessionImplementor session, Object owner)
            throws HibernateException {
        return cached;
    }

    public Object deepCopy(Object value) throws HibernateException {
        return value;
    }

    public Serializable disassemble(Object value, SessionImplementor session) throws HibernateException {
        return (Serializable) value;
    }

    public boolean equals(Object first, Object second) throws HibernateException {
        if (first == second)
            return true;
        if (first == null || second == null)
            return false;
        return first.equals(second);
    }

    public String[] getPropertyNames() {
        return new String[] {"amount","currency"};
    }

    public Type[] getPropertyTypes() {
        return new Type[] {Hibernate.BIG_DECIMAL,Hibernate.CURRENCY};
    }

    public Object getPropertyValue(Object component, int property) throws HibernateException {
        Money money = (Money)component;
        return (property == 0) ? money.breachEncapsulationOfAmount() : money.breachEncapsulationOfCurrency();
    }

    public int hashCode(Object x) throws HibernateException {
        return x.hashCode();
    }

    public boolean isMutable() {
        return false;
    }

    public Object nullSafeGet(ResultSet rs, String[] names, SessionImplementor session, Object owner)
            throws HibernateException, SQLException {
        BigDecimal amt = (BigDecimal) Hibernate.BIG_DECIMAL.nullSafeGet( rs, names[0] );
        Currency cur = (Currency) Hibernate.CURRENCY.nullSafeGet( rs, names[1] );
        if (amt==null) return null;
        return Money.valueOf(amt, cur);
    }

    public void nullSafeSet(PreparedStatement st, Object value, int index, SessionImplementor session)
            throws HibernateException, SQLException {
        Money money = (Money) value;
        BigDecimal amt = money == null ? null : money.breachEncapsulationOfAmount();
        Currency cur = money == null ? null : money.breachEncapsulationOfCurrency();
        Hibernate.BIG_DECIMAL.nullSafeSet(st, amt, index);
        Hibernate.CURRENCY.nullSafeSet(st, cur, index+1);

    }

    public Object replace(Object original, Object target, SessionImplementor session, Object owner)
            throws HibernateException {
        return original;
    }

    public Class returnedClass() {
        return Money.class;
    }

    public void setPropertyValue(Object component, int property, Object value) throws HibernateException {
        throw new UnsupportedOperationException("Method Not implemented");
    }

}

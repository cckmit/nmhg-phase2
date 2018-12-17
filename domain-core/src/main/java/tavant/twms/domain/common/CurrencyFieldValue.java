/*
 *   Copyright (c)2007 Tavant Technologies
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
package tavant.twms.domain.common;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.springframework.util.ReflectionUtils;

import com.domainlanguage.money.Money;

/**
 * @author radhakrishnan.j
 * 
 */
/**
 * @author radhakrishnan.j
 *
 */
public class CurrencyFieldValue {
    private Object owningParent;

    private Field field;
    
    private Method getter;
    
    private Method setter;

    public CurrencyFieldValue(Object owningParent, Field field) {
        super();
        this.owningParent = owningParent;
        this.field = field;
		String name = field.getName();
		StringBuffer methodName = new StringBuffer();
		methodName.append( name.toUpperCase().charAt(0));
		methodName.append( name.substring(1));
		Class<?> declaringClass = field.getDeclaringClass();
		String getterMethodName = new StringBuffer("get").append(methodName).toString();
		String setterMethodName = new StringBuffer("set").append(methodName).toString();
		getter = ReflectionUtils.findMethod(declaringClass, getterMethodName, new Class[]{});
		setter = ReflectionUtils.findMethod(declaringClass, setterMethodName, new Class[]{field.getType()});
    }

    public Money getFieldValue() {
        ReflectionUtils.makeAccessible(field);    	
        try {
			return (Money)(getter==null ? field.get(owningParent) : getter.invoke(owningParent, (Object[])null));
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		} catch (InvocationTargetException e) {
			throw new RuntimeException(e);
		}
    }

    public void setFieldValue(Money newMoney)  {
        ReflectionUtils.makeAccessible(field);
        try {
			if (setter==null) {
				field.set(owningParent, newMoney);
			} else {
				setter.invoke(owningParent,new Object[]{newMoney});
			}
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		} catch (InvocationTargetException e) {
			throw new RuntimeException(e);
		}
    }
    
    public Object getOwningParent() {
        return owningParent;
    }

    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("(");
        buffer.append("owningParent").append('=').append(owningParent).append(", ");
        buffer.append("field").append('=').append(field).append(")");
        return buffer.toString();
    }

	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + ((field == null) ? 0 : field.hashCode());
		result = PRIME * result + ((owningParent == null) ? 0 : owningParent.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final CurrencyFieldValue other = (CurrencyFieldValue) obj;
		if (field == null) {
			if (other.field != null)
				return false;
		} else if (!field.equals(other.field))
			return false;
		if (owningParent == null) {
			if (other.owningParent != null)
				return false;
		} else if (!owningParent.equals(other.owningParent))
			return false;
		return true;
	}
}

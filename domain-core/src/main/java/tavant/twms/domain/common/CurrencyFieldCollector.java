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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ognl.Ognl;
import ognl.OgnlException;

import org.springframework.util.ReflectionUtils;

import com.domainlanguage.money.Money;

/**
 * @author radhakrishnan.j
 *
 */
public class CurrencyFieldCollector {
    public Set<CurrencyFieldValue> collectCurrencyFieldValuesOf(final Object anObject) throws OgnlException {
        final Class<? extends Object> _klass = anObject.getClass();
		if( _klass.getName().equals("java.lang.Object") ) {
            return Collections.emptySet();
        }
		final Set<CurrencyFieldValue> currencyFieldValues = new HashSet<CurrencyFieldValue>();
		
		ReflectionUtils.doWithFields(_klass,new ReflectionUtils.FieldCallback(){
			public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {
				if(!field.isAnnotationPresent(ExcludeConversion.class) && Money.class.equals(field.getType() ) ){
					ReflectionUtils.makeAccessible(field);
					
					currencyFieldValues.add(new CurrencyFieldValue(anObject,field));
				}
			}
			
		});        
        List<Class> klasses = classesInClassHierarchyOf(_klass);
        
        
        for (Class<? extends Object> klass : klasses) {
            PropertiesWithNestedCurrencyFields nonLeafNodes = klass.getAnnotation(PropertiesWithNestedCurrencyFields.class);
            if (nonLeafNodes != null) {
                for (String nonLeafProperty : nonLeafNodes.value()) {
                    Object value = Ognl.getValue(nonLeafProperty, anObject);
                    if (value != null ) {
                        if( value instanceof Collection )  {
                            Collection collectionValue = (Collection)value;
                            for( Object anEntry : collectionValue ) {
                                Set<CurrencyFieldValue> nestedCurrencyFieldValues = collectCurrencyFieldValuesOf(anEntry);
                                currencyFieldValues.addAll(nestedCurrencyFieldValues);
                            }
                        } else {
                            Set<CurrencyFieldValue> nestedCurrencyFieldValues = collectCurrencyFieldValuesOf(value);
                            currencyFieldValues.addAll(nestedCurrencyFieldValues);
                        }
                    }
                }
            }
        }        
        return currencyFieldValues;
    }

    private List<Class> classesInClassHierarchyOf(Class<? extends Object> klass) {
        //Starting from this class, go up in the hierarchy.
        List<Class> klasses = new ArrayList<Class>();
        boolean yetToReachRootClass = true;
        while(yetToReachRootClass) {
            klasses.add(klass);
            klass = klass.getSuperclass();
            yetToReachRootClass = !klass.getName().equals("java.lang.Object");
        }
        return klasses;
    }
}

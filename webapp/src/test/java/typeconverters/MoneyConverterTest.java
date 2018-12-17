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
package typeconverters;

import java.util.Collections;
import java.util.Map;

import junit.framework.TestCase;
import tavant.twms.web.typeconverters.MoneyConverter;

import com.domainlanguage.money.Money;

public class MoneyConverterTest extends TestCase {

    MoneyConverter MoneyConverter = new MoneyConverter();
    Map ctx = Collections.EMPTY_MAP; 
    
    /**
     * Incompatible objects should be converted to null.
     *
     */
    public void testConvertValueWithIncompatibleType() {
        
        Object convertedValue = 
            MoneyConverter.convertValue(ctx, new Integer(1),  
                Boolean.class);
        
        assertNull(convertedValue);
    }

    /**
     * Null Money objects should be left untouched.
     *
     */
    public void testConvertValueWithNullMoney() {
        
        Object convertedValue = 
            MoneyConverter.convertValue(ctx, null, 
                String.class);
        
        assertNull(convertedValue);
    }
    
    /**
     * Non null Money objects should be converted to 
     * formatted currency string.
     *
     */
    public void testConvertValueWithMoney() {
        
        Money money = Money.dollars(123.45); 
        
        Object convertedValue = 
            MoneyConverter.convertValue(ctx, money, 
                String.class);
        
        assertNotNull(convertedValue);
        assertTrue(convertedValue instanceof String);
        
        String formattedDate = (String) convertedValue;
        assertEquals("$ 123.45", formattedDate);
    }
    
    /**
     *  Null Currency String arrays should be converted
     *  to null.
     *
     */
    public void testConvertValueWithNullDateString() {
        
        Object convertedValue = 
            MoneyConverter.convertValue(ctx, new String[] {null,null}, 
                Money.class);
        
        assertNull(convertedValue);
    }
    
    
}
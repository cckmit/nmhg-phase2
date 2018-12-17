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
package tavant.twms.domain.rules;

import java.util.TimeZone;

import junit.framework.TestCase;

import com.domainlanguage.money.Money;
import com.domainlanguage.time.CalendarDate;
import com.domainlanguage.timeutil.Clock;

/**
 * @author radhakrishnan.j
 *
 */
public class TypeSystemTest extends TestCase {
    static {
        Clock.setDefaultTimeZone( TimeZone.getDefault() );
        Clock.timeSource();
    }
    
    
    @SuppressWarnings("unchecked")
    public void testIntegration() throws Exception{
        TypeSystem typeMap = TypeSystem.getInstance();
        assertNotNull(typeMap.getType(Type.STRING));
        assertNotNull(typeMap.getType(Type.BOOLEAN));
        assertNotNull(typeMap.getType(Type.INTEGER));
        assertNotNull(typeMap.getType(Type.LONG));
        assertNotNull(typeMap.getType(Type.BIGDECIMAL));
        assertNotNull(typeMap.getType(Type.DATE));
        assertNotNull(typeMap.getType(Type.MONEY));
   }

    public void testGetType() {
        assertNotNull(literalType(Type.STRING));
        assertNotNull(literalType(Type.INTEGER));
        assertNotNull(literalType(Type.MONEY));
        assertNotNull(literalType(Type.DATE));
        assertNotNull(literalType(Type.BOOLEAN));
    }

    public void testStringType() {
        String typeName = Type.STRING;
        assertEquals("Junk",literalType(typeName).getJavaObject("Junk"));
    }

    public void testDateType() {
        CalendarDate expected = CalendarDate.date(2006,12,3);
        assertEquals(expected,literalType(Type.DATE).getJavaObject("12/3/2006"));
    }

    public void testCurrencyType() {
        Money expected = Money.dollars(20);
        assertEquals(expected,literalType(Type.MONEY).getJavaObject("20"));
        
        expected = Money.euros(20.69);
        assertEquals(expected,literalType(Type.MONEY).getJavaObject(" EUR 20.69 "));
    }

    public void testBooleanType() {
        assertEquals(Boolean.TRUE,literalType(Type.BOOLEAN).getJavaObject("true"));
        assertEquals(Boolean.FALSE,literalType(Type.BOOLEAN).getJavaObject("false"));
    }

    private LiteralSupport literalType(String typeName) {
        return ((LiteralSupport)TypeSystem.getInstance().getType(typeName));
    }
}


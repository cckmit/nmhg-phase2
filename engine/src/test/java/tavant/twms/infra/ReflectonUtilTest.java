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

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

public class ReflectonUtilTest extends TestCase {

    public void testSimpleMethodGet() {
        List list = new ArrayList();
        Method method = ReflectionUtil.getMethod(list, "size");
        assertEquals("size", method.getName());
        assertEquals(method.getParameterTypes().length, 0);
    }
    
    public void testGetMethodOverloaded() {
        //try getting overloaded method it must fail
        List list = new ArrayList();
        try {
            ReflectionUtil.getMethod(list, "toArray");
            fail("Dint throw exception");
        } catch (IllegalStateException e) {
            // succeed
        }        
    }
    
    public void testExecuteMethod() {
        List<Integer> list = new ArrayList<Integer>();
        list.add(100);
        list.add(200);
        list.add(300);
        Object obj = ReflectionUtil.executeMethod(list, "get", new Object[]{1});
        assertEquals(obj, new Integer(200));
    }
    
    public void testMethodWithNoArgs() {
    	List<Integer> list = new ArrayList<Integer>();
        list.add(100);
        list.add(200);
        list.add(300);
        Object obj = ReflectionUtil.executeMethod(list, "clear", new Object[]{});
        assertNull(obj);
        assertTrue(list.isEmpty());
    }
}

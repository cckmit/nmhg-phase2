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
package tavant.twms.jbpm.bsh;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import junit.framework.TestCase;

import org.jbpm.context.exe.ContextInstance;
import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.graph.exe.ProcessInstance;

import tavant.twms.domain.catalog.Item;
import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.claim.MachineClaim;
import tavant.twms.domain.inventory.InventoryItem;

import com.domainlanguage.time.CalendarDate;
import com.domainlanguage.timeutil.Clock;

public class BeanScriptTest extends TestCase {

    BeanScript script;

    ProcessDefinition processDefinition;

    ProcessInstance processInstance;

    ExecutionContext executionContext;
    
    ContextInstance contextInstance;
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        ProcessDefinition processDefinition = ProcessDefinition.parseXmlString("<process-definition>"
                + "  <start-state>" + "    <transition to='s' />" + "  </start-state>" + "  <state name='s'>"
                + "    <transition to='end' />" + "  </state>" + "  <end-state name='end' />"
                + "</process-definition>");

        ProcessInstance processInstance = new ProcessInstance(processDefinition);
        executionContext = new ExecutionContext(processInstance.getRootToken());
        contextInstance = executionContext.getContextInstance();
    }

    public void testSimpleExecution() {
        contextInstance.setVariable("var1", "value1");
        contextInstance.setVariable("var2", "value2");
        contextInstance.setTransientVariable("tv1", "tvalue1");
        script = new BeanScript("some exp");
        
        Map map = script.createInputMap(executionContext);
        
        assertEquals("value1", map.get("var1"));
        assertEquals("value2", map.get("var2"));
        assertEquals("tvalue1", map.get("tv1"));
        
        //This wont be true since all tasks are put in the map
        //assertEquals(script.contextVariables.size(), 2);
        assertTrue(script.scriptVariables.get("var1"));
        assertTrue(script.scriptVariables.get("var2"));
        assertFalse(script.scriptVariables.get("tv1"));
    }
    
    public void testCompleteScriptExecution() {
        contextInstance.setVariable("a", "hello");
        contextInstance.setVariable("b", "world");
        contextInstance.setTransientVariable("c", "somethingelse");
        script = new BeanScript("c=a+b");
        try {
            script.execute(executionContext);
            //now check if result got changed?
            assertEquals(contextInstance.getTransientVariable("c"), "helloworld");
        } catch (Exception e) {
            fail("Unexpected exception");
        }
    }
    
    public void testClaimOtherComments() {
        Clock.setDefaultTimeZone(TimeZone.getDefault());
        Clock.timeSource();
        InventoryItem item = new InventoryItem();
        item.setOfType(new Item());
        Claim claim = new MachineClaim(item, CalendarDate.date(2005, 7, 13), CalendarDate.date(2005, 7, 15));
        contextInstance.setVariable("claim", claim);
        contextInstance.setTransientVariable("otherComments", "somecomments");
        BeanScript testScript = new BeanScript("claim.setOtherComments(otherComments)");
        try {
            testScript.execute(executionContext);
            assertEquals("somecomments", claim.getOtherComments());
        } catch (Exception e) {
            fail("Unexpected exception");
        }        
    }
    
    public void testClaimOtherCommentsFromList() {
        Clock.setDefaultTimeZone(TimeZone.getDefault());
        Clock.timeSource();
        InventoryItem item = new InventoryItem();
        item.setOfType(new Item());
        Claim claim = new MachineClaim(item, CalendarDate.date(2005, 7, 13), CalendarDate.date(2005, 7, 15));
        contextInstance.setVariable("claim", claim);
        List<String> comments = new ArrayList<String>();
        comments.add("somecomments");
        comments.add("someothercomments");
        contextInstance.setTransientVariable("comments", comments);
        BeanScript testScript = new BeanScript("claim.setOtherComments(comments.get(0))");
        try {
            testScript.execute(executionContext);
            assertEquals("somecomments", claim.getOtherComments());
        } catch (Exception e) {
            fail("Unexpected exception");
        }
    }
    
}


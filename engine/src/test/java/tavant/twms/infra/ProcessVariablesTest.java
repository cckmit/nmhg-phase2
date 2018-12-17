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

import java.io.ByteArrayInputStream;

import junit.framework.TestCase;

import org.jbpm.context.exe.ContextInstance;
import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.graph.exe.ProcessInstance;

import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.claim.MachineClaim;

public class ProcessVariablesTest extends TestCase {

    ProcessVariables var;

    ContextInstance ctx;

    String processdef = "<process-definition xmlns='' name='ClaimCreation'>" + "  <start-state name='Start'>"
            + "    <transition name='' to='ClaimEntry'></transition>" + "  </start-state>"
            + "  <task-node name='ClaimEntry'>" + "    <task name='Initial Claim Draft'/>"
            + "    <transition name='Next' to='Confirmation'/>" + "  </task-node>"
            + "  <task-node name='Confirmation'>" + "    <task name='Final Claim Draft'/>"
            + "    <transition name='Submit' to='End'/>" + "    <transition name='Back' to='ClaimEntry'/>"
            + "  </task-node>" + "  <end-state name='End'/>" + "</process-definition>";

    ProcessInstance processInstance;

    protected void setUp() throws Exception {
        super.setUp();
        var = new ProcessVariables();
        ProcessDefinition def = ProcessDefinition.parseXmlInputStream(new ByteArrayInputStream(processdef
                .getBytes()));
        processInstance = def.createProcessInstance();
        ctx = processInstance.getContextInstance();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testAddVariable() {
        String obj = "Test";
        assertNull("No variables should be associated with the id [claim]", var.getVariable("claim"));
        var.setVariable("claim", obj);
        assertEquals("Should be able to retrieve the object added as variable", obj, var.getVariable("claim"));
    }

    public void testGetVariable() {
        Claim claim = new MachineClaim();
        assertNull("No variables should be associated with the id [claim]", var.getClaim());
        var.setClaim(claim);
        assertEquals("Should be able to retrieve the object added as variable", claim, var.getClaim());
    }

    public void testInitializeWithNullContextVariables() {
        try {
            var.initializeWithContextVariables(null);
            fail("Expected Illegal Argument exception");
        } catch (IllegalArgumentException e) {
            // Expected
        }
    }

    public void testInitializeWithContextVariables() {
        assertNull(var.getVariable("key1"));
        assertNull(var.getVariable("key2"));
        ctx.setVariable("key1", "value1");
        ctx.setVariable("key2", "value2");
        var.initializeWithContextVariables(ctx);
        assertEquals("value1", var.getVariable("key1"));
        assertEquals("value2", var.getVariable("key2"));
    }

    public void testExtractOutContextVariables() {
        var.setVariable("key1", "value1");
        var.setVariable("key2", "value2");
        assertNull(ctx.getVariable("key1"));
        assertNull(ctx.getVariable("key2"));
        var.extractOutContextVariables(ctx);
        assertEquals("value1", ctx.getVariable("key1"));
        assertEquals("value2", ctx.getVariable("key2"));

    }
}

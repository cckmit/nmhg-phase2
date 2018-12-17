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
package tavant.twms.jbpm.nodes;

import java.util.Collection;

import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.graph.exe.ProcessInstance;
import org.jbpm.graph.exe.Token;
import org.jbpm.taskmgmt.exe.TaskInstance;
import org.jbpm.taskmgmt.exe.TaskMgmtInstance;

import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.claim.ClaimState;
import tavant.twms.domain.claim.MachineClaim;
import tavant.twms.infra.ProcessDeployableTestCase;

public class TaskBasedClaimStateTest extends ProcessDeployableTestCase {
    
    ProcessInstance processInstance;
    Claim claim;
    
    @Override
    protected void setUpInTxnRollbackOnFailure() throws Exception {
        super.setUpInTxnRollbackOnFailure();
        ProcessDefinition def = deployProcess("task-based-claim-state-process.xml");
        claim = new MachineClaim();
        claim.setId(35L);
        processInstance = new ProcessInstance(def);
        processInstance.getContextInstance().setVariable("claim", claim);
    }
    
    public void testTaskBasedClaimState() {
        assertNull(claim.getState());
        
        processInstance.signal();
        Token token  = processInstance.getRootToken();
        assertEquals("ProcessorReview", token.getNode().getName());
        TaskInstance taskInstance = getTaskInstance(token);
        assertEquals("Processor Review", taskInstance.getName());
        assertNotNull(claim.getState());
        assertEquals("manual review", claim.getState().getState());        
        taskInstance.end("Next");  
        
        assertEquals("SuperProcessorReview", token.getNode().getName());
        taskInstance = getTaskInstance(token);
        assertEquals("Super Processor Review", taskInstance.getName());
        assertNotNull(claim.getState());
        assertEquals(ClaimState.SUBMITTED, claim.getState());        
        taskInstance.end("Accept");
        
        assertEquals("Cashier", token.getNode().getName());
        taskInstance = getTaskInstance(token);
        assertEquals("Cashier", taskInstance.getName());
        assertNotNull(claim.getState());
        assertEquals(ClaimState.SUBMITTED, claim.getState());        
        taskInstance.end("Pay");
        
        assertTrue(processInstance.hasEnded());        
    }
    
    TaskInstance getTaskInstance(Token token) {
        TaskMgmtInstance mgmt = token.getProcessInstance().getTaskMgmtInstance();
        Collection tasks = mgmt.getUnfinishedTasks(token);
        assertEquals(1, tasks.size());
        return (TaskInstance)tasks.iterator().next();        
    }

}

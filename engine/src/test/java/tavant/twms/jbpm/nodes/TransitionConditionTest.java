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
import java.util.List;

import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.graph.def.Transition;
import org.jbpm.graph.exe.ProcessInstance;
import org.jbpm.graph.exe.Token;
import org.jbpm.taskmgmt.exe.TaskInstance;
import org.jbpm.taskmgmt.exe.TaskMgmtInstance;

import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.claim.MachineClaim;
import tavant.twms.infra.ProcessDeployableTestCase;

public class TransitionConditionTest extends ProcessDeployableTestCase {
    
    ProcessInstance processInstance;
    Claim claim;
    
    @Override
    protected void setUpInTxnRollbackOnFailure() throws Exception {
        ProcessDefinition def = deployProcess("transition-condition-process.xml");
        processInstance = new ProcessInstance(def);
        claim = new MachineClaim();
        processInstance.getContextInstance().setVariable("claim", claim);
    }
    
    public void testTransitionConditions() {
        processInstance.signal();
        Token token  = processInstance.getRootToken();
        assertEquals("ClaimSubmit", token.getNode().getName());
        TaskInstance taskInstance = getTaskInstance(token);
        assertEquals("Claim Submission", taskInstance.getName());
        taskInstance.end();
        
        assertEquals("LimitCheck", token.getNode().getName());
        taskInstance = getTaskInstance(token);
        assertEquals("Check Limit", taskInstance.getName());
        FormTaskNode formTaskNode = (FormTaskNode)token.getNode();
        List<Transition> transitions = formTaskNode.getAvailableTransitions(taskInstance);
        assertEquals(1, transitions.size());
        Transition transition = transitions.iterator().next();
        assertEquals("ReSubmit", transition.getName());
        taskInstance.end(transition);
        
        assertEquals("ClaimSubmit", token.getNode().getName());
        taskInstance = getTaskInstance(token);
        assertEquals("Claim Submission", taskInstance.getName());
        taskInstance.end();
        
        assertEquals("LimitCheck", token.getNode().getName());
        taskInstance = getTaskInstance(token);
        assertEquals("Check Limit", taskInstance.getName());
        formTaskNode = (FormTaskNode)token.getNode();
        transitions = formTaskNode.getAvailableTransitions(taskInstance);
        assertEquals(1, transitions.size());
        transition = transitions.iterator().next();
        assertEquals("ReSubmit", transition.getName());
        taskInstance.end(transition);
        
        assertEquals("ClaimSubmit", token.getNode().getName());
        taskInstance = getTaskInstance(token);
        assertEquals("Claim Submission", taskInstance.getName());
        taskInstance.end();
        
        assertEquals("LimitCheck", token.getNode().getName());
        taskInstance = getTaskInstance(token);
        assertEquals("Check Limit", taskInstance.getName());
        formTaskNode = (FormTaskNode)token.getNode();
        transitions = formTaskNode.getAvailableTransitions(taskInstance);       
        assertEquals(1, transitions.size());
        transition = transitions.iterator().next();
        assertEquals("Allowed", transition.getName());
        taskInstance.end(transition);
        
        assertEquals("ClaimApprove", token.getNode().getName());
        taskInstance = getTaskInstance(token);
        assertEquals("Claim Approve", taskInstance.getName());
        formTaskNode = (FormTaskNode)token.getNode();
        transitions = formTaskNode.getAvailableTransitions(taskInstance);
        assertEquals(2, transitions.size());
        taskInstance.end("Approve");
        
        assertTrue(processInstance.hasEnded());
    }
    
    TaskInstance getTaskInstance(Token token) {
        TaskMgmtInstance mgmt = token.getProcessInstance().getTaskMgmtInstance();
        Collection tasks = mgmt.getUnfinishedTasks(token);
        assertEquals(1, tasks.size());
        return (TaskInstance)tasks.iterator().next();        
    }
}

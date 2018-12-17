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
package tavant.twms.worklist;

import java.util.Collection;
import java.util.Iterator;

import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.graph.exe.ProcessInstance;
import org.jbpm.graph.exe.Token;
import org.jbpm.taskmgmt.exe.TaskInstance;

import com.domainlanguage.time.CalendarDate;

import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.claim.ClaimState;
import tavant.twms.domain.claim.MachineClaim;
import tavant.twms.infra.ProcessDeployableTestCase;
import tavant.twms.infra.ProcessVariables;
import tavant.twms.jbpm.WorkflowConstants;
import tavant.twms.process.ProcessService;

/**
 * @author vineeth.varghese
 * @date Sep 2, 2006
 */
public class WorkListItemServiceImplTest extends ProcessDeployableTestCase {

    WorkListItemService workListItemService;

    ProcessService processService;

    private static final String PROCESS_FILE = "worklist-item-service-impl-test.xml";

    @Override
    protected void setUpInTxnRollbackOnFailure() throws Exception {
        super.setUpInTxnRollbackOnFailure();
        deployProcess(PROCESS_FILE);
    }

    public void testEndTaskWithTransitionWithNull() {
        try {
            workListItemService.endTaskWithTransition(null, "");
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // Expected
        }
        try {
            workListItemService.endTaskWithTransition(new TaskInstance(), null);
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // Expected
        }
    }

    public void testLoadTask() {
        ProcessInstance instance = 
            processService.startProcess("WorkListItemTest", new ProcessVariables());
        TaskInstance task = 
            (TaskInstance) instance.getTaskMgmtInstance().getTaskInstances().iterator()
                .next();
        task = workListItemService.findTask(task.getId());
        assertEquals("Processor Review", task.getName());
    }

    public void testFindTaskWithNull() {
        try {
            workListItemService.findTask(null);
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // Expected
        }
    }

    public void testEndTaskWithTransition() {
        ProcessInstance instance = processService.startProcess("WorkListItemTest", 
                new ProcessVariables());
        assertEquals(1, instance.getTaskMgmtInstance().getTaskInstances().size());
        TaskInstance task = (TaskInstance) instance.getTaskMgmtInstance()
                .getTaskInstances().iterator().next();
        assertEquals("Processor Review", task.getName());
        workListItemService.endTaskWithTransition(task, "t1");
        Iterator iter = instance.getTaskMgmtInstance().getTaskInstances().iterator();
        while (iter.hasNext()) {
            TaskInstance taskInstance = (TaskInstance) iter.next();
            if (taskInstance.hasEnded()) {
                assertEquals("Processor Review", taskInstance.getName());
            } else {
                assertEquals("Task2", taskInstance.getName());
            }
        }
    }
    
    public void testEndTaskWithReassignmentWithNull() {
        try {
            workListItemService.endTaskWithReassignment(null, "t1", "Wally");
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // Expected
        }
        try {
            workListItemService.endTaskWithReassignment(new TaskInstance(), null, "Wally");            
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // Expected
        }
        try {
            workListItemService.endTaskWithReassignment(new TaskInstance(), "t1", null);            
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // Expected
        }
    }

    @SuppressWarnings("unchecked")
    public void testEndTaskWithReassignment() {
        ProcessInstance instance = processService.startProcess("WorkListItemTest", 
                new ProcessVariables());
        assertEquals(1, instance.getTaskMgmtInstance().getTaskInstances().size());
        TaskInstance task = (TaskInstance)instance.getTaskMgmtInstance()
            .getTaskInstances().iterator().next();
        Token token = task.getToken();
        assertEquals("Processor Review", task.getName());
        assertEquals("Jim", task.getActorId());
        workListItemService.endTaskWithReassignment(task, "t1", "Jake");
        task = (TaskInstance)instance.getTaskMgmtInstance()
            .getUnfinishedTasks(token).iterator().next();
        assertEquals("Task2", task.getName());
        assertEquals("Jake", task.getActorId());
        workListItemService.endTaskWithReassignment(task, "t2", "Jerry");
        Collection<TaskInstance> tasks = 
            instance.getTaskMgmtInstance().getUnfinishedTasks(token);
        assertEquals(0, tasks.size());
    }
    
    public void testClaimToTaskInstance() {
        Claim claim = new MachineClaim();
        claim.setHoursInService(5);
        claim.setFailureDate(CalendarDate.date(2006,1,1));
        claim.setRepairDate(CalendarDate.date(2006,1,1));
        claim.setState(ClaimState.SUBMITTED);
        ProcessVariables processVariables = new ProcessVariables();
        processVariables.setClaim(claim);
        ProcessInstance instance = processService.startProcess("WorkListItemTest", 
                processVariables);
        
        ProcessDefinition processDefinition = instance.getProcessDefinition();
        assertEquals(processDefinition.getNode("TaskNode1"), instance.getRootToken().getNode());
        getSession().save(instance);
        Long claimId = claim.getId();
        TaskInstance taskInstance = (TaskInstance) instance.getTaskMgmtInstance().getTaskInstances().iterator().next();
        Long taskInstanceId = taskInstance.getId();
        assertNotNull(claimId);
        assertNotNull(taskInstanceId);
        
        assertEquals(taskInstanceId.longValue(), workListItemService.findTaskForClaimWithTaskName(claimId, WorkflowConstants.PROCESSOR_REVIEW_TASK_NAME).getId());
    }

    public void testTaskInstanceCount() {
    	assertEquals(0, workListItemService.getTaskInstancesAtTaskName("Processor Review").size());
    	processService.startProcess("WorkListItemTest", new ProcessVariables());
    	assertEquals(1, workListItemService.getTaskInstancesAtTaskName("Processor Review").size());
    	processService.startProcess("WorkListItemTest", new ProcessVariables());
    	assertEquals(2, workListItemService.getTaskInstancesAtTaskName("Processor Review").size());
    }
    
    /**
     * @param workListItemService
     *            the workListItemService to set
     */
    public void setWorkListItemService(WorkListItemService workListItemService) {
        this.workListItemService = workListItemService;
    }

    /**
     * @param processService
     *            the processService to set
     */
    public void setProcessService(ProcessService processService) {
        this.processService = processService;
    }

}

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
package tavant.twms.jbpm.assignment;

import java.util.Collection;

import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.graph.exe.ProcessInstance;
import org.jbpm.graph.exe.Token;
import org.jbpm.taskmgmt.exe.TaskInstance;
import org.jbpm.taskmgmt.exe.TaskMgmtInstance;

import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.claim.MachineClaim;
import tavant.twms.domain.orgmodel.Dealership;
import tavant.twms.domain.orgmodel.OrgService;
import tavant.twms.infra.EngineRepositoryTestCase;

public class RuleBasedAssignmentIntegrationTest extends EngineRepositoryTestCase {

    ProcessInstance processInstance;

    private OrgService orgService;

    protected void setUpInTxnRollbackOnFailure() throws Exception {
        super.setUpInTxnRollbackOnFailure();
        ProcessDefinition def = ProcessDefinition.parseXmlInputStream(this.getClass().getResourceAsStream(
                "rule_based_assignment_process.xml"));
        processInstance = new ProcessInstance(def);
    }    
    
    @SuppressWarnings("unchecked")
    private TaskInstance getCurrentActiveTask(ProcessInstance processInstance) {
        Token token = processInstance.getRootToken();
        TaskMgmtInstance mgmt = processInstance.getTaskMgmtInstance();
        Collection<TaskInstance> tasks = mgmt.getUnfinishedTasks(token);
        assertEquals(1, tasks.size());
        return tasks.iterator().next();
    }
    
    private Claim getClaim() {
        Claim claim = new MachineClaim();
        Dealership dealer = orgService.findDealerByName("A-L-L EQUIPMENT");
        claim.setForDealerShip(dealer);
        //claim.setType("Machine");
        return claim;
    }
    
    public void testDsmAssignment() {        
        processInstance.getContextInstance().setVariable("claim", getClaim());
        processInstance.signal();        
        TaskInstance taskInstance = getCurrentActiveTask(processInstance);
        assertEquals("DsmAssignmentTask", taskInstance.getName());
        assertEquals("dsm", taskInstance.getActorId());
    }
    
    public void testDefaultProcessorAssignmentWithNoProcessorRoutingRules() {
        processInstance.getContextInstance().setVariable("claim", getClaim());
        processInstance.signal();        
        TaskInstance taskInstance = getCurrentActiveTask(processInstance);
        assertEquals("DsmAssignmentTask", taskInstance.getName());
        assertEquals("dsm", taskInstance.getActorId());
        taskInstance.end();
        taskInstance = getCurrentActiveTask(processInstance);
        assertEquals("ProcessorAssignmentTask", taskInstance.getName());
        assertEquals("processor", taskInstance.getActorId());
    }
    
    /**
     * @param orgService
     * the orgService to set
     */
    public void setOrgService(OrgService orgService) {
        this.orgService = orgService;
    }
}

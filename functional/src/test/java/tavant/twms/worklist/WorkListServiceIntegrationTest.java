/*
 *   Copyright (c)2008 Tavant Technologies
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

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.taskmgmt.def.Task;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import tavant.twms.domain.claim.ClaimService;
import tavant.twms.domain.claim.MachineClaim;
import tavant.twms.domain.orgmodel.ServiceProvider;
import tavant.twms.domain.orgmodel.User;
import tavant.twms.infra.IntegrationTestCase;
import tavant.twms.infra.ProcessVariables;
import tavant.twms.process.ProcessDefinitionService;
import tavant.twms.process.ProcessService;
import tavant.twms.security.SelectedBusinessUnitsHolder;

public class WorkListServiceIntegrationTest extends IntegrationTestCase {

    WorkListService workListService;

    ProcessDefinitionService processDefinitionService;

    ProcessService processService;

    ClaimService claimService;

    public void testGetAllTasksForProcessor() throws IOException {
        User user = login("fboselli");

        ServiceProvider dummyServiceProvider = new ServiceProvider();
        dummyServiceProvider.setId(-1L);

        WorkListCriteria criteria = new WorkListCriteria(user);
        criteria.setProcess("ClaimSubmission");
        criteria.setServiceProvider(dummyServiceProvider);
        criteria.setServiceProviderList(user.getBelongsToOrganizations());

        Map<String, Long> taskListBeforeDeployProcDef = transform(workListService.getAllTasks(criteria));

        // assert all processor tasks exist
        if (user.hasRole("processor")) {
            Set<String> taskNames = taskListBeforeDeployProcDef.keySet();
            assertTrue(taskNames.contains("Forwarded Internally"));
            assertTrue(taskNames.contains("Processor Review"));
            assertTrue(taskNames.contains("Rejected Part Return"));
            assertTrue(taskNames.contains("Appeals"));
            assertTrue(taskNames.contains("On Hold"));
            assertTrue(taskNames.contains("On Hold For Part Return"));
            assertTrue(taskNames.contains("Replies"));
            assertTrue(taskNames.contains("Transferred"));
            assertTrue(taskNames.contains("Forwarded Externally"));
            assertTrue(taskNames.contains("Part Shipped Not Received"));
        }

        deployProcessDefinition("/ClaimSubmission/processdefinition.xml");

        Map<String, Long> taskListAfterDeployProcDef = transform(workListService.getAllTasks(criteria));

        // check if the tasklist fetched after deploying the process definition has
        // all the tasks that were there before deploying the process definition
        for (Map.Entry<String, Long> entry : taskListBeforeDeployProcDef.entrySet()) {
            assertTrue(taskListAfterDeployProcDef.containsKey(entry.getKey()));
            assertTrue(taskListAfterDeployProcDef.get(entry.getKey()).equals(entry.getValue()));
        }
    }

    public void testSRISwimlaneAssignment() throws IOException {
        User user = login("sedinap");
        SelectedBusinessUnitsHolder.setSelectedBusinessUnit("Thermo King TSA");

        deployProcessDefinition("/tavant/twms/worklist/test-processdefinition.xml");

        MachineClaim machineClaim = new MachineClaim();
        machineClaim.setServiceManagerRequest(false);
        ProcessVariables processVariables = new ProcessVariables();
        processVariables.setClaim(machineClaim);
        processService.startProcess("ClaimSubmission", processVariables);

        ServiceProvider dummyServiceProvider = new ServiceProvider();
        dummyServiceProvider.setId(-1L);

        WorkListCriteria criteria = new WorkListCriteria(user);
        criteria.setProcess("ClaimSubmission");
        criteria.setServiceProvider(dummyServiceProvider);
        criteria.setServiceProviderList(user.getBelongsToOrganizations());

        Map<String, Long> taskListBeforeDeployProcDef = transform(workListService.getAllTasks(criteria));
        assertTrue(1 == taskListBeforeDeployProcDef.get("Test Task"));
    }

    private void deployProcessDefinition(String procDefPath) throws IOException {
        Resource resource = new ClassPathResource(procDefPath);
        ProcessDefinition processDefinition = ProcessDefinition.parseXmlInputStream(resource.getInputStream());
        processDefinitionService.deploy(processDefinition);
    }

    private Map<String, Long> transform(Map<Task, Long> tasks) {
        // transform from "Task, Count" Map to "Task Name, Count" Map
        Map<String, Long> taskList = new HashMap<String, Long>();
        for (Map.Entry<Task, Long> entry : tasks.entrySet()) {
            taskList.put(entry.getKey().getName(), entry.getValue());
        }
        return taskList;
    }

    public void setWorkListService(WorkListService workListService) {
        this.workListService = workListService;
    }

    public void setProcessDefinitionService(ProcessDefinitionService processDefinitionService) {
        this.processDefinitionService = processDefinitionService;
    }

    public void setProcessService(ProcessService processService) {
        this.processService = processService;
    }

    public void setClaimService(ClaimService claimService) {
        this.claimService = claimService;
    }
}

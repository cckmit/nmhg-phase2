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

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.graph.exe.ProcessInstance;
import org.jbpm.taskmgmt.def.Task;
import org.jbpm.taskmgmt.exe.TaskInstance;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;

import tavant.twms.domain.catalog.CatalogRepository;
import tavant.twms.domain.catalog.ItemReference;
import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.claim.ClaimState;
import tavant.twms.domain.claim.MachineClaim;
import tavant.twms.domain.inventory.InventoryItem;
import tavant.twms.domain.inventory.InventoryItemRepository;
import tavant.twms.domain.orgmodel.OrgService;
import tavant.twms.infra.EngineRepositoryTestCase;
import tavant.twms.jbpm.WorkflowConstants;
import tavant.twms.process.ClaimProcessService;
import tavant.twms.process.ProcessDefinitionService;

import com.domainlanguage.time.CalendarDate;

public class WorkListDaoTest extends EngineRepositoryTestCase {

    private ProcessDefinitionService processDefinitionService;

    private ClaimProcessService claimProcessService;

    private OrgService orgService;

    private WorkListCriteria criteria;

    private WorkListDao workListDao;

    private WorkList workList;

    @SuppressWarnings("unused")
    private CatalogRepository catalogRepository;

    private InventoryItemRepository inventoryItemRepository;

    private final List<TaskInstance> createdTasks = new ArrayList<TaskInstance>();

    /**
     * @param catalogRepository the catalogRepository to set
     */
    @Required
    public void setCatalogRepository(CatalogRepository catalogRepository) {
        this.catalogRepository = catalogRepository;
    }

    /**
     * @param inventoryItemRepository the inventoryItemRepository to set
     */
    public void setInventoryItemRepository(InventoryItemRepository inventoryItemRepository) {
        this.inventoryItemRepository = inventoryItemRepository;
    }

    @Override
    protected void setUpInTxnRollbackOnFailure() throws Exception {
        super.setUpInTxnRollbackOnFailure();
        deploy("worklist-claimsubmit.xml");
        createMultipleClaims();
        this.criteria = new WorkListCriteria(this.orgService.findUserByName("ann"));
        this.criteria.setTaskName(WorkflowConstants.PENDING_PAYMENT_RESPONSE);
    }

    /**
     * Test for the method getWorkList() with no criteria's.
     */
    public void testGetWorkListNoCriterias() {
        this.workList = this.workListDao.getWorkList(this.criteria);
        assertNotNull(this.workList.getTaskList());
        assertEquals(5, this.workList.getTaskList().size());
    }

    /**
     * Test getWorkList() with a single sort criteria. The criteria is on the
     * taskInstance id and the sort order is descending.
     */
    public void testGetWorkListWithSingleSort() {
        this.criteria.addSortCriteria("taskInstance.id", false); // test for
        // descending
        this.workList = this.workListDao.getWorkList(this.criteria);
        assertNotNull(this.workList.getTaskList());

        long firstTaskId = this.workList.getTaskList().get(0).getId();
        long secondTaskId = this.workList.getTaskList().get(1).getId();
        assertTrue((firstTaskId > secondTaskId));
        assertEquals(5, this.workList.getTaskList().size());
    }

    /**
     * Test the method to get a WorkList with multiple sort criteria's.
     * Initially, the criteria is on the create date of the task instance with
     * the sort order as descending. Then a second sort criteria is added on the
     * task instance id with sort order as ascending. This criteria reverses the
     * result set from the previous one.
     */
    public void testGetWorkListWithMultipleSort() {
        // First let us sort on priority desc and id asc
        this.criteria.addSortCriteria("taskInstance.priority", false);
        this.criteria.addSortCriteria("taskInstance.id", true);
        // Now you will get task instance ids as 5,1,2,3,4
        this.workList = this.workListDao.getWorkList(this.criteria);
        List<TaskInstance> taskList = this.workList.getTaskList();
        assertNotNull(taskList);
        assertEquals(5, taskList.size());
        assertEquals(this.createdTasks.get(4).getId(), taskList.get(0).getId());
        assertEquals(this.createdTasks.get(0).getId(), taskList.get(1).getId());
        assertEquals(this.createdTasks.get(1).getId(), taskList.get(2).getId());
        assertEquals(this.createdTasks.get(2).getId(), taskList.get(3).getId());
        assertEquals(this.createdTasks.get(3).getId(), taskList.get(4).getId());
        // Now amongst these order by id desc
        this.criteria.addSortCriteria("taskInstance.id", false);
        // Now you must get taskinstance ids like 5,4,3,2,1
        this.workList = this.workListDao.getWorkList(this.criteria);
        taskList = this.workList.getTaskList();
        assertNotNull(taskList);
        assertEquals(5, taskList.size());
        assertEquals(this.createdTasks.get(4).getId(), taskList.get(0).getId());
        assertEquals(this.createdTasks.get(3).getId(), taskList.get(1).getId());
        assertEquals(this.createdTasks.get(2).getId(), taskList.get(2).getId());
        assertEquals(this.createdTasks.get(1).getId(), taskList.get(3).getId());
        assertEquals(this.createdTasks.get(0).getId(), taskList.get(4).getId());
    }

    /**
     * Test the method to get the WorkList with a single filter criteria. The
     * criteria is on the model of the Item of the Claim.
     */
    public void _disabled_testGetWorkListSingleFilter() {
        this.criteria.addFilterCriteria(
                "claim.itemReference.referredInventoryItem.ofType.model.name", "COU");
        this.workList = this.workListDao.getWorkList(this.criteria);
        Claim claim = (Claim) this.workList.getTaskList().get(0).getVariable("claim");
        assertNotNull(this.workList.getTaskList());
        assertEquals("COUGAR_50HZ", claim.getForItem().getOfType().getModel().getName());
        assertEquals(1, this.workList.getTaskList().size());
    }

    /*
     * For bug 125547. Commented for now.
     */
    public void _disabled_testGetWorkListSingleFilterWithSpecialChars() {
        this.criteria.addFilterCriteria(
                "claim.itemReference.referredInventoryItem.ofType.model.name", "'");
        this.workList = this.workListDao.getWorkList(this.criteria);
        assertEquals(0, this.workList.getTaskListCount());
        assertTrue(this.workList.getTaskList().isEmpty());
    }

    /**
     * This method is to test the getWorkList() with both a sort criteria as
     * well as a filter criteria.
     */
    public void _disabled_testGetWorkListWithSortAndFilter() {
        this.criteria.addFilterCriteria(
                "claim.itemReference.referredInventoryItem.ofType.model.name", "COU");
        this.criteria.addSortCriteria("taskInstance.name", false);
        this.workList = this.workListDao.getWorkList(this.criteria);
        Claim claim = (Claim) this.workList.getTaskList().get(0).getVariable("claim");
        assertNotNull(this.workList.getTaskList());
        assertEquals("COUGAR_50HZ", claim.getForItem().getOfType().getModel().getName());
        assertEquals(1, this.workList.getTaskList().size());
    }

    /**
     * Test for the page size criteria. The page size is set to a value less
     * than the expected value of results. However the number of task instances
     * fetched should be equal to the page size.
     */
    public void testGetWorkListWithSmallPageSize() {
        this.criteria.getPageSpecification().setPageSize(3);
        this.workList = this.workListDao.getWorkList(this.criteria);
        assertNotNull(this.workList.getTaskList());
        assertEquals(3, this.workList.getTaskList().size());
        assertEquals(5, this.workList.getTaskListCount());
    }

    /**
     * Test for getting all the tasks along with the count for a particular
     * user.
     */
    public void testGetAllTasks() {
        // swimlane - processor
        this.criteria = new WorkListCriteria(this.orgService.findUserByName("ann"));
        Map<Task, Long> tasks = this.workListDao.getAllTasks(this.criteria);
        assertEquals(1, tasks.size()); // Returns a single Task with count of 5
        assertEquals(Long.valueOf(5l), tasks.get(tasks.keySet().iterator().next()));
        // swimlane - dealer
        this.criteria = new WorkListCriteria(this.orgService.findUserByName("bishop"));
        tasks = this.workListDao.getAllTasks(this.criteria);
        assertEquals(2, tasks.size()); // Returns a single Task with count of 5
        Set<Task> setOfTasks = tasks.keySet();
        for (Task task : setOfTasks) {
            assertEquals(Long.valueOf(0l), tasks.get(task));
        }
        // swimlane - dsm
        this.criteria = new WorkListCriteria(this.orgService.findUserByName("phil"));
        tasks = this.workListDao.getAllTasks(this.criteria);
        assertEquals(1, tasks.size()); // Returns a single Task with count of 5
        assertEquals(Long.valueOf(0l), tasks.get(tasks.keySet().iterator().next()));
    }

    /**
     * Method to set up five dummy claim objects to create their corresponding
     * task instances.
     * @throws Exception
     */
    public void createMultipleClaims() throws Exception {
        int noOfClaims = 4;
        for (int i = 1; i <= noOfClaims; i++) {
            Claim claim = new MachineClaim();
            claim.setHoursInService(10);
            claim.setState(ClaimState.SUBMITTED);
            claim.setFailureDate(CalendarDate.date(2006, 6, 6));
            claim.setRepairDate(CalendarDate.date(2006, 7, 9));
            claim.setForDealerShip(this.orgService.findDealerByName("A-L-L EQUIPMENT "));

            // Set the inventory item.
            Assert.notNull(claim.getHoursInService());
            ProcessInstance processInstance = this.claimProcessService.startClaimProcessing(claim);
            TaskInstance taskInstance = ((TaskInstance) (processInstance.getTaskMgmtInstance()
                    .getTaskInstances().iterator().next()));
            // set a priority of 4 to show some difference
            this.createdTasks.add(taskInstance);
            taskInstance.setPriority(4);
        }
        Claim claim = new MachineClaim();
        claim.setHoursInService(10);
        claim.setState(ClaimState.SUBMITTED);

        InventoryItem invItem = this.inventoryItemRepository.findSerializedItem("LX3742");
        claim.setFailureDate(CalendarDate.date(2006, 6, 11));
        claim.setRepairDate(CalendarDate.date(2006, 7, 13));
        claim.setForDealerShip(this.orgService.findDealerByName("A-L-L EQUIPMENT "));
        claim.setItemReference(new ItemReference(invItem));
        ProcessInstance processInstance = this.claimProcessService.startClaimProcessing(claim);
        TaskInstance taskInstance = ((TaskInstance) (processInstance.getTaskMgmtInstance()
                .getTaskInstances().iterator().next()));
        // Set the last claims priority to be 5
        taskInstance.setPriority(5);
        this.createdTasks.add(taskInstance);
    }

    protected void deploy(String process) {
        InputStream prodessDefinitionStream = WorkListDaoTest.class.getResourceAsStream(process);
        final ProcessDefinition processDefnition = ProcessDefinition
                .parseXmlInputStream(prodessDefinitionStream);
        this.processDefinitionService.deploy(processDefnition);
    }

    // Getters and Setters
    public void setProcessDefinitionService(ProcessDefinitionService processDefinitionService) {
        this.processDefinitionService = processDefinitionService;
    }

    public void setClaimProcessService(ClaimProcessService claimProcessService) {
        this.claimProcessService = claimProcessService;
    }

    public void setOrgService(OrgService orgService) {
        this.orgService = orgService;
    }

    public void setWorkListDao(WorkListDao workListDao) {
        this.workListDao = workListDao;
    }
}

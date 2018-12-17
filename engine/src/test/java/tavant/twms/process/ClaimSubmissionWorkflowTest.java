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
package tavant.twms.process;

import java.io.InputStream;
import java.math.BigDecimal;
import java.util.TimeZone;

import org.jbpm.context.exe.ContextInstance;
import org.jbpm.graph.def.Node;
import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.graph.exe.ProcessInstance;
import org.jbpm.graph.exe.Token;
import org.springframework.core.io.ClassPathResource;

import tavant.twms.domain.catalog.Item;
import tavant.twms.domain.catalog.ItemReference;
import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.claim.Job;
import tavant.twms.domain.claim.LaborDetail;
import tavant.twms.domain.claim.MachineClaim;
import tavant.twms.domain.claim.OEMPartReplaced;
import tavant.twms.domain.claim.ServiceDetail;
import tavant.twms.domain.claim.ServiceInformation;
import tavant.twms.domain.claim.TravelDetail;
import tavant.twms.domain.claim.payment.CostCategory;
import tavant.twms.domain.claim.payment.CostCategoryRepository;
import tavant.twms.domain.claim.payment.Payment;
import tavant.twms.domain.inventory.InventoryItem;
import tavant.twms.domain.inventory.InventoryService;
import tavant.twms.domain.inventory.InventoryType;
import tavant.twms.domain.orgmodel.Dealership;
import tavant.twms.domain.policy.ApplicablePolicy;
import tavant.twms.infra.EngineRepositoryTestCase;
import tavant.twms.infra.ProcessVariables;
import tavant.twms.rules.model.RuleRepository;

import com.domainlanguage.money.Money;
import com.domainlanguage.time.CalendarDate;
import com.domainlanguage.timeutil.Clock;

public class ClaimSubmissionWorkflowTest extends EngineRepositoryTestCase {
    ProcessDefinition processDefinition, subProcessDefinition;

    ProcessDefinitionService processDefinitionService;

    ProcessService processService;

    RuleRepository ruleRepository;

    Claim claim;

    CostCategoryRepository costCategoryRepository;

    InventoryService inventoryService;

    public ClaimSubmissionWorkflowTest() {
        Clock.setDefaultTimeZone(TimeZone.getDefault());
        Clock.timeSource();
    }

    public void setCostCategoryRepository(CostCategoryRepository costCategoryRepository) {
        this.costCategoryRepository = costCategoryRepository;
    }

    public void setRuleRepository(RuleRepository ruleRepository) {
        this.ruleRepository = ruleRepository;
    }

    @Override
    protected void setUpInTxnRollbackOnFailure() throws Exception {
        super.setUpInTxnRollbackOnFailure();

        // Deploy the policy applicability and payment computation sub
        // processes.
        ClassPathResource resource = new ClassPathResource("ClaimPaymentServiceSubProcess.xml",
                getClass());
        InputStream processDefinitionStream = resource.getInputStream();
        assertNotNull(processDefinitionStream);

        this.subProcessDefinition = ProcessDefinition.parseXmlInputStream(processDefinitionStream);
        this.processDefinitionService.deploy(this.subProcessDefinition);

        resource = new ClassPathResource("ClaimAdjudicationProcess.xml", getClass());
        processDefinitionStream = resource.getInputStream();
        assertNotNull(processDefinitionStream);

        this.subProcessDefinition = ProcessDefinition.parseXmlInputStream(processDefinitionStream);
        this.processDefinitionService.deploy(this.subProcessDefinition);

        // Deploy our ClaimSubmission process
        resource = new ClassPathResource("ClaimSubmissionWithRefactoredNodes.xml", getClass());
        processDefinitionStream = resource.getInputStream();
        assertNotNull(processDefinitionStream);

        this.processDefinition = ProcessDefinition.parseXmlInputStream(processDefinitionStream);
        this.processDefinitionService.deploy(this.processDefinition);

        // setup code from claim rules test cases
        this.claim = new MachineClaim();
        this.claim.setFailureDate(Clock.today().plusDays(-9));
        this.claim.setRepairDate(Clock.today().plusDays(-2));
        this.claim.setFiledOnDate(Clock.today());
        ApplicablePolicy policy = new ApplicablePolicy();
        policy.setId(new Long(1));
        this.claim.setApplicablePolicy(policy);

        ServiceInformation si = new ServiceInformation();
        this.claim.setServiceInformation(si);

        ServiceDetail sd = new ServiceDetail();
        si.setServiceDetail(sd);

        final InventoryItem inventoryItem = this.inventoryService.findSerializedItem("ABCD123456");
        this.claim.setItemReference(new ItemReference(inventoryItem));

        Payment payment = new Payment();
        this.claim.setPayment(payment);
        CostCategory category = this.costCategoryRepository.findCostCategoryByCode("OEM_PARTS");
        //payment.addNewComponent(category);

        category = this.costCategoryRepository.findCostCategoryByCode("LABOR");
        //payment.addNewComponent(category);

        category = this.costCategoryRepository.findCostCategoryByCode("TRAVEL");
        //payment.addNewComponent(category);
    }

    public void testWhiteBox() {
        assertTrue(true);
    }

    public void testWorkflowCheckPolicyPaymentApplicabilityExecution() throws Exception {
        try {
            ProcessVariables processVariables = new ProcessVariables();
            this.claim.setHoursInService(1500);
            this.claim.setFailureDate(CalendarDate.date(2006, 7, 25));
            processVariables.setClaim(this.claim);
            assertNull(this.claim.getOtherComments());
            ProcessInstance processInstance = this.processService.startProcess("ClaimSubmission",
                    processVariables);
            assertEquals(this.processDefinition.getNode("end"), processInstance.getRootToken()
                    .getNode());

            // In addition to the above assert that policy applicability has
            // been computed
            assertNotNull(this.claim.getApplicablePolicy());
            assertEquals("STD-01", this.claim.getApplicablePolicy().getCode());

            // Check if there is some payment info also
            assertNotNull(this.claim.getPayment());
            assertTrue(this.claim.getPayment().getClaimedAmount().isZero());
        } catch (Exception e) {
        	e.printStackTrace();
            fail("Threw unexpected exception");
        }
    }

    public void setInventoryService(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    public void setProcessDefinitionService(ProcessDefinitionService processDefinitionService) {
        this.processDefinitionService = processDefinitionService;
    }

    public void setProcessService(ProcessService processService) {
        this.processService = processService;
    }

    public void XtestInvalidInputs_RepairDateEarlierThanFailureDate() throws Exception {
        this.claim.setFailureDate(Clock.today());
        this.claim.setRepairDate(Clock.today().plusDays(-3));

        ProcessInstance processInstance = this.processDefinition.createProcessInstance();
        ContextInstance contextInstance = processInstance.getContextInstance();
        contextInstance.setVariable("claim", this.claim);

        Token rootToken = processInstance.getRootToken();
        rootToken.signal();
        Node node = this.processDefinition.getNode("ManualClaimAdjudication");
        assertEquals(node.getName(), rootToken.getNode().getName());
    }

    public void XtestInvalidInputs_JobPerformed_ButNoHoursSpent() throws Exception {
        LaborDetail laborDetail = new LaborDetail();
        laborDetail.setJobPerformed(new Job());
        laborDetail.setHoursSpent(new BigDecimal(0));
        this.claim.getServiceInformation().getServiceDetail().addLaborDetail(laborDetail);

        ProcessInstance processInstance = this.processDefinition.createProcessInstance();
        ContextInstance contextInstance = processInstance.getContextInstance();
        contextInstance.setVariable("claim", this.claim);

        Token rootToken = processInstance.getRootToken();
        rootToken.signal();
        Node node = this.processDefinition.getNode("ManualClaimAdjudication");
        assertEquals(node.getName(), rootToken.getNode().getName());
    }

    public void XtestInvalidInputs_NoJobPerformed_ButHoursSpent() throws Exception {
        LaborDetail laborDetail = new LaborDetail();
        laborDetail.setHoursSpent(new BigDecimal(10));
        this.claim.getServiceInformation().getServiceDetail().addLaborDetail(laborDetail);

        ProcessInstance processInstance = this.processDefinition.createProcessInstance();
        ContextInstance contextInstance = processInstance.getContextInstance();
        contextInstance.setVariable("claim", this.claim);

        Token rootToken = processInstance.getRootToken();
        rootToken.signal();
        Node node = this.processDefinition.getNode("ManualClaimAdjudication");
        assertEquals(node.getName(), rootToken.getNode().getName());
    }

    public void XtestInvalidInputs_InvalidTravelDetails() throws Exception {
        TravelDetail travelDetail = new TravelDetail();
        travelDetail.setTrips(1);
        travelDetail.setDistance(new BigDecimal(100));
        travelDetail.setHours(new BigDecimal(0));
        this.claim.getServiceInformation().getServiceDetail().setTravelDetails(travelDetail);

        ProcessInstance processInstance = this.processDefinition.createProcessInstance();
        ContextInstance contextInstance = processInstance.getContextInstance();
        contextInstance.setVariable("claim", this.claim);

        Token rootToken = processInstance.getRootToken();
        rootToken.signal();
        Node node = this.processDefinition.getNode("ManualClaimAdjudication");
        assertEquals(node.getName(), rootToken.getNode().getName());
    }

    // TODO: Need to think about this test, 'computePolicyApplicability' defines
    // TODO: expressions for object assertion in working memory. These
    // expression evaluations
    // TODO: break when claim.forItem happens to be null ( which is a genuine
    // scenario )
    // TODO: the check needs to run before computePolicyApplicability executes.
    public void XXtestInvalidInputs_InventoryItemNotSpecified() throws Exception {
        this.claim.setItemReference(new ItemReference((Item) null));

        ProcessInstance processInstance = this.processDefinition.createProcessInstance();
        ContextInstance contextInstance = processInstance.getContextInstance();
        contextInstance.setVariable("claim", this.claim);

        Token rootToken = processInstance.getRootToken();
        rootToken.signal();
        Node node = this.processDefinition.getNode("ManualClaimAdjudication");
        assertEquals(node.getName(), rootToken.getNode().getName());
    }

    public void XtestSetupChecks_PartPriceNotSet() throws Exception {

        OEMPartReplaced oEMPartReplaced = new OEMPartReplaced();
        final Item item = new Item();
        item.setId(new Long(2));
        oEMPartReplaced.setItemReference(new ItemReference(item));

        this.claim.getServiceInformation().getServiceDetail().addOEMPartReplaced(oEMPartReplaced);

        ProcessInstance processInstance = this.processDefinition.createProcessInstance();
        ContextInstance contextInstance = processInstance.getContextInstance();
        contextInstance.setVariable("claim", this.claim);

        Token rootToken = processInstance.getRootToken();
        rootToken.signal();
        Node node = this.processDefinition.getNode("ManualClaimAdjudication");
        assertEquals(node.getName(), rootToken.getNode().getName());
    }

    public void XtestSetupChecks_LaborRateNull() throws Exception {
        LaborDetail laborDetail = new LaborDetail();
        laborDetail.setHoursSpent(new BigDecimal(10));
        laborDetail.setJobPerformed(new Job());
        laborDetail.setLaborRate(null);
        this.claim.getServiceInformation().getServiceDetail().addLaborDetail(laborDetail);

        ProcessInstance processInstance = this.processDefinition.createProcessInstance();
        ContextInstance contextInstance = processInstance.getContextInstance();
        contextInstance.setVariable("claim", this.claim);

        Token rootToken = processInstance.getRootToken();
        rootToken.signal();
        Node node = this.processDefinition.getNode("ManualClaimAdjudication");
        assertEquals(node.getName(), rootToken.getNode().getName());
    }

    public void XtestSetupChecks_LaborRateZero() throws Exception {
        LaborDetail laborDetail = new LaborDetail();
        laborDetail.setHoursSpent(new BigDecimal(10));
        laborDetail.setJobPerformed(new Job());

        this.claim.getServiceInformation().getServiceDetail().addLaborDetail(laborDetail);

        ProcessInstance processInstance = this.processDefinition.createProcessInstance();
        ContextInstance contextInstance = processInstance.getContextInstance();
        contextInstance.setVariable("claim", this.claim);

        Token rootToken = processInstance.getRootToken();
        rootToken.signal();
        Node node = this.processDefinition.getNode("ManualClaimAdjudication");
        assertEquals(node.getName(), rootToken.getNode().getName());
    }

    public void XtestHighValueClaimChecks_OEMPartsTotalIsHigh() throws Exception {
        //this.claim.getPayment().getComponent(
                //this.costCategoryRepository.findCostCategoryByCode("OEM_PARTS")).setClaimedAmount(
                //Money.dollars(1001));

        ProcessInstance processInstance = this.processDefinition.createProcessInstance();
        ContextInstance contextInstance = processInstance.getContextInstance();
        contextInstance.setVariable("claim", this.claim);

        Token rootToken = processInstance.getRootToken();
        rootToken.signal();
        Node node = this.processDefinition.getNode("ManualClaimAdjudication");
        assertEquals(node.getName(), rootToken.getNode().getName());
    }

    public void XtestHighValueClaimChecks_LaborCostIsHigh() throws Exception {
        //this.claim.getPayment().getComponent(
                //this.costCategoryRepository.findCostCategoryByCode("LABOR")).setClaimedAmount(
                //Money.dollars(251));

        ProcessInstance processInstance = this.processDefinition.createProcessInstance();
        ContextInstance contextInstance = processInstance.getContextInstance();
        contextInstance.setVariable("claim", this.claim);

        Token rootToken = processInstance.getRootToken();
        rootToken.signal();
        Node node = this.processDefinition.getNode("ManualClaimAdjudication");
        assertEquals(node.getName(), rootToken.getNode().getName());
    }

    public void XtestHighValueClaimChecks_TraveCostIsHigh() throws Exception {
        //this.claim.getPayment().getComponent(
                //this.costCategoryRepository.findCostCategoryByCode("TRAVEL")).setClaimedAmount(
                //Money.dollars(251));

        ProcessInstance processInstance = this.processDefinition.createProcessInstance();
        ContextInstance contextInstance = processInstance.getContextInstance();
        contextInstance.setVariable("claim", this.claim);

        Token rootToken = processInstance.getRootToken();
        rootToken.signal();
        Node node = this.processDefinition.getNode("ManualClaimAdjudication");
        assertEquals(node.getName(), rootToken.getNode().getName());
    }

    public void XtestValidityChecks_FiledTooLateAndSMRNotRequested() throws Exception {
        this.claim.setFiledOnDate(this.claim.getRepairDate().plusDays(6));
        ProcessInstance processInstance = this.processDefinition.createProcessInstance();
        ContextInstance contextInstance = processInstance.getContextInstance();
        contextInstance.setVariable("claim", this.claim);

        Token rootToken = processInstance.getRootToken();
        rootToken.signal();
        Node node = this.processDefinition.getNode("ManualClaimAdjudication");
        assertEquals(node.getName(), rootToken.getNode().getName());
    }

    public void XtestReviewChecks_DealerOnWatchList() throws Exception {
        Dealership dealer = new Dealership();
        dealer.setId(new Long(7));
        this.claim.setForDealerShip(dealer);

        ProcessInstance processInstance = this.processDefinition.createProcessInstance();
        ContextInstance contextInstance = processInstance.getContextInstance();
        contextInstance.setVariable("claim", this.claim);

        Token rootToken = processInstance.getRootToken();
        rootToken.signal();
        Node node = this.processDefinition.getNode("ManualClaimAdjudication");
        assertEquals(node.getName(), rootToken.getNode().getName());
    }

    public void XtestReviewChecks_DealerNotOnWatchList() throws Exception {
        Dealership dealer = new Dealership();
        dealer.setId(new Long(5));
        this.claim.setForDealerShip(dealer);

        ProcessInstance processInstance = this.processDefinition.createProcessInstance();
        ContextInstance contextInstance = processInstance.getContextInstance();
        contextInstance.setVariable("claim", this.claim);

        Token rootToken = processInstance.getRootToken();
        rootToken.signal();
        Node node = this.processDefinition.getNode("end");
        assertEquals(node.getName(), rootToken.getNode().getName());
    }

    public void XtestReviewChecks_PartReplacedOnWatchList() throws Exception {
        OEMPartReplaced oEMPartReplaced = new OEMPartReplaced();
        final Item item = new Item();
        item.setId(new Long(3));
        oEMPartReplaced.setItemReference(new ItemReference(item));
        this.claim.getServiceInformation().getServiceDetail().addOEMPartReplaced(oEMPartReplaced);

        ProcessInstance processInstance = this.processDefinition.createProcessInstance();
        ContextInstance contextInstance = processInstance.getContextInstance();
        contextInstance.setVariable("claim", this.claim);

        Token rootToken = processInstance.getRootToken();
        rootToken.signal();
        Node node = this.processDefinition.getNode("ManualClaimAdjudication");
        assertEquals(node.getName(), rootToken.getNode().getName());
    }

    public void XtestWarrantyChecks_Repaired30DaysAfterFailure() throws Exception {
        this.claim.setFailureDate(Clock.today().plusDays(-38));
        this.claim.setRepairDate(this.claim.getFailureDate().plusDays(31));
        this.claim.setFiledOnDate(this.claim.getRepairDate().plusDays(4));

        ProcessInstance processInstance = this.processDefinition.createProcessInstance();
        ContextInstance contextInstance = processInstance.getContextInstance();
        contextInstance.setVariable("claim", this.claim);

        Token rootToken = processInstance.getRootToken();
        rootToken.signal();
        Node node = this.processDefinition.getNode("ManualClaimAdjudication");
        assertEquals(node.getName(), rootToken.getNode().getName());
    }

    // TODO: Need to think about this test, 'computePolicyApplicability' defines
    // the
    // TODO: a policy and this check runs afer it. We need to make the first
    // fail,
    // TODO: for the applicablePolicy==null condition to be true. :-?
    public void XXtestRetailedInventoryChecks_PolicyNotSpecified() throws Exception {
        this.claim.getForItem().setType(new InventoryType("RETAIL"));
        this.claim.setApplicablePolicy(null);

        ProcessInstance processInstance = this.processDefinition.createProcessInstance();
        ContextInstance contextInstance = processInstance.getContextInstance();
        contextInstance.setVariable("claim", this.claim);

        Token rootToken = processInstance.getRootToken();
        rootToken.signal();
        Node node = this.processDefinition.getNode("ManualClaimAdjudication");
        assertEquals(node.getName(), rootToken.getNode().getName());
    }

    public void XtestStockedInventoryChecks_PolicyNotSpecified() throws Exception {
        this.claim.getForItem().setType(new InventoryType("STOCK"));
        this.claim.setServiceManagerRequest(false);
        this.claim.setHoursInService(70);

        ProcessInstance processInstance = this.processDefinition.createProcessInstance();
        ContextInstance contextInstance = processInstance.getContextInstance();
        contextInstance.setVariable("claim", this.claim);

        Token rootToken = processInstance.getRootToken();
        rootToken.signal();
        Node node = this.processDefinition.getNode("ManualClaimAdjudication");
        assertEquals(node.getName(), rootToken.getNode().getName());
    }
}

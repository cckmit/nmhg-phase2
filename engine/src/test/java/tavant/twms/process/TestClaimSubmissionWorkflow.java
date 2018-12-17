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
import java.util.TimeZone;

import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.graph.exe.ProcessInstance;
import org.springframework.core.io.ClassPathResource;

import tavant.twms.domain.catalog.ItemReference;
import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.claim.MachineClaim;
import tavant.twms.domain.claim.ServiceDetail;
import tavant.twms.domain.claim.ServiceInformation;
import tavant.twms.domain.claim.payment.CostCategory;
import tavant.twms.domain.claim.payment.CostCategoryRepository;
import tavant.twms.domain.claim.payment.LineItemGroup;
import tavant.twms.domain.claim.payment.Payment;
import tavant.twms.domain.inventory.InventoryItem;
import tavant.twms.domain.inventory.InventoryService;
import tavant.twms.infra.EngineRepositoryTestCase;
import tavant.twms.infra.ProcessVariables;
import tavant.twms.rules.model.RuleRepository;

import com.domainlanguage.money.Money;
import com.domainlanguage.time.CalendarDate;
import com.domainlanguage.timeutil.Clock;

public class TestClaimSubmissionWorkflow extends EngineRepositoryTestCase {
	ProcessDefinition processDefinition, subProcessDefinition;

	ProcessDefinitionService processDefinitionService;

	ProcessService processService;

	RuleRepository ruleRepository;

	Claim claim;

	CostCategoryRepository costCategoryRepository;

	InventoryService inventoryService;

	public TestClaimSubmissionWorkflow() {
		Clock.setDefaultTimeZone(TimeZone.getDefault());
		Clock.timeSource();
	}

	public void setCostCategoryRepository(
			CostCategoryRepository costCategoryRepository) {
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
		ClassPathResource resource = new ClassPathResource("test.xml",
				getClass());
		InputStream processDefinitionStream = resource.getInputStream();
		assertNotNull(processDefinitionStream);

		this.subProcessDefinition = ProcessDefinition
				.parseXmlInputStream(processDefinitionStream);
		this.processDefinitionService.deploy(this.subProcessDefinition);

		// resource = new ClassPathResource("ClaimAdjudicationProcess.xml",
		// getClass());
		// processDefinitionStream = resource.getInputStream();
		// assertNotNull(processDefinitionStream);
		//
		// this.subProcessDefinition =
		// ProcessDefinition.parseXmlInputStream(processDefinitionStream);
		// this.processDefinitionService.deploy(this.subProcessDefinition);
		//
		// // Deploy our ClaimSubmission process
		// resource = new
		// ClassPathResource("ClaimSubmissionWithRefactoredNodes.xml",
		// getClass());
		// processDefinitionStream = resource.getInputStream();
		// assertNotNull(processDefinitionStream);
		//
		// this.processDefinition =
		// ProcessDefinition.parseXmlInputStream(processDefinitionStream);
		// this.processDefinitionService.deploy(this.processDefinition);

		// setup code from claim rules test cases
		this.claim = new MachineClaim();
		this.claim.setFailureDate(Clock.today().plusDays(-9));
		this.claim.setRepairDate(Clock.today().plusDays(-2));
		this.claim.setFiledOnDate(Clock.today());
//		ApplicablePolicy policy = new ApplicablePolicy();
//		policy.setId(new Long(1));
//		this.claim.setApplicablePolicy(policy);

		ServiceInformation si = new ServiceInformation();
		this.claim.setServiceInformation(si);

		ServiceDetail sd = new ServiceDetail();
		si.setServiceDetail(sd);

		final InventoryItem inventoryItem = this.inventoryService
				.findSerializedItem("ABCD123456");
		this.claim.setItemReference(new ItemReference(inventoryItem));

		Payment payment = new Payment();
		this.claim.setPayment(payment);
		payment.addLineItemGroup("Club Car Parts");
		LineItemGroup lineItemGroup = payment.getLineItemGroup("Club Car Parts");
		lineItemGroup.setAcceptedTotal(Money.dollars(156));
		lineItemGroup.setGroupTotal(Money.dollars(156));
		lineItemGroup.setId(1L);
		lineItemGroup.setVersion(0);
		CostCategory category = this.costCategoryRepository
				.findCostCategoryByCode("OEM_PARTS");
		//payment.addNewComponent(category);

		category = this.costCategoryRepository.findCostCategoryByCode("LABOR");
		//payment.addNewComponent(category);

//		category = this.costCategoryRepository.findCostCategoryByCode("TRAVEL");
//		payment.addNewComponent(category);
	}

	public void testWhiteBox() {
		assertTrue(true);
	}

	public void testWorkflowCheckPolicyPaymentApplicabilityExecution()
			throws Exception {
		try {
			ProcessVariables processVariables = new ProcessVariables();
			this.claim.setHoursInService(1500);
			this.claim.setFailureDate(CalendarDate.date(2006, 7, 25));
			processVariables.setClaim(this.claim);
			assertNull(this.claim.getOtherComments());
			ProcessInstance processInstance = this.processService.startProcess(
					"Test", processVariables);
			processInstance.signal("goToNotifyPayment");
			Claim claim2 = processVariables.getClaim();
			System.out.println(claim2.getState());
			System.out.println(claim2.getPayment().isPaymentToBeMade());
			
		} catch (Exception e) {
			e.printStackTrace();
			fail("Threw unexpected exception");
		}
	}

	public void setInventoryService(InventoryService inventoryService) {
		this.inventoryService = inventoryService;
	}

	public void setProcessDefinitionService(
			ProcessDefinitionService processDefinitionService) {
		this.processDefinitionService = processDefinitionService;
	}

	public void setProcessService(ProcessService processService) {
		this.processService = processService;
	}

}

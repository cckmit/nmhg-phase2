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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.graph.exe.ProcessInstance;
import org.jbpm.graph.exe.Token;

import tavant.twms.domain.catalog.ItemReference;
import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.claim.MachineClaim;
import tavant.twms.domain.claim.OEMPartReplaced;
import tavant.twms.domain.claim.ServiceDetail;
import tavant.twms.domain.claim.ServiceInformation;
import tavant.twms.domain.inventory.InventoryItem;
import tavant.twms.domain.inventory.InventoryItemRepository;
import tavant.twms.domain.partreturn.PartReturn;
import tavant.twms.domain.partreturn.PaymentCondition;
import tavant.twms.infra.EngineRepositoryTestCase;
import tavant.twms.process.ProcessDefinitionService;

import com.domainlanguage.timeutil.Clock;

public class CloneTokenNodeTest extends EngineRepositoryTestCase {

	private ProcessDefinitionService processDefinitionService;

	private InventoryItemRepository inventoryItemRepository;

	String superProcessDefinitionString = "<process-definition name='main_process'>"
			+ "  <start-state>"
			+ "    <transition to='forkprocess' />"
			+ "  </start-state>"
			+ "  <fork name='forkprocess'>"
			+ "    <transition to='join' />"
			+ "    <transition name='multiprocess' to='triggersubprocess' repeat='true'>"
			+ "      <repetition-criteria>tavant.twms.jbpm.nodes.fork.PartsReturnBasedRepetitionCriteria</repetition-criteria>"
			+ "      <input-expression>claim.serviceInformation.serviceDetail.oEMPartsReplaced</input-expression>"
			+ "      <output-expression>part</output-expression>"
			+ "    </transition>"
			+ "  </fork>"
			+ "  <process-state name='triggersubprocess'>"
			+ "    <sub-process name='sub_process' />"
			+ "    <variable name='claim' access='read,write' mapped-name='claim' />"
			+ "    <variable name='part' access='read' mapped-name='part' />"
			+ "    <transition to='join' />"
			+ "  </process-state>"
			+ "  <join name='join'>"
			+ "    <transition to='end' />"
			+ "  </join>"
			+ "  <end-state name='end' />"
			+ "</process-definition>";

	String subProcessDefinitionString = "<process-definition name='sub_process'>"
			+ "<swimlane name='processor'>"
			+ "<assignment class='tavant.twms.jbpm.assignment.ExpressionAssignmentHandler'>"
			+ "<expression>actor=ann</expression>"
			+ "</assignment>"
			+ "</swimlane>"
			+ "  <start-state>"
			+ "    <transition to='isClaimAvailable' />"
			+ "  </start-state>"
			+ "<decision name='isClaimAvailable'>"
			+ "<transition name='No' to='ErrorState'> +"
			+ "<condition>#{claim != null and claim.id != 50}</condition>"
			+ "</transition>"
			+ "<transition name='Yes' to='waittask'>"
			+ "<condition>#{claim != null and claim.id == 50}</condition>"
			+ "</transition>"
			+ "</decision>"
			+ "  <task-node name='waittask'>"
			+ "    <task name='wait_task' swimlane='processor'>"
			+ "    </task>"
			+ "    <transition name='To Clone' to='Clone' />"
			+ "  </task-node>"
			+ "<clone-token-node name='Clone'>"
			+ "  <transition name='Notify Payment On Receipt' to='subprocessend' end-process-transition='true'>"
			+ "  </transition>"
			+ "  <transition name='Send for Inspection' to='isClaimStillAvailable'>"
			+ "  </transition>"
			+ "</clone-token-node>"
			+ "<decision name='isClaimStillAvailable'>"
			+ "<transition name='No' to='ErrorState'> +"
			+ "<condition>#{claim != null and claim.id != 50}</condition>"
			+ "</transition>"
			+ "<transition name='Yes' to='inspection'>"
			+ "<condition>#{claim != null and claim.id == 50}</condition>"
			+ "</transition>"
			+ "</decision>"
			+ "  <task-node name='inspection'>"
			+ "    <task name='inspection_task'  swimlane='processor'>"
			+ "    </task>"
			+ "    <transition to='supplyrecovery' />"
			+ "  </task-node>"
			+ "  <task-node name='supplyrecovery'>"
			+ "    <task name='supplyrecovery_task'  swimlane='processor'>"
			+ "    </task>"
			+ "    <transition to='subprocessend' />"
			+ "  </task-node>"
			+ "<end-state name='ErrorState'>"
			+ "</end-state>"
			+ "  <end-state name='subprocessend' />"
			+ "</process-definition>";

	@SuppressWarnings("unchecked")
	public void test() throws Exception {
		Claim claim = new MachineClaim();
		claim.setId(50L);
		claim.setFailureDate(Clock.today().plusDays(-30));
		claim.setRepairDate(Clock.today().plusDays(-5));
		ServiceInformation serviceInformation = new ServiceInformation();
		claim.setServiceInformation(serviceInformation);
		ServiceDetail serviceDetail = new ServiceDetail();
		serviceInformation.setServiceDetail(serviceDetail);

		InventoryItem invItem = inventoryItemRepository
				.findSerializedItem("LX3742");
		assertNotNull(invItem);
		OEMPartReplaced part1 = new OEMPartReplaced(new ItemReference(invItem),
				5);
		PartReturn return1 = new PartReturn();
		return1.setPaymentCondition(new PaymentCondition("PAY_ON_RECEIPT"));
		List<PartReturn> partReturns = new ArrayList<PartReturn>();
		partReturns.add(return1);
		part1.setPartReturns(partReturns);
		part1.setPartToBeReturned(true);
		part1.setNumberOfUnits(1);
		serviceDetail.addOEMPartReplaced(part1);

		ProcessDefinition subProcessDefinition = ProcessDefinition
				.parseXmlString(subProcessDefinitionString);
		processDefinitionService.deploy(subProcessDefinition);
		ProcessDefinition superProcessDefinition = ProcessDefinition
				.parseXmlString(superProcessDefinitionString);
		processDefinitionService.deploy(superProcessDefinition);
		ProcessDefinition def = processDefinitionService.find("main_process");
		ProcessInstance processInstance = new ProcessInstance(def);
		processInstance.getContextInstance().setVariable("claim", claim);
		processInstance.signal();

		Token rootToken = processInstance.getRootToken();
		assertEquals("forkprocess", rootToken.getNode().getName());

		Map childTokens = rootToken.getChildren();
		assertEquals(2, childTokens.size());
		Iterator iterator = childTokens.values().iterator();
		Token child1 = (Token) iterator.next();
		Token child2 = (Token) iterator.next();

		Token processStateToken = ("triggersubprocess".equals(child1.getNode()
				.getName())) ? child1 : child2;

		ProcessInstance subProcessInstance = processStateToken
				.getSubProcessInstance();
		Token subProcessToken = subProcessInstance.getRootToken();
		assertEquals("waittask", subProcessToken.getNode().getName());
		assertEquals("waittask", subProcessInstance.getRootToken().getNode()
				.getName());

		assertFalse(subProcessInstance.hasEnded());

		subProcessToken.getProcessInstance().signal();
		assertFalse(subProcessInstance.hasEnded());
		assertEquals("end", rootToken.getNode().getName());
		assertEquals("inspection", subProcessToken.getNode().getName());
		assertEquals("subprocessend", subProcessInstance.getRootToken()
				.getNode().getName());

		subProcessToken.signal();
		assertFalse(subProcessInstance.hasEnded());
		assertEquals("supplyrecovery", subProcessToken.getNode().getName());

		subProcessToken.signal();
		assertTrue(subProcessToken.hasEnded());
		assertEquals("subprocessend", subProcessToken.getNode().getName());
		assertEquals("end", rootToken.getNode().getName());
	}

	/**
	 * @return the processDefinitionService
	 */
	public ProcessDefinitionService getProcessDefinitionService() {
		return processDefinitionService;
	}

	/**
	 * @param processDefinitionService
	 *            the processDefinitionService to set
	 */
	public void setProcessDefinitionService(
			ProcessDefinitionService processDefinitionService) {
		this.processDefinitionService = processDefinitionService;
	}

	public void setInventoryItemRepository(
			InventoryItemRepository inventoryItemRepository) {
		this.inventoryItemRepository = inventoryItemRepository;
	}

}

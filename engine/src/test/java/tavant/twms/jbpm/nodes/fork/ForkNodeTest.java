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
package tavant.twms.jbpm.nodes.fork;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.graph.exe.ProcessInstance;
import org.jbpm.graph.exe.Token;

import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.claim.MachineClaim;
import tavant.twms.domain.claim.OEMPartReplaced;
import tavant.twms.domain.claim.ServiceDetail;
import tavant.twms.domain.claim.ServiceInformation;
import tavant.twms.domain.partreturn.PartReturn;
import tavant.twms.domain.partreturn.PaymentCondition;
import tavant.twms.infra.EngineRepositoryTestCase;
import tavant.twms.process.ProcessDefinitionService;

public class ForkNodeTest extends EngineRepositoryTestCase {

	private ProcessDefinitionService processDefinitionService;

	String superProcessDefinitionString = "<process-definition name='test_process'>"
			+ "  <start-state>"
			+ "    <transition to='multifork' />"
			+ "  </start-state>"
			+ "  <fork name='multifork'>"
			+ "    <transition to='join' />"
			+ "    <transition name='multiprocess' to='manual' repeat='true'>"
			+ "      <repetition-criteria>tavant.twms.jbpm.nodes.fork.PartsReturnBasedRepetitionCriteria</repetition-criteria>"
			+ "      <input-expression>claim.serviceInformation.serviceDetail.oEMPartsReplaced</input-expression>"
			+ "      <output-expression>part</output-expression>"
			+ "    </transition>"
			+ "  </fork>"
			+ "  <process-state name='manual'>"
			+ "    <sub-process name='manual_process' />"
			+ "    <variable name='claim' access='read,write' mapped-name='claim' />"
			+ "    <variable name='part' access='read' mapped-name='part' />"
			+ "    <transition to='join' />"
			+ "  </process-state>"
			+ "  <join name='join'>"
			+ "    <transition to='end' />"
			+ "  </join>"
			+ "  <end-state name='end' />"
			+ "</process-definition>";

	String subProcessDefinitionString = "<process-definition name='manual_process'>"
			+ "  <start-state>"
			+ "    <transition to='manual' />"
			+ "  </start-state>"
			+ "  <task-node name='manual'>"
			+ "    <task name='manual_task'>"
			+ "      <assignment actor-id='michael' />"
			+ "    </task>"
			+ "    <transition to='end' />"
			+ "  </task-node>"
			+ "  <end-state name='end' />" + "</process-definition>";

	@SuppressWarnings("unchecked")
	public void test() {
		Claim claim = new MachineClaim();
		ServiceInformation serviceInformation = new ServiceInformation();
		claim.setServiceInformation(serviceInformation);
		ServiceDetail serviceDetail = new ServiceDetail();
		serviceInformation.setServiceDetail(serviceDetail);

		OEMPartReplaced part1 = new OEMPartReplaced();
		PartReturn return1 = new PartReturn();
		return1.setPaymentCondition(new PaymentCondition("PAY_ON_RECEIPT"));
		List<PartReturn> partReturns = new ArrayList<PartReturn>();
		partReturns.add(return1);
		part1.setPartReturns(partReturns);
		part1.setPartToBeReturned(true);
		part1.setNumberOfUnits(1);
		serviceDetail.addOEMPartReplaced(part1);

		OEMPartReplaced part2 = new OEMPartReplaced();
		PartReturn return2 = new PartReturn();
		return2.setPaymentCondition(new PaymentCondition("PAY"));
		partReturns = new ArrayList<PartReturn>();
		partReturns.add(return2);
		part2.setPartReturns(partReturns);
		part2.setPartToBeReturned(true);
		part2.setNumberOfUnits(2);
		serviceDetail.addOEMPartReplaced(part2);

		OEMPartReplaced part3 = new OEMPartReplaced();
		PartReturn return3 = new PartReturn();
		return3.setPaymentCondition(new PaymentCondition("PAY"));
		partReturns = new ArrayList<PartReturn>();
		partReturns.add(return2);
		part3.setPartReturns(partReturns);
		part3.setPartToBeReturned(true);
		part3.setNumberOfUnits(3);
		serviceDetail.addOEMPartReplaced(part3);

		OEMPartReplaced part4 = new OEMPartReplaced();
		PartReturn return4 = new PartReturn();
		return4.setPaymentCondition(new PaymentCondition("PAY"));
		partReturns = new ArrayList<PartReturn>();
		partReturns.add(return2);
		part4.setPartReturns(partReturns);
		part4.setPartToBeReturned(true);
		part4.setNumberOfUnits(4);
		serviceDetail.addOEMPartReplaced(part4);

		OEMPartReplaced part5 = new OEMPartReplaced();
		PartReturn return5 = new PartReturn();
		return5.setPaymentCondition(new PaymentCondition("PAY"));
		partReturns = new ArrayList<PartReturn>();
		partReturns.add(return2);
		part5.setPartReturns(partReturns);
		part5.setPartToBeReturned(true);
		part5.setNumberOfUnits(5);
		serviceDetail.addOEMPartReplaced(part5);

		List<OEMPartReplaced> expectedParts = claim.getServiceInformation()
				.getServiceDetail().getOEMPartsReplaced();
		ProcessDefinition subProcessDefinition = ProcessDefinition
				.parseXmlString(subProcessDefinitionString);
		processDefinitionService.deploy(subProcessDefinition);
		ProcessDefinition superProcessDefinition = ProcessDefinition
				.parseXmlString(superProcessDefinitionString);
		processDefinitionService.deploy(superProcessDefinition);
		ProcessDefinition def = processDefinitionService.find("test_process");
		ProcessInstance processInstance = new ProcessInstance(def);
		processInstance.getContextInstance().setVariable("claim", claim);
		processInstance.signal();

		Token rootToken = processInstance.getRootToken();
		Map childTokensMap = rootToken.getActiveChildren();
		Collection<Token> childTokens = childTokensMap.values();
		List<OEMPartReplaced> actualParts = new ArrayList<OEMPartReplaced>();
		int joinNodeCount = 0, processNodeCount = 0;
		for (Token childToken : childTokens) {
			if (childToken.getNode().getName().equals("join")) {
				++joinNodeCount;
			} else {
				assertEquals("manual", childToken.getNode().getName());
				++processNodeCount;
				actualParts.add((OEMPartReplaced) childToken
						.getSubProcessInstance().getContextInstance()
						.getVariable("part"));
			}

		}
		assertEquals(1, joinNodeCount);
		assertEquals(5, processNodeCount);
		assertEquals(6, childTokens.size());
		// check if both collections are equal in some order
		for (OEMPartReplaced part : expectedParts) {
			assertTrue(actualParts.contains(part));
		}
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

	public boolean skipDataPopulation() {
		return true;
	}
}

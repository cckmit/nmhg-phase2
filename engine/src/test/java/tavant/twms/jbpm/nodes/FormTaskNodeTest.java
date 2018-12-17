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

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.graph.exe.ProcessInstance;
import org.jbpm.graph.exe.Token;

import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.inventory.InventoryService;
import tavant.twms.infra.EngineRepositoryTestCase;
import tavant.twms.infra.ProcessVariables;
import tavant.twms.process.ProcessDefinitionService;
import tavant.twms.process.ProcessService;
import tavant.twms.rules.model.RuleRepository;

public class FormTaskNodeTest extends EngineRepositoryTestCase {
    ProcessDefinition processDefinition;

    ProcessDefinitionService processDefinitionService;

    RuleRepository ruleRepository;

    ProcessService processService;

    InventoryService inventoryService;

    Claim claim;

    @Override
    protected void setUpInTxnRollbackOnFailure() throws Exception {
        // super.setUpInTxnRollbackOnFailure();
        InputStream prodessDefinitionStream = this.getClass().getResourceAsStream(
                "form-node-process.xml");
        assertNotNull(prodessDefinitionStream);
        processDefinition = ProcessDefinition.parseXmlInputStream(prodessDefinitionStream);
        processDefinitionService.deploy(processDefinition);
    }

    public void testSimpleDeploy() {
        ProcessInstance processInstance = processService.startProcess("FormNodeProcess",
                new ProcessVariables());
        Token token = processInstance.getRootToken();
        FormTaskNode node = (FormTaskNode) processDefinition.getNode("ManualClaimAdjudication");
        assertEquals(node, token.getNode());        
        assertEquals(node.getTasks().size(), 1);
        assertNotNull(node.getTask("Assign claim to processor"));
        Map<String, String> formNodes = node.getFormNodes();
        List<String> expectedNames = Arrays.asList(new String[] { "ManualClaimAdjudication.Input",
                "ManualClaimAdjudication.Success" });
        List<String> expectedTypes = Arrays.asList(new String[] { "inputForm",
                "successForm" });
        for(String key : formNodes.keySet()) {
            assertTrue(expectedTypes.contains(key));
            assertTrue(expectedNames.contains(formNodes.get(key)));
        }
        assertTrue(expectedNames.contains(node.getDefaultForm()));
        //also check if task has correct inverse relationship
        assertEquals(node.getTask("Assign claim to processor").getTaskNode(), node);
        
        //check out task nodes without forms elements
        FormTaskNode anotherNode = (FormTaskNode) processDefinition.getNode("ManualClaimAdjudicationWithoutForms");
        assertEquals(anotherNode.getTasks().size(), 1);
        assertTrue(anotherNode.getFormNodes().isEmpty());
    }

    public void setProcessDefinitionService(ProcessDefinitionService processDefinitionService) {
        this.processDefinitionService = processDefinitionService;
    }

    public void setRuleRepository(RuleRepository ruleRepository) {
        this.ruleRepository = ruleRepository;
    }

    public void setInventoryService(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    public void setProcessService(ProcessService processService) {
        this.processService = processService;
    }
}

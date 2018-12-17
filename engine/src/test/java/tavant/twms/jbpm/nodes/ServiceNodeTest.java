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

import com.domainlanguage.time.CalendarDate;
import com.domainlanguage.timeutil.Clock;

import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.graph.exe.ProcessInstance;
import org.jbpm.graph.exe.Token;
import tavant.twms.domain.catalog.Item;
import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.claim.MachineClaim;
import tavant.twms.domain.inventory.InventoryItem;
import tavant.twms.domain.inventory.InventoryService;
import tavant.twms.infra.EngineRepositoryTestCase;
import tavant.twms.process.ProcessDefinitionService;
import tavant.twms.rules.model.RuleRepository;
import tavant.twms.rules.model.RuleSet;

import java.io.InputStream;
import java.util.TimeZone;

public class ServiceNodeTest  extends EngineRepositoryTestCase{
    ProcessDefinition processDefinition;

    ProcessDefinitionService processDefinitionService;

    RuleRepository ruleRepository;

    InventoryService inventoryService;

    Long ruleId;

    Claim claim;

    public ServiceNodeTest() {
        Clock.setDefaultTimeZone(TimeZone.getDefault());
        Clock.timeSource();
    }

    @Override
    protected void setUpInTxnRollbackOnFailure() throws Exception {
        super.setUpInTxnRollbackOnFailure();
        RuleSet rule = new RuleSet("validate", "Simple vaildation rule", "Rule");
        rule.setPath("somePath");
        ruleRepository.save(rule);
        ruleId = rule.getId();
        InputStream prodessDefinitionStream = this.getClass().getResourceAsStream(
                "service-node-process.xml");
        assertNotNull(prodessDefinitionStream);
        processDefinition = ProcessDefinition.parseXmlInputStream(prodessDefinitionStream);
        processDefinitionService.deploy(processDefinition);
        InventoryItem item = new InventoryItem();
        item.setOfType(new Item());
        claim = new MachineClaim(item, CalendarDate.date(2006, 8, 25), CalendarDate.date(2006, 8, 26));
    }

    public void testProcess() {
        ProcessInstance processInstance = new ProcessInstance(processDefinition);

        processInstance.getContextInstance().setVariable("ruleId", ruleId);
        processInstance.getContextInstance().setVariable("claim", claim);

        Token token = processInstance.getRootToken();
        token.signal();
        assertEquals(processDefinition.getNode("accept"), token.getNode());
        assertEquals("validate", claim.getOtherComments());

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
}

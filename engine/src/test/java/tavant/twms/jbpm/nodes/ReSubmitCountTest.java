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

import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.graph.exe.ProcessInstance;
import org.jbpm.graph.exe.Token;

import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.claim.MachineClaim;
import tavant.twms.domain.inventory.InventoryService;
import tavant.twms.infra.EngineRepositoryTestCase;
import tavant.twms.process.ProcessDefinitionService;

import com.domainlanguage.time.CalendarDate;

public class ReSubmitCountTest extends EngineRepositoryTestCase {

    ProcessDefinition processDefinition;

    ProcessDefinitionService processDefinitionService;
    
    Claim claim;
    
    InventoryService inventoryService;
    
    @Override
    protected void setUpInTxnRollbackOnFailure() throws Exception {
        super.setUpInTxnRollbackOnFailure();
        InputStream prodessDefinitionStream = this.getClass().getResourceAsStream(
                "smr-submit-limit-check-process.xml");
        assertNotNull(prodessDefinitionStream);
        processDefinition = ProcessDefinition.parseXmlInputStream(prodessDefinitionStream);
        processDefinitionService.deploy(processDefinition);
        claim = new MachineClaim(inventoryService.findSerializedItem("ABCD123456"), CalendarDate.date(2005, 7, 13),
                CalendarDate.date(2005, 7, 15));
    }
    
    public void testSubmitLimit() {
        ProcessInstance processInstance = new ProcessInstance(processDefinition);
        processInstance.getContextInstance().setVariable("claim", claim);
        Token token = processInstance.getRootToken();
        token.signal();
        
        assertEquals(processDefinition.getNode("ClaimSubmit"), token.getNode());
        assertEquals(claim.getNoOfResubmits().intValue(), 0);
        
        //now submit once
        token.signal();
        assertEquals(processDefinition.getNode("ClaimApprove"), token.getNode());
        token.signal("Reject"); //reject it once
        assertEquals(processDefinition.getNode("ClaimSubmit"), token.getNode());
        assertEquals(claim.getNoOfResubmits().intValue(), 1);
        //now submit again
        token.signal();
        assertEquals(processDefinition.getNode("ClaimApprove"), token.getNode());
        token.signal("Reject"); //reject it again
        assertEquals(processDefinition.getNode("ClaimSubmit"), token.getNode());
        assertEquals(claim.getNoOfResubmits().intValue(), 2);
        //now submit it third time. It must run away
        token.signal();
        assertEquals(processDefinition.getNode("ClaimSubmit"), token.getNode());
        assertEquals(claim.getNoOfResubmits().intValue(), 3);
    }

    public void setInventoryService(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    public void setProcessDefinitionService(ProcessDefinitionService processDefinitionService) {
        this.processDefinitionService = processDefinitionService;
    }

    
}

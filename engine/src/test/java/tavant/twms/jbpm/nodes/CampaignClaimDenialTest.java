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

import tavant.twms.domain.claim.CampaignClaim;
import tavant.twms.domain.claim.Claim;
import tavant.twms.infra.EngineRepositoryTestCase;
import tavant.twms.process.ProcessDefinitionService;

public class CampaignClaimDenialTest extends EngineRepositoryTestCase {

    ProcessDefinition processDefinition;

    ProcessDefinitionService processDefinitionService;

    Claim claim;

    @Override
    protected void setUpInTxnRollbackOnFailure() throws Exception {
        super.setUpInTxnRollbackOnFailure();
        InputStream prodessDefinitionStream = this.getClass().getResourceAsStream(
                "campaign-claim-ondeny-process.xml");
        assertNotNull(prodessDefinitionStream);
        this.processDefinition = ProcessDefinition.parseXmlInputStream(prodessDefinitionStream);
        this.processDefinitionService.deploy(this.processDefinition);
        this.claim = new CampaignClaim();
        this.claim.setId(1L);
    }

    public void testClaimDenialFlow() {
        ProcessInstance processInstance = new ProcessInstance(this.processDefinition);
        processInstance.getContextInstance().setVariable("claim", this.claim);
        Token token = processInstance.getRootToken();
        token.signal();

        assertEquals(this.processDefinition.getNode("ClaimDeny"), token.getNode());

        token.signal();
        assertEquals("End", token.getNode().getName());
    }

    public void setProcessDefinitionService(ProcessDefinitionService processDefinitionService) {
        this.processDefinitionService = processDefinitionService;
    }

}

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
package tavant.twms.jbpm.decision.supplier.recovery;

import org.jbpm.graph.node.DecisionHandler;
import org.jbpm.graph.exe.ExecutionContext;

import tavant.twms.jbpm.infra.BeanLocator;
import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.supplier.contract.ContractService;

/**
 * @author kaustubhshobhan.b
 */
public class IsRecoverable implements DecisionHandler {

    private BeanLocator beanLocator = new BeanLocator();
    
    public String decide(ExecutionContext executionContext) throws Exception {
        Claim claim = (Claim)executionContext.getVariable("claim");
        ContractService contractService = (ContractService) beanLocator
				.lookupBean("contractService");
        if((!claim.getReopened()||claim.getReopenRecoveryClaim()) && contractService.isRecoverable(claim)){
            return "Yes";
        }else{
            return "No";
        }
    }
}

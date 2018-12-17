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

import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.graph.exe.ProcessInstance;

import tavant.twms.domain.catalog.ItemReference;
import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.claim.ClaimState;
import tavant.twms.domain.claim.MachineClaim;
import tavant.twms.domain.claim.PartsClaim;
import tavant.twms.domain.claim.ServiceDetail;
import tavant.twms.domain.claim.ServiceInformation;
import tavant.twms.domain.inventory.InventoryItem;
import tavant.twms.domain.inventory.InventoryService;
import tavant.twms.domain.inventory.ItemNotFoundException;
import tavant.twms.domain.orgmodel.DealershipRepository;
import tavant.twms.infra.ProcessDeployableTestCase;
import tavant.twms.infra.ProcessVariables;

import com.domainlanguage.time.CalendarDate;

public class ClaimPolicyPaymentWorkflowTest extends ProcessDeployableTestCase {
    
    ProcessService processService;
    
    ProcessDefinition processDefinition;
    
    InventoryService inventoryService;
    
    DealershipRepository dealershipRepository;
    
    Claim claim;
    @Override
    protected void setUpInTxnRollbackOnFailure() throws Exception {
        super.setUpInTxnRollbackOnFailure();
        this.processDefinition = deployProcess("ClaimPaymentServiceSubProcess.xml");
        //claim.setType("Machine");
        this.claim = newMachineClaim();
        this.claim.setHoursInService(1500);
        this.claim.setState(ClaimState.SUBMITTED);
        
        CalendarDate _12thDec2005 = CalendarDate.date(2005,12,12);
        this.claim.setFailureDate( _12thDec2005 );
        this.claim.setRepairDate( _12thDec2005.nextDay() );
        this.claim.setFiledOnDate( _12thDec2005.nextDay().nextDay() );
        InventoryItem inventoryItem = null;
        try {
            inventoryItem = this.inventoryService.findSerializedItem("LX3742");
        } catch (ItemNotFoundException e) {
            fail("");
        }
        this.claim.setItemReference(new ItemReference(inventoryItem));
        ServiceInformation si = new ServiceInformation();
        this.claim.setServiceInformation(si);
        
        
        ServiceDetail sd = new ServiceDetail();
        si.setServiceDetail(sd);
        
    }

    protected MachineClaim newMachineClaim() {
        MachineClaim machineClaim = new MachineClaim();
        machineClaim.setForDealerShip(this.dealershipRepository.findByDealerId(new Long(7)));
        return machineClaim;
    }
    
    
    
    public void setDealershipRepository(DealershipRepository dealershipRepository) {
        this.dealershipRepository = dealershipRepository;
    }

    public void testCheckPolicyComputationNotRequired() {
        ProcessVariables processVariables = new ProcessVariables();
        this.claim = new PartsClaim();
        this.claim.setForDealerShip(this.dealershipRepository.findByDealerId((long) 7));
        
        //claim.setType("Parts");
        this.claim.setHoursInService(1500);
        this.claim.setState(ClaimState.SUBMITTED);
        this.claim.setFailureDate(CalendarDate.date(2006,1,1));
        this.claim.setRepairDate(CalendarDate.date(2006,1,1));
        processVariables.setClaim(this.claim);
        ProcessInstance processInstance = this.processService
                .startProcess("PolicyAndPaymentComputationProcess", processVariables);
        assertEquals(this.processDefinition.getNode("End"), processInstance.getRootToken().getNode());
    }

    //TODO: [Vikas S] Temporarily disabling this to get multicar changes in. Need to look into this.
    public void _disable_testCheckPolicyComputationRequired() {
        ProcessVariables processVariables = new ProcessVariables();
        processVariables.setClaim(this.claim);
        ProcessInstance processInstance = this.processService
                .startProcess("PolicyAndPaymentComputationProcess", processVariables);
        assertEquals(this.processDefinition.getNode("End"), processInstance.getRootToken().getNode());
        assertNotNull(this.claim.getApplicablePolicy());
    }
    
    public void testCheckPolicyComputationNotSerializedItem() {
        ProcessVariables processVariables = new ProcessVariables();
        //claim.setType("Machine");
        InventoryItem inventoryItem = null;
        try {
            inventoryItem = this.inventoryService.findSerializedItem("LX3742");
        } catch (ItemNotFoundException e) {
            fail("");
        }
        ItemReference itemReference = new ItemReference(inventoryItem.getOfType());
        this.claim.setItemReference(itemReference);
        processVariables.setClaim(this.claim);
        ProcessInstance processInstance = this.processService
                .startProcess("PolicyAndPaymentComputationProcess", processVariables);
        assertEquals(this.processDefinition.getNode("End"), processInstance.getRootToken().getNode());
        assertNull(this.claim.getApplicablePolicy());
    }
    
    public void setProcessService(ProcessService processService) {
        this.processService = processService;
    }

    public void setInventoryService(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    
}

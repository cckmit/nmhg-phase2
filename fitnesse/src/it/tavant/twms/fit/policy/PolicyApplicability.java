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
package tavant.twms.fit.policy;

import tavant.twms.domain.catalog.CatalogService;
import tavant.twms.domain.catalog.ItemReference;
import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.claim.MachineClaim;
import tavant.twms.domain.inventory.InventoryItem;
import tavant.twms.domain.inventory.InventoryService;
import tavant.twms.domain.policy.Policy;
import tavant.twms.domain.policy.PolicyService;
import tavant.twms.fit.infra.BeanWiredColumnFixture;

import com.domainlanguage.time.CalendarDate;

/**
 * @author vineeth.varghese
 * @date Oct 27, 2006
 */
@SuppressWarnings("hiding")
public class PolicyApplicability extends BeanWiredColumnFixture {

    private PolicyService policyService;

    private InventoryService inventoryService;
    
    private boolean isUnderWarranty;

    public String serialNumber;

    public String registerationDate;

    public String failureDate;

    public String failureCode;
    
    public String installationDate;

    public String hoursInService;

    /**
     * @return the policyService
     */
    public PolicyService getPolicyService() {
        return policyService;
    }

    /**
     * @param policyService
     *            the policyService to set
     */
    public void setPolicyService(PolicyService policyService) {
        this.policyService = policyService;
    }

    public void setInventoryService(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    

    public void setUnderWarranty(
    boolean isUnderWarranty) {
        this.isUnderWarranty = isUnderWarranty;
    }

    @Override
    public void execute() throws Exception {
        InventoryItem inventoryItem = inventoryService.findSerializedItem(serialNumber);
        ItemReference itemReference = new ItemReference(inventoryItem);
        
        
        CalendarDate dateOfRegistration = getDate(registerationDate);
        inventoryItem.setRegistrationDate(dateOfRegistration);
        CalendarDate dateOfFailure = getDate(failureDate);
        
        Claim claim = new MachineClaim();
        claim.setFailureDate(dateOfFailure);
        claim.setItemReference(itemReference);
        
        claim.setHoursInService(Integer.valueOf(hoursInService).intValue());
        
        Policy policy = policyService.findApplicablePolicy(claim);
        isUnderWarranty = policy != null;
    }

    public boolean isUnderWarranty() {
        return isUnderWarranty;
    }
}

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
package tavant.twms.domain.policy;

import java.util.List;

import tavant.twms.domain.catalog.Item;
import tavant.twms.domain.catalog.ItemGroup;
import tavant.twms.domain.common.Label;
import tavant.twms.domain.inventory.InventoryItem;
import tavant.twms.domain.orgmodel.ServiceProvider;
import tavant.twms.infra.GenericRepository;
import tavant.twms.infra.ListCriteria;
import tavant.twms.infra.PageResult;

import com.domainlanguage.time.CalendarDate;

/**
 * @author radhakrishnan.j
 *
 *
 */
public interface PolicyDefinitionRepository extends GenericRepository<PolicyDefinition,Long>{

    public PageResult<PolicyDefinition> findAll(ListCriteria listCriteria);

    public List<PolicyDefinition> findPoliciesForProduct(ItemGroup product,CalendarDate asOfDate);
    
    public List<PolicyDefinition> findInvisiblePoliciesForProductCode(String productCode,CalendarDate asOfDate);

    public List<PolicyDefinition> findPoliciesForProductCode(String productCode,CalendarDate asOfDate);

    public List<PolicyDefinition> findPoliciesFor(Item item,CalendarDate asOfDate);

    public List<PolicyDefinition> findPoliciesForItem(String itemNumber,CalendarDate asOfDate);
    
    public List<PolicyDefinition> findInvisiblePoliciesForItem(String itemNumber,CalendarDate asOfDate);
    
    public List<PolicyDefinition> findApplicablePolicyForClaimedPart(String itemNumber,CalendarDate asOfDate);   

    public List<PolicyDefinition> findAllExtendedPolicies(boolean isInternal);
    
    public List<PolicyDefinition> findExtendedPoliciesForInventory(String serialNumber,CalendarDate asOfDate);

    public List<PolicyDefinition> findExistingPoliciesForUsedItem(InventoryItem inventoryItem,CalendarDate asOfDate);

    public List<PolicyDefinition> findTransferablePoliciesForInventoryItem(InventoryItem inventoryItem,CalendarDate asOfDate);

    public boolean isCodeUnique(PolicyDefinition policyDefinition);


    public List<PolicyDefinition> findPoliciesForAdminWarrantyReg(InventoryItem inventoryItem);
    
    public List<PolicyDefinition> findInvisiblePoliciesForAdminWarrantyReg(InventoryItem inventoryItem);    
    

    List<String> findPolicyDefinitionCodesStartingWith(String codePrefix, int pageNumber, int pageSize);

    PolicyDefinition findPolicyDefinitionByCode(String code);

    public List<PolicyDefinition> findPolicyDefinitionsForLabel(Label label);

    public List<PolicyDefinition> getAllGoodWillPolicies();
    
    public PolicyDefinition findPolicyDefinitionWithPriority(final Long priority);

    public List<PolicyDefinition> findGoodWillPoliciesForInventory(String serialNumber,CalendarDate asOfDate);
 
	public List<PolicyDefinition> findPoliciesForInventory(
			InventoryItem inventoryItem, CalendarDate asOfDate,
			String customerType, ServiceProvider forDealer,Boolean invisibleFilingDr);

	public List<PolicyDefinition> findPoliciesAvailableForMajorCompRegistration(InventoryItem inventoryItem, CalendarDate asOfDate,
			String customerType, ServiceProvider forDealer);
	
	public List<PolicyDefinition> findGoodWillPoliciesForMajorComponent(String serialNumber,CalendarDate asOfDate);
    
	public List<PolicyDefinition> findPoliciesAvailableForRegistrationUsingOptionCode(InventoryItem item, String optionCode) throws PolicyException;
	
	public List<PolicyDefinition> findPoliciesUsingAllOptionCodes(InventoryItem inventoryItem, List<String> optionCodes) throws PolicyException;
}

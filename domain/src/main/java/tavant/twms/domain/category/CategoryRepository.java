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

/**
 * User: <a href="mailto:vikas.sasidharan@tavant.com>Vikas Sasidharan</a>
 * Date: Apr 5, 2007
 * Time: 1:03:58 AM
 */

package tavant.twms.domain.category;

import java.util.List;

import tavant.twms.domain.campaign.Campaign;
import tavant.twms.domain.catalog.Item;
import tavant.twms.domain.catalog.ItemGroupService;
import tavant.twms.domain.catalog.ItemGroup;
import tavant.twms.domain.common.LabelService;
import tavant.twms.domain.failurestruct.FaultCode;
import tavant.twms.domain.failurestruct.ServiceProcedure;
import tavant.twms.domain.inventory.InventoryItem;
import tavant.twms.domain.orgmodel.ServiceProvider;
import tavant.twms.domain.orgmodel.Supplier;
import tavant.twms.domain.partreturn.Warehouse;
import tavant.twms.domain.policy.ApplicablePolicy;
import tavant.twms.domain.policy.PolicyDefinition;
import tavant.twms.infra.GenericRepository;

public interface CategoryRepository extends GenericRepository<Category, Long> {

	public List<? extends Category> findAllCategoriesOfABusinessObject(
			final Class businessObjectClass);

	public List<DealerCategory> findAllDealerCategories();

	public List<ApplicablePolicyCategory> findAllApplicablePolicyCategories();

	public List<PolicyDefinition> findPolicyDefinitionsByName(final String name);

	public List<ApplicablePolicyCategory> findApplicablePolicyCategoryByName(
			final String name);

	public boolean isDealerInNamedCategory(ServiceProvider dealer,
			String categoryName);

	public boolean isApplicablePolicyInNamedCategory(ApplicablePolicy policy,
			String labelName, LabelService labelService);

	public boolean isFaultCodeInNamedCategory(FaultCode faultCode,
			String labelName, LabelService labelService);

	public boolean isInventoryItemInNamedCategory(InventoryItem inventoryItem,
			String labelName, LabelService labelService);

	public boolean isServiceProcedureInNamedCategory(
			ServiceProcedure procedure, String labelName,
			LabelService labelService);
	
	public boolean isItemInNamedCategory(
			Item item, String groupName,
			ItemGroupService itemGroupService);

	public boolean isSupplierInNamedCategory(Supplier supplier,
			String labelName, LabelService labelService);

     public boolean isModelInNamedCategory(ItemGroup model, String labelName,
			LabelService labelService);
     
     public boolean isCampaignInNamedCategory(Campaign campaign, String labelName,
 			LabelService labelService);
     
     public boolean isWarehouseInNamedCategory(Warehouse warehouse, String labelName,
  			LabelService labelService);
}

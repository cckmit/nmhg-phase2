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
 * Time: 1:00:42 AM
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
import tavant.twms.domain.orgmodel.DealerGroupRepository;
import tavant.twms.domain.orgmodel.ServiceProvider;
import tavant.twms.domain.orgmodel.Supplier;
import tavant.twms.domain.partreturn.Warehouse;
import tavant.twms.domain.policy.ApplicablePolicy;
import tavant.twms.infra.GenericRepository;
import tavant.twms.infra.GenericServiceImpl;
import tavant.twms.infra.HibernateCast;
import tavant.twms.infra.InstanceOfUtil;

public class CategoryServiceImpl extends
		GenericServiceImpl<Category, Long, Exception> implements
		CategoryService {

	private CategoryRepository categoryRepository;
	private LabelService labelService;
	private ItemGroupService itemGroupService;
	private DealerGroupRepository dealerGroupRepository;

	@Override
	public GenericRepository<Category, Long> getRepository() {
		return categoryRepository;
	}

	public void setCategoryRepository(CategoryRepository categoryRepository) {
		this.categoryRepository = categoryRepository;
	}

	public List<? extends Category> findAllCategoriesForABusinessObject(
			Class businessObjectClass) {
		return categoryRepository
				.findAllCategoriesOfABusinessObject(businessObjectClass);
	}

	public List<DealerCategory> findAllDealerCategories() {
		return categoryRepository.findAllDealerCategories();
	}

	public List<ApplicablePolicyCategory> findAllApplicablePolicyCategories() {
		return categoryRepository.findAllApplicablePolicyCategories();
	}

	// public boolean isDealerInNamedCategory(Dealership dealer,
	// String categoryName) {
	// return categoryRepository.isDealerInNamedCategory(dealer, categoryName);
	// }
	//
	// public boolean isApplicablePolicyInNamedCategory(ApplicablePolicy policy,
	// String labelName) {
	// return categoryRepository.isApplicablePolicyInNamedCategory(policy,
	// labelName, labelService);
	// }

	public boolean isBusinessObjectInNamedCategory(Object businessObject,
			String categoryName) {
		if (businessObject == null) {
			return false;
		}
		if (InstanceOfUtil.isInstanceOfClass(ServiceProvider.class, businessObject)) {
			return dealerGroupRepository.isDealerGroupExistByNameAndDealer(
					categoryName,new HibernateCast<ServiceProvider>().cast(businessObject));
		} else if (InstanceOfUtil.isInstanceOfClass(ApplicablePolicy.class, businessObject)) {
			return categoryRepository.isApplicablePolicyInNamedCategory(
					new HibernateCast<ApplicablePolicy>().cast(businessObject), categoryName,
					labelService);
		} else if (InstanceOfUtil.isInstanceOfClass(FaultCode.class, businessObject)) {
			return categoryRepository.isFaultCodeInNamedCategory(
					new HibernateCast<FaultCode>().cast(businessObject), categoryName, labelService);
		} else if (InstanceOfUtil.isInstanceOfClass(InventoryItem.class, businessObject)) {
			return categoryRepository.isInventoryItemInNamedCategory(
					new HibernateCast<InventoryItem>().cast(businessObject), categoryName, labelService);
		} else if (InstanceOfUtil.isInstanceOfClass(ServiceProcedure.class, businessObject)) {
			return categoryRepository.isServiceProcedureInNamedCategory(
					new HibernateCast<ServiceProcedure>().cast(businessObject), categoryName,
					labelService);
		} else if (InstanceOfUtil.isInstanceOfClass(Supplier.class, businessObject)) {
			return categoryRepository.isSupplierInNamedCategory(
					new HibernateCast<Supplier>().cast(businessObject), categoryName, labelService);
		} else if (InstanceOfUtil.isInstanceOfClass(Item.class, businessObject)) {
			return categoryRepository.isItemInNamedCategory(
					new HibernateCast<Item>().cast(businessObject), categoryName, itemGroupService);
		} else if (InstanceOfUtil.isInstanceOfClass(ItemGroup.class, businessObject)) {
			return categoryRepository.isModelInNamedCategory(
					new HibernateCast<ItemGroup>().cast(businessObject), categoryName, labelService);
		} else if (InstanceOfUtil.isInstanceOfClass(Campaign.class, businessObject)) {
			return categoryRepository.isCampaignInNamedCategory(
					new HibernateCast<Campaign>().cast(businessObject), categoryName, labelService);
		} else if (InstanceOfUtil.isInstanceOfClass(Warehouse.class, businessObject)) {
			return categoryRepository.isWarehouseInNamedCategory(
					new HibernateCast<Warehouse>().cast(businessObject), categoryName, labelService);
		} else {
			throw new IllegalArgumentException("Unsupported Business Object"
					+ businessObject + "].");
		}
	}
	
	public boolean isBusinessObjectNotInNamedCategory(Object businessObject,
			String categoryName) {
		//invert result of belongs to case
		return !isBusinessObjectInNamedCategory(businessObject, categoryName);
	}

	public void setLabelService(LabelService labelService) {
		this.labelService = labelService;
	}

	public boolean isBusinessObjectInNamedCategory(
			List<Object> businessObjects, String categoryName, boolean forEach) {
		Object businessObject = null;
		if (businessObjects != null && businessObjects.size() > 0) {
			businessObject = businessObjects.iterator().next();
		}
		if (businessObject == null) {
			return false;
		}

		if (InstanceOfUtil.isInstanceOfClass(InventoryItem.class, businessObject)) {
			boolean returnBoolean = false;
			for (Object inventoryItemObject : businessObjects) {
				returnBoolean = categoryRepository
						.isInventoryItemInNamedCategory(
								new HibernateCast<InventoryItem>().cast(inventoryItemObject),
								categoryName, labelService);
				if (forEach && !returnBoolean) {
					return false;
				} else if (!forEach && returnBoolean) {
					return true;
				}
			}
			return returnBoolean;
		} else if (InstanceOfUtil.isInstanceOfClass(ApplicablePolicy.class, businessObject)) {
			boolean returnBoolean = false;
			for (Object applicablePolicyObject : businessObjects) {
				returnBoolean = categoryRepository
						.isApplicablePolicyInNamedCategory(
								new HibernateCast<ApplicablePolicy>().cast(applicablePolicyObject),
								categoryName, labelService);
				if (forEach && !returnBoolean) {
					return false;
				} else if (!forEach && returnBoolean) {
					return true;
				}
			}
			return returnBoolean;
		} else if (InstanceOfUtil.isInstanceOfClass(ServiceProcedure.class, businessObject)) {
			boolean returnBoolean = false;
			for (Object serviceProcedure : businessObjects) {
				returnBoolean = categoryRepository
						.isServiceProcedureInNamedCategory(
								new HibernateCast<ServiceProcedure>().cast(serviceProcedure),
								categoryName, labelService);
				if (forEach && !returnBoolean) {
					return false;
				} else if (!forEach && returnBoolean) {
					return true;
				}
			}
			return returnBoolean;
		} else if (InstanceOfUtil.isInstanceOfClass(Item.class, businessObject)) {
			boolean returnBoolean = false;
			for (Object item : businessObjects) {
				returnBoolean = categoryRepository
						.isItemInNamedCategory(
								new HibernateCast<Item>().cast(item),
								categoryName, itemGroupService);
				if (forEach && !returnBoolean) {
					return false;
				} else if (!forEach && returnBoolean) {
					return true;
				}
			}
			return returnBoolean;
		}else if (InstanceOfUtil.isInstanceOfClass(ItemGroup.class, businessObject)) {
			boolean returnBoolean = false;
			for (Object item : businessObjects) {
				returnBoolean = categoryRepository
						.isModelInNamedCategory(
								new HibernateCast<ItemGroup>().cast(item),
								categoryName, labelService);
				if (forEach && !returnBoolean) {
					return false;
				} else if (!forEach && returnBoolean) {
					return true;
				}
			}
			return returnBoolean;
		} else if (InstanceOfUtil.isInstanceOfClass(Warehouse.class, businessObject)) {
			boolean returnBoolean = false;
			for (Object warehouse : businessObjects) {
				returnBoolean = categoryRepository
						.isWarehouseInNamedCategory(
								new HibernateCast<Warehouse>().cast(warehouse),
								categoryName, labelService);
				if (forEach && !returnBoolean) {
					return false;
				} else if (!forEach && returnBoolean) {
					return true;
				}
			}
			return returnBoolean;
		}else {
			throw new IllegalArgumentException("Un supported Business Object"
					+ businessObjects.iterator().next() + "].");
		}
	}
	
	public boolean isBusinessObjectNotInNamedCategory(
			List<Object> businessObjects, String categoryName, boolean forEach) {
		//invert result of belongs to case
		return !isBusinessObjectInNamedCategory(businessObjects, categoryName, forEach);
	}

	public void setDealerGroupRepository(DealerGroupRepository dealerGroupRepository) {
		this.dealerGroupRepository = dealerGroupRepository;
	}

	public void setItemGroupService(ItemGroupService itemGroupService) {
		this.itemGroupService = itemGroupService;
	}
}

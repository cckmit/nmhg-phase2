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
 * Time: 1:05:42 AM
 */

package tavant.twms.domain.category;

import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.hibernate3.HibernateCallback;

import tavant.twms.domain.campaign.Campaign;
import tavant.twms.domain.catalog.Item;
import tavant.twms.domain.catalog.ItemGroupService;
import tavant.twms.domain.catalog.ItemGroup;
import tavant.twms.domain.common.AdminConstants;
import tavant.twms.domain.common.LabelService;
import tavant.twms.domain.failurestruct.FaultCode;
import tavant.twms.domain.failurestruct.ServiceProcedure;
import tavant.twms.domain.inventory.InventoryItem;
import tavant.twms.domain.orgmodel.ServiceProvider;
import tavant.twms.domain.orgmodel.Supplier;
import tavant.twms.domain.partreturn.Warehouse;
import tavant.twms.domain.policy.ApplicablePolicy;
import tavant.twms.domain.policy.PolicyDefinition;
import tavant.twms.infra.GenericRepositoryImpl;

@SuppressWarnings("serial")
public class CategoryRepositoryImpl extends
		GenericRepositoryImpl<Category, Long> implements CategoryRepository {

	// TODO: Better?
	private static final Map<Class, Class> bO2CategoryMap = new HashMap<Class, Class>() {
		{
			put(ServiceProvider.class, DealerCategory.class);
			put(ApplicablePolicy.class, ApplicablePolicyCategory.class);
		}
	};

	@SuppressWarnings("unchecked")
	public List<DealerCategory> findAllDealerCategories() {
		return getHibernateTemplate().find("from DealerCategory");
	}

	@SuppressWarnings("unchecked")
	public List<ApplicablePolicyCategory> findAllApplicablePolicyCategories() {
		return getHibernateTemplate().find("from ApplicablePolicyCategory");
	}

	@SuppressWarnings("unchecked")
	public List<ApplicablePolicyCategory> findApplicablePolicyCategoryByName(
			final String name) {
		if (logger.isDebugEnabled()) {
			logger.debug("findApplicablePolicyCategoryByName(" + name + ")");
		}
		return (List) getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session)
					throws HibernateException, SQLException {
				return session.createCriteria(ApplicablePolicyCategory.class)
						.add(Restrictions.ilike("name", name, MatchMode.START))
						.list();

			}
		});
	}

	@SuppressWarnings("unchecked")
	public List<PolicyDefinition> findPolicyDefinitionsByName(final String name) {
		if (logger.isDebugEnabled()) {
			logger.debug("findApplicablePolicyByName(" + name + ")");
		}
		return (List) getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session)
					throws HibernateException, SQLException {
				return session.createCriteria(PolicyDefinition.class).add(
						Restrictions.ilike("code", name, MatchMode.START))
						.list();

			}
		});
	}

	@SuppressWarnings("unchecked")
	public List<? extends Category> findAllCategoriesOfABusinessObject(
			final Class businessObjectClass) {
		return (List<? extends Category>) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						Class categoryClass = bO2CategoryMap
								.get(businessObjectClass);
						if (categoryClass == null) {
							return Collections.EMPTY_LIST;
						}

						Criteria allCategoriesOfABO = session
								.createCriteria(categoryClass);
						return allCategoriesOfABO.list();
					}
				});
	}

	public boolean isDealerInNamedCategory(final ServiceProvider dealer,
			final String categoryName) {
		return (Boolean) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						Class categoryClass = bO2CategoryMap.get(dealer
								.getClass());
						Criteria namedCategoryCriteria = session
								.createCriteria(categoryClass);
						namedCategoryCriteria.createAlias("members", "members");

						namedCategoryCriteria.add(Restrictions.eq("name",
								categoryName));
						namedCategoryCriteria.add(Restrictions.eq("members.id",
								dealer.getId()));
						namedCategoryCriteria.setProjection(Projections
								.rowCount());
						int numResults = (Integer) namedCategoryCriteria
								.uniqueResult();

						return numResults > 0;
					}
				});
	}

	public boolean isApplicablePolicyInNamedCategory(ApplicablePolicy policy,
			String labelName, LabelService labelService) {
		return policy.getPolicyDefinition().getLabels().contains(
				labelService.findLabelWithName(labelName));
	}

	public boolean isFaultCodeInNamedCategory(FaultCode faultCode,
			String labelName, LabelService labelService) {
		return faultCode.getDefinition().getLabels().contains(
				labelService.findLabelWithName(labelName));
	}

	public boolean isInventoryItemInNamedCategory(InventoryItem inventoryItem,
			String labelName, LabelService labelService) {
		return inventoryItem.getLabels().contains(
				labelService.findLabelWithName(labelName));
	}

	public boolean isServiceProcedureInNamedCategory(
			ServiceProcedure procedure, String labelName,
			LabelService labelService) {
		return procedure.getDefinition().getLabels().contains(
				labelService.findLabelWithName(labelName));
	}
	
	public boolean isItemInNamedCategory(
			Item item, String groupName,
			ItemGroupService itemGroupService) {
		return itemGroupService.isItemPresentInItemGroupWithName(item, groupName);        
	}

	public boolean isSupplierInNamedCategory(Supplier supplier,
			String labelName, LabelService labelService) {
		return supplier.getLabels().contains(labelService.findLabelWithName(labelName));
	}

    public boolean isModelInNamedCategory(
			ItemGroup model, String labelName,
			LabelService labelService) {
		return model.getLabels().contains(
				labelService.findLabelWithName(labelName));
	}
    
    public boolean isCampaignInNamedCategory(
			Campaign campaign, String labelName,
			LabelService labelService) {
		return campaign.getLabels().contains(
				labelService.findLabelWithName(labelName));
	}

	public boolean isWarehouseInNamedCategory(Warehouse warehouse,
			String labelName, LabelService labelService) {
		return warehouse.getLabels().contains(
				labelService.findLabelWithName(labelName));
	}
}

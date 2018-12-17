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
package tavant.twms.domain.claim.payment;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;

import tavant.twms.domain.catalog.ItemGroup;
import tavant.twms.infra.GenericRepositoryImpl;
import tavant.twms.security.SecurityHelper;

/**
 * @author kamal.govindraj
 * 
 */
public class CostCategoryRepositoryImpl extends GenericRepositoryImpl<CostCategory, Long>  implements CostCategoryRepository {

	
	private List<CostCategory> costCategories;
    private Map<String, CostCategory> costCategoriesMap = new HashMap<String, CostCategory>();

    @SuppressWarnings("unchecked")
    public void init() {
        costCategories = getHibernateTemplate().find("from CostCategory order by name");
        for (CostCategory costCategory : costCategories) {
            costCategoriesMap.put(costCategory.getName(), costCategory);
        }
    }
    
    public CostCategory findCostCategoryByCode(final String code) {
        return (CostCategory) getHibernateTemplate().execute(new HibernateCallback() {
            public Object doInHibernate(Session session) throws HibernateException, SQLException {
                return session.createQuery("from CostCategory category where category.code = :codeParam")
                        .setParameter("codeParam", code).uniqueResult();
            }

        });
    }

    public CostCategory findCostCategoryByName(final String name) {
    	return costCategoriesMap.get(name);
    }
    
    @SuppressWarnings("unchecked")
	public List<ItemGroup> findProducts(final String itemGroupType) {
		return (List<ItemGroup>) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						return session
								.createQuery(
										"select parent from ItemGroup parent join parent.scheme.purposes purpose "
												+ " where parent.itemGroupType= :itemGroupType " +
												 " and purpose.name='PRODUCT STRUCTURE' " 
												+ " order by parent.name ")
								.setParameter("itemGroupType", itemGroupType)
								.list();
					}
				});
	}
    
    @SuppressWarnings("unchecked")
    public List<CostCategory> findAllCostCategories() {
        return costCategories;
    }
    
    /**
     * This API returns the list of cost categories from the provided list that are applicable for 
     * the given item group
     */
    @SuppressWarnings("unchecked")
	public List<CostCategory> findCostCategoryApplicableForProduct(final ItemGroup itemGroup,final List<Long> costCatIds){
    	List<CostCategory> execute = (List<CostCategory>) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						return session
								.createQuery(
										"select costcat from CostCategory costcat where :itemGrpParam in elements(costcat.applicableProducts)" +
										"and costcat.id in (:costCatIds)")
								.setParameter("itemGrpParam", itemGroup).setParameterList("costCatIds", costCatIds)
								.list();
					}
				});
		return execute;
    	
    }

    public void saveCostCategoryProductMapping(final List<CostCategory> costCategories) {
        getHibernateTemplate().execute(new HibernateCallback() {
            public Object doInHibernate(Session session) throws HibernateException, SQLException {

                // Any changes to list of products mapped to a cost category is done by direct SQL. This was modified to direct SQL due to an
                // issue in how hibernate was persisting these changes.. Hibernate first deletes all the records from costcat_appl_products for
                // a cost category and then re-inserts the products mapped to that category. During the delete the business unit info is not
                // considered and all the records associated with a cost category were getting deleted from costcat_appl_products table. Due
                // to this changes done to cost category for any BU results in the cost cotegory - products mapping for the other BUs getting lost
                for (CostCategory costCategory : costCategories) {
                    final String warrantyAdminBusinessUnit = new SecurityHelper().getWarrantyAdminBusinessUnit();

                    session.createSQLQuery("delete from costcat_appl_products cap where cap.cost_category = :cost_category " +
                            "and exists (select 1 from item_group ig where business_unit_info = :bu_name and ig.id = cap.item_group)")
                            .setParameter("cost_category", costCategory.getId())
                            .setParameter("bu_name", warrantyAdminBusinessUnit)
                            .executeUpdate();

                    for (ItemGroup ig : costCategory.getApplicableProducts()) {
                        session.createSQLQuery("insert into costcat_appl_products (cost_category, item_group) values (:cost_category, :item_group) ")
                                .setParameter("cost_category", costCategory.getId())
                                .setParameter("item_group", ig.getId())
                                .executeUpdate();
                    }
                }
                return null;
            }
        });
        // The session needs to cleared. Otherwise hibernate will delete the records from costcat_appl_products table
        getSession().clear();
    }

	public List<CostCategory> findAllStateMandateCostCategories() {
			String query = "select costcat from CostCategory costcat where costcat.stateMandate=:value";
			Map<String, Object> params = new HashMap<String, Object>(1);
			params.put("value", Boolean.TRUE);
			return findUsingQuery(query, params);
	}
}

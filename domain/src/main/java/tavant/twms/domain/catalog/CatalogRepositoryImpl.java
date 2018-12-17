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
package tavant.twms.domain.catalog;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.SortedSet;
import java.util.Collection;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.hibernate3.HibernateCallback;


import tavant.twms.domain.claim.Criteria;
import tavant.twms.domain.orgmodel.DealerCriterion;
import tavant.twms.domain.orgmodel.Organization;
import tavant.twms.domain.orgmodel.Party;
import tavant.twms.domain.orgmodel.ServiceProvider;
import tavant.twms.domain.orgmodel.Supplier;
import tavant.twms.domain.supplier.contract.Contract;
import tavant.twms.infra.*;
import tavant.twms.security.SecurityHelper;
import tavant.twms.domain.bu.BusinessUnit;
/**
 * @author kamal.govindraj
 *
 */
public class CatalogRepositoryImpl extends GenericRepositoryImpl<Item, Long> implements CatalogRepository {

	private SecurityHelper securityHelper ;

    private Repository repository;

    public Item findItemByItemNumberOwnedByManuf(final String itemNumber) {
			return (Item) getHibernateTemplate().execute(new HibernateCallback() {
				public Object doInHibernate(Session session)
						throws HibernateException, SQLException {
					if(itemNumber.contains("#"))
					{						
						return session.createQuery(
								" select i from Item i where i.number = :number " +
								" and i.ownedBy = :oemorg "
								)
								.setParameter("number", itemNumber).setParameter("oemorg", securityHelper.getOEMOrganization()).uniqueResult();
					}
					else
					{
						return session.createQuery(
								" select i from Item i where i.alternateNumber = :number " +
								" and i.ownedBy = :oemorg "
								)
								.setParameter("number", itemNumber).setParameter("oemorg", securityHelper.getOEMOrganization()).uniqueResult();
					}					
				}
			});
		}
    
    public Item findItemByItemNumberOwnedByManuf(final String itemNumber, final String itemType) {
		return (Item) getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session)
					throws HibernateException, SQLException {
				if(itemNumber.contains("#"))
				{	
					List<Item> items = session.createQuery(
							" select i from Item i where i.number = :number " +
							" and i.ownedBy = :oemorg " + 
							" and i.itemType = :itemType "
							)
							.setParameter("number", itemNumber).setParameter("oemorg", securityHelper.getOEMOrganization()).setParameter("itemType", itemType).list();
					if(items!=null && !items.isEmpty())
						return items.get(0);
				}
				else
				{
					List<Item> items = session.createQuery(
							" select i from Item i where i.alternateNumber = :number " +
							" and i.ownedBy = :oemorg "  + 
							" and i.itemType = :itemType "
							)
							.setParameter("number", itemNumber).setParameter("itemType", itemType).setParameter("oemorg", securityHelper.getOEMOrganization()).list();
					if(items!=null && !items.isEmpty())
						return items.get(0);
				}	
				return null;
			}
		});
	}
    
    
    public Item findItemByItemNumber(final String itemNumber){
    	
    	return (Item) getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session)
					throws HibernateException, SQLException {
					return session.createQuery(
							" select i from Item i where i.number = :number"
							)
							.setParameter("number", itemNumber).uniqueResult();
				}
		});
    	
    }

    public Item findItemByItemNumberOwnedByManufAndProduct(final String itemNumber, final ItemGroup product) {
		return (Item) getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session)
					throws HibernateException, SQLException {
				if(itemNumber.contains("#"))
				{						
					return session.createQuery(
							" select i from Item i where i.number = :number " +
							" and i.ownedBy = :oemorg and i.product = :product "
							)
							.setParameter("number", itemNumber).setParameter("product", product)
							.setParameter("oemorg", securityHelper.getOEMOrganization()).uniqueResult();
				}
				else
				{
					return session.createQuery(
							" select i from Item i where i.alternateNumber = :number " +
							" and i.ownedBy = :oemorg and i.product = :product "
							)
							.setParameter("number", itemNumber).setParameter("product", product)
							.setParameter("oemorg", securityHelper.getOEMOrganization()).uniqueResult();
				}					
			}
		});
	}

    @SuppressWarnings("unchecked")
	public BrandItem findItemByItemNumberAndBrand(final String itemId,final String brand) {
		return (BrandItem) getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session)
					throws HibernateException, SQLException {
					return session.createQuery(
							" select b from Brand b where b.item = :id " +
							" and i.brand = :brand"
							)
							.setParameter("id", itemId).setParameter("brand",brand).uniqueResult();
				}
		});
	}

		public Item findItemByItemNumberOwnedByServiceProvider(final String itemNumber,final Long ownedById) {
			return (Item) getHibernateTemplate().execute(new HibernateCallback() {
				public Object doInHibernate(Session session)
						throws HibernateException, SQLException {
					if(itemNumber.contains("#"))
					{
						return session.createQuery(
								" select i from Item i  where i.number = :number " +
								" and i.ownedBy.id = :ownedByPartyId "
								)
								.setParameter("number", itemNumber).setParameter("ownedByPartyId", ownedById).uniqueResult();
					}
					else
					{
						return session.createQuery(
								" select i from Item i  where i.alternateNumber = :number " +
								" and i.ownedBy.id = :ownedByPartyId "
								)
								.setParameter("number", itemNumber).setParameter("ownedByPartyId", ownedById).uniqueResult();
					}
				}
			});
	}
	@SuppressWarnings("unchecked")
	public List<Item> findItemsWithModelName(final String modelName){
		return (List<Item>) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						String queryStr = "Select item from ItemGroup itemGrp,Item item where" +
								" itemGrp.name=:modelName and itemGrp.id=item.model.id " +
						        " and item.ownedBy = :oemorg ";
						return session
								.createQuery(queryStr).setParameter("modelName", modelName).setParameter("oemorg", securityHelper.getOEMOrganization()).list();
					}
				});
	}

	@SuppressWarnings("unchecked")
	public List<String> findItemGroupsWithNameLike(
			final String partialProductName, final int pageNumber,
			final int pageSize) {
		return (List<String>) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						return session
								.createQuery(
										"select i.name from ItemGroup i where upper(i.name) like :name")
								.setParameter("name", partialProductName + "%")
								.setFirstResult(pageNumber * pageSize)
								.setMaxResults(pageSize).list();
					}
				});
	}
	
	@SuppressWarnings("unchecked")
	public List<ItemGroup> findAllItemGroupsWithNameLike(
			final String partialProductName, final int pageNumber,
			final int pageSize) {
		return (List<ItemGroup>) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {		
								return session.createQuery(
										"select itemGroup from ItemGroup itemGroup join itemGroup.scheme.purposes purpose " +
										"where upper(itemGroup.name) like :name " +
										"and (purpose.name = 'PRODUCT STRUCTURE' or purpose.name='Warranty Coverage')")
								.setParameter("name", partialProductName + "%")
								.setFirstResult(pageNumber * pageSize)
								.setMaxResults(pageSize).list();
					}
				});
	}
	
	@SuppressWarnings("unchecked")
	public Item findItemWithPurposeWarrantyCoverage(final String purpose, final String partialItemNumber) {
		return (Item) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
								return session
								.createQuery(
										" select distinct item from "
												+ " Item item, ItemGroup ig join ig.scheme.purposes purpose"
												+ " where item in elements(ig.includedItems)"												
												+ " and purpose.name=:purpose "									
												+ " and (item.number=:nameOrNumber )"												
												+ " and item.ownedBy.name='OEM' ")
								.setParameter("nameOrNumber", partialItemNumber)
								.setParameter("purpose",purpose).uniqueResult();
							}
				});
	}
	
	@SuppressWarnings("unchecked")
	public List<Item> findAllItemsWithPurposeWarrantyCoverage(final String purpose,final String partialItemNumber, final int pageNumber,
			final int pageSize) {
		return (List<Item>) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {		
								return session
								.createQuery(
										" select distinct item from "
												+ " Item item, ItemGroup ig join ig.scheme.purposes purpose"
												+ " where item in elements(ig.includedItems)"												
												+ " and purpose.name=:purpose "									
												+ " and (item.number like :nameOrNumber )"												
												+ " and item.ownedBy.id= " + securityHelper.getOEMOrganization().getId().longValue())
								.setParameter("nameOrNumber",
										partialItemNumber + "%")
								.setParameter("purpose",purpose)
										
								.setFirstResult(pageNumber).setMaxResults(pageSize).list();
								}
				});
	}
	
	public Item findItemByItemNumberAndPurposeOwnedByServiceProvider(final String purpose, final String itemNumber,
			final Long ownedById) {
		return (Item) getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				if (itemNumber.contains("#")) {
					return session.createQuery(
							" select i from Item i, ItemGroup ig join ig.scheme.purposes purpose"
									+ " where i in elements(ig.includedItems)"									
									+ " and purpose.name=:purpose " + " and i.number = :number "
									+ " and i.ownedBy.id = :ownedByPartyId ").setParameter("number", itemNumber)
							.setParameter("ownedByPartyId", ownedById).setParameter("purpose", purpose).uniqueResult();
				} else {
					return session.createQuery(
							" select i from Item i, ItemGroup ig join ig.scheme.purposes purpose"
									+ " where i in elements(ig.includedItems)"									
									+ " and purpose.name=:purpose " + " and i.alternateNumber = :number "
									+ " and i.ownedBy.id = :ownedByPartyId ").setParameter("number", itemNumber)
							.setParameter("ownedByPartyId", ownedById).setParameter("purpose", purpose).uniqueResult();
				}
			}
		});
	}

	@SuppressWarnings("unchecked")
	public List<String> findItemGroupsOfTypeWithNameLike(
			final String partialProductName, final String itemGroupType,
			final int pageNumber, final int pageSize) {
		return (List<String>) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						return session
								.createQuery(
										"select i.name from ItemGroup i where upper(i.name) like :name "
												+ " and i.itemGroupType= :itemGroupType ")
								.setParameter("name", partialProductName + "%")
								.setParameter("itemGroupType", itemGroupType)
								.setFirstResult(pageNumber * pageSize)
								.setMaxResults(pageSize).list();
					}
				});
	}

	@SuppressWarnings("unchecked")
	public List<String> findItemNumbersStartingWith(
			final String partialItemNumber, final int pageNumber,
			final int pageSize) {

		return (List<String>) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						return session
								.createQuery(
										"select item.number from Item item  "
												+ " where item.number like :partialItemNumber "
												+ " and item.ownedBy = :oemorg order by item.number")
								.setParameter("partialItemNumber",
										partialItemNumber + "%").
										setParameter("oemorg", securityHelper.getOEMOrganization()).
								setFirstResult(pageNumber * pageSize)
								.setMaxResults(pageSize).list();
					}
				});
	}

	@SuppressWarnings("unchecked")
	public List<Item> findItemsWhoseNumbersStartWith(
			final String partialItemNumber, final int pageNumber,
			final int pageSize) {

		return (List<Item>) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						return session
								.createQuery(
										"select item from Item item "
												+ " where item.number like :partialItemNumber "
												+ " and item.ownedBy = :oemorg order by item.number")
								.setParameter("partialItemNumber",
										partialItemNumber + "%").
										setParameter("oemorg", securityHelper.getOEMOrganization())		
								.setFirstResult(pageNumber * pageSize)
								.setMaxResults(pageSize).list();
					}
				});
	}

	@SuppressWarnings("unchecked")
	public List<ItemGroup> findModelsWhoseNumbersStartWith(
			final String partialModelName, final int pageNumber,
			final int pageSize) {

		return (List<ItemGroup>) getHibernateTemplate().execute(
				new HibernateCallback() {

					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						String queryString = " select itemGrp "
								+ " from ItemGroup itemGrp, ItemGroup parentGrp "
								+ " where itemGrp.nodeInfo.lft between parentGrp.nodeInfo.lft and parentGrp.nodeInfo.rgt "
								+ " and itemGrp.itemGroupType = 'MODEL' "
								+ " and UPPER(parentGrp.name) = 'MACHINE' "
								+ " and parentGrp.itemGroupType = 'PRODUCT TYPE' "
								+ " and upper(itemGrp.name) like :name ";

						return session.createQuery(queryString).setParameter(
								"name", partialModelName + "%").setFirstResult(
								pageNumber * pageSize).setMaxResults(pageSize)
								.list();
					}
				});
	}

	@SuppressWarnings("unchecked")
	public List<Item> findParts(final String itemNumber) {
		return (List<Item>) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						session.disableFilter("bu_name");
						List<Item> items = session.getNamedQuery("parts").setParameter(
								"number", itemNumber + "%")
								.setFirstResult(0).setMaxResults(10).list();
						session.enableFilter("bu_name");
						return items;
					}
				});
	}

	@SuppressWarnings("unchecked")
	public List<Item> findParts(final String itemNumber, final List<Object> itemGroup) {
		return (List<Item>) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						return session.getNamedQuery("partsForItemGroup").setParameter(
								"number", itemNumber + "%")
								.setParameterList("itemGroup", itemGroup)
								.setFirstResult(0).setMaxResults(10).list();
					}
				});
	}

	public ItemGroup findItemGroup(final Long id) {
		return (ItemGroup) getHibernateTemplate().get(ItemGroup.class, id);
	}

	@SuppressWarnings("unchecked")
	public List<ItemGroup> findAllItemGroupsOfType(String itemGroupType) {
		return getHibernateTemplate()
				.find(
						"from ItemGroup ig where upper(ig.itemGroupType) = ? order by ig.name",
						itemGroupType);
	}


    @SuppressWarnings("unchecked")
	public List<ItemGroup> findAllItemGroupsOfType(final String partialItemGroup, final List<String> itemGroupType, final int pageNumber, final int pageSize) {
        return (List<ItemGroup>) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
								return session.createQuery(
                                        "select ig from ItemGroup ig where upper(ig.name) like :name and upper(ig.itemGroupType) in (:itemGroupType) order by ig.name ")
							    .setParameter("name", partialItemGroup + "%")
                                .setParameterList("itemGroupType", itemGroupType)
								.list();
					}
				});
	}

	@SuppressWarnings("unchecked")
	public List<ItemGroup> findAllItemGroups() {
		return getHibernateTemplate().find("from ItemGroup ig order by ig.name");
	}

	public List<ItemGroup> findAllProductCodes() {
		return findAllItemGroupsOfType("PRODUCT CODE");
	}
	
	public List<ItemGroup> listAllProductCodesMatchingName(final String partialProductName){
		return listAllProductsOrModelsMatchingName(partialProductName,"PRODUCT");
	}
	
	@SuppressWarnings("unchecked")
	public List<ItemGroup> listAllProductsOrModelsMatchingName(final String partialProductName,
			final String itemGroupType){
		return (List<ItemGroup>) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {		
								return session.createQuery(
										"select itemGroup from ItemGroup itemGroup join itemGroup.scheme.purposes purpose " +
										"where upper(itemGroup.description) like :name " +
										"and itemGroup.itemGroupType =:itemGroupType "+
										"and purpose.name = 'PRODUCT STRUCTURE' ")
								.setParameter("name", partialProductName + "%")
								.setParameter("itemGroupType",itemGroupType)
								.list();
					}
				});
	
		
	}
	
	public List<ItemGroup> findAllProductsMatchingGroupCode(final String partialProductCode){
		return listAllProductsMatchingGroupCode(partialProductCode,"PRODUCT");
	}
	
	@SuppressWarnings("unchecked")
	public List<ItemGroup> listAllProductsMatchingGroupCode(final String partialProductCode,
			final String itemGroupType){
		return (List<ItemGroup>) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {		
								return session.createQuery(
										"select itemGroup from ItemGroup itemGroup join itemGroup.scheme.purposes purpose " +
										"where upper(itemGroup.groupCode) like upper(:code) " +
										"and itemGroup.itemGroupType =:itemGroupType "+
										"and purpose.name = 'PRODUCT STRUCTURE' ")
								.setParameter("code", partialProductCode.trim() + "%")
								.setParameter("itemGroupType",itemGroupType)
								.list();
					}
				});
	
		
	}

	public List<ItemGroup> findAllItemModels() {
		return findAllItemGroupsOfType("MODEL");
	}

    public List<ItemGroup> findAllItemProductsAndModels(String partialItemGroup, final int pageNumber,
			final int pageSize) {
		List<String> itemgroupTypes = new ArrayList<String>();
        itemgroupTypes.add("PRODUCT");
        itemgroupTypes.add("MODEL");
        return findAllItemGroupsOfType(partialItemGroup, itemgroupTypes, pageNumber, pageSize);
	}

	public void createItemGroup(ItemGroup ig) {
		getHibernateTemplate().save(ig);
	}

	public void updateItemGroup(ItemGroup ig) {
		getHibernateTemplate().update(ig);
	}
	
	public List<String> findAllUoms(){
		
		final String sql = "select distinct(upper(uom)) from Item";
		return (List<String>) getHibernateTemplate().execute(new HibernateCallback(){
			
			public Object doInHibernate(Session session)
					throws HibernateException, SQLException {
			   session.disableFilter("excludeInactive");	
               Query query = session.createQuery(sql);
               return query.list();
			}
			
		});
	}	
	
	public ItemGroup findItemGroupByName(final String name) {
		return (ItemGroup) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						return session.createCriteria(ItemGroup.class, "ig")
								.add(Restrictions.eq("ig.name", name))
								.setMaxResults(1).uniqueResult();
					}
				});
	}

	public ItemGroup findItemGroupByCode(final String code) {
		return (ItemGroup) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						return session.createCriteria(ItemGroup.class, "ig")
								.add(Restrictions.eq("ig.groupCode", code))
								.setMaxResults(1).uniqueResult();
					}
				});
	}
	
	
	@SuppressWarnings("unchecked")
	public PageResult<Item> findItemsWithNumberAndDescriptionLike(
			String number, String description, ListCriteria criteria) {
		String numberParam = number != null ? number.toUpperCase() : null;
		String descriptionParam = description != null ? description.toUpperCase() : null;
        Map<String,Object> parameterMap = new HashMap<String,Object>(4);
        StringBuilder sb = new StringBuilder("from Item item ");
        if(StringUtils.isNotBlank(descriptionParam)){
            sb.append("join item.i18nItemTexts i18nItem ");
            parameterMap.put("partialItemDescription", descriptionParam + "%");
            parameterMap.put("userLocale", securityHelper.getLoggedInUser().getLocale().toString());
        }
        sb.append(" where ");
        if(StringUtils.isNotBlank(descriptionParam)){
            sb.append("upper(i18nItem.description) like :partialItemDescription and ");
            sb.append("i18nItem.locale = :userLocale and ");
        }
        if(StringUtils.isNotBlank(numberParam)){
            sb.append("upper(item.number) like :partialItemNumber and ");
            parameterMap.put("partialItemNumber", numberParam + "%");
        }
        sb.append("item.ownedBy = :oemorg");
        parameterMap.put("oemorg", securityHelper.getOEMOrganization());
        return findPageUsingQuery(sb.toString(), criteria.getSortCriteriaString(), 
                "select item ", criteria.getPageSpecification(), 
                new QueryParameters(parameterMap));
	}

	@SuppressWarnings("unchecked")
	public List<Item> findItemsOwnedBy(final Long id) {
		return getHibernateTemplate().find(
				"select distinct i from Item i,ItemMapping im where i.ownedBy.id = ? and i.id=im.toItem", id);
	}

	public List<Item> findProdutsWithNameStartingWith(
			final String productNamePrefix,
			final PageSpecification pageSpecification) {

		final DetachedCriteria criteria = DetachedCriteria.forClass(Item.class);
		criteria.createCriteria("product").add(
				Restrictions.ilike("name", productNamePrefix, MatchMode.START));
		criteria.addOrder(Order.asc("name"));

		return findItemsForCriteria(criteria, pageSpecification);
	}

	@SuppressWarnings("unchecked")
	public List<Item> findItemsForCriteria(final DetachedCriteria criteria,
			final PageSpecification pageSpecification) {
		final int firstResult = pageSpecification.getPageSize()
				* pageSpecification.getPageNumber();
		final int pageSize = pageSpecification.getPageSize();

		return (List<Item>) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						return criteria.getExecutableCriteria(session)
								.setFirstResult(firstResult).setMaxResults(
										pageSize).list();
					};
				});
	}

	@SuppressWarnings("unchecked")
	public List<String> findProductsAndModelsWhoseNameStartsWith(
			final String name, final int firstResult, final int pageMaxResult) {
		return (List<String>) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						return session
								.createQuery(
										"select ig.name from ItemGroup ig where "
												+ "(ig.itemGroupType = 'MODEL' or ig.itemGroupType = 'PRODUCT') and "
												+ "ig.name like :name")
								.setParameter("name", name + "%")
								.setFirstResult(firstResult).setMaxResults(
										pageMaxResult).list();
					}
				});
	}

	public ItemGroup findProductOrModelWhoseNameIs(final String name) {
		return (ItemGroup) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						return session
								.createQuery(
										"from ItemGroup ig where "
												+ "(ig.itemGroupType = 'MODEL' or ig.itemGroupType = 'PRODUCT') and "
												+ "ig.name = :name")
								.setParameter("name", name).setMaxResults(1)
								.uniqueResult();
					}
				});
	}

	@SuppressWarnings("unchecked")
	public List<Item> findOEMDealerParts(final String itemNameOrNumber,
			final Organization organization, final List<Object> itemGroup) {
		return (List<Item>) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						return session
								.createQuery(
										" select distinct item from "
												+ " Item item, ItemGroup parent join parent.scheme.purposes purpose, ItemGroup child, ItemMapping itemMapping"
												+ " where item in elements(child.includedItems)"
												+ " and child.nodeInfo.treeId=parent.nodeInfo.treeId "
												+ " and ( parent.nodeInfo.lft < child.nodeInfo.lft and child.nodeInfo.rgt < parent.nodeInfo.rgt ) "
												+ " and purpose.name='PRODUCT STRUCTURE' "
												+ " and upper(parent.name) in (:itemGroup) "
												+ " and parent.itemGroupType = 'PRODUCT TYPE' "
												+ " and (item.number like :nameOrNumber )"
												+ " and item.ownedBy = :organization "
												+ " and itemMapping.toItem = item ")
								.setParameter("nameOrNumber",
										itemNameOrNumber + "%").setParameter(
										"organization", organization)
										.setParameterList("itemGroup",itemGroup)
								.setFirstResult(0).setMaxResults(10).list();
						
						
					}
				});
	}
	
	
	public Item findItemByNumberAndSupplier(final String number,
			final Supplier supplier) {
		return (Item) getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session)
					throws HibernateException, SQLException {
				return session.createQuery(
						"from Item item where item.number = :number and "
								+ "item.ownedBy = :supplier").setParameter(
						"number", number).setParameter("supplier", supplier)
						.setMaxResults(1).uniqueResult();
			}
		});
	}

	public Item findItemByNumberAndParty(final String number, final Party party){
		return (Item) getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session)
					throws HibernateException, SQLException {
				return session.createQuery(
						"from Item item where item.number = :number and "
								+ "item.ownedBy = :party").setParameter(
						"number", number).setParameter("party", party)
						.setMaxResults(1).uniqueResult();
			}
		});
	}

	@SuppressWarnings("unchecked")
	public PageResult<Item> findItems(ListCriteria criteria,
			Organization organization) {
		PageSpecification pageSpecification = criteria.getPageSpecification();
		final StringBuffer fromAndWhereClause = new StringBuffer();
		fromAndWhereClause.append("from Item item");
		fromAndWhereClause.append(" where ");
		fromAndWhereClause.append("item.ownedBy.id=").append(organization.getId());
		if (criteria.isFilterCriteriaSpecified()) {
			fromAndWhereClause.append(" and ");
			String paramterizedFilterCriteria = criteria
					.getParamterizedFilterCriteria();
			fromAndWhereClause.append(paramterizedFilterCriteria);
		}
		final String queryWithoutSelect = fromAndWhereClause.toString();
		final String sortClause = criteria.getSortCriteriaString();
		final Map<String, Object> parameterMap = criteria.getParameterMap();
		return findPageUsingQuery(queryWithoutSelect, sortClause,
				pageSpecification, parameterMap);

	}

	public PageResult<Item> findItemsUsingDynamicQuery(
			String queryWithoutSelect, String orderByClause,
			String selectClause, PageSpecification pageSpecification,
			QueryParameters parameters) {
		return super.findPageUsingQuery(queryWithoutSelect, orderByClause,
				selectClause, pageSpecification, parameters);
	}

	public Item findSupplierItem(final String itemNumber, final Long supplierId) {
		return (Item) getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session)
					throws HibernateException, SQLException {
				return session.createQuery(
						"select item from Item item, Supplier supplier where item.number = :number"
						+ " and item.ownedBy.id = :supplierId")
						.setParameter("number",itemNumber)
						.setParameter("supplierId",supplierId)
						.uniqueResult();
			}
		});
	}

	public ResultSet findItems(final PageSpecification pageSpecification) {
		return (ResultSet) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						long firstResult = pageSpecification.getPageSize()
								* pageSpecification.getPageNumber();
						long maxRecord = firstResult
								+ pageSpecification.getPageSize();
						String sql = "SELECT * FROM (SELECT it.item_number \"PartNumber\","
								+ "it.description \"PartDescription\", it.dealer_actual_price \"DealerActualPrice\", "
								+ "it.elite_price \"ElitePrice\", it.weight \"PartWeight\", "
								+ "it.dim_pkg_length \"DimensionPackageLength\", "
								+ "it.dim_pkg_width \"DimensionPackageWidth\", "
								+ "it.dim_pkg_height \"DimensionPackageHeight\", "
								+ "it.dimension_uom \"DimensionUOM\","
								+ " ROWNUM rnum FROM (select * from item order by id) it where "
								+ " ROWNUM <= ? )WHERE rnum > ?";
						Connection connection = session.connection();
						PreparedStatement pst = connection
								.prepareStatement(sql);
						pst.setLong(1, maxRecord);
						pst.setLong(2, firstResult);
						ResultSet rs = pst.executeQuery();
						return rs;
					}
				});
	}

	public BigDecimal findItemCount() {
		return (BigDecimal) getHibernateTemplate().execute(new HibernateCallback() {

			public Object doInHibernate(Session session)
					throws HibernateException, SQLException {
				return session.createSQLQuery("select count(id) from item")
						.uniqueResult();
			}
		});
	}
	
	@SuppressWarnings("unchecked")
	public  List<Item> findItemsByAlternateItemNumber(final String alternateItemNumber, final Long ownedById) {	
		return (List<Item>) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						return session
								.createQuery(
										"select item from Item item where "
												+ "item.alternateNumber = :alternateItemNumber and "
												+ "item.ownedBy.id = :ownedById")
								.setParameter("alternateItemNumber",alternateItemNumber)
								.setParameter("ownedById",ownedById).list();
					}
				});
	}


	@SuppressWarnings("unchecked")
	public List<Item> findItemNumbersByModelName(final String modelName , final String itemNumber) {
		return (List<Item>) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
					return session
					.createQuery(
							"select item from Item item  "
									+ " where upper(item.number) like :itemNumber" 
									+ " and item.ownedBy = :oemorg "
									+ " and item.model.name= :modelName"
									+ " order by item.number")
					.setParameter("modelName",modelName)
					.setParameter("oemorg", securityHelper.getOEMOrganization())
					.setParameter("itemNumber",itemNumber.toUpperCase()+ "%")
					.list();
					}
				});
	}
	
	@SuppressWarnings("unchecked")
	public List<Item> findItemNumbersForNonSerializedClaim(final String itemNumber,final String itemType ,final int pageNumber,
			final int pageSize){
		return (List<Item>) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
					return session
					.createQuery(
							"select item from Item item  "
									+ " where upper(item.number) like :itemNumber" 
									+ " and item.ownedBy = :oemorg "
									+ " and item_type = :itemType "
									+ " order by item.number")					
					.setParameter("oemorg", securityHelper.getOEMOrganization())
					.setParameter("itemNumber",itemNumber.toUpperCase()+ "%")
                    .setParameter("itemType", itemType.toUpperCase())
					.setFirstResult(
							pageNumber * pageSize).setMaxResults(pageSize)
							.list();
					}
				});
	}
	
    
    /* To display to the dealer all parts that are configured to be  returned by him to the OEM (Thermoking  )*/
    @SuppressWarnings("unchecked")
	public PageResult<Item> findPartReturnItemsForDealer(final ListCriteria listCriteria, Criteria criteria) {
    	   	Session session = getSession();
    		StringBuffer query = new StringBuffer(session.getNamedQuery("partreturnDefinitionsForDealersSQL").getQueryString());
	    	if (listCriteria.isFilterCriteriaSpecified()) {
				String paramterizedFilterCriteria = listCriteria
						.getParamterizedFilterCriteria();
				query.append("and ").append(paramterizedFilterCriteria);
          }
	    	Map<String, Object> params = new HashMap<String, Object>();
	        DealerCriterion dealerCriterion = criteria.getDealerCriterion();
	        ServiceProvider dummyDealer = new ServiceProvider();
	        dummyDealer.setId(-1L);	      
			params=listCriteria.getParameterMap();
			params.put("dealerId", (dealerCriterion != null)
				        && (dealerCriterion.getDealer() != null) ? dealerCriterion
				        .getDealer().getId() : dummyDealer.getId());
			SortedSet<BusinessUnit> businessUnits = getSecurityHelper().getLoggedInUser().getBusinessUnits();
			List<Object> businessUnitNames = new ArrayList<Object> ();
			for(BusinessUnit businessUnit : businessUnits){
				businessUnitNames.add(businessUnit.getName());
			}
			params.put("businessUnitInfo",businessUnitNames);
			return findPRDsUsingDynamicQuery(query.toString(),listCriteria.getSortCriteriaString(),"select distinct item.* ",
        		listCriteria.getPageSpecification(), params,"id");
             
        }
	

	private PageResult<Item> findPRDsUsingDynamicQuery(
			final String queryWithoutSelect, final String orderByClause,
			final String selectClause, PageSpecification pageSpecification,
			final Map<String,Object> params,final String distinctClause) {
		return findPageUsingSqlQueryForDistinctItems(queryWithoutSelect, orderByClause,
				selectClause, pageSpecification, params,distinctClause);
	}
	//SQL query is used instead of HQL to deal with performance issues.
	 @SuppressWarnings("unchecked")
		public PageResult<Item> findPageUsingSqlQueryForDistinctItems(final String queryWithoutSelect, final String orderByClause,
				final String selectClause,PageSpecification pageSpecification,
				final Map<String,Object> params,final String distinctClause) {
			final StringBuffer countQuery = new StringBuffer(" select count( "+distinctClause+" ) ");
	        countQuery.append(queryWithoutSelect);
	        
	        Long numberOfRows = (Long)getHibernateTemplate().execute(new HibernateCallback() {
	            public Object doInHibernate(Session session) throws HibernateException, SQLException {
					Query query = session.createSQLQuery(countQuery.toString());
					//query.setProperties(parameterMap);
				    Set<Map.Entry<String, Object>> entrySet = params.entrySet();
					 for (Map.Entry<String, Object> entry : entrySet) {
				            if (entry.getValue() instanceof List) {
				                query.setParameterList(entry.getKey(), (Collection) entry.getValue());
				            } else {
				                query.setParameter(entry.getKey(), entry.getValue());
				            }
				        }
					return ((BigDecimal)query.uniqueResult()).longValue();
					
	            }
	        });
	           Integer numberOfPages = pageSpecification.convertRowsToPages(numberOfRows);

	        List<Item> rowsInPage = new ArrayList<Item>();
	        PageResult<Item> page = new PageResult<Item>(rowsInPage,pageSpecification,numberOfPages);

	        StringBuffer filterAndSort = new StringBuffer();
	        if(selectClause!=null && !("".equals(selectClause.trim()))) {
				filterAndSort.append(selectClause);
			}
	        filterAndSort.append(queryWithoutSelect);
	        if( orderByClause!=null && orderByClause.trim().length() > 0 ) {
	            filterAndSort.append( " order by ");
	            filterAndSort.append( orderByClause );
	        }
	        final String finalQuery = filterAndSort.toString();
	        
	        final Integer pageOffset = pageSpecification.offSet();
	        if( numberOfRows > 0 && numberOfRows > pageOffset) {
	            
	            final Integer pageSize = pageSpecification.getPageSize();
	            rowsInPage = (List<Item>)getHibernateTemplate().execute(new HibernateCallback() {
	                public Object doInHibernate(Session session) throws HibernateException, SQLException {
	                	//Need to addEntity(Item.Class) so that the result set is mapped automatically into Item Objects
	                    Query query = session.createSQLQuery(finalQuery).addEntity(Item.class);
	                    Set<Map.Entry<String, Object>> entrySet = params.entrySet();
	                    for (Map.Entry<String, Object> entry : entrySet) {
				            if (entry.getValue() instanceof List) {
				                query.setParameterList(entry.getKey(), (Collection) entry.getValue());
				            } else {
				                query.setParameter(entry.getKey(), entry.getValue());
				            }
				        }
	                    return query
	                        .setFirstResult(pageOffset)
	                        .setMaxResults(pageSize)
	                        .list();
	                }
	            });
	            page = new PageResult<Item>(rowsInPage,pageSpecification,numberOfPages);
	        }
	        return page;
		}
	
	public SecurityHelper getSecurityHelper() {
		return securityHelper;
	}

	public void setSecurityHelper(SecurityHelper securityHelper) {
		this.securityHelper = securityHelper;
	}

    @SuppressWarnings("unchecked")
	public PageResult<ItemGroup> findAllModelsWithCriteria(ListCriteria listCriteria) {
        String fromClause = " from ItemGroup itemGroup ";
        String whereClause = " where upper(itemGroup.itemGroupType)='MODEL' ";
        return (PageResult<ItemGroup>)
                this.repository.findPage(fromClause,whereClause,listCriteria);
	}

    public void setRepository(Repository repository) {
        this.repository = repository;
    }

    @SuppressWarnings("unchecked")
	public ItemGroup findModelByModelName(final String modelName) {
		return (ItemGroup) getHibernateTemplate().execute(
				new HibernateCallback() {

					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						String queryString = " select itemGrp "
								+ " from ItemGroup itemGrp, ItemGroup parentGrp "
								+ " where itemGrp.nodeInfo.lft between parentGrp.nodeInfo.lft and parentGrp.nodeInfo.rgt "
								+ " and itemGrp.itemGroupType = 'MODEL' "
								+ " and UPPER(parentGrp.name) = 'MACHINE' "
								+ " and parentGrp.itemGroupType = 'PRODUCT TYPE' "
								+ " and upper(itemGrp.name) = :name ";

						return session.createQuery(queryString).setParameter(
								"name", modelName).uniqueResult();
					}
				});
	}


	@SuppressWarnings("unchecked")
	public List<Item> findItemsWithNumberLike(final String number) {
		final String numberParam = number != null ? number.toUpperCase() : null;
		return (List<Item>) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						return session
								.createQuery
									     ("select item from Item item"
										        + " where upper(item.number) like :partialItemNumber"
										       	+ " and item.ownedBy = :oemorg order by item.number")
								.setParameter("partialItemNumber",numberParam + "%")
								.setParameter("oemorg", securityHelper.getOEMOrganization())							    
								.list();
					}
				});
	}
    
    public PageResult<Item> findItemsForItemGroup(long itemGroup, ListCriteria criteria){
        PageSpecification ps = criteria.getPageSpecification();
        Map<String,Object> parameterMap = new HashMap<String,Object>(2);
        parameterMap.put("itemGroupId", itemGroup);
        StringBuilder sb = new StringBuilder();
        sb.append("from ItemGroup ig join ig.includedItems as item where ");
        String paramterizedFilterCriteria = criteria.getParamterizedFilterCriteria();
        if(StringUtils.isNotBlank(paramterizedFilterCriteria)){
            sb.append(paramterizedFilterCriteria).append(" and ");
            parameterMap.putAll(criteria.getParameterMap());
        }
        sb.append("ig.id = :itemGroupId");
        return findPageUsingQuery(sb.toString() , 
                criteria.getSortCriteriaString(), "select item ", ps, new QueryParameters(parameterMap));
    }
    
    @Override
    public List<Item> findByIds(final Collection<Long> ids){
        return (List<Item>) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						return session
								.createQuery
									     ("select item from Item item"
										        + " where id in (:ids)")
								.setParameterList("ids",ids)
								.list();
					}
				});
    }


    public ItemGroup findItemGroupByProductOrModelName(final String name, final String itemGroupType)
    {
            return (ItemGroup) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						return session
								.createQuery(
										"select ig.name from ItemGroup ig where "
                                                + "ig.itemGroupType= :itemGroupType  and "
												+ "ig.name = :name")
								.setParameter("name", name).setParameter("itemGroupType", itemGroupType);

					}
				});
    }

	@SuppressWarnings("unchecked")
	public BrandItem findBrandItemById(final Long id) {
		return (BrandItem) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						return session
								.createQuery
									     ("select brandItem from BrandItem brandItem"
										        + " where brandItem.id =:id")
								.setParameter("id",id)
								.uniqueResult();
					}
				});
	}
	
	  
	public PageResult<Item> findAllItemsForContract(Contract contract, ListCriteria listCriteria) {
		PageSpecification ps = listCriteria.getPageSpecification();
		Map<String, Object> parameterMap = new HashMap<String, Object>(2);
		parameterMap.put("contract", contract);
		StringBuilder sb = new StringBuilder();
		sb.append("from Contract c join c.itemsCovered as item where ");
		String paramterizedFilterCriteria = listCriteria.getParamterizedFilterCriteria();
		if (StringUtils.isNotBlank(paramterizedFilterCriteria)) {
			sb.append(paramterizedFilterCriteria).append(" and ");
			parameterMap.putAll(listCriteria.getParameterMap());
		}
		sb.append("c = :contract");
		return findPageUsingQuery(sb.toString(), listCriteria.getSortCriteriaString(), "select item ", ps, new QueryParameters(parameterMap));
	}
	
	public PageResult<Item> findAllItemsOwnedByWithNumberLike(Long id,
			String number, ListCriteria lc) {
		String numberParam = number != null ? number.toUpperCase() : null;
		Map<String, Object> parameterMap = new HashMap<String, Object>(4);
		StringBuilder sb = new StringBuilder("from Item item,ItemMapping im ");
		sb.append(" where item.ownedBy.id = :id and item.id=im.toItem ");
		parameterMap.put("id", id);
		if (StringUtils.isNotBlank(numberParam)) {
			sb.append("and upper(item.number) like :partialNumber ");
			parameterMap.put("partialNumber", numberParam + "%");
		}
		return findPageUsingQuery(sb.toString(), lc.getSortCriteriaString(),
				"select distinct item ", lc.getPageSpecification(),
				new QueryParameters(parameterMap));
	}

	public Item findItemByBrandPartNumber(final String number, final String brand) {

		return (Item) getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session)
					throws HibernateException, SQLException {
					return session.createQuery(
							" select i from Item i, BrandItem b where b.itemNumber = :number " +
							" and i.id=b.item and b.brand=:brand"
							)
							.setParameter("number", number).setParameter("brand", brand).uniqueResult();
				}					
		});
	
	}
	
	public ItemGroup findSeriesByGroupCode(final String seriesGroupCode){
		return (ItemGroup) getHibernateTemplate().execute(new HibernateCallback(){
			public Object doInHibernate(Session session)
				throws HibernateException, SQLException {
			return session.createQuery("from ItemGroup ig where "
												+ "upper(ig.groupCode) = upper(:groupCode)")
					.setParameter("groupCode", seriesGroupCode).uniqueResult();	
			}
		});
		
	}
	
	public ItemGroup findItemGroupByDescription(final String description) {
		return (ItemGroup) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						return session.createCriteria(ItemGroup.class, "ig")
								.add(Restrictions.eq("ig.description", description))
								.setMaxResults(1).uniqueResult();
					}
				});
	}
}

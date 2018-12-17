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
package tavant.twms.domain.supplier;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.util.Assert;

import tavant.twms.domain.catalog.BrandItem;
import tavant.twms.domain.catalog.Item;
import tavant.twms.domain.orgmodel.Organization;
import tavant.twms.domain.orgmodel.Supplier;
import tavant.twms.infra.GenericRepositoryImpl;
import tavant.twms.security.SecurityHelper;

import com.domainlanguage.time.CalendarDate;

public class ItemMappingRepositoryImpl extends GenericRepositoryImpl<ItemMapping, Long> implements
        ItemMappingRepository {

	private SecurityHelper securityHelper ;
	
    @SuppressWarnings("unchecked")
    public List<ItemMapping> findItemMappingsForPart(final Item item, CalendarDate buildDate) {
        return getHibernateTemplate().find("from ItemMapping where fromItem = ?", item);
    }

    @SuppressWarnings("unchecked")
    public List<ItemMapping> findItemMappingsItem(final Item item) {
        return getHibernateTemplate().find("from ItemMapping where fromItem = ?", item);
    }

    public void updateItemMappings(List<ItemMapping> itemMappingList) {
        for (ItemMapping itemMapping : itemMappingList) {
            if (itemMapping.getId() == null) {
                save(itemMapping);
            } else {
                update(itemMapping);
            }
        }
    }

    public ItemMapping findItemMappingForOEMItem(Item item, Supplier supplier,
            CalendarDate buildDate) {
        List<ItemMapping> mappings = findItemMappingsForPart(item, buildDate);
        for (ItemMapping mapping : mappings) {
            if (mapping.getToItem().getOwnedBy().getId().longValue()== supplier.getId().longValue()) {
                return mapping;
            }
        }
        throw new HibernateException("Couldnt find matching item for [" + item
                + "] with supplier [" + supplier + "]");
    }
    
    public ItemMapping findItemMappingForOEMandSupplierItem(Item oemItem, String supplierItemNumber, Supplier supplier){
    	 List<ItemMapping> mappings = findItemMappingsForPart(oemItem,null);
         for (ItemMapping mapping : mappings) {
             if (mapping.getToItem().getOwnedBy().getId().longValue()== supplier.getId().longValue() && mapping.getToItem().getNumber().equalsIgnoreCase(supplierItemNumber)) {
                 return mapping;
             }
         }
         throw new HibernateException("Couldnt find matching item for [" + oemItem
                 + "] with supplier [" + supplier + "]");
    }

    @SuppressWarnings("unchecked")
    public Item findOEMItemForSupplierItem(Item supplierItem) {
        Assert.state(!supplierItem.isOwnedByOEM(), "The Item has to be owned by supplier and not ["
                + supplierItem.getOwnedBy() + "]");
        List<ItemMapping> mappings = getHibernateTemplate().find(
                "from ItemMapping where toItem = ? ", supplierItem);
        //Assert.state(mappings.size() <= 1, "Supplier Item was mapped from more than one OEM item "
        //        + mappings);
        return (mappings.size() == 0 ? null : mappings.get(0).getFromItem());
    }
    
    public List<Item> findSupplierItemsForOEMItem(Item OEMItem){
    	List<ItemMapping> mappings = getHibernateTemplate().find("from ItemMapping where fromItem = ? ", OEMItem);
    	List<Item> supplierItems = new ArrayList<Item>(mappings.size());
    	for(ItemMapping mapping : mappings){
    		supplierItems.add(mapping.getToItem());
    	}
    	return supplierItems;
    }

    @SuppressWarnings("unchecked")
    public List<ItemMapping> findItemMappingForSupplier(Supplier supplier) {
        //return getHibernateTemplate().find("from ItemMapping where toItem.ownedBy = ? ", supplier);
    	 return getHibernateTemplate()
         .find("select distinct itemMapping from Item item, ItemMapping itemMapping where item = itemMapping.toItem and item.ownedBy = ?",
        		 supplier);
    }

    /**
	 * In case there are more than one item mappings for the same TK item and the same supplier , this will give an exception.We should probably use
	 * build date as a parameter also and check for the from and to date of the item mapping also.
	 */
	public ItemMapping findItemMappingForSupplier(final Item item, final Supplier supplier) {
		return (ItemMapping) getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				List itemMappings = session.createQuery(
						" select itemMapping from ItemMapping itemMapping where itemMapping.fromItem = :item "
								+ " and itemMapping.toItem.ownedBy.id = :supplierId").setParameter("item", item).setParameter(
						"supplierId", supplier.getId()).list();
				
				// FIX for SLMSPROD-891 which failed earlier because query expected a unique result.
				if (CollectionUtils.isNotEmpty(itemMappings)) {
					return itemMappings.get(0);
			}
				else {
					return null;
				}
			}
		});
	}

    @SuppressWarnings("unchecked")
    public Item findPartForOEMDealerPart(final Item toItem, final Organization organization) {
        return (Item) getHibernateTemplate().execute(new HibernateCallback() {
            public Object doInHibernate(Session session) throws HibernateException, SQLException {
                return session
                        .createQuery(
                                " select distinct item from"
                                        + " Item item, ItemGroup parent join parent.scheme.purposes purpose,"
                                        + " ItemGroup child, ItemMapping itemMapping, Item toItem where"
                                        + " item in elements(child.includedItems)"
                                        + " and child.nodeInfo.treeId=parent.nodeInfo.treeId"
                                        + " and ( parent.nodeInfo.lft < child.nodeInfo.lft and child.nodeInfo.rgt < parent.nodeInfo.rgt )"
                                        + " and purpose.name='PRODUCT STRUCTURE'"
                                        + " and parent.name = 'Parts'"
                                        + " and parent.itemGroupType = 'PRODUCT TYPE'"
                                        + " and item.ownedBy = :oemorg "                                        
                                        + " and item = itemMapping.fromItem"
                                        + " and itemMapping.toItem = :toItem"
                                        + " and toItem.id = itemMapping.toItem"
                                        + " and toItem.ownedBy = :organization").setParameter(
                                "toItem", toItem).setParameter("organization", organization).
                                setParameter("organization", securityHelper.getOEMOrganization())
                        .uniqueResult();

            }
        });
    }

    @SuppressWarnings("unchecked")
    public Item findOEMDealerPartForPart(final Item fromItem, final Organization organization) {
        return (Item) getHibernateTemplate().execute(new HibernateCallback() {
            public Object doInHibernate(Session session) throws HibernateException, SQLException {
                return session
                        .createQuery(
                                " select distinct item from"
                                        + " Item item, ItemGroup parent join parent.scheme.purposes purpose,"
                                        + " ItemGroup child, ItemMapping itemMapping, Item fromItem where"
                                        + " item in elements(child.includedItems)"
                                        + " and child.nodeInfo.treeId=parent.nodeInfo.treeId"
                                        + " and ( parent.nodeInfo.lft < child.nodeInfo.lft and child.nodeInfo.rgt < parent.nodeInfo.rgt )"
                                        + " and purpose.name='PRODUCT STRUCTURE'"
                                        + " and parent.name = 'Parts'"
                                        + " and parent.itemGroupType = 'PRODUCT TYPE'"
                                        + " and item.ownedBy = :organization"
                                        + " and item = itemMapping.toItem"
                                        + " and itemMapping.fromItem = :fromItem"
                                        + " and fromItem.id = :fromItem"
                                        + " and fromItem.ownedBy = :oemorg"
                                        + " ").setParameter("fromItem",
                                fromItem).setParameter("organization", organization).setParameter("oemorg", securityHelper.getOEMOrganization()).uniqueResult();

            }
        });
    }
    
    @SuppressWarnings("unchecked")
    public Item findPartForOEMDealerPartUsingItemNumber(final String toItemNumber, final Organization organization) {
        return (Item) getHibernateTemplate().execute(new HibernateCallback() {
            public Object doInHibernate(Session session) throws HibernateException, SQLException {
                return session
                        .createQuery(
                                " select distinct item from"
                                        + " Item item, ItemGroup parent join parent.scheme.purposes purpose,"
                                        + " ItemGroup child,  ItemMapping itemMapping, Item toItem where"
                                        + " item in elements(child.includedItems)"
                                        + " and child.nodeInfo.treeId=parent.nodeInfo.treeId"
                                        + " and ( parent.nodeInfo.lft < child.nodeInfo.lft and child.nodeInfo.rgt < parent.nodeInfo.rgt )"
                                        + " and purpose.name='PRODUCT STRUCTURE'"
                                        + " and parent.name = 'Parts'"
                                        + " and parent.itemGroupType = 'PRODUCT TYPE'"
                                        + " and item.ownedBy = :oemorg"                                       
                                        + " and item = itemMapping.fromItem"
                                        + " and itemMapping.toItem.number = :toItemNumber"
                                        + " and toItem.id = itemMapping.toItem"
                                        + " and toItem.ownedBy = :organization").setParameter(
                                "toItemNumber", toItemNumber).setParameter("oemorg", securityHelper.getOEMOrganization()).setParameter("organization", organization)
                        .uniqueResult();

            }
        });
    }
    
    @SuppressWarnings("unchecked")
    public List<Item> fetchManufParts(final String businessUnit, final String partNumber, final int fetchSize, List<Object> itemGroups, boolean onlyServicePart)
    {
    	List<Object> itemListToReturn = null;
    	
    	//fetch the list of items and return it. default start value is 0
        HashMap<String, Object> bindVars = new HashMap<String, Object>();
        Query query =  getSession().createSQLQuery(
                buildPartQueryForManufUsingPartNumber(partNumber, businessUnit, fetchSize, itemGroups, bindVars,onlyServicePart));
        Set<Map.Entry<String, Object>> entrySet = bindVars.entrySet();
        for (Map.Entry<String, Object> entry : entrySet) {
            if (entry.getValue() instanceof List) {
                query.setParameterList(entry.getKey(), (Collection) entry.getValue());
            } else {
                query.setParameter(entry.getKey(), entry.getValue());
            }
        }
        itemListToReturn = query.list();
    	
    	//if we haven't found any items just return an empty array list in case someone is just checking the
    	//size. In that case null pointer won't be thrown at least.
    	return prepareItemsFromResultSet(itemListToReturn);
    }
    
    @SuppressWarnings("unchecked")
    public Item fetchManufPartsUsingPartNumber(final String businessUnit, final String partNumber,final List<Object> itemGroups)
    {
    	return (Item) getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session)
					throws HibernateException, SQLException {
				return session
						.createQuery(
								" select  item from"
										+ " Item item,ItemGroup ig,ItemGroup pg"
										+ "  where"
										+ "  item.number=:itemNumber and item.ownedBy=:ownedBy and item.model=ig.id and pg.name in(:itemGroups)"
										+ " and ( pg.nodeInfo.lft < ig.nodeInfo.lft and ig.nodeInfo.rgt < pg.nodeInfo.rgt )")
						.setParameter("itemNumber", partNumber)
						.setParameter("ownedBy",
								securityHelper.getOEMOrganization())
						.setParameterList("itemGroups", itemGroups)
						.uniqueResult();
			}
		});
    }


	private String buildPartQueryForManufUsingPartNumber(String partNumber,
			String businessUnit, List<Object> itemGroups,
			HashMap<String, Object> bindVars) {
		StringBuffer query = new StringBuffer();
		List<Object> itemGroupNames = new ArrayList<Object>();
		itemGroupNames.addAll(itemGroups);

		if (partNumber != null && partNumber.trim().length() > 0
				&& businessUnit != null) {
			query.append(
					"SELECT distinct i")
					.append("FROM Item i,ItemGroup ig,ItemGroup pg")
					.append(" WHERE ")
					.append(" ( i.number = :partNumber )")
					.append(" AND i.ownedBy = ")
					.append(securityHelper.getOEMOrganization().getId()
							.longValue()).append(" AND i.model = ig.ID");
			bindVars.put("partNumber", partNumber);

			// if we get the item group list as null always append part as
			// default value
			// else use the list.
			if (itemGroupNames.size() == 0)
				itemGroupNames.add("PART");

			query.append(" AND (UPPER(pg.name) in (:itemGroups)) ")
					.append(" AND ig.tree_id = pg.tree_id ")
					.append(" AND ig.lft >= pg.lft ")
					.append(" AND ig.rgt <= pg.rgt ");
			bindVars.put("itemGroups", itemGroupNames);
		}

		return query.toString();
	}

	/**
     * This query is a perf fix native sql query which will be used to fetch causal parts and OEM replaced parts
     * as we need a performing query to do that action.
     */
    private String buildPartQueryForManufUsingPartNumber(final String partNumber, final String businessUnit,
            final int fetchSize, final List<Object> itemGroups, HashMap<String, Object> bindVars,boolean onlyServicePart) {

        StringBuffer query = new StringBuffer();
        List<Object> itemGroupNames = new ArrayList<Object>();
        itemGroupNames.addAll(itemGroups);

    	if (partNumber != null && partNumber.trim().length() > 0 && businessUnit != null) {
    		query.append("SELECT distinct i.id, i.description, i.name, i.item_number, i.duplicate_alternate_number,")
                    .append(" i.alternate_item_number FROM item i,item_group ig,item_group pg")
                    .append(" WHERE ")
                    .append(" i.d_active = 1 AND ( i.item_number LIKE :partNumber )")
    		        .append(" AND i.owned_by = ").append(securityHelper.getOEMOrganization().getId().longValue())
                    .append(" AND i.model = ig.ID");
    		
    		if(onlyServicePart){
    			query.append(" AND i.service_part = 1 ");
    		}
            bindVars.put("partNumber", partNumber + "%");

    		//if we get the item group list as null always append part as default value 
    		//else use the list.
    		if (itemGroupNames.size() == 0)
    			itemGroupNames.add("PART");

			query.append(" AND (UPPER(pg.name) in (:itemGroups)) ")
			.append(" AND ig.tree_id = pg.tree_id ")
			.append(" AND ig.lft >= pg.lft ")
			.append(" AND ig.rgt <= pg.rgt ");
			bindVars.put("itemGroups", itemGroupNames);

    		query.append(" AND rownum < :fetchSize");
            bindVars.put("fetchSize", fetchSize+1);
        }
  
    	return query.toString();
    }
    
    /**
     * This is the method that converts all the objects coming from data base back to Item object instance.
     * It's a very important method. Learn to respect such methods.
     * 
     * @param listOfAllItems
     * @return
     */
    private List<Item> prepareItemsFromResultSet(final List<Object> listOfAllItems)
    {
    	List<Item> listToReturn = null;
    	
    	if(listOfAllItems != null && listOfAllItems.size() > 0)
    	{
    		listToReturn = new ArrayList<Item>(listOfAllItems.size());
    		
    		Object[] currentItemElements = null;
    		Item newItemObject = null;
    		Long duplicateValue = null;
    		
    		for(Object currentItem : listOfAllItems)
    		{
    			duplicateValue = new Long(1);
    			currentItemElements = (Object[])currentItem;
    			
    			//create a new instance of an item object
    			newItemObject = new Item();
    			
    			newItemObject.setId(Long.valueOf(((BigDecimal)currentItemElements[0]).longValue()));
    			newItemObject.setDescription((String)currentItemElements[1]);
    			newItemObject.setName((String)currentItemElements[2]);
    			newItemObject.setNumber((String)currentItemElements[3]);
    			newItemObject.setDuplicateAlternateNumber(true);
    			if(currentItemElements[4] != null)
    			{
    				duplicateValue = Long.valueOf(((BigDecimal)currentItemElements[4]).longValue());
    				if(duplicateValue.equals(new Long(0)))
    				{
    					newItemObject.setDuplicateAlternateNumber(false);
    				}    				
    			}
    			newItemObject.setAlternateNumber((String)currentItemElements[5]);
    			
    			//add new item object to the list
    			listToReturn.add(newItemObject);
    		}	
    	}
    	
    	//return the list of the objects
    	return listToReturn == null? new ArrayList<Item>():listToReturn;
    }

	public SecurityHelper getSecurityHelper() {
		return securityHelper;
	}

	public void setSecurityHelper(SecurityHelper securityHelper) {
		this.securityHelper = securityHelper;
	}
    
	 private String buildPartQueryForManufUsingBrandPartNumber(final String partNumber, final String businessUnit,
	            final int fetchSize, final List<Object> itemGroups, HashMap<String, Object> bindVars,boolean onlyServicePart,List<String> brands, boolean activeParts) {

	        StringBuffer query = new StringBuffer();
	        List<Object> itemGroupNames = new ArrayList<Object>();
	        itemGroupNames.addAll(itemGroups);

	    	if (partNumber != null && partNumber.trim().length() > 0 && businessUnit != null) {
	    		query.append("SELECT distinct bi.id brandid,bi.item_number branditemnumber,i.id, i.description, i.name, i.item_number,bi.brand, i.status ")
	                    .append(" FROM brand_item bi,item i,item_group ig,item_group pg")
//	                    .append(" WHERE i.business_unit_info = :businessUnit")
	                    .append(" WHERE bi.item = i.id ")
	                    .append(" AND i.d_active = 1 AND ( bi.item_number LIKE :partNumber )")
	                   //.append(" AND bi.brand in (:brands)")
	    		        .append(" AND i.owned_by = ").append(securityHelper.getOEMOrganization().getId().longValue())
	                    .append(" AND i.model = ig.ID");

	    		if(onlyServicePart){
	    			query.append(" AND i.service_part = 1 ");
	    		}

//	            bindVars.put("businessUnit", businessUnit);
	            bindVars.put("partNumber", partNumber + "%");
                if(brands!=null && brands.size()>0){
                    query.append(" AND bi.brand in (:brands)");
                    bindVars.put("brands", brands);
                }

                //if we get the item group list as null always append part as default value
	    		//else use the list.
	    		if (itemGroupNames.size() == 0)
	    			itemGroupNames.add("PART");

				query.append(" AND (UPPER(pg.name) in (:itemGroups)) ")
				.append(" AND ig.tree_id = pg.tree_id ")
				.append(" AND ig.lft >= pg.lft ")
				.append(" AND ig.rgt <= pg.rgt ");
				bindVars.put("itemGroups", itemGroupNames);

	    		query.append(" AND rownum < :fetchSize");
	            bindVars.put("fetchSize", fetchSize+1);
	        }
	  
	    	return query.toString();
	    }
	    
	 private List<BrandItem> prepareBrandItemsFromResultSet(final List<Object> listOfAllItems)
	    {
	    	List<BrandItem> listToReturn = null;
	    	
	    	if(listOfAllItems != null && listOfAllItems.size() > 0)
	    	{
	    		listToReturn = new ArrayList<BrandItem>(listOfAllItems.size());
	    		
	    		Object[] currentItemElements = null;
	    		Item newItemObject = null;
	    		BrandItem newBrandItemObject = null;
	    		
	    		
	    		for(Object currentItem : listOfAllItems)
	    		{
	    		
	    			currentItemElements = (Object[])currentItem;
	    			newBrandItemObject = new BrandItem();
	    			//create a new instance of an item object
	    			newBrandItemObject.setId(((BigDecimal)currentItemElements[0]).longValue());
	    			newBrandItemObject.setItemNumber((String)currentItemElements[1]);
	    			newBrandItemObject.setBrand((String)currentItemElements[6]);
	    			newItemObject = new Item();
	    			
	    			newItemObject.setId(Long.valueOf(((BigDecimal)currentItemElements[2]).longValue()));
	    			newItemObject.setDescription((String)currentItemElements[3]);
	    			newItemObject.setName((String)currentItemElements[4]);
	    			newItemObject.setNumber((String)currentItemElements[5]);
	    			newItemObject.setStatus((String)currentItemElements[7]);
	    			newBrandItemObject.setItem(newItemObject);
	    			//add new item object to the list
	    			listToReturn.add(newBrandItemObject);
	    		}	
	    	}
	    	
	    	//return the list of the objects
	    	return listToReturn == null? new ArrayList<BrandItem>():listToReturn;
	    }

	 @SuppressWarnings("unchecked")
		public List<BrandItem> fetchManufBrandParts(String businessUnit,
				String partNumber, int fetchSize, List<Object> itemGroups,
				boolean onlyServiceParts, List<String> brands, boolean activeParts) {
			
			List<Object> itemListToReturn = new ArrayList<Object>();
	    	
	    	//fetch the list of items and return it. default start value is 0
	        HashMap<String, Object> bindVars = new HashMap<String, Object>();
	        Query query =  getSession().createSQLQuery(
	                buildPartQueryForManufUsingBrandPartNumber(partNumber, businessUnit, fetchSize, itemGroups, bindVars,onlyServiceParts,brands, activeParts));
	        Set<Map.Entry<String, Object>> entrySet = bindVars.entrySet();
	        for (Map.Entry<String, Object> entry : entrySet) {
	            if (entry.getValue() instanceof List) {
	                query.setParameterList(entry.getKey(), (Collection) entry.getValue());
	            } else {
	                query.setParameter(entry.getKey(), entry.getValue());
	            }
	        }
	        
	        itemListToReturn = query.list();
	    	
	    	
	    return prepareBrandItemsFromResultSet(itemListToReturn);
		}
	 
	@SuppressWarnings("unchecked")
	public BrandItem fetchManufBrandPartswithBarndItemNumber(
			final String businessUnit, final String partNumber,
			final List<Object> itemGroups, final String brand) {
		return (BrandItem) getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session)
					throws HibernateException, SQLException {
				return session
						.createQuery(
								" select  bi from"
										+ " Item item join item.belongsToItemGroups ig,ItemGroup ig1, BrandItem bi"
										+ "  where ig.id=ig1.id and "
										+ " item.id=bi.item and bi.itemNumber=:itemNumber and bi.brand=:brand and item.ownedBy=:ownedBy and ig.name in(:itemGroups)")
										//+ " and ( pg.nodeInfo.lft < ig.nodeInfo.lft and ig.nodeInfo.rgt < pg.nodeInfo.rgt )")
						.setParameter("itemNumber", partNumber)
						.setParameter("brand", brand)
						.setParameter("ownedBy",
								securityHelper.getOEMOrganization())
						.setParameterList("itemGroups", itemGroups)
						.uniqueResult();
			}
		});
	}

	@SuppressWarnings("unchecked")
	public List<BrandItem> fetchManufBrandPartswithBarndItemNumber(
			final String businessUnit, final String partNumber,
			final List<Object> itemGroupss, final List<String> brands) {
		
		final List<String> itemGroups=new ArrayList<String>();
		itemGroups.add("PARTS");
		return (List<BrandItem>) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						return session
								.createQuery(
										" select  bi from"
												+ " Item item join item.belongsToItemGroups ig,ItemGroup ig1, BrandItem bi"
												+ "  where ig.id=ig1.id and "
											   // + " ig.businessUnitInfo=:businessUnitInfo"
												+ " item.id=bi.item and bi.itemNumber=:itemNumber and bi.brand in(:brands) and item.ownedBy=:ownedBy and ig.name in(:itemGroups)")
												//+ " and ( pg.nodeInfo.lft < ig.nodeInfo.lft and ig.nodeInfo.rgt < pg.nodeInfo.rgt )")
							   // .setParameter("businessUnitInfo", businessUnit)
								.setParameter("itemNumber", partNumber)
								.setParameterList("brands", brands)
								.setParameter("ownedBy",
										securityHelper.getOEMOrganization())
								.setParameterList("itemGroups", itemGroups)
								.list();
					}
				});
	}
	

	private String buildPartQueryForManufUsingBrandPartNumber(
			String partNumber, String businessUnit, List<Object> itemGroups,
			HashMap<String, Object> bindVars, String brand) {
		StringBuffer query = new StringBuffer();
		List<Object> itemGroupNames = new ArrayList<Object>();
		itemGroupNames.addAll(itemGroups);
		if (partNumber != null && partNumber.trim().length() > 0
				&& businessUnit != null) {
			query.append(
					"SELECT i.*")
					.append(" FROM brand_item bi,item i,item_group ig,item_group pg")
//					.append(" WHERE i.business_unit_info = :businessUnit")
					.append(" WHERE bi.item = i.id ")
					.append(" AND i.d_active = 1 AND ( bi.item_number = :partNumber )")
					.append(" AND bi.brand =:brand")
					.append(" AND i.owned_by = ")
					.append(securityHelper.getOEMOrganization().getId()
							.longValue()).append(" AND i.model = ig.ID");
//			bindVars.put("businessUnit", businessUnit);
			bindVars.put("partNumber", partNumber);
			bindVars.put("brand", brand);

			// if we get the item group list as null always append part as
			// default value
			// else use the list.
			if (itemGroupNames.size() == 0)
				itemGroupNames.add("PART");

			query.append(" AND (UPPER(pg.name) in (:itemGroups)) ")
					.append(" AND ig.tree_id = pg.tree_id ")
					.append(" AND ig.lft >= pg.lft ")
					.append(" AND ig.rgt <= pg.rgt ");
			bindVars.put("itemGroups", itemGroupNames);
		}

		return query.toString();
	}

	public void deleteSupplierItemLocation(final Long id){
		 getHibernateTemplate().execute(
					new HibernateCallback(){
						public Object doInHibernate(Session session)
								throws HibernateException, SQLException {
							String queryString ="delete from SupplierItemLocation where id =:queryId";
							session.createQuery(queryString).setParameter("queryId", id).executeUpdate();
							return null;
						}
						
					});		
	 }

	public List<BrandItem> fetchBrandItemsForbrandPartNumber(
			final String businessUnit, final String partNumber,
			final List<Object> itemGroups) {
		return (List<BrandItem>) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						List<BrandItem> brandItems= session
								.createQuery(
										" select  bi from"
												+ " Item item join item.belongsToItemGroups ig,ItemGroup ig1,BrandItem bi"
												+ "  where ig.id=ig1.id and "
											//+" ig.businessUnitInfo=:businessUnit"
												+ " item.id=bi.item and bi.itemNumber=:itemNumber and item.ownedBy=:ownedBy and ig.name in(:itemGroups)")
												//+ " and ( pg.nodeInfo.lft < ig.nodeInfo.lft and ig.nodeInfo.rgt < pg.nodeInfo.rgt )")
												//.setParameter("businessUnit", businessUnit)
								.setParameter("itemNumber", partNumber)
								.setParameter("ownedBy",
										securityHelper.getOEMOrganization())
								.setParameterList("itemGroups", itemGroups)
								.list();
						return brandItems;
						
					}
				});
	}

}

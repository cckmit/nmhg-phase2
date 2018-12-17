/*
 *   Copyright (c)2007 Tavant Technologies
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

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.hibernate3.HibernateCallback;

import tavant.twms.domain.bu.BusinessUnit;
import tavant.twms.domain.common.AdminConstants;
import tavant.twms.domain.inventory.Option;
import tavant.twms.domain.orgmodel.ServiceProvider;
import tavant.twms.domain.partreturn.PartReturnDefinition;
import tavant.twms.infra.GenericRepositoryImpl;
import tavant.twms.infra.ListCriteria;
import tavant.twms.infra.PageResult;
import tavant.twms.infra.PageSpecification;
import tavant.twms.security.SecurityHelper;

/**
 * @author aniruddha.chaturvedi
 *
 */
public class ItemGroupRepositoryImpl extends GenericRepositoryImpl<ItemGroup, Long> implements
        ItemGroupRepository {	
	private SecurityHelper securityHelper;
	
    public SecurityHelper getSecurityHelper() {
		return securityHelper;
	}

	public void setSecurityHelper(SecurityHelper securityHelper) {
		this.securityHelper = securityHelper;
	}

	public List<ItemGroup> findItemGroupsFromScheme(ItemScheme itemScheme) {
        String query = "select ic from ItemGroup ic where ic.scheme =:scheme";
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("scheme", itemScheme);
        return findUsingQuery(query, params);
    }

    public ItemGroup findGroupContainingItem(Item item, ItemScheme scheme) {
        String query = "select ic from ItemGroup ic join ic.includedItems as item where item=:anItem and ic.scheme =:scheme ";
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("scheme", scheme);
        params.put("anItem", item);
        return findUniqueUsingQuery(query, params);
    }

    @SuppressWarnings("unchecked")
    public PageResult<ItemGroup> findPage(final ListCriteria listCriteria,
            final ItemScheme itemScheme) {
        PageSpecification pageSpecification = listCriteria.getPageSpecification();
        final StringBuffer countQuery = new StringBuffer(" select count(*) ");

        final StringBuffer fromAndWhereClause = new StringBuffer();
        final String fromClause = "from ItemGroup itemGroup where itemGroup.scheme=:scheme";
        fromAndWhereClause.append(fromClause);

        if (listCriteria.isFilterCriteriaSpecified()) {
            fromAndWhereClause.append(" and ");
            fromAndWhereClause.append(listCriteria.getParamterizedFilterCriteria());
        }

        countQuery.append(fromAndWhereClause);

        if (logger.isDebugEnabled()) {
            logger.debug("findPage(" + fromClause + ",listCriteria) count query is [" + countQuery
                    + "]");
        }

        Long numberOfRows = (Long) getHibernateTemplate().execute(new HibernateCallback() {
            public Object doInHibernate(Session session) throws HibernateException, SQLException {
                Query query = session.createQuery(countQuery.toString());
                query.setParameter("scheme", itemScheme);
                for (Map.Entry<String, Object> parameterSpecification : listCriteria
                        .getParameterMap().entrySet()) {
                    String name = parameterSpecification.getKey();
                    Object value = parameterSpecification.getValue();
                    query.setParameter(name, value);
                }
                return query.uniqueResult();
            }
        });
        Integer numberOfPages = pageSpecification.convertRowsToPages(numberOfRows);

        List<ItemGroup> rowsInPage = new ArrayList<ItemGroup>();
        PageResult<ItemGroup> page = new PageResult<ItemGroup>(rowsInPage, pageSpecification,
                numberOfPages);

        if (logger.isDebugEnabled()) {
            logger.debug(" fetchPage(" + pageSpecification + ",...,...) found (rows="
                    + numberOfRows + ",pages=" + numberOfPages + ")");
        }

        final Integer pageOffset = pageSpecification.offSet();
        if (numberOfRows > 0 && numberOfRows > pageOffset) {

            final Integer pageSize = pageSpecification.getPageSize();
            rowsInPage = (List<ItemGroup>) getHibernateTemplate().execute(new HibernateCallback() {
                public Object doInHibernate(Session session) throws HibernateException,
                        SQLException {
                    StringBuffer filterAndSort = new StringBuffer(fromAndWhereClause);

                    if (listCriteria.isSortCriteriaSpecified()) {
                        filterAndSort.append(" order by ");
                        filterAndSort.append(listCriteria.getSortCriteriaString());
                    }

                    if (logger.isDebugEnabled()) {
                        logger.debug(" Unpaginated query for findPage(" + fromClause
                                + ",listCriteria) [ " + filterAndSort + " ]");
                    }

                    Query query = session.createQuery(filterAndSort.toString());
                    for (Map.Entry<String, Object> parameterSpecification : listCriteria
                            .getParameterMap().entrySet()) {
                        String name = parameterSpecification.getKey();
                        Object value = parameterSpecification.getValue();
                        query.setParameter(name, value);
                    }

                    query.setParameter("scheme", itemScheme);
                    return query.setFirstResult(pageOffset).setMaxResults(pageSize).list();
                }
            });
            page = new PageResult<ItemGroup>(rowsInPage, pageSpecification, numberOfPages);
        }
        return page;
    }

    public ItemGroup findItemGroupByName(String name, ItemScheme itemScheme) {
        String query = "select ic from ItemGroup ic where ic.scheme =:scheme and upper(ic.name)=upper(:name)";
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("scheme", itemScheme);
        params.put("name", name);
        return findUniqueUsingQuery(query, params);
    }

    public ItemGroup findItemGroupByCode(final String code) {
        String query = "select ig from ItemGroup ig where upper(ig.groupCode)=:code";
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("code", code.toUpperCase());
        return findUniqueUsingQuery(query, params);
    }
    
    public ItemGroup findItemGroupByCodeAndIsPartOf(final String code,ItemGroup isPartOf) {
        String query = "select ig from ItemGroup ig where upper(ig.groupCode)=:code and ig.isPartOf=:isPartOf";
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("code", code.toUpperCase());
        params.put("isPartOf", isPartOf);
        return findUniqueUsingQuery(query, params);
    }
    
    public ItemGroup findItemGroupByCodeAndType(final String code, final String groupType) {
        String query = "select ig from ItemGroup ig where upper(ig.groupCode)=:code and ig.itemGroupType =:type";
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("code", code.toUpperCase());
        params.put("type", groupType);
        return findUniqueUsingQuery(query, params);
    }
    
    public ItemGroup findItemGroupByCodeAndTypeIncludeInactive(final String code, final String groupType){
    	 return (ItemGroup) getHibernateTemplate().execute(new HibernateCallback() {
             public Object doInHibernate(Session session) throws HibernateException, SQLException {
             	session.disableFilter("excludeInactive");
                 return session
                         .createQuery(
                                 "select ig from ItemGroup ig where upper(ig.groupCode)=:code and ig.itemGroupType =:type")
                         .setParameter("code", code.toUpperCase())
                         .setParameter("type", groupType).uniqueResult();
             };
         });
    }

      public ItemGroup findItemGroupForModel(final String productCode, final String modelCode) {
        String query = "select child from ItemGroup child,ItemGroup parent where upper(child.groupCode)=:modelCode and upper(parent.groupCode)=:productCode" +
                " and child.itemGroupType = 'MODEL' and parent.itemGroupType = 'PRODUCT'";
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("modelCode", modelCode.toUpperCase());
        params.put("productCode", productCode.toUpperCase());
        return findUniqueUsingQuery(query, params);
    }

    //Here BUfilter must be applied, please don't disable bufilter for this method
    public ItemGroup findItemGroupByType(final String groupType) {
        String query = "select ig from ItemGroup ig where ig.itemGroupType =:type";
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("type", groupType);
        return findUniqueUsingQuery(query, params);
    }

    public List<ItemGroup> findGroupsByNameAndDescription(ItemScheme scheme, String name,
            String description) {
        String query = "select ic from ItemGroup ic where ic.scheme =:scheme and upper(ic.name) like " +
        		"upper(:nameParam) and upper(ic.description) like upper(:descriptionParam) order by ic.name";
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("scheme", scheme);
        params.put("nameParam", name + "%");
        params.put("descriptionParam", description + "%");
        return findUsingQuery(query, params);

    }

	public ItemGroup findItemGroupByName(String name) {
        String query = "select ic from ItemGroup ic where upper(ic.name)=upper(:name)";
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("name", name);
        return findUniqueUsingQuery(query, params);
	}


    public ItemGroup findByNameAndPurpose(String name, String purpose) {
        String query = "select ig from ItemGroup ig where upper(ig.name) =upper(:name) " +
        		"and ig.scheme in (select itemScheme from ItemScheme itemScheme join itemScheme.purposes " +
        		"as purpose where purpose.name=:purpose)";
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("name", name);
        params.put("purpose", purpose);
        return findUniqueUsingQuery(query, params);
    }

    @SuppressWarnings("unchecked")
    public List<String> findGroupsWithNameStartingWith(final String name,
            final PageSpecification pageSpecification, final String purpose) {
        return (List<String>) getHibernateTemplate().execute(new HibernateCallback() {
            public Object doInHibernate(Session session) throws HibernateException, SQLException {
                return session
                        .createQuery(
                                "select itemGroup.name from ItemGroup itemGroup where upper(itemGroup.name) " +
                                "like upper(:name) and itemGroup.scheme in (select itemScheme from ItemScheme itemScheme " +
                        		"join itemScheme.purposes as purpose where purpose.name=:purpose)")
                        .setParameter("name", name + "%")
                        .setParameter("purpose", purpose)
                        .setFirstResult(
                                pageSpecification.getPageSize() * pageSpecification.getPageNumber())
                        .setMaxResults(pageSpecification.getPageSize()).list();
            };
        });
    }

    public List<ItemGroup> findDescendentsOf(final ItemGroup itemGroup) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("treeId", itemGroup.getNodeInfo().getTreeId());
        params.put("lft", itemGroup.getNodeInfo().getLft());
        params.put("rgt", itemGroup.getNodeInfo().getRgt());
        return findUsingQuery(
                " from ItemGroup ig where ig.nodeInfo.treeId=:treeId and :lft < ig.nodeInfo.lft " +
                "and ig.nodeInfo.rgt < :rgt order by ig.nodeInfo.depth,ig.nodeInfo.lft",
                params);
    }
    
    public ItemGroup  findProductOfModel(final ItemGroup itemGroup){
    	 Map<String, Object> params = new HashMap<String, Object>();
         params.put("treeId", itemGroup.getNodeInfo().getTreeId());
         params.put("lft", itemGroup.getNodeInfo().getLft());
         params.put("rgt", itemGroup.getNodeInfo().getRgt());
         List<ItemGroup> product =  findUsingQuery(
                 " from ItemGroup ig where ig.nodeInfo.treeId=:treeId and :lft > ig.nodeInfo.lft " +
                 "and ig.nodeInfo.rgt > :rgt  and ig.itemGroupType = 'PRODUCT' order by ig.nodeInfo.depth,ig.nodeInfo.lft ",
                 params);
         return (product!=null ? product.get(0) : null);
    }

    @SuppressWarnings("unchecked")
    public List<ItemGroup> findModelsForProduct(final ItemGroup itemGroup) {
        return (List<ItemGroup>) getHibernateTemplate().execute(new HibernateCallback() {
            public Object doInHibernate(Session session) throws HibernateException, SQLException {
                Map<String, Object> params = new HashMap<String, Object>();
                params.put("treeId", itemGroup.getNodeInfo().getTreeId());
                params.put("lft", itemGroup.getNodeInfo().getLft());
                params.put("rgt", itemGroup.getNodeInfo().getRgt());
                Query hbmQuery = session.getNamedQuery("modelsForItemGroup");
                hbmQuery.setProperties(params);
                return hbmQuery.list();
            }
        });
    }

    public List<ItemGroup> findGroupsForGroupType(final String groupType) {
        String query = "select ig from ItemGroup ig where ig.itemGroupType=:groupType";
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("groupType", groupType);
        return findUsingQuery(query, params);
    }
   
   
      
    public ItemGroup findItemGroupsForWatchedItem(Item item, String itemWatchlistType) {
        String query = "select ig from ItemGroup ig join ig.scheme itemScheme join itemScheme.purposes as purpose " +
        		"join ig.includedItems as item where purpose.name= :itemWatchlistType and item = :item ";
        Map<String, Object> params = new HashMap<String, Object>(3);
        params.put("itemWatchlistType", itemWatchlistType);
        params.put("item", item);
        return findUniqueUsingQuery(query, params);
    }
    
    public ItemGroup isItemPresentInItemGroupWithName(Item item, String itemGroupName) {
        String query = "select ig from ItemGroup ig join ig.includedItems as item where ig.name= :itemGroupName and item = :item ";
        Map<String, Object> params = new HashMap<String, Object>(3);
        params.put("itemGroupName", itemGroupName);
        params.put("item", item);
        return findUniqueUsingQuery(query, params);
    }

    public List<ItemGroup> findGroupsByNameAndType(String userEntry, String groupType) {
        String query = "select ig from ItemGroup ig join ig.scheme.purposes purpose "
				+ " where ig.itemGroupType =:type and upper(ig.name) like upper(:nameParam) "
				+ " and purpose.name='PRODUCT STRUCTURE' "
				+ " order by ig.name";
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("nameParam", userEntry + "%");
        params.put("type", groupType);
        return findUsingQuery(query, params);
    }
    
    @SuppressWarnings("unchecked")
	public List<ItemGroup> listAllProductsAndModelsMatchingName(final String partialName,
			final List<String> itemGroupTypes,final PageSpecification pageSpecification){
		return (List<ItemGroup>) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {		
								//Fix for SLMSPROD-1486
								String propertyForPartialName="";
								StringBuilder appendQuery = new StringBuilder(
										" and ( itemGroup.itemGroupType  = 'PRODUCT' ");
								if (itemGroupTypes.contains("MODEL")) {
									appendQuery = appendQuery
											.append(" or itemGroup.itemGroupType = 'MODEL' )");
								} else {
									appendQuery = appendQuery.append(" ) ");
								}
								if(isBuConfigAMER()){
									propertyForPartialName = "itemGroup.groupCode";
								}else{
									propertyForPartialName = "itemGroup.name";
								}								
								Query q= session.createQuery(
										"select itemGroup from ItemGroup itemGroup join itemGroup.scheme.purposes purpose " +
										"where upper("+propertyForPartialName+") like :name " +
										appendQuery.toString() +
										"and purpose.name = 'PRODUCT STRUCTURE' ")
								.setParameter("name", partialName.toUpperCase() + "%")
								.setFirstResult(
		                        		pageSpecification.getPageSize() * pageSpecification.getPageNumber())
								.setMaxResults(pageSpecification.getPageSize());
								return q.list();
					}
				});
	
		
	}

    public List<ItemGroup> findGroupsByCodeAndType(String userEntry, String groupType) {
        String query = "select ig from ItemGroup ig where upper(ig.itemGroupType) =:type and upper(ig.groupCode) like :codeParam  order by ig.name";
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("codeParam", userEntry + "%");
        params.put("type", groupType);
        return findUsingQuery(query, params);
    }

    @SuppressWarnings("unchecked")
    public List<PartReturnDefinition> findPartReturnDefinitionForItem(final Item item) {
        return (List<PartReturnDefinition>) getHibernateTemplate().execute(new HibernateCallback() {
            public Object doInHibernate(Session session) throws HibernateException, SQLException {
                return session.createQuery(
                        "select prc from PartReturnDefinition prc where prc.itemCriterion.item =:item "
                		+" OR prc.itemCriterion.itemGroup in(select ig from ItemGroup ig join ig.includedItems as item "
                		+" where  item = :item )")
                        .setParameter("item",item).list();
            }

        });
    }

    public List<ItemGroup> findItemGroupsByPurposes(List<String> purposes) {
		String query = "select distinct ig from ItemGroup ig join ig.scheme scheme join scheme.purposes purpose join  "
				+ " ig.includedItems as item where "
				+ " purpose.name in (:purposes)"
				+ " order by ig.name asc ";
		Map<String, Object> params = new HashMap<String, Object>(2);
		params.put("purposes", purposes);
		return findUsingQuery(query, params);
	}

    public void updateTreeInfo(final String groupCode, final String itemScheme, final String buName)
    {
		getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session)
				throws HibernateException, SQLException
				{
				return (session.createSQLQuery("BEGIN UPDATE_TREE_INFO(:groupCode, :itemScheme, :buName); END;")
                        .setString("groupCode", groupCode)
                        .setString("itemScheme", itemScheme)
                        .setString("buName", buName)
                        .executeUpdate());
			    }
		});
     }

    public ItemGroup findItemGroupsById(final Long id) {
        String query = "select ic from ItemGroup ic where ic.id = :id";
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("id", id);
        return findUniqueUsingQuery(query, params);
	}

	@SuppressWarnings("unchecked")
	public PageResult<ItemGroup> fetchPageForModels(final ListCriteria criteria) {
		return (PageResult<ItemGroup>) getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				Criteria productCriteria = session.createCriteria(ItemGroup.class);
				productCriteria.add(Restrictions.eq("itemGroupType", ItemGroup.MODEL));
				return fetchPage(productCriteria, criteria, null);
			}
		});
	}

	public void updateMachineUrlForModel(ItemGroup model) {
		getSession().createQuery("update ItemGroup itemGroup set itemGroup.machineUrl = :machineUrl " +
				" where itemGroup.id = :modelId").setParameter("machineUrl", model.getMachineUrl())
				.setParameter("modelId", model.getId()).executeUpdate();
	}

    public List<ItemGroup> findAllModelForLabel(final String labelName) {
		List<ItemGroup> itemGroups;
		String query = "select itemGroup from ItemGroup itemGroup join itemGroup.labels label where label.name=:label"
				+ " order by itemGroup.name";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("label", labelName);
		return findUsingQuery(query, params);

	}

    @SuppressWarnings("unchecked")
    public ItemGroup findModelForProduct(final ItemGroup itemGroup, final String modelCode) {
        return (ItemGroup) getHibernateTemplate().execute(new HibernateCallback() {
            public Object doInHibernate(Session session) throws HibernateException, SQLException {
                Map<String, Object> params = new HashMap<String, Object>();
                params.put("treeId", itemGroup.getNodeInfo().getTreeId());
                params.put("lft", itemGroup.getNodeInfo().getLft());
                params.put("rgt", itemGroup.getNodeInfo().getRgt());
                params.put("modelCode", modelCode.toUpperCase());
                params.put("isPartOf", itemGroup);
                return findUniqueUsingNamedQuery("modelForProduct", params);
            }
        });
    }
    
    @SuppressWarnings("unchecked")
    public List<ItemGroup>  findItemGroupsByPurposeStartingWith(final String purpose,final String searchPrefix,final PageSpecification pageSpecification){
        return (List<ItemGroup>) getHibernateTemplate().execute(new HibernateCallback() {
            public Object doInHibernate(Session session) throws HibernateException, SQLException {
                return session
                        .createQuery(
                                "select itemGroup from ItemGroup itemGroup where upper(itemGroup.name) " +
                                "like upper(:name) and itemGroup.scheme in (select itemScheme from ItemScheme itemScheme " +
                        		"join itemScheme.purposes as purpose where purpose.name=:purpose)")
                        .setParameter("name", searchPrefix + "%")
                        .setParameter("purpose", purpose)
                        .setFirstResult(
                        		pageSpecification.getPageSize() * pageSpecification.getPageNumber())
                        .setMaxResults(pageSpecification.getPageSize()).list();
            };
        });
    	   }



    public ItemGroup findProductFamilyForProduct(final ItemGroup itemGroup, final String productFamilyCode) {
        return (ItemGroup) getHibernateTemplate().execute(new HibernateCallback() {
            public Object doInHibernate(Session session) throws HibernateException, SQLException {
                Map<String, Object> params = new HashMap<String, Object>();
                params.put("treeId", itemGroup.getNodeInfo().getTreeId());
                params.put("lft", itemGroup.getNodeInfo().getLft());
                params.put("rgt", itemGroup.getNodeInfo().getRgt());
                params.put("productFamilyCode", productFamilyCode.toUpperCase());
                return findUniqueUsingNamedQuery("productFamilyForProduct", params);
            }
        });
    }
    
    public ItemGroup findProductFamilyForProductType(final ItemGroup productType,final String productFamilyCode){
    	return (ItemGroup) getHibernateTemplate().execute(new HibernateCallback() {
            public Object doInHibernate(Session session) throws HibernateException, SQLException {
                Map<String, Object> params = new HashMap<String, Object>();
                params.put("treeId", productType.getNodeInfo().getTreeId());
                params.put("lft", productType.getNodeInfo().getLft());
                params.put("rgt", productType.getNodeInfo().getRgt());
                params.put("productFamilyCode", productFamilyCode.toUpperCase());
                return findUniqueUsingNamedQuery("productTypeFamilyForProduct", params);
            }
        });
    }


    public ItemGroup findItemGroupForItem(final ItemGroup itemGroup, final Item item,final String purpose) {
            return (ItemGroup) getHibernateTemplate().execute(
                new HibernateCallback() {
                    public Object doInHibernate(Session session)
                            throws HibernateException, SQLException {
                        return session.createQuery("from ItemGroup ig where" +
                                " (:lft < ig.nodeInfo.lft and " +
                                " ig.nodeInfo.rgt < :rgt and " +
                                " :item in elements (ig.includedItems) " +
                                " and ig.scheme in (select itemScheme from ItemScheme itemScheme join itemScheme.purposes " +
                                " as purpose where purpose.name=:purpose)) "+
                                " or (ig = :itemGroup and :item in elements (ig.includedItems))").
                                setParameter("lft",itemGroup.getNodeInfo().getLft()).
                                setParameter("rgt",itemGroup.getNodeInfo().getRgt()).
                                setParameter("itemGroup",itemGroup).
                                setParameter("purpose",purpose).
                                setParameter("item",item).uniqueResult();
                    }
                });
        }

 
	@SuppressWarnings("unchecked")
	public List<Option> findOptionsList(final String brandType,final String searchPrefix,final int pageNumber, final int pageSize) {

		return (List<Option>) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						return session
								.createQuery(
										 
										 "select distinct(options.optionCode) from InventoryItem inventoryItem join inventoryItem.options options where (upper(inventoryItem.brandType) in (upper(:brandType),'UTILEV')) and (upper(options.optionCode)) = :name")
								        .setParameter("brandType", brandType)
								        .setParameter("name", searchPrefix.toUpperCase() + "%")
									    .setFirstResult(pageSize * pageNumber)
									    .setMaxResults(pageSize).list();
						 
					}
					
				});
	
	}
	 @SuppressWarnings("unchecked")
	public List<Option> findOptionsList(final String searchPrefix,final int pageNumber, final int pageSize) {

		return (List<Option>) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						return session
								.createQuery(
										"select distinct(options.optionCode) from InventoryItem inventoryItem join inventoryItem.options options  where (upper(options.optionCode)) = :name")
										 .setParameter("name", searchPrefix.toUpperCase() + "%")
										 .setFirstResult(pageSize * pageNumber)
								         .setMaxResults(pageSize).list();
					}
				});
	
	}
	 
	 @SuppressWarnings("unchecked")
		public List<Option> findOptionDescriptionList(final String brandType,final String searchPrefix,final int pageNumber, final int pageSize) {

			return (List<Option>) getHibernateTemplate().execute(
					new HibernateCallback() {
						public Object doInHibernate(Session session)
								throws HibernateException, SQLException {
							return session
									.createQuery(
											 
											 "select distinct(options.optionDescription) from InventoryItem inventoryItem join inventoryItem.options options where (upper(inventoryItem.brandType) in (upper(:brandType),'UTILEV')) and (upper(options.optionDescription)) like upper(: % name %)")
									        .setParameter("brandType", brandType)
									        .setParameter("name", searchPrefix.toUpperCase() + "%")
										    .setFirstResult(pageSize * pageNumber)
										    .setMaxResults(pageSize).list();
							 
						}
						
					});
		
		}
		 @SuppressWarnings("unchecked")
		public List<Option> findOptionDescriptionList(final String searchPrefix,final int pageNumber, final int pageSize) {

			return (List<Option>) getHibernateTemplate().execute(
					new HibernateCallback() {
						public Object doInHibernate(Session session)
								throws HibernateException, SQLException {
							return session
									.createQuery(
											"select distinct(options.optionDescription) from InventoryItem inventoryItem join inventoryItem.options options  where (upper(options.optionDescription)) like upper(: % name %)")
											 .setParameter("name", searchPrefix.toUpperCase() + "%")
											 .setFirstResult(pageSize * pageNumber)
									         .setMaxResults(pageSize).list();
						}
					});
		
		}
		 
	  
	public List<ItemGroup> findGroupsByNameAndTypeAndBrand(String userEntry,
			String groupType, String brandType) {
		String query = "select ig from ItemGroup ig join ig.scheme.purposes purpose "
				+ " where  ig.itemGroupType =:type and upper(ig.name) like upper(:nameParam) and (upper(ig.companyType) = upper(:brandType)) "
				+ " and purpose.name='PRODUCT STRUCTURE' "
				+ " order by ig.name";
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("nameParam", userEntry + "%");
        params.put("type", groupType);
        params.put("brandType", brandType);
        return findUsingQuery(query, params);
	}
	
	
	

	public List<ItemGroup> findModelByNameAndTypeAndBrand(String userEntry,
			String groupType, String brandType) {
		String query = "select ig1 from ItemGroup ig1 join ig1.isPartOf ig2 join ig1.scheme.purposes purpose "
				+ " where ig1.itemGroupType =:type and upper(ig1.name) like upper(:nameParam) and (upper(ig2.companyType) = upper(:brandType))"
				+ " and purpose.name='PRODUCT STRUCTURE' "
				+ " order by ig1.name";
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("nameParam", userEntry + "%");
        params.put("type", groupType);
        params.put("brandType", brandType);
        return findUsingQuery(query, params);
	}
	
	@SuppressWarnings({"unchecked"})
    public List<Item> findItemsAtAllLevelForGroup(final Long id) {
        return (List<Item>) getHibernateTemplate().execute(new HibernateCallback() {
            public Object doInHibernate(Session session) {
                return session.createQuery("select igChild.includedItems from " +
                        "ItemGroup igParent, ItemGroup igChild where " +
                        "igParent.id = :id and " +
                        "igParent.nodeInfo.treeId=igChild.nodeInfo.treeId and " +
                        "igParent.nodeInfo.lft <= igChild.nodeInfo.lft and " +
                        "igChild.nodeInfo.rgt <= igParent.nodeInfo.rgt")
                        .setLong("id", id)
                        .list();
            }
        });
    }
	
	@SuppressWarnings("unchecked")
	public List<ItemGroup> findGroupCodeBasedOnGroupType(){
		return getHibernateTemplate().executeFind(new HibernateCallback() {
			public Object doInHibernate(Session session)
					throws HibernateException, SQLException {
				Query query = session.createQuery("select distinct groupCode from ItemGroup where itemGroupType ='PRODUCT FAMILY'"
						+ " order by groupCode").setCacheable(true);
				return query.list();
			}
		});		
	}

	public List<ItemGroup> findItemGroupByCodeAndTypeForParts(
			final String code, final String groupType) {
		return (List<ItemGroup>) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						String query = "select ig from ItemGroup ig where upper(ig.groupCode)=:code and ig.itemGroupType =:type";
						session.disableFilter("bu_name");
						List<Object> itemGroupsList = session
								.createQuery(query).setParameter("code",
										code.toUpperCase()).setParameter(
										"type", groupType).list();
						session.enableFilter("bu_name");
						return itemGroupsList;
					}
				});
	}
    private boolean isBuConfigAMER() {
    	return getCurrentBusinessUnit().getName().equals(AdminConstants.NMHGAMER);		
	}
    
	public BusinessUnit getCurrentBusinessUnit() {
		return this.securityHelper.getDefaultBusinessUnit();
	}
}

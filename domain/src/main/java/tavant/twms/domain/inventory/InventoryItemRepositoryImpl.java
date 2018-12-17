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

package tavant.twms.domain.inventory;

import java.sql.SQLException;
import java.util.*;

import org.apache.commons.lang.math.NumberUtils;
import org.apache.log4j.Logger;
import org.hibernate.*;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.util.StringUtils;

import tavant.twms.domain.catalog.Item;
import tavant.twms.domain.claim.MultiInventorySearch;
import tavant.twms.domain.configuration.ConfigName;
import tavant.twms.domain.configuration.ConfigParamService;
import tavant.twms.domain.orgmodel.Organization;
import tavant.twms.domain.orgmodel.ServiceProvider;
import tavant.twms.domain.orgmodel.User;
import tavant.twms.infra.CriteriaHelper;
import tavant.twms.infra.GenericRepositoryImpl;
import tavant.twms.infra.ListCriteria;
import tavant.twms.infra.PageResult;
import tavant.twms.infra.PageSpecification;
import tavant.twms.infra.QueryParameters;
import tavant.twms.security.authz.infra.SecurityHelper;

import com.domainlanguage.time.CalendarDate;
import com.domainlanguage.timeutil.Clock;

/**
 * @author kamal.govindraj
 * 
 */
public class InventoryItemRepositoryImpl extends
		GenericRepositoryImpl<InventoryItem, Long> implements
		InventoryItemRepository {

	private CriteriaHelper criteriaHelper;

	private static final Logger LOGGER = Logger
			.getLogger(InventoryItemRepositoryImpl.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see tavant.twms.domain.inventory.InventoryItemRepository#findSerializedItem
	 *      (java.lang.String)
	 */
	public InventoryItem findSerializedItem(final String id)
			throws ItemNotFoundException {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("id", Long.valueOf(id));
		String query = "from InventoryItem item where item.id = :id";
		InventoryItem inventoryItem = findUniqueUsingQuery(query, params);
		if (inventoryItem == null) {
			throw new ItemNotFoundException("Item with Id "
					+ id + " doesn't exist");
		}
		return inventoryItem;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public InventoryItem findInvItemByIdWithoutInactiveFilter(final String id) throws ItemNotFoundException {
		final String query = "from InventoryItem item where item.id = " + id;
		return (InventoryItem) getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				session.disableFilter("excludeInactive");
				InventoryItem inventoryItem = (InventoryItem) session.createQuery(query).uniqueResult();
				session.enableFilter("excludeInactive");
				return inventoryItem;
			}
		});
	}
	
	public InventoryItem findSerializedItem(final String serialNumber , final String model )
			throws ItemNotFoundException {
		InventoryItem inventoryItem = executeQueryTofindSerializedItem(serialNumber,model);
		return inventoryItem;
	}
	
	public InventoryItem findSerializedItemWithOutActiveFilter(
			final String serialNumber, final String sequencNumber,final InventoryType inventoryType)
			throws ItemNotFoundException {
		this.getSession().disableFilter("excludeInactive");
		logger.error("searching component with component serail number ..."+serialNumber+" and psequenc number.."+sequencNumber);
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("serialNumber", serialNumber.toUpperCase());
		params.put("serializedPart", true);
		params.put("sequenceNumber", sequencNumber);
		params.put("type", inventoryType);
		String query = "from InventoryItem item where upper(item.serialNumber) = :serialNumber and serializedPart= :serializedPart and "
				+ "item.sequenceNumber=:sequenceNumber and type=:type";
		InventoryItem inventoryItem = findUniqueUsingQuery(query, params);
		this.getSession().enableFilter("excludeInactive");
		if (inventoryItem == null) {
			throw new ItemNotFoundException("Item with serialNumber "
					+ serialNumber + " doesn't exist");
		}
		return inventoryItem;
	}
	
	private InventoryItem executeQueryTofindSerializedItem(final String serialNumber , final String model)throws ItemNotFoundException{
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("serialNumber", serialNumber.toUpperCase());
        params.put("model", model.toUpperCase());
        String query = "from InventoryItem item where upper(item.serialNumber) = :serialNumber and " +
                                        "item.ofType in (select itemElement from Item itemElement where  itemElement.model in " +
                                        "(select itemGroup from ItemGroup itemGroup where upper(itemGroup.name) like :model))";
        InventoryItem inventoryItem = findUniqueUsingQuery(query, params);
        if (inventoryItem == null) {
                        throw new ItemNotFoundException("Item with serialNumber "
                                                        + serialNumber + " and model name "+ model +" doesn't exist");
        }
        return inventoryItem;
}
	
	public InventoryItem findSerializedItemByConNumAndModel(final String containerNumber, final String model)
			throws ItemNotFoundException {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("containerNumber", containerNumber);
		params.put("model", model);
		String query = "from InventoryItem item where item.vinNumber = :containerNumber and "
				+ "item.ofType.model.name = :model";
		InventoryItem inventoryItem = findUniqueUsingQuery(query, params);
		if (inventoryItem == null) {
			throw new ItemNotFoundException("Item with containerNumber " + containerNumber + " and model name " + model
					+ " doesn't exist");
		}
		return inventoryItem;
	}
	
	
	public InventoryItem findInventoryItemBySerialNumber(final String serialNumber) throws ItemNotFoundException {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("serialNumberParam", serialNumber.toUpperCase());
		String query = "from InventoryItem item where upper(item.serialNumber) = :serialNumberParam";
		InventoryItem inventoryItem = findUniqueUsingQuery(query, params);
		if (inventoryItem == null) {
			throw new ItemNotFoundException("Item with serial number "
					+ serialNumber + " doesn't exist");
		}
		return inventoryItem;
	}
	
	// populating only Machine , not populating major components. added serialized part condition
	public InventoryItem findMachine(final String serialNumber, final String model) throws ItemNotFoundException {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("serialNumber", serialNumber);
		params.put("model", model);
		String query = "from InventoryItem item where item.serialNumber = :serialNumber and "
				+ "item.ofType in (select itemElement from Item itemElement where  itemElement.model in "
				+ "(select itemGroup from ItemGroup itemGroup where upper(itemGroup.name) like :model)) and item.serializedPart = 0";
		InventoryItem inventoryItem = findUniqueUsingQuery(query, params);
		if (inventoryItem == null) {
			throw new ItemNotFoundException("Item with serialNumber " + serialNumber + " and model name " + model
					+ " doesn't exist");
		}
		return inventoryItem;
	}

	// populating only Machine , not populating major components.
	public InventoryItem findMachine(final String serialNumber) throws ItemNotFoundException {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("serialNumberParam", serialNumber.toUpperCase());
		String query = "from InventoryItem item where upper(item.serialNumber) = :serialNumberParam and item.serializedPart = 0";
		InventoryItem inventoryItem = findUniqueUsingQuery(query, params);
		if (inventoryItem == null) {
			throw new ItemNotFoundException("Item with serial number " + serialNumber + " doesn't exist");
		}
		return inventoryItem;
	}
	
	public InventoryItem findInventoryItemByContainerNumber(final String containerNumber) throws ItemNotFoundException {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("containerNumberParam", containerNumber.toUpperCase());
		String query = "from InventoryItem item where upper(item.vinNumber) = :containerNumberParam";
		InventoryItem inventoryItem = findUniqueUsingQuery(query, params);
		if (inventoryItem == null) {
			throw new ItemNotFoundException("Item with vin number "
					+ containerNumber + " doesn't exist");
		}
		return inventoryItem;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see tavant.twms.domain.inventory.InventoryItemRepository#findSerializedItem
	 *      (java.lang.String)
	 */
	public List<InventoryItem> findItemBySerialNumber(final String serialNumber)
			throws ItemNotFoundException {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("serialNumberParam", serialNumber.toUpperCase());
		String query = "from InventoryItem item where upper(item.serialNumber) = :serialNumberParam and item.serializedPart = 0 ";
		List<InventoryItem> inventoryItems = findUsingQueryDisableCurrentDisable(query, params);
		if ( inventoryItems == null || inventoryItems.isEmpty() ) {
			throw new ItemNotFoundException("Item with serial number "
					+ serialNumber + " doesn't exist");
		}
		return inventoryItems;
	}
	
	public InventoryItem findItemBySerialNumberAndProduct(String serialNumber, String productCode)	throws ItemNotFoundException{
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("serialNumberParam", serialNumber.toUpperCase());
		params.put("productCode", productCode.toUpperCase());
		params.put("serializedPart", false);
		String query = "from InventoryItem item where upper(item.serialNumber) = :serialNumberParam and item.serializedPart=:serializedPart and item.ofType in (select itemElement from Item itemElement where  itemElement.product in " +
				"(select itemGroup from ItemGroup itemGroup where upper(itemGroup.groupCode) = :productCode))";
		List<InventoryItem> inventoryItems = findUsingQuery(query, params);
		if ( inventoryItems == null || inventoryItems.isEmpty() ) {
			throw new ItemNotFoundException("Item with serial number "
					+ serialNumber + " doesn't exist");
		}
		return inventoryItems.get(0);
	}
	

	@SuppressWarnings("unchecked")
	public List<String> findAllSerialNumbersStartingWith(
			final String partialSerialNumber, final int pageNumber,
			final int pageSize) {

		return (List<String>) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						return session
								.createQuery(
										"select item.serialNumber from InventoryItem item "
												+ "where item.serialNumber like :partialSerialNumber "
												+ "order by item.serialNumber")
								.setParameter("partialSerialNumber",
										partialSerialNumber + "%")
								.setFirstResult(pageNumber * pageSize)
								.setMaxResults(pageSize).list();
					}

				});
	}

	@SuppressWarnings("unchecked")
	public List<InventoryItem> findAllSerializedSerialNumbersStartingWith(
					final String partialSerialNumber,final InventoryItemSource source, final int pageNumber,
					final int pageSize) {
        final String query = "from InventoryItem item where upper(item.serialNumber) "
                + "like :partialSerialNumber and item.serializedPart=1 and item.source=:source "
                + "order by item.serialNumber";
        return (List<InventoryItem>) getHibernateTemplate().execute(new HibernateCallback() {

            public Object doInHibernate(Session session) throws HibernateException, SQLException {
                return session.createQuery(query)
                        .setParameter("partialSerialNumber", partialSerialNumber + "%")
                        .setParameter("source", source)
                        .setFirstResult(pageNumber * pageSize)
                        .setMaxResults(pageSize)
                        .list();
            }
        });
    }

			

	
	@SuppressWarnings("unchecked")
	public List<InventoryItem> findInventoryItemsWhoseSerialNumbersStartWith(
			final String partialSerialNumber,final String itemType , final int pageNumber,
			final int pageSize) {
        final String query = "SELECT item FROM InventoryItem item , Item i where upper(item.serialNumber) like :partialSerialNumber and item.serializedPart = 0" +
                " and  i.id=item.ofType and i.itemType = :itemType" +
                " order by item.serialNumber";
        return (List<InventoryItem>) getHibernateTemplate().execute(new HibernateCallback() {

            public Object doInHibernate(Session session) throws HibernateException, SQLException {
                session.disableFilter("currentOwner");
                Query q = session.createQuery(query)
                        .setParameter("partialSerialNumber", partialSerialNumber + "%")
                        .setParameter("itemType", itemType.toUpperCase())
                        .setFirstResult(pageNumber * pageSize)
                        .setMaxResults(pageSize);
                return q.list();
            }
        });
	}
	
	@SuppressWarnings("unchecked")
	public List<InventoryItem> findNationalAccountInventoryItemsWhoseSerialNumbersStartWith(
			final String partialSerialNumber,final String claimType , final int pageNumber,
			final int pageSize, final Long orgId) {
        final String query = "SELECT item FROM InventoryItem item , Item i where upper(item.serialNumber) like :partialSerialNumber and item.serializedPart = 0" +
                " and  i.id=item.ofType and upper(i.itemType) = :claimType and (item.shipTo.id = :orgId or item.currentOwner.id = :orgId)" +
                " order by item.serialNumber";
        return (List<InventoryItem>) getHibernateTemplate().execute(new HibernateCallback() {

            public Object doInHibernate(Session session) throws HibernateException, SQLException {
                Query q = session.createQuery(query)
                        .setParameter("partialSerialNumber", partialSerialNumber + "%")
                        .setParameter("claimType", claimType.toUpperCase())
                        .setParameter("orgId",orgId)
                        .setFirstResult(pageNumber * pageSize)
                        .setMaxResults(pageSize);
                return q.list();
            }
        });
	}

	@SuppressWarnings("unchecked")
	public List<InventoryItem> findPartsWhoseSerialNumbersStartWith(
			final String partialSerialNumber, final int pageNumber,
			final int pageSize) {
		final String queryWithoutSelect = "from InventoryItem item where item.serialNumber like :partialSerialNumber and item.serializedPart=1";
		final String orderByClause = "item.serialNumber";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("partialSerialNumber", partialSerialNumber + "%");			
		PageSpecification pageSpecification = new PageSpecification(pageNumber,
				pageSize);
		PageResult<InventoryItem> page = findPageUsingQuery(queryWithoutSelect,
				orderByClause, pageSpecification, params);
		return page.getResult();
	}

	@SuppressWarnings("unchecked")
	public PageResult<InventoryItem> findAllInventoryItemsForDealer(
			final ServiceProvider dealer, final PageSpecification pageSpecification) {

		final String baseQuery = "from InventoryItem item where item in "
				+ " ( select itemWithTrnx from InventoryItem itemWithTrnx, InventoryTransaction invetoryTransaction "
				+ " where invetoryTransaction.transactedItem = itemWithTrnx "
				+ " and (invetoryTransaction.seller = :dealer or invetoryTransaction.buyer = :dealer ) ) and ()";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("dealer", dealer);
		return findPageUsingQuery(baseQuery, "", pageSpecification, params);
	}

    @SuppressWarnings("unchecked")
    public List<InventoryItem> findAllInventoriesByTypeStartingWith(
			String partialSerialNumber,Long currentOwnerId,String type, int pageNumber, int pageSize) {
        {
        	boolean isInternalUser = new SecurityHelper().getLoggedInUser().isInternalUser();
            String queryWithoutSelect = "";
            if(isInternalUser){
            	queryWithoutSelect =  "from InventoryItem item "
                    + "where upper(item.serialNumber) like :partialSerialNumber "
                    + "and item.type = :type "
                    + "and (item.conditionType not like 'SCRAP' ) "
                    + "and item.currentOwner.id = :currentOwnerId "
                    + "and item.serializedPart = 0 ";
            }
            else{
            queryWithoutSelect =  "from InventoryItem item "
                    + "where upper(item.serialNumber) like :partialSerialNumber "
                    + "and item.type = :type "
                    + "and (item.conditionType not like 'SCRAP' ) "
                    // + "and item.currentOwner.id = :currentOwnerId "
                    + "and item.serializedPart = 0 ";
            }
            final String orderByClause = "item.serialNumber";
            final String selectClause = "select item ";
            final QueryParameters parameters = new QueryParameters();
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("partialSerialNumber", partialSerialNumber + "%");
            if(type.equalsIgnoreCase("STOCK"))
                params.put("type", InventoryType.STOCK);
            else
                params.put("type", InventoryType.RETAIL);
            if(isInternalUser){
            	getHibernateTemplate().getSessionFactory().getCurrentSession().disableFilter("currentOwner");
            	if(currentOwnerId!=null)
            		params.put("currentOwnerId", currentOwnerId);
            }
            //if(currentOwnerId!=null)
            //   params.put("currentOwnerId", currentOwnerId);
            parameters.setNamedParameters(params);
            PageSpecification pageSpecification = new PageSpecification(pageNumber,
                    pageSize);
            PageResult<InventoryItem> page = findPageUsingQuery(queryWithoutSelect,
                    orderByClause, selectClause, pageSpecification, parameters);
            return page.getResult();
        }

    }

	public InventoryItem findInventoryBySerialNumberAndType(final String serialNumber, final InventoryType inventoryType)throws ItemNotFoundException
	{
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("serialNumber", serialNumber);
		params.put("inventoryType", inventoryType);
		String query = "from InventoryItem item where item.serialNumber = :serialNumber and item.serializedPart=0 and item.type=:inventoryType ";
		InventoryItem inventoryItem = findUniqueUsingQuery(query, params);
		if (inventoryItem == null) {
			throw new ItemNotFoundException("Item with serialNumber "
					+ serialNumber + " with type "+ inventoryType.getType() +" doesn't exist");
		}
		return inventoryItem;
		
	}

	@SuppressWarnings("unchecked")
	// The length check of serial numbers is a workaround.
	// The 'BETWEEN' clause would fetch 'CA1571' when the range was
	// "BETWEEN 'CA000'and 'CA222'". Hence to ensure that the range is
	// maintained.
	public List<InventoryItem> findInventoryItemsBetweenSerialNumbers(
			String fromSNo, String toSNo) {
		String query = "from InventoryItem item " + "where item.serialNumber "
				+ "between '" + fromSNo + "' and '" + toSNo + "' " + "and "
				+ "(length(item.serialNumber) = length('" + fromSNo
				+ "') or length(item.serialNumber) = length('" + toSNo + "')) "
				+ "order by item.serialNumber";
		return getHibernateTemplate().find(query);
	}

	@SuppressWarnings("unchecked")
	// This method is same as
	// <code>findInventoryItemsBetweenSerialNumbers()<code> but selects
	// Inventory items
	// according to item condition specified.
	public List<InventoryItem> findInventoryItemsBetweenSerialNumbersByItemCondition(
			String fromSNo, String toSNo, final List<String> itemConditionTypes) {

		final String query = "from InventoryItem item " + "where item.serialNumber "
				+ "between '" + fromSNo + "' and '" + toSNo + "' " + "and "
				+ "(length(item.serialNumber) = length('" + fromSNo
				+ "') or length(item.serialNumber) = length('" + toSNo
				+ "')) and " + "item.conditionType.itemCondition  in  ( :itemConditions ) order by item.serialNumber";

        return (List<InventoryItem>) getHibernateTemplate().execute(new HibernateCallback() {
                    public Object doInHibernate(Session session) throws HibernateException, SQLException {
                        return session.createQuery(query)
                                .setParameterList("itemConditions",itemConditionTypes)
                                .list();
                    }
                });

	}

	@SuppressWarnings("unchecked")
	// removed from scope of dealership filter as in ServiceProviderFilter, check for dealer or admin added.
	public PageResult<InventoryItem> findAllInventoryItemsOfTypeForDealer(
			final InventoryListCriteria inventoryListCriteria,final User loggedInUser) {

        StringBuffer baseQuery = new StringBuffer();
        baseQuery.append(
        		"from InventoryItem item where item.type = :type "
        		+ getFilterCriteriaString(inventoryListCriteria, true));
        if(!loggedInUser.isInternalUser()){
        	baseQuery.append(" and (item.currentOwner in (:dealer) or item.shipTo in (:dealer))");
        }
        baseQuery.append(" and item.serializedPart = 0 ");
		Map<String, Object> params = new HashMap<String, Object>();
		if(!loggedInUser.isInternalUser()){
			List<Organization> dealer = getChildOrganizations(loggedInUser.getBelongsToOrganization().getId());
			dealer.add(loggedInUser.getBelongsToOrganization());
			params.put("dealer", dealer);
		}
		if(!loggedInUser.isInternalUser()){
        	baseQuery.append(" and item.conditionType not in (:itemConditions) ");
        	List<InventoryItemCondition> itemConditions = new ArrayList<InventoryItemCondition>();
        	itemConditions.add(InventoryItemCondition.SCRAP);
        	params.put("itemConditions", itemConditions);
        }
		if(!inventoryListCriteria.getType().equals(InventoryType.RETAIL) && inventoryListCriteria.isVintageStock() && inventoryListCriteria.getVintageStockShipmentDate() != null){
			baseQuery.append(" and item.shipmentDate < :shipmentDate");
			params.put("shipmentDate", inventoryListCriteria.getVintageStockShipmentDate());
		}else if(!inventoryListCriteria.getType().equals(InventoryType.RETAIL) && !inventoryListCriteria.isVintageStock() && inventoryListCriteria.getVintageStockShipmentDate() != null){
			baseQuery.append(" and item.shipmentDate >= :shipmentDate");
			params.put("shipmentDate", inventoryListCriteria.getVintageStockShipmentDate());
		}
		params.put("type", inventoryListCriteria.getType());
        return findPageUsingQueryForDistinctItems(baseQuery.toString(),
                getSortCriteriaString(inventoryListCriteria), "select (item) ", // distinct is not required, no joins which will give duplicte results
                inventoryListCriteria.getPageSpecification(), new QueryParameters(params), " item");
    }

    public InventoryItem findInventoryItem(Long id) {
		return (InventoryItem) getHibernateTemplate().get(InventoryItem.class,
				id);
	}

	private String getFilterCriteriaString(InventoryListCriteria criteria, boolean isPrefixRequired) {
		if (criteria.isFilterCriteriaSpecified()) {
			StringBuffer dynamicQuery = new StringBuffer();
			dynamicQuery
					.append(criteria.getParamterizedFilterCriteriaForDate(isPrefixRequired));// filter
			// based
			// on
			// date
			for (String expression : criteria.getFilterCriteria().keySet()) {
				if (!expression.endsWith("Date") && !expression.endsWith("createdOn")) {
					dynamicQuery.append(" and ");
					dynamicQuery.append("upper( item." + expression + ")");
					dynamicQuery.append(" like ");
					dynamicQuery.append("'"
							+ criteria.getFilterCriteria().get(expression)
							+ "%'");

				}
			}

			return dynamicQuery.toString();
		}
		return " ";
	}

	private String getSortCriteriaString(InventoryListCriteria criteria) {
		if (criteria.getSortCriteria().size() > 0) {
			StringBuffer dynamicQuery = new StringBuffer();
			for (String columnName : criteria.getSortCriteria().keySet()) {
				dynamicQuery.append("item."+columnName);
				dynamicQuery.append(" ");
				dynamicQuery.append(criteria.getSortCriteria().get(columnName));
				dynamicQuery.append(",");
			}
			dynamicQuery.deleteCharAt(dynamicQuery.length() - 1);
			return dynamicQuery.toString();
		}
		return "";
	}

	@SuppressWarnings("unchecked")
	public PageResult<InventoryItem> findAllInventoryItemsForMultiClaim(
			final MultiInventorySearch multiInventorySearch,
			final Long dealerId, final ListCriteria listCriteria) {
		/*
		 * This is the search criteria used by dealerto find the inventories to
		 * file MultiInventory Claim.
		 */
		Map<String, Object> params = new HashMap<String, Object>();
		StringBuffer query = new StringBuffer(" from InventoryItem item ");
		boolean isInternalUser = new SecurityHelper().getLoggedInUser().isInternalUser();
		boolean canSearchOtherDealersRetail  = configParamService
					.getBooleanValue(ConfigName.CAN_DEALER_SEARCH_OTHER_DEALERS_RETAIL.getName());
		if (multiInventorySearch.getInventoryType() != null
				&& InventoryType.STOCK.getType().equals(
						multiInventorySearch.getInventoryType())) {
			query.append(" where item.type='STOCK'");
		} else if (multiInventorySearch.getInventoryType() != null
				&& InventoryType.RETAIL.getType().equals(
						multiInventorySearch.getInventoryType())) {
			query.append(" where item.type='RETAIL' ");
			if(!isInternalUser && !canSearchOtherDealersRetail){
				query.append(" and item.currentOwner in ( select dealer.id from ServiceProvider dealer where dealer.id =:dealerId)");
				params.put("dealerId", dealerId);
			}	
        }

        if (StringUtils.hasText(multiInventorySearch.getSerialNumber())) {
			query.append(" and upper(item.serialNumber) like :serialNumber ");
			params.put("serialNumber", multiInventorySearch.getSerialNumber().toUpperCase()
					+ "%");
		}
		if (StringUtils.hasText(multiInventorySearch.getModelNumber())) {
			query
					.append(" and item.ofType in (select itemElement from Item itemElement where  itemElement.model in (select itemGroup from ItemGroup itemGroup where upper(itemGroup.name) like :groupCode))");
			params
					.put("groupCode", 
							 multiInventorySearch.getModelNumber()
									.toUpperCase() + "%");
		}
		if (multiInventorySearch.getCustomer() != null
				&& InventoryType.RETAIL.getType().equals(
						multiInventorySearch.getInventoryType())
				&& multiInventorySearch.getCustomer().getId() == null) {
			if (StringUtils.hasText(multiInventorySearch.getCustomer()
					.getCompanyName())) {
				query
						.append(" and item.latestBuyer in (select customer from Customer customer where upper(customer.companyName) like :companyName )");
				params.put("companyName", 
						 multiInventorySearch.getCustomer().getCompanyName()
								.toUpperCase() + "%");
			}
			if (StringUtils.hasText(multiInventorySearch.getCustomer()
					.getCorporateName())) {
				query
						.append(" and item.latestBuyer in (select customer from Customer customer where upper(customer.corporateName) like :corporateName)");
				params.put("corporateName", 
						 multiInventorySearch.getCustomer().getCorporateName()
								.toUpperCase() + "%");
			}
		} else if (multiInventorySearch.getCustomer() != null
				&& InventoryType.RETAIL.getType().equals(
						multiInventorySearch.getInventoryType())
				&& multiInventorySearch.getCustomer().getId() != null) {
			query
					.append(" and item.latestBuyer in (select customer from Customer customer where upper(customer.id)= :customerId)");
			params
					.put("customerId", multiInventorySearch.getCustomer()
							.getId());
		} else if (InventoryType.STOCK.getType().equals(
				multiInventorySearch.getInventoryType()) && !isInternalUser) {
			query.append(" and item.currentOwner in ( select dealer.id from ServiceProvider dealer where dealer.id =:dealerId)");
			params.put("dealerId", dealerId);
		}
		if (StringUtils.hasText(multiInventorySearch.getDealerNumber())) {
			query
					.append(" and item.currentOwner in ( select dealership.id from ServiceProvider dealership where upper(dealership.serviceProviderNumber) like :dealerNumber )");
			params.put("dealerNumber", 
					 multiInventorySearch.getDealerNumber().toUpperCase()
					+ "%");
		}
		if (StringUtils.hasText(multiInventorySearch.getYearOfShipment())) {
			query.append(" and to_char(item.shipmentDate,'yyyy')= :year");
			params.put("year", multiInventorySearch.getYearOfShipment());
		}
		query.append(" and item.serializedPart = 0 ");
		return findPageUsingQueryForDistinctItems(query.toString(),
				"item.serialNumber asc", "select distinct(item)", listCriteria
						.getPageSpecification(), new QueryParameters(params),
				"distinct item");
	}

	public void setCriteriaHelper(CriteriaHelper criteriaHelper) {
		this.criteriaHelper = criteriaHelper;
	}

	@SuppressWarnings("unchecked")
	public List<InventoryItemCondition> listInventoryItemConditionTypes() {

		return getHibernateTemplate().executeFind(new HibernateCallback() {
			public Object doInHibernate(Session session)
					throws HibernateException, SQLException {
				Query query = session
						.createQuery(" from InventoryItemCondition ");
				return query.list();
			}
		});
	}

    @SuppressWarnings("unchecked")
	public InventoryItemCondition findInventoryItemConditionByType(final String itemCondition) {
         return (InventoryItemCondition) getHibernateTemplate().execute(
                new HibernateCallback() {
                    public Object doInHibernate(Session session)
                            throws HibernateException, SQLException {
                        return session
                                .createQuery(
                                        "from InventoryItemCondition where itemCondition =:itemCondition ")
                                .setParameter("itemCondition", itemCondition)
                                .uniqueResult();
                    }
                });
	}

    public PageResult<InventoryItem> findInventoryItemsUsingDynamicQuery(
			String queryWithoutSelect, String orderByClause,
			String selectClause, PageSpecification pageSpecification,
			QueryParameters parameters) {
		return super.findPageUsingQuery(queryWithoutSelect, orderByClause,
				selectClause, pageSpecification, parameters);
	}
	
	@SuppressWarnings("unchecked")
	public List<InventoryItem> findInventoryItemsForIds(
			final List<Long> inventoryIds) {
		return (List<InventoryItem>) getHibernateTemplate().execute(
				new HibernateCallback() {
					/**
					 * @see org.springframework.orm.hibernate3.HibernateCallback#doInHibernate(org.hibernate.Session)
					 */
					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
                        //Was throwing exception if more than 1000 ids were passed in the collection
                        int start = 0,end = (inventoryIds.size() > 1000 ? 1000 : inventoryIds.size()), size = inventoryIds.size();
                        Criterion c = Restrictions.in("id", inventoryIds.subList(start, end));
                        while(end < inventoryIds.size()){
                            // we have already picked up 0 - 1000 need to pick up from 1001
                            size -= 1000;
                            start += 1000;
                            end = (size > 1000) ? end + 1000 : end+size;
                            c = Restrictions.or(c, Restrictions.in("id", inventoryIds.subList(start, end)));
                        }
                        Criteria invCriteria = session.createCriteria(InventoryItem.class);
                        invCriteria.add(c);
						return invCriteria.list();
					};
				});
	}
	
	@SuppressWarnings("unchecked")
	public List<InventoryItem> findInventoryItemsForSerialNumbers(
			final String[] serialNumbers) {
		return (List<InventoryItem>) getHibernateTemplate().execute(
				new HibernateCallback() {
					/**
					 * @see org.springframework.orm.hibernate3.HibernateCallback#doInHibernate(org.hibernate.Session)
					 */
					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						Criteria invCriteria = session
								.createCriteria(InventoryItem.class);
						invCriteria.add(Restrictions.in("serialNumber",
								serialNumbers));
						invCriteria
								.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);
						return invCriteria.list();
					};
				});
	}

	@SuppressWarnings("unchecked")
	public PageResult<InventoryItem> findAllInventoryItemsForCampaignMultiClaim(
			final MultiInventorySearch multiInventorySearch,
			final Long dealerId, final ListCriteria listCriteria,
			final String campaignCode) {
		Map<String, Object> params = new HashMap<String, Object>();
		List<String> fromClauses = new ArrayList<String>();
		fromClauses.add("CampaignNotification notification");
		fromClauses.add("Campaign campaign");
		fromClauses.add("InventoryItem item");
		StringBuffer query = new StringBuffer(" from ");
		StringBuffer mandatory = new StringBuffer(
				" where notification.campaign = campaign "
						+ " and notification.notificationStatus='PENDING' "
						+ " and notification.claim is null "
						+ " and upper(campaign.code)=:campaignCode "
						+ " and campaign.fromDate <= :today "
						+ " and campaign.tillDate >= :today "
						+ " and notification.item=item ");
		for (int i = 0; i < fromClauses.size() - 1; i++) {
			query.append(fromClauses.get(i) + ",");
		}
		query.append(fromClauses.get(fromClauses.size() - 1));
		query.append(mandatory.toString());
		params.put("campaignCode", campaignCode);
		params.put("today", Clock.today());
		boolean isInternalUser = new SecurityHelper().getLoggedInUser().isInternalUser();
		boolean canSearchOtherDealersRetail  = configParamService
				.getBooleanValue(ConfigName.CAN_DEALER_SEARCH_OTHER_DEALERS_RETAIL.getName());
		if (multiInventorySearch.getInventoryType() != null
				&& InventoryType.STOCK.getType().equals(
						multiInventorySearch.getInventoryType())) {
			query.append(" and item.type='STOCK'");
			query.append(" and notification.dealership = (select dealer from ServiceProvider dealer where id=:dealerId)");
			params.put("dealerId", dealerId);
		} else if (multiInventorySearch.getInventoryType() != null
				&& InventoryType.RETAIL.getType().equals(
						multiInventorySearch.getInventoryType())) {
			query.append(" and item.type='RETAIL' ");
			if(!isInternalUser && !canSearchOtherDealersRetail) {
				query.append(" and item.currentOwner in ( select dealer.id from ServiceProvider dealer where dealer.id =:dealerId)");
				params.put("dealerId", dealerId);
			}
        }
	
		
		if (StringUtils.hasText(multiInventorySearch.getSerialNumber())) {
			query.append(" and upper(item.serialNumber) like :serialNumber ");
			params.put("serialNumber", multiInventorySearch.getSerialNumber().toUpperCase()
					+ "%");
		}
		if (StringUtils.hasText(multiInventorySearch.getModelNumber())) {
			query
					.append(" and item.ofType in (select itemelemt from Item itemelemt where  itemelemt.model in (select itemGroup from ItemGroup itemGroup where upper(itemGroup.name) like :groupCode))");
			params
					.put("groupCode", multiInventorySearch.getModelNumber()
									.toUpperCase() + "%");
		}
		if (multiInventorySearch.getCustomer() != null
				&& InventoryType.RETAIL.getType().equals(
						multiInventorySearch.getInventoryType())
				&& multiInventorySearch.getCustomer().getId() == null) {
			if (StringUtils.hasText(multiInventorySearch.getCustomer()
					.getCompanyName())) {
				query
						.append(" and item.latestBuyer in (select customer from Customer customer where upper(customer.companyName) like :companyName )");
				params.put("companyName", multiInventorySearch.getCustomer().getCompanyName()
								.toUpperCase() + "%");
			}
			if (StringUtils.hasText(multiInventorySearch.getCustomer()
					.getCorporateName())) {
				query
						.append(" and item.latestBuyer in (select customer from Customer customer where upper(customer.corporateName) like :corporateName)");
				params.put("corporateName", multiInventorySearch.getCustomer().getCorporateName()
								.toUpperCase() + "%");
			} else if (InventoryType.STOCK.getType().equals(
					multiInventorySearch.getInventoryType())) {
				query
						.append(" and item.currentOwner in ( select dealer.id from ServiceProvider dealer where dealer.id =:dealerId)");
				params.put("dealerId", dealerId);
			}
		} else if (multiInventorySearch.getCustomer() != null
				&& InventoryType.RETAIL.getType().equals(
						multiInventorySearch.getInventoryType())
				&& multiInventorySearch.getCustomer().getId() != null) {
			query
					.append(" and item.latestBuyer in (select customer from Customer customer where upper(customer.id)= :customerId)");
			params
					.put("customerId", multiInventorySearch.getCustomer()
							.getId());
		}
		if (StringUtils.hasText(multiInventorySearch.getDealerNumber())) {
			query
					.append(" and item.currentOwner in ( select dealership.id from ServiceProvider dealership where upper(dealership.serviceProviderNumber) like :dealerNumber ) ");
			params.put("dealerNumber", multiInventorySearch.getDealerNumber().toUpperCase()
					+ "%");
		}
		if (StringUtils.hasText(multiInventorySearch.getYearOfShipment())) {
			query.append(" and to_char(item.shipmentDate,'yyyy')= :year");
			params.put("year", multiInventorySearch.getYearOfShipment());
		}
		return findPageUsingQueryForDistinctItems(query.toString(),
				"item.serialNumber asc", "select distinct(item) ", listCriteria
						.getPageSpecification(), new QueryParameters(params),
				"distinct item");

	}

	private String getSortCriteriaString(InventorySearchCriteria criteria) {
		if (criteria.getSortCriteria().size() > 0) {
			StringBuffer dynamicQuery = new StringBuffer();
			for (String columnName : criteria.getSortCriteria().keySet()) {
				dynamicQuery.append(columnName);
				dynamicQuery.append(" ");
				dynamicQuery.append(criteria.getSortCriteria().get(columnName));
				dynamicQuery.append(",");
			}
			dynamicQuery.deleteCharAt(dynamicQuery.length() - 1);
			return dynamicQuery.toString();
		}
		return "item.serialNumber asc";
	}

	private String getFilterCriteriaString(InventorySearchCriteria criteria) {
		if (criteria.getFilterCriteria().size() > 0) {
			StringBuffer dynamicQuery = new StringBuffer();			
			for (Iterator it = criteria.getFilterCriteria().keySet().iterator();it.hasNext();) {
				String expression = (String)it.next();
				String value = criteria.getFilterCriteria().get(expression);	
				if (!expression.endsWith("Date") && !expression.endsWith("createdOn")) { 	
					dynamicQuery.append(" and ");
	    			dynamicQuery.append("upper(" + expression + ")");
	    			dynamicQuery.append(" like ");
	    			dynamicQuery.append("'"
	    					+ value + "%'");	    			
				}
			}
			return dynamicQuery.toString();
		}
		return " ";
	}

	protected boolean isDateProperty(String propertyExpression) {
    	return (propertyExpression.endsWith("Date") 
    				|| propertyExpression.endsWith("createdOn"));
    }
	@SuppressWarnings("unchecked")
	public List<InventoryItem> findAllItemsMatchingCriteria(
            final InventorySearchCriteria invSearchCriteria,
            final Pagination pagination, PageSpecification pageSpecification) {
		/*
		 * This is the search criteria used by dealer to find the inventories to
		 * file MultiInventory Claim.
		 */
		Map<String, Object> params = new HashMap<String, Object>();
		pageSpecification.setPageNumber(pagination.getPageNumber());
		pageSpecification.setPageSize(pagination.getPageSize());
		StringBuffer query = new StringBuffer("");

		 if(StringUtils.hasText(invSearchCriteria.getOptions())|| StringUtils.hasText(invSearchCriteria.getOptionDescription())){
			  query.append(" from InventoryItem item left join item.options options");
			  
			   }
		 else{ 
			 query.append(" from InventoryItem item ");
		  }
		//StringBuffer query = new StringBuffer(" from InventoryItem item ");
		boolean isInternalUser = new SecurityHelper().getLoggedInUser().isInternalUser();
		
		if (invSearchCriteria.getWarrantyType() != null
				&& StringUtils.hasText(invSearchCriteria.getWarrantyType())) {
			query.append(", PolicyDefinition policydef");
		}
		
		if (StringUtils.hasText(invSearchCriteria.getSaleOrderNumber())
			    || invSearchCriteria.isDemoTruck()
			    ||(invSearchCriteria.getContractCode() != null && invSearchCriteria.getContractCode().getId() != null)
				|| (invSearchCriteria.getInternalInstallType()!= null && invSearchCriteria.getInternalInstallType().getId() != null)) {
			query.append(", InventoryTransaction transaction ");
		}
		if (StringUtils.hasText(invSearchCriteria.getSalesPerson())
				|| (invSearchCriteria.getContractCode() != null && invSearchCriteria.getContractCode().getId() != null)
				|| (invSearchCriteria.getInternalInstallType()!= null && invSearchCriteria.getInternalInstallType().getId() != null)
				|| (invSearchCriteria.getWarrantyType()!=null && StringUtils.hasText(invSearchCriteria.getWarrantyType()))
				|| (invSearchCriteria != null && invSearchCriteria.getCustomerType() != null && !invSearchCriteria.getCustomerType().equals("-1"))
				|| invSearchCriteria.getSubmitFromDate() != null
				|| invSearchCriteria.getSubmitToDate() != null 
				|| (invSearchCriteria.isForFlagDR())
				|| (invSearchCriteria.isPendingApprovalDr())
				|| (invSearchCriteria.getGroupCodeForProductFamily() != null && StringUtils.hasText(invSearchCriteria.getGroupCodeForProductFamily()))
				|| (StringUtils.hasText(invSearchCriteria.getMarketingGroupCode()))) {
			query.append(", Warranty w");
		}
		if (invSearchCriteria.getPolicies() != null
				&& invSearchCriteria.getPolicies().length > 0) {
			query.append(", RegisteredPolicy policy join policy.policyAudits policyAudit");
		} else if ((StringUtils.hasText(invSearchCriteria.getWarrantyType()))
				&& (invSearchCriteria.getPolicies() == null || invSearchCriteria
						.getPolicies().length == 0)) {
			query.append(", RegisteredPolicy policy");

		}
		if (invSearchCriteria.getInventoryType() != null
				&& InventoryType.STOCK.getType().equals(
						invSearchCriteria.getInventoryType().getType())) {
			query.append(" where item.type='STOCK'");
		} else if (invSearchCriteria.getInventoryType() != null
				&& InventoryType.RETAIL.getType().equals(
						invSearchCriteria.getInventoryType().getType())) {
			query.append(" where item.type='RETAIL' ");
		} else if (invSearchCriteria.getInventoryType() == null) {
			query.append(" where item.type in ('RETAIL','STOCK') ");
		}
		query.append(invSearchCriteria.getParamterizedFilterCriteriaForDate(false));
		query.append(getFilterCriteriaString(invSearchCriteria));		
		if (StringUtils.hasText(invSearchCriteria.getSaleOrderNumber())
				|| invSearchCriteria.isDemoTruck()) {
			query.append(" AND item = transaction.transactedItem ");
		}
		if (StringUtils.hasText(invSearchCriteria.getSalesPerson())
				|| (invSearchCriteria.getContractCode() != null && invSearchCriteria.getContractCode().getId() != null)
				|| (invSearchCriteria.getInternalInstallType() != null && invSearchCriteria.getInternalInstallType().getId() != null)
				|| (invSearchCriteria != null && invSearchCriteria.getCustomerType() != null && !invSearchCriteria.getCustomerType().equals("-1"))
				|| (invSearchCriteria.isForFlagDR())
				|| (invSearchCriteria.getGroupCodeForProductFamily() != null && StringUtils.hasText(invSearchCriteria.getGroupCodeForProductFamily()))
				|| (StringUtils.hasText(invSearchCriteria.getMarketingGroupCode()))
				|| invSearchCriteria.getSubmitFromDate() != null
				|| invSearchCriteria.getSubmitToDate() != null
				|| (invSearchCriteria.isPendingApprovalDr())) {
			query.append(" AND item = w.forItem ");

		}
		
		if (invSearchCriteria.getPolicies() != null
				&& invSearchCriteria.getPolicies().length > 0) {
			query.append(" AND item.latestWarranty = policy.warranty "
					+ " AND policy.policyDefinition.id in ("
					+ invSearchCriteria
							.getPoliciesDelimitedByComma(invSearchCriteria
									.getPolicies())
					+ ")"
					+ " AND (policyAudit.warrantyPeriod.fromDate <= :policyToDate "
					+ " and policyAudit.warrantyPeriod.tillDate >= :policyFromDate)");
			params.put("policyFromDate", invSearchCriteria.getPolicyFromDate());
			params.put("policyToDate", invSearchCriteria.getPolicyToDate());
			query.append(" and policy.status != 'Terminated' ");
		}
		if (StringUtils.hasText(invSearchCriteria.getWarrantyType())) {
			query.append(" AND item.id=w.forItem and w.id=policy.warranty " +
					"and policy.policyDefinition=policydef.id and policydef.warrantyType.type=:warrantyType");
			params.put("warrantyType", invSearchCriteria.getWarrantyType());
		}

		if (invSearchCriteria.getConditionTypeIs() != null) {
			query.append(" and item.conditionType=:conditionType ");
			params.put("conditionType", invSearchCriteria.getConditionTypeIs());
		}
		if (invSearchCriteria.getConditionTypeNot() != null) {
			query.append(" and item.conditionType<>:conditionType ");
			params
					.put("conditionType", invSearchCriteria
							.getConditionTypeNot());
		}
		if (StringUtils.hasText(invSearchCriteria.getSerialNumber())) {
			query.append(" and upper(item.serialNumber) like :serialNumber ");
			params.put("serialNumber", invSearchCriteria.getSerialNumber().toUpperCase() + "%");
		}
		
		if(invSearchCriteria.isForFlagDR()){
			query.append("and w.manualFlagDr = true");
			
		}
		
		if (StringUtils.hasText(invSearchCriteria.getMarketingGroupCode())) {
			query.append(" and upper(item.marketingGroupCode) like :marketingGroupCode");
			params.put("marketingGroupCode", invSearchCriteria
					.getMarketingGroupCode().toUpperCase() + "%");
		}
		
		if (invSearchCriteria.isWarrantyCheck()) {
			query.append(" and item.pendingWarranty = false ");
		}
		if (StringUtils.hasText(invSearchCriteria.getFactoryOrderNumber())) {
			query
					.append(" and upper(item.factoryOrderNumber) like :factoryOrderNumber ");
			params.put("factoryOrderNumber", invSearchCriteria.getFactoryOrderNumber().toUpperCase()
					+ "%");
		}
		if (StringUtils.hasText(invSearchCriteria.getVinNumber())) {
			query
			.append(" and upper(item.vinNumber) like :vinNumber ");
			params.put("vinNumber", invSearchCriteria.getVinNumber().toUpperCase()
					+ "%");
		}
      
		// I seriously dnt understand wats the problem in giving the same in
		// params MAP Ramalakshmi P.
		if (invSearchCriteria.getSelectedBusinessUnits() != null
				&& invSearchCriteria.getSelectedBusinessUnits().size() > 0) {
			String buNameToAppend = invSearchCriteria.getSelectedBusinessUnitInfoDelimitedByComma();
			if(!buNameToAppend.equals(""))
			{
				query.append(" and item.businessUnitInfo in ("
						+ buNameToAppend
						+ " )");
			}
		}

		if (StringUtils.hasText(invSearchCriteria.getItemModel())) {
			query
					.append(" and item.ofType in (select itemElement from Item itemElement where  itemElement.model in (select itemGroup from ItemGroup itemGroup where upper(itemGroup.groupCode) like :groupCode))");
			params.put("groupCode",  invSearchCriteria.getItemModel().toUpperCase() + "%");
		}
		if (invSearchCriteria.getItemNumber() != null
				&& StringUtils.hasText(invSearchCriteria.getItemNumber())) {
			query
					.append(" and item.ofType in (select itemElement from Item itemElement where  upper(itemElement.number) like :itemNumber)");
			params.put("itemNumber",  invSearchCriteria.getItemNumber().toUpperCase() + "%");
		}

		if (invSearchCriteria.getCustomer() != null) {
			if (StringUtils.hasText(invSearchCriteria.getCustomer()
					.getCompanyName())) {
				query
						.append(" and item.latestBuyer in (select customer from Customer customer where upper(customer.companyName) like :companyName )");
				params.put("companyName",  invSearchCriteria.getCustomer().getCompanyName()
								.toUpperCase() + "%");
			}
			if (StringUtils.hasText(invSearchCriteria.getCustomer()
					.getCorporateName())) {
				query
						.append(" and item.latestBuyer in (select customer from Customer customer where upper(customer.corporateName) like :corporateName)");
				params.put("corporateName",  invSearchCriteria.getCustomer().getCorporateName()
								.toUpperCase() + "%");
			}
		}
		if (StringUtils.hasText(invSearchCriteria.getDealerName())) {
			query.append(" and (item.currentOwner in ( select dealership.id from ServiceProvider dealership where upper(dealership.name) like :dealerName ) " +
					"or item.shipTo in ( select dealership.id from ServiceProvider dealership where upper(dealership.name) like :dealerName ))");
			params.put("dealerName",  invSearchCriteria.getDealerName().toUpperCase() + "%");
		}
		if (StringUtils.hasText(invSearchCriteria.getDealerNumber())) {
//          Performance fix, usually we have numbers, by making it as upper the index created on db
//          was not getting used, Now if only numbers are present then we won't do upper so that
//          index is used.
            String param = null, paramVal = null;
            if(NumberUtils.isDigits(invSearchCriteria.getDealerNumber())){
                param = "dealership.serviceProviderNumber";
                paramVal = invSearchCriteria.getDealerNumber() + "%";
            }else{
                param = "upper(dealership.serviceProviderNumber)";
                paramVal = invSearchCriteria.getDealerNumber().toUpperCase() + "%";
            }
            query.append(" and (item.currentOwner in ( select dealership.id from ServiceProvider dealership where ").append(param).append(" like :dealerNumber ) " +
            		"or item.shipTo in ( select dealership.id from ServiceProvider dealership where ").append(param).append(" like :dealerNumber ))");
            params.put("dealerNumber",  paramVal);
		}

		if (invSearchCriteria.getSaleOrderNumber() != null
				&& StringUtils.hasText(invSearchCriteria.getSaleOrderNumber())) {
			query
					.append(" and transaction.transactionOrder = (select max(transactionOrder) from InventoryTransaction where transactedItem = item and salesOrderNumber like :salesOrderNumber) "
							+ " and transaction.salesOrderNumber like :salesOrderNumber");
			params.put("salesOrderNumber",  invSearchCriteria.getSaleOrderNumber().toUpperCase()
					+ "%");
		}
		
		if(invSearchCriteria.getInventoryType() != null && InventoryType.RETAIL.getType().equals(invSearchCriteria.getInventoryType().getType()) && (invSearchCriteria.getSubmitFromDate() != null || invSearchCriteria.getSubmitToDate() != null)) {
			query.append(" and w.status = 'ACCEPTED' ");
		}else if(invSearchCriteria.getInventoryType() != null && InventoryType.STOCK.getType().equals(invSearchCriteria.getInventoryType().getType())){
			if(invSearchCriteria.isPendingApprovalDr()){
				query.append(" and w.status = 'SUBMITTED' ");
			}
			if(invSearchCriteria.getSubmitFromDate() != null || invSearchCriteria.getSubmitToDate() != null){
				query.append(" and w.status != 'DELETED' and w.status != 'DRAFT' ");
			}
		}
		
		if (StringUtils.hasText(invSearchCriteria.getSalesPerson())) {
			query
					.append(" and w.marketingInformation.dealerRepresentative  like :salesPerson");
			params.put("salesPerson", invSearchCriteria.getSalesPerson()
					+ "%");
		}
		if(invSearchCriteria.getContractCode() != null && invSearchCriteria.getContractCode().getId() != null){
			query.append("and transaction.transactionOrder = " +
					"(select max(transactionOrder) from InventoryTransaction where transactedItem = item and (invTransactionType = 2 or invTransactionType = 3 or invTransactionType = 5 or invTransactionType = 7)) and w.forTransaction = transaction and w.marketingInformation.contractCode = :contractCode ");
			params.put("contractCode", invSearchCriteria.getContractCode());
		}
		if(invSearchCriteria.getInternalInstallType() != null && invSearchCriteria.getInternalInstallType().getId() != null){
			query.append("and transaction.transactionOrder = " +
					"(select max(transactionOrder) from InventoryTransaction where transactedItem = item and (invTransactionType = 2 or invTransactionType = 3 or invTransactionType = 5 or invTransactionType = 7)) and w.forTransaction = transaction and w.marketingInformation.internalInstallType = :internalInstallType ");
			params.put("internalInstallType", invSearchCriteria.getInternalInstallType());
		}	
		
		if (invSearchCriteria.getProductType() != null
				&& StringUtils.hasText(invSearchCriteria.getProductType())) {
			query
					.append(" and upper(item.ofType.product.name)  like :productType");
			params.put("productType", invSearchCriteria.getProductType().toUpperCase() + "%");
		}
		if (invSearchCriteria.getProductGroupCode() != null
				&& StringUtils.hasText(invSearchCriteria.getProductGroupCode())) {
			query
					.append(" and upper(item.ofType.product.groupCode)  like :productCode");
			params.put("productCode", invSearchCriteria.getProductGroupCode().toUpperCase() + "%");
		}
		if (invSearchCriteria.getGroupCodeForProductFamily() != null 
				&& StringUtils.hasText(invSearchCriteria.getGroupCodeForProductFamily())) {
			query
					.append("and upper(item.ofType.product.isPartOf.groupCode)  like :groupCode");
			params.put("groupCode", invSearchCriteria.getGroupCodeForProductFamily().toUpperCase() + "%");
		}
		if (StringUtils.hasText(invSearchCriteria.getModelNumber())) {
			query
					.append(" and upper(item.ofType.model.name)  like :modelNumber");
			params.put("modelNumber",  invSearchCriteria.getModelNumber().toUpperCase() + "%");
		}
		 if(invSearchCriteria.getOptions()!=null && StringUtils.hasText(invSearchCriteria.getOptions())){
	        	query.append(" and upper(options.optionCode)  like :options");
	        	params.put("options", invSearchCriteria.getOptions().toUpperCase() + "%");
	        }
		 
		 if(invSearchCriteria.getOptionDescription()!=null && StringUtils.hasText(invSearchCriteria.getOptionDescription())){
	        	query.append(" and upper(options.optionDescription)  like :optionDescription");
	        	params.put("optionDescription", "%" + invSearchCriteria.getOptionDescription().toUpperCase() + "%");
	        }
		 
		if (invSearchCriteria.getFromDate() != null
				&& invSearchCriteria.getToDate() != null) {
			query
					.append(" and item.shipmentDate  >=:fromDate and item.shipmentDate <= :toDate");
			params.put("fromDate", invSearchCriteria.getFromDate());
			params.put("toDate", invSearchCriteria.getToDate());
		} else if (invSearchCriteria.getFromDate() != null) {
			query.append(" and upper(item.shipmentDate)  >= :fromDate");
			params.put("fromDate", invSearchCriteria.getFromDate());
		} else if (invSearchCriteria.getToDate() != null) {
			query.append(" and upper(item.shipmentDate)  <= :toDate");
			params.put("toDate", invSearchCriteria.getToDate());
		}
		if (invSearchCriteria.getBuildFromDate() != null
				&& invSearchCriteria.getBuildToDate() != null) {
			query
					.append(" and item.builtOn  >=:buildFromDate and item.builtOn <= :buildToDate");
			params.put("buildFromDate", invSearchCriteria.getBuildFromDate());
			params.put("buildToDate", invSearchCriteria.getBuildToDate());
		} else if (invSearchCriteria.getBuildFromDate() != null) {
			query.append(" and upper(item.builtOn)  >= :buildFromDate");
			params.put("buildFromDate", invSearchCriteria.getBuildFromDate());
		} else if (invSearchCriteria.getBuildToDate() != null) {
			query.append(" and upper(item.builtOn)  <= :buildToDate");
			params.put("buildToDate", invSearchCriteria.getBuildToDate());
		}

		if (invSearchCriteria.getDeliveryFromDate() != null
				&& invSearchCriteria.getDeliveryToDate() != null) {
			query
					.append(" and item.deliveryDate  >=:deliveryFromDate and item.deliveryDate <= :deliveryToDate");
			params.put("deliveryFromDate", invSearchCriteria
					.getDeliveryFromDate());
			params.put("deliveryToDate", invSearchCriteria.getDeliveryToDate());
		} else if (invSearchCriteria.getDeliveryFromDate() != null) {
			query.append(" and upper(item.deliveryDate)  >= :deliveryFromDate");
			params.put("deliveryFromDate", invSearchCriteria
					.getDeliveryFromDate());
		} else if (invSearchCriteria.getDeliveryToDate() != null) {
			query.append(" and upper(item.deliveryDate)  <= :deliveryToDate");
			params.put("deliveryToDate", invSearchCriteria.getDeliveryToDate());
		}

		if (invSearchCriteria.getSubmitFromDate() != null
				&& invSearchCriteria.getSubmitToDate() != null) {
			query
					.append(" and w.filedDate  >=:submitFromDate and "
							+ " w.filedDate <= :submitToDate and (w.transactionType.trnxTypeKey in('IB','DR','ETR','DR_RENTAL','RMA','DEMO'))");  //SLMSPROD-1560 , submit should not fetch DR_Modify and ETR_mMdify results
			params.put("submitFromDate", invSearchCriteria.getSubmitFromDate());
			params.put("submitToDate", invSearchCriteria.getSubmitToDate());
		} else if (invSearchCriteria.getSubmitFromDate() != null) {
			query
					.append(" and w.filedDate  >=:submitFromDate and (w.transactionType.trnxTypeKey in('IB','DR','ETR','DR_RENTAL','RMA','DEMO'))"); // SLMSPROD-1560
			params.put("submitFromDate", invSearchCriteria.getSubmitFromDate());
		} else if (invSearchCriteria.getSubmitToDate() != null) {
			query
					.append(" and w.filedDate <= :submitToDate and (w.transactionType.trnxTypeKey in('IB','DR','ETR','DR_RENTAL','RMA','DEMO'))");  //SLMSPROD-1560
			params.put("submitToDate", invSearchCriteria.getSubmitToDate());
		}
		if (StringUtils.hasText(invSearchCriteria.getCondition())) {
			query
					.append(" and upper(item.conditionType.itemCondition)  like :condition");
			params.put("condition",  invSearchCriteria.getCondition().toUpperCase() + "%");
		}
		if (invSearchCriteria.getManufacturingSite() != null) {
			query.append(" and item.manufacturingSiteInventory.id in ("
					+  invSearchCriteria.getManufSiteIdDelimitedByComma() + ")");
		}
		if (invSearchCriteria.getDiscountType() != null) {
			query.append(" and item.latestWarranty.discountType in (:discountType)");
			params.put("discountType",  invSearchCriteria.getDiscountType());
		}

        if (invSearchCriteria.getChildDealers() != null && !(invSearchCriteria.getChildDealers().length == 0)) {
            getHibernateTemplate().getSessionFactory().getCurrentSession().disableFilter("currentOwner");
			query.append(" and ( item.currentOwner.id in (:childDealers)  or  item.shipTo.id in (:childDealers)   )");
            params.put("childDealers",  invSearchCriteria.getChildDealers());
		}

        if (invSearchCriteria.getAllowedDealers() != null && !(invSearchCriteria.getAllowedDealers().size() == 0)) {
            getHibernateTemplate().getSessionFactory().getCurrentSession().disableFilter("currentOwner");
            query.append(" and ( item.currentOwner.id in (:allowedDealers)  or  item.shipTo.id in (:allowedDealers)   )");
            params.put("allowedDealers",  invSearchCriteria.getAllowedDealers());
        }
		
		query.append(" and item.serializedPart = 0 ");
		if(!isInternalUser){
			query.append(" and item.conditionType not in (:itemConditions) ");
			List<InventoryItemCondition> itemConditions = new ArrayList<InventoryItemCondition>();
			itemConditions.add(InventoryItemCondition.SCRAP);
			params.put("itemConditions", itemConditions);
		}
		if (invSearchCriteria.isDemoTruck()) {
			query.append(" and transaction.invTransactionType.trnxTypeValue = 'DEMO' ");
		}

		if (invSearchCriteria != null && invSearchCriteria.getCustomerType() != null && !invSearchCriteria.getCustomerType().equals("-1")) {
			query.append(" and w.customerType = :customerType and w.status != 'DELETED' and w=item.latestWarranty");
			params.put("customerType", invSearchCriteria.getCustomerType());
		}
		
		if(invSearchCriteria.isPreOrderBooking()){
			query.append(" and item.preOrderBooking = 1 ");
		}
		if(invSearchCriteria.isForFlagDR()){
			query.append(" and w.manualFlagDr = 1 ");
		}
        
        if(isInternalUser){
            getHibernateTemplate().getSessionFactory().getCurrentSession().disableFilter("currentOwner");
        }
		PageResult<InventoryItem> itemResultSet = findPageUsingQueryForDistinctItems(
				query.toString(), getSortCriteriaString(invSearchCriteria),
				"select distinct (item)", pageSpecification,
				new QueryParameters(params), " distinct item");
		List<InventoryItem> items = itemResultSet.getResult();
		pagination.setNoOfPages(itemResultSet.getNumberOfPagesAvailable());
		return items;
		
		
	}

	public List<InventoryItem> findAllInventoryItemsByLabel(
			final String labelName) {
		List<InventoryItem> items;
		String query = "select item from InventoryItem item join item.labels label where label.name=:label"
				+ " order by item.serialNumber";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("label", labelName);
		return findUsingQuery(query, params);

	}

	public PageResult<?> findAllInventoryLabels(final ListCriteria listCriteria) {
		PageSpecification pageSpecification = listCriteria
				.getPageSpecification();
		final StringBuffer fromAndWhereClause = new StringBuffer(
				" from InventoryItem item join item.labels lb");
		if (listCriteria.isFilterCriteriaSpecified()) {
			fromAndWhereClause.append(" where ");
			String paramterizedFilterCriteria = listCriteria
					.getParamterizedFilterCriteria();
			fromAndWhereClause.append(paramterizedFilterCriteria);
		}
		final String queryWithoutSelect = fromAndWhereClause.toString();
		final QueryParameters parameters = new QueryParameters();
		final Map<String, Object> parameterMap = listCriteria.getParameterMap();
		parameters.setNamedParameters(parameterMap);

		return findPageUsingQueryForDistinctItems(queryWithoutSelect, "",
				"select distinct(lb)", pageSpecification, parameters,
				"distinct lb");

	}

	public InventoryType findInventoryTypeByType(final String type) {
		return (InventoryType) getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session)
					throws HibernateException, SQLException {
				return session.createQuery(" from InventoryType invType where invType.type = :type ")
				.setParameter("type", type).uniqueResult();
				
			}
		});
	}
	
	public InventoryItem findItemBySerialNumberAndModelNumber(final String serialNumber, Item ofType)
			throws ItemNotFoundException {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("serialNumberParam", serialNumber.toUpperCase());
		params.put("ofTypeParam", ofType);
		String query = " from InventoryItem item where upper(item.serialNumber) = :serialNumberParam and item.ofType=:ofTypeParam";
		InventoryItem inventoryItem = findUniqueUsingQuery(query, params);
		if (inventoryItem == null) {
			throw new ItemNotFoundException("Item with serial number "
					+ serialNumber + " and with in given model id "+ofType.getNumber() +" doesn't exist");
		}
		return inventoryItem;
	}

	public InventoryItem findItemBySerialNumberAndModelNumberAndType(final String serialNumber, Item ofType,InventoryType inventoryType)
			throws ItemNotFoundException {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("serialNumberParam", serialNumber.toUpperCase());
		params.put("ofTypeParam", ofType);
		params.put("inventoryType", inventoryType);
		String query = " from InventoryItem item where upper(item.serialNumber) = :serialNumberParam and item.ofType=:ofTypeParam and item.type =:inventoryType";
		InventoryItem inventoryItem = findUniqueUsingQuery(query, params);
		if (inventoryItem == null) {
			throw new ItemNotFoundException("Item with serial number " + serialNumber + " and with in given model id "
					+ ofType.getNumber() + " doesn't exist in "+inventoryType);
		}
		return inventoryItem;
	}
	
    public InventoryItem findItemBySerialNumberAndItemNumber(final String serialNumber,
            final String itemNumber) throws ItemNotFoundException {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("serialNumberParam", serialNumber.toUpperCase());
        params.put("itemNumberParam", itemNumber.toUpperCase());
        StringBuffer querBuff = new StringBuffer(120);
        querBuff.append("select invitem from InventoryItem invitem, Item item")
                .append(" where upper(invitem.serialNumber)=:serialNumberParam")
                .append(" and invitem.ofType = item.id and ")
                .append(" upper(item.number)=:itemNumberParam");
        InventoryItem inventoryItem = findUniqueUsingQuery(querBuff.toString(), params);
        if (inventoryItem == null) {
            throw new ItemNotFoundException("Item with serial number " + serialNumber
                    + " and with item number " + itemNumber + " doesn't exist");
        }
        return inventoryItem;
    }
    
   
	public boolean areInventoriesPresentforThisServiceProvider(final String serviceProvider){
		long rowCount =  (Long)getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
					throws HibernateException, SQLException {
					Query q = session.createQuery("select count(item.id) from InventoryItem item where " +
						" item.currentOwner in ( select sp.id from ServiceProvider sp where sp.serviceProviderNumber =:serviceProvider) ")
						.setString("serviceProvider", serviceProvider);
					return q.uniqueResult();
				};
				});
		return (rowCount > 0)? true : false;
	}


	private ConfigParamService configParamService;

	public ConfigParamService getConfigParamService() {
		return configParamService;
	}

	public void setConfigParamService(ConfigParamService configParamService) {
		this.configParamService = configParamService;
	}
	
	@SuppressWarnings("unchecked")
	public List<InventoryItem> findInventoryItemCompositionForInvItem(
			final String partialSerialNumber, final Long inventoryItemId) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("replacedPartSerialNumber", partialSerialNumber.toUpperCase() + "%");
		params.put("invItemId", inventoryItemId);
		String query = "select composition.part from InventoryItem invItem join invItem.composedOf as composition "
			+ "where invItem.id = :invItemId "
			+ "and upper(composition.part.serialNumber) like :replacedPartSerialNumber";
		List<InventoryItem> inventoryItems = findUsingQuery(query, params);		
		return inventoryItems;
		
	}
	
	public Boolean doesInvItemExistWithSNAndItem(final String serialNumber,final Item item)  {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("serialNumberParam", serialNumber.toUpperCase());
		params.put("itemParam", item);
		String query = "from InventoryItem item where upper(item.serialNumber) = :serialNumberParam and ofType = :itemParam";
		InventoryItem inventoryItem = findUniqueUsingQuery(query, params);
		if (inventoryItem == null) {
			return false;
		}
		return true;
	}
	
	@SuppressWarnings("unchecked")
	public List<InventoryItem> findInvItemCompositionForInvItem(final Long inventoryItemId) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("invItemId", inventoryItemId);
		String query = "select composition.part from InventoryItem invItem join invItem.composedOf as composition "
			+ "where invItem.id = :invItemId ";
		List<InventoryItem> inventoryItemList = findUsingQuery(query, params);	
		return inventoryItemList;
	}
	
	@SuppressWarnings("unchecked")
	public InventoryItem findSerializedPartBySerialNumber (
					final String partialSerialNumber,final InventoryItemSource source)  throws ItemNotFoundException {
				final String query = "select item from InventoryItem item where lower(item.serialNumber) = :partialSerialNumber and item.serializedPart=1 and item.source=:source";
				Map<String, Object> params = new HashMap<String, Object>();
				params.put("partialSerialNumber", partialSerialNumber.toLowerCase());
				params.put("source", source);
				InventoryItem inventoryItem = findUniqueUsingQuery(query, params);
				if(inventoryItem == null) {
					
					throw new ItemNotFoundException("Item with serialNumber "
							+ partialSerialNumber + " doesn't exist");
				}
				return inventoryItem;
			}
		

	@SuppressWarnings("unchecked")
	public List<InventoryItem> findAllRetailMachinesBySerialNumber(
			final String partialSerialNumber, final int pageNumber,
			final int pageSize) {
		return (List<InventoryItem>) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						return session
								.createQuery(
										" select inventoryItem from InventoryItem inventoryItem "
												+ "where upper(inventoryItem.serialNumber) like :partialSerialNumber "
												+ "and inventoryItem.type = 'RETAIL' "
												+ "and (inventoryItem.conditionType not like 'SCRAP') "
												+ "and inventoryItem.serializedPart = 0 "
												+ "order by inventoryItem.serialNumber")
								.setParameter("partialSerialNumber",
										partialSerialNumber + "%")
								.setFirstResult(pageNumber).setMaxResults(
										pageSize).list();
					}
				});
	}
	
	public InventoryItem findInventoryItemForMajorComponent(final Long inventoryId) {
		final String query = "select invItem from InventoryItem invItem join invItem.composedOf as composedOF where composedOF.part.id = :inventoryId";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("inventoryId", inventoryId);
		InventoryItem inventoryItem = findUniqueUsingQuery(query, params);
		return inventoryItem;
	}
	
	public List<InventoryItem> findMajorComponentBySerialNumber(final String serialNumber)
			throws ItemNotFoundException {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("serialNumberParam", serialNumber.toUpperCase());
		String query = "from InventoryItem item where upper(item.serialNumber) = :serialNumberParam and item.serializedPart = 1 ";
		List<InventoryItem> inventoryItems = findUsingQuery(query, params);
		if (inventoryItems == null || inventoryItems.isEmpty()) {
			throw new ItemNotFoundException("Item with serial number "
					+ serialNumber + " doesn't exist");
		}
		return inventoryItems;
	}

	public InventoryItemComposition findInvItemCompForInvItemAndInvItemComposition(
			final Long inventoryId, final Long majorCompInvItemId) {
		return (InventoryItemComposition) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						return session
								.createQuery(
										" select composition from InventoryItem invItem join invItem.composedOf as composition "
												+ "where invItem.id = :invItemId "
												+ "and composition.part.id = :majorCompInvItemId")
								.setParameter("invItemId",inventoryId)
								.setParameter("majorCompInvItemId", majorCompInvItemId)
								.uniqueResult();
					}
				});		
	}
	
	public Item findPdiNameBySerialNumber(final String serialNumber) throws ItemNotFoundException{
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("serialNumber", serialNumber.toUpperCase());
		String query ="from InventoryItem initem where initem.serialNumber = :serialNumber";
		List<InventoryItem> inventoryItems = findUsingQuery(query, params);
		if ( inventoryItems == null || inventoryItems.isEmpty() ) {
			throw new ItemNotFoundException("Item with serial number "
					+ serialNumber + " doesn't exist");
		}
		return inventoryItems.get(0).getOfType();
	}
	/**
	 * To find stock with dealer and admin and display on home page.
	 */
	public Long findCountOfInventoryItems(final InventoryType type,final Organization userOrg,boolean vintageStock, final CalendarDate shipmentDate){
		final StringBuffer countQueryClause = new StringBuffer(" select count(*) from InventoryItem item where item.type = :type   and item.serializedPart = 0 ");
		StringBuffer filterClauseForDealer = new StringBuffer(" and ( item.shipTo in ( :org_user ) or item.currentOwner in ( :org_user ) )");
		if(!type.equals(InventoryType.RETAIL) && vintageStock && shipmentDate != null){
			countQueryClause.append(" and item.shipmentDate < :shipmentDate ");
		}else if(!type.equals(InventoryType.RETAIL) && !vintageStock && shipmentDate != null){
			countQueryClause.append("  and item.shipmentDate >= :shipmentDate  ");
		}
		filterClauseForDealer.append(" and item.conditionType not in (:itemConditions) ");
		final List<Organization> childOrgs = new ArrayList<Organization>();
		final List<InventoryItemCondition> itemConditions = new ArrayList<InventoryItemCondition>();
		if(userOrg != null){
			countQueryClause.append(filterClauseForDealer);
            childOrgs.addAll(getChildOrganizations(userOrg.getId()));
			childOrgs.add(userOrg);
			itemConditions.add(InventoryItemCondition.SCRAP);
		}
        Long numberOfRows = (Long)getHibernateTemplate().execute(new HibernateCallback() {
            public Object doInHibernate(Session session) throws HibernateException, SQLException {
				Query query = session.createQuery(countQueryClause.toString()).setParameter("type",type);
				if(userOrg != null){
					query.setParameterList("org_user",childOrgs);
					query.setParameterList("itemConditions", itemConditions);
				}
				if(!type.equals(InventoryType.RETAIL) && shipmentDate != null){
					query.setParameter("shipmentDate", shipmentDate);
				}
				return query.uniqueResult();
            }
        });
        return numberOfRows;
    }

	public List<InventoryItem> findInventoryItemsWhoseSerialNumbersStartWithAndBrand(
			final String partialSerialNumber, final String itemType, final int pageNumber,
			final int pageSize, final List<String> brands) {
		 final String query = "SELECT item FROM InventoryItem item , Item i where upper(item.serialNumber) like :partialSerialNumber and item.serializedPart = 0" +
         " and  i.id=item.ofType and i.itemType = :itemType" +" and item.brandType in (:brands) " +
         " order by item.serialNumber";
 return (List<InventoryItem>) getHibernateTemplate().execute(new HibernateCallback() {

     public Object doInHibernate(Session session) throws HibernateException, SQLException {
         session.disableFilter("currentOwner");
         Query q = session.createQuery(query)
                 .setParameter("partialSerialNumber", partialSerialNumber + "%")
                 .setParameter("itemType", itemType.toUpperCase())
                 .setParameterList("brands", brands)
                 .setFirstResult(pageNumber * pageSize)
                 .setMaxResults(pageSize);
         return q.list();
     }
 });
	}

	public List<InventoryItem> findAllSerializedSerialNumbersStartingWithAndBrand(
			final String partialSerialNumber,
			final int pageNumber, final int pageSize, final String brand) {
		final String query = "from InventoryItem item where upper(item.serialNumber) "
            + "like :partialSerialNumber and item.serializedPart=1"
            +" and item.brandType=:brand"
            + " order by item.serialNumber";
    return (List<InventoryItem>) getHibernateTemplate().execute(new HibernateCallback() {

        public Object doInHibernate(Session session) throws HibernateException, SQLException {
            return session.createQuery(query)
                    .setParameter("partialSerialNumber", partialSerialNumber + "%") 
                    .setParameter("brand", brand)
                    .setFirstResult(pageNumber * pageSize)
                    .setMaxResults(pageSize)
                    .list();
        }
    });
	}

	public void saveComponentAuditHistory(
			ComponentAuditHistory componentAuditHistory) {
		getHibernateTemplate().save(componentAuditHistory);
		
	}
	
	public List<ComponentAuditHistory> findComponentAuditForInventoryAndSequenceNumber(final InventoryItem inventoryItem, final String sequenceNumber){
		final String query = "from ComponentAuditHistory audit where audit.inventoryItem = :inventoryItem and audit.sequenceNumber = :sequenceNumber order by audit.d.updatedTime desc";
	    return (List<ComponentAuditHistory>) getHibernateTemplate().execute(new HibernateCallback() {

	        public Object doInHibernate(Session session) throws HibernateException, SQLException {
	            return session.createQuery(query)
	                    .setParameter("inventoryItem", inventoryItem)
	                    .setParameter("sequenceNumber", sequenceNumber)
	                    .list();
	        }
	    });
	}

	public List<InventoryItemComposition> getComponentDetailsFromMajorComponentInventoryItem(
			final List<InventoryItem> inventoryItemsList) {
		final String query = "from InventoryItemComposition component where component.part in(:majorCompInventoryItems)";
	    return (List<InventoryItemComposition>) getHibernateTemplate().execute(new HibernateCallback() {

	        public Object doInHibernate(Session session) throws HibernateException, SQLException {
	            return session.createQuery(query)
	                    .setParameterList("majorCompInventoryItems", inventoryItemsList)
	                    .list();
	        }
	    });
	}

    @SuppressWarnings("unchecked")
    public List<InventoryItem> findAllInventoriesByTypeForChildDealersTooStartingWith(
            String partialSerialNumber,List<Long> parentAndChildIDs,String type, int pageNumber, int pageSize) {
        {
            boolean isInternalUser = new SecurityHelper().getLoggedInUser().isInternalUser();
            String queryWithoutSelect = "";
            if(isInternalUser){
                queryWithoutSelect =  "from InventoryItem item "
                        + "where upper(item.serialNumber) like :partialSerialNumber "
                        + "and item.type = :type "
                        + "and (item.conditionType not like 'SCRAP' ) "
                        + "and item.currentOwner.id in (:parentAndChildIDs) "
                        + "and item.serializedPart = 0 ";
            }
            else{
                queryWithoutSelect =  "from InventoryItem item "
                        + "where upper(item.serialNumber) like :partialSerialNumber "
                        + "and item.type = :type "
                        + "and (item.conditionType not like 'SCRAP' ) "
                        // + "and item.currentOwner.id = :currentOwnerId "
                        + "and item.serializedPart = 0 ";
            }
            final String orderByClause = "item.serialNumber";
            final String selectClause = "select item ";
            final QueryParameters parameters = new QueryParameters();
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("partialSerialNumber", partialSerialNumber + "%");
            if(type.equalsIgnoreCase("STOCK"))
                params.put("type", InventoryType.STOCK);
            else
                params.put("type", InventoryType.RETAIL);
            getHibernateTemplate().getSessionFactory().getCurrentSession().disableFilter("currentOwner");
            if(isInternalUser){
                if(parentAndChildIDs!=null && parentAndChildIDs.size() >0)
                    params.put("parentAndChildIDs", parentAndChildIDs);
            }
            //if(currentOwnerId!=null)
            //   params.put("currentOwnerId", currentOwnerId);
            parameters.setNamedParameters(params);
            PageSpecification pageSpecification = new PageSpecification(pageNumber,
                    pageSize);
            PageResult<InventoryItem> page = findPageUsingQuery(queryWithoutSelect,
                    orderByClause, selectClause, pageSpecification, parameters);
            return page.getResult();
        }

    }

    @SuppressWarnings("unchecked")
    public List<InventoryItem> findAllInventoriesByTypeStartingWithDisableCuurentOwnerFilter(
            String partialSerialNumber,List<Long> currentOwnersId,Long currentOwnerId,String type, int pageNumber, int pageSize) {
        {
            boolean isInternalUser = new SecurityHelper().getLoggedInUser().isInternalUser();
            String queryWithoutSelect = "";
            if(isInternalUser){
                queryWithoutSelect =  "from InventoryItem item "
                        + "where upper(item.serialNumber) like :partialSerialNumber "
                        + "and item.type = :type "
                        + "and (item.conditionType not like 'SCRAP' ) "
                        + "and item.currentOwner.id = :currentOwnerId "
                        + "and item.serializedPart = 0 ";
            }
            else{
                queryWithoutSelect =  "from InventoryItem item "
                        + "where upper(item.serialNumber) like :partialSerialNumber "
                        + "and item.type = :type "
                        + "and (item.conditionType not like 'SCRAP' ) "
                        + "and item.currentOwner.id in (:currentOwnersId) "
                        + "and item.serializedPart = 0 ";
            }
            final String orderByClause = "item.serialNumber";
            final String selectClause = "select item ";
            final QueryParameters parameters = new QueryParameters();
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("partialSerialNumber", partialSerialNumber + "%");

            if(type.equalsIgnoreCase("STOCK"))
                params.put("type", InventoryType.STOCK);
            else
                params.put("type", InventoryType.RETAIL);
            getHibernateTemplate().getSessionFactory().getCurrentSession().disableFilter("currentOwner");
            if(isInternalUser){
                if(currentOwnerId!=null)
                    params.put("currentOwnerId", currentOwnerId);
            } else{
                params.put("currentOwnersId", currentOwnersId);
            }
            //if(currentOwnerId!=null)
            //   params.put("currentOwnerId", currentOwnerId);
            parameters.setNamedParameters(params);
            PageSpecification pageSpecification = new PageSpecification(pageNumber,
                    pageSize);
            PageResult<InventoryItem> page = findPageUsingQuery(queryWithoutSelect,
                    orderByClause, selectClause, pageSpecification, parameters);
            return page.getResult();
        }

    }

}
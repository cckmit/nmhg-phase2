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

import java.util.List;

import tavant.twms.domain.catalog.Item;
import tavant.twms.domain.claim.MultiInventorySearch;
import tavant.twms.domain.orgmodel.Organization;
import tavant.twms.domain.orgmodel.ServiceProvider;
import tavant.twms.domain.orgmodel.User;
import tavant.twms.infra.GenericRepository;
import tavant.twms.infra.ListCriteria;
import tavant.twms.infra.PageResult;
import tavant.twms.infra.PageSpecification;
import tavant.twms.infra.QueryParameters;

import com.domainlanguage.time.CalendarDate;

/**
 * @author kamal.govindraj
 *
 */

public interface InventoryItemRepository extends
		GenericRepository<InventoryItem, Long> {

	InventoryItem findSerializedItem(final String id)
			throws ItemNotFoundException;
	
	InventoryItem findInventoryBySerialNumberAndType(final String serialNumber, final InventoryType inventoryType)throws ItemNotFoundException;

	InventoryItem findSerializedItem(final String serialNumber , final String modelName)
			throws ItemNotFoundException;
	
	InventoryItem findSerializedItemWithOutActiveFilter(final String serialNumber ,final String sequencNumber ,final InventoryType invetoryType)throws ItemNotFoundException;
	
	InventoryItem findMachine(final String serialNumber , final String modelName)throws ItemNotFoundException;
	InventoryItem findSerializedItemByConNumAndModel ( String containerNumber , String modelName)
	throws ItemNotFoundException;
	
	List<String> findAllSerialNumbersStartingWith(String partialSerialNumber,
			int pageNumber, int pageSize);

	PageResult<InventoryItem> findAllInventoryItemsForDealer(ServiceProvider dealer, PageSpecification pageSpecification);

	List<InventoryItem> findInventoryItemsWhoseSerialNumbersStartWith(String partialSerialNumber, String itemType,
			int pageNumber, int pageSize);
	
	List<InventoryItem> findNationalAccountInventoryItemsWhoseSerialNumbersStartWith(String partialSerialNumber, String claimType,
			int pageNumber, int pageSize , Long orgId);

	List<InventoryItem> findPartsWhoseSerialNumbersStartWith(String partialSerialNumber,
			int pageNumber, int pageSize);
		
    List<InventoryItem> findAllInventoriesByTypeStartingWith(
			String partialSerialNumber,Long currentOwnerId,String type, int pageNumber, int pageSize);

	PageResult<InventoryItem> findAllInventoryItemsOfTypeForDealer(
			InventoryListCriteria inventoryListCriteria, final User loggedInUser);

	InventoryItem findInventoryItem(Long id);
	
	InventoryItem findInvItemByIdWithoutInactiveFilter(String id) throws ItemNotFoundException;

	public List<InventoryItem> findAllItemsMatchingCriteria(
            InventorySearchCriteria inventorySearchCriteria,
            Pagination pagination, PageSpecification pageSpecification);

	PageResult<InventoryItem> findAllInventoryItemsForMultiClaim(
			MultiInventorySearch multiInventorySearch, Long dealerId,
			ListCriteria listCriteria);

	List<InventoryItemCondition> listInventoryItemConditionTypes();

	List<InventoryItem> findInventoryItemsBetweenSerialNumbers(String fromSNo,
			String toSNo);

	public PageResult<InventoryItem> findInventoryItemsUsingDynamicQuery(
			final String queryWithoutSelect, final String orderByClause,
			final String selectClause, PageSpecification pageSpecification,
			final QueryParameters parameters);

	List<InventoryItem> findInventoryItemsBetweenSerialNumbersByItemCondition(
			String fromSNo, String toSNo, List<String> conditionTypes);

	public List<InventoryItem> findInventoryItemsForSerialNumbers(
			String[] serialNumbers);
	
	public List<InventoryItem> findInventoryItemsForIds(List<Long> inventoryIds);
	

	public PageResult<InventoryItem> findAllInventoryItemsForCampaignMultiClaim(
			final MultiInventorySearch multiInventorySearch,
			final Long dealerId, final ListCriteria listCriteria,
			final String campaignCode);

	public List<InventoryItem> findAllInventoryItemsByLabel(
			final String labelName);

	public PageResult<?> findAllInventoryLabels(final ListCriteria listCriteria);
	
	public InventoryType findInventoryTypeByType(final String type);
	
	List<InventoryItem> findItemBySerialNumber(final String serialNumber)
			throws ItemNotFoundException;
	
	public InventoryItem findItemBySerialNumberAndProduct(String serialNumber, String productCode) throws ItemNotFoundException;
	
	public InventoryItem findItemBySerialNumberAndModelNumber(final String serialNumber, Item ofType)
			throws ItemNotFoundException;
	
	public InventoryItem findItemBySerialNumberAndModelNumberAndType(final String serialNumber, Item ofType,InventoryType inventoryType)
	throws ItemNotFoundException;
	

	InventoryItem findInventoryItemBySerialNumber(String serialNumber) throws ItemNotFoundException;
	
	InventoryItem findMachine(String serialNumber) throws ItemNotFoundException;
	
	public InventoryItem findInventoryItemByContainerNumber(final String containerNumber) throws ItemNotFoundException;

    public InventoryItemCondition findInventoryItemConditionByType(final String itemCondition);
    
    public InventoryItem findItemBySerialNumberAndItemNumber(final String serialNumber, final String itemNumber)
    throws ItemNotFoundException;

    List<InventoryItem> findAllSerializedSerialNumbersStartingWith(
			String partialSerialNumber,InventoryItemSource source, int pageNumber, int pageSize);
    
    public List<InventoryItem> findInventoryItemCompositionForInvItem(
			final String partialSerialNumber, final Long inventoryItemId);
    
    public List<InventoryItem> findInvItemCompositionForInvItem(final Long inventoryItemId);
    
    public InventoryItem findSerializedPartBySerialNumber (
			final String partialSerialNumber,final InventoryItemSource source)  throws ItemNotFoundException;

    public boolean areInventoriesPresentforThisServiceProvider(final String serviceProvider);
    
    public Boolean doesInvItemExistWithSNAndItem(final String serialNumber,final Item item);

	List<InventoryItem> findAllRetailMachinesBySerialNumber(
			String partialSerialNumber, int pageNumber, int pageSize);
	
	public InventoryItem findInventoryItemForMajorComponent(final Long inventoryId);

	public List<InventoryItem> findMajorComponentBySerialNumber(String serialNumber) throws ItemNotFoundException;

	InventoryItemComposition findInvItemCompForInvItemAndInvItemComposition(final Long inventoryId, final Long majorCompInvItemId);
	
	Item findPdiNameBySerialNumber(final String serialNumber) throws ItemNotFoundException;
	
	public Long findCountOfInventoryItems(final InventoryType type,final Organization userOrg, boolean vintageStock, CalendarDate shipmentDate);

	public List<InventoryItem> findInventoryItemsWhoseSerialNumbersStartWithAndBrand(
			final String partialSerialNumber, final String itemType, final int pageNumber,
			final int pageSize,final List<String> brands);

	public List<InventoryItem> findAllSerializedSerialNumbersStartingWithAndBrand(
			final String partialSerialNumber, final int pageNumber, final int pageSize, final String brand);

	void saveComponentAuditHistory(ComponentAuditHistory componentAuditHistory);
	
	public List<ComponentAuditHistory> findComponentAuditForInventoryAndSequenceNumber(InventoryItem inventoryItem, String sequenceNumber);
	
	List<InventoryItemComposition> getComponentDetailsFromMajorComponentInventoryItem(
			List<InventoryItem> inventoryItemsList);

    List<InventoryItem> findAllInventoriesByTypeForChildDealersTooStartingWith(
            String partialSerialNumber,List<Long> parentAndChildIDs,String type, int pageNumber, int pageSize);

    public List<InventoryItem> findAllInventoriesByTypeStartingWithDisableCuurentOwnerFilter(
            String partialSerialNumber,List<Long> currentOwnersId,Long currentOwnerId,String type, int pageNumber, int pageSize) ;

    public List<Organization> getChildOrganizations(final Long orgId);
    public List<Long> getChildOrganizationIds(final Long orgId);
}
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
package tavant.twms.domain.campaign;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import tavant.twms.domain.catalog.ItemGroup;
import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.common.Label;
import tavant.twms.domain.inventory.InventoryItem;
import tavant.twms.domain.orgmodel.Party;
import tavant.twms.infra.GenericRepository;
import tavant.twms.infra.ListCriteria;
import tavant.twms.infra.PageResult;

/**
 * @author Kiran.Kollipara
 *
 */
public interface CampaignRepository extends GenericRepository<Campaign, Long> {

    List<CampaignClass> getAllClasses();
    
    List<FieldModificationInventoryStatus> getFieldModificationInventoryStatus();
    
    Campaign findByCode(String code);

	Set<Campaign> getCampaignsForInventoryItem(InventoryItem inventoryItem);
	
	List<Campaign> getAllCampaignsForInventoryItem(InventoryItem inventoryItem);
	
	void saveNotifications(List<CampaignNotification> notifications);
	
	public List<InventoryItem> findInvItemsStartWithForCampaignClaims(String partialSerialNumber,String dealerNumber);
	
	public List<InventoryItem> findInvItemsStartWithForCampaignClaimsForAdmin(String partialSerialNumber);
	
	public List<Campaign> getCampaignsForInventoryItemById(String serialNumber);
	
	public List<Campaign> getCampaignsForDealer(String partialCampaignCode,String dealerName);
	
	public List<Campaign> getCampaignsByCode(String partialCampaignCode);
	
	public void updateCampaignNotifications(Collection<InventoryItem> items,Claim claim,Campaign campaign, String campaignNotificationStatus);
	
	public void updateDealershipForCampaignNotification(final InventoryItem item, Party oldDealership, Party newDealership);
	
	public List<Campaign> findPendingCampaigns(final InventoryItem invItem , List<String> campaignClasses);
	
	public PageResult<Campaign> findAllCampaigns(final String fromClause,final ListCriteria listCriteria);
	
	public void deactivateCampaignNotificationsForACampaign(final Campaign campaign);

	public void activateCampaignNotificationsForACampaign(final Campaign campaign);

	public void deleteDraftCampaignClaim(final Claim claim);
	
	public List<CampaignNotification> findNotificationsForItem(InventoryItem item);

	public List<CampaignNotification> findPendingNotificationsForItem(InventoryItem item);
	
	public CampaignNotification getNotificationForItemwithCampaign(final InventoryItem inventoryItem,final Campaign campaign);
	
	public List<CampaignNotification> getNotificationForItemsWithCampaign(final Campaign campaign);
	
	public List<CampaignNotification> getNotificationForItemWithCampaigns(final InventoryItem inventoryItem,final List<Campaign> campaigns);
	
	public List<CampaignNotification> getNotificationForAllRelatedCampaigns(final List<Campaign> campaigns,final List<InventoryItem> items);
	
	public List<CampaignNotification> findNotificationsForCampaign(final Campaign campaign, final String status);
	
	public void deactivateAllCampaignNotifications(final List<CampaignNotification> campaignNotifications);
	
	public List<InventoryItem> findCompletedNotificationsForCampaignsAndItems(final List<Campaign> campaigns,final List<InventoryItem> items);
	
	public void activateAllCampaignNotifications(final List<CampaignNotification> campaignNotifications);
	
	public InventoryItem findInvItemForCampaignClaims(final String serialNumber, final String dealerNumber, final Campaign campaign);

	public List<Campaign> findAllActiveCampaignsWithCodeLike(String partialCampaignCode,int pageNumber, int pageSize);
	
	public CampaignNotification findCampaignNotification(String inventorySerialNumber, String dealerNumber, String campaignCode);
	
	public CampaignNotification findPendingCampaignNotification(
			final InventoryItem item, final Campaign campaign);
	
	public List<Campaign> findAllCampaigns();
	public List<Campaign> findAllCampaignsForLabel(Label label);
}

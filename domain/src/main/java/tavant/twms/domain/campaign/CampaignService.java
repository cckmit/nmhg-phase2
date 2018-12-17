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

import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.inventory.InventoryItem;
import tavant.twms.domain.orgmodel.Party;
import tavant.twms.infra.GenericService;

public interface CampaignService extends
		GenericService<Campaign, Long, Exception> {

	public List<InventoryItem> findInvItemsStartWithForCampaignClaims(String partialSerialNumber,String dealerNumber);
	
	public List<InventoryItem> findInvItemsStartWithForCampaignClaimsForAdmin(String partialSerialNumber);
	
	public Set<Campaign> getCampaignsForInventoryItem(InventoryItem inventoryItem);
	
	public List<Campaign> getCampaignsForInventoryItemById(String serialNumber);
	
	public CampaignNotification getNotificationForItemwithCampaign(InventoryItem inventoryItem,Campaign campaign);
	
	public List<Campaign> getCampaignsForDealer(String partialCampaignCode,String dealerName);
	
	public void updateCampaignNotifications(Collection<InventoryItem> items,Claim claim,Campaign campaign,String campaignNotificationStatus);
	
	public void updateDealershipForCampaignNotification(InventoryItem item, Party oldDealership, Party newDealership);

	public void deleteDraftCampaignClaim(Claim claim);

	public List<Campaign> findPendingCampaigns(InventoryItem invItem, List<String> campaignClasses);

	public Campaign findByCode(String campaignCode);
	
	public List<Campaign> getCampaignsByCode(String partialCampaignCode);
	
	public List<CampaignNotification> findNotificationsForItem(InventoryItem item);
	public List<CampaignNotification> findPendingNotificationsForItem(InventoryItem item);
	
	public InventoryItem findInvItemForCampaignClaims(final String serialNumber, final String dealerNumber, final Campaign campaign);
	
	public void deActivateRelatedCampaigns(Boolean deactivate, Claim claim);
	
	public CampaignNotification findCampaignNotification(String inventorySerialNumber, String dealerNumber, String campaignCode);
	
	public CampaignNotification findPendingCampaignNotification(
			final InventoryItem item, final Campaign campaign);
	
	public void saveNotifications(List<CampaignNotification> notifications);
	
	public List<Campaign> findAll();
}

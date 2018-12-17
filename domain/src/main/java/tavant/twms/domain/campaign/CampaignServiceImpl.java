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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import tavant.twms.domain.campaign.relateCampaigns.RelateCampaign;
import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.claim.ClaimedItem;
import tavant.twms.domain.inventory.InventoryItem;
import tavant.twms.domain.orgmodel.Party;
import tavant.twms.infra.GenericRepository;
import tavant.twms.infra.GenericServiceImpl;

public class CampaignServiceImpl extends
		GenericServiceImpl<Campaign, Long, Exception> implements
		CampaignService {

	private CampaignRepository campaignRepository;

	@SuppressWarnings("unchecked")
	@Override
	public GenericRepository getRepository() {
		return campaignRepository;
	}

	public void delete(Campaign campaign) {
		campaignRepository.delete(campaign);
	}
	
	public Campaign findByCode(String campaignCode) {
		return campaignRepository.findByCode(campaignCode);
	}

	public Campaign findById(Long id) {
		return campaignRepository.findById(id);
	}

	public List<Campaign> findByIds(Collection<Long> collectionOfIds) {
		return campaignRepository.findByIds(collectionOfIds);
	}

	public void save(Campaign campaign) {
		campaignRepository.save(campaign);
	}

	public void update(Campaign campaign) {
		campaignRepository.update(campaign);
	}

	public void setCampaignRepository(CampaignRepository campaignRepository) {
		this.campaignRepository = campaignRepository;
	}
	
	public List<InventoryItem> findInvItemsStartWithForCampaignClaims(String partialSerialNumber,String dealerNumber){
		return campaignRepository.findInvItemsStartWithForCampaignClaims(partialSerialNumber, dealerNumber);
	}
	
	public List<InventoryItem> findInvItemsStartWithForCampaignClaimsForAdmin(String partialSerialNumber){
		return campaignRepository.findInvItemsStartWithForCampaignClaimsForAdmin(partialSerialNumber);
	}

	public Set<Campaign> getCampaignsForInventoryItem(InventoryItem inventoryItem){
		return campaignRepository.getCampaignsForInventoryItem(inventoryItem);
	}
	
	public CampaignNotification getNotificationForItemwithCampaign(
			InventoryItem inventoryItem,Campaign campaign){
		return campaignRepository.getNotificationForItemwithCampaign(inventoryItem, campaign);
	}

	
	public List<Campaign> getCampaignsForInventoryItemById(String serialNumber){
		return campaignRepository.getCampaignsForInventoryItemById(serialNumber);
	}
	
	public List<Campaign> getCampaignsForDealer(String partialCampaignCode,String dealerName){
		return campaignRepository.getCampaignsForDealer(partialCampaignCode, dealerName);
	}
	
	public void updateCampaignNotifications(Collection<InventoryItem> items,Claim claim,Campaign campaign,String campaignNotificationStatus){
		campaignRepository.updateCampaignNotifications(items, claim,campaign,campaignNotificationStatus);
		deActivateRelatedCampaigns(true,claim);
	}
	
	public void updateDealershipForCampaignNotification(InventoryItem item, Party oldDealership, Party newDealership){
		campaignRepository.updateDealershipForCampaignNotification(item, oldDealership, newDealership);
	}

	public void deleteDraftCampaignClaim(Claim claim){
		campaignRepository.deleteDraftCampaignClaim(claim);
		deActivateRelatedCampaigns(false,claim);
	}
	
	public void deActivateRelatedCampaigns(Boolean deactivate, Claim claim) {

		List<CampaignNotification> notifications = new ArrayList<CampaignNotification>();
		List<InventoryItem> itemsClaimed = getInventoryItemsFromClaimedItems(claim);
		List<Campaign> listOfRelatedCampaigns = convertToList(getAllRelatedCampaigns(claim
				.getCampaign()));
		if (listOfRelatedCampaigns != null && listOfRelatedCampaigns.size() > 0) {
			notifications = campaignRepository
					.getNotificationForAllRelatedCampaigns(
							listOfRelatedCampaigns, itemsClaimed);
		}
		if (notifications != null && notifications.size() > 0) {
			if(deactivate){
			this.campaignRepository
					.deactivateAllCampaignNotifications(notifications);
			}else{
				this.campaignRepository
				.activateAllCampaignNotifications(notifications);
			}
		}

	}
	
	private List<InventoryItem> getInventoryItemsFromClaimedItems(Claim claim) {
		List<InventoryItem> items = new ArrayList<InventoryItem>();
		for (ClaimedItem claimedItem : claim.getClaimedItems()) {
			items
					.add(claimedItem.getItemReference()
							.getReferredInventoryItem());
		}
		return items;
	}
	
	private Set<Campaign> getAllRelatedCampaigns(Campaign campaign) {
		List<RelateCampaign> relatedCampaigns = campaign.getRelatedCampaign();
		Set<Campaign> allRelatedCampaigns = new HashSet<Campaign>();
		for (RelateCampaign relateCampaign : relatedCampaigns) {
			for (Campaign toBeIncludedCampaign : relateCampaign
					.getIncludedCampaigns())
				if (toBeIncludedCampaign.getId() != campaign.getId()) {
					allRelatedCampaigns.add(toBeIncludedCampaign);
				}
		}
		return allRelatedCampaigns;
	}
	
	private List<Campaign> convertToList(Set<Campaign> set){
		List<Campaign> list = new ArrayList<Campaign>();
		for(Campaign campaign :set){
			list.add(campaign);
		}
		
		return list;
	}

	public List<Campaign> findPendingCampaigns(InventoryItem invItem , List<String> campaignClasses) {
		return campaignRepository.findPendingCampaigns(invItem , campaignClasses);
	}
	
	public List<Campaign> getCampaignsByCode(String partialCampaignCode){
		return campaignRepository.getCampaignsByCode(partialCampaignCode);
	}
	
	public List<CampaignNotification> findNotificationsForItem(InventoryItem item){
		return campaignRepository.findNotificationsForItem(item);
	}
	public List<CampaignNotification> findPendingNotificationsForItem(InventoryItem item){
		return campaignRepository.findPendingNotificationsForItem(item);
	}
	
	public InventoryItem findInvItemForCampaignClaims(final String serialNumber, final String dealerNumber, final Campaign campaign) {
		return campaignRepository.findInvItemForCampaignClaims(serialNumber, dealerNumber, campaign);
	}

	public CampaignNotification findCampaignNotification(String inventorySerialNumber, String dealerNumber,
			String campaignCode) {
		return campaignRepository.findCampaignNotification(inventorySerialNumber, dealerNumber, campaignCode);
	}
	
	public CampaignNotification findPendingCampaignNotification(
			final InventoryItem item, final Campaign campaign) {
		return campaignRepository.findPendingCampaignNotification(item,
				campaign);
	}

	public void saveNotifications(List<CampaignNotification> notifications) {
		campaignRepository.saveNotifications(notifications);
	}
	
	public List<Campaign> findAll(){
		return campaignRepository.findAllCampaigns();
	}
	
}

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

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Required;

import com.domainlanguage.timeutil.Clock;

import tavant.twms.domain.campaign.relateCampaigns.RelateCampaign;
import tavant.twms.domain.common.Label;
import tavant.twms.domain.inventory.InventoryItem;
import tavant.twms.domain.inventory.InventoryItemCondition;
import tavant.twms.domain.inventory.InventoryItemRepository;
import tavant.twms.domain.policy.PolicyDefinition;
import tavant.twms.infra.GenericRepository;
import tavant.twms.infra.GenericServiceImpl;
import tavant.twms.infra.HibernateCast;
import tavant.twms.infra.InstanceOfUtil;

/**
 * @author Kiran.Kollipara
 * 
 */
public class CampaignAdminServiceImpl extends
		GenericServiceImpl<Campaign, Long, Exception> implements
		CampaignAdminService {

	private CampaignRepository campaignRepository;

	private InventoryItemRepository inventoryItemRepository;

	@Override
	public GenericRepository<Campaign, Long> getRepository() {
		return campaignRepository;
	}

	@Required
	public void setCampaignRepository(CampaignRepository campaignRepository) {
		this.campaignRepository = campaignRepository;
	}

	public List<CampaignClass> getAllClasses() {
		return campaignRepository.getAllClasses();
	}
	
	public List<FieldModificationInventoryStatus> getFieldModificationInventoryStatus() {
		return campaignRepository.getFieldModificationInventoryStatus();
	}

	public Campaign findByCode(String code) {
		return campaignRepository.findByCode(code);
	}
	
	 public List<Campaign> findByIds(Collection<Long> collectionOfIds) {
	        return this.campaignRepository.findByIds(collectionOfIds);
	    }

	public void setInventoryItemRepository(
			InventoryItemRepository inventoryItemRepository) {
		this.inventoryItemRepository = inventoryItemRepository;
	}

	public void generateInventoryItemsForCampaign(
			Campaign campaign) {
		CampaignCoverage coverage = campaign.getCampaignCoverage();
		List<InventoryItem> items = new ArrayList<InventoryItem>();
		List<InventoryItem> removedItems = new ArrayList<InventoryItem>();
        List<CampaignSerialRange> rangeList = coverage.getRangeCoverage().getRanges();
        for (CampaignSerialRange range : rangeList) {
        	if("attach".equals(range.getAttachOrDelete()))
        	{	
            items.addAll(applyPatternOnInventoryItem(
                            inventoryItemRepository
                                    .findInventoryItemsBetweenSerialNumbersByItemCondition(
												range.getFromSerialNumber().toUpperCase().trim(),
												range.getToSerialNumber().toUpperCase().trim(),
                                            Arrays.asList(InventoryItemCondition.REFURBISHED.getItemCondition(),
                                                    InventoryItemCondition.NEW.getItemCondition(),
                                                    InventoryItemCondition.CONSIGNMENT.getItemCondition())),
                                                range.getPatternToApply().toUpperCase().trim()));
        	}
        	else if("delete".equals(range.getAttachOrDelete()))
        	{
        		removedItems.addAll(applyPatternOnInventoryItem(
                         inventoryItemRepository
                                 .findInventoryItemsBetweenSerialNumbersByItemCondition(
												range.getFromSerialNumber().toUpperCase().trim(),
												range.getToSerialNumber().toUpperCase().trim(),
                                         Arrays.asList(InventoryItemCondition.REFURBISHED.getItemCondition(),
                                                 InventoryItemCondition.NEW.getItemCondition(),
                                                 InventoryItemCondition.CONSIGNMENT.getItemCondition())),
                                             range.getPatternToApply().toUpperCase().trim()));
        		
        	}
        }
		Set<InventoryItem> itemsSet = new LinkedHashSet<InventoryItem>();
		itemsSet.addAll(items);
		if(!itemsSet.isEmpty())
		{
		if (campaign.getCampaignCoverage()!=null && campaign.getCampaignCoverage().getRangeCoverage()!=null 
				&&campaign.getCampaignCoverage().getRangeCoverage().getItems()!=null &&
				!campaign.getCampaignCoverage().getRangeCoverage().getItems().isEmpty()) {
			campaign.getCampaignCoverage().getRangeCoverage().getItems()
					.addAll(itemsSet);
		} else {
			campaign.getCampaignCoverage().getRangeCoverage().setItems(new ArrayList<InventoryItem>(itemsSet));
		}
		}
		Set<InventoryItem> removedItemsSet = new LinkedHashSet<InventoryItem>();
		removedItemsSet.addAll(removedItems);
		if(!removedItemsSet.isEmpty())
		{
		if(campaign.getCampaignCoverage().getRangeCoverage().getItems()!=null && !campaign.getCampaignCoverage().getRangeCoverage().getItems().isEmpty())
		{
			campaign.getCampaignCoverage().getRangeCoverage().getItems().removeAll(removedItemsSet)	;
		}
		if(campaign.getCampaignCoverage().getSerialNumberCoverage().getItems()!=null && !campaign.getCampaignCoverage().getSerialNumberCoverage().getItems().isEmpty())
		{
		campaign.getCampaignCoverage().getSerialNumberCoverage().getItems().removeAll(removedItemsSet);
		}
		}
	}

	private List<InventoryItem> applyPatternOnInventoryItem(
			List<InventoryItem> inventoryItems, String patternToApply) {
		List<InventoryItem> invItemListToReturn = new ArrayList<InventoryItem>();
		for (InventoryItem inventoryItem : inventoryItems) {
			if (patternToApply != null) {
				Pattern patternForCampaign = Pattern.compile(patternToApply);
				Matcher matcherForCampaign = patternForCampaign
						.matcher(inventoryItem.getSerialNumber());
				if (matcherForCampaign.find()) {
					invItemListToReturn.add(inventoryItem);
				}
			} else {
				invItemListToReturn.add(inventoryItem);
			}
		}
		return invItemListToReturn;
	}
	
	public void deactivateCampaign(Campaign campaign) {
		campaignRepository.update(campaign);
		campaignRepository.deactivateCampaignNotificationsForACampaign(campaign);		
	}

	public void activateCampaign(Campaign campaign) {
		campaignRepository.update(campaign);
		campaignRepository.activateCampaignNotificationsForACampaign(campaign);
	}
	
	public void deactivateNotificationBasedOnRelatedCampaign(Campaign campaign) {

		Set<Campaign> allDistinctRelatedCampaigns = getAllRelatedCampaigns(campaign);
		if (allDistinctRelatedCampaigns != null
				&& allDistinctRelatedCampaigns.size() > 0) {
			List<Campaign> listOfRelatedCampaigns = convertToList(allDistinctRelatedCampaigns);
			List<InventoryItem> relatedItemstoBeDeactivated = new ArrayList<InventoryItem>();
			
			List<InventoryItem> campaignCoverageItems = campaign.getCampaignCoverage().getItems();
			int invItemsCount = campaignCoverageItems.size();
			if (invItemsCount <= 1000) {
				relatedItemstoBeDeactivated = campaignRepository
								.findCompletedNotificationsForCampaignsAndItems(
											listOfRelatedCampaigns, campaignCoverageItems);
			} else {// if no. of serial numbers are more than 1000 then dividing as batch of 1000 serial numbers
				int maxLoopTimes = (int)Math.ceil(invItemsCount/1000.0);
				for (int times = 0; times < maxLoopTimes; times++) {
					int fromIndex = times*1000;
					int toIndex = ((times+1)*1000)-1;
					
					if (invItemsCount < toIndex) {
						toIndex = invItemsCount;
					}
					relatedItemstoBeDeactivated.addAll(campaignRepository
								.findCompletedNotificationsForCampaignsAndItems(
											listOfRelatedCampaigns, campaignCoverageItems.subList(fromIndex, toIndex)));
				}
			}			
			List<CampaignNotification> campaignNotificationsToBeDeactivated = new ArrayList<CampaignNotification>();

			List<Campaign> campaigns = new ArrayList<Campaign>();
			campaigns.add(campaign);

			if (relatedItemstoBeDeactivated != null
					&& relatedItemstoBeDeactivated.size() > 0) {				
				int campaignNotificationsCount = relatedItemstoBeDeactivated.size();
				if (campaignNotificationsCount <= 1000) {					
					campaignNotificationsToBeDeactivated = campaignRepository
					.getNotificationForAllRelatedCampaigns(campaigns,
							relatedItemstoBeDeactivated);					
				} else {// if no. of campaignNotifications are more than 1000 then dividing as batch of 1000 campaignNotifications
					int maxLoopTimes = (int)Math.ceil(campaignNotificationsCount/1000.0);
					for (int times = 0; times < maxLoopTimes; times++) {
						int fromIndex = times*1000;
						int toIndex = ((times+1)*1000)-1;						
						if (campaignNotificationsCount < toIndex) {
							toIndex = campaignNotificationsCount;
						}
						campaignNotificationsToBeDeactivated.addAll(campaignRepository
								.getNotificationForAllRelatedCampaigns(campaigns,
										relatedItemstoBeDeactivated.subList(fromIndex, toIndex)));				
					}
				}
			}

			if (campaignNotificationsToBeDeactivated.size() > 0) {				
				int campaignNotificationsCount = campaignNotificationsToBeDeactivated.size();
				if (invItemsCount <= 1000) {					
					campaignRepository.deactivateAllCampaignNotifications(campaignNotificationsToBeDeactivated);				
				} else {// if no. of campaignNotifications are more than 1000 then dividing as batch of 1000 campaignNotifications
					int maxLoopTimes = (int)Math.ceil(campaignNotificationsCount/1000.0);
					for (int times = 0; times < maxLoopTimes; times++) {
						int fromIndex = times*1000;
						int toIndex = ((times+1)*1000)-1;						
						if (campaignNotificationsCount < toIndex) {
							toIndex = campaignNotificationsCount;
						}
						campaignRepository.deactivateAllCampaignNotifications(campaignNotificationsToBeDeactivated.subList(fromIndex, toIndex));										
					}
				}
			}
		}
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
	
	 public List<Campaign> findAllCampaignsForLabel(Label label){
		 return this.campaignRepository.findAllCampaignsForLabel(label);
	 }
	 
	public void addActionHistoryAndUpdateCampaign(Campaign campaign) {
		// storing action history
		addActionHistory(campaign);
		try {
			this.update(campaign);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void addActionHistory(Campaign campaign)
	{
		//storing action history
		CampaignAudit campaignAudit = new CampaignAudit();
		campaignAudit.setActionTaken(campaign.getStatus());
		campaignAudit.setComments(campaign.getComments());
		campaignAudit.getD().setUpdatedOn(Clock.today());
		campaignAudit.setForCampaign(campaign);
		campaign.getCampaignAudits().add(campaignAudit);    
	}
	

}

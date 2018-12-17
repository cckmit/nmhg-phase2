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

import java.util.List;

import tavant.twms.domain.bu.BusinessUnit;
import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.inventory.InventoryItem;
import tavant.twms.domain.orgmodel.ServiceProvider;
import tavant.twms.infra.GenericRepository;
import tavant.twms.infra.ListCriteria;
import tavant.twms.infra.PageResult;
import tavant.twms.infra.PageSpecification;

public interface CampaignAssignmentRepository extends
		GenericRepository<CampaignNotification, Long> {

	public void saveAllNotifications(List<CampaignNotification> notifications);

	public List<Campaign> findAllCampaignsForNotification();

	public PageResult<CampaignNotification> findAllCampaignNotificationsForDealer(
			final CampaignAssignmentCriteria criteria);
	
	public PageResult<CampaignNotification> findAllCampaignNotificationRequestsByStatus(
			final CampaignAssignmentCriteria criteria, FieldModUpdateStatus campaignNotificationStatus, boolean isUserADealer,String businessUnitName);
		
	public void deleteNotificationsForCampaign(Campaign campaign);

	public CampaignNotification findNotificationForClaim(Claim claim);
	
	public List<CampaignNotification> findAllNotificationsForClaim(Claim claim);

	public CampaignNotification findNotificationForCampaignAndItem(
			Campaign campaign, InventoryItem item);

	public PageResult<CampaignNotification> findAllItemsMatchingCriteria(
			CampaignItemsSearchCriteria campaignItemsSearchCriteria);
	
	public List<ServiceProvider> findAllAssignedDealersForCampaign(Campaign campaign);
	
	public int findCampaignNotificationsCountForDealer(ServiceProvider dealer);
	
	public int findAllCampaignNotificationCountByStatus(ServiceProvider dealer, FieldModUpdateStatus campaignNotificationStatus,boolean isUserADealer,String businessUnitName);
			
	public CampaignNotification findCompletedNotificationOnInventory( final Campaign campaign,
            final InventoryItem item);

	public PageResult<CampaignNotification> findCampaignsForPredefinedSearches(
			CampaignCriteria criteria, ListCriteria listCriteria,ServiceProvider dealerShip);

	public List<String> findAllStatusForCampaign();
	
	public List<CampaignNotification> findCampaignNotificationsForCampaign(
			Campaign campaign);
	
	public List<CampaignNotification> findCampaignStatus(InventoryItem item);

	public List<String> findInventoriesForCampaignWithClaim(Campaign campaign, List<String> listOfSerialNumbers);
	
	public PageResult<CampaignNotification> findAllCampaignNotificationsForCampaign(final Long campaignId, PageSpecification pageSpecification);

	public List<Claim> findAllClaimsForCampaignNotifications(
			CampaignNotification campaignNotifiation);
	
}

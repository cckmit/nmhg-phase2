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

import org.springframework.transaction.annotation.Transactional;

import tavant.twms.domain.bu.BusinessUnit;
import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.common.ListOfValues;
import tavant.twms.domain.inventory.InventoryItem;
import tavant.twms.domain.orgmodel.ServiceProvider;
import tavant.twms.infra.GenericService;
import tavant.twms.infra.ListCriteria;
import tavant.twms.infra.PageResult;
import tavant.twms.infra.PageSpecification;


public interface CampaignAssignmentService extends
		GenericService<CampaignNotification, Long, Exception> {

	@Transactional(readOnly = false)
	public void generateNotificationForCampaignItems() throws Exception;

	@Transactional(readOnly = true)
	public List<CampaignNotification> findAllNotifications();

	@Transactional(readOnly = true)
	public CampaignNotification findById(Long id);

	@Transactional(readOnly = true)
	public int findNotificationsCountForDealer(ServiceProvider dealer);
	
	@Transactional(readOnly = true)
	public int findAllCampaignNotificationCountByStatus(ServiceProvider dealer, FieldModUpdateStatus campaignNotificationStatus,boolean isUserADealer,String businessUnitName);
	
	@Transactional(readOnly = true)
	public PageResult<CampaignNotification> findAllCampaignNotificationsForDealer(
			final CampaignAssignmentCriteria criteria);
	@Transactional(readOnly = true)
	public PageResult<CampaignNotification> findAllCampaignNotificationRequestsByStatus(
			final CampaignAssignmentCriteria criteria, FieldModUpdateStatus campaignNotificationStatus, boolean isUserADealer,String businessUnitName);
	
	@Transactional(readOnly = true)
	public CampaignNotification findNotificationForClaim(Claim claim);

	@Transactional(readOnly = false)
	public void sendDealerNotificationForClaimDenial(Claim claim)
			throws Exception;

	@Transactional(readOnly = true)
	public Campaign findCampaignAssociatedWithClaim(Claim claim);

	@Transactional(readOnly = true)
	public PageResult<CampaignNotification> findAllItemsMatchingCriteria(
			CampaignItemsSearchCriteria campaignItemsSearchCriteria);

	@Transactional(readOnly = true)
	public List<ServiceProvider> findAllAssignedDealersForCampaign(Campaign campaign);

	@Transactional(readOnly = false)
	public void generateCampaignNotificationForCampaignItems(Long campaignId,
			boolean forAllCampaignItems);
	
	@Transactional(readOnly = false)
	public List<CampaignNotification> findCampaignStatus(InventoryItem item);
	
	@Transactional(readOnly = false)
	public void removeItemsFromCampaign(Long campaignId);
	
	@Transactional(readOnly = true)
	public boolean canRemoveInventoryFromCampaign(InventoryItem inventoryItem, Campaign campaign);

	@Transactional(readOnly = true)
	public PageResult<CampaignNotification> findCampaignsForPredefinedSearches(
			final CampaignCriteria criteria,ListCriteria listCriteria,ServiceProvider dealerShip);

	public List<String> findAllStatusForCampaign();
	
	@Transactional(readOnly = true)
	public List<CampaignNotification> findCampaignNotificationsForCampaign(
			Campaign campaign);

	public List<String> findInventoriesForCampaignWithClaim(Campaign campaign, List<String> listOfSerialNumbers);
	
	public PageResult<CampaignNotification> findAllCampaignNotificationsForCampaign(final Long campaignId, PageSpecification pageSpecification);
	
	public List<ListOfValues> getLovsForClass(String className, CampaignNotification campaignNotifiation);
	
	public List<Claim> findAllClaimsForCampaignNotifications(CampaignNotification campaignNotifiation);

}

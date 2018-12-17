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
import java.util.List;

import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import tavant.twms.domain.bu.BusinessUnit;
import tavant.twms.domain.claim.CampaignClaim;
import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.common.AcceptanceReason;
import tavant.twms.domain.common.AcceptanceReasonForCP;
import tavant.twms.domain.common.ListOfValues;
import tavant.twms.domain.common.ListOfValuesType;
import tavant.twms.domain.common.LovRepository;
import tavant.twms.domain.inventory.InventoryItem;
import tavant.twms.domain.inventory.InventoryTransaction;
import tavant.twms.domain.orgmodel.ServiceProvider;
import tavant.twms.infra.HibernateCast;
import tavant.twms.infra.InstanceOfUtil;
import tavant.twms.infra.ListCriteria;
import tavant.twms.infra.PageResult;
import tavant.twms.infra.PageSpecification;
import tavant.twms.security.SelectedBusinessUnitsHolder;

public class CampaignAssignmentServiceImpl extends HibernateDaoSupport
		implements CampaignAssignmentService {

	private static final String PENDING = "PENDING";

	private CampaignAdminService campaignAdminService;

	private CampaignAssignmentRepository campaignAssignmentRepository;

	private CampaignNotificationRepository campaignNotificationRepository;
	
	private CampaignService campaignService;
	
	 protected LovRepository lovRepository;

	public void setCampaignService(CampaignService campaignService) {
		this.campaignService = campaignService;
	}

	public void generateNotificationForCampaignItems() throws Exception {
		List<Campaign> campaigns = fetchCampaignsForNotifications();
		if (campaigns != null && campaigns.size() > 0) {
			for (Campaign campaign : campaigns) {
				/*
				 * TODO: Campaign/Item form a unique key for a notification.
				 * Currently having a DB hit each time to check if the
				 * combination already exists. This would be return true only
				 * for records for which claim has already been filed. The list
				 * is being over-written for now.
				 */
				campaignAssignmentRepository
						.deleteNotificationsForCampaign(campaign);
				List<CampaignNotification> notificationList = generateNotificationList(campaign);
				campaignAssignmentRepository
						.saveAllNotifications(notificationList);
				campaign.setNotificationsGenerated(true);
				campaignAdminService.update(campaign);
			}
		}
	}

	@SuppressWarnings("unchecked")
	private List<Campaign> fetchCampaignsForNotifications() {
		List<Campaign> campaigns = campaignAssignmentRepository
				.findAllCampaignsForNotification();
		return campaigns;
	}

	private List<CampaignNotification> generateNotificationList(
			Campaign campaign) {
		List<CampaignNotification> campaignNotifications = new ArrayList<CampaignNotification>();
		List<InventoryItem> items = campaign.getCampaignCoverage().getItems();
		for (InventoryItem item : items) {
			if (checkIfNotificationExists(campaign, item)) {
				continue;
			}
			CampaignNotification notification = populateCampaignNotification(
					campaign, item);
			if (notification != null) {
				campaignNotifications.add(notification);
			}
		}
		return campaignNotifications;
	}

	private boolean checkIfNotificationExists(Campaign campaign,
			InventoryItem item) {
		if (campaignAssignmentRepository.findNotificationForCampaignAndItem(
				campaign, item) == null) {
			return false;
		}
		return true;
	}

	private CampaignNotification populateCampaignNotification(
			Campaign campaign, InventoryItem item) {
		CampaignNotification notification = new CampaignNotification();
		notification.setCampaign(campaign);
		notification.setItem(item);
		notification.setNotificationStatus(PENDING);
		ServiceProvider dealer = fetchDealerForItem(item);
		if (dealer == null) {
			return null;
		}
		notification.setDealership(dealer);
		return notification;
	}

	private ServiceProvider fetchDealerForItem(InventoryItem item) {
		List<InventoryTransaction> txns = item.getTransactionHistory();
		ServiceProvider dealership = null;
		for (int i = txns.size(); i > 0; i--) {
			InventoryTransaction txn = txns.get(i - 1);
			if (InstanceOfUtil.isInstanceOfClass(ServiceProvider.class, txn.getBuyer())) {
				dealership = new HibernateCast<ServiceProvider>().cast(txn.getBuyer());
			}
		}
		return dealership;
	}

	@SuppressWarnings("unchecked")
	public List<CampaignNotification> findAllNotifications() {
		return campaignAssignmentRepository.findAll();
	}

	@SuppressWarnings("unchecked")
	public int findNotificationsCountForDealer(ServiceProvider dealer) {
		return campaignAssignmentRepository
				.findCampaignNotificationsCountForDealer(dealer);
	}
	
	@SuppressWarnings("unchecked")
	public int findAllCampaignNotificationCountByStatus(ServiceProvider dealer, FieldModUpdateStatus campaignNotificationStatus,boolean isUserADealer,String businessUnitName) {
		return campaignAssignmentRepository
				.findAllCampaignNotificationCountByStatus(dealer,campaignNotificationStatus,isUserADealer,businessUnitName);
	}
			
	public void setCampaignAdminService(
			CampaignAdminService campaignAdminService) {
		this.campaignAdminService = campaignAdminService;
	}

	public CampaignNotification findById(Long id) {
		return campaignAssignmentRepository.findById(id);
	}

	public void setCampaignAssignmentRepository(
			CampaignAssignmentRepository campaignAssignmentRepository) {
		this.campaignAssignmentRepository = campaignAssignmentRepository;
	}

	public void delete(CampaignNotification t) {
		campaignAssignmentRepository.delete(t);

	}

	public List<CampaignNotification> findAll() {
		return campaignAssignmentRepository.findAll();
	}

	public PageResult<CampaignNotification> findAll(
			PageSpecification pageSpecification) {
		return campaignAssignmentRepository.findAll(pageSpecification);
	}

	public List<CampaignNotification> findByIds(Collection<Long> collectionOfIds) {
		return campaignAssignmentRepository.findByIds(collectionOfIds);
	}

	public void save(CampaignNotification t) {
		campaignAssignmentRepository.save(t);
	}

	public void update(CampaignNotification t) {
		campaignAssignmentRepository.update(t);
	}

	public PageResult<CampaignNotification> findAllCampaignNotificationsForDealer(
			final CampaignAssignmentCriteria criteria) {
		return campaignAssignmentRepository
				.findAllCampaignNotificationsForDealer(criteria);
	}
	
	public PageResult<CampaignNotification> findAllCampaignNotificationRequestsByStatus(
			final CampaignAssignmentCriteria criteria, FieldModUpdateStatus campaignNotificationStatus, boolean isUserADealer,String businessUnitName){
		return campaignAssignmentRepository
				.findAllCampaignNotificationRequestsByStatus(criteria,campaignNotificationStatus,isUserADealer,businessUnitName);
	}
			
	public CampaignNotification findNotificationForClaim(Claim claim) {
		return campaignAssignmentRepository.findNotificationForClaim(claim);
	}

	public void sendDealerNotificationForClaimDenial(Claim claim)
			throws Exception {
		if (InstanceOfUtil.isInstanceOfClass(CampaignClaim.class, claim)) {
			List<CampaignNotification> notificationList = new ArrayList<CampaignNotification>();
			notificationList = campaignAssignmentRepository.findAllNotificationsForClaim(claim);
			if(notificationList != null)
			{
			for (CampaignNotification notificationItem : notificationList)
			{
			notificationItem.setClaim(null);
			notificationItem.setNotificationStatus(PENDING);
			campaignAssignmentRepository.update(notificationItem);
			}
			}
			campaignService.deActivateRelatedCampaigns(false, claim);
		}
	}

	public Campaign findCampaignAssociatedWithClaim(Claim claim) {
		Campaign campaign = null;
		if(claim.getId() == null) {
			campaign = claim.getCampaign();
		} else {
			CampaignNotification campaignNotification = findNotificationForClaim(claim);
			if(campaignNotification == null) {
				campaign = claim.getCampaign();
			} else {
				campaign = campaignNotification.getCampaign();
			}
		}
		return campaign;
	}

	public PageResult<CampaignNotification> findAllItemsMatchingCriteria(
			CampaignItemsSearchCriteria campaignItemsSearchCriteria) {
		return campaignAssignmentRepository
				.findAllItemsMatchingCriteria(campaignItemsSearchCriteria);
	}

	public List<ServiceProvider> findAllAssignedDealersForCampaign(Campaign campaign) {
		return campaignAssignmentRepository
				.findAllAssignedDealersForCampaign(campaign);
	}

	public void setCampaignNotificationRepository(
			CampaignNotificationRepository campaignNotificationRepository) {
		this.campaignNotificationRepository = campaignNotificationRepository;
	}

	public void generateCampaignNotificationForCampaignItems(Long campaignId,
			boolean forAllCampaignItems) {
		campaignNotificationRepository
				.generateCampaignNotificationForCampaignItems(campaignId,
						forAllCampaignItems);
	}

	public void removeItemsFromCampaign(Long campaignId){
		campaignNotificationRepository.removeCampaignNotificationsForItems(campaignId);
	}

	public boolean canRemoveInventoryFromCampaign(InventoryItem inventoryItem, Campaign campaign){
		if(campaignAssignmentRepository.findCompletedNotificationOnInventory(campaign, inventoryItem) == null){
			return true;
		}
		return false;
	}

	public PageResult<CampaignNotification> findCampaignsForPredefinedSearches(
			CampaignCriteria criteria, ListCriteria listCriteria,ServiceProvider dealerShip) {

		return campaignAssignmentRepository.findCampaignsForPredefinedSearches(criteria, listCriteria,dealerShip);
	}

	public List<String> findAllStatusForCampaign() {
		return this.campaignAssignmentRepository.findAllStatusForCampaign();
	}

	public void deleteAll(List<CampaignNotification> entitiesToDelete) {
		this.campaignAssignmentRepository.deleteAll(entitiesToDelete);		
	}
	
	public List<CampaignNotification> findCampaignNotificationsForCampaign(
			Campaign campaign) {
		return campaignAssignmentRepository
				.findCampaignNotificationsForCampaign(campaign);
	}
	
	public List<CampaignNotification> findCampaignStatus(
			InventoryItem item) {
		return campaignAssignmentRepository
				.findCampaignStatus(item);
	}

	public List<String> findInventoriesForCampaignWithClaim(
			Campaign campaign, List<String> listOfSerialNumbers) {
		return campaignAssignmentRepository.findInventoriesForCampaignWithClaim(campaign, listOfSerialNumbers);
	}
	
	public PageResult<CampaignNotification> findAllCampaignNotificationsForCampaign(final Long campaignId, PageSpecification pageSpecification){
		return campaignAssignmentRepository.findAllCampaignNotificationsForCampaign(campaignId, pageSpecification);
	}
	@SuppressWarnings("unchecked")
	public List<ListOfValues> getLovsForClass(String className, CampaignNotification campaignNotifiation){
		List<ListOfValues> lovs = new ArrayList<ListOfValues>();

		SelectedBusinessUnitsHolder.setSelectedBusinessUnit(campaignNotifiation.getCampaign()
				.getBusinessUnitInfo().getName());
		lovs = this.lovRepository.findAllActive(className);
		
		return lovs;
			
		}
	public void setLovRepository(LovRepository lovRepository) {
		this.lovRepository = lovRepository;
	}

	public List<Claim> findAllClaimsForCampaignNotifications(
			CampaignNotification campaignNotifiation) {
		return campaignAssignmentRepository
		.findAllClaimsForCampaignNotifications(campaignNotifiation);

	}

}

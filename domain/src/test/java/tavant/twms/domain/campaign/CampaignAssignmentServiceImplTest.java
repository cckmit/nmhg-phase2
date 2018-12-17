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

package tavant.twms.domain.campaign;

import java.util.ArrayList;
import java.util.List;

import tavant.twms.domain.catalog.CatalogException;
import tavant.twms.domain.catalog.CatalogService;
import tavant.twms.domain.inventory.InventoryItem;
import tavant.twms.domain.inventory.InventoryItemCondition;
import tavant.twms.domain.inventory.InventoryItemRepository;
import tavant.twms.domain.inventory.InventoryTransaction;
import tavant.twms.domain.inventory.InventoryType;
import tavant.twms.domain.orgmodel.OrgService;
import tavant.twms.infra.DomainRepositoryTestCase;
import tavant.twms.infra.HibernateCast;

import com.domainlanguage.time.CalendarDate;

public class CampaignAssignmentServiceImplTest extends DomainRepositoryTestCase {

	private CampaignAssignmentService campaignAssignmentService;

	private InventoryItemRepository inventoryItemRepository;

	private CatalogService catalogService;

	private OrgService orgService;

	private CampaignRepository campaignRepository;

	public void testGenerateNotificationForCampaignItems() throws Exception {

		populateCampaignList();
		campaignAssignmentService.generateNotificationForCampaignItems();

		List<CampaignNotification> notifications = campaignAssignmentService
				.findAllNotifications();
		assertEquals(2, notifications.size());
	}

	private Campaign saveCampaign(List<InventoryItem> items) {
		Campaign campaign = getNewCampaignObj(items);
		campaignRepository.save(campaign);
		return campaign;
	}

	private void populateCampaignList() throws CatalogException {
		InventoryItem inv1 = createInventoryItem("S001");
		InventoryTransaction invTx1 = createInventoryTx(inv1);
		inv1.getTransactionHistory().add(invTx1);

		inventoryItemRepository.save(inv1);

		InventoryItem inv2 = createInventoryItem("S002");
		InventoryTransaction invTx2 = createInventoryTx(inv2);
		inv2.getTransactionHistory().add(invTx2);

		inventoryItemRepository.save(inv2);

		flushAndClear();

		List<InventoryItem> items = inventoryItemRepository
				.findInventoryItemsBetweenSerialNumbers("S001", "S002");
		assertEquals(2, items.size());

		Campaign campaign = saveCampaign(items);

		flush();

		assertNotNull(campaign.getId());
		assertNotNull(campaign.getCampaignCoverage().getId());
		assertEquals((new HibernateCast<CampaignRangeCoverage>().cast(campaign.getCampaignCoverage()))
				.getRanges().size(), 1);
	}

	private Campaign getNewCampaignObj(List<InventoryItem> items) {
		Campaign campaign = new Campaign();
		campaign.setCode("ASSERTME");
		campaign.setDescription("My First Campaign Test!!!!");
		campaign.setFromDate(CalendarDate.from("04/20/2009", "MM/dd/yyyy"));
		campaign.setTillDate(CalendarDate.from("05/20/2009", "MM/dd/yyyy"));
		campaign.setCampaignClass("Security");
		campaign.setNotificationsGenerated(false);
		CampaignRangeCoverage coverage = new CampaignRangeCoverage();
		CampaignSerialRange range = new CampaignSerialRange();
		range.setFromSerialNumber("S001");
		range.setToSerialNumber("S002");
		List<CampaignSerialRange> list = new ArrayList<CampaignSerialRange>();
		list.add(range);
		coverage.setRanges(list);
		coverage.setItems(items);
		campaign.setCampaignCoverage(coverage);
		return campaign;
	}

	public void setCampaignAssignmentService(
			CampaignAssignmentService campaignAssignmentService) {
		this.campaignAssignmentService = campaignAssignmentService;
	}

	public void setInventoryItemRepository(
			InventoryItemRepository inventoryItemRepository) {
		this.inventoryItemRepository = inventoryItemRepository;
	}

	private InventoryItem createInventoryItem(String serialNumber)
			throws CatalogException {
		InventoryItem inv = new InventoryItem();

		inv.setSerialNumber(serialNumber);
		inv.setOfType(catalogService.findItemOwnedByManuf("PRTVLV1"));
		inv.setRegistrationDate(CalendarDate.date(2005, 1, 6));
		inv.setDeliveryDate(CalendarDate.date(2005, 1, 6));
		inv.setType(new InventoryType("STOCK"));
		// TODO Fix this once the id field is removed from
		// InventoryItemCondition
		inv.setConditionType(InventoryItemCondition.NEW);
		return inv;
	}

	private InventoryTransaction createInventoryTx(InventoryItem inv) {
		InventoryTransaction invTx = new InventoryTransaction();

		invTx.setTransactionDate(CalendarDate.date(2005, 1, 6));
		invTx.setSeller(orgService.findDealerById(20L));
		invTx.setBuyer(orgService.findDealerById(7L));
		invTx.setSalesOrderNumber("SO-1");
		invTx.setInvoiceNumber("IN-1");
		invTx.setInvoiceDate(CalendarDate.date(2005, 1, 6));
		invTx.setTransactedItem(inv);
		return invTx;
	}

	public void setCatalogService(CatalogService catalogService) {
		this.catalogService = catalogService;
	}

	public void setOrgService(OrgService orgService) {
		this.orgService = orgService;
	}

	public void setCampaignRepository(CampaignRepository campaignRepository) {
		this.campaignRepository = campaignRepository;
	}

}

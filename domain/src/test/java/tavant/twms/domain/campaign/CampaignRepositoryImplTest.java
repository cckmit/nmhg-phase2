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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Required;

import tavant.twms.domain.catalog.CatalogRepository;
import tavant.twms.domain.common.NumberRange;
import tavant.twms.infra.HibernateCast;

import com.domainlanguage.money.Money;
import com.domainlanguage.time.CalendarDate;
import tavant.twms.infra.DomainRepositoryTestCase;

/**
 * @author Kiran.Kollipara
 * 
 */
public class CampaignRepositoryImplTest extends DomainRepositoryTestCase {

	private CampaignRepository campaignRepository;

	private CatalogRepository catalogRepository;

	// private LocationRepository locationRepository;

	// private InventoryItemRepository inventoryItemRepository;

	public void testSaveCampaign() {

		Campaign campaign = getNewCampaignObj();
		campaignRepository.save(campaign);

		flush();

		assertNotNull(campaign.getId());
		assertNotNull(campaign.getCampaignCoverage().getId());
		assertEquals((new HibernateCast<CampaignRangeCoverage>().cast(campaign.getCampaignCoverage()))
				.getRanges().size(), 1);
	}

	private Campaign getNewCampaignObj() {
		Campaign campaign = new Campaign();
		campaign.setCode("ASSERTME");
		campaign.setDescription("My First Campaign Test!!!!");
		campaign.setFromDate(CalendarDate.from("04/20/2009", "MM/dd/yyyy"));
		campaign.setTillDate(CalendarDate.from("05/20/2009", "MM/dd/yyyy"));
		campaign.setCampaignClass("Security");

		CampaignRangeCoverage coverage = new CampaignRangeCoverage();
		CampaignSerialRange range = new CampaignSerialRange();
		range.setFromSerialNumber("12345");
		range.setToSerialNumber("12350");
		List<CampaignSerialRange> list = new ArrayList<CampaignSerialRange>();
		list.add(range);
		coverage.setRanges(list);

		campaign.setCampaignCoverage(coverage);
		return campaign;
	}

	public void testUpdateCampaign() {
		Campaign campaign = campaignRepository.findById(new Long(1));

		campaign.setBuildFromDate(null);
		campaign.setBuildTillDate(null);

		Set<NumberRange> serialNumberRanges = new HashSet<NumberRange>();
		NumberRange numberRange = new NumberRange();
		numberRange.setMin("CA001");
		numberRange.setMax("CA099");
		serialNumberRanges.add(numberRange);

		campaignRepository.update(campaign);

		flushAndClear();

		campaign = campaignRepository.findById(new Long(1));
	}

	public void testUpdateForOEMParts() {
		Campaign campaign = campaignRepository.findById(new Long(1));

		OEMPartToReplace partToReplace = new OEMPartToReplace();
//		partToReplace.setItem(catalogRepository.findItem("PRTVLV1"));
		partToReplace.setNoOfUnits(new Integer(2));

		List<OEMPartToReplace> partsToReplace = campaign.getOemPartsToReplace();
		partsToReplace.add(partToReplace);
		campaign.setOemPartsToReplace(partsToReplace);

		campaignRepository.update(campaign);
		flushAndClear();

		campaign = campaignRepository.findById(new Long(1));
		assertEquals(campaign.getOemPartsToReplace().size(), 1);
		campaign.getOemPartsToReplace().clear();

		campaignRepository.update(campaign);
		flushAndClear();
		campaign = campaignRepository.findById(new Long(1));
		assertEquals(campaign.getOemPartsToReplace().size(), 0);
	}

	public void testUpdateForNonOEMParts() {
		Campaign campaign = campaignRepository.findById(new Long(1));

		NonOEMPartToReplace partToReplace = new NonOEMPartToReplace();
		partToReplace.setNoOfUnits(new Integer(2));
//		partToReplace.setDescription("OIL");
		// partToReplace.setPartNumber("45646");
		partToReplace.setPricePerUnit(Money.dollars(2));

		List<NonOEMPartToReplace> partsToReplace = campaign
				.getNonOEMpartsToReplace();
		partsToReplace.add(partToReplace);
		campaign.setNonOEMpartsToReplace(partsToReplace);

		campaignRepository.update(campaign);
		flushAndClear();

		campaign = campaignRepository.findById(new Long(1));
		assertEquals(campaign.getNonOEMpartsToReplace().size(), 1);
		campaign.getNonOEMpartsToReplace().clear();

		campaignRepository.update(campaign);
		flushAndClear();
		campaign = campaignRepository.findById(new Long(1));
		assertEquals(campaign.getNonOEMpartsToReplace().size(), 0);
	}

	// Is being worked on. Test case will be updated as and when done.

	/*
	 * public void testGetInventoryItemsForProducts() { InventoryItem
	 * inventoryItem = inventoryItemRepository.findById(37L); Set<Campaign>
	 * campaignList = null; campaignList = campaignRepository
	 * .getCampaignsForInventoryItem(inventoryItem);
	 * assertFalse(campaignList.isEmpty()); assertEquals(campaignList.size(),
	 * 2); }
	 */
	@Required
	public void setCatalogRepository(CatalogRepository catalogRepository) {
		this.catalogRepository = catalogRepository;
	}

	@Required
	public void setCampaignRepository(CampaignRepository campaignRepository) {
		this.campaignRepository = campaignRepository;
	}
	//
	// @Required
	// public void setLocationRepository(LocationRepository locationRepository)
	// {
	// this.locationRepository = locationRepository;
	// }
	//
	// @Required
	// public void setInventoryItemRepository(
	// InventoryItemRepository inventoryItemRepository) {
	// this.inventoryItemRepository = inventoryItemRepository;
	// }
}
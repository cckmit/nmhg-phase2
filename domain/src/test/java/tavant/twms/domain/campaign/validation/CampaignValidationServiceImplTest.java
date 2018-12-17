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
package tavant.twms.domain.campaign.validation;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import tavant.twms.domain.campaign.Campaign;
import tavant.twms.domain.campaign.CampaignRangeCoverage;
import tavant.twms.domain.campaign.CampaignSerialRange;
import tavant.twms.domain.campaign.CampaignServiceException;
import tavant.twms.domain.campaign.NonOEMPartToReplace;
import tavant.twms.domain.campaign.OEMPartToReplace;
import tavant.twms.domain.catalog.CatalogException;
import tavant.twms.domain.catalog.CatalogRepository;
import tavant.twms.infra.DomainRepositoryTestCase;

import com.domainlanguage.money.Money;
import com.domainlanguage.time.CalendarDate;
import com.domainlanguage.time.TimePoint;

/**
 * @author Kiran.Kollipara
 * 
 */
public class CampaignValidationServiceImplTest extends DomainRepositoryTestCase {

	private CampaignValidationService campaignValidationService;

	private CatalogRepository catalogRepository;

	public void testCampaignValidationForSerialNumberRange()
			throws CatalogException {
		Calendar _today = Calendar.getInstance();
		CalendarDate _calDate = CalendarDate.from(TimePoint.from(_today),
				_today.getTimeZone());

		Campaign campaign = new Campaign();
		campaign.setCode("ASDF1234");
		campaign.setCampaignClass("Security");
		campaign.setFromDate(_calDate);
		campaign.setTillDate(_calDate);
		campaign.setDescription("Test case with valid data");

		CampaignRangeCoverage serialNumberRange = new CampaignRangeCoverage();
		List<CampaignSerialRange> rangeList = new ArrayList<CampaignSerialRange>();
		CampaignSerialRange range1 = new CampaignSerialRange();
		range1.setFromSerialNumber("CA123455");
		range1.setToSerialNumber("CA123515");
		rangeList.add(range1);

		CampaignSerialRange range2 = new CampaignSerialRange();
		range2.setFromSerialNumber("99123455");
		range2.setToSerialNumber("99123515");
		rangeList.add(range2);

		serialNumberRange.setRanges(rangeList);
		campaign.setCampaignCoverage(serialNumberRange);

		List<OEMPartToReplace> oemPartsToReplace = new ArrayList<OEMPartToReplace>();
		OEMPartToReplace partToReplace = new OEMPartToReplace();
		partToReplace.setItem(catalogRepository.findItem("PRTVLV1"));
		partToReplace.setNoOfUnits(new Integer(2));
		partToReplace.setShippedByOem(false);
		oemPartsToReplace.add(partToReplace);
		campaign.setOemPartsToReplace(oemPartsToReplace);

		List<NonOEMPartToReplace> nonOemPartsToReplace = new ArrayList<NonOEMPartToReplace>();
		NonOEMPartToReplace nonOemPartToReplace = new NonOEMPartToReplace();
		nonOemPartToReplace.setNoOfUnits(new Integer(2));
		nonOemPartToReplace.setDescription("OIL");
		// nonOemPartToReplace.setPartNumber("45646");
		nonOemPartToReplace.setPricePerUnit(Money.dollars(2));
		nonOemPartsToReplace.add(nonOemPartToReplace);
		campaign.setNonOEMpartsToReplace(nonOemPartsToReplace);

		try {
			campaignValidationService.validate(campaign);
			assertTrue("Validation Success", true);
		} catch (CampaignServiceException e) {
			System.out.println(e);
			fail("Validation not implemented properly");
		}
	}

	public void testHasInvalidPatterns() {
		List<CampaignSerialRange> ranges = new ArrayList<CampaignSerialRange>();
		CampaignSerialRange range1 = getCampaignSerialRange("123456", "123490");
		ranges.add(range1);
		assertEquals(true, campaignValidationService.hasValidPatterns(ranges));

		CampaignSerialRange range2 = getCampaignSerialRange("CA1", "CA8");
		ranges.add(range2);
		assertEquals(true, campaignValidationService.hasValidPatterns(ranges));

		CampaignSerialRange range3 = getCampaignSerialRange("123CA790",
				"123CA850");
		ranges.add(range3);
		assertEquals(true, campaignValidationService.hasValidPatterns(ranges));

		CampaignSerialRange range6 = getCampaignSerialRange("123490", "123456");
		ranges.add(range6);
		assertEquals(false, campaignValidationService.hasValidPatterns(ranges));

		CampaignSerialRange range8 = getCampaignSerialRange("123CA850",
				"123CA790");
		ranges.add(range8);
		assertEquals(false, campaignValidationService.hasValidPatterns(ranges));

		CampaignSerialRange range7 = getCampaignSerialRange("CA8", "CA1");
		ranges.add(range7);
		assertEquals(false, campaignValidationService.hasValidPatterns(ranges));

		CampaignSerialRange range9 = getCampaignSerialRange("CA1", "CB8");
		ranges.add(range9);
		assertEquals(false, campaignValidationService.hasValidPatterns(ranges));

		CampaignSerialRange range4 = getCampaignSerialRange("XXXX90", "XXXX850");
		ranges.add(range4);
		assertEquals(false, campaignValidationService.hasValidPatterns(ranges));

	}

	private CampaignSerialRange getCampaignSerialRange(String frm, String to) {
		CampaignSerialRange range1 = new CampaignSerialRange();
		range1.setFromSerialNumber(frm);
		range1.setToSerialNumber(to);
		return range1;
	}

	public void testMatchesAllNumbersPattern() {
		List<CampaignSerialRange> ranges = new ArrayList<CampaignSerialRange>();
		CampaignSerialRange range1 = getCampaignSerialRange("123456", "123490");
		ranges.add(range1);
		assertEquals(true, campaignValidationService.hasValidPatterns(ranges));

		CampaignSerialRange range6 = getCampaignSerialRange("123490", "123456");
		ranges.add(range6);
		assertEquals(false, campaignValidationService.hasValidPatterns(ranges));
	}

	public void testMatchesAlphaNumberPattern() {
		List<CampaignSerialRange> ranges = new ArrayList<CampaignSerialRange>();
		CampaignSerialRange range2 = getCampaignSerialRange("CA1", "CA8");
		ranges.add(range2);
		assertEquals(true, campaignValidationService.hasValidPatterns(ranges));

		CampaignSerialRange range7 = getCampaignSerialRange("CA8", "CA1");
		ranges.add(range7);
		assertEquals(false, campaignValidationService.hasValidPatterns(ranges));
	}

	public void testMatchesNumberAlphaNumberPattern() {
		List<CampaignSerialRange> ranges = new ArrayList<CampaignSerialRange>();
		CampaignSerialRange range3 = getCampaignSerialRange("123CA790",
				"123CA850");
		ranges.add(range3);
		assertEquals(true, campaignValidationService.hasValidPatterns(ranges));

	}

	public void setCampaignValidationService(
			CampaignValidationService campaignValidationService) {
		this.campaignValidationService = campaignValidationService;
	}

	public void setCatalogRepository(CatalogRepository catalogRepository) {
		this.catalogRepository = catalogRepository;
	}

}
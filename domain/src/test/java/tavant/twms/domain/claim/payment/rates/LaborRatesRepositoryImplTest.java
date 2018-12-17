package tavant.twms.domain.claim.payment.rates;

import org.springframework.beans.factory.annotation.Required;

import tavant.twms.domain.catalog.ItemGroup;
import tavant.twms.domain.catalog.ItemGroupRepository;
import tavant.twms.domain.claim.Criteria;
import tavant.twms.domain.common.CalendarDuration;
import tavant.twms.domain.common.DurationOverlapException;
import tavant.twms.domain.orgmodel.DealerCriterion;
import tavant.twms.domain.orgmodel.DealershipRepository;
import tavant.twms.infra.DomainRepositoryTestCase;
import tavant.twms.infra.ListCriteria;
import tavant.twms.infra.PageResult;
import tavant.twms.infra.PageSpecification;
import tavant.twms.security.SecurityHelper;

import com.domainlanguage.money.Money;
import com.domainlanguage.time.CalendarDate;

public class LaborRatesRepositoryImplTest extends DomainRepositoryTestCase {

	private LaborRatesRepository laborRatesRepository;
	private ItemGroupRepository itemGroupRepository;
	private DealershipRepository dealershipRepository;
	private SecurityHelper securityHelper;

	public void testSaveItem() {
		// Delete existing value
		for (LaborRates entity :  laborRatesRepository.findAll()) {
			laborRatesRepository.delete(entity);
		}
		flushAndClear();

		Criteria forCriteria = new Criteria();
		forCriteria.setWarrantyType("Standard");
		forCriteria.setClaimType("Parts");
		ItemGroup productType = itemGroupRepository.findById(new Long(1));
		assertNotNull(productType);
		forCriteria.setProductType(productType);

		LaborRates laborRates = new LaborRates();
		laborRates.setForCriteria(forCriteria);
		CalendarDate startDate = CalendarDate.from("11/10/2007", "MM/dd/yyyy");
		CalendarDate endDate = CalendarDate.from("11/10/2007", "MM/dd/yyyy");
		Money _10Dollars = Money.dollars(10.00);
		try {
			laborRates
					.set(_10Dollars, new CalendarDuration(startDate, endDate));
			laborRates.set(_10Dollars, new CalendarDuration(endDate.nextDay(),
					endDate.nextDay().nextDay()));
		} catch (DurationOverlapException e) {
			fail();
		}
		laborRatesRepository.save(laborRates);
		flushAndClear();

		LaborRates laborRate = laborRatesRepository
				.findById(laborRates.getId());
		
		assertEquals(_10Dollars, laborRate.getValueAsOf(startDate));
		assertEquals(_10Dollars, laborRate.getValueAsOf(endDate));
		assertNull(laborRate.getValueAsOf(startDate.previousDay()));
	}

	public void testBusinessUnitInfoSave() {
		// Delete the existing information
		for (LaborRates laborRates : laborRatesRepository.findAll()) {
			laborRatesRepository.delete(laborRates);
		}
		// create a new instance do not set the business unit
		LaborRates irLaborRates = newLaborRatesInstance("Standard", "Parts");

		laborRatesRepository.save(irLaborRates);

		assertNotNull(irLaborRates.getId());

		// fetch the results and check if the business unit is set
		// correctly.
		for (LaborRates laborRates : laborRatesRepository.findAll()) {
			// Check of the BusinessUnitInfo gets saved automatically
			assertEquals(securityHelper.getCurrentBusinessUnit().getName(),
					laborRates.getBusinessUnitInfo().getName());
		}
	}
	
	public void testBusinessUnitFilter()
	{
		// create a new instance do not set the business unit
		LaborRates irLaborRates = newLaborRatesInstance("Standard", "Parts");
		laborRatesRepository.save(irLaborRates);
		assertNotNull(irLaborRates.getId());
		assertEquals(securityHelper.getCurrentBusinessUnit().getName(),
				irLaborRates.getBusinessUnitInfo().getName());
		flushAndClear();
		PageSpecification pageSpecification = new PageSpecification();
		pageSpecification.setPageSize(100);
		pageSpecification.setPageNumber(0);
		ListCriteria listCriteria = new ListCriteria();
		listCriteria.setPageSpecification(pageSpecification);
		PageResult<LaborRates> laborRatesList = laborRatesRepository.findPage(
				"from LaborRates laborRates", listCriteria);
		assertNotNull(laborRatesList);
		for (LaborRates laborRates : laborRatesList.getResult()) {
			assertEquals(securityHelper.getCurrentBusinessUnit().getName(),
					laborRates.getBusinessUnitInfo().getName());
		}
	}

	public void testUpdateItem() {
		// TODO: This test case needs to be written propertly.
	}

	public void findLaborRates_Default() {
		Criteria criteria = new Criteria();
		criteria.setProductType(itemGroupRepository.findById(1L));
		criteria.setWarrantyType("GOODWILL");
		criteria.setClaimType("Parts");
		criteria.setDealerCriterion(new DealerCriterion(dealershipRepository
				.findByDealerId(1L)));

		LaborRate laborRate = laborRatesRepository.findLaborRateConfiguration(
				criteria, CalendarDate.date(2007, 1, 1));
		assertNotNull(laborRate);
		assertEquals(Money.dollars(1), laborRate.getValue());
	}

	public void testFindLaborRates_ProductTypeMatch() {
		Criteria criteria = new Criteria();
		criteria.setProductType(itemGroupRepository.findById(5L));
		criteria.setWarrantyType("GOODWILL");
		criteria.setClaimType("Parts");
		criteria.setDealerCriterion(new DealerCriterion(dealershipRepository
				.findByDealerId(21L)));

		LaborRate laborRate = laborRatesRepository.findLaborRateConfiguration(
				criteria, CalendarDate.date(2007, 1, 1));
		assertNotNull(laborRate);
		assertEquals(Money.dollars(2), laborRate.getValue());
	}

	public void testFindLaborRates_WarrantyTypeMatch() {
		Criteria criteria = new Criteria();
		criteria.setProductType(itemGroupRepository.findById(1L));
		criteria.setWarrantyType("STANDARD");
		criteria.setClaimType("Parts");
		criteria.setDealerCriterion(new DealerCriterion(dealershipRepository
				.findByDealerId(21L)));

		LaborRate laborRate = laborRatesRepository.findLaborRateConfiguration(
				criteria, CalendarDate.date(2007, 1, 1));
		assertNotNull(laborRate);
		assertEquals(Money.dollars(3), laborRate.getValue());
	}

	public void testFindLaborRates_ProductTypeAndWarrantyTypeMatch() {
		Criteria criteria = new Criteria();
		criteria.setProductType(itemGroupRepository.findById(5L));
		criteria.setWarrantyType("STANDARD");
		criteria.setClaimType("Parts");
		criteria.setDealerCriterion(new DealerCriterion(dealershipRepository
				.findByDealerId(21L)));

		criteria.setWarrantyType("STANDARD");
		LaborRate laborRate = laborRatesRepository.findLaborRateConfiguration(
				criteria, CalendarDate.date(2007, 1, 1));
		assertNotNull(laborRate);
		assertEquals(Money.dollars(4), laborRate.getValue());
	}

	public void testFindLaborRates_ClaimTypeMatch() {
		Criteria criteria = new Criteria();
		criteria.setProductType(itemGroupRepository.findById(1L));
		criteria.setWarrantyType("GOODWILL");
		criteria.setClaimType("Machine");
		criteria.setDealerCriterion(new DealerCriterion(dealershipRepository
				.findByDealerId(21L)));

		LaborRate laborRate = laborRatesRepository.findLaborRateConfiguration(
				criteria, CalendarDate.date(2007, 1, 1));
		assertNotNull(laborRate);
		assertEquals(Money.dollars(5), laborRate.getValue());
	}

	public void testFindLaborRates_ClaimTypeAndProductTypeMatch() {
		Criteria criteria = new Criteria();
		criteria.setProductType(itemGroupRepository.findById(5L));
		criteria.setWarrantyType("GOODWILL");
		criteria.setClaimType("Machine");
		criteria.setDealerCriterion(new DealerCriterion(dealershipRepository
				.findByDealerId(21L)));

		LaborRate laborRate = laborRatesRepository.findLaborRateConfiguration(
				criteria, CalendarDate.date(2007, 1, 1));
		assertNotNull(laborRate);
		assertEquals(Money.dollars(6), laborRate.getValue());
	}

	public void testFindLaborRates_ClaimTypeAndWarrantyTypeMatch() {
		Criteria criteria = new Criteria();
		criteria.setProductType(itemGroupRepository.findById(1L));
		criteria.setWarrantyType("STANDARD");
		criteria.setClaimType("Machine");
		criteria.setDealerCriterion(new DealerCriterion(dealershipRepository
				.findByDealerId(21L)));

		LaborRate laborRate = laborRatesRepository.findLaborRateConfiguration(
				criteria, CalendarDate.date(2007, 1, 1));
		assertNotNull(laborRate);
		assertEquals(Money.dollars(7), laborRate.getValue());
	}

	public void testFindLaborRates_ClaimTypeWarrantyTypeAndProductTypeMatch() {
		Criteria criteria = new Criteria();
		criteria.setProductType(itemGroupRepository.findById(5L));
		criteria.setWarrantyType("STANDARD");
		criteria.setClaimType("Machine");
		criteria.setDealerCriterion(new DealerCriterion(dealershipRepository
				.findByDealerId(21L)));

		LaborRate laborRate = laborRatesRepository.findLaborRateConfiguration(
				criteria, CalendarDate.date(2007, 1, 1));
		assertNotNull(laborRate);
		assertEquals(Money.dollars(8), laborRate.getValue());
	}

	public void testFindLaborRates_DealerGroupMatch() {
		Criteria criteria = new Criteria();
		criteria.setProductType(itemGroupRepository.findById(1L));
		criteria.setWarrantyType("GOODWILL");
		criteria.setClaimType("Parts");
		criteria.setDealerCriterion(new DealerCriterion(dealershipRepository
				.findByDealerId(20L)));

		LaborRate laborRate = laborRatesRepository.findLaborRateConfiguration(
				criteria, CalendarDate.date(2007, 1, 1));
		assertNotNull(laborRate);
		assertEquals(Money.dollars(9 + 0), laborRate.getValue());
	}

	public void testFindLaborRates_DealerGroupAndProductTypeMatch() {
		Criteria criteria = new Criteria();
		criteria.setProductType(itemGroupRepository.findById(5L));
		criteria.setWarrantyType("GOODWILL");
		criteria.setClaimType("Parts");
		criteria.setDealerCriterion(new DealerCriterion(dealershipRepository
				.findByDealerId(20L)));

		LaborRate laborRate = laborRatesRepository.findLaborRateConfiguration(
				criteria, CalendarDate.date(2007, 1, 1));
		assertNotNull(laborRate);
		assertEquals(Money.dollars(10 + 0), laborRate.getValue());
	}

	public void testFindLaborRates_DealerGroupAndWarrantyTypeMatch() {
		Criteria criteria = new Criteria();
		criteria.setProductType(itemGroupRepository.findById(1L));
		criteria.setWarrantyType("STANDARD");
		criteria.setClaimType("Parts");
		criteria.setDealerCriterion(new DealerCriterion(dealershipRepository
				.findByDealerId(20L)));

		LaborRate laborRate = laborRatesRepository.findLaborRateConfiguration(
				criteria, CalendarDate.date(2007, 1, 1));
		assertNotNull(laborRate);
		assertEquals(Money.dollars(11 + 0), laborRate.getValue());
	}

	public void testFindLaborRates_DealerGroupWarrantyTypeAndProductTypeMatch() {
		Criteria criteria = new Criteria();
		criteria.setProductType(itemGroupRepository.findById(5L));
		criteria.setWarrantyType("STANDARD");
		criteria.setClaimType("Parts");
		criteria.setDealerCriterion(new DealerCriterion(dealershipRepository
				.findByDealerId(20L)));

		LaborRate laborRate = laborRatesRepository.findLaborRateConfiguration(
				criteria, CalendarDate.date(2007, 1, 1));
		assertNotNull(laborRate);
		assertEquals(Money.dollars(12 + 0), laborRate.getValue());
	}

	public void testFindLaborRates_DealerGroupAndClaimTypeMatch() {
		Criteria criteria = new Criteria();
		criteria.setProductType(itemGroupRepository.findById(1L));
		criteria.setWarrantyType("GOODWIL");
		criteria.setClaimType("Machine");
		criteria.setDealerCriterion(new DealerCriterion(dealershipRepository
				.findByDealerId(20L)));

		LaborRate laborRate = laborRatesRepository.findLaborRateConfiguration(
				criteria, CalendarDate.date(2007, 1, 1));
		assertNotNull(laborRate);
		assertEquals(Money.dollars(13 + 0), laborRate.getValue());
	}

	public void testFindLaborRates_DealerGroupClaimTypeAndProductTypeMatch() {
		Criteria criteria = new Criteria();
		criteria.setProductType(itemGroupRepository.findById(5L));
		criteria.setWarrantyType("GOODWIL");
		criteria.setClaimType("Machine");
		criteria.setDealerCriterion(new DealerCriterion(dealershipRepository
				.findByDealerId(20L)));

		LaborRate laborRate = laborRatesRepository.findLaborRateConfiguration(
				criteria, CalendarDate.date(2007, 1, 1));
		assertNotNull(laborRate);
		assertEquals(Money.dollars(14 + 0), laborRate.getValue());
	}

	public void testFindLaborRates_DealerGroupClaimTypeAndWarrantyTypeMatch() {
		Criteria criteria = new Criteria();
		criteria.setProductType(itemGroupRepository.findById(1L));
		criteria.setWarrantyType("STANDARD");
		criteria.setClaimType("Machine");
		criteria.setDealerCriterion(new DealerCriterion(dealershipRepository
				.findByDealerId(20L)));

		LaborRate laborRate = laborRatesRepository.findLaborRateConfiguration(
				criteria, CalendarDate.date(2007, 1, 1));
		assertNotNull(laborRate);
		assertEquals(Money.dollars(15 + 0), laborRate.getValue());
	}

	public void testFindLaborRates_DealerGroupAllMatch() {
		Criteria criteria = new Criteria();
		criteria.setProductType(itemGroupRepository.findById(5L));
		criteria.setWarrantyType("STANDARD");
		criteria.setClaimType("Machine");
		criteria.setDealerCriterion(new DealerCriterion(dealershipRepository
				.findByDealerId(20L)));

		LaborRate laborRate = laborRatesRepository.findLaborRateConfiguration(
				criteria, CalendarDate.date(2007, 1, 1));
		assertNotNull(laborRate);
		assertEquals(Money.dollars(16 + 0), laborRate.getValue());
	}

	public void testFindLaborRates_DealerMatch() {
		Criteria criteria = new Criteria();
		criteria.setProductType(itemGroupRepository.findById(1L));
		criteria.setWarrantyType("GOODWILL");
		criteria.setClaimType("Parts");
		criteria.setDealerCriterion(new DealerCriterion(dealershipRepository
				.findByDealerId(7L)));

		LaborRate laborRate = laborRatesRepository.findLaborRateConfiguration(
				criteria, CalendarDate.date(2007, 1, 1));
		assertNotNull(laborRate);
		assertEquals(Money.dollars(9 + 16), laborRate.getValue());
	}

	public void testFindLaborRates_DealerAndProductTypeMatch() {
		Criteria criteria = new Criteria();
		criteria.setProductType(itemGroupRepository.findById(5L));
		criteria.setWarrantyType("GOODWILL");
		criteria.setClaimType("Parts");
		criteria.setDealerCriterion(new DealerCriterion(dealershipRepository
				.findByDealerId(7L)));

		LaborRate laborRate = laborRatesRepository.findLaborRateConfiguration(
				criteria, CalendarDate.date(2007, 1, 1));
		assertNotNull(laborRate);
		assertEquals(Money.dollars(10 + 16), laborRate.getValue());
	}

	public void testFindLaborRates_DealerAndWarrantyTypeMatch() {
		Criteria criteria = new Criteria();
		criteria.setProductType(itemGroupRepository.findById(1L));
		criteria.setWarrantyType("STANDARD");
		criteria.setClaimType("Parts");
		criteria.setDealerCriterion(new DealerCriterion(dealershipRepository
				.findByDealerId(7L)));

		LaborRate laborRate = laborRatesRepository.findLaborRateConfiguration(
				criteria, CalendarDate.date(2007, 1, 1));
		assertNotNull(laborRate);
		assertEquals(Money.dollars(11 + 16), laborRate.getValue());
	}

	public void testFindLaborRates_DealerWarrantyTypeAndProductTypeMatch() {
		Criteria criteria = new Criteria();
		criteria.setProductType(itemGroupRepository.findById(5L));
		criteria.setWarrantyType("STANDARD");
		criteria.setClaimType("Parts");
		criteria.setDealerCriterion(new DealerCriterion(dealershipRepository
				.findByDealerId(7L)));

		LaborRate laborRate = laborRatesRepository.findLaborRateConfiguration(
				criteria, CalendarDate.date(2007, 1, 1));
		assertNotNull(laborRate);
		assertEquals(Money.dollars(12 + 16), laborRate.getValue());
	}

	public void testFindLaborRates_DealerAndClaimTypeMatch() {
		Criteria criteria = new Criteria();
		criteria.setProductType(itemGroupRepository.findById(1L));
		criteria.setWarrantyType("GOODWIL");
		criteria.setClaimType("Machine");
		criteria.setDealerCriterion(new DealerCriterion(dealershipRepository
				.findByDealerId(7L)));

		LaborRate laborRate = laborRatesRepository.findLaborRateConfiguration(
				criteria, CalendarDate.date(2007, 1, 1));
		assertNotNull(laborRate);
		assertEquals(Money.dollars(13 + 16), laborRate.getValue());
	}

	public void testFindLaborRates_DealerClaimTypeAndProductTypeMatch() {
		Criteria criteria = new Criteria();
		criteria.setProductType(itemGroupRepository.findById(5L));
		criteria.setWarrantyType("GOODWIL");
		criteria.setClaimType("Machine");
		criteria.setDealerCriterion(new DealerCriterion(dealershipRepository
				.findByDealerId(7L)));

		LaborRate laborRate = laborRatesRepository.findLaborRateConfiguration(
				criteria, CalendarDate.date(2007, 1, 1));
		assertNotNull(laborRate);
		assertEquals(Money.dollars(14 + 16), laborRate.getValue());
	}

	public void testFindLaborRates_DealerClaimTypeAndWarrantyTypeMatch() {
		Criteria criteria = new Criteria();
		criteria.setProductType(itemGroupRepository.findById(1L));
		criteria.setWarrantyType("STANDARD");
		criteria.setClaimType("Machine");
		criteria.setDealerCriterion(new DealerCriterion(dealershipRepository
				.findByDealerId(7L)));

		LaborRate laborRate = laborRatesRepository.findLaborRateConfiguration(
				criteria, CalendarDate.date(2007, 1, 1));
		assertNotNull(laborRate);
		assertEquals(Money.dollars(15 + 16), laborRate.getValue());
	}

	public void testFindLaborRates_DealerAllMatch() {
		Criteria criteria = new Criteria();
		criteria.setProductType(itemGroupRepository.findById(5L));
		criteria.setWarrantyType("STANDARD");
		criteria.setClaimType("Machine");
		criteria.setDealerCriterion(new DealerCriterion(dealershipRepository
				.findByDealerId(7L)));

		LaborRate laborRate = laborRatesRepository.findLaborRateConfiguration(
				criteria, CalendarDate.date(2007, 1, 1));
		assertNotNull(laborRate);
		assertEquals(Money.dollars(16 + 16), laborRate.getValue());
	}

	private LaborRates newLaborRatesInstance(String warrantyType, String claimType){
		Criteria forCriteria = new Criteria();
		forCriteria.setWarrantyType(warrantyType);
		forCriteria.setClaimType(claimType);
		ItemGroup productType = itemGroupRepository.findById(new Long(1));
		assertNotNull(productType);
		forCriteria.setProductType(productType);

		LaborRates laborRates = new LaborRates();
		laborRates.setForCriteria(forCriteria);
		CalendarDate startDate = CalendarDate.from("11/10/2007", "MM/dd/yyyy");
		CalendarDate endDate = CalendarDate.from("11/10/2007", "MM/dd/yyyy");
		Money _10Dollars = Money.dollars(10.00);
		try {
			laborRates
 					.set(_10Dollars, new CalendarDuration(startDate, endDate));
			laborRates.set(_10Dollars, new CalendarDuration(endDate.nextDay(),
					endDate.nextDay().nextDay()));
		} catch (DurationOverlapException e) {
			fail();
		}
		return laborRates;
	}
	
	@Required
	public void setLaborRatesRepository(
			LaborRatesRepository laborRatesRepository) {
		this.laborRatesRepository = laborRatesRepository;
	}

	@Required
	public void setDealershipRepository(
			DealershipRepository dealershipRepository) {
		this.dealershipRepository = dealershipRepository;
	}

	@Required
	public void setItemGroupRepository(ItemGroupRepository itemGroupRepository) {
		this.itemGroupRepository = itemGroupRepository;
	}

	public void setSecurityHelper(SecurityHelper securityHelper) {
		this.securityHelper = securityHelper;
	}

}
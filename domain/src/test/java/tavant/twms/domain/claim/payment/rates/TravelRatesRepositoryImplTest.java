package tavant.twms.domain.claim.payment.rates;

import java.util.List;

import org.springframework.beans.factory.annotation.Required;

import tavant.twms.domain.catalog.ItemGroup;
import tavant.twms.domain.catalog.ItemGroupRepository;
import tavant.twms.domain.claim.Criteria;
import tavant.twms.domain.common.CalendarDuration;
import tavant.twms.domain.common.DurationOverlapException;
import tavant.twms.domain.orgmodel.DealerCriterion;
import tavant.twms.domain.orgmodel.Dealership;
import tavant.twms.domain.orgmodel.DealershipRepository;
import tavant.twms.infra.DomainRepositoryTestCase;

import com.domainlanguage.money.Money;
import com.domainlanguage.time.CalendarDate;

public class TravelRatesRepositoryImplTest extends DomainRepositoryTestCase {

    private TravelRatesRepository travelRatesRepository;
    private ItemGroupRepository itemGroupRepository;
    private DealershipRepository dealershipRepository;

	public void testSaveItem() {
    	List<TravelRates> findAll = travelRatesRepository.findAll();
    	
    	for(TravelRates travelRates : findAll ) {
    		travelRatesRepository.delete(travelRates);
    	}
    	flush();
    	
        Criteria forCriteria = new Criteria();
        forCriteria.setWarrantyType("STANDARD");
        ItemGroup group = new ItemGroup();
        group.setId(5L);
        forCriteria.setProductType(group);
        Dealership dealership = new Dealership();
        dealership.setId(20L);
        forCriteria.setDealerCriterion(new DealerCriterion(dealership));

        TravelRates travelRates = new TravelRates();
        travelRates.setForCriteria(forCriteria);
        CalendarDate startDate = CalendarDate.from("11/10/2007", "MM/dd/yyyy");
        CalendarDate endDate = CalendarDate.from("11/10/2007", "MM/dd/yyyy");
        Money _10Dollars = Money.dollars(10.00);
        try {
            TravelRateValues travelRateValues = new TravelRateValues();
            travelRateValues.setDistanceRate(_10Dollars);
            travelRateValues.setHourlyRate(_10Dollars);
            travelRateValues.setTripRate(_10Dollars);
            travelRates.set(travelRateValues, new CalendarDuration(startDate, endDate));
        } catch (DurationOverlapException e) {
            fail();
        }
        travelRatesRepository.save(travelRates);
        flushAndClear();

        TravelRates example = travelRatesRepository.findByCriteria(forCriteria);
        assertEquals(example.getId(), travelRates.getId());
        assertEquals(example.getEntries().size(), 1);
        assertEquals(_10Dollars, example.getEntryAsOf(startDate).getValue().getDistanceRate());
        assertEquals(_10Dollars, example.getEntryAsOf(endDate).getValue().getDistanceRate());
        assertNull(example.getEntryAsOf(startDate.previousDay()));
        assertNull(example.getEntryAsOf(endDate.nextDay()));
    }

    public void testUpdateItem() {
    	//TODO: This test case needs to be written propertly.
    }

    public void findTravelRates_Default() {
    	Criteria criteria = new Criteria();
    	criteria.setProductType(itemGroupRepository.findById(7L));
    	criteria.setWarrantyType("GOODWILL");
    	criteria.setClaimType("Parts");
    	criteria.setDealerCriterion(new DealerCriterion(dealershipRepository.findByDealerId(21L)));

    	TravelRate travelRate = travelRatesRepository.findTravelRateConfiguration(criteria,CalendarDate.date(2007,1,1));
    	assertNotNull(travelRate);
		assertEquals(Money.dollars(1),travelRate.getValue().getDistanceRate());
    }
    public void testFindByCriteria_BusinessUnitFilter()
    {
    	//TODO find 
    	List<TravelRates> findAll = travelRatesRepository.findAll();
    	
    	for(TravelRates travelRates : findAll ) {
    		travelRatesRepository.delete(travelRates);
    	}
    	flush();
    	
        Criteria forCriteria = new Criteria();
        forCriteria.setWarrantyType("STANDARD");
        ItemGroup group = new ItemGroup();
        group.setId(5L);
        forCriteria.setProductType(group);
        Dealership dealership = new Dealership();
        dealership.setId(20L);
        forCriteria.setDealerCriterion(new DealerCriterion(dealership));

        TravelRates travelRates = new TravelRates();
        travelRates.setForCriteria(forCriteria);
        CalendarDate startDate = CalendarDate.from("11/10/2007", "MM/dd/yyyy");
        CalendarDate endDate = CalendarDate.from("11/10/2007", "MM/dd/yyyy");
        Money _10Dollars = Money.dollars(10.00);
        try {
            TravelRateValues travelRateValues = new TravelRateValues();
            travelRateValues.setDistanceRate(_10Dollars);
            travelRateValues.setHourlyRate(_10Dollars);
            travelRateValues.setTripRate(_10Dollars);
            travelRates.set(travelRateValues, new CalendarDuration(startDate, endDate));
        } catch (DurationOverlapException e) {
            fail();
        }
        travelRatesRepository.save(travelRates);
        flushAndClear();

        TravelRates example = travelRatesRepository.findByCriteria(forCriteria);
        assertEquals(example.getId(), travelRates.getId());
    }
    public void testFindTravelRates_ProductTypeMatch() {
    	Criteria criteria = new Criteria();
    	criteria.setProductType(itemGroupRepository.findById(5L));
    	criteria.setWarrantyType("GOODWILL");
    	criteria.setClaimType("Parts");
    	criteria.setDealerCriterion(new DealerCriterion(dealershipRepository.findByDealerId(21L)));


		TravelRate travelRate = travelRatesRepository.findTravelRateConfiguration(criteria,CalendarDate.date(2007,1,1));
    	assertNotNull(travelRate);
		assertEquals(Money.dollars(2),travelRate.getValue().getDistanceRate());
    }    
    
    public void testFindTravelRates_WarrantyTypeMatch() {
    	Criteria criteria = new Criteria();
    	criteria.setProductType(itemGroupRepository.findById(7L));
    	criteria.setWarrantyType("STANDARD");
    	criteria.setClaimType("Parts");
    	criteria.setDealerCriterion(new DealerCriterion(dealershipRepository.findByDealerId(21L)));

    	
		TravelRate travelRate = travelRatesRepository.findTravelRateConfiguration(criteria,CalendarDate.date(2007,1,1));
    	assertNotNull(travelRate);
		assertEquals(Money.dollars(3),travelRate.getValue().getDistanceRate());
    }    

    public void testFindTravelRates_ProductTypeAndWarrantyTypeMatch() {
    	Criteria criteria = new Criteria();
    	criteria.setProductType(itemGroupRepository.findById(5L));
    	criteria.setWarrantyType("STANDARD");
    	criteria.setClaimType("Parts");
    	criteria.setDealerCriterion(new DealerCriterion(dealershipRepository.findByDealerId(21L)));

    	criteria.setWarrantyType("STANDARD");
		TravelRate travelRate = travelRatesRepository.findTravelRateConfiguration(criteria,CalendarDate.date(2007,1,1));
    	assertNotNull(travelRate);
		assertEquals(Money.dollars(4),travelRate.getValue().getDistanceRate());
    }    
    
    
    public void testFindTravelRates_ClaimTypeMatch() {
    	Criteria criteria = new Criteria();
    	criteria.setProductType(itemGroupRepository.findById(7L));
    	criteria.setWarrantyType("GOODWILL");
    	criteria.setClaimType("Machine");
    	criteria.setDealerCriterion(new DealerCriterion(dealershipRepository.findByDealerId(21L)));

    	
		TravelRate travelRate = travelRatesRepository.findTravelRateConfiguration(criteria,CalendarDate.date(2007,1,1));
    	assertNotNull(travelRate);
		assertEquals(Money.dollars(5),travelRate.getValue().getDistanceRate());
    }    
    
    public void testFindTravelRates_ClaimTypeAndProductTypeMatch() {
    	Criteria criteria = new Criteria();
    	criteria.setProductType(itemGroupRepository.findById(5L));
    	criteria.setWarrantyType("GOODWILL");
    	criteria.setClaimType("Machine");
    	criteria.setDealerCriterion(new DealerCriterion(dealershipRepository.findByDealerId(21L)));

    	
		TravelRate travelRate = travelRatesRepository.findTravelRateConfiguration(criteria,CalendarDate.date(2007,1,1));
    	assertNotNull(travelRate);
		assertEquals(Money.dollars(6),travelRate.getValue().getDistanceRate());
    }    
    
    public void testFindTravelRates_ClaimTypeAndWarrantyTypeMatch() {
    	Criteria criteria = new Criteria();
    	criteria.setProductType(itemGroupRepository.findById(7L));
    	criteria.setWarrantyType("STANDARD");
    	criteria.setClaimType("Machine");
    	criteria.setDealerCriterion(new DealerCriterion(dealershipRepository.findByDealerId(21L)));

    	
		TravelRate travelRate = travelRatesRepository.findTravelRateConfiguration(criteria,CalendarDate.date(2007,1,1));
    	assertNotNull(travelRate);
		assertEquals(Money.dollars(7),travelRate.getValue().getDistanceRate());
    }    
    
    public void testFindTravelRates_ClaimTypeWarrantyTypeAndProductTypeMatch() {
    	Criteria criteria = new Criteria();
    	criteria.setProductType(itemGroupRepository.findById(5L));
    	criteria.setWarrantyType("STANDARD");
    	criteria.setClaimType("Machine");
    	criteria.setDealerCriterion(new DealerCriterion(dealershipRepository.findByDealerId(21L)));

    	
		TravelRate travelRate = travelRatesRepository.findTravelRateConfiguration(criteria,CalendarDate.date(2007,1,1));
    	assertNotNull(travelRate);
		assertEquals(Money.dollars(8),travelRate.getValue().getDistanceRate());
    }    
    
    public void testFindTravelRates_DealerGroupMatch() {
    	Criteria criteria = new Criteria();
    	criteria.setProductType(itemGroupRepository.findById(7L));
    	criteria.setWarrantyType("GOODWILL");
    	criteria.setClaimType("Parts");
    	criteria.setDealerCriterion(new DealerCriterion(dealershipRepository.findByDealerId(20L)));

		TravelRate travelRate = travelRatesRepository.findTravelRateConfiguration(criteria,CalendarDate.date(2007,1,1));
    	assertNotNull(travelRate);
		assertEquals(Money.dollars(9+0),travelRate.getValue().getDistanceRate());
    }    
    
    public void testFindTravelRates_DealerGroupAndProductTypeMatch() {
    	Criteria criteria = new Criteria();
    	criteria.setProductType(itemGroupRepository.findById(5L));
    	criteria.setWarrantyType("GOODWILL");
    	criteria.setClaimType("Parts");
    	criteria.setDealerCriterion(new DealerCriterion(dealershipRepository.findByDealerId(20L)));

		TravelRate travelRate = travelRatesRepository.findTravelRateConfiguration(criteria,CalendarDate.date(2007,1,1));
    	assertNotNull(travelRate);
		assertEquals(Money.dollars(10+0),travelRate.getValue().getDistanceRate());
    }    
    
    public void testFindTravelRates_DealerGroupAndWarrantyTypeMatch() {
    	Criteria criteria = new Criteria();
    	criteria.setProductType(itemGroupRepository.findById(7L));
    	criteria.setWarrantyType("STANDARD");
    	criteria.setClaimType("Parts");
    	criteria.setDealerCriterion(new DealerCriterion(dealershipRepository.findByDealerId(20L)));

		TravelRate travelRate = travelRatesRepository.findTravelRateConfiguration(criteria,CalendarDate.date(2007,1,1));
    	assertNotNull(travelRate);
		assertEquals(Money.dollars(11+0),travelRate.getValue().getDistanceRate());
    }    
    
    
    public void testFindTravelRates_DealerGroupWarrantyTypeAndProductTypeMatch() {
    	Criteria criteria = new Criteria();
    	criteria.setProductType(itemGroupRepository.findById(5L));
    	criteria.setWarrantyType("STANDARD");
    	criteria.setClaimType("Parts");
    	criteria.setDealerCriterion(new DealerCriterion(dealershipRepository.findByDealerId(20L)));

		TravelRate travelRate = travelRatesRepository.findTravelRateConfiguration(criteria,CalendarDate.date(2007,1,1));
    	assertNotNull(travelRate);
		assertEquals(Money.dollars(12+0),travelRate.getValue().getDistanceRate());
    }    
    
    public void testFindTravelRates_DealerGroupAndClaimTypeMatch() {
    	Criteria criteria = new Criteria();
    	criteria.setProductType(itemGroupRepository.findById(7L));
    	criteria.setWarrantyType("GOODWIL");
    	criteria.setClaimType("Machine");
    	criteria.setDealerCriterion(new DealerCriterion(dealershipRepository.findByDealerId(20L)));

		TravelRate travelRate = travelRatesRepository.findTravelRateConfiguration(criteria,CalendarDate.date(2007,1,1));
    	assertNotNull(travelRate);
		assertEquals(Money.dollars(13+0),travelRate.getValue().getDistanceRate());
    }    
    
    public void testFindTravelRates_DealerGroupClaimTypeAndProductTypeMatch() {
    	Criteria criteria = new Criteria();
    	criteria.setProductType(itemGroupRepository.findById(5L));
    	criteria.setWarrantyType("GOODWIL");
    	criteria.setClaimType("Machine");
    	criteria.setDealerCriterion(new DealerCriterion(dealershipRepository.findByDealerId(20L)));

		TravelRate travelRate = travelRatesRepository.findTravelRateConfiguration(criteria,CalendarDate.date(2007,1,1));
    	assertNotNull(travelRate);
		assertEquals(Money.dollars(14+0),travelRate.getValue().getDistanceRate());
    }    
    
    
    public void testFindTravelRates_DealerGroupClaimTypeAndWarrantyTypeMatch() {
    	Criteria criteria = new Criteria();
    	criteria.setProductType(itemGroupRepository.findById(7L));
    	criteria.setWarrantyType("STANDARD");
    	criteria.setClaimType("Machine");
    	criteria.setDealerCriterion(new DealerCriterion(dealershipRepository.findByDealerId(20L)));

		TravelRate travelRate = travelRatesRepository.findTravelRateConfiguration(criteria,CalendarDate.date(2007,1,1));
    	assertNotNull(travelRate);
		assertEquals(Money.dollars(15+0),travelRate.getValue().getDistanceRate());
    }    
    
    public void testFindTravelRates_DealerGroupAllMatch() {
    	Criteria criteria = new Criteria();
    	criteria.setProductType(itemGroupRepository.findById(5L));
    	criteria.setWarrantyType("STANDARD");
    	criteria.setClaimType("Machine");
    	criteria.setDealerCriterion(new DealerCriterion(dealershipRepository.findByDealerId(20L)));

		TravelRate travelRate = travelRatesRepository.findTravelRateConfiguration(criteria,CalendarDate.date(2007,1,1));
    	assertNotNull(travelRate);
		assertEquals(Money.dollars(16+0),travelRate.getValue().getDistanceRate());
    }    
    
    
    public void testFindTravelRates_DealerMatch() {
    	Criteria criteria = new Criteria();
    	criteria.setProductType(itemGroupRepository.findById(7L));
    	criteria.setWarrantyType("GOODWILL");
    	criteria.setClaimType("Parts");
    	criteria.setDealerCriterion(new DealerCriterion(dealershipRepository.findByDealerId(7L)));

		TravelRate travelRate = travelRatesRepository.findTravelRateConfiguration(criteria,CalendarDate.date(2007,1,1));
    	assertNotNull(travelRate);
		assertEquals(Money.dollars(9+16),travelRate.getValue().getDistanceRate());
    }    
    
    public void testFindTravelRates_DealerAndProductTypeMatch() {
    	Criteria criteria = new Criteria();
    	criteria.setProductType(itemGroupRepository.findById(5L));
    	criteria.setWarrantyType("GOODWILL");
    	criteria.setClaimType("Parts");
    	criteria.setDealerCriterion(new DealerCriterion(dealershipRepository.findByDealerId(7L)));

		TravelRate travelRate = travelRatesRepository.findTravelRateConfiguration(criteria,CalendarDate.date(2007,1,1));
    	assertNotNull(travelRate);
		assertEquals(Money.dollars(10+16),travelRate.getValue().getDistanceRate());
    }    
    
    public void testFindTravelRates_DealerAndWarrantyTypeMatch() {
    	Criteria criteria = new Criteria();
    	criteria.setProductType(itemGroupRepository.findById(7L));
    	criteria.setWarrantyType("STANDARD");
    	criteria.setClaimType("Parts");
    	criteria.setDealerCriterion(new DealerCriterion(dealershipRepository.findByDealerId(7L)));

		TravelRate travelRate = travelRatesRepository.findTravelRateConfiguration(criteria,CalendarDate.date(2007,1,1));
    	assertNotNull(travelRate);
		assertEquals(Money.dollars(11+16),travelRate.getValue().getDistanceRate());
    }    
    
    
    public void testFindTravelRates_DealerWarrantyTypeAndProductTypeMatch() {
    	Criteria criteria = new Criteria();
    	criteria.setProductType(itemGroupRepository.findById(5L));
    	criteria.setWarrantyType("STANDARD");
    	criteria.setClaimType("Parts");
    	criteria.setDealerCriterion(new DealerCriterion(dealershipRepository.findByDealerId(7L)));

		TravelRate travelRate = travelRatesRepository.findTravelRateConfiguration(criteria,CalendarDate.date(2007,1,1));
    	assertNotNull(travelRate);
		assertEquals(Money.dollars(12+16),travelRate.getValue().getDistanceRate());
    }    
    
    public void testFindTravelRates_DealerAndClaimTypeMatch() {
    	Criteria criteria = new Criteria();
    	criteria.setProductType(itemGroupRepository.findById(7L));
    	criteria.setWarrantyType("GOODWIL");
    	criteria.setClaimType("Machine");
    	criteria.setDealerCriterion(new DealerCriterion(dealershipRepository.findByDealerId(7L)));

		TravelRate travelRate = travelRatesRepository.findTravelRateConfiguration(criteria,CalendarDate.date(2007,1,1));
    	assertNotNull(travelRate);
		assertEquals(Money.dollars(13+16),travelRate.getValue().getDistanceRate());
    }    
    
    public void testFindTravelRates_DealerClaimTypeAndProductTypeMatch() {
    	Criteria criteria = new Criteria();
    	criteria.setProductType(itemGroupRepository.findById(5L));
    	criteria.setWarrantyType("GOODWIL");
    	criteria.setClaimType("Machine");
    	criteria.setDealerCriterion(new DealerCriterion(dealershipRepository.findByDealerId(7L)));

		TravelRate travelRate = travelRatesRepository.findTravelRateConfiguration(criteria,CalendarDate.date(2007,1,1));
    	assertNotNull(travelRate);
		assertEquals(Money.dollars(14+16),travelRate.getValue().getDistanceRate());
    }    
    
    
    public void testFindTravelRates_DealerClaimTypeAndWarrantyTypeMatch() {
    	Criteria criteria = new Criteria();
    	criteria.setProductType(itemGroupRepository.findById(7L));
    	criteria.setWarrantyType("STANDARD");
    	criteria.setClaimType("Machine");
    	criteria.setDealerCriterion(new DealerCriterion(dealershipRepository.findByDealerId(7L)));

		TravelRate travelRate = travelRatesRepository.findTravelRateConfiguration(criteria,CalendarDate.date(2007,1,1));
    	assertNotNull(travelRate);
		assertEquals(Money.dollars(15+16),travelRate.getValue().getDistanceRate());
    }    
    
    public void testFindTravelRates_AllMatch() {
    	Criteria criteria = new Criteria();
    	criteria.setProductType(itemGroupRepository.findById(5L));
    	criteria.setWarrantyType("STANDARD");
    	criteria.setClaimType("Machine");
    	criteria.setDealerCriterion(new DealerCriterion(dealershipRepository.findByDealerId(7L)));

		TravelRate travelRate = travelRatesRepository.findTravelRateConfiguration(criteria,CalendarDate.date(2007,1,1));
    	assertNotNull(travelRate);
		assertEquals(Money.dollars(16+16),travelRate.getValue().getDistanceRate());
    }    
    
    @Required
    public void setTravelRatesRepository(TravelRatesRepository travelRatesRepository) {
        this.travelRatesRepository = travelRatesRepository;
    }

	/**
	 * @param itemGroupRepository the itemGroupRepository to set
	 */
    @Required
	public void setItemGroupRepository(ItemGroupRepository itemGroupRepository) {
		this.itemGroupRepository = itemGroupRepository;
	}

	/**
	 * @param dealershipRepository the dealershipRepository to set
	 */
    @Required
	public void setDealershipRepository(DealershipRepository dealershipRepository) {
		this.dealershipRepository = dealershipRepository;
	}
}
package tavant.twms.domain.claim.payment.rates;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.springframework.beans.factory.annotation.Required;

import tavant.twms.domain.catalog.CatalogRepository;
import tavant.twms.domain.catalog.Item;
import tavant.twms.domain.catalog.ItemCriterion;
import tavant.twms.domain.claim.Criteria;
import tavant.twms.domain.common.CalendarDuration;
import tavant.twms.domain.common.CriteriaElement;
import tavant.twms.domain.common.CriteriaEvaluationPrecedence;
import tavant.twms.domain.common.DurationOverlapException;
import tavant.twms.domain.common.GlobalConfiguration;
import tavant.twms.domain.orgmodel.DealerCriterion;
import tavant.twms.domain.orgmodel.Dealership;
import tavant.twms.domain.orgmodel.DealershipRepository;
import tavant.twms.domain.policy.WarrantyType;
import tavant.twms.infra.DomainRepositoryTestCase;

import com.domainlanguage.time.CalendarDate;

public class AdministeredItemPriceRepositoryImplTest extends DomainRepositoryTestCase {

    private CalendarDate _1Jan2001 = CalendarDate.date(2001,1,1);
    private CalendarDate lastDayOfFirstQuarter = _1Jan2001.plusMonths(3).previousDay();
    private CalendarDuration firstQuarter = new CalendarDuration(_1Jan2001,lastDayOfFirstQuarter);
    private CalendarDate firstDayOfSecondQuarter = _1Jan2001.plusMonths(3);
    private CalendarDate lastDayOfSecondQuarter = firstDayOfSecondQuarter.plusMonths(3).previousDay();
    private CalendarDuration secondQuarter = new CalendarDuration(firstDayOfSecondQuarter,lastDayOfSecondQuarter);        

    private AdministeredItemPriceRepository administeredItemPriceRepository;    
    private CatalogRepository catalogRepository;
    private DealershipRepository dealershipRepository;
    

	/**
	 * @param dealershipRepository the dealershipRepository to set
	 */
    @Required
	public void setDealershipRepository(DealershipRepository dealershipRepository) {
		this.dealershipRepository = dealershipRepository;
	}

	/**
	 * @param catalogRepository the catalogRepository to set
	 */
    @Required
	public void setCatalogRepository(CatalogRepository catalogRepository) {
		this.catalogRepository = catalogRepository;
	}


    /**
	 * @param administeredItemPriceRepository the administeredItemPriceRepository to set
	 */
	public void setAdministeredItemPriceRepository(
			AdministeredItemPriceRepository administeredItemPriceRepository) {
		this.administeredItemPriceRepository = administeredItemPriceRepository;
	}

	public void testSave() throws DurationOverlapException {
    	Object[] itemAndPrice = newAdministeredPrice(catalogRepository.findItem("ATTCH-UNIGY-50-HZ-1"));
    	AdministeredItemPrice entity = (AdministeredItemPrice)itemAndPrice[1];
        
        entity.set(new BigDecimal(25),firstQuarter);
        entity.set(new BigDecimal(30),secondQuarter);
        administeredItemPriceRepository.save(entity);
        assertNotNull(entity.getId());
        flushAndClear();
        AdministeredItemPrice reloadedEntity = administeredItemPriceRepository.findById(entity.getId());
        assertNotNull(reloadedEntity);
        
        assertEquals(new BigDecimal(25),reloadedEntity.getEntryAsOf(_1Jan2001).getValue());
        assertEquals(new BigDecimal(25),reloadedEntity.getEntryAsOf(lastDayOfFirstQuarter).getValue());        
        assertEquals(new BigDecimal(30),reloadedEntity.getEntryAsOf(firstDayOfSecondQuarter).getValue());
        assertEquals(new BigDecimal(30),reloadedEntity.getEntryAsOf(lastDayOfSecondQuarter).getValue());        
    }
	
	public void testFindPriceModifier_BusinessUnitFilter()
			throws DurationOverlapException {
		Object[] itemAndPrice = priceCriteria(new Criteria(), catalogRepository
				.findItem("ATTCH-UNIGY-50-HZ-1"));
		AdministeredItemPrice entity = (AdministeredItemPrice) itemAndPrice[1];
		Item item = (Item) itemAndPrice[0];

		GlobalConfiguration instance = GlobalConfiguration.getInstance();
		MathContext mathContext = instance.getMathContext();

		CalendarDate _1Jan2001 = CalendarDate.date(2001, 1, 1);
		CalendarDate _31Dec2001 = CalendarDate.date(2001, 12, 31);
		CalendarDuration calendarDuration = new CalendarDuration(_1Jan2001,
				_31Dec2001);
		BigDecimal modifier = new BigDecimal(94.57D, mathContext);
		entity.set(modifier, calendarDuration);
		
		administeredItemPriceRepository.save(entity);
		
		AdministeredItemPrice savedEntity = administeredItemPriceRepository.findById(entity.getId());
		assertEquals(1, savedEntity.getPriceModifiers().size());
		
		SortedSet<ItemPriceModifier>  itemPriceModifiers = savedEntity.getPriceModifiers();
		for(ItemPriceModifier itemPriceModifier:itemPriceModifiers)
		{
			assertNotNull(itemPriceModifier.getBusinessUnitInfo());
			assertEquals("IR", itemPriceModifier.getBusinessUnitInfo().getName());
		}
		
		Criteria criteria = new Criteria();
		Dealership findByDealerId = dealershipRepository
				.findByDealerId(new Long(7));
		criteria.setDealerCriterion(new DealerCriterion(findByDealerId));
		criteria.setClaimType("Machine");
		criteria.setWarrantyType(WarrantyType.STANDARD.getType());

		CriteriaEvaluationPrecedence evalPrecedence = dealerClaimWarrantyProduct();

		ItemPriceModifier priceModifier = administeredItemPriceRepository
				.findPriceModifier(item, criteria, evalPrecedence, _1Jan2001);
		assertNotNull(priceModifier);
		assertNotNull(priceModifier.getBusinessUnitInfo());
	}

    
    public void testUpdate() throws DurationOverlapException {
    	Object[] itemAndPrice = newAdministeredPrice(catalogRepository.findItem("ATTCH-UNIGY-50-HZ-1"));
    	AdministeredItemPrice entity = (AdministeredItemPrice)itemAndPrice[1];
        entity.set(new BigDecimal(25),firstQuarter);
        entity.set(new BigDecimal(30),secondQuarter);
        administeredItemPriceRepository.save(entity);
        assertNotNull(entity.getId());
        flushAndClear();
        AdministeredItemPrice reloadedEntity = administeredItemPriceRepository.findById(entity.getId());
        assertNotNull(reloadedEntity);
        
        assertEquals(new BigDecimal(25),reloadedEntity.getEntryAsOf(_1Jan2001).getValue());
        assertEquals(new BigDecimal(25),reloadedEntity.getEntryAsOf(lastDayOfFirstQuarter).getValue());        
        assertEquals(new BigDecimal(30),reloadedEntity.getEntryAsOf(firstDayOfSecondQuarter).getValue());
        assertEquals(new BigDecimal(30),reloadedEntity.getEntryAsOf(lastDayOfSecondQuarter).getValue());        
        
        reloadedEntity.getEntryAsOf(_1Jan2001).setValue(new BigDecimal(20));
        reloadedEntity.getEntryAsOf(firstDayOfSecondQuarter).setValue(new BigDecimal(25));
        administeredItemPriceRepository.update(reloadedEntity);
        flushAndClear();
        reloadedEntity = administeredItemPriceRepository.findById(entity.getId());
        
        assertEquals(new BigDecimal(20),reloadedEntity.getEntryAsOf(lastDayOfFirstQuarter).getValue());        
        assertEquals(new BigDecimal(25),reloadedEntity.getEntryAsOf(firstDayOfSecondQuarter).getValue());
    }

    public void testFind_MostGeneralMatch_ItemNotInAnyGroup() throws DurationOverlapException {
    	/**
    	 * Default price.
    	 */
    	Object[] itemAndPrice = priceCriteria(new Criteria(),catalogRepository.findItem("ATTCH-UNIGY-50-HZ-1"));
    	AdministeredItemPrice entity = (AdministeredItemPrice)itemAndPrice[1];
    	Item item = (Item)itemAndPrice[0];
    	
    	GlobalConfiguration instance = GlobalConfiguration.getInstance();
		MathContext mathContext = instance.getMathContext();
		
		CalendarDate _1Jan2001 = CalendarDate.date(2001, 1, 1);
		CalendarDate _31Dec2001 = CalendarDate.date(2001, 12, 31);
		CalendarDuration calendarDuration = new CalendarDuration(_1Jan2001,_31Dec2001);
		BigDecimal modifier = new BigDecimal(94.57D,mathContext);
		entity.set(modifier, calendarDuration);
    	administeredItemPriceRepository.save(entity);
    	
    	Criteria criteria = new Criteria();
    	Dealership findByDealerId = dealershipRepository.findByDealerId(new Long(7));
		criteria.setDealerCriterion(new DealerCriterion(findByDealerId));
		criteria.setClaimType("Machine");
		criteria.setWarrantyType(WarrantyType.STANDARD.getType());

		CriteriaEvaluationPrecedence evalPrecedence = dealerClaimWarrantyProduct();
		
    	ItemPriceModifier priceModifier = administeredItemPriceRepository.findPriceModifier(item, criteria, evalPrecedence, _1Jan2001);
    	assertNotNull(priceModifier);
    	assertEquals(calendarDuration,priceModifier.getDuration());
    	assertEquals(modifier,priceModifier.getScalingFactor());
    	
    	priceModifier = administeredItemPriceRepository.findPriceModifier(item, criteria, evalPrecedence, _31Dec2001);
    	assertNotNull(priceModifier);
    	assertEquals(calendarDuration,priceModifier.getDuration());
    	assertEquals(modifier,priceModifier.getScalingFactor());

    	priceModifier = administeredItemPriceRepository.findPriceModifier(item, criteria, evalPrecedence, _1Jan2001.previousDay());
    	assertNull(priceModifier);

    	priceModifier = administeredItemPriceRepository.findPriceModifier(item, criteria, evalPrecedence, _31Dec2001.nextDay());
    	assertNull(priceModifier);
    }

    
    public void testFind_WarrantyTypeMatches_ItemNotInAnyGroup() throws DurationOverlapException {
    	Item item = catalogRepository.findItem("ATTCH-UNIGY-50-HZ-1");

    	Criteria criteria = new Criteria();
		/**
    	 * Default price.
    	 */
    	Object[] itemAndPrice = priceCriteria(criteria, item);
		
		CalendarDate _1Jan2001 = CalendarDate.date(2001, 1, 1);
		CalendarDate _31Dec2001 = CalendarDate.date(2001, 12, 31);
		CalendarDuration calendarDuration = new CalendarDuration(_1Jan2001,_31Dec2001);    	
    	GlobalConfiguration instance = GlobalConfiguration.getInstance();
		MathContext mathContext = instance.getMathContext();
		BigDecimal modifier = new BigDecimal(94.57D,mathContext);
    	AdministeredItemPrice entity = (AdministeredItemPrice)itemAndPrice[1];
		entity.set(modifier, calendarDuration);
    	administeredItemPriceRepository.save(entity);

    	/**
    	 * Price for a specific warranty.
    	 */
    	criteria = new Criteria();
    	criteria.setWarrantyType(WarrantyType.STANDARD.getType());
    	itemAndPrice = priceCriteria(criteria, item);
		BigDecimal modifier1 = new BigDecimal(49.75D,mathContext);
    	entity = (AdministeredItemPrice)itemAndPrice[1];
		entity.set(modifier1, calendarDuration);
    	administeredItemPriceRepository.save(entity);
    	
    	/**
    	 * The warranty specific price should get picked.
    	 */
    	Criteria searchByCriteria = new Criteria();
    	Dealership findByDealerId = dealershipRepository.findByDealerId(new Long(7));
		searchByCriteria.setDealerCriterion(new DealerCriterion(findByDealerId));
		searchByCriteria.setClaimType("Machine");
		searchByCriteria.setWarrantyType(WarrantyType.STANDARD.getType());
		CriteriaEvaluationPrecedence evalPrecedence = dealerClaimWarrantyProduct();
		
    	ItemPriceModifier priceModifier = administeredItemPriceRepository.findPriceModifier(item, searchByCriteria, evalPrecedence, _1Jan2001);
    	assertNotNull(priceModifier);
    	assertEquals(calendarDuration,priceModifier.getDuration());
    	assertEquals(modifier1,priceModifier.getScalingFactor());
    	
    	priceModifier = administeredItemPriceRepository.findPriceModifier(item, searchByCriteria, evalPrecedence, _31Dec2001);
    	assertNotNull(priceModifier);
    	assertEquals(calendarDuration,priceModifier.getDuration());
    	assertEquals(modifier1,priceModifier.getScalingFactor());

    	
    	/**
    	 * The warranty condition won't match, and the default price should
    	 * get picked up.
    	 */
    	searchByCriteria.setWarrantyType(WarrantyType.EXTENDED.getType());

    	priceModifier = administeredItemPriceRepository.findPriceModifier(item, searchByCriteria, evalPrecedence, _1Jan2001);
    	assertNotNull(priceModifier);
    	assertEquals(calendarDuration,priceModifier.getDuration());
    	assertEquals(modifier,priceModifier.getScalingFactor());
    	
    	priceModifier = administeredItemPriceRepository.findPriceModifier(item, searchByCriteria, evalPrecedence, _31Dec2001);
    	assertNotNull(priceModifier);
    	assertEquals(calendarDuration,priceModifier.getDuration());
    	assertEquals(modifier,priceModifier.getScalingFactor());
    	
    	
    	priceModifier = administeredItemPriceRepository.findPriceModifier(item, searchByCriteria, evalPrecedence, _1Jan2001.previousDay());
    	assertNull(priceModifier);

    	priceModifier = administeredItemPriceRepository.findPriceModifier(item, searchByCriteria, evalPrecedence, _31Dec2001.nextDay());
    	assertNull(priceModifier);
    }

    
    public void testFind_ClaimTypeMatches_ItemNotInAnyGroup() throws DurationOverlapException {
    	Item item = catalogRepository.findItem("ATTCH-UNIGY-50-HZ-1");

    	Criteria criteria = new Criteria();
		/**
    	 * Default price.
    	 */
    	Object[] itemAndPrice = priceCriteria(criteria, item);
		
		CalendarDate _1Jan2001 = CalendarDate.date(2001, 1, 1);
		CalendarDate _31Dec2001 = CalendarDate.date(2001, 12, 31);
		CalendarDuration calendarDuration = new CalendarDuration(_1Jan2001,_31Dec2001);    	
    	GlobalConfiguration instance = GlobalConfiguration.getInstance();
		MathContext mathContext = instance.getMathContext();
		BigDecimal _94point57 = new BigDecimal(94.57D,mathContext);
    	AdministeredItemPrice entity = (AdministeredItemPrice)itemAndPrice[1];
		entity.set(_94point57, calendarDuration);
    	administeredItemPriceRepository.save(entity);

    	/**
    	 * Price for a specific warranty.
    	 */
    	criteria = new Criteria();
    	criteria.setWarrantyType(WarrantyType.STANDARD.getType());
    	itemAndPrice = priceCriteria(criteria, item);
		BigDecimal _49point75 = new BigDecimal(49.75D,mathContext);
    	entity = (AdministeredItemPrice)itemAndPrice[1];
		entity.set(_49point75, calendarDuration);
    	administeredItemPriceRepository.save(entity);

    	
    	/**
    	 * The claim type based price should get picked up.
    	 */
    	Criteria searchByCriteria = new Criteria();
    	Dealership findByDealerId = dealershipRepository.findByDealerId(new Long(7));
		searchByCriteria.setDealerCriterion(new DealerCriterion(findByDealerId));
		searchByCriteria.setClaimType("Machine");
		searchByCriteria.setWarrantyType(WarrantyType.STANDARD.getType());
		CriteriaEvaluationPrecedence evalPrecedence = dealerClaimWarrantyProduct();
		
    	ItemPriceModifier priceModifier = administeredItemPriceRepository.findPriceModifier(item, searchByCriteria, evalPrecedence, _1Jan2001);
    	assertNotNull(priceModifier);
    	assertEquals(calendarDuration,priceModifier.getDuration());
    	assertEquals(_49point75,priceModifier.getScalingFactor());
    	
    	priceModifier = administeredItemPriceRepository.findPriceModifier(item, searchByCriteria, evalPrecedence, _31Dec2001);
    	assertNotNull(priceModifier);
    	assertEquals(calendarDuration,priceModifier.getDuration());
    	assertEquals(_49point75,priceModifier.getScalingFactor());

    	
    	/**
    	 * The claim condition won't match, and the warranty based price should
    	 * get picked up.
    	 */
    	searchByCriteria.setClaimType("Parts");

    	priceModifier = administeredItemPriceRepository.findPriceModifier(item, searchByCriteria, evalPrecedence, _1Jan2001);
    	assertNotNull(priceModifier);
    	assertEquals(calendarDuration,priceModifier.getDuration());
    	assertEquals(_49point75,priceModifier.getScalingFactor());
    	
    	priceModifier = administeredItemPriceRepository.findPriceModifier(item, searchByCriteria, evalPrecedence, _31Dec2001);
    	assertNotNull(priceModifier);
    	assertEquals(calendarDuration,priceModifier.getDuration());
    	assertEquals(_49point75,priceModifier.getScalingFactor());
    	
    	/**
    	 * The default price should get picked up.
    	 */
    	searchByCriteria.setWarrantyType(WarrantyType.EXTENDED.getType());
    	priceModifier = administeredItemPriceRepository.findPriceModifier(item, searchByCriteria, evalPrecedence, _1Jan2001);
    	assertNotNull(priceModifier);
    	assertEquals(calendarDuration,priceModifier.getDuration());
    	assertEquals(_94point57,priceModifier.getScalingFactor());
    	
    	priceModifier = administeredItemPriceRepository.findPriceModifier(item, searchByCriteria, evalPrecedence, _31Dec2001);
    	assertNotNull(priceModifier);
    	assertEquals(calendarDuration,priceModifier.getDuration());
    	assertEquals(_94point57,priceModifier.getScalingFactor());
    	
    	
    	priceModifier = administeredItemPriceRepository.findPriceModifier(item, searchByCriteria, evalPrecedence, _1Jan2001.previousDay());
    	assertNull(priceModifier);

    	priceModifier = administeredItemPriceRepository.findPriceModifier(item, searchByCriteria, evalPrecedence, _31Dec2001.nextDay());
    	assertNull(priceModifier);
    }
    
    public void testFind_DealerMatches_ItemNotInAnyGroup() throws DurationOverlapException {
    	Item item = catalogRepository.findItem("ATTCH-UNIGY-50-HZ-1");

    	Criteria criteria = new Criteria();
		/**
    	 * Default price.
    	 */
    	Object[] itemAndPrice = priceCriteria(criteria, item);
		
		CalendarDate _1Jan2001 = CalendarDate.date(2001, 1, 1);
		CalendarDate _31Dec2001 = CalendarDate.date(2001, 12, 31);
		CalendarDuration calendarDuration = new CalendarDuration(_1Jan2001,_31Dec2001);    	
    	GlobalConfiguration instance = GlobalConfiguration.getInstance();
		MathContext mathContext = instance.getMathContext();
		BigDecimal _94point57 = new BigDecimal(94.57D,mathContext);
    	AdministeredItemPrice entity = (AdministeredItemPrice)itemAndPrice[1];
		entity.set(_94point57, calendarDuration);
    	administeredItemPriceRepository.save(entity);

    	/**
    	 * Price for a specific warranty.
    	 */
    	criteria = new Criteria();
    	criteria.setWarrantyType(WarrantyType.STANDARD.getType());
    	itemAndPrice = priceCriteria(criteria, item);
		BigDecimal _49point75 = new BigDecimal(49.75D,mathContext);
    	entity = (AdministeredItemPrice)itemAndPrice[1];
		entity.set(_49point75, calendarDuration);
    	administeredItemPriceRepository.save(entity);

    	
    	/**
    	 * The dealer specific price should get picked up.
    	 */
    	Criteria searchByCriteria = new Criteria();
		searchByCriteria.setDealerCriterion(new DealerCriterion(dealershipRepository.findByDealerId(7L)));
		searchByCriteria.setClaimType("Machine");
		searchByCriteria.setWarrantyType(WarrantyType.STANDARD.getType());
		CriteriaEvaluationPrecedence evalPrecedence = dealerClaimWarrantyProduct();
		
    	ItemPriceModifier priceModifier = administeredItemPriceRepository.findPriceModifier(item, searchByCriteria, evalPrecedence, _1Jan2001);
    	assertNotNull(priceModifier);
    	assertEquals(calendarDuration,priceModifier.getDuration());
    	assertEquals(_49point75,priceModifier.getScalingFactor());
    	
    	priceModifier = administeredItemPriceRepository.findPriceModifier(item, searchByCriteria, evalPrecedence, _31Dec2001);
    	assertNotNull(priceModifier);
    	assertEquals(calendarDuration,priceModifier.getDuration());
    	assertEquals(_49point75,priceModifier.getScalingFactor());

    	
    	/**
    	 * The default price should get picked up.
    	 */
    	searchByCriteria.setWarrantyType(WarrantyType.EXTENDED.getType());
    	priceModifier = administeredItemPriceRepository.findPriceModifier(item, searchByCriteria, evalPrecedence, _1Jan2001);
    	assertNotNull(priceModifier);
    	assertEquals(calendarDuration,priceModifier.getDuration());
    	assertEquals(_94point57,priceModifier.getScalingFactor());
    	
    	priceModifier = administeredItemPriceRepository.findPriceModifier(item, searchByCriteria, evalPrecedence, _31Dec2001);
    	assertNotNull(priceModifier);
    	assertEquals(calendarDuration,priceModifier.getDuration());
    	assertEquals(_94point57,priceModifier.getScalingFactor());
    	
    	
    	priceModifier = administeredItemPriceRepository.findPriceModifier(item, searchByCriteria, evalPrecedence, _1Jan2001.previousDay());
    	assertNull(priceModifier);

    	priceModifier = administeredItemPriceRepository.findPriceModifier(item, searchByCriteria, evalPrecedence, _31Dec2001.nextDay());
    	assertNull(priceModifier);
    }
    
    
    
	protected Object[] priceCriteria(Criteria forCriteria, Item item) {
		Object[] itemAndPrice = newAdministeredPrice(item);
    	((AdministeredItemPrice)itemAndPrice[1]).setForCriteria(forCriteria);
		return itemAndPrice;
	}    
	protected CriteriaEvaluationPrecedence dealerClaimWarrantyProduct() {
		CriteriaEvaluationPrecedence evalPrecedence = new CriteriaEvaluationPrecedence();
		List<CriteriaElement> criteriaElements = evalPrecedence.getProperties();
		criteriaElements.add(new CriteriaElement("Dealer","dealerCriterion"));
		criteriaElements.add(new CriteriaElement("Claim Type","claimType"));
		criteriaElements.add(new CriteriaElement("Warranty Type","warrantyType"));
		criteriaElements.add(new CriteriaElement("Product","productType"));
		return evalPrecedence;
	}
	
	protected Object[] newAdministeredPrice(Item anItem) {
    	assertNotNull(anItem);
        AdministeredItemPrice entity = new AdministeredItemPrice();
        entity.setItemCriterion(new ItemCriterion(anItem));
		return new Object[]{anItem,entity};
	}
}

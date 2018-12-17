package tavant.twms.domain.catalog;

import java.math.BigDecimal;
import java.math.MathContext;

import org.hibernate.Query;
import org.springframework.beans.factory.annotation.Required;

import tavant.twms.domain.common.CalendarDuration;
import tavant.twms.domain.common.DurationOverlapException;
import tavant.twms.domain.common.GlobalConfiguration;
import tavant.twms.infra.DomainRepositoryTestCase;

import com.domainlanguage.time.CalendarDate;

public class ItemBasePriceRepositoryImplTest extends DomainRepositoryTestCase {
	private ItemBasePriceRepository itemBasePriceRepository;
	private CatalogRepository catalogRepository;
	
    private CalendarDate _1Jan2011 = CalendarDate.date(2011,1,1);
    private CalendarDate lastDayOfFirstQuarter = _1Jan2011.plusMonths(3).previousDay();
    private CalendarDuration firstQuarter = new CalendarDuration(_1Jan2011,lastDayOfFirstQuarter);
    private CalendarDate firstDayOfSecondQuarter = _1Jan2011.plusMonths(3);
    private CalendarDate lastDayOfSecondQuarter = firstDayOfSecondQuarter.plusMonths(3).previousDay();
    private CalendarDuration secondQuarter = new CalendarDuration(firstDayOfSecondQuarter,lastDayOfSecondQuarter);        
	
	
	/**
	 * @param itemBasePriceRepository the itemBasePriceRepository to set
	 */
	@Required
	public void setItemBasePriceRepository(
			ItemBasePriceRepository itemBasePriceRepository) {
		this.itemBasePriceRepository = itemBasePriceRepository;
	}

	/**
	 * @param catalogRepository the catalogRepository to set
	 */
	@Required
	public void setCatalogRepository(CatalogRepository catalogRepository) {
		this.catalogRepository = catalogRepository;
	}

	public void testFindByItem() throws DurationOverlapException {
		Query deleteBasePriceValues = getSession().createQuery("delete from BasePriceValue");
		Query deletePrices = getSession().createQuery("delete from ItemBasePrice");
		
		deleteBasePriceValues.executeUpdate();
		deletePrices.executeUpdate();
		
		ItemBasePrice itemBasePrice = new ItemBasePrice();
		Item anItem = catalogRepository.findItem("ATTCH-UNIGY-50-HZ-1");
		itemBasePrice.setForItem(anItem);
		
		MathContext mathContext = GlobalConfiguration.getInstance().getMathContext();
		BigDecimal _10 = new BigDecimal(10.0D,mathContext);
		itemBasePrice.set(_10, firstQuarter);
		BigDecimal _11 = new BigDecimal(11.0D,mathContext);
		itemBasePrice.set(_11, secondQuarter);
		
		itemBasePriceRepository.save(itemBasePrice);
		flushAndClear();
		ItemBasePrice reloaded = itemBasePriceRepository.findByItem(anItem);
		
		assertNull(reloaded.getValueAsOf(_1Jan2011.previousDay()));
		assertEquals(_10,reloaded.getValueAsOf(_1Jan2011));
		
		assertEquals(_10,reloaded.getValueAsOf(lastDayOfFirstQuarter));
		assertEquals(_11,reloaded.getValueAsOf(firstDayOfSecondQuarter));
		assertEquals(_11,reloaded.getValueAsOf(lastDayOfSecondQuarter));
		assertNull(reloaded.getValueAsOf(lastDayOfSecondQuarter.nextDay()));
	}

}

package tavant.twms.domain.claim.payment.rates;

import static tavant.twms.domain.DomainTestHelper.getOrCreateFirstClaimedItemFromClaim;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.TimeZone;

import org.jmock.Mock;
import org.jmock.cglib.MockObjectTestCase;
import org.jmock.core.Constraint;

import tavant.twms.domain.catalog.Item;
import tavant.twms.domain.catalog.ItemBasePrice;
import tavant.twms.domain.catalog.ItemBasePriceRepository;
import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.claim.ClaimedItem;
import tavant.twms.domain.claim.Criteria;
import tavant.twms.domain.claim.MachineClaim;
import tavant.twms.domain.common.CalendarDuration;
import tavant.twms.domain.common.CriteriaEvaluationPrecedence;
import tavant.twms.domain.common.CriteriaEvaluationPrecedenceRepository;
import tavant.twms.domain.common.CurrencyExchangeRate;
import tavant.twms.domain.common.CurrencyExchangeRateRepository;
import tavant.twms.domain.common.DurationOverlapException;
import tavant.twms.domain.common.GlobalConfiguration;
import tavant.twms.domain.orgmodel.Dealership;
import tavant.twms.domain.policy.PolicyDefinition;
import tavant.twms.domain.policy.WarrantyType;

import com.domainlanguage.money.Money;
import com.domainlanguage.time.CalendarDate;
import com.domainlanguage.timeutil.Clock;

public class ItemPriceAdminServiceImplTest extends MockObjectTestCase {
    private ItemPriceAdminServiceImpl fixture;

    private Mock itemBasePriceRepositoryMock;

    private Mock currencyExchangeRepositoryMock;

    private Mock administeredItemPriceRepositoryMock;

    private Mock criteriaEvaluationPrecedenceRepositoryMock;

    static {
        Clock.setDefaultTimeZone(TimeZone.getDefault());
        Clock.timeSource();
    }

    @Override
    protected void setUp() throws Exception {
        this.itemBasePriceRepositoryMock = mock(ItemBasePriceRepository.class);
        this.currencyExchangeRepositoryMock = mock(CurrencyExchangeRateRepository.class);
        this.administeredItemPriceRepositoryMock = mock(AdministeredItemPriceRepository.class);
        this.criteriaEvaluationPrecedenceRepositoryMock = mock(CriteriaEvaluationPrecedenceRepository.class);

		fixture = new ItemPriceAdminServiceImpl();
		fixture.setAdministeredItemPriceRepository(
                (AdministeredItemPriceRepository) administeredItemPriceRepositoryMock.proxy());
		fixture.setCriteriaEvaluationPrecedenceRepository(
                (CriteriaEvaluationPrecedenceRepository) criteriaEvaluationPrecedenceRepositoryMock.proxy());
		fixture.setCurrencyExchangeRateRepository(
                (CurrencyExchangeRateRepository) currencyExchangeRepositoryMock.proxy());
		fixture.setItemBasePriceRepository(
                (ItemBasePriceRepository) itemBasePriceRepositoryMock.proxy());
	}

	public void testFindPriceItemClaim() {
		final Claim claim = new MachineClaim();
        final ClaimedItem claimedItem = getOrCreateFirstClaimedItemFromClaim(claim);
        Dealership dealer = new Dealership();
		claim.setForDealerShip(dealer);

		PolicyDefinition policyDefinition = new PolicyDefinition();
		policyDefinition.setWarrantyType(WarrantyType.STANDARD);
		claimedItem.setApplicablePolicy(policyDefinition);

		final Item item = new Item();
		Currency EUR = Currency.getInstance("EUR");
		final Money returnValue = Money.valueOf(10.0D, EUR);
		fixture = new ItemPriceAdminServiceImpl() {
			@Override
			public Money findPrice(Item forItem, Criteria withCriteria, CalendarDate asOfDate, Currency inCurrency) {
				assertEquals(item, forItem);
				assertEquals(claim.getForDealerShip(), withCriteria.getDealerCriterion().getDealer());
				assertEquals(claimedItem.getApplicablePolicy().getWarrantyType().getType(),
                        withCriteria.getWarrantyType());
				assertEquals(forItem.getProduct(), withCriteria.getProductType());
				return returnValue;
			}
        };
        this.fixture.findPrice(item, claim, policyDefinition);
    }

	public void testFindPriceItemCriteriaCalendarDateCurrency_NoBasePrice() {
        Item anItem = new Item();
        Criteria criteria = new Criteria();
        CalendarDate date = Clock.today();
        Currency EUR = Currency.getInstance("EUR");

        ItemBasePrice itemBasePrice = new ItemBasePrice();

        this.itemBasePriceRepositoryMock.expects(once()).method("findByItem").with(
                new Constraint[] { eq(anItem) }).will(returnValue(itemBasePrice));

        assertNull(this.fixture.findPrice(anItem, criteria, date, EUR));
    }

    public void testFindPriceItemCriteriaCalendarDateCurrency_NoModifier()
            throws DurationOverlapException {
        Item anItem = new Item();
        Claim claim = new MachineClaim();
        List<PriceFetchData> priceFetchDataList = new ArrayList<PriceFetchData>();
        Criteria criteria = new Criteria();
        CalendarDate date = Clock.today();
        Currency EUR = Currency.getInstance("EUR");
        Currency baseCurrency = GlobalConfiguration.getInstance().getBaseCurrency();

        ItemBasePrice itemBasePrice = new ItemBasePrice();
        CalendarDuration duration = new CalendarDuration(date, date);
        itemBasePrice
                .set(new BigDecimal(20.0D, GlobalConfiguration.getInstance().getMathContext()),
                        duration);

/*        this.itemBasePriceRepositoryMock.expects(once()).method("findByItem").with(
                new Constraint[] { eq(claim),eq(priceFetchDataList)});*/

        CriteriaEvaluationPrecedence criteriaEvaluationPrecedence = new CriteriaEvaluationPrecedence();
/*        this.criteriaEvaluationPrecedenceRepositoryMock.expects(once()).method("findByName").with(
                eq("Parts Price List")).will(returnValue(criteriaEvaluationPrecedence));

        this.administeredItemPriceRepositoryMock.expects(once()).method("findPriceModifier").with(
                new Constraint[] { eq(anItem), eq(criteria), eq(criteriaEvaluationPrecedence),
                        eq(date) }).will(returnValue(null));*/

        CurrencyExchangeRate cER = new CurrencyExchangeRate();
        cER.setFromCurrency(EUR);
        cER.setToCurrency(baseCurrency);

        // EUR -> Base is 0.5D, so base -> EUR => 2.0
        cER.set(new BigDecimal(0.5D), duration);
/*        this.currencyExchangeRepositoryMock.expects(once()).method("findConversionFactor").with(
                new Constraint[] { eq(EUR), eq(baseCurrency), eq(date) }).will(
                returnValue(cER.getEntryAsOf(date)));*/
       // assertEquals(Money.valueOf(40.0D, EUR), this.fixture.findPrice(anItem, criteria, date, EUR));
    }

    public void testFindPriceItemCriteriaCalendarDateCurrency_SomeModifier()
            throws DurationOverlapException {
        Item anItem = new Item();
        Claim claim = new MachineClaim();
        List<PriceFetchData> priceFetchDataList = new ArrayList<PriceFetchData>();
        Criteria criteria = new Criteria();
        CalendarDate date = Clock.today();
        Currency EUR = Currency.getInstance("EUR");
        Currency baseCurrency = GlobalConfiguration.getInstance().getBaseCurrency();

        ItemBasePrice itemBasePrice = new ItemBasePrice();
        CalendarDuration duration = new CalendarDuration(date, date);
        itemBasePrice
                .set(new BigDecimal(20.0D, GlobalConfiguration.getInstance().getMathContext()),
                        duration);

      /*  this.itemBasePriceRepositoryMock.expects(once()).method("findByItem").with(
                new Constraint[] { eq(claim),eq(priceFetchDataList)});*/

        CriteriaEvaluationPrecedence criteriaEvaluationPrecedence = new CriteriaEvaluationPrecedence();
  /*      this.criteriaEvaluationPrecedenceRepositoryMock.expects(once()).method("findByName").with(
                eq("Parts Price List")).will(returnValue(criteriaEvaluationPrecedence));*/

        ItemPriceModifier modifier = new ItemPriceModifier();
        modifier.setScalingFactor(new BigDecimal(80.0D));
/*        this.administeredItemPriceRepositoryMock.expects(once()).method("findPriceModifier").with(
                new Constraint[] { eq(anItem), eq(criteria), eq(criteriaEvaluationPrecedence),
                        eq(date) }).will(returnValue(modifier));
*/
        CurrencyExchangeRate cER = new CurrencyExchangeRate();
        cER.setFromCurrency(EUR);
        cER.setToCurrency(baseCurrency);

        // EUR -> Base is 0.5D, so base -> EUR => 2.0
        cER.set(new BigDecimal(0.5D), duration);
/*        this.currencyExchangeRepositoryMock.expects(once()).method("findConversionFactor").with(
                new Constraint[] { eq(EUR), eq(baseCurrency), eq(date) }).will(
                returnValue(cER.getEntryAsOf(date)));*/
       // assertEquals(Money.valueOf(32.0D, EUR), this.fixture.findPrice(anItem, criteria, date, EUR));
    }

}

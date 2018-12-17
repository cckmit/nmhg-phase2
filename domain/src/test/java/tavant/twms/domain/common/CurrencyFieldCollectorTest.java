package tavant.twms.domain.common;

import java.lang.reflect.Field;
import java.util.Currency;
import java.util.HashSet;
import java.util.Set;

import ognl.OgnlException;

import org.hibernate.annotations.Type;
import org.springframework.beans.factory.annotation.Required;

import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.claim.LaborDetail;
import tavant.twms.domain.claim.MachineClaim;
import tavant.twms.domain.claim.NonOEMPartReplaced;
import tavant.twms.domain.claim.OEMPartReplaced;
import tavant.twms.domain.claim.PartReplaced;
import tavant.twms.domain.claim.ServiceDetail;
import tavant.twms.domain.claim.ServiceInformation;
import tavant.twms.domain.claim.TravelDetail;
import tavant.twms.domain.claim.payment.CostCategory;
import tavant.twms.domain.claim.payment.CostCategoryRepository;
import tavant.twms.domain.claim.payment.Payment;
import tavant.twms.domain.claim.payment.PaymentComponent;
import tavant.twms.infra.DomainRepositoryTestCase;
import tavant.twms.infra.ObjectGraphTraverser;
import tavant.twms.infra.ObjectGraphTraverser.FieldFilter;
import tavant.twms.infra.ObjectGraphTraverser.FieldOperation;

import com.domainlanguage.money.Money;

public class CurrencyFieldCollectorTest extends DomainRepositoryTestCase {
    private CostCategoryRepository costCategoryRepository;
    private boolean includeLaborAndParts = true;
    private ObjectGraphTraverser objectGraphTraverser;
    
    /**
	 * @param objectGraphTraverser the objectGraphTraverser to set
	 */
    @Required
	public void setObjectGraphTraverser(ObjectGraphTraverser objectGraphTraverser) {
		this.objectGraphTraverser = objectGraphTraverser;
	}

	public void setIncludeLaborAndParts(boolean includeLaborAndParts) {
        this.includeLaborAndParts = includeLaborAndParts;
    }

    public void setCostCategoryRepository(CostCategoryRepository costCategoryRepository) {
        this.costCategoryRepository = costCategoryRepository;
    }

    /**
     * @throws OgnlException
     */
    public void testCollectCurrencyFieldValues() throws Exception {
        Set<CurrencyFieldValue> expectedValues = new HashSet<CurrencyFieldValue>();
        Claim claim = aClaimWithVariousCurrencyFields(expectedValues,Currency.getInstance("INR"));
        CurrencyFieldCollector fixture = new CurrencyFieldCollector();
        Set<CurrencyFieldValue> collectCurrencyFieldValues = fixture.collectCurrencyFieldValuesOf(claim);
        
        assertEquals(expectedValues.size(),collectCurrencyFieldValues.size());
        assertEquals(expectedValues, collectCurrencyFieldValues);
        final Set<CurrencyFieldValue> valuesUsingAnotherApproach = new HashSet<CurrencyFieldValue>();
        objectGraphTraverser.traverse(claim, new FieldFilter(){

			public boolean isOfInterest(Field field, Object ofObject) {
				if( field.getType().equals(Money.class) && field.isAnnotationPresent(Type.class) ) {
					Type type = field.getAnnotation(Type.class);
					return "tavant.twms.infra.MoneyUserType".equals(type.type()); 
				}
				return false;
			}
        	
        }, new FieldOperation(){

			public void doSomething(Field field, Object ofObject) {
				valuesUsingAnotherApproach.add(new CurrencyFieldValue(ofObject,field));
			}
        });
        assertTrue(valuesUsingAnotherApproach.containsAll(expectedValues));
    }

    public Claim aClaimWithVariousCurrencyFields(Set<CurrencyFieldValue> expectedValues,Currency inCurrency) throws Exception {
        Claim claim = new MachineClaim();
        Payment payment = new Payment();
        
        ServiceInformation serviceInformation = new ServiceInformation();
        ServiceDetail serviceDetail = new ServiceDetail();
        serviceInformation.setServiceDetail(serviceDetail);
        claim.setServiceInformation(serviceInformation);
        
        serviceDetail.setItemFreightAndDuty(Money.valueOf(120,inCurrency));
        expectedValues.add(new CurrencyFieldValue(serviceDetail,ServiceDetail.class.getDeclaredField("itemFreightAndDuty")));
        
        serviceDetail.setMealsExpense(Money.valueOf(48,inCurrency));
        expectedValues.add(new CurrencyFieldValue(serviceDetail,ServiceDetail.class.getDeclaredField("mealsExpense")));
        expectedValues.add(new CurrencyFieldValue(serviceDetail,ServiceDetail.class.getDeclaredField("parkingAndTollExpense")));
        
        if (includeLaborAndParts) {
            LaborDetail laborDetail = new LaborDetail();
            serviceDetail.addLaborDetail(laborDetail);
            laborDetail.setLaborRate(Money.valueOf(90, inCurrency));
            expectedValues.add(new CurrencyFieldValue(laborDetail, LaborDetail.class.getDeclaredField("laborRate")));
            OEMPartReplaced oemPartReplaced = new OEMPartReplaced();
            oemPartReplaced.setPricePerUnit(Money.valueOf(88, inCurrency));
            serviceDetail.getOEMPartsReplaced().add(oemPartReplaced);
            expectedValues.add(new CurrencyFieldValue(oemPartReplaced,
            		PartReplaced.class.getDeclaredField("pricePerUnit")));
            expectedValues.add(new CurrencyFieldValue(oemPartReplaced,
            		PartReplaced.class.getDeclaredField("costPricePerUnit")));
            expectedValues.add(new CurrencyFieldValue(oemPartReplaced, 
            		PartReplaced.class.getDeclaredField("materialCost")));
            oemPartReplaced = new OEMPartReplaced();
            oemPartReplaced.setPricePerUnit(Money.valueOf(92, inCurrency));
            serviceDetail.getOEMPartsReplaced().add(oemPartReplaced);
            expectedValues.add(new CurrencyFieldValue(oemPartReplaced,
            		PartReplaced.class.getDeclaredField("pricePerUnit")));
            expectedValues.add(new CurrencyFieldValue(oemPartReplaced,
            		PartReplaced.class.getDeclaredField("costPricePerUnit")));
            expectedValues.add(new CurrencyFieldValue(oemPartReplaced,
            		PartReplaced.class.getDeclaredField("materialCost")));
            NonOEMPartReplaced nonOEMPartReplaced = new NonOEMPartReplaced();
            nonOEMPartReplaced.setPricePerUnit(Money.valueOf(101, inCurrency));
            serviceDetail.getNonOEMPartsReplaced().add(nonOEMPartReplaced);
            expectedValues.add(new CurrencyFieldValue(nonOEMPartReplaced, 
            		PartReplaced.class.getDeclaredField("pricePerUnit")));
            expectedValues.add(new CurrencyFieldValue(nonOEMPartReplaced, 
            		PartReplaced.class.getDeclaredField("costPricePerUnit")));
            expectedValues.add(new CurrencyFieldValue(nonOEMPartReplaced, 
            		PartReplaced.class.getDeclaredField("materialCost")));
        }        
        TravelDetail travelDetails = new TravelDetail();
        travelDetails.setDistanceCharge(Money.valueOf(25,inCurrency));
        travelDetails.setTripCharge(Money.valueOf(35, inCurrency));
        travelDetails.setTimeCharge(Money.valueOf(45,inCurrency));
        serviceDetail.setTravelDetails(travelDetails);
        
        expectedValues.add(new CurrencyFieldValue(travelDetails,TravelDetail.class.getDeclaredField("distanceCharge")));
        expectedValues.add(new CurrencyFieldValue(travelDetails,TravelDetail.class.getDeclaredField("tripCharge")));
        expectedValues.add(new CurrencyFieldValue(travelDetails,TravelDetail.class.getDeclaredField("timeCharge")));
        
        payment.setClaimedAmount(Money.valueOf(400,inCurrency));
        expectedValues.add(new CurrencyFieldValue(payment,Payment.class.getDeclaredField("claimedAmount")));
        
        payment.setTotalAmount(Money.valueOf(200,inCurrency));
        expectedValues.add(new CurrencyFieldValue(payment,Payment.class.getDeclaredField("totalAmount")));
        
        //payment.setPreviousPaidAmount(Money.valueOf(0, inCurrency));
        //expectedValues.add(new CurrencyFieldValue(payment,Payment.class.getDeclaredField("previousPaidAmount")));
        
        CostCategory someCostCategory = costCategoryRepository.findCostCategoryByCode("OEM_PARTS");
        //PaymentComponent paymentComponent = payment.addNewComponent(someCostCategory);
        //paymentComponent.setClaimedAmount(Money.valueOf(400,inCurrency));
        //expectedValues.add(new CurrencyFieldValue(paymentComponent,PaymentComponent.class.getDeclaredField("claimedAmount")));
        
        
        claim.setPayment(payment);
        return claim;
    }

}

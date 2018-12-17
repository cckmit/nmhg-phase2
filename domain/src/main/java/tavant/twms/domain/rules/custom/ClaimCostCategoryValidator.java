package tavant.twms.domain.rules.custom;

import com.domainlanguage.money.Money;

import tavant.twms.domain.claim.ServiceDetail;
import tavant.twms.domain.claim.TravelDetail;
import tavant.twms.domain.rules.SystemDefinedBusinessCondition;
import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.claim.payment.CostCategory;
import tavant.twms.domain.claim.payment.CostCategoryRepository;
import tavant.twms.domain.claim.payment.definition.PaymentDefinition;
import tavant.twms.domain.claim.payment.definition.PaymentDefinitionAdminService;
import tavant.twms.domain.claim.payment.definition.PaymentSection;
import tavant.twms.domain.claim.payment.definition.Section;
import tavant.twms.domain.policy.Policy;

import java.math.BigDecimal;
import java.util.SortedMap;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import org.apache.commons.lang.StringUtils;

/**
 * Created by IntelliJ IDEA.
 * User: pradyot.rout
 * Date: Apr 25, 2009
 * Time: 5:07:28 PM
 * To change this template use File | Settings | File Templates.
 */
public class ClaimCostCategoryValidator implements
        SystemDefinedBusinessCondition {
    private PaymentDefinitionAdminService paymentDefinitionAdminService;
    private CostCategoryRepository costCategoryRepository;

    public boolean execute(SortedMap<String, Object> ruleExecutionContext) {
		Claim claim = (Claim) ruleExecutionContext.get("claim");
		return !isCostCategoryClaimedCoveredUnderPymtDefn(claim);
	}

    private boolean isCostCategoryClaimedCoveredUnderPymtDefn(Claim claim){
        boolean toReturn = true;
        Policy applicablePolicy = claim.getClaimedItems().get(0).getApplicablePolicy();
        PaymentDefinition paymentDefnPicked = paymentDefinitionAdminService.findBestPaymentDefinition(claim,
				applicablePolicy);
        Map<String,CostCategory> costCategoriesOnClaim = listOfCostCategoriesOnClaim(claim);
        Map<String,Boolean> costCategoriesClaimed = costCategoriesClaimed(claim);
        for (String costCategoryCode : costCategoriesOnClaim.keySet()) {
            boolean isConfiguredInPymntDefn = false;
            for (PaymentSection pymtSection : paymentDefnPicked.getPaymentSections()) {
                if(costCategoriesOnClaim.get(costCategoryCode)!=null &&
                        pymtSection.getSection().getName().equals(costCategoriesOnClaim.get(costCategoryCode).getName())){
                    isConfiguredInPymntDefn = true;
                    break;
                }
            }
            if(!isConfiguredInPymntDefn &&
                    costCategoriesOnClaim.get(costCategoryCode)!=null &&
                    costCategoriesClaimed.get(costCategoriesOnClaim.get(costCategoryCode).getName())!=null){
                    toReturn = false;
                    break;
            }
        }
        return toReturn;
    }

    public PaymentDefinitionAdminService getPaymentDefinitionAdminService() {
        return paymentDefinitionAdminService;
    }

    public void setPaymentDefinitionAdminService(PaymentDefinitionAdminService paymentDefinitionAdminService) {
        this.paymentDefinitionAdminService = paymentDefinitionAdminService;
    }

    public CostCategoryRepository getCostCategoryRepository() {
        return costCategoryRepository;
    }

    public void setCostCategoryRepository(CostCategoryRepository costCategoryRepository) {
        this.costCategoryRepository = costCategoryRepository;
    }

    private Map<String,CostCategory> listOfCostCategoriesOnClaim(Claim claim){
        Map<String,CostCategory> allCostCategoriesMap = new HashMap<String,CostCategory>();
        List<CostCategory> allCostCategories = costCategoryRepository.findAllCostCategories();
        for (CostCategory costCategory : allCostCategories) {
           allCostCategoriesMap.put(costCategory.getCode(),costCategory);
        }
        Map<String,CostCategory> costCategoriesMapToReturn = new HashMap<String,CostCategory>();
        for (String costCategoryCode : claim.getIncludedCostCategories().keySet()) {
                if(claim.getIncludedCostCategories().get(costCategoryCode)){
                    costCategoriesMapToReturn.put(costCategoryCode,allCostCategoriesMap.get(costCategoryCode));
                }
            }
        return costCategoriesMapToReturn;
    }

    private Map<String,Boolean> costCategoriesClaimed(Claim claim) {
        Map<String, Boolean> toReturn = new HashMap<String, Boolean>();
        
        final ServiceDetail serviceDetail =
                claim.getServiceInformation().getServiceDetail();
        final TravelDetail travelDetails = serviceDetail.getTravelDetails();
        
        if (!serviceDetail.getLaborPerformed().isEmpty()) {
            toReturn.put(Section.LABOR, Boolean.TRUE);
        }
        
        if (travelDetails != null) {
            if(isNonZero(travelDetails.getDistance())) {
                toReturn.put(Section.TRAVEL_BY_DISTANCE, Boolean.TRUE);
            }

            if (!StringUtils.isEmpty(travelDetails.getHours())&&isNonZero(new BigDecimal(travelDetails.getHours()))) {
                toReturn.put(Section.TRAVEL_BY_HOURS, Boolean.TRUE);
            }

            if (isNonZero(travelDetails.getTrips())) {
                toReturn.put(Section.TRAVEL_BY_TRIP, Boolean.TRUE);
            }

            if (isNonZero(serviceDetail.getMealsExpense())) {
                toReturn.put(Section.MEALS, Boolean.TRUE);
            }

            if (isNonZero(serviceDetail.getParkingAndTollExpense())) {
                toReturn.put(Section.PARKING, Boolean.TRUE);
            }

            if (isNonZero(serviceDetail.getItemFreightAndDuty())) {
                toReturn.put(Section.ITEM_FREIGHT_DUTY, Boolean.TRUE);
            }

            if (isNonZero(serviceDetail.getPerDiem())) {
                toReturn.put(Section.PER_DIEM, Boolean.TRUE);
            }

            if (isNonZero(serviceDetail.getRentalCharges())) {
                toReturn.put(Section.RENTAL_CHARGES, Boolean.TRUE);
            }
            
            if (isNonZero(travelDetails.getAdditionalHours())) {
                toReturn.put(Section.ADDITIONAL_TRAVEL_HOURS, Boolean.TRUE);
            }

            if (isNonZero(serviceDetail.getLocalPurchaseExpense())) {
                toReturn.put(Section.LOCAL_PURCHASE, Boolean.TRUE);
            }

            if (isNonZero(serviceDetail.getTollsExpense())) {
                toReturn.put(Section.TOLLS, Boolean.TRUE);
            }

            if (isNonZero(serviceDetail.getOtherFreightDutyExpense())) {
                toReturn.put(Section.OTHER_FREIGHT_DUTY, Boolean.TRUE);
            }

            if (isNonZero(serviceDetail.getOthersExpense())) {
                toReturn.put(Section.OTHERS, Boolean.TRUE);
            }

            if (!serviceDetail.getMiscPartsReplaced().isEmpty()) {
                toReturn.put(Section.MISCELLANEOUS_PARTS, Boolean.TRUE);
            }
            if(isNonZero(serviceDetail.getHandlingFee())){
            	toReturn.put(Section.HANDLING_FEE, Boolean.TRUE);
            }
            
			if (isNonZero(serviceDetail.getTransportationAmt())) {
				toReturn.put(Section.TRANSPORTATION_COST, Boolean.TRUE);
			}
        }
        
        return toReturn;
    }

    private boolean isNonZero(Number number) {
        return !(number == null || number.doubleValue() == 0);
    }

    private boolean isNonZero(Money money) {
        return !(money == null || money.isZero());
    }
}

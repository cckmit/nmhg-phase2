package tavant.twms.domain.claim.payment;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.claim.Criteria;
import tavant.twms.domain.claim.payment.definition.PaymentSection;
import tavant.twms.domain.claim.payment.definition.PaymentVariableLevel;
import tavant.twms.domain.claim.payment.definition.Section;
import tavant.twms.domain.claim.payment.definition.modifiers.CriteriaBasedValue;
import tavant.twms.domain.claim.payment.definition.modifiers.PaymentModifierRepository;
import tavant.twms.domain.claim.payment.definition.modifiers.PaymentVariable;
import tavant.twms.domain.common.AdminConstants;
import tavant.twms.domain.common.GlobalConfiguration;
import tavant.twms.domain.orgmodel.DealerCriterion;
import tavant.twms.domain.orgmodel.DealerGroup;
import tavant.twms.domain.orgmodel.DealerGroupService;

import com.domainlanguage.money.Money;
import com.domainlanguage.time.CalendarDate;

public class ModifierService {
	private static Logger logger = LogManager.getLogger(ModifierService.class);
	private PaymentModifierRepository paymentModifierRepository;
    private DealerGroupService dealerGroupService;

	public void applyModifiers(Claim claim, LineItemGroup group, PaymentSection section) {
		Criteria criteria = getCriteria(claim);
		String customerType = claim.getCustomerType();
		// For the first time base amount is the previous level amount
		Money previousLevelAmount = group.getBaseAmount();
		Money stateMandateAmt = GlobalConfiguration
		.getInstance().zeroInBaseCurrency();
		Money previousLevelstateMandateAmount =GlobalConfiguration
		.getInstance().zeroInBaseCurrency();
		group.setSMandateModifierAmount(GlobalConfiguration
		.getInstance().zeroInBaseCurrency());
		Money zero = GlobalConfiguration
				.getInstance().zeroInBaseCurrency();
	/*	if(group.getName().equals(Section.LABOR)||group.getName().equals(Section.TOTAL_CLAIM))
			previousLevelstateMandateAmount=group.getGroupTotalStateMandateAmount();
		else*/
		if(group.getGroupTotalStateMandateAmount()!=null){
			previousLevelstateMandateAmount=group.getGroupTotalStateMandateAmount();
		}
		if (previousLevelAmount.breachEncapsulationOfAmount().floatValue() != new BigDecimal(
				0).floatValue()) {
			java.util.Map<Integer, List<PaymentVariableLevel>> paymentVariablesForLevels = section
					.getPaymentVariablesForLevels();
			Set<Long> paymentVariableId=new HashSet<Long>(10);
			for (Integer level : paymentVariablesForLevels.keySet()) {
				Money currentLevelModifiedAmt = GlobalConfiguration
						.getInstance().zeroInBaseCurrency();
				Money currentLevelStateMandateModifiedAmt = GlobalConfiguration
				.getInstance().zeroInBaseCurrency();
				CalendarDate repairDate = claim.getRepairDate();
				if (previousLevelAmount.breachEncapsulationOfAmount()
						.doubleValue() < 0) {
					previousLevelAmount = GlobalConfiguration.getInstance()
							.zeroInBaseCurrency();
					previousLevelstateMandateAmount= GlobalConfiguration.getInstance()
					.zeroInBaseCurrency();
				}
				for (PaymentVariableLevel paymentVariableLevel : paymentVariablesForLevels
						.get(level)) {
					PaymentVariable paymentVariable = paymentVariableLevel
							.getPaymentVariable();
					paymentVariableId.add(paymentVariable.getId());

					CriteriaBasedValue criteriaBasedValue = paymentModifierRepository
							.findModifierForClaim(claim, criteria,
									paymentVariable, repairDate, customerType);
					if (criteriaBasedValue == null) { // do further look ups for
														// parent of parent
						if (criteria.getDealerCriterion() != null
								&& criteria.getDealerCriterion()
										.getDealerGroup() != null) {
							criteria.getDealerCriterion().setDealerGroup(
									criteria.getDealerCriterion()
											.getDealerGroup().getIsPartOf());
							criteriaBasedValue = paymentModifierRepository
									.findValue(criteria, paymentVariable,
											repairDate, customerType);
						}
					}
                    criteriaBasedValue = (criteriaBasedValue!=null && criteriaBasedValue.getParent()!=null && criteriaBasedValue.getParent().isLandedCost() && claim.getWarrantyOrder())  ? null : criteriaBasedValue;
					if (criteriaBasedValue == null) {
						paymentVariableId.remove(paymentVariable.getId());
						continue;
					}
					if (logger.isDebugEnabled()) {
						logger.debug("CriteriaBasedValue:  "
								+ criteriaBasedValue + "found for Criteria"
								+ criteria + ", PaymentVariable "
								+ paymentVariable + ", repairDate "
								+ repairDate);
					}
					Double modifierPercentage = criteriaBasedValue != null ? criteriaBasedValue
							.getPercentage() : 0.0D;
					Boolean flatRate = (criteriaBasedValue != null && criteriaBasedValue
							.getIsFlatRate() != null) ? criteriaBasedValue
							.getIsFlatRate() : false;
					LineItem lineItem = group.addLineItemToAudits(
							paymentVariable, level, previousLevelAmount,
							modifierPercentage, flatRate,claim);
					
					if (logger.isDebugEnabled()) {
						logger.debug("Added Line Item " + lineItem
								+ " for Group " + group + " for Claim " + claim);
					}
					currentLevelModifiedAmt = currentLevelModifiedAmt
							.plus(lineItem.getValue());
					if(lineItem.getStateMandateAmount()!=null)
					{
					currentLevelStateMandateModifiedAmt=currentLevelStateMandateModifiedAmt
					.plus(lineItem.getStateMandateAmount());
					}

				}
				previousLevelAmount = previousLevelAmount
						.plus(currentLevelModifiedAmt);
				previousLevelstateMandateAmount=previousLevelstateMandateAmount.plus(currentLevelStateMandateModifiedAmt);
				if (logger.isDebugEnabled()) {
					logger.debug("Adjusted Payment Variables for " + group
							+ " for Claim " + claim);
				}
			}	

			Iterator<LineItem> lineItems = group.getModifiers().iterator();
			while (lineItems.hasNext()) {
				LineItem lineItem = lineItems.next(); 
				// Remove/clear modifier
				if(!paymentVariableId.contains(lineItem.getPaymentVariable().getId()))
				{
					if(lineItem.getId()==null)
					{
						lineItems.remove();
					}
					else
					{
						lineItem.clear(zero);	
					}						

				}

			}
		}	
		else
		{
			for(LineItem lineItem:group.getModifiers())
			{	
					lineItem.clear(zero);				
				
			}
		}

	
	}

	private Criteria getCriteria(Claim claim) {

		Criteria criteria = claim.getCriteriaForPayment();

		DealerCriterion dealerCriterion = criteria.getDealerCriterion();

		DealerGroup dealerGroupWithDealers = dealerGroupService
				.findGroupContainingDealership(dealerCriterion.getDealer(),
						AdminConstants.MODIFIERS_PURPOSE,
						claim.getBusinessUnitInfo());

		dealerCriterion.setDealerGroup(dealerGroupWithDealers);
		return criteria;
	}

    public void setPaymentModifierRepository(PaymentModifierRepository paymentModifierRepository) {
        this.paymentModifierRepository = paymentModifierRepository;
    }

    public void setDealerGroupService(DealerGroupService dealerGroupService) {
        this.dealerGroupService = dealerGroupService;
    }
}

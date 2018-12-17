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
package tavant.twms.domain.rules;

import com.domainlanguage.money.Money;
import com.domainlanguage.time.CalendarDate;
import com.domainlanguage.time.Duration;
import com.domainlanguage.timeutil.Clock;
import ognl.Ognl;
import ognl.OgnlException;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.BeanFactory;

import tavant.twms.domain.additionalAttributes.AdditionalAttributes;

import tavant.twms.domain.campaign.Campaign;
import tavant.twms.domain.campaign.CampaignNotification;
import tavant.twms.domain.campaign.CampaignService;
import tavant.twms.domain.campaign.HussPartsToReplace;
import tavant.twms.domain.campaign.OEMPartToReplace;
import tavant.twms.domain.catalog.CatalogService;
import tavant.twms.domain.catalog.Item;
import tavant.twms.domain.catalog.ItemGroup;
import tavant.twms.domain.catalog.ItemReference;
import tavant.twms.domain.claim.*;
import tavant.twms.domain.claim.payment.LineItemGroup;
import tavant.twms.domain.claim.payment.Payment;
import tavant.twms.domain.claim.payment.definition.Section;
import tavant.twms.domain.common.AdminConstants;
import tavant.twms.domain.common.CurrencyConversionFactor;
import tavant.twms.domain.common.CurrencyExchangeRateRepository;
import tavant.twms.domain.common.GlobalConfiguration;
import tavant.twms.domain.common.Label;
import tavant.twms.domain.configuration.ConfigName;
import tavant.twms.domain.configuration.ConfigParamService;
import tavant.twms.domain.customReports.CustomReport;
import tavant.twms.domain.customReports.CustomReportService;
import tavant.twms.domain.failurestruct.AssemblyDefinition;
import tavant.twms.domain.failurestruct.ServiceProcedure;
import tavant.twms.domain.failurestruct.ServiceProcedureDefinition;
import tavant.twms.domain.inventory.InvTransationType;
import tavant.twms.domain.inventory.InventoryItem;
import tavant.twms.domain.inventory.InventoryTransaction;
import tavant.twms.domain.orgmodel.CertificateService;
import tavant.twms.domain.orgmodel.DealerGroup;
import tavant.twms.domain.orgmodel.DealerGroupService;
import tavant.twms.domain.orgmodel.Dealership;
import tavant.twms.domain.orgmodel.OrgService;
import tavant.twms.domain.orgmodel.SeriesCertification;
import tavant.twms.domain.orgmodel.SeriesRefCertification;
import tavant.twms.domain.orgmodel.ServiceProvider;
import tavant.twms.domain.orgmodel.Supplier;
import tavant.twms.domain.orgmodel.Technician;
import tavant.twms.domain.orgmodel.TechnicianCertification;
import tavant.twms.domain.supplier.contract.Contract;
import tavant.twms.domain.supplier.contract.ContractService;
import tavant.twms.infra.HibernateCast;
import tavant.twms.infra.RegexBuilder;
import tavant.twms.security.SecurityHelper;
import tavant.twms.security.SelectedBusinessUnitsHolder;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility that serves as the context for resolving OGNL expressions to objects
 * with useful methods that augment out-of-the-box OGNL.
 * 
 * @author radhakrishnan.j
 */
@SuppressWarnings("serial")
public class OgnlContextHelper extends TreeMap<String, Object> {

	private BeanFactory beanFactory;

    private static final Logger logger = Logger
            .getLogger(OgnlContextHelper.class);

	private static final String TECHNICIAN_CERTIFIED = "Warning Message: Technician is Certified for the Claim";

	private static final String TECHNICIAN_NOTCERTIFIED = "Warning Message: Technician is Not Certified for the Claim";

	private final Pattern ognlVarPattern = new RegexBuilder().text("${")
			.startGroup().anyCharsOtherThan("}$").occurOnceOrMore().endGroup()
			.text("}$").compile();

	public OgnlContextHelper() {
		// since beanFactory is optional.
	}

	public OgnlContextHelper(BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	public void setBeanFactory(BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}
	
	
	@SuppressWarnings("unchecked")
	public Object firstEntryIfAnyNullOtherwise(Collection aCollection) {
		if (aCollection.isEmpty()) {
			return null;
		}

		return aCollection.iterator().next();
	}

	/**
	 * Assumes the collection to be homogenous.
	 * 
	 * @param aBunchOfNumbers
	 * @return
	 */
	public Long sumOfIntegers(Collection<Integer> aBunchOfNumbers) {
		long sum = 0;		
		for (Integer eachNumber : aBunchOfNumbers) 
		{
			if(eachNumber != null)
			{
				sum += eachNumber;
			}	
		}		
		return sum;
	}

	/**
	 * Assumes the collection to be homogenous.
	 * 
	 * @param aBunchOfNumbers
	 * @return
	 */
	public BigDecimal sumOfDecimals(Collection<Number> aBunchOfNumbers) {
		BigDecimal bigDecimal = new BigDecimal(0.0D, MathContext.DECIMAL32);		
		for (Number aNumber : aBunchOfNumbers) 
		{
			if(aNumber != null)
			{
				if (aNumber instanceof BigDecimal) {
					bigDecimal = bigDecimal.add((BigDecimal) aNumber);
				} else {
					bigDecimal = bigDecimal.add(new BigDecimal(aNumber
							.doubleValue(), MathContext.DECIMAL32));
				}
			}
		}	    
		return bigDecimal;
	}

	/**
	 * Returns the duration (in days) between two dates.
	 * 
	 * @param endDate
	 *            The ending date
	 * @param startDate
	 *            The starting date
	 * @return duration, in days
	 */
	public Integer duration(CalendarDate endDate, CalendarDate startDate) {
		return startDate.through(endDate).lengthInDaysInt() - 1; // Since
		// it's
		// inclusive
	}

	public boolean isWithinLast(CalendarDate dateToBeChecked,
			int durationLength, int durationType) {

		CalendarDate today = Clock.today();
		return isWithinLast(dateToBeChecked, today, durationLength,
				durationType);
	}

	public boolean isWithinLast(CalendarDate dateToBeChecked,
			CalendarDate dateToCompare, int durationLength, int durationType) {
		CalendarDate startingDate = subtractDurationFromDate(dateToCompare,
				durationLength, durationType);
		// Date shud be on or after the starting date and on or before
		// yesterday.
		return checkIfDateIsWithinInterval(dateToBeChecked, startingDate,
				dateToCompare.previousDay());
	}

	public boolean isWithinNext(CalendarDate dateToBeChecked,
			int durationLength, int durationType) {

		CalendarDate today = Clock.today();
		return isWithinNext(dateToBeChecked, today, durationLength,
				durationType);
	}

	public boolean isWithinNext(CalendarDate dateToBeChecked,
			CalendarDate dateToCompare, int durationLength, int durationType) {
		CalendarDate endingDate = addDurationToDate(dateToCompare,
				durationLength, durationType);
		// Date shud be on or before the ending date and on or after tomorrow.
		return checkIfDateIsWithinInterval(dateToBeChecked, dateToCompare
				.nextDay(), endingDate);
	}

	public boolean isDuringLast(CalendarDate dateToBeChecked,
			int durationLength, int durationType) {

		CalendarDate endingDate = Clock.today();

		return isDuringLast(dateToBeChecked, endingDate, durationLength,
				durationType);
	}

	public boolean isDuringLast(CalendarDate dateToBeChecked,
			CalendarDate dateToCompare, int durationLength, int durationType) {
		if (durationType == DateType.DurationType.WEEK.getType()) {
			do {
				dateToCompare = dateToCompare.previousDay();
			} while (dateToCompare.dayOfWeek() != Calendar.SUNDAY);
		} else if (durationType == DateType.DurationType.MONTH.getType()) {
			dateToCompare = dateToCompare.month().start().previousDay();
		}

		CalendarDate startingDate = subtractDurationFromDate(dateToCompare,
				durationLength, durationType);
		// Date shud be on or after the starting date and on or before the
		// ending date.
		return checkIfDateIsWithinInterval(dateToBeChecked, startingDate,
				dateToCompare);
	}

	public boolean isDuringNext(CalendarDate dateToBeChecked,
			int durationLength, int durationType) {

		CalendarDate startingDate = Clock.today();
		return isDuringNext(dateToBeChecked, startingDate, durationLength,
				durationType);
	}

	public boolean isDuringNext(CalendarDate dateToBeChecked,
			CalendarDate dateToCompare, int durationLength, int durationType) {
		if (durationType == DateType.DurationType.WEEK.getType()) {
			do {
				dateToCompare = dateToCompare.nextDay();
			} while (dateToCompare.dayOfWeek() != Calendar.MONDAY);
		} else if (durationType == DateType.DurationType.MONTH.getType()) {
			dateToCompare = dateToCompare.month().end().nextDay();
		}

		CalendarDate endingDate = addDurationToDate(dateToCompare,
				durationLength, durationType);
		// Date shud be on or after the starting date and on or before the
		// ending date.
		return checkIfDateIsWithinInterval(dateToBeChecked, dateToCompare,
				endingDate);
	}

	private boolean checkIfDateIsWithinInterval(CalendarDate dateToBeChecked,
			CalendarDate startingDate, CalendarDate endingDate) {
		return !dateToBeChecked.isBefore(startingDate)
				&& !dateToBeChecked.isAfter(endingDate);
	}

	public boolean executeQuery(String query) {

		try {
			String replacedQuery = replaceOgnlVariables(query);
			return getDomainRuleRepository().isQuerySatisfied(replacedQuery);
		} catch (OgnlException e) {
			throw new RuntimeException(
					"Exception while replacing Ognl variables in query ["
							+ query + "].", e);
		}
	}

	protected String replaceOgnlVariables(String query) throws OgnlException {

		Matcher matcher = ognlVarPattern.matcher(query);

		int startIndex = 0;

		StringBuffer replacedQuery = new StringBuffer(query.length());

		while (matcher.find()) {
			replacedQuery.append(query.substring(startIndex, matcher.start()));
			Object value = Ognl.getValue(matcher.group(1), this);
			if (value instanceof List) {
				replacedQuery.append(getValuesFromList((List) value));
			} else {
				replacedQuery.append(value);
			}
			startIndex = matcher.end();
		}

		replacedQuery.append(query.substring(startIndex));

		return replacedQuery.toString();
	}

	private DomainRuleRepository getDomainRuleRepository() {
		return (DomainRuleRepository) beanFactory.getBean(
				"domainRuleRepository", DomainRuleRepository.class);
	}
	
	private CampaignService getCampaignService() {
		return (CampaignService) beanFactory.getBean(
				"campaignService", CampaignService.class);
	}
	
	private SecurityHelper getSecurityHelper() {
		return (SecurityHelper) beanFactory.getBean(
				"securityHelper", SecurityHelper.class);
	}

	public CalendarDate addDurationToDate(CalendarDate date,
			int durationLength, int durationType) {
		return getDurationForLengthAndType(durationLength, durationType)
				.addedTo(date);
	}

	public CalendarDate subtractDurationFromDate(CalendarDate date,
			int durationLength, int durationType) {
		return getDurationForLengthAndType(durationLength, durationType)
				.subtractedFrom(date);
	}

	protected Duration getDurationForLengthAndType(int durationLength,
			int durationType) {
		return DateType.DurationType.getDurationForTypeAndLength(
				durationLength, durationType);
	}

	public boolean dateGreaterBy(CalendarDate dateToBeChecked,
			CalendarDate dateToCompare, int durationLength, int durationType) {
		CalendarDate endingDate = addDurationToDate(dateToCompare,
				durationLength, durationType);
		return !checkIfDateIsWithinInterval(dateToBeChecked, dateToCompare,
				endingDate)
				&& !dateToBeChecked.isBefore(dateToCompare);
	}

        public boolean dateLesserBy(CalendarDate dateToBeChecked,
			CalendarDate dateToCompare, int durationLength, int durationType) {
		CalendarDate endingDate = addDurationToDate(dateToBeChecked,
				durationLength, durationType);
		return !checkIfDateIsWithinInterval(dateToBeChecked, dateToCompare,
				endingDate)
				&& !dateToBeChecked.isAfter(subtractDurationFromDate(
						dateToCompare, durationLength, durationType));
	}

	public Integer averageSpeed(TravelDetail travelDetail) {
		Integer avgSpeed = new Integer(0);
		if (travelDetail != null) {
			BigDecimal distance = travelDetail.getDistance();
			BigDecimal hours = BigDecimal.ZERO;
			if(!StringUtils.isEmpty(travelDetail.getHours()))
				hours=new BigDecimal(travelDetail.getHours());
			BigDecimal zero = new BigDecimal(0);
			if (!distance.equals(zero) && !hours.equals(zero)) {
				avgSpeed = distance.divide(hours).intValue();
			}
		}
		return avgSpeed;
	}

	public Money totalClaimedAmtExcludingTax(Payment payment) {
		Money totalClaimAmt = null;
		Money totalTaxOnClm = null;
		if (payment != null) {
			if (payment.getTotalAmount() != null) {
				totalClaimAmt = payment.getTotalAmount();

				if (payment.getActiveCreditMemo() != null) {
					totalTaxOnClm = payment.getActiveCreditMemo()
							.getTaxAmount();
					if (totalTaxOnClm != null) {
						totalClaimAmt.minus(totalTaxOnClm);
					}
				}
			}
		}
		return totalClaimAmt;
	}
	
	public Money totalCurrentClaimAmount(Payment payment) {
		Money totalClaimedAmt = GlobalConfiguration.getInstance().zeroInBaseCurrency();
		if (payment != null && payment.getLineItemGroup(Section.TOTAL_CLAIM)!= null) {
			LineItemGroup lineItemGroupForTotalAmount = payment.getLineItemGroup(Section.TOTAL_CLAIM);
			List<LineItemGroup> lineItemGroups = payment.getLineItemGroups();
			Currency currency = lineItemGroupForTotalAmount.getAcceptedTotal().breachEncapsulationOfCurrency();
			totalClaimedAmt = Money.valueOf(0, currency);
			for (LineItemGroup lineItemGroup : lineItemGroups) {
				if (!lineItemGroup.getName().equalsIgnoreCase(Section.TOTAL_CLAIM)) {
		            if(lineItemGroup.getGroupTotal()!=null){
		            	totalClaimedAmt = totalClaimedAmt.plus(lineItemGroup.getGroupTotal());
		            }
				}
			}
		}
		return totalClaimedAmt;
	}
	
	public Money totalClaimCostAmount(Payment payment) {
		Money totalClaimedAmt = GlobalConfiguration.getInstance().zeroInBaseCurrency();
		if (payment != null && payment.getLineItemGroup(Section.TOTAL_CLAIM)!= null) {
			LineItemGroup lineItemGroupForTotalAmount = payment.getLineItemGroup(Section.TOTAL_CLAIM);
			List<LineItemGroup> lineItemGroups = payment.getLineItemGroups();
			Currency currency = lineItemGroupForTotalAmount.getAcceptedTotal().breachEncapsulationOfCurrency();
			totalClaimedAmt = Money.valueOf(0, currency);
			for (LineItemGroup lineItemGroup : lineItemGroups) {
				if (!lineItemGroup.getName().equalsIgnoreCase(Section.TOTAL_CLAIM)){
					if(lineItemGroup.getName().equalsIgnoreCase(Section.OEM_PARTS)){
						if(lineItemGroup.getAcceptedTotal()!=null){
			            	totalClaimedAmt = totalClaimedAmt.plus(lineItemGroup.getAcceptedCpTotal());
			            }
					}else{
			            if(lineItemGroup.getAcceptedTotal()!=null){
			            	totalClaimedAmt = totalClaimedAmt.plus(lineItemGroup.
			            			getAcceptedTotal());
			            }
					}
				}
			}
		}
		return totalClaimedAmt;
	}
	
	public Money totalLaborAmount(Payment payment) {
        return getTotalAcceptedAmountForGroup(payment, Section.LABOR);
	}
	
	public Money laborAmount(Payment payment) {
		return getTotalGroupAmountForGroup(payment, Section.LABOR);
	}
	
	public Money travelByDistanceAmount(Payment payment) {
		return getTotalGroupAmountForGroup(payment, Section.TRAVEL_BY_DISTANCE);
	}
	
	public Money travelByTripAmount(Payment payment) {
		return getTotalGroupAmountForGroup(payment, Section.TRAVEL_BY_TRIP);
	}
	
	public Money travelByHoursAmount(Payment payment) {
		return getTotalGroupAmountForGroup(payment, Section.TRAVEL_BY_HOURS);
	}
	
	public Money totalTravelAmount(Payment payment) {
		return getTotalAcceptedAmountForGroupList(payment,
                Arrays.asList(Section.TRAVEL_BY_HOURS, Section.TRAVEL_BY_TRIP, Section.TRAVEL_BY_DISTANCE));
	}
	
	public Money oemPartsAmount(Payment payment) {
		return getTotalGroupAmountForGroup(payment, Section.OEM_PARTS);
	}
	
	public Money totalOemPartsAmount(Payment payment) {
		return getTotalAcceptedAmountForGroup(payment, Section.OEM_PARTS);
	}
	
	public Money totalNonOemPartsAmount(Payment payment) {
		return getTotalAcceptedAmountForGroup(payment, Section.NON_OEM_PARTS);
	}

	public Money itemFreightDutyAmount(Payment payment) {
		return getTotalGroupAmountForGroup(payment, Section.ITEM_FREIGHT_DUTY);
	}
	
	public Money perDiemAmount(Payment payment) {
		return getTotalGroupAmountForGroup(payment, Section.PER_DIEM);
	}
	
	public Money mealsAmount(Payment payment) {
		return getTotalGroupAmountForGroup(payment, Section.MEALS);
	}

    public Money totalMiscellaneousAmount(Payment payment) {
        return getTotalAcceptedAmountForGroupList(payment,
                Arrays.asList(Section.ITEM_FREIGHT_DUTY, Section.MEALS, Section.MEALS, Section.LOCAL_PURCHASE,
                        Section.OTHER_FREIGHT_DUTY, Section.PER_DIEM, Section.RENTAL_CHARGES, Section.TOLLS,
                        Section.OTHERS));
    }

    protected Money getTotalAcceptedAmountForGroupList(
            Payment payment, List<String> groupSectionNames) {
        return getTotalAcceptedOrGroupAmountForGroupList(payment,
                groupSectionNames, true);
    }

    protected Money getTotalGroupAmountForGroupList(
            Payment payment, List<String> groupSectionNames) {
        return getTotalAcceptedOrGroupAmountForGroupList(payment,
                groupSectionNames, false);
    }

    protected Money getTotalAcceptedOrGroupAmountForGroupList(
            Payment payment, List<String> groupSectionNames,
            boolean isAcceptedTotal) {
        Money totalAmount = GlobalConfiguration.getInstance().zeroInBaseCurrency();

        for (String groupSectionName : groupSectionNames) {
            Money totalForSection = getTotalAcceptedOrGroupAmountForGroup(payment,
                                                                  groupSectionName, isAcceptedTotal);
            totalAmount = addInNonDollarCurrencyIfRequired(totalAmount,
                    totalForSection);
        }
        return totalAmount;
    }

    protected Money getTotalAcceptedAmountForGroup(Payment payment, String groupSectionName) {
        return getTotalAcceptedOrGroupAmountForGroup(payment,
                groupSectionName, true);
    }

    protected Money getTotalGroupAmountForGroup(Payment payment, String groupSectionName) {
        return getTotalAcceptedOrGroupAmountForGroup(payment,
                groupSectionName, false);
    }

    protected Money getTotalAcceptedOrGroupAmountForGroup(Payment payment,
                                                          String groupSectionName, boolean isAcceptedTotal) {
        Money amtToReturn = Money.dollars(0);
        if (payment != null && payment.getLineItemGroup(groupSectionName) != null) {
            LineItemGroup lineItemGroup = payment.getLineItemGroup(groupSectionName);
            if (lineItemGroup != null) {
                amtToReturn = isAcceptedTotal ? lineItemGroup.getAcceptedTotal() : lineItemGroup.getGroupTotal();
            }
        }
        //TKTSA-1084 Return null if amount is 0.00
        if(amtToReturn.isZero())
            return null ;      
         else
         	return amtToReturn; 
    }

	public List<String> executeDuplicateClaimsQuery(String query) {

		try {
			String replacedQuery = replaceOgnlVariables(query);
			return getDomainRuleRepository().executeDuplicateClaimsQuery(
					replacedQuery);
		} catch (OgnlException e) {
			throw new RuntimeException(
					"Exception while replacing Ognl variables in query ["
							+ query + "].", e);
		}
	}

	

	public int maximumValue(List<Integer> values) {
		int maxValue = Integer.MIN_VALUE;
		for (Integer val : values) {
			if (val > maxValue) {
				maxValue = val;
			}
		}
		return maxValue;
	}

	public int minimumValue(List<Integer> values) {
		int minValue = Integer.MAX_VALUE;
		for (Integer val : values) {
			if (val < minValue) {
				minValue = val;
			}
		}
		return minValue;
	}

	public String getValuesFromList(List values) {
		StringBuffer returnStrBuffer = new StringBuffer(100);
		Object object = null;
		if (values != null && values.size() > 0) {
			object = values.iterator().next();
		}
		
		boolean isStringVal = (object instanceof String);
		String elementPrefix = isStringVal ?  "'" : "";
		String elementSuffix = isStringVal ? "'," : ",";
		
		returnStrBuffer.append(elementPrefix);
		for (Object businessObject : values) {
			returnStrBuffer.append(businessObject);
			returnStrBuffer.append(elementSuffix);
		}
			
		return returnStrBuffer.substring(0, returnStrBuffer.length() - 1);
	}

	public String getDuplicateClaimedItems(List<ClaimedItem> claimedItems) {
		StringBuffer duplicateClaimNoStr = new StringBuffer(100);
		duplicateClaimNoStr.append("claim.id in (");
		String dupClaimsId = "";
		for (ClaimedItem claimedItem : claimedItems) {
			ItemReference itemReference = claimedItem.getItemReference();
			if (itemReference != null
					&& itemReference.getReferredInventoryItem() != null) {
				String serialNumber = claimedItem.getItemReference()
						.getReferredInventoryItem().getSerialNumber();
				BigDecimal hoursInService = claimedItem.getHoursInService();
				Collection<Claim> dupClaims = getClaimRepository()
						.findAllPreviousClaimsForClaimedItems(serialNumber,
								hoursInService.doubleValue());
				for (Claim dupClaim : dupClaims) {
					dupClaimsId = dupClaimsId + dupClaim.getId() + ",";
				}
			}
			if (!"".equals(dupClaimsId)) {
				dupClaimsId = dupClaimsId
						.substring(0, dupClaimsId.length() - 1);
			}
		}
		duplicateClaimNoStr.append(dupClaimsId);
		duplicateClaimNoStr.append(")");
		return duplicateClaimNoStr.toString();
	}

	private ClaimRepository getClaimRepository() {
		return (ClaimRepository) beanFactory.getBean("claimRepository",
				ClaimRepository.class);
	}

	public boolean isWithinLast(Collection<CalendarDate> dateToBeCheckedCol,
			int durationLength, int durationType, boolean forEach) {
		boolean isWithinLastBoolean = false;
		CalendarDate today = Clock.today();
		for (CalendarDate dateToBeChecked : dateToBeCheckedCol) {
			isWithinLastBoolean = isWithinLast(dateToBeChecked, today,
					durationLength, durationType);
			if (forEach && !isWithinLastBoolean) {
				return false;
			}
			if (!forEach && isWithinLastBoolean) {
				return true;
			}
		}
		return isWithinLastBoolean;
	}

	public boolean isWithinNext(Collection<CalendarDate> dateToBeCheckedCol,
			int durationLength, int durationType, boolean forEach) {
		boolean isWithinNextBoolean = false;
		CalendarDate today = Clock.today();
		for (CalendarDate dateToBeChecked : dateToBeCheckedCol) {
			isWithinNextBoolean = isWithinNext(dateToBeChecked, today,
					durationLength, durationType);
			if (forEach && !isWithinNextBoolean) {
				return false;
			}
			if (!forEach && isWithinNextBoolean) {
				return true;
			}
		}
		return isWithinNextBoolean;
	}

	public boolean isDuringLast(Collection<CalendarDate> dateToBeCheckedCol,
			int durationLength, int durationType, boolean forEach) {
		boolean isDuringLastBoolean = false;
		CalendarDate endingDate = Clock.today();
		for (CalendarDate dateToBeChecked : dateToBeCheckedCol) {
			isDuringLastBoolean = isDuringLast(dateToBeChecked, endingDate,
					durationLength, durationType);
			if (forEach && !isDuringLastBoolean) {
				return false;
			}
			if (!forEach && isDuringLastBoolean) {
				return true;
			}
		}
		return isDuringLastBoolean;
	}

	public boolean isDuringNext(Collection<CalendarDate> dateToBeCheckedCol,
			int durationLength, int durationType, boolean forEach) {
		boolean isDuringNextBoolean = false;
		CalendarDate startingDate = Clock.today();
		for (CalendarDate dateToBeChecked : dateToBeCheckedCol) {
			isDuringNextBoolean = isDuringNext(dateToBeChecked, startingDate,
					durationLength, durationType);
			if (forEach && !isDuringNextBoolean) {
				return false;
			}
			if (!forEach && isDuringNextBoolean) {
				return true;
			}
		}
		return isDuringNextBoolean;
	}

	public boolean dateGreaterBy(Collection<CalendarDate> dateToBeCheckedCol,
			CalendarDate dateToCompare, int durationLength, int durationType,
			boolean forEach) {

		boolean isDateGreaterBy = false;
		for (CalendarDate dateToBeChecked : dateToBeCheckedCol) {
			isDateGreaterBy = dateGreaterBy(dateToBeChecked, dateToCompare,
					durationLength, durationType);
			if (forEach && !isDateGreaterBy) {
				return false;
			}
			if (!forEach && isDateGreaterBy) {

				return true;
			}
		}
		return isDateGreaterBy;
	}

	public boolean dateLesserBy(Collection<CalendarDate> dateToBeCheckedCol,
			CalendarDate dateToCompare, int durationLength, int durationType,
			boolean forEach) {
		boolean isDateLesserBy = false;
		for (CalendarDate dateToBeChecked : dateToBeCheckedCol) {
			isDateLesserBy = dateLesserBy(dateToBeChecked, dateToCompare,
					durationLength, durationType);
			if (forEach && !isDateLesserBy) {
				return false;
			}
			if (!forEach && isDateLesserBy) {
				return true;
			}
		}
		return isDateLesserBy;
	}

	public boolean dateGreaterBy(Collection<CalendarDate> dateToBeCheckedCol,
			Collection<CalendarDate> dateToCompareCol, int durationLength,
			int durationType, boolean forEach) {

		boolean isDateGreaterBy = false;
		Object[] dateToCompareArray = dateToCompareCol.toArray();
		int index = 0;
		for (CalendarDate dateToBeChecked : dateToBeCheckedCol) {
			CalendarDate dateToCompare = (CalendarDate) dateToCompareArray[index];
			isDateGreaterBy = dateGreaterBy(dateToBeChecked, dateToCompare,
					durationLength, durationType);
			if (forEach && !isDateGreaterBy) {
				return false;
			}
			if (!forEach && isDateGreaterBy) {

				return true;
			}
			index++;
		}
		return isDateGreaterBy;
	}

	public boolean dateLesserBy(Collection<CalendarDate> dateToBeCheckedCol,
			Collection<CalendarDate> dateToCompareCol, int durationLength,
			int durationType, boolean forEach) {
		boolean isDateLesserBy = false;
		Object[] dateToCompareArray = dateToCompareCol.toArray();
		int index = 0;
		for (CalendarDate dateToBeChecked : dateToBeCheckedCol) {
			CalendarDate dateToCompare = (CalendarDate) dateToCompareArray[index];
			isDateLesserBy = dateLesserBy(dateToBeChecked, dateToCompare,
					durationLength, durationType);
			if (forEach && !isDateLesserBy) {
				return false;
			}
			if (!forEach && isDateLesserBy) {
				return true;
			}
			index++;
		}
		return isDateLesserBy;
	}

	public boolean isManualReviewRequiredForSupplierContract(Claim claim) {
		Item causalPart = claim.getServiceInformation().getCausalPart();
		if (causalPart == null) {
			return false;
		}
		SelectedBusinessUnitsHolder.setSelectedBusinessUnit(claim.getBusinessUnitInfo().getName());
		List<Contract> contracts = getContractService().findContract(claim,causalPart, true);
		if (contracts == null || contracts.isEmpty()) {
			return false;
		} else if (contracts.size() > 1) {
			return true;
		}
		return false;
	}

	private ContractService getContractService() {
		return (ContractService) beanFactory.getBean("contractService",
				ContractService.class);
	}
	
	public Integer numberOfHussmannInstalledParts(List<HussmanPartsReplacedInstalled> 
								hussmanPartsReplacedInstalled){
		int numberOfHussmannInstalledParts = 0;
		if(hussmanPartsReplacedInstalled != null && !hussmanPartsReplacedInstalled.isEmpty()){
			for(HussmanPartsReplacedInstalled hprt : hussmanPartsReplacedInstalled){
				if(hprt!=null && hprt.getHussmanInstalledParts() != null && 
						!hprt.getHussmanInstalledParts().isEmpty()){
					numberOfHussmannInstalledParts = numberOfHussmannInstalledParts + 
											hprt.getHussmanInstalledParts().size();
				}
			}
			
		}
		return new Integer(numberOfHussmannInstalledParts);
	}
	
	public Integer numberOfReplacedParts(List<HussmanPartsReplacedInstalled> 
											hussmanPartsReplacedInstalled){
		int numberOfReplacedParts = 0;
		if(hussmanPartsReplacedInstalled != null && !hussmanPartsReplacedInstalled.isEmpty()){
			for(HussmanPartsReplacedInstalled hprt : hussmanPartsReplacedInstalled){
				if(hprt!=null && hprt.getReplacedParts() != null && 
						!hprt.getReplacedParts().isEmpty()){
					numberOfReplacedParts = numberOfReplacedParts + hprt.getReplacedParts().size();
				}
			}

		}
		return new Integer(numberOfReplacedParts);
	}

	public Integer numberOfNonHussmanInstalledParts(List<HussmanPartsReplacedInstalled> 
													hussmanPartsReplacedInstalled){
		int numberOfNonHussmanInstalledParts = 0;
		if(hussmanPartsReplacedInstalled != null && !hussmanPartsReplacedInstalled.isEmpty()){
			for(HussmanPartsReplacedInstalled hprt : hussmanPartsReplacedInstalled){
				if(hprt!=null && hprt.getNonHussmanInstalledParts() != null && 
						!hprt.getNonHussmanInstalledParts().isEmpty()){
					numberOfNonHussmanInstalledParts = numberOfNonHussmanInstalledParts + 
											hprt.getNonHussmanInstalledParts().size();
				}
			}

		}
		return new Integer(numberOfNonHussmanInstalledParts);
	}
	
	public Integer sumOfHussmannInstalledPartsQuantity(List<HussmanPartsReplacedInstalled> 
												hussmanPartsReplacedInstalled){
		int sumOfHussmannInstalledParts = 0;
		if(hussmanPartsReplacedInstalled != null && !hussmanPartsReplacedInstalled.isEmpty()){
			for(HussmanPartsReplacedInstalled hprt : hussmanPartsReplacedInstalled){
				if(hprt!=null && hprt.getHussmanInstalledParts() != null && 
						!hprt.getHussmanInstalledParts().isEmpty()){
					for(InstalledParts installedPart : hprt.getHussmanInstalledParts()){
						if(installedPart.getNumberOfUnits() != null){
							sumOfHussmannInstalledParts = sumOfHussmannInstalledParts + 
									installedPart.getNumberOfUnits().intValue();
						}
					}
				}
			}
		}
		return new Integer(sumOfHussmannInstalledParts);
	}

	public Integer sumOfHussmannReplacedPartsQuantity(List<HussmanPartsReplacedInstalled> 
											hussmanPartsReplacedInstalled){
		int sumOfReplacedParts = 0;
		if(hussmanPartsReplacedInstalled != null && !hussmanPartsReplacedInstalled.isEmpty()){
			for(HussmanPartsReplacedInstalled hprt : hussmanPartsReplacedInstalled){
				if(hprt!=null && hprt.getReplacedParts() != null && 
						!hprt.getReplacedParts().isEmpty()){
					for(OEMPartReplaced oemPartReplaced : hprt.getReplacedParts()){
						if(oemPartReplaced.getNumberOfUnits() !=null){
							sumOfReplacedParts = sumOfReplacedParts + oemPartReplaced.getNumberOfUnits();
						}
					}
				}
			}
		}
		return new Integer(sumOfReplacedParts);
	}
	
	/**
	 * 
	 * @param hussmanPartsReplacedInstalled
	 * @return Sum of quantity of each non hussmann part installed item
	 */
	public Integer sumOfNONHussmannPartsInstalledQuantity(List<HussmanPartsReplacedInstalled> 
														hussmanPartsReplacedInstalled){
		int sumOfNonHussmanInstalledParts = 0;
		if(hussmanPartsReplacedInstalled != null && !hussmanPartsReplacedInstalled.isEmpty()){
			for(HussmanPartsReplacedInstalled hprt : hussmanPartsReplacedInstalled){
				if(hprt!=null && hprt.getNonHussmanInstalledParts() != null && 
						!hprt.getNonHussmanInstalledParts().isEmpty()){
					for(InstalledParts installedParts : hprt.getNonHussmanInstalledParts()){
						if(installedParts.getNumberOfUnits() != null){
							sumOfNonHussmanInstalledParts = sumOfNonHussmanInstalledParts 
											+ installedParts.getNumberOfUnits();
						}
					}
				}
			}
		}
		return new Integer(sumOfNonHussmanInstalledParts);
	}
	
	public List<InstalledParts> installedParts(List<HussmanPartsReplacedInstalled> 
										hussmanPartsReplacedInstalled){
		List<InstalledParts> installedParts = new ArrayList<InstalledParts>();
		if(hussmanPartsReplacedInstalled != null && !hussmanPartsReplacedInstalled.isEmpty()){
			for(HussmanPartsReplacedInstalled hprt : hussmanPartsReplacedInstalled){
				if(hprt!=null && hprt.getHussmanInstalledParts() != null && 
						!hprt.getHussmanInstalledParts().isEmpty()){
					return hprt.getHussmanInstalledParts();
				}
			}
			
		}
		return installedParts;
		
	}
	
	public boolean isMultiJobCodeSameSubComponent(Claim claim){
		boolean isMultiJobCodeSameSubComponent = false;
		
		if(claim != null && claim.getServiceInformation() != null 
				&& claim.getServiceInformation().getServiceDetail() != null
				&& claim.getServiceInformation().getServiceDetail().getLaborPerformed() != null
				&& claim.getServiceInformation().getServiceDetail().getLaborPerformed().size() > 1){
			int numberOfSubComponent = 0;
			Set<String> jobCodes = new HashSet<String>();
			for(LaborDetail laborDetail : claim.getServiceInformation().getServiceDetail().getLaborPerformed()){
				if(laborDetail.getServiceProcedure() != null){
					ServiceProcedure serviceProcedure = laborDetail.getServiceProcedure();
					ServiceProcedureDefinition serviceProcedureDefinition = serviceProcedure.getDefinition();
					AssemblyDefinition assemblyDefinition = serviceProcedureDefinition.getLastComponent();
					if(assemblyDefinition.getAssemblyLevel().getLevel()==4){
						numberOfSubComponent++;
						String delimiter = "-";
						int index = StringUtils.lastIndexOf(serviceProcedureDefinition.getCode(), delimiter);
						if(index > 0)
							jobCodes.add(StringUtils.substring(serviceProcedureDefinition.getCode(),0,index));
					}
				}
			}
			//If there are any duplicate actions within same sub component
			if(numberOfSubComponent > jobCodes.size()){
				isMultiJobCodeSameSubComponent = true;
			}
		}
		return isMultiJobCodeSameSubComponent;
	}
	
	public boolean isOwnedByFilingUser(Claim claim){
		boolean isOwnedByFilingUser = false;
		if(claim != null && claim.getClaimedItems() != null && !claim.getClaimedItems().isEmpty()){
			for(ClaimedItem claimedItem : claim.getClaimedItems()){
				if(claimedItem.getItemReference()!= null && claimedItem.getItemReference().isSerialized()){
					if(claimedItem.getItemReference().getReferredInventoryItem() != null
						&& (claimedItem.getItemReference().getReferredInventoryItem().getCurrentOwner().getId()
						.longValue()== claim.getForDealer().getId().longValue() )){
						isOwnedByFilingUser = true;
					}else{
						isOwnedByFilingUser = false;
					}
				}
			}
		}
		return isOwnedByFilingUser;
	}
		
	
	
	public boolean isClaimingDealerInActive(Claim claim) {
		boolean isClaimingDealerInActive = false;
		if (claim != null) {
			if (claim.getForDealer().getStatus().equalsIgnoreCase("INACTIVE")) {
				isClaimingDealerInActive = true;
			} else {
				isClaimingDealerInActive = false;
			}
		}
		return isClaimingDealerInActive;
	}
	
	public String anyPendingFieldModifications(Claim claim){
		Set<String> pendingFieldModificationClasses = new HashSet<String>();
		if(claim.getClaimedItems()!=null){
			for(ClaimedItem claimedItem : claim.getClaimedItems()){
				if(claimedItem.getItemReference().isSerialized()){
					InventoryItem inventoryItem = claimedItem.getItemReference().getReferredInventoryItem();
					if(inventoryItem.getSerialNumber() != null){
						List<Campaign> fieldMods = getCampaignService().getCampaignsForInventoryItemById(
								inventoryItem.getSerialNumber());
						if(fieldMods != null && !fieldMods.isEmpty()){
							for(Campaign campaign : fieldMods){
								pendingFieldModificationClasses.add(campaign.getCampaignClass().getCode());
							}
						}
					}
				}
			}
		}
		String allPendingFieldModifications = null;
		for(String campaignclass : pendingFieldModificationClasses){
			if(allPendingFieldModifications==null){
				allPendingFieldModifications = campaignclass;
			}else{
				allPendingFieldModifications = allPendingFieldModifications + "~" + campaignclass;
			}
		}
		return allPendingFieldModifications;
	}

	private CustomReportService getCustomReportService() {
		return (CustomReportService) beanFactory.getBean("customReportService",
				CustomReportService.class);
	}
	
	public boolean isACRPublished(Claim claim) {
		ClaimedItem claimedItem = claim.getClaimedItems().get(0);
		boolean isPublished = false;
		if(claimedItem != null && claimedItem.getItemReference() != null && 
				claimedItem.getItemReference().getReferredInventoryItem() != null) {
			List<CustomReport> reports = getCustomReportService().findReportsForInventory(
											claimedItem.getItemReference().getReferredInventoryItem());
			if (reports != null && !reports.isEmpty()) {
				for(CustomReport report : reports)
					if("ACR".equalsIgnoreCase(report.getReportType().getCode())) {
						isPublished = true;
						break;
					}
			}
		}
		return isPublished;
	}
	
    public Object get(Object key) {
        Object value = super.get(key);
        return value == null ? beanFactory.getBean(key.toString()) : value;
    }

	public boolean isJobCodeAlreadyIncluded(Claim claim) {		
		if (claim != null && claim.getServiceInformation() != null
				&& claim.getServiceInformation().getServiceDetail() != null
				&& claim.getServiceInformation().getServiceDetail().getLaborPerformed() != null) {
			for (LaborDetail laborDetail : claim.getServiceInformation().getServiceDetail().getLaborPerformed()) {
				if (laborDetail.getServiceProcedure() != null
						&& !laborDetail.getServiceProcedure().getDefinition().getChildJobs().isEmpty()) {
					for (LaborDetail lbrDetail : claim.getServiceInformation().getServiceDetail().getLaborPerformed()) {
						if (laborDetail.getServiceProcedure().getDefinition().getChildJobs().contains(
								lbrDetail.getServiceProcedure().getDefinition())) {
							return true;
						} 
					}
				}
			}
		}
		return false;
	}

    
    private Money addInNonDollarCurrencyIfRequired(Money baseMoney, Money toBeAddedMoney) {
        baseMoney = (baseMoney == null) ?
            GlobalConfiguration.getInstance().zeroInBaseCurrency() : baseMoney;
        toBeAddedMoney = (toBeAddedMoney == null) ?
            GlobalConfiguration.getInstance().zeroInBaseCurrency() : toBeAddedMoney;

        Currency targetCurrency = baseMoney.breachEncapsulationOfCurrency();
        if(targetCurrency.equals(Currency.getInstance("USD"))) {
            targetCurrency = toBeAddedMoney.breachEncapsulationOfCurrency();
        }

        final BigDecimal addedUpAmount =
                baseMoney.breachEncapsulationOfAmount().add(
                    toBeAddedMoney.breachEncapsulationOfAmount());

        return Money.valueOf(addedUpAmount, targetCurrency);
    }

    public boolean isPartsClaimWithoutHost(Claim claim){
    	boolean isPartsClaimWithoutHost = false;
    	if(ClaimType.PARTS.getType().equalsIgnoreCase(claim.getType().getType())){
    		PartsClaim partsClaim = new HibernateCast<PartsClaim>().cast(claim);
    		if(!partsClaim.getPartInstalled().booleanValue()){
    			isPartsClaimWithoutHost=true;
    		}
    	}
    	return isPartsClaimWithoutHost;
    }
    
    public boolean isPartsClaimOnCompetitorModel(Claim claim){
    	boolean isPartsClaimOnCompetitorModel = false;
    	if(ClaimType.PARTS.getType().equalsIgnoreCase(claim.getType().getType())){
    		PartsClaim partsClaim = new HibernateCast<PartsClaim>().cast(claim);
    		if(partsClaim.getPartInstalled().booleanValue() && (partsClaim.getCompetitorModelBrand()!=null && !partsClaim.getCompetitorModelBrand().isEmpty()
					&& !partsClaim.getCompetitorModelDescription().isEmpty() && !partsClaim
					.getCompetitorModelTruckSerialnumber().isEmpty())){
    			isPartsClaimOnCompetitorModel=true;
    		}
    	}
    	return isPartsClaimOnCompetitorModel;
    }

    //This api has a dependancy on BU Config for ERP Currency
    public boolean isExchangeRateSetupForRepairDate(Claim claim) {
        SelectedBusinessUnitsHolder.setSelectedBusinessUnit(claim.getBusinessUnitInfo().getName());
        GlobalConfiguration.getInstance().setClaimCurrency(claim.getCurrencyForCalculation());
        String erpCurrency = "";
        List<Object> configValues = getConfigParamService().getListofObjects(ConfigName.ERP_CURRENCY.getName());
        if (configValues == null || configValues.get(0) == null || !(configValues.get(0) instanceof String)) {
            logger.error("Could not fetch erpCurrency BU config for claim " + claim.getClaimNumber() +
                        ". There will be no currency conversion");
        } else {
            erpCurrency = (String) configValues.get(0);
            logger.debug("erpCurrency is " + erpCurrency + " for BU " + claim.getBusinessUnitInfo().getName());
        }

        if (claim.getCurrencyForCalculation().getCurrencyCode().equalsIgnoreCase(erpCurrency) ||
                "dealersCurrency".equalsIgnoreCase(erpCurrency)) {
            return true;
        }
        Currency naturalCurrency = Currency.getInstance(erpCurrency.toUpperCase());
        Currency baseCurrency = GlobalConfiguration.getInstance().getBaseCurrency();
        CurrencyConversionFactor conversionFactorNat2Base =getCurrencyExchangeRateRepository()
                                        .findConversionFactor(naturalCurrency, baseCurrency, claim.getRepairDate());
        if (conversionFactorNat2Base == null) {
            return false;
        } else {
            //assuming we have only usd and eur as options in bu config
            //and same are present in currencyexchangerate sync
            return true;
        }
    }
    
   
			
	
	public boolean isCausalPartModelSameAsInventoryItemModel(Claim claim) {
		boolean causalPartModelDifferentFromInventoryItemModel = false;
		ServiceInformation serviceInformation = claim.getServiceInformation();
		ItemGroup causalPartModel = new ItemGroup();
		if(serviceInformation!=null && serviceInformation.getCausalPart()!=null && serviceInformation.getCausalPart().getModel()!=null ){
			causalPartModel = serviceInformation.getCausalPart().getModel();	
		}else {
			return causalPartModelDifferentFromInventoryItemModel;
		}
		for (ClaimedItem claimedItem : claim.getClaimedItems()) {
			if (claimedItem.getItemReference() != null) {
					if(claimedItem.getItemReference().getModel().getGroupCode().equalsIgnoreCase(causalPartModel.getGroupCode())){
						return true;	
					}
				}
		}
		return causalPartModelDifferentFromInventoryItemModel;
	}
	
	public boolean isReplacedPartModelSameAsInventoryItemModel(Claim claim) {
		boolean replacedPartModelDifferentFromInventoryItemModel = false;
		ServiceInformation serviceInformation = claim.getServiceInformation();
		ServiceDetail serviceDetail = serviceInformation.getServiceDetail();
		List<OEMPartReplaced> partsReplaced = serviceDetail.getReplacedParts();
		for (OEMPartReplaced part : partsReplaced) {
			ItemGroup replacedItemModel = part.getItemReference().getModel();
			for (ClaimedItem claimedItem : claim.getClaimedItems()) {
				if (claimedItem.getItemReference() != null) {
						if(claimedItem.getItemReference().getModel().getGroupCode().equalsIgnoreCase(replacedItemModel.getGroupCode())){
							return true;	
						}
					}
			}
		}
		return replacedPartModelDifferentFromInventoryItemModel;
	}
	
	public boolean isMultipleSuppliersAssociatedWithClaim(Claim claim){
		boolean multipleSuppliersAssociatedWithClaim = false;
		List<Contract> applicableContracts =null;
		Item causalPart;
		if(!ClaimType.CAMPAIGN.getType().equalsIgnoreCase(claim.getType().getType())){
		causalPart = claim.getServiceInformation().getCausalPart();
		applicableContracts = getContractService().findContract(claim, causalPart, true);
		
		if(applicableContracts != null){
			if (applicableContracts.size() == 1) {
				return multipleSuppliersAssociatedWithClaim;
			}else if ( applicableContracts.size() > 1) {
				return true;
			}
		}
		ServiceInformation serviceInformation = claim.getServiceInformation();
		ServiceDetail serviceDetail = serviceInformation.getServiceDetail();
		List<OEMPartReplaced> partsReplaced = serviceDetail.getReplacedParts();
		for (OEMPartReplaced part : partsReplaced) {
			applicableContracts = getContractService().findContract(claim, part.getItemReference().getReferredItem(), true);
			if(applicableContracts != null){
				if (applicableContracts.size() == 1) {
					return multipleSuppliersAssociatedWithClaim;
				}else if ( applicableContracts.size() > 1) {
					return true;
				}
			}
		}
		return multipleSuppliersAssociatedWithClaim;
	}
		return multipleSuppliersAssociatedWithClaim;	
	}
	
	
	public boolean isFaultCodeAndJobCodeOfExactMatch(Claim claim){
		boolean faultCodeAndJobCodeOfExactMatch = false;
		List<LaborDetail> laborDetails = claim.getServiceInformation().getServiceDetail().getLaborPerformed();
		String faultCode = claim.getServiceInformation().getFaultCode();
		if(faultCode!=null){
			for(LaborDetail laborDetail : laborDetails){
				if(laborDetail.getServiceProcedure().getDefinition()!=null){
					String jobCode = laborDetail.getServiceProcedure().getDefinition().getCode();
					if(jobCode.contains(faultCode)){
						faultCodeAndJobCodeOfExactMatch = true;
					}else {
						return false;
					}
					
				}
				
			}
		}
		return faultCodeAndJobCodeOfExactMatch;
	}
	
    private ConfigParamService getConfigParamService() {
        return (ConfigParamService) beanFactory.getBean("configParamService",
                ConfigParamService.class);
    }

    private CurrencyExchangeRateRepository getCurrencyExchangeRateRepository() {
        return (CurrencyExchangeRateRepository) beanFactory.getBean("currencyExchangeRateRepository",
                CurrencyExchangeRateRepository.class);
    }

    public boolean isReplacedSerialNumberSameAsOtherClaim(Claim claim){
        if(claim != null && claim.getServiceInformation() != null &&
                claim.getServiceInformation().getServiceDetail() != null){
            List<HussmanPartsReplacedInstalled> hussmanPartsReplacedInstalled = claim.getServiceInformation().getServiceDetail().getHussmanPartsReplacedInstalled();
            if(hussmanPartsReplacedInstalled != null && !hussmanPartsReplacedInstalled.isEmpty()){
                List<String> replacedSerialNumberList = new ArrayList<String>();
                for (HussmanPartsReplacedInstalled replacedInstalled : hussmanPartsReplacedInstalled) {
                    List<OEMPartReplaced> hussmanReplcaedParts = replacedInstalled.getReplacedParts();
                    if(hussmanReplcaedParts != null && !hussmanReplcaedParts.isEmpty()){
                        for (OEMPartReplaced replacedPart : hussmanReplcaedParts) {
                            if(StringUtils.isNotBlank(replacedPart.getSerialNumber())){
                                replacedSerialNumberList.add(replacedPart.getSerialNumber().toUpperCase());
                            }
                        }
                    }
                }
                return getClaimRepository().isAnyClaimWithReplacedParts(replacedSerialNumberList);
            }        
        }
        return false;
    }
	
    public boolean isInstalledSerialNumberSameAsOtherClaim(Claim claim){
        if(claim != null && claim.getServiceInformation() != null &&
                claim.getServiceInformation().getServiceDetail() != null){
            List<HussmanPartsReplacedInstalled> hussmanPartsReplacedInstalled = claim.getServiceInformation().getServiceDetail().getHussmanPartsReplacedInstalled();
            if(hussmanPartsReplacedInstalled != null && !hussmanPartsReplacedInstalled.isEmpty()){
                List<String> installedSerialNumberList = new ArrayList<String>();
                for (HussmanPartsReplacedInstalled replacedInstalled : hussmanPartsReplacedInstalled) {
                    List<InstalledParts> hussmanInstalledParts = replacedInstalled.getHussmanInstalledParts();
                    if(hussmanInstalledParts != null && !hussmanInstalledParts.isEmpty()){
                        for (InstalledParts installedPart : hussmanInstalledParts) {
                            if(StringUtils.isNotBlank(installedPart.getSerialNumber())){
                                installedSerialNumberList.add(installedPart.getSerialNumber().toUpperCase());
                            }
                        }
                    }
                }
                return getClaimRepository().isAnyClaimWithInstalledParts(installedSerialNumberList);
            }        
        }
        return false;
    }

    /**
     * @param claim
     * @return additionalTravelHours
     * This is added for creating the rule if 
     * Additional Travel Hours is greater than 0
     * Added for NMHGSMLS-577: RTM-165
     */
	public BigDecimal additionalTravelHours(Claim claim) {
		BigDecimal addTravelHours = new BigDecimal(0.0D, MathContext.DECIMAL32);
		if (claim.getServiceInformation().getServiceDetail().getTravelDetails()
				.getAdditionalHours() != null) {
			addTravelHours = claim.getServiceInformation().getServiceDetail()
					.getTravelDetails().getAdditionalHours();

		}
		return addTravelHours;
	}
    
    /**
     * @param travelDetail
     * @return travelTrips
     * This is added for creating the rule if 
     * Travel Trips is greater than 0
     * Added for NMHGSMLS-577: RTM-165
     */
	public Integer travelTrips(TravelDetail travelDetail) {
		Integer intTrvlTrips = new Integer(0);
		if (travelDetail != null) {
			intTrvlTrips = travelDetail.getTrips();
		}
		return intTrvlTrips;
	}
	/**
     * @param claim
     * @return boolean
     * This is added for creating the rule if 
     * causal part brand and truck brand are same or not
     * Added for NMHGSMLS-434: RTM-84
     */
	public boolean isCasualPartBrandSameOfClaimBrand(Claim claim) {
		if(isPartsClaimOnCompetitorModel(claim)){
			return true;
		}else{
			if(claim.getServiceInformation().getCausalBrandPart().getBrand().equalsIgnoreCase(claim.getBrand())) {				
				return true;
			}else{
				return false;
			}
		}		
	}
	/**
     * @param claim
     * @return boolean
     * This is added for creating the rule if 
     * installed part brand and truck brand are same or not
     * Added for NMHGSMLS-434: RTM-84
     */
	public boolean isInstalledPartBrandSameOfClaimBrand(Claim claim) {
		boolean isDistinctPart=true;
		 if(claim != null && claim.getServiceInformation() != null &&
	                claim.getServiceInformation().getServiceDetail() != null && !isPartsClaimOnCompetitorModel(claim)){
			 List<InstalledParts> partsInstalled = claim
						.getServiceInformation().getServiceDetail()
						.getInstalledParts();			
			 if(partsInstalled.size()==0){
				 isDistinctPart = true;
			 }
				for (InstalledParts partInstalled : partsInstalled) {								
					 if(!partInstalled.getBrandItem().getBrand().equalsIgnoreCase(claim.getBrand())){														 	
						 isDistinctPart = false;
						}					
					if(!isDistinctPart){
						break;
					}					
				}        
	        }
		return isDistinctPart;
	}
	/**
     * @param claim
     * @return boolean
     * This is added for creating the rule if 
     * removed part brand and truck brand are same or not
     * Added for NMHGSMLS-434: RTM-84
     */
	public boolean isRemovedPartBrandSameOfClaimBrand(Claim claim) {
		boolean isDistinctPart=true;
		 if(claim != null && claim.getServiceInformation() != null &&
	                claim.getServiceInformation().getServiceDetail() != null && !isPartsClaimOnCompetitorModel(claim)){
			 List<OEMPartReplaced> partsReplaced = claim
						.getServiceInformation().getServiceDetail()
						.getReplacedParts(); 
			 if(partsReplaced.size()==0){
				 isDistinctPart = true;
			 }
			 for (OEMPartReplaced partReplaced : partsReplaced) {										
					 if(!partReplaced.getBrandItem().getBrand().equalsIgnoreCase(claim.getBrand())){
						 isDistinctPart = false;						
						}
					if(!isDistinctPart){
						break;
					}
			 }			 
	        }
		return isDistinctPart;
	}
	
	public boolean isTechCertifiedRule(Claim claim) {
		if(verifyIfUtilevTruck(claim)){ // technician certification not needed for utilev trucks/trucks with no series ref certification
			return (Boolean) null;
		}
		boolean isLamDealer = isLAMDealer(claim);
			if (isLamDealer && isTechRequiredForLAMDealers()) {
				return isTechnicianCertifed(claim);
			}
		if ((claim.getType().getType().equals(ClaimType.PARTS.getType()) && !isTechRequiredForPartsClaim())
				|| (claim.getType().getType()
						.equals(ClaimType.CAMPAIGN.getType()) && !isTechRequiredForFPIClaim())
				|| isLamDealer && !isTechRequiredForLAMDealers()) {
			isTechCertifiedForNotifyingProcessor(claim);
			return (Boolean) null;
		} else {
			return isTechnicianCertifed(claim);
		}
	}

	public boolean isLAMDealer(Claim claim) {
		Dealership forDealer = new HibernateCast<Dealership>().cast(claim
				.getForDealer());
		DealerGroup forDealerGroup = getDealerGroupService()
				.findDealerGroupsForWatchedDealership(forDealer);
		boolean isLamDealer = false;
		if (forDealerGroup != null) {
			isLamDealer = (forDealerGroup.getName().equalsIgnoreCase(
					AdminConstants.LATIN_AMERICAN_DEALERS) || forDealerGroup
					.getDescription()
					.equalsIgnoreCase(AdminConstants.LATIN_AMERICAN_DEALERS)) ? true : false;
		}
		return isLamDealer;
	}
	
	private boolean isTechnicianCertifed(Claim claim) {
		String technicianEntered = claim.getServiceInformation()
				.getServiceDetail().getServiceTechnician();
		Technician technician = getOrgService().findTechnicianByName(
				technicianEntered);
		if (!isTechExists(technician)) {
			return false;
		}
		/*
		 * boolean isTechBelongingToFilingDealer = isTechBelongsToDealer(
		 * technician, claim);
		 */
		
		Dealership forDealer = new HibernateCast<Dealership>().cast(claim
				.getForDealer());
		ItemGroup series = getSeriesOfClaimedItem(claim);
		if(series!=null){
			ItemGroup sisterSeries = series.getOppositeSeries();
			boolean isTechHasSisterSeriesCore = false;
			boolean isTechHasCore = isTechHasAtleastOneCoreOfSeriesBrand(
					technician, claim,series);
			if(forDealer.getDualDealer()!=null && !isTechHasCore && sisterSeries!=null){
			isTechHasSisterSeriesCore = isTechHasAtleastOneCoreOfSeriesBrand(technician,
						claim,sisterSeries);
			}
			/*isTechBelongingToFilingDealer && */
			if (isTechHasCore) {
				return checkIfPRLevelExists(technician,series,sisterSeries,claim,forDealer); 
			}
			else if(isTechHasSisterSeriesCore){
				boolean isPRExist = checkIfPRLevelExists(technician,sisterSeries,series,claim,forDealer); //this method returns true, if technician has sister series core and (sister series PR OR PR for the brand of the truck)
				if(!isPRExist){
					if(isSeriesHasAtleastOnePRLevel(series, claim.getRepairDate()) && isTechHasPRLevelOfSeries(series, technician,
							claim.getRepairDate())) {
						return true;
					}
				}
				else{
					return isPRExist;
				}
			}
		}	
		return false;
	}

	
	/**
	 * 
	 * @param technician
	 * @param series
	 * @param oppositeSeries
	 * @param claim
	 * @param forDealer
	 * @return boolean
	 * This method checks if PR level exists for the series and technician. If it does not exist
	 * for Series, it checks for sister series
	 */
	private boolean checkIfPRLevelExists(Technician technician,
			ItemGroup series, ItemGroup oppositeSeries, Claim claim,
			Dealership forDealer) {
		if (!isSeriesHasAtleastOnePRLevel(series, claim.getRepairDate())) {
			if (forDealer.getDualDealer() != null && oppositeSeries != null) {
				if (!isSeriesHasAtleastOnePRLevel(oppositeSeries,
						claim.getRepairDate())) {
					return true;
				}
				if (isTechHasPRLevelOfSeries(oppositeSeries, technician,
						claim.getRepairDate())) {
					return true;
				}
			}
			return true;
		} else if (isTechHasPRLevelOfSeries(series, technician,
				claim.getRepairDate())) {
			return true;
		}
		return false;
	}

	private boolean verifyIfUtilevTruck(Claim claim) {
		ItemGroup series = getSeriesOfClaimedItem(claim);
		SeriesRefCertification seriesRefCertification = getCertificateService()
				.findBySeries(series);
		if(seriesRefCertification==null){ //series does not exist in seriesRefCertification, so it is a Utilev 
			return true;
		}
		return false;
	}
	
	private boolean isTechHasAtleastOneCoreOfSeriesBrand(Technician technician,
			Claim claim,ItemGroup series) {
		boolean isVerified = false;
		List<TechnicianCertification> techCertificates = technician
				.getTechnicianCertifications();
		CalendarDate repairDate = claim.getRepairDate();
		String brand = series.getBrandType();
		for (TechnicianCertification eachCertificate : techCertificates) {
			if(eachCertificate.getD().isActive()){
			boolean isCertCoreLevel = eachCertificate.getIsCoreLevel();
			boolean isRepairDateBetweenCertDate = isRepairDateBetweenCertDate(eachCertificate,repairDate);
			boolean isCertBrandMatchesSeriesBrand = eachCertificate.getBrand()
					.equalsIgnoreCase(brand);
			if (isCertCoreLevel && isCertBrandMatchesSeriesBrand
					&& isRepairDateBetweenCertDate) {
				isVerified = true;
				break;
				}
			}
		}
		return isVerified;
	
	}

	private void notifyProcessor(boolean isVerified, Claim claim) {
		if (isVerified) {
			claim.addNotifications(TECHNICIAN_CERTIFIED);
		} else {
			claim.addNotifications(TECHNICIAN_NOTCERTIFIED);
		}
	}

	/*private boolean checkForAuthOrCmsOrUnitComments(Claim claim) {
		boolean isClaimHasAuthNum = claim.isCmsAuthCheck().booleanValue();
		boolean isClaimHasCms = org.springframework.util.StringUtils
				.hasText(claim.getCmsTicketNumber());
		boolean isClaimedUnitHasComments = claim.getClaimedItems().get(0)
				.getItemReference().getReferredInventoryItem()
				.getInventoryCommentExists().booleanValue();
		if (isClaimHasAuthNum || isClaimHasCms || isClaimedUnitHasComments) {
			return true;
		}
		return false;
	}*/

	private boolean isSeriesHasAtleastOnePRLevel(ItemGroup series,
			CalendarDate repairDate) {
		SeriesRefCertification seriesRefCertification = getCertificateService()
				.findBySeries(series);
		if (seriesRefCertification != null) {
			boolean isSeriesInPeriod = isInPeriodOfRepairDate(
					seriesRefCertification, repairDate);
			if (isSeriesInPeriod) {
				List<SeriesCertification> certifactionsForSeries = seriesRefCertification
						.getSeriesCertification();
				for (SeriesCertification eachCertification : certifactionsForSeries) {
					if (eachCertification.getCategoryLevel().equals(AdminConstants.PRODUCT_CERTIFICATE_LEVEL)) {
						return true;
					}
				}
			}
		}
		return false;
	}

	private boolean isInPeriodOfRepairDate(
			SeriesRefCertification seriesRefCertification,
			CalendarDate repairDate) {
		CalendarDate startDate = seriesRefCertification.getStartDate();
		CalendarDate endDate = seriesRefCertification.getEndDate();
		if (repairDate.isAfter(startDate) && repairDate.isBefore(endDate)) {
			return true;
		}
		return false;
	}

	private ItemGroup getSeriesOfClaimedItem(Claim claim) {
		ItemReference itemReference = claim.getClaimedItems().get(0)
				.getItemReference();
		ItemGroup series;
		if (itemReference.isSerialized()) {
			series = itemReference.getUnserializedItem().getProduct();
		} else {
			series = itemReference.getModel().getIsPartOf();
		}
		return series;
	}

	private boolean isTechExists(Technician technician) {
		if (technician != null) {
			return true;
		}
		return false;
	}

	private boolean isTechHasSeriesCertification(Technician technician,
			SeriesCertification certification, CalendarDate repairDate) {
		List<TechnicianCertification> techCertifications = technician
				.getTechnicianCertifications();
		for (TechnicianCertification eachTechCertification : techCertifications) {
			if(eachTechCertification.getD().isActive()){
			boolean isCertFromDateBeforeRepairDate = eachTechCertification
					.getCertificationFromDate().isBefore(repairDate)
					|| eachTechCertification.getCertificationFromDate().equals(
							repairDate);
			if (!eachTechCertification.getIsCoreLevel()){
				for(SeriesRefCertification eachCert:eachTechCertification.getSeriesCertification()){
					if(eachCert.equals(certification.getSeriesRefCert()) && isCertFromDateBeforeRepairDate){
						return true;
						}
					}				
				}
			}
		}
		return false;
	}

	private boolean isTechHasPRLevelOfSeries(ItemGroup series,
			Technician technician, CalendarDate repairDate) {
		boolean isVerified = false;
		SeriesRefCertification seriesRefCertification = getCertificateService()
				.findBySeries(series);
		if (seriesRefCertification != null) {
			boolean isSeriesInPeriod = isInPeriodOfRepairDate(
					seriesRefCertification, repairDate);
			if (isSeriesInPeriod) {
				List<SeriesCertification> certifactionsForSeries = seriesRefCertification
						.getSeriesCertification();
				for (SeriesCertification eachCertification : certifactionsForSeries) {
					if (eachCertification.getCategoryLevel().equals(AdminConstants.PRODUCT_CERTIFICATE_LEVEL)) {
						isVerified = isTechHasSeriesCertification(technician,
								eachCertification, repairDate);
						break;
					}
				}
			}
		}
		return isVerified;
	}


	private boolean isRepairDateBetweenCertDate(
			TechnicianCertification eachCertificate, CalendarDate repairDate) {
		return (repairDate.isAfter(eachCertificate
				.getCertificationFromDate()) || repairDate.equals(eachCertificate
				.getCertificationFromDate())) && (repairDate.isBefore(
				 eachCertificate.getCertificationToDate()) || repairDate.equals(eachCertificate
							.getCertificationToDate()));
	}


	/*private boolean isTechBelongsToDealer(Technician technician, Claim claim) {
		@SuppressWarnings("unchecked")
		List<Dealership> dealers = (List<Dealership>) (List<?>) technician.getOrgUser()
				.getBelongsToOrganizations();
		Dealership forDealer = new HibernateCast<Dealership>().cast(claim
				.getForDealer());
		if (dealers.contains(forDealer))
			return true;
		return false;

	}*/

	private boolean isTechRequiredForLAMDealers() {
		return getConfigParamService().getBooleanValue(
				ConfigName.TECHNICIAN_CERTIFICATION_FOR_LAM_DEALERS.getName());
	}

	private boolean isTechRequiredForPartsClaim() {
		return getConfigParamService().getBooleanValue(
				ConfigName.TECHNICIAN_CERTIFICATION_FOR_PARTS_CLAIMS.getName());
	}

	private boolean isTechRequiredForFPIClaim() {
		return getConfigParamService().getBooleanValue(
				ConfigName.TECHNICIAN_CERTIFICATION_FOR_FPI_CLAIMS.getName());
	}

	private DealerGroupService getDealerGroupService() {
		return (DealerGroupService) beanFactory.getBean("dealerGroupService",
				DealerGroupService.class);
	}

	public void isTechCertifiedForNotifyingProcessor(Claim claim) {
		boolean isVerified = isTechnicianCertifed(claim);
		String technicianEntered = claim.getServiceInformation()
				.getServiceDetail().getServiceTechnician();
		if (org.springframework.util.StringUtils.hasText(technicianEntered)) {
			notifyProcessor(isVerified,claim);
		}
	}
	
	private OrgService getOrgService() {
		return (OrgService) beanFactory.getBean("orgService", OrgService.class);
	}

	private CertificateService getCertificateService() {
		return (CertificateService) beanFactory.getBean("certificateService",
				CertificateService.class);
	}

}
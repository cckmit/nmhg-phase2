/*
 *   Copyright (c) 2007 Tavant Technologies
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
package tavant.twms.domain.claim;

import java.util.ArrayList;
import java.util.List;
import java.util.Currency;
import java.util.Map;
import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import tavant.twms.domain.claim.payment.RecoveryPayment;
import tavant.twms.domain.claim.payment.CreditMemo;
import tavant.twms.domain.claim.payment.definition.Section;
import tavant.twms.domain.common.ListOfValues;
import tavant.twms.domain.common.LovRepository;
import tavant.twms.domain.partrecovery.PartRecoverySearchCriteria;
import tavant.twms.domain.partreturn.PartReturnStatus;
import tavant.twms.domain.query.HibernateQuery;
import tavant.twms.domain.query.HibernateQueryGenerator;
import tavant.twms.domain.query.RecoveryClaimQueryGenerator;
import tavant.twms.domain.query.SupplierPartReturnClaimSummary;
import tavant.twms.domain.rules.DomainPredicate;
import tavant.twms.domain.rules.PredicateAdministrationService;
import tavant.twms.domain.supplier.RecoveryClaimCriteria;
import tavant.twms.domain.supplier.CostLineItem;
import tavant.twms.infra.ListCriteria;
import tavant.twms.infra.PageResult;
import tavant.twms.infra.QueryParameters;
import tavant.twms.security.SecurityHelper;

import com.domainlanguage.money.Money;
import com.domainlanguage.time.TimePoint;

/**
 * @author pradipta.a
 */
public class RecoveryClaimServiceImpl extends HibernateDaoSupport implements
		RecoveryClaimService {

	RecoveryClaimRepository recoveryClaimRepository;

	PredicateAdministrationService predicateAdministrationService;
	
	private SecurityHelper securityHelper;
	
	private LovRepository lovRepository;

	public void setLovRepository(LovRepository lovRepository) {
		this.lovRepository = lovRepository;
	}

	public void updateRecoveryClaim(RecoveryClaim recoveryClaim) {
		getHibernateTemplate().update(recoveryClaim);
	}

	public List<RecoveryClaim> findClaimInState(RecoveryClaimState state) {
		return recoveryClaimRepository.findClaimInState(state);
	}

	public RecoveryClaim findRecoveryClaim(Long id) {
		return recoveryClaimRepository.find(id);
	}

   
	
	public RecoveryClaim findRecoveryClaim(String recoveryClaimNumber) {
		return recoveryClaimRepository.find(recoveryClaimNumber);
	}

	public RecoveryClaim findRecoveryClaimForClaim(Long id) {
		if (recoveryClaimRepository.findRecClaimForClaim(id).size() >= 1)
			return recoveryClaimRepository.findRecClaimForClaim(id).get(0);
		else
			return null;
	}

	public boolean checkAutoClose(RecoveryClaim recClaim) {
		return false;
	}

	public void updatePayment(RecoveryClaim recoveryClaim) {
		RecoveryClaim newRecoveryClaim = recoveryClaimRepository.find(recoveryClaim.getId());
		if (newRecoveryClaim.getRecoveryPayment() == null) {
			RecoveryPayment recoveryPayment = new RecoveryPayment();
			recoveryPayment.setContractAmount(newRecoveryClaim
					.getTotalCostAfterApplyingContract());
			recoveryPayment.setPreviousPaidAmount(Money.valueOf(0.0, 
					newRecoveryClaim.getContract().getSupplier().getPreferredCurrency()));
			newRecoveryClaim.setRecoveryPayment(recoveryPayment);
		}
		newRecoveryClaim.getRecoveryPayment().setTotalRecoveryAmount(
				newRecoveryClaim.getTotalRecoveredCost());
		if(newRecoveryClaim.getRecoveryPayment().getActiveCreditMemo() != null){
			newRecoveryClaim.getRecoveryPayment().setPreviousPaidAmount(
					newRecoveryClaim.getRecoveryPayment().getActiveCreditMemo().getPaidAmount());
		}else{
			newRecoveryClaim.getRecoveryPayment().setPreviousPaidAmount(Money.valueOf(0.0, 
					newRecoveryClaim.getContract().getSupplier().getPreferredCurrency()));
		}
		newRecoveryClaim.getRecoveryPayment().setForRecoveryClaim(newRecoveryClaim);

	}

	public boolean isPaymentMade(RecoveryClaim recoveryClaim) {
		return recoveryClaim.getRecoveryClaimState().ordinal() >= RecoveryClaimState.DEBITTED_AND_CLOSED
				.ordinal();
	}

	public PageResult<RecoveryClaim> findAllRecoveryClaimsMatchingQuery(
			Long domainPredicateId, ListCriteria listCriteria) {
		DomainPredicate predicate = predicateAdministrationService
				.findById(domainPredicateId);
		HibernateQueryGenerator generator = new RecoveryClaimQueryGenerator();
		generator.visit(predicate);
		HibernateQuery query = generator.getHibernateQuery();
		String sortString = listCriteria.getSortCriteriaString();
		String queryWithoutSelect = query.getQueryWithoutSelect();
		if (listCriteria.isFilterCriteriaSpecified()) {
			String filter = listCriteria.getParamterizedFilterCriteria();
			queryWithoutSelect = queryWithoutSelect + " and (" + filter + " )";
		}
		queryWithoutSelect = queryWithoutSelect.replaceAll("USER_LOCALE",
				securityHelper.getLoggedInUser().getLocale().toString());
		
		QueryParameters queryParameters = new QueryParameters(query.getParameters(), listCriteria.getTypedParameterMap());

		return recoveryClaimRepository.findRecoveryClaimsUsingDynamicQuery(
				queryWithoutSelect, sortString, query.getSelectClause(),
				listCriteria.getPageSpecification(), queryParameters);
	}

	public List<RecoveryClaim> findAllRecClaimsForMultiMaintainance(
			Long domainPredicateId, ListCriteria listCriteria) {
		DomainPredicate predicate = this.predicateAdministrationService
				.findById(domainPredicateId);
		HibernateQueryGenerator generator = new RecoveryClaimQueryGenerator();
		generator.visit(predicate);
		HibernateQuery query = generator.getHibernateQuery();
		String queryWithoutSelect = query.getQueryWithoutSelect();
		if (listCriteria.isFilterCriteriaSpecified()) {
			queryWithoutSelect = queryWithoutSelect + " and ("
					+ listCriteria.getParamterizedFilterCriteria() + " )";
		}
		QueryParameters params = new QueryParameters(query.getParameters(),
				listCriteria.getTypedParameterMap());
		queryWithoutSelect = queryWithoutSelect.replaceAll("USER_LOCALE",
				securityHelper.getLoggedInUser().getLocale().toString());
		
		return this.recoveryClaimRepository
				.findAllRecClaimsForMultiMaintainance(queryWithoutSelect,
						listCriteria.getSortCriteriaString(), query
								.getSelectClause(), params);
	}

	public void setRecoveryClaimRepository(
			RecoveryClaimRepository recoveryClaimRepository) {
		this.recoveryClaimRepository = recoveryClaimRepository;
	}

	public void setPredicateAdministrationService(
			PredicateAdministrationService predicateAdministrationService) {
		this.predicateAdministrationService = predicateAdministrationService;
	}

	public RecoveryClaim findActiveRecoveryClaimForClaim(
			String warrantyClaimNumber) {
		return recoveryClaimRepository
				.findActiveRecoveryClaimForClaim(warrantyClaimNumber);
	}
	
	public RecoveryClaim findActiveRecoveryClaimForClaimForOfflineDebit(
			String warrantyClaimNumber) {
		return recoveryClaimRepository
				.findActiveRecoveryClaimForClaimForOfflineDebit(warrantyClaimNumber);
	}

	public PageResult<RecoveryClaim> findRecClaimsForPredefinedSearch(
			RecoveryClaimCriteria searchObj, ListCriteria listCriteria) {
		return recoveryClaimRepository.findRecClaimsForPredefinedSearch(
				searchObj, listCriteria);
	}

	public PageResult<SupplierPartReturnClaimSummary> findAllRecoveryClaimsMatchingCriteria(
			final PartRecoverySearchCriteria partRecoverySearchCriteria) {
		return recoveryClaimRepository
				.findAllRecoveryClaimsMatchingCriteria(partRecoverySearchCriteria);
	}

	public List<PartReturnStatus> findAllStatusForPartRecovery() {
		return recoveryClaimRepository.findAllStatusForPartRecovery();
	}
	
	@Required
	public void setSecurityHelper(SecurityHelper securityHelper) {
		this.securityHelper = securityHelper;
	}

    public void createRecClaimPaymentAudits(RecoveryClaim recoveryClaim, TimePoint updatedOn, Long claimAuditId) {
        SupplierRecoveryPaymentAudit pmtAudit = new SupplierRecoveryPaymentAudit();
        //id was getting set twice, so commenting this
        //pmtAudit.setClaimId(recoveryClaim.getId());
        RecoveryPayment recoveryPayment = recoveryClaim.getRecoveryPayment();
        if(recoveryPayment != null){
            pmtAudit.setClaimedAmount(recoveryPayment.getContractAmount());
            CreditMemo activeCreditMemo = recoveryPayment.getActiveCreditMemo();
            if(activeCreditMemo != null){
                pmtAudit.setCreditMemoDate(activeCreditMemo.getCreditMemoDate());
                pmtAudit.setCreditMemoNumber(activeCreditMemo.getCreditMemoNumber());
                pmtAudit.setTaxAmount(activeCreditMemo.getTaxAmount());
                pmtAudit.setRecoveredAmount(activeCreditMemo.getPaidAmount().negated());
            }
        }

        pmtAudit.setClaimId(recoveryClaim.getClaim().getId());
        pmtAudit.setUpdatedOn(updatedOn);
        pmtAudit.setRecClaimAuditId(claimAuditId);

        /*Calculate the individual cost category amount for this transaction
        Oem Parts
        Non Oem Parts
        Labor
        Travel By Distance
        Item Freight And Duty
        Meals
        Parking
        Travel By Trip
        Travel by Hours
        Handling Fee
        Transportation
        */
        Currency currency = recoveryClaim.getContract().getSupplier().getPreferredCurrency();
        BigDecimal num = new BigDecimal(0);
        Money recoveredCCPartsAmount = new Money(num.setScale(currency.getDefaultFractionDigits()), currency);
        Money recoveredNonCCPartsAmount = new Money(num.setScale(currency.getDefaultFractionDigits()), currency);
        Money recoveredLaborAmount = new Money(num.setScale(currency.getDefaultFractionDigits()), currency);
        Money recoveredTravelByDistAmount = new Money(num.setScale(currency.getDefaultFractionDigits()), currency);
        Money recoveredTravelByTripAmount = new Money(num.setScale(currency.getDefaultFractionDigits()), currency);
        Money recoveredTravelByHoursAmount = new Money(num.setScale(currency.getDefaultFractionDigits()), currency);
        Money recoveredFreightAndDutyAmount = new Money(num.setScale(currency.getDefaultFractionDigits()), currency);
        Money recoveredMealsAmount = new Money(num.setScale(currency.getDefaultFractionDigits()), currency);
        Money recoveredParkingAmount = new Money(num.setScale(currency.getDefaultFractionDigits()), currency);
        Money recoveredMiscPartsAmount = new Money(num.setScale(currency.getDefaultFractionDigits()), currency);
        Money recoveredPerDiemAmount = new Money(num.setScale(currency.getDefaultFractionDigits()), currency);
        Money recoveredRentalChargesAmount = new Money(num.setScale(currency.getDefaultFractionDigits()), currency);
        Money recoveredAdditionalTravelHoursAmount = new Money(num.setScale(currency.getDefaultFractionDigits()), currency);
        Money recoveredLocalPurchaseAmount = new Money(num.setScale(currency.getDefaultFractionDigits()), currency);
        Money recoveredTollsAmount = new Money(num.setScale(currency.getDefaultFractionDigits()), currency);
        Money recoveredOtherFreightDutyAmount = new Money(num.setScale(currency.getDefaultFractionDigits()), currency);
        Money recoveredOthersAmount = new Money(num.setScale(currency.getDefaultFractionDigits()), currency);
        Money recoveredHandlingFeeAmount = new Money(num.setScale(currency.getDefaultFractionDigits()), currency);
        Money recoveredTransportationAmount = new Money(num.setScale(currency.getDefaultFractionDigits()), currency);

        List<RecoveryClaimAudit> recoveryClaimAudits = recoveryClaim.getRecoveryClaimAudits();
        List<Long> recClaimAuditIds = new ArrayList<Long>();
        for(RecoveryClaimAudit recoveryClaimAudit: recoveryClaimAudits)
        {
            recClaimAuditIds.add(recoveryClaimAudit.getId());
        }
        List<SupplierRecoveryPaymentAudit> recoveryPaymentAudits = recoveryClaimRepository.fetchAllRecClaimPaymentAudits(recClaimAuditIds);


        for(SupplierRecoveryPaymentAudit recPaymentAudit: recoveryPaymentAudits)
        {
            if (recPaymentAudit!=null) {
                List<SupplierRecCostCategories> categories = recPaymentAudit.getCostCategories();
                for (SupplierRecCostCategories costcategory: categories) {
                    //calculate the category amounts
                    if(costcategory.getCostCategoryName().equals(Section.OEM_PARTS))
                    {
                        recoveredCCPartsAmount =recoveredCCPartsAmount.plus(costcategory.getRecoveredAmount());
                    }
                    else if(costcategory.getCostCategoryName().equals(Section.NON_OEM_PARTS))
                    {
                        recoveredNonCCPartsAmount =recoveredNonCCPartsAmount.plus(costcategory.getRecoveredAmount());
                    }
                    else if(costcategory.getCostCategoryName().equals(Section.LABOR))
                    {
                        recoveredLaborAmount=recoveredLaborAmount.plus(costcategory.getRecoveredAmount());
                    }
                    else if(costcategory.getCostCategoryName().equals(Section.TRAVEL_BY_DISTANCE))
                    {
                        recoveredTravelByDistAmount=recoveredTravelByDistAmount.plus(costcategory.getRecoveredAmount());
                    }
                    else if(costcategory.getCostCategoryName().equals(Section.ITEM_FREIGHT_DUTY))
                    {
                        recoveredFreightAndDutyAmount=recoveredFreightAndDutyAmount.plus(costcategory.getRecoveredAmount());
                    }
                    else if(costcategory.getCostCategoryName().equals(Section.MEALS))
                    {
                        recoveredMealsAmount=recoveredMealsAmount.plus(costcategory.getRecoveredAmount());
                    }
                    else if(costcategory.getCostCategoryName().equals(Section.PARKING))
                    {
                        recoveredParkingAmount=recoveredParkingAmount.plus(costcategory.getRecoveredAmount());
                    }
                    else if(costcategory.getCostCategoryName().equals(Section.TRAVEL_BY_TRIP))
                    {
                        recoveredTravelByTripAmount=recoveredTravelByTripAmount.plus(costcategory.getRecoveredAmount());
                    }
                    else if(costcategory.getCostCategoryName().equals(Section.TRAVEL_BY_HOURS))
                    {
                        recoveredTravelByHoursAmount=recoveredTravelByHoursAmount.plus(costcategory.getRecoveredAmount());
                    }
                    else if(costcategory.getCostCategoryName().equals(Section.MISCELLANEOUS_PARTS))
                    {
                        recoveredMiscPartsAmount=recoveredMiscPartsAmount.plus(costcategory.getRecoveredAmount());
                    }
                    else if(costcategory.getCostCategoryName().equals(Section.PER_DIEM))
                    {
                        recoveredPerDiemAmount=recoveredPerDiemAmount.plus(costcategory.getRecoveredAmount());
                    }
                    else if(costcategory.getCostCategoryName().equals(Section.RENTAL_CHARGES))
                    {
                        recoveredRentalChargesAmount=recoveredRentalChargesAmount.plus(costcategory.getRecoveredAmount());
                    }
                    else if(costcategory.getCostCategoryName().equals(Section.ADDITIONAL_TRAVEL_HOURS))
                    {
                        recoveredAdditionalTravelHoursAmount=recoveredAdditionalTravelHoursAmount.plus(costcategory.getRecoveredAmount());
                    }
                    else if(costcategory.getCostCategoryName().equals(Section.LOCAL_PURCHASE))
                    {
                        recoveredAdditionalTravelHoursAmount=recoveredAdditionalTravelHoursAmount.plus(costcategory.getRecoveredAmount());
                    }
                    else if(costcategory.getCostCategoryName().equals(Section.TOLLS))
                    {
                        recoveredTollsAmount=recoveredTollsAmount.plus(costcategory.getRecoveredAmount());
                    }
                    else if(costcategory.getCostCategoryName().equals(Section.OTHER_FREIGHT_DUTY))
                    {
                        recoveredOtherFreightDutyAmount=recoveredOtherFreightDutyAmount.plus(costcategory.getRecoveredAmount());
                    }
                    else if(costcategory.getCostCategoryName().equals(Section.OTHERS))
                    {
                        recoveredOthersAmount=recoveredOthersAmount.plus(costcategory.getRecoveredAmount());
                    }
                    else if(costcategory.getCostCategoryName().equals(Section.HANDLING_FEE))
                    {
                    	recoveredHandlingFeeAmount =recoveredHandlingFeeAmount.plus(costcategory.getRecoveredAmount());
                    }
                    else if (costcategory.getCostCategoryName().equals(Section.TRANSPORTATION_COST))
                    {
                    	recoveredTransportationAmount = recoveredTransportationAmount.plus(costcategory.getRecoveredAmount());
                    }

                }
            }

        }

        List<SupplierRecCostCategories> categories = new ArrayList<SupplierRecCostCategories>();
        List<CostLineItem> costLineItems = recoveryClaim.getCostLineItems();
        for (CostLineItem costLineItem : costLineItems)
        {
                //calculate the category amounts
                SupplierRecCostCategories category = new SupplierRecCostCategories();
                category.setClaimedAmount(costLineItem.getCostAfterApplyingContract());
                category.setCostCategoryName(costLineItem.getSection().getName());

                if(costLineItem.getSection().getName().equals(Section.OEM_PARTS))
                {
                    recoveredCCPartsAmount =costLineItem.getRecoveredCost().minus(recoveredCCPartsAmount);
                    category.setRecoveredAmount(recoveredCCPartsAmount);
                }
                else if(costLineItem.getSection().getName().equals(Section.NON_OEM_PARTS))
                {
                    recoveredNonCCPartsAmount =costLineItem.getRecoveredCost().minus(recoveredNonCCPartsAmount);
                    category.setRecoveredAmount(recoveredNonCCPartsAmount);
                }
                else if(costLineItem.getSection().getName().equals(Section.LABOR))
                {
                    recoveredLaborAmount=costLineItem.getRecoveredCost().minus(recoveredLaborAmount);
                    category.setRecoveredAmount(recoveredLaborAmount);
                }
                else if(costLineItem.getSection().getName().equals(Section.TRAVEL_BY_DISTANCE))
                {
                    recoveredTravelByDistAmount=costLineItem.getRecoveredCost().minus(recoveredTravelByDistAmount);
                    category.setRecoveredAmount(recoveredTravelByDistAmount);
                }
                else if(costLineItem.getSection().getName().equals(Section.ITEM_FREIGHT_DUTY))
                {
                    recoveredFreightAndDutyAmount=costLineItem.getRecoveredCost().minus(recoveredFreightAndDutyAmount);
                    category.setRecoveredAmount(recoveredFreightAndDutyAmount);
                }
                else if(costLineItem.getSection().getName().equals(Section.MEALS))
                {
                    recoveredMealsAmount=costLineItem.getRecoveredCost().minus(recoveredMealsAmount);
                    category.setRecoveredAmount(recoveredMealsAmount);
                }
                else if(costLineItem.getSection().getName().equals(Section.PARKING))
                {
                    recoveredParkingAmount=costLineItem.getRecoveredCost().minus(recoveredParkingAmount);
                    category.setRecoveredAmount(recoveredParkingAmount);
                }
                else if(costLineItem.getSection().getName().equals(Section.TRAVEL_BY_TRIP))
                {
                    recoveredTravelByTripAmount=costLineItem.getRecoveredCost().minus(recoveredTravelByTripAmount);
                    category.setRecoveredAmount(recoveredTravelByTripAmount);
                }
                else if(costLineItem.getSection().getName().equals(Section.TRAVEL_BY_HOURS))
                {
                    recoveredTravelByHoursAmount=costLineItem.getRecoveredCost().minus(recoveredTravelByHoursAmount);
                    category.setRecoveredAmount(recoveredTravelByHoursAmount);
                }
                else if(costLineItem.getSection().getName().equals(Section.OTHER_FREIGHT_DUTY))
                {
                    recoveredOtherFreightDutyAmount=costLineItem.getRecoveredCost().minus(recoveredOtherFreightDutyAmount);
                    category.setRecoveredAmount(recoveredOtherFreightDutyAmount);
                }
                else if(costLineItem.getSection().getName().equals(Section.OTHERS))
                {
                    recoveredOthersAmount=costLineItem.getRecoveredCost().minus(recoveredOthersAmount);
                    category.setRecoveredAmount(recoveredOthersAmount);
                }
                else if(costLineItem.getSection().getName().equals(Section.ADDITIONAL_TRAVEL_HOURS))
                {
                    recoveredAdditionalTravelHoursAmount=costLineItem.getRecoveredCost().minus(recoveredAdditionalTravelHoursAmount);
                    category.setRecoveredAmount(recoveredAdditionalTravelHoursAmount);
                }
                else if(costLineItem.getSection().getName().equals(Section.RENTAL_CHARGES))
                {
                    recoveredRentalChargesAmount=costLineItem.getRecoveredCost().minus(recoveredRentalChargesAmount);
                    category.setRecoveredAmount(recoveredRentalChargesAmount);
                }
                else if(costLineItem.getSection().getName().equals(Section.PER_DIEM))
                {
                    recoveredPerDiemAmount=costLineItem.getRecoveredCost().minus(recoveredPerDiemAmount);
                    category.setRecoveredAmount(recoveredPerDiemAmount);
                }
                else if(costLineItem.getSection().getName().equals(Section.MISCELLANEOUS_PARTS))
                {
                    recoveredMiscPartsAmount=costLineItem.getRecoveredCost().minus(recoveredMiscPartsAmount);
                    category.setRecoveredAmount(recoveredMiscPartsAmount);
                }
                else if(costLineItem.getSection().getName().equals(Section.LOCAL_PURCHASE))
                {
                    recoveredLocalPurchaseAmount=costLineItem.getRecoveredCost().minus(recoveredLocalPurchaseAmount);
                    category.setRecoveredAmount(recoveredLocalPurchaseAmount);
                }
                else if(costLineItem.getSection().getName().equals(Section.TOLLS))
                {
                    recoveredTollsAmount=costLineItem.getRecoveredCost().minus(recoveredTollsAmount);
                    category.setRecoveredAmount(recoveredTollsAmount);
                }
                else if(costLineItem.getSection().getName().equals(Section.HANDLING_FEE))
                {
                    recoveredHandlingFeeAmount=costLineItem.getRecoveredCost().minus(recoveredHandlingFeeAmount);
                    category.setRecoveredAmount(recoveredHandlingFeeAmount);
                }
                
                else if (costLineItem.getSection().getName().equals(Section.TRANSPORTATION_COST))
                {
                	recoveredTransportationAmount = costLineItem.getRecoveredCost().minus(recoveredTransportationAmount);
                	category.setRecoveredAmount(recoveredTransportationAmount);
                }
                categories.add(category);
        }

    	pmtAudit.setCostCategories(categories);
        recoveryClaimRepository.updateAllRecClaimPaymentAudits(pmtAudit);
    }

    public List<RecoveryClaim> findRecoveryClaimsForVRRDownload(PartRecoverySearchCriteria criteria,
            int pageNum, int recordsPerPage){
        return recoveryClaimRepository.findRecoveryClaimsForVRRDownload(criteria, pageNum, recordsPerPage);
    }

    public Long findRecClmsCountForVRRDownload(PartRecoverySearchCriteria criteria){
        return recoveryClaimRepository.findRecClmsCountForVRRDownload(criteria);
    }
    
    public RecoveryClaim findRecoveryClaimByPartNumber(final Map<String,Object> params){
    	 return recoveryClaimRepository.findRecoveryClaimByPartNumber(params);
    }
    
    public List<ListOfValues> getLovsForClass(String className, RecoveryClaim recoveryClaim) {
    	
    	return lovRepository.findAllActive(className);
    }

	public RecoveryClaim findByRecoveryClaimNumber(String recoveryClaimNumber) {
		return recoveryClaimRepository.findByRecoveryClaimNumber(recoveryClaimNumber);
	}

	public String findRecClaimFromPartReturn(Long partReturnId) {
		return recoveryClaimRepository.findRecClaimFromPartReturn(partReturnId);
	}
	
	
    
}

package tavant.twms.integration.layer.transformer.global.dealerinterfaces.claimsubmission;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Currency;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.IncorrectResultSizeDataAccessException;

import tavant.twms.domain.catalog.ItemGroup;
import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.claim.LaborDetail;
import tavant.twms.domain.claim.ServiceDetail;
import tavant.twms.domain.claim.ServiceInformation;
import tavant.twms.domain.claim.TravelDetail;
import tavant.twms.domain.claim.payment.CostCategory;
import tavant.twms.domain.claim.payment.CostCategoryRepository;
import tavant.twms.domain.configuration.ConfigName;
import tavant.twms.domain.configuration.ConfigParamService;
import tavant.twms.domain.failurestruct.FailureStructure;
import tavant.twms.domain.failurestruct.ServiceProcedure;
import tavant.twms.domain.orgmodel.AdditionalLaborEligibility;
import tavant.twms.domain.orgmodel.AdditionalLaborEligibilityService;
import tavant.twms.domain.orgmodel.Address;
import tavant.twms.domain.orgmodel.OrganizationAddress;
import tavant.twms.domain.orgmodel.ServiceProvider;
import tavant.twms.domain.orgmodel.User;
import tavant.twms.domain.orgmodel.UserRepository;
import tavant.twms.infra.HibernateCast;
import tavant.twms.integration.layer.constants.DealerInterfaceErrorConstants;
import tavant.twms.integration.layer.constants.IntegrationConstants;
import tavant.twms.integration.layer.util.GoogleMapDistanceCalculater;
import tavant.twms.integration.layer.util.IntegrationLayerUtil;
import tavant.twms.security.SecurityHelper;

import com.domainlanguage.money.Money;
import com.tavant.dealerinterfaces.claimsubmission.request.ClaimSubmissionDocument.ClaimSubmission;
import com.tavant.dealerinterfaces.claimsubmission.request.ClaimSubmissionDocument.ClaimSubmission.CustomerDetails;
import com.tavant.dealerinterfaces.claimsubmission.request.ClaimSubmissionDocument.ClaimSubmission.JobCodes;
import com.tavant.dealerinterfaces.claimsubmission.request.ClaimSubmissionDocument.ClaimSubmission.JobCodes.EachJobCode;

public class BatchClaimCostCategoryConfigurationDetails {
	private final Logger logger = Logger.getLogger("dealerAPILogger");

	private DealerInterfaceErrorConstants dealerInterfaceErrorConstants;
	private ConfigParamService configParamService;
	private ClaimSubmissionTransformer claimSubmissionTransformer;
	private CostCategoryRepository costCategoryRepository;
	private AdditionalLaborEligibilityService additionalLaborService;
	public static String DEAFULT_REASON_FOR_ADDITIONAL_LABOR = "Labor";

	public void validateConfiguredCostCategories(
			ClaimSubmission claimSubmissionDTO, ServiceDetail serviceDetail,
			Currency currency, Map<String, String> resultMap, Claim claim,
			FailureStructure failureStructure) {
		List<String> configuredCostCategoriesCode = new ArrayList<String>();
		List<Object> configuredCostCategories = configParamService
				.getListofObjects(ConfigName.CONFIGURED_COST_CATEGORIES
						.getName());
		List<CostCategory> configuredCostCategoriesList = new ArrayList<CostCategory>();
		for (Object object : configuredCostCategories) {
			CostCategory costCategory = new HibernateCast<CostCategory>()
					.cast(object);
			configuredCostCategoriesList.add(costCategory);
		}
		ItemGroup productToConsider = null;
		if (claim.getItemReference() != null
				&& claim.getItemReference().isSerialized()
				&& claim.getItemReference().getReferredInventoryItem() != null) {
			productToConsider = claim.getItemReference()
					.getReferredInventoryItem().getOfType().getProduct();
		} else if (claim.getItemReference() != null
				&& claim.getItemReference().getModel() != null) {
			productToConsider = claimSubmissionTransformer.getProduct(claim
					.getItemReference().getModel());
		}
		List<Long> costCategoryIds = new ArrayList<Long>(
				configuredCostCategories.size());
		for (CostCategory costCat : configuredCostCategoriesList) {
			costCategoryIds.add(costCat.getId());
		}
		if (productToConsider != null) {
			List<CostCategory> configuredCostCategoriesListForSeries = costCategoryRepository
					.findCostCategoryApplicableForProduct(productToConsider,
							costCategoryIds);
			for (CostCategory category : configuredCostCategoriesListForSeries) {
				if (configuredCostCategoriesList.contains(category))
					configuredCostCategoriesCode.add(category.getCode());
			}
		}
		if(configuredCostCategoriesCode!=null&&!configuredCostCategoriesCode.isEmpty())
		setCostCategories(claimSubmissionDTO, resultMap,
				configuredCostCategoriesCode, serviceDetail, currency, claim,
				failureStructure);
	}

	private void setCostCategories(ClaimSubmission claimSubmissionDTO,
			Map<String, String> resultMap,
			List<String> configuredCostCategoriesCode,
			ServiceDetail serviceDetail, Currency currency, Claim claim,
			FailureStructure failureStructure) {
		validateAndSetRentalCharges(configuredCostCategoriesCode, resultMap,
				serviceDetail, claimSubmissionDTO, currency);
		validateAndSetTolls(configuredCostCategoriesCode, resultMap,
				serviceDetail, claimSubmissionDTO, currency);
		validateAndSetLocalPurchases(configuredCostCategoriesCode, resultMap,
				serviceDetail, claimSubmissionDTO, currency);
		validateAndSetParkingExpenses(configuredCostCategoriesCode, resultMap,
				serviceDetail, claimSubmissionDTO, currency);
		validateAndSetPerdim(configuredCostCategoriesCode, resultMap,
				serviceDetail, claimSubmissionDTO, currency);
		validateAndSetItemFreightAndDuty(configuredCostCategoriesCode,
				resultMap, serviceDetail, claimSubmissionDTO, currency);
		validateAndSetMeals(configuredCostCategoriesCode, resultMap,
				serviceDetail, claimSubmissionDTO, currency);
		validateAndSetOthers(configuredCostCategoriesCode, resultMap,
				serviceDetail, claimSubmissionDTO, currency);
		validateAndSetOtherFreightAndDuty(configuredCostCategoriesCode,
				resultMap, serviceDetail, claimSubmissionDTO, currency);
		validateAndSetMislaniousParts(configuredCostCategoriesCode, resultMap,
				serviceDetail, claimSubmissionDTO, currency);
		setLaborInformation(configuredCostCategoriesCode, claimSubmissionDTO,
				serviceDetail, claim, failureStructure, resultMap);
		if (IntegrationLayerUtil.isAMERBusinessUnit(claim.getBusinessUnitInfo()
				.getName())) {
			setTransporationInformation(configuredCostCategoriesCode,
					claimSubmissionDTO, serviceDetail, claim, failureStructure,
					resultMap, currency);
			setHandlingFee(configuredCostCategoriesCode, claimSubmissionDTO,
					serviceDetail, claim, failureStructure, resultMap, currency);
		}
	}

	private void setHandlingFee(List<String> configuredCostCategoriesCode,
			ClaimSubmission claimSubmissionDTO, ServiceDetail serviceDetail,
			Claim claim, FailureStructure failureStructure,
			Map<String, String> resultMap, Currency currency) {
		if (claimSubmissionDTO.getHandlingFee() != null
				&& claimSubmissionDTO.getHandlingFee().intValue() > 0) {
			if (configuredCostCategoriesCode
					.contains(CostCategory.HANDLING_FEE_CODE)) {
				serviceDetail.setHandlingFee(Money.valueOf(
						claimSubmissionDTO.getHandlingFee(), currency));
			} else {
				resultMap
						.put(DealerInterfaceErrorConstants.CSA0142,
								dealerInterfaceErrorConstants
										.getPropertyMessageFromErrorCode(DealerInterfaceErrorConstants.CSA0142));
			}
		}

	}

	private void setTransporationInformation(
			List<String> configuredCostCategoriesCode,
			ClaimSubmission claimSubmissionDTO, ServiceDetail serviceDetail,
			Claim claim, FailureStructure failureStructure,
			Map<String, String> resultMap, Currency currency) {
		if (claimSubmissionDTO.getTransportation() != null
				&& claimSubmissionDTO.getTransportation().intValue() > 0) {
			if (configuredCostCategoriesCode
					.contains(CostCategory.TRANSPORTATION_COST_CATEGORY_CODE)) {
				serviceDetail.setTransportationAmt(Money.valueOf(
						claimSubmissionDTO.getTransportation(), currency));
			} else {
				resultMap
						.put(DealerInterfaceErrorConstants.CSA0141,
								dealerInterfaceErrorConstants
										.getPropertyMessageFromErrorCode(DealerInterfaceErrorConstants.CSA0141));
			}
		}
		serviceDetail.setInvoiceAvailable(claimSubmissionDTO
				.getNoInvoiceAvalibleForTransportation());
	}

	private void validateAndSetOtherFreightAndDuty(
			List<String> configuredCostCategoriesCode,
			Map<String, String> resultMap, ServiceDetail serviceDetail,
			ClaimSubmission claimSubmissionDTO, Currency currency) {
		if (claimSubmissionDTO.getOtherFreightAndDuty() != null
				&& !(claimSubmissionDTO.getOtherFreightAndDuty().intValue() == 0)) {
			if (configuredCostCategoriesCode
					.contains(CostCategory.OTHER_FREIGHT_DUTY_COST_CATEGORY_CODE)) {
				serviceDetail.setOtherFreightDutyExpense(Money.valueOf(
						claimSubmissionDTO.getOtherFreightAndDuty(), currency));
			} else {
				resultMap
						.put(DealerInterfaceErrorConstants.CSA0100,
								dealerInterfaceErrorConstants
										.getPropertyMessageFromErrorCode(DealerInterfaceErrorConstants.CSA0100));
			}
		}
	}

	private void validateAndSetOthers(
			List<String> configuredCostCategoriesCode,
			Map<String, String> resultMap, ServiceDetail serviceDetail,
			ClaimSubmission claimSubmissionDTO, Currency currency) {
		if (claimSubmissionDTO.getOthers() != null
				&& !(claimSubmissionDTO.getOthers().intValue() == 0)) {
			if (configuredCostCategoriesCode
					.contains(CostCategory.OTHERS_CATEGORY_CODE)) {
				serviceDetail.setOthersExpense(Money.valueOf(
						claimSubmissionDTO.getOthers(), currency));
			} else {
				resultMap
						.put(DealerInterfaceErrorConstants.CSA099,
								dealerInterfaceErrorConstants
										.getPropertyMessageFromErrorCode(DealerInterfaceErrorConstants.CSA099));
			}
		}
	}

	private void validateAndSetMeals(List<String> configuredCostCategoriesCode,
			Map<String, String> resultMap, ServiceDetail serviceDetail,
			ClaimSubmission claimSubmissionDTO, Currency currency) {
		if (claimSubmissionDTO.getMeals() != null
				&& !(claimSubmissionDTO.getMeals().intValue() == 0)) {
			if (configuredCostCategoriesCode
					.contains(CostCategory.MEALS_HOURS_COST_CATEGORY_CODE)) {
				serviceDetail.setMealsExpense(Money.valueOf(
						claimSubmissionDTO.getMeals(), currency));
			} else {
				resultMap
						.put(DealerInterfaceErrorConstants.CSA098,
								dealerInterfaceErrorConstants
										.getPropertyMessageFromErrorCode(DealerInterfaceErrorConstants.CSA098));
			}
		}
	}

	private void validateAndSetItemFreightAndDuty(
			List<String> configuredCostCategoriesCode,
			Map<String, String> resultMap, ServiceDetail serviceDetail,
			ClaimSubmission claimSubmissionDTO, Currency currency) {
		if (claimSubmissionDTO.getItemFreightAndDuty() != null
				&& !(claimSubmissionDTO.getItemFreightAndDuty().intValue() == 0)) {
			if (configuredCostCategoriesCode
					.contains(CostCategory.FREIGHT_DUTY_CATEGORY_CODE)) {
				serviceDetail.setItemFreightAndDuty(Money.valueOf(
						claimSubmissionDTO.getItemFreightAndDuty(), currency));
			} else {
				resultMap
						.put(DealerInterfaceErrorConstants.CSA097,
								dealerInterfaceErrorConstants
										.getPropertyMessageFromErrorCode(DealerInterfaceErrorConstants.CSA097));
			}

		}
	}

	private void validateAndSetPerdim(
			List<String> configuredCostCategoriesCode,
			Map<String, String> resultMap, ServiceDetail serviceDetail,
			ClaimSubmission claimSubmissionDTO, Currency currency) {
		if (claimSubmissionDTO.getPerDiem() != null
				&& !(claimSubmissionDTO.getPerDiem().intValue() == 0)) {
			if (configuredCostCategoriesCode
					.contains(CostCategory.PER_DIEM_COST_CATEGORY_CODE)) {
				serviceDetail.setPerDiem(Money.valueOf(
						claimSubmissionDTO.getPerDiem(), currency));
			} else {
				resultMap
						.put(DealerInterfaceErrorConstants.CSA096,
								dealerInterfaceErrorConstants
										.getPropertyMessageFromErrorCode(DealerInterfaceErrorConstants.CSA096));
			}
		}
	}

	private void validateAndSetParkingExpenses(
			List<String> configuredCostCategoriesCode,
			Map<String, String> resultMap, ServiceDetail serviceDetail,
			ClaimSubmission claimSubmissionDTO, Currency currency) {
		if (claimSubmissionDTO.getParking() != null
				&& !(claimSubmissionDTO.getParking().intValue() == 0)) {
			if (configuredCostCategoriesCode
					.contains(CostCategory.PARKING_COST_CATEGORY_CODE)) {
				serviceDetail.setParkingAndTollExpense(Money.valueOf(
						claimSubmissionDTO.getParking(), currency));
			} else {
				resultMap
						.put(DealerInterfaceErrorConstants.CSA095,
								dealerInterfaceErrorConstants
										.getPropertyMessageFromErrorCode(DealerInterfaceErrorConstants.CSA095));
			}
		}
	}

	private void validateAndSetLocalPurchases(
			List<String> configuredCostCategoriesCode,
			Map<String, String> resultMap, ServiceDetail serviceDetail,
			ClaimSubmission claimSubmissionDTO, Currency currency) {
		if (claimSubmissionDTO.getLocalPurchase() != null
				&& !(claimSubmissionDTO.getLocalPurchase().intValue() == 0)) {
			if (configuredCostCategoriesCode
					.contains(CostCategory.LOCAL_PURCHASE_COST_CATEGORY_CODE)) {
				serviceDetail.setLocalPurchaseExpense(Money.valueOf(
						claimSubmissionDTO.getLocalPurchase(), currency));
			} else {
				resultMap
						.put(DealerInterfaceErrorConstants.CSA094,
								dealerInterfaceErrorConstants
										.getPropertyMessageFromErrorCode(DealerInterfaceErrorConstants.CSA094));
			}
		}
	}

	private void validateAndSetTolls(List<String> configuredCostCategoriesCode,
			Map<String, String> resultMap, ServiceDetail serviceDetail,
			ClaimSubmission claimSubmissionDTO, Currency currency) {
		if (claimSubmissionDTO.getTolls() != null
				&& !(claimSubmissionDTO.getTolls().intValue() == 0)) {
			if (configuredCostCategoriesCode
					.contains(CostCategory.TOLLS_COST_CATEGORY_CODE)) {
				serviceDetail.setTollsExpense(Money.valueOf(
						claimSubmissionDTO.getTolls(), currency));
			} else {
				resultMap
						.put(DealerInterfaceErrorConstants.CSA093,
								dealerInterfaceErrorConstants
										.getPropertyMessageFromErrorCode(DealerInterfaceErrorConstants.CSA093));
			}
		}
	}

	private void validateAndSetRentalCharges(
			List<String> configuredCostCategoriesCode,
			Map<String, String> resultMap, ServiceDetail serviceDetail,
			ClaimSubmission claimSubmissionDTO, Currency currency) {
		if (claimSubmissionDTO.getRentalCharges() != null
				&& !(claimSubmissionDTO.getRentalCharges().intValue() == 0)) {
			if (configuredCostCategoriesCode
					.contains(CostCategory.RENTAL_CHARGES_COST_CATEGORY_CODE)) {
				serviceDetail.setRentalCharges(Money.valueOf(
						claimSubmissionDTO.getRentalCharges(), currency));
			} else {
				resultMap
						.put(DealerInterfaceErrorConstants.CSA092,
								dealerInterfaceErrorConstants
										.getPropertyMessageFromErrorCode(DealerInterfaceErrorConstants.CSA092));
			}
		}
	}

	private void setLaborInformation(List<String> configuredCostCategoriesCode,
			ClaimSubmission claimSubmissionDTO, ServiceDetail serviceDetail,
			Claim claim, FailureStructure failureStructure,
			Map<String, String> claimPage2errorCodeMap) {
		if ((claim.getType().PARTS.equals(claim.getType()))&&!claim.getItemReference().isSerialized()
				&& (claimSubmissionDTO.getCompetitorModel() != null && !StringUtils
						.isEmpty(claimSubmissionDTO.getCompetitorModel()))) {
			setLaborDetails(configuredCostCategoriesCode, claimSubmissionDTO,
					serviceDetail, claimPage2errorCodeMap);
		} else {
			setLaborDetails(configuredCostCategoriesCode, claimSubmissionDTO,
					serviceDetail, claim, failureStructure,
					claimPage2errorCodeMap);
		}
		validateClaimForDuplicateJobCodes(claim, claimPage2errorCodeMap);
	}

	void setLaborDetails(List<String> configuredCostCategoriesCode,
			ClaimSubmission claimSubmissionDTO, ServiceDetail serviceDetail,
			Map<String, String> errorCodeMap) {
		JobCodes jobCodesType = claimSubmissionDTO.getJobCodes();
		if (jobCodesType != null) {
			EachJobCode[] eachJobCode = jobCodesType.getEachJobCodeArray();
			if (eachJobCode != null
					&& (eachJobCode.length > 1 || StringUtils
							.isNotBlank(eachJobCode[0].getJobCode()))) {
				errorCodeMap
						.put(DealerInterfaceErrorConstants.CSA079,
								dealerInterfaceErrorConstants
										.getPropertyMessageFromErrorCode(DealerInterfaceErrorConstants.CSA079));
			}
		}
		BigDecimal totalLaborHrs = claimSubmissionDTO.getTotalLaborHours();
		LaborDetail laborDetail = new LaborDetail();
		if (totalLaborHrs != null && totalLaborHrs.intValue() != 0) {
			if (totalLaborHrs.signum() == 1) {
				laborDetail.setHoursSpent(totalLaborHrs);
			} else {
				errorCodeMap
						.put(DealerInterfaceErrorConstants.CSA080,
								dealerInterfaceErrorConstants
										.getPropertyMessageFromErrorCode(DealerInterfaceErrorConstants.CSA080));
			}
		}
		serviceDetail.getLaborPerformed().add(laborDetail);
	}

	private Map<String, String> setLaborDetails(
			List<String> configuredCostCategoriesCode,
			ClaimSubmission claimSubmissionDTO, ServiceDetail serviceDetail,
			Claim claim, FailureStructure failureStructure,
			Map<String, String> errorCodeMap) {
		JobCodes jobCodesType = claimSubmissionDTO.getJobCodes();
		if (jobCodesType != null) {
			EachJobCode[] eachJobCode = jobCodesType.getEachJobCodeArray();
			if (eachJobCode != null) {
				if (configuredCostCategoriesCode
						.contains(CostCategory.LABOR_COST_CATEGORY_CODE)) {
					if (!configParamService
							.getBooleanValue(ConfigName.MULTIPLE_JOB_CODE_ALLOWED
									.getName())
							&& eachJobCode.length > 1) {
						errorCodeMap
								.put(DealerInterfaceErrorConstants.CSA075,
										dealerInterfaceErrorConstants
												.getPropertyMessageFromErrorCode(DealerInterfaceErrorConstants.CSA075));
					} else {
						setLaborDeatilsForClaim(serviceDetail, eachJobCode,
								failureStructure, errorCodeMap, claim);
					}
				} else {
					errorCodeMap
							.put(DealerInterfaceErrorConstants.CSA0133,
									dealerInterfaceErrorConstants
											.getPropertyMessageFromErrorCode(DealerInterfaceErrorConstants.CSA0133));
				}

			}

		}
		return errorCodeMap;
	}

	private void setLaborDeatilsForClaim(ServiceDetail serviceDetail,
			EachJobCode[] eachJobCode, FailureStructure failureStructure,
			Map<String, String> errorCodeMap, Claim claim) {
		for (int index = 0; index < eachJobCode.length; index++) {
			String jobCode = eachJobCode[index].getJobCode();
			if (StringUtils.isNotBlank(jobCode)) {
				if (failureStructure == null) {
					errorCodeMap
							.put(DealerInterfaceErrorConstants.CSA076,
									dealerInterfaceErrorConstants
											.getPropertyMessageFromErrorCode(DealerInterfaceErrorConstants.CSA076));
				} else {
					LaborDetail laborDetail = new LaborDetail();
					BigDecimal additionalLabourHours = eachJobCode[index]
							.getLabourHours();
					ServiceProcedure serviceProcedure = failureStructure
							.findSeriveProcedure(jobCode);
					if (serviceProcedure == null) {
						errorCodeMap
								.put(DealerInterfaceErrorConstants.CSA077,
										dealerInterfaceErrorConstants
												.getPropertyMessage(
														DealerInterfaceErrorConstants.CSA077,
														new String[] { jobCode }));
					} else {
						laborDetail.setServiceProcedure(serviceProcedure);
						if (additionalLabourHours != null
								&& additionalLabourHours.intValue()>0) {
							if (isEligibleForAdditionalLaborDetails(claim)) {
								laborDetail
										.setAdditionalLaborHours(additionalLabourHours);
								String reasonForAdditionalHrs = eachJobCode[index]
										.getReasonForLabourHours();
								if (StringUtils.isBlank(reasonForAdditionalHrs)) {
									laborDetail
											.setReasonForAdditionalHours(DEAFULT_REASON_FOR_ADDITIONAL_LABOR);
								} else {
									laborDetail
											.setReasonForAdditionalHours(reasonForAdditionalHrs);
								}
							} else {
								errorCodeMap
										.put(DealerInterfaceErrorConstants.CSA078,
												dealerInterfaceErrorConstants
														.getPropertyMessageFromErrorCode(DealerInterfaceErrorConstants.CSA078));
							}
						} 
						serviceDetail.getLaborPerformed().add(laborDetail);
					}
				}
			}
		}
	}

	public boolean isEligibleForAdditionalLaborDetails(Claim claim) {
		boolean isEligible = false;
		AdditionalLaborEligibility additionalLaborEligibility = additionalLaborService
				.findAddditionalLabourEligibility();
		if (additionalLaborEligibility != null) {
			List<ServiceProvider> serviceProviders = additionalLaborEligibility
					.getServiceProviders();
			if ((serviceProviders == null || serviceProviders.isEmpty())
					|| (!serviceProviders.isEmpty() && serviceProviders
							.contains(claim.getForDealer()))) {
				isEligible = true;
			}
		}
		return isEligible;
	}

	private void validateAndSetMislaniousParts(
			List<String> configuredCostCategoriesCode,
			Map<String, String> resultMap, ServiceDetail serviceDetail,
			ClaimSubmission claimSubmissionDTO, Currency currency) {
		// TODO Auto-generated method stub

	}

	public void validateClaimForDuplicateJobCodes(Claim claim,
			Map<String, String> errorCodeMap) {
		Set<String> s = new HashSet<String>();
		ServiceInformation serviceInformation = claim.getServiceInformation();
		boolean duplicateJobCodes = false;
		if (serviceInformation != null) {
			List<LaborDetail> laborDetails = serviceInformation
					.getServiceDetail().getLaborPerformed();
			if (laborDetails != null) {
				for (int i = 0; i < laborDetails.size(); i++) {
					ServiceProcedure serviceProcedure = laborDetails.get(i)
							.getServiceProcedure();
					if (serviceProcedure != null
							&& serviceProcedure.getDefinedFor() != null
							&& !s.add(serviceProcedure.getDefinition()
									.getCode())) {
						duplicateJobCodes = true;
						break;

					}
				}
			}
		}

		if (duplicateJobCodes) {
			errorCodeMap
					.put(DealerInterfaceErrorConstants.CSA0105,
							dealerInterfaceErrorConstants
									.getPropertyMessageFromErrorCode(DealerInterfaceErrorConstants.CSA0105));
		}
	}

	public DealerInterfaceErrorConstants getDealerInterfaceErrorConstants() {
		return dealerInterfaceErrorConstants;
	}

	public void setDealerInterfaceErrorConstants(
			DealerInterfaceErrorConstants dealerInterfaceErrorConstants) {
		this.dealerInterfaceErrorConstants = dealerInterfaceErrorConstants;
	}

	public ConfigParamService getConfigParamService() {
		return configParamService;
	}

	public void setConfigParamService(ConfigParamService configParamService) {
		this.configParamService = configParamService;
	}

	public ClaimSubmissionTransformer getClaimSubmissionTransformer() {
		return claimSubmissionTransformer;
	}

	public void setClaimSubmissionTransformer(
			ClaimSubmissionTransformer claimSubmissionTransformer) {
		this.claimSubmissionTransformer = claimSubmissionTransformer;
	}

	public CostCategoryRepository getCostCategoryRepository() {
		return costCategoryRepository;
	}

	public void setCostCategoryRepository(
			CostCategoryRepository costCategoryRepository) {
		this.costCategoryRepository = costCategoryRepository;
	}

	public AdditionalLaborEligibilityService getAdditionalLaborService() {
		return additionalLaborService;
	}

	public void setAdditionalLaborService(
			AdditionalLaborEligibilityService additionalLaborService) {
		this.additionalLaborService = additionalLaborService;
	}

}

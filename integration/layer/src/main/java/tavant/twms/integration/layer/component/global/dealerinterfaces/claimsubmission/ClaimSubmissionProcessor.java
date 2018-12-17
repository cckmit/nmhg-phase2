package tavant.twms.integration.layer.component.global.dealerinterfaces.claimsubmission;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import tavant.twms.domain.campaign.CampaignService;
import tavant.twms.domain.claim.CampaignClaim;
import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.claim.ClaimService;
import tavant.twms.domain.claim.ClaimType;
import tavant.twms.domain.claim.LaborDetail;
import tavant.twms.domain.claim.ServiceDetail;
import tavant.twms.domain.claim.ServiceInformation;
import tavant.twms.domain.claim.claimsubmission.ClaimSubmissionUtil;
import tavant.twms.domain.claim.validation.ValidationResults;
import tavant.twms.domain.configuration.ConfigName;
import tavant.twms.domain.configuration.ConfigParamService;
import tavant.twms.domain.inventory.InventoryItem;
import tavant.twms.domain.laborType.LaborSplit;
import tavant.twms.domain.orgmodel.OrgService;
import tavant.twms.domain.rules.RuleAdministrationService;
import tavant.twms.domain.supplier.contract.ContractService;
import tavant.twms.infra.InstanceOfUtil;
import tavant.twms.integration.layer.constants.DealerInterfaceErrorConstants;
import tavant.twms.process.ClaimProcessService;
import tavant.twms.process.PartReturnProcessingService;

import com.domainlanguage.timeutil.Clock;
import com.tavant.dealerinterfaces.claimsubmission.request.ClaimSubmissionDocument.ClaimSubmission;
import com.tavant.dealerinterfaces.claimsubmission.request.ClaimSubmissionDocument.ClaimSubmission.ComponentsReplaced;

public class ClaimSubmissionProcessor {
	private ClaimSubmissionUtil claimSubmissionUtil;
	private ClaimService claimService;
	private CampaignService campaignService;
	private ClaimProcessService claimProcessService;
	private RuleAdministrationService ruleAdministrationService;
	private ConfigParamService configParamService;
	protected PartReturnProcessingService partReturnProcessingService;
	protected ContractService contractService;
	protected OrgService orgService;
	private DealerInterfaceErrorConstants dealerInterfaceErrorConstants;
	private static final Logger logger = Logger.getLogger("dealerAPILogger");

	public void saveDraft(Claim claim) {
		claim.setFiledOnDate(Clock.today());
		this.claimService.initializeClaim(claim);
		this.claimService.createClaim(claim);
		this.claimProcessService.startClaimProcessingWithTransition(claim,
				"Draft");
		if (InstanceOfUtil.isInstanceOfClass(CampaignClaim.class, claim)) {
			updateCampaignNotifications(claim);
		}
	}

	public void updateClaim(Claim claim, ClaimSubmission claimSubmissionDTO,
			Map<String, String> claimPage2errorCodeMap) {
		Map<String, String[]> errorCodeMap = new HashMap<String, String[]>();
		errorCodeMap.putAll(claimSubmissionUtil.validateClaim(claim, false,
				"Draft", false, "Draft_Claim", false));
		if (errorCodeMap.isEmpty()) {
			claim.setFiledOnDate(Clock.today());
			if (claim.getCompetitorModelBrand() != null
					&& (!claim.getCompetitorModelBrand().isEmpty()
							|| !claim.getCompetitorModelDescription().isEmpty() || !claim
							.getCompetitorModelTruckSerialnumber().isEmpty())) {
				claim.getClaimedItems().get(0).getItemReference()
						.setSerialized(false);
			}
			if (claim.getServiceInformation().getServiceDetail()
					.getHussmanPartsReplacedInstalled() != null
					&& !claim.getServiceInformation().getServiceDetail()
							.getHussmanPartsReplacedInstalled().isEmpty()) {
				claimSubmissionUtil.prepareReplacedInstalledParts(claim);
			}
			claimSubmissionUtil.setTotalQtyForReplacedParts(claim);
			claimSubmissionUtil.setPolicyForClaim(claim);
			if (claim.getApplicablePolicy() != null
					&& claim.getClaimedItems().size() > 0) {
				claim.getClaimedItems().get(0)
						.setApplicablePolicy(claim.getApplicablePolicy());
			}
			claimSubmissionUtil.computePayment(claim, "Draft");
			if (claim.getActiveClaimAudit().getIsPriceFetchDown()
					&& isErrorMessageShowOnEPODown()) {
				errorCodeMap.put("error.newclaim.label.common.epo.system.down",
						null);
			}
			ValidationResults messages = ruleAdministrationService
					.executeClaimEntryValidationRules(claim);
			setValidationResultAsActionMessage(messages, errorCodeMap);
			if (errorCodeMap.isEmpty()) {
				claim.setCanUpdatePayment(true);
				if(claim.getType().PARTS.equals(claim.getType())){
					validateAndRemoveUnusedInstalledOrRemovedParts(claim,claimSubmissionDTO);
				}
				this.claimProcessService.startClaimProcessingWithTransition(
						claim, "Submit Claim");
				if (InstanceOfUtil
						.isInstanceOfClass(CampaignClaim.class, claim)) {
					updateCampaignNotifications(claim);
				} else {
					if (contractService.canAutoInitiateRecovery(claim)) {
						partReturnProcessingService
								.autoStartRecoveryProcess(claim);
					}
				}
			}
		}
		setErrorCodeMapValues(claimPage2errorCodeMap, errorCodeMap);
	}

	private void validateAndRemoveUnusedInstalledOrRemovedParts(Claim claim,
			ClaimSubmission claimSubmissionDTO) {
		if (claimSubmissionDTO.getComponentsReplacedArray() == null
				|| claimSubmissionDTO.getComponentsReplacedArray().length == 0
				|| (claimSubmissionDTO.getComponentsReplacedArray()[0]
						.getNMHGInstalledPartsArray() == null || claimSubmissionDTO
						.getComponentsReplacedArray()[0]
						.getNMHGInstalledPartsArray().length == 0)
				|| (claimSubmissionDTO.getComponentsReplacedArray()[0]
						.getNMHGReplacedPartsArray() != null && claimSubmissionDTO
						.getComponentsReplacedArray()[0]
						.getNMHGReplacedPartsArray().length == 0))
			claim.getServiceInformation().getServiceDetail()
					.getHussmanPartsReplacedInstalled().clear();
	}

	public void setErrorCodeMapValues(
			Map<String, String> claimPage2errorCodeMap,
			Map<String, String[]> errorCodeMap) {
		if (!errorCodeMap.isEmpty()) {
			for (Map.Entry<String, String[]> entry : errorCodeMap.entrySet()) {
				if (entry.getKey() != null
						&& dealerInterfaceErrorConstants
								.getErrorCodeFromKey(entry.getKey()) != null) {
					if (entry.getValue() != null) {
						claimPage2errorCodeMap.put(
								dealerInterfaceErrorConstants
										.getErrorCodeFromKey(entry.getKey()),
								dealerInterfaceErrorConstants
										.getPropertyMessageValue(
												entry.getKey(),
												entry.getValue()));
					}
					claimPage2errorCodeMap.put(dealerInterfaceErrorConstants
							.getErrorCodeFromKey(entry.getKey()),
							dealerInterfaceErrorConstants
									.getPropertyMessage(entry.getKey()));
				} else if (entry.getKey() != null) {
					claimPage2errorCodeMap.put(
							DealerInterfaceErrorConstants.CSA0127,
							entry.getKey());
				}
			}
		}
	}

	public boolean isErrorMessageShowOnEPODown() {
		if (configParamService
				.getBooleanValue(ConfigName.IS_DEALER_ALLOWED_TO_TAKE_ACTIONS_ON_CLAIM_DURING_EPO_DOWN
						.getName())) {
			return false;
		}
		return true;
	}

	private void setValidationResultAsActionMessage(ValidationResults messages,
			Map<String, String[]> errorCodeMap) {
		List<String> errors = messages.getErrors();
		for (String error : errors) {
			if (StringUtils.isNotBlank(error)) {
				int pos1 = error.indexOf(")", 1);
				int pos2 = error.indexOf("[");
				error = error.substring(pos1 + 1, pos2);
				errorCodeMap.put(error, null);
			}
		}
	}

	private void updateCampaignNotifications(Claim claim) {

		try {
			List<InventoryItem> items = new ArrayList<InventoryItem>();
			items.add(claim.getClaimedItems().get(0).getItemReference()
					.getReferredInventoryItem());
			if (claim.getId() != null && claim.getCampaign() != null) {
				campaignService.updateCampaignNotifications(items, claim,
						claim.getCampaign(), "IN PROGRESS");
			}
		} catch (Exception e) {
			logger.error("Error in updateCampaignNotifications", e);
		}

	}

	protected Map<String, String[]> anyPendingFailureReport(Claim claim) {
		Map<String, String[]> errorCodeMap = new HashMap<String, String[]>();
		boolean isReportConfigured = this.claimService
				.isAnyFailureReportPendingOnClaim(claim);
		if (isReportConfigured) {
			errorCodeMap.put("dealerAPI.claim.draftForFailureReport", null);
		}
		return errorCodeMap;
	}

	public Map<String, String> updateClaimDetails(Claim claim,
			ClaimSubmission claimSubmissionDTO, Map<String, String> errorCodeMap) {
		if ((claim.getCompetitorModelBrand() != null
				&& !claim.getCompetitorModelBrand().isEmpty()
				&& !claim.getCompetitorModelDescription().isEmpty() && !claim
				.getCompetitorModelTruckSerialnumber().isEmpty())) {

			claim.getClaimedItems().get(0).getItemReference()
					.setSerialized(false);
		}
		claimSubmissionUtil.setTotalLaborHoursForClaim(claim);
		if (isLaborSplitEnabled()) {
			if (!validateLaborType(claim, errorCodeMap)) {
				return errorCodeMap;
			}
		}
		if (claim.getServiceInformation().getServiceDetail()
				.getHussmanPartsReplacedInstalled() != null
				&& !claim.getServiceInformation().getServiceDetail()
						.getHussmanPartsReplacedInstalled().isEmpty()) {
			claimSubmissionUtil.prepareReplacedInstalledParts(claim);
		}
		claimService.updateClaim(claim);
		return errorCodeMap;
	}

	public boolean isLaborSplitEnabled() {
		return configParamService.getBooleanValue(ConfigName.ENABLE_LABOR_SPLIT
				.getName());
	}

	private boolean validateLaborType(Claim claim,
			Map<String, String> errorCodeMap) {
		ServiceInformation serviceInformation = claim.getServiceInformation();
		ServiceDetail serviceDetail = serviceInformation.getServiceDetail();
		List<LaborDetail> laborDetails = serviceDetail.getLaborPerformed();
		List<LaborSplit> laborSplit = serviceDetail.getLaborSplit();
		BigDecimal totalInclusiveHrs = new BigDecimal(0.0);
		BigDecimal totalStdPlusAdditionalHrs = new BigDecimal(0.0);
		if (laborDetails.size() > 0) {
			for (LaborDetail laborDetail : laborDetails) {
				if (claim.getType().equals(ClaimType.CAMPAIGN)) {
					if (null != laborDetail.getSpecifiedHoursInCampaign()) {
						if (null != laborDetail.getAdditionalLaborHours()) {
							totalStdPlusAdditionalHrs = totalStdPlusAdditionalHrs
									.add(laborDetail
											.getSpecifiedHoursInCampaign()
											.add(laborDetail
													.getAdditionalLaborHours()));
						} else {
							totalStdPlusAdditionalHrs = totalStdPlusAdditionalHrs
									.add(laborDetail
											.getSpecifiedHoursInCampaign());
						}
					} else {
						totalStdPlusAdditionalHrs = totalStdPlusAdditionalHrs
								.add(laborDetail.getTotalHours(serviceDetail
										.getStdLaborEnabled()));
					}

				} else {
					BigDecimal totalHours = laborDetail
							.getTotalHours(serviceDetail.getStdLaborEnabled());
					if (totalHours != null) {
						totalStdPlusAdditionalHrs = totalStdPlusAdditionalHrs
								.add(totalHours);
					}
				}
			}
		}
		if (laborSplit.size() > 0 || claim.getType().equals(ClaimType.CAMPAIGN)) {
			for (LaborSplit lbrSplit : laborSplit) {
				if (lbrSplit != null) {
					if (null == lbrSplit.getLaborType().getLaborType()) {
						errorCodeMap.put("error.laborType.laborTypeName", null);
						return false;
					}

					if (!StringUtils.isBlank(lbrSplit.getReason())) {
						errorCodeMap.put("error.laborType.reason", null);
						return false;
					}

					if (null == lbrSplit.getHoursSpent()
							|| !(lbrSplit.getHoursSpent().signum() == 1)) {
						errorCodeMap.put("error.laborType.hoursSpent", null);
						return false;
					}
					if (lbrSplit.getInclusive()) {
						totalInclusiveHrs = totalInclusiveHrs.add(lbrSplit
								.getHoursSpent());
					}
				}
			}
		}
		if (totalInclusiveHrs.doubleValue() > totalStdPlusAdditionalHrs
				.doubleValue()) {
			errorCodeMap.put("error.laborType.validate.message", null);
			return false;
		}
		return true;
	}

	public void setRuleAdministrationService(
			RuleAdministrationService ruleAdministrationService) {
		this.ruleAdministrationService = ruleAdministrationService;
	}

	public void setCampaignService(CampaignService campaignService) {
		this.campaignService = campaignService;
	}

	public void setClaimService(ClaimService claimService) {
		this.claimService = claimService;
	}

	public void setClaimSubmissionUtil(ClaimSubmissionUtil claimSubmissionUtil) {
		this.claimSubmissionUtil = claimSubmissionUtil;
	}

	public void setClaimProcessService(ClaimProcessService claimProcessService) {
		this.claimProcessService = claimProcessService;
	}

	public void setConfigParamService(ConfigParamService configParamService) {
		this.configParamService = configParamService;
	}

	public OrgService getOrgService() {
		return orgService;
	}

	public void setOrgService(OrgService orgService) {
		this.orgService = orgService;
	}

	public PartReturnProcessingService getPartReturnProcessingService() {
		return partReturnProcessingService;
	}

	public void setPartReturnProcessingService(
			PartReturnProcessingService partReturnProcessingService) {
		this.partReturnProcessingService = partReturnProcessingService;
	}

	public ContractService getContractService() {
		return contractService;
	}

	public void setContractService(ContractService contractService) {
		this.contractService = contractService;
	}

	public DealerInterfaceErrorConstants getDealerInterfaceErrorConstants() {
		return dealerInterfaceErrorConstants;
	}

	public void setDealerInterfaceErrorConstants(
			DealerInterfaceErrorConstants dealerInterfaceErrorConstants) {
		this.dealerInterfaceErrorConstants = dealerInterfaceErrorConstants;
	}
}

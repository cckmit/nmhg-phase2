package tavant.twms.integration.layer.component.global.dealerinterfaces.claimsubmission;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.IncorrectResultSizeDataAccessException;

import tavant.twms.domain.catalog.BrandItem;
import tavant.twms.domain.catalog.CatalogService;
import tavant.twms.domain.catalog.ItemGroup;
import tavant.twms.domain.catalog.ItemReference;
import tavant.twms.domain.claim.CampaignClaim;
import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.claim.ClaimType;
import tavant.twms.domain.claim.MachineClaim;
import tavant.twms.domain.claim.PartsClaim;
import tavant.twms.domain.claim.ServiceInformation;
import tavant.twms.domain.common.ListOfValues;
import tavant.twms.domain.common.LovRepository;
import tavant.twms.domain.common.SmrReason;
import tavant.twms.domain.configuration.ConfigName;
import tavant.twms.domain.configuration.ConfigParamService;
import tavant.twms.domain.orgmodel.Address;
import tavant.twms.domain.orgmodel.Dealership;
import tavant.twms.domain.orgmodel.OrgService;
import tavant.twms.domain.orgmodel.Organization;
import tavant.twms.domain.orgmodel.OrganizationAddress;
import tavant.twms.domain.orgmodel.ServiceProvider;
import tavant.twms.domain.policy.Customer;
import tavant.twms.domain.policy.CustomerService;
import tavant.twms.domain.policy.RegisteredPolicy;
import tavant.twms.domain.policy.RegisteredPolicyStatusType;
import tavant.twms.domain.policy.Warranty;
import tavant.twms.domain.policy.WarrantyType;
import tavant.twms.infra.HibernateCast;
import tavant.twms.infra.InstanceOfUtil;
import tavant.twms.integration.layer.constants.DealerInterfaceErrorConstants;
import tavant.twms.integration.layer.constants.IntegrationConstants;
import tavant.twms.integration.layer.util.CalendarUtil;
import tavant.twms.integration.layer.util.IntegrationLayerUtil;
import tavant.twms.security.SecurityHelper;
import tavant.twms.security.SelectedBusinessUnitsHolder;
import tavant.twms.domain.catalog.Item;
import tavant.twms.domain.failurestruct.Assembly;
import tavant.twms.domain.failurestruct.FailureCauseDefinition;
import tavant.twms.domain.failurestruct.FailureRootCauseDefinition;
import tavant.twms.domain.failurestruct.FailureStructure;
import tavant.twms.domain.failurestruct.FailureStructureService;
import tavant.twms.domain.failurestruct.FailureTypeDefinition;
import tavant.twms.domain.failurestruct.FaultCode;
import tavant.twms.domain.inventory.InventoryItem;

import com.domainlanguage.time.CalendarDate;
import com.tavant.dealerinterfaces.claimsubmission.request.ClaimSubmissionDocument.ClaimSubmission;
import com.tavant.dealerinterfaces.claimsubmission.request.ClaimSubmissionDocument.ClaimSubmission.CustomerDetails;

public class ClaimSubmssionValidator {

	private DealerInterfaceErrorConstants dealerInterfaceErrorConstants;

	private SecurityHelper securityHelper;

	private ConfigParamService configParamService;

	private LovRepository lovRepository;

	private CatalogService catalogService;

	protected OrgService orgService;

	private CustomerService customerService;

	private FailureStructureService failureStructureService;

	private static final String CASUAL_PART_ITEM_TYPE = "causalpart";

	private static final String REPLACED_PART_ITEM_TYPE = "replacedpart";

	private static final String PARTS_CLAIM_ITEM_TYPE = "partsClaimItemType";

	private static final String CLAIM_ID = "claimId";

	private final Logger logger = Logger.getLogger("dealerAPILogger");

	public void validateCommonFeild(ClaimSubmission claimSubmissionDTO,
			Map<String, String> errorCodeMap) {
		if (claimSubmissionDTO.getBUName().toString() == null
				|| claimSubmissionDTO.getBUName().toString().isEmpty()) {
			errorCodeMap
					.put(DealerInterfaceErrorConstants.CSA001,
							dealerInterfaceErrorConstants
									.getPropertyMessageFromErrorCode(DealerInterfaceErrorConstants.CSA001));
		}
		if (claimSubmissionDTO.getDealerNumber() == null
				|| claimSubmissionDTO.getDealerNumber().isEmpty()) {
			errorCodeMap
					.put(DealerInterfaceErrorConstants.CSA002,
							dealerInterfaceErrorConstants
									.getPropertyMessageFromErrorCode(DealerInterfaceErrorConstants.CSA002));
		}
		if ((claimSubmissionDTO.getRepairStartDate() == null) || (claimSubmissionDTO.getRepairStartDate().get(Calendar.YEAR)<1970)) { //NMHGSLMS-1270 , start date not to pickup 0001-01-01
			errorCodeMap
					.put(DealerInterfaceErrorConstants.CSA003,
							dealerInterfaceErrorConstants
									.getPropertyMessageFromErrorCode(DealerInterfaceErrorConstants.CSA003));
		}
		if ((claimSubmissionDTO.getRepairEndDate() == null)  || (claimSubmissionDTO.getRepairEndDate().get(Calendar.YEAR)<1970)) { //NMHGSLMS-1270 , end date not to pickup 0001-01-01
			errorCodeMap
					.put(DealerInterfaceErrorConstants.CSA004,
							dealerInterfaceErrorConstants
									.getPropertyMessageFromErrorCode(DealerInterfaceErrorConstants.CSA004));
		}
		if (claimSubmissionDTO.getClaimType() == null
				|| claimSubmissionDTO.getClaimType().toString().isEmpty()) {
			errorCodeMap
					.put(DealerInterfaceErrorConstants.CSA005,
							dealerInterfaceErrorConstants
									.getPropertyMessageFromErrorCode(DealerInterfaceErrorConstants.CSA005));
		}
		if (claimSubmissionDTO.getHoursOnTruck() == null) {
			errorCodeMap
					.put(DealerInterfaceErrorConstants.CSA026,
							dealerInterfaceErrorConstants
									.getPropertyMessageFromErrorCode(DealerInterfaceErrorConstants.CSA026));
		}

	}

	private String getBuName(String buCode) {
		if (buCode.equalsIgnoreCase(IntegrationConstants.US)) {
			SelectedBusinessUnitsHolder
					.setSelectedBusinessUnit(IntegrationConstants.NMHG_US);
		} else if (buCode.equalsIgnoreCase(IntegrationConstants.EMEA)) {
			SelectedBusinessUnitsHolder
					.setSelectedBusinessUnit(IntegrationConstants.NMHG_EMEA);
		}
		return SelectedBusinessUnitsHolder.getSelectedBusinessUnit();
	}

	public void validateAndSetClaimPgae1Data(
			ClaimSubmission claimSubmissionDTO, Claim claim,
			Map<String, String> errorCodeMap) {
		validateCommonFeild(claimSubmissionDTO, errorCodeMap);
		claim.getBusinessUnitInfo().setName(
				getBuName(claimSubmissionDTO.getBUName().toString()));
		errorCodeMap.putAll(validateAndSetCommonDateFeilds(claimSubmissionDTO,
				claim));
		if (claimSubmissionDTO.getHoursOnTruck() != null
				&& claimSubmissionDTO.getHoursOnTruck().signum() == -1) {
			errorCodeMap
					.put(DealerInterfaceErrorConstants.CSA031,
							dealerInterfaceErrorConstants
									.getPropertyMessageFromErrorCode(DealerInterfaceErrorConstants.CSA031));
		} else {
			claim.getClaimedItems().get(0)
					.setHoursInService(claimSubmissionDTO.getHoursOnTruck());
		}
		if (claimSubmissionDTO.getBT30DayNCR()
				&& !(InstanceOfUtil
						.isInstanceOfClass(MachineClaim.class, claim))) {
			errorCodeMap
					.put(DealerInterfaceErrorConstants.CSA032,
							dealerInterfaceErrorConstants
									.getPropertyMessageFromErrorCode(DealerInterfaceErrorConstants.CSA032));
		}
		if (InstanceOfUtil.isInstanceOfClass(MachineClaim.class, claim)) {
			validateMachineClaimPage1Data(claimSubmissionDTO, claim,
					errorCodeMap);
		}
		if (InstanceOfUtil.isInstanceOfClass(PartsClaim.class, claim)) {
			validatePartsClaimPage1Data(claimSubmissionDTO, claim, errorCodeMap);

		}
		validateAndSetSmrClaim(claimSubmissionDTO, claim, errorCodeMap);
		if (claimSubmissionDTO.getAuthorizationNumber() != null
				&& StringUtils.isNotEmpty(claimSubmissionDTO
						.getAuthorizationNumber())) {
			claim.setAuthNumber(claimSubmissionDTO.getAuthorizationNumber());
			claim.setCmsAuthCheck(true);
		}
	}

	private void validatePartsClaimPage1Data(
			ClaimSubmission claimSubmissionDTO, Claim claim,
			Map<String, String> errorCodeMap) {
		validateAndSetClaimBrand(claimSubmissionDTO,claim,errorCodeMap);
		
		if (claimSubmissionDTO.getPartNumber() == null

		|| StringUtils.isBlank(claimSubmissionDTO.getPartNumber())) {
			errorCodeMap
					.put(DealerInterfaceErrorConstants.CSA047,
							dealerInterfaceErrorConstants
									.getPropertyMessageFromErrorCode(DealerInterfaceErrorConstants.CSA047));
		} else {
			setPartItemNumber(claim, claimSubmissionDTO.getPartNumber(),
					PARTS_CLAIM_ITEM_TYPE, errorCodeMap);
		}
		if (claimSubmissionDTO.getPartFittedDate() == null) {
			errorCodeMap
					.put(DealerInterfaceErrorConstants.CSA043,
							dealerInterfaceErrorConstants
									.getPropertyMessageFromErrorCode(DealerInterfaceErrorConstants.CSA043));
		} else if (claim.getFailureDate() != null
				&& CalendarUtil.convertToCalendarDate(
						claimSubmissionDTO.getPartFittedDate()).isAfter(
						claim.getFailureDate())) {
			errorCodeMap
					.put(DealerInterfaceErrorConstants.CSA044,
							dealerInterfaceErrorConstants
									.getPropertyMessageFromErrorCode(DealerInterfaceErrorConstants.CSA044));
		} else {
			claim.setInstallationDate(CalendarUtil
					.convertToCalendarDate(claimSubmissionDTO
							.getPartFittedDate()));
		}
		if (!claim.getItemReference().isSerialized()
				&& claim.getCompetitorModelDescription() == null
				&& (claimSubmissionDTO.getComponentDateCode() == null || StringUtils
						.isBlank(claimSubmissionDTO.getComponentDateCode()))) {
			errorCodeMap
					.put(DealerInterfaceErrorConstants.CSA040,
							dealerInterfaceErrorConstants
									.getPropertyMessageFromErrorCode(DealerInterfaceErrorConstants.CSA040));
		} else {
			claim.setDateCode(claimSubmissionDTO.getComponentDateCode());
		}
		validateAndSetHoursInformation(claimSubmissionDTO, claim, errorCodeMap);
		if (claim.getCompetitorModelDescription() != null) {
			validateAndSetCompetetorModelInformation(claimSubmissionDTO, claim,
					errorCodeMap);
		}
		validatePartsClaimforStandardPolicy(claim, errorCodeMap);
	}

	private void validateAndSetClaimBrand(ClaimSubmission claimSubmissionDTO,
			Claim claim, Map<String, String> errorCodeMap) {
		if (((claimSubmissionDTO.getBrand() == null || StringUtils
				.isBlank(claimSubmissionDTO.getBrand().toString()))
				&& claim.getItemReference().isSerialized() && IntegrationLayerUtil
				.isAMERBusinessUnit(claim.getBusinessUnitInfo().getName()))
				|| ((claimSubmissionDTO.getBrand() == null || StringUtils
						.isBlank(claimSubmissionDTO.getBrand().toString()))
				&& !IntegrationLayerUtil.isAMERBusinessUnit(claim
						.getBusinessUnitInfo().getName()))) {
			errorCodeMap
					.put(
							DealerInterfaceErrorConstants.CSA042,
							dealerInterfaceErrorConstants
									.getPropertyMessageFromErrorCode(DealerInterfaceErrorConstants.CSA042));
		} else {
			claim.setBrand(claimSubmissionDTO.getBrand().toString());
		}
	}

	private void validateAndSetCompetetorModelInformation(
			ClaimSubmission claimSubmissionDTO, Claim claim,
			Map<String, String> errorCodeMap) {
		if (claimSubmissionDTO.getCompetitorModelTruckSerialNumber() == null
				|| StringUtils.isBlank(claimSubmissionDTO
						.getCompetitorModelTruckSerialNumber())) {
			errorCodeMap
					.put(DealerInterfaceErrorConstants.CSA011,
							dealerInterfaceErrorConstants
									.getPropertyMessageFromErrorCode(DealerInterfaceErrorConstants.CSA011));
		} else {
			claim.setCompetitorModelTruckSerialnumber(claimSubmissionDTO
					.getCompetitorModelTruckSerialNumber());
		}
		if (claimSubmissionDTO.getBrandOnCompetitorModel() == null
				|| StringUtils.isBlank(claimSubmissionDTO
						.getBrandOnCompetitorModel())) {
			errorCodeMap
					.put(DealerInterfaceErrorConstants.CSA012,
							dealerInterfaceErrorConstants
									.getPropertyMessageFromErrorCode(DealerInterfaceErrorConstants.CSA012));
		} else {
			claim.setCompetitorModelBrand(claimSubmissionDTO
					.getBrandOnCompetitorModel());
		}
	}

	private void validateAndSetHoursInformation(
			ClaimSubmission claimSubmissionDTO, Claim claim,
			Map<String, String> errorCodeMap) {
		if (claimSubmissionDTO.getHoursOnTruckWhenPartInstalled() != null) {
			claim.setHoursOnTruck(claimSubmissionDTO
					.getHoursOnTruckWhenPartInstalled());
		}
		if (claim.getHoursInService() != null
				&& claim.getHoursOnTruck() != null) {
			if (claim.getHoursOnTruck().compareTo(claim.getHoursInService()) == 1) {
				errorCodeMap
						.put(DealerInterfaceErrorConstants.CSA045,
								dealerInterfaceErrorConstants
										.getPropertyMessageFromErrorCode(DealerInterfaceErrorConstants.CSA045));
			} else {
				claim.setHoursOnPart(claim.getHoursInService().subtract(
						claimSubmissionDTO.getHoursOnTruckWhenPartInstalled()));
			}
		}
	}

	private Map<String, String> validateAndSetCommonDateFeilds(
			ClaimSubmission claimSubmissionDTO, Claim claim) {
		Map<String, String> errorCodeMap = new HashMap<String, String>();
		setFailureDate(claimSubmissionDTO, claim, errorCodeMap);
		if (claimSubmissionDTO.getRepairStartDate() != null
				&& claimSubmissionDTO.getRepairEndDate() != null
				&& claimSubmissionDTO.getRepairStartDate().after(
						claimSubmissionDTO.getRepairEndDate())) {
			errorCodeMap
					.put(DealerInterfaceErrorConstants.CSA006,
							dealerInterfaceErrorConstants
									.getPropertyMessageFromErrorCode(DealerInterfaceErrorConstants.CSA006));
		}
		if (claimSubmissionDTO.getRepairStartDate() != null
				&& claimSubmissionDTO.getFailureDate() != null
				&& (claimSubmissionDTO.getRepairStartDate().before(
						claimSubmissionDTO.getFailureDate()) && !InstanceOfUtil
						.isInstanceOfClass(CampaignClaim.class, claim))) {
			errorCodeMap
					.put(DealerInterfaceErrorConstants.CSA028,
							dealerInterfaceErrorConstants
									.getPropertyMessageFromErrorCode(DealerInterfaceErrorConstants.CSA028));
		}
		if (claimSubmissionDTO.getRepairEndDate() != null
				&& claimSubmissionDTO.getFailureDate() != null
				&& (claimSubmissionDTO.getRepairEndDate().before(
						claimSubmissionDTO.getFailureDate()) && !InstanceOfUtil
						.isInstanceOfClass(CampaignClaim.class, claim))) {
			errorCodeMap
					.put(DealerInterfaceErrorConstants.CSA029,
							dealerInterfaceErrorConstants
									.getPropertyMessageFromErrorCode(DealerInterfaceErrorConstants.CSA029));
		}
		if(claimSubmissionDTO.getRepairEndDate() != null&&claimSubmissionDTO.getRepairEndDate().after(
				Calendar.getInstance())){
			errorCodeMap
			.put(DealerInterfaceErrorConstants.CSA0149,
					dealerInterfaceErrorConstants
							.getPropertyMessageFromErrorCode(DealerInterfaceErrorConstants.CSA0149));
		}
		if(claimSubmissionDTO.getRepairStartDate() != null&&claimSubmissionDTO.getRepairStartDate().after(
				Calendar.getInstance())){
			errorCodeMap
			.put(DealerInterfaceErrorConstants.CSA0150,
					dealerInterfaceErrorConstants
							.getPropertyMessageFromErrorCode(DealerInterfaceErrorConstants.CSA0150));
		}
		if (!InstanceOfUtil.isInstanceOfClass(CampaignClaim.class, claim)
				&& errorCodeMap.isEmpty()) {
			claim.setRepairStartDate(CalendarUtil
					.convertToCalendarDate(claimSubmissionDTO
							.getRepairStartDate()));
			claim.setRepairDate(CalendarUtil
					.convertToCalendarDate(claimSubmissionDTO
							.getRepairEndDate()));
		} else if (errorCodeMap.isEmpty() && claim.getCampaign() != null) {
			setCampaignClaimDates(claimSubmissionDTO, errorCodeMap, claim);
		}
		return errorCodeMap;
	}

	private void setCampaignClaimDates(ClaimSubmission claimSubmissionDTO,
			Map<String, String> errorCodeMap, Claim claim) {
		CalendarDate campaignActiveFrom = claim.getCampaign().getFromDate();
		CalendarDate campaignActiveTill = claim.getCampaign().getTillDate();
		if (CalendarUtil.convertToCalendarDate(
				claimSubmissionDTO.getRepairEndDate()).isBefore(
				campaignActiveFrom)
				|| CalendarUtil.convertToCalendarDate(
						claimSubmissionDTO.getRepairEndDate()).isAfter(
						campaignActiveTill)) {
			errorCodeMap
					.put(DealerInterfaceErrorConstants.CSA030,
							dealerInterfaceErrorConstants
									.getPropertyMessageFromErrorCode(DealerInterfaceErrorConstants.CSA030));
		} else {
			claim.setRepairStartDate(CalendarUtil
					.convertToCalendarDate(claimSubmissionDTO
							.getRepairStartDate()));
			claim.setRepairDate(CalendarUtil
					.convertToCalendarDate(claimSubmissionDTO
							.getRepairEndDate()));
		}
	}

	private void setFailureDate(ClaimSubmission claimSubmissionDTO,
			Claim claim, Map<String, String> errorCodeMap) {
		if (claimSubmissionDTO.getFailureDate() == null
				&& !InstanceOfUtil
						.isInstanceOfClass(CampaignClaim.class, claim)) {
			errorCodeMap
					.put(DealerInterfaceErrorConstants.CSA024,
							dealerInterfaceErrorConstants
									.getPropertyMessageFromErrorCode(DealerInterfaceErrorConstants.CSA024));
		}else if (claimSubmissionDTO.getFailureDate()!=null&&claimSubmissionDTO.getFailureDate().get(Calendar.YEAR)<1970){ //NMHGSLMS-1270 , failure date not to pickup 0001-01-01
			errorCodeMap
			.put(DealerInterfaceErrorConstants.CSA0148,
					dealerInterfaceErrorConstants
							.getPropertyMessageFromErrorCode(DealerInterfaceErrorConstants.CSA0148));
		}
		else if ((claimSubmissionDTO.getFailureDate() != null
				&& InstanceOfUtil.isInstanceOfClass(CampaignClaim.class, claim))) { 
			errorCodeMap
					.put(DealerInterfaceErrorConstants.CSA025,
							dealerInterfaceErrorConstants
									.getPropertyMessageFromErrorCode(DealerInterfaceErrorConstants.CSA025));
		}
		if (claimSubmissionDTO.getFailureDate() != null
				&& claimSubmissionDTO.getFailureDate().after(
						Calendar.getInstance())) {
			errorCodeMap
					.put(DealerInterfaceErrorConstants.CSA027,
							dealerInterfaceErrorConstants
									.getPropertyMessageFromErrorCode(DealerInterfaceErrorConstants.CSA027));
		} else if (!InstanceOfUtil
				.isInstanceOfClass(CampaignClaim.class, claim)
				&& claim.getItemReference().getReferredInventoryItem() != null
				&& claim.getItemReference().getReferredInventoryItem()
						.isRetailed()
				&& claim.getItemReference().getReferredInventoryItem()
						.getDeliveryDate() != null
				&& claimSubmissionDTO.getFailureDate() != null
				&& CalendarUtil.convertToCalendarDate(
						claimSubmissionDTO.getFailureDate()).isBefore(
						claim.getItemReference().getReferredInventoryItem()
								.getDeliveryDate())) {
			errorCodeMap
					.put(DealerInterfaceErrorConstants.CSA046,
							dealerInterfaceErrorConstants
									.getPropertyMessageFromErrorCode(DealerInterfaceErrorConstants.CSA046));
		} else {
			claim.setFailureDate(CalendarUtil
					.convertToCalendarDate(claimSubmissionDTO.getFailureDate()));
		}
	}

	private void validateMachineClaimPage1Data(
			ClaimSubmission claimSubmissionDTO, Claim claim,
			Map<String, String> errorCodeMap) {
		ItemReference itemReference = claim.getClaimedItems().get(0)
				.getItemReference();
		if (!itemReference.isSerialized()&&claimSubmissionDTO.getInventorySerialNumber()==null) {
			if (claimSubmissionDTO.getPurchaseDate() == null) {
				errorCodeMap
						.put(DealerInterfaceErrorConstants.CSA038,
								dealerInterfaceErrorConstants
										.getPropertyMessageFromErrorCode(DealerInterfaceErrorConstants.CSA038));
			} else if (claimSubmissionDTO.getPurchaseDate() != null) {
				if (claim.getFailureDate() != null
						&& CalendarUtil.convertToCalendarDate(
								claimSubmissionDTO.getPurchaseDate()).isAfter(
								claim.getFailureDate())) {
					errorCodeMap
							.put(DealerInterfaceErrorConstants.CSA039,
									dealerInterfaceErrorConstants
											.getPropertyMessageFromErrorCode(DealerInterfaceErrorConstants.CSA039));
				} else {
					claim.setPurchaseDate(CalendarUtil
							.convertToCalendarDate(claimSubmissionDTO
									.getPurchaseDate()));
				}
			}
			if (claimSubmissionDTO.getComponentDateCode() == null
					|| StringUtils.isBlank(claimSubmissionDTO
							.getComponentDateCode())) {
				errorCodeMap
						.put(DealerInterfaceErrorConstants.CSA040,
								dealerInterfaceErrorConstants
										.getPropertyMessageFromErrorCode(DealerInterfaceErrorConstants.CSA040));
			} else {
				claim.setDateCode(claimSubmissionDTO.getComponentDateCode());
			}
		}
		if(getConfigParamService().getBooleanValue(
				ConfigName.DISPLAY_NCR_AND_BT_30DAY_NCR_ON_CLAIM_PAGE.getName())){
		validateAndSetNCRClaimDate(claimSubmissionDTO, itemReference, claim,
				errorCodeMap);
		}
	}

	private void validateAndSetNCRClaimDate(ClaimSubmission claimSubmissionDTO,
			ItemReference itemReference, Claim claim,
			Map<String, String> errorCodeMap) {
		if (claimSubmissionDTO.getBT30DayNCR()) {
			if (getLoggedInUsersDealership().isAllowedNCRWith30Days()
					&& itemReference.getReferredInventoryItem() != null
					&& claim.getClaimedItems().get(0).getItemReference()
							.isSerialized()) {
				if (!itemReference.getReferredInventoryItem().isRetailed()) {
					errorCodeMap
							.put(DealerInterfaceErrorConstants.CSA033,
									dealerInterfaceErrorConstants
											.getPropertyMessageFromErrorCode(DealerInterfaceErrorConstants.CSA033));
				} else if (itemReference.getReferredInventoryItem()
						.isRetailed()
						&& itemReference.getReferredInventoryItem()
								.getDeliveryDate() != null

						&& !CalendarUtil.convertToCalendarDate(
								Calendar.getInstance()).isBefore(
								itemReference.getReferredInventoryItem()
										.getDeliveryDate().plusDays(30))) {
					errorCodeMap
							.put(DealerInterfaceErrorConstants.CSA034,
									dealerInterfaceErrorConstants
											.getPropertyMessageFromErrorCode(DealerInterfaceErrorConstants.CSA034));
				} else {
					claim.setNcrWith30Days(true);
				}
			} else if (!claim.getClaimedItems().get(0).getItemReference()
					.isSerialized()
					|| !getLoggedInUsersDealership().isAllowedNCRWith30Days()) {
				errorCodeMap
						.put(DealerInterfaceErrorConstants.CSA032,
								dealerInterfaceErrorConstants
										.getPropertyMessageFromErrorCode(DealerInterfaceErrorConstants.CSA032));
			}
		}
	}

	private void validateAndSetSmrClaim(ClaimSubmission claimSubmissionDTO,
			Claim claim, Map<String, String> errorCodeMap) {
		if (claimSubmissionDTO.getSMRClaim()
				&& !isDealerEligibleToFillSmrClaim()) {
			errorCodeMap.put(DealerInterfaceErrorConstants.CSA035,
					dealerInterfaceErrorConstants.getPropertyMessage(
							DealerInterfaceErrorConstants.CSA035,
							new String[] { claimSubmissionDTO.getDealerNumber() }));
		} else if (claimSubmissionDTO.getSMRClaim()) {
			String reasonForSMRClaim = claimSubmissionDTO
					.getReasonforSMRClaim();
			if (reasonForSMRClaim == null
					|| StringUtils.isBlank(reasonForSMRClaim)) {
				errorCodeMap
						.put(DealerInterfaceErrorConstants.CSA036,
								dealerInterfaceErrorConstants
										.getPropertyMessageFromErrorCode(DealerInterfaceErrorConstants.CSA036));
			} else {
				SmrReason smrReason = (SmrReason) getListOfValue(
						reasonForSMRClaim, "SmrReason");
				if (smrReason == null) {
					errorCodeMap.put(DealerInterfaceErrorConstants.CSA037,
							dealerInterfaceErrorConstants.getPropertyMessage(
									DealerInterfaceErrorConstants.CSA037,
									new String[] { reasonForSMRClaim }));
				} else {
					claim.setReasonForServiceManagerRequest(smrReason);
					claim.setServiceManagerRequest(claimSubmissionDTO
							.getSMRClaim());
				}
			}
		}
	}

	private ListOfValues getListOfValue(String lovDesc, String lovType) {
		ListOfValues listOfValue = null;
		if (StringUtils.isNotBlank(lovDesc)) {
			List<ListOfValues> listOfValues = lovRepository
					.findAllActive(lovType);
			for (ListOfValues lstOfValue : listOfValues) {
				if (lovDesc.equals(lstOfValue.getDescription())) {
					listOfValue = lstOfValue;
					break;
				}
			}
		}
		return listOfValue;
	}

	public boolean isDealerEligibleToFillSmrClaim() {
		boolean isEligible = false;
		Map<String, List<Object>> buValues = getConfigParamService()
				.getValuesForAllBUs(ConfigName.SMR_CLAIM_ALLOWED.getName());
		for (String buName : buValues.keySet()) {
			Boolean booleanValue = new Boolean(buValues.get(buName).get(0)
					.toString());
			if (booleanValue) {
				isEligible = true;
				break;
			}
		}
		return isEligible;
	}

	private ServiceProvider getLoggedInUsersDealership() {
		ServiceProvider loggedInUserOrganization = null;
		Organization userOrg = securityHelper.getLoggedInUser()
				.getBelongsToOrganization();

		if (userOrg != null
				&& InstanceOfUtil.isInstanceOfClass(ServiceProvider.class,
						userOrg)) {
			loggedInUserOrganization = new HibernateCast<ServiceProvider>()
					.cast(userOrg);
		}
		return loggedInUserOrganization;
	}

	private void validatePartsClaimforStandardPolicy(Claim claim,
			Map<String, String> errorCodeMap) {
		boolean isStandardWarrantyExist = false;
		if (claim.getType().equals(ClaimType.PARTS)) {
			if (claim.getClaimedItems().get(0).getItemReference()
					.getReferredInventoryItem() != null) {
				List<RegisteredPolicy> policies = listApplicablePolicies(claim
						.getClaimedItems().get(0).getItemReference()
						.getReferredInventoryItem(), claim);
				if (!policies.isEmpty()) {
					for (RegisteredPolicy policy : policies) {
						if (policy.getWarrantyType().getType().equals(
								WarrantyType.STANDARD.getType())) {
							isStandardWarrantyExist = true;
							break;
						}
					}
				}
			}
			if (isStandardWarrantyExist
					&& configParamService
							.getBooleanValue(ConfigName.VALIDATE_STD_WRNTY_ON_PARTS_CLAIMS
									.getName())) {
				errorCodeMap
						.put(
								DealerInterfaceErrorConstants.CSA055,
								dealerInterfaceErrorConstants
										.getPropertyMessageFromErrorCode(DealerInterfaceErrorConstants.CSA055));
			}
		}
	}

	private List<RegisteredPolicy> listApplicablePolicies(
			InventoryItem inventoryItem, Claim claim) {
		List<RegisteredPolicy> existingPolices = new ArrayList<RegisteredPolicy>();
		Warranty earlierWarranty = inventoryItem.getWarranty();
		if (earlierWarranty != null) {
			for (RegisteredPolicy registeredPolicy : earlierWarranty
					.getPolicies()) {
				if (registeredPolicy.getPolicyDefinition().getTransferDetails()
						.isTransferable()
						&& !registeredPolicy.getWarrantyPeriod().getTillDate()
								.isBefore(inventoryItem.getDeliveryDate())) {
					if (claim.getFailureDate() != null
							&& claim.getFailureDate().isBefore(
									registeredPolicy.getWarrantyPeriod()
											.getTillDate().nextDay())) {
						if (RegisteredPolicyStatusType.ACTIVE.getStatus()
								.equals(registeredPolicy.getLatestPolicyAudit()
										.getStatus())) {
							if (claim.getHoursInService().intValue() <= registeredPolicy
									.getLatestPolicyAudit()
									.getServiceHoursCovered())
								if (registeredPolicy.getPolicyDefinition()
										.getApplicabilityTerms().isEmpty())
									existingPolices.add(registeredPolicy);
						}
					}
				}
			}
		}
		Collections.sort(existingPolices);
		return existingPolices;
	}

	public void validateAndSetClaimPage2MandatoryData(
			Organization organization, ClaimSubmission claimSubmissionDTO, Claim claim,
			Map<String, String> errorCodeMap) {
		String brand=null;
		if (claimSubmissionDTO.getBrand() != null
				&& claimSubmissionDTO.getBrand().toString() != null
				&& StringUtils.isNotBlank(claimSubmissionDTO.getBrand()
						.toString())) {
			brand = claimSubmissionDTO.getBrand().toString();
		}
		if (claimSubmissionDTO.getClaimNotes() != null) {
			claim.setOtherComments(claimSubmissionDTO.getClaimNotes());
		}
		if (claimSubmissionDTO.getConditionsFound() == null
				|| StringUtils.isBlank(claimSubmissionDTO.getConditionsFound())) {
			errorCodeMap
					.put(DealerInterfaceErrorConstants.CSA058,
							dealerInterfaceErrorConstants
									.getPropertyMessageFromErrorCode(DealerInterfaceErrorConstants.CSA058));
		} else {
			claim.setConditionFound(claimSubmissionDTO.getConditionsFound());
		}
		if (claimSubmissionDTO.getWorkPerformed() == null
				|| StringUtils.isBlank(claimSubmissionDTO.getWorkPerformed())) {
			errorCodeMap
					.put(DealerInterfaceErrorConstants.CSA059,
							dealerInterfaceErrorConstants
									.getPropertyMessageFromErrorCode(DealerInterfaceErrorConstants.CSA059));
		} else {
			claim.setWorkPerformed(claimSubmissionDTO.getWorkPerformed());
		}
		validateAndSetServicingLocation(organization,
				claimSubmissionDTO.getServicingLocationSiteNumber(), claim,
				errorCodeMap);
		if (!InstanceOfUtil.isInstanceOfClass(CampaignClaim.class, claim)) {
			validateAndSetCasualPartNumber(claimSubmissionDTO.getCausalPart(),
					claim, errorCodeMap,brand,claimSubmissionDTO.getInventorySerialNumber());
			if (!(InstanceOfUtil.isInstanceOfClass(PartsClaim.class, claim)
					&& claimSubmissionDTO.getCompetitorModel() != null
					&& !claim.getItemReference().isSerialized() && claim
					.getItemReference().getModel() == null)) {
				validateAndSetFailureInformation(claimSubmissionDTO, claim,
						errorCodeMap);
				validateAndSetCasuedByValues(claimSubmissionDTO, claim,
						errorCodeMap);
			}
		} else if (InstanceOfUtil.isInstanceOfClass(CampaignClaim.class, claim)
				|| (!(InstanceOfUtil.isInstanceOfClass(PartsClaim.class, claim) && claimSubmissionDTO
						.getCompetitorModel() != null))) {
			checkIfFailureDeatilisPresentForCmapaignOrCompetetorModelClaims(
					claimSubmissionDTO, claim, errorCodeMap);
		}
		setOwnerInfo(claimSubmissionDTO, claim, errorCodeMap);
	}

	private void checkIfFailureDeatilisPresentForCmapaignOrCompetetorModelClaims(
			ClaimSubmission claimSubmissionDTO, Claim claim,
			Map<String, String> errorCodeMap) {
		if (InstanceOfUtil.isInstanceOfClass(CampaignClaim.class, claim)) {
			if (StringUtils.isNotBlank(claimSubmissionDTO.getCausalPart())
					&& (!(InstanceOfUtil.isInstanceOfClass(PartsClaim.class,
							claim) && claimSubmissionDTO.getCompetitorModel() != null))) {
				errorCodeMap
						.put(DealerInterfaceErrorConstants.CSA056,
								dealerInterfaceErrorConstants
										.getPropertyMessageFromErrorCode(DealerInterfaceErrorConstants.CSA056));
			}
			if (StringUtils.isNotBlank(claimSubmissionDTO.getFaultFound())) {
				errorCodeMap
						.put(DealerInterfaceErrorConstants.CSA073,
								dealerInterfaceErrorConstants
										.getPropertyMessageFromErrorCode(DealerInterfaceErrorConstants.CSA073));
			}
			if (StringUtils.isNotBlank(claimSubmissionDTO.getCausedBy())) {
				errorCodeMap
						.put(DealerInterfaceErrorConstants.CSA074,
								dealerInterfaceErrorConstants
										.getPropertyMessageFromErrorCode(DealerInterfaceErrorConstants.CSA074));
			}
		}
	}

	private void validateAndSetCasuedByValues(
			ClaimSubmission claimSubmissionDTO, Claim claim,
			Map<String, String> errorCodeMap) {
		if (claimSubmissionDTO.getCausedBy() == null
				|| StringUtils.isBlank(claimSubmissionDTO.getCausedBy())) {
			errorCodeMap
					.put(DealerInterfaceErrorConstants.CSA065,
							dealerInterfaceErrorConstants
									.getPropertyMessageFromErrorCode(DealerInterfaceErrorConstants.CSA065));
		} else if (claim.getServiceInformation().getFaultFound() != null
				&& claim.getServiceInformation().getFaultFound().getId() != null) {
			List<FailureCauseDefinition> failureCauseDefinitionsList = new ArrayList<FailureCauseDefinition>();
			FailureCauseDefinition failureCauseDefinition = null;
			if (claim.getItemReference().getReferredInventoryItem() != null
					&& claim.getItemReference().getReferredInventoryItem()
							.getId() != null) {
				failureCauseDefinitionsList = failureStructureService
						.findCausedByOptionsById(claim.getItemReference()
								.getReferredInventoryItem().getId().toString(),
								claim.getServiceInformation().getFaultFound()
										.getId().toString());
			} else if (claim.getItemReference().getModel() != null
					&& claim.getItemReference().getModel().getId() != null) {
				failureCauseDefinitionsList = failureStructureService
						.findCausedByOptionsForModelById(claim
								.getItemReference().getModel().getId()
								.toString(), claim.getServiceInformation()
								.getFaultFound().getId().toString());
			}
			for (FailureCauseDefinition failureCauseDefinitionObj : failureCauseDefinitionsList) {
				if ((failureCauseDefinitionObj.getName()
						.equalsIgnoreCase(claimSubmissionDTO.getCausedBy()))
						|| (failureCauseDefinitionObj
								.getName()!=null&&failureCauseDefinitionObj
								.getNameInEnglish(failureCauseDefinitionObj
										.getName())
								.equalsIgnoreCase(claimSubmissionDTO
										.getCausedBy()))) {
					failureCauseDefinition = failureCauseDefinitionObj;
				}
			}
			if (failureCauseDefinition == null) {
				errorCodeMap
				.put(DealerInterfaceErrorConstants.CSA066,
						dealerInterfaceErrorConstants
								.getPropertyMessageFromErrorCode(DealerInterfaceErrorConstants.CSA066));
			} else {
				claim.getServiceInformation().setCausedBy(
						failureCauseDefinition);
			}
		}
	}

	private void validateAndSetFailureInformation(
			ClaimSubmission claimSubmissionDTO, Claim claim,
			Map<String, String> errorCodeMap) {
		if (claimSubmissionDTO.getFaultLocation() == null
				|| StringUtils.isBlank(claimSubmissionDTO.getFaultLocation())) {
			errorCodeMap
					.put(DealerInterfaceErrorConstants.CSA060,
							dealerInterfaceErrorConstants
									.getPropertyMessageFromErrorCode(DealerInterfaceErrorConstants.CSA060));
		} else if (claim.getServiceInformation() != null
				&& claim.getServiceInformation().getCausalPart() != null) {
			FailureStructure failureStructure = failureStructureService
					.getFailureStructure(claim, null);
			if (failureStructure != null) {
				errorCodeMap.putAll(setFaultLocation(claim, failureStructure,
						claim.getServiceInformation(), claimSubmissionDTO));
			} else {
				errorCodeMap
						.put(DealerInterfaceErrorConstants.CSA0122,
								dealerInterfaceErrorConstants
										.getPropertyMessageFromErrorCode(DealerInterfaceErrorConstants.CSA0122));
			}
		}
		setFaultFound(claimSubmissionDTO, claim, errorCodeMap);
		validateRootCauseForClaim(claim, errorCodeMap);
	}

	private Map<String, String> setFaultLocation(Claim claim,
			FailureStructure failureStructure,
			ServiceInformation serviceInformation,
			ClaimSubmission claimSubmissionDTO) {
		String faultLocation = claimSubmissionDTO.getFaultLocation();
		Map<String, String> errorCodeMap = new HashMap<String, String>();
		if (StringUtils.isNotBlank(faultLocation)) {
			Assembly assembly = failureStructure.getAssembly(faultLocation);
			if (assembly == null || assembly.getFaultCode() == null) {
				if (IntegrationLayerUtil.isAMERBusinessUnit(claim
						.getBusinessUnitInfo().getName())) {
					List<FaultCode> faultCodeRef = failureStructureService
							.getAnyFaultCodeRefForGivenFaultCode(faultLocation,0,1);
					if (faultCodeRef == null||faultCodeRef.isEmpty()) {
						errorCodeMap
								.put(DealerInterfaceErrorConstants.CSA062,
										dealerInterfaceErrorConstants
												.getPropertyMessage(
														DealerInterfaceErrorConstants.CSA062,
														new String[] { claimSubmissionDTO
																.getFaultLocation() }));
					} else {
						serviceInformation.setFaultCode(faultCodeRef.get(0)
								.getDefinition().getCode());
						serviceInformation.setFaultCodeRef(faultCodeRef.get(0));
					}
				} else {
					errorCodeMap.put(DealerInterfaceErrorConstants.CSA062,
							dealerInterfaceErrorConstants.getPropertyMessage(
									DealerInterfaceErrorConstants.CSA062,
									new String[] { claimSubmissionDTO
											.getFaultLocation() }));
				}
			} else {
				FaultCode faultCodeRef = assembly.getFaultCode();
				serviceInformation.setFaultCode(faultCodeRef.getDefinition()
						.getCode());
				serviceInformation.setFaultCodeRef(faultCodeRef);
			}
		}
		return errorCodeMap;
	}

	private void setFaultFound(ClaimSubmission claimSubmissionDTO, Claim claim,
			Map<String, String> errorCodeMap) {
		if ((claimSubmissionDTO.getFaultFound() == null || StringUtils
				.isBlank((claimSubmissionDTO.getFaultFound())))) {
			errorCodeMap
					.put(DealerInterfaceErrorConstants.CSA061,
							dealerInterfaceErrorConstants
									.getPropertyMessageFromErrorCode(DealerInterfaceErrorConstants.CSA061));
		} else if((claim.getClaimedItems() != null
					&& !claim.getClaimedItems().isEmpty()
					&& claim.getClaimedItems().get(0) != null
					&& claim.getClaimedItems().get(0).getItemReference() != null
					&& claim.getClaimedItems().get(0).getItemReference()
							.isSerialized()
					&& claim.getClaimedItems().get(0).getItemReference()
							.getReferredInventoryItem() != null)||(claim.getItemReference() != null
					&& claim.getItemReference().getModel() != null)){
			FailureTypeDefinition failureTypeDef = null;
			if (claim.getClaimedItems() != null
					&& !claim.getClaimedItems().isEmpty()
					&& claim.getClaimedItems().get(0) != null
					&& claim.getClaimedItems().get(0).getItemReference() != null
					&& claim.getClaimedItems().get(0).getItemReference()
							.isSerialized()
					&& claim.getClaimedItems().get(0).getItemReference()
							.getReferredInventoryItem() != null) {
				failureTypeDef = failureStructureService
						.findFaultFoundOptionsForProductByFaultName(claim
								.getClaimedItems().get(0).getItemReference()
								.getReferredInventoryItem().getId().toString(),
								claimSubmissionDTO.getFaultFound().toUpperCase());
			} else if (claim.getItemReference() != null
					&& claim.getItemReference().getModel() != null) {
				failureTypeDef = failureStructureService
						.findFaultFoundOptionsForModelsByFaultName(claim
								.getItemReference().getModel().getId()
								.toString(), claimSubmissionDTO.getFaultFound());
				if (failureTypeDef == null) {
					if (claim.getItemReference().getModel().getIsPartOf()
							.getItemGroupType().equalsIgnoreCase("PRODUCT")) {
						failureTypeDef = failureStructureService
								.findFaultFoundOptionsForModelsByFaultName(
										claim.getItemReference().getModel()
												.getIsPartOf().getId()
												.toString(),
										claimSubmissionDTO.getFaultFound());
					} else {

						failureTypeDef = failureStructureService
								.findFaultFoundOptionsForModelsByFaultName(
										claim.getItemReference().getModel()
												.getIsPartOf().getIsPartOf()
												.getId().toString(),
										claimSubmissionDTO.getFaultFound());
					}
				}
			}
			if (failureTypeDef != null) {
				claim.getServiceInformation().setFaultFound(failureTypeDef);
			} else {
				errorCodeMap.put(DealerInterfaceErrorConstants.CSA063,
						dealerInterfaceErrorConstants.getPropertyMessage(
								DealerInterfaceErrorConstants.CSA063,
								new String[] { claimSubmissionDTO
										.getFaultFound() }));
			}
		}
	}

	Long getModelId(Claim claim) {
		Long modelId = null;
		if (claim.getClaimedItems() != null
				&& !claim.getClaimedItems().isEmpty()
				&& claim.getClaimedItems().get(0) != null
				&& claim.getClaimedItems().get(0).getItemReference() != null
				&& claim.getClaimedItems().get(0).getItemReference()
						.isSerialized()) {
			modelId = claim.getClaimedItems().get(0).getItemReference()
					.getReferredInventoryItem().getOfType().getModel().getId();
		} else if (claim.getItemReference() != null
				&& claim.getItemReference().getModel() != null) {
			modelId = claim.getItemReference().getModel().getId();
		}
		return modelId;
	}

	private void validateRootCauseForClaim(Claim claim,
			Map<String, String> errorCodeMap) {
		if (isRootCauseAllowed() && claim != null
				&& claim.getServiceInformation() != null) {
			/**
			 * Root cause is mandatory only if configured for a given FF
			 */
			Long modelId = null;
			Long faultFoundId = null;

			if (claim.getClaimedItems() != null
					&& !claim.getClaimedItems().isEmpty()
					&& claim.getClaimedItems().get(0) != null
					&& claim.getClaimedItems().get(0).getItemReference() != null
					&& claim.getClaimedItems().get(0).getItemReference()
							.isSerialized()) {
				modelId = claim.getClaimedItems().get(0).getItemReference()
						.getReferredInventoryItem().getOfType().getModel()
						.getId();
			} else if (claim.getItemReference() != null
					&& claim.getItemReference().getModel() != null) {
				modelId = claim.getItemReference().getModel().getId();
			}
			if (claim.getServiceInformation() != null
					&& claim.getServiceInformation().getFaultFound() != null) {
				faultFoundId = claim.getServiceInformation().getFaultFound()
						.getId();
			}
			if (modelId != null && faultFoundId != null) {
				List<FailureRootCauseDefinition> possibleRootCauses = failureStructureService
						.findRootCauseOptionsByModel(modelId.toString(),
								faultFoundId.toString());
				if (possibleRootCauses != null && !possibleRootCauses.isEmpty()) {
					if (claim.getServiceInformation().getRootCause() == null) {
						errorCodeMap
								.put(DealerInterfaceErrorConstants.CSA064,
										dealerInterfaceErrorConstants
												.getPropertyMessageFromErrorCode(DealerInterfaceErrorConstants.CSA064));
					}
				}
			}
		}
	}

	public boolean isRootCauseAllowed() {
		return getConfigParamService().getBooleanValue(
				ConfigName.IS_ROOT_CAUSE_ALLOWED.getName());
	}

	public List<FailureTypeDefinition> prepareFaultFoundList(String invItemId) {

		List<FailureTypeDefinition> possibleFailures = this.failureStructureService
				.findFaultFoundOptions(invItemId);
		if (possibleFailures.isEmpty()) {
			possibleFailures = this.failureStructureService
					.findFaultFoundOptionsAtProduct(invItemId);

		}
		Collections.sort(possibleFailures, new Comparator() {
			public int compare(Object obj1, Object obj2) {
				FailureTypeDefinition failure1 = (FailureTypeDefinition) obj1;
				FailureTypeDefinition failure2 = (FailureTypeDefinition) obj2;
				return failure1.getName().compareTo(failure2.getName());
			}
		});
		return possibleFailures;
	}

	public List<FailureTypeDefinition> prepareFaultFoundListForModels(
			ItemGroup model) {

		List<FailureTypeDefinition> possibleFailures = this.failureStructureService
				.findFaultFoundOptionsForModels(model.getId().toString());
		if (possibleFailures.isEmpty()) {
			if (model.getIsPartOf().getItemGroupType()
					.equalsIgnoreCase("PRODUCT")) {
				possibleFailures = this.failureStructureService
						.findFaultFoundOptionsForModels(model.getIsPartOf()
								.getId().toString());
			} else {

				possibleFailures = this.failureStructureService
						.findFaultFoundOptionsForModels(model.getIsPartOf()
								.getIsPartOf().getId().toString());
			}
		}
		Collections.sort(possibleFailures, new Comparator() {
			public int compare(Object obj1, Object obj2) {
				FailureTypeDefinition failure1 = (FailureTypeDefinition) obj1;
				FailureTypeDefinition failure2 = (FailureTypeDefinition) obj2;
				return failure1.getName().compareTo(failure2.getName());
			}
		});
		return possibleFailures;
	}

	private void validateAndSetCasualPartNumber(String causalPart, Claim claim,
			Map<String, String> errorCodeMap,String brand, String InventorySerialNumkber) {
		ServiceInformation serviceInformation = claim.getServiceInformation();
		Map<String, String> resultMap = new HashMap<String, String>();
		Item causalPartItem = null;
		if ((causalPart == null || StringUtils.isBlank(causalPart))
				&& !InstanceOfUtil
						.isInstanceOfClass(CampaignClaim.class, claim)) {
			resultMap
					.put(DealerInterfaceErrorConstants.CSA053,
							dealerInterfaceErrorConstants
									.getPropertyMessageFromErrorCode(DealerInterfaceErrorConstants.CSA053));
		}else if(InstanceOfUtil.isInstanceOfClass(MachineClaim.class, claim)){
			validateCasualPartNumber(claim, causalPart,
					resultMap, serviceInformation, brand);	
		}
		else if (InstanceOfUtil.isInstanceOfClass(PartsClaim.class, claim)
		
				&& claim.getPartItemReference().getReferredItem() != null) {
			if (((claim.getPartItemReference().getReferredItem().getNumber()
					.equals(causalPart) || claim.getPartItemReference()
					.getReferredItem().getAlternateNumber().equals(causalPart)))
					|| (serviceInformation.getCausalBrandPart() != null && serviceInformation
							.getCausalBrandPart().getItemNumber()
							.equalsIgnoreCase(causalPart))) {
				causalPartItem = claim.getPartItemReference().getReferredItem();
				serviceInformation.setCausalPart(causalPartItem);
			} else if (claim.getCompetitorModelDescription() != null&& (InventorySerialNumkber == null || StringUtils
					.isBlank(InventorySerialNumkber))) {
				resultMap
						.put(
								DealerInterfaceErrorConstants.CSA057,
								dealerInterfaceErrorConstants
										.getPropertyMessageFromErrorCode(DealerInterfaceErrorConstants.CSA057));
			}
			errorCodeMap.putAll(resultMap);
			if (resultMap.isEmpty()
					&& causalPartItem == null
					&& serviceInformation.getCausalPart() == null) {
				validateCasualPartNumber(claim, causalPart,
						resultMap, serviceInformation, brand);
			}
		}
		errorCodeMap.putAll(resultMap);
	}

	private void setPartItemNumber(Claim claim, String partNumber,
			String configParamType, Map<String, String> errorCodeMap) {

		BrandItem brandItem = null;
		if (IntegrationLayerUtil.isAMERBusinessUnit(claim.getBusinessUnitInfo()
				.getName())) {
			brandItem = getOEMPartBrandItemNumbersForAMERBu(claim, partNumber,
					configParamType);
			if (brandItem!=null&&(claim.getBrand() == null || StringUtils.isBlank(claim
					.getBrand()))
					&& !claim.getItemReference().isSerialized()) {
				claim.setBrand(brandItem.getBrand());
			}
		} else if (claim.getBrand() != null
				&& StringUtils.isNotBlank(claim.getBrand())) {
			brandItem = getOEMPartBrandItemNumbers(claim, partNumber,
					configParamType, claim.getBrand());
		}
		if (brandItem != null) {
			claim.getPartItemReference().setReferredItem(brandItem.getItem());
			claim.getServiceInformation().setCausalBrandPart(brandItem);
		} else {
			errorCodeMap.put(DealerInterfaceErrorConstants.CSA010,
					dealerInterfaceErrorConstants.getPropertyMessage(
							DealerInterfaceErrorConstants.CSA010,
							new String[] { partNumber }));
		}
		if (brandItem==null) {
			Item item = getOEMPartItemNumbers(claim, partNumber,
					configParamType);
			if (item != null) {
				claim.getPartItemReference().setReferredItem(item);
			} else {
				errorCodeMap.put(DealerInterfaceErrorConstants.CSA010,
						dealerInterfaceErrorConstants.getPropertyMessage(
								DealerInterfaceErrorConstants.CSA010,
								new String[] { partNumber }));

			}
		}
	}

	public Item getOEMPartItemNumbers(Claim claim, String partNumber,
			String configParamType) {
		Item item = null;
		{
			List<Object> itemGroups = getItemGroupsFromBUConfig(configParamType);
			if (item == null) {
				item = catalogService.fetchManufPartsForPartNumber(claim
						.getBusinessUnitInfo().getName(), partNumber
						.toUpperCase(), itemGroups);
			}
		}
		return item;
	}

	public BrandItem getOEMPartBrandItemNumbers(Claim claim, String partNumber,
			String configParamType, String brand) {
		List<Object> itemGroups = getItemGroupsFromBUConfig(configParamType);

		BrandItem item = catalogService.fetchManufBrandPartsForBrandPartNumber(claim
				.getBusinessUnitInfo().getName(), partNumber.toUpperCase(),
				itemGroups, brand);

		return item;
	}
	
	public BrandItem getOEMPartBrandItemNumbersForAmer(Claim claim, String partNumber,
			String configParamType) {
		List<Object> itemGroups = getItemGroupsFromBUConfig(configParamType);

		List<BrandItem> brandItems = catalogService
				.fetchBrandItemsForbrandPartNumber(claim
						.getBusinessUnitInfo().getName(), partNumber,
						itemGroups);
		if(brandItems!=null&&!brandItems.isEmpty()){
			return brandItems.get(0);
		}
		return null;
	}
	
	public BrandItem getOEMPartBrandItemNumbersForAMERBu(Claim claim, String partNumber,
			String configParamType) {
		List<Object> itemGroups = getItemGroupsFromBUConfig(configParamType);
		List<String> brands=new ArrayList<String>();
		if (claim.getForDealer() != null
				&& null != new HibernateCast<Dealership>().cast(
						claim.getForDealer())&& new HibernateCast<Dealership>().cast(
								claim.getForDealer()).getBrand()!=null) {
		brands.add( new HibernateCast<Dealership>().cast(
				claim.getForDealer()).getBrand());
		}
		brands.add(IntegrationConstants.DEFAULT_DEALER_BRAND);
		List<BrandItem> items = catalogService.fetchManufBrandPartsForBrandPartNumberForAMER(claim
				.getBusinessUnitInfo().getName(), partNumber.toUpperCase(),
				itemGroups, brands);
		if(items!=null&&!items.isEmpty()){
			return items.get(0);
		}
		return null;
	}

	private Map<String, String> validateCasualPartNumber(Claim claim,
			String causalPart, Map<String, String> errorCodeMap,
			ServiceInformation serviceInformation,String brand) {
		List<Object> itemGroups = getItemGroupsFromBUConfig(CASUAL_PART_ITEM_TYPE);
			 setBrandItemsForClaim(claim,serviceInformation,causalPart,errorCodeMap,itemGroups,brand);
		if(serviceInformation.getCausalBrandPart()==null) {
			Item possiblePart = catalogService.fetchManufPartsForPartNumber(
					claim.getBusinessUnitInfo().getName(), causalPart,
					itemGroups);
			if (possiblePart == null) {
				errorCodeMap.put(DealerInterfaceErrorConstants.CSA054,
						dealerInterfaceErrorConstants.getPropertyMessage(
								DealerInterfaceErrorConstants.CSA054,
								new String[] { causalPart }));
			} else {
				serviceInformation.setCausalPart(possiblePart);
			}
		}
		return errorCodeMap;
	}

	private void setBrandItemsForClaim(Claim claim,
			ServiceInformation serviceInformation, String causalPart,
			Map<String, String> errorCodeMap, List<Object> itemGroups,String brand) {
		if (IntegrationLayerUtil.isAMERBusinessUnit(claim.getBusinessUnitInfo()
				.getName())) {
			List<BrandItem> brandItems = catalogService
					.fetchBrandItemsForbrandPartNumber(claim
							.getBusinessUnitInfo().getName(), causalPart,
							itemGroups);
			if (brandItems != null && !brandItems.isEmpty()) {
				serviceInformation.setCausalPart(brandItems.get(0).getItem());
				serviceInformation.setCausalBrandPart(brandItems.get(0));
				if (!claim.getItemReference().isSerialized() && brand == null
						|| StringUtils.isEmpty(brand)) {
					claim.setBrand(brandItems.get(0).getBrand());
				}
			}else{
				errorCodeMap.put(DealerInterfaceErrorConstants.CSA010,
						dealerInterfaceErrorConstants.getPropertyMessage(
								DealerInterfaceErrorConstants.CSA010,
								new String[] { causalPart }));
			}
		}else{
			try {
			BrandItem brandItem=null;
			if(claim.getBrand()==null){
				List<BrandItem> brandItems = catalogService
						.fetchBrandItemsForbrandPartNumber(claim
								.getBusinessUnitInfo().getName(), causalPart,
								itemGroups);
				 brandItem=brandItems.get(0);
			}else{
			 brandItem = catalogService
					.fetchManufBrandPartsForBrandPartNumber(claim
							.getBusinessUnitInfo().getName(), causalPart,
							itemGroups, claim.getBrand());
			}
			if (brandItem != null) {
				serviceInformation.setCausalPart(brandItem.getItem());
				serviceInformation.setCausalBrandPart(brandItem);
				if (!claim.getItemReference().isSerialized() && brand == null
						|| StringUtils.isEmpty(brand)) {
					claim.setBrand(brandItem.getBrand());
				}
			} else {
				errorCodeMap.put(DealerInterfaceErrorConstants.CSA010,
						dealerInterfaceErrorConstants.getPropertyMessage(
								DealerInterfaceErrorConstants.CSA010,
								new String[] { causalPart }));
			}
		} catch (Exception e) {
			errorCodeMap.put(DealerInterfaceErrorConstants.CSA010,
					dealerInterfaceErrorConstants.getPropertyMessage(
							DealerInterfaceErrorConstants.CSA010,
							new String[] { causalPart }));
		}
	}
	}

	private void validateAndSetServicingLocation(Organization organization,
			String servicingLocationSiteNumber, Claim claim,
			Map<String, String> errorCodeMap) {
		OrganizationAddress oganizationAddress = null;
		if (servicingLocationSiteNumber != null
				&& StringUtils.isNotBlank(servicingLocationSiteNumber)) {
			try {
				oganizationAddress = getServicingLocations(
						servicingLocationSiteNumber, organization);
			} catch (IncorrectResultSizeDataAccessException he) {
				logger.error("Error in setClaimRequiredFields", he);
				errorCodeMap
						.put(
								DealerInterfaceErrorConstants.CSA050,
								dealerInterfaceErrorConstants
										.getPropertyMessageFromErrorCode(DealerInterfaceErrorConstants.CSA050));
			}
			if (oganizationAddress == null) {
				errorCodeMap.put(DealerInterfaceErrorConstants.CSA051,
						dealerInterfaceErrorConstants.getPropertyMessage(
								DealerInterfaceErrorConstants.CSA051,
								new String[] { servicingLocationSiteNumber }));
			} else {
				claim.setServicingLocation(oganizationAddress);
			}
		} else if (claim.getForDealer() != null
				&& claim.getForDealer().getOrgAddresses() != null
				&& claim.getForDealer().getOrgAddresses().size() >= 1
				&& IntegrationLayerUtil.isAMERBusinessUnit(claim
						.getBusinessUnitInfo().getName())) {
			claim.setServicingLocation(claim.getForDealer().getOrgAddresses()
					.get(0));
		}
		if (claim.getServicingLocation() == null) {
			errorCodeMap
					.put(
							DealerInterfaceErrorConstants.CSA052,
							dealerInterfaceErrorConstants
									.getPropertyMessageFromErrorCode(DealerInterfaceErrorConstants.CSA052));
		}
	}

	private OrganizationAddress getServicingLocations(
			String servicingLocationSiteNumber, Organization organization) {
		OrganizationAddress organizationAddress = null;
		if (StringUtils.isNotBlank(servicingLocationSiteNumber)&&organization!=null) {
			organizationAddress = orgService
					.getAddressesForOrganizationBySiteNumber(organization,
							servicingLocationSiteNumber.trim());
		}
		return organizationAddress;
	}

	private List<Object> getItemGroupsFromBUConfig(String configParamType) {

		List<Object> itemGroup = null;

		if (configParamType.equals(REPLACED_PART_ITEM_TYPE)) {
			// if Replaced part section
			itemGroup = this.configParamService
					.getListofObjects(ConfigName.REPLACED_ITEMS_ON_CLAIM_CONFIGURATION
							.getName());

		} else if (configParamType.equals(CASUAL_PART_ITEM_TYPE)) {
			// if causal part section
			itemGroup = this.configParamService
					.getListofObjects(ConfigName.CAUSAL_ITEMS_ON_CLAIM_CONFIGURATION
							.getName());
		} else if (configParamType.equals(PARTS_CLAIM_ITEM_TYPE)) {
			// if causal part section
			itemGroup = this.configParamService
					.getListofObjects(ConfigName.PARTSCLAIM_ITEMTYPE_ON_CLAIM_CONFIGURATION
							.getName());
		}

		return toUpper(itemGroup);
	}

	private List<Object> toUpper(List<Object> itemGroups) {
		List<Object> toUpperItemGroups = new ArrayList<Object>();

		for (Object itemGroup : itemGroups) {
			String itGroup = (String) itemGroup;
			toUpperItemGroups.add(itGroup.toUpperCase());
		}
		return toUpperItemGroups;

	}

	// String customerId = claimSubmissionDTO.getCustomerId();
	// This condition is for a Non Serialized Machine claim or for a Non
	// Serialized Parts Claim hosted on a Non Serialized Host/Competitor
	// Model
	private Map<String, String> setOwnerInfo(
			ClaimSubmission claimSubmissionDTO, Claim claim,
			Map<String, String> errorCodeMap) {
		if (((InstanceOfUtil.isInstanceOfClass(MachineClaim.class, claim)) && !claim
				.getClaimedItems().get(0).getItemReference().isSerialized())
				|| (InstanceOfUtil.isInstanceOfClass(PartsClaim.class, claim)
						&& !claim.getPartItemReference().isSerialized() && !claim
						.getClaimedItems().get(0).getItemReference()
						.isSerialized())) {
			String customerId = null;
			if (claim.getForDealer() != null
					&& claim.getForDealer().getDealerNumber() != null
					&& claim.getServicingLocation() != null
					&& StringUtils.isNotBlank(claimSubmissionDTO
							.getDealerNumber())
					&& StringUtils.isNotBlank(claim.getServicingLocation()
							.getSiteNumber())) {
				customerId = claim.getForDealer().getDealerNumber()
						+ claim.getServicingLocation().getSiteNumber();
				if (customerId != null) {
					Customer owner = customerService
							.findCustomerByCustomerIdAndDealer(customerId,
									getLoggedInUsersDealership());
					if (owner == null) {
						createOwner(claimSubmissionDTO, claim, errorCodeMap,
								customerId);

					} else {
						claim.setOwnerInformation(owner.getAddress());
					}
				}
			}
		}
		return errorCodeMap;
	}

	private void createOwner(ClaimSubmission claimSubmissionDTO, Claim claim,
			Map<String, String> errorCodeMap, String customerId) {
		Customer customer = new Customer();
		if (claimSubmissionDTO.getCustomerDetails() == null
				|| customerId == null || StringUtils.isBlank(customerId)) {
			errorCodeMap
					.put(DealerInterfaceErrorConstants.CSA067,
							dealerInterfaceErrorConstants
									.getPropertyMessageFromErrorCode(DealerInterfaceErrorConstants.CSA067));
		} else {
			Address address = new Address();
			customer.setIndividual(Boolean.FALSE);
			customer.setCustomerId(customerId);
			if (claimSubmissionDTO.getCustomerDetails().getCustomerName() == null
					|| StringUtils.isBlank(claimSubmissionDTO
							.getCustomerDetails().getCustomerName())) {
				errorCodeMap
						.put(DealerInterfaceErrorConstants.CSA068,
								dealerInterfaceErrorConstants
										.getPropertyMessageFromErrorCode(DealerInterfaceErrorConstants.CSA068));
			} else {
				customer.setCompanyName(claimSubmissionDTO.getCustomerDetails()
						.getCustomerName());
				customer.setName(claimSubmissionDTO.getCustomerDetails()
						.getCustomerName());
				customer.setCorporateName(claimSubmissionDTO
						.getCustomerDetails().getCustomerName());
				populateAddress(claimSubmissionDTO.getCustomerDetails(),
						address, errorCodeMap);
				customer.setAddress(address);
				List<Address> addresses = new ArrayList<Address>();
				addresses.add(address);
				customer.setAddresses(addresses);
				customerService.createCustomer(customer);
				claim.setOwnerInformation(customer.getAddress());
			}
		}
	}

	private void populateAddress(CustomerDetails customerDetails,
			final Address address, Map<String, String> errorCodeMap) {
		if (customerDetails.getCustomerAddress() == null
				|| StringUtils.isBlank(customerDetails.getCustomerAddress())) {
			errorCodeMap
					.put(DealerInterfaceErrorConstants.CSA069,
							dealerInterfaceErrorConstants
									.getPropertyMessageFromErrorCode(DealerInterfaceErrorConstants.CSA069));
		} else {
			address.setAddressLine1(customerDetails.getCustomerAddress());
		}
		if (customerDetails.getCustomerCity() == null
				|| StringUtils.isBlank(customerDetails.getCustomerCity())) {
			errorCodeMap
					.put(DealerInterfaceErrorConstants.CSA070,
							dealerInterfaceErrorConstants
									.getPropertyMessageFromErrorCode(DealerInterfaceErrorConstants.CSA070));
		} else {
			address.setCity(customerDetails.getCustomerCity());
		}
		if ((customerDetails.getCustomerState() == null
				|| StringUtils.isBlank(customerDetails.getCustomerState()))
				&& (customerDetails.getCustomerCountry() != null && customerDetails
						.getCustomerCountry().equals(
								IntegrationConstants.COUNTRY_US))) {
			errorCodeMap
					.put(DealerInterfaceErrorConstants.CSA071,
							dealerInterfaceErrorConstants
									.getPropertyMessageFromErrorCode(DealerInterfaceErrorConstants.CSA071));
		} else {
			address.setState(customerDetails.getCustomerState());
		}
		if (customerDetails.getCustomerZip() == null
				|| StringUtils.isBlank(customerDetails.getCustomerZip())) {
			errorCodeMap
					.put(DealerInterfaceErrorConstants.CSA072,
							dealerInterfaceErrorConstants
									.getPropertyMessageFromErrorCode(DealerInterfaceErrorConstants.CSA072));
		} else {
			address.setZipCode(customerDetails.getCustomerZip());
		}
		if (customerDetails.getCustomerCountry() == null
				|| StringUtils.isBlank(customerDetails.getCustomerZip())) {
			errorCodeMap
					.put(DealerInterfaceErrorConstants.CSA071,
							dealerInterfaceErrorConstants
									.getPropertyMessageFromErrorCode(DealerInterfaceErrorConstants.CSA071));
		} else {
			address.setCountry(customerDetails.getCustomerCountry());
		}
		if (customerDetails.getCustomerAddress2() != null
				|| !StringUtils.isBlank(customerDetails.getCustomerAddress2())) {
			address.setAddressLine2(customerDetails.getCustomerAddress2());
		}
	}

	public DealerInterfaceErrorConstants getDealerInterfaceErrorConstants() {
		return dealerInterfaceErrorConstants;
	}

	public void setDealerInterfaceErrorConstants(
			DealerInterfaceErrorConstants dealerInterfaceErrorConstants) {
		this.dealerInterfaceErrorConstants = dealerInterfaceErrorConstants;
	}

	public SecurityHelper getSecurityHelper() {
		return securityHelper;
	}

	public void setSecurityHelper(SecurityHelper securityHelper) {
		this.securityHelper = securityHelper;
	}

	public ConfigParamService getConfigParamService() {
		return configParamService;
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

	public CatalogService getCatalogService() {
		return catalogService;
	}

	public void setCatalogService(CatalogService catalogService) {
		this.catalogService = catalogService;
	}

	public LovRepository getLovRepository() {
		return lovRepository;
	}

	public void setLovRepository(LovRepository lovRepository) {
		this.lovRepository = lovRepository;
	}

	public FailureStructureService getFailureStructureService() {
		return failureStructureService;
	}

	public void setFailureStructureService(
			FailureStructureService failureStructureService) {
		this.failureStructureService = failureStructureService;
	}

	public CustomerService getCustomerService() {
		return customerService;
	}

	public void setCustomerService(CustomerService customerService) {
		this.customerService = customerService;
	}
}

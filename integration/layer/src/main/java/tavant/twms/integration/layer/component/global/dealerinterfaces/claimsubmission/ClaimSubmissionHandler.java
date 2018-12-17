package tavant.twms.integration.layer.component.global.dealerinterfaces.claimsubmission;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.orgmodel.Organization;
import tavant.twms.infra.ApplicationSettingsHolder;
import tavant.twms.integration.layer.constants.DealerInterfaceErrorConstants;
import tavant.twms.integration.layer.constants.IntegrationConstants;
import tavant.twms.integration.layer.transformer.global.dealerinterfaces.claimsubmission.ClaimSubmissionTransformer;
import tavant.twms.integration.layer.util.IntegrationLayerUtil;
import tavant.twms.security.SelectedBusinessUnitsHolder;

import com.nmhg.batchclaim_response.ClaimSubmissionResponse;
import com.nmhg.batchclaim_response.ClaimSubmissionResponse.Exceptions;
import com.nmhg.batchclaim_response.ClaimSubmissionResponse.Exceptions.Error;
import com.tavant.dealerinterfaces.claimsubmission.request.ClaimSubmissionDocument.ClaimSubmission;

public class ClaimSubmissionHandler {
	private static final Logger logger = Logger.getLogger("dealerAPILogger");

	private DealerInterfaceErrorConstants dealerInterfaceErrorConstants;

	private ClaimSubmissionTransformer claimSubmissionTransformer;

	private ClaimSubmissionProcessor claimSubmissionProcessor;

	private ClaimSubmssionValidator claimSubmssionValidator;

	private ApplicationSettingsHolder applicationSettings;

	public void processClaim(ClaimSubmission claimSubmissionDTO,
			ClaimSubmissionResponse claimSubmissionRespDTO,
			Organization organization) {
		Map<String, String> claimPage1errorCodeMap = new HashMap<String, String>();
		Map<String, String> claimPage2errorCodeMap = new HashMap<String, String>();
		Claim claim=null;
		try {
			String buName=IntegrationLayerUtil.getBusinessUnit(claimSubmissionDTO.getBUName().toString());
			dealerInterfaceErrorConstants
					.getI18nDomainTextReader()
					.setLoggedInUserLocale(
							buName);
			SelectedBusinessUnitsHolder.setSelectedBusinessUnit(buName);
			claim = claimSubmissionTransformer
					.getClaim(claimSubmissionDTO,organization,claimPage1errorCodeMap); 
			claimSubmissionTransformer
					.isClaimedItemSerilized(claimSubmissionDTO, claim,
							organization, claimPage1errorCodeMap,buName);
			claimSubmssionValidator.validateAndSetClaimPgae1Data(
					claimSubmissionDTO, claim, claimPage1errorCodeMap);
			claimSubmissionTransformer.setClaimpage1Data(claimSubmissionDTO,
					claim,organization, claimPage1errorCodeMap);
			claimSubmssionValidator.validateAndSetClaimPage2MandatoryData(organization,
					claimSubmissionDTO, claim, claimPage2errorCodeMap);
			claimSubmissionTransformer.validateAndSetClaimPage2OptionalData(
					claimSubmissionDTO, claim, claimPage2errorCodeMap);
			if (claimPage1errorCodeMap.isEmpty()){
			if((IntegrationConstants.NMHG_EMEA
								.equalsIgnoreCase(buName) && claimSubmissionDTO
								.getForceToDraft())){
				claimSubmissionProcessor.saveDraft(claim);
			}else if(!claimPage2errorCodeMap.isEmpty()){
				claimSubmissionProcessor.saveDraft(claim);
			}
			}
			if (claimPage1errorCodeMap.isEmpty()
					&& claimPage2errorCodeMap.isEmpty()) {
				if (IntegrationLayerUtil.isAMERBusinessUnit(buName)
						|| (IntegrationConstants.NMHG_EMEA
								.equalsIgnoreCase(buName) && !claimSubmissionDTO
								.getForceToDraft())) {
					claimSubmissionProcessor.updateClaim(claim,
							claimSubmissionDTO, claimPage2errorCodeMap);
				}else{
					claimSubmissionProcessor.updateClaimDetails(claim,
							claimSubmissionDTO, claimPage2errorCodeMap);
				}
			} else if (claimPage1errorCodeMap.isEmpty()) {
				claimSubmissionProcessor.updateClaimDetails(claim,
						claimSubmissionDTO, claimPage2errorCodeMap);
			}
			if (!claimPage1errorCodeMap.isEmpty()
					|| !claimPage2errorCodeMap.isEmpty()) {
				setErrorResponseDTO(claimSubmissionDTO, claimSubmissionRespDTO,
						claimPage1errorCodeMap, claimPage2errorCodeMap, null,
						claim);
			} else {
				if (claim != null && claim.getClaimNumber() != null) {
					setSuccessResponse(claim.getClaimNumber(),
							claimSubmissionRespDTO);
				} else {
					claimPage1errorCodeMap
							.put(
									DealerInterfaceErrorConstants.CSA0146,
									dealerInterfaceErrorConstants
											.getPropertyMessageFromErrorCode(DealerInterfaceErrorConstants.CSA0146));
					setErrorResponseDTO(claimSubmissionDTO,
							claimSubmissionRespDTO, claimPage1errorCodeMap,
							claimPage2errorCodeMap, null, claim);
				}
			}
		} catch (RuntimeException e) {
			logger.error(e.getStackTrace());
			claimPage2errorCodeMap
			.put(DealerInterfaceErrorConstants.CSA0125,
					dealerInterfaceErrorConstants
							.getPropertyMessageFromErrorCode(DealerInterfaceErrorConstants.CSA0125));
			if (claimPage1errorCodeMap.isEmpty()) {
				claimSubmissionProcessor.updateClaimDetails(claim,
						claimSubmissionDTO, claimPage2errorCodeMap);
			}
			setErrorResponseDTO(claimSubmissionDTO,claimSubmissionRespDTO,
					claimPage1errorCodeMap, claimPage2errorCodeMap,
					DealerInterfaceErrorConstants.CSA0125, null);
			
		}
	}

	private void setSuccessResponse(String claimNumber,
			ClaimSubmissionResponse claimSubmissionRespDTO) {
		claimSubmissionRespDTO.setStatus(dealerInterfaceErrorConstants
				.getPropertyMessage(DealerInterfaceErrorConstants.SUCESS));
		claimSubmissionRespDTO.setClaimNumber(claimNumber);
	}

	public void setErrorResponseDTO(ClaimSubmission claimSubmissionDTO,
			ClaimSubmissionResponse claimSubmissionRespDTO,
			Map<String, String> claimPage1errorCodeMap,
			Map<String, String> claimPage2errorCodeMap, String errorCode,
			Claim claim) {
		if (claim != null && claim.getId() != null) {
			claimSubmissionRespDTO.setTavantClaimUniqueIdentifier(claim.getId()
					.toString());
		}
		Exceptions exceptions = claimSubmissionRespDTO.addNewExceptions();
		if(claimPage2errorCodeMap!=null)
		claimPage1errorCodeMap.putAll(claimPage2errorCodeMap);
		setErrorResponse(exceptions, claimPage1errorCodeMap);
		claimSubmissionRespDTO.setStatus(DealerInterfaceErrorConstants.STATUS_FAILURE);
	}

	private void setErrorResponse(Exceptions exceptions,
			Map<String, String> errorCodesMap) {
		if (!errorCodesMap.isEmpty()) {
			for (Map.Entry<String, String> errorEntry : errorCodesMap
					.entrySet()) {
				Error error = exceptions.addNewError();
				error.setErrorCode(errorEntry.getKey());
				error.setErrorMessage(errorEntry.getValue());
			}
		}
	}

	public ApplicationSettingsHolder getApplicationSettings() {
		return applicationSettings;
	}

	public DealerInterfaceErrorConstants getDealerInterfaceErrorConstants() {
		return dealerInterfaceErrorConstants;
	}

	public void setDealerInterfaceErrorConstants(
			DealerInterfaceErrorConstants dealerInterfaceErrorConstants) {
		this.dealerInterfaceErrorConstants = dealerInterfaceErrorConstants;
	}

	public ClaimSubmissionTransformer getClaimSubmissionTransformer() {
		return claimSubmissionTransformer;
	}

	public void setClaimSubmissionTransformer(
			ClaimSubmissionTransformer claimSubmissionTransformer) {
		this.claimSubmissionTransformer = claimSubmissionTransformer;
	}

	public ClaimSubmissionProcessor getClaimSubmissionProcessor() {
		return claimSubmissionProcessor;
	}

	public void setClaimSubmissionProcessor(
			ClaimSubmissionProcessor claimSubmissionProcessor) {
		this.claimSubmissionProcessor = claimSubmissionProcessor;
	}

	public ClaimSubmssionValidator getClaimSubmssionValidator() {
		return claimSubmssionValidator;
	}

	public void setClaimSubmssionValidator(
			ClaimSubmssionValidator claimSubmssionValidator) {
		this.claimSubmssionValidator = claimSubmssionValidator;
	}

	public void setApplicationSettings(
			ApplicationSettingsHolder applicationSettings) {
		this.applicationSettings = applicationSettings;
	}

}

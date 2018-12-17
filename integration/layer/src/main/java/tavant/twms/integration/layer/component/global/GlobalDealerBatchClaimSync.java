package tavant.twms.integration.layer.component.global;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlException;
import org.springframework.util.StringUtils;

import tavant.twms.domain.orgmodel.OrgService;
import tavant.twms.domain.orgmodel.Organization;
import tavant.twms.domain.orgmodel.ServiceProvider;
import tavant.twms.integration.layer.component.global.dealerinterfaces.claimsubmission.ClaimSubmissionHandler;
import tavant.twms.integration.layer.constants.DealerInterfaceErrorConstants;
import tavant.twms.integration.layer.constants.IntegrationConstants;
import tavant.twms.integration.layer.transformer.global.dealerinterfaces.claimsubmission.ClaimSubmissionTransformer;
import tavant.twms.integration.layer.util.CalendarUtil;

import com.nmhg.batchclaim_response.ClaimSubmissionResponse;
import com.nmhg.batchclaim_response.ClaimSubmissionResponse.ApplicationArea;
import com.nmhg.batchclaim_response.ClaimSubmissionResponse.ApplicationArea.Sender;
import com.nmhg.batchclaim_response.MTClaimSubmissionResponseDocument;
import com.tavant.dealerinterfaces.claimsubmission.request.ClaimSubmissionDocument;
import com.tavant.dealerinterfaces.claimsubmission.request.ClaimSubmissionDocument.ClaimSubmission;

public class GlobalDealerBatchClaimSync {
	private static final Logger logger = Logger.getLogger("dealerAPILogger");
	private ClaimSubmissionHandler claimSubmissionHandler;
	private ClaimSubmissionTransformer claimSubmissionTransformer;
	private DealerInterfaceErrorConstants dealerInterfaceErrorConstants;
	 private OrgService orgService;

	public MTClaimSubmissionResponseDocument processBatchClaim(String inputXML) {
		MTClaimSubmissionResponseDocument claimSubmissionRespDocDTO = MTClaimSubmissionResponseDocument.Factory
				.newInstance();
		ClaimSubmissionDocument claimSubmissionDocDTO = null;
		ClaimSubmission claimSubmissionDTO = null;
		Map<String, String> errorCodeMap = new HashMap<String, String>();
		ClaimSubmissionResponse claimSubmissionRespDTO = claimSubmissionRespDocDTO.addNewMTClaimSubmissionResponse();
			try {
				claimSubmissionDocDTO = claimSubmissionTransformer
						.convertXMLToRequestDTO(inputXML);
				claimSubmissionDTO = claimSubmissionDocDTO.getClaimSubmission();
				claimSubmissionRespDTO = ClaimSubmissionResponse.Factory
						.newInstance();
				setApplicationArea(claimSubmissionDTO, claimSubmissionRespDTO);
			Organization organization = validateAndGetThedealerOrganization(
					claimSubmissionDTO.getDealerNumber(), errorCodeMap);
				if(organization!=null){
				claimSubmissionHandler.processClaim(claimSubmissionDTO,
							claimSubmissionRespDTO, organization);
				}else {
					errorCodeMap.put(DealerInterfaceErrorConstants.CSA0134,
							dealerInterfaceErrorConstants.getPropertyMessage(
									DealerInterfaceErrorConstants.CSA0134,
									new String[] { claimSubmissionDTO.getDealerNumber() }));
					claimSubmissionHandler.setErrorResponseDTO(
							claimSubmissionDTO, claimSubmissionRespDTO, errorCodeMap,
							null, DealerInterfaceErrorConstants.DAPI04, null);
				}
			} catch (XmlException xe) {
				logger.error("Exception from submitClaim method of DealerIntegrationServiceImpl while parsing or validating request xml :: "
						+ xe);
				errorCodeMap.put(DealerInterfaceErrorConstants.CSA0126,
						xe.getMessage());
				claimSubmissionHandler.setErrorResponseDTO(claimSubmissionDTO,
						claimSubmissionRespDTO, errorCodeMap, null,
						DealerInterfaceErrorConstants.CSA0126, null);
			} catch (Exception e) {
				logger.error(
						"Exception from submitClaim method of DealerIntegrationServiceImpl",
						e);
				errorCodeMap.put(DealerInterfaceErrorConstants.CSA0125,
						dealerInterfaceErrorConstants
						.getPropertyMessageFromErrorCode(DealerInterfaceErrorConstants.CSA0125));
				claimSubmissionHandler.setErrorResponseDTO(claimSubmissionDTO,
						claimSubmissionRespDTO, errorCodeMap, null,
						DealerInterfaceErrorConstants.CSA0125, null);
			}

		if (logger.isDebugEnabled())
			logger.debug("Exiting submitClaim method of IntegrationServiceImpl with response XML"
					+ claimSubmissionRespDocDTO.xmlText());
		claimSubmissionRespDocDTO.setMTClaimSubmissionResponse(claimSubmissionRespDTO);
		return claimSubmissionRespDocDTO;
	}
   
	private Organization validateAndGetThedealerOrganization(
			String dealerNumber, Map<String, String> errorCodeMap) {
		Organization dealerOrg = null;
		if (dealerNumber == null || !StringUtils.hasText(dealerNumber)) {
			errorCodeMap
					.put(DealerInterfaceErrorConstants.CSA002,
							dealerInterfaceErrorConstants
									.getPropertyMessageFromErrorCode(DealerInterfaceErrorConstants.CSA002));
		} else {
			List<ServiceProvider> serviceProviders = orgService.findDealersListByNumber(dealerNumber);
			if(serviceProviders!=null&&!serviceProviders.isEmpty()&&serviceProviders.size()>1){
				for(ServiceProvider serviceProvider:serviceProviders){
					if(serviceProvider.getStatus()!=null&&IntegrationConstants.ACTIVE.equalsIgnoreCase(serviceProvider.getStatus())){
						dealerOrg=serviceProvider;
					}
				}
				if(dealerOrg==null){
					dealerOrg=serviceProviders.get(0);
				}
			}else{
				dealerOrg=serviceProviders.get(0);
			}
			if (dealerOrg == null) {
				errorCodeMap.put(DealerInterfaceErrorConstants.CSA0134,
						dealerInterfaceErrorConstants.getPropertyMessage(
								DealerInterfaceErrorConstants.CSA0134,
								new String[] { dealerNumber }));
			}
		}
		return dealerOrg;
	}

	private void setApplicationArea(ClaimSubmission claimSubmissionDTO,
				ClaimSubmissionResponse claimSubmissionRespDTO) {
			if (claimSubmissionRespDTO != null && claimSubmissionDTO != null
					&& claimSubmissionDTO.getApplicationArea() != null) {
				ApplicationArea applicationArea = claimSubmissionRespDTO
						.addNewApplicationArea();
				if (claimSubmissionDTO.getApplicationArea().getSender() != null) {
					Sender sender = applicationArea.addNewSender();
					if (claimSubmissionDTO.getApplicationArea().getSender()
							.getTask() != null) {
						sender.setTask(claimSubmissionDTO.getApplicationArea()
								.getSender().getTask());
					}
					if (claimSubmissionDTO.getApplicationArea().getSender()
							.getLogicalId() != null) {
						sender.setLogicalId(claimSubmissionDTO.getApplicationArea()
								.getSender().getLogicalId());
					}
					if (claimSubmissionDTO.getApplicationArea().getSender()
							.getReferenceId() != null) {
						sender.setReferenceId(claimSubmissionDTO
								.getApplicationArea().getSender().getReferenceId());
					}
				}
				if (claimSubmissionDTO.getApplicationArea().getBODId() != null) {
					applicationArea.setBODId(claimSubmissionDTO
							.getApplicationArea().getBODId());
				}
				if (claimSubmissionDTO.getApplicationArea().getInterfaceNumber() != null) {
					applicationArea.setInterfaceNumber(claimSubmissionDTO
							.getApplicationArea().getInterfaceNumber());
				}
				Date date = new Date();
				Calendar calender = Calendar.getInstance();
				calender.setTime(date);
				applicationArea.setCreationDateTime(CalendarUtil
						.convertToDateTimeToString(calender.getTime()));
			}
		}

	public ClaimSubmissionHandler getClaimSubmissionHandler() {
		return claimSubmissionHandler;
	}

	public void setClaimSubmissionHandler(
			ClaimSubmissionHandler claimSubmissionHandler) {
		this.claimSubmissionHandler = claimSubmissionHandler;
	}

	public ClaimSubmissionTransformer getClaimSubmissionTransformer() {
		return claimSubmissionTransformer;
	}

	public void setClaimSubmissionTransformer(
			ClaimSubmissionTransformer claimSubmissionTransformer) {
		this.claimSubmissionTransformer = claimSubmissionTransformer;
	}

	public DealerInterfaceErrorConstants getDealerInterfaceErrorConstants() {
		return dealerInterfaceErrorConstants;
	}

	public void setDealerInterfaceErrorConstants(
			DealerInterfaceErrorConstants dealerInterfaceErrorConstants) {
		this.dealerInterfaceErrorConstants = dealerInterfaceErrorConstants;
	}

	public OrgService getOrgService() {
		return orgService;
	}

	public void setOrgService(OrgService orgService) {
		this.orgService = orgService;
	}
	 
}

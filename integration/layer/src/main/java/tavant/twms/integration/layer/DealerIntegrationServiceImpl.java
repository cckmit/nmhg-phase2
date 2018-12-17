package tavant.twms.integration.layer;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlException;

import tavant.twms.domain.orgmodel.Organization;
import tavant.twms.domain.orgmodel.ServiceProvider;
import tavant.twms.domain.orgmodel.User;
import tavant.twms.integration.layer.authentication.dealerinterfaces.DealerInterfaceAuthenticationHandler;
import tavant.twms.integration.layer.component.global.dealerinterfaces.claimsubmission.ClaimSubmissionHandler;
import tavant.twms.integration.layer.component.global.dealerinterfaces.registermajorcomponent.RegisterMajorComponentHandler;
import tavant.twms.integration.layer.component.global.dealerinterfaces.unitservicehistory.UnitServiceHistoryHandler;
import tavant.twms.integration.layer.component.global.dealerinterfaces.unitwarrantyregistration.UnitWarrantyRegistrationHandler;
import tavant.twms.integration.layer.constants.DealerInterfaceErrorConstants;
import tavant.twms.integration.layer.transformer.global.dealerinterfaces.claimsubmission.ClaimSubmissionTransformer;
import tavant.twms.integration.layer.transformer.global.dealerinterfaces.registermajorcomponent.RegisterMajorComponentTransformer;
import tavant.twms.integration.layer.transformer.global.dealerinterfaces.unitservicehistory.UnitServiceHistoryTransformer;
import tavant.twms.integration.layer.transformer.global.dealerinterfaces.unitwarrantyregistration.UnitWarrantyRegistrationTransformer;
import tavant.twms.integration.layer.util.CalendarUtil;
import tavant.twms.security.SecurityHelper;

import com.nmhg.batchclaim_response.ClaimSubmissionResponse;
import com.nmhg.batchclaim_response.ClaimSubmissionResponse.ApplicationArea;
import com.nmhg.batchclaim_response.ClaimSubmissionResponse.ApplicationArea.Sender;
import com.nmhg.batchclaim_response.MTClaimSubmissionResponseDocument;
import com.tavant.dealerinterfaces.claimsubmission.request.ClaimSubmissionDocument;
import com.tavant.dealerinterfaces.claimsubmission.request.ClaimSubmissionDocument.ClaimSubmission;
import com.tavant.dealerinterfaces.majorcomponentregistration.majorcomponentregistrationrequest.MajorComponentRegistrationDocument;
import com.tavant.dealerinterfaces.unitservicehistory.unitservicehistoryrequest.UnitServiceHistoryRequestDocumentDTO;
import com.tavant.dealerinterfaces.unitservicehistory.unitservicehistoryresponse.UnitServiceHistoryResponseDocumentDTO;
import com.tavant.dealerinterfaces.warrantyregistration.request.UnitWarrantyRegistrationRequestDocument;
import com.tavant.dealerinterfaces.warrantyregistration.response.WarrantyRegistrationResponseDocumentDTO;

/**
 * This class is exposed as Webservice to return the unit service history data
 * for the request.
 * 
 * @author TWMSUSER
 */
public class DealerIntegrationServiceImpl implements DealerIntegrationService {

	private static final Logger logger = Logger.getLogger("dealerAPILogger");

	private UnitServiceHistoryTransformer unitServiceHistoryTransformer;

	private UnitServiceHistoryHandler unitServiceHistoryHandler;

	private SecurityHelper securityHelper;

	private ClaimSubmissionHandler claimSubmissionHandler;

	private ClaimSubmissionTransformer claimSubmissionTransformer;

	private DealerInterfaceAuthenticationHandler dealerInterfaceAuthenticationHandler;

	private UnitWarrantyRegistrationHandler unitWarrantyRegistrationHandler;

	private UnitWarrantyRegistrationTransformer unitWarrantyRegistrationTransformer;

	private RegisterMajorComponentHandler registerMajorComponentHandler;

	private RegisterMajorComponentTransformer registerMajorComponentTransformer;
	
	private DealerInterfaceErrorConstants dealerInterfaceErrorConstants;

	/**
	 * The method returns the Unit Service History Data for the requested Serial
	 * number and Item Number if the user is authenticated.
	 * 
	 * @see tavant.twms.integration.layer.DealerIntegrationService#getUnitServiceHistory(java.lang.String)
	 */
	public String getUnitServiceHistory(String inputXml) {
		if (logger.isDebugEnabled())
			logger.debug("Inside getUnitServiceHistory method of IntegrationServiceImpl with request XML" + inputXml);
		UnitServiceHistoryResponseDocumentDTO unitServiceHistoryResponseDocDTO = UnitServiceHistoryResponseDocumentDTO.Factory
				.newInstance();
		String errorCode = dealerInterfaceAuthenticationHandler.getErrorCode();
		UnitServiceHistoryRequestDocumentDTO unitServiceHistoryRequest = null;
		if (errorCode == null) {
			try {
				unitServiceHistoryRequest = unitServiceHistoryTransformer.convertXMLToRequestDTO(inputXml);
				
				if (doesUserNeedCurrentActiveOrg(unitServiceHistoryRequest.getUnitServiceHistoryRequest()
						.getCurrentlyActiveOrganizationNumber())) {
					unitServiceHistoryResponseDocDTO = unitServiceHistoryHandler
							.getUnitServiceHistory(unitServiceHistoryRequest);
					
				} else {
					unitServiceHistoryHandler.setErrorResponseDTO(unitServiceHistoryResponseDocDTO, null,
							DealerInterfaceErrorConstants.DAPI04);
				}
			} catch (XmlException xe) {
				// input XML is invalid
				logger.error("Exception from getUnitServiceHistory method of DealerIntegrationServiceImpl while parsing or validating request xml :: " + xe);
				Map<String, String[]> errorCodeMap = new HashMap<String, String[]>();
				errorCodeMap.put(xe.getMessage(), null);
				unitServiceHistoryHandler.setErrorResponseDTO(unitServiceHistoryResponseDocDTO, errorCodeMap,
						DealerInterfaceErrorConstants.DAPI01);			
			} catch (Exception e) {
				logger.error("Exception from getUnitServiceHistory method of DealerIntegrationServiceImpl", e);
				unitServiceHistoryHandler.setErrorResponseDTO(unitServiceHistoryResponseDocDTO, null,
						DealerInterfaceErrorConstants.DAPI02);
			}
		} else {
			unitServiceHistoryHandler.setErrorResponseDTO(unitServiceHistoryResponseDocDTO, null, errorCode);
		}
		if (logger.isDebugEnabled())
			logger.debug("Exiting getUnitServiceHistory method of IntegrationServiceImpl with response XML"
					+ unitServiceHistoryResponseDocDTO.xmlText());
		return unitServiceHistoryResponseDocDTO.xmlText();
	}

	public void setUnitServiceHistoryHandler(UnitServiceHistoryHandler unitServiceHistoryHandler) {
		this.unitServiceHistoryHandler = unitServiceHistoryHandler;
	}

	public void setUnitServiceHistoryTransformer(UnitServiceHistoryTransformer unitServiceHistoryTransformer) {
		this.unitServiceHistoryTransformer = unitServiceHistoryTransformer;
	}

	private boolean doesUserNeedCurrentActiveOrg(String currentlyActiveOrganizationNumber) {

		boolean isOrganizationOnLoginSet = false;
		List<Organization> orgsList = getAuthenticatedUser().getBelongsToOrganizations();
		int totalOrgsMapped = orgsList.size();
		if (getAuthenticatedUser().isInternalUser()) {
			getAuthenticatedUser().setCurrentlyActiveOrganization(
					getAuthenticatedUser().getBelongsToOrganizations().iterator().next());
			isOrganizationOnLoginSet = true;
		} else {
			if (!(totalOrgsMapped > 1 && StringUtils.isEmpty(currentlyActiveOrganizationNumber))) {
				for (int index = 0; index < totalOrgsMapped; index++) {
					if (!isOrganizationOnLoginSet) {
						Organization organization = orgsList.get(index);
						if (StringUtils.isEmpty(currentlyActiveOrganizationNumber)) {
							getAuthenticatedUser().setCurrentlyActiveOrganization(organization);
							isOrganizationOnLoginSet = true;
						} else if (organization instanceof ServiceProvider
								&& currentlyActiveOrganizationNumber.equals(((ServiceProvider) organization)
										.getServiceProviderNumber())) {
							getAuthenticatedUser().setCurrentlyActiveOrganization(organization);
							isOrganizationOnLoginSet = true;
						}
					}
				}
			}
		}
		return isOrganizationOnLoginSet;
	}

	public String submitClaim(String inputXML) {
		if (logger.isDebugEnabled())
			logger.debug("Inside submitClaim method of IntegrationServiceImpl with request XML"
					+ inputXML);
		MTClaimSubmissionResponseDocument claimSubmissionRespDocDTO = MTClaimSubmissionResponseDocument.Factory
				.newInstance();
		String errorCode = dealerInterfaceAuthenticationHandler.getErrorCode();
		ClaimSubmissionDocument claimSubmissionDocDTO = null;
		ClaimSubmission claimSubmissionDTO = null;
		Map<String, String> errorCodeMap = new HashMap<String, String>();
		ClaimSubmissionResponse claimSubmissionRespDTO = claimSubmissionRespDocDTO.addNewMTClaimSubmissionResponse();
		if (errorCode == null) {
			try {
				claimSubmissionDocDTO = claimSubmissionTransformer
						.convertXMLToRequestDTO(inputXML);
				claimSubmissionDTO = claimSubmissionDocDTO.getClaimSubmission();
				claimSubmissionRespDTO = ClaimSubmissionResponse.Factory
						.newInstance();
				setApplicationArea(claimSubmissionDTO, claimSubmissionRespDTO);
				Organization loggedInUserSelectedOrg=claimSubmissionTransformer.getLoggedInUsersDealershipOrganization(claimSubmissionDTO
						.getDealerNumber(),errorCodeMap);
				if (loggedInUserSelectedOrg!=null&& claimSubmissionTransformer
								.getLoggedInUsersDealership(loggedInUserSelectedOrg, errorCodeMap) != null) {
					claimSubmissionHandler.processClaim(claimSubmissionDTO,
							claimSubmissionRespDTO, loggedInUserSelectedOrg);
				} else {
					errorCodeMap
					.put(DealerInterfaceErrorConstants.CSA0147,
							dealerInterfaceErrorConstants
									.getPropertyMessageFromErrorCode(DealerInterfaceErrorConstants.CSA0147));	
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
						DealerInterfaceErrorConstants.DAPI01, null);
			} catch (RuntimeException e) {
				logger.error(
						"Exception from submitClaim method of DealerIntegrationServiceImpl",
						e);
				errorCodeMap.put(DealerInterfaceErrorConstants.DAPI02,
						dealerInterfaceErrorConstants
						.getPropertyMessageFromErrorCode(DealerInterfaceErrorConstants.DAPI02));
				claimSubmissionHandler.setErrorResponseDTO(claimSubmissionDTO,
						claimSubmissionRespDTO, errorCodeMap, null,
						DealerInterfaceErrorConstants.DAPI02, null);
			}

		} else {
			errorCodeMap.put(errorCode,
					dealerInterfaceErrorConstants
					.getPropertyMessageFromErrorCode(errorCode));
			claimSubmissionHandler.setErrorResponseDTO(claimSubmissionDTO,
					claimSubmissionRespDTO, errorCodeMap, null,
					DealerInterfaceErrorConstants.DAPI01, null);
		}
		if (logger.isDebugEnabled())
			logger.debug("Exiting submitClaim method of IntegrationServiceImpl with response XML"
					+ claimSubmissionRespDocDTO.xmlText());
		claimSubmissionRespDocDTO.setMTClaimSubmissionResponse(claimSubmissionRespDTO);
		return claimSubmissionRespDocDTO.xmlText();
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

	/**
	 * This method process UnitWarrantyRegistration SOAP request
	 * 
	 */
	public String registerUnitWarranty(String soapInputXML) {
		if (logger.isDebugEnabled())
			logger.debug("Inside registerUnitWarranty method of DealerIntegrationServiceImpl with request XML :: "
					+ soapInputXML);
		WarrantyRegistrationResponseDocumentDTO unitWarrantyRegistrationResponseDTO = WarrantyRegistrationResponseDocumentDTO.Factory
				.newInstance();
		// checking authentication status
		String errorCode = dealerInterfaceAuthenticationHandler.getErrorCode();

		if (errorCode == null) {
			try {
				// validating input XML and getting requestDTO
				UnitWarrantyRegistrationRequestDocument unitWarrantyRegistrationRequestDocument = unitWarrantyRegistrationTransformer
						.convertXMLtoRequestDTO(soapInputXML);

				// checking user multiOrg
				if (getAuthenticatedUser().getBelongsToOrganization()!=null) {
					// registering warranty
					unitWarrantyRegistrationHandler.processWarrantyRegistration(
							unitWarrantyRegistrationRequestDocument, unitWarrantyRegistrationResponseDTO);
				} else {
					// multiOrg error
					unitWarrantyRegistrationHandler.setErrorResponseDTO(unitWarrantyRegistrationResponseDTO, null,
							DealerInterfaceErrorConstants.DAPI04);
				}
			} catch (XmlException xe) {
				// input XML is invalid
				logger.error("Exception from registerUnitWarranty method of DealerIntegrationServiceImpl while parsing or validating request xml :: " + xe);
				Map<String, String[]> errorCodeMap = new HashMap<String, String[]>();
				errorCodeMap.put(xe.getMessage(), null);
				unitWarrantyRegistrationHandler.setErrorResponseDTO(unitWarrantyRegistrationResponseDTO, errorCodeMap,
						DealerInterfaceErrorConstants.DAPI01);
			} catch (Exception e) {
				logger.error("Exception from registerUnitWarranty method of DealerIntegrationServiceImpl :: " + e);
				unitWarrantyRegistrationHandler.setErrorResponseDTO(unitWarrantyRegistrationResponseDTO, null,
						DealerInterfaceErrorConstants.DAPI02);
			}
			

		} else {
			// authentication problem
			unitWarrantyRegistrationHandler.setErrorResponseDTO(unitWarrantyRegistrationResponseDTO, null, errorCode);
		}
		if (logger.isDebugEnabled())
			logger.debug("Exiting registerUnitWarranty method of IntegrationServiceImpl with response XML :: "
					+ unitWarrantyRegistrationResponseDTO.xmlText());
		return unitWarrantyRegistrationResponseDTO.xmlText();
	}

	public void setClaimSubmissionHandler(ClaimSubmissionHandler claimSubmissionHandler) {
		this.claimSubmissionHandler = claimSubmissionHandler;
	}

	public void setClaimSubmissionTransformer(ClaimSubmissionTransformer claimSubmissionTransformer) {
		this.claimSubmissionTransformer = claimSubmissionTransformer;
	}

	public void setRegisterMajorComponentHandler(RegisterMajorComponentHandler registerMajorComponentHandler) {
		this.registerMajorComponentHandler = registerMajorComponentHandler;
	}

	public void setRegisterMajorComponentTransformer(RegisterMajorComponentTransformer registerMajorComponentTransformer) {
		this.registerMajorComponentTransformer = registerMajorComponentTransformer;
	}

	private User getAuthenticatedUser() {
		return securityHelper.getLoggedInUser();
	}

	public void setSecurityHelper(SecurityHelper securityHelper) {
		this.securityHelper = securityHelper;
	}

	public DealerInterfaceAuthenticationHandler getDealerInterfaceAuthenticationHandler() {
		return dealerInterfaceAuthenticationHandler;
	}

	public void setDealerInterfaceAuthenticationHandler(
			DealerInterfaceAuthenticationHandler dealerInterfaceAuthenticationHandler) {
		this.dealerInterfaceAuthenticationHandler = dealerInterfaceAuthenticationHandler;
	}

	public UnitWarrantyRegistrationHandler getUnitWarrantyRegistrationHandler() {
		return unitWarrantyRegistrationHandler;
	}

	public void setUnitWarrantyRegistrationHandler(UnitWarrantyRegistrationHandler unitWarrantyRegistrationHandler) {
		this.unitWarrantyRegistrationHandler = unitWarrantyRegistrationHandler;
	}

	public UnitWarrantyRegistrationTransformer getUnitWarrantyRegistrationTransformer() {
		return unitWarrantyRegistrationTransformer;
	}

	public void setUnitWarrantyRegistrationTransformer(
			UnitWarrantyRegistrationTransformer unitWarrantyRegistrationTransformer) {
		this.unitWarrantyRegistrationTransformer = unitWarrantyRegistrationTransformer;
	}

	public String registerMajorComponent(String inputXML) {
		if (logger.isDebugEnabled())
			logger.debug("Inside registerMajorComponent method of IntegrationServiceImpl with request XML" + inputXML);
		WarrantyRegistrationResponseDocumentDTO majorComponentRegistrationRespDocDTO = WarrantyRegistrationResponseDocumentDTO.Factory
				.newInstance();

		String errorCode = dealerInterfaceAuthenticationHandler.getErrorCode();
		MajorComponentRegistrationDocument majorComponentRegistrationDocDTO = null;
		if (errorCode == null) {
			try {
				majorComponentRegistrationDocDTO = registerMajorComponentTransformer.convertXMLToRequestDTO(inputXML);

				if (doesUserNeedCurrentActiveOrg(majorComponentRegistrationDocDTO.getMajorComponentRegistration()
						.getCurrentlyActiveOrganizationNumber())) {
					registerMajorComponentHandler.registerMajorComponent(majorComponentRegistrationDocDTO,
							majorComponentRegistrationRespDocDTO);

				} else {
					registerMajorComponentTransformer.setErrorResponseDTO(majorComponentRegistrationRespDocDTO, null,
							DealerInterfaceErrorConstants.DAPI04);
				}
			} catch (XmlException xe) {
				// input XML is invalid
				logger.error("Exception from registerMajorComponent method of DealerIntegrationServiceImpl while parsing or validating request xml :: " + xe);
				Map<String, String[]> errorCodeMap = new HashMap<String, String[]>();
				errorCodeMap.put(xe.getMessage(), null);				
				registerMajorComponentTransformer.setErrorResponseDTO(majorComponentRegistrationRespDocDTO, errorCodeMap,
						DealerInterfaceErrorConstants.DAPI01);
			} catch (Exception e) {
				logger.error("Exception from registerMajorComponent method of DealerIntegrationServiceImpl", e);
				registerMajorComponentTransformer.setErrorResponseDTO(majorComponentRegistrationRespDocDTO, null,
						DealerInterfaceErrorConstants.DAPI02);
			}

		} else {
			registerMajorComponentTransformer
					.setErrorResponseDTO(majorComponentRegistrationRespDocDTO, null, errorCode);
		}
		if (logger.isDebugEnabled())
			logger.debug("Exiting registerMajorComponent method of IntegrationServiceImpl with response XML"
					+ majorComponentRegistrationRespDocDTO.xmlText());
		return majorComponentRegistrationRespDocDTO.xmlText();
	}

	public DealerInterfaceErrorConstants getDealerInterfaceErrorConstants() {
		return dealerInterfaceErrorConstants;
	}

	public void setDealerInterfaceErrorConstants(
			DealerInterfaceErrorConstants dealerInterfaceErrorConstants) {
		this.dealerInterfaceErrorConstants = dealerInterfaceErrorConstants;
	}

}

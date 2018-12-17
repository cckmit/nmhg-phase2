package tavant.twms.integration.layer.component.global;

import static tavant.twms.integration.layer.util.CalendarUtil.convertToCalendarDate;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.log4j.Logger;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.StringUtils;

import tavant.twms.domain.bu.BusinessUnit;
import tavant.twms.domain.bu.BusinessUnitService;
import tavant.twms.domain.orgmodel.CertificateService;
import tavant.twms.domain.orgmodel.CoreCertification;
import tavant.twms.domain.orgmodel.Dealership;
import tavant.twms.domain.orgmodel.OrgService;
import tavant.twms.domain.orgmodel.Organization;
import tavant.twms.domain.orgmodel.Role;
import tavant.twms.domain.orgmodel.SeriesRefCertification;
import tavant.twms.domain.orgmodel.SupplierService;
import tavant.twms.domain.orgmodel.Technician;
import tavant.twms.domain.orgmodel.TechnicianCertification;
import tavant.twms.domain.orgmodel.User;
import tavant.twms.integration.layer.component.SyncResponse;
import tavant.twms.integration.layer.constants.IntegrationConstants;
import tavant.twms.integration.layer.constants.UserSyncSyncInterfaceErrorConstants;
import tavant.twms.integration.layer.util.LocaleUtil;

import com.tavant.globalsync.techniciansync.TechnicianSyncRequestDocument;
import com.tavant.globalsync.techniciansync.TechnicianSyncRequestDocument.TechnicianSyncRequest;
import com.tavant.globalsync.techniciansync.TechnicianSyncRequestDocument.TechnicianSyncRequest.ApplicationArea;
import com.tavant.globalsync.techniciansync.TechnicianSyncRequestDocument.TechnicianSyncRequest.DataArea;
import com.tavant.globalsync.techniciansync.TechnicianSyncRequestDocument.TechnicianSyncRequest.ApplicationArea.Sender;
import com.tavant.globalsync.techniciansync.TechnicianSyncRequestDocument.TechnicianSyncRequest.DataArea.CertificationDetails.CertificationDetail;


public class GlobalTechnicianSync {
	private TransactionTemplate transactionTemplate;
	private OrgService orgService;
	private SupplierService supplierService;
	private BusinessUnitService businessUnitService;
	private UserSyncSyncInterfaceErrorConstants userSyncSyncInterfaceErrorConstants;
	private CertificateService certificateService;
	private static Logger logger = Logger.getLogger(GlobalTechnicianSync.class
			.getName());
	
	public List<SyncResponse> syncTechnicianUser(
			TechnicianSyncRequestDocument technicianSyncRequestDoc) {
		List<SyncResponse> responses = new ArrayList<SyncResponse>();
		SyncResponse response = new SyncResponse();
		final Map<String, String> errorMessageCodes = new HashMap<String, String>();
		final TechnicianSyncRequest technicianSyncRequest = technicianSyncRequestDoc
				.getTechnicianSyncRequest();
		if (technicianSyncRequestDoc.getTechnicianSyncRequest() != null
				&& technicianSyncRequestDoc.getTechnicianSyncRequest()
						.getApplicationArea() != null) {
			setApplicationArea(response, technicianSyncRequest);
		}
		try {
			transactionTemplate.execute(new TransactionCallbackWithoutResult() {
				protected void doInTransactionWithoutResult(
						TransactionStatus transactionStatus) {
					syncTechnician(technicianSyncRequest, errorMessageCodes);
				}
			});
			if (errorMessageCodes.isEmpty()) {
				response.setSuccessful(true);
			} else {
				buildErrorResponse(response, errorMessageCodes);
			}
			setBusinessUnitInfo(response,technicianSyncRequest);
		} catch (RuntimeException e) {
			logger.error(e,e);
			errorMessageCodes
					.put(UserSyncSyncInterfaceErrorConstants.TK010,
							userSyncSyncInterfaceErrorConstants
									.getPropertyMessageFromErrorCode(UserSyncSyncInterfaceErrorConstants.TK010));
			buildErrorResponse(response, errorMessageCodes);
		}
		responses.add(response);
		return responses;
	}

	private void setBusinessUnitInfo(SyncResponse response,
			TechnicianSyncRequest technicianSyncRequest) {
		TechnicianSyncRequest.DataArea technicianData = technicianSyncRequest
				.getDataArea();
		Technician technician = null;
		if (technicianData.getUserId() != null
				&& StringUtils.hasText(technicianData.getUserId())) {
			technician = orgService.findTechnicianByName(technicianData
					.getUserId());
			if (technician != null && technician.getOrgUser() != null
					&& technician.getOrgUser().getBusinessUnits() != null
					&& !technician.getOrgUser().getBusinessUnits().isEmpty()) {
				if (technician.getOrgUser().getBusinessUnits().size() > 1) {
					if (technician.getOrgUser().getPreferredBu() != null
							&& StringUtils.hasText(technician.getOrgUser()
									.getPreferredBu())) {
						response.setBusinessUnitName(technician.getOrgUser()
								.getPreferredBu());
					}
				} else {
					response.setBusinessUnitName(technician.getOrgUser()
							.getBusinessUnits().first().getName());
				}
			}
		}
		if (response.getBusinessUnitName() == null
				|| !StringUtils.hasText(response.getBusinessUnitName())) {
			response.setBusinessUnitName(IntegrationConstants.NMHG_US);
		}
	}
	private SyncResponse buildErrorResponse(SyncResponse response,
			Map<String, String> errorMessageCodes) {
		response.setSuccessful(false);
		if (errorMessageCodes.isEmpty()) {
			errorMessageCodes
					.put(UserSyncSyncInterfaceErrorConstants.TK010,
							userSyncSyncInterfaceErrorConstants
									.getPropertyMessageFromErrorCode(UserSyncSyncInterfaceErrorConstants.TK010));
		}
		response.setErrorMessages(errorMessageCodes);
		return response;
	}
	
	public void setApplicationArea(SyncResponse response,
			TechnicianSyncRequest technicianSyncRequest) {
		ApplicationArea applicationArea = technicianSyncRequest
				.getApplicationArea();
		Sender sender = applicationArea.getSender();
		response.setUniqueIdName(IntegrationConstants.TECHNICIAN_SYNC_UNIQUE_ID);
		response.setCreationDateTime(Calendar.getInstance());
		if (applicationArea.getBODId() != null) {
			response.setBodId(technicianSyncRequest.getApplicationArea()
					.getBODId());
		}
		if (applicationArea.getInterfaceNumber() != null) {
			response.setInterfaceNumber(applicationArea.getInterfaceNumber());
		}
		if (sender != null) {
			response.setLogicalId(applicationArea.getSender().getLogicalId());
			response.setTask(applicationArea.getSender().getTask());
			response.setReferenceId(applicationArea.getSender()
					.getReferenceId());
			response.setUniqueIdValue(applicationArea.getSender()
					.getReferenceId());
		}
	}
	public void syncTechnician(TechnicianSyncRequest technicianSyncRequest,
			Map<String, String> errorMessageCodes) {
		Technician technician = null;
		boolean isCreate = false;
		User user=null;
		TechnicianSyncRequest.DataArea technicianData = technicianSyncRequest
				.getDataArea();
		if (technicianData.getUserId() == null
				|| !StringUtils.hasText(technicianData.getUserId())) {
			errorMessageCodes
					.put(UserSyncSyncInterfaceErrorConstants.TK001,
							userSyncSyncInterfaceErrorConstants
									.getPropertyMessageFromErrorCode(UserSyncSyncInterfaceErrorConstants.TK001));
		} else {
			technician = orgService.findTechnicianByName(technicianData
					.getUserId());
		}
		if (technician == null) {
		 user = orgService.findInternalUser(technicianData
					.getUserId(),IntegrationConstants.DEALER_TYPE);
			technician = new Technician();
			isCreate = true;
		}else{
			user=technician.getOrgUser();
		}
		createOrUpdateTechnician(technicianData, errorMessageCodes, isCreate,
				technician,user);
	}
	private void createOrUpdateTechnician(DataArea technicianData,
			Map<String, String> errorMessageCode, boolean isCreate,
			Technician technician,User user) {
		if ((technicianData.getDealerCode() == null
				|| !StringUtils.hasText(technicianData.getDealerCode()))
				&& isCreate&&user==null) {
			errorMessageCode
					.put(UserSyncSyncInterfaceErrorConstants.TK002,
							userSyncSyncInterfaceErrorConstants
									.getPropertyMessageFromErrorCode(UserSyncSyncInterfaceErrorConstants.TK002));
		} else if ((technicianData.getDealerCode() != null && StringUtils
				.hasText(technicianData.getDealerCode()))
				&& (technicianData.getDealerCode().length() < 6 || technicianData
						.getDealerCode().length() > 6)) {
			errorMessageCode
					.put(UserSyncSyncInterfaceErrorConstants.TK009,
							userSyncSyncInterfaceErrorConstants
									.getPropertyMessageFromErrorCode(UserSyncSyncInterfaceErrorConstants.TK009));
		} else {
			 if(user==null){
				 user=new User(); 
			 }
			List<Dealership> dealersList = null;
			if (technicianData.getDealerCode() != null
					&& StringUtils.hasText(technicianData.getDealerCode())) {
				dealersList = getTechnicianDealers(technicianData, errorMessageCode);
			}
			if ((dealersList == null || dealersList.isEmpty()) && isCreate&&user==null) {
				errorMessageCode
						.put(UserSyncSyncInterfaceErrorConstants.TK003,
								userSyncSyncInterfaceErrorConstants
										.getPropertyMessageVlaue(
												UserSyncSyncInterfaceErrorConstants.TK003,
												new String[] { technicianData
														.getDealerCode() }));
			} else {
				setUserDetails(technicianData, dealersList, technician,
						isCreate,user);
				addBusinessUnits(technicianData, user);
				setTechnicianDetails(technicianData,technician);
				if (errorMessageCode.isEmpty()) {
					if (isCreate) {
						if(user.getId()==null){
							Locale  defaultLocale=new Locale("en_US");
							user.setLocale(defaultLocale);
							orgService.createUser(user)	;
						}
						technician.setOrgUser(user);
						orgService.createTechnician(technician);
					} else {
						orgService.updateTechnician(technician);
					}
				}
				if (technicianData.getCertificationDetails() != null
						&& technicianData.getCertificationDetails()
								.getCertificationDetailArray() != null
						&& technicianData.getCertificationDetails()
								.getCertificationDetailArray().length > 0) {
					createOrUpdateTechnicianCertifications(technicianData,
							technician, errorMessageCode);
				}
			}
		}
	}
	private void setTechnicianDetails(DataArea technicianData,
			Technician technician) {
		if (technicianData.getManagerName() != null
				&& StringUtils.hasText(technicianData.getManagerName())) {
			technician.setServiceManagerName(technicianData.getManagerName());
		}
		if (technicianData.getComments() != null
				&& StringUtils.hasText(technicianData.getComments())) {
			technician.setComments(technicianData.getComments());
		}
		if (technicianData.getStatus() != null
				&& StringUtils.hasText(technicianData.getStatus().toString())) {
			technician.setStatus(technicianData.getStatus().toString());
		} else {
			technician.setStatus(IntegrationConstants.ACTIVE);
		}
	}

	private List<Dealership> getTechnicianDealers(DataArea technicianData,
			Map<String, String> errorMessageCode) {
		List<Dealership> dealersList = new ArrayList<Dealership>();
		List<Dealership> dealers = orgService
				.findDealershipsFromDealerCode(technicianData.getDealerCode());
		if (dealers == null || dealers.isEmpty()) {
			errorMessageCode.put(UserSyncSyncInterfaceErrorConstants.TK003,
					userSyncSyncInterfaceErrorConstants
							.getPropertyMessageVlaue(
									UserSyncSyncInterfaceErrorConstants.TK003,
									new String[] { technicianData
											.getDealerCode() }));
		}
		dealersList.addAll(dealers);
		return dealersList;
	}

	/*private void eleminateExistingDealercodesForTechncian(
			Technician technician, List<Dealership> dealersList,
			List<Dealership> dealers) {
		if (technician.getBelongsToOrganizations() != null
				&& !technician.getBelongsToOrganizations().isEmpty()
				&& dealers != null && !dealers.isEmpty()) {
			for (Dealership dealership : dealers) {
				boolean isDealerExist = false;
				for (Organization organization : technician
						.getBelongsToOrganizations()) {
					if (organization.getId().equals(dealership.getId())) {
						isDealerExist = true;
						break;
					}
				}
				if (!isDealerExist) {
					dealersList.add(dealership);
				}
			}
		}
	}*/
	private void createOrUpdateTechnicianCertifications(
			DataArea technicianData, Technician technician,Map<String, String> errorMessageCode) {
		CertificationDetail[] CertificationDetails=technicianData.getCertificationDetails()
		.getCertificationDetailArray();
		for(CertificationDetail certificationDetail:CertificationDetails){
			Map<String, String> certificationErrorCodeMap=validateCertificationDetails(certificationDetail);
			if(!certificationErrorCodeMap.isEmpty()){
				errorMessageCode.putAll(certificationErrorCodeMap);
			}else if(technician!=null&&technician.getId()!=null){
				createOrUpdateCertificate(technician,certificationDetail,errorMessageCode);
			}
		}
		
	}

	private void createOrUpdateCertificate(Technician technician,
			CertificationDetail certificationDetail,
			Map<String, String> errorMessageCode) {
		//TODO: Just provided compilation fix, actual fix to be provided by Kalyani
		List<CoreCertification> coreCertificates = certificateService
				.findCoCertificationByCertificateNameAndBrand(
						certificationDetail.getTitle(),
						IntegrationConstants.CORE_CERTIFICATION_DEFAULT_CATEGORY_NAME,
						certificationDetail.getBrand().toString());
		List<TechnicianCertification> technicianCertifications = new ArrayList<TechnicianCertification>();
		if (coreCertificates != null && !coreCertificates.isEmpty()) {
			for (CoreCertification coreCertificate : coreCertificates) {
				TechnicianCertification techcertification = setTechnicianCertificationData(
						technician, certificationDetail);
				techcertification.setIsCoreLevel(Boolean.TRUE);
				techcertification.setCoreCertification(coreCertificate);
				technicianCertifications.add(techcertification);
			}
		} else {
			List<SeriesRefCertification> seriesRefCertifications = certificateService
					.findSeriesCertificationsByCertificateNameAndBrand(
							IntegrationConstants.SERIES_CERTIFICATION_DEFAULT_CATEGORY_NAME,
							certificationDetail.getTitle(), certificationDetail
									.getBrand().toString());
			if (seriesRefCertifications != null
					&& !seriesRefCertifications.isEmpty()) {
				TechnicianCertification techcertification = setTechnicianCertificationData(
						technician, certificationDetail);
				techcertification.setIsCoreLevel(Boolean.FALSE);
				techcertification
						.setSeriesCertification(seriesRefCertifications);
				technicianCertifications.add(techcertification);
			}
		}
		if (technicianCertifications == null
				|| technicianCertifications.isEmpty()) {
			errorMessageCode.put(UserSyncSyncInterfaceErrorConstants.TK008,
					userSyncSyncInterfaceErrorConstants
							.getPropertyMessageVlaue(
									UserSyncSyncInterfaceErrorConstants.TK008,
									new String[] { certificationDetail
											.getTitle() }));
		} else {
			technician.setTechnicianCertifications(technicianCertifications);
			orgService.updateTechnician(technician);
		}
	}

	private TechnicianCertification setTechnicianCertificationData(
			Technician technician, CertificationDetail certificationDetail) {
		TechnicianCertification techcertification = new TechnicianCertification();
		techcertification.setTechUser(technician);
		techcertification.setCertificationName(certificationDetail.getTitle());
		techcertification.setBrand(certificationDetail.getBrand().toString());
		techcertification
				.setCertificationFromDate(convertToCalendarDate(certificationDetail
						.getDateAchieved()));
		techcertification
				.setCertificationToDate(convertToCalendarDate(certificationDetail
						.getDateExpired()));
		return techcertification;
	}

	private Map<String, String> validateCertificationDetails(
			CertificationDetail certificationDetail) {
		Map<String, String> certificationErrorCodeMap = new HashMap<String, String>();
		if (certificationDetail.getTitle() == null
				|| !StringUtils.hasText(certificationDetail.getTitle())) {
			certificationErrorCodeMap
					.put(UserSyncSyncInterfaceErrorConstants.TK004,
							userSyncSyncInterfaceErrorConstants
									.getPropertyMessageFromErrorCode(UserSyncSyncInterfaceErrorConstants.TK004));
			setErrorMessagesWithOutCertificateName(certificationDetail,
					certificationErrorCodeMap);
		} else {
			setErrorMessagesWithCertificateName(certificationDetail,
					certificationErrorCodeMap);

		}
		return certificationErrorCodeMap;
	}

	private void setErrorMessagesWithOutCertificateName(
			CertificationDetail certificationDetail,
			Map<String, String> certificationErrorCodeMap) {
		if (certificationDetail.getDateAchieved() == null) {
			certificationErrorCodeMap
					.put(UserSyncSyncInterfaceErrorConstants.TK011,
							userSyncSyncInterfaceErrorConstants
									.getPropertyMessageFromErrorCode(UserSyncSyncInterfaceErrorConstants.TK011));
		}
		if (certificationDetail.getBrand() == null
				|| !StringUtils.hasText(certificationDetail.getBrand()
						.toString())) {
			certificationErrorCodeMap
					.put(UserSyncSyncInterfaceErrorConstants.TK012,
							userSyncSyncInterfaceErrorConstants
									.getPropertyMessageFromErrorCode(UserSyncSyncInterfaceErrorConstants.TK012));
		}
		if (certificationDetail.getDateExpired() == null) {
			certificationErrorCodeMap
					.put(UserSyncSyncInterfaceErrorConstants.TK013,
							userSyncSyncInterfaceErrorConstants
									.getPropertyMessageFromErrorCode(UserSyncSyncInterfaceErrorConstants.TK013));
		}
	}

	private void setErrorMessagesWithCertificateName(CertificationDetail certificationDetail,Map<String, String> certificationErrorCodeMap) {
		if (certificationDetail.getDateAchieved() == null) {
			certificationErrorCodeMap
					.put(UserSyncSyncInterfaceErrorConstants.TK005,
							userSyncSyncInterfaceErrorConstants
									.getPropertyMessageVlaue(
											UserSyncSyncInterfaceErrorConstants.TK005,
											new String[] { certificationDetail
													.getTitle() }));
		}
		if (certificationDetail.getBrand() == null
				|| !StringUtils.hasText(certificationDetail.getBrand().toString())) {
			certificationErrorCodeMap
					.put(UserSyncSyncInterfaceErrorConstants.TK006,
							userSyncSyncInterfaceErrorConstants
									.getPropertyMessageVlaue(
											UserSyncSyncInterfaceErrorConstants.TK006,
											new String[] { certificationDetail
													.getTitle() }));
		}
		if (certificationDetail.getDateExpired() == null) {
			certificationErrorCodeMap
					.put(UserSyncSyncInterfaceErrorConstants.TK007,
							userSyncSyncInterfaceErrorConstants
									.getPropertyMessageVlaue(
											UserSyncSyncInterfaceErrorConstants.TK007,
											new String[] { certificationDetail
													.getTitle() }));
		}
	}

	private void setUserDetails(DataArea technicianData,
			List<Dealership> dealers,Technician technician,boolean isCreate,User user) {
		if(dealers!=null&&!dealers.isEmpty()&&dealers.get(0).getAddress()!=null){
		user.setAddress(dealers.get(0).getAddress());
		}
		user.setUserId(technicianData.getUserId().trim());
		if(user==null||user.getName()==null||StringUtils.hasText(user.getName()))
		user.setName(technicianData.getUserId().trim());
		if(user==null||user.getUserType()==null||!StringUtils.hasText(user.getUserType()))
		user.setUserType(IntegrationConstants.TECHNICIAN_SYNC_USER_TYPE);
		if(technicianData.getJobTitle()!=null&&StringUtils.hasText(technicianData.getJobTitle())){
		user.setJobTitle(technicianData.getJobTitle());
		}else if(isCreate&&user==null){
			user.setJobTitle(IntegrationConstants.TECHNICIAN_SYNC_USER_TYPE);
		}if(technicianData.getFirstName()!=null&&StringUtils.hasText(technicianData.getFirstName())){
			user.setFirstName(technicianData.getFirstName());
		}else if(isCreate&&user==null){
			user.setFirstName(technicianData.getUserId());
		}if(technicianData.getLastName()!=null&&StringUtils.hasText(technicianData.getLastName())){
			user.setLastName(technicianData.getLastName());
		}
		setRole(technicianData,user,dealers,isCreate);
	}
	
	private void setRole(DataArea technicianData, User user,
			List<Dealership> dealers, boolean isCreate) {
		Role role = null;
		Set<Role> roles = null;
		if (user.getRoles() != null && !user.getRoles().isEmpty()) {
			roles = user.getRoles();
		} else if (isCreate) {
			roles = new HashSet<Role>();
			role = orgService.findRoleByName(Role.TECHNICIAN);
			roles.add(role);
			user.setRoles(roles);
		}
		if (!technicianRoleIsAlreadyExist(roles)) {
			role = orgService.findRoleByName(Role.TECHNICIAN);
			if(roles==null){
			roles = new HashSet<Role>();
			roles.add(role);
			user.setRoles(roles);
			}else{
			roles.add(role);
			}
		}
		if (technicianData.getStatus() != null
				&& StringUtils.hasText(technicianData.getStatus().toString())) {
			if ("ACTIVE"
					.equalsIgnoreCase(technicianData.getStatus().toString())) {
				user.getD().setActive(Boolean.TRUE);
			} else {
				user.getD().setActive(Boolean.FALSE);
			}
		}
		if (dealers != null && !dealers.isEmpty()) {
			setOrganozationsForTechnician(user, dealers);
		}
	}


	private void setOrganozationsForTechnician(User user,
			List<Dealership> dealers) {
		List<Organization> orgList = new ArrayList<Organization>();
		for (Dealership dealer : dealers) {
			boolean isOrganizationExist=false;
			if (!orgList.isEmpty()) {
				for (Organization existingOrganization : orgList) {
					if (dealer.getId().equals(existingOrganization.getId())) {
						isOrganizationExist=true;
						break;
					}
				}
				if(!isOrganizationExist){
					Organization dealerOrg = (Organization) dealer;
					orgList.add(dealerOrg);	
				}
			} else {
				Organization dealerOrg = (Organization) dealer;
				orgList.add(dealerOrg);
			}
		}
		user.setBelongsToOrganizations(orgList);
	}

	private boolean technicianRoleIsAlreadyExist(Set<Role> roles) {
		if (roles != null && !roles.isEmpty()) {
			for (Role userRole : roles) {
				if (userRole.getName().equalsIgnoreCase("TECHNICIAN")) {
					return true;
				}
			}
		}
		return false;
	}

	private void addBusinessUnits(final DataArea technicianData, final User user) {
		final TreeSet<BusinessUnit> businessUnitMapping = new TreeSet<BusinessUnit>();
		if (user.getBelongsToOrganizations() != null
				&& !user.getBelongsToOrganizations().isEmpty()) {
			for (Organization organization : user.getBelongsToOrganizations()) {
				businessUnitMapping.addAll(organization.getBusinessUnits());
			}
		}
		user.setBusinessUnits(businessUnitMapping);
	}
	public TransactionTemplate getTransactionTemplate() {
		return transactionTemplate;
	}
	public void setTransactionTemplate(TransactionTemplate transactionTemplate) {
		this.transactionTemplate = transactionTemplate;
	}
	public OrgService getOrgService() {
		return orgService;
	}
	public void setOrgService(OrgService orgService) {
		this.orgService = orgService;
	}
	public SupplierService getSupplierService() {
		return supplierService;
	}
	public void setSupplierService(SupplierService supplierService) {
		this.supplierService = supplierService;
	}
	public BusinessUnitService getBusinessUnitService() {
		return businessUnitService;
	}
	public void setBusinessUnitService(BusinessUnitService businessUnitService) {
		this.businessUnitService = businessUnitService;
	}
	public UserSyncSyncInterfaceErrorConstants getUserSyncSyncInterfaceErrorConstants() {
		return userSyncSyncInterfaceErrorConstants;
	}
	public void setUserSyncSyncInterfaceErrorConstants(
			UserSyncSyncInterfaceErrorConstants userSyncSyncInterfaceErrorConstants) {
		this.userSyncSyncInterfaceErrorConstants = userSyncSyncInterfaceErrorConstants;
	}
	public CertificateService getCertificateService() {
		return certificateService;
	}
	public void setCertificateService(CertificateService certificateService) {
		this.certificateService = certificateService;
	}
	public static Logger getLogger() {
		return logger;
	}
	public static void setLogger(Logger logger) {
		GlobalTechnicianSync.logger = logger;
	}
	

}

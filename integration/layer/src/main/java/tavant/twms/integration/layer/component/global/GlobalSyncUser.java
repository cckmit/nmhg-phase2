package tavant.twms.integration.layer.component.global;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.StringUtils;

import tavant.globalsync.usersync.PostalAddressTypeDTO;
import tavant.globalsync.usersync.UserTypeDTO;
import tavant.twms.domain.bu.BusinessUnit;
import tavant.twms.domain.bu.BusinessUnitService;
import tavant.twms.domain.orgmodel.Address;
import tavant.twms.domain.orgmodel.OrgService;
import tavant.twms.domain.orgmodel.Organization;
import tavant.twms.domain.orgmodel.Role;
import tavant.twms.domain.orgmodel.SupplierService;
import tavant.twms.domain.orgmodel.Technician;
import tavant.twms.domain.orgmodel.User;
import tavant.twms.integration.layer.component.SyncResponse;

public class GlobalSyncUser {

	private static Logger logger = Logger.getLogger(GlobalSyncUser.class
			.getName());
	private TransactionTemplate transactionTemplate;
	private OrgService orgService;
	private SupplierService supplierService;
	private BusinessUnitService businessUnitService;

	public List<SyncResponse> sync(final Collection<UserTypeDTO> users) {
		logger.debug("Received " + users + " users for synchronising.");
		final List<SyncResponse> responses = new ArrayList<SyncResponse>();
		SyncResponse response = new SyncResponse();
		for (final UserTypeDTO userTypeDTO : users) {
			try {
				validate(userTypeDTO);
				final String login = userTypeDTO.getNMHGUid();
				response.setBusinessId(login);
				response.setUniqueIdName("Login");
				response.setUniqueIdValue(login);
				transactionTemplate
						.execute(new TransactionCallbackWithoutResult() {
							protected void doInTransactionWithoutResult(
									TransactionStatus transactionStatus) {
								sync(userTypeDTO);
							}
						});
				response.setSuccessful(true);
			} catch (RuntimeException e) {
				logger.error(e, e);
				response = buildErrorResponse(response, e.getMessage(),
						userTypeDTO.getUserId());
			}
			responses.add(response);
		}
		return responses;
	}

	private SyncResponse buildErrorResponse(final SyncResponse response,
			final String message, final String userId) {
		response.setSuccessful(false);
		if (userId == null) {
			response.setException(new StringBuilder().append(
					" Error syncing, no userId Found").append("\n").append(
					" The Reason for the Error is : ").append(message).append(
					"\n").append("\n").toString());
		} else {
			response.setException(new StringBuilder().append(
					" Error syncing User with id ").append(userId).append("\n")
					.append(" The Reason for the Error is : ").append(message)
					.append("\n").append("\n").toString());
		}
		response.setErrorCode(SyncResponse.ERROR_CODE_BUSINESS_PROCESS_ERROR);
		response.setErrorType(SyncResponse.ERROR_CODE_BUSINESS_PROCESS_ERROR);
		return response;
	}

	private void validate(final UserTypeDTO userTypeDTO) {
		final StringBuilder errorMessage = new StringBuilder();
		if (userTypeDTO != null) {
			if (!StringUtils.hasText(userTypeDTO.getCustomerNumber())) {
				appendErrorMessage(errorMessage,
						"CustomerNumber cannot be Null");
			}
			if (!StringUtils.hasText(userTypeDTO.getUserId())) {
				appendErrorMessage(errorMessage, "UserId cannot be Null");
			}
			if (!StringUtils.hasText(userTypeDTO.getNMHGUid())) {
				appendErrorMessage(errorMessage, "Login cannot be Null");
			}
			if (!StringUtils.hasText(userTypeDTO.getPreferredLanguage())) {
				appendErrorMessage(errorMessage,
						"Preferred  Language cannot be Null");
			}

			if (userTypeDTO.getBusinessUnits() == null
					|| userTypeDTO.getBusinessUnits().sizeOfBUNameArray() == 0) {
				appendErrorMessage(errorMessage,
						"List of busisness units cannot be Null or Empty");
			}
		}

		if (StringUtils.hasText(errorMessage.toString())) {
			throw new RuntimeException(errorMessage.toString());
		}
	}

	public void sync(final UserTypeDTO userTypeDTO) {
		boolean isTechnician = false;

		for (String roleStr : userTypeDTO.getRoles().getNMHGRoleArray()) {
			if (roleStr.equalsIgnoreCase("TECHNICIAN")) {
				isTechnician = true;
			}
		}

		if (isTechnician) {
			Technician technician = orgService.findTechnicianByName(userTypeDTO
					.getNMHGUid());
			logger.debug(userTypeDTO.getNMHGUid());
			if (technician == null) {
				logger.debug("Creating new Technician");
				createNewTechnician(userTypeDTO, userTypeDTO.getRoles()
						.getNMHGRoleArray());
			} else {
				logger.debug("Updating Technician");
				updateTechnician(userTypeDTO, technician, userTypeDTO
						.getRoles().getNMHGRoleArray());
			}

		} else {
			final User user = orgService.findUserByName(userTypeDTO
					.getNMHGUid());
			if (user == null) {
				logger.debug("Creating new User");
				createNewUser(userTypeDTO, userTypeDTO.getRoles()
						.getNMHGRoleArray());
			} else {
				logger.debug("Updating User");
				updateUser(userTypeDTO, user, userTypeDTO.getRoles()
						.getNMHGRoleArray());
			}
		}

	}

	private void updateUser(final UserTypeDTO userTypeDTO, final User user,
			final String[] strings) {
		merge(userTypeDTO, user);
		mapRoleOrganization(userTypeDTO, user, strings);
		addBusinessUnit(userTypeDTO, user);
		orgService.updateUser(user);
	}

	private void createNewUser(final UserTypeDTO userTypeDTO,
			final String[] strings) {
		final User user = new User();
		final Address address = new Address();
		user.setAddress(address);
		merge(userTypeDTO, user);
		mapRoleOrganization(userTypeDTO, user, strings);
		addBusinessUnit(userTypeDTO, user);
		orgService.createUser(user);
	}

	private void addBusinessUnit(final UserTypeDTO userTypeDTO, final User user) {
		String[] businessUnits = null;
		if (userTypeDTO.getBusinessUnits() != null) {
			businessUnits = userTypeDTO.getBusinessUnits().getBUNameArray();
		}
		List<String> businessUnitList = null;
		final TreeSet<BusinessUnit> businessUnitMapping = new TreeSet<BusinessUnit>();
		if (businessUnits != null && businessUnits.length > 1) {
			businessUnitList = Arrays.asList(businessUnits);
		}
		if (!CollectionUtils.isEmpty(businessUnitList)) {
			for (String buName : businessUnitList) {
				BusinessUnit bu = null;
				if (StringUtils.hasText(buName)) {
					businessUnitService.findBusinessUnit(buName.trim());
				}
				businessUnitMapping.add(bu);
			}
			user.setBusinessUnits(businessUnitMapping);
		}
		if (user.getD() != null) {
			if ("ACTIVE".equalsIgnoreCase(userTypeDTO.getStatus().toString())) {
				user.getD().setActive(Boolean.TRUE);
			} else {
				user.getD().setActive(Boolean.FALSE);
			}
		}
	}

	private void merge(final UserTypeDTO userTypeDTO, final User user) {
		user.setUserId(userTypeDTO.getUserId().trim());
		user.setName(userTypeDTO.getNMHGUid().trim());
		user.setUserType(userTypeDTO.getUserType().toString());
		final String preferredLanguage = getLocale(userTypeDTO
				.getPreferredLanguage());
		final Locale locale = new Locale(preferredLanguage);
		user.setLocale(locale);
		user.setJobTitle(userTypeDTO.getJobTitle().trim());
		if (userTypeDTO.getPostalAddress() != null) {
			user.setFirstName(userTypeDTO.getPostalAddress().getFirstName());
			user.setLastName(userTypeDTO.getPostalAddress().getLastName());
			user.setEmail(userTypeDTO.getPostalAddress().getEmail());
		}
		setUserAddress(userTypeDTO, user);
	}

	private void setUserAddress(final UserTypeDTO userTypeDTO, final User user) {
		final Address address = user.getAddress();
		PostalAddressTypeDTO addressDTO = userTypeDTO.getPostalAddress();
		address.setAddressLine1(addressDTO.getAddressLine1());
		address.setAddressLine2(addressDTO.getAddressLine2());
		address.setAddressLine3(addressDTO.getAddressLine3());
		address.setAddressLine4(addressDTO.getAddressLine4());
		address.setAddressIdOnRemoteSystem(addressDTO.getSiteNumber());
		address.setCity(addressDTO.getCity());
		address.setState(addressDTO.getState());
		address.setCounty(addressDTO.getCountyCode());
		address.setCountry(addressDTO.getCountry());
		address.setZipCode(addressDTO.getPostalCode());
		address.setZipcodeExtension(addressDTO.getZipExtension());
		address.setPhone(addressDTO.getTelephone());
		address.setPhoneExt(addressDTO.getPhoneExt());
		address.setSecondaryPhone(addressDTO.getMobile());
		address.setSecondaryPhoneExt(addressDTO.getSecondaryPhoneExt());
		address.setFax(addressDTO.getFax());
		address.setEmail(addressDTO.getEmail());
		address.setDeliveryPointCode(addressDTO.getDeliveryPointCode());
		address.setStatus(userTypeDTO.getStatus().toString());
	}

	private void updateTechnician(final UserTypeDTO userTypeDTO,
			final Technician technician, final String[] userRoles) {
		merge(userTypeDTO, technician.getOrgUser());
		mapRoleOrganization(userTypeDTO, technician.getOrgUser(), userRoles);
		addBusinessUnit(userTypeDTO, technician.getOrgUser());
		orgService.updateTechnician(technician);
	}

	private void createNewTechnician(final UserTypeDTO userTypeDTO,
			final String[] userRoles) {
		final Technician technician = new Technician();
		final Address address = new Address();
		technician.getOrgUser().setAddress(address);
		merge(userTypeDTO, technician.getOrgUser());
		mapRoleOrganization(userTypeDTO, technician.getOrgUser(), userRoles);
		addBusinessUnit(userTypeDTO, technician.getOrgUser());
		orgService.createTechnician(technician);
	}

	private void mapRoleOrganization(UserTypeDTO userTypeDTO, User user,
			String[] userRoles) {
		Role role = null;
		Set<Role> roles = new HashSet<Role>();
		Organization belongsToOrganization = null;

		if (userTypeDTO.getUserType().toString().equalsIgnoreCase("INTERNAL")) {
			belongsToOrganization = orgService.findOrganizationByName("OEM");
		} else {
			for (String user_role : userRoles) {
				if (user_role.equalsIgnoreCase("DEALER")) {
					role = orgService.findRoleByName(Role.DEALER);
					belongsToOrganization = orgService
							.findDealerByNumber(userTypeDTO.getCustomerNumber());
				} else if (user_role.equalsIgnoreCase("SUPPLIER")) {
					role = orgService.findRoleByName(Role.SUPPLIER);
					belongsToOrganization = supplierService
							.findSupplierByNumber(userTypeDTO
									.getCustomerNumber());
				} else if (user_role.equalsIgnoreCase("TECHNICIAN")) {
					role = orgService.findRoleByName(Role.TECHNICIAN);
					belongsToOrganization = orgService
							.findDealerByNumber(userTypeDTO.getCustomerNumber());
				}
				roles.add(role);
			}
			user.setRoles(roles);
		}
		if (belongsToOrganization == null) {
			throw new RuntimeException("Unknown Company Id");
		}
		final List<Organization> orgList = new ArrayList<Organization>();
		orgList.add(belongsToOrganization);
		user.setBelongsToOrganizations(orgList);
	}

	/**
	 * This method take preferredLanguage from sync and then return locale as
	 * String
	 * 
	 * @param preferredLanguage
	 * @return locale as String
	 */
	private String getLocale(String preferredLanguage) {
		if (preferredLanguage.equalsIgnoreCase("DE")) {
			return "de_DE";
		} else if (preferredLanguage.equalsIgnoreCase("ES")) {

			return "es_ES";
		} else if (preferredLanguage.equalsIgnoreCase("FR")) {

			return "fr_FR";
		} else if (preferredLanguage.equalsIgnoreCase("IT")) {

			return "it_IT";
		} else if (preferredLanguage.equalsIgnoreCase("NL")) {

			return "nl_NL";
		} else if (preferredLanguage.equalsIgnoreCase("EN")) {

			return "en_GB";
		} else if (preferredLanguage.equalsIgnoreCase("ZH")) {

			return "zh_CN";
		}
		return "en_US";
	}

	private void appendErrorMessage(final StringBuilder errorMessage,
			String appendMessage) {
		if (StringUtils.hasText(errorMessage.toString())) {
			errorMessage.append(", ");
		}
		errorMessage.append(appendMessage);
	}

	public void setTransactionTemplate(TransactionTemplate transactionTemplate) {
		this.transactionTemplate = transactionTemplate;
	}

	public void setOrgService(OrgService orgService) {
		this.orgService = orgService;
	}

	public void setSupplierService(SupplierService supplierService) {
		this.supplierService = supplierService;
	}

	public void setBusinessUnitService(BusinessUnitService businessUnitService) {
		this.businessUnitService = businessUnitService;
	}

}

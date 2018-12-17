/*
 *   Copyright (c)2006 Tavant Technologies
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
package tavant.twms.web.actions;

import static tavant.twms.domain.common.AdminConstants.INTEGRATION_ERRORS;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Pattern;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts2.util.ServletContextAware;

import tavant.twms.common.TWMSCommonUtil;
import tavant.twms.common.TWMSException;
import tavant.twms.dateutil.TWMSDateFormatUtil;
import tavant.twms.domain.bu.BusinessUnit;
import tavant.twms.domain.bu.BusinessUnitService;
import tavant.twms.domain.common.AdminConstants;
import tavant.twms.domain.complaints.CountryState;
import tavant.twms.domain.complaints.CountryStateService;
import tavant.twms.domain.integration.SyncTracker;
import tavant.twms.domain.integration.SyncTrackerService;
import tavant.twms.domain.orgmodel.Address;
import tavant.twms.domain.orgmodel.Country;
import tavant.twms.domain.orgmodel.OrgService;
import tavant.twms.domain.orgmodel.Organization;
import tavant.twms.domain.orgmodel.Role;
import tavant.twms.domain.orgmodel.RoleCategory;
import tavant.twms.domain.orgmodel.ServiceProvider;
import tavant.twms.domain.orgmodel.User;
import tavant.twms.security.SecurityHelper;
import tavant.twms.web.admin.dto.UserDTO;
import tavant.twms.web.security.authn.decrypter.UsernameDecrypter;

@SuppressWarnings("serial")
public class ManageSSO extends TwmsActionSupport implements ServletContextAware {

	private static Logger logger = Logger.getLogger(ManageSSO.class);

	private ServletContext servletContext;

	private String actionUrl;

	private SecurityHelper securityHelper;

	private ServiceProvider serviceProvider;

	private UsernameDecrypter aesBasedUsernameDecrypter;

	private User user;

	private Address address;

	private UserDTO userDTO;

	List<String> ssoErrorsList = new ArrayList<String>();

	private SyncTrackerService syncTrackerService;

	private String loginName;

	private HttpSession session;

	private boolean isUpdateduser = false;
	
	private CountryStateService countryStateService;
	
	private BusinessUnitService businessUnitService;
	
	private final String BUSINESS_UNIT_AMER="AMER";

	public String createOrUpdateUser() throws Exception {
		try {
			session = request.getSession();
			request.getRequestURI();
			populateDTOFromRequest();
			securityHelper.populateSystemUser();
			decryptUserId();
			user = orgService.findUserByName(loginName);
			if (user == null) {
				user = new User();
				isUpdateduser = true;
			}
			preValidateUserDetails();
			populateServiceProvider();
			popuateUserDTO();
			validateUserFields();
			populateUserRoles(Arrays.asList(userDTO.getRoleNames()));
			manageUser();
			isActiveUser();
		} catch (TWMSException e) {
			createSyncTracker();
			session.setAttribute(INTEGRATION_ERRORS, ssoErrorsList);
			return INPUT;
		}
		session.setAttribute(AdminConstants.AESENCRYPTION, AdminConstants.YES);
		actionUrl = "/authenticateUser?user=" + userDTO.getUserId();
		if(isFleetUser(user)){
			//TODO: redirect to Fleet
			
			session.setAttribute("userId", userDTO.getUserId());
			session.setAttribute("slmsCode", userDTO.getSlmsCode());
			return "fleetSuccess";
		}
		return SUCCESS;
	}
	
	private boolean isFleetUser(User user){
		for(Role role : user.getRoles()){
			if(role.getRoleCategory().equals(RoleCategory.WARRANTY)){
				return false;
			}
		}
		return true;
	}

	private void populateDTOFromRequest() throws Exception {
		userDTO = new UserDTO();
		BeanUtils.populate(userDTO, request.getParameterMap());
		
	}

	private void manageUser() {
		if (isUpdateduser) {
			orgService.createUser(user);
		} else {
			orgService.updateUser(user);
		}
	}

	private void populateServiceProvider() {
		Iterator<String> deLarIterator = Arrays
				.asList(userDTO.getDealerCodes()).iterator();
		List<Organization> belongsToOrganizations = new ArrayList<Organization>();
		TreeSet<BusinessUnit> businessUnitTreeSet = new TreeSet<BusinessUnit>();
		String businessUnit = getBusinessUnit();
		if (businessUnit == null||StringUtils.isEmpty(businessUnit)) {
			ssoErrorsList.add("error.manageDealerUsers.requiredBusinessUnit");
		} else {
			BusinessUnit businessUnitInfo = null;
			businessUnitInfo = businessUnitService
						.findBusinessUnit(businessUnit);
			businessUnitTreeSet.add(businessUnitInfo);
			user.setBusinessUnits(businessUnitTreeSet);
		}
		while (deLarIterator.hasNext() && businessUnit != null&&!StringUtils.isEmpty(businessUnit)) {
			String dealerCode = deLarIterator.next().trim();
			List<BigDecimal> serviceProviderIds = orgService.findServiceProviderIds(
					dealerCode, businessUnit);
			if (serviceProviderIds != null && !serviceProviderIds.isEmpty()) {
				for (BigDecimal id : serviceProviderIds) {
					ServiceProvider serviceProvider = orgService
							.findServiceProviderById(id.longValue());
					if (serviceProvider == null) {
						ssoErrorsList.add("error.dealerCode.notExists");
						throw new TWMSException(
								"Dealer Codes not Exist in SLMS");
					}
					belongsToOrganizations.add(serviceProvider);
				}
			}else{
				ssoErrorsList.add("error.dealerCode.notExists");
				throw new TWMSException(
						"Dealer Codes not Exist in SLMS");	
			}
		}
		
		user.setBelongsToOrganizations(belongsToOrganizations);
	}

	public void preValidateUserDetails() throws Exception {
		if (userDTO.getDealerCodes() == null) {
			ssoErrorsList.add("error.dealerCode.notExists");
		}
		if (StringUtils.isBlank(userDTO.getLocale())) {
			ssoErrorsList.add("error.manageDealerUsers.requiredLocale");
		}
		if (StringUtils.isBlank(userDTO.getStatus())) {

			ssoErrorsList.add("error.manageDealerUsers.status.required");

		} else if (isInValidStatus()) {
			ssoErrorsList.add("error.manageDealerUsers.status.inValid");

		}
		if (ssoErrorsList.size() > 0) {
			throw new TWMSException("Mandatory Fields Missing or Not Proper");
		}
	}

	private void popuateUserDTO() throws Exception {
		String ssoUrlLoginName = loginName; // Login name sent in the SSO Login URL
		loginName = loginName.toLowerCase(); // Convert to lower case as some application parts depends on loginName being in lower case
		
		user.setName(loginName);
		user.setFirstName(userDTO.getFirstName());
		user.setLastName(userDTO.getLastName());
		user.setLocale(new Locale(userDTO.getLocale()));
		user.setEmail(userDTO.getEmail());
		user.setJobTitle(userDTO.getJobTitle());
		user.setUserType(AdminConstants.DEALER_USER);
		// tavant as password default setting
		user.setPassword("13f17f39a6c61ac56522e9014e9ecd650724d9e6");
		if (userDTO.getStatus().equalsIgnoreCase(AdminConstants.INACTIVE)) {
			user.getD().setActive(false);
		} else {
			user.getD().setActive(true);
		}
		address = user.getAddress();
		if (address == null)
			address = new Address();
		address.setAddressLine1(userDTO.getAddressLine1());
		address.setAddressLine2(userDTO.getAddressLine2());
		address.setEmail(userDTO.getEmail());
		address.setPhone(userDTO.getPhone());
		address.setCity(userDTO.getCity());
		if(user.getBusinessUnits().first().getName().equals(BUSINESS_UNIT_AMER)){
			if (StringUtils.isNotBlank(userDTO.getCountry())) {
			Country country = countryStateService.fetchCountryCodeByName(userDTO.getCountry().trim());
			if(country != null){
				address.setCountry(country.getCode());
				if (StringUtils.isNotBlank(userDTO.getState())) {
				CountryState countryState = countryStateService.fetchStateCodeByName(userDTO.getState().trim(), country.getCode());
				if(countryState != null)
					address.setState(countryState.getStateCode());
				else
					ssoErrorsList.add("error.manageDealerUsers.state.notproper");
				}
			}
			else
				ssoErrorsList.add("error.manageDealerUsers.country.notproper");
			}
		}else{
			if (StringUtils.isNotBlank(userDTO.getCountry())) {
				Country country = countryStateService.fetchCountryCodeByName(userDTO.getCountry().trim());
				if(country != null)
					address.setCountry(country.getCode());
				else
					ssoErrorsList.add("error.manageDealerUsers.country.notproper");
			}
			address.setState(userDTO.getState());
		}
		address.setZipCode(userDTO.getZipCode());
		user.setAddress(address);

		if( logger.isInfoEnabled())
		{
			logger.info("While Populating UserDTO : SSO Login Name:" + ssoUrlLoginName + " & converted login name:" + loginName);
            logger.info("While Populating UserDTO : " + userDTO.toString());
            
	}

	}

	private void decryptUserId() {
		loginName =userDTO.getUserId();
		if(StringUtils.isBlank(loginName)){
			ssoErrorsList.add("error.manageDealerUsers.login.required");
			throw new TWMSException("user name required");
		}else if (loginName
				.equalsIgnoreCase("error.manageDealerUsers.authToken.notMatch")) {
			ssoErrorsList.add("error.manageDealerUsers.authToken.notMatch");
			throw new TWMSException("Exception while decrypting userid");
			
		}
		loginName = aesBasedUsernameDecrypter.decrypt(userDTO.getUserId());
		
	}

	private void populateUserRoles(List<String> roles) throws Exception {
		Set<Role> userRoles = new HashSet<Role>();
		Iterator<String> rolesIterator = roles.iterator();
		while (rolesIterator.hasNext()) {
			String roleName = (String) rolesIterator.next().trim();
			Role role = this.orgService.findRoleByName(roleName);
			if (role == null) {
				ssoErrorsList.add("error.role.notExists");
				throw new TWMSException("Role name not exist in SLMS");

			}
			userRoles.add(role);
			if (Role.DEALER_WARRANTY_ADMIN.equals(role.getName())) {
				userRoles.add(orgService.findRoleByName(Role.DEALER));
				userRoles.add(orgService.findRoleByName(Role.BASE_ROLE));
			}
			if (Role.DEALER_SALES_ADMIN.equals(role.getName())) {
				userRoles.add(orgService.findRoleByName(Role.INVENTORY_SEARCH));
				userRoles
						.add(orgService.findRoleByName(Role.INVENTORY_LISTING));
			}
		}
		if(!user.getRoles().isEmpty() && user.getRoles().contains(orgService.findRoleByName(Role.TECHNICIAN))){
			userRoles.add(orgService.findRoleByName(Role.TECHNICIAN));
		}
		user.setRoles(userRoles);
	}

	private void createSyncTracker() {
		if(userDTO.toString()==null){
			return;
		}
		SyncTracker syncTracker = new SyncTracker(AdminConstants.SSO,
				userDTO.toString());
		syncTracker.setErrorMessage(ssoErrorsList.toString());
		syncTrackerService.save(syncTracker);

	}

	private void isActiveUser() {
		if (userDTO.getStatus().equalsIgnoreCase(AdminConstants.INACTIVE)) {
			ssoErrorsList.add("error.manageDealerUsers.inActive");
			throw new TWMSException("Inactive User");

		}
	}

	private boolean isAuthTokenNotMatching() throws Exception {
		String slmsToken = aesBasedUsernameDecrypter.decrypt(userDTO
				.getSlmsCode());
		String serverGeneratedToken = "B096%5kr-c7x-H24d-FY"
				+ TWMSDateFormatUtil.getDayNumberInYear();
		if (slmsToken.equals(serverGeneratedToken)) {
			return false;
		}
		return true;

	}

	private void validateUserFields() throws Exception {
		if (StringUtils.isBlank(userDTO.getSlmsCode())) {
			ssoErrorsList.add("error.manageDealerUsers.slmscode.required");
		} else if (isAuthTokenNotMatching()) {
			ssoErrorsList.add("error.manageDealerUsers.authToken.notMatch");
			throw new TWMSException("not authenticated user");
		}
		if (StringUtils.isBlank(userDTO.getUserId())) {
			ssoErrorsList.add("error.manageDealerUsers.login.required");
		} else if (isNotValidUserId(userDTO.getUserId())) {
			ssoErrorsList.add("error.manageDealerUsers.invalidUser");
		} else if (userDTO.getUserId().length() > 255) {
			ssoErrorsList.add("error.manageDealerUsers.size");
		}

		if (StringUtils.isBlank(userDTO.getJobTitle())) {

			ssoErrorsList.add("error.manageDealerUsers.jobTitle.required");

		}
		if (StringUtils.isBlank(userDTO.getFirstName())) {
			ssoErrorsList.add("error.manageDealerUsers.firstName");

		}
		if (StringUtils.isBlank(userDTO.getLastName())) {
			ssoErrorsList.add("error.manageDealerUsers.lastName");

		}

		if (StringUtils.isBlank(userDTO.getEmail())) {
			ssoErrorsList.add("error.manageDealerUsers.requiredEmail");

		} else if (isNotValidEmail(userDTO.getEmail().trim())) {

			ssoErrorsList.add("error.manageDealerUsers.email");

		}
		if (StringUtils.isBlank(userDTO.getAddressLine1())) {
			ssoErrorsList.add("error.manageDealerUsers.requiredAddress");

		}
		if (StringUtils.isBlank(userDTO.getCountry())) {
			ssoErrorsList.add("error.manageDealerUsers.requiredCountry");

		}
		if (StringUtils.isBlank(userDTO.getDealerName())) {
			ssoErrorsList.add("error.manageDealerUsers.dealername.required");

		}
		if (StringUtils.isBlank(userDTO.getPhone())) {
			ssoErrorsList.add("error.manageDealerUsers.requiredPhone");

		} /*else if (TWMSCommonUtil.isNotNumber(userDTO.getPhone())) {
			ssoErrorsList.add("error.manageCustomer.invalidPhoneFax");
		}*/
		if (userDTO.getRoleNames() == null) {
			ssoErrorsList.add("error.role.notExists");
		}

		if (ssoErrorsList.size() > 0) {

			throw new TWMSException("Mandatory Fields Missing or Not Proper");
		}

	}

	private boolean isNotValidEmail(final String email) {
		Pattern pattern = Pattern
				.compile("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
						+ "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");
		return !pattern.matcher(email).matches();
	}

	private boolean isNotValidUserId(String userId) {
		if (loginName.substring(1, 2).equalsIgnoreCase("e")
				|| loginName.substring(1, 2).equalsIgnoreCase("a")
				|| loginName.substring(1, 2).equalsIgnoreCase("b")
				|| loginName.substring(1, 2).equalsIgnoreCase("c")) {
			return false;
		}
		return true;
	}

	public String getBusinessUnit() {
		if (loginName.length() >= 2) {
			if (loginName.substring(1, 2).equalsIgnoreCase("e")) {
				return AdminConstants.NMHGEMEA;
			} else if (loginName.substring(1, 2).equalsIgnoreCase("a")
					|| loginName.substring(1, 2).equalsIgnoreCase("b")
					|| loginName.substring(1, 2).equalsIgnoreCase("c")) {
				return AdminConstants.NMHGAMERICA;
			} else {
				return "";
			}
		}
		return "";

	}

	public boolean isInValidStatus() {
		if (userDTO.getStatus().trim().equalsIgnoreCase(AdminConstants.ACTIVE)
				|| userDTO.getStatus().trim()
						.equalsIgnoreCase(AdminConstants.INACTIVE)) {
			return false;
		}
		return true;

	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public ServiceProvider getServiceProvider() {
		return serviceProvider;
	}

	public void setServiceProvider(ServiceProvider serviceProvider) {
		this.serviceProvider = serviceProvider;
	}

	public OrgService getOrgService() {
		return orgService;
	}

	public void setOrgService(OrgService orgService) {
		this.orgService = orgService;
	}

	public UsernameDecrypter getAesBasedUsernameDecrypter() {
		return aesBasedUsernameDecrypter;
	}

	public void setAesBasedUsernameDecrypter(
			UsernameDecrypter aesBasedUsernameDecrypter) {
		this.aesBasedUsernameDecrypter = aesBasedUsernameDecrypter;
	}

	public String getActionUrl() {
		return actionUrl;
	}

	public SyncTrackerService getSyncTrackerService() {
		return syncTrackerService;
	}

	public void setSyncTrackerService(SyncTrackerService syncTrackerService) {
		this.syncTrackerService = syncTrackerService;
	}

	public SecurityHelper getSecurityHelper() {
		return securityHelper;
	}

	public void setSecurityHelper(SecurityHelper securityHelper) {
		this.securityHelper = securityHelper;
	}

	public ServletContext getServletContext() {
		return servletContext;
	}

	public void setServletContext(ServletContext servletContext) {
		this.servletContext = servletContext;
	}

	public CountryStateService getCountryStateService() {
		return countryStateService;
	}

	public void setCountryStateService(CountryStateService countryStateService) {
		this.countryStateService = countryStateService;
	}

	public BusinessUnitService getBusinessUnitService() {
		return businessUnitService;
	}

	public void setBusinessUnitService(BusinessUnitService businessUnitService) {
		this.businessUnitService = businessUnitService;
	}
	

}

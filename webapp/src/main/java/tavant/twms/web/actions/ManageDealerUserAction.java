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

import static tavant.twms.web.inbox.SummaryTableColumn.IMAGE;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.acegisecurity.providers.encoding.PasswordEncoder;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.classic.Validatable;
import org.json.JSONArray;
import org.json.JSONException;
import org.springframework.util.CollectionUtils;

import tavant.twms.dateutil.TWMSStringUtil;
import tavant.twms.domain.bu.BusinessUnit;
import tavant.twms.domain.catalog.CatalogException;
import tavant.twms.domain.catalog.CatalogService;
import tavant.twms.domain.catalog.Item;
import tavant.twms.domain.catalog.ItemGroup;
import tavant.twms.domain.orgmodel.CertificateService;
import tavant.twms.domain.claim.ClaimService;
import tavant.twms.domain.claim.Criteria;
import tavant.twms.domain.claim.PartsClaim;
import tavant.twms.domain.orgmodel.SeriesRefCertification;
import tavant.twms.domain.orgmodel.SeriesRefCertificationRepository;
import tavant.twms.domain.common.AdminConstants;
import tavant.twms.domain.configuration.ConfigParamService;
import tavant.twms.domain.configuration.ConfigValue;
import tavant.twms.domain.orgmodel.Address;
import tavant.twms.domain.orgmodel.Attribute;
import tavant.twms.domain.orgmodel.AttributeConstants;
import tavant.twms.domain.orgmodel.AttributeService;
import tavant.twms.domain.orgmodel.CountyCodeMapping;
import tavant.twms.domain.orgmodel.Dealership;
import tavant.twms.domain.orgmodel.SeriesCertification;
import tavant.twms.domain.orgmodel.CoreCertification;
import tavant.twms.domain.orgmodel.Country;
import tavant.twms.domain.orgmodel.MSAService;
import tavant.twms.domain.orgmodel.Organization;
import tavant.twms.domain.orgmodel.Role;
import tavant.twms.domain.orgmodel.ServiceProvider;
import tavant.twms.domain.orgmodel.Technician;
import tavant.twms.domain.orgmodel.TechnicianCertification;
import tavant.twms.domain.orgmodel.TechnicianCertificationService;
import tavant.twms.domain.orgmodel.TechnicianDetails;
import tavant.twms.domain.orgmodel.User;
import tavant.twms.domain.orgmodel.UserAttributeValue;
import tavant.twms.domain.orgmodel.UserAttributeValueService;
import tavant.twms.infra.HibernateCast;
import tavant.twms.infra.InstanceOfUtil;
import tavant.twms.infra.ListCriteria;
import tavant.twms.infra.PageResult;
import tavant.twms.infra.PageSpecification;
import tavant.twms.infra.i18n.ProductLocale;
import tavant.twms.infra.i18n.ProductLocaleService;
import tavant.twms.security.SecurityHelper;
import tavant.twms.security.SelectedBusinessUnitsHolder;
import tavant.twms.web.i18n.I18nActionSupport;
import tavant.twms.web.inbox.SummaryTableAction;
import tavant.twms.web.inbox.SummaryTableColumn;
import tavant.twms.web.inbox.SummaryTableColumnOptions;
import tavant.twms.web.security.authn.provider.TWMSPasswordEncoder;
import tavant.twms.domain.orgmodel.TechnicianCertification;

import com.opensymphony.xwork2.Preparable;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

@SuppressWarnings("serial")
public class ManageDealerUserAction extends I18nActionSupport implements
		Preparable, Validatable {

	private static Logger logger = Logger
			.getLogger(ManageDealerUserAction.class);

	private static final int MAX_LIMIT_FOR_SEARCH_RESULT = 100;

	private ServiceProvider serviceProvider;

	private ClaimService claimService;

	private ConfigParamService configParamService;

	private String selectedBusinessUnit;

	private User user;

	private Technician technician;

	private ItemGroup itemGroup;

	private List<User> dealerUsers;

	private TechnicianCertificationService technicianCertificationService;

	private SeriesRefCertificationRepository seriesRefCertificationRepository;

	private List<TechnicianCertification> certificationList = new ArrayList<TechnicianCertification>();

	private List<TechnicianCertification> selectedCertificationList = new ArrayList<TechnicianCertification>();

	private List<ServiceProvider> dealersForUser = new ArrayList<ServiceProvider>();

	private TechnicianCertification techCertification;

	private CatalogService catalogService;

	private static JSONArray EMPTY_SERIES_DESCRIPTION;

	private String description;
	private CertificateService certificateService;

	public List<User> getDealerUsers() {
		return dealerUsers;
	}

	public boolean isPageReadOnlyAdditional() {
		boolean isReadOnlyDealer = false;
		Set<Role> roles = getLoggedInUser().getRoles();
		for (Role role : roles) {
			if (role.getName().equalsIgnoreCase(Role.READ_ONLY_DEALER)) {
				isReadOnlyDealer = true;
				break;
			}
		}
		return isReadOnlyDealer;
	}

	public boolean isPageReadOnly() {
		return false;
	}

	public void setDealerUsers(List<User> dealerUsers) {
		this.dealerUsers = dealerUsers;
	}

	private String dealerUserId;

	public String getDealerUserId() {
		return dealerUserId;
	}

	public void setDealerUserId(String dealerUserId) {
		this.dealerUserId = dealerUserId;
	}

	private String confirmPassword;

	private MSAService msaService;

	private final SortedHashMap<String, String> countryList = new SortedHashMap<String, String>();

	private List<String> countriesFromMSA = new ArrayList<String>();

	private String countryCode;

	private String stateCode;

	private String cityCode;

	private String zipsCode;

	private String countyCode;
	
	private String countyCodeWithName;

	private String defaultLocale;

	private List<Role> roleToBeAssigned = new ArrayList<Role>();

	private List<Role> rolesToBeDisplayed = new ArrayList<Role>();

	private List<Role> dealerRoles = new ArrayList<Role>();

	private boolean userCreated;

	private String loginName;

	private List<ProductLocale> listOfLocale;

	private ProductLocaleService productLocaleService;

	private String userPassword;

	private Technician technicianDetails;

	private UserAttributeValueService userAttributeValueService;

	private AttributeService attributeService;

	private SeriesRefCertification seriesAndCertifications;

	private Set<ServiceProvider> companiesWorkingFor = new HashSet<ServiceProvider>();

	public SeriesRefCertification getSeriesAndCertifications() {
		return seriesAndCertifications;
	}

	public void setSeriesAndCertifications(
			SeriesRefCertification seriesAndCertifications) {
		this.seriesAndCertifications = seriesAndCertifications;
	}

	public AttributeService getAttributeService() {
		return attributeService;
	}

	public void setAttributeService(AttributeService attributeService) {
		this.attributeService = attributeService;
	}

	public UserAttributeValueService getUserAttributeValueService() {
		return userAttributeValueService;
	}

	public void setUserAttributeValueService(
			UserAttributeValueService userAttributeValueService) {
		this.userAttributeValueService = userAttributeValueService;
	}

	public void setTechnician(Technician technician) {
		this.technician = technician;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.opensymphony.xwork2.Preparable#prepare()
	 */
	public void prepare() {
		List<Country> countries = msaService.getCountryList();
		for (Country country : countries) {
			countryList.put(country.getCode(), country.getName());
		}
		countriesFromMSA = msaService.getCountriesFromMSA();

		defaultLocale = getLoggedInUser().getLocale().toString();

		createRolesToBeDisplayed();

		user = new User();
		user.setAddress(new Address());
		if (!isLoggedInUserAnInternalUser()
				&& getLoggedInUsersDealership() != null) {
			serviceProvider = orgService
					.findDealerById(getLoggedInUsersDealership().getId());
			user.getAddress().setCountry(
					serviceProvider.getAddress().getCountry());
		}
		listOfLocale = productLocaleService.findAll();
		resetDealerRoles();
		Collections.sort(dealerRoles);
		// Add to retain User role in select box after getting validation
		// Error(Fix for 1411

		for (Role role : roleToBeAssigned) {
			rolesToBeDisplayed.add(role);
		}
		companiesWorkingFor.addAll(dealersForUser);
		if (isAssignedTechnicianRole()) {
		if(technicianDetails.getTechnicianCertifications()!=null){
			certificationList.addAll(technicianDetails.getTechnicianCertifications());
			}
		}
		if (serviceProvider == null && dealersForUser != null
				&& !dealersForUser.isEmpty()
				&& dealersForUser.get(0).getId() != null) {
			setServiceProvider(dealersForUser.get(0));
		}

	}

	private void createRolesToBeDisplayed() {

		this.selectedBusinessUnit = new SecurityHelper()
				.getWarrantyAdminBusinessUnit();
		if (this.selectedBusinessUnit == null) {
			selectedBusinessUnit = getLoggedInUser().getBusinessUnits().first()
					.getName();
		}
		List<ConfigValue> configValues = configParamService
				.getConfiguredRolesToBeDisplayed(selectedBusinessUnit);
		dealerRoles.clear();
		for (ConfigValue configValue : configValues) {
			Role role = new Role();
			role.setName(configValue.getConfigParamOption().getValue());
			role.setDisplayName(configValue.getConfigParamOption()
					.getDisplayValue());
			dealerRoles.add(role);
		}
	}

	public String forwardToCreateUser() {
		if (serviceProvider != null) {
			countryCode = serviceProvider.getAddress().getCountry();
			if (!countriesFromMSA.contains(serviceProvider.getAddress()
					.getCountry())) {
				stateCode = serviceProvider.getAddress().getState();
				cityCode = serviceProvider.getAddress().getCity();
				zipsCode = serviceProvider.getAddress().getZipCode();
				countyCode = serviceProvider.getAddress().getCounty();
			} else {
				user.getAddress().setState(
						serviceProvider.getAddress().getState());
				user.getAddress().setCity(
						serviceProvider.getAddress().getCity());
				user.getAddress().setZipCode(
						serviceProvider.getAddress().getZipCode());
				user.getAddress().setCounty(
						serviceProvider.getAddress().getCounty());
			}
		}
		return SUCCESS;

	}

	@Override
	public void validate() {
		if (this.user.getId() == null) {
			if (orgService.findUserByName(this.user.getName()) != null) {
				addActionError("error.manageDealerUsers.login.exists");
			}
		}

		if (roleToBeAssigned == null || roleToBeAssigned.isEmpty()) {
			addActionError("error.manageDealerUsers.assign.role");
		}

		if (StringUtils.isNotBlank(user.getPassword())
				&& user.getPassword().trim().length() < 6) {
			addActionError("error.manageDealerUsers.password.minimum");
		}

		if (isLoggedInUserAnInternalUser() && (serviceProvider == null || (dealersForUser!=null && dealersForUser.isEmpty()))) {
			if(isAssignedTechnicianRole())
				addActionError("error.technician.addAtleastOne");
			else
			addActionError("error.manageDealerUsers.serviceProvider.required");
		}
	}

	public String search() {

		if (StringUtils.isBlank(loginName)) {
			addActionError("error.manageDealerUsers.login.required");
			return INPUT;
		}
		List<String> roleName = new ArrayList<String>();
		for (Role role : dealerRoles) {
			roleName.add(role.getName());
		}

		dealerUsers = orgService.findDealerUsers(serviceProvider, roleName,
				loginName.trim());
		if (dealerUsers == null || dealerUsers.isEmpty()) {
			addActionError("invalid.resource",
					new String[] { getText("label.loginUser") });
			return INPUT;
		}

		if (dealerUsers.size() > MAX_LIMIT_FOR_SEARCH_RESULT) {
			addActionError("error.manageDealerUsers.tooManyRecords",
					new String[] { "" + MAX_LIMIT_FOR_SEARCH_RESULT });
			dealerUsers.clear();
			return INPUT;
		}

		if (isLoggedInUserAnInternalUser()) {
			Iterator<User> it = dealerUsers.iterator();
			while (it.hasNext()) {
				if (it.next().isInternalUser())
					it.remove();
			}
		}

		if (dealerUsers.size() == 1) {
			user = dealerUsers.get(0);
			if (!countriesFromMSA.contains(user.getAddress().getCountry())) {
				stateCode = user.getAddress().getState();
				cityCode = user.getAddress().getCity();
				zipsCode = user.getAddress().getZipCode();
				countyCode = user.getAddress().getCounty();
			}
			roleToBeAssigned.addAll(user.getRoles());
			rolesToBeDisplayed.clear();
			for (Role role : user.getRoles()) {
				if (!role.getName().equals("inventorylisting")
						&& !role.getName().equals("baserole")
						&& !role.getName().equals("dealer")
						&& !role.getName().equals("inventorysearch")) {
					rolesToBeDisplayed.add(role);
				}
			}
			UserAttributeValue attr = fetchLatestAddedUserAttributes();
			// this.certificationList =
			// technicianCertificationService.getCertificationForTechnician(attr);
			Collections.sort(roleToBeAssigned);
			resetDealerRoles();
			Collections.sort(dealerRoles);
			if (isTechnician())
				fetchTechnicianDetails();
			defaultLocale = user.getLocale() != null ? user.getLocale()
					.toString() : null;
			if(!user.getBelongsToOrganizations().isEmpty()){
			for(Organization orgs : user.getBelongsToOrganizations()){
				if (InstanceOfUtil.isInstanceOfClass(ServiceProvider.class, orgs)){
					serviceProvider =(ServiceProvider) orgs;
					companiesWorkingFor.add(serviceProvider);
				}
					}
			}
			return "exactMatch";
		}
		return SUCCESS;
	}

	public String forwardToUpdateDealerUser() {
		user = orgService.findUserById(new Long(dealerUserId));
		if (!countriesFromMSA.contains(user.getAddress().getCountry())) {
			stateCode = user.getAddress().getState();
			cityCode = user.getAddress().getCity();
			zipsCode = user.getAddress().getZipCode();
			countyCode = user.getAddress().getCounty();
		}
		roleToBeAssigned.addAll(user.getRoles());
		resetDealerRoles();
		for (Role role : user.getRoles()) {
			if (!role.getName().equals("inventorylisting")
					&& !role.getName().equals("baserole")
					&& !role.getName().equals("dealer")
					&& !role.getName().equals("inventorysearch")) {
				rolesToBeDisplayed.add(role);
			}
		}
		if (isTechnician()) {
			fetchTechnicianDetails();

		}
		defaultLocale = user.getLocale() != null ? user.getLocale().toString()
				: null;
		if(!user.getBelongsToOrganizations().isEmpty()){
			for(Organization orgs : user.getBelongsToOrganizations()){
				if (InstanceOfUtil.isInstanceOfClass(ServiceProvider.class, orgs)){
					serviceProvider =(ServiceProvider) orgs;
					companiesWorkingFor.add(serviceProvider);
				}
					}
			}
		return SUCCESS;
	}

	public String create() throws Exception {
		validationForCreate();
		if (hasActionErrors()) {
			return INPUT;
		}
		if(!isAssignedTechnicianRole()){
		user.setLocale(new Locale(this.defaultLocale));
		Set<Role> userRoles = new HashSet<Role>();
		for (Role role : roleToBeAssigned) {
			userRoles.add(role);
			// rolesToBeDisplayed.add(role);

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
		user.setRoles(userRoles);

		// update UserType
		updateUserTypeForUser();

			TreeSet<BusinessUnit> businessUnitTreeSet = new TreeSet<BusinessUnit>();
			businessUnitTreeSet.addAll(serviceProvider.getBusinessUnits());
			user.setBusinessUnits(businessUnitTreeSet);
			user.setBelongsToOrganizations(new ArrayList<Organization>());
			user.getBelongsToOrganizations().addAll(dealersForUser);
			Address address = user.getAddress();
		if (!countriesFromMSA.contains(user.getAddress().getCountry())) {
			user.getAddress().setState(stateCode);
			user.getAddress().setCity(cityCode);
			user.getAddress().setZipCode(zipsCode);
			user.getAddress().setCounty(countyCode);
			user.getAddress().setCountyCodeWithName(countyCodeWithName);
		}

		address.setEmail(this.user.getEmail());
		address.setBelongsTo(serviceProvider);
		this.user.setAddress(address);
		PasswordEncoder passowrdEncoder = new TWMSPasswordEncoder();
		if (StringUtils.isNotBlank(user.getPassword())) {
			user.setPassword(passowrdEncoder.encodePassword(user.getPassword(),
					user.getSalt())
					+ "|"
					+ TWMSStringUtil.bytesToHexString(user.getSalt()));
		}
		this.orgService.createUser(user);
	}
		if (isAssignedTechnicianRole()) {
			Technician techCreation = new Technician();
			addTechnicianDetails(techCreation);
			this.orgService.createUser(user);
			techCreation.setOrgUser(user);
			this.orgService.createTechnician(techCreation);
			createTechnicianCertifications(techCreation);
		}
		userCreated = true;
		return SUCCESS;
	}

	private void validateCertifications(TechnicianCertification techCert) {
		List<SeriesCertification> seriesCertificates = this.certificateService
				.findByCertificateNameForPR(techCert.getCertificationName()
						.toUpperCase(), techCert.getBrand().toUpperCase());
		if (seriesCertificates==null || seriesCertificates.isEmpty()) {
			CoreCertification coreCertificate = this.certificateService
					.findByCertificateNameForCO(techCert.getCertificationName()
							.toUpperCase(), techCert.getBrand().toUpperCase());
			if (coreCertificate == null) {
				addActionError("error.certificate.noCertificateFound",techCert.getCertificationName());
			} 
		}
	}

	private void addTechnicianDetails(Technician techCreation) {
	
		user.setLocale(new Locale(this.defaultLocale));
		Set<Role> userRoles = new HashSet<Role>();
		for (Role role : roleToBeAssigned) {
			userRoles.add(role);
			// rolesToBeDisplayed.add(role);

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
		user.setRoles(userRoles);

		// update UserType
		if (isAssignedTechnicianRole()) {
			user.setUserType(AdminConstants.EXTERNAL);
		} 
		// createOrUpdateTechnicianDetails();
		techCreation.setEmailId(technicianDetails.getEmailId());
		techCreation.setServiceManagerName(technicianDetails.getServiceManagerName());
		techCreation.setComments(technicianDetails.getComments());

		if (isAssignedTechnicianRole()) {
			if(!dealersForUser.isEmpty()){
				for(ServiceProvider srcPrdr : dealersForUser)
					if(srcPrdr!= null){
						setServiceProvider(srcPrdr);
						break;
					}
			}
			TreeSet<BusinessUnit> businessUnitTreeSet = new TreeSet<BusinessUnit>();
			businessUnitTreeSet.addAll(serviceProvider.getBusinessUnits());
			user.setBusinessUnits(businessUnitTreeSet);
			user.setBelongsToOrganizations(new ArrayList<Organization>());
			user.getBelongsToOrganizations().add(serviceProvider);
			user.setBelongsToOrganizations(new ArrayList<Organization>());
			user.getBelongsToOrganizations().add(serviceProvider);
		}
		Address address = user.getAddress();
		address.setEmail(user.getEmail());
		address.setBelongsTo(serviceProvider);
		if (!countriesFromMSA.contains(user.getAddress().getCountry())) {
			user.getAddress().setState(stateCode);
			user.getAddress().setCity(cityCode);
			user.getAddress().setZipCode(zipsCode);
			user.getAddress().setCounty(countyCode);
		}
		PasswordEncoder passowrdEncoder = new TWMSPasswordEncoder();
		if (StringUtils.isNotBlank(userPassword)) {
			user.setPassword(passowrdEncoder.encodePassword(userPassword,
					user.getSalt())
					+ "|"
					+ TWMSStringUtil.bytesToHexString(user.getSalt()));
		}else{
			user.setPassword(user.getPassword()+ "|" + TWMSStringUtil.bytesToHexString(user.getSalt()));
		}
		updateCompaniesWorkingFor(dealersForUser);
	}

	private void createTechnicianCertifications(Technician technicianCreated) {
		try{
		if (technicianDetails.getTechnicianCertifications() != null) {
			for (TechnicianCertification techCert : technicianDetails
					.getTechnicianCertifications()) {
				if (techCert != null) {
					addCertificateDetails(techCert);
					if(!hasActionErrors()){
						techCert.setTechUser(technicianCreated);
						technicianCertificationService.save(techCert);	
					}
				}
			}
		}
		}
		catch(Exception exception){
			logger.error("Failed to create technician certifications", exception);
		}

	}

	private Technician createTechnicianDetails(User techinicianCreation) {
		Technician newTech = new Technician();
		if (technicianDetails.getServiceManagerName() != null
				&& org.springframework.util.StringUtils
						.hasText(technicianDetails.getServiceManagerName())) {
			newTech.setServiceManagerName(technicianDetails
					.getServiceManagerName());
		}
		if (technicianDetails.getComments() != null
				&& org.springframework.util.StringUtils
						.hasText(technicianDetails.getComments())) {
			newTech.setComments(technicianDetails.getComments());
		}
		return newTech;
	}

	private void validateTechnicianCertifications() {
		if (technicianDetails.getTechnicianCertifications() != null
				&& !technicianDetails.getTechnicianCertifications().isEmpty()) {
			Iterator<TechnicianCertification> ite = technicianDetails
					.getTechnicianCertifications().iterator();
			TechnicianCertification technicianCertification = new TechnicianCertification();
			while (ite.hasNext()) {
				technicianCertification = ite.next();
				if (technicianCertification != null) {
					if (StringUtils.isBlank(technicianCertification.getBrand()))
						addActionError("error.technicianCertification.brand.required");
					if (technicianCertification.getCertificationFromDate() == null)
						addActionError("error.technicianCertification.certificationAcheievedDate.required");
					if (technicianCertification.getCertificationToDate() == null)
						addActionError("error.technicianCertification.certificationExpirationDate.required");
					if (StringUtils.isBlank(technicianCertification
							.getCertificationName()))
						addActionError("error.technicianCertification.certificationName.required");
					if (technicianCertification.getCertificationFromDate() != null
							&& technicianCertification.getCertificationToDate() != null
							&& technicianCertification
									.getCertificationFromDate().isAfter(
											technicianCertification
													.getCertificationToDate()))
						addActionError("error.technicianCertification.certificationFromDate.AfterToDate");
					if(org.springframework.util.StringUtils.hasText(technicianCertification.getCertificationName())){
						validateCertifications(technicianCertification);					
					}
				}
				if(hasActionErrors()){
					break;
				}
			}
		}
	}

	private UserAttributeValue fetchLatestAddedUserAttributes() {
		if (user == null || user.getUserAttrVals() == null)
			return null;
		try {
			for (UserAttributeValue attr : user.getUserAttrVals()) {
				if (AttributeConstants.TECHNICIAN_DETAILS.equals(attr
						.getAttribute().getName())) {
					return attr;
				}
			}
		} catch (Exception exception) {
			logger.error("Failed to fetch technician details", exception);
		}
		return null;
	}

	public String update() throws Exception {
		validationForUpdate();
		if (hasActionErrors()) {
			return INPUT;
		}
		user.setLocale(new Locale(this.defaultLocale));
		PasswordEncoder passowrdEncoder = new TWMSPasswordEncoder();
		if (StringUtils.isNotBlank(userPassword)) {
			user.setPassword(passowrdEncoder.encodePassword(userPassword,
					user.getSalt())
					+ "|" + TWMSStringUtil.bytesToHexString(user.getSalt()));
		}else{
			user.setPassword(user.getPassword()+ "|" + TWMSStringUtil.bytesToHexString(user.getSalt()));
		}
		Set<Role> rolesToBeAdded = new HashSet<Role>();
		for (Role role : roleToBeAssigned) {
			rolesToBeAdded.add(role);
			// rolesToBeDisplayed.add(role);
			if (Role.DEALER_WARRANTY_ADMIN.equals(role.getName())) {
				rolesToBeAdded.add(orgService.findRoleByName(Role.DEALER));
				rolesToBeAdded.add(orgService.findRoleByName(Role.BASE_ROLE));
			}
			if (Role.DEALER_SALES_ADMIN.equals(role.getName())) {
				rolesToBeAdded.add(orgService
						.findRoleByName(Role.INVENTORY_SEARCH));
				rolesToBeAdded.add(orgService
						.findRoleByName(Role.INVENTORY_LISTING));
			}
		}
		user.setRoles(rolesToBeAdded);

		// update UserType
		updateUserTypeForUser();

		Address address = user.getAddress();
		if (!countriesFromMSA.contains(address.getCountry())) {
			address.setState(stateCode);
			address.setCity(cityCode);
			address.setZipCode(zipsCode);
			address.setCounty(countyCode);
		}

		// createOrUpdateTechnicianDetails();

		this.user.getAddress().setEmail(this.user.getEmail());
		if (isAssignedTechnicianRole()) {
			technician = orgService.findTechnicianByName(user.getName());
			if(technician == null){
				Technician techCreation = new Technician();
				addTechnicianDetails(techCreation);
				this.orgService.updateUser(user);
				techCreation.setOrgUser(user);
				this.orgService.createTechnician(techCreation);
				createTechnicianCertifications(techCreation);
			}else{
				addTechnicianDetails(technician);
				this.orgService.updateUser(user);
				technician.setOrgUser(user);
				updateCertifications();
				this.orgService.updateTechnician(technician);
			}
		}
		else{
			updateCompaniesWorkingFor(dealersForUser);
			orgService.updateUser(user);
		}
		userCreated = false;
		return SUCCESS;
	}

	private void updateCompaniesWorkingFor(List<ServiceProvider> dealers) {
		// TODO Auto-generated method stub
		List<Organization> orgList = new ArrayList<Organization>();
		if(null != user.getBelongsToOrganizations() && !user.getBelongsToOrganizations().isEmpty())
		user.getBelongsToOrganizations().clear();
		for (ServiceProvider dealer : dealers) {
			Organization dealerOrg = (Organization) dealer;
			orgList.add(dealerOrg);
		}
		user.setBelongsToOrganizations(orgList);
	}

	private TechnicianCertification addCertificateDetails(
			TechnicianCertification techCert) {
		List<SeriesCertification> seriesCertificates = this.certificateService
				.findByCertificateNameForPR(techCert.getCertificationName()
						.toUpperCase(), techCert.getBrand().toUpperCase());
		techCert.setSeriesCertification(new ArrayList<SeriesRefCertification>());
		if (seriesCertificates==null || seriesCertificates.isEmpty()) {
			CoreCertification coreCertificate = this.certificateService
					.findByCertificateNameForCO(techCert.getCertificationName()
							.toUpperCase(), techCert.getBrand().toUpperCase());
			if (coreCertificate != null) {
				techCert.setIsCoreLevel(Boolean.TRUE);
				techCert.setCoreCertification(coreCertificate);
				techCert.setTechUser(technician);
			}
		} else {
			techCert.setIsCoreLevel(Boolean.FALSE);
			for(SeriesCertification eachSeries:seriesCertificates){
				techCert.getSeriesCertification().add(eachSeries.getSeriesRefCert());
			}
			techCert.setTechUser(technician);
		}
		return techCert;
	}

	private void updateCertifications() throws Exception {

		
		try{
		if (technician != null) {
			Iterator<TechnicianCertification> ite = technician
					.getTechnicianCertifications().iterator();
			while (ite.hasNext()) {
				TechnicianCertification techCert = ite.next();
				techCert.getD().setActive(Boolean.FALSE);
			}
		}
		if (technicianDetails!=null && technicianDetails.getTechnicianCertifications() != null
				&& !technicianDetails.getTechnicianCertifications().isEmpty()) {
			for (TechnicianCertification techCert : technicianDetails
					.getTechnicianCertifications()) {
				if (techCert != null) {
					addCertificateDetails(techCert);
					technicianCertificationService.save(techCert);
				}
			}
		}
	}
		catch(Exception exception){
			logger.error("Failed to update technician certifications", exception);

		}

	}

	private void createOrUpdateTechnicianDetails() {
		Set<UserAttributeValue> userAttributes = user.getUserAttrVals();
		if (isAssignedTechnicianRole()) {
			UserAttributeValue attributeVal = null;
			XStream xstream = new XStream(new DomDriver());
			String xml = xstream.toXML(technicianDetails);
			if (userAttributes == null)
				userAttributes = new HashSet<UserAttributeValue>();
			else {
				for (UserAttributeValue attr : userAttributes) {
					if (AttributeConstants.TECHNICIAN_DETAILS
							.equalsIgnoreCase(attr.getAttribute().getName())) {
						attributeVal = attr;
						break;
					}
				}
			}
			if (attributeVal == null) {
				SelectedBusinessUnitsHolder
						.setSelectedBusinessUnit(getLoggedInUser()
								.getBusinessUnits().first().getName());
				Attribute attributeName = new Attribute(
						AttributeConstants.TECHNICIAN_DETAILS);
				attributeService.createAttribute(attributeName);
				attributeVal = new UserAttributeValue(attributeName, xml);
				userAttributeValueService
						.createUserAttributeValue(attributeVal);
			} else {
				attributeVal.setValue(xml);
				userAttributeValueService
						.updateUserAttributeValue(attributeVal);
			}
			userAttributes.add(attributeVal);
		} else if (userAttributes != null) {
			for (UserAttributeValue attr : userAttributes) {
				if (AttributeConstants.TECHNICIAN_DETAILS.equalsIgnoreCase(attr
						.getAttribute().getName())) {
					userAttributes.remove(attr);
					break;
				}
			}
		}
		user.setUserAttrVals(userAttributes);
	}

	private void fetchTechnicianDetails() {
		try {
			for (Organization orgs : user.getBelongsToOrganizations()) {
				companiesWorkingFor.add((ServiceProvider) orgs);
			}
			technician = orgService.findTechnicianByName(user.getName());
			technicianDetails = new Technician();
			technicianDetails.setEmailId(technician.getEmailId());
			technicianDetails.setServiceManagerName(technician.getServiceManagerName());
			technicianDetails.setComments(technician.getComments());
			for (TechnicianCertification eachCert : technician
					.getTechnicianCertifications()) {
				if (eachCert.getD().isActive()) {
					certificationList.add(eachCert);
					if (eachCert.getIsCoreLevel() && eachCert.getCoreCertification()!=null) {
						eachCert.setCategoryLevel(eachCert
								.getCoreCertification().getCategoryLevel());
						eachCert.setCategoryName(eachCert
								.getCoreCertification().getCategoryName());
					} else {
						List<SeriesCertification> certificate = this.certificateService
								.findByCertificateNameForPR(
										eachCert.getCertificationName(),
										eachCert.getBrand());
						if(!certificate.isEmpty()){
							eachCert.setCategoryLevel(AdminConstants.PRODUCT_CERTIFICATE_LEVEL);
							eachCert.setCategoryName(certificate.get(0).getCategoryName());
							for(SeriesCertification seriesCert:certificate){
								eachCert.getSeriesCertification().add(seriesCert.getSeriesRefCert());
							}
						}							
					}
				}
			}
		} catch (Exception exception) {
			logger.error("Failed to fetch technician details", exception);
		}
	}

	public boolean isTechnician() {
		for (Role role : user.getRoles()) {
			if (Role.TECHNICIAN.equalsIgnoreCase(role.getName()))
				return true;
		}
		return false;
	}

	public boolean isAssignedTechnicianRole() {
		for (Role role : roleToBeAssigned) {
			if (Role.TECHNICIAN.equalsIgnoreCase(role.getName()))
				return true;
		}
		return false;
	}

	public void updateUserTypeForUser() {
		if (isTechnician()) {
			user.setUserType(AdminConstants.EXTERNAL);
		} else {
			user.setUserType(AdminConstants.DEALER_USER);
		}
	}

	private void validationForUpdate() {
		
		if (dealersForUser.isEmpty()){
			addActionError("error.manageDealerUsers.serviceProvider.required");	
		}
		
		if(dealersForUser!=null){
			for(ServiceProvider dealer:dealersForUser){
				if (dealer!=null && dealer.getId()==null){
					addActionError("error.technician.dealerRequired");
					break;
				}
			}	
		}

		if (countriesFromMSA.contains(this.user.getAddress().getCountry())) {
			if (StringUtils.isBlank(user.getAddress().getState()))
				addActionError("error.manageDealerUsers.requiredStateCode");
			if (StringUtils.isBlank(user.getAddress().getCity()))
				addActionError("error.manageDealerUsers.requiredCityCode");
			if (StringUtils.isBlank(user.getAddress().getZipCode()))
				addActionError("error.manageDealerUsers.requiredzipCode");

		}
		if (StringUtils.isNotBlank(userPassword)
				&& !userPassword.equals(confirmPassword)) {
			addActionError("error.manageDealerUsers.differentPasswords");
		}

		if (!this.user.getD().isActive()) {
			List<String> tasks = this.claimService
					.findTasksAssingedToUser(this.user.getName());

			if (!tasks.isEmpty()) {
				Set<String> taskTobeDisplayed = new HashSet<String>(tasks);
				for (String task : taskTobeDisplayed) {
					addActionError("error.manageDealerUsers.userHasOpenTask",
							task);
				}

			}

		}

		resetDealerRoles();
		Collections.sort(dealerRoles);

		if (isAssignedTechnicianRole()) {
			if (dealersForUser == null || dealersForUser.isEmpty()) {
				addActionError("error.technician.addAtleastOne");
			}
			validateTechnicianCertifications();
		}
	}

	private void resetDealerRoles() {
		if (this.user != null && this.roleToBeAssigned.isEmpty()) {
			for (Role role : user.getRoles()) {
				Iterator<Role> iteratorRole = dealerRoles.iterator();
				while (iteratorRole.hasNext()) {
					if (iteratorRole.next().getDisplayName()
							.equals(role.getDisplayName())) {
						iteratorRole.remove();
					}
				}
			}
		} else {
			for (Role role : this.roleToBeAssigned) {
				Iterator<Role> iteratorRole = dealerRoles.iterator();
				while (iteratorRole.hasNext()) {
					if (iteratorRole.next().getDisplayName()
							.equals(role.getDisplayName())) {
						iteratorRole.remove();
					}
				}
			}

		}
	}

	private void validationForCreate() {

		if (!isAssignedTechnicianRole()
				&& StringUtils.isBlank(serviceProvider
						.getServiceProviderNumber())) {
			addActionError("error.invalidDealer");
		}

		if (countriesFromMSA.contains(this.user.getAddress().getCountry())) {
			if (StringUtils.isBlank(user.getAddress().getState()))
				addActionError("error.manageDealerUsers.requiredStateCode");
			if (StringUtils.isBlank(user.getAddress().getCity()))
				addActionError("error.manageDealerUsers.requiredCityCode");
			if (StringUtils.isBlank(user.getAddress().getZipCode()))
				addActionError("error.manageDealerUsers.requiredzipCode");

		}
		if (StringUtils.isBlank(user.getPassword())) {
			addActionError("error.manageDealerUsers.password.required");
		}
		if (StringUtils.isNotBlank(user.getPassword())
				&& !user.getPassword().equals(confirmPassword)) {
			addActionError("error.manageDealerUsers.differentPasswords");
		}
		resetDealerRoles();
		Collections.sort(dealerRoles);
		validateTechnicianCertifications();
	}

	public String getStatesByCountry() throws IOException {
		List<String> statesFromDB = this.msaService
				.getStatesByCountry(this.countryCode);
		return generateAndWriteComboboxJson(statesFromDB);
	}

	public String getCitiesByCountryAndState() throws IOException {
		List<String> citiesFromDB = this.msaService.getCitiesByCountryAndState(
				this.countryCode, this.stateCode);
		return generateAndWriteComboboxJson(citiesFromDB);
	}

	public String getZipsByCountryStateAndCity() throws IOException {
		List<String> zipsFromDB = this.msaService.getZipsByCountryStateAndCity(
				this.countryCode, this.stateCode, this.cityCode);
		return generateAndWriteComboboxJson(zipsFromDB);
	}

	public String getCountiesByCountryStateAndZip() throws IOException {
		List<CountyCodeMapping> countyFromDB = this.msaService
				.getCountiesByCountryStateAndZip(this.countryCode,
						this.stateCode, this.zipsCode);
		return generateAndWriteComboboxJson(countyFromDB,"countyName");
	}

	public boolean checkForValidatableCountry(String country) {
		if (this.countriesFromMSA.contains(country)) {
			return true;
		}
		return false;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getConfirmPassword() {
		return confirmPassword;
	}

	public void setConfirmPassword(String confirmPassword) {
		this.confirmPassword = confirmPassword;
	}

	public MSAService getMsaService() {
		return msaService;
	}

	public void setMsaService(MSAService msaService) {
		this.msaService = msaService;
	}

	public List<String> getCountriesFromMSA() {
		return countriesFromMSA;
	}

	public void setCountriesFromMSA(List<String> countriesFromMSA) {
		this.countriesFromMSA = countriesFromMSA;
	}

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	public String getStateCode() {
		return stateCode;
	}

	public void setStateCode(String stateCode) {
		this.stateCode = stateCode;
	}

	public String getCityCode() {
		return cityCode;
	}

	public void setCityCode(String cityCode) {
		this.cityCode = cityCode;
	}

	public SortedHashMap<String, String> getCountryList() {
		return countryList;
	}

	public ServiceProvider getServiceProvider() {
		return serviceProvider;
	}

	public void setServiceProvider(ServiceProvider serviceProvider) {
		this.serviceProvider = serviceProvider;
	}

	public String getZipCode() {
		return zipsCode;
	}

	public void setZipCode(String zipCode) {
		this.zipsCode = zipCode;
	}

	public String getDefaultLocale() {
		return defaultLocale;
	}

	public void setDefaultLocale(String defaultLocale) {
		this.defaultLocale = defaultLocale;
	}

	public List<Role> getRoleToBeAssigned() {
		return roleToBeAssigned;
	}

	public void setRoleToBeAssigned(List<Role> roleToBeAssigned) {
		this.roleToBeAssigned = roleToBeAssigned;
	}

	public List<Role> getRolesToBeDisplayed() {
		return rolesToBeDisplayed;
	}

	public void setRolesToBeDisplayed(List<Role> rolesToBeDisplayed) {
		this.rolesToBeDisplayed = rolesToBeDisplayed;
	}

	public List<Role> getDealerRoles() {
		return dealerRoles;
	}

	public void setDealerRoles(List<Role> dealerRoles) {
		this.dealerRoles = dealerRoles;
	}

	public boolean isUserCreated() {
		return userCreated;
	}

	public void setUserCreated(boolean userCreated) {
		this.userCreated = userCreated;
	}

	public String getLoginName() {
		return loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

	public List<ProductLocale> getListOfLocale() {
		return listOfLocale;
	}

	public void setListOfLocale(List<ProductLocale> listOfLocale) {
		this.listOfLocale = listOfLocale;
	}

	public ProductLocaleService getProductLocaleService() {
		return productLocaleService;
	}

	public void setProductLocaleService(
			ProductLocaleService productLocaleService) {
		this.productLocaleService = productLocaleService;
	}

	public String getUserPassword() {
		return userPassword;
	}

	public void setUserPassword(String userPassword) {
		this.userPassword = userPassword;
	}

	public void setConfigParamService(ConfigParamService configParamService) {
		this.configParamService = configParamService;
	}

	public void setClaimService(ClaimService claimService) {
		this.claimService = claimService;
	}

	public String getSelectedBusinessUnit() {
		return selectedBusinessUnit;
	}

	public void setSelectedBusinessUnit(String selectedBusinessUnit) {
		this.selectedBusinessUnit = selectedBusinessUnit;
	}

	public List<TechnicianCertification> getCertificationList() {
		return certificationList;
	}

	public void setCertificationList(
			List<TechnicianCertification> certificationList) {
		this.certificationList = certificationList;
	}

	public TechnicianCertificationService getTechnicianCertificationService() {
		return technicianCertificationService;
	}

	public void setTechnicianCertificationService(
			TechnicianCertificationService technicianCertificationService) {
		this.technicianCertificationService = technicianCertificationService;
	}

	public List<TechnicianCertification> getSelectedCertificationList() {
		return selectedCertificationList;
	}

	public void setSelectedCertificationList(
			List<TechnicianCertification> selectedCertificationList) {
		this.selectedCertificationList = selectedCertificationList;
	}

	public TechnicianCertification getTechCertification() {
		return techCertification;
	}

	public void setTechCertification(TechnicianCertification techCertification) {
		this.techCertification = techCertification;
	}

	public Technician getTechnician() {
		return technician;
	}

	public CertificateService getCertificateService() {
		return certificateService;
	}

	public void setCertificateService(CertificateService certificateService) {
		this.certificateService = certificateService;
	}


	public List<ServiceProvider> getDealersForUser() {
		return dealersForUser;
	}

	public void setDealersForUser(
			List<ServiceProvider> dealersForTechnician) {
		this.dealersForUser = dealersForTechnician;
	}

	public Technician getTechnicianDetails() {
		return technicianDetails;
	}

	public void setTechnicianDetails(Technician technicianDetails) {
		this.technicianDetails = technicianDetails;
	}

	public Set<ServiceProvider> getCompaniesWorkingFor() {
		return companiesWorkingFor;
	}

	public void setCompaniesWorkingFor(Set<ServiceProvider> companiesWorkingFor) {
		this.companiesWorkingFor = companiesWorkingFor;
	}

	public String getCountyCodeWithName() {
		return countyCodeWithName;
	}

	public void setCountyCodeWithName(String countyCodeWithName) {
		this.countyCodeWithName = countyCodeWithName;
	}
}

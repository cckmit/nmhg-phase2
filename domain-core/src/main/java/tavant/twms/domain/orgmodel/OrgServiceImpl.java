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

package tavant.twms.domain.orgmodel;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Currency;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.transaction.annotation.Transactional;

import tavant.twms.domain.bu.BusinessUnit;
import tavant.twms.domain.bu.SourceSystemBuMapping;
import tavant.twms.domain.bu.SourceSystemBuMappingRepository;
import tavant.twms.domain.thirdparty.ThirdPartySearch;
import tavant.twms.infra.HibernateCast;
import tavant.twms.infra.InstanceOfUtil;
import tavant.twms.infra.ListCriteria;
import tavant.twms.infra.PageResult;
import tavant.twms.security.SecurityHelper;

public class OrgServiceImpl  implements OrgService {

	UserRepository userRepository;

	TechnicianRepository technicianRepository;

	DealershipRepository dealershipRepository;

	ServiceProviderRepository serviceProviderRepository;

	ThirdPartyRepository thirdPartyRepository;

	UserGroupRepository userGroupRepository;

	OrganizationRepository organizationRepository;

	SupplierRepository supplierRepository;

	RoleRepository roleRepository;

    UserAuthenticationRepository userAuthenticationRepository;

    SourceSystemBuMappingRepository sourceSystemBuMappingRepository;
    
    MarketingGroupRepository marketingGroupRepository;
    
    BrandRepository brandRepository;

	private SecurityHelper securityHelper;

    public void createUser(User user) {
		userRepository.save(user);
	}
    
    public User findInternalUser(String loginId, String userType){
    	return userRepository.findInternalUser(loginId, userType);
    }

	public void updateUser(User user) {
		userRepository.update(user);
	}

	public void createDealership(ServiceProvider dealer) {
		dealershipRepository.createDealership(dealer);
	}
    public void updateAllUser(List<User> users) {
            userRepository.updateAll(users);
        }


    public void updateDealership(ServiceProvider dealer) {
		dealershipRepository.updateDealership(dealer);
	}
    
    public void updateShipmentAddress(Address shipmentAddress)
    {
    	dealershipRepository.updateShipmentAddress(shipmentAddress);
    }

    public void createShipmentAddress(Address shipmentAddress)
    {
        dealershipRepository.createShipmentAddress(shipmentAddress);
    }

	public void createUserGroup(UserGroup group) {
		userGroupRepository.createUserGroup(group);
	}

	public void updateUserGroup(UserGroup group) {
		userGroupRepository.updateUserGroup(group);
	}

	public User findUserById(Long id) {
		return userRepository.findById(id);
	}

	public User findUserByUserId(String userId) {
		return userRepository.findByUserId(userId);
	}

	public User findUserByName(String userName) {
		return userRepository.findByName(userName);
	}

	public Set<User> findUsersBelongingToRole(String roleName) {
		return userRepository.findUsersBelongingToRole(roleName);
	}
	
	public Set<User> findAllAssignToUsers(){
		return userRepository.findAllAssignToUsers();
		
	}

	/**
	 * @see tavant.twms.domain.orgmodel.OrgService#findDealerById(java.lang.Long)
	 */
	public ServiceProvider findDealerById(Long dealerId) {
		return dealershipRepository.findByDealerId(dealerId);
	}

	/**
	 * @see tavant.twms.domain.orgmodel.OrgService#findDealerByName(java.lang.String)
	 */
	public ServiceProvider findDealerByName(String dealerName) {
		return dealershipRepository.findByDealerName(dealerName);
	}
	
	public ServiceProvider findCertifiedDealerByNumber(String dealerNumber) {
		return dealershipRepository.findCertifiedDealerByNumber(dealerNumber);
	}
	
	

	public Set<String> findAllDealersZipCodesByBUName(String businessUnitName) {
		return allDealerZipCodes(dealershipRepository.findAllBUDealers(businessUnitName));
	}

	public List<ServiceProvider> findAllDealersByBUName(String businessUnitName) {
		return dealershipRepository.findAllBUDealers(businessUnitName);
	}

	


	private Set<String> allDealerZipCodes(List<ServiceProvider> allDealers) {
		Set<String> tkDealersZipCodes = new HashSet<String>(20);
		for (ServiceProvider dealer : allDealers)
		{
			if (dealer!=null && dealer.getAddress()!=null &&
					dealer.getAddress().getZipCode()!=null)
				tkDealersZipCodes.add(dealer.getAddress().getZipCode());
		}
		return tkDealersZipCodes;
	}

	public Set<String> findAllZipCodesForAFamily(String dealerFamilyCode) {
		List<ServiceProvider>  allDealers = findDealersByFamily(dealerFamilyCode);
		return allDealerZipCodes(allDealers);
	}

	public List<ServiceProvider> findDealersByFamily(String dealerFamilyCode)
	{
		return dealershipRepository.findDealersByFamily(dealerFamilyCode);
	}

	public List<ServiceProvider> findDealersByName(String dealerName) {
		return isLoggedInUserAnEnterpriseDealer() ?  getMatchingDealerShips(findDealersByIds(
                getLoggedInUsersDealership().getChildDealersIds()), "name", dealerName, null)
                            :dealershipRepository.findDealersByNumberOrName(null, dealerName);
	}

	public ServiceProvider findServiceProviderByName(String serviceProviderName) {
		return serviceProviderRepository.findServiceProviderByName(serviceProviderName);
	}

	/**
	 * @param serviceProviderName
	 * @param pageNumber
	 * @param pageSize
	 * @return
	 */
	public List<String> findServiceProviderNamesStartingWith(String serviceProviderName,
			int pageNumber, int pageSize) {
		return serviceProviderRepository.findServiceProviderNamesStartingWith(serviceProviderName,
				pageNumber, pageSize);
	}

	public List<String> findServiceProviderNumbersStartingWith(String serviceProviderNumber,
			int pageNumber, int pageSize) {
		return serviceProviderRepository.findServiceProviderNumbersStartingWith(serviceProviderNumber,
				pageNumber, pageSize);
	}

	/**
	 * @see tavant.twms.domain.orgmodel.OrgService#findDealerByNumber(java.lang.String)
	 */
	public ServiceProvider findDealerByNumber(String dealerNumber) {
		return dealershipRepository.findByDealerNumber(dealerNumber);
	}
	
	public List<BigDecimal> findServiceProviderIds(String dealerNumber,String businessUnit) {
		return dealershipRepository.findServiceProviderIds(dealerNumber,businessUnit);
	}


	/* (non-Javadoc)
	 * @see tavant.twms.domain.orgmodel.OrgService#findServiceProviderByNumber(java.lang.String)
	 */
	public ServiceProvider findServiceProviderByNumber(String serviceProviderNumber)
	{
		return serviceProviderRepository.findServiceProviderByNumber(serviceProviderNumber);
	}
	
	public ServiceProvider findServiceProviderByNumberWithOutBU(final String serviceProviderNumber){
		ServiceProvider selectedServiceProvider = null;
		List<ServiceProvider>serviceProviders=serviceProviderRepository.findServiceProviderByNumberWithOutBU(serviceProviderNumber);
		if (serviceProviders.size() > 1) {
			for (ServiceProvider activeServiceProvider : serviceProviders) {
				if (activeServiceProvider.getStatus() != null
						&& activeServiceProvider.getStatus().equalsIgnoreCase(
								"ACTIVE")) {
					selectedServiceProvider = activeServiceProvider;
				}
			}
			if (selectedServiceProvider == null) {
				selectedServiceProvider = serviceProviders.get(0);
			}
		} else if(!serviceProviders.isEmpty()) {
			selectedServiceProvider = serviceProviders.get(0);
		}
		return selectedServiceProvider;
	}

	
	public List<ServiceProvider> findNationalAccountsWhoseNameStartsWith(String serviceProviderName,
			int pageNumber, int pageSize) {
		return serviceProviderRepository.findNationalAccountsWhoseNameStartsWith(serviceProviderName,
				pageNumber, pageSize);
	}
	
	public List<ServiceProvider> findNationalAccountsWhoseNumberStartsWith(String serviceProviderName,
			int pageNumber, int pageSize) {
		return serviceProviderRepository.findNationalAccountsWhoseNumberStartsWith(serviceProviderName,
				pageNumber, pageSize);
	}
	
	public List<ServiceProvider> findDealersByNumber(String dealerNumber) {
		return isLoggedInUserAnEnterpriseDealer() ?  getMatchingDealerShips(findDealersByIds(
                getLoggedInUsersDealership().getChildDealersIds()), "number", null, dealerNumber)
                        :dealershipRepository.findDealersByNumberOrName(dealerNumber,
				null);
	}

	public List<ServiceProvider> findDealersWhoseNumberStartingWith(
			String dealerNumber, int pageNumber, int pageSize) {
        if(isLoggedInUserAnEnterpriseDealer())
            return getMatchingDealerShips(findDealersByIds(
                    getLoggedInUsersDealership().getChildDealersIds()), "number", dealerNumber);
        else
            return dealershipRepository.findDealersWhoseNumberStartingWith(
                    dealerNumber, pageNumber, pageSize);
    }

	public Set<UserGroup> findAllUserGroups() {
		return userGroupRepository.findAllUserGroups();
	}

	public Set<User> findAllDealers() {
		return userRepository.findAllDealers();
	}

	public Set<User> findAllProcessors() {
		return userRepository.findAllProcessors();
	}

	public List<User> findAllAvailableProcessors() {
		return userRepository.findAllAvailableProcessors();
	}
	
	public List<User> findAllAvailableRecoveryProcessors() {
		return userRepository.findAllAvailableRecoveryProcessors();
	}

	public Set<User> findAllUsers() {
		return userRepository.findAllUsers();
	}

	public Role findRoleByName(String roleName) {
		return roleRepository.findByName(roleName);
	}

	@SuppressWarnings("unchecked")
	public Collection getOrgModel() {
		final List objectList = new ArrayList();
		Set<UserGroup> groups = findAllUserGroups();
		objectList.addAll(groups);
		for (UserGroup group : groups) {
			objectList.addAll(group.getUserGroupAttrVals());
		}
		Set<User> users = findAllUsers();
		objectList.addAll(users);
		for (User user : users) {
			objectList.addAll(user.getUserAttrVals());
		}
		return objectList;
	}

	public List<Supplier> findAllSuppliers() {
		return userRepository.findAllSuppliers();
	}

	public Party getPartyById(Long id) {
		return organizationRepository.findById(id);
	}

	public Organization findOrganizationByName(String name) {
		return organizationRepository.findByName(name);
	}

	/**
	 * @param dealershipRepository
	 *            the dealershipRepository to set
	 */
	public void setDealershipRepository(
			DealershipRepository dealershipRepository) {
		this.dealershipRepository = dealershipRepository;
	}

	/**
	 * @param userRepository
	 *            the userRepository to set
	 */
	public void setUserRepository(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	/**
	 * @return the userRepository
	 */
	public UserRepository getUserRepository() {
		return userRepository;
	}

	/**
	 * @param userGroupRepositry
	 *            the userGroupRepositry to set
	 */
	public void setUserGroupRepository(UserGroupRepository userGroupRepositry) {
		userGroupRepository = userGroupRepositry;
	}

	public void setOrganizationRepository(
			OrganizationRepository organisationRepository) {
		organizationRepository = organisationRepository;
	}

	/**
	 * @return the userGroupRepositry
	 */
	public UserGroupRepository getUserGroupRepository() {
		return userGroupRepository;
	}
	
	/**
	 * 
	 * @param brandRepository
	 */
	public void setBrandRepository(BrandRepository brandRepository) {
		this.brandRepository = brandRepository;
	}

	/**
	 * 
	 * @param marketingGroupRepository
	 */
	public void setMarketingGroupRepository(
			MarketingGroupRepository marketingGroupRepository) {
		this.marketingGroupRepository = marketingGroupRepository;
	}

	public List<String> findDealerNamesStartingWith(String dealerName,
			int pageNumber, int pageSize) {
		return dealershipRepository.findDealerNamesStartingWith(dealerName,
				pageNumber, pageSize);
	}

	public List<String> findDealerNumbersStartingWith(String dealerNumber,
			int pageNumber, int pageSize) {
		return dealershipRepository.findDealerNumbersStartingWith(dealerNumber,
				pageNumber, pageSize);
	}

	public List<ServiceProvider> findDealersWhoseNameStartsWith(String dealerName,
			int pageNumber, int pageSize) {
        if(isLoggedInUserAnEnterpriseDealer())
            return getMatchingDealerShips(findDealersByIds(
                    getLoggedInUsersDealership().getChildDealersIds()), "name", dealerName);
        else
            return dealershipRepository.findDealersWhoseNameStartsWith(dealerName,
                    pageNumber, pageSize);
    }

	public List<String> findUsersWithNameLike(String name, int pageNumber,
			int pageSize, String currentBusinessUnit) {
		return userRepository.findUsersWithNameLike(name, pageNumber, pageSize, currentBusinessUnit);
	}

	public List<String> findUsersWithNameLikeOfType(String name, String userType) {
		return userRepository.findUsersWithNameLikeOfType(name, userType);
	}

	public boolean isDealer(User user) {
		return dealershipRepository.isDealer(user);
	}

	/**
	 * This method will return true or false value depending on the fact whether the current logged in user
	 * belongs to a third party organization or not.
	 *
	 * @param user
	 * @return
	 */
	public boolean isThirdParty(User user)
	{
		return thirdPartyRepository.isThirdParty(user);
	}

	public boolean isServiceProvider(User user) {

		return dealershipRepository.isDealer(user);
	}
	
	public void setRoleRepository(RoleRepository roleRepository) {
		this.roleRepository = roleRepository;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see tavant.twms.domain.orgmodel.OrgService#isInspector(tavant.twms.domain.orgmodel.User)
	 *      TODO This API has to be change as now it returns true if any user
	 *      has this role. It does not checks for the user who has only this
	 *      role
	 */
	public boolean isInspector(User user) {
		boolean isInspector = false;
		Set<Role> rolesOfUser = user.getRoles();
		if (rolesOfUser != null && !rolesOfUser.isEmpty()) {
			for (Iterator<Role> iterator = rolesOfUser.iterator(); iterator
					.hasNext();) {
				Role role = iterator.next();
				if ("inspector".equals(role.getName())) {
					isInspector = true;
					break;
				}
			}
		}
		return isInspector;
	}

	public boolean doesUserHaveRole(User user, String roleName)
	{
		boolean isRolePresent = false;
		Set<Role> rolesOfUser = user.getRoles();
		if (rolesOfUser != null && !rolesOfUser.isEmpty()) {
			for (Iterator<Role> iterator = rolesOfUser.iterator(); iterator
					.hasNext();) {
				Role role = iterator.next();
				if (roleName.equals(role.getName())) {
					isRolePresent = true;
					break;
				}
			}
		}
		return isRolePresent;
	}



	/*
	 * (non-Javadoc)
	 *
	 * @see tavant.twms.domain.orgmodel.OrgService#isInspector(tavant.twms.domain.orgmodel.User)
	 *      TODO This API has to be change as now it returns true if any user
	 *      has this role. It does not checks for the user who has only this
	 *      role
	 */
	public boolean isReceiver(User user) {
		boolean isInspector = false;
		Set<Role> rolesOfUser = user.getRoles();
		if (rolesOfUser != null && !rolesOfUser.isEmpty()) {
			for (Iterator<Role> iterator = rolesOfUser.iterator(); iterator
					.hasNext();) {
				Role role = iterator.next();
				if ("receiver".equals(role.getName())) {
					isInspector = true;
					break;
				}
			}
		}
		return isInspector;
	}

	public boolean isInternalUser(final User user) {
		return userRepository.isInternalUser(user);
	}
	
	public PageResult<Party> findAllSuppliers(ListCriteria listCriteria) {
		return organizationRepository.findAllSuppliers(listCriteria);
	}

	public List<Party> findByIds(Collection<Long> collectionOfIds) {
		return organizationRepository.findByIds(collectionOfIds);
	}

	public void updateOrganization(Organization organization) {
		organizationRepository.updateOrganization(organization);
	}

	public void createTechnician(Technician technician) {
		technicianRepository.save(technician);
	}

	public void updateTechnician(Technician technician) {
		technicianRepository.update(technician);
	}

	public void setTechnicianRepository(
			TechnicianRepository technicianRepository) {
		this.technicianRepository = technicianRepository;
	}

	public Technician findTechnicianById(Long id) {
		// TODO Auto-generated method stub
		return null;
	}

	public Technician findTechnicianByName(String userName) {
		return technicianRepository.findByName(userName);
	}

	public Technician findTechnicianByUserId(String userId) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<OrganizationAddress> getAddressesForOrganization(
			Organization organization) {
		return this.organizationRepository
				.getAddressesForOrganization(organization);
	}
	
	public Organization findOrgById(Long id)
	{
		return this.organizationRepository.findOrgById(id);
	}

	public PageResult<OrganizationAddress> getAddressesForOrganization(ListCriteria criteria, Organization organization) {
		return this.organizationRepository.getAddressesForOrganization(criteria, organization);
	}

	public OrganizationAddress getPrimaryOrganizationAddressForOrganization(
			Organization organization) {
		return this.organizationRepository
				.getPrimaryOrganizationAddressForOrganization(organization);
	}

	public List<ServiceProvider> findDealersByNumberOrName(String dealerNumber,
			String dealerName) {
        return isLoggedInUserAnEnterpriseDealer() ?  getMatchingDealerShips(findDealersByIds(
                            getLoggedInUsersDealership().getChildDealersIds()), "both", dealerName,dealerNumber)
		 :dealershipRepository.findDealersByNumberOrName(dealerNumber,
				dealerName);
	}

	public List<String> findRoleNamesStartingWith(String name, int pageNumber,
			int pageSize) {
		return roleRepository.findRoleNamesStartingWith(name, pageNumber,
				pageSize);
	}

	public SupplierRepository getSupplierRepository() {
		return supplierRepository;
	}

	public void setSupplierRepository(SupplierRepository supplierRepository) {
		this.supplierRepository = supplierRepository;
	}

    public User findUserByNameForLogin(String userName) {
		return userAuthenticationRepository.findByName(userName);
	}

    public void setUserAuthenticationRepository(UserAuthenticationRepository userAuthenticationRepository) {
        this.userAuthenticationRepository = userAuthenticationRepository;
    }

	public PageResult<User> findProcessors(ListCriteria criteria, String role) {
		return userRepository.findAllProcessors(criteria, role);
	}
	
	public User findDefaultUserBelongingToRole(String userRole) {
		return userRepository.findDefaultUserBelongingToRole(userRole);
	}

	public User findDefaultUserBelongingToRoleForSelectedBU(final String businessUnit, final String userRole) {
		return userRepository.findDefaultUserBelongingToRoleForSelectedBU(businessUnit, userRole);
	}

	@Transactional(readOnly = false)
	public void createOrgAddressForDealer(OrganizationAddress userOrgAddress,
			Long dealerId) {
		ServiceProvider dealer = dealershipRepository.findByDealerId(dealerId);
		List<OrganizationAddress> orgAddresses = dealer.getOrgAddresses();
		if (orgAddresses == null) {
			orgAddresses = new ArrayList<OrganizationAddress>();
		} else {
			for (OrganizationAddress orgAddress : orgAddresses) {
				if (orgAddress.getSiteNumber().equalsIgnoreCase(
						userOrgAddress.getSiteNumber())) {
					removeExistingAddressesForOrganization(orgAddress
							.getSiteNumber());
				}
			}
		}
		orgAddresses.add(userOrgAddress);
		dealer.setOrgAddresses(orgAddresses);
		dealershipRepository.updateDealership(dealer);
	}
	
	@Transactional(readOnly = false)
	public void createAddressForDealer(Address address, ServiceProvider dealer) {
        dealer.setAddress(address);
		dealershipRepository.updateDealership(dealer);
	}

	@Transactional(readOnly = false)
	public void createOrgAddressForDealer(OrganizationAddress userOrgAddress, ServiceProvider dealer) {
		List<OrganizationAddress> orgAddresses = dealer.getOrgAddresses();
        if (orgAddresses==null) {
			orgAddresses = new ArrayList<OrganizationAddress>();
        }
        if(userOrgAddress!=null){
           orgAddresses.add(userOrgAddress);
        }
        dealer.setOrgAddresses(orgAddresses);
        dealershipRepository.updateDealership(dealer);
	}

	/* (non-Javadoc)
	 * @see tavant.twms.domain.orgmodel.OrgService#findThirdPartyByNumberOrName(tavant.twms.domain.thirdparty.ThirdPartySearch, tavant.twms.infra.ListCriteria)
	 */
	public PageResult<ThirdParty> findThirdPartyByNumberOrName(ThirdPartySearch thirdPartySearch, ListCriteria listCriteria)
	{
		return thirdPartyRepository.findThirdPartyByNumberOrName(thirdPartySearch, listCriteria);
	}

	public boolean isUserHasRole(User loggedInUser, String roleName) {
		return userRepository.isUserHasRole(loggedInUser, roleName);
	}

	public List<ServiceProvider> findAllOtherDealersByBUName(String businessUnitName, String dealerFamilyCode) {
		return dealershipRepository.findAllOtherDealersByBUName(businessUnitName, dealerFamilyCode);
	}

	public SourceSystemBuMapping findBySourceSystem(final String sourceSystem)
	{
		return sourceSystemBuMappingRepository.findBySourceSystem(sourceSystem);
	}

	/**
	 * Getter for ThirdPartyRepository
	 *
	 * @return
	 */
	public ThirdPartyRepository getThirdPartyRepository()
	{
		return thirdPartyRepository;
    }

	public ServiceProvider findDealersByNumberWithoutLike(final String dealerNumber)
	{
		return dealershipRepository.findDealersByNumberWithoutLike(dealerNumber);
	}


	/**
	 * Setter for ThirdPartyRepository
	 *
	 * @param thirdPartyRepository
	 */
	public void setThirdPartyRepository(ThirdPartyRepository thirdPartyRepository) {
		this.thirdPartyRepository = thirdPartyRepository;
	}

	/**
	 * @return
	 */
	public ServiceProviderRepository getServiceProviderRepository() {
		return serviceProviderRepository;
	}

	/**
	 * @param serviceProviderRepository
	 */
	public void setServiceProviderRepository(
			ServiceProviderRepository serviceProviderRepository) {
		this.serviceProviderRepository = serviceProviderRepository;
	}

	public Boolean isThirdPartyDealerWithLogin(Long id) {
		return userRepository.isThirdPartyDealerWithLogin(id);
	}

	public SourceSystemBuMappingRepository getSourceSystemBuMappingRepository() {
		return sourceSystemBuMappingRepository;
	}

	public void setSourceSystemBuMappingRepository(
			SourceSystemBuMappingRepository sourceSystemBuMappingRepository) {
		this.sourceSystemBuMappingRepository = sourceSystemBuMappingRepository;
	}

	public void updateUserAvailability(Long userId, String selectedBusinessUnit, String role, Boolean available,
			Boolean isDefaultUserForRole) {
		User orgUser = findUserById(userId);
		UserBUAvailability userBUAvailability = orgUser.findUserBUAvailability(selectedBusinessUnit, role);
		if (orgUser.getUserAvailablity()!=null && !orgUser.getUserAvailablity().isEmpty())
			orgUser.getUserAvailablity().remove(userBUAvailability);
		Role roleForAvailability = findRoleByName(role);
		if (userBUAvailability==null)
		{
			userBUAvailability = new UserBUAvailability();
			userBUAvailability.setOrgUser(orgUser);
			userBUAvailability.getBusinessUnitInfo().setName(selectedBusinessUnit);
			userBUAvailability.setRole(roleForAvailability);
		}
		userBUAvailability.setAvailable(available);
		if (isDefaultUserForRole)
		{
			// Remove the role from the existing default user
			removeExistingDefaultUserForRole(selectedBusinessUnit, role, roleForAvailability);
			// Add the role to the user as Default
		}
		userBUAvailability.setDefaultToRole(isDefaultUserForRole);
		userBUAvailability.setRole(roleForAvailability);
		orgUser.getUserAvailablity().add(userBUAvailability);
		this.userRepository.update(orgUser);
	}
	
	public void removeExistingDefaultUserForRole(String selectedBusinessUnit, String role, Role roleForAvailability)
	{
		// Remove the role from the existing default user
		User existingDefaultUserForRole = findDefaultUserBelongingToRoleForSelectedBU(selectedBusinessUnit, role);
		if (existingDefaultUserForRole!=null)
		{
			UserBUAvailability existingUserBUAvailability = existingDefaultUserForRole.findDefaultUserBUAvailability(selectedBusinessUnit, role);
			if (existingUserBUAvailability!=null)
			{
				/*if (existingDefaultUserForRole.getUserAvailablity()!=null &&
						!existingDefaultUserForRole.getUserAvailablity().isEmpty())
				{
					for (Iterator<UserBUAvailability> iterator = existingDefaultUserForRole.getUserAvailablity().iterator(); iterator
							.hasNext();) {
						UserBUAvailability buAvailability = (UserBUAvailability) iterator.next();
						if (buAvailability!=null && buAvailability.isDefaultToRole())
						{
							buAvailability.setDefaultToRole(Boolean.FALSE);
							break;
						}
					}
				}*/
				existingUserBUAvailability.setDefaultToRole(Boolean.FALSE);
				this.userRepository.update(existingDefaultUserForRole);
			}
		}
	}
    public List<User> findInternalUsersWithNameLike(String name){
      return  this.userRepository.findInternalUsersWithNameLike(name);
    }
    
    public List<User> findInternalUsersWithNameLike(String name,int pageNumber, int pageSize){
        return  this.userRepository.findInternalUsersWithNameLike(name,pageNumber,pageSize);
      }
    
    public List<BusinessUnit> findAllBusinessUnitsForUser(final User name){
        return  this.userRepository.findAllBusinessUnitsForUser(name);
      }

	public ServiceProvider findServiceProviderById(Long id) {
		return serviceProviderRepository.findServiceProviderById(id);
	}

	public User findFOCUserInServiceProviderOrganization(final String serviceProviderNo){
		return this.userRepository.findFOCUserInServiceProviderOrganization(serviceProviderNo);
	}


	public List<User> findUsersWithRoleInServiceProviderOrganization(final String serviceProviderNo,final String role){
		return this.userRepository.findUsersWithRoleInServiceProviderOrganization(serviceProviderNo, role);
	}

    public List<User> findDealerUsers(ServiceProvider serviceProvider, List<String> listOfRoles, String partialLoginId) {
        return userRepository.findDealerUsers(serviceProvider,listOfRoles,partialLoginId);
    }

	public List<User> findAllUsersByRole(List<String> listOfRoles) {
		return userRepository.findAllUsersByRole(listOfRoles);
	}
    
    public Set<Role> getRolesForUser(User user) {
		return this.userRepository.getRolesForUser(user);
	}

    public List<Currency> listUniqueCurrencies()
    {
    	List<Currency> uniqueCurrencies = organizationRepository.listUniqueCurrencies();
    	return uniqueCurrencies;
    }

    @Transactional(readOnly = false)
    public void updateOrganizationAddress(OrganizationAddress orgAddress) {
    	this.organizationRepository.updateOrganizationAddress(orgAddress);
    }
    
    @Transactional(readOnly = false)
    public void updateAddress(Address address) {
    	this.organizationRepository.updateAddress(address);
    }
    
    public OrganizationAddress getAddressesForOrganizationBySiteNumber(
			final Organization organization, final String siteNumber) {
    	return this.organizationRepository.getAddressesForOrganizationBySiteNumber(organization, siteNumber);
    }
    
    public OrganizationAddress getOrganizationAddressBySiteNumber(final String siteNumber){
    	return this.organizationRepository.getOrganizationAddressBySiteNumber(siteNumber);
    }
    
    public OrganizationAddress getOrgAddressBySiteNumberForUpload(final String siteNumber, final Long orgId){
        return this.organizationRepository.getOrgAddressBySiteNumberForUpload(siteNumber, orgId);
    }
    
    public List<ServiceProvider> findCertifiedDealersWhoseNameStartsWith(String dealerName,
			int pageNumber, int pageSize) {
		return dealershipRepository.findCertifiedDealersWhoseNameStartsWith(dealerName,
				pageNumber, pageSize);
	}

	public Set<Role> getRolesByType(RoleType roleType) {
		return this.userRepository.getRolesByType(roleType);
	}
	
	public User findSystemUserByName() {
		return userRepository.findSystemUserByName();
	}
   
	public List<User> findUsersWithLoginIds(List<String> loginIds) {
		return userRepository.findUsersWithLoginIds(loginIds);
	}

    public List<ServiceProvider> findDealersByIds(List<Long> dealerIds) {
       return serviceProviderRepository.findByIds(dealerIds);

    }

    public boolean isLoggedInUserAnEnterpriseDealer() {
		ServiceProvider dealership = getLoggedInUsersDealership();
		return (dealership != null) && dealership.isEnterpriseDealer();
    }
    public ServiceProvider getLoggedInUsersDealership() {
		Organization organization = securityHelper.getLoggedInUser().getBelongsToOrganization();
        return ( organization != null && InstanceOfUtil.isInstanceOfClass(ServiceProvider.class, organization)) ?
        								new HibernateCast<ServiceProvider>().cast(organization) : null;

	}

    public SecurityHelper getSecurityHelper() {
        return securityHelper;
    }

    public void setSecurityHelper(SecurityHelper securityHelper) {
        this.securityHelper = securityHelper;
    }

        //used for autocompletion
	public List<ServiceProvider> getMatchingDealerShips(List<ServiceProvider> dealers, String identifier , String startsWith) {
        if(dealers== null || dealers.isEmpty())
            return dealers;
        List<ServiceProvider> matchingDealers = new ArrayList<ServiceProvider>();
        for(ServiceProvider serviceProvider: dealers) {
            if(identifier.equalsIgnoreCase("name") && serviceProvider.getName().startsWith(startsWith)) {
                matchingDealers.add(serviceProvider);
            }
            else if(identifier.equalsIgnoreCase("number") && serviceProvider.getDealerNumber().startsWith(startsWith)) {
                matchingDealers.add(serviceProvider);
            }
        }
        return matchingDealers;
    }

        //used for search
    public List<ServiceProvider> getMatchingDealerShips(List<ServiceProvider> dealers, String identifier ,
                                                        String nameStartsWith, String numberStartsWith) {
        if(dealers== null || dealers.isEmpty())
            return dealers;
        List<ServiceProvider> matchingDealers = new ArrayList<ServiceProvider>();
        for(ServiceProvider serviceProvider: dealers) {
            if(identifier.equalsIgnoreCase("name") && serviceProvider.getName().toUpperCase().contains(nameStartsWith.toUpperCase())) {
                matchingDealers.add(serviceProvider);
            }
            else if(identifier.equalsIgnoreCase("number") && serviceProvider.getDealerNumber().toUpperCase().contains(numberStartsWith.toUpperCase())) {
                matchingDealers.add(serviceProvider);
            }
            else if(identifier.equalsIgnoreCase("both") && serviceProvider.getDealerNumber().toUpperCase().contains(numberStartsWith.toUpperCase())
                    && serviceProvider.getName().toUpperCase().contains(nameStartsWith.toUpperCase())) {
                matchingDealers.add(serviceProvider);
            }
        }
        return matchingDealers;
    }
    
	public PageResult<ServiceProvider> findAllNationalAccounts(String nationalAccountName,ListCriteria listCriteria){
		return serviceProviderRepository.findAllNationalAccounts(nationalAccountName, listCriteria);
	}
	
	public PageResult<Party> findDealersByOrganizations(ListCriteria listCriteria ,List<Organization> organizations) {
		return this.organizationRepository.findDealersByOrganizations(listCriteria, organizations);
	}
    //TODO Need to refactor following methods

    public List<User> findUsersListBelongingToRoleForDisplay(String roleName){
        return this.userRepository.findUsersListBelongingToRoleForDisplay(roleName);
    }

    public List<User> findUsersBelongingToRoleAndOrgForDisplay(Organization organization, String roleName) {
        return this.userRepository.findUsersBelongingToRoleAndOrgForDisplay(organization, roleName);
    }

    public List<User> findUsersLikeBelongingToRoles(String name, List<RoleType> roleTypes) {
        return this.userRepository.findUsersLikeBelongingToRoles(name, roleTypes);
    }

    public List<User> findUsersWithFullNameLike(String fullNamePrefix, int pageNumber, int pageSize) {
        return userRepository.findUsersWithFullNameLike(fullNamePrefix, pageNumber, pageSize);
    }

    public List<User> findUsersWithNameLike(String name){
        return  this.userRepository.findUsersWithNameLike(name);
    }

    public List<User> findUsersWithNameLike(final String name ,final String type){
        return  this.userRepository.findUsersWithNameLike(name,type);
    }

    public List<User> findUsersForDealerWithNameLike(final Long dealerId, final String name) {
        return this.userRepository.findUsersForDealerWithNameLike(dealerId, name);
    }
    public List<User> findFleetDealerUsersWithNameLike(final Long dealerId, final String name,String userType) {
        return this.userRepository.findFleetDealerUsersWithNameLike(dealerId, name,userType);
    }

	public MarketingGroup findMarketingGroupByCode(String mktGrpCode) {
		MarketingGroup marketingGroup = marketingGroupRepository.findByMarketingCode(mktGrpCode);
		return marketingGroup;
	}
	
	public Brand findBrandByCode(String brandCode) {
		Brand brand = brandRepository.findByBrandCode(brandCode);
		return brand;
	}

	public Dealership findDealerDetailsByNumber(String dealerNumber) {
		// TODO Auto-generated method stub
		return serviceProviderRepository.findDealerDetailsByNumber(dealerNumber);
	}

	public void removeExistingAddressesForOrganization(
			final String siteNumber) {
		organizationRepository
				.removeAddressesForOrganization(siteNumber);
		
	}

	public String findDealerBrands(Organization organization) {
		return dealershipRepository.findDealerBrands(organization);
	}

    public String getCevaProcessor(Object claim){
        List<User> cevaUsers =  userRepository.findUsersListBelongingToRoleForDisplay(Role.CEVA_PROCESSOR);
        if(cevaUsers.size() >0){
            return cevaUsers.get(0).getName();
        }
        return null;
    }
    
    public List<Dealership> findDealershipsFromDealerCode(String dealerCode) {
		return userRepository.findAllDealersFromDealerCode(dealerCode);
	}

    public ServiceProvider findDealerByServiceProviderID(
            String serviceProviderId) {
        return dealershipRepository
                .findDealerByServiceProviderID(serviceProviderId);
    }

    public void saveOrUpdate(User user) {
        userRepository.saveOrUpdate(user);
    }

    public Map<Long, String> getTechniciansForDealer(Organization organization, String businessUnitName) {
        return this.userRepository.findTechnicianForDealer(organization.getId(),businessUnitName);
    }

    public Address findAddressWithMandatoryFields(String addressLine1,String country, String city, String state, String zipCode){
        return organizationRepository.findAddressWithMandatoryFields(addressLine1, country, city, state, zipCode);
    }

    public List<Organization> getChildOrganizations(final Long orgId){
        return organizationRepository.getChildOrganizations(orgId);
    }

    public List<Long> getChildOrganizationsIds(final Long orgId){
        return organizationRepository.getChildOrganizationIds(orgId);
    }

    public List<Long> getParentOrganizationsIds(final Long orgId){
        return organizationRepository.getChildOrganizationIds(orgId);
    }
    
    public List<User> findAllAvailableFleetProcessors() {

        return userRepository.findAllAvailableFleetProcessors();
    }
    
	
	public List<ServiceProvider> findAllServiceProviders()
	{
		return serviceProviderRepository.findAll();
	}

    public List<User> getDealersFromDealerShip(List<Long> dealerShips) {
        return organizationRepository.getDealersFromDealerShip(dealerShips);
    }

    public List<User> getCustomers(List<Long> customerIds) {
        return organizationRepository.getCustomers(customerIds);
    }
   
   public String findMarketingGroupCodeBrandByDealership(Dealership dealer){
	   return dealershipRepository.findMarketingGroupCodeBrandByDealership(dealer);
   }
    
    public String getLoggedInUsersCountry(Long id){
    	return userRepository.getLoggedInUsersCountry(id);
    }

    public Long checkLoggedInDealerForDualBrand(Long id){
        return organizationRepository.checkLoggedInDealerForDualBrand(id);
    }

	public List<User> findFleetCustomerUsers(Organization organization,
			List<Role> listOfRoles, String partialLoginId) {
		 return userRepository.findFleetCustomerUsers(organization,listOfRoles,partialLoginId);
	}
    
    public Set<User> findAllInternalUsers(){
        return userRepository.findAllInternalUsers();
    }

    public List<User> findAvailableUsersByRoleAndPermission(final String roleName, final Long orgId, final String permissionString) {
        return userRepository.findAvailableUsersByRoleAndPermission(roleName, orgId, permissionString);
    }

    public List<User> findUsersByRoleAndPermission(String roleName, Long orgId, String permissionString) {
        return userRepository.findUsersByRoleAndPermission(roleName,orgId,permissionString); 
    }

	public List<ServiceProvider> findDealersListByNumber(String dealerNumber) {
		return dealershipRepository.findByDealerListByNumber(dealerNumber);
	}
	
	public List<User> findAllUsersBelongingToServiceProvider(final String serviceProviderNo){
		return this.userRepository.findAllUsersBelongingToServiceProvider(serviceProviderNo);
	}
	
	public Set<Role> getRolesByTypeAndCategory(RoleType roleType , RoleCategory roleCategory) {
		return this.userRepository.getRolesByTypeAndCategory(roleType,roleCategory);
	}
	
	public List<User> findCustomerUsers(List<Organization> orgs, String partialLoginId) {
		 return userRepository.findCustomerUsers(orgs,partialLoginId);
	}

    public List<User> findfleetCustomerUsersBasedOnOrganization(Organization organization,String partialLoginName) {
        return userRepository.findfleetCustomerUsersBasedOnOrganization(organization,partialLoginName);
    }
    
    public List<User> findDealerUsersBasedOnOrganizations( List<Long> orgIds, String eventName)
    {
        return userRepository.findDealerUsersBasedOnOrganizations(orgIds,eventName);
    }
    public Set<Role> getFleetRolesByTypeAndCategory( List<RoleType> roleTypes, RoleCategory eventName)
    {
        return userRepository.getFleetRolesByTypeAndCategory(roleTypes,eventName);
    }

    public List<User> findAllUsersSubscribedForEvent(Set<User> actorList, String eventName) {
        return userRepository.findAllUsersSubscribedForEvent(actorList,eventName);
    }
    
    public ServiceProvider findServiceProviderByNumberAndBusinessUnit(String serviceProviderNumber, String businessUnit) {
        return serviceProviderRepository.findServiceProviderByNumberAndBusinessUnit(serviceProviderNumber,businessUnit);
    }

   

}
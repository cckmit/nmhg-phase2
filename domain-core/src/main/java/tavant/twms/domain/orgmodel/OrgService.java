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
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.transaction.annotation.Transactional;

import tavant.twms.domain.bu.BusinessUnit;
import tavant.twms.domain.bu.SourceSystemBuMapping;
import tavant.twms.domain.thirdparty.ThirdPartySearch;
import tavant.twms.infra.ListCriteria;
import tavant.twms.infra.PageResult;

@Transactional(readOnly = true)
public interface OrgService {

	@Transactional(readOnly = false)
	void createUser(User user);

	@Transactional(readOnly = false)
	void updateUser(User user);
	
	@Transactional(readOnly = false)
	void createTechnician(Technician technician);

	@Transactional(readOnly = false)
	void updateTechnician(Technician technician);
	

	@Transactional(readOnly = false)
	void createDealership(ServiceProvider dealer);

	@Transactional(readOnly = false)
	void updateDealership(ServiceProvider dealer);
	
	@Transactional(readOnly = false)
	void updateShipmentAddress(Address shipmentAddress);

    @Transactional(readOnly = false)
    void createShipmentAddress(Address shipmentAddress);

	@Transactional(readOnly = false)
	void createUserGroup(UserGroup group);

	@Transactional(readOnly = false)
	void updateUserGroup(UserGroup group);

    @Transactional(readOnly = false)
	void updateAllUser(List<User> users);

    /**
	 * @param id
	 * @return User object with given id or null if not found
	 */
	User findUserById(Long id);

	/**
	 * @param userId
	 * @return User object with given user id or null if not found
	 */
	User findUserByUserId(String userId);

	/**
	 * @param userName
	 * @return User object with given userName or null if not found
	 */
	User findUserByName(String userName);

	/**
	 * @param id
	 * @return Technician object with given id or null if not found
	 */
	Technician findTechnicianById(Long id);

	/**
	 * @param userId
	 * @return Technician object with given user id or null if not found
	 */
	Technician findTechnicianByUserId(String userId);
	
	/**
	 * @param userName
	 * @return Technician object with given userName or null if not found
	 */
	Technician findTechnicianByName(String userName);

	/**
	 * @param dealerId
	 * @return User object with given id or null if not found
	 */
	ServiceProvider findDealerById(Long dealerId);

	/**
	 * @param dealerName
	 * @return User object with given userName or null if not found
	 */
	ServiceProvider findDealerByName(String dealerName);
	
	ServiceProvider findCertifiedDealerByNumber(String dealerNumber);

	List<String> findDealerNamesStartingWith(String dealerName, int pageNumber,
			int pageSize);
	
	
	/**
	 * @param serviceProviderName
	 * @return
	 */
	ServiceProvider findServiceProviderByName(String serviceProviderName);
	
	/**
	 * @param serviceProviderName
	 * @param pageNumber
	 * @param pageSize
	 * @return
	 */
	public List<String> findServiceProviderNamesStartingWith(String serviceProviderName,
			int pageNumber, int pageSize);

	List<ServiceProvider> findDealersWhoseNameStartsWith(String dealerName,
			int pageNumber, int pageSize);
    
	List<ServiceProvider> findDealersWhoseNumberStartingWith(String dealerNumber,
			int pageNumber, int pageSize);
	
	public List<ServiceProvider> findNationalAccountsWhoseNameStartsWith(String serviceProviderName,
			int pageNumber, int pageSize); 
	
	public List<ServiceProvider> findNationalAccountsWhoseNumberStartsWith(String serviceProviderName,
			int pageNumber, int pageSize);
	
	/**
	 * This has been implemented in ThirdPartyRepositoryImpl. So don't beat around bush finding it's implementation
	 * 
	 * @param thirdPartyNumber
	 * @param thirdPartyName
	 * @return
	 */
	PageResult<ThirdParty> findThirdPartyByNumberOrName(ThirdPartySearch thirdPartySearch, ListCriteria listCriteria);
	
	/**
	 * @param dealerNumber
	 * @return User object with given userName or null if not found
	 */
	ServiceProvider findDealerByNumber(String dealerNumber);
	
	
	/**
	 * @param serviceProviderNumber
	 * @return
	 */
	ServiceProvider findServiceProviderByNumber(String serviceProviderNumber);

	/**
	 * @param roleName
	 * @return Set of users who have been assigned to role roleName
	 */
	Set<User> findUsersBelongingToRole(String roleName);

	/**
	 * @return Set of all dealers
	 */
	Set<User> findAllDealers();

	/**
	 * @return List of all dealers based on BU
	 */
	List<ServiceProvider> findAllDealersByBUName(String businessUnitName);

	
	/**
	 * @return List of all dealers based on BU except the specified family code
	 */
	List<ServiceProvider> findAllOtherDealersByBUName(String businessUnitName, String dealerFamilyCode);

	/**
	 * @return List of all dealers zip codes based on BU
	 */
	Set<String> findAllDealersZipCodesByBUName(String businessUnitName);

	/**
	 * @return Set of all processors
	 */
	Set<User> findAllProcessors();
	
	/**
	 * @return Set of all AssignToUsers
	 */
	Set<User> findAllAssignToUsers();

	/**
	 *
	 * @return Set of all Users
	 */
	Set<User> findAllUsers();

	/**
	 *
	 * @return Set of all Groups
	 */
	Set<UserGroup> findAllUserGroups();

	List<Supplier> findAllSuppliers();

	/**
	 * This method gets the org model as a collection. Org model includes all
	 * users, user groups, groups attributes, users attributes. <br/> It must be
	 * noted that the return type is a general collection and each element could
	 * be user/usergroup/attribute <br/> This is a costly API, since it fetches
	 * all entities <b>in memory</b>
	 *
	 * @return a collection containing all users, user groups, groups
	 *         attributes, users.
	 */
	Collection getOrgModel();

	Party getPartyById(Long id);

	Organization findOrganizationByName(String name);

	public Role findRoleByName(String roleName);

	public List<String> findUsersWithNameLike(String name, int pageNumber,
			int pageSize, String currentBusinessUnit);

	public List<String> findUsersWithNameLikeOfType(String name, String userType);

	public boolean isDealer(User user);
	public boolean isServiceProvider(User user);

	/**
	 * This method will return true or false depending on whether the current logged in user belongs to a
	 * Third Party organization or not.
	 * 
	 * @param user
	 * @return
	 */
	public boolean isThirdParty(User user);

	public boolean isInspector(User user);

	public boolean isReceiver(User user);

	public boolean isInternalUser(final User user);

	public List<ServiceProvider> findDealersByNumber(String dealerNumber);

	public List<ServiceProvider> findDealersByName(String dealerName);

	public PageResult<Party> findAllSuppliers(ListCriteria listCriteria);

	public List<Party> findByIds(Collection<Long> collectionOfIds);

	@Transactional(readOnly = false)
	public void updateOrganization(Organization organization);

	public List<OrganizationAddress> getAddressesForOrganization(
			Organization organization);

	public OrganizationAddress getPrimaryOrganizationAddressForOrganization(
			Organization organization);
	/**
	 * This method is to do lookup for a dealer by name or number. If both are given, it will be an AND condition.
	 * @param dealerNumber
	 * @param dealerName
	 * @return
	 */
	public List<ServiceProvider> findDealersByNumberOrName(String dealerNumber,String dealerName);
	public List<ServiceProvider> findDealersByFamily(String dealerFamilyCode);

	public boolean isUserHasRole(User loggedInUser, String roleName);

	public Set<String> findAllZipCodesForAFamily(String dealerFamilyCode);
	public ServiceProvider findDealersByNumberWithoutLike(final String dealerNumber);

	public List<String> findRoleNamesStartingWith(String name, int pageNumber, int pageSize);

	public boolean doesUserHaveRole(User user, String roleName);


	List<String> findDealerNumbersStartingWith(String dealerNumber, int pageNumber, int pageSize);

    public User findUserByNameForLogin(String userName);

    public Organization findOrgById(Long id);
    
    public ServiceProvider findServiceProviderById(Long id);
    
    public Boolean isThirdPartyDealerWithLogin(Long id);

	public PageResult<User> findProcessors(ListCriteria criteria, String role);

	public User findDefaultUserBelongingToRole(String userRole);

	public List<User> findAllAvailableProcessors();
	
	public List<User> findAllAvailableRecoveryProcessors();

	public PageResult<OrganizationAddress> getAddressesForOrganization(ListCriteria criteria, Organization belongsToOrganization);

	@Transactional(readOnly = false)
	void createOrgAddressForDealer(OrganizationAddress userOrgAddress, Long id);
	
	@Transactional(readOnly = false)
	void createOrgAddressForDealer(OrganizationAddress userOrgAddress, ServiceProvider dealer);
	
	@Transactional(readOnly = false)
	void createAddressForDealer(Address userAddress, ServiceProvider dealer);
	
	public SourceSystemBuMapping findBySourceSystem(final String sourceSystem);

	@Transactional(readOnly = false)
	void updateUserAvailability(Long id, String selectedBusinessUnit, String role, Boolean available, Boolean defaultUserForRole);
	
    public List<User> findInternalUsersWithNameLike(String name);
    
    public List<User> findInternalUsersWithNameLike(String name,int pageNumber, int pageSize);
    
    public List<BusinessUnit> findAllBusinessUnitsForUser(final User name);

    public User findFOCUserInServiceProviderOrganization(final String serviceProviderNo);
    
    public Set<Role> getRolesForUser(final User user);
    
    public Set<Role> getRolesByType(final RoleType roleType);
    
    public List<User> findUsersWithRoleInServiceProviderOrganization(final String serviceProviderNo,final String role);

   public List<User> findDealerUsers(ServiceProvider serviceProvider,List<String> listOfRoles,String partialLoginId);
   
   public List<Currency> listUniqueCurrencies();

   public User findDefaultUserBelongingToRoleForSelectedBU(final String businessUnit, final String userRole);
   
   public void updateOrganizationAddress(OrganizationAddress orgAddress);
   
   public void updateAddress(Address address);
   
   public ServiceProvider findServiceProviderByNumberWithOutBU(final String serviceProviderNumber);

   public OrganizationAddress getAddressesForOrganizationBySiteNumber(
			final Organization organization, final String siteNumber);
   
   public OrganizationAddress getOrganizationAddressBySiteNumber(final String siteNumber);
   
   public OrganizationAddress getOrgAddressBySiteNumberForUpload(final String siteNumber, final Long orgId);

   List<ServiceProvider> findCertifiedDealersWhoseNameStartsWith(
			String name, int pageNumber, int pageSize);
   
   public List<User> findAllUsersByRole(List<String> listOfRoles);
   
   public User findInternalUser(String loginId, String userType);
   
   public User findSystemUserByName();
      
   public List<User> findUsersWithLoginIds(List<String> loginIds);

   public List<ServiceProvider> findDealersByIds(List<Long> dealerIds);
   /*   public PageResult<Party> findNACustomersForNACompanyNameLikeAndTypeFromAddressBook(
			String name,AddressBookType type,
			ListCriteria listCriteria);*/
    public PageResult<ServiceProvider> findAllNationalAccounts(
			String nationalAccountName,ListCriteria listCriteria);

    //TODO Need to refactor following methods

    public List<User> findUsersBelongingToRoleAndOrgForDisplay(Organization organization, String roleName);

    public List<User> findUsersListBelongingToRoleForDisplay(String roleName);

    public List<User> findUsersLikeBelongingToRoles(String name, List<RoleType> roleTypes);

    public List<User> findUsersWithFullNameLike(String fullNamePrefix, int pageNumber, int pageSize);

    public List<User> findUsersWithNameLike(final String name ,final String type);

    public List<User> findUsersWithNameLike(String name);

    public List<User> findUsersForDealerWithNameLike(final Long dealerId, final String name);

	public MarketingGroup findMarketingGroupByCode(String mktGrpCode);
	
	public Brand findBrandByCode(String brandCode);
	
	public Dealership findDealerDetailsByNumber(final String dealerNumber);
	
	public void removeExistingAddressesForOrganization(final String siteNumber);
	
	public String findDealerBrands(Organization organization);

    public String getCevaProcessor(Object claim);

    public ServiceProvider findDealerByServiceProviderID(String dealerId);

    @Transactional(readOnly = false)
    public void saveOrUpdate(User user);

    public Map<Long, String> getTechniciansForDealer(Organization organization, String businessUnitName);

    public List<Organization> getChildOrganizations(final Long orgId);

    public List<Long> getChildOrganizationsIds(final Long orgId);

    public List<Long> getParentOrganizationsIds(final Long orgId);

    public Address findAddressWithMandatoryFields(String addressLine1,String country, String city, String state, String zipCode);
    	
    public List<ServiceProvider> findAllServiceProviders();

	List<Dealership> findDealershipsFromDealerCode(String dealerCode);
    
    public List<User> findAllAvailableFleetProcessors();

    public List<User> getDealersFromDealerShip(List<Long> dealerShips);

    public List<User> getCustomers(List<Long> customerIds);

    public Long checkLoggedInDealerForDualBrand(Long id);
    
   public String findMarketingGroupCodeBrandByDealership(Dealership dealer);

   public String getLoggedInUsersCountry(Long id);

   public List<User> findFleetCustomerUsers(Organization organization,List<Role> listOfRoles,String partialLoginId);
   
   public Set<User> findAllInternalUsers();
 
   public List<User> findAvailableUsersByRoleAndPermission(final String roleName, final Long orgId, final String permissionString);

   List<ServiceProvider> findDealersListByNumber(String dealerNumber);
   
   public PageResult<Party> findDealersByOrganizations(ListCriteria listCriteria, List<Organization> organizations);
    
   public List<User> findUsersByRoleAndPermission(final String roleName, final Long orgId, final String permissionString);
   
   public List<User> findAllUsersBelongingToServiceProvider(final String serviceProviderNo);
   
   public Set<Role> getRolesByTypeAndCategory(final RoleType roleType, final RoleCategory roleCategory);
   
   public List<User> findFleetDealerUsersWithNameLike(final Long dealerId, final String name,String userType);
   
   public List<User> findCustomerUsers(List<Organization> orgs,String partialLoginId);
   
   public List<User> findfleetCustomerUsersBasedOnOrganization(Organization organization,String partialLoginName);
   
   public List<User> findDealerUsersBasedOnOrganizations( List<Long> orgIds, String eventName);
   
   public Set<Role> getFleetRolesByTypeAndCategory( List<RoleType> roleTypes, RoleCategory eventName);

   public List<User> findAllUsersSubscribedForEvent(Set<User> actorList, String eventName);

   List<BigDecimal> findServiceProviderIds(String dealerCode, String businessUnit);
   
   public ServiceProvider findServiceProviderByNumberAndBusinessUnit(String serviceProviderNumber, String businessUnit);
}

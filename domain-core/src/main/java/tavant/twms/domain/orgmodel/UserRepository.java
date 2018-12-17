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

import java.util.List;
import java.util.Map;
import java.util.Set;

import tavant.twms.infra.ListCriteria;
import tavant.twms.infra.PageResult;
import tavant.twms.domain.bu.BusinessUnit;

public interface UserRepository {

    /**
     * @param user
     */
    public void save(User user);

    /**
     *
     * @param user
     */
    public void update(User user);

    /**
        *
        * @param users
        */
    public void updateAll(List<User> users);

    /**
     * @param id
     * @return User object with given id or null if not found
     */
    User findById(Long id);

    /**
     * @param userId
     * @return User object with given user id or null if not found
     */
    User findByUserId(String userId);


    /**
     * @param userName
     * @return User object with given userName or null if not found
     */
    User findByName(String userName);

    /**
     * @param roleName
     * @return Set of users who have been assigned to role roleName
     */
    Set<User> findUsersBelongingToRole(String roleName);

    /**
     *
     * @return Set of all Users
     */
    Set<User> findAllUsers();

    /**
     *
     * @return Set of all Dealers
     */
    Set<User> findAllDealers();

    /**
     *
     * @return Set of Dealers matching a specific criteria.
     */

    Set<User> findAllDealersLike(String name);

    /**
     *
     * @return Set of all Processors
     */
    Set<User> findAllProcessors();
    
    /**
    *
    * @return Set of all assign to users
    */
    Set<User> findAllAssignToUsers();

    public List<Supplier> findAllSuppliers();

    public List<Supplier> findAllSuppliers(String name);

    public List<Supplier> findSuppliersWithNameLike(String name, int pageNumber, int pageSize);

    public List<String> findUsersWithNameLike(String name, int pageNumber, int pageSize, String currentBusinessUnit);
    
    public List<String> findUsersWithNameLikeOfType(String name, String userType);

    public boolean isInternalUser(final User user);

    public Map<Long, String> findTechnicianForDealer(final Long dealerId,final String businessUnit);
    
    public List<User> findSalesPersonForDealer(final Long dealerId);

    public List<User> findAssociatedUsersForDealers(final Set<Long> dealerIds, final String associatedUserType);

	public boolean isUserHasRole(User loggedInUser, String roleName);
    
    public PageResult<User> findAllProcessors(ListCriteria processorCriteria, String role);

	public User findDefaultUserBelongingToRole(String userRole);

	public List<User> findAllAvailableProcessors();
	
	public List<User> findAllAvailableRecoveryProcessors(); 
    
    public boolean isThirdPartyDealerWithLogin(final Long id);

    public List<User> findInternalUsersWithNameLike(final String name);
    
    public List<User> findInternalUsersWithNameLike(String name,int pageNumber, int pageSize);
    
    public List<BusinessUnit> findAllBusinessUnitsForUser(final User name);
    public List<User> findProcessorUsersWithNameLike(final String roleName,final String name);
    
    public User findFOCUserInServiceProviderOrganization(final String serviceProviderNo);
    
    public Set<Role> getRolesForUser(final User user);
    
    public List<User> findAllUsersByRole(List<String> listOfRoles);
    
    public List<User> findUsersWithRoleInServiceProviderOrganization(final String serviceProviderNo,final String role);

    public List<User> findDealerUsers(ServiceProvider serviceProvider,List<String> listOfRoles,String partialLoginId);
    
    public User findDefaultUserBelongingToRoleForSelectedBU(final String businessUnit, final String userRole);
    
    public User findTechnicianForDealerByLoginName(final Long dealerId, final String loginName);
    
    public User findInternalUser(String loginId,String userType);
    
    public Set<Role> getRolesByType(final RoleType roleType);

    public User findSystemUserByName();
	    
    public List<User> findUsersWithLoginIds(List<String> loginIds);

    public List<User> findUsersBelongingToRoleAndOrgForDisplay(final Organization organization, final String roleName);

    public List<User> findUsersListBelongingToRoleForDisplay(String roleName);

    public List<User> findUsersWithFullNameLike(String fullNamePrefix, int pageNumber, int pageSize);

    public List<User> findUsersLikeBelongingToRoles(String name, List<RoleType> roleTypes);

    public List<User> findUsersWithNameLike(final String name ,final String type);

    public List<User> findUsersWithNameLike(final String name);

    public List<User> findUsersForDealerWithNameLike(final Long dealerId, final String name);
    
    public Address fetchLoggedInUserAddress(User user);

    public void saveOrUpdate(User user);

	public List<Dealership> findAllDealersFromDealerCode(String dealerCode);
	
    public List<User> findAllAvailableFleetProcessors();
    
    public List<User> findAllParticipantsForEquipmentTransfer();

	public String getLoggedInUsersCountry(Long id);
	
	public Set<User> findAllInternalUsers();
    
	public List<User> findFleetCustomerUsers(Organization serviceProvider,List<Role> listOfRoles,String partialLoginId);

    public List<User> findAvailableUsersByRoleAndPermission(final String roleName, final Long orgId, final String permissionString);
    
    public List<User> findUsersByRoleAndPermission(final String roleName, final Long orgId, final String permissionString); 
    
    public List<User> findUsersForDealerOwnedWithNameLike(Long orgId, String name);
    
    public List<User> findAllUsersBelongingToServiceProvider(final String serviceProviderNo);
    
    public Set<Role> getRolesByTypeAndCategory(final RoleType roleType, final RoleCategory roleCategory);
    
    public List<User> findFleetDealerUsersWithNameLike( Long dealerId,  String name, String userType) ;
    
    public List<User> findCustomerUsers(List<Organization> orgs,String partialLoginId);

    public List<User> findfleetCustomerUsersBasedOnOrganization(Organization organization,String partialLoginName);
    
    public List<User> findDealerUsersBasedOnOrganizations( List<Long> orgIds, String eventName) ;
    
    public Set<Role> getFleetRolesByTypeAndCategory(List<RoleType> roleTypes,  RoleCategory roleCategory);

    public List<User> findAllUsersSubscribedForEvent(Set<User> actorList, String eventName);
 
}
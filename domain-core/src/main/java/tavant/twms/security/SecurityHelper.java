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
package tavant.twms.security;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;

import org.acegisecurity.Authentication;
import org.acegisecurity.GrantedAuthority;
import org.acegisecurity.GrantedAuthorityImpl;
import org.acegisecurity.context.SecurityContext;
import org.acegisecurity.context.SecurityContextHolder;
import org.acegisecurity.context.SecurityContextImpl;
import org.acegisecurity.providers.UsernamePasswordAuthenticationToken;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import tavant.twms.domain.bu.BusinessUnit;

import tavant.twms.domain.orgmodel.Organization;
import tavant.twms.domain.orgmodel.OrganizationRepository;
import tavant.twms.domain.orgmodel.Role;
import tavant.twms.domain.orgmodel.User;
import tavant.twms.domain.orgmodel.UserRepository;
import tavant.twms.security.model.OrgAwareUserDetails;

public class SecurityHelper implements ApplicationContextAware{

    public static final String SYSTEM_USER_NAME = "system";

    private ApplicationContext applicationContext;

    private UserRepository userRepository;
    
    private OrganizationRepository organizationRepository;
    
    private Organization oemOrg;

    /**
     * Returns the currently logged in Org user.
     *
     * @throws IllegalStateException
     *             If there is no valid authentication present.
     * @return the currently logged in Org user.
     */
    public User getLoggedInUser() {
        OrgAwareUserDetails userDetails = getAuthenticatedUserDetails();
        return userDetails.getOrgUser();
    }

    public BusinessUnit getDefaultBusinessUnit() {
        OrgAwareUserDetails userDetails = getAuthenticatedUserDetails();
        return userDetails.getDefaultBusinessUnit();
    }
    public BusinessUnit getCurrentBusinessUnit() {
		OrgAwareUserDetails userDetails = getAuthenticatedUserDetails();
		return userDetails.getCurrentBusinessUnit();
	}

    public String getWarrantyAdminBusinessUnit() {
        OrgAwareUserDetails userDetails = getAuthenticatedUserDetails();
        return userDetails.getWarrantyAdminSelectedBusinessUnit();
    }
    
    public String getContractAdminBusinessUnit() {
        OrgAwareUserDetails userDetails = getAuthenticatedUserDetails();
        return userDetails.getContractAdminSelectedBusinessUnit();
    }

	private OrgAwareUserDetails getAuthenticatedUserDetails() {
		final Authentication authentication = SecurityContextHolder.getContext()
                .getAuthentication();

        if (authentication == null) {
            throw new IllegalStateException("Illegal attempt to retrieve the"
                    + " logged-in user in an unauthenticated context!");
        }

        return (OrgAwareUserDetails) authentication.getPrincipal();
	}

    @SuppressWarnings("unchecked")
    public void authenticateUser(Long userId, String userName, String password, String[] roles, Locale locale) {

        // Simulate a successful user authentication.

        User orgUser = new User();
        orgUser.setName(userName);
        orgUser.setPassword(password);
        orgUser.setId(userId);
        orgUser.setLocale(locale);

        BusinessUnit buUS = new BusinessUnit();
        buUS.setName("AMER");
        buUS.setDisplayName("AMER");
        BusinessUnit buEMEA = new BusinessUnit();
        buEMEA.setName("EMEA");
        buEMEA.setDisplayName("EMEA");

        TreeSet<BusinessUnit> businessUnits = new TreeSet<BusinessUnit>();
        businessUnits.add(buUS);
        businessUnits.add(buEMEA);
        orgUser.setBusinessUnits(businessUnits);
        Set<Role> userRoles;

        if (roles == null) {
            userRoles = Collections.EMPTY_SET;
        } else {
            userRoles = new HashSet<Role>(roles.length);
            for (String roleName : roles) {
                Role role = new Role();
                role.setName(roleName);
                userRoles.add(role);
            }
        }
        orgUser.setRoles(userRoles);
        
        List<Organization> organizationList = new ArrayList<Organization>();
        Organization organization = new Organization();
        organization.setId(1L);
        organizationList.add(organization);
        orgUser.setBelongsToOrganizations(organizationList);
        orgUser.setUserType("INTERNAL");

        authenticateUser(orgUser, password);
    }

    private void authenticateUser(User orgUser, String password) {
        GrantedAuthority[] authorities;
        Set<Role> roles = orgUser.getRoles();

        if (roles == null || roles.size() == 0) {
            authorities = new GrantedAuthority[0];
        } else {
            authorities = new GrantedAuthority[roles.size()];
            int i = 0;
            for (Role role : roles) {
                authorities[i++] = new GrantedAuthorityImpl(role.getName());
            }
        }

        OrgAwareUserDetails orgUserDetails = new OrgAwareUserDetails(orgUser);

        /*
         * Note : We *have* to use this three-argument version of the
         * constructor to set the authentication token as "trusted", there by
         * preventing Acegi from attempting to actually authenticate it.
         */
        final UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                orgUserDetails, password, authorities);

        SecurityContext securityContext = new SecurityContextImpl();
        securityContext.setAuthentication(authenticationToken);
        SecurityContextHolder.setContext(securityContext);
    }

    /*
     * This method allows incoming WS call to land here and fake authentication.
     * Code copied from AbstractRepositoryTestCase.
     */
    public void populateSystemUser() {
    	SelectedBusinessUnitsHolder.clearChosenBusinessUnitFilter();
        String[] roles = new String[] {SYSTEM_USER_NAME};
        authenticateUser(56L, SYSTEM_USER_NAME, SYSTEM_USER_NAME, roles, Locale.US);
    }

    public void doDefaultAuthentication() {
        authenticateUser(1L, "bishop", "tavant", new String[]{"dealer"}, Locale.US);
    }

    public boolean isSystemUserLoggedIn() {
        return isSystemSecurityContext(SecurityContextHolder.getContext());
    }

    public boolean isSystemSecurityContext(SecurityContext context) {
        Authentication currentUser = context.getAuthentication();
        return (currentUser != null) &&
            SYSTEM_USER_NAME.equals(currentUser.getName());
    }

    public void populateIntegrationUser() {
        String[] roles = new String[] {SYSTEM_USER_NAME};
        authenticateUser(10640L, "integration", "tavant", roles, Locale.US);
    }
    
    public void authenticateUser(User user) {
        authenticateUser(user, user.getPassword());
    }
    
    public void populateFocUser() {
    }

	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	public UserRepository getUserRepository() {
        return (UserRepository) this.applicationContext.getBean("userRepository");
	}

	public OrganizationRepository getOrganizationRepository() {
        return (OrganizationRepository) this.applicationContext.getBean("organizationRepository");
	}

    public Organization getOEMOrganization() {
        if (oemOrg == null)
            oemOrg = getOrganizationRepository().findByOrganizationName("OEM");
        return oemOrg;
    }
}

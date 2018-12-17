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
package tavant.twms.security.authz.infra;

import java.util.Collections;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import org.acegisecurity.Authentication;
import org.acegisecurity.GrantedAuthority;
import org.acegisecurity.GrantedAuthorityImpl;
import org.acegisecurity.context.SecurityContext;
import org.acegisecurity.context.SecurityContextHolder;
import org.acegisecurity.context.SecurityContextImpl;
import org.acegisecurity.providers.UsernamePasswordAuthenticationToken;

import tavant.twms.domain.bu.BusinessUnit;
import tavant.twms.domain.orgmodel.Role;
import tavant.twms.domain.orgmodel.User;
import tavant.twms.security.model.OrgAwareUserDetails;


public class SecurityHelper {

    public static final String SYSTEM_USER_NAME = "system";
    
    

    /**
     * Returns the currently logged in Org user.
     *
     * @throws IllegalStateException
     *             If there is no valid authentication present.
     * @return the currently logged in Org user.
     */
    public User getLoggedInUser() {
        final Authentication authentication = SecurityContextHolder.getContext()
                .getAuthentication();

        if (authentication == null) {
            throw new IllegalStateException("Illegal attempt to retrieve the"
                    + " logged-in user in an unauthenticated context!");
        }

        OrgAwareUserDetails userDetails = (OrgAwareUserDetails) authentication.getPrincipal();

        return userDetails.getOrgUser();
    }

    /**
     * Simulate user authentication. Used by test cases as well as non-HTTP flows in the app (such as quartz tasks etc.)
     * @param userId User Id
     * @param userName User Name
     * @param roles Roles
     * @param password Password
     * @param locale Locale
     */
    @SuppressWarnings("unchecked")
    public void populateTestUserCredentials(Long userId, String userName, String password,
            String[] roles, Locale locale) {

        User orgUser = new User();
        orgUser.setName(userName);
        orgUser.setPassword(password);
        orgUser.setId(userId);
        orgUser.setLocale(locale);

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

        populateTestUserCredentials(orgUser);
    }

    /**
     * Simulate user authentication. Used by test cases as well as non-HTTP flows in the app (such as quartz tasks etc.)
     * @param orgUser The Org User used for simulating the authentication.
     */
    public void populateTestUserCredentials(User orgUser) {

        OrgAwareUserDetails orgUserDetails = new OrgAwareUserDetails(orgUser);

        Set<Role> roles = orgUser.getRoles();

        GrantedAuthority[] authorities = new GrantedAuthority[roles.size()];

        int i = 0;

        for (Role role : roles) {
            authorities[i++] = new GrantedAuthorityImpl(role.getName());
        }

        /*
         * Note : We *have* to use this three-argument version of the constructor since only this constructor implicitly
         * sets the authentication token as "trusted", there by preventing Acegi from attempting to actually
         * authenticate it.
         */
        final UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                orgUserDetails, orgUser.getPassword(), authorities);

        SecurityContext securityContext = new SecurityContextImpl();
        securityContext.setAuthentication(authenticationToken);
        SecurityContextHolder.setContext(securityContext);
    }

    /*
     * This method allows incoming WS call to land here and fake authentication.
     * Code copied from AbstractRepositoryTestCase.
     */
    public void populateSystemUser() {
        Long userId = 56L;
        String userName = SYSTEM_USER_NAME;
        String password = SYSTEM_USER_NAME;
        String[] roles = new String[] {SYSTEM_USER_NAME};
        populateTestUserCredentials(userId, userName, password, roles, Locale.US);
    }

    public void doDefaultAuthentication() {
        populateTestUserCredentials(1L, "bishop", "tavant", new String[] { "dealer" }, Locale.US);
    }
    
    public boolean isSystemUserLoggedIn() {
        return isSystemSecurityContext(SecurityContextHolder.getContext());
    }

    public boolean isSystemSecurityContext(SecurityContext context) {
        Authentication currentUser = context.getAuthentication();
        return (currentUser != null) && SYSTEM_USER_NAME.equals(currentUser.getName());
    }
    
}

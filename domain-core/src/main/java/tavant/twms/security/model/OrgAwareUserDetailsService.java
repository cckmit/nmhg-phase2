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
package tavant.twms.security.model;

import org.acegisecurity.providers.AuthenticationProvider;
import org.acegisecurity.userdetails.UserDetails;
import org.acegisecurity.userdetails.UserDetailsService;
import org.acegisecurity.userdetails.UsernameNotFoundException;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;

import tavant.twms.domain.orgmodel.OrgService;
import tavant.twms.domain.orgmodel.User;

/**
 * An Org Model - aware implementation of the Acegi {@link UserDetailsService} interface. 
 * This class is consulted by the Acegi {@link AuthenticationProvider} while performing
 * user authentication.
 * 
 * @author vikas.sasidharan
 *
 */
public class OrgAwareUserDetailsService implements UserDetailsService {

    private OrgService orgService;
    private Logger logger = Logger.getLogger(OrgAwareUserDetailsService.class);
    
    public void setOrgService(OrgService orgService) {
        this.orgService = orgService;
    }

    /**
     * Implements {@link UserDetailsService#loadUserByUsername(String)}.
     */
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException,
            DataAccessException {
        
        User orgUser = orgService.findUserByNameForLogin(userName);
        
        OrgAwareUserDetails userDetails = null;
        if(orgUser != null)
        	userDetails = new OrgAwareUserDetails(orgUser);
        	
        if (orgUser == null || !orgUser.belongsToActiveOrganization()) {
            throw new UsernameNotFoundException("User [" + userName + "] not found.");
        }
        
        if(logger.isDebugEnabled()) {
            logger.debug("Fetched Org User : [" + orgUser +"].");
        }

        return userDetails;
    }
}
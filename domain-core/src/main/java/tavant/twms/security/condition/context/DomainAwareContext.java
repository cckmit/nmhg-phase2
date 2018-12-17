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

/**
 * User: <a href="mailto:vikas.sasidharan@tavant.com>Vikas Sasidharan</a>
 * Date: Jun 5, 2007
 * Time: 7:24:08 PM
 */

package tavant.twms.security.condition.context;

import tavant.twms.domain.orgmodel.User;
import tavant.twms.domain.orgmodel.Role;
import tavant.twms.security.SecurityHelper;

import java.util.HashMap;
import java.util.Set;

public class DomainAwareContext {

    private SecurityHelper securityHelper;

    public void setSecurityHelper(SecurityHelper securityHelper) {
        this.securityHelper = securityHelper;
    }

    public User getCurrentUser() {
        return securityHelper.getLoggedInUser();
    }

    public boolean getCurrentUserJustADealer() {
        User user = getCurrentUser();
        Set<Role> roles = user.getRoles();
        if(roles.size() > 1){
        	return false;
        }
		for(Role role : roles) {
            if("dealer".equals(role.getName())) {
                return true;
            }
        }

        return false;
    }
}

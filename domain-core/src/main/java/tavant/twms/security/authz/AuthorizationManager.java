/*
 *   Copyright (c)2007 Tavant Technologies
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
package tavant.twms.security.authz;



import java.util.List;

import tavant.twms.domain.orgmodel.User;

/**
* Service exposed to presentation tier for authorization 
* purposes   
*/
public interface AuthorizationManager {
	/**
	 * API exposed to UI layer to fetch 
	 * the resources accessible by a User
	 * 
	 * @param userProfile
	 * @return UserResource
	 */
	public List<String> getResources(User userProfile); 
	
	/**
	 * API which is invoked by Acegi framework
	 * 
	 * @param userProfile
	 * @param securedObject
	 * @return boolean
	 */
	public boolean isPermissionGranted(User userProfile,Object securedObject);

    /**
     * does the instance based authorization
     * @param userProfile of user logged in
     * @param resourceName to be protected
     * @param businessId for instance based authorization
     * @return permission true||false
     */
    public boolean isPermitted(User userProfile, String resourceName, Object businessId);
}

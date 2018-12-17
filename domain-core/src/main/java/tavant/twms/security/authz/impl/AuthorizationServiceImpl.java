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
package tavant.twms.security.authz.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

import org.apache.log4j.Logger;

import tavant.twms.domain.admin.Permission;
import tavant.twms.domain.orgmodel.Role;
import tavant.twms.domain.orgmodel.User;
import tavant.twms.security.authz.AuthorizationService;
import tavant.twms.security.authz.resource.ResourceManager;
import tavant.twms.security.vo.Resource;


/**
* Implementation class for Authorization service for a user
*
*/
public class AuthorizationServiceImpl implements AuthorizationService  {

    private static final Logger logger = Logger.getLogger(AuthorizationServiceImpl.class);
    
    private ResourceManager resourceManager;


	/**
     * Returns true if the User has permission
     * on the specified Resource else false
     *
     * @param user client
     * @param resource to be authorized
     * @return boolean
     */
    public boolean isAuthorized(User user, Resource resource, Object instanceData) {
/*        if(userProfile.isSuperuser()) {
            return true;
        }*/
        Collection<String> userPermissions = getUserPermissions(user);
        if (resource.getPrefix() != null) {
            return validateForPrefix(resource.getPrefix(), userPermissions);
        }
        Collection<String> resourcePermissions = resource.getPermissionList();
        // Iterate on Role Permission list and compare with
        // Resource permission list
        boolean isAuthzFlag;
        if (resource.getLogicalOR()) {
            isAuthzFlag = validateForOrOperation(resourcePermissions, userPermissions);
        } else {
            isAuthzFlag = validateForAndOperation(resourcePermissions, userPermissions);
        }
        if (isAuthzFlag ) {
            if (null != resource.getPolicyExecutor()) {
            	//If instance data is null then user can not be authorized so return false
                return instanceData != null && resource.getPolicyExecutor().execute(resource, user, instanceData);
            }
        }

        logger.debug("User " + user.getName() +  " does not have access to " + resource.getResourceName());
        return isAuthzFlag;
    }

    /**
     * performs 'OR' operator for the permission
     * @param resourcePermissions List of permissions required by the resource
     * @param userPermissions List of user permissions
     * @return true if user has any one of permissions required to access resource
     */
    private boolean validateForOrOperation(Collection<String> resourcePermissions,
                                           Collection<String> userPermissions) {
        for (String resourcePermission : resourcePermissions) {
            if (userPermissions.contains(resourcePermission)) {
                    return Boolean.TRUE;
            }
        }
     //   logger.error("User has permissions: " + userPermissions);
     //   logger.error("Resource permissions: " + resourcePermissions);
        String permString = "";
        for (String perm : resourcePermissions) {
        	permString = permString + ", " + perm;
        }
        logger.debug("User does not have any of the following permissions : " + permString);
        return Boolean.FALSE;
    }

    /**
     * performs 'AND' operator for the permission
     * @param resourcePermissions  List of permissions required by the resource
     * @param userPermissions List of user permissions
     * @return true if user has all permissions required to access resource
     */
    private boolean validateForAndOperation(Collection<String> resourcePermissions,
                                           Collection<String> userPermissions) {
        for (String resourcePermission : resourcePermissions) {
            if (!userPermissions.contains(resourcePermission)) {
            	logger.debug("User does not have following permission : " + resourcePermission);
                return Boolean.FALSE;
            }
        }
        return Boolean.TRUE;
    }

    /**
     * @param resourcePrefix for auth
     * @param permissionList for user
     * @return boolean for autborization
     */
    private boolean validateForPrefix(String resourcePrefix,
                                      Collection<String> permissionList) {
        for (String permissionDesc : permissionList) {
            if (permissionDesc.indexOf(resourcePrefix) != -1) {
                return Boolean.TRUE;
            }
        }
        return Boolean.FALSE;
    }

    /**
     * Helper method
     * @param user
     * @return Collection<String>
     */
    private Collection<String> getUserPermissions(User user) {
        Set<Role> rolePermissionMap = user.getRoles();        
        Collection<String> userPermissions = new ArrayList<String>();
        if(rolePermissionMap!=null){
	        for (Role role : rolePermissionMap) {
	        	for (Permission perm : role.getPermissions()) {
					userPermissions.add(perm.getPermissionString());
				}
			}
        }
        
        return userPermissions;
    }
    
    public boolean isAuthorized(User user, String functionalArea, String subjectArea) {
    	Resource resource = resourceManager.getResource(functionalArea, subjectArea);
    	return isAuthorized(user, resource, null);    	
	}
    
    public Resource getResource(String functionalArea, String subjectArea){
    	return resourceManager.getResource(functionalArea, subjectArea);
    }

    public boolean isHavingReadOnlyAccessToTwms(User user) {
        return isAuthorized(user, READ_ONLY_FUNCTIONAL_AREA, READ_ONLY_SUBJECT_AREA);
    }

    public void setResourceManager(ResourceManager resourceManager) {
		this.resourceManager = resourceManager;
	}
	
	
	
}

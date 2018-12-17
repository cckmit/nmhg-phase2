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
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.acegisecurity.vote.AccessDecisionVoter;

import tavant.twms.domain.orgmodel.User;
import tavant.twms.security.authz.AuthorizationManager;
import tavant.twms.security.authz.AuthorizationService;
import tavant.twms.security.authz.resource.ResourceManager;
import tavant.twms.security.context.ResourceContextBuilder;
import tavant.twms.security.vo.Resource;


/**
 * Class which interacts with resource manager to manage authorization for
 * users. Authorized resources are available with resource manager.
 * authorization manager fetches the authorized resources and determines against
 * user permissions which of the resources the user has access to.
 */
public class AuthorizationManagerImpl implements AuthorizationManager {
	private ResourceManager resourceManager;
	private AuthorizationService authorizationService;

	/**
	 * Method to fetch the list of resource {functional area@resource name}
	 * which the user is authorized to access.
	 * 
	 * @param userProfile
	 *            User Profile
	 * @return List of resources authorized for the user.
	 */
	@SuppressWarnings("unchecked")
	public List<String> getResources(User userProfile) {
		List<String> authorizedResources = new ArrayList<String>();
		// Fetch resources for all functional area from ResourceManager
		Map<String, Map<String, Resource>> fsResourceMap = resourceManager.getFunctionalAreaResourcesMap();
		Set<String> functionalAreaKeySet = fsResourceMap.keySet();
		for (String functionalArea : functionalAreaKeySet) {
			Map<String, Resource> resources = fsResourceMap.get(functionalArea);
			Set<String> resourcesKeySet = resources.keySet();
			// iterate on resources and find out if the user has required
			// permissions to access
			// the resource
			for (String resourceName : resourcesKeySet) {
				Resource resource = resources.get(resourceName);
				if (authorizationService.isAuthorized(userProfile, resource, null)) {
					authorizedResources.add(functionalArea + "@" + resource.getResourceName());
				}
			}
		}
		return authorizedResources;
	}

	public void setResourceManager(ResourceManager resourceManager) {
		this.resourceManager = resourceManager;
	}

	public void setAuthorizationService(AuthorizationService authorizationService) {
		this.authorizationService = authorizationService;
	}

	/**
	 * This method is invoked by {@linkplain AccessDecisionVoter}
	 * implementation.
	 * 
	 * @param userProfile
	 *            user
	 * @param securedObject
	 *            resources
	 * @return boolean
	 */
	public boolean isPermissionGranted(User userProfile, Object securedObject) {
		String resourceName = resourceManager.retrieveResourceName(securedObject);
		Resource resource = resourceManager.getResource(resourceName);
		if (resource == null)
		{
			throw new RuntimeException("Unable to get resource data for resource " + resourceName);
		}

		ResourceContextBuilder contextBuilder = resource.getContextBuilder();
		Object context = null;
		if (contextBuilder != null) {
			context = contextBuilder.createContext(resourceName, securedObject);
		}
		return authorizationService.isAuthorized(userProfile, resource, context);
	}

	/**
	 * Method to perform instance based authorization check. Instance data is
	 * passed as Object instance and is read accordingly in the custom policy
	 * executor. This method is called by authorization action (struts class)
	 * 
	 * @param userProfile
	 *            User profile
	 * @param resourceName
	 *            Name of the resource
	 * @param businessId
	 *            Object representing instance data or business data
	 * @return boolean flag indicating if user has access to resource or not
	 */
	public boolean isPermitted(User userProfile, String resourceName, Object businessId) {
		Resource resource = resourceManager.getResource(null, resourceName);
		return authorizationService.isAuthorized(userProfile, resource, businessId);
	}
}

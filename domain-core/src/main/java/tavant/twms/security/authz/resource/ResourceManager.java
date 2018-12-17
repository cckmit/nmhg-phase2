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

package tavant.twms.security.authz.resource;

import java.util.Map;
import java.util.Collection;

import tavant.twms.security.vo.Resource;;




public interface ResourceManager {
	/**
	 * Returns a resource of a functional area
	 * 
	 * @param functionalArea
	 * @param resourceName
	 * @return Resource
	 */
	public Resource getResource(String functionalArea, String resourceName);

	/**
	 * Returns a {@linkplain Resource}. This method be used if the functional Area is unknown.It
	 * queries all the {@linkplain FunctionalArea} and returns the appropriate
	 * {@linkplain Resource} with the give <code>name</code>.
	 * 
	 * @param resourceName - Name of the {@linkplain Resource} that should be queried.
	 * @return Resource - {@linkplain Resource} with the <code>name</code>
	 */
	public Resource getResource(String resourceName);

	/**
	 * Returns a Map of Resources under a functional area
	 * 
	 * @param functionalArea
	 * @return Map
	 */
	public Map getResourcesForFunctArea(String functionalArea);

	/**
	 * Returns a resource name
	 * 
	 * @param securedResource
	 * @return String
	 */
	public String retrieveResourceName(Object securedResource);

	/**
	 * Returns all resources under a functional area
	 * 
	 * @return Map<String, Map<String, Resource>>
	 */
	public Map<String, Map<String, Resource>> getFunctionalAreaResourcesMap();

	/**
	 * Returns a collection of resources of a resource type
	 * 
	 * @param resourceType
	 * @return Collection<Resource>
	 */
	public Collection<Resource> getResourcesForType(String resourceType);
}

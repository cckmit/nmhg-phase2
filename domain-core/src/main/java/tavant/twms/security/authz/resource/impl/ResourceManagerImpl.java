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
package tavant.twms.security.authz.resource.impl;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.acegisecurity.intercept.web.FilterInvocation;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.util.ClassUtils;

import tavant.twms.security.authz.resource.ResourceManager;
import tavant.twms.security.vo.Resource;


/**
* Class which holds all the resources configured in securityconfig/security-*.xml
*/
public class ResourceManagerImpl implements ResourceManager {
    // Map<Functional Area, Map<Resource Name, Resource>>
    private Map<String, Map<String, Resource>> functionalAreaResourcesMap;
    // Map<Resource, Functional Area>
    //The way this map is structured requires unique resource names to be used
    //across various functional areas. //todo this approach might be required to change in future
    private Map<String, String> resourceFunctionalArea;

    /**
     * Method to fetch the requested resource
     * @param functionalArea functional area
     * @param resourceName  name of the resource requested
     * @return Resource resource
     */
    public Resource getResource(String functionalArea, String resourceName) {
        if (functionalArea == null) {
            return getResource(resourceName);
        }
        Map resourceMap = functionalAreaResourcesMap.get(functionalArea);
        if (resourceMap != null && resourceMap.size() > 0) {
            return (Resource) resourceMap.get(resourceName);
        }
        return null;
    }

    /**
	 * Returns a {@linkplain Resource}. This method be used if the functional Area is unknown.It
	 * queries all the {@linkplain FunctionalArea} and returns the appropriate
	 * {@linkplain Resource} with the give <code>name</code>.
	 * 
	 * @param resourceName - Name of the {@linkplain Resource} that should be queried.
	 * @return Resource - {@linkplain Resource} with the <code>name</code>
	 */
    public Resource getResource(String resourceName) {
        Map resourceMap = functionalAreaResourcesMap.
                get(resourceFunctionalArea.get(resourceName));
        if (resourceMap != null && resourceMap.size() > 0) {
            return (Resource) resourceMap.get(resourceName);
        }
        return null;
    }

    /**
     * Method to fetch all the resources for the specified functional area.
     * @param functionalArea Name of the functionl area
     * @return Map<Resource Name, Resource>
     */
    public Map getResourcesForFunctArea(String functionalArea) {
        return functionalAreaResourcesMap.get(functionalArea);
    }

    /**
     * Setter method for injecting the resource
     * @param resources list for functional area and resources
     */
    @SuppressWarnings({"unchecked"})
    public void setResources(List<Map> resources) {
        this.functionalAreaResourcesMap = resources.get(0);
        this.resourceFunctionalArea = resources.get(1);
    }

    /**
     * Returns resource name for the secured object
     * @param securedResource resource
     * @return String representing resource name
     */
    public String retrieveResourceName(Object securedResource) {
       String resourceName;
        if (securedResource instanceof FilterInvocation) {
        	resourceName = createResourceName((FilterInvocation) securedResource);
        }else if (securedResource instanceof MethodInvocation) {
        	resourceName = createResourceName((MethodInvocation) securedResource);
        }else {
        	resourceName = securedResource.toString();
        }
      return resourceName;
    }

    /**
     * Method to fetch resources for all the functional areas
     * @return Map<Functional Area, Map<Resource Name, Resource>>
     */
    public Map<String, Map<String, Resource>> getFunctionalAreaResourcesMap() {
        return functionalAreaResourcesMap;
    }

    /**
     * Helper method to create a rsource name based on MethodInvocation (API)
     * @param invocation MethodInvocation
     * @return String
     */
	private String createResourceName(MethodInvocation invocation) {
	    Method method = invocation.getMethod();
	    StringBuffer sb = new StringBuffer(100);
	    String className = ClassUtils.getQualifiedName(method.getDeclaringClass());
	    sb.append(className).append(".").append(method.getName());
	   return sb.toString();
	}

	/**
     * Helper method to create a Resource Name
     * based on FilterInvocation (URL)
     *
	 * @param invocation FilterInvocation
	 * @return String
	 */
	private String createResourceName(FilterInvocation invocation) {
	   return invocation.getHttpRequest().getRequestURI();
	}

	/**
	 * Method which returns a collection of resource of the specified resource type
	 * of a particular type
	 * @param resourceType  Resource type {ui/url/menu/method}
	 * @return Collection of resource objects
	 */
	public Collection<Resource> getResourcesForType(String resourceType) {
	   Collection<Resource> resourcesColl = new ArrayList<Resource>();
	   // Fetch Resources for all functional areas
	   Map<String, Map<String, Resource>> functAreaResourcesMap = getFunctionalAreaResourcesMap();
	   Set<String> functAreaResourcesKeyset = functAreaResourcesMap.keySet();
	   // Iterate through all the Resources
	   for(String functAreaName:functAreaResourcesKeyset) {
		   Map<String, Resource> resourcesOfAllTypes = getResourcesForFunctArea(functAreaName);
		   Set<String> resourceKeyset = resourcesOfAllTypes.keySet();
		   for(String resourceName: resourceKeyset) {
			   Resource resource = resourcesOfAllTypes.get(resourceName);
			   // Add to List only those resources which are requested for
			   if(resourceType.equals(resource.getResourceType())) {
				   resourcesColl.add(resource);
			   }
		   }
	   }
	   return resourcesColl;
	}

}

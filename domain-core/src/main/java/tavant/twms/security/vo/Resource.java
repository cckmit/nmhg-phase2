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
package tavant.twms.security.vo;

import java.util.Collection;

import tavant.twms.security.context.ResourceContextBuilder;
import tavant.twms.security.policy.PolicyExecutor;


/**
* Resource value object to hold details about resource  
*  
*/
public class Resource {
    private String resourceName;
    private String resourceType;
    private String functionalArea;
    private String defaultPolicy;
    private String policy;
    private Collection<String> permissionList;
    private PolicyExecutor policyExecutor;
    private String prefix;
    private boolean logicalOR;
    private String context;
    private ResourceContextBuilder contextBuilder;
    

    public String getPrefix() {
        return prefix;
    }

    public String getResourceName() {
        return resourceName;
    }

    public String getResourceType() {
        return resourceType;
    }

    public String getFunctionalArea() {
        return functionalArea;
    }

    public String getDefaultPolicy() {
        return defaultPolicy;
    }

    public String getPolicy() {
        return policy;
    }

    public Collection<String> getPermissionList() {
        return permissionList;
    }

    public PolicyExecutor getPolicyExecutor() {
        return policyExecutor;
    }

    public boolean getLogicalOR() {
        return logicalOR;
    }

    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    public void setFunctionalArea(String functionalArea) {
        this.functionalArea = functionalArea;
    }

    public void setDefaultPolicy(String defaultPolicy) {
        this.defaultPolicy = defaultPolicy;
    }

    public void setPolicy(String policy) {
        this.policy = policy;
    }

    public void setPermissionList(Collection<String> permissionList) {
        this.permissionList = permissionList;
    }

    public void setPolicyExecutor(PolicyExecutor policyExecutor) {
        this.policyExecutor = policyExecutor;
    }

    public void setLogicalOR(boolean logicalOR) {
        this.logicalOR = logicalOR;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

	public String getContext() {
		return context;
	}

	public void setContext(String context) {
		this.context = context;
	}

	public ResourceContextBuilder getContextBuilder() {
		return contextBuilder;
	}

	public void setContextBuilder(ResourceContextBuilder contextBuilder) {
		this.contextBuilder = contextBuilder;
	}
}

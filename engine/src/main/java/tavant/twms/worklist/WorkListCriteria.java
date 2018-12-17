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
package tavant.twms.worklist;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.util.Assert;

import tavant.twms.domain.orgmodel.Organization;
import tavant.twms.domain.orgmodel.ServiceProvider;
import tavant.twms.domain.orgmodel.User;
import tavant.twms.infra.ListCriteria;

/**
 * Criteria for a work list ( or task list ). Tasks are meant for 'a user' and
 * are of 'different kind'. Each kind of task has a unique task name. 
 * 
 * @author amar.patel
 */
public class WorkListCriteria extends ListCriteria {
    private User user;
    
    private String taskName;
    
    private String process;
    
    private Organization serviceProvider ;
    private List<Organization> serviceProviderList;
    
    // TODO : Need a better name for this field
    private String identifier;

    List<User> userGroup = new ArrayList<User>();

    private Boolean displayNewClaimToAllProcessors = Boolean.FALSE;

    /**
     * @return the process
     */
    public String getProcess() {
        return process;
    }

    /**
     * @param process the process to set
     */
    public void setProcess(String process) {
        this.process = process;
    }

    public WorkListCriteria(User user) {
        Assert.notNull(user, "User cannot be null.");
        this.user = user;
        this.process = "ClaimSubmission";
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }
    
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
    
    public Organization getServiceProvider() {
    	return this.serviceProvider;
    }
    
    public void setServiceProvider(Organization serviceProvider) {
    	this.serviceProvider = serviceProvider;
    }
    
    public List<Organization> getServiceProviderList() {
    	return this.serviceProviderList;
    }
    
    public void setServiceProviderList(List<Organization> serviceProviderList) {
    	this.serviceProviderList = serviceProviderList;
    }
    
    public int getPageNumber() {
        return getPageSpecification().getPageNumber();
    }

    public void setPageNumber(int pageNumber) {
        getPageSpecification().setPageNumber(pageNumber);
    }

    public int getPageSize() {
        return getPageSpecification().getPageSize();
    }

    public void setPageSize(int pageSize) {
        getPageSpecification().setPageSize(pageSize);
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public List<User> getUserGroup() {
        return userGroup;
    }

    public void setUserGroup(List<User> userGroup) {
        this.userGroup = userGroup;
    }

    public Boolean getDisplayNewClaimToAllProcessors() {
        return displayNewClaimToAllProcessors;
    }

    public void setDisplayNewClaimToAllProcessors(Boolean displayNewClaimToAllProcessors) {
        this.displayNewClaimToAllProcessors = displayNewClaimToAllProcessors;
    }
}

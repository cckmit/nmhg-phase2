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

import java.util.Set;

import org.acegisecurity.GrantedAuthority;
import org.acegisecurity.GrantedAuthorityImpl;
import org.acegisecurity.userdetails.UserDetails;
import org.hibernate.collection.PersistentCollection;
import org.springframework.util.Assert;

import tavant.twms.domain.bu.BusinessUnit;
import tavant.twms.domain.login.LoginHistory;
import tavant.twms.domain.orgmodel.Role;
import tavant.twms.domain.orgmodel.ServiceProvider;
import tavant.twms.domain.orgmodel.User;
import tavant.twms.infra.InstanceOfUtil;

/**
 * An Org Model - aware implementation of the Acegi {@link UserDetails}
 * interface.
 * 
 * @author vikas.sasidharan
 * 
 */
@SuppressWarnings("serial")
public class OrgAwareUserDetails implements UserDetails {

	private User orgUser;
	private BusinessUnit defaultBusinessUnit;
	private LoginHistory loginHistory;

	private BusinessUnit currentBusinessUnit;
	private String warrantyAdminSelectedBusinessUnit;
	private GrantedAuthority[] roles;
	private String contractAdminSelectedBusinessUnit;
	
	public OrgAwareUserDetails() {
		// Required for mock tests to work.
	}

	public OrgAwareUserDetails(User orgUser) {

		Assert.notNull(orgUser, "Org User cannot be null !");
		this.orgUser = orgUser;

		if (this.orgUser.getBelongsToOrganizations() instanceof PersistentCollection) {
			((PersistentCollection)this.orgUser.getBelongsToOrganizations()).forceInitialization();
		}

		if(InstanceOfUtil.isInstanceOfClass(ServiceProvider.class, this.orgUser.getBelongsToOrganizations().get(0)) &&
				((ServiceProvider)this.orgUser.getBelongsToOrganizations().get(0)).getConsistsOf() instanceof PersistentCollection ) {
            ((ServiceProvider)this.orgUser.getBelongsToOrganizations().get(0)).setChildDealersIds();
        }

		orgUser.getBusinessUnits();
		Set<Role> userRoles = orgUser.getRoles();
		roles = new GrantedAuthority[userRoles.size()];

		int i = 0;
		for (Role userRole : userRoles) {
			roles[i++] = new GrantedAuthorityImpl(userRole.getName());
		}
	}

	public GrantedAuthority[] getAuthorities() {
		return roles;
	}

	public String getPassword() {
		return orgUser.getPassword();
	}

	public String getUsername() {
		return orgUser.getName();
	}

	public boolean isAccountNonExpired() {
		// No requirement for this yet.
		return true;
	}

	public boolean isAccountNonLocked() {
		// No requirement for this yet.
		return true;
	}

	public boolean isCredentialsNonExpired() {
		// No requirement for this yet.
		return true;
	}

	public boolean isEnabled() {
		// No requirement for this yet.
		return true;
	}

	public User getOrgUser() {
		return orgUser;
	}

	public BusinessUnit getCurrentBusinessUnit() {
		return currentBusinessUnit;
	}

	public BusinessUnit getDefaultBusinessUnit() {
		return defaultBusinessUnit;
	}

	public void setDefaultBusinessUnit(BusinessUnit defaultBusinessUnit) {
		this.defaultBusinessUnit = defaultBusinessUnit;
	}

	public String getWarrantyAdminSelectedBusinessUnit() {
		return warrantyAdminSelectedBusinessUnit;
	}

	public void setWarrantyAdminSelectedBusinessUnit(
			String warrantyAdminSelectedBusinessUnit) {
		this.warrantyAdminSelectedBusinessUnit = warrantyAdminSelectedBusinessUnit;
	}

	public void setCurrentBusinessUnit(BusinessUnit currentBusinessUnit) {
		this.currentBusinessUnit = currentBusinessUnit;
	}

	public LoginHistory getLoginHistory() {
		return loginHistory;
	}

	public void setLoginHistory(LoginHistory loginHistory) {
		this.loginHistory = loginHistory;
	}

	public String getContractAdminSelectedBusinessUnit() {
		return contractAdminSelectedBusinessUnit;
	}

	public void setContractAdminSelectedBusinessUnit(
			String contractAdminSelectedBusinessUnit) {
		this.contractAdminSelectedBusinessUnit = contractAdminSelectedBusinessUnit;
	}

}

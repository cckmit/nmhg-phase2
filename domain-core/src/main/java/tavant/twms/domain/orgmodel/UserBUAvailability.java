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

package tavant.twms.domain.orgmodel;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

import tavant.twms.domain.bu.BusinessUnitAware;
import tavant.twms.domain.bu.BusinessUnitInfo;

/**
 * @author jhulfikar.ali
 *
 */
@Entity
@Table(name = "user_bu_availability")
public class UserBUAvailability implements BusinessUnitAware {

	@Id
	@GeneratedValue(generator = "UserBUAvailability")
	@GenericGenerator(name = "UserBUAvailability", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "User_BU_Availability_SEQ"),
			@Parameter(name = "initial_value", value = "1000"),
			@Parameter(name = "increment_size", value = "20") })
	private Long id;
	
	@ManyToOne(fetch=FetchType.LAZY)
	private User orgUser;
	
	@Type(type="tavant.twms.domain.bu.BusinessUnitInfoType")
    @JsonIgnore
    private BusinessUnitInfo businessUnitInfo = new BusinessUnitInfo();;
    
	@Column
	private boolean available;

	@Column
	private boolean defaultToRole;
	
	@ManyToOne(fetch=FetchType.LAZY)
	private Role role;
	
	@Column
	private String maxNotifications;
	
	public User getOrgUser() {
		return orgUser;
	}

	public void setOrgUser(User orgUser) {
		this.orgUser = orgUser;
	}

	public BusinessUnitInfo getBusinessUnitInfo() {
		return businessUnitInfo;
	}

	public void setBusinessUnitInfo(BusinessUnitInfo businessUnitInfo) {
		this.businessUnitInfo = businessUnitInfo;
	}

	public boolean isAvailable() {
		return available;
	}

	public void setAvailable(boolean available) {
		this.available = available;
	}

	public Long getId() {
		return id;
	}

	public boolean isDefaultToRole() {
		return defaultToRole;
	}

	public void setDefaultToRole(boolean defaultToRole) {
		this.defaultToRole = defaultToRole;
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	public String getMaxNotifications() {
		return maxNotifications;
	}

	public void setMaxNotifications(String maxNotifications) {
		this.maxNotifications = maxNotifications;
	}
	
}

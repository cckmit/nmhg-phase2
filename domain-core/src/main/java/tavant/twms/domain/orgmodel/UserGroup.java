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
package tavant.twms.domain.orgmodel;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Set;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import org.hibernate.annotations.Filters;
import org.hibernate.annotations.Filter;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Version;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.springframework.core.style.ToStringCreator;

import tavant.twms.domain.common.AuditableColEntity;
import tavant.twms.security.AuditableColumns;

/**
 * @author vineeth.varghese
 * 
 */
@Entity
@Filters({
  @Filter(name="excludeInactive")
})
@SuppressWarnings("serial")
public class UserGroup implements Serializable, AuditableColumns{

	@Id
	@GeneratedValue(generator = "UserGroup")
	@GenericGenerator(name = "UserGroup", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "USER_GROUP_SEQ"),
			@Parameter(name = "initial_value", value = "1000"),
			@Parameter(name = "increment_size", value = "20") })
	private Long id;

	@Version
	private int version;

	private String name;

	@ManyToMany(mappedBy = "userGroups")
	private Set<User> users;

	@ManyToMany
	@JoinTable(name = "user_group_attr_vals", joinColumns = { @JoinColumn(name = "user_group_id") }, inverseJoinColumns = { @JoinColumn(name = "user_group_attr_val_id") })
	private Set<UserGroupAttributeValue> userGroupAttrVals;
	
	@Embedded
	@Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	private AuditableColEntity d = new AuditableColEntity();

	/**
	 * @return the userGroupAttrVals
	 */
	public Set<UserGroupAttributeValue> getUserGroupAttrVals() {
		return userGroupAttrVals;
	}

	/**
	 * @param userGroupAttrVals
	 *            the userGroupAttrVals to set
	 */
	public void setUserGroupAttrVals(
			Set<UserGroupAttributeValue> userGroupAttrVals) {
		this.userGroupAttrVals = userGroupAttrVals;
	}

	public boolean hasAttribute(String attributeName, String attributeValue) {
		for (Iterator<UserGroupAttributeValue> iter = userGroupAttrVals
				.iterator(); iter.hasNext();) {
			UserGroupAttributeValue userGroupAttributeValue = iter.next();
			if (userGroupAttributeValue.getAttribute().getName().equals(
					attributeName)
					&& (userGroupAttributeValue.getValue() != null && userGroupAttributeValue
							.getValue().equals(attributeValue))) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the users
	 */
	public Set<User> getUsers() {
		return users;
	}

	/**
	 * @param users
	 *            the users to set
	 */
	public void setUsers(Set<User> users) {
		this.users = users;
	}

	@Override
	public String toString() {
		return new ToStringCreator(this).append("id", id).append("name", name)
				.toString();
	}

	public AuditableColEntity getD() {
		return d;
	}

	public void setD(AuditableColEntity d) {
		this.d = d;
	}

}

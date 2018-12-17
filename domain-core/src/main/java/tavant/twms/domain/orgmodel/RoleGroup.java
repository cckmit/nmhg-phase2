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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Version;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import javax.validation.constraints.NotNull;

import tavant.twms.domain.common.AuditableColEntity;
import tavant.twms.domain.common.GroupHierarchyException;
import tavant.twms.domain.common.GroupInclusionException;
import tavant.twms.infra.TreeNode;
import tavant.twms.infra.TreeNodeInfo;
import tavant.twms.infra.TreeStructuredData;
import tavant.twms.security.AuditableColumns;

@Entity
@SuppressWarnings("serial")
@TreeStructuredData(parentProperty = "isPartOf")
public class RoleGroup implements TreeNode, Serializable, AuditableColumns {
	@Id
	@GeneratedValue(generator = "RoleGroup")
	@GenericGenerator(name = "RoleGroup", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "ROLE_GROUP_SEQ"),
			@Parameter(name = "initial_value", value = "1000"),
			@Parameter(name = "increment_size", value = "20") })
	private Long id;

	@Version
	private int version;

	@NotNull
	private String name;

	private String description;

	@ManyToOne(fetch = FetchType.LAZY)
	private RoleGroup isPartOf;

	@OneToMany(mappedBy = "isPartOf", fetch = FetchType.LAZY)
	@Cascade( { CascadeType.ALL })
	private Set<RoleGroup> consistsOf = new HashSet<RoleGroup>();

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "roles_in_group", joinColumns = { @JoinColumn(name = "role_group") }, inverseJoinColumns = { @JoinColumn(name = "role") })
	private List<Role> includedRoles = new ArrayList<Role>();

	@Embedded
	@AttributeOverrides( {
			@AttributeOverride(name = "lft", column = @Column(name = "lft", nullable = false)),
			@AttributeOverride(name = "rgt", column = @Column(name = "rgt", nullable = false)),
			@AttributeOverride(name = "treeId", column = @Column(name = "tree_id", nullable = false)),
			@AttributeOverride(name = "depth", column = @Column(name = "depth", nullable = false)) })
	private TreeNodeInfo nodeInfo = new TreeNodeInfo();

	@ManyToOne(fetch = FetchType.LAZY)
	private RoleScheme scheme;

	@Embedded
	@Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	private AuditableColEntity d = new AuditableColEntity();

	// private Object includedRole;

	public RoleGroup findTopMostParent() {
		if (this.isPartOf == null) {
			return this;
		}
		return this.isPartOf.findTopMostParent();
	}

	public void includeGroups(Set<RoleGroup> groups)
			throws GroupInclusionException, GroupHierarchyException {
		if (isGroupOfGroups()) {
			for (RoleGroup group : groups) {
				includeGroup(group);
			}
		} else {
			throw new GroupInclusionException("The Group [" + this.getName()
					+ "] is incapable of including other Groups");
		}
	}

	public void includeGroup(RoleGroup group) throws GroupInclusionException,
			GroupHierarchyException {
		if (isGroupOfGroups()) {
			if (group.getIsPartOf() == null) {
				this.consistsOf.add(group);
				group.setIsPartOf(this);
			} else {
				throw new GroupHierarchyException("The Group ["
						+ group.getName()
						+ "] already has a parent and so cannot be included.");
			}
		} else {
			throw new GroupInclusionException("The Group [" + this.getName()
					+ "] is incapable of including other Groups");
		}
	}

	public void includeRoles(Set<Role> roles) throws GroupInclusionException {
		if (isGroupOfRoles()) {
			for (Role role : roles) {
				includeRole(role);
			}
		} else {
			throw new GroupInclusionException("The Group [" + this.getName()
					+ "] is incapable of including Roles");
		}
	}

	public void includeRole(Role role) throws GroupInclusionException {
		if (isGroupOfRoles()) {
			this.includedRoles.add(role);
		} else {
			throw new GroupInclusionException("The Group [" + this.getName()
					+ "] is incapable of including ROles");
		}
	}

	public void removeGroups(Set<RoleGroup> groups) {
		for (RoleGroup group : groups) {
			removeGroup(group);
		}
	}

	public void removeGroup(RoleGroup group) {
		this.consistsOf.remove(group);
		group.setIsPartOf(null);
	}

	public void removeRoles(Set<RoleGroup> roles) {
		for (RoleGroup role : roles) {
			removeRole(role);
		}
	}

	public void removeRole(RoleGroup group) {
		this.consistsOf.remove(group);
		group.setIsPartOf(null);
	}

	public boolean isGroupOfRoles() {
		return this.consistsOf.isEmpty();
	}

	public boolean isGroupOfGroups() {
		return this.includedRoles.isEmpty();
	}

	// Only getters and setters follow.
	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public int getVersion() {
		return this.version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the nodeInfo
	 */
	public TreeNodeInfo getNodeInfo() {
		return this.nodeInfo;
	}

	/**
	 * @param nodeInfo
	 *            the nodeInfo to set
	 */
	public void setNodeInfo(TreeNodeInfo nodeInfo) {
		this.nodeInfo = nodeInfo;
	}

	public String getForestName() {
		return "DealerGroup";
	}

	public TreeNode getParent() {
		return this.isPartOf;
	}

	public AuditableColEntity getD() {
		return d;
	}

	public void setD(AuditableColEntity d) {
		this.d = d;
	}

	public Set<RoleGroup> getConsistsOf() {
		return consistsOf;
	}

	public void setConsistsOf(Set<RoleGroup> consistsOf) {
		this.consistsOf = consistsOf;
	}

	public List<Role> getIncludedRoles() {
		return includedRoles;
	}

	public void setIncludedRoles(List<Role> includedRoles) {
		this.includedRoles = includedRoles;
	}

	public RoleGroup getIsPartOf() {
		return isPartOf;
	}

	public void setIsPartOf(RoleGroup isPartOf) {
		this.isPartOf = isPartOf;
	}

	public RoleScheme getScheme() {
		return scheme;
	}

	public void setScheme(RoleScheme scheme) {
		this.scheme = scheme;
	}
}

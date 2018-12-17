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

import java.util.HashSet;
import java.util.Set;

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

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.Filters;
import org.hibernate.annotations.ParamDef;
import org.hibernate.annotations.Type;
import javax.validation.constraints.NotNull;

import tavant.twms.domain.bu.BusinessUnitAware;
import tavant.twms.domain.bu.BusinessUnitInfo;
import tavant.twms.domain.common.AuditableColEntity;
import tavant.twms.domain.common.GroupHierarchyException;
import tavant.twms.domain.common.GroupInclusionException;
import tavant.twms.security.AuditableColumns;

/**
 * @author aniruddha.chaturvedi
 *
 */
@Entity
@Filters({
  @Filter(name="excludeInactive")
})
@FilterDef(name = "bu_name", parameters = { @ParamDef(name = "name", type = "string") })
@Filter(name = "bu_name", condition = "business_unit_info in (:name)")
public class UserCluster implements AuditableColumns,BusinessUnitAware{
    @Id
    @GeneratedValue
    private Long id;

    @Version
    private int version;

    @NotNull
    private String name;

    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    private UserCluster isPartOf;

    @OneToMany(mappedBy = "isPartOf", fetch = FetchType.LAZY)
    @Cascade( { CascadeType.ALL })
    private Set<UserCluster> consistsOf = new HashSet<UserCluster>();

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_cluster_user", joinColumns = { @JoinColumn(name = "user_cluster") }, inverseJoinColumns = { @JoinColumn(name = "org_user") })
    private Set<User> includedUsers = new HashSet<User>();

    @ManyToOne(fetch = FetchType.LAZY)
    private UserScheme scheme;

    @Embedded
	@Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	private AuditableColEntity d = new AuditableColEntity();

    public UserCluster findTopMostParent() {
        if (this.isPartOf == null) {
            return this;
        }
        return this.isPartOf.findTopMostParent();
    }

    public void includeGroups(Set<UserCluster> clusters) throws GroupInclusionException,
            GroupHierarchyException {
        if (isClusterOfClusters()) {
            for (UserCluster cluster : clusters) {
                includeGroup(cluster);
            }
        } else {
            throw new GroupInclusionException("The Group [" + this.getName()
                    + "] is incapable of including other Groups");
        }
    }

    public void includeGroup(UserCluster cluster) throws GroupInclusionException,
            GroupHierarchyException {
        if (isClusterOfClusters()) {
            if (cluster.getIsPartOf() == null) {
                this.consistsOf.add(cluster);
                cluster.setIsPartOf(this);
            } else {
                throw new GroupHierarchyException("The Group [" + cluster.getName()
                        + "] already has a parent and so cannot be included.");
            }
        } else {
            throw new GroupInclusionException("The Group [" + this.getName()
                    + "] is incapable of including other Groups");
        }
    }

    public void removeGroups(Set<UserCluster> clusters) {
        for (UserCluster cluster : clusters) {
            removeGroup(cluster);
        }
    }

    public void removeGroup(UserCluster cluster) {
        this.consistsOf.remove(cluster);
        cluster.setIsPartOf(null);
    }

    public void includeUsers(Set<User> users) throws GroupInclusionException {
        if (isClusterOfUsers()) {
            for (User user : users) {
                includeUser(user);
            }
        } else {
            throw new GroupInclusionException("The Cluster [" + this.getName()
                    + "] is incapable of including Users");
        }
    }

    public void includeUser(User user) throws GroupInclusionException {
        if (isClusterOfUsers()) {
            this.includedUsers.add(user);
        } else {
            throw new GroupInclusionException("The Cluster [" + this.getName()
                    + "] is incapable of including Users");
        }
    }

    public void removeUsers(Set<User> users) {
        for (User user : users) {
            removeUser(user);
        }
    }

    public void removeUser(User user) {
        this.includedUsers.remove(user);
    }

    public boolean isUserInCluster(User user) {
        return this.includedUsers.contains(user);
    }

    public boolean isClusterOfClusters() {
        return this.includedUsers.isEmpty();
    }

    public boolean isClusterOfUsers() {
        return this.consistsOf.isEmpty();
    }

    // only getters and setters follow.

    public Set<UserCluster> getConsistsOf() {
        return this.consistsOf;
    }

    public void setConsistsOf(Set<UserCluster> consistsOf) {
        this.consistsOf = consistsOf;
    }

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

    public Set<User> getIncludedUsers() {
        return this.includedUsers;
    }

    public void setIncludedUsers(Set<User> includedUsers) {
        this.includedUsers = includedUsers;
    }

    public UserCluster getIsPartOf() {
        return this.isPartOf;
    }

    public void setIsPartOf(UserCluster isPartOf) {
        this.isPartOf = isPartOf;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UserScheme getScheme() {
        return this.scheme;
    }

    public void setScheme(UserScheme scheme) {
        this.scheme = scheme;
    }

	public AuditableColEntity getD() {
		return d;
	}

	public void setD(AuditableColEntity d) {
		this.d = d;
	}

	@Type(type="tavant.twms.domain.bu.BusinessUnitInfoType")
    @JsonIgnore
    private BusinessUnitInfo businessUnitInfo = new BusinessUnitInfo();

	public BusinessUnitInfo getBusinessUnitInfo() {
		return businessUnitInfo;
	}

	public void setBusinessUnitInfo(BusinessUnitInfo buAudit) {
		this.businessUnitInfo = buAudit;
	}

}

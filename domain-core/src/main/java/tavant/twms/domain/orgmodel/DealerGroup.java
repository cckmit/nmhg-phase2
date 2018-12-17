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
import java.util.HashSet;
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

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.Filters;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.ParamDef;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.hibernate.validator.constraints.NotEmpty;

import tavant.twms.domain.bu.BusinessUnitAware;
import tavant.twms.domain.bu.BusinessUnitInfo;
import tavant.twms.domain.common.AuditableColEntity;
import tavant.twms.domain.common.GroupHierarchyException;
import tavant.twms.domain.common.GroupInclusionException;
import tavant.twms.infra.TreeNode;
import tavant.twms.infra.TreeNodeInfo;
import tavant.twms.infra.TreeStructuredData;
import tavant.twms.security.AuditableColumns;

/**
 * @author aniruddha.chaturvedi
 * 
 */
@Entity
@Filters({
  @Filter(name="excludeInactive")
})
@SuppressWarnings("serial")
@TreeStructuredData(parentProperty = "isPartOf")
@FilterDef(name = "bu_name", parameters = { @ParamDef(name = "name", type = "string") })
@Filter(name = "bu_name", condition = "business_unit_info in (:name)")
public class DealerGroup implements TreeNode,Serializable, AuditableColumns,BusinessUnitAware
{
    @Id
    @GeneratedValue(generator = "DealerGroup")
	@GenericGenerator(name = "DealerGroup", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "DEALER_GROUP_SEQ"),
			@Parameter(name = "initial_value", value = "1000"),
			@Parameter(name = "increment_size", value = "20") })
    private Long id;

    @Version
    private int version;

    @NotEmpty
    private String name;

    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    private DealerGroup isPartOf;

    @OneToMany(mappedBy = "isPartOf", fetch = FetchType.LAZY)
    @Cascade( { CascadeType.ALL })
    private Set<DealerGroup> consistsOf = new HashSet<DealerGroup>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "dealers_in_group", joinColumns = { @JoinColumn(name = "dealer_group") }, inverseJoinColumns = { @JoinColumn(name = "dealer") })
    private Set<ServiceProvider> includedDealers = new HashSet<ServiceProvider>();
    
    @Embedded
    @AttributeOverrides( {
            @AttributeOverride(name = "lft", column = @Column(name = "lft", nullable = false)),
            @AttributeOverride(name = "rgt", column = @Column(name = "rgt", nullable = false)),
            @AttributeOverride(name = "treeId", column = @Column(name = "tree_id", nullable = false)),
            @AttributeOverride(name = "depth", column = @Column(name = "depth", nullable = false)) })
    private TreeNodeInfo nodeInfo = new TreeNodeInfo();

    @ManyToOne(fetch = FetchType.LAZY)
    private DealerScheme scheme;
    
    @Embedded
	@Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	private AuditableColEntity d = new AuditableColEntity();
    
    private String code;
    
    public String getCode() {
		return code;
	}
    public void setCode(String code) {
		this.code = code;
	}

	public DealerGroup findTopMostParent() {
        if (this.isPartOf == null) {
            return this;
        }
        return this.isPartOf.findTopMostParent();
    }

    public void includeGroups(Set<DealerGroup> groups) throws GroupInclusionException,
            GroupHierarchyException {
        if (isGroupOfGroups()) {
            for (DealerGroup group : groups) {
                includeGroup(group);
            }
        } else {
            throw new GroupInclusionException("The Group [" + this.getName()
                    + "] is incapable of including other Groups");
        }
    }

    public void includeGroup(DealerGroup group) throws GroupInclusionException,
            GroupHierarchyException {
        if (isGroupOfGroups()) {
            if (group.getIsPartOf() == null) {
                this.consistsOf.add(group);
                group.setIsPartOf(this);
            } else {
                throw new GroupHierarchyException("The Group [" + group.getName()
                        + "] already has a parent and so cannot be included.");
            }
        } else {
            throw new GroupInclusionException("The Group [" + this.getName()
                    + "] is incapable of including other Groups");
        }
    }

    public void removeGroups(Set<DealerGroup> groups) {
        for (DealerGroup group : groups) {
            removeGroup(group);
        }
    }

    public void removeGroup(DealerGroup group) {
        this.consistsOf.remove(group);
        group.setIsPartOf(null);
    }

    public void includeDealers(Set<ServiceProvider> dealerships) throws GroupInclusionException {
        if (isGroupOfDealers()) {
            for (ServiceProvider dealership : dealerships) {
                includeDealer(dealership);
            }
        } else {
            throw new GroupInclusionException("The Group [" + this.getName()
                    + "] is incapable of including Items");
        }
    }

    public void includeDealer(ServiceProvider dealership) throws GroupInclusionException {
        if (isGroupOfDealers()) {
            this.includedDealers.add(dealership);
        } else {
            throw new GroupInclusionException("The Group [" + this.getName()
                    + "] is incapable of including Items");
        }
    }

    public void removeDealers(Set<ServiceProvider> dealerships) {
        for (ServiceProvider dealership : dealerships) {
            removeDealer(dealership);
        }
    }

    public void removeDealer(ServiceProvider dealership) {
        this.includedDealers.remove(dealership);
    }

    public boolean isDealerInGroup(ServiceProvider dealership) {
        return this.includedDealers.contains(dealership);
    }

    public boolean isGroupOfGroups() {
        return this.includedDealers.isEmpty();
    }

    public boolean isGroupOfDealers() {
        return this.consistsOf.isEmpty();
    }

    // Only getters and setters follow.
    public Set<DealerGroup> getConsistsOf() {
        return this.consistsOf;
    }

    public void setConsistsOf(Set<DealerGroup> consistsOf) {
        this.consistsOf = consistsOf;
    }

    public Set<ServiceProvider> getIncludedDealers() {
        return this.includedDealers;
    }

    public void setIncludedDealers(Set<ServiceProvider> consistsOfDealers) {
        this.includedDealers = consistsOfDealers;
    }
    public Set<ServiceProvider> getIncludedDealersOfGroup() {
        return  this.includedDealers;
    }

    public void setIncludedDealersOfGroup(Set<ServiceProvider> consistsOfDealers) {
        this.includedDealers = consistsOfDealers;
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

    public DealerGroup getIsPartOf() {
        return this.isPartOf;
    }

    public void setIsPartOf(DealerGroup isPartOf) {
        this.isPartOf = isPartOf;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public DealerScheme getScheme() {
        return this.scheme;
    }

    public void setScheme(DealerScheme scheme) {
        this.scheme = scheme;
    }

    /**
     * @return the nodeInfo
     */
    public TreeNodeInfo getNodeInfo() {
        return this.nodeInfo;
    }

    /**
     * @param nodeInfo the nodeInfo to set
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

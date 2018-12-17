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

package tavant.twms.domain.catalog;

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
import javax.persistence.Transient;
import javax.persistence.Version;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.Filters;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.ParamDef;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.springframework.core.style.ToStringCreator;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import javax.validation.constraints.NotNull;

import tavant.twms.common.Views;
import tavant.twms.domain.bu.BusinessUnitAware;
import tavant.twms.domain.bu.BusinessUnitInfo;
import tavant.twms.domain.common.AuditableColEntity;
import tavant.twms.domain.common.GroupHierarchyException;
import tavant.twms.domain.common.GroupInclusionException;
import tavant.twms.domain.common.Label;
import tavant.twms.domain.failurestruct.FailureStructure;
import tavant.twms.infra.TreeNode;
import tavant.twms.infra.TreeNodeInfo;
import tavant.twms.infra.TreeStructuredData;
import tavant.twms.security.AuditableColumns;
import tavant.twms.security.SecurityHelper;

@SuppressWarnings("serial")
@Entity
@Filters({
  @Filter(name="excludeInactive")
})
@TreeStructuredData(parentProperty = "isPartOf")
@FilterDef(name = "bu_name", parameters = { @ParamDef(name = "name", type = "string") })
@Filter(name = "bu_name", condition = "business_unit_info in (:name)")
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "@id")
@JsonAutoDetect
public class ItemGroup implements Comparable<ItemGroup>, TreeNode, BusinessUnitAware,AuditableColumns{


    @Id
    @GeneratedValue(generator = "ItemGroup")
	@GenericGenerator(name = "ItemGroup", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "ITEM_GROUP_SEQ"),
			@Parameter(name = "initial_value", value = "1000"),
			@Parameter(name = "increment_size", value = "20") })
    private Long id;

    public static final String DIVISION = "DIVISION";

    public static final String PRODUCT_TYPE = "PRODUCT TYPE";

    public static final String PRODUCT = "PRODUCT";
    
    public static final String MACHINE = "MACHINE";

    public static final String MODEL = "MODEL";
    
    public static final String PART_CLASS = "PART CLASS";

    public static final String PRODUCT_FAMILY = "PRODUCT FAMILY";
    
    public static final String ITEM_DIVISION_CODE = "DIVISIONCODE";

    @Version
    @JsonIgnore
    private int version;

    @NotNull
    @Column(unique = true)
    @JsonView(Views.Public.class)
    private String name;

    private String groupCode;

    private String description;

    @OneToMany(mappedBy = "forItemGroup")
    @Cascade( { CascadeType.ALL })
    private Set<FailureStructure> failureStructures = new HashSet<FailureStructure>(1);

    private String itemGroupType;

    @ManyToOne(fetch = FetchType.LAZY)
    private ItemGroup isPartOf;

    @OneToMany(mappedBy = "isPartOf", fetch = FetchType.LAZY)
    @Cascade( { CascadeType.ALL })
    private Set<ItemGroup> consistsOf = new HashSet<ItemGroup>();

    @OneToMany(fetch = FetchType.LAZY)
	@Cascade( { CascadeType.ALL, CascadeType.DELETE_ORPHAN })
	@JoinColumn(name = "ITEM_GROUP_I18NDESCRIPTION", nullable = false)
	private List<I18NItemGroupText> itemGroupTexts = new ArrayList<I18NItemGroupText>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "items_in_group", joinColumns = { @JoinColumn(name = "item_group") }, inverseJoinColumns = { @JoinColumn(name = "item") })
    private Set<Item> includedItems = new HashSet<Item>();

    @ManyToOne(fetch = FetchType.LAZY)
    private ItemScheme scheme;

    @Embedded
    @AttributeOverrides( {
            @AttributeOverride(name = "lft", column = @Column(name = "lft", nullable = false)),
            @AttributeOverride(name = "rgt", column = @Column(name = "rgt", nullable = false)),
            @AttributeOverride(name = "treeId", column = @Column(name = "tree_id", nullable = false)),
            @AttributeOverride(name = "depth", column = @Column(name = "depth", nullable = false)) })
    @JsonIgnore
    private final TreeNodeInfo nodeInfo = new TreeNodeInfo();

    @Embedded
	@Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	private AuditableColEntity d = new AuditableColEntity();

    @JsonIgnore
    private String machineUrl;

    @ManyToMany
	private Set<Label> labels = new HashSet<Label>();
    
    private String brandType;
    @ManyToOne(fetch = FetchType.LAZY)
    private ItemGroup oppositeSeries;

    @JsonIgnore
    public String getItemGroupDescription() {
		return this.description;
	}

    @JsonIgnore
    private String companyType;

    public String getCompanyType() {
		return companyType;
	}

	public void setCompanyType(String companyType) {
		this.companyType = companyType;
	}

    @JsonIgnore
    private String buildPlant;

	public String getBuildPlant() {
		return buildPlant;
	}

	public void setBuildPlant(String buildPlant) {
		this.buildPlant = buildPlant;
	}


    
	public ItemGroup getOppositeSeries() {
		return oppositeSeries;
	}

	public void setOppositeSeries(ItemGroup oppositeSeries) {
		this.oppositeSeries = oppositeSeries;
	}

	public String getBrandType() {
		return brandType;
	}

	public void setBrandType(String brandType) {
		this.brandType = brandType;
	}

	
    @JsonIgnore
    public String getForestName() {
        return "ItemGroup";
    }

    public TreeNodeInfo getNodeInfo() {
        return this.nodeInfo;
    }

    @JsonIgnore
    public TreeNode getParent() {
        return this.isPartOf;
    }

    public void includeGroups(Set<ItemGroup> groups) throws GroupInclusionException,
            GroupHierarchyException {
        if (isGroupOfGroups()) {
            for (ItemGroup group : groups) {
                includeGroup(group);
            }
        } else {
            throw new GroupInclusionException("The Group [" + this.getName()
                    + "] is incapable of including other Groups");
        }
    }

    public void includeGroup(ItemGroup group) throws GroupInclusionException,
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

    public void removeGroups(Set<ItemGroup> groups) {
        for (ItemGroup group : groups) {
            removeGroup(group);
        }
    }

    public void removeGroup(ItemGroup group) {
        this.consistsOf.remove(group);
        group.setIsPartOf(null);
    }

    public void includeItems(Set<Item> items) throws GroupInclusionException {
        if (isGroupOfItems()) {
            for (Item item : items) {
                includeItem(item);
            }
        } else {
            throw new GroupInclusionException("The Group [" + this.getName()
                    + "] is incapable of including Items");
        }
    }

    public void includeItem(Item item) throws GroupInclusionException {
        if (isGroupOfItems()) {
            Set<ItemGroup> belongsToItemGroups = item.getBelongsToItemGroups();
            for (ItemGroup otherGroup : belongsToItemGroups) {
                if (getScheme().equals(otherGroup.getScheme())
                        && !this.name.equals(otherGroup.getName())) {
                    throw new GroupInclusionException("The Item [" + item
                            + "] is already part of another group [" + otherGroup.getName()
                            + "] in scheme [" + getScheme().getName() + "]");
                }
            }
            this.includedItems.add(item);
            item.getBelongsToItemGroups().add(this);
        } else {
            throw new GroupInclusionException("The Group [" + this.getName()
                    + "] is incapable of including Items");
        }
    }

    public void removeItems(Set<Item> items) {
        for (Item item : items) {
            removeItem(item);
        }
    }

    public void removeItem(Item item) {
        this.includedItems.remove(item);
    }

    public boolean isItemInGroup(Item item) {
        return this.includedItems.contains(item);
    }

    @JsonIgnore
    public boolean isGroupOfGroups() {
        return this.includedItems.isEmpty();
    }

    @JsonIgnore
    public boolean isGroupOfItems() {
        return this.consistsOf.isEmpty();
    }

    public ItemGroup findTopMostParent() {
        if (this.isPartOf == null) {
            return this;
        }
        return this.isPartOf.findTopMostParent();
    }

    public Set<Item> getIncludedItems() {
        return this.includedItems;
    }

    public void setIncludedItems(Set<Item> consistsOfItems) {
        this.includedItems = consistsOfItems;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGroupCode() {
        return this.groupCode;
    }

    public void setGroupCode(String groupCode) {
        this.groupCode = groupCode;
    }

    public String getDescription() {
    	String i18nDescription = "";
		for (I18NItemGroupText itemGroupText : this.itemGroupTexts) {
			if (itemGroupText.getLocale().equalsIgnoreCase(
					new SecurityHelper().getLoggedInUser().getLocale()
							.toString()) && itemGroupText.getDescription() != null) {
				i18nDescription = itemGroupText.getDescription();
				break;
			}
			else if(itemGroupText.getLocale().equalsIgnoreCase("en_US")) {
				i18nDescription = itemGroupText.getDescription();
			}

		}
		if(!StringUtils.hasText(i18nDescription) && StringUtils.hasText(description))
		{
			return this.description;
		}
		return i18nDescription;
    }

    public void setDescription(String description) {
    	if(this.getItemGroupTexts() != null && this.getItemGroupTexts().size() > 0) {
			this.getItemGroupTexts().get(0).setDescription(description);
			this.getItemGroupTexts().get(0).setLocale(new SecurityHelper().getLoggedInUser().getLocale()
							.toString());
		}
		else
		{
			I18NItemGroupText i18NItemGroupText = new I18NItemGroupText();
			i18NItemGroupText.setDescription(description);
			i18NItemGroupText.setLocale("en_US");
			this.getItemGroupTexts().add(i18NItemGroupText);
		}
    	this.description = description;
    }

    public Set<FailureStructure> getFailureStructures() {
        return this.failureStructures;
    }

    public void setFailureStructures(Set<FailureStructure> failureStructures) {
        this.failureStructures = failureStructures;
    }

    public void addFailureStructure(FailureStructure failureStructure) {
        this.failureStructures.add(failureStructure);
        failureStructure.setForItemGroup(this);
    }

    public String getItemGroupType() {
        return this.itemGroupType;
    }

    public void setItemGroupType(String itemGroupType) {
        this.itemGroupType = itemGroupType;
    }

    public Set<ItemGroup> getConsistsOf() {
        return this.consistsOf;
    }

    public void setConsistsOf(Set<ItemGroup> consistsOf) {
        this.consistsOf = consistsOf;
    }

    public ItemGroup getIsPartOf() {
        return this.isPartOf;
    }

    public void setIsPartOf(ItemGroup isPartOf) {
        this.isPartOf = isPartOf;
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

    public int compareTo(ItemGroup otherItemGroup) {
        if (this.name != null) {
            return this.name.compareTo(otherItemGroup.getName());
        } else {
            return -1; // anything other than 0 is fine
        }
    }

    @JsonIgnore
	public String getProductNameForUnserializedItem() {
		return getProductName(this);
	}

	public String getProductName(ItemGroup itemGroup) {
		String productName = null;
		ItemGroup group = getProduct(itemGroup);
		if (group != null) {
			productName = group.getName();
		}
		return productName;
	}

	@JsonIgnore
	public ItemGroup getProduct(ItemGroup itemGroup) {
		if (itemGroup == null)
			return itemGroup;
		return ItemGroup.PRODUCT.equals(itemGroup.getItemGroupType()) ? itemGroup
				: getProduct(itemGroup.getIsPartOf());

	}
	
	@JsonIgnore
	public ItemGroup getProduct() {
        return ItemGroup.PRODUCT.equals(this.itemGroupType) ? this : isPartOf == null ? null : isPartOf.getProduct();
	}
    // @Override
    // public boolean equals(Object object) {
    // if (!(object instanceof ItemGroup)) {
    // return false;
    // } else {
    // ItemGroup otherGrp = (ItemGroup) object;
    // return new
    // EqualsBuilder().appendSuper(super.equals(otherGrp)).append(this.name,
    // otherGrp.name).isEquals();
    // }
    // }
    //
    // @Override
    // public int hashCode() {
    // return new HashCodeBuilder(777, 333).append(this.name).toHashCode();
    // }

    @Override
    public String toString() {
        return new ToStringCreator(this).append("id", this.id).append("name", this.name).append(
                "description", this.description)
                .append("failure structure", this.failureStructures).append("nodeIfo",
                        this.nodeInfo).toString();
    }

    public ItemScheme getScheme() {
        return this.scheme;
    }

    public void setScheme(ItemScheme scheme) {
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

	public List<I18NItemGroupText> getItemGroupTexts() {
		return itemGroupTexts;
	}

	public void setItemGroupTexts(List<I18NItemGroupText> itemGroupTexts) {
		this.itemGroupTexts = itemGroupTexts;
	}

	public String getMachineUrl() {
		return machineUrl;
	}

	public void setMachineUrl(String machineUrl) {
		this.machineUrl = machineUrl;
	}

    public Set<Label> getLabels() {
        return labels;
    }

    public void setLabels(Set<Label> labels) {
        this.labels = labels;
    }

	@JsonIgnore
	public String getNameAndParentName() {
		if(this.name == null)
			return "";
		return this.name+(this.getIsPartOf()==null ? "" : (" ("+this.getIsPartOf().getName()+")"));
	}
	
	@JsonIgnore
	public String getCodeAndParentName() {
		if(this.groupCode == null)
			return "";
		return this.groupCode+(this.getIsPartOf()==null ? "" : (" -"+this.getIsPartOf().getGroupCode()));
	}
	
	@JsonIgnore
    public String getBuAppendedName(){
        StringBuffer buAppendedName = new StringBuffer(getBusinessUnitInfo().getName()+"-");
        buAppendedName.append(getName());
        return buAppendedName.toString();
    }
}
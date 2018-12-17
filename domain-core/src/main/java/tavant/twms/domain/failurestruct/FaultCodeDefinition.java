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
package tavant.twms.domain.failurestruct;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.AssociationOverride;
import javax.persistence.AssociationOverrides;
import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Version;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CollectionOfElements;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.Filters;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.IndexColumn;
import org.hibernate.annotations.ParamDef;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

import tavant.twms.domain.bu.BusinessUnitAware;
import tavant.twms.domain.bu.BusinessUnitInfo;
import tavant.twms.domain.catalog.ItemCriterion;
import tavant.twms.domain.common.AuditableColEntity;
import tavant.twms.domain.common.Label;
import tavant.twms.security.AuditableColumns;

/**
 * @author kamal.govindraj
 *
 */
@Entity
@Filters({
  @Filter(name="excludeInactive")
})
@FilterDef(name = "bu_name", parameters = { @ParamDef(name = "name", type = "string") })
@Filter(name = "bu_name", condition = "business_unit_info in (:name)")
public class FaultCodeDefinition implements BusinessUnitAware,AuditableColumns{


    @Id
    @GeneratedValue(generator = "FaultCodeDefinition")
	@GenericGenerator(name = "FaultCodeDefinition", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "FAULT_CODE_DEFN_SEQ"),
			@Parameter(name = "initial_value", value = "1000"),
			@Parameter(name = "increment_size", value = "20") })
    private Long id;

    @Version
    private int version;

    private String code;

    @ManyToMany(fetch = FetchType.LAZY)
    @IndexColumn(name = "list_index")
    @JoinTable(name = "FAULT_CODE_DEF_COMPS")
    private List<AssemblyDefinition> components = new ArrayList<AssemblyDefinition>();

    @ManyToMany
    private Set<Label> labels = new HashSet<Label>();

   	@CollectionOfElements
    @JoinTable(name = "FAULT_CODE_PART_CLASS_ASSOC")
    @JoinColumn(name = "fault_code", nullable = false)
    @IndexColumn(name = "ITEM_CRITERION_INDEX")
    @AssociationOverrides( 
    		{@AssociationOverride(name = "element.item", joinColumns = @JoinColumn(name = "itm_cr_item")), 
            @AssociationOverride(name = "element.itemGroup", joinColumns = @JoinColumn(name = "itm_cr_item_grp"))}
    		)
    @AttributeOverride(name = "element.itemIdentifier", column = @Column(name = "itm_cr_item_idntfr"))
    private List<ItemCriterion> partClasses = new ArrayList<ItemCriterion>();

    @Embedded
	@Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	private AuditableColEntity d = new AuditableColEntity();

	@Type(type="tavant.twms.domain.bu.BusinessUnitInfoType")
    @JsonIgnore
    private BusinessUnitInfo businessUnitInfo = new BusinessUnitInfo();

    public FaultCodeDefinition() {
    }

    public FaultCodeDefinition(String faultCode, List<AssemblyDefinition> components) {
        this.code = faultCode;
        this.components = components;
    }

    public FaultCodeDefinition(String faultCode) {
        this.code = faultCode;
    }

    public String getCode() {
        return this.code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public List<AssemblyDefinition> getComponents() {
        return this.components;
    }

    public void setComponents(List<AssemblyDefinition> components) {
        this.components = components;
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

    public void addComponent(AssemblyDefinition assemblyDefinition) {
        this.components.add(assemblyDefinition);
    }

    public Set<Label> getLabels() {
        return this.labels;
    }

    public void setLabels(Set<Label> labels) {
        this.labels = labels;
    }
	
	public List<ItemCriterion> getPartClasses() {
		return partClasses;
	}

	public void setPartClasses(List<ItemCriterion> partClasses) {
		this.partClasses = partClasses;
	}

	public AuditableColEntity getD() {
		return d;
	}

	public void setD(AuditableColEntity d) {
		this.d = d;
	}

	public BusinessUnitInfo getBusinessUnitInfo() {
		return businessUnitInfo;
	}

	public void setBusinessUnitInfo(BusinessUnitInfo buAudit) {
		this.businessUnitInfo = buAudit;
	}

}

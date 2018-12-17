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
package tavant.twms.domain.failurestruct;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
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
import javax.persistence.Transient;
import javax.persistence.Version;

import org.hibernate.annotations.Cascade;
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
import tavant.twms.domain.common.AuditableColEntity;
import tavant.twms.domain.common.Label;
import tavant.twms.infra.HibernateCast;
import tavant.twms.infra.InstanceOfUtil;
import tavant.twms.security.AuditableColumns;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Filters({
  @Filter(name="excludeInactive")
})
@FilterDef(name = "bu_name", parameters = { @ParamDef(name = "name", type = "string") })
@Filter(name = "bu_name", condition = "business_unit_info in (:name)")
public class ServiceProcedureDefinition implements BusinessUnitAware,AuditableColumns{

	@Id
	@GeneratedValue(generator = "ServiceProcedureDef")
	@GenericGenerator(name = "ServiceProcedureDef", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "SERVICE_PROCEDUREDEF_SEQ"),
			@Parameter(name = "initial_value", value = "1000"),
			@Parameter(name = "increment_size", value = "20") })
    private Long id;

    @Version
    private int version;

	private String code;
	
	@Transient
	private String description;

    public String getDescription() {
		return getServiceProcDefinitionDesc();
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@ManyToMany(fetch = FetchType.LAZY)
    @IndexColumn(name = "list_index")
    @JoinTable(name = "SERVICE_PROC_DEF_COMPS")
    private List<AssemblyDefinition> components = new ArrayList<AssemblyDefinition>();

    @ManyToOne(fetch = FetchType.LAZY)
    private ActionDefinition actionDefinition;

    @ManyToMany
    @JoinTable(name = "service_procedure_def_labels")
    private Set<Label> labels = new HashSet<Label>();
    
    @JsonIgnore
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "INCLUSIVE_JOB_CODES", 
    		   joinColumns = { @JoinColumn(name = "parent_job") }, 
    		   inverseJoinColumns = { @JoinColumn(name = "child_job") })
    private List<ServiceProcedureDefinition> childJobs = new ArrayList<ServiceProcedureDefinition>();
    
    @Embedded
	@Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	private AuditableColEntity d = new AuditableColEntity();

    public ServiceProcedureDefinition() {
    }

    public ServiceProcedureDefinition(String code) {
        this.code = code;
    }

    public String getCode() {
        return this.code;
    }

    public void setCode(String code) {
        this.code = code;
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

    public ActionDefinition getActionDefinition() {
        return this.actionDefinition;
    }

    public void setActionDefinition(ActionDefinition actionDefinition) {
        this.actionDefinition = actionDefinition;
    }

    public List<AssemblyDefinition> getComponents() {
        return this.components;
    }

    public void setComponents(List<AssemblyDefinition> components) {
        this.components = components;
    }

    public void addComponent(AssemblyDefinition assemblyDefinition) {
        components.add(assemblyDefinition);
    }

    public Set<Label> getLabels() {
        return this.labels;
    }

    public void setLabels(Set<Label> labels) {
        this.labels = labels;
    }

    public List<ServiceProcedureDefinition> getChildJobs() {
		return childJobs;
	}

	public void setChildJobs(List<ServiceProcedureDefinition> childJobs) {
		this.childJobs = childJobs;
	}

    @Override
    public boolean equals(Object other) {

    	if (InstanceOfUtil.isInstanceOfClass( ServiceProcedureDefinition.class, other)) {
            return getCode().equals((new HibernateCast<ServiceProcedureDefinition>().cast(other)).getCode());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return this.code.hashCode();
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
	
	@JsonIgnore
	public AssemblyDefinition getLastComponent(){
		if (components.isEmpty()) {
			return null;
		}else{
            return getComponents().get(getComponents().size()-1);
        }
	}
	
	@JsonIgnore
	public String getServiceProcDefinitionDesc() {
		StringBuffer toReturn = new StringBuffer();
		List<AssemblyDefinition> assemblyDefList = getComponents();
		if (assemblyDefList != null && assemblyDefList.size() > 0) {
			for (Iterator<AssemblyDefinition> iter = assemblyDefList.iterator(); iter
					.hasNext();) {
				toReturn = toReturn.append(iter.next().getName());
				toReturn = toReturn.append("-");
			}
		}
		toReturn.append(getActionDefinition().getName());
		return toReturn.toString();
	}
}


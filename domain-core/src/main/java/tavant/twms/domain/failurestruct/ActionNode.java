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

import static javax.persistence.CascadeType.ALL;
import java.util.Iterator;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import org.hibernate.annotations.Filters;
import org.hibernate.annotations.Filter;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Version;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;

import tavant.twms.common.Views;
import tavant.twms.domain.common.AuditableColEntity;
import tavant.twms.security.AuditableColumns;

/**
 * @author kamal.govindraj
 * 
 */
@Entity
@Filters({
  @Filter(name="excludeInactive")
})
public class ActionNode implements Comparable<ActionNode>, AuditableColumns {
    @Id
    @GeneratedValue(generator = "ActionNode")
	@GenericGenerator(name = "ActionNode", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "ACTION_NODE_SEQ"),
			@Parameter(name = "initial_value", value = "1000"),
			@Parameter(name = "increment_size", value = "20") })
    Long id;

    @Version
    private int version;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonView(Views.Public.class)
    private ActionDefinition definition;

    @ManyToOne(fetch = FetchType.LAZY)
    private Assembly definedFor;

    @OneToOne(fetch = FetchType.EAGER, cascade = { ALL }, mappedBy = "definedFor", optional = false)
    @Cascade( { org.hibernate.annotations.CascadeType.DELETE_ORPHAN })
    @JsonIgnore
    private ServiceProcedure serviceProcedure;
    
    @Column(nullable=false)
    private Boolean active = new Boolean(true);
    
    @Embedded
	@Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	private AuditableColEntity d = new AuditableColEntity();

    public ActionNode() {

    }
 @JsonIgnore
    public ActionNode(Assembly assembly, ActionDefinition definition,
            ServiceProcedureDefinition serviceProcedureDefinition) {
        this.definedFor = assembly;
        this.definition = definition;
        this.serviceProcedure = new ServiceProcedure(this, serviceProcedureDefinition);
    }

    public ActionDefinition getDefinition() {
        return this.definition;
    }

    public void setDefinition(ActionDefinition definition) {
        this.definition = definition;
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

    public Assembly getDefinedFor() {
        return this.definedFor;
    }

    public void setDefinedFor(Assembly definedFor) {
        this.definedFor = definedFor;
    }

    public ServiceProcedure getServiceProcedure() {
        return this.serviceProcedure;
    }

    public void setServiceProcedure(ServiceProcedure serviceProcedure) {
        this.serviceProcedure = serviceProcedure;
    }

    public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}
	
   @JsonIgnore
	public String getFullCode() {
        return this.definedFor.getFullCode() + Assembly.FAULT_CODE_SEPARATOR
                + this.definition.getCode();
    }
   
   @JsonIgnore
	public String getFullDescription() {
        return this.definedFor.getFullDescription() + Assembly.FAULT_CODE_SEPARATOR
                + this.definition.getName();
    }
	
	public String getJobCodeDescription() {
		String jobCodeDescription = "";
		String jobCodeDesc ="";
		List<AssemblyDefinition> assemblyDefList = this.definedFor
				.getFaultCode().getDefinition().getComponents();
		if (assemblyDefList != null && assemblyDefList.size() > 0) {
			for (Iterator<AssemblyDefinition> iter = assemblyDefList.iterator(); iter
					.hasNext();) {
				jobCodeDescription = jobCodeDescription + iter.next().getName();
				if (iter.hasNext()) {
					jobCodeDescription = jobCodeDescription + "-";
				}
			}
		}
		
		if ((this.definition != null) && (this.definition.getName() != null)) {
			jobCodeDesc = this.getDefinition().getName();
        }
		return jobCodeDescription + Assembly.FAULT_CODE_SEPARATOR + jobCodeDesc ;
	}
	
	public int compareTo(ActionNode otherItemGroup) {
        if ((this.definition != null) && (this.definition.getName() != null)) {
            return this.getDefinition().getName().compareTo(otherItemGroup.getDefinition().getName());
        } else {
            return -1; // anything other than 0 is fine
        }
    }

	public AuditableColEntity getD() {
		return d;
	}

	public void setD(AuditableColEntity d) {
		this.d = d;
	}

}

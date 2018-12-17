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

import java.util.Iterator;
import java.util.List;

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
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

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
public class ServiceProcedure implements AuditableColumns{

    public ServiceProcedure() {
    }

    public ServiceProcedure(ActionNode definedFor, ServiceProcedureDefinition definition) {
        this.definedFor = definedFor;
        this.definition = definition;
    }

    @Id
	@GeneratedValue(generator = "ServiceProcedure")
	@GenericGenerator(name = "ServiceProcedure", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "SERVICE_PROCEDURE_SEQ"),
			@Parameter(name = "initial_value", value = "1000"),
			@Parameter(name = "increment_size", value = "20") })
    private Long id;

    @Version
    private int version;
    
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    private ServiceProcedureDefinition definition;

    private Double suggestedLabourHours;

    private Boolean forCampaigns;

    @OneToOne(fetch = FetchType.LAZY)
    @JsonView(Views.Public.class)
    private ActionNode definedFor;
    
    @Embedded
	@Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	private AuditableColEntity d = new AuditableColEntity();

    public ServiceProcedureDefinition getDefinition() {
        return this.definition;
    }

    public void setDefinition(ServiceProcedureDefinition definition) {
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

    public ActionNode getDefinedFor() {
        return this.definedFor;
    }

    public void setDefinedFor(ActionNode definedFor) {
        this.definedFor = definedFor;
    }

    @Override
    public boolean equals(Object o) {// auto generated
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        ServiceProcedure that = (ServiceProcedure) o;

        if (this.definition != null ? !this.definition.equals(that.definition)
                : that.definition != null)
            return false;
        if (this.id != null ? !this.id.equals(that.id) : that.id != null)
            return false;
        return true;
    }

    @Override
    public int hashCode() {// auto generated
        int result;
        result = (this.id != null ? this.id.hashCode() : 0);
        result = 31 * result + (this.definition != null ? this.definition.hashCode() : 0);
        return result;
    }

    @JsonIgnore
    public String getServiceProcedureDescription(){
    	return definition.getDescription();
    }
    
    public Boolean getForCampaigns() {
        return this.forCampaigns;
    }

    public void setForCampaigns(Boolean forCampaigns) {
        this.forCampaigns = forCampaigns;
    }

    public Double getSuggestedLabourHours() {
        return this.suggestedLabourHours;
    }

    public void setSuggestedLabourHours(Double suggestedLabourHours) {
        this.suggestedLabourHours = suggestedLabourHours;
    }

	public AuditableColEntity getD() {
		return d;
	}

	public void setD(AuditableColEntity d) {
		this.d = d;
	}

}

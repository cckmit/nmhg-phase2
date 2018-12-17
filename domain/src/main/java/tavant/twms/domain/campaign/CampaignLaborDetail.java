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
package tavant.twms.domain.campaign;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import org.hibernate.annotations.Filters;
import org.hibernate.annotations.Filter;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Version;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Columns;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.springframework.core.style.ToStringCreator;

import tavant.twms.domain.common.AuditableColEntity;
import tavant.twms.domain.failurestruct.ServiceProcedureDefinition;
import tavant.twms.security.AuditableColumns;

import com.domainlanguage.money.Money;

@Entity
@Filters({
  @Filter(name="excludeInactive")
})
// @Expression(value = "validLaborHours()", message = "{required.labor.hour}")
public class CampaignLaborDetail implements AuditableColumns, Comparable<CampaignLaborDetail>{
    @Id
    @GeneratedValue(generator = "CampaignLaborDetail")
	@GenericGenerator(name = "CampaignLaborDetail", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "CAMPAIGN_LABOR_SEQ"),
			@Parameter(name = "initial_value", value = "1000"),
			@Parameter(name = "increment_size", value = "20") })
    private Long id;

    @Version
    private int version;

    @ManyToOne(fetch = FetchType.LAZY)
    private ServiceProcedureDefinition serviceProcedureDefinition;

    private BigDecimal specifiedLaborHours;

    private boolean laborStandardsUsed;

    @Type(type = "tavant.twms.infra.MoneyUserType")
    @Columns(columns = { @Column(name = "labor_rate_amt"), @Column(name = "labor_rate_curr") })
    private Money laborRate;
    
    @Embedded
	@Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	private AuditableColEntity d = new AuditableColEntity();

    public CampaignLaborDetail() {

    }

    /**
     * default implementation for compareTo method.
     * Currently used only in CampaignClaimValidationService, which overrides itwith a local implementation  
     */
    public int compareTo(CampaignLaborDetail o) {    
    	return 0;
    }

    /**
     * @return the id
     */
    public Long getId() {
        return this.id;
    }

    /**
     * @param id the id to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    public int getVersion() {
        return this.version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    @Override
    public String toString() {
        return new ToStringCreator(this).append("id", this.id).append("specified labor hours",
                                                                      this.specifiedLaborHours)
                .append("labor rate", this.laborRate).toString();
    }

    public Money getLaborRate() {
        return this.laborRate;
    }

    public void setLaborRate(Money laborRate) {
        this.laborRate = laborRate;
    }

    public boolean isLaborStandardsUsed() {
        return this.laborStandardsUsed;
    }

    public void setLaborStandardsUsed(boolean laborStandardsUsed) {
        this.laborStandardsUsed = laborStandardsUsed;
    }

    public BigDecimal getSpecifiedLaborHours() {
        return this.specifiedLaborHours;
    }

    public void setSpecifiedLaborHours(BigDecimal specifiedLaborHours) {
        this.specifiedLaborHours = specifiedLaborHours;
    }

    public ServiceProcedureDefinition getServiceProcedureDefinition() {
        return this.serviceProcedureDefinition;
    }

    public void setServiceProcedureDefinition(ServiceProcedureDefinition serviceProcedureDefinition) {
        this.serviceProcedureDefinition = serviceProcedureDefinition;
    }

	public AuditableColEntity getD() {
		return d;
	}

	public void setD(AuditableColEntity d) {
		this.d = d;
	}
}
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
package tavant.twms.domain.claim;

import com.domainlanguage.money.Money;
import org.hibernate.annotations.*;
import org.hibernate.annotations.Parameter;
import org.springframework.core.style.ToStringCreator;
import tavant.twms.domain.common.AuditableColEntity;
import tavant.twms.domain.failurestruct.ServiceProcedure;
import tavant.twms.security.AuditableColumns;

import javax.persistence.*;
import javax.persistence.Entity;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Filters({
        @Filter(name = "excludeInactive")
})
public class LaborDetail implements AuditableColumns {
    @Id
    @GeneratedValue(generator = "LaborDetail")
    @GenericGenerator(name = "LaborDetail", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
            @Parameter(name = "sequence_name", value = "LABOR_DETAIL_SEQ"),
            @Parameter(name = "initial_value", value = "1000"),
            @Parameter(name = "increment_size", value = "20")})
    private Long id;

    @Version
    private int version;

    // TODO - Need to get rid of Job & hours spent once we
    // move to the new UI.
    @ManyToOne(fetch = FetchType.LAZY)
    private Job jobPerformed;

    private BigDecimal hoursSpent;

    @ManyToOne(fetch = FetchType.LAZY)
    private ServiceProcedure serviceProcedure;

    private BigDecimal additionalLaborHours;

    private String reasonForAdditionalHours;

    // TODO: Need to find a better place to persist this info
    // Will be populated only incase of Campaign Claims.
    private BigDecimal specifiedHoursInCampaign;

    private BigDecimal laborHrsEntered;

    @Type(type = "tavant.twms.infra.MoneyUserType")
    @Columns(columns = {@Column(name = "labor_rate_amt"), @Column(name = "labor_rate_curr")})
    private Money laborRate;

    @OneToMany(fetch = FetchType.LAZY)
    @Cascade({org.hibernate.annotations.CascadeType.ALL,
            org.hibernate.annotations.CascadeType.DELETE_ORPHAN})
    @JoinTable(name = "labor_detail_clm_attr")
    private List<ClaimAttributes> claimAttributes = new ArrayList<ClaimAttributes>();

    @Embedded
    @Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
    private AuditableColEntity d = new AuditableColEntity();

    public LaborDetail(Job jobPerformed, BigDecimal hoursSpent) {
        this.jobPerformed = jobPerformed;
        this.hoursSpent = hoursSpent;

        // FIX-ME: The labor rate is determine as part of payment calculation.
        // It is not part of job definition, this is incorrect.
        if (jobPerformed != null) {
            this.laborRate = jobPerformed.getDefinition().getLaborRate();
        }
    }

    @Transient
    private Boolean emptyAdditionalHours;

    public LaborDetail() {

    }

    /**
     * @return the hoursSpent
     */
    public BigDecimal getHoursSpent() {
        if (this.serviceProcedure == null && hoursSpent != null
                || hoursSpent != null) {
            return hoursSpent;
        }
        return new BigDecimal(this.serviceProcedure != null ? this.serviceProcedure
                .getSuggestedLabourHours() : 0);
    }

    public BigDecimal getHoursSpentForCampaignClaim() {
        return this.hoursSpent;
    }

    public BigDecimal getTotalHours(Boolean stdLaborEnabled) {
        if (!stdLaborEnabled) {
            return laborHrsEntered;
        } else {
            BigDecimal totalHours = hoursSpent;
            if (this.additionalLaborHours == null) {
                return totalHours;
            }
            totalHours = totalHours.add(additionalLaborHours);
            return totalHours;
        }
    }

    public BigDecimal getTotalHours() {
        BigDecimal totalHours = BigDecimal.ZERO;
        hoursSpent = hoursSpent == null ? BigDecimal.ZERO : hoursSpent;
        additionalLaborHours = additionalLaborHours == null ? BigDecimal.ZERO : additionalLaborHours;
        laborHrsEntered = laborHrsEntered == null ? BigDecimal.ZERO : laborHrsEntered;
        totalHours = totalHours.add(additionalLaborHours).add(laborHrsEntered).add(hoursSpent);
        return totalHours;
    }

    /**
     * @param hoursSpent the hoursSpent to set
     */
    public void setHoursSpent(BigDecimal hoursSpent) {
        if (hoursSpent != null) {
            this.hoursSpent = hoursSpent;
        } else {
            this.hoursSpent = new BigDecimal(
                    this.serviceProcedure != null ? this.serviceProcedure
                            .getSuggestedLabourHours() : 0);
        }
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

    /**
     * @return the jobCode
     */
    public Job getJobPerformed() {
        return this.jobPerformed;
    }

    /**
     * @param jobCode the jobCode to set
     */
    public void setJobPerformed(Job jobCode) {
        this.jobPerformed = jobCode;
    }

    /**
     * @return the laborRate
     */
    public Money getLaborRate() {
        return this.laborRate;
    }

    /**
     * @param laborRate the laborRate to set
     */
    public void setLaborRate(Money laborRate) {
        this.laborRate = laborRate;
    }

    public Money cost(Boolean stdLaborEnabled) {
        return this.laborRate.times(getTotalHours(stdLaborEnabled));
    }
    
    public Money acceptedCost(BigDecimal hour) {
        return this.laborRate.times(hour);
    }

    public BigDecimal getAdditionalLaborHours() {
        return this.additionalLaborHours;
    }

    public void setAdditionalLaborHours(BigDecimal additionalLaborHours) {
        this.additionalLaborHours = additionalLaborHours;
    }

    public ServiceProcedure getServiceProcedure() {
        return this.serviceProcedure;
    }

    public void setServiceProcedure(ServiceProcedure serviceProcedure) {
        this.serviceProcedure = serviceProcedure;
    }

    public String getReasonForAdditionalHours() {
        return this.reasonForAdditionalHours;
    }

    public void setReasonForAdditionalHours(String reasonForAdditionalHours) {
        this.reasonForAdditionalHours = reasonForAdditionalHours;
    }

    @Override
    public String toString() {
        return new ToStringCreator(this).append("id", this.id).append("hours spent",
                this.hoursSpent).append("labor rate", this.laborRate).toString();
    }

    public BigDecimal getSpecifiedHoursInCampaign() {
        return this.specifiedHoursInCampaign;
    }

    public void setSpecifiedHoursInCampaign(BigDecimal specifiedHoursInCampaign) {
        this.specifiedHoursInCampaign = specifiedHoursInCampaign;
    }

    public List<ClaimAttributes> getClaimAttributes() {
        return claimAttributes;
    }

    public void setClaimAttributes(List<ClaimAttributes> claimAttributes) {
        this.claimAttributes = claimAttributes;
    }

    /**
     * @param hoursSpent for Multi Claims
     */
    public void setHoursSpentForMultiClaim(BigDecimal hoursSpent) {
        this.hoursSpent = hoursSpent;
    }

    /**
     * @param additionalhoursSpent for Multi Claims
     */
    public void setAdditionalHoursSpentForMultiClaim(BigDecimal additionalLaborHours) {
        this.additionalLaborHours = additionalLaborHours;
    }

    public Boolean getEmptyAdditionalHours() {
        return emptyAdditionalHours;
    }

    public void setEmptyAdditionalHours(Boolean emptyAdditionalHours) {
        this.emptyAdditionalHours = emptyAdditionalHours;
    }

    public AuditableColEntity getD() {
        return d;
    }

    public void setD(AuditableColEntity d) {
        this.d = d;
    }

    public BigDecimal getLaborHrsEntered() {
        return laborHrsEntered;
    }

    public void setLaborHrsEntered(BigDecimal laborHrsEntered) {
        this.laborHrsEntered = laborHrsEntered;
    }


    public LaborDetail clone() {
        LaborDetail laborDetail = new LaborDetail();
        laborDetail.setJobPerformed(jobPerformed);
        laborDetail.setHoursSpent(hoursSpent);
        laborDetail.setServiceProcedure(serviceProcedure);
        laborDetail.setAdditionalLaborHours(additionalLaborHours);
        laborDetail.setReasonForAdditionalHours(reasonForAdditionalHours);
        laborDetail.setSpecifiedHoursInCampaign(specifiedHoursInCampaign);
        laborDetail.setLaborHrsEntered(laborHrsEntered);
        laborDetail.setLaborRate(laborRate);
        for (ClaimAttributes claimAttributes : this.claimAttributes) {
            laborDetail.getClaimAttributes().add(claimAttributes.clone());
        }
        return laborDetail;
    }
}

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
package tavant.twms.domain.claim.payment.rates;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Version;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.Filters;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.IndexColumn;
import org.hibernate.annotations.ParamDef;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.springframework.util.StringUtils;

import tavant.twms.domain.bu.BusinessUnitAware;
import tavant.twms.domain.bu.BusinessUnitInfo;
import tavant.twms.domain.claim.Criteria;
import tavant.twms.domain.common.AuditableColEntity;
import tavant.twms.security.AuditableColumns;



/**
 * @author radhakrishnan.j
 * 
 */
@Entity
@FilterDef(name = "bu_name", parameters = { @ParamDef(name = "name", type = "string") })
@Filter(name = "bu_name", condition = "business_unit_info in (:name)")
@Filters({
  @Filter(name="excludeInactive")
})
public class LaborRates  implements BusinessUnitAware,AuditableColumns{
    @Id
    @GeneratedValue(generator = "LaborRates")
	@GenericGenerator(name = "LaborRates", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "LABOR_RATES_SEQ"),
			@Parameter(name = "initial_value", value = "1000"),
			@Parameter(name = "increment_size", value = "20") })
    private Long id;

    @Version
    private int version;

    @Embedded
    @AttributeOverrides( {
            @AttributeOverride(name = "relevanceScore", column = @Column(name = "relevance_score")),
            @AttributeOverride(name = "warrantyType", column = @Column(name = "warranty_type")),
            @AttributeOverride(name = "claimType", column = @Column(name = "claim_type")),
            @AttributeOverride(name = "productName", column = @Column(name = "product_name")),
            @AttributeOverride(name = "identifier", column = @Column(name = "identifier")),
            @AttributeOverride(name = "wntyTypeName", column = @Column(name = "wnty_type_name")),
            @AttributeOverride(name = "clmTypeName", column = @Column(name = "clm_type_name"))})
   
    private Criteria forCriteria = new Criteria();

	@OneToMany(fetch = FetchType.LAZY)
	@org.hibernate.annotations.Cascade({
			org.hibernate.annotations.CascadeType.ALL,
			org.hibernate.annotations.CascadeType.DELETE_ORPHAN })
	@IndexColumn(name = "list_index", nullable = false)
	@JoinColumn(name = "LABOR_RATES")
	private List<LaborRateAudit> laborRateAudits = new ArrayList<LaborRateAudit>();
	    
    @OneToMany(mappedBy = "laborRates", fetch = FetchType.LAZY)
	@Cascade( { org.hibernate.annotations.CascadeType.ALL,
			org.hibernate.annotations.CascadeType.DELETE_ORPHAN })
	@Column(nullable = false)	
	@Filter(name="excludeInactive")
	private List<LaborRate> rates = new ArrayList<LaborRate>();
    
	@Embedded
	@Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	private AuditableColEntity d = new AuditableColEntity();
    
    private String customerType;
    
    public Criteria getCriteria() {
        return this.forCriteria;
    }

    public Criteria getForCriteria() {
        return this.forCriteria;
    }

    public void setForCriteria(Criteria forCriteria) {
        this.forCriteria = forCriteria;
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

    public String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append("forCriteria").append('=').append(this.forCriteria);
         return buf.toString();
    }

    public void computeCriteriaRelevanceScore() {
        int score = 0;
        int weight = 1;

        if (this.forCriteria.isProductTypeSpecified()) {
            score = score + weight;
        }

        weight = weight * 2;
        if (this.forCriteria.isWarrantyTypeSpecified()) {
            score = score + weight;
        }

        weight = weight * 2;
        if (this.forCriteria.isClaimTypeSpecified()) {
            score = score + weight;
        }

        weight = weight * 2;
        if (this.forCriteria.isDealerGroupSpecified()) {
            score = score + weight;
        }

        weight = weight * 2;
        if (this.forCriteria.isDealerSpecified()) {
            score = score + weight;
        }

        this.forCriteria.setRelevanceScore(score);
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

	public String getCustomerType() {
		return customerType;
	}

	public void setCustomerType(String customerType) {
		this.customerType = customerType;
	}
	
	public boolean isCustomerSpecified(){
		if(StringUtils.hasText(customerType)){
			if(!"ALL".equalsIgnoreCase(this.customerType)){
				return true; 
			}
		}
		return false;
	}

	public List<LaborRate> getRates() {
		return rates;
	}

	public void setRates(List<LaborRate> rates) {
		this.rates = rates;
	}
	
	public AuditableColEntity getD() {
		return d;
	}

	public void setD(AuditableColEntity d) {
		this.d = d;
	}

	public List<LaborRateAudit> getLaborRateAudits() {
		 if (this.laborRateAudits != null && this.laborRateAudits.size() > 0) {
	            Collections.sort(this.laborRateAudits,Collections.reverseOrder());
	        }
		return laborRateAudits;
	}
	
	public void setLaborRateAudits(List<LaborRateAudit> laborRateAudits) {
		this.laborRateAudits = laborRateAudits;
	}

	
	

}
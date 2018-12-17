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

import java.util.ArrayList;
import java.util.Currency;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Version;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.Filters;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.springframework.core.style.ToStringCreator;

import com.domainlanguage.money.Money;

import tavant.twms.domain.common.AuditableColEntity;
import tavant.twms.domain.common.PropertiesWithNestedCurrencyFields;
import tavant.twms.security.AuditableColumns;

@Entity
@Filters({
  @Filter(name="excludeInactive")
})
@PropertiesWithNestedCurrencyFields( { "travelDetails","campaignLaborLimits","campaignSectionPrice" })
public class CampaignServiceDetail implements AuditableColumns{

    @Id
    @GeneratedValue(generator = "CampaignServiceDetail")
	@GenericGenerator(name = "CampaignServiceDetail", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "CAMPAIGN_SERVICE_SEQ"),
			@Parameter(name = "initial_value", value = "1000"),
			@Parameter(name = "increment_size", value = "20") })
    private Long id;

    @Version
    private int version;

    @OneToOne(fetch = FetchType.LAZY, cascade = { CascadeType.ALL })
    @Cascade( { org.hibernate.annotations.CascadeType.DELETE_ORPHAN })
    private CampaignTravelDetail travelDetails;

    @OneToMany(cascade = { CascadeType.ALL })
    @Cascade( { org.hibernate.annotations.CascadeType.DELETE_ORPHAN })
    @JoinTable(name = "CAMPAIGN_LABOR_LIMITS")
    private List<CampaignLaborDetail> campaignLaborLimits = new ArrayList<CampaignLaborDetail>();
    
    @OneToMany(fetch = FetchType.LAZY)
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    @JoinColumn(name = "CAMPAIGN_SERVICE_DETAIL")
    protected List<CampaignSectionPrice> campaignSectionPrice = new ArrayList<CampaignSectionPrice>();

    @Embedded
	@Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	private AuditableColEntity d = new AuditableColEntity();

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
     * @return the travelDetails
     */
    public CampaignTravelDetail getTravelDetails() {
        return this.travelDetails;
    }

    /**
     * @param travelDetails the travelDetails to set
     */
    public void setTravelDetails(CampaignTravelDetail travelDetails) {
        this.travelDetails = travelDetails;
    }

    @Override
    public String toString() {
        return new ToStringCreator(this).append("id", this.id).toString();
    }

    public List<CampaignLaborDetail> getCampaignLaborLimits() {
        return this.campaignLaborLimits;
    }

    public void setCampaignLaborLimits(List<CampaignLaborDetail> laborLimits) {
        this.campaignLaborLimits = laborLimits;
    }

	public AuditableColEntity getD() {
		return d;
	}

	public void setD(AuditableColEntity d) {
		this.d = d;
	}

	public List<CampaignSectionPrice> getCampaignSectionPrice() {
		return campaignSectionPrice;
	}

	public void setCampaignSectionPrice(
			List<CampaignSectionPrice> campaignSectionPrice) {
		this.campaignSectionPrice = campaignSectionPrice;
	}
	
	public Map<String,CampaignSectionPrice> getCampaignPriceForSection(String sectionName){
		Map<String, CampaignSectionPrice> campaignPriceForSection = new HashMap<String, CampaignSectionPrice>();
		for(CampaignSectionPrice campaignSectionPrice : this.campaignSectionPrice){
			if(campaignSectionPrice.getSectionName().equalsIgnoreCase(sectionName)){
				campaignPriceForSection.put(sectionName, campaignSectionPrice);
			}
		}
		return campaignPriceForSection;
	}
	
	public Money getCampaignPriceForSectionAndCurrency(String sectionName, Currency currency){
		Money sectionPrice = null;
		for(CampaignSectionPrice campaignSectionPrice : this.campaignSectionPrice){
			if(campaignSectionPrice.getSectionName().equalsIgnoreCase(sectionName)
					&& campaignSectionPrice.getPricePerUnit().breachEncapsulationOfCurrency().equals(currency)){
				return campaignSectionPrice.getPricePerUnit();
			}
		}
		return sectionPrice;
	}
	
}

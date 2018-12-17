package tavant.twms.domain.orgmodel;

import static javax.persistence.CascadeType.ALL;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;
import org.springframework.core.style.ToStringCreator;
import org.springframework.util.StringUtils;

import tavant.twms.infra.HibernateCast;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;


/**
 * @author Priyank.Gupta
 *
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@SuppressWarnings("serial")
@FilterDef(name = "party_bu_name", parameters = { @ParamDef(name = "name", type = "string") })
@Filter(name = "party_bu_name", 
		condition = "id in (select bom.org from bu_org_mapping bom where bom.bu in (:name))")
@JsonAutoDetect
public class ServiceProvider extends Organization 
{
	
	private String serviceProviderNumber;
	
    private String salesDistrictCode;

    private String regionCode;

    @JsonIgnore
    private String primaryContactpersonFstName;

    @JsonIgnore
    private String primaryContactpersonLstName;

    private String status;

    private String companyType;

    @JsonIgnore
    private String siteNumber;

    @JsonIgnore
    private String submitCredit;

    @JsonIgnore
    private boolean certified;

    @JsonIgnore
    private boolean enterpriseDealer;
    
    @ManyToOne(fetch = FetchType.LAZY)
	private ServiceProvider partOf;
    
	@OneToMany(mappedBy = "partOf", cascade = { ALL }, fetch = FetchType.LAZY, orphanRemoval=true)
    @Cascade( { org.hibernate.annotations.CascadeType.ALL})
    private Set<ServiceProvider> consistsOf = new HashSet<ServiceProvider>();

    @Transient
    @JsonIgnore
    private List<Long> childDealersIds = new ArrayList<Long>();

    @Column(name="ALLOWED_NCR_WITH_30_DAYS")
    @JsonIgnore
    private Boolean allowedNCRWith30Days = Boolean.FALSE;
    
    
    @ManyToOne(fetch = FetchType.LAZY)
    private Organization ownedBy;
    
    
	public Boolean isAllowedNCRWith30Days() {
		if(allowedNCRWith30Days == null){
			return Boolean.FALSE;
		}
		return allowedNCRWith30Days;
	}

	public void setAllowedNCRWith30Days(Boolean allowedNCRWith30Days) {
		this.allowedNCRWith30Days = allowedNCRWith30Days;
	}
	
    public boolean getCertified() {
		return certified;
	}

	public void setCertified(boolean certified) {
		this.certified = certified;
	}
	

	
	@Transient
    @JsonIgnore
	public String getSalesDistrictCode() {
		return "";
	}

	public void setSalesDistrictCode(String salesDistrictCode) {
		this.salesDistrictCode = salesDistrictCode;
	}

	@Transient
    @JsonIgnore
    public String getRegionCode() {
		return "";
	}

	public void setRegionCode(String regionCode) {
		this.regionCode = regionCode;
	}



	@Transient
	public String getPrimaryContactpersonFstName() {
		return "";
	}

	public void setPrimaryContactpersonFstName(String primaryContactpersonFstName) {
		this.primaryContactpersonFstName = primaryContactpersonFstName;
	}

	@Transient
	public String getPrimaryContactpersonLstName() {
		return "";
	}

	public void setPrimaryContactpersonLstName(String primaryContactpersonLstName) {
		this.primaryContactpersonLstName = primaryContactpersonLstName;
	}

	@Transient
	public String getStatus() {
		return "";
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@Transient
	public String getCompanyType() {
		return "";
	}

	public void setCompanyType(String companyType) {
		this.companyType = companyType;
	}

	@Transient
    @JsonIgnore
    public String getHrsOperationWeekday() {
		return "";
	}

	/*public void setHrsOperationWeekday(String hrsOperationWeekday) {
		this.hrsOperationWeekday = hrsOperationWeekday;
	}*/

	@Transient
	public String getSiteNumber() {
		return "";
	}

	public void setSiteNumber(String siteNumber) {
		this.siteNumber = siteNumber;
	}

	public String getSubmitCredit() {
		return submitCredit;
	}

	public void setSubmitCredit(String submitCredit) {
		this.submitCredit = submitCredit;
	}

	/**
	 * This api is written because due to major refactoring now we expect all objects to return a dealer number
	 * which is equivalent to their own numbers. This is also stored as service provider number so we are returning
	 * that
	 * 
	 * @return
	 */
	
	@JsonIgnore
	public String getDealerNumber() 
	{
		return serviceProviderNumber;
	}

	/**
	 * @return
	 */
	public String getServiceProviderNumber() 
	{
		return serviceProviderNumber;
	}

	/**
	 * @param serviceProviderNumber
	 */
	public void setServiceProviderNumber(String serviceProviderNumber) 
	{
		this.serviceProviderNumber = serviceProviderNumber;
	}
	
	@Override
	public String toString() {
		return new ToStringCreator(this).append("service provider number", serviceProviderNumber)
				.toString();
	}

    @JsonIgnore
    public boolean getSubmitCreditBooleanVal(){
		if(!StringUtils.hasText(this.submitCredit)){
			return Boolean.TRUE;
		}
		
		if("N".equalsIgnoreCase(this.submitCredit)){
			return Boolean.FALSE;
		}
		
		return Boolean.TRUE;
	}
	
	@Override
	public boolean equals(Object serviceProvider) {
		if (serviceProvider!=null&& ((ServiceProvider) serviceProvider).getId() != null && ((ServiceProvider) serviceProvider).getId().equals(this.getId())) {
			return true;
		} else {
			return false;
		}
	}

	public ServiceProvider getPartOf() {
		return partOf;
	}

	public void setPartOf(ServiceProvider partOf) {
		this.partOf = partOf;
	}

    @JsonIgnore
    public List<ServiceProvider> getAllChildDealers() {
        List<ServiceProvider> childDealers = new ArrayList<ServiceProvider>();
        for (ServiceProvider dealership : consistsOf) {
            if (dealership.isEnterpriseDealer()) {
                childDealers.addAll(dealership.getAllChildDealers());
            } else {
                childDealers.add(dealership);
            }
        }
        return childDealers;
    }

	public Set<ServiceProvider> getConsistsOf() {
		return consistsOf;
	}

	public void setConsistsOf(Set<ServiceProvider> consistsOf) {
		this.consistsOf = consistsOf;
	}

	public boolean isEnterpriseDealer() {
		return enterpriseDealer;
	}

	public void setEnterpriseDealer(boolean enterpriseDealer) {
		this.enterpriseDealer = enterpriseDealer;
	}

    public void setChildDealersIds() {
        if(childDealersIds.isEmpty() && isEnterpriseDealer()){
            for(ServiceProvider dealer: getAllChildDealers()){
                childDealersIds.add(dealer.getId());
            }
        }
    }

    public List<Long> getChildDealersIds() {
        return childDealersIds;
    }
    
    public String getMarketingGroup(){    	
    	Dealership dealerShip = new HibernateCast<Dealership>()
				.cast(this);
    	return dealerShip.getMarketingGroup();
    }

	public Organization getOwnedBy() {
		return ownedBy;
	}

	public void setOwnedBy(Organization ownedBy) {
		this.ownedBy = ownedBy;
	}
    
}

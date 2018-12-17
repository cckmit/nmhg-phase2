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
package tavant.twms.domain.orgmodel;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.Currency;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.core.style.ToStringCreator;

import tavant.twms.domain.common.Purpose;

/**
 * @author radhakrishnan.j
 *
 */
@Entity
@SuppressWarnings("serial")
@Inheritance(strategy=InheritanceType.JOINED)
public class Dealership extends ServiceProvider implements Serializable  {

	private String dealerNumber;

    private String salesDistrictCode;

    private String regionCode;

    @JsonIgnore
    private String primaryContactpersonFstName;

    @JsonIgnore
    private String primaryContactpersonLstName;

    private String status;

    @JsonIgnore
    private String companyType;

    private String siteNumber;
    
    @ManyToOne(fetch = FetchType.LAZY)
	private ServiceProvider partOf;
    
    private Currency preferredCurrency;

	@ManyToMany(fetch = FetchType.LAZY, mappedBy = "includedDealers")
	private Set<DealerGroup> belongsToGroups = new HashSet<DealerGroup>();

    @Column(name="marketing_group")
    @JsonIgnore
    private String marketingGroup;

    @Column(name="brand")
    private String brand;

    @Column(name="selling_location")
    @JsonIgnore
    private String sellingLocation;

    @Column(name="business_area")
    @JsonIgnore
    private String businessArea;
    
    @Column(name="fleet_business_area")
    @JsonIgnore
    private String fleetBusinessArea;

    @Column(name="dealer_type")
    @JsonIgnore
    private String serviceProviderType;

    @Column(name="dealer_type_desc")
    @JsonIgnore
    private String serviceProviderDescription;

    @OneToOne
    @JoinColumn(name="dual_dealer")
    @JsonIgnore
    private Dealership dualDealer;

    @Column(name="xml_footer")
    @JsonIgnore
    String xmlacknowledgementFooter;

    @Column(name="network")
    @JsonIgnore
    String network;

    @Column(name="language")
    @JsonIgnore
    String language;
    
    @Column(name="sales_territory_name")
    @JsonIgnore
    String SalesTerritoryName;
    
    @Column(name="enterprise_dealer")
    @JsonIgnore
    private Boolean enterpriseDealer;

	public boolean isEnterpriseDealer() {
		return enterpriseDealer;
	}

	public void setEnterpriseDealer(boolean enterpriseDealer) {
		this.enterpriseDealer = enterpriseDealer;
	}

	/**
	 * @return the dealerNumber
	 */
	public String getDealerNumber() {
		return dealerNumber;
	}

	/**
	 * @param dealerNumber
	 *            the dealerNumber to set
	 */
	public void setDealerNumber(String dealerNumber) {
		this.dealerNumber = dealerNumber;
	}

	@Override
	public String getRegionCode() {
		return regionCode;
	}

	@Override
	public void setRegionCode(String regionCode) {
		this.regionCode = regionCode;
	}

	@Override
	public String getSalesDistrictCode() {
		return salesDistrictCode;
	}

	@Override
	public void setSalesDistrictCode(String salesDistrictCode) {
		this.salesDistrictCode = salesDistrictCode;
	}

	/**
	 * @return the belongsToGroups
	 */

	public Set<DealerGroup> getBelongsToGroups() {
		return belongsToGroups;
	}

	/**
	 * @param belongsToGroups
	 *            the belongsToGroups to set
	 */
	public void setBelongsToGroups(Set<DealerGroup> belongsToGroups) {
		this.belongsToGroups = belongsToGroups;
	}

	@Override
	public String toString() {
		return new ToStringCreator(this).append("dealer number", dealerNumber)
				.toString();
	}

	public DealerGroup belongsToGroupForPurposeIfAny(String purposeName) {
		for (DealerGroup aGroup : getBelongsToGroups()) {
			Set<Purpose> purposes = aGroup.getScheme().getPurposes();
			for (Purpose purpose : purposes) {
				if (purpose.getName().equals(purposeName)) {
					return aGroup;
				}
			}
		}
		return null;
	}

	@Override
	public String getPrimaryContactpersonFstName() {
		return primaryContactpersonFstName;
	}

	@Override
	public void setPrimaryContactpersonFstName(String primaryContactpersonFstName) {
		this.primaryContactpersonFstName = primaryContactpersonFstName;
	}

	@Override
	public String getPrimaryContactpersonLstName() {
		return primaryContactpersonLstName;
	}

	@Override
	public void setPrimaryContactpersonLstName(String primaryContactpersonLstName) {
		this.primaryContactpersonLstName = primaryContactpersonLstName;
	}

	@Override
	public String getStatus() {
		return status;
	}

	@Override
	public void setStatus(String status) {
		this.status = status;
	}

	@Override
	public String getCompanyType() {
		return companyType;
	}

	@Override
	public void setCompanyType(String companyType) {
		this.companyType = companyType;
	}

	@Override
	public String getSiteNumber() {
		return siteNumber;
	}

	@Override
	public void setSiteNumber(String siteNumber) {
		this.siteNumber = siteNumber;
	}

	@Transient
    @JsonIgnore
    public String getAddressBookType(){
		return AddressBookType.DEALER.getType();
	}

	public Currency getPreferredCurrency() {
		return preferredCurrency;
	}

	public void setPreferredCurrency(Currency preferredCurrency) {
		this.preferredCurrency = preferredCurrency;
	}

    public String getMarketingGroup() {
        return marketingGroup;
    }

    public void setMarketingGroup(String marketingGroup) {
        this.marketingGroup = marketingGroup;
    }

    public String getSellingLocation() {
        return sellingLocation;
    }

    public void setSellingLocation(String sellingLocation) {
        this.sellingLocation = sellingLocation;
    }

    public String getBusinessArea() {
        return businessArea;
    }

    public void setBusinessArea(String businessArea) {
        this.businessArea = businessArea;
    }

    public String getFleetBusinessArea() {
		return fleetBusinessArea;
	}

	public void setFleetBusinessArea(String fleetBusinessArea) {
		this.fleetBusinessArea = fleetBusinessArea;
	}

	public String getServiceProviderType() {
        return serviceProviderType;
    }

    public void setServiceProviderType(String serviceProviderType) {
        this.serviceProviderType = serviceProviderType;
    }

    public String getServiceProviderDescription() {
        return serviceProviderDescription;
    }

    public void setServiceProviderDescription(String serviceProviderDescription) {
        this.serviceProviderDescription = serviceProviderDescription;
    }

    public Dealership getDualDealer() {
        return dualDealer;
    }

    public void setDualDealer(Dealership dualDealer) {
        this.dualDealer = dualDealer;
    }

    @JsonIgnore
    public String getSalesTerritoryName() {
		return SalesTerritoryName;
	}

	public void setSalesTerritoryName(String salesTerritoryName) {
		SalesTerritoryName = salesTerritoryName;
	}

	public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getXmlacknowledgementFooter() {
        return xmlacknowledgementFooter;
    }

    public void setXmlacknowledgementFooter(String xmlacknowledgementFooter) {
        this.xmlacknowledgementFooter = xmlacknowledgementFooter;
    }

    public String getNetwork() {
        return network;
    }

    public void setNetwork(String network) {
        this.network = network;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }
    
    @Override
    public ServiceProvider getPartOf() {
		return partOf;
	}

    @Override
	public void setPartOf(ServiceProvider partOf) {
		this.partOf = partOf;
	}

}


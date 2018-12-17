/**
 * 
 */
package tavant.twms.domain.orgmodel;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.springframework.core.style.ToStringCreator;
import org.springframework.util.StringUtils;

/**
 * @author mritunjay.kumar
 *
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "ORGANIZATION_ADDRESS")
@SuppressWarnings("serial")
public class OrganizationAddress extends Address implements Comparable<OrganizationAddress> {
	private String siteNumber;
	private String location;
	private String name;
	private Boolean addressActive;
	
	@Transient
	private String locationWithBrand;
	

	public String getSiteNumber() {
		return siteNumber;
	}
	public void setSiteNumber(String siteNumber) {
		this.siteNumber = siteNumber;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	
	@Override
	public String toString() {
		return new ToStringCreator(this).append("id", getId()).append(
				"address line1", getAddressLine1()).append("address line2",
						getAddressLine2()).append("city", getCity()).append("state", getState())
				.append("country", getCountry()).append("zip code", getZipCode())
				.append("siteNumber","siteNumber").append("location","location")
				.toString();
	}
	public int compareTo(OrganizationAddress orgAddress) {
		String site1 = siteNumber==null ? "" : siteNumber;
		String site2 = orgAddress.getSiteNumber()==null ? "" : orgAddress.getSiteNumber();
		return site1.compareTo(site2);
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public String getShipToCodeAppended() {
		if (siteNumber != null) {
            if (siteNumber.length() >= 13) {
                  String dealerSiteNumber = siteNumber.substring(9,
                                13);
                  return dealerSiteNumber+"-"+location;
            } else if (siteNumber.length() >= 9
                         && siteNumber.length() < 13) {
                  String dealerSiteNumber = siteNumber.substring(9,
                		  siteNumber.length());
                  return dealerSiteNumber+"-"+location;
            }else{
                  return siteNumber+"-"+location;
            }
     }
     return "";
	}

	public Boolean isAddressActive() {
		return addressActive;
	}
	public void setAddressActive(Boolean addressActive) {
		this.addressActive = addressActive;
	}

    public String getSiteNumberForDisplay(){
        if(StringUtils.hasText(siteNumber)){
            return siteNumber.substring(9, 13);
        }
        return siteNumber;
    }
    public String getSiteNumberForDisplay(String siteNumber){
        if(StringUtils.hasText(siteNumber)){
            return siteNumber.substring(9, 13);
        }
        return siteNumber;
    }
	public String getLocationWithBrand() {
		return locationWithBrand;
	}
	public void setLocationWithBrand(String locationWithBrand) {
		this.locationWithBrand = locationWithBrand;
	}
    
}

/**
 *
 */
package tavant.twms.domain.orgmodel;

import java.io.Serializable;

import org.springframework.core.style.ToStringCreator;

/**
 * @author mritunjay.kumar
 *
 */
public enum AddressBookType implements Serializable {

	NATIONALACCOUNT("NationalAccount"), GOVERNMENTACCOUNT("GovernmentAccount"), ENDCUSTOMER(
			"EndCustomer"), DEALERUSER("DealerUser"),SELF("Self"), INTERCOMPANY("InterCompany"),
			DEALERRENTAL("Dealer Rental"),DIRECTCUSTOMER("DirectCustomer"),DEALER("Dealer"),THIRDPARTY("ThirdParty"),
			FEDERAL_GOVERNMENT ("Federal Government"), 
			STATE_GOVERNMENT ("State Government"),
			COUNTY_GOVERNMENT("County Government"),              
			CTV_GOVERNMENT("City/Town/Village Government"), 
			HOMEOWNERS  ("Homeowners"), 
			BUSINESS  ("Business"),                      
			REGIONAL_ACCOUNT ("Regional Account"),
			DEMO("Demo"), GOVERNMENT_ACCOUNT("Government Account");

	private String type;

	private AddressBookType(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return new ToStringCreator(this).append("type", type).toString();
	}

}

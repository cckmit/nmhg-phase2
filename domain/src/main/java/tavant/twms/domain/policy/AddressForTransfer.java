/**
 * 
 */
package tavant.twms.domain.policy;

import java.sql.Types;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import org.hibernate.annotations.Filters;
import org.hibernate.annotations.Filter;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Version;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.springframework.core.style.ToStringCreator;

import tavant.twms.domain.common.AuditableColEntity;
import tavant.twms.domain.orgmodel.AddressType;
import tavant.twms.security.AuditableColumns;

/**
 * @author mritunjay.kumar
 *
 */
@Entity
@Filters({
  @Filter(name="excludeInactive")
})
public class AddressForTransfer implements AuditableColumns{
	
	@Id
	@GeneratedValue(generator = "AddressForTransfer")
	@GenericGenerator(name = "AddressForTransfer", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "ADDRESSFORTRANS_SEQ"),
			@Parameter(name = "initial_value", value = "1000"),
			@Parameter(name = "increment_size", value = "20") })
	private Long id;

	@Version
	private int version;
	
	@Type(type = "org.hibernate.type.EnumType", parameters = {
			@Parameter(name = "enumClass", value = "tavant.twms.domain.orgmodel.AddressType"),
			@Parameter(name = "type", value = "" + Types.VARCHAR) })
	private AddressType type;
	
	private String addressLine;
	
	private String addressLine2;
	
	private String addressLine3;
	
	private String city;

	private String state;

	private String country;

	private String zipCode;

	private String email;

	private String phone;

	private String secondaryPhone;
	
	private String contactPersonName;
	
	private String customerContactTitle;
	
	private String fax;
	
	private String county;
	
	@Column(name="COUNTY_CODE_NAME")
	private String countyCodeWithName;

	
	@Embedded
	@Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	private AuditableColEntity d = new AuditableColEntity();

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public AddressType getType() {
		return type;
	}

	public void setType(AddressType type) {
		this.type = type;
	}

	public String getAddressLine() {
		return addressLine;
	}

	public void setAddressLine(String addressLine) {
		this.addressLine = addressLine;
	}

	public String getAddressLine2() {
		return addressLine2;
	}

	public void setAddressLine2(String addressLine2) {
		this.addressLine2 = addressLine2;
	}

	public String getAddressLine3() {
		return addressLine3;
	}

	public void setAddressLine3(String addressLine3) {
		this.addressLine3 = addressLine3;
	}

	
	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getZipCode() {
		return zipCode;
	}

	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getSecondaryPhone() {
		return secondaryPhone;
	}

	public void setSecondaryPhone(String secondaryPhone) {
		this.secondaryPhone = secondaryPhone;
	}

	public String getContactPersonName() {
		return contactPersonName;
	}

	public void setContactPersonName(String contactPersonName) {
		this.contactPersonName = contactPersonName;
	}
	
	@Override
	public String toString() {
		return new ToStringCreator(this).append("id", id).append("type",type).append(
				"address line", addressLine).append("city", city).append("state", state)
				.append("country", country).append("zip code", zipCode)
				.toString();
	}

	public AuditableColEntity getD() {
		return d;
	}

	public void setD(AuditableColEntity d) {
		this.d = d;
	}
	
	public String getFax() {
		return fax;
	}

	public void setFax(String fax) {
		this.fax = fax;
	}

	public String getCustomerContactTitle() {
		return customerContactTitle;
	}

	public void setCustomerContactTitle(String customerContactTitle) {
		this.customerContactTitle = customerContactTitle;
	}

	public String getCounty() {
		return county;
	}

	public void setCounty(String county) {
		this.county = county;
	}

	public String getCountyCodeWithName() {
		return countyCodeWithName;
	}

	public void setCountyCodeWithName(String countyCodeWithName) {
		this.countyCodeWithName = countyCodeWithName;
	}

}

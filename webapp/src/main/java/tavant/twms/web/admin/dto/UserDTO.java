package tavant.twms.web.admin.dto;


public class UserDTO {

	private String userId;
	private String slmsCode;
	private String status;
	private String jobTitle;
	private String firstName;
	private String lastName;
	private String locale;
	private String email;
	private String phone;
	private String addressLine1;
	private String addressLine2;
	private String city;
	private String state;
	private String country;
	private String zipCode;
	private String[] roleNames;
	private String brand;
	private String mktGroup;
	private String[] dealerCodes;
	private String dealerName;

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getSlmsCode() {
		return slmsCode;
	}

	public void setSlmsCode(String slmsCode) {
		this.slmsCode = slmsCode;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getJobTitle() {
		return jobTitle;
	}

	public void setJobTitle(String jobTitle) {
		this.jobTitle = jobTitle;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getLocale() {
		return locale;
	}

	public void setLocale(String locale) {
		this.locale = locale;
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

	public String getAddressLine1() {
		return addressLine1;
	}

	public void setAddressLine1(String addressLine1) {
		this.addressLine1 = addressLine1;
	}

	public String getAddressLine2() {
		return addressLine2;
	}

	public void setAddressLine2(String addressLine2) {
		this.addressLine2 = addressLine2;
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

	public String[] getRoleNames() {
		return roleNames;
	}

	public void setRoleNames(String[] roleNames) {
		this.roleNames = roleNames;
	}

	public String getBrand() {
		return brand;
	}

	public void setBrand(String brand) {
		this.brand = brand;
	}

	public String getMktGroup() {
		return mktGroup;
	}

	public void setMktGroup(String mktGroup) {
		this.mktGroup = mktGroup;
	}

	public String[] getDealerCodes() {
		return dealerCodes;
	}

	public void setDealerCodes(String[] dealerCodes) {
		this.dealerCodes = dealerCodes;
	}

	public String getDealerName() {
		return dealerName;
	}

	public void setDealerName(String dealerName) {
		this.dealerName = dealerName;
	}
	
	
	
	@Override
	public String toString() {
		return "userId:"+userId
				+"slmsCode:"+slmsCode
				+",status:"+status
				+",jobTitle:"+jobTitle
				+",firstName:"+firstName
				+",lastName:"+lastName
				+",locale:"+locale
				+",email:"+email
				+",phone:"+phone
				+",addressLine1:"+addressLine1
				+",addressLine2:"+addressLine2
				+",city:"+city
				+",state:"+state
				+",country:"+country
				+",zipCode:"+zipCode
				+",roleNames:"+roleNames
				+",brand:"+brand
				+",mktGroup:"+mktGroup
				+",dealerCodes:"+dealerCodes
				+",dealerName:"+dealerName;
	}

}

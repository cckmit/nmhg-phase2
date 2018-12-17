package tavant.twms.domain.upload;

import tavant.twms.domain.orgmodel.User;

public class CustomerStaging implements StagingData{
	
	
	private Long id;
	private Long fileUploadMgtId;
	private String errorStatus;
	private String errorCode;
	private String uploadStatus;
	private String uploadError;
	private String uploadDate;
	private String customerName;
	private String customerNumber;
	private String customerType;
	private String currency;
    private String contactPerson;
    private User uploadedBy;

    public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getFileUploadMgtId() {
		return fileUploadMgtId;
	}

	public void setFileUploadMgtId(Long fileUploadMgtId) {
		this.fileUploadMgtId = fileUploadMgtId;
	}

	public String getErrorStatus() {
		return errorStatus;
	}

	public void setErrorStatus(String errorStatus) {
		this.errorStatus = errorStatus;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public String getUploadStatus() {
		return uploadStatus;
	}

	public void setUploadStatus(String uploadStatus) {
		this.uploadStatus = uploadStatus;
	}

	public String getUploadError() {
		return uploadError;
	}

	public void setUploadError(String uploadError) {
		this.uploadError = uploadError;
	}

	public String getUploadDate() {
		return uploadDate;
	}

	public void setUploadDate(String uploadDate) {
		this.uploadDate = uploadDate;
	}

	private String firstName;
	
	private String lastName;
	
	private String addressline1;
	
	private String addressline2;
	
	private String addressline3;
	private String addressline4;
	private String city;
	private String state;
	private String zipCode;
	private String zipCodeExtension;
	private String country;
	private String phone;
	private String secondaryPhone;
	private String fax;
	private String email;
	private String status;
	private String businessUnit;
	private String updates;
	private String dealerFamily;
	private String dealerSite;
	private String classification;

	public String getCustomerNumber() {
		return customerNumber;
	}

	public void setCustomerNumber(String customerNumber) {
		this.customerNumber = customerNumber;
	}

	
	public String getCustomerType() {
		return customerType;
	}

	public void setCustomerType(String customerType) {
		this.customerType = customerType;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
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

	public String getAddressline1() {
		return addressline1;
	}

	public void setAddressline1(String addressline1) {
		this.addressline1 = addressline1;
	}

	public String getAddressline2() {
		return addressline2;
	}

	public void setAddressline2(String addressline2) {
		this.addressline2 = addressline2;
	}

	public String getAddressline3() {
		return addressline3;
	}

	public void setAddressline3(String addressline3) {
		this.addressline3 = addressline3;
	}

	public String getAddressline4() {
		return addressline4;
	}

	public void setAddressline4(String addressline4) {
		this.addressline4 = addressline4;
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

	public String getZipCode() {
		return zipCode;
	}

	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}

	public String getZipCodeExtension() {
		return zipCodeExtension;
	}

	public void setZipCodeExtension(String zipCodeExtension) {
		this.zipCodeExtension = zipCodeExtension;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
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

	public String getFax() {
		return fax;
	}

	public void setFax(String fax) {
		this.fax = fax;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getBusinessUnit() {
		return businessUnit;
	}

	public void setBusinessUnit(String businessUnit) {
		this.businessUnit = businessUnit;
	}

	public String getUpdates() {
		return updates;
	}

	public void setUpdates(String updates) {
		this.updates = updates;
	}

	public String getDealerFamily() {
		return dealerFamily;
	}

	public void setDealerFamily(String dealerFamily) {
		this.dealerFamily = dealerFamily;
	}

	public String getDealerSite() {
		return dealerSite;
	}

	public void setDealerSite(String dealerSite) {
		this.dealerSite = dealerSite;
	}

	public String getClassification() {
		return (classification == null || "".equals(classification.trim())) ? customerType : classification;
	}

	public void setClassification(String classification) {
		this.classification = classification;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public String getCustomerName() {
		return customerName;
	}

    public String getContactPerson() {
        return contactPerson;
    }

    public void setContactPerson(String contactPerson) {
        this.contactPerson = contactPerson;
    }

    public User getUploadedBy() {
        return uploadedBy;
    }

	public void setUploadedBy(User uploadedBy) {
		this.uploadedBy = uploadedBy;
	}

	
}

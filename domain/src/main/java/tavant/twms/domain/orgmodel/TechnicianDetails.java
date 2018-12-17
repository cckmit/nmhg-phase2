package tavant.twms.domain.orgmodel;

import org.hibernate.annotations.Type;

import com.domainlanguage.time.CalendarDate;

public class TechnicianDetails {

	@Type(type = "tavant.twms.infra.CalendarDateUserType")
	private CalendarDate dateOfHire;
	
	@Type(type = "tavant.twms.infra.CalendarDateUserType")
	private CalendarDate certificationFromDate;
	
	@Type(type = "tavant.twms.infra.CalendarDateUserType")
	private CalendarDate certificationToDate;
	
	@Type(type = "tavant.twms.infra.CalendarDateUserType")
	private CalendarDate dateOfRenewal;
	
	private String serviceManagerName;
	
	private String serviceManagerPhone;
	
	private String serviceManagerAddress;
	
	private String comments;
	
	private String serviceManagerFax;
	
	private String serviceManagerEmail;

	public CalendarDate getDateOfHire() {
		return dateOfHire;
	}

	public void setDateOfHire(CalendarDate dateOfHire) {
		this.dateOfHire = dateOfHire;
	}

	public CalendarDate getCertificationFromDate() {
		return certificationFromDate;
	}

	public void setCertificationFromDate(CalendarDate certificationFromDate) {
		this.certificationFromDate = certificationFromDate;
	}

	public CalendarDate getCertificationToDate() {
		return certificationToDate;
	}

	public void setCertificationToDate(CalendarDate certificationToDate) {
		this.certificationToDate = certificationToDate;
	}

	public CalendarDate getDateOfRenewal() {
		return dateOfRenewal;
	}

	public void setDateOfRenewal(CalendarDate dateOfRenewal) {
		this.dateOfRenewal = dateOfRenewal;
	}

	public String getServiceManagerName() {
		return serviceManagerName;
	}

	public void setServiceManagerName(String serviceManagerName) {
		this.serviceManagerName = serviceManagerName;
	}

	public String getServiceManagerPhone() {
		return serviceManagerPhone;
	}

	public void setServiceManagerPhone(String serviceManagerPhone) {
		this.serviceManagerPhone = serviceManagerPhone;
	}

	public String getServiceManagerAddress() {
		return serviceManagerAddress;
	}

	public void setServiceManagerAddress(String serviceManageraddress) {
		this.serviceManagerAddress = serviceManageraddress;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public String getServiceManagerFax() {
		return serviceManagerFax;
	}

	public void setServiceManagerFax(String serviceManagerfax) {
		this.serviceManagerFax = serviceManagerfax;
	}

	public String getServiceManagerEmail() {
		return serviceManagerEmail;
	}

	public void setServiceManagerEmail(String serviceManageremail) {
		this.serviceManagerEmail = serviceManageremail;
	}
	
}

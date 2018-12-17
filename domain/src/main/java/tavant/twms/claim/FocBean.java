package tavant.twms.claim;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.Type;

import tavant.twms.domain.catalog.Item;
import tavant.twms.domain.claim.HussmanPartsReplacedInstalled;
import tavant.twms.domain.failurestruct.FailureCauseDefinition;
import tavant.twms.domain.failurestruct.FailureTypeDefinition;
import tavant.twms.domain.failurestruct.FaultCode;

import com.domainlanguage.time.CalendarDate;

public class FocBean {

	private String serialNumber;
	
	private String workOrderNumber;
	
	//TODO rename this 
	Item  causalPart = new Item();
	
	@Type(type = "tavant.twms.infra.CalendarDateUserType")
	private CalendarDate failureDate;

	@Type(type = "tavant.twms.infra.CalendarDateUserType")
	private CalendarDate repairDate;
	
	FailureTypeDefinition faultFound = new FailureTypeDefinition();
	
	FailureCauseDefinition causedBy = new FailureCauseDefinition();
	
    String faultCode;

    FaultCode faultCodeRef = new FaultCode();
    	
	private List<HussmanPartsReplacedInstalled> hussmanPartsReplacedInstalled = new ArrayList<HussmanPartsReplacedInstalled>();
	
	private String companyName;	
	private String companyId;
	private String orderNo;
	private String serviceProviderNo;
	private String serviceProviderType;
	
	public CalendarDate getRepairDate() {
		return repairDate;
	}

	public void setRepairDate(CalendarDate repairDate) {
		this.repairDate = repairDate;
	}

	public String getSerialNumber() {
		return serialNumber;
	}

	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}

	public String getWorkOrderNumber() {
		return workOrderNumber;
	}

	public void setWorkOrderNumber(String workOrderNumber) {
		this.workOrderNumber = workOrderNumber;
	}

	public Item getCausalPart() {
		return causalPart;
	}

	public void setCausalPart(Item causalPart) {
		this.causalPart = causalPart;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getCompanyId() {
		return companyId;
	}

	public void setCompanyId(String companyId) {
		this.companyId = companyId;
	}

	public CalendarDate getFailureDate() {
		return failureDate;
	}

	public void setFailureDate(CalendarDate failureDate) {
		this.failureDate = failureDate;
	}

	public List<tavant.twms.domain.claim.HussmanPartsReplacedInstalled> getHussmanPartsReplacedInstalled() {
		return hussmanPartsReplacedInstalled;
	}

	public void setHussmanPartsReplacedInstalled(
			List<tavant.twms.domain.claim.HussmanPartsReplacedInstalled> hussmanPartsReplacedInstalled) {
		this.hussmanPartsReplacedInstalled = hussmanPartsReplacedInstalled;
	}

	public FailureTypeDefinition getFaultFound() {
		return faultFound;
	}

	public void setFaultFound(FailureTypeDefinition faultFound) {
		this.faultFound = faultFound;
	}

	public FailureCauseDefinition getCausedBy() {
		return causedBy;
	}

	public void setCausedBy(FailureCauseDefinition causedBy) {
		this.causedBy = causedBy;
	}

	public String getFaultCode() {
		return faultCode;
	}

	public void setFaultCode(String faultCode) {
		this.faultCode = faultCode;
	}

	public FaultCode getFaultCodeRef() {
		return faultCodeRef;
	}

	public void setFaultCodeRef(FaultCode faultCodeRef) {
		this.faultCodeRef = faultCodeRef;
	}

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	public String getServiceProviderType() {
		return serviceProviderType;
	}

	public void setServiceProviderType(String serviceProviderType) {
		this.serviceProviderType = serviceProviderType;
	}

	public String getServiceProviderNo() {
		return serviceProviderNo;
	}

	public void setServiceProviderNo(String serviceProviderNo) {
		this.serviceProviderNo = serviceProviderNo;
	}

}

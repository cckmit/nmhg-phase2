package tavant.twms.domain.partrecovery;

import com.domainlanguage.time.CalendarDate;
import java.io.Serializable;
import java.util.List;

import tavant.twms.domain.claim.RecoveryClaimState;
import tavant.twms.domain.partreturn.PartReturnStatus;
import tavant.twms.infra.ListCriteria;

@SuppressWarnings("serial")
public class PartRecoverySearchCriteria extends ListCriteria implements
		Serializable {

	private String supplierName;

	private String supplierNumber;

	private String partNumber;

	private String supplierpartNumber;

	private String claimNumber;

	private String trackingNumber;

	private PartReturnStatus status;

	private String rgaNumber;

	private List<String> selectedBusinessUnits = null;
    
    private CalendarDate fromCreatedDate; 
    
    private CalendarDate toCreatedDate;

    private CalendarDate fromUpdatedDate; 
    
    private CalendarDate toUpdatedDate;

    private RecoveryClaimState recoveryClaimState;

    public RecoveryClaimState getRecoveryClaimState() {
        return recoveryClaimState;
    }

    public void setRecoveryClaimState(RecoveryClaimState recoveryClaimState) {
        this.recoveryClaimState = recoveryClaimState;
    }
    
	public String getSupplierName() {
		return supplierName;
	}

	public void setSupplierName(String supplierName) {
		this.supplierName = supplierName;
	}

	public String getSupplierNumber() {
		return supplierNumber;
	}

	public void setSupplierNumber(String supplierNumber) {
		this.supplierNumber = supplierNumber;
	}

	public String getPartNumber() {
		return partNumber;
	}

	public void setPartNumber(String partNumber) {
		this.partNumber = partNumber;
	}

	public String getSupplierpartNumber() {
		return supplierpartNumber;
	}

	public void setSupplierpartNumber(String supplierpartNumber) {
		this.supplierpartNumber = supplierpartNumber;
	}

	public String getClaimNumber() {
		return claimNumber;
	}

	public void setClaimNumber(String claimNumber) {
		this.claimNumber = claimNumber;
	}

	public String getTrackingNumber() {
		return trackingNumber;
	}

	public void setTrackingNumber(String trackingNumber) {
		this.trackingNumber = trackingNumber;
	}

	public PartReturnStatus getStatus() {
		return status;
	}

	public void setStatus(PartReturnStatus status) {
		this.status = status;
	}
	
	public List<String> getSelectedBusinessUnits() {
		return selectedBusinessUnits;
	}

	public void setSelectedBusinessUnits(List<String> selectedBusinessUnits) {
		this.selectedBusinessUnits = selectedBusinessUnits;
	}

	public String getRgaNumber() {
		return rgaNumber;
	}

	public void setRgaNumber(String rgaNumber) {
		this.rgaNumber = rgaNumber;
	}

	
    public CalendarDate getFromCreatedDate() {
        return fromCreatedDate;
    }

    public void setFromCreatedDate(CalendarDate fromCreatedDate) {
        this.fromCreatedDate = fromCreatedDate;
    }

    public CalendarDate getFromUpdatedDate() {
        return fromUpdatedDate;
    }

    public void setFromUpdatedDate(CalendarDate fromUpdatedDate) {
        this.fromUpdatedDate = fromUpdatedDate;
    }

    public CalendarDate getToCreatedDate() {
        return toCreatedDate;
    }

    public void setToCreatedDate(CalendarDate toCreatedDate) {
        this.toCreatedDate = toCreatedDate;
    }

    public CalendarDate getToUpdatedDate() {
        return toUpdatedDate;
    }

    public void setToUpdatedDate(CalendarDate toUpdatedDate) {
        this.toUpdatedDate = toUpdatedDate;
    }    
    
}

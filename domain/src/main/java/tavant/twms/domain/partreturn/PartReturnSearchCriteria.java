package tavant.twms.domain.partreturn;

import java.io.Serializable;
import java.util.List;

import com.domainlanguage.time.CalendarDate;

import tavant.twms.infra.ListCriteria;

public class PartReturnSearchCriteria extends ListCriteria implements Serializable{
	
	private String dealerName;

    private String dealerNumber;
    
    private String claimNumber;
    
    private String serialNumber;
    
    private String trackingNumber;
    
	private String wpraNumber;
    
    private String status;
    
    private String returnToLocation;
    
    private String selected;
    
    private boolean locationSelected;

    private String partNumber;

    private boolean scrapped;
    
    private String[] claimStatus;
    
	private CalendarDate fromDate;
	private CalendarDate toDate;

    private List<String> selectedBusinessUnits = null;

	public List<String> getSelectedBusinessUnits() {
		return selectedBusinessUnits;
	}

	public void setSelectedBusinessUnits(List<String> selectedBusinessUnits) {
		this.selectedBusinessUnits = selectedBusinessUnits;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getReturnToLocation() {
		return returnToLocation;
	}

	public void setReturnToLocation(String returnToLocation) {
		this.returnToLocation = returnToLocation;
	}

	public String getDealerName() {
		return dealerName;
	}

	public void setDealerName(String dealerName) {
		this.dealerName = dealerName;
	}

	public String getDealerNumber() {
		return dealerNumber;
	}

	public void setDealerNumber(String dealerNumber) {
		this.dealerNumber = dealerNumber;
	}

	public String getClaimNumber() {
		return claimNumber;
	}

	public void setClaimNumber(String claimNumber) {
		this.claimNumber = claimNumber;
	}

	public String getSerialNumber() {
		return serialNumber;
	}

	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}

	public String getTrackingNumber() {
		return trackingNumber;
	}

	public void setTrackingNumber(String trackingNumber) {
		this.trackingNumber = trackingNumber;
	}

	public String getSelected() {
		return selected;
	}

	public void setSelected(String selected) {
		this.selected = selected;
	}

	public boolean isLocationSelected() {
		return locationSelected;
	}

	public void setLocationSelected(boolean locationSelected) {
		this.locationSelected = locationSelected;
	}

	public String getWpraNumber() {
		return wpraNumber;
	}

	public void setWpraNumber(String wpraNumber) {
		this.wpraNumber = wpraNumber;
	}

    public String getPartNumber() {
        return partNumber;
    }

    public void setPartNumber(String partNumber) {
        this.partNumber = partNumber;
    }

    public boolean isScrapped() {
        return scrapped;
    }

    public void setScrapped(boolean scrapped) {
        this.scrapped = scrapped;
    }

	public String[] getClaimStatus() {
		return claimStatus;
	}

	public void setClaimStatus(String[] claimStatus) {
		this.claimStatus = claimStatus;
	}

	public CalendarDate getFromDate() {
		return fromDate;
	}

	public void setFromDate(CalendarDate fromDate) {
		this.fromDate = fromDate;
	}

	public CalendarDate getToDate() {
		return toDate;
	}

	public void setToDate(CalendarDate toDate) {
		this.toDate = toDate;
	}
    
}

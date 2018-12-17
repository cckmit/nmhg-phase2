package tavant.twms.domain.reports;

import java.util.List;

import com.domainlanguage.time.CalendarDate;

import tavant.twms.infra.ListCriteria;

public class VendorRecoveryListCriteria extends ListCriteria {

	private CalendarDate startRepairDate;
	
	private CalendarDate endRepairDate;
	
	private List<String> bussinesUnitNames;
	
	private String recoveryClaimState;

	public CalendarDate getEndRepairDate() {
		return endRepairDate;
	}

	public void setEndRepairDate(CalendarDate endRepairDate) {
		this.endRepairDate = endRepairDate;
	}

	public CalendarDate getStartRepairDate() {
		return startRepairDate;
	}

	public void setStartRepairDate(CalendarDate startRepairDate) {
		this.startRepairDate = startRepairDate;
	}

	public List<String> getBussinesUnitNames() {
		return bussinesUnitNames;
	}

	public void setBussinesUnitNames(List<String> bussinesUnitNames) {
		this.bussinesUnitNames = bussinesUnitNames;
	}

	public String getRecoveryClaimState() {
		return recoveryClaimState;
	}

	public void setRecoveryClaimState(String recoveryClaimState) {
		this.recoveryClaimState = recoveryClaimState;
	}
}

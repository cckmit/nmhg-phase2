package tavant.twms.domain.download;

import java.util.ArrayList;
import java.util.List;

public class InventoryReportSearchBean extends ReportSearchBean {

	public static String CVG_COVERED = "covered";
	public static String CVG_NOT_COVERED = "notCovered";
	public static String CVG_TERMINATED = "terminated";
	
	private String coveredOrTerminated;
	
	private Long startWindowPeriodFromDeliveryDate;
	
	private Long endWindowPeriodFromDeliveryDate;
	
	private List<Long> selectedProducts = new ArrayList<Long>();
	
	private List<Long> policyDefinitionIds = new ArrayList<Long>();
	
	private boolean allExtendedPlansSelected;

	public boolean isAllExtendedPlansSelected() {
		return allExtendedPlansSelected;
	}

	public void setAllExtendedPlansSelected(boolean allExtendedPlansSelected) {
		this.allExtendedPlansSelected = allExtendedPlansSelected;
	}

	public Long getStartWindowPeriodFromDeliveryDate() {
		return startWindowPeriodFromDeliveryDate;
	}

	public void setStartWindowPeriodFromDeliveryDate(
			Long startWindowPeriodFromDeliveryDate) {
		this.startWindowPeriodFromDeliveryDate = startWindowPeriodFromDeliveryDate;
	}

	public Long getEndWindowPeriodFromDeliveryDate() {
		return endWindowPeriodFromDeliveryDate;
	}

	public void setEndWindowPeriodFromDeliveryDate(
			Long endWindowPeriodFromDeliveryDate) {
		this.endWindowPeriodFromDeliveryDate = endWindowPeriodFromDeliveryDate;
	}

	public String getCoveredOrTerminated() {
		return coveredOrTerminated;
	}

	public void setCoveredOrTerminated(String coveredOrTerminated) {
		this.coveredOrTerminated = coveredOrTerminated;
	}

	public List<Long> getPolicyDefinitionIds() {
		return policyDefinitionIds;
	}

	public void setPolicyDefinitionIds(List<Long> policyDefinitionIds) {
		this.policyDefinitionIds = policyDefinitionIds;
	}

	public List<Long> getSelectedProducts() {
		return selectedProducts;
	}

	public void setSelectedProducts(List<Long> selectedProducts) {
		this.selectedProducts = selectedProducts;
	}
	
}

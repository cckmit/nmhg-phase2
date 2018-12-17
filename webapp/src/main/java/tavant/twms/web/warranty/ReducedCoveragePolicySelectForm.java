package tavant.twms.web.warranty;

import com.domainlanguage.time.CalendarDate;

public class ReducedCoveragePolicySelectForm {

	private Boolean selected;
	
	private Long goodWillPolicyId;
	
	private CalendarDate warrantyEndDate;
	
	private Integer serviceHoursCovered;

	public Long getGoodWillPolicyId() {
		return goodWillPolicyId;
	}

	public void setGoodWillPolicyId(Long goodWillPolicyId) {
		this.goodWillPolicyId = goodWillPolicyId;
	}

	public CalendarDate getWarrantyEndDate() {
		return warrantyEndDate;
	}

	public void setWarrantyEndDate(CalendarDate warrantyEndDate) {
		this.warrantyEndDate = warrantyEndDate;
	}

	public Integer getServiceHoursCovered() {
		return serviceHoursCovered;
	}

	public void setServiceHoursCovered(Integer serviceHoursCovered) {
		this.serviceHoursCovered = serviceHoursCovered;
	}

	public Boolean getSelected() {
		return selected;
	}

	public void setSelected(Boolean selected) {
		this.selected = selected;
	}
	
	
}

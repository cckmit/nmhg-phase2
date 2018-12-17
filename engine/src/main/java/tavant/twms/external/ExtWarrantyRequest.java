package tavant.twms.external;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import tavant.twms.domain.policy.ExtWarrantyPlan;

public class ExtWarrantyRequest {

	private String dealerNo;
	
	private String itemNumber;
	
	private String description;
	
	private String serialNumber;
	
	private Date purchaseDate;
	
	private String claimNumber;
	
	private List<ExtWarrantyPlan> plans = new ArrayList<ExtWarrantyPlan>();

	public String getDealerNo() {
		return dealerNo;
	}

	public void setDealerNo(String dealerNo) {
		this.dealerNo = dealerNo;
	}

	public String getItemNumber() {
		return itemNumber;
	}

	public void setItemNumber(String itemNumber) {
		this.itemNumber = itemNumber;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<ExtWarrantyPlan> getPlans() {
		return plans;
	}

	public void setPlans(List<ExtWarrantyPlan> plans) {
		this.plans = plans;
	}

	public String getSerialNumber() {
		return serialNumber;
	}

	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}

	public Date getPurchaseDate() {
		return purchaseDate;
	}

	public void setPurchaseDate(Date purchaseDate) {
		this.purchaseDate = purchaseDate;
	}

	public String getClaimNumber() {
		return claimNumber;
	}

	public void setClaimNumber(String claimNumber) {
		this.claimNumber = claimNumber;
	}
	
	
	
	
	
	
}

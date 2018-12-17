package tavant.twms.external;

import com.domainlanguage.time.CalendarDate;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class PriceCheckRequest {

	//Set to Claim Number if request is made from claim.
    //Set to Recovery Claim Number if request is made from recovery claim
    private String uniqueId;

    private String businessUnitName;

    private String dealerNumber;
    
    private String dealerSiteNumber;
	
	public String getDealerSiteNumber() {
		return dealerSiteNumber;
	}

	public void setDealerSiteNumber(String dealerSiteNumber) {
		this.dealerSiteNumber = dealerSiteNumber;
	}

	private String warrantyType;
	
	private String currencyCode;
	
	private CalendarDate dateOfRepair;
	
	private List<PriceCheckItem> priceCheckItemList = new ArrayList<PriceCheckItem>();

    private List<PriceCheckItem> costCheckItemList = new ArrayList<PriceCheckItem>();

    public List<PriceCheckItem> getCostCheckItemList() {
        return costCheckItemList;
    }

    public void setCostCheckItemList(List<PriceCheckItem> costCheckItemList) {
        this.costCheckItemList = costCheckItemList;
    }

    public List<PriceCheckItem> getPriceCheckItemList() {
		return priceCheckItemList;
	}

	public void setPriceCheckItemList(List<PriceCheckItem> priceCheckItemList) {
		this.priceCheckItemList = priceCheckItemList;
	}

	public String getDealerNumber() {
		return dealerNumber;
	}

	public void setDealerNumber(String dealerNumber) {
		this.dealerNumber = dealerNumber;
	}

	public String getWarrantyType() {
		return warrantyType;
	}

	public void setWarrantyType(String warrantyType) {
		this.warrantyType = warrantyType;
	}

	public String getCurrencyCode() {
		return currencyCode;
	}

	public void setCurrencyCode(String currencyCode) {
		this.currencyCode = currencyCode;
	}

	public CalendarDate getDateOfRepair() {
		return dateOfRepair;
	}

	public void setDateOfRepair(CalendarDate dateOfRepair) {
		this.dateOfRepair = dateOfRepair;
	}

    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    public String getBusinessUnitName() {
        return businessUnitName;
    }

    public void setBusinessUnitName(String businessUnitName) {
        this.businessUnitName = businessUnitName;
    }
}

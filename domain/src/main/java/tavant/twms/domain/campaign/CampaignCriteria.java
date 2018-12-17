package tavant.twms.domain.campaign;

import java.io.Serializable;
import java.util.List;

import org.apache.commons.lang.StringUtils;



/*
 * Simple pojo class to store all search conditions
 */
public class CampaignCriteria implements Serializable{

	private String campaignClass;
	private String dealerNumber;
	private String dealerName;
	private String dealerGroup;
	private String serialNumber;
	private String campaignCode;
	private String campaignStatus;
	private String campaignReason;
	private Integer minRangeCampaignAge;
	private Integer maxRangeCampaignAge;
	// Fix for NMHGSLMS-992
	private String[] selectedBusinessUnits = null;

	
	
	public String[] getSelectedBusinessUnits() {
		return selectedBusinessUnits;
	}

	public void setSelectedBusinessUnits(String[] selectedBusinessUnits) {
		this.selectedBusinessUnits = selectedBusinessUnits;
	}

	public String getSelectedBusinessUnitInfoDelimitedByComma()
	{
		String buNamesDelimitedByComma = "";
		String[] businessUnits = selectedBusinessUnits;
		for(int i=0;i<businessUnits.length;i++)
		{
			String buName  = (String)businessUnits[i];
			buNamesDelimitedByComma = buNamesDelimitedByComma + "'" + buName + "'";
			if(i < (businessUnits.length - 1) )
			{
				buNamesDelimitedByComma = buNamesDelimitedByComma + ",";
			}
		}
		return buNamesDelimitedByComma;
	}

	public String getCampaignClass() {
		return campaignClass;
	}

	public void setCampaignClass(String campaignClass) {
		this.campaignClass = StringUtils.trim(campaignClass);
	}

	public String getDealerNumber() {
		return dealerNumber;
	}

	public void setDealerNumber(String dealerNumber) {
		this.dealerNumber = StringUtils.trim(dealerNumber);
	}

	public String getDealerName() {
		return dealerName;
	}

	public void setDealerName(String dealerName) {
		this.dealerName = dealerName;
	}

	public String getSerialNumber() {
		return serialNumber;
	}

	public void setSerialNumber(String serialNumber) {
		this.serialNumber = StringUtils.trim(serialNumber);
	}

	public String getCampaignCode() {
		return campaignCode;
	}

	public void setCampaignCode(String campaignCode) {
		this.campaignCode = StringUtils.trim(campaignCode);
	}

	public String getCampaignStatus() {
		return campaignStatus;
	}

	public void setCampaignStatus(String campaignStatus) {
		this.campaignStatus = StringUtils.trim(campaignStatus);
	}

	public Integer getMinRangeCampaignAge() {
		return minRangeCampaignAge;
	}

	public void setMinRangeCampaignAge(Integer minRangeCampaignAge) {
		this.minRangeCampaignAge = minRangeCampaignAge;
	}

	public Integer getMaxRangeCampaignAge() {
		return maxRangeCampaignAge;
	}

	public void setMaxRangeCampaignAge(Integer maxRangeCampaignAge) {
		this.maxRangeCampaignAge = maxRangeCampaignAge;
	}

	public String getDealerGroup() {
		return dealerGroup;
	}

	public void setDealerGroup(String dealerGroup) {
		this.dealerGroup = dealerGroup;
	}
	
	public String getCampaignReason() {
		return campaignReason;
	}

	public void setCampaignReason(String campaignReason) {
		this.campaignReason = StringUtils.trim(campaignReason);
	}




}

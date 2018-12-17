package tavant.twms.domain.customReports;

import tavant.twms.domain.catalog.Item;
import tavant.twms.domain.catalog.ItemGroup;

public class CustomReportResultSet {
	
	 CustomReport  customReport;
	 
	 CustomReportApplicablePart custRepAppPart;
	 
	 Item item;
	 
	 Item itemInGroup;
	 
	 ItemGroup itemGroup;
	 
	 
	public CustomReport getCustomReport() {
		return customReport;
	}

	public void setCustomReport(CustomReport customReport) {
		this.customReport = customReport;
	}

	public CustomReportApplicablePart getCustRepAppPart() {
		return custRepAppPart;
	}

	public void setCustRepAppPart(CustomReportApplicablePart custRepAppPart) {
		this.custRepAppPart = custRepAppPart;
	}

	public Item getItem() {
		return item;
	}

	public void setItem(Item item) {
		this.item = item;
	}

	public Item getItemInGroup() {
		return itemInGroup;
	}

	public void setItemInGroup(Item itemInGroup) {
		this.itemInGroup = itemInGroup;
	}

	public ItemGroup getItemGroup() {
		return itemGroup;
	}

	public void setItemGroup(ItemGroup itemGroup) {
		this.itemGroup = itemGroup;
	}
	 
	
	 

}

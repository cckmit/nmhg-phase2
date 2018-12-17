/*
 *   Copyright (c)2006 Tavant Technologies
 *   All Rights Reserved.
 *
 *   This software is furnished under a license and may be used and copied
 *   only  in  accordance  with  the  terms  of such  license and with the
 *   inclusion of the above copyright notice. This software or  any  other
 *   copies thereof may not be provided or otherwise made available to any
 *   other person. No title to and ownership of  the  software  is  hereby
 *   transferred.
 *
 *   The information in this software is subject to change without  notice
 *   and  should  not be  construed as a commitment  by Tavant Technologies.
 */
package tavant.twms.domain.inventory;


import java.io.Serializable;
import java.util.List;



import org.springframework.core.style.ToStringCreator;

import tavant.twms.domain.policy.Customer;
import tavant.twms.infra.ListCriteria;

import com.domainlanguage.time.CalendarDate;

@SuppressWarnings("serial")
public class InventorySearchCriteria extends ListCriteria implements Serializable{

    private Long dealerId;

    private String dealerName;

    private String dealerNumber;

    private Long[] childDealers;

    private Long productCode;

    private String serialNumber;

    private Customer customer;

    private String itemModel;

    private String itemNumber;
    
    private Long[] manufacturingSite;

    private InventoryType inventoryType;

    private InventoryItemCondition conditionTypeIs;

    private InventoryItemCondition conditionTypeNot;

    private String productType;

    private String engineSerialNumber;

    private String modelNumber;

    private String saleOrderNumber;

    private CalendarDate fromDate;

    private CalendarDate toDate;

    private String condition;

    private boolean stolen;

    private CalendarDate buildFromDate;

    private CalendarDate buildToDate;

    private CalendarDate deliveryFromDate;

    private CalendarDate deliveryToDate;

    private CalendarDate submitFromDate;

    private CalendarDate submitToDate;
    
    private CalendarDate policyFromDate;
    
    private CalendarDate policyToDate;

    private String salesPerson;
    
    private String customerRepresentative;

    private String companyName;

    private List<String> selectedBusinessUnits = null;
    
    private Long[] policies = null;
    
    private String factoryOrderNumber;
  
    private String vinNumber;

    private String warrantyType;
    
    private boolean demoTruck;
    
    private boolean pendingApprovalDr;
    
    private String customerType;
    
    private boolean preOrderBooking;
    
    private String productGroupCode;
    
    private String options; 
    
    private String optionDescription;
    
    private boolean forFlagDR;

    private ContractCode contractCode;
    
    private String groupCodeForProductFamily;
    
    private InternalInstallType internalInstallType; 
    
    private String marketingGroupCode;
    
    private List<String> discountType;
    
	public InternalInstallType getInternalInstallType() {
		return internalInstallType;
	}

	public void setInternalInstallType(InternalInstallType internalInstallType) {
		this.internalInstallType = internalInstallType;
	}

	public ContractCode getContractCode() {
		return contractCode;
	}

	public void setContractCode(ContractCode contractCode) {
		this.contractCode = contractCode;
	}

	public boolean isForFlagDR() {
		return forFlagDR;
	}

	public void setForFlagDR(boolean forFlagDR) {
		this.forFlagDR = forFlagDR;
	}

    private List<Long> allowedDealers;

    public List<Long> getAllowedDealers() {
        return allowedDealers;
    }

    public void setAllowedDealers(List<Long> allowedDealers) {
        this.allowedDealers = allowedDealers;
    }

 
    public String getProductGroupCode() {
		return productGroupCode;
	}

	public void setProductGroupCode(String productGroupCode) {
		this.productGroupCode = productGroupCode;
	}

	public String getWarrantyType() {
		return warrantyType;
	}

	public void setWarrantyType(String warrantyType) {
		this.warrantyType = warrantyType;
	}

	private boolean warrantyCheck;

    public InventorySearchCriteria(){
        super();    
    }    

    public Customer getCustomer() {
		return this.customer;
	}

    public void setCustomer(Customer customer) {
        this.customer = customer;
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

    public String getItemNumber() {
        return itemNumber;
    }

    public void setItemNumber(String itemNumber) {
        this.itemNumber = itemNumber;
    }

    public Long getProductCode() {
        return productCode;
    }

    public void setProductCode(Long productCode) {
        this.productCode = productCode;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getItemModel() {
        return itemModel;
    }

    public void setItemModel(String itemModel) {
        this.itemModel = itemModel;
    }

    public InventoryType getInventoryType() {
        return inventoryType;
    }

    public void setInventoryType(InventoryType inventoryType) {
        this.inventoryType = inventoryType;
    }

    public Long getDealerId() {
        return dealerId;
    }

    public void setDealerId(Long dealerId) {
        this.dealerId = dealerId;
    }

    public InventoryItemCondition getConditionTypeIs() {
		return conditionTypeIs;
	}

	public void setConditionTypeIs(InventoryItemCondition conditionTypeIs) {
		this.conditionTypeIs = conditionTypeIs;
	}

	public InventoryItemCondition getConditionTypeNot() {
		return conditionTypeNot;
	}

	public void setConditionTypeNot(InventoryItemCondition conditionTypeNot) {
		this.conditionTypeNot = conditionTypeNot;
	}

	@Override
    public String toString() {
        return new ToStringCreator(this)
            .append("serialNumber", serialNumber)
            .append("dealerName", dealerName)
            .append("dealerNumber", dealerNumber)
            .append("productCode", productCode)
            .append("customer", customer)
            .append("itemModel", itemModel)
            .append("itemNumber", itemNumber)
            .append("manufacturingSite",manufacturingSite)
            .append("inventoryType", inventoryType)
            .append("filterCriteria", filterCriteria)
            .append("childDealers",childDealers)
              
            .toString();
    }

	public String getProductType() {
		return productType;
	}

	public void setProductType(String productType) {
		this.productType = productType;
	}

	public String getEngineSerialNumber() {
		return engineSerialNumber;
	}

	public void setEngineSerialNumber(String engineSerialNumber) {
		this.engineSerialNumber = engineSerialNumber;
	}

	public String getModelNumber() {
		return modelNumber;
	}

	public void setModelNumber(String modelNumber) {
		this.modelNumber = modelNumber;
	}

	public String getSaleOrderNumber() {
		return saleOrderNumber;
	}

	public void setSaleOrderNumber(String saleOrderNumber) {
		this.saleOrderNumber = saleOrderNumber;
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

	public String getCondition() {
		return condition;
	}

	public void setCondition(String condition) {
		this.condition = condition;
	}

	public CalendarDate getBuildFromDate() {
		return buildFromDate;
	}

	public void setBuildFromDate(CalendarDate buildFromDate) {
		this.buildFromDate = buildFromDate;
	}

	public CalendarDate getBuildToDate() {
		return buildToDate;
	}

	public void setBuildToDate(CalendarDate buildToDate) {
		this.buildToDate = buildToDate;
	}

	public CalendarDate getDeliveryFromDate() {
		return deliveryFromDate;
	}

	public void setDeliveryFromDate(CalendarDate deliveryFromDate) {
		this.deliveryFromDate = deliveryFromDate;
	}

	public CalendarDate getDeliveryToDate() {
		return deliveryToDate;
	}

	public void setDeliveryToDate(CalendarDate deliveryToDate) {
		this.deliveryToDate = deliveryToDate;
	}

	public CalendarDate getSubmitFromDate() {
		return submitFromDate;
	}

	public void setSubmitFromDate(CalendarDate submitFromDate) {
		this.submitFromDate = submitFromDate;
	}

	public CalendarDate getSubmitToDate() {
		return submitToDate;
	}

	public void setSubmitToDate(CalendarDate submitToDate) {
		this.submitToDate = submitToDate;
	}

	public String getSalesPerson() {
		return salesPerson;
	}

	public void setSalesPerson(String salesPerson) {
		this.salesPerson = salesPerson;
	}

	public boolean isStolen() {
		return stolen;
	}

	public void setStolen(boolean stolen) {
		this.stolen = stolen;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public List<String> getSelectedBusinessUnits() {
		return selectedBusinessUnits;
	}

	public void setSelectedBusinessUnits(List<String> selectedBusinessUnits) {
		this.selectedBusinessUnits = selectedBusinessUnits;
	}

	public String getSelectedBusinessUnitInfoDelimitedByComma()
	{
		String buNamesDelimitedByComma = "";
		Object[] businessUnits = (Object[])selectedBusinessUnits.toArray();
		for(int i=0;i<businessUnits.length;i++)
		{
			String buName  = (String)businessUnits[i];
			if(buName != null && !buName.equals(""))
			{
				buNamesDelimitedByComma = buNamesDelimitedByComma + "'" + buName + "'";
				if(i < (businessUnits.length - 1) )
				{
					buNamesDelimitedByComma = buNamesDelimitedByComma + ",";
				}
			}
		}
		return buNamesDelimitedByComma;
	}

    public String getManufSiteIdDelimitedByComma()
	{
		String manufIdsDelimitedByComma = "";
		Object[] manufIds = (Object[])manufacturingSite;
		for(int i=0;i<manufacturingSite.length;i++)
		{
			Long manufId  = (Long)manufIds[i];
			if(manufId  != null)
			{
				manufIdsDelimitedByComma = manufIdsDelimitedByComma + manufId.toString() ;
				if(i < (manufacturingSite.length - 1) )
				{
					manufIdsDelimitedByComma = manufIdsDelimitedByComma + ",";
				}
			}
		}
		return manufIdsDelimitedByComma;
	}
	
	public String getDelimitedByCommaValueForList(List<Long> list)
	{
		String delimitedByCommaValueForList = "";
		Object[] listValues = (Object[])list.toArray();
		for(int i=0;i<list.size();i++)
		{	
			Object listValue  = listValues[i];
			if(listValue  != null)
			{
				delimitedByCommaValueForList = delimitedByCommaValueForList + listValue.toString() ;
				if(i < (list.size() - 1) )
				{
					delimitedByCommaValueForList = delimitedByCommaValueForList + ",";
				}
			}
		}
		return delimitedByCommaValueForList;
	}
	


	public String getFactoryOrderNumber() {
		return factoryOrderNumber;
	}
	public Long[] getManufacturingSite() {
		return manufacturingSite;
	}

	public void setManufacturingSite(Long[] manufacturingSite) {
		this.manufacturingSite = manufacturingSite;
	}

	public void setFactoryOrderNumber(String factoryOrderNumber) {
		this.factoryOrderNumber = factoryOrderNumber;
	}
    
	
    public String getVinNumber() {
		return vinNumber;
	}

	public void setVinNumber(String vinNumber) {
		this.vinNumber = vinNumber;
	}

	public boolean isWarrantyCheck() {
        return warrantyCheck;
    }

    public void setWarrantyCheck(boolean warrantyCheck) {
        this.warrantyCheck = warrantyCheck;
    }

	public CalendarDate getPolicyFromDate() {
		return policyFromDate;
	}

	public void setPolicyFromDate(CalendarDate policyFromDate) {
		this.policyFromDate = policyFromDate;
	}

	public CalendarDate getPolicyToDate() {
		return policyToDate;
	}

	public void setPolicyToDate(CalendarDate policyToDate) {
		this.policyToDate = policyToDate;
	}

    public Long[] getPolicies() {
		return policies;
	}

	public void setPolicies(Long[] policies) {
		this.policies = policies;
	}

	public Long[] getChildDealers() {
        return childDealers;
    }

    public void setChildDealers(Long[] childDealers) {
        this.childDealers = childDealers;
    }
    
	public boolean isDemoTruck() {
		return demoTruck;
	}

	public void setDemoTruck(boolean demoTruck) {
		this.demoTruck = demoTruck;
	}
	
	public String getCustomerType() {
		return customerType;
	}

	public void setCustomerType(String customerType) {
		this.customerType = customerType;
	}

	public boolean isPreOrderBooking() {
		return preOrderBooking;
	}

	public void setPreOrderBooking(boolean preOrderBooking) {
		this.preOrderBooking = preOrderBooking;
	}

	public String getOptions() {
		return options;
	}

	public void setOptions(String options) {
		this.options = options;
	}

	public String getCustomerRepresentative() {
		 
		return customerRepresentative  ;
	}
	public void setCustomerRepresentative(String customerRepresentative) {
		this.customerRepresentative = customerRepresentative;
	}

	public String getOptionDescription() {
		return optionDescription;
	}

	public void setOptionDescription(String optionDescription) {
		this.optionDescription = optionDescription;
	}

	public String getGroupCodeForProductFamily() {
		return groupCodeForProductFamily;
	}

	public void setGroupCodeForProductFamily(String groupCodeForProductFamily) {
		this.groupCodeForProductFamily = groupCodeForProductFamily;
	}

	public String getMarketingGroupCode() {
		return marketingGroupCode;
	}

	public void setMarketingGroupCode(String marketingGroupCode) {
		this.marketingGroupCode = marketingGroupCode;
	}

	 public String getPoliciesDelimitedByComma(Long[] invPolicies){
	    	String policiesDelimitedByComma = "";
			Object[] policyIds = (Object[])invPolicies;
			for(int i=0;i<invPolicies.length;i++)
			{
				Long manufId  = (Long)policyIds[i];
				if(manufId  != null)
				{
					policiesDelimitedByComma = policiesDelimitedByComma + manufId.toString() ;
					if(i < (invPolicies.length - 1) )
					{
						policiesDelimitedByComma = policiesDelimitedByComma + ",";
					}
				}
			}
			return policiesDelimitedByComma;
	    }

	public boolean isPendingApprovalDr() {
		return pendingApprovalDr;
	}

	public void setPendingApprovalDr(boolean pendingApprovalDr) {
		this.pendingApprovalDr = pendingApprovalDr;
	}

	public List<String> getDiscountType() {
		return discountType;
	}

	public void setDiscountType(List<String> discountType) {
		this.discountType = discountType;
	}
	
	
}

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
package tavant.twms.domain.claim;

import com.domainlanguage.money.Money;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.core.style.ToStringCreator;

import tavant.twms.domain.partreturn.PartReturnStatus;
import tavant.twms.infra.ListCriteria;

import com.domainlanguage.time.CalendarDate;
import tavant.twms.domain.orgmodel.User;

/**
 * Claim Search criteria object created to obtain claims by passing in search,
 * sort or filter criteria.
 */
public class ClaimSearchCriteria extends ListCriteria implements Serializable {

	private String claimFiledBy;

	private String dealerName;

	private String dealerNumber;

    private String dealerGroup;
        
	private Long claimId;

	private Long[] productCodes;
	
	private Long[] dealerGroups;
	
	private Long[] partGroups;

	private String serialNumber;

	private String[] claimTypes;

	private CalendarDate startDate;

	private CalendarDate endDate;

	private String[] claimStates;

	private boolean smrRequest;

	private String claimNumber;
	private String workOrderNumber;
	private String productType;
	private String productGroupCode;
	private String modelNumber;
	private String invoiceNumber;
	private String causalPart;
	
	/**
	 * SLMS-776 Changes to add dateCode
	 */
	private String dateCode;
	private String faultCode;	
	boolean checkProductType;

	private CalendarDate buildForm;
	private CalendarDate buildTo;
	private Long[] manufacturingSite;

	private CalendarDate fromDate;
	private CalendarDate toDate;
	private String ofDate;
	private String ofClaimStatus = "currentState";

	private String serviceManagerReview;

	private Map<ClaimState, Boolean> selectedStates;

	private Map<PartReturnStatus, Boolean> partStatusMap;

	private Map<String, Boolean> openClosedState;

	private Boolean resubmitted;

	private Long[] accountabilityCodeList;

	private Integer[] failedRules;

	private Integer[] notFailed;

	private Long[] userIds;
	
	private Long[] assignToUserIds;

	private Long[] acceptanceReason;

	private Long[] campaignList;

	private Boolean restrictSearch;

	private String[] selectedBusinessUnits = null;
	
	private List<String> claimType;

	private Boolean inProgressState;

    private Money totalAmountClaim;

    private String totalAmountOperator;
    
    private String belongsToDealerGroup = "true";
      
    private String vinNumber;  
    
    private String creditMemoNumber;
    
    private User loggedInUser;

    private Long[]  childDealers;

    private String authNumber;
    
    private boolean duplicateClaim;

    private String warrantyOrder;
    
    private String includeNCRClaims;
    
    private Boolean manuallyReviewed;
    
    private String groupCodeForProductFamily;
    
    private Long[] onHoldReason;
    
    private Long[] forwaredReason;
   
	private Long[] rejectionReason; 
    
    private String marketingGroupCode;
    	
	public Boolean getManuallyReviewed() {
		return manuallyReviewed;
	}

	public void setManuallyReviewed(Boolean manuallyReviewed) {
		this.manuallyReviewed = manuallyReviewed;
	}

	public String getIncludeNCRClaims() {
		return includeNCRClaims;
	}

	public void setIncludeNCRClaims(String includeNCRClaims) {
		this.includeNCRClaims = includeNCRClaims;
	}

	public String getVinNumber() {
			return vinNumber;
		}

		public void setVinNumber(String vinNumber) {
			this.vinNumber = vinNumber;
		}

	public String[] getSelectedBusinessUnits() {
		return selectedBusinessUnits;
	}

	public void setSelectedBusinessUnits(String[] selectedBusinessUnits) {
		this.selectedBusinessUnits = selectedBusinessUnits;
	}

	public boolean isSmrRequest() {
		return smrRequest;
	}

	public void setSmrRequest(boolean smrRequest) {
		this.smrRequest = smrRequest;
	}

	public ClaimSearchCriteria() {
		super();
	}

	public Long getClaimId() {
		return this.claimId;
	}

	public void setClaimId(Long claimId) {
		this.claimId = claimId;
	}

	public String[] getClaimStates() {
		return this.claimStates;
	}

	public void setClaimStates(String[] claimStates) {
		this.claimStates = claimStates;
	}

	public String[] getClaimTypes() {
		return this.claimTypes;
	}

	public void setClaimTypes(String[] claimTypes) {
		this.claimTypes = claimTypes;
	}

	public String getDealerNumber() {
		return this.dealerNumber;
	}

	public void setDealerNumber(String dealerNumbers) {
		this.dealerNumber = dealerNumbers;
	}

	public CalendarDate getEndDate() {
		return this.endDate;
	}

	public void setEndDate(CalendarDate endDate) {
		this.endDate = endDate;
	}

	public Long[] getProductCodes() {
		return this.productCodes;
	}

	public void setProductCodes(Long[] productCodes) {
		this.productCodes = productCodes;
	}

	public String getSerialNumber() {
		return this.serialNumber;
	}

	public void setSerialNumber(String serialNumber) {
		this.serialNumber = StringUtils.trim(serialNumber);
	}

	public CalendarDate getStartDate() {
		return this.startDate;
	}

	public void setStartDate(CalendarDate startDate) {
		this.startDate = startDate;
	}

	public String getClaimFiledBy() {
		return this.claimFiledBy;
	}

	public void setClaimFiledBy(String claimFiledBy) {
		this.claimFiledBy = claimFiledBy;
	}

	public String getDealerName() {
		return this.dealerName;
	}

	public void setDealerName(String dealerName) {
		this.dealerName = dealerName;
	}

	@Override
	public String toString() {
		return new ToStringCreator(this)
				.append("claimFiledBy", this.claimFiledBy)
				.append("dealerName", this.dealerName)
				.append("dealerNumber", this.dealerNumber)
				.append("claimId", this.claimId)
				.append("productCodes", this.productCodes)
				.append("serialNumber", this.serialNumber)
				//.append("marketingGroupCode", this.marketingGroupCode)
				.append("claimTypes", this.claimTypes)
				.append("startDate", this.startDate)
				.append("endDate", this.endDate)
				.append("claimStates", this.claimStates)
				.append("filterCriteria", this.filterCriteria)
				.append("sortCriteria", this.sortCriteria).toString();
	}

	public String getClaimNumber() {
		return claimNumber;
	}

	public void setClaimNumber(String claimNumber) {
		this.claimNumber = StringUtils.trim(claimNumber);
	}

	public String getWorkOrderNumber() {
		return workOrderNumber;
	}

	public void setWorkOrderNumber(String workOrderNumber) {
		this.workOrderNumber = StringUtils.trim(workOrderNumber);
	}

	public String getProductType() {
		return productType;
	}

	public void setProductType(String productType) {
		this.productType = StringUtils.trim(productType);
	}

	public String getModelNumber() {
		return modelNumber;
	}

	public void setModelNumber(String modelNumber) {
		this.modelNumber = StringUtils.trim(modelNumber);
	}

	public String getInvoiceNumber() {
		return invoiceNumber;
	}

	public void setInvoiceNumber(String invoiceNumber) {
		this.invoiceNumber = StringUtils.trim(invoiceNumber);
	}

	public String getCausalPart() {
		return causalPart;
	}

	public void setCausalPart(String causalPart) {
		this.causalPart = StringUtils.trim(causalPart);
	}

	public String getFaultCode() {
		return faultCode;
	}

	public void setFaultCode(String faultCode) {
		this.faultCode = StringUtils.trim(faultCode);
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

	public Map<ClaimState, Boolean> getSelectedStates() {
		return selectedStates;
	}

	public void setSelectedStates(Map<ClaimState, Boolean> selectedStates) {
		this.selectedStates = selectedStates;
	}

	public String getServiceManagerReview() {
		return serviceManagerReview;
	}

	public void setServiceManagerReview(String serviceManagerReview) {
		this.serviceManagerReview = serviceManagerReview;
	}

	public Boolean getServiceMangerRequest() {
		return ((serviceManagerReview == null) ? null : (serviceManagerReview
				.equals("true") ? Boolean.TRUE : Boolean.FALSE));
	}

    public String getWarrantyOrder() {
        return warrantyOrder;
    }

    public void setWarrantyOrder(String warrantyOrder) {
        this.warrantyOrder = warrantyOrder;
    }

    public Boolean getWarrantyOrderRequest() {
        return ((warrantyOrder == null) ? null : (warrantyOrder
                .equals("true") ? Boolean.TRUE : Boolean.FALSE));
    }


    public String getOfDate() {
		return ofDate;
	}

	public void setOfDate(String ofDate) {
		this.ofDate = ofDate;
	}

	public List<ClaimState> getStateListInProgress() {
		return ClaimState.getStateListInProgress();
	}
	public List<ClaimState> getStatesList() {
		List<ClaimState> list = new ArrayList<ClaimState>();

		if (selectedStates != null && selectedStates.size() > 0) {
			for (ClaimState key : selectedStates.keySet()) {

				if (selectedStates.get(key).booleanValue()) {
					list.add(key);
				}
			}
		}

		return list;
	}

	public List<PartReturnStatus> getPartsReturnStatusList() {
		List<PartReturnStatus> list = new ArrayList<PartReturnStatus>();
		if (partStatusMap != null && partStatusMap.size() > 0) {
			for (PartReturnStatus key : partStatusMap.keySet()) {
				if (partStatusMap.get(key).booleanValue()) {
					list.add(key);
				}
			}
		}
		return list;
	}

	public Map<PartReturnStatus, Boolean> getPartStatusMap() {
		return partStatusMap;
	}

	public Map<PartReturnStatus, Boolean> getPartReturnStatuses() {
		Map<PartReturnStatus, Boolean> map = new HashMap<PartReturnStatus, Boolean>();
		map.put(PartReturnStatus.PART_RECEIVED, Boolean.FALSE);
		map.put(PartReturnStatus.PART_SHIPPED, Boolean.FALSE);
		return map;

	}

	public void setPartStatusMap(Map<PartReturnStatus, Boolean> partStatusMap) {
		this.partStatusMap = partStatusMap;
	}

	public boolean isCheckProductType() {
		return checkProductType;
	}

	public void setCheckProductType(boolean checkProductType) {
		this.checkProductType = checkProductType;
	}

	public Map<String, Boolean> getOpenClosedState() {
		return openClosedState;
	}

	public void setOpenClosedState(Map<String, Boolean> openClosedState) {
		this.openClosedState = openClosedState;
	}

	public Boolean getResubmitted() {
		return resubmitted;
	}

	public void setResubmitted(Boolean resubmitted) {
		this.resubmitted = resubmitted;
	}

	public Integer[] getFailedRules() {
		return failedRules;
	}

	public void setFailedRules(Integer[] failedRules) {
		this.failedRules = failedRules;
	}

	public Integer[] getNotFailed() {
		return notFailed;
	}

	public void setNotFailed(Integer[] notFailed) {
		this.notFailed = notFailed;
	}

	public Long[] getUserIds() {
		return userIds;
	}

	public void setUserIds(Long[] userIds){
		this.userIds = userIds;
	}

	public Long[] getAccountabilityCodeList() {
		return accountabilityCodeList;
	}

	public void setAccountabilityCodeList(Long[] accountabilityCodeList) {
		this.accountabilityCodeList = accountabilityCodeList;
	}

	public Long[] getAcceptanceReason() {
		return acceptanceReason;
	}

	public void setAcceptanceReason(Long[] acceptanceReason) {
		this.acceptanceReason = acceptanceReason;
	}

	public Long[] getCampaignList() {
		return campaignList;
	}

	public void setCampaignList(Long[] campaignList) {
		this.campaignList = campaignList;
	}

	public Boolean getRestrictSearch() {
		return restrictSearch;
	}

	public void setRestrictSearch(Boolean restrictSearch) {
		this.restrictSearch = restrictSearch;
	}

	public CalendarDate getBuildForm() {
		return buildForm;
	}

	public void setBuildForm(CalendarDate buildForm) {
		this.buildForm = buildForm;
	}

	public CalendarDate getBuildTo() {
		return buildTo;
	}

	public void setBuildTo(CalendarDate buildTo) {
		this.buildTo = buildTo;
	}

	public Long[] getManufacturingSite() {
		return manufacturingSite;
	}

	public void setManufacturingSite(Long[] manufacturingSite) {
		this.manufacturingSite = manufacturingSite;
	}

	public Boolean getInProgressState() {
		return inProgressState;
	}

	public void setInProgressState(Boolean inProgressState) {
		this.inProgressState = inProgressState;
	}

	public List<String> getClaimType() {
		return claimType;
	}

	public void setClaimType(List<String> claimType) {
		this.claimType = claimType;
	}

    public String getDealerGroup() {
        return dealerGroup;
    }

    public void setDealerGroup(String dealerGroup) {
        this.dealerGroup = dealerGroup;
    }

	public String getOfClaimStatus() {
		return ofClaimStatus;
	}

	public void setOfClaimStatus(String ofClaimStatus) {
		this.ofClaimStatus = ofClaimStatus;
	}

    public Money getTotalAmountClaim() {
        return totalAmountClaim;
    }

    public void setTotalAmountClaim(Money totalAmountClaim) {
        this.totalAmountClaim = totalAmountClaim;
    }

    public String getTotalAmountOperator() {
        return totalAmountOperator;
    }

    public void setTotalAmountOperator(String totalAmountOperator) {
        this.totalAmountOperator = totalAmountOperator;
    }

	public String getBelongsToDealerGroup() {
		return belongsToDealerGroup;
	}

	public void setBelongsToDealerGroup(String belongsToDealerGroup) {
		this.belongsToDealerGroup = belongsToDealerGroup;
	}

	public String getCreditMemoNumber() {
		return creditMemoNumber;
	}

	public void setCreditMemoNumber(String creditMemoNumber) {
		this.creditMemoNumber = creditMemoNumber;
	}

    public User getLoggedInUser() {
        return loggedInUser;
    }

    public void setLoggedInUser(User loggedInUser) {
        this.loggedInUser = loggedInUser;
    }

    public Long[] getChildDealers() {
        return childDealers;
    }

    public void setChildDealers(Long[] childDealers) {
        this.childDealers = childDealers;
    }

	public Long[] getAssignToUserIds() {
		return assignToUserIds;
	}

	public void setAssignToUserIds(Long[] assignToUserIds) {
		this.assignToUserIds = assignToUserIds;
	}

	public String getAuthNumber() {
		return authNumber;
	}

	public void setAuthNumber(String authNumber) {
		this.authNumber = authNumber;
	}

	public boolean isDuplicateClaim() {
		return duplicateClaim;
	}

	public void setDuplicateClaim(boolean duplicateClaim) {
		this.duplicateClaim = duplicateClaim;
	}
	
	public String getDateCode() {
		return dateCode;
	}

	public void setDateCode(String dateCode) {
		this.dateCode = dateCode;
	}

	public String getProductGroupCode() {
		return productGroupCode;
	}

	public void setProductGroupCode(String productGroupCode) {
		this.productGroupCode = productGroupCode;
	}
	

	public Long[] getDealerGroups() {
		return dealerGroups;
	}

	public void setDealerGroups(Long[] dealerGroups) {
		this.dealerGroups = dealerGroups;
	}

	public Long[] getPartGroups() {
		return partGroups;
	}

	public void setPartGroups(Long[] partGroups) {
		this.partGroups = partGroups;
	}

	public String getGroupCodeForProductFamily() {
		return groupCodeForProductFamily;
	}

	public void setGroupCodeForProductFamily(String groupCodeForProductFamily) {
		this.groupCodeForProductFamily = groupCodeForProductFamily;
	}

	public Long[] getOnHoldReason() {
		return onHoldReason;
	}

	public void setOnHoldReason(Long[] onHoldReason) {
		this.onHoldReason = onHoldReason;
	}

	public Long[] getForwaredReason() {
		return forwaredReason;
	}

	public void setForwaredReason(Long[] forwaredReason) {
		this.forwaredReason = forwaredReason;
	}

	public String getMarketingGroupCode() {
		return marketingGroupCode;
	}

	public void setMarketingGroupCode(String marketingGroupCode) {
		this.marketingGroupCode = marketingGroupCode;
	}
	
    public Long[] getRejectionReason() {
		return rejectionReason;
	}

	public void setRejectionReason(Long[] rejectionReason) {
		this.rejectionReason = rejectionReason;
	}

}
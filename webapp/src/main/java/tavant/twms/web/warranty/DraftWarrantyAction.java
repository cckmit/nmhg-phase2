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
package tavant.twms.web.warranty;

import static tavant.twms.web.documentOperations.DocumentAction.getDocumentListJSON;
import static tavant.twms.web.documentOperations.DocumentAction.getUnitDocumentListJSON;

import java.util.List;
import java.util.ArrayList;

import tavant.twms.domain.common.ListOfValues;
import tavant.twms.domain.configuration.ConfigName;
import tavant.twms.domain.configuration.ConfigParamService;
import tavant.twms.domain.inventory.InventoryItem;
import tavant.twms.domain.orgmodel.ServiceProvider;
import tavant.twms.domain.policy.*;
import tavant.twms.domain.common.Document;
import tavant.twms.infra.HibernateCast;
import tavant.twms.infra.InstanceOfUtil;
import tavant.twms.security.SelectedBusinessUnitsHolder;
import tavant.twms.worklist.common.BUSetting;
import tavant.twms.worklist.common.BuSettingsService;

import com.opensymphony.xwork2.Preparable;
import com.opensymphony.xwork2.Validateable;
import common.Logger;

/**
 * @author fatima.marneni
 * 
 */
@SuppressWarnings("serial")
public class DraftWarrantyAction extends BaseManageWarrantyAction implements
		Preparable, Validateable {

	private static final Logger logger = Logger
			.getLogger(DraftWarrantyAction.class);
	private Warranty warranty;
	private WarrantyAudit warrantyAudit;
	private MarketingInformation marketingInformation;	
	private List<ListOfValues> additionalComponentTypes;
	private List<ListOfValues> additionalComponentSubTypes;
	private boolean isModifyDRorETR = false;
	private CustomerService customerService;   
	private Customer operator;
	
    public CustomerService getCustomerService() {
		return customerService;
	}

	public void setCustomerService(CustomerService customerService) {
		this.customerService = customerService;
	}

	public Customer getOperator() {
		return operator;
	}

	public void setOperator(Customer operator) {
		this.operator = operator;
	}

    public boolean isPrintPdf(){
    	return this.buSettingsService.getBooleanSetting(BUSetting.PRINT_PDI, getCurrentBusinessUnit().getName());
    }
    
    public List<ListOfValues> getAdditionalComponentSubTypes() {
		return additionalComponentSubTypes;
	}

	public void setAdditionalComponentSubTypes(
			List<ListOfValues> additionalComponentSubTypes) {
		this.additionalComponentSubTypes = additionalComponentSubTypes;
	}

	public List<ListOfValues> getAdditionalComponentTypes() {
		return additionalComponentTypes;
	}

	public void setAdditionalComponentTypes(List<ListOfValues> additionalComponentTypes) {
		this.additionalComponentTypes = additionalComponentTypes;
	}



	public void prepare() throws Exception {
		if (getId() != null) {
			setWarrantyTaskInstance(getWarrantyTaskInstanceService().findById(
					Long.parseLong(getId())));

			for (InventoryItem invItem : getWarrantyTaskInstance()
					.getForItems()) {
				prepareInventoryItem(invItem);
			}

			warranty = getWarrantyTaskInstance().getWarrantyAudit()
					.getForWarranty();
			SelectedBusinessUnitsHolder.setSelectedBusinessUnit(warranty
					.getForItem().getBusinessUnitInfo().getName());
			setMarketingInformation(warranty.getMarketingInformation());
		}
		prepareCommonAttachments();
		if (warranty != null
				&& InstanceOfUtil.isInstanceOfClass(ServiceProvider.class,
						warranty.getInstallingDealer())){
			setInstallingDealer(new HibernateCast<ServiceProvider>()
					.cast(warranty.getInstallingDealer()));
            SelectedBusinessUnitsHolder.setSelectedBusinessUnit(warranty.getForItem().getBusinessUnitInfo().getName());
            setMarketingInformation(warranty.getMarketingInformation());
        }
        prepareCommonAttachments();
        additionalComponentTypes = this.lovRepository.findAllActive("AdditionalComponentType");
     	additionalComponentSubTypes = this.lovRepository.findAllActive("AdditionalComponentSubType");
        if(warranty!=null && InstanceOfUtil.isInstanceOfClass(ServiceProvider.class,  warranty.getInstallingDealer()))
        setInstallingDealer(new HibernateCast<ServiceProvider>().cast(warranty.getInstallingDealer()));	
		prepareMarketingInformation(warranty.getForItem());
		populateCustomerTypes();
	}

	public String preview() {
		warranty = getWarrantyTaskInstance().warrantyAudit.getForWarranty();
		if (isAdditionalInformationDetailsApplicable()
				|| warranty.getMarketingInformation() != null) {
			marketingInformation = warranty.getMarketingInformation();
		}
		return SUCCESS;
	}

	public String detail() {
		if(null != warranty) {
			if(null != warranty.getCustomer()){
				operator=customerService.findCustomerById(warranty.getCustomer().getId());
			}
			if(null != warranty.getCustomerType()) {
				setAddressBookType(warranty.getCustomerType());
			}
		}		
		if (isAdditionalInformationDetailsApplicable()
				|| warranty.getMarketingInformation() != null) {
			setMarketingInformation(warranty.getMarketingInformation());
		}
		for (MultipleInventoryAttributesMapper mapper : getInventoryItemMappings()) {
			mapper.setDieselTierWaiver(warranty.getDieselTierWaiver());
			if (mapper.getDieselTierWaiver() != null) {
				if (mapper.getInventoryItem().getWaiverDuringDr() == null
						|| mapper.getDieselTierWaiver().getId().longValue() != mapper
								.getInventoryItem().getWaiverDuringDr().getId()
								.longValue()) {
					mapper.setWaiverInformationEditable(true);
				}
				if (mapper.getDieselTierWaiver().getApprovedByAgentName() != null)
					mapper.setDisclaimerAccepted(true);
			}
		}
		return SUCCESS;
	}

	public String deleteWarranty() {
		deleteDraftWarranty();
		return SUCCESS;
	}

	public String getJSONifiedCommonAttachmentList() {
		try {
			List<Document> attachments = this.warranty.getAttachments();
			if (attachments == null || attachments.size() <= 0) {
				return "[]";
			}
			return getUnitDocumentListJSON(attachments).toString();
		} catch (Exception e) {
			return "[]";
		}
	}

	public boolean isGenericAttachmentRequired() {
		final boolean genericAttachmentAllowedForBU = getConfigParamService()
				.getBooleanValue(ConfigName.ENABLE_GENERIC_ATTACHMENT.getName())
				.booleanValue();
		return genericAttachmentAllowedForBU
				&& (getInventoryItemMappings() == null || getInventoryItemMappings()
						.size() > 1);
	}

	public Warranty getWarranty() {
		return warranty;
	}

	public void setWarranty(Warranty warranty) {
		this.warranty = warranty;
	}

	public MarketingInformation getMarketingInformation() {
		return marketingInformation;
	}

	public void setMarketingInformation(
			MarketingInformation marketingInformation) {
		this.marketingInformation = marketingInformation;
	}

	public PolicyService getPolicyService() {
		return policyService;
	}

	public void setPolicyService(PolicyService policyService) {
		this.policyService = policyService;
	}

	public boolean isModifyDRorETR() {
		return isModifyDRorETR;
	}

	public void setModifyDRorETR(boolean isModifyDRorETR) {
		this.isModifyDRorETR = isModifyDRorETR;
	}

	public boolean isAdditionalInformationDetailsApplicable() {
		return getConfigParamService().getBooleanValue(
				ConfigName.ADDITIONAL_INFORMATION_DETAILS_APPLICABLITY
						.getName());
	}
	
	public boolean displayStockUnitDiscountDetails() {
		return getConfigParamService()
				.getBooleanValue(ConfigName.ENABLE_STOCK_UNIT_DISCOUNT_DETAILS.getName());
	}

}

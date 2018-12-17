package tavant.twms.web.warranty;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.aop.aspectj.SingletonAspectInstanceFactory;

import tavant.twms.domain.common.Oem;
import tavant.twms.domain.inventory.DieselTierWaiver;
import tavant.twms.domain.inventory.InventoryItem;
import tavant.twms.domain.inventory.InventoryItemComposition;
import tavant.twms.domain.policy.AdditionalMarketingInfo;
import tavant.twms.domain.policy.AdditionalMarketingInfoOptions;
import tavant.twms.domain.policy.RegisteredPolicy;
import tavant.twms.domain.policy.SelectedAdditionalMarketingInfo;
import tavant.twms.domain.common.Document;
import com.domainlanguage.time.CalendarDate;

public class MultipleInventoryAttributesMapper {

    InventoryItem inventoryItem;

    List<RegisteredPolicy> availablePolicies = new ArrayList<RegisteredPolicy>();   

    List<RegisteredPolicy> selectedPolicies = new ArrayList<RegisteredPolicy>();
    
    List<RegisteredPolicy> extendedPolicies = new ArrayList<RegisteredPolicy>();
    
    List<Document> attachments = new ArrayList<Document>();

    List<SelectedAdditionalMarketingInfo> selectedMarketingInfo = new ArrayList<SelectedAdditionalMarketingInfo>();
    
    private Map<AdditionalMarketingInfo,Map<String,List<AdditionalMarketingInfoOptions>>> marketingInfo= new HashMap<AdditionalMarketingInfo,Map<String,List<AdditionalMarketingInfoOptions>>>();
    
    CalendarDate warrantyDeliveryDate;
    
    String equipmentVIN;
    
    String fleetNumber;
    
    CalendarDate installationDate;
    
    Oem oem;
    
    boolean disclaimerAccepted; 
    DieselTierWaiver dieselTierWaiver;
    boolean waiverInformationEditable;

	private boolean ewpDrivenByPurchaseDate;

    public boolean isEwpDrivenByPurchaseDate() {
		return ewpDrivenByPurchaseDate;
	}

	public void setEwpDrivenByPurchaseDate(boolean ewpDrivenByPurchaseDate) {
		this.ewpDrivenByPurchaseDate = ewpDrivenByPurchaseDate;
	}

	public InventoryItem getInventoryItem() {
        return this.inventoryItem;
    }

    public List<RegisteredPolicy> getAvailablePolicies() {
        return this.availablePolicies;
    }

    public void setInventoryItem(InventoryItem inventoryItem) {   	
        this.inventoryItem = inventoryItem;   
     }

    public void setAvailablePolicies(List<RegisteredPolicy> availablePolicies) {
        this.availablePolicies = availablePolicies;
    }

    public List<RegisteredPolicy> getSelectedPolicies() {    	
        return this.selectedPolicies;
    }

    public void setSelectedPolicies(List<RegisteredPolicy> selectedPolicies) {      	
        this.selectedPolicies = selectedPolicies;       
    }

	public List<Document> getAttachments() {
		return attachments;
	}

	public void setAttachments(List<Document> attachments) {
		this.attachments = attachments;
	}

    public CalendarDate getWarrantyDeliveryDate() {
        return warrantyDeliveryDate;
    }

    public void setWarrantyDeliveryDate(CalendarDate warrantyDeliveryDate) {
        this.warrantyDeliveryDate = warrantyDeliveryDate;
    }

	public String getEquipmentVIN() {
		return equipmentVIN;
	}

	public void setEquipmentVIN(String equipmentVIN) {
		this.equipmentVIN = equipmentVIN;
	}

	public String getFleetNumber() {
		return fleetNumber;
	}

	public void setFleetNumber(String fleetNumber) {
		this.fleetNumber = fleetNumber;
	}

	public Oem getOem() {
		return oem;
	}

	public void setOem(Oem oem) {
		this.oem = oem;
	}

	public List<SelectedAdditionalMarketingInfo> getSelectedMarketingInfo() {
		return selectedMarketingInfo;
	}

	public void setSelectedMarketingInfo(List<SelectedAdditionalMarketingInfo> selectedMarketingInfo) {
		this.selectedMarketingInfo = selectedMarketingInfo;
	}

	public CalendarDate getInstallationDate() {
		return installationDate;
	}

	public void setInstallationDate(CalendarDate installationDate) {
		this.installationDate = installationDate;
	}
	
	public List<RegisteredPolicy> getExtendedPolicies() {		
		return extendedPolicies;
	}

	public void setExtendedPolicies(List<RegisteredPolicy> extendedPolicies) {
		this.extendedPolicies = extendedPolicies;
	}

	public void setMarketingInfo(Map<AdditionalMarketingInfo,Map<String,List<AdditionalMarketingInfoOptions>>> marketingInfo) {
		this.marketingInfo = marketingInfo;
	}

	public Map<AdditionalMarketingInfo,Map<String,List<AdditionalMarketingInfoOptions>>> getMarketingInfo() {
		return marketingInfo;
	}

	public boolean isDisclaimerAccepted() {
		return disclaimerAccepted;
	}

	public void setDisclaimerAccepted(boolean disclaimerAccepted) {
		this.disclaimerAccepted = disclaimerAccepted;
	}

	public DieselTierWaiver getDieselTierWaiver() {
		return dieselTierWaiver;
	}

	public void setDieselTierWaiver(DieselTierWaiver dieselTierWaiver) {
		this.dieselTierWaiver = dieselTierWaiver;
	}

	public boolean isWaiverInformationEditable() {
		return waiverInformationEditable;
	}

	public void setWaiverInformationEditable(boolean waiverInformationEditable) {
		this.waiverInformationEditable = waiverInformationEditable;
	}
	
}

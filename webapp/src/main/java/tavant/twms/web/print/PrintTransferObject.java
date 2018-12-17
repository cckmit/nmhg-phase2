/**
 * 
 */
package tavant.twms.web.print;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import tavant.twms.domain.common.DeliveryCheckList;
import tavant.twms.domain.inventory.DieselTierWaiver;
import tavant.twms.domain.inventory.InventoryItem;
import tavant.twms.domain.inventory.InventoryItemAdditionalComponents;
import tavant.twms.domain.orgmodel.Organization;
import tavant.twms.domain.orgmodel.Party;
import tavant.twms.domain.policy.AddressForTransfer;
import tavant.twms.domain.policy.MarketingInformation;
import tavant.twms.domain.policy.Warranty;
import tavant.twms.web.warranty.PurchaseExtendedWarranty;

/**
 * @author mritunjay.kumar
 * 
 */
public class PrintTransferObject {
	private List<PrintTransferInventoryObject> inventoryObjects = new ArrayList<PrintTransferInventoryObject>();
	private Organization dealer;
	private String customerName;
	private String comments;
	private AddressForTransfer addressForTransfer;
	private MarketingInformation marketingInformation;
	private String title;
	private List<TransferCoverageObject> policies;
	private String transactionDate;
	private boolean forETR;
	private String gifName;
	private String feeTitle;
	private String operatorName;
	private AddressForTransfer operatorAddressForTransfer;
	private Party installingDealer;
	private Boolean isInstDealerInstDateEnabled;
	private String isCustomerFirstTimeOwner;	
	private Boolean disclaimer;
	private DieselTierWaiver waiver;
	private String disclaimerInfo;
	private String isTradeIn;
	private String isFirstTimeOwnerOfProductBeingRegistered;
	private static Logger logger = LogManager.getLogger(PrintTransferObject.class);
	private String dateTitle;
	private String salesOrderNumber;
	private InventoryItem inventoryItem;
	private List<InventoryItemAdditionalComponents> invItemAdditionalComponents;
	private String deliveryDate;
	private List<OptionObject> options;
	private List<MajorComponentsObject> majorComponents;
	private AttachmentObject attachment;
	private CarriageObject carriage;
	private CarriageObject fork;
	private List<DeliveryCheckList> deliveryChkLst;
	private String contractDisplay;

	/**
	 * @return the deliveryChkLst
	 */
	public List<DeliveryCheckList> getDeliveryChkLst() {
		return deliveryChkLst;
	}

	/**
	 * @param deliveryChkLst the deliveryChkLst to set
	 */
	public void setDeliveryChkLst(List<DeliveryCheckList> deliveryChkLst) {
		this.deliveryChkLst = deliveryChkLst;
	}

	public CarriageObject getCarriage() {
		return carriage;
	}

	public void setCarriage(CarriageObject carriage) {
		this.carriage = carriage;
	}

	public CarriageObject getFork() {
		return fork;
	}

	public void setFork(CarriageObject fork) {
		this.fork = fork;
	}

	public AttachmentObject getAttachment() {
		return attachment;
	}

	public void setAttachment(AttachmentObject attachmentObject) {
		this.attachment = attachmentObject;
	}

	public List<MajorComponentsObject> getMajorComponents() {
		return majorComponents;
	}

	public void setMajorComponents(List<MajorComponentsObject> majorComponents) {
		this.majorComponents = majorComponents;
	}

	public List<OptionObject> getOptions() {
		return options;
	}

	public void setOptions(List<OptionObject> options) {
		this.options = options;
	}

	public String getDeliveryDate() {
		return deliveryDate;
	}

	public void setDeliveryDate(String deliveryDate) {
		this.deliveryDate = deliveryDate;
	}

	public List<InventoryItemAdditionalComponents> getInvItemAdditionalComponents() {
		return invItemAdditionalComponents;
	}

	public void setInvItemAdditionalComponents(
			List<InventoryItemAdditionalComponents> invItemAdditionalComponents) {
		this.invItemAdditionalComponents = invItemAdditionalComponents;
	}

	public InventoryItem getInventoryItem() {
		return inventoryItem;
	}

	public void setInventoryItem(InventoryItem inventoryItem) {
		this.inventoryItem = inventoryItem;
	}

	public String getDateTitle() {
		return dateTitle;
	}

	public void setDateTitle(String dateTitle) {
		this.dateTitle = dateTitle;
	}

	public String getIsTradeIn() {
    	if(marketingInformation != null){
    		if(marketingInformation.getTradeIn())
    			return "Yes";
    		else
    			return "No";
    	}
		return null;
	}

	public String getIsFirstTimeOwnerOfProductBeingRegistered() {
		if(marketingInformation != null){
    		if(marketingInformation.getFirstTimeOwnerOfProductBeingRegistered())
    			return "Yes";
    		else
    			return "No";
    	}
		return null;
	}

	public Boolean getDisclaimer() {
		return disclaimer;
	}

	public void setDisclaimer(Boolean disclaimer) {
		this.disclaimer = disclaimer;
	}

	public DieselTierWaiver getWaiver() {
		return waiver;
	}

	public void setWaiver(DieselTierWaiver waiver) {
		this.waiver = waiver;
	}

	public String getDisclaimerInfo() {
		return disclaimerInfo;
	}

	public void setDisclaimerInfo(String disclaimerInfo) {
		this.disclaimerInfo = disclaimerInfo;
	}

	public List<PrintTransferInventoryObject> getInventoryObjects() {
        return inventoryObjects;
    }

    public void setInventoryObjects(List<PrintTransferInventoryObject> inventoryObjects) {
        this.inventoryObjects = inventoryObjects;
    }

    public Organization getDealer() {
		return dealer;
	}

	public void setDealer(Organization dealer) {
		this.dealer = dealer;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public AddressForTransfer getAddressForTransfer() {
		return addressForTransfer;
	}

	public void setAddressForTransfer(AddressForTransfer addressForTransfer) {
		this.addressForTransfer = addressForTransfer;
	}

	public MarketingInformation getMarketingInformation() {
		return marketingInformation;
	}

	public void setMarketingInformation(
			MarketingInformation marketingInformation) {
		this.marketingInformation = marketingInformation;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public List<TransferCoverageObject> getPolicies() {
		return policies;
	}

	public void setPolicies(List<TransferCoverageObject> policies) {
		this.policies = policies;
	}

    public String getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(String transactionDate) {
        this.transactionDate = transactionDate;
    }

    public boolean isForETR() {
		return forETR;
	}

	public void setForETR(boolean forETR) {
		this.forETR = forETR;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public String getGifName() {
		return gifName;
	}

	public void setGifName(String gifName) {
		this.gifName = gifName;
	}

	public String getFeeTitle() {
		return feeTitle;
	}

	public void setFeeTitle(String feeTitle) {
		this.feeTitle = feeTitle;
	}

	public String getOperatorName() {
		return operatorName;
	}

	public void setOperatorName(String operatorName) {
		this.operatorName = operatorName;
	}

	public AddressForTransfer getOperatorAddressForTransfer() {
		return operatorAddressForTransfer;
	}

	public void setOperatorAddressForTransfer(
			AddressForTransfer operatorAddressForTransfer) {
		this.operatorAddressForTransfer = operatorAddressForTransfer;
	}

	public Party getInstallingDealer() {
		return installingDealer;
	}

	public void setInstallingDealer(Party installingDealer) {
		this.installingDealer = installingDealer;
	}

	public Boolean getIsInstDealerInstDateEnabled() {
		return isInstDealerInstDateEnabled;
	}

	public void setIsInstDealerInstDateEnabled(Boolean isInstDealerInstDateEnabled) {
		this.isInstDealerInstDateEnabled = isInstDealerInstDateEnabled;
	}

	public String getIsCustomerFirstTimeOwner() {
		if(marketingInformation != null){
    		if(marketingInformation.getCustomerFirstTimeOwner())
    			return "Yes";
    		else
    			return "No";
    	}
		return null;
	}

	public String getDealerNumber(){
		String dealerName = this.dealer.getName();
		if(dealerName != null){
			try {
				String splitString[] = dealerName.split("-");
				return splitString[splitString.length - 1];
			} catch (Exception e) {
				logger.error(e);
				return "";
			}
		}
		return "";
	}
	
	public String getSalesPersonName(){
		String salesPersonName = null;
		if(marketingInformation.getDealerRepresentative() != null){
			salesPersonName = marketingInformation.getDealerRepresentative();
		}
		return salesPersonName;
	}
	
	public String getCustomerRepresentative(){
		String customerRepresentative= null;
		if(marketingInformation.getCustomerRepresentative() != null){
			customerRepresentative = marketingInformation.getCustomerRepresentative();
		}
		return customerRepresentative;
	}
	
	public void setSalesOrderNumber(String salesOrderNumber) {
		this.salesOrderNumber = salesOrderNumber;
	}

	public String getSalesOrderNumber() {
		return salesOrderNumber;
	}

	/**
	 * @return the contractDisplay
	 */
	public String getContractDisplay() {
		return contractDisplay;
	}

	/**
	 * @param contractDisplay the contractDisplay to set
	 */
	public void setContractDisplay(String contractDisplay) {
		this.contractDisplay = contractDisplay;
	}

}

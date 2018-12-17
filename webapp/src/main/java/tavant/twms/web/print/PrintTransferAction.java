/**
 * 
 */
package tavant.twms.web.print;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.hibernate.Criteria;

import tavant.twms.dateutil.TWMSDateFormatUtil;
import tavant.twms.domain.common.AdditionalComponentSubType;
import tavant.twms.domain.common.AdditionalComponentType;
import tavant.twms.domain.common.DeliveryCheckList;
import tavant.twms.domain.common.ListOfValues;
import tavant.twms.domain.common.LovRepository;
import tavant.twms.domain.common.PdiService;
import tavant.twms.domain.configuration.ConfigName;
import tavant.twms.domain.configuration.ConfigParamService;
import tavant.twms.domain.inventory.DieselTierWaiver;
import tavant.twms.domain.inventory.I18NWaiverText;
import tavant.twms.domain.inventory.InvTransationType;
import tavant.twms.domain.inventory.InventoryItem;
import tavant.twms.domain.inventory.InventoryItemAdditionalComponents;
import tavant.twms.domain.inventory.InventoryItemComposition;
import tavant.twms.domain.inventory.Option;
import tavant.twms.domain.orgmodel.BrandType;
import tavant.twms.domain.policy.RegisteredPolicy;
import tavant.twms.domain.policy.Warranty;
import tavant.twms.security.SelectedBusinessUnitsHolder;

/**
 * @author mritunjay.kumar
 * 
 */
@SuppressWarnings("serial")
public class PrintTransferAction extends PrintClaimAction {
	private List<InventoryItem> inventoryItems = new ArrayList<InventoryItem>(5);
	private PrintTransferObject printTransferObject = new PrintTransferObject();
    private boolean forETR;
    private ConfigParamService configParamService;
    private Warranty warranty;
    private PdiService pdiService;
    private List<ListOfValues> additionalComponentTypes;
    private List<ListOfValues> additionalComponentSubTypes;
    private LovRepository lovRepository;
    
    private String carriage;
	private String fork;
    private String attachment;
    private String hook;
    private String pin;
     
	public String getHook() {
		return hook;
	}

	public void setHook(String hook) {
		this.hook = hook;
	}

	public String getPin() {
		return pin;
	}

	public void setPin(String pin) {
		this.pin = pin;
	}

	public LovRepository getLovRepository() {
		return lovRepository;
	}

	public void setLovRepository(LovRepository lovRepository) {
		this.lovRepository = lovRepository;
	}

	public PdiService getPdiService() {
		return pdiService;
	}

	public void setPdiService(PdiService pdiService) {
		this.pdiService = pdiService;
	}

	public Warranty getWarranty() {
		return warranty;
	}

	public void setWarranty(Warranty warranty) {
		this.warranty = warranty;
	}

	public List<InventoryItem> getInventoryItems() {
		return inventoryItems;
	}

	public void setInventoryItems(List<InventoryItem> inventoryItems) {
		this.inventoryItems = inventoryItems;
	}

	public PrintTransferObject getPrintTransferObject() {
		return printTransferObject;
	}

	public void setPrintTransferObject(PrintTransferObject printTransferObject) {
		this.printTransferObject = printTransferObject;
	}

	public boolean isForETR() {
		return forETR;
	}

	public void setForETR(boolean forETR) {
		this.forETR = forETR;
	}
	 
	public String printTransfer() {
		List<String> transactionTypes = new ArrayList<String>();
		if (!forETR) {
			transactionTypes.add(InvTransationType.DR.getTransactionType());
			transactionTypes.add(InvTransationType.DR_MODIFY
					.getTransactionType());
			transactionTypes.add(InvTransationType.DR_DELETE
					.getTransactionType());
			transactionTypes.add(InvTransationType.DR_RENTAL.getTransactionType());
		} else {
			transactionTypes.add(InvTransationType.ETR.getTransactionType());
			transactionTypes.add(InvTransationType.ETR_MODIFY
					.getTransactionType());
			transactionTypes.add(InvTransationType.ETR_DELETE
					.getTransactionType());
		}
		prepareTransferDetail(transactionTypes);
		return SUCCESS;
	}

	private void prepareTransferDetail(List<String> transactionTypes) {
        printTransferObject.setForETR(forETR);    
		for (InventoryItem inventoryItem : inventoryItems) {
			setAdditionalComponentTypes(this.lovRepository.findAllActive("AdditionalComponentType"));
			setAdditionalComponentSubTypes(this.lovRepository.findAllActive("AdditionalComponentSubType"));
			for(ListOfValues additionalComponentType:additionalComponentTypes){
				if(additionalComponentType.getCode().equalsIgnoreCase("C"))
					 carriage = additionalComponentType.getDescription();
				if(additionalComponentType.getCode().equalsIgnoreCase("A"))
					 attachment = additionalComponentType.getDescription();
				if(additionalComponentType.getCode().equalsIgnoreCase("F"))
					 fork = additionalComponentType.getDescription();
			}
			for(ListOfValues additionalComponentSubType:additionalComponentSubTypes){
				if(additionalComponentSubType.getCode().equalsIgnoreCase("H"))
					 hook = additionalComponentSubType.getDescription();
				if(additionalComponentSubType.getCode().equalsIgnoreCase("P"))
					 pin = additionalComponentSubType.getDescription();				
			}
			this.warranty=inventoryItem.getLatestWarranty();
            PrintTransferInventoryObject invObject = new PrintTransferInventoryObject();
            invObject.setInventoryItem(inventoryItem);
            printTransferObject.setInventoryItem(inventoryItem);
            if(warranty.getDeliveryDate()!=null)
            	printTransferObject.setDeliveryDate(warranty.getDeliveryDate().toString(TWMSDateFormatUtil.getDateFormatForLoggedInUser()));
           /* if(inventoryItem.isRetailed() && !inventoryItem.getWarranties(transactionTypes).isEmpty() && inventoryItem.getWarranties(transactionTypes).size() > 0 && inventoryItem.getWarranties(transactionTypes).get(0).getDeliveryDate() != null)
              invObject.setDeliveryDate(inventoryItem.getWarranties(transactionTypes).get(0).getDeliveryDate().toString(TWMSDateFormatUtil.getDateFormatForLoggedInUser()));
            invObject.setShipmentDate(inventoryItem.getShipmentDate().toString(TWMSDateFormatUtil.getDateFormatForLoggedInUser()));
            if(inventoryItem.getInstallationDate() != null)
             invObject.setInstallationDate(inventoryItem.getInstallationDate().toString(TWMSDateFormatUtil.getDateFormatForLoggedInUser()));
            if(inventoryItem.getVinNumber() != null)
             invObject.setVinNumber(inventoryItem.getVinNumber());
            if(inventoryItem.getFleetNumber() != null)
             invObject.setFleetNumber(inventoryItem.getFleetNumber());	
            printTransferObject.getInventoryObjects().add(invObject);*/
		}
		InventoryItem invItem = inventoryItems.get(0);
		printTransferObject.setDealer(invItem.getDealer());
		if(invItem.getInstallingDealer() != null)
		  printTransferObject.setInstallingDealer(invItem.getInstallingDealer());
		//List<Warranty> warrantyList = invItem.getWarranties(transactionTypes);
		//Collections.reverse(warrantyList);
		//if(warrantyList.size()>0){
		//warranty = warrantyList.get(0);
		//Collections.reverse(warrantyList);
		if(warranty.getCustomer()!=null)
			printTransferObject.setCustomerName(warranty.getCustomer().getName());
		printTransferObject.setAddressForTransfer(warranty.getAddressForTransfer());
		if(warranty.getOperator() != null)
		  printTransferObject.setOperatorName(warranty.getOperator().getName());
		if(warranty.getOperatorAddressForTransfer() != null)
		  printTransferObject.setOperatorAddressForTransfer(warranty.getOperatorAddressForTransfer());
		printTransferObject.setMarketingInformation(warranty.getMarketingInformation());
		printTransferObject.setComments(warranty.getLatestAudit().getExternalComments());
		
		if (null != invItem.getTransactionHistory()
				&& null != invItem.getTransactionHistory().get(0)) {
			printTransferObject.setSalesOrderNumber(invItem
					.getTransactionHistory().get(0).getSalesOrderNumber());
		}
		
		if (warranty.getForTransaction() != null) {
			printTransferObject.setTransactionDate(
                    warranty.getForTransaction().getTransactionDate().toString(TWMSDateFormatUtil.getDateFormatForLoggedInUser()));
		}
	//}
		printTransferObject.setIsInstDealerInstDateEnabled(configParamService.
				getBooleanValue(ConfigName.ENABLE_DEALER_AND_INSTALLATION_DATE.getName()));
		
		List<TransferCoverageObject> coverages = populatePolicies(
				inventoryItems, transactionTypes);
		printTransferObject.setPolicies(coverages);
		if (!forETR) {
			printTransferObject.setTitle("DELIVERY REPORT");
			printTransferObject.setFeeTitle("Policy Fee");
			printTransferObject.setDateTitle("DELIVERY DATE");
		} else {
			printTransferObject.setTitle("UNIT TRANSFER REPORT");
			printTransferObject.setFeeTitle("Transfer Fee");
			printTransferObject.setDateTitle("TRANSFER DATE");
		}
//		SelectedBusinessUnitsHolder.setSelectedBusinessUnit(getInventoryItems().get(0).getBusinessUnitInfo().getName());
//		String selectedBU = SelectedBusinessUnitsHolder.getSelectedBusinessUnit().replace(" ","" );
//		printTransferObject.setGifName(selectedBU +".gif");
		String selectedBrand = getInventoryItems().get(0).getBrandType();
		/*if(BrandType.UTILEV.name().equalsIgnoreCase(selectedBrand)){
			selectedBrand =this.orgService.findDealerBrands(invItem.getCurrentOwner());
		}*/
		printTransferObject.setGifName(selectedBrand +".gif");
		if(warranty!=null && warranty.getForItem().getIsDisclaimer()){
			printTransferObject.setDisclaimer(true);
			printTransferObject.setDisclaimerInfo(warranty.getForItem().getDisclaimerInfo());
			printTransferObject.setWaiver(warranty.getForItem().getWaiverDuringDr());
		}
		List<OptionObject> options = populateOptions(inventoryItems);
		printTransferObject.setOptions(options);
		List<MajorComponentsObject> majorComponents = populateMajorComponents(inventoryItems);
		printTransferObject.setMajorComponents(majorComponents);
		AttachmentObject additionalAttachment = populateAdditionalComponentAttachment(inventoryItems);
		printTransferObject.setAttachment(additionalAttachment);
		CarriageObject additionalCarriage = populateAdditionalComponentCarriage(inventoryItems,carriage);
		printTransferObject.setCarriage(additionalCarriage);
		CarriageObject additionalFork = populateAdditionalComponentCarriage(inventoryItems,fork);
		printTransferObject.setFork(additionalFork);
		List<DeliveryCheckList> deliveryCheckList = this.pdiService.populateDeliveryCheckList();		
		printTransferObject.setDeliveryChkLst(deliveryCheckList);
		if(warranty!=null){
			printTransferObject.setContractDisplay(warranty.getMarketingInformation().getContractCode()!=null ?
					warranty.getMarketingInformation().getContractCode().getDisplayContractCode() : null);
			}
	}

	private AttachmentObject populateAdditionalComponentAttachment(
			List<InventoryItem> inventoryItems) {
		AttachmentObject additionalAttachment = new AttachmentObject(null, null, null, null);
		 
		if(inventoryItems != null && inventoryItems.size() > 0){
			//InventoryItem invItem = inventoryItems.get(0);
			for(InventoryItem invItem:inventoryItems){
				List<InventoryItemAdditionalComponents> additionalComponentList=invItem.getAdditionalComponents();
				for(InventoryItemAdditionalComponents additionalComponent: additionalComponentList){
				if(additionalComponent != null &&
						additionalComponent.getType() != null &&
						additionalComponent.getType().equalsIgnoreCase(attachment)){
					additionalAttachment.setManufacturer(additionalComponent.getManufacturer());
					additionalAttachment.setModel(additionalComponent.getModel());
					additionalAttachment.setSerialNumber(additionalComponent.getSerialNumber());
					additionalAttachment.setType(additionalComponent.getType());
					break;
					}
				}
			}
		}
		return additionalAttachment;
	}	
	

	private CarriageObject populateAdditionalComponentCarriage(
			List<InventoryItem> inventoryItems, String type) {
		CarriageObject additionalCarriage = new CarriageObject(null, null, null,null);
		 
		if(inventoryItems != null && inventoryItems.size() > 0){
			//InventoryItem invItem = inventoryItems.get(0);
			for(InventoryItem invItem:inventoryItems){
				List<InventoryItemAdditionalComponents> additionalComponentList=invItem.getAdditionalComponents();
				for(InventoryItemAdditionalComponents additionalComponent: additionalComponentList){
					if(additionalComponent!=null &&
							additionalComponent.getType() != null &&
							additionalComponent.getType().equalsIgnoreCase(type)){
						additionalCarriage.setSubType(additionalComponent.getSubType());
						additionalCarriage.setSerialNumber(additionalComponent.getSerialNumber());
						additionalCarriage.setPartNumber(additionalComponent.getPartNumber());
						additionalCarriage.setDateCode(additionalComponent.getDateCode());
						break;
					}
				}
				
			}
		}
		return additionalCarriage;
	}
	
	
	private List<TransferCoverageObject> populatePolicies(
			List<InventoryItem> inventoryItems, List<String> transactionTypes) {
		List<TransferCoverageObject> coverages = new ArrayList<TransferCoverageObject>();
		 
		if(inventoryItems != null && inventoryItems.size() > 0){
			//InventoryItem invItem = inventoryItems.get(0);
			for(InventoryItem invItem:inventoryItems){
			List<Warranty> warrantyList = invItem
					.getWarranties(transactionTypes);
			Collections.reverse(warrantyList);
			if(warrantyList.size()>0){
			Warranty warranty = warrantyList.get(0);
			Set<RegisteredPolicy> policies = warranty.getPolicies();
			List<RegisteredPolicy> sortedPolicies = new ArrayList<RegisteredPolicy>(policies);
			Collections.sort(sortedPolicies);
			for (RegisteredPolicy registeredPolicy : sortedPolicies) {
				TransferCoverageObject coverageObject = new TransferCoverageObject(
						invItem.getSerialNumber(), registeredPolicy.getCode(),
						registeredPolicy.getPrice());
				coverages.add(coverageObject);
			}
			}
			}
			coverages.add(new TransferCoverageObject(null,null,null));
		}
		return coverages;
	}
	
	private List<OptionObject> populateOptions(
			List<InventoryItem> inventoryItems) {
		List<OptionObject> options = new ArrayList<OptionObject>();
		 
		if(inventoryItems != null && inventoryItems.size() > 0){
			//InventoryItem invItem = inventoryItems.get(0);
			for(InventoryItem invItem:inventoryItems){
			List<Option> optionsList = invItem
					.getOptions();
			Collections.reverse(optionsList);
			if(optionsList.size()>0){
			for (Option option : optionsList) {
				OptionObject optionObject = new OptionObject(
						 option.getOptionCode(),
						option.getOptionDescription(),invItem.getSerialNumber());
				options.add(optionObject);
			}
			}
			}
			options.add(new OptionObject(null,null,null));
		}
		if(options.size()>10)
			return options.subList(0, 10);
		else
			return options;
	}
	
	private List<MajorComponentsObject> populateMajorComponents(
			List<InventoryItem> inventoryItems) {
		List<MajorComponentsObject> majorComponents = new ArrayList<MajorComponentsObject>();
		 
		if(inventoryItems != null && inventoryItems.size() > 0){
			//InventoryItem invItem = inventoryItems.get(0);
			for(InventoryItem invItem:inventoryItems){
			List<InventoryItemComposition> majorComponentsList = invItem
					.getComposedOf();
			if(majorComponentsList.size()>0){
			for (InventoryItemComposition majorComponent : majorComponentsList) {
				MajorComponentsObject majorComponentObject = new MajorComponentsObject(
						majorComponent.getComponentSerialType(),
						majorComponent.getSerialTypeDescription(),majorComponent.getPart().getSerialNumber());
				majorComponents.add(majorComponentObject);
			}
			}
			}
			majorComponents.add(new MajorComponentsObject(null,null,null));
		}
		if(majorComponents.size()>8)
			return majorComponents.subList(0, 8);
		else
			return majorComponents;
	}
	
	

	public ConfigParamService getConfigParamService() {
		return configParamService;
	}

	public void setConfigParamService(ConfigParamService configParamService) {
		this.configParamService = configParamService;
	}

	public List<ListOfValues> getAdditionalComponentTypes() {
		return additionalComponentTypes;
	}

	public void setAdditionalComponentTypes(List<ListOfValues> additionalComponentTypes) {
		this.additionalComponentTypes = additionalComponentTypes;
	}

	public List<ListOfValues> getAdditionalComponentSubTypes() {
		return additionalComponentSubTypes;
	}

	public void setAdditionalComponentSubTypes(
			List<ListOfValues> additionalComponentSubTypes) {
		this.additionalComponentSubTypes = additionalComponentSubTypes;
	}	
	
    public String getCarriage() {
		return carriage;
	}

	public void setCarriage(String carriage) {
		this.carriage = carriage;
	}

	public String getFork() {
		return fork;
	}

	public void setFork(String fork) {
		this.fork = fork;
	}

	public String getAttachment() {
		return attachment;
	}

	public void setAttachment(String attachment) {
		this.attachment = attachment;
	}

}

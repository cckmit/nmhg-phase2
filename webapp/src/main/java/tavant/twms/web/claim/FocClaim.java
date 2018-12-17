package tavant.twms.web.claim;

import static tavant.twms.web.admin.jobcode.AssemblyTreeJSONifier.ASM_CHILDREN;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TimeZone;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;

import tavant.twms.claim.FocBean;
import tavant.twms.domain.catalog.CatalogException;
import tavant.twms.domain.catalog.CatalogService;
import tavant.twms.domain.catalog.Item;
import tavant.twms.domain.catalog.ItemReference;
import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.claim.ClaimedItem;
import tavant.twms.domain.claim.HussmanPartsReplacedInstalled;
import tavant.twms.domain.claim.InstalledParts;
import tavant.twms.domain.claim.MachineClaim;
import tavant.twms.domain.claim.OEMPartReplaced;
import tavant.twms.domain.claim.foc.FocOrder;
import tavant.twms.domain.claim.foc.FocService;
import tavant.twms.domain.failurestruct.ActionNode;
import tavant.twms.domain.failurestruct.Assembly;
import tavant.twms.domain.failurestruct.FailureStructure;
import tavant.twms.domain.failurestruct.FailureStructureService;
import tavant.twms.domain.failurestruct.FailureTypeDefinition;
import tavant.twms.domain.failurestruct.FaultCode;
import tavant.twms.domain.inventory.InventoryItem;
import tavant.twms.domain.inventory.InventoryService;
import tavant.twms.domain.inventory.ItemNotFoundException;
import tavant.twms.domain.policy.Policy;
import tavant.twms.domain.policy.PolicyException;
import tavant.twms.domain.policy.PolicyService;
import tavant.twms.infra.DomainRepository;
import tavant.twms.jbpm.infra.BeanLocator;
import tavant.twms.web.actions.TwmsActionSupport;
import tavant.twms.web.admin.jobcode.AssemblyTreeJSONifier;
import tavant.twms.web.admin.jobcode.AssemblyTreeJSONifier.Filter;

import com.domainlanguage.time.CalendarDate;
import com.domainlanguage.timeutil.Clock;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

@SuppressWarnings("serial")
public class FocClaim extends TwmsActionSupport {

	private static final Logger logger = Logger.getLogger(FocClaim.class
			.getName());
	
	private FocBean focbean = new FocBean();
	private boolean isHomeButtonHidden = true;	
	private boolean isSerialNumberAvailable;
	private List<HussmanPartsReplacedInstalled> hussmanPartsReplacedInstalled = new ArrayList<HussmanPartsReplacedInstalled>();
	private int rowIndex =0;
	private int subRowIndex=0;
	private String faultCodeID;
	private String status;
	private Boolean allowSessionTimeOut = Boolean.FALSE;
    private String requestType;
	private FailureStructureService failureStructureService;
	private FocService focService;
	private InventoryService inventoryService;
	private PolicyService policyService;
	private AssemblyTreeJSONifier asmTreeJSONifier;
	private CatalogService catalogService;
	private DomainRepository domainRepository;
	private String ploc; 
	
	/*
	 * Inner fields
	 */
	
	FailureTypeDefinition faultFound;
	
    FaultCode faultCodeRef;

    Item  causalPart;
	
    /*String faultCode;*/
	
	
	public String execute() {
		try{
		Assert.isTrue(StringUtils.isNotBlank(this.focbean.getOrderNo()),"Invalid paramter: OrderNo");
		if("M".equals(this.requestType)){		
			FocOrder focOrder = this.focService.fetchFOCOrderDetails(this.focbean.getOrderNo());
			/*Assert.isTrue(focOrder!=null,"No Order available for orderNo:"+this.focbean.getOrderNo());*/
			if(focOrder == null){
				this.status = "DRAFT";
				return "NO_ORDER";
			}
			
		  	XStream xstream = new XStream(new DomDriver());
		  	this.focbean =  (FocBean)xstream.fromXML(focOrder.getOrderInfo());		  	
		  	rowIndex = this.focbean.getHussmanPartsReplacedInstalled() == null ? 0 : this.focbean.getHussmanPartsReplacedInstalled().size();
		  	populateWidgetFields();
		} else {
		  Assert.isTrue(StringUtils.isNotBlank(this.focbean.getSerialNumber()),"Invalid parameter: SerialNo");
			
			try {
				 this.inventoryService.findInventoryItemBySerialNumber(focbean
						.getSerialNumber());
			} catch (ItemNotFoundException e) {
				this.status = "SN_INVALID";
				return "SN_INVALID";
			}
			
			//TODO CHECK IF ORDER ALREADY EXISTS
		}
	    this.isSerialNumberAvailable = true;
	    prepareReplacedInstalledPartsDisplay();
		}catch(Throwable t){	
			logger.error("In method execute()"+t,t);
			this.status = "TWMS_FAILURE";
			return "NO_ORDER";
		}
		return SUCCESS;
	}

	private void validateClaimData() {

		if (this.focbean == null) {
			addActionError("foc.widget.missingData");
			return;
		}

		if (StringUtils.isBlank(this.focbean.getSerialNumber())) {
			addActionError("foc.widget.serialNumberMandatory");
		}
		
		if(this.getFaultFound() == null){
			addActionError("foc.widget.faultFoundMandatory");
		}

		if (this.getCausalPart() == null) {
			addActionError("foc.widget.causalPartNumberMandatory");
		}

		if (this.getFaultCodeRef() == null) {
			addActionError("foc.widget.faultCodeMandatory");
		}

		if (StringUtils.isBlank(this.focbean.getWorkOrderNumber())) {
			addActionError("foc.widget.workOrderNumberMandatory");
		}

		if (this.focbean.getFailureDate() == null) {
			addActionError("foc.widget.failureDateMandatory");
		}

		if (this.focbean.getRepairDate() == null) {
			addActionError("foc.widget.repairDateMandatory");
		}
		
		if(this.focbean.getFailureDate()!=null && this.focbean.getRepairDate()!=null){			
			if(this.focbean.getRepairDate().isBefore(this.focbean.getFailureDate())){
				addActionError("foc.widget.repairDateBeforeFailure");
			}			
		}
		
		
		validateHussmanParts();
	}
	
	
	private void validateHussmanParts(){
		
		int noOfValidHussmanConfigurations =0;
		if(this.hussmanPartsReplacedInstalled != null && this.hussmanPartsReplacedInstalled.size() >0){
			for (HussmanPartsReplacedInstalled element : hussmanPartsReplacedInstalled ) {
				//validate parts replaced
				boolean isReplacedPartValid =false;
				if (element.getReplacedParts() != null
						&& element.getReplacedParts().size() > 0) {
				    element.getReplacedParts().removeAll(Collections.singleton(null));
				    List<String> replacedPartsList = new ArrayList<String>();
					for (OEMPartReplaced replacedPart : element
							.getReplacedParts()) {
						
						if(replacedPart.getItemReference() == null ||replacedPart.getItemReference().getReferredItem() == null){
							 addActionError("foc.widget.partNumberMandatory");
						} else{							
							isReplacedPartValid =true;
	                         if(replacedPartsList.contains(replacedPart.getItemReference().getReferredItem().getNumber())){
	                        	 addActionError("foc.widget.replacedPartRepeated");
	                         } else{
	                        	 replacedPartsList.add(replacedPart.getItemReference().getReferredItem().getNumber());	                         
	                         }
						}
						
                         if(replacedPart.getNumberOfUnits() == null){
                        	 addActionError("foc.widget.partQuantityMandatory");
                         }
                         
					}
				}
				//validate installed parts
				
				boolean isInstalledPartValid = false;
				if (element.getHussmanInstalledParts() != null
						&& element.getHussmanInstalledParts().size() > 0) {
				    element.getHussmanInstalledParts().removeAll(Collections.singleton(null));
				    List<String> installedPartList = new ArrayList<String>();
					for (InstalledParts installedPart : element.getHussmanInstalledParts()) {
                      if(installedPart.getItem() == null){
                    	  addActionError("foc.widget.partNumberMandatory");
                      } else{                    	  
                    	  isInstalledPartValid = true;
                          if(installedPartList.contains(installedPart.getItem().getNumber())){
                          	 addActionError("foc.widget.installedPartRepeated");
                           } else{
                              installedPartList.add(installedPart.getItem().getNumber());
                           }
                      }

                      if(installedPart.getNumberOfUnits()==null){
                    	  addActionError("foc.widget.partQuantityMandatory");
                      }
                      
                     
					}
				}
				
				if(isReplacedPartValid && isInstalledPartValid){
					noOfValidHussmanConfigurations++;
				}
				
			}
		}
		
		if(noOfValidHussmanConfigurations == 0){
			addActionError("foc.widget.partQuantityMandatory");
		}
	}
	

	public String submit() throws Exception {
		try{
		this.hussmanPartsReplacedInstalled.removeAll(Collections.singleton(null));
		validateClaimData();
		if(hasActionErrors()){
			this.isSerialNumberAvailable = true;
			return INPUT;
		}
		prepareFocBeanForSave();
		XStream xstream = new XStream(new DomDriver());
		String xml = xstream.toXML(focbean);
        FocOrder focOrder = null;        
        focOrder = this.focService.fetchFOCOrderDetails(focbean.getOrderNo());
        if(focOrder == null){
        	focOrder = new FocOrder();
        }
        focOrder.setOrderInfo(xml);   
        focOrder.setOrderNo(this.focbean.getOrderNo());		
		focOrder.getD().setActive(Boolean.TRUE);
		focOrder.getD().setCreatedOn(CalendarDate.from(Clock.now(),TimeZone.getDefault()));
		focOrder.getD().setCreatedTime(new java.util.Date());
		if (isMachineUnderWarranty()) {
			focOrder.setStatus(FocOrder.CLAIM_INFO_AWAITED);
			this.status = "SUCCESS";			
		} else {
			focOrder.setStatus(FocOrder.ORDER_PERSISTED);
			this.status = "FAILURE";
		}
		this.focService.save(focOrder);
		}catch(Throwable t){	
			logger.error("Error in submit for order"+focbean.getOrderNo());
			logger.error("Error in submit for order",t);
			this.status = "TWMS_FAILURE";
			return "NO_ORDER";
		}
		
		return SUCCESS;
	}

	private void prepareFocBeanForSave() {
		/*focbean.setHussmanPartsReplacedInstalled(this.hussmanPartsReplacedInstalled);*/
		focbean.getCausalPart().setNumber(this.causalPart.getNumber());
		focbean.getFaultCodeRef().setId(this.getFaultCodeRef().getId());
		/*focbean.setFaultCode(this.getFaultCode());*/
		focbean.getFaultFound().setId((this.getFaultFound().getId()));
		prepareHussmanPartsForSave();
		focbean.setHussmanPartsReplacedInstalled(this.hussmanPartsReplacedInstalled);
		logger.error("Fault code obtained is "+this.getFaultCodeRef().getId());
	}
	
	/**
	 * We just remove references to hibernate calls in this method.
	 * we only store item number and required fields
	 */
	
	private void prepareHussmanPartsForSave(){
		
		if(this.hussmanPartsReplacedInstalled != null && this.hussmanPartsReplacedInstalled.size() >0){
			for (HussmanPartsReplacedInstalled element : hussmanPartsReplacedInstalled ) {
				//validate parts replaced
				if (element.getReplacedParts() != null
						&& element.getReplacedParts().size() > 0) {
				    element.getReplacedParts().removeAll(Collections.singleton(null));
					for (OEMPartReplaced replacedPart : element
							.getReplacedParts()) {
	                    ItemReference itemReference = new ItemReference();
	                    Item item = new Item();
	                    item.setNumber(replacedPart.getItemReference().getReferredItem().getNumber());
	                    itemReference.setReferredItem(item);
	                    replacedPart.setItemReference(itemReference);
					}
				}
				
				if(element.getHussmanInstalledParts() !=null && element.getHussmanInstalledParts().size() >0){					
					  element.getHussmanInstalledParts().removeAll(Collections.singleton(null));
						for (InstalledParts installedPart : element
								.getHussmanInstalledParts()) {		                    
		                    Item item = new Item();
		                    item.setNumber(installedPart.getItem().getNumber());
		                    installedPart.setItem(item);
						}
					  
				}
				
			}
		}
	}

	
	private void prepareReplacedInstalledPartsDisplay() {
		setRowIndex(this.hussmanPartsReplacedInstalled.size());
	}
	
	
	public String saveDraft() throws Exception {
		focbean.setHussmanPartsReplacedInstalled(this.hussmanPartsReplacedInstalled);
		XStream xstream = new XStream(new DomDriver());
		String xml = xstream.toXML(focbean);
		Assert.isTrue(StringUtils.isNotBlank(focbean.getOrderNo()),"Invalid paramter: OrderNo");				
        FocOrder focOrder = null;        
        focOrder = this.focService.fetchFOCOrderDetails(focbean.getOrderNo());
        if(focOrder == null){
        	focOrder = new FocOrder();
        }
        focOrder.setOrderInfo(xml);
        focOrder.setOrderNo(this.focbean.getOrderNo());
		this.focService.save(focOrder);
		this.isSerialNumberAvailable = true;
		return SUCCESS;
	}
	
	private boolean isMachineUnderWarranty() {

		InventoryItem inventoryItem = null;
		try {
			inventoryItem = this.inventoryService.findInventoryItemBySerialNumber(focbean
					.getSerialNumber());
		} catch (ItemNotFoundException e) {			
		}

		ClaimedItem claimedItem = new ClaimedItem();
		claimedItem.setItemReference(new ItemReference(inventoryItem));
		claimedItem.setHoursInService(new BigDecimal(0.0));

		Claim claim = new MachineClaim();
		claim.setFailureDate(this.focbean.getFailureDate());
		claim.setRepairDate(this.focbean.getRepairDate());
		claim.addClaimedItem(claimedItem);

		Policy policy = null;

		try {
			policy = policyService.findApplicablePolicy(claimedItem);
		} catch (PolicyException e) {
		}

		if (policy != null) {
			return true;
		}

/*
		Warranty warranty = this.warrantyService.findWarranty(inventoryItem);
		for (Policy element : warranty.getPolicies()) {
			CalendarDuration calendarDuration = null;
			try {
				calendarDuration = element.getPolicyDefinition()
						.warrantyPeriodFor(inventoryItem);
			} catch (PolicyException e) {
				e.printStackTrace();
				continue;
			}
			if (calendarDuration.includes(claim.getRepairDate())) {
				return true;
			}
		}
*/		
		return false;
	}

	
	private void populateWidgetFields(){
		if(this.domainRepository == null){
			initDomainRepository();
		}		
		try {
			causalPart = this.catalogService.findItemOwnedByManuf(this.focbean.getCausalPart().getNumber());
		} catch (CatalogException e) {		
			e.printStackTrace();
		}		
		faultCodeRef = (FaultCode)this.domainRepository.load(FaultCode.class, this.focbean.getFaultCodeRef().getId());
		faultFound =(FailureTypeDefinition) this.domainRepository.load(FailureTypeDefinition.class,this.focbean.getFaultFound().getId());
		this.hussmanPartsReplacedInstalled = this.focbean.getHussmanPartsReplacedInstalled();
	/*	this.faultCode = faultCodeRef.getDefinition().getCode();*/
		prepareReplacedParts(this.hussmanPartsReplacedInstalled);	
	}

	
	private void prepareReplacedParts(List<HussmanPartsReplacedInstalled> huList ){
		    	
		huList.removeAll(Collections.singleton(null));
		for (HussmanPartsReplacedInstalled hussmanConfig : huList) {
			
			if(hussmanConfig.getReplacedParts() !=null){
				List<OEMPartReplaced> rParts = hussmanConfig.getReplacedParts();
				rParts.removeAll(Collections.singleton(null));
				for (OEMPartReplaced element : rParts) {
					Item itemToReturn =null; 
					try {
						itemToReturn =  this.catalogService.findItemOwnedByManuf(element.getItemReference().getReferredItem().getNumber());
					} catch (CatalogException e) {					
						e.printStackTrace();
					}
			    	ItemReference itemReference = new ItemReference();
			    	itemReference.setReferredItem(itemToReturn);
			    	element.setItemReference(itemReference);
				}
			}
		}
		
	}
	
	
	public String fetchSerialNumberDetails(){
		this.isSerialNumberAvailable = true;
		return SUCCESS;
	}

	public List<FailureTypeDefinition> prepareFaultFoundList(String serialNumber) {
		String invId = "";
		try {
			 InventoryItem invItem = this.inventoryService.findInventoryItemBySerialNumber(serialNumber);
			 if(invItem != null && invItem.getId() != null){
				 invId = invItem.getId().toString(); 
			 }
		} catch (ItemNotFoundException e) {
			return null;
		}
		List<FailureTypeDefinition> possibleFailures = this.failureStructureService
				.findFaultFoundOptions(invId);
		return possibleFailures;
	}

	public String getJsonFaultCodeTree() throws JSONException {
		Filter filter = new Filter() {

			public boolean preTestNode(Assembly assembly) {
				if ((assembly.getComposedOfAssemblies() != null)
						&& (assembly.getComposedOfAssemblies().size() > 0)) {
					return true;
				}

				return assembly.isFaultCode();
			}

			public boolean preTestNode(ActionNode actionNode) {
				return false;
			}

			public boolean postTestNode(JSONObject node, Assembly assembly)
					throws JSONException {
				if (node.getJSONArray(ASM_CHILDREN).length() > 0) {
					return true;
				}

				return assembly.isFaultCode();
			}

			public boolean postTestNode(JSONObject node, ActionNode actionNode) {
				return false;
			}

			public boolean includeFaultCodeInfo() {
				return true;
			}
		};
		FailureStructure fs = getFailureStructure();
		return this.asmTreeJSONifier.getSerializedJSONString(
				fs, filter, null);
	}

	private FailureStructure getFailureStructure()  {
        InventoryItem inventoryItem = null; 
        try {
			inventoryItem =	this.inventoryService.findInventoryItemBySerialNumber(this.focbean.getSerialNumber());
		} catch (ItemNotFoundException e) {
			return null;			
		}
			
/*		if (inventoryItem.getOfType().getModel() != null) {
			return this.failureStructureService
					.getFailureStructureForItemGroup(inventoryItem.getOfType().getModel());
		} else {*/
		return this.failureStructureService
					.getFailureStructureForItem(inventoryItem.getOfType());
		/*}*/
	}
	
	
	public void setCompanyId(String companyId) {
		this.focbean.setCompanyId(companyId);
	}

	public void setCompanyName(String companyName) {
		this.focbean.setCompanyName(companyName);
	}

	public void setSerialNo(String serialNo) {
		this.focbean.setSerialNumber(serialNo);
	}

	public void setServiceProviderNo(String serviceProviderNo) {
		this.focbean.setServiceProviderNo(serviceProviderNo);
	}

	public void setServiceProviderType(String serviceProviderType) {
		this.focbean.setServiceProviderType(serviceProviderType);
	}
	

	public void setFailureStructureService(
			FailureStructureService failureStructureService) {
		this.failureStructureService = failureStructureService;
	}
	
	@Required
	public void setAssemblyTreeJsonifier(AssemblyTreeJSONifier jsonifier) {
		this.asmTreeJSONifier = jsonifier;
	}


	public FocBean getFocbean() {
		return focbean;
	}

	public void setFocbean(FocBean focbean) {
		this.focbean = focbean;
	}


	public boolean isSerialNumberAvailable() {
		return isSerialNumberAvailable;
	}


	public void setSerialNumberAvailable(boolean isSerialNumberAvailable) {
		this.isSerialNumberAvailable = isSerialNumberAvailable;
	}


	public List<HussmanPartsReplacedInstalled> getHussmanPartsReplacedInstalled() {
		return hussmanPartsReplacedInstalled;
	}


	public void setHussmanPartsReplacedInstalled(
			List<HussmanPartsReplacedInstalled> hussmanPartsReplacedInstalled) {
		this.hussmanPartsReplacedInstalled = hussmanPartsReplacedInstalled;
	}


	public int getRowIndex() {
		return rowIndex;
	}


	public void setRowIndex(int rowIndex) {
		this.rowIndex = rowIndex;
	}


	public int getSubRowIndex() {
		return subRowIndex;
	}


	public void setSubRowIndex(int subRowIndex) {
		this.subRowIndex = subRowIndex;
	}


	public String getFaultCodeID() {
		return faultCodeID;
	}


	public void setFaultCodeID(String faultCodeID) {
		this.faultCodeID = faultCodeID;
	}


	public void setInventoryService(InventoryService inventoryService) {
		this.inventoryService = inventoryService;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public void setPolicyService(PolicyService policyService) {
		this.policyService = policyService;
	}


	public void setFocService(FocService focService) {
		this.focService = focService;
	}

	public Boolean isAllowSessionTimeOut() {
		return allowSessionTimeOut;
	}

	public void setAllowSessionTimeOut(Boolean allowSessionTimeOut) {
		this.allowSessionTimeOut = allowSessionTimeOut;
	}

	public void setOrderNo(String orderNo) {
		this.focbean.setOrderNo(orderNo);
	}

	public String getRequestType() {
		return requestType;
	}

	public void setRequestType(String requestType) {
		this.requestType = requestType;
	}

	public void setCID(String CID){
		this.focbean.setCompanyId(CID);
	}

	public void setCNM(String CNM){
	    this.focbean.setCompanyName(CNM);
	}

	public FailureTypeDefinition getFaultFound() {
		return faultFound;
	}

	public void setFaultFound(FailureTypeDefinition faultFound) {
		this.faultFound = faultFound;
	}

	public FaultCode getFaultCodeRef() {
		return faultCodeRef;
	}

	public void setFaultCodeRef(FaultCode faultCodeRef) {
		this.faultCodeRef = faultCodeRef;
	}

	public Item getCausalPart() {
		return causalPart;
	}

	public void setCausalPart(Item causalPart) {
		this.causalPart = causalPart;
	}

	public String getFaultCode() {
		return this.faultCodeRef.getDefinition().getCode();
	}

/*	public void setFaultCode(String faultCode) {
		this.faultCode = faultCode;
	}*/

	public void setCatalogService(CatalogService catalogService) {
		this.catalogService = catalogService;
	}

    private void initDomainRepository() {
        BeanLocator beanLocator = new BeanLocator();
        this.domainRepository = (DomainRepository) beanLocator.lookupBean("domainRepository");
    }

	public void setDomainRepository(DomainRepository domainRepository) {
		this.domainRepository = domainRepository;
	}

	public boolean isHomeButtonHidden() {
		return isHomeButtonHidden;
	}

	public String getPloc() {
		return ploc;
	}

	public void setPloc(String ploc) {
		this.ploc = ploc;
	}
    
	

	
}

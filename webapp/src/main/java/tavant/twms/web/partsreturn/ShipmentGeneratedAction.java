
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

package tavant.twms.web.partsreturn;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.jbpm.graph.def.Transition;
import org.jbpm.taskmgmt.exe.TaskInstance;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.claim.ClaimAudit;
import tavant.twms.domain.claim.OEMPartReplaced;
import tavant.twms.domain.claim.RecoveryClaim;
import tavant.twms.domain.claim.RecoveryClaimAudit;
import tavant.twms.domain.configuration.ConfigName;
import tavant.twms.domain.configuration.ConfigOptionConstants;
import tavant.twms.domain.orgmodel.EventState;
import tavant.twms.domain.orgmodel.Role;
import tavant.twms.domain.partreturn.*;
import tavant.twms.domain.supplier.recovery.RecoverablePart;
import tavant.twms.jbpm.WorkflowConstants;
import tavant.twms.process.PartReturnProcessingService;
import tavant.twms.process.PartTaskBean;
import tavant.twms.security.SelectedBusinessUnitsHolder;
import tavant.twms.web.xforms.TaskViewService;
import tavant.twms.worklist.WorkListCriteria;

import com.domainlanguage.time.CalendarDate;
import com.opensymphony.xwork2.Preparable;

@SuppressWarnings("serial")
public class ShipmentGeneratedAction extends PartReturnInboxAction implements Preparable, ServletRequestAware, ConfigOptionConstants {

    private Long carrierId;

    private String trackingNumber;

    private CarrierRepository carrierRepository;

    private TaskViewService taskViewService;
    
    private WarehouseService warehouseService;
   
    private Claim claim;

	private PartReturnProcessingService partReturnProcessingService;

    private String comments;
    
    private Integer hour;
    
    private Integer minute;

    private ShipmentRepository shipmentRepository;
    
    private static final Logger logger = Logger
    .getLogger(ShipmentGeneratedAction.class);

    private Date shipmentDate;

    /* Todo: This is a temporary quick patch.
       We need to figure out how the datetimepicker should be configured so that the date format will be unique for all requests.
       Tried with putting displayFormat attribute but that is not working properly.
     */
    private CalendarDate shipmentCalenderDate;

    public static final String SHIPMENT_GENERATED_TASK_NAME = "Shipment Generated";

    public HttpServletRequest request;

    private List<ClaimWithPartBeans> claimWithPartBeansList = new ArrayList<ClaimWithPartBeans>();

    private List<OEMPartReplacedBean> selectedPartReplacedToAdd = new ArrayList<OEMPartReplacedBean>();

    private boolean isDealer = false;

    private List<ShipmentLoadDimension> shipmentLoadDimension = new ArrayList<ShipmentLoadDimension>();
    
    private String supplierComments;
          

	public String getSupplierComments() {
		return supplierComments;
	}

	public void setSupplierComments(String supplierComments) {
		this.supplierComments = supplierComments;
	}

	@Override
	public void validate() {
		if (!"Remove Part".equals(transitionTaken)
				&& !"Submit".equals(transitionTaken) && !"SubmitShipment".equals(transitionTaken)) {
			List<PartTaskBean> partTasks = getSelectedPartTaskBeans(
					selectedPartReplacedToAdd, false);
			if (partTasks.size() == 0) {
				addActionError("error.partReturnConfiguration.noPartSelected");
			}
			if (!hasActionErrors()) {
					validateData();
			}
		} else if ("Submit".equals(transitionTaken) || "SubmitShipment".equals(transitionTaken)) {
			List<PartTaskBean> selectedpartTasks = getSelectedPartTaskBeans(
					getPartReplacedBeans(), false);
			if (selectedpartTasks.size() == 0) {
				addActionError("error.partReturnConfiguration.noPartSelected");
			}
				if (!StringUtils.hasText(getComments())) {
					addActionError("error.manageFleetCoverage.commentsMandatory");
				}

                if (!isShipmentThroughCEVA() && !getTaskName().equalsIgnoreCase(WorkflowConstants.SHIPMENT_GENERATED_FOR_DEALER) && !StringUtils.hasText(getTrackingNumber())) {
                    addActionError("error.partReturn.trackingInfo.mandatory");
                }

                if ((!isShipmentThroughCEVA() && shipmentCalenderDate == null)) {
                    addActionError("error.partReturnConfiguration.shipmentDateIsRequired");
                }

                if(getTaskName().equalsIgnoreCase(WorkflowConstants.SHIPMENT_GENERATED_FOR_DEALER) && shipmentCalenderDate == null){
                    addActionError("error.partReturnConfiguration.availableDateIsRequired");
                }

                if(!isShipmentThroughCEVA() && !getTaskName().equalsIgnoreCase(WorkflowConstants.SHIPMENT_GENERATED_FOR_DEALER)&& (carrierId == null || (carrierId !=null && carrierId.equals("")))){
                    addActionError("error.sra.contract.carrier");
                }

				if ((!isShipmentThroughCEVA() && shipmentDate != null ) || (getTaskName().equalsIgnoreCase(WorkflowConstants.SHIPMENT_GENERATED_FOR_DEALER) && shipmentCalenderDate != null) ) {
                    if(!isBuConfigAMER()){
						if(hour == -1 ){
	                        addActionError("error.field.hour");
	                    }
	                    if(minute == -1){
	                        addActionError("error.field.minute");
	                    }
                    }
	
	                    if(hour!=null && hour != -1){
	                        this.shipmentDate.setHours(hour);
	                    }else{
	                        this.shipmentDate.setHours(00);
	                    }
	                    if(minute!=null && minute != -1){
	                        this.shipmentDate.setMinutes(minute);
	                    }else{
	                        this.shipmentDate.setMinutes(00);
	                    }
                    

                    //Demo comments : Future date should be allowed. For details talk to Priya
                    // for any flow if shipment date is there it should be today or future date
                    // for us shipment not through ceva
	                // Shipment date in past validation is not required for AMER -- SLMSPROD-1480
                    if (shipmentDate.getTime() < new Date().getTime() && shipmentDate.getDate() != new Date().getDate() && getTaskName().equalsIgnoreCase(WorkflowConstants.SHIPMENT_GENERATED_FOR_DEALER)) {
                        addActionError("error.partReturnConfiguration.availableDateInPast");
                    }else if(isShipmentThroughCEVA() && shipmentDate.getTime() < new Date().getTime() && shipmentDate.getDate() != new Date().getDate()){
                        addActionError("error.partReturnConfiguration.shipmentDateInPast");
                    }
				}

                if(isShipmentThroughCEVA() && shipmentLoadDimension != null && !isBuConfigAMER()){
                    if(shipmentLoadDimension.size() == 0){
                        addActionError("message.partReturn.shipment.provideLoadDimesion");
                    } else{
                    //Get all the shipment
                    boolean flagForErrorFound = false;
                    boolean isLoadTypeNotSelected = false;
                    if(shipmentLoadDimension.size() > 0){
                        for(ShipmentLoadDimension loadDimension : shipmentLoadDimension){
                            if(null != loadDimension){
                            	  if(!StringUtils.hasText(loadDimension.getLoadType())){
                            		  isLoadTypeNotSelected  = true;
                                  }
                                  if(!isPositiveNumber(loadDimension.getBreadth())  
                                	  || !isPositiveNumber(loadDimension.getLength())
                                	  || !isPositiveNumber(loadDimension.getHeight())
                                	  || !isPositiveNumber(loadDimension.getWeight())){
                                	  flagForErrorFound = true;
                                  }
                                  }
                                  }
                        //If a LoadDimension record is getting removed from top or middle it is set as null
                        List<ShipmentLoadDimension> nullRemovedList = new ArrayList<ShipmentLoadDimension>();
                        for (ShipmentLoadDimension sld : shipmentLoadDimension) {
                        	if (sld != null) {
                        		nullRemovedList.add(sld);
                                  }
                            }
                        shipmentLoadDimension.clear();
                        shipmentLoadDimension.addAll(nullRemovedList);

                    }
                    if(flagForErrorFound){
                        addActionError("message.partReturn.shipment.provideLoadDimesion");
                      }
                    if(isLoadTypeNotSelected){
                    	 addActionError("message.partReturn.shipment.provideLoadType");
                      }
                  }

                }
                
			if(!hasActionErrors() && !receiversSetForWarehouse()){
					addActionError("error.NoReceiversPresent.ForWarehouse");
				}

			}
		if (hasActionErrors() || hasFieldErrors()) {
			generateView();

		}
	}

	private boolean receiversSetForWarehouse() {
		boolean receiverSet = false;
		if (!CollectionUtils.isEmpty(getPartReplacedBeans()) && getPartReplacedBeans().get(0) != null) {
			try {
                if(getPartReplacedBeans().get(0).getOemPartReplaced().isReturnDirectlyToSupplier()){
                    receiverSet = true;
                }
				Location location = getPartReplacedBeans().get(0).getPartReturnTasks().get(0).getPartReturn().getReturnLocation();
				if ((warehouseService.getReceiverAtLocation(location)) != null) {
					receiverSet = true;
				}
			} catch (Exception e) {
					logger.debug("Invalid data" );
			}
		}
		return receiverSet;
	}
	
	 public boolean isPageReadOnly() {
			return false;
		}
	 
	 public boolean isPageReadOnlyAdditional() {
			boolean isReadOnlyDealer = false;
			Set<Role> roles = getLoggedInUser().getRoles();
			for (Role role : roles) {
				if (role.getName().equalsIgnoreCase(Role.READ_ONLY_DEALER)) {
					isReadOnlyDealer = true;
					break;
				}
			}
			return isReadOnlyDealer;
		}
	
	
    
	public void validateData() {
		for (OEMPartReplacedBean partReplacedBean : getSelectedPartReplacedToAdd()) {
			if (partReplacedBean.isSelected()) {
				if (partReplacedBean.getPartReturnTasks() != null
						&& (isTaskShipmentGenerated()))
					if ((partReplacedBean.getCannotShip() == 0 && partReplacedBean
							.getShip() == 0)) {
						addActionError("error.partReturnConfiguration.noPartSelected");
					} else if (partReplacedBean.getCannotShip()
							+ partReplacedBean.getShip() > partReplacedBean.partReturnTasks
							.size()) {
						addActionError("error.partReturnConfiguration.excessPartsShipmentGenerate");
					}
			}
		}
	}

    public ShipmentGeneratedAction() {
        super();
        setActionUrl("shipmentGenerated");
    }

    public void prepare() throws Exception {
    	if (isLoggedInUserADealer()){
			isDealer = true;
		}
        if(shipmentCalenderDate != null){
            shipmentDate =  new Date(shipmentCalenderDate.breachEncapsulationOf_year()-1900,shipmentCalenderDate.breachEncapsulationOf_month()-1,shipmentCalenderDate.breachEncapsulationOf_day());
        }
    }

    public List<Carrier> getShimentCarriers() {
    	//Fetching the shipment carrier for the business unit of the first claim in the shipment generated group
    	List<ClaimWithPartBeans> claimsWithPart = getClaimWithPartBeans();    	
		for (Iterator<ClaimWithPartBeans> iterator = claimsWithPart
				.iterator(); iterator.hasNext();) {
			ClaimWithPartBeans individualClaim = iterator.next();
			if(individualClaim  != null)
			{
				SelectedBusinessUnitsHolder.setSelectedBusinessUnit(individualClaim.getClaim().getBusinessUnitInfo().getName());
				break;
			}
		}    	
        final List<Carrier> allCarriers = carrierRepository.findAllCarriers();
        return allCarriers;
    }
    
    public List<Carrier> getShipperCompanies(){
    	List<Carrier> shippers = new ArrayList<Carrier>();
    	List<ClaimWithPartBeans> claimWithParts = this.getClaimWithPartBeans();
    	if(!CollectionUtils.isEmpty(claimWithParts) && claimWithParts.get(0) != null){
    		Location location = claimWithParts.get(0).getPartReplacedBeans().get(0).getPartReturnTasks().get(0).getPartReturn().getReturnLocation();
            if(claimWithParts.get(0).getPartReplacedBeans().get(0).getPartReturnTasks().get(0).getPart().isReturnDirectlyToSupplier()){
                //fetch the details from the contract, need the contract id
                shippers.addAll(carrierRepository.findAllCarriers());
            }else{
                Warehouse warehouse = warehouseService.findByWarehouseCode(location.getCode());
                if(null != warehouse && !CollectionUtils.isEmpty(warehouse.getWarehouseShippers())){
                    for(WarehouseShippers warehouseShipper : warehouse.getWarehouseShippers()){
                        shippers.add(warehouseShipper.getForCarrier());
                    }
                }
            }
    	}
    	return shippers;
    }
    
    public String removeParts() {
        List<PartTaskBean> partTaskBeans = getSelectedPartTaskBeans();
        List<PartReturn> partsList = getPartReturnsFromPartTaskBeans(partTaskBeans);
        Shipment shipment = partsList.get(0).getShipment();
        List<String> parts = new ArrayList<String>();
        partReturnProcessingService.removePartsFromItsShipment(getPartReturnsFromPartTaskBeans(partTaskBeans),
                getTasksFromPartTaskBeans(partTaskBeans), transitionTaken);
        for(OEMPartReplacedBean partReplacedBean : getPartReplacedBeans()){
        	if(partReplacedBean.isSelected()){
        		OEMPartReplaced part = partReplacedBean.partReturnTasks.get(0).getPart();
        		part.setPartAction1(new PartReturnAction(PartReturnStatus.PART_TO_BE_SHIPPED.getStatus()
        				,(partReplacedBean.getCountOfToBeShipped())));
        			part.setPartAction2(new PartReturnAction(PartReturnStatus.CANNOT_BE_SHIPPED.getStatus()
            				,(partReplacedBean.getCountOfCannotBeShipped())));
                part.setComments(comments);
                updatePartStatus(part);
                getPartReplacedService().updateOEMPartReplaced(part);
                parts.add(part.getItemReference().getReferredItem().getNumber());
            }
        }
        addActionMessage("label.part.shipmentRemoved", parts);
        // return resultingView();
        return SUCCESS;
    }

    // FIX ME :- Need partReturnProcessingService for this as well ?
    public String shipParts() {
    	
        List<PartTaskBean> partTaskBeans = getSelectedPartTaskBeans();
        List<PartReturn> parts = getPartReturnsFromPartTaskBeans(partTaskBeans);
        Shipment shipment = parts.get(0).getShipment();
        if(!isShipmentThroughCEVA() || WorkflowConstants.SHIPMENT_GENERATED_FOR_DEALER.equals(getTaskName()) ){
            if(!WorkflowConstants.SHIPMENT_GENERATED_FOR_DEALER.equals(getTaskName())){
             Carrier carrier = carrierRepository.findCarrierById(carrierId);
             shipment.setCarrier(carrier);
             shipment.setTrackingId(trackingNumber);
            }
            shipment.setShipmentDate(shipmentDate);
        }
        for(ShipmentLoadDimension shipmentld : shipmentLoadDimension){
            shipmentld.setShipment(shipment);
        }
        shipment.setShipmentLoadDimension(shipmentLoadDimension);
        shipment.setComments(comments);
        
        taskViewService.submitAllTaskInstances(getTasksFromPartTaskBeans(partTaskBeans), transitionTaken);
        for(OEMPartReplacedBean partReplacedBean : getPartReplacedBeans()){
        	if(partReplacedBean.isSelected()){
        		OEMPartReplaced part = partReplacedBean.partReturnTasks.get(0).getPart();
                if(WorkflowConstants.SHIPMENT_GENERATED_FOR_DEALER.equals(getTaskName())){
        		     part.setPartAction1(new PartReturnAction(PartReturnStatus.NMHG_TO_DEALER_PART_SHIPPED.getStatus()
        				,partReplacedBean.getCountOfShipmentGenerated()));
                }
                else if(isShipmentThroughCEVA()){
                   part.setPartAction1(new PartReturnAction(PartReturnStatus.WAITING_FOR_CEVA_TRACKING_INFO.getStatus()
        				,partReplacedBean.getCountOfShipmentGenerated()));
                }
                else{
                    part.setPartAction1(new PartReturnAction(PartReturnStatus.PART_SHIPPED.getStatus()
                            ,partReplacedBean.getCountOfShipmentGenerated()));
                }
                part.getPartAction1().setTrackingNumber(trackingNumber);
                part.setPartAction2(null);
                part.setComments(comments);
                part.setShipment(shipment);
                updatePartStatus(part);
                getPartReplacedService().updateOEMPartReplaced(part);
            }
        }

        //if it is through wpra then count will update after ceva update
        if(!isShipmentThroughCEVA()){
            updatePartReceivedCount(getPartReplacedBeans(),shipment);
        }
        
        //create event for shipped parts
//        createEvent(parts);
        //end of call to method to create event
        
        return resultingView();
    }

    public String fetchAllDueAndOverdueParts() {
        WorkListCriteria criteria = createCriteria();
        criteria.setIdentifier(getId());
        claimWithPartBeansList = preparePreviewAndReturn(getPartReturnWorkListItemService()
                .findAllDueAndOverduePartTasksForLocation(criteria), claimWithPartBeansList);
        return SUCCESS;
    }

    public String addParts() {
    	validateForDueParts();
    	
    	if(! hasActionErrors()){
    	    List<PartTaskBean> selectedPartTasks = getSelectedPartTaskBeans(selectedPartReplacedToAdd);
	        List<TaskInstance> taskInstances = new ArrayList<TaskInstance>();
	        List<PartReturn> parts = new ArrayList<PartReturn>();
	        for (PartTaskBean bean : getBeansForShipmentGeneration(selectedPartTasks)) {
	            taskInstances.add(bean.getTask());
	            parts.add(bean.getPartReturn());
	            bean.getPartReturn().setTriggerStatus(PartReturnTaskTriggerStatus.SHIPMENT_GENERATED);
	        }
	        partReturnProcessingService.addPartsToShipment(new Long(getId()), parts, taskInstances, transitionTaken);
            List<PartTaskBean> partTasksToEnd = getBeansToBeEnded(selectedPartTasks);
	        for (PartTaskBean partTaskBean : partTasksToEnd) {
	            getWorkListItemService().endTaskWithTransition(partTaskBean.getTask(), "toEnd");
	            partTaskBean.getPartReturn().setTriggerStatus(PartReturnTaskTriggerStatus.ENDED);
	        }
            for (OEMPartReplacedBean partReplacedBean : selectedPartReplacedToAdd) {
                if (partReplacedBean.isSelected()) {
                    OEMPartReplaced part = partReplacedBean.partReturnTasks.get(0).getPart();
                 	  part.setPartAction1(new PartReturnAction(PartReturnStatus.SHIPMENT_GENERATED.getStatus()
                                  , partReplacedBean.getCountOfShip()));
                    part.getPartAction1().setShipmentId(getId());
                    part.setPartAction2(null);
                    part.setComments(comments);
                    updatePartStatus(part);
                    getPartReplacedService().updateOEMPartReplaced(part);
                }
            }
        }
	        selectedPartReplacedToAdd = new ArrayList<OEMPartReplacedBean>();
	        claimWithPartBeansList = new ArrayList<ClaimWithPartBeans>();
	        return SUCCESS;
    }

    private void validateForDueParts() {
    	if (!hasActionErrors()) {
            for (OEMPartReplacedBean partReplacedBean : selectedPartReplacedToAdd) {
                if (partReplacedBean.isSelected()) {
                    if (partReplacedBean.getCannotShip() == 0 && partReplacedBean.getShip() == 0) {
                        addActionError("error.partReturnConfiguration.noPartSelected");
                    } else if (partReplacedBean.getCannotShip() + partReplacedBean.getShip() > partReplacedBean
                            .getToBeShipped()) {
                        addActionError("error.partReturnConfiguration.excessParts");
                    }
                }
            }
        }
		
	}

   
	public List<OEMPartReplacedBean> getSelectedPartReplacedToAdd() {
			return selectedPartReplacedToAdd;
	}
    
    public List<OEMPartReplacedBean> getSelectedPartReplacedBeansAfterSetting(){
    	for (OEMPartReplacedBean partReplacedBean : this.selectedPartReplacedToAdd) {
    		partReplacedBean.setSelected(false);
		   for(PartTaskBean partTaskBean : partReplacedBean.getPartReturnTasks()){
			   if(partTaskBean.isSelected())
			   {   partReplacedBean.setSelected(true);
				   break;
				   }
			   }
    }
    	return this.selectedPartReplacedToAdd;
    }
    
    
    public void setSelectedPartReplacedToAdd(List<OEMPartReplacedBean> selectedPartReplacedToAdd) {
        this.selectedPartReplacedToAdd = selectedPartReplacedToAdd;
    }

    private void updatePartReceivedCount(List<OEMPartReplacedBean> oemPartReplacedBeans, Shipment shipment) {
        if (ON_PART_SHIPPED.equalsIgnoreCase(
                getConfigParamService().getStringValue(ConfigName.PART_RETURN_STATUS_TO_BE_CONSIDERED_FOR_PRC_MAX_QTY.getName()))) {
            for (OEMPartReplacedBean oemPartReplacedBean : oemPartReplacedBeans) {
                if (oemPartReplacedBean.getOemPartReplaced() != null
                        && oemPartReplacedBean.getOemPartReplaced().getItemReference() != null) {
                    PartReturnConfiguration partReturnConfiguration = oemPartReplacedBean.getOemPartReplaced().getPartReturnConfiguration();
                    if (partReturnConfiguration!=null && 
                            oemPartReplacedBean.getOemPartReplaced().getPartReturns() != null
                            && !oemPartReplacedBean.getOemPartReplaced().getPartReturns().isEmpty()
                            && partReturnConfiguration.getMaxQuantity() != null) {
                    	int quantityReceived = partReturnConfiguration.getQuantityReceived();
                    	for(PartTaskBean partTaskBean : oemPartReplacedBean.getPartReturnTasks()) {
                    		if(partTaskBean.isSelected()) 
                    			quantityReceived ++;
                    	}
                        partReturnConfiguration.setQuantityReceived(quantityReceived);
                        getPartReturnService().updatePartReturnConfiguration(partReturnConfiguration);
                    }                    
                }
            }
        }
    }

    
    /**
     * This is the list of part return from which we are going to create an event.  
     * 
     * @param partReturnList
     */
    private void createEvent(List<PartReturn> partReturnList)
    {
    	HashMap<String, Integer> partsShipped = new HashMap<String, Integer>();
    	PartReturn currentPartReturn = null;
    	String currentItemNumber;
    	Set<String> keySet=null;
    	StringBuffer finalPartNumberString; 
    	String finalPartNumberValue;
    	HashMap<String,Object> eventHashMap = new HashMap<String, Object>();
    	Long partReturnId = null;
    	if(partReturnList != null && partReturnList.size() > 0)
    	{
    		for(Iterator<PartReturn> partReturnIte = partReturnList.iterator(); partReturnIte.hasNext();)
    		{
    			currentPartReturn = partReturnIte.next();
    			if(currentPartReturn != null && currentPartReturn.getOemPartReplaced() != null && currentPartReturn.getOemPartReplaced().getItemReference() != null && currentPartReturn.getOemPartReplaced().getItemReference().getReferredItem() != null)
    			{
    				if(partReturnId == null)
    				{
    					partReturnId = currentPartReturn.getId();
    				}
    				currentItemNumber = currentPartReturn.getOemPartReplaced().getItemReference().getReferredItem().getNumber();
    				if(partsShipped.containsKey(currentItemNumber))
    				{
    					//since this part already exist we will merely update quantity
    					partsShipped.put(currentItemNumber, new Integer(partsShipped.get(currentItemNumber) + 1));
    				}
    				else
    				{
    					//since part number doesn't exist we will make an entry with quantity as one.
    					partsShipped.put(currentItemNumber, new Integer(1));
    				}
    			}
    		}
    		
    		//now that we are done with updating part numbers and quantities lets just create a string out of it
        	keySet = partsShipped.keySet();
        	finalPartNumberString = new StringBuffer();
        	for(Iterator<String> ite=keySet.iterator(); ite.hasNext();)
        	{
        		currentItemNumber = ite.next();
        		finalPartNumberString.append(currentItemNumber);
        		finalPartNumberString.append(" :: ");
        		finalPartNumberString.append(partsShipped.get(currentItemNumber));
        		finalPartNumberString.append(", ");
        	}
        	
        	//remove the last comma from the string buffer
        	finalPartNumberValue = finalPartNumberString.substring(0, (finalPartNumberString.length()-2));
        	
        	//set the values in hashMap
        	eventHashMap.put("claimId",claim.getId().toString());
        	eventHashMap.put("partNumberString",finalPartNumberValue);
        	eventHashMap.put("taskInstanceId", partReturnId.toString());
        	eventHashMap.put("subject", "Claim - " + claim.getClaimNumber() + " needs attention");
        	//create the event
        	getEventService().createEvent("partReturn", EventState.PART_RETURN_SHIPPED, eventHashMap);
    	}   	
    }
    @Override
    public Map<ShipmentStatus,String> getShipmentStatusList() {
    	this.shipmentStatus=new HashMap<ShipmentStatus,String>();
        this.shipmentStatus.put(ShipmentStatus.GENERATE_SHIPMENT,ShipmentStatus.GENERATE_SHIPMENT.getStatus());
        return this.shipmentStatus;
   }
    
	 public void setCarrierRepository(CarrierRepository carrierRepository) {
	        this.carrierRepository = carrierRepository;
	    }

	    public void setCarrierId(Long carrierId) {
	        this.carrierId = carrierId;
	    }

	    public void setTrackingNumber(String trackingNumber) {
	        this.trackingNumber = trackingNumber;
	    }

	    public void setTaskViewService(TaskViewService taskViewService) {
	        this.taskViewService = taskViewService;
	    }

	    public Long getCarrierId() {
	        return carrierId;
	    }

	    public String getTrackingNumber() {
	        return trackingNumber;
	    }

	    public void setComments(String remarks) {
	        this.comments = remarks;
	    }

	    public boolean getIsShipmentGeneratedTask() {
	        if (SHIPMENT_GENERATED_TASK_NAME.equals(getTaskName()))
	            return true;
	        else
	            return false;
	    }

	    // getters required for paramsprepareparams
	    public String getComments() {
	        return comments;
	    }

	    public void setPartReturnProcessingService(PartReturnProcessingService partReturnProcessingService) {
	        this.partReturnProcessingService = partReturnProcessingService;
	    }

	    public Date getShipmentDate() {
	        return shipmentDate;
	    }

	    public void setShipmentDate(Date shipmentDate) {
	        this.shipmentDate = shipmentDate;
	    }    	    

	    public void setHour(Integer hour) {
			this.hour = hour;
		}

		public void setMinute(Integer minute) {
			this.minute = minute;
		}

		public Integer getHour() {
			return hour;
		}

		public Integer getMinute() {
			return minute;
		}
		
		// This is only for the parts receipt preview. Need a better way ?
	    public Shipment getShipmentFromPartBeans() {
	        return getClaimWithPartBeans().get(0).getPartReplacedBeans().get(0).getPartTaskBean().getPartReturn()
	                .getShipment();
	    }

	    public void setServletRequest(HttpServletRequest request) {
	        this.request = request;
	    }

	    public List<ClaimWithPartBeans> getClaimWithPartBeansList() {
	        return claimWithPartBeansList;
	    }

	    public void setClaimWithPartBeansList(List<ClaimWithPartBeans> claimWithPartBeansList) {
	        this.claimWithPartBeansList = claimWithPartBeansList;
	    }

		public WarehouseService getWarehouseService() {
			return warehouseService;
		}

		public void setWarehouseService(WarehouseService warehouseService) {
			this.warehouseService = warehouseService;
		}

        /*public String getLoadType() {
            return loadType;
        }

        public void setLoadType(String loadType) {
            this.loadType = loadType;
        }*/

        public List<String> getLoadTypes(){
            List<String> loadTypes = new ArrayList<String>();
            for(String loadType : LoadType.getAllLoadType()){
                loadTypes.add(getText(loadType));
            }
            return loadTypes;

        }

        public CalendarDate getShipmentCalenderDate() {
            return shipmentCalenderDate;
        }

        public void setShipmentCalenderDate(CalendarDate shipmentCalenderDate) {
            this.shipmentCalenderDate = shipmentCalenderDate;
        }
        
    	public boolean isDealer() {
    		return isDealer;
    	}

    	public void setDealer(boolean isDealer) {
    		this.isDealer = isDealer;
    	}
     @Override
     public boolean getIsSwitchViewEnabled() {
        return (getSwitchButtonActionName() != null && getConfigParamService().getBooleanValue(ConfigName.ENABLE_PART_RETURN_THROUGH_WPRA.getName()));
     }

    public ShipmentRepository getShipmentRepository() {
        return shipmentRepository;
    }

    public void setShipmentRepository(ShipmentRepository shipmentRepository) {
        this.shipmentRepository = shipmentRepository;
    }

    public boolean isShipmentThroughCEVA() {
        return getConfigParamService().getBooleanValue(ConfigName.ENABLE_PART_RETURN_THROUGH_WPRA.getName());
    }

    public List<ShipmentLoadDimension> getShipmentLoadDimension() {
        return shipmentLoadDimension;
    }

    public void setShipmentLoadDimension(List<ShipmentLoadDimension> shipmentLoadDimension) {
        this.shipmentLoadDimension = shipmentLoadDimension;
    }

    //Method to be called for dealer claimed parts receipt
    public String claimedPartReceipt(){

        List<OEMPartReplaced> receivedParts = new ArrayList<OEMPartReplaced>();
        for(OEMPartReplacedBean partReplacedBean : getPartReplacedBeans()){
            if(partReplacedBean.isSelected()){
                OEMPartReplaced part = partReplacedBean.getOemPartReplaced();
                receivedParts.add(part);
            }
        }
        List<TaskInstance> taskinstances = partReturnProcessingService.findClaimedPartReceiptTasks(receivedParts);
        List<TaskInstance> selectedTasksIfTransitionAvailable = new ArrayList<TaskInstance>();
        for(TaskInstance task : taskinstances){
            List transitions = task.getAvailableTransitions();
            if(checkIfTransitionIsAvailable(transitions, "toEnd")){
                selectedTasksIfTransitionAvailable.add(task);
            }
        }
        if(!selectedTasksIfTransitionAvailable.isEmpty()){
            partReturnProcessingService.endClaimedPartReceiptAndDealerPartsShipped(selectedTasksIfTransitionAvailable);
        }

        if(taskinstances.size() != selectedTasksIfTransitionAvailable.size()){
            addActionError(getText("error.transition.not.available"));
            return resultingView();
        }

        for(OEMPartReplacedBean partReplacedBean : getPartReplacedBeans()){
            if(partReplacedBean.isSelected()){
                OEMPartReplaced part = partReplacedBean.getOemPartReplaced();

                part.setPartAction1(new PartReturnAction(PartReturnStatus.PARTS_COLLECTED_BY_DEALER.getStatus()
                        ,partReplacedBean.getShipped()));
                part.setComments(comments);
                updatePartStatus(part);
                getPartReplacedService().updateOEMPartReplaced(part);

            }
        }
        return resultingView();
    }

    private boolean checkIfTransitionIsAvailable(List transitions, String transition) {
        for (Object t : transitions) {
            if (transition.equals(((Transition) t).getName())) {
                return true;
            }
        }
        return false;
    }
    
    public String shippingCommentsInClaim(Claim claim){
    	String shippingCommentsInClaim = claim.getPartReturnCommentsToDealer();
    	if(StringUtils.hasText(shippingCommentsInClaim)){
    		return shippingCommentsInClaim;
    	}
    	for(ClaimAudit audit : claim.getClaimAudits()){
    		if(StringUtils.hasText(audit.getPartReturnCommentsToDealer())){
    			shippingCommentsInClaim = audit.getPartReturnCommentsToDealer();
    		}
    	}
    	return shippingCommentsInClaim;
    }
    
    public String shippingCommentsInRecClaim(Claim claim, OEMPartReplaced replacedPart){
    	String shippingCommentsInRecClaim = null;
    	RecoveryClaim matchingRecClaim = getMatchingRecClaim(claim, replacedPart);	    	    	    	    	        	    
    	if(matchingRecClaim != null){
    		for(RecoveryClaimAudit recClaimAudit : matchingRecClaim.getRecoveryClaimAudits()){			
    			for(Role role:recClaimAudit.getCreatedBy().getRoles()){
    				if(role.getName().equals(Role.SUPPLIER)){
        				supplierComments = recClaimAudit.getExternalComments();
        			} 
    			}
    			   			
    		}
    		shippingCommentsInRecClaim = matchingRecClaim.getPartReturnCommentsToDealer();    		
    		if(StringUtils.hasText(shippingCommentsInRecClaim)){
        		return shippingCommentsInRecClaim;
        	}
    		for(RecoveryClaimAudit recClaimAudit : matchingRecClaim.getRecoveryClaimAudits()){
    			if(StringUtils.hasText(recClaimAudit.getPartReturnCommentsToDealer())){
    				shippingCommentsInRecClaim = recClaimAudit.getPartReturnCommentsToDealer();
    			}    			    			  		
    		}
    	}
    	return shippingCommentsInRecClaim;
    }
    
    private RecoveryClaim getMatchingRecClaim(Claim claim, OEMPartReplaced replacedPart){
    	if(claim.getRecoveryClaims() == null || claim.getRecoveryClaims().isEmpty()){
    		return null;
    	}
    	for(RecoveryClaim recClaim : claim.getRecoveryClaims()){
    		for(RecoverablePart recoverablePart : recClaim.getRecoveryClaimInfo().getRecoverableParts()){
    			if(recoverablePart.getOemPart().equals(replacedPart)){
    				return recClaim;
    			}
    		}
    	}
    	return null;
    }
}

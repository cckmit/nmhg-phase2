package tavant.twms.web.supplier;

import java.util.*;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.jbpm.taskmgmt.exe.TaskInstance;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.claim.OEMPartReplaced;
import tavant.twms.domain.claim.PartReplacedService;
import tavant.twms.domain.claim.RecoveryClaim;
import tavant.twms.domain.orgmodel.Address;
import tavant.twms.domain.orgmodel.User;
import tavant.twms.domain.orgmodel.UserRepository;
import tavant.twms.domain.partreturn.Carrier;
import tavant.twms.domain.partreturn.CarrierRepository;
import tavant.twms.domain.partreturn.Location;
import tavant.twms.domain.partreturn.Shipment;
import tavant.twms.domain.partreturn.ShipmentRepository;
import tavant.twms.domain.supplier.SupplierPartReturn;
import tavant.twms.domain.supplier.recovery.RecoverablePart;
import tavant.twms.domain.supplier.shipment.ContractShipmentService;
import tavant.twms.infra.DomainRepository;
import tavant.twms.infra.PageResult;
import tavant.twms.infra.PageSpecification;
import tavant.twms.jbpm.WorkflowConstants;
import tavant.twms.worklist.InboxItemList;
import tavant.twms.worklist.WorkListCriteria;
import tavant.twms.worklist.WorkListItemService;
import tavant.twms.worklist.supplier.SupplierRecoveryWorkListDao;

import com.domainlanguage.time.CalendarDate;

@SuppressWarnings("serial")
public class PartShipperUpdateTagAction extends AbstractSupplierActionSupport {

    private static Logger logger = Logger.getLogger(PartShipperUpdateTagAction.class);

    private WorkListItemService workListItemService;

    private PartReplacedService partReplacedService;

    private ContractShipmentService contractShipmentService;

    //private SupplierRecoveryWorkListDao supplierRecoveryWorkListDao;

    private DomainRepository domainRepository;

    private CarrierRepository carrierRepository;

    private ShipmentRepository shipmentRepository;
    
    private List<SupplierPartReturn> supplierPartReturnBeans = new ArrayList<SupplierPartReturn>();

    private Address address;
    private User user;

    //private String shipmentIdString;

    private List<Shipment> shipments = new ArrayList<Shipment>();
    
    public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	private UserRepository userRepository;

    public UserRepository getUserRepository() {
		return userRepository;
	}

	public void setUserRepository(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	private String transitionTaken;
    
    private Map<RecoveryClaim, List<SupplierPartReturn>> claimsInShipment = new HashMap<RecoveryClaim, List<SupplierPartReturn>>();

    private List<SupplierPartReturn> additionalSupplierPartReturns = new ArrayList<SupplierPartReturn>();

    // FIX ME - a seperate field for Shipment date since the shipment date is
    // set to the shipment during
    // the generate shipment phase. Probably need to change that.
    //private CalendarDate shipmentDate;

   /* private List<CalendarDate> shipmentsDate;

    public List<CalendarDate> getShipmentsDate() {
        return shipmentsDate;
    }

    public void setShipmentsDate(List<CalendarDate> shipmentsDate) {
        this.shipmentsDate = shipmentsDate;
    }*/

    //private Date shipmentDateinDateFormat;

  /*  public Date getShipmentDateinDateFormat() {
        return shipmentDateinDateFormat;
    }

    public void setShipmentDateinDateFormat(Date shipmentDateinDateFormat) {
        this.shipmentDateinDateFormat = shipmentDateinDateFormat;
    }*/

    private Shipment shipment;

    private List<TaskInstance> taskInstances;

    @Override
    protected InboxItemList getInboxItemList(WorkListCriteria criteria) {
        if(getInboxViewId() != null && getInboxViewId().equals(DEFAULT_VIEW_ID)) {
            return this.workListService.getSupplierShipmentBasedView(criteria);
        }
        else {
            return workListService.getPartShipperRecoveryClaimView(criteria);
        }
    }

    public String preview() {
        if(getInboxViewId() != null && getInboxViewId().equals(DEFAULT_VIEW_ID))  {
            fetchShipmentView();
            return SUCCESS;
        }else{
            fetchRecoveryClaimView();
            return "recoveryClaimView";
        }

    }
    
    protected String getAlias() {
        if(getInboxViewId() != null && getInboxViewId().equals(DEFAULT_VIEW_ID))  {
            return "shipment";
        }else{
            return "recoveryClaim";
        }
	}

    public String submit() {
        Assert.hasText(this.transitionTaken, "Transition taken should not be empty");
        // Update the shipment
       // this.shipment.setShipmentDate((this.shipmentDateinDateFormat == null) ? new Date() : this.shipmentDateinDateFormat);
        //update supplier part returns
        List<SupplierPartReturn> newSupplierReturns = new ArrayList<SupplierPartReturn>();
        List<TaskInstance> allInstances = allOpenInstancesForTask(this.getTaskInstances(), WorkflowConstants.SUPPLIER_SHIPMENMT_GENERATED);

        for (TaskInstance taskInstance : allInstances) {
            SupplierPartReturn supplierPartReturn = (SupplierPartReturn) taskInstance.getVariable("supplierPartReturn");
            supplierPartReturn.setReturnLocation(shipment.getDestination());
            newSupplierReturns.add(supplierPartReturn);

        }
        this.shipment.setSupplierPartReturns(newSupplierReturns);
        if(!StringUtils.hasText(shipment.getContactPersonName())){
            if(shipment.getDestination()!=null &&
          	 StringUtils.hasText(shipment.getDestination().getAddress().getContactPersonName())){
              shipment.setContactPersonName(shipment.getDestination().getAddress().getContactPersonName());	
          	}
            else{
          	  shipment.setContactPersonName(""); 
            }
          }
        this.shipmentRepository.updateShipment(this.shipment);
        if (logger.isDebugEnabled()) {
            logger.debug("Updated " + this.shipment);
        }
        // Change the transition of all the parts
        this.workListItemService.endAllTasksWithTransition(allInstances, this.transitionTaken);
        addActionMessage("message.itemStatus.updated");
        return SUCCESS;
    }

    public String submitReclaimView() {
        Assert.hasText(this.transitionTaken, "Transition taken should not be empty");
        // Update the shipment
        for(Shipment ship : this.shipments){
            List<SupplierPartReturn> newSupplierReturns = new ArrayList<SupplierPartReturn>();
            List<TaskInstance> allInstances = allOpenInstancesForTask(this.getTaskInstances(), WorkflowConstants.SUPPLIER_SHIPMENMT_GENERATED);
            for (TaskInstance taskInstance : allInstances) {
                SupplierPartReturn supplierPartReturn = (SupplierPartReturn) taskInstance.getVariable("supplierPartReturn");
                supplierPartReturn.setReturnLocation(ship.getDestination());
                newSupplierReturns.add(supplierPartReturn);

            }
            ship.setSupplierPartReturns(newSupplierReturns);
            if(!StringUtils.hasText(ship.getContactPersonName())){
              if(ship.getDestination()!=null &&
            	 StringUtils.hasText(ship.getDestination().getAddress().getContactPersonName())){
            	  ship.setContactPersonName(ship.getDestination().getAddress().getContactPersonName());	
            	}
              else{
            	  ship.setContactPersonName(""); 
              }
            }
            this.shipmentRepository.updateShipment(ship);
            if (logger.isDebugEnabled()) {
                logger.debug("Updated " + this.shipment);
            }
            // Change the transition of all the parts
            this.workListItemService.endAllTasksWithTransition(allInstances, this.transitionTaken);

        }
        addActionMessage("message.itemStatus.updated");
        return SUCCESS;
    }

    
    
   /* private void updateSupplierPartReturnWithRGA(RecoveryClaim recoveryClaim,List<SupplierPartReturn> supplierPartReturns){
		List<RecoverablePart> recoverableParts = recoveryClaim.getRecoveryClaimInfo().getRecoverableParts();
		for (int i = 0; i < recoverableParts.size(); i++) {
			if (recoverableParts.get(i).isSupplierReturnNeeded()) {
			  for(PartNumberAndRmaBean partNumberAndRmaBean : rgaWithPartNumberList){
			  if(partNumberAndRmaBean.getPartNumber().equals(recoverableParts.get(i).getSupplierItem().getNumber())
					 &&  StringUtils.hasText(partNumberAndRmaBean.getRgaNumber())){
			   this.getContractService().updateSupplierPartReturn(recoverableParts.get(i),partNumberAndRmaBean.getRgaNumber());
				 }
			  }
			}
		  }
		}*/

    public String shipmentTag() {
        // the interceptors would have already populated the
        // domain
    	user = getLoggedInUser();
//    	address = userRepository.fetchLoggedInUserAddress(user);
        List<TaskInstance> taskInstances = null;
        if(shipment != null){
    	    taskInstances = filterTaskInstancesBasedOnPart(getSupplierRecoveryWorkListDao().getTasksForSupplierPartReturns(this.getShipment().getSupplierPartReturns(),this.getTaskName()));
           // this.shipments.add(shipment);
        }/*else{
            //fet the taskinstances for all the shipments
            if(getShipmentIdString() != null && getShipmentIdString().contains(":")){
                String[]  ids = getShipmentIdString().split(":");
                List<Long> shipmentList = new ArrayList<Long>();
                for(String id: ids){
                    shipmentList.add(Long.valueOf(id));
                }
                shipments = getShipmentRepository().findByIds("id", shipmentList);
                List<SupplierPartReturn> supplierPartReturns = new ArrayList<SupplierPartReturn>() ;
                for(Shipment shipment : shipments){
                    supplierPartReturns.addAll(shipment.getSupplierPartReturns());
                }
                taskInstances = getSupplierRecoveryWorkListDao().getTasksForSupplierPartReturns(supplierPartReturns,this.getTaskName());
            }else{
                Shipment ship = getShipmentRepository().findById(Long.valueOf(getShipmentIdString()));
                this.shipments.add(ship);
                taskInstances = getSupplierRecoveryWorkListDao().getTasksForSupplierPartReturns(ship.getSupplierPartReturns(), this.getTaskName());
            }

        }*/
    	for (TaskInstance taskInstance : taskInstances) {
			RecoveryClaim recoveryClaim = (RecoveryClaim) taskInstance.getVariable("recoveryClaim");
			if (claimsInShipment.containsKey(recoveryClaim)) {
				claimsInShipment.get(recoveryClaim).add((SupplierPartReturn) taskInstance.getVariable("supplierPartReturn"));
			} else {
				claimsInShipment.put(recoveryClaim, new ArrayList<SupplierPartReturn>());
				claimsInShipment.get(recoveryClaim).add((SupplierPartReturn) taskInstance.getVariable("supplierPartReturn"));
			}
		}
        return SUCCESS;
    }

	public String addNewPart() {
		// Fetch the parts to add
		Location locationId = null;
		String actorId = getLoggedInUser().getName();
		try {
			locationId = getShipment().getDestination();
		} catch (Exception e) {
			if (logger.isDebugEnabled()) {
				logger.debug("Error in getting location for shipment " + getShipment() + "] ");
			}
		}
		this.taskInstances = filterTaskInstancesBasedOnPart(getSupplierRecoveryWorkListDao().getPreviewPaneForPartsToBeAdded(locationId, getTaskName(), actorId));
		return SUCCESS;
	}

    public String addNewPartsToShipment() {
        // Add the new parts to the shipment
        addPartsToShipment(this.additionalSupplierPartReturns);

        // Now perform the transition
        performTransitions(this.additionalSupplierPartReturns,WorkflowConstants.SUPPLIER_PARTS_CLAIMED);
        addActionMessage("message.supplier.partsAdded");
        return SUCCESS;
    }

    private void performTransitions(List<TaskInstance> instanceList) {
        for (TaskInstance taskInstance : instanceList){
        	this.workListItemService.endTaskWithTransition(taskInstance, this.transitionTaken);
        }
    }

    private void performTransitions(List<SupplierPartReturn> additionalSupplierPartReturns,String taskName) {
        for (TaskInstance taskInstance : getSupplierRecoveryWorkListDao().getTasksForSupplierPartReturns(additionalSupplierPartReturns,
        		taskName)) {
            this.workListItemService.endTaskWithTransition(taskInstance, this.transitionTaken);
            if (logger.isDebugEnabled()) {
                logger.debug("Ending the task [" + taskInstance + "] with transition [" + this.transitionTaken + "]");
            }
        }
    }


	private void addPartsToShipment(List<SupplierPartReturn> additionalSupplierPartReturns) {
		try {
			this.contractShipmentService.addPartsToSupplierShipment(this.shipment, additionalSupplierPartReturns);
		} catch (Exception e) {
			addActionError("Error in adding parts to  shipment");
		}
		if (logger.isDebugEnabled()) {
			logger.debug("Adding Parts" + this.additionalSupplierPartReturns + "to shipment " + this.shipment);
		}
	}

    public String removePart() {
        // Remove parts from shipment
    	List<SupplierPartReturn> list = new ArrayList<SupplierPartReturn>();
        List<TaskInstance> allSelectedInstances = allOpenInstancesForTask(this.getTaskInstances(),WorkflowConstants.SUPPLIER_SHIPMENMT_GENERATED);
    	for(TaskInstance taskInstance:allSelectedInstances){
    		list.add((SupplierPartReturn)taskInstance.getVariable("supplierPartReturn"));
    	}
        this.contractShipmentService.removeSupplierPartReturnFromSupplierShipment(list);

        // Reverse the transition
        performTransitions(allSelectedInstances);
        addActionMessage("message.supplier.partsRemoved");
        return SUCCESS;
    }

    /**
     * This method is directly called in the jsp to get the carrier's list.
     * 
     * @return
     */
    public List<Carrier> getCarriers() {
    	List<Carrier> carriers=new ArrayList<Carrier>();
    	Carrier carrier=null;
    	if(shipment != null && !shipment.getSupplierPartReturns().isEmpty()){
    		carrier =shipment.getSupplierPartReturns().get(0).getCarrier();
    	}
        List<Carrier> tempCarriers=this.carrierRepository.findAllCarriers();
        if(tempCarriers!= null && carrier != null){
        	tempCarriers.remove(carrier);
        	carriers.add(carrier);
        }
        carriers.addAll(tempCarriers);
        return carriers;
    }

    /**
     * This method is directly called in the jsp to get the claim for a
     * OEMPartReplaced
     * 
     * @param
     * @return
     */
    public Claim getClaimForOEMPartReplaced(OEMPartReplaced oemPartReplaced) {
        return this.partReplacedService.getClaimForOEMPartReplaced(oemPartReplaced);
    }

    private void fetchShipmentView() {
        Assert.hasText(getId(), "Id should not be empty for fetch");
        // Get the shipment from the repository
        this.shipment = (Shipment) this.domainRepository.load(Shipment.class, new Long(getId()));
        this.shipments.add(this.shipment);
    }

    private void fetchRecoveryClaimView() {
        Assert.hasText(getId(), "Id should not be empty for fetch");
        // Get the shipment from the repository
        this.shipments = getWorkListService().getAllShipmentsForRecoveryClaim(Long.valueOf(getId()),getTaskName());
        //set the shipment id string for shipment tag
        //commenting out since multiple pop up is opening for multiple shipments
        /*StringBuffer tempString = new StringBuffer();
        for(int i=0; i<this.shipments.size() ;i++){
            tempString.append(this.shipments.get(i).getId().toString());
            if(i < this.shipments.size()-1){
                tempString.append(":");
            }
        }
        shipmentIdString = tempString.toString();*/
    }
    
    public List<TaskInstance> getTaskInstancesForShipment(){
        List<TaskInstance> tempListWithUniqueRecoverablePart = new ArrayList<TaskInstance>();
        if(getInboxViewId() != null && getInboxViewId().equals(DEFAULT_VIEW_ID)) {
            tempListWithUniqueRecoverablePart = getSupplierRecoveryWorkListDao().getTasksForSupplierPartReturns(this.shipment.getSupplierPartReturns() , this.getTaskName());
        }else{
            tempListWithUniqueRecoverablePart =  getWorkListService().getAllTaskInstancesForRecoveryClaim(Long.valueOf(getId()), getTaskName());
        }

    /*    Map<Long, TaskInstance> tempMap = new HashMap<Long, TaskInstance>();
        for(TaskInstance instance : tempListWithUniqueRecoverablePart){
            SupplierPartReturn supplierPartReturn = (SupplierPartReturn) instance.getVariable("supplierPartReturn");
            if(supplierPartReturn != null && tempMap.get(supplierPartReturn.getRecoverablePart().getId()) == null){
                tempMap.put(supplierPartReturn.getRecoverablePart().getId(), instance);
            }
        }*/
        return this.taskInstances = filterTaskInstancesBasedOnPart(tempListWithUniqueRecoverablePart);
    }

    public WorkListItemService getWorkListItemService() {
        return this.workListItemService;
    }

    public void setWorkListItemService(WorkListItemService workListItemService) {
        this.workListItemService = workListItemService;
    }

    public DomainRepository getDomainRepository() {
        return this.domainRepository;
    }

    public void setDomainRepository(DomainRepository domainRepository) {
        this.domainRepository = domainRepository;
    }

    public String getTransitionTaken() {
        return this.transitionTaken;
    }

    public void setTransitionTaken(String transitionTaken) {
        this.transitionTaken = transitionTaken;
    }

    public Shipment getShipment() {
        return this.shipment;
    }

    public void setShipment(Shipment shipment) {
        this.shipment = shipment;
    }

    public CarrierRepository getCarrierRepository() {
        return this.carrierRepository;
    }

    public void setCarrierRepository(CarrierRepository carrierRepository) {
        this.carrierRepository = carrierRepository;
    }

    public ShipmentRepository getShipmentRepository() {
        return this.shipmentRepository;
    }

    public void setShipmentRepository(ShipmentRepository shipmentRepository) {
        this.shipmentRepository = shipmentRepository;
    }

    public PartReplacedService getPartReplacedService() {
        return this.partReplacedService;
    }

    @Override
    public void setPartReplacedService(PartReplacedService partReplacedService) {
        this.partReplacedService = partReplacedService;
    }

    public ContractShipmentService getContractShipmentService() {
        return this.contractShipmentService;
    }

    public void setContractShipmentService(ContractShipmentService contractShipmentService) {
        this.contractShipmentService = contractShipmentService;
    }

    public List<TaskInstance> getTaskInstances() {
        return this.taskInstances;
    }

    public void setTaskInstances(List<TaskInstance> taskInstances) {
        this.taskInstances = taskInstances;
    }

   /* public SupplierRecoveryWorkListDao getSupplierRecoveryWorkListDao() {
        return this.supplierRecoveryWorkListDao;
    }

    public void setSupplierRecoveryWorkListDao(SupplierRecoveryWorkListDao supplierRecoveryWorkListDao) {
        this.supplierRecoveryWorkListDao = supplierRecoveryWorkListDao;
    }*/


   /* public CalendarDate getShipmentDate() {
        return shipmentDate;
    }

    public void setShipmentDate(CalendarDate shipmentDate) {
        this.shipmentDate = shipmentDate;
    }*/

    @SuppressWarnings("unchecked")
    @Override
    protected PageResult<?> getPageResult(List inboxItems, PageSpecification pageSpecification, int noOfPages) {
        return new PageResult<OEMPartReplaced>(inboxItems, pageSpecification, noOfPages);
    }


	public List<SupplierPartReturn> getAdditionalSupplierPartReturns() {
		return additionalSupplierPartReturns;
	}

	public void setAdditionalSupplierPartReturns(List<SupplierPartReturn> additionalSupplierPartReturns) {
		this.additionalSupplierPartReturns = additionalSupplierPartReturns;
	}

	public Map<RecoveryClaim, List<SupplierPartReturn>> getClaimsInShipment() {
		return claimsInShipment;
	}

	public void setClaimsInShipment(Map<RecoveryClaim, List<SupplierPartReturn>> claimsInShipment) {
		this.claimsInShipment = claimsInShipment;
	}

   /* public void prepare() {
        if (shipmentDate != null) {
            shipmentDateinDateFormat = new Date(shipmentDate.breachEncapsulationOf_year()-1900,shipmentDate.breachEncapsulationOf_month()-1,shipmentDate.breachEncapsulationOf_day());
        }
    }*/

    @Override
    public void validate() {
        super.validate();
        Date date = new Date();
        Date currentDate = new Date(date.getDate(),date.getMonth(),date.getYear());
        if (this.shipment != null && this.shipment.getShipmentDate()!=null &&
        		this.shipment.getShipmentDate().before(currentDate)) {
                addFieldError("shipmentDate", "prc.shipment.date.before.today");
        }
        else if(this.shipments !=null){
            for(Shipment ship : this.shipments){
                if(ship.getShipmentDate() != null && ship.getShipmentDate().before(currentDate)){
                    addActionError("prc.shipment.date.before.today");
                }
                if(!StringUtils.hasText(ship.getTrackingId())){
                   addActionError("error.part.shipper.tracking.number");
                }
                if(ship.getCarrier() == null){
                    addActionError("error.sra.contract.carrier");
                }
            }
        }
    }
    
    public void setSupplierPartReturnBeans(List<SupplierPartReturn> supplierPartReturnBeans) {
        this.supplierPartReturnBeans = supplierPartReturnBeans;
    }

    public List<SupplierPartReturn> getSupplierPartReturnBeans() {
        return supplierPartReturnBeans;
    }

    /*public String getShipmentIdString() {
        return shipmentIdString;
    }

    public void setShipmentIdString(String shipmentIdString) {
        this.shipmentIdString = shipmentIdString;
    }
*/
    public List<Shipment> getShipments() {
        return shipments;
    }

    public void setShipments(List<Shipment> shipments) {
        this.shipments = shipments;
    }
}

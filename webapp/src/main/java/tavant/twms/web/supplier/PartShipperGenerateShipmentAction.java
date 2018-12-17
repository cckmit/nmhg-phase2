package tavant.twms.web.supplier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.jbpm.taskmgmt.exe.TaskInstance;
import org.json.JSONArray;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import tavant.twms.common.CollectionUtils;
import tavant.twms.domain.claim.OEMPartReplaced;
import tavant.twms.domain.claim.RecoveryClaim;
import tavant.twms.domain.orgmodel.Supplier;
import tavant.twms.domain.partreturn.Location;
import tavant.twms.domain.partreturn.Shipment;
import tavant.twms.domain.supplier.SupplierPartReturn;
import tavant.twms.domain.supplier.recovery.RecoverablePart;
import tavant.twms.domain.supplier.shipment.ContractShipmentService;
import tavant.twms.infra.PageResult;
import tavant.twms.infra.PageSpecification;
import tavant.twms.jbpm.WorkflowConstants;
import tavant.twms.worklist.InboxItemList;
import tavant.twms.worklist.WorkListCriteria;
import tavant.twms.worklist.WorkListItemService;
import tavant.twms.worklist.supplier.SupplierRecoveryWorkListDao;

/**
 * @author kiran.sg
 */
@SuppressWarnings("serial")
public class PartShipperGenerateShipmentAction extends AbstractSupplierActionSupport {

	private static Logger logger = Logger
			.getLogger(PartShipperGenerateShipmentAction.class);

	// List of task Instances
	private List<TaskInstance> taskInstances = new ArrayList<TaskInstance>();

	// OEM parts selected for shipment
	private List<OEMPartReplaced> oemPartsReplaced = new ArrayList<OEMPartReplaced>();
	
	//private SupplierRecoveryWorkListDao supplierRecoveryWorkListDao;

	private WorkListItemService workListItemService;

	private String transitionTaken;

	private ContractShipmentService contractShipmentService;

	private Shipment shipment;
	
	private List<String> taskIdsForBulkShipment= new ArrayList<String>();

    private List<SupplierPartReturn> supplierPartReturnBeans = new ArrayList<SupplierPartReturn>();

    private Supplier supplier;

    private Location location;

	@Override
	protected InboxItemList getInboxItemList(WorkListCriteria criteria) {
        if(getInboxViewId() != null && getInboxViewId().equals(DEFAULT_VIEW_ID)){
		    return getSupplierRecoveryWorkListDao().getSupplierLocationListForAssignedShipper(criteria);
        }else{
            return getSupplierRecoveryWorkListDao().getPartShipperRecoveryClaimList(criteria);
        }
	}

    @Override
    protected String getAlias() {
        if(getInboxViewId() != null && getInboxViewId().equals(DEFAULT_VIEW_ID)){
            return null;
        }else{
            return "recoveryClaim";
        }

    }

	public String preview() {
		Assert.hasText(getId(), "Id should not be empty for fetch");
        List<TaskInstance> tempListWithUniqueRecoverablePart = new ArrayList<TaskInstance>();
        if(getInboxViewId() != null && getInboxViewId().equals(DEFAULT_VIEW_ID)){
            tempListWithUniqueRecoverablePart  = getTaskInstancesForSupplier(((RecoveryClaim)workListItemService.findTask(new Long(getId())).getVariable("recoveryClaim")).getId());
        }else{
            //get the task instance
            tempListWithUniqueRecoverablePart = getTaskInstancesForSupplier(Long.valueOf(getId()));
        }
        /*Map<Long, TaskInstance> tempMap = new HashMap<Long, TaskInstance>();
        for(TaskInstance instance : tempListWithUniqueRecoverablePart){
            SupplierPartReturn supplierPartReturn = (SupplierPartReturn) instance.getVariable("supplierPartReturn");
            if(supplierPartReturn != null && tempMap.get(supplierPartReturn.getRecoverablePart().getId()) == null){
                tempMap.put(supplierPartReturn.getRecoverablePart().getId(), instance);
            }
        }*/
        this.taskInstances = filterTaskInstancesBasedOnPart(tempListWithUniqueRecoverablePart);
		return SUCCESS;
	}

	/**
	 * This method is directly called in the jsp (response page after the parts
	 * have been selected to ship)
	 *
	 * @param //shipmentIdString
	 * @return
	 */
	public List<TaskInstance> getTaskInstancesForSupplier(Long id) {
		String actorId = getLoggedInUser().getName();
		return getSupplierRecoveryWorkListDao().getPreviewPaneForSupplierLocation(getTaskName(),actorId, id);
	}

	public String submit() {
		Assert.hasText(getId(), "Id should not be empty for submit");
		Assert.hasText(this.transitionTaken, "Transition taken should not be empty");
		// Generate Shipment
		generateShipmentForEachTask();
		if (shipment!=null) {
			return SUCCESS;
		} else {
			return INPUT;
		}
	}
	
	public String bulkShipmentGenerate() {
		JSONArray details = new JSONArray();
		for (String id : this.taskIdsForBulkShipment) {
			if (StringUtils.hasText(id)) {
				try {
					this.taskInstances.add(workListItemService.findTask(new Long(id)));
				} catch (Exception e) {
					logger.debug("Unable to load transaction with Id [" + id + "] of transition ["
							+ this.transitionTaken + "]");
				}
			}
		}
		if (partReturnsShipToSameLocation()) {
			generateShipmentForEachTask();
			if(this.shipment!=null){
				details.put(Boolean.TRUE);
				String shipmentId = this.shipment.getTransientId() == null ? this.shipment.getId()+"" : this.shipment.getTransientId();
				details.put(shipmentId);
			}else{
				details.put(Boolean.FALSE);
			}
			jsonString = details.toString();
		} else {
			details.put(Boolean.FALSE);
			jsonString = details.toString();
		}
		return SUCCESS;
	}

	private boolean partReturnsShipToSameLocation() {
		Location baseLocation = null;
		try {
			for (TaskInstance task : this.taskInstances) {
				Location currentLocation = ((SupplierPartReturn) task.getVariable("supplierPartReturn")).getReturnLocation();
				if (currentLocation == null) {
					return false;
				}else if(baseLocation==null){
					baseLocation = currentLocation;
				} else if (!baseLocation.equals(currentLocation)) {
					return false;
				}
			}
		} catch (Exception e) {
			logger.debug("Error in reading supplier contract");
			return false;
		}
		return true;
	}
	
	private void generateShipmentForEachTask() {
		List<SupplierPartReturn> supplierPartReturns = new ArrayList<SupplierPartReturn>();
        List<TaskInstance> allInstances = allOpenInstancesForTask(this.getTaskInstances(), WorkflowConstants.SUPPLIER_PARTS_CLAIMED);

		for (TaskInstance taskInstance : allInstances) {
			supplierPartReturns.add((SupplierPartReturn) taskInstance.getVariable("supplierPartReturn"));
		}
        //set the supplier comment back to null since this is a new shipment
        for(SupplierPartReturn supPart : supplierPartReturns){
            supPart.setSupplierComment(null);
        }
		this.shipment = this.contractShipmentService.generateSupplierContractShipment(supplierPartReturns);
		this.workListItemService.endAllTasksWithTransition(allInstances, this.transitionTaken);
		if (this.transitionTaken.equals(WorkflowConstants.UPDATE)) {
			addActionMessage("message.partshipper.partsShipped.response");
		} else {
			String shipmentId = this.shipment.getTransientId() == null ? this.shipment.getId()+"" : this.shipment.getTransientId();
			addActionMessage("message.partshipper.shipmentgenerated.response",new String[] {shipmentId});
		}
	}

	public List<OEMPartReplaced> getOemPartsReplaced() {
		return this.oemPartsReplaced;
	}

	public void setOemPartsReplaced(List<OEMPartReplaced> oemPartsReplaced) {
		this.oemPartsReplaced = oemPartsReplaced;
	}

	public WorkListItemService getWorkListItemService() {
		return this.workListItemService;
	}

	public void setWorkListItemService(WorkListItemService workListItemService) {
		this.workListItemService = workListItemService;
	}

	public String getTransitionTaken() {
		return this.transitionTaken;
	}

	public void setTransitionTaken(String transitionTaken) {
		this.transitionTaken = transitionTaken;
	}

	public List<TaskInstance> getTaskInstances() {
		return this.taskInstances;
	}

	public void setTaskInstances(List<TaskInstance> taskInstances) {
		this.taskInstances = taskInstances;
	}

	/*public SupplierRecoveryWorkListDao getSupplierRecoveryWorkListDao() {
		return this.supplierRecoveryWorkListDao;
	}

	public void setSupplierRecoveryWorkListDao(
			SupplierRecoveryWorkListDao supplierRecoveryWorkListDao) {
		this.supplierRecoveryWorkListDao = supplierRecoveryWorkListDao;
	}*/

	public ContractShipmentService getContractShipmentService() {
		return this.contractShipmentService;
	}

	public void setContractShipmentService(
			ContractShipmentService contractShipmentService) {
		this.contractShipmentService = contractShipmentService;
	}

	public Shipment getShipment() {
		return this.shipment;
	}

	public void setShipment(Shipment shipment) {
		this.shipment = shipment;
	}
	
	

	public List<String> getTaskIdsForBulkShipment() {
		return taskIdsForBulkShipment;
	}


	public void setTaskIdsForBulkShipment(List<String> taskIdsForBulkShipment) {
		this.taskIdsForBulkShipment = taskIdsForBulkShipment;
	}

	@Override
	protected PageResult<?> getPageResult(List inboxItems, PageSpecification pageSpecification, int noOfPages) {
		 return new PageResult<RecoveryClaim>(inboxItems, pageSpecification, noOfPages);
	}

    public List<SupplierPartReturn> getSupplierPartReturnBeans() {
        return supplierPartReturnBeans;
    }

    public void setSupplierPartReturnBeans(List<SupplierPartReturn> supplierPartReturnBeans) {
        this.supplierPartReturnBeans = supplierPartReturnBeans;
    }

    public Supplier getSupplier() {
        return supplier;
    }

    public void setSupplier(Supplier supplier) {
        this.supplier = supplier;
    }

    public String listSupplierLocations() {
        String locationCode = getSearchPrefix().toLowerCase();
        List<Location> locations = new ArrayList<Location>();
        for (Location location : getSupplier().getLocations()) {
            if (location.getD() != null
                    && location.getD().isActive()
                    && StringUtils.hasText(location.getCode())
                    && location.getCode().toLowerCase()
                    .startsWith(locationCode)) {
                locations.add(location);
            }
        }
        return generateAndWriteComboboxJson(locations, "id", "code");
    }

    public String shippingAddress(){
        return SUCCESS;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }
}
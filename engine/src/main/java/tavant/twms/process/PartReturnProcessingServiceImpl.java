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
package tavant.twms.process;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.jbpm.graph.def.Transition;
import org.jbpm.scheduler.exe.Timer;
import org.jbpm.taskmgmt.exe.TaskInstance;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import tavant.twms.domain.bu.BusinessUnitService;
import tavant.twms.domain.catalog.CatalogService;
import tavant.twms.domain.catalog.Item;
import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.claim.ClaimCurrencyConversionAdvice;
import tavant.twms.domain.claim.ClaimService;
import tavant.twms.domain.claim.ClaimType;
import tavant.twms.domain.claim.LaborDetail;
import tavant.twms.domain.claim.OEMPartReplaced;
import tavant.twms.domain.claim.RecoveryClaim;
import tavant.twms.domain.claim.RecoveryClaimState;
import tavant.twms.domain.claim.payment.Payment;
import tavant.twms.domain.claim.payment.PaymentCalculationException;
import tavant.twms.domain.claim.payment.PaymentService;
import tavant.twms.domain.common.LovRepository;
import tavant.twms.domain.common.RejectionReason;
import tavant.twms.domain.common.TaskCriteria;
import tavant.twms.domain.configuration.ConfigName;
import tavant.twms.domain.configuration.ConfigParam;
import tavant.twms.domain.configuration.ConfigParamService;
import tavant.twms.domain.configuration.ConfigParamServiceImpl;
import tavant.twms.domain.configuration.ConfigValue;
import tavant.twms.domain.notification.EventService;
import tavant.twms.domain.orgmodel.*;
import tavant.twms.domain.partreturn.Location;
import tavant.twms.domain.partreturn.PartReturn;
import tavant.twms.domain.partreturn.PartReturnAction;
import tavant.twms.domain.partreturn.PartReturnService;
import tavant.twms.domain.partreturn.PartReturnServiceImpl;
import tavant.twms.domain.partreturn.PartReturnStatus;
import tavant.twms.domain.partreturn.PartReturnTaskTriggerStatus;
import tavant.twms.domain.partreturn.Shipment;
import tavant.twms.domain.partreturn.ShipmentService;
import tavant.twms.domain.partreturn.Wpra;
import tavant.twms.domain.partreturn.WpraService;
import tavant.twms.domain.supplier.SupplierPartReturn;
import tavant.twms.domain.supplier.contract.ContractService;
import tavant.twms.domain.supplier.contract.ContractServiceImpl;
import tavant.twms.domain.supplier.recovery.RecoverablePart;
import tavant.twms.domain.supplier.recovery.RecoveryClaimInfo;
import tavant.twms.domain.supplier.recovery.RecoveryInfo;
import tavant.twms.domain.supplier.recovery.RecoveryInfoService;
import tavant.twms.infra.InstanceOfUtil;
import tavant.twms.infra.ProcessVariables;
import tavant.twms.jbpm.WorkflowConstants;
import tavant.twms.security.SecurityHelper;
import tavant.twms.security.SelectedBusinessUnitsHolder;
import tavant.twms.worklist.WorkListItemService;
import tavant.twms.worklist.partreturn.PartReturnWorkListDao;
import tavant.twms.worklist.partreturn.PartReturnWorkListService;
import tavant.twms.worklist.supplier.SupplierRecoveryWorkListDao;

public class PartReturnProcessingServiceImpl implements
		PartReturnProcessingService {
	

	private final Logger logger = Logger.getLogger(this.getClass());

	public static final String PART = "part";

	public static final String PART_RETURN = "partReturn";

	public static final String PARTS_RETURN_PROCESS = "PartsReturn";
	
	private static final String SUPPLIER_PART_RETURN = "supplierPartReturn";
	
	//private List<PartTaskBean> partTaskBeans = new ArrayList<PartTaskBean>();

	private SupplierRecoveryWorkListDao supplierRecoveryWorkListDao;

	private static final String RECOVERY_CLAIM = "recoveryClaim";

	private static final String RETURN_MARKED_BY = "returnMarkedBy";

	public static final String CLAIM_PROCESSING = "ClaimSubmission";
	
	public static final String AUTO_DISPUTE = "AutoDispute";

	private ShipmentService shipmentService;

	private WorkListItemService workListItemService;

	private ContractService contractService;

	private ProcessService processService;

	private PartReturnWorkListService partReturnWorkListService;

	private PartReturnService partReturnService;

	private ConfigParamService configParamService;

	private SecurityHelper securityHelper;

	private BusinessUnitService businessUnitService;

	//Email-Notification Event Service
	private EventService eventService;

	//E-mail notification Code End

	private Boolean replacedInstalled = false;

	private CatalogService catalogService;

	private PaymentService paymentService;

	private ClaimService claimService;

	private ClaimCurrencyConversionAdvice claimCurrencyConversionAdvice;

	private OrgService orgService;

	private LovRepository lovRepository;

    private PartReturnWorkListDao partReturnWorkListDao;

    private RecoveryInfoService recoveryInfoService;

    private WpraService wpraService;

   /* private SendEmailService sendEmailService;
    private ApplicationSettingsHolder applicationSettings;*/

    public void startPartReturnProcessForAllParts(Claim claim) {
		List<OEMPartReplaced> parts = claim.getServiceInformation()
				.getServiceDetail().getReplacedParts();
		for (OEMPartReplaced part : parts) {
			startPartReturnProcessForPart(claim, part);
		}
	}

	public void startPartReturnProcessForPart(Claim claim, OEMPartReplaced part) {
		List<PartTaskBean> partTaskBeans = new ArrayList<PartTaskBean>();
		getAllTasksForClaim(claim, partTaskBeans);
		if (!(claim.getServiceInformation().getServiceDetail()
				.getReplacedParts().contains(part))) {
			throw new IllegalArgumentException("Part["
					+ part.getItemReference().getUnserializedItem().getNumber()
					+ "] is not a part replaced of Claim[" + claim.getId()
					+ "]");
		}
		if (part.isPartToBeReturned()
				|| (part.getPartReturns() != null && !part.getPartReturns()
						.isEmpty())) {
			int partsRemovedByProcessor = 0;
			int partsToBeShipped = 0;
			for (PartReturn partReturn : part.getPartReturns()) {
				if (partReturn.isDueDateUpdated())
				{
					Timer timer = partReturnWorkListDao.findTimerForTaskInstance(this.findTaskForPartReturn(partReturn, partTaskBeans));
					if(timer != null)
			        processService.updateDueDateForPartReturn(timer, partReturn.getDueDate());
				}
				if (PartReturnTaskTriggerStatus.TO_BE_TRIGGERED
						.equals(partReturn.getTriggerStatus())) {
					startPartReturnProcess(claim, partReturn);
					partReturn
							.setTriggerStatus(PartReturnTaskTriggerStatus.TRIGGERED);
					partsToBeShipped++;
				} else if (PartReturnTaskTriggerStatus.TO_BE_ENDED
						.equals(partReturn.getTriggerStatus())) {
					endPartReturnProcess(claim, partReturn, partTaskBeans);
					partReturn.setStatus(PartReturnStatus.REMOVED_BY_PROCESSOR);
					partReturn
							.setTriggerStatus(PartReturnTaskTriggerStatus.ENDED);
					partsRemovedByProcessor++;
				}
			}

			if(partsToBeShipped > 0) {
				part.setPartAction1(new PartReturnAction(
						PartReturnStatus.PART_TO_BE_SHIPPED.getStatus(),
						partsToBeShipped));
				part.setPartAction2(null);
			} else if(partsRemovedByProcessor > 0) {
				part.setPartAction1(new PartReturnAction(
						PartReturnStatus.REMOVED_BY_PROCESSOR.getStatus(),
						partsRemovedByProcessor));
				part.setPartAction2(null);
			} else {
				return;
			}

			if(partsToBeShipped > 0 || partsRemovedByProcessor > 0) {
				//I have to create an event to ensure that we have email sent to respective users when due part
				//returns are created.
				createEvent(part.getPartReturns(), claim);
				//My beautiful code ends here
	
				partReturnService.updatePartStatus(part);
			}
		}
	}

	public void startPartReturnProcess(Claim claim, PartReturn partReturn) {
		ProcessVariables variables = new ProcessVariables();
        claim.getServiceInformation().getServiceDetail();
        variables.setVariable("claim", claim);
        List<ConfigValue> configValues= configParamService.getValuesForConfigParam(ConfigName.ENABLE_PART_RETURN_THROUGH_WPRA.getName());
        String wpra = null;
        for(ConfigValue cfgVl :configValues ){
        	if(cfgVl.getBusinessUnitInfo().getName().equalsIgnoreCase(claim.getBusinessUnitInfo().getName()))
        		wpra = cfgVl.getConfigParamOption().getValue();
        }
        variables.setVariable("isThroughWPRA",wpra);
		variables.setVariable(PART_RETURN, partReturn);

		processService.startProcess(PARTS_RETURN_PROCESS, variables);
	}
	
	private TaskInstance findTaskForPartReturn(PartReturn partReturn,
			List<PartTaskBean> partTaskBeans) {
		for (PartTaskBean partTask : partTaskBeans) {
			PartReturn partReturnFromBean = partTask.getPartReturn();
			if (partReturnFromBean != null
					&& partReturnFromBean.getId() != null
					&& partReturn.getId() != null
					&& partReturnFromBean.getId().longValue() == partReturn
							.getId().longValue()) {
				return partTask.getTask();
			}
		}
		return null;
	}

	public void endPartReturnProcess(Claim claim, PartReturn partReturn,
			List<PartTaskBean> partTaskBeans) {

		for (PartTaskBean partTask : partTaskBeans) {
			PartReturn partReturnFromBean = partTask.getPartReturn();
			if (partReturnFromBean != null
					&& partReturnFromBean.getId() != null
					&& partReturn.getId() != null
					&& partReturnFromBean.getId().longValue() == partReturn
							.getId().longValue()) {
				workListItemService.endTaskWithTransition(partTask.getTask(),
						"toEnd");
			}

		}
	}

	public void endRecoveryPartReturnProcess(OEMPartReplaced partReplaced) {
		List<TaskInstance> supPartReturnTasks = supplierRecoveryWorkListDao
				.findAllNotShippedPartTasksForLocation(partReplaced);
		for (TaskInstance task : supPartReturnTasks) {
			workListItemService.endTaskWithTransition(task, "toEnd");
		}

	}

	public void endPartReturnNotGenerated(Claim claim) {
		List<TaskInstance> supPartReturnTasks = supplierRecoveryWorkListDao
				.findDuePartNotGeneratedTasks(claim);
		for (TaskInstance task : supPartReturnTasks) {
			workListItemService.endTaskWithTransition(task, "toEnd");
		}

	}

	public void endPartReturnNotIntiatedBySupplier(RecoveryClaim recClaim) {
		List<TaskInstance> supPartReturnTasks = supplierRecoveryWorkListDao
				.findPartShipperShipmentOpenTasks(recClaim);
		for (TaskInstance task : supPartReturnTasks) {
			workListItemService.endTaskWithTransition(task, "toEnd");
		}

	}
	
	public void startRecoveryRoutingProcess(RecoverablePart recoverablePart, RecoveryClaim recClaim) {
		for (SupplierPartReturn supplierPartReturn : recoverablePart.getSupplierPartReturns()) {
			if (supplierPartReturn.getStatus().equals(PartReturnStatus.SUP_PART_RETURN_NOT_INITIATED)) {
				ProcessVariables variables = new ProcessVariables();
				variables.setVariable(RECOVERY_CLAIM, recClaim);
				variables.setVariable(SUPPLIER_PART_RETURN, supplierPartReturn);
				processService.startProcessWithTransition("SupplierPartReturn", variables, "Routed to NMHG");
			}
		}
	}

	public void startRecoveryPartReturnProcess(RecoverablePart recoverablePart, RecoveryClaim recClaim) {
		for (SupplierPartReturn supplierPartReturn : recoverablePart.getSupplierPartReturns()) {
			if (supplierPartReturn.getStatus().equals(PartReturnStatus.SUP_PART_RETURN_NOT_INITIATED)) {
				ProcessVariables variables = new ProcessVariables();
				variables.setVariable(RECOVERY_CLAIM, recClaim);
				variables.setVariable(SUPPLIER_PART_RETURN, supplierPartReturn);
				if (isPartReturnPresent(supplierPartReturn)) {
					if (isPartInWarehouse(supplierPartReturn)) {// need to fix this for all BUs
						supplierPartReturn.setStatus(PartReturnStatus.PART_TO_BE_SHIPPED);
						processService.startProcessWithTransition("SupplierPartReturn", variables, "Generate Shipment");
					} else {
						supplierPartReturn.setStatus(PartReturnStatus.AWAITING_SHIPMENT_TO_WAREHOUSE);
						processService.startProcessWithTransition("SupplierPartReturn", variables, "Awaiting Shipment to Warehouse");
					}
				} else {
					supplierPartReturn.setStatus(PartReturnStatus.DUE_PART_TO_BE_GENERATED);
					processService.startProcessWithTransition("SupplierPartReturn", variables, "Return from Dealer");
				}
			}
		}
	}

    public void startRecoveryPartReturnProcessForRecPart(RecoverablePart recoverablePart, RecoveryClaim recClaim, Location returnLocation) {
                ProcessVariables variables = new ProcessVariables();
                SupplierPartReturn supplierPartReturn = new SupplierPartReturn();
                supplierPartReturn.setRecoverablePart(recoverablePart);
                supplierPartReturn.setOemPartReplaced(recoverablePart.getOemPart());
                supplierPartReturn.setBasePartReturnStatus(PartReturnStatus.PARTS_TO_BE_SHIPPED_BY_SUPPLIER_TO_NMHG);
                supplierPartReturn.setReturnLocation(returnLocation);
                variables.setVariable(RECOVERY_CLAIM, recClaim);
                variables.setVariable(SUPPLIER_PART_RETURN, supplierPartReturn);
                supplierPartReturn.setStatus(PartReturnStatus.AWAITING_SHIPMENT_TO_WAREHOUSE);
                processService.startProcessWithTransition("SupplierPartReturn", variables, "Awaiting Shipment to Warehouse");

    }

	private boolean isPartReturnPresent(SupplierPartReturn supplierPartReturn) {
		
			return supplierPartReturn.getRecoverablePart().getOemPart().isPartReturnsPresent();
	}

	private boolean isPartInWarehouse(SupplierPartReturn supplierPartReturn) {
		return supplierPartReturn.getRecoverablePart().getOemPart().isPartInWarehouse();		
	}

	public void startRecoveryProcess(RecoveryInfo recoveryInfo) {
		Claim claim = recoveryInfo.getWarrantyClaim();
		if (claim.getReopenRecoveryClaim()) {
			for (RecoveryClaimInfo recClaimInfo : recoveryInfo.getReplacedPartsRecovery()) {
				if (recClaimInfo.getContract() != null) {
					if (recClaimInfo.getRecoveryClaim().getRecoveryClaimState() != null)
						startRecoveryWorkflow(recClaimInfo.getRecoveryClaim(), "Reopen");// recovery claim needs to be reopened
					else
						startRecoveryWorkflow(recClaimInfo.getRecoveryClaim(), null);
				}
			}
		} else {
			// Create a recovery claim if none exists
			if (!containsRecoveryClaim(recoveryInfo)) {
				contractService.createRecoveryClaims(recoveryInfo);
				recoveryInfoService.saveUpdate(recoveryInfo);
			}
			if (recoveryInfo.getReplacedPartsRecovery() != null && !recoveryInfo.getReplacedPartsRecovery().isEmpty()) {
				for (RecoveryClaimInfo recClaimInfo : recoveryInfo.getReplacedPartsRecovery()) {
					startRecoveryWorkflow(recClaimInfo.getRecoveryClaim(), null);
				}
			}
		}
	}
    
	public void initiateRecoveryProcess(RecoveryInfo recoveryinfo){		
		this.recoveryInfoService.saveUpdate(recoveryinfo);
		startRecoveryProcess(recoveryinfo);
	}
	

	private void startRecoveryWorkflow(RecoveryClaim recClaim, String transitionName) {		
		if (transitionName != null) {
			if (transitionName.equals("Reopen")) {
				TaskInstance taskInstance = workListItemService.findTaskForRecClaimWithTaskName(recClaim.getId(), "Closed");
				workListItemService.endTaskWithTransition(taskInstance, transitionName);
			} else if (transitionName.equals("Cannot Recover")) {
				TaskInstance taskInstance = workListItemService.findTaskForRecClaimWithTaskName(recClaim.getId(), "Reopened");
				workListItemService.endTaskWithTransition(taskInstance, transitionName);
			}
		}
        //If recovery claim state is not null then it has been initiated already
        else if(recClaim.getRecoveryClaimState() == null) {
			if(recClaim.getActiveRecoveryClaimAudit().getCreatedBy() == null && this.securityHelper!=null && this.securityHelper.getLoggedInUser()!=null){
                recClaim.getActiveRecoveryClaimAudit().setCreatedBy(this.securityHelper.getLoggedInUser());
            }
            recClaim.setRecoveryClaimState(RecoveryClaimState.NEW);
            if(recClaim.getLatestRecoveryAudit().getCreatedBy() == null && this.securityHelper!=null && this.securityHelper.getLoggedInUser()!=null){
                recClaim.getLatestRecoveryAudit().setCreatedBy(this.securityHelper.getLoggedInUser());
            }
			ProcessVariables variables = new ProcessVariables();
			variables.setVariable(RECOVERY_CLAIM, recClaim);
			variables.setVariable(AUTO_DISPUTE, getConfigParamService().getBooleanValue(ConfigName.AUTO_DISPUTE_VRCLAIMS_IF_NO_ACTION_FROM_SUPPLIER.getName()));
			processService.startProcess("SupplierRecovery", variables);
		}
		
		// P2 UAT: Automatic part return Changes(NMHGSLMS-1117)
		if ("AMER".equalsIgnoreCase(recClaim.getClaim().getBusinessUnitInfo().getName())) 
		{
			if (!recClaim.getContract().getSraReviewRequired()&& recClaim.getContract().getPhysicalShipmentRequired())
			{
				
	    		TaskInstance taskInstanceOne = this.workListItemService
	    				.findTaskForRecClaimWithTaskName(
	    						recClaim.getId(), "New");

	    		if(taskInstanceOne != null){
	    			this.workListItemService.endTaskWithTransition(taskInstanceOne,
	    				"On Hold For Part Return");
	    		}
	            
				startPartReturnFlowForRecoveryClaim(recClaim);
			}
			else if (recClaim.getContract().getSraReviewRequired()&& "Send To Supplier".equals(transitionName))
			{
				startPartReturnFlowForRecoveryClaim(recClaim);
			}

		} else {
			if (recClaim.getContract().getPhysicalShipmentRequired()) 
			{
				startPartReturnFlowForRecoveryClaim(recClaim);
			}
		}
	}

    public boolean isClaimFiledByCanadianDealer(Long id) {
        ServiceProvider dealership = orgService.findDealerById(id);
        return dealership.getAddress().getCountry().equalsIgnoreCase("CA");
    }

    public String getDefaultPartReturnLocation(RecoveryClaim recoveryClaim){
        String centralLogisticName  = getConfigParamService().getStringValueByBU(ConfigName.DEFAULT_RETURN_LOCATION_CODE.getName(), recoveryClaim.getBusinessUnitInfo().getName());
        return centralLogisticName;
    }

	public void  startPartReturnFlowForRecoveryClaim(RecoveryClaim recoveryClaim){
		for (RecoverablePart recoverablePart : recoveryClaim.getRecoveryClaimInfo().getRecoverableParts()) {
			if (recoverablePart.isSupplierReturnNeeded()) {
                //If part return has not already initiated then notify directly to dealer
                if(!recoverablePart.getOemPart().isPartToBeReturned() && configParamService.getBooleanValue(ConfigName.PART_RECOVERY_DIRECTLY_THROUGH_DEALER.getName())){
                    //initiate return directly to supplier
                    recoverablePart.getOemPart().setPartToBeReturned(true);
                    recoverablePart.getOemPart().setReturnDirectlyToSupplier(true);
                    //TODO Get the due days from the config param
                    PartReturn partReturn = new PartReturn();
                    partReturn.setDueDays(getConfigParamService().getLongValue(ConfigName.DEFAULT_DUE_DAYS_FOR_PART_RETURN.getName()).intValue());
                    if(isClaimFiledByCanadianDealer(recoveryClaim.getClaim().getForDealer().getId())){
                        //2 return required -- dealer -> nmhg, nmhg-> supplier scheduler
                        //initiate the dealer --> nmhg data -- > return location change
                       partReturn.setReturnLocation(claimService.getLocationForDefaultPartReturn(getDefaultPartReturnLocation(recoveryClaim)));
                        //initiate recovery return
                        recoverablePart.setSupplierReturnNeeded(true);
                        startRecoveryPartReturnProcessForRecPart(recoverablePart,recoveryClaim, recoveryClaim.getContract().getLocation());
                        //Make direct return to false
                        recoverablePart.getOemPart().setReturnDirectlyToSupplier(false);

                    }else{
                        partReturn.setReturnLocation(recoveryClaim.getContract().getLocation());
                    }
                    partReturn.setOemPartReplaced(recoverablePart.getOemPart());
                    recoverablePart.getOemPart().setPartReturn(partReturn);
                    partReturnService.updateExistingPartReturns(recoverablePart.getOemPart(), recoveryClaim.getClaim());
                    startPartReturnProcessForPart(recoveryClaim.getClaim(),recoverablePart.getOemPart());
                }else{              
                	this.contractService.updateSupplierPartReturn(recoverablePart, recoveryClaim.getContract().getLocation(), recoveryClaim.getContract().getCarrier(), null);
				    startRecoveryPartReturnProcess(recoverablePart, recoveryClaim);
                }
			}
		}
	}

    private Boolean containsRecoveryClaim(RecoveryInfo recoveryinfo) {

        for (RecoveryClaimInfo recClaimInfo : recoveryinfo.getReplacedPartsRecovery()) {
            if (recClaimInfo.getRecoveryClaim() != null) {
                return true;
            }
        }
        return false;
    }

	private void getAllTasksForClaim(Claim claim,
			List<PartTaskBean> partTaskBeans) {
		List<TaskInstance> taskInstances = this.partReturnWorkListService
				.getNotShippedPartReturnTaskInstancesByClaim(claim);
		for (TaskInstance taskInstance : taskInstances) {
			PartTaskBean partTaskBean = new PartTaskBean();
			partTaskBean.setTask(taskInstance);
			partTaskBeans.add(partTaskBean);
		}
	}

	public List<Shipment> createShipmentsByLocation(
			Collection<List<PartTaskBean>> partBeansByLocation,
			String transitionTaken) {
		List<Shipment> shipments = new ArrayList<Shipment>();
		for (List<PartTaskBean> partTasks : partBeansByLocation) {
			shipments.add(createShipment(partTasks, transitionTaken));
		}
		shipmentService.reloadShipments(shipments);
		 return shipments;
	}

    public List<Wpra> movePartToDuePartInbox(Collection<List<PartTaskBean>> partBeansByLocation, String transitionTaken){
		List<Wpra> wpras = new ArrayList<Wpra>();
		for (List<PartTaskBean> partTasks : partBeansByLocation) {
			wpras.add(createWpra(partTasks, transitionTaken));
		}
		wpraService.reloadWpras(wpras);
		return wpras;
    }

    public Wpra createWpra(List<PartTaskBean> partTasks,
			String transitionTaken) {
		List<TaskInstance> taskInstances = new ArrayList<TaskInstance>();
		List<PartReturn> parts = new ArrayList<PartReturn>();
		for (PartTaskBean bean : partTasks) {
			taskInstances.add(bean.getTask());
            if(null != bean.getPartReturn() && null == bean.getPartReturn().getReturnedBy()){
                     bean.getPartReturn().setReturnedBy(bean.getClaim().getForDealer());
                }
			parts.add(bean.getPartReturn());
            if(WorkflowConstants.MOVE_TO_DUE_PARTS_FROM_WPRA.equals(transitionTaken)){
                bean.getPartReturn().setTriggerStatus(
					PartReturnTaskTriggerStatus.WPRA_GENERATED);
            }
		}
        Wpra wpra = null;
        if(partTasks.size() >0 && null != partTasks.get(0) && partTasks.get(0).getClaim() != null){
               wpra = wpraService.createWpraForParts(parts, partTasks.get(0).getClaim());
        }else{
		 wpra = wpraService.createWpraForParts(parts);
        }
		workListItemService.endAllTasksWithTransition(taskInstances,
				transitionTaken);
		return wpra;
	}

	public Shipment createShipment(List<PartTaskBean> partTasks,
			String transitionTaken) {
		List<TaskInstance> taskInstances = new ArrayList<TaskInstance>();
		List<PartReturn> parts = new ArrayList<PartReturn>();
		for (PartTaskBean bean : partTasks) {
			taskInstances.add(bean.getTask());
            if(null != bean.getPartReturn() && null == bean.getPartReturn().getReturnedBy()){
                     bean.getPartReturn().setReturnedBy(bean.getClaim().getForDealer());
                }
			parts.add(bean.getPartReturn());
            if(WorkflowConstants.GENERATE_SHIPMENT_FOR_DEALER.equals(transitionTaken)){
                bean.getPartReturn().setTriggerStatus(
					PartReturnTaskTriggerStatus.SHIPMENT_GENERATED_FOR_DEALER);
            }else{
			bean.getPartReturn().setTriggerStatus(
					PartReturnTaskTriggerStatus.SHIPMENT_GENERATED);
            }
		}

		Shipment shipment = shipmentService.createShipmentForParts(parts);
		workListItemService.endAllTasksWithTransition(taskInstances,
				transitionTaken);
		return shipment;
	}


	public void removePartsFromItsShipment(List<PartReturn> parts,
			List<TaskInstance> taskInstances, String transition) {
		shipmentService.removePartsFromItsShipment(parts);
		workListItemService
				.endAllTasksWithTransition(taskInstances, transition);
	}
	
	public void addPartsToShipment(Long shipmentId, List<PartReturn> parts,
			List<TaskInstance> taskInstances, String transition) {
		shipmentService.addPartsToShipment(shipmentId, parts);
		workListItemService
				.endAllTasksWithTransition(taskInstances, transition);
	}

	public void setShipmentService(ShipmentService shipmentService) {
		this.shipmentService = shipmentService;
	}

	public void setWorkListItemService(WorkListItemService workListItemService) {
		this.workListItemService = workListItemService;
	}

	public void setProcessService(ProcessService processService) {
		this.processService = processService;
	}

	public void setPartReturnWorkListService(
			PartReturnWorkListService partReturnWorkListService) {
		this.partReturnWorkListService = partReturnWorkListService;
	}

	public void setPartReturnService(PartReturnService partReturnService) {
		this.partReturnService = partReturnService;
	}

	public void setSupplierRecoveryWorkListDao(
			SupplierRecoveryWorkListDao supplierRecoveryWorkListDao) {
		this.supplierRecoveryWorkListDao = supplierRecoveryWorkListDao;
	}

	public void setContractService(ContractService contractService) {
		this.contractService = contractService;
	}

	private TaskCriteria createTaskCriteria(String configParam, 
			Map<String, List<Object>> params, 
			List<String> endTransitions, 
			Set<String> ignoreTasks, 
			Map<String, RejectionReason> defaultRejectionReasonMap, 
			String internalComment, 
			Map<String, String> filterColumns){
		TaskCriteria criteria = new TaskCriteria();
		criteria.setConfigParam(configParam);
		criteria.setEndTransitions(endTransitions);
		criteria.setBuWiseFilterColumns(filterColumns);
		criteria.setIgnoreTasks(ignoreTasks);
		criteria.setInternalComment(internalComment);
		criteria.setParams(params);
		criteria.setRejectionReasonMap(defaultRejectionReasonMap);
		return criteria;
	}

	public ConfigParamService getConfigParamService() {
		return configParamService;
	}

	public void setConfigParamService(ConfigParamService configParamService) {
		this.configParamService = configParamService;
	}

	private boolean isShipmentGeneratedForPart(OEMPartReplaced part) {
		boolean exists = false;
		for (PartReturn partReturn : part.getPartReturns()) {
			if (partReturn.getStatus().ordinal() >= PartReturnStatus.SHIPMENT_GENERATED
					.ordinal()
					&& !PartReturnStatus.REMOVED_BY_PROCESSOR.equals(partReturn
							.getStatus())) {
				exists = true;
				break;
			}
		}
		return exists;
	}

	private boolean isRemovedByProcessorForPart(OEMPartReplaced part) {
		boolean exists = false;
		for (PartReturn partReturn : part.getPartReturns()) {
			if (partReturn.getStatus().equals(
					PartReturnStatus.REMOVED_BY_PROCESSOR)) {
				exists = true;
				break;
			}
		}
		return exists;
	}

	public void setSecurityHelper(SecurityHelper securityHelper) {
		this.securityHelper = securityHelper;
	}

	public void setBusinessUnitService(BusinessUnitService businessUnitService) {
		this.businessUnitService = businessUnitService;
	}

	private int getPartToBeShippedCount(OEMPartReplaced part) {
		int count = 0;
		for (PartReturn partReturn : part.getPartReturns()) {
			if (partReturn.getStatus().equals(
					PartReturnStatus.PART_TO_BE_SHIPPED)) {
				count++;
			}
		}
		return count;
	}

	private int getRemovedByProcessorCount(OEMPartReplaced part) {
		int count = 0;
		for (PartReturn partReturn : part.getPartReturns()) {
			if (partReturn.getStatus().equals(
					PartReturnStatus.REMOVED_BY_PROCESSOR)) {
				count++;
			}
		}
		return count;
	}

	/**
	 * This is the list of part return from which we are going to create an event.  
	 * 
	 * @param partReturnList
	 */
	private void createEvent(List<PartReturn> partReturnList, Claim claim) {
		HashMap<String, Integer> partsShipped = new HashMap<String, Integer>();
		PartReturn currentPartReturn = null;
		String currentItemNumber;
		Set<String> keySet = null;
		StringBuffer finalPartNumberString;
		String finalPartNumberValue;
		HashMap<String, Object> eventHashMap = new HashMap<String, Object>();
		Long partReturnId = null;
		if (partReturnList != null && partReturnList.size() > 0) {
			for (Iterator<PartReturn> partReturnIte = partReturnList.iterator(); partReturnIte
					.hasNext();) {
				currentPartReturn = partReturnIte.next();
				if (currentPartReturn != null
						&& currentPartReturn.getOemPartReplaced() != null
						&& currentPartReturn.getOemPartReplaced()
								.getItemReference() != null
						&& currentPartReturn.getOemPartReplaced()
								.getItemReference().getReferredItem() != null) {
					if (partReturnId == null) {
						partReturnId = currentPartReturn.getId();
					}
					currentItemNumber = currentPartReturn.getOemPartReplaced()
							.getItemReference().getReferredItem().getBrandItemNumber(claim.getBrand());
					if (partsShipped.containsKey(currentItemNumber)) {
						//since this part already exist we will merely update quantity
						partsShipped.put(currentItemNumber, new Integer(
								partsShipped.get(currentItemNumber) + 1));
					} else {
						//since part number doesn't exist we will make an entry with quantity as one.
						partsShipped.put(currentItemNumber, new Integer(1));
					}
				}
			}

			//now that we are done with updating part numbers and quantities lets just create a string out of it
			keySet = partsShipped.keySet();
			finalPartNumberString = new StringBuffer();
			for (Iterator<String> ite = keySet.iterator(); ite.hasNext();) {
				currentItemNumber = ite.next();
				finalPartNumberString.append(currentItemNumber);
				finalPartNumberString.append(" :: ");
				finalPartNumberString.append(partsShipped
						.get(currentItemNumber));
				finalPartNumberString.append(", ");
			}

			//remove the last comma from the string buffer
			finalPartNumberValue = finalPartNumberString.substring(0,
					(finalPartNumberString.length() - 2));

			//set the values in hashMap
			eventHashMap.put("claimId", claim.getId().toString());
			eventHashMap.put("partNumberString", finalPartNumberValue);
			eventHashMap.put("taskInstanceId", partReturnId.toString());
			eventHashMap.put("subject", "Claim - " + claim.getClaimNumber() + " needs attention");
			//create the event
			eventService.createEvent("partReturn",
					EventState.START_PART_RETURN, eventHashMap);
		}
	}

	public void focClaimsPendingSubmission() {
		securityHelper.populateFocUser();
		SelectedBusinessUnitsHolder.setSelectedBusinessUnit("Hussmann");
		Long periodForAutoSubmit = configParamService
				.getLongValue(ConfigName.DAYS_FOR_FOC_CLAIM_AUTO_SUBMISSION
						.getName());
		StringBuffer draftClaimPeriodConstraint = new StringBuffer();
		draftClaimPeriodConstraint.append(" claim.filedOnDate ");
		draftClaimPeriodConstraint.append(" + (");
		draftClaimPeriodConstraint.append(periodForAutoSubmit);
		draftClaimPeriodConstraint.append(") <= sysdate) ");
		List<TaskInstance> allTaskInstances = workListItemService
				.getAllFocClaimsForAutoSubmit(draftClaimPeriodConstraint
						.toString());
		for (TaskInstance taskInstance : allTaskInstances) {
			Claim claim = (Claim) taskInstance.getContextInstance()
					.getVariable("claim");
			prepareOEMPartCrossRef(claim);
			setTotalLaborHoursForClaim(claim);
			computePayment(claim);
			populateServicingLocationOnClaim(claim);
			taskInstance.getContextInstance().setVariable("claim", claim);
			workListItemService.endTaskWithTransition(taskInstance,
					"Submit Claim");

		}
		SelectedBusinessUnitsHolder.clearChosenBusinessUnitFilter();
	}

	private void prepareOEMPartCrossRef(Claim claim) {
		Organization organization = claim.getForDealer();

		if (InstanceOfUtil.isInstanceOfClass(Dealership.class, organization)) {
			prepareOEMCrossRefForDealer(claim);
		} else {
			prepareOEMCrossRefForInternalUser(claim);
		}
	}

	private void prepareOEMCrossRefForDealer(Claim claim) {
		if (claim.getServiceInformation().getCausalPart() != null) {
			Item item = this.catalogService.findPartForOEMDealerPart(claim
					.getServiceInformation().getCausalPart(), claim
					.getForDealer());
			if (item != null) {
				claim.getServiceInformation().setOemDealerCausalPart(
						claim.getServiceInformation().getCausalPart());
				claim.getServiceInformation().setCausalPart(item);
			}
		}
		if (claim.getServiceInformation().getServiceDetail() != null
				&& claim.getServiceInformation().getServiceDetail()
						.getOEMPartsReplaced() != null) {
			Iterator<OEMPartReplaced> oemPartReplacedIterator = claim
					.getServiceInformation().getServiceDetail()
					.getOEMPartsReplaced().iterator();
			while (oemPartReplacedIterator.hasNext()) {
				OEMPartReplaced oemPartReplaced = oemPartReplacedIterator
						.next();
				Item item = this.catalogService.findPartForOEMDealerPart(
						oemPartReplaced.getItemReference().getReferredItem(),
						claim.getForDealer());
				if (item != null) {
					oemPartReplaced.setOemDealerPartReplaced(oemPartReplaced
							.getItemReference().getReferredItem());
					oemPartReplaced.getItemReference().setReferredItem(item);
				}
			}
		}
	}

	private void prepareOEMCrossRefForInternalUser(Claim claim) {
		if (claim.getServiceInformation().getCausalPart() != null) {
			Item item = this.catalogService.findOEMDealerPartForPart(claim
					.getServiceInformation().getCausalPart(), claim
					.getForDealer());
			if (item != null) {
				claim.getServiceInformation().setOemDealerCausalPart(item);
			} else {
				claim.getServiceInformation().setOemDealerCausalPart(null);
			}
		}
		if (claim.getServiceInformation().getServiceDetail() != null
				&& claim.getServiceInformation().getServiceDetail()
						.getOEMPartsReplaced() != null) {
			Iterator<OEMPartReplaced> oemPartReplacedIterator = claim
					.getServiceInformation().getServiceDetail()
					.getOEMPartsReplaced().iterator();
			while (oemPartReplacedIterator.hasNext()) {
				OEMPartReplaced oemPartReplaced = oemPartReplacedIterator
						.next();
				Item item = this.catalogService.findOEMDealerPartForPart(
						oemPartReplaced.getItemReference().getReferredItem(),
						claim.getForDealer());
				if (item != null) {
					oemPartReplaced.setOemDealerPartReplaced(item);
				} else {
					oemPartReplaced.setOemDealerPartReplaced(null);
				}
			}
		}
	}

	protected void setTotalLaborHoursForClaim(Claim claim) {
		List<LaborDetail> LaborDetails = claim.getServiceInformation()
				.getServiceDetail().getLaborPerformed();
		for (LaborDetail labor : LaborDetails) {
			if (labor != null) {
				labor
						.setHoursSpentForMultiClaim(labor.getHoursSpent()
								.multiply(
										new BigDecimal(claim
												.getApprovedClaimedItems())));
				if (labor.getAdditionalLaborHours() != null) {
					labor.setAdditionalHoursSpentForMultiClaim(labor
							.getAdditionalLaborHours().multiply(
									new BigDecimal(claim
											.getApprovedClaimedItems())));
				}
			}
		}
	}

	void computePayment(Claim theClaim) {
		try {
			Payment payment = this.paymentService
					.calculatePaymentForClaim(theClaim,null);
			theClaim.setPayment(payment);
		} catch (PaymentCalculationException e) {
			throw new RuntimeException(
					"Error occured while performing payment calculation.", e);
		}
	}

	private void populateServicingLocationOnClaim(Claim claim) {
		List<OrganizationAddress> addressesForOrganization = orgService
				.getAddressesForOrganization(claim.getForDealer());
		if (addressesForOrganization == null
				|| addressesForOrganization.isEmpty()) {
			return;
		}
		claim.setServicingLocation(addressesForOrganization.get(0));
	}

	/**
	 * @return
	 */
	public EventService getEventService() {
		return eventService;
	}

	/**
	 * @param eventService
	 */
	public void setEventService(EventService eventService) {
		this.eventService = eventService;
	}

	public void endAllPartTasksForClaim(Claim claim){
        Map<OEMPartReplaced, List<PartReturn>> partReturnsNotShipped = new HashMap<OEMPartReplaced, List<PartReturn>>();
        List<TaskInstance> taskInstances = workListItemService.getNotShippedPartReturnTaskinstancesForClaim(claim.getId());
        for (Iterator<TaskInstance> iter = taskInstances.iterator(); iter.hasNext();) {
            PartReturn partReturn = (PartReturn)iter.next().getVariable("partReturn");
            List<PartReturn> partReturns = partReturnsNotShipped.get(partReturn.getOemPartReplaced());
            if(partReturns == null) {
            	partReturns = new ArrayList<PartReturn>();
            	partReturnsNotShipped.put(partReturn.getOemPartReplaced(), partReturns);
            }
            partReturns.add(partReturn);
        }

        for (OEMPartReplaced partNotShipped : partReturnsNotShipped.keySet()) {
            for (OEMPartReplaced partreplaced : claim.getServiceInformation().getServiceDetail().getReplacedParts()) {
            	if(partreplaced.getId().longValue()==partNotShipped.getId().longValue()){
            		if (partreplaced.getPartReturns() != null && !partreplaced.getPartReturns().isEmpty()) {
            			List<PartReturn> partReturnsToBeClosed = partReturnsNotShipped.get(partNotShipped);
            			List<PartReturn> shippedPartReturns = new ArrayList<PartReturn>(); 
            			for(PartReturn partReturn : partreplaced.getPartReturns()) {
            				if(!partReturnsToBeClosed.contains(partReturn))
            					shippedPartReturns.add(partReturn);
            			}
            			partreplaced.setPartAction1(new PartReturnAction(
    							PartReturnStatus.CLOSE.getStatus(),
    							partReturnsToBeClosed.size()));
            			partreplaced.setPartReturns(shippedPartReturns);
            			partreplaced.setComments(claim.getLatestAudit().getExternalComments());
            			int shippedPartCount = 0;
            			for(PartReturn pr : shippedPartReturns)
            				if(!pr.getStatus().equals(PartReturnStatus.REMOVED_BY_PROCESSOR))
            					shippedPartCount ++;
            			if(shippedPartCount == 0) {
            				partreplaced.setStatus(PartReturnStatus.CLOSE);
            				if(partreplaced.getPartReturnConfiguration() == null) {
            					partreplaced.getPartReturns().addAll(partReturnsToBeClosed);
            					for(PartReturn pr : partReturnsToBeClosed) {
            						pr.setStatus(PartReturnStatus.CLOSE);
            						pr.setTriggerStatus(PartReturnTaskTriggerStatus.ENDED);
            					}
            				}
            			}
            			else
            				partreplaced.setStatus(partreplaced.getStatus());
            		}
            		break;
            	}
            }
        }
        workListItemService.endAllTasksWithTransition(taskInstances, "toEnd");
        /*Bar code should be reset for parts that are not shipped for a claim that is denied.
         * 
         */
        for (OEMPartReplaced partNotShipped : partReturnsNotShipped.keySet()){
        	List<PartReturn> partReturns = partReturnsNotShipped.get(partNotShipped);
        	for(PartReturn pr : partReturns){
        		partReturnService.update(pr);
        	}
        }

        //Notify the dealer in his Rejected Parts inbox if claim is denied and part return process is not initiated.
        startPartReturnProcessForRejectedParts(claim);
    }

	public Map<String, RejectionReason> getDefaultRejectionReason(
			String defaultRejectionReasonToUse) {
		String configNameParam = defaultRejectionReasonToUse;
		if (defaultRejectionReasonToUse == null) {
			configNameParam = ConfigName.DEFAULT_REJECTION_REASON.getName();
		}

		List<ConfigValue> configValues = configParamService.getValuesForConfigParam(configNameParam);
				
		Map<String, RejectionReason> businessUnitRejectionReasonMap = new HashMap<String, RejectionReason>();
		if (configValues == null
				|| CollectionUtils.isEmpty(configValues)) {			
			return null;
		}
		ConfigParam configParam = configValues.get(0).getConfigParam();
		for (ConfigValue configValue : configValues) {
			try {
				RejectionReason rejectionReason = (RejectionReason) configParamService
						.findObjectForLovId(Long.parseLong(configValue
								.getValue()), Class.forName(configParam
								.getType()));
				businessUnitRejectionReasonMap.put(configValue
						.getBusinessUnitInfo().getName(), rejectionReason);
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return businessUnitRejectionReasonMap;
	}

	private boolean hasTransitionByName(TaskInstance taskInstance,
			String transitionName) {
        boolean isTransitionAvailable = false;
        List<Transition> transitions = taskInstance.getAvailableTransitions();
		for (Transition transition : transitions) {
			if (transitionName.equals(transition.getName())){
                isTransitionAvailable=true;
                break;
            }
		}
		return isTransitionAvailable;
	}
	
   	public void setCatalogService(CatalogService catalogService) {
		this.catalogService = catalogService;
	}

	public void setPaymentService(PaymentService paymentService) {
		this.paymentService = paymentService;
	}

	public void setClaimCurrencyConversionAdvice(
			ClaimCurrencyConversionAdvice claimCurrencyConversionAdvice) {
		this.claimCurrencyConversionAdvice = claimCurrencyConversionAdvice;
	}

	public void setOrgService(OrgService orgService) {
		this.orgService = orgService;
	}

	public ClaimService getClaimService() {
		return claimService;
	}

	public void setClaimService(ClaimService claimService) {
		this.claimService = claimService;
	}

	public void setLovRepository(LovRepository lovRepository) {
		this.lovRepository = lovRepository;
	}

    public void endTasksForParts(List<OEMPartReplaced> removedParts) {
		List<TaskInstance> taskinstances = partReturnWorkListDao.findAllTaskInstanceForParts(removedParts);
        workListItemService.endAllTasksWithTransition(taskinstances, "toEnd");
    }

    public void autoStartRecoveryProcess(Claim claim) {
        SelectedBusinessUnitsHolder.setSelectedBusinessUnit(claim.getBusinessUnitInfo().getName());
        if(!claim.getType().getType().equals(ClaimType.CAMPAIGN.getType())
        		|| CollectionUtils.isEmpty(claim.getRecoveryClaims())){
        RecoveryInfo recoveryInfo = contractService.createRecoveryInfo(claim);
        for(RecoveryClaimInfo recClaimInfo : recoveryInfo.getReplacedPartsRecovery()){
            if(null != recClaimInfo.getContract() && !recClaimInfo.getContract().getSraReviewRequired()) {
                if(null != recClaimInfo.getContract().getSupplier().getUsers()
                        && recClaimInfo.getContract().getSupplier().getUsers().size() > 0){
                    recoveryInfoService.saveUpdate(recoveryInfo);
                    contractService.createRecoveryClaims(recoveryInfo);
                    startRecoveryProcess(recoveryInfo);
                }
            }
            else {
                 recoveryInfoService.saveUpdate(recoveryInfo);
                 contractService.createRecoveryClaims(recoveryInfo);
                 startRecoveryProcess(recoveryInfo);
            }
            //Since we don't have a info on mail receiver, we will do it later. :)
            /*else {
                //Send Email that recovery claim can not be initiated.
                HashMap<String,Object> paramMap = new LinkedHashMap<String, Object>();
                paramMap.put("userName","deepak");
                if(null != recClaimInfo.getContract())
                   paramMap.put("supplier",recClaimInfo.getContract().getSupplier().getFirstName());
                paramMap.put("claim",claim.getClaimNumber());
                paramMap.put("url",applicationSettings.getExternalUrlForEmail());
                sendEmailService.sendEmail(applicationSettings.getFromAddress(),"deepak.patel@tavant.com","",applicationSettings.getRecoveryClaimTemplate(),paramMap);
            }*/
        }
        }

    }

    public void setPartReturnWorkListDao(PartReturnWorkListDao partReturnWorkListDao) {
        this.partReturnWorkListDao = partReturnWorkListDao;
    }

    public void setRecoveryInfoService(RecoveryInfoService recoveryInfoService) {
        this.recoveryInfoService = recoveryInfoService;
    }

    //Added for dealer part back request
     public void startPartReturnProcessForRejectedParts(Claim claim) {
		List<OEMPartReplaced> parts = claim.getServiceInformation().getServiceDetail().getReplacedParts();
        for(OEMPartReplaced part : parts){
		    for (PartReturn partReturn : part.getPartReturns()) {
                if(PartReturnStatus.getShippedPartsStatus().contains(partReturn.getStatus()) && !part.isPartScrapped() &&
                        !(partReturn.getActionTaken() != null && partReturn.getActionTaken().getStatus().equalsIgnoreCase(PartReturnStatus.MARK_NOT_RECEIVED.getStatus()))){
                    ProcessVariables variables = new ProcessVariables();
                    claim.getServiceInformation().getServiceDetail();
                    variables.setVariable("claim", claim);
                    variables.setVariable(PART_RETURN, partReturn);
			        processService.startProcessWithTransition(PARTS_RETURN_PROCESS, variables, "Returned Rejected Part");
             }
		    }
        }
	}

    public void initiateDealerRequestedPart(List<PartTaskBean> partTasks, String transitionTaken){
        List<TaskInstance> taskInstances = new ArrayList<TaskInstance>();
		for (PartTaskBean bean : partTasks) {
            if(bean.getPartReturn().getTriggerStatus().equals(PartReturnTaskTriggerStatus.DEALER_REQUEST_TRIGGERED)){
                taskInstances.add(bean.getTask());
            }
		}
		workListItemService.endAllTasksWithTransition(taskInstances,transitionTaken);
    }

    public void endAllDealerRequestPartReturnTaskForClaim(Long claimId){
        List<TaskInstance> openTasksForClaim =  workListItemService.findAllOpenTasksForClaim(claimId);
        List<TaskInstance> openTasks = new ArrayList<TaskInstance>();
        for(TaskInstance instance : openTasksForClaim){
            if(null != instance.getTask() && null != instance.getTask().getName()){
                if (getTaskNameForDealerPartReturnProcess().contains(instance.getTask().getName())) {
                    openTasks.add(instance);
                }
            }
        }
        workListItemService.endAllTasksWithTransition(openTasks,  "toEnd");
    }

    public List<String> getTaskNameForDealerPartReturnProcess(){
        List<String> taskNames = new ArrayList<String>();
        taskNames.add(WorkflowConstants.REJETCTED_PARTS_INBOX);
        taskNames.add(WorkflowConstants.DEALER_REQUESTED_PART);
        taskNames.add(WorkflowConstants.SHIPMENT_GENERATED_FOR_DEALER);
        return taskNames;
    }

    public void endPrepareDuePartsTasksForParts(List<OEMPartReplaced> removedParts) {
		List<TaskInstance> taskinstances = partReturnWorkListDao.findAllPrepareDuePartTaskInstanceForParts(removedParts);
        workListItemService.endAllTasksWithTransition(taskinstances, "toEnd");
    }

     public void endPrepareDuePartsAndWpraTasksForParts(List<OEMPartReplaced> removedParts) {
		List<TaskInstance> taskinstances = partReturnWorkListDao.findAllPrepareDuePartAndWpraTaskInstanceForParts(removedParts);
        workListItemService.endAllTasksWithTransition(taskinstances, "toEnd");
    }

    public List<TaskInstance> findClaimedPartReceiptTasks(List<OEMPartReplaced> receivedParts){
         return partReturnWorkListDao.findAllClaimedPartReceiptAndDealerPartShipped(receivedParts);
    }

    public void endClaimedPartReceiptAndDealerPartsShipped(List<TaskInstance> tasksToEnd){
        workListItemService.endAllTasksWithTransition(tasksToEnd, "toEnd");
    }

    public void setWpraService(WpraService wpraService) {
        this.wpraService = wpraService;
    }

   /* public void setSendEmailService(SendEmailService sendEmailService) {
        this.sendEmailService = sendEmailService;
    }

    public void setApplicationSettings(ApplicationSettingsHolder applicationSettings) {
        this.applicationSettings = applicationSettings;
    }*/

    public void endTasksForParts(List<Long> partReturnsIds, String taskName){
        List<TaskInstance> taskinstances = partReturnWorkListDao.findAllPartTaskInstanceForParts(partReturnsIds, taskName);
        workListItemService.endAllTasksWithTransition(taskinstances, "toEnd");
    }

    public void endWPRATasksForParts(List<Long> partReturnsIds, String taskName, String transitionTaken){
        List<TaskInstance> taskinstances = partReturnWorkListDao.findAllPartTaskInstanceForParts(partReturnsIds, taskName);
        List<TaskInstance> newTaskListWithAvailableTransition = new ArrayList<TaskInstance>();
        for(TaskInstance instance : taskinstances){
            List transitions = instance.getAvailableTransitions();
            Assert.notEmpty(transitions, "No Transitions can be taken from [" + instance + "]");
            if(logger.isDebugEnabled())
            {
                logger.debug("Available transitions are [" + transitions + "]");
            }
            boolean transitionAvailable = checkIfTransitionIsAvailable(transitions, transitionTaken);
            //Assert.isTrue(transitionAvailable, "Transition [endWpra] is not one of the available transitions");
            if(logger.isInfoEnabled()) {
                logger.info("Taking the transition [" +transitionTaken +"] on task instance [" + instance + "]");
            }
            if(transitionAvailable) {
                newTaskListWithAvailableTransition.add(instance);
            }
         }
        if(newTaskListWithAvailableTransition.size() > 0)
         workListItemService.endAllTasksWithTransition(newTaskListWithAvailableTransition, transitionTaken);
    }

    private boolean checkIfTransitionIsAvailable(List transitions, String transition) {
        for (Object t : transitions) {
            if (transition.equals(((Transition) t).getName())) {
                return true;
            }
        }
        return false;
    }
    
    public void initiateReturnToDealer(Claim claim, OEMPartReplaced part) {
		//List<OEMPartReplaced> parts = claim.getServiceInformation().getServiceDetail().getReplacedParts();
		    for (PartReturn partReturn : part.getPartReturns()) {
                if(!part.isPartScrapped()){
                    ProcessVariables variables = new ProcessVariables();
                    variables.setVariable("claim", claim);
                    variables.setVariable(PART_RETURN, partReturn);
			        processService.startProcessWithTransition(PARTS_RETURN_PROCESS, variables, "Initiate Return To Dealer By Processor");
             }
        }
	}

    public void startRecoveryPartReturnProcessFromSupplier(List<RecoverablePart> recoverableParts, RecoveryClaim recClaim, Location returnLocation) {
        for (RecoverablePart recoverablePart : recoverableParts) {
                if(recoverablePart.getSupplierPartReturns() != null && !recoverablePart.getSupplierPartReturns().isEmpty()){
                    recoverablePart.setStatus(PartReturnStatus.PARTS_TO_BE_SHIPPED_BY_SUPPLIER_TO_NMHG, PartReturnStatus.PARTS_TO_BE_SHIPPED_BY_SUPPLIER_TO_NMHG.getStatus(),String.valueOf(recoverablePart.getQuantity()));
                    for(int i=0 ; i < recoverablePart.getReceivedFromSupplier();i++){
                        ProcessVariables variables = new ProcessVariables();
                        SupplierPartReturn supplierPartReturn = new SupplierPartReturn();
                        supplierPartReturn.setRecoverablePart(recoverablePart);
                        supplierPartReturn.setOemPartReplaced(recoverablePart.getOemPart());
                        supplierPartReturn.setBasePartReturnStatus(PartReturnStatus.PARTS_TO_BE_SHIPPED_BY_SUPPLIER_TO_NMHG);
                        supplierPartReturn.setReturnLocation(returnLocation);
                        variables.setVariable(RECOVERY_CLAIM, recClaim);
                        variables.setVariable(SUPPLIER_PART_RETURN, supplierPartReturn);
                        processService.startProcessWithTransition("SupplierPartReturn", variables, "NMHG Request Parts Back From Supplier");
                    }
                }else{
                    int count = 0;
                    for(PartReturn partReturn : recoverablePart.getOemPart().getPartReturns()){
                        if(PartReturnStatus.PART_RECEIVED_BY_SUPPLIER.ordinal() <= partReturn.getStatus().ordinal()){
                            ProcessVariables variables = new ProcessVariables();
                            SupplierPartReturn supplierPartReturn = new SupplierPartReturn();
                            supplierPartReturn.setRecoverablePart(recoverablePart);
                            supplierPartReturn.setOemPartReplaced(recoverablePart.getOemPart());
                            supplierPartReturn.setBasePartReturnStatus(PartReturnStatus.PARTS_TO_BE_SHIPPED_BY_SUPPLIER_TO_NMHG);
                            supplierPartReturn.setReturnLocation(returnLocation);
                            variables.setVariable(RECOVERY_CLAIM, recClaim);
                            variables.setVariable(SUPPLIER_PART_RETURN, supplierPartReturn);
                            processService.startProcessWithTransition("SupplierPartReturn", variables, "NMHG Request Parts Back From Supplier");
                            count++;
                        }
                    }
                    recoverablePart.setStatus(PartReturnStatus.PARTS_TO_BE_SHIPPED_BY_SUPPLIER_TO_NMHG, PartReturnStatus.PARTS_TO_BE_SHIPPED_BY_SUPPLIER_TO_NMHG.getStatus(),String.valueOf(count));

                }
        }
    }

    public void initiateReturnToDealer(Claim claim, OEMPartReplaced part, Location location) {
        //List<OEMPartReplaced> parts = claim.getServiceInformation().getServiceDetail().getReplacedParts();
        for (PartReturn partReturn : part.getPartReturns()) {
            if(!part.isPartScrapped()){
                ProcessVariables variables = new ProcessVariables();
                partReturn.setReturnLocation(location);
                variables.setVariable("claim", claim);
                variables.setVariable(PART_RETURN, partReturn);
                processService.startProcessWithTransition(PARTS_RETURN_PROCESS, variables, "Initiate Return To Dealer By Processor");
            }
        }
    }
}

package tavant.twms.web.partsreturn;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;
import org.jbpm.taskmgmt.exe.TaskInstance;
import org.springframework.util.Assert;
import tavant.twms.domain.catalog.Item;
import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.claim.OEMPartReplaced;
import tavant.twms.domain.claim.RecoveryClaim;
import tavant.twms.domain.claim.RecoveryClaimService;
import tavant.twms.domain.configuration.ConfigName;
import tavant.twms.domain.configuration.ConfigOptionConstants;
import tavant.twms.domain.partreturn.*;
import tavant.twms.domain.supplier.ItemMapping;
import tavant.twms.domain.supplier.ItemMappingService;
import tavant.twms.domain.supplier.SupplierPartReturn;
import tavant.twms.domain.supplier.recovery.RecoverablePart;
import tavant.twms.infra.BeanProvider;
import tavant.twms.infra.DomainRepository;
import tavant.twms.jbpm.WorkflowConstants;
import tavant.twms.jbpm.infra.CustomTaskInstance;
import tavant.twms.process.PartReturnProcessingService;
import tavant.twms.process.PartTaskBean;
import tavant.twms.web.inbox.DefaultPropertyResolver;
import tavant.twms.web.xforms.TaskViewService;
import tavant.twms.worklist.WorkListCriteria;
import tavant.twms.worklist.partreturn.PartReturnWorkList;
import tavant.twms.worklist.partreturn.PartReturnWorkListItemService;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: deepak.patel
 * Date: 14/1/13
 * Time: 3:52 PM
 * To change this template use File | Settings | File Templates.
 */
public class SupplierPartReceiptInboxAction extends PartReturnInboxAction implements ConfigOptionConstants {

    private static final Logger logger = Logger
            .getLogger(SupplierPartReceiptInboxAction.class);

    public SupplierPartReceiptInboxAction() {
        setActionUrl("supplierPartReceiptFromDealer");
    }

    private List<String> transitions = new ArrayList<String>();

    private TaskViewService taskViewService;

    private Shipment shipment;

    Map<Long, OEMPartReplaced> distinctOemParts = new HashMap<Long, OEMPartReplaced>();

    private PartReturnProcessingService partReturnProcessingService;
    
    private RecoveryClaimService recoveryClaimService;
    
    public RecoveryClaimService getRecoveryClaimService() {
		return recoveryClaimService;
	}

	public void setRecoveryClaimService(RecoveryClaimService recoveryClaimService) {
		this.recoveryClaimService = recoveryClaimService;
	}

	public PartReturnProcessingService getPartReturnProcessingService() {
        return partReturnProcessingService;
    }

    public void setPartReturnProcessingService(PartReturnProcessingService partReturnProcessingService) {
        this.partReturnProcessingService = partReturnProcessingService;
    }

    private ItemMappingService itemMappingService;
    public boolean isPageReadOnly() {
        return false;
    }
    
    @Override
	public BeanProvider getBeanProvider() {
    	return new DefaultPropertyResolver();
    }

    public String partReceipt(){
        this.shipment = (Shipment) this.domainRepository.load(Shipment.class, new Long(getId()));
        for (PartReturn partReturn: this.shipment.getParts()) {
            OEMPartReplaced oemPartReplaced = partReturn.getOemPartReplaced();
            if(distinctOemParts.get(oemPartReplaced.getId()) == null){
                distinctOemParts.put(oemPartReplaced.getId(),oemPartReplaced);
            }
        }
        logger.log(Priority.INFO,"Supplier is taking action on received parts");
        int count = 0;
        for(OEMPartReplaced oemPartReplaced: distinctOemParts.values()) {
            logger.log(Priority.INFO,"Taking action on oem part id : "+oemPartReplaced.getId());
            Set<Long> partReturnsIds = new HashSet<Long>();
            for(PartReturn partReturn : oemPartReplaced.getPartReturns()) {
                logger.log(Priority.INFO,"Taking action on part return "+partReturn.getId());
                TaskInstance taskInstance = getPartReturnWorkListItemService().findAllTasksForShipmentForPartReturn(partReturn, getTaskName());
                if(taskInstance != null){
                    getWorkListItemService().endTaskWithTransition(taskInstance, transitions.get(count));
                    partReturnsIds.add(partReturn.getId());
                }
            }
            //End Part Shipped instance
            if(partReturnsIds.size() > 0){
                partReturnProcessingService.endWPRATasksForParts(new ArrayList<Long>(partReturnsIds),WorkflowConstants.PARTS_SHIPPED,"toEnd");

                //Update part return status
                if(transitions.get(count).equals(WorkflowConstants.RECEIVED)){
                    //oemPartReplaced.setStatus(PartReturnStatus.PART_RECEIVED_BY_SUPPLIER);
                    oemPartReplaced.setPartAction1(new PartReturnAction(PartReturnStatus.PART_RECEIVED_BY_SUPPLIER.getStatus(),oemPartReplaced.getNumberOfUnits()));
                        //End wpra instance
                    partReturnProcessingService.endWPRATasksForParts(new ArrayList<Long>(partReturnsIds),WorkflowConstants.GENERATED_WPRA, "endWpra");
                }
                else if(transitions.get(count).equals(WorkflowConstants.NOT_RECEIVED)){
                    //oemPartReplaced.setStatus(PartReturnStatus.PART_TO_BE_SHIPPED);
                    oemPartReplaced.setPartAction1(new PartReturnAction(PartReturnStatus.PART_NOT_RECEIVED_BY_SUPPLIER.getStatus(),oemPartReplaced.getNumberOfUnits()));
                }
                logger.log(Priority.INFO,"Update oem part with id : "+oemPartReplaced.getId());
                updatePartStatus(oemPartReplaced);
                getPartReplacedService().updateOEMPartReplaced(oemPartReplaced);
                count++;
            }

        }
        //ok,now update the prc configuration
        updatePartReceivedCount(distinctOemParts.values());
        addActionMessage("message.itemStatus.updated");
       // return resultingView();
        return SUCCESS;
    }

    /*@Override
    public void prepare() throws Exception{
        super.prepare();
        logger.log(Priority.INFO,"Preparing the distinct oem replaced parts");
        Assert.hasText(getId(), "Id should not be empty for fetch");
        this.shipment = (Shipment) this.domainRepository.load(Shipment.class, new Long(getId()));
        for (PartReturn partReturn: this.shipment.getParts()) {
            OEMPartReplaced oemPartReplaced = partReturn.getOemPartReplaced();
            if(distinctOemParts.get(oemPartReplaced.getId()) == null){
                distinctOemParts.put(oemPartReplaced.getId(),oemPartReplaced);
            }
        }
        logger.log(Priority.INFO,"Found "+distinctOemParts.size() +" distinct oem replaced parts" );
    }
*/
    private void updatePartReceivedCount(Collection<OEMPartReplaced> oemPartsReplaced) {
        List<OEMPartReplaced> oemParts = new ArrayList<OEMPartReplaced>();
        oemParts.addAll(oemPartsReplaced);
        if (ON_PART_RECIEVED.equalsIgnoreCase(getConfigParamService().getStringValue(
                ConfigName.PART_RETURN_STATUS_TO_BE_CONSIDERED_FOR_PRC_MAX_QTY.getName()))) {
            for (OEMPartReplaced oemPartReplaced : oemParts) {
                if (oemPartReplaced.getItemReference() != null) {
                    PartReturnConfiguration partReturnConfiguration = oemPartReplaced.getPartReturnConfiguration();
                    if (partReturnConfiguration != null
                            && oemPartReplaced.getPartReturns() != null
                            && !oemPartReplaced.getPartReturns().isEmpty()
                            && partReturnConfiguration.getMaxQuantity() != null) {

                        partReturnConfiguration
                                .setQuantityReceived(partReturnConfiguration
                                        .getQuantityReceived()
                                        + oemPartReplaced.getNumberOfUnits());

                        getPartReturnService().updatePartReturnConfiguration(
                                partReturnConfiguration);
                    }
                }
            }
        }
    }

    // This is only for the parts receipt preview. Need a better way ?
    public Shipment getShipmentFromPartBeans() {
        return getClaimWithPartBeans().get(0).getPartReplacedBeans().get(0).getPartTaskBean().getPartReturn()
                .getShipment();
    }

    public Item getSupplierItemDescription(String oemPartReplaced){
        OEMPartReplaced part = getPartReplacedService().findOEMPartReplacedById(Long.valueOf(oemPartReplaced));
        ItemMapping mapping = itemMappingService.findItemMappingForOEMItem(part.getItemReference().getReferredItem(), part.getAppliedContract().getSupplier(), null);
        return mapping.getToItem();
    }



   /* @Override
    public void validate(){

        logger.log(Priority.INFO,"Found "+distinctOemParts.size() +" distinct oem replaced parts" );
        logger.log(Priority.INFO,"Validating the ui selection");
        if(this.transitions.size()==0 && this.transitions.size() <distinctOemParts.size()){
            addActionError("error.dealer.partReturnToSupplier.noActionSelected");
        }
        for(String tran : transitions){
            if(tran == null ||(tran != null && StringUtils.isEmpty(tran))){
                addActionError("error.dealer.partReturnToSupplier.noActionSelected");
            }
        }
        resultingView();
    }
*/
    public void validate(){
        if(transitions.contains("")||transitions.contains(null))
            addActionError("error.supplierParReceipt.noActionTaken");
        if (hasActionErrors()) {
            generateView();
        }
    }

    public void setTaskViewService(TaskViewService taskViewService) {
        this.taskViewService = taskViewService;
    }

    public void setTransitions(List<String> transitions) {
        this.transitions = transitions;
    }

    public List<String> getTransitions() {
       return transitions;
    }

    public void setShipment(Shipment shipment) {
        this.shipment = shipment;
    }

    public Shipment getShipment() {
        return shipment;
    }

    private DomainRepository domainRepository;

    public void setDomainRepository(DomainRepository domainRepository) {
        this.domainRepository = domainRepository;
    }

    public ItemMappingService getItemMappingService() {
        return itemMappingService;
    }

    public void setItemMappingService(ItemMappingService itemMappingService) {
        this.itemMappingService = itemMappingService;
    }
    
    public String getRecClaim(){
    	String recClaim = null;
    	 this.shipment = (Shipment) this.domainRepository.load(Shipment.class, new Long(getId()));
    	 for (PartReturn partReturn: this.shipment.getParts()) {
    		 recClaim = recoveryClaimService.findRecClaimFromPartReturn(partReturn.getId());
    	 }
    	 return recClaim;
	}
}

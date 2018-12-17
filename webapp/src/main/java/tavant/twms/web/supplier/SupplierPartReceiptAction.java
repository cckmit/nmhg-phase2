/*
 *   Copyright (c) 2007 Tavant Technologies
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
package tavant.twms.web.supplier;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.jbpm.taskmgmt.exe.TaskInstance;
import org.springframework.util.Assert;

import tavant.twms.domain.claim.OEMPartReplaced;
import tavant.twms.domain.claim.PartReplacedService;
import tavant.twms.domain.claim.RecoveryClaim;
import tavant.twms.domain.claim.RecoveryClaimRepository;
import tavant.twms.domain.partreturn.Shipment;
import tavant.twms.domain.supplier.SupplierPartReturn;
import tavant.twms.infra.DomainRepository;
import tavant.twms.infra.PageResult;
import tavant.twms.infra.PageSpecification;
import tavant.twms.jbpm.WorkflowConstants;
import tavant.twms.jbpm.infra.CustomTaskInstance;
import tavant.twms.worklist.InboxItemList;
import tavant.twms.worklist.WorkListCriteria;
import tavant.twms.worklist.WorkListItemService;
import tavant.twms.worklist.WorkListService;
import tavant.twms.worklist.supplier.SupplierRecoveryWorkListDao;

/**
 * @author pradipta.a
 */
@SuppressWarnings("serial")
public class SupplierPartReceiptAction extends AbstractSupplierActionSupport {

    private final Logger logger = Logger.getLogger(SupplierPartReceiptAction.class);

    private DomainRepository domainRepository;

    private Shipment shipment;

    private PartReplacedService partReplacedService;

    private WorkListItemService workListItemService;
    
    private WorkListService workListService;
    
    //private SupplierRecoveryWorkListDao supplierRecoveryWorkListDao;

    private List<OEMPartReplaced> partsInShipment = new ArrayList<OEMPartReplaced>();

    private List<String> transitions = new ArrayList<String>();
    
    private String recClaimNo = null;
    
    private String dealerName = null;
    
    private RecoveryClaimRepository recoveryClaimRepository;
    
	public String preview() {
        fetchShipmentView();
        fetchPartsInShipment();
        this.setRecClaimNo(getRecClaim().getRecoveryClaimNumber());
        this.setDealerName(getRecClaim().getClaim().getForDealer().getName());
        return SUCCESS;
    }
    
    public void validate(){
    	if(transitions.contains("")||transitions.contains(null))
    		addActionError("error.supplierParReceipt.noActionTaken");
    }

    public String submitPreview() {
        fetchShipmentView();
        updateAndPerformTransition();
        addActionMessage("message.supplier.response");
        return SUCCESS;
    }

    private void fetchShipmentView() {
        Assert.hasText(getId(), "Id should not be empty for fetch");
        // Get the shipment from the repository
        this.shipment = (Shipment) this.domainRepository.load(Shipment.class, new Long(getId()));
    }

    private void updateAndPerformTransition() {
    	int count = 0;
        for (SupplierPartReturn supplierPartReturn: this.shipment.returnWithUniquePart()) {
            List<TaskInstance> taskInstances = getSupplierRecoveryWorkListDao().findAllAwaitedTasks(supplierPartReturn.getRecoverablePart() , this.getTaskName());
            if(this.transitions.get(count).equalsIgnoreCase("Not Received")){
                supplierPartReturn.setSupplierComment(getText("message.supplier.not.received"));
            }
            this.workListItemService.endAllTasksWithTransition(taskInstances, this.transitions.get(count));
            count++;
        }

    }

    private void fetchPartsInShipment() {
        Assert.notNull(this.shipment, "The claim should be fetched before initialising suppliers");
        for(SupplierPartReturn supplierPartReturn : this.shipment.returnWithUniquePart())
        {
        	this.partsInShipment.add(supplierPartReturn.getRecoverablePart().getOemPart());
        }
    }

    @Override
    protected InboxItemList getInboxItemList(WorkListCriteria criteria) {
        return this.workListService.getSupplierPartReceiptView(criteria);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected PageResult<?> getPageResult(List inboxItems, PageSpecification pageSpecification, int noOfPages) {
        return new PageResult<OEMPartReplaced>(inboxItems, pageSpecification, noOfPages);
    }

    public Shipment getShipment() {
        return this.shipment;
    }

    public void setShipment(Shipment shipment) {
        this.shipment = shipment;
    }

    public List<String> getTransitions() {
        return this.transitions;
    }

    public void setTransitions(List<String> transitions) {
        this.transitions = transitions;
    }

    public void setWorkListItemService(WorkListItemService workListItemService) {
        this.workListItemService = workListItemService;
    }

    @Override
    public void setWorkListService(WorkListService workListService) {
        this.workListService = workListService;
    }

    public List<OEMPartReplaced> getPartsInShipment() {
        return this.partsInShipment;
    }

    public void setPartsInShipment(List<OEMPartReplaced> partsInShipment) {
        this.partsInShipment = partsInShipment;
    }

    public void setDomainRepository(DomainRepository domainRepository) {
        this.domainRepository = domainRepository;
    }

	/*public SupplierRecoveryWorkListDao getSupplierRecoveryWorkListDao() {
		return supplierRecoveryWorkListDao;
	}

	public void setSupplierRecoveryWorkListDao(SupplierRecoveryWorkListDao supplierRecoveryWorkListDao) {
		this.supplierRecoveryWorkListDao = supplierRecoveryWorkListDao;
	}*/
	
	 public String getRecClaimNo() {
			return recClaimNo;
		}

	public void setRecClaimNo(String recClaimNo) {
			this.recClaimNo = recClaimNo;
		}

	public String getDealerName() {
			return dealerName;
		}

	public void setDealerName(String dealerName) {
			this.dealerName = dealerName;
		}
	
	public RecoveryClaimRepository getRecoveryClaimRepository() {
		return recoveryClaimRepository;
	}

	public void setRecoveryClaimRepository(
			RecoveryClaimRepository recoveryClaimRepository) {
		this.recoveryClaimRepository = recoveryClaimRepository;
	}

	public RecoveryClaim getRecClaim(){
		Long id = null;
		  for (SupplierPartReturn supplierPartReturn: this.shipment.getSupplierPartReturns()) {
	            CustomTaskInstance taskInstance = (CustomTaskInstance) getSupplierRecoveryWorkListDao().getTaskForSupplierPartReturn(supplierPartReturn , this.getTaskName());
	            id = taskInstance.getClaimId();
		  }
		  RecoveryClaim recoveryClaim = this.recoveryClaimRepository.find(id);
		  return recoveryClaim;
	}
}

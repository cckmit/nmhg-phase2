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

import org.apache.commons.lang.math.NumberUtils;
import org.jbpm.taskmgmt.exe.TaskInstance;
import org.springframework.util.Assert;

import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.claim.OEMPartReplaced;
import tavant.twms.domain.claim.RecoveryClaim;
import tavant.twms.domain.partreturn.Location;
import tavant.twms.domain.partreturn.PartReturn;
import tavant.twms.domain.partreturn.PartReturnService;
import tavant.twms.domain.supplier.SupplierPartReturn;
import tavant.twms.domain.supplier.recovery.RecoverablePart;
import tavant.twms.infra.DomainRepository;
import tavant.twms.infra.PageResult;
import tavant.twms.infra.PageSpecification;
import tavant.twms.process.PartReturnProcessingService;
import tavant.twms.process.PartTaskBean;
import tavant.twms.worklist.InboxItemList;
import tavant.twms.worklist.WorkListCriteria;
import tavant.twms.worklist.WorkListItemService;
import tavant.twms.worklist.supplier.SupplierRecoveryWorkListDao;

/**
 * @author pradipta.a
 */
@SuppressWarnings("serial")
public class PartShipperGenerateDuePartAction extends
		AbstractSupplierActionSupport {

	private List<TaskInstance> taskInstances = new ArrayList<TaskInstance>();

	//private SupplierRecoveryWorkListDao supplierRecoveryWorkListDao;

	private List<PartTaskBean> partTaskBeans = new ArrayList<PartTaskBean>();

	private PartReturnService partReturnService;

	private List<Location> locations = new ArrayList<Location>();
	
	private List<RecoverablePart> recoveredPartsToBeShipped = new ArrayList<RecoverablePart>();

	private PartReturnProcessingService partReturnProcessingService;

	private WorkListItemService workListItemService;
	
	private DomainRepository domainRepository;

	private Claim claim;

	private List<String> dueDays;
	
	@Override
    protected String getAlias() {
    	return "recoveryClaim";
    }

	@Override
	protected InboxItemList getInboxItemList(WorkListCriteria criteria) {
		return getSupplierRecoveryWorkListDao().getDuePartClaimList(criteria);
	}
	
	
	public String preview() {
		Assert.hasText(getId(), "Id should not be empty for fetch");
		recoveredPartsToBeShipped = getSupplierRecoveryWorkListDao().getRecoveredPartsForPreview(new Long(getId()), getLoggedInUser().getName(),this.getTaskName());
		this.setRecoveryClaim((RecoveryClaim) this.domainRepository.load(RecoveryClaim.class, new Long(getId())));
		claim=this.getRecoveryClaim().getClaim();
		return SUCCESS;
	}

	private void populatePartTaskBeans() {
		for (TaskInstance partTask : taskInstances) {
			PartTaskBean partTaskBean = new PartTaskBean(partTask);
			partTaskBeans.add(partTaskBean);
		}

	}

	@Override
	public void validate() {
		if (recoveredPartsToBeShipped.isEmpty()) {
			addActionError("error.partShipper.NoPartSelected");
			recoveredPartsToBeShipped = getSupplierRecoveryWorkListDao().getRecoveredPartsForPreview(new Long(getId()), getLoggedInUser().getName(),this.getTaskName());
		}
		for(Location location : locations){
			if (location.getId()== null || location.getCode().length() == 0) {
				addActionError("error.partShipper.NoLocation");
				break;
			}
		}
		for(String dueDate : dueDays){
			if (dueDate.length() == 0) {
				addActionError("error.partShipper.NoDueDays");
				break;
			
			} else {
				try {
					
					if(Integer.parseInt(dueDate)<0){
						addActionError("error.partShipper.inValidDueDays");
						break;	
					}else{
						Integer.parseInt(dueDate);	
					}
				} catch (Exception e) {
					addActionError("error.partShipper.inValidDueDays");
					break;
				}
			}
		}
		
		Assert.hasText(getId(), "Id should not be empty for fetch");
		taskInstances = getTaskInstancesForShipper(getId());
		populatePartTaskBeans();
		if(this.getRecoveryClaim() == null){
			this.setRecoveryClaim(partTaskBeans.iterator().next().getRecoveryClaim());
		}
		claim = partTaskBeans.iterator().next().getRecoveryClaim().getClaim();
	}

	public String submit() {
		int count = 0;
		boolean otherRecoveryClaimTasksFound = false;
		for (RecoverablePart recoverablePart : this.getRecoveredPartsToBeShipped()) {
			OEMPartReplaced supplierPart = recoverablePart.getOemPart();
			supplierPart.setPartToBeReturned(recoverablePart.isSupplierReturnNeeded());
			supplierPart.setPartReturnInitiatedBySupplier(true);
			PartReturn partReturn = new PartReturn();
			partReturn.setReturnedBy(claim.getForDealer());
			partReturn.setReturnLocation(locations.get(count));
			partReturn.setDueDays(Integer.parseInt(dueDays.get(count)));
			partReturn.setOemPartReplaced(supplierPart);
			supplierPart.setPartReturn(partReturn);
			partReturnService.updateExistingPartReturns(supplierPart, claim);
			partReturnProcessingService.startPartReturnProcessForPart(claim, supplierPart);
			List<TaskInstance> taskInstancesTobeEndedWithTransition = new ArrayList<TaskInstance>();
			for (RecoverablePart recoverablePart2 : recoverablePart.getOemPart().getRecoverableParts()) {
				for (SupplierPartReturn supplierPartReturn : recoverablePart2.getSupplierPartReturns()) {
					TaskInstance taskInstance = getSupplierRecoveryWorkListDao().getTaskForSupplierPartReturn(supplierPartReturn, this
							.getTaskName());
					if (taskInstance != null) {
						if (!recoverablePart2.equals(recoverablePart))
							otherRecoveryClaimTasksFound = true;
						taskInstancesTobeEndedWithTransition.add(taskInstance);
					}
				}
			}
			workListItemService.endAllTasksWithTransition(taskInstancesTobeEndedWithTransition, "Submit");
			count++;
		}
		addActionMessage("message.partShipper.successfulMessage");
		if (otherRecoveryClaimTasksFound)
			addActionMessage("message.partShipper.partsAskedForReturnFromDealerForOtherRecoverClaimsAlso");
		return SUCCESS;
	}

	@Override
	protected PageResult<?> getPageResult(List inboxItems,
			PageSpecification pageSpecification, int noOfPages) {
		return new PageResult<RecoveryClaim>(inboxItems, pageSpecification,
				noOfPages);
	}

	public List<TaskInstance> getTaskInstances() {
		return taskInstances;
	}

	public void setTaskInstances(List<TaskInstance> taskInstances) {
		this.taskInstances = taskInstances;
	}

/*	public SupplierRecoveryWorkListDao getSupplierRecoveryWorkListDao() {
		return supplierRecoveryWorkListDao;
	}

	public void setSupplierRecoveryWorkListDao(
			SupplierRecoveryWorkListDao supplierRecoveryWorkListDao) {
		this.supplierRecoveryWorkListDao = supplierRecoveryWorkListDao;
	}*/

	public List<PartTaskBean> getPartTaskBeans() {
		return partTaskBeans;
	}

	public void setPartTaskBeans(List<PartTaskBean> partTaskBeans) {
		this.partTaskBeans = partTaskBeans;
	}

	public Claim getClaim() {
		return claim;
	}

	public void setClaim(Claim claim) {
		this.claim = claim;
	}

	public void setPartReturnService(PartReturnService partReturnService) {
		this.partReturnService = partReturnService;
	}

	public void setPartReturnProcessingService(
			PartReturnProcessingService partReturnProcessingService) {
		this.partReturnProcessingService = partReturnProcessingService;
	}

	public void setWorkListItemService(WorkListItemService workListItemService) {
		this.workListItemService = workListItemService;
	}

	public void setLocations(List<Location> locations) {
		this.locations = locations;
	}
	
	public List<Location> getLocations() {
		return locations;
	}
	
	public List<String> getDueDays() {
		return dueDays;
	}
	
	public void setDueDays(List<String> dueDays) {
		this.dueDays = dueDays;
	}

	public List<RecoverablePart> getRecoveredPartsToBeShipped() {
		return recoveredPartsToBeShipped;
	}

	public void setRecoveredPartsToBeShipped(List<RecoverablePart> recoveredPartsToBeShipped) {
		this.recoveredPartsToBeShipped = recoveredPartsToBeShipped;
	}

	public DomainRepository getDomainRepository() {
		return domainRepository;
	}

	public void setDomainRepository(DomainRepository domainRepository) {
		this.domainRepository = domainRepository;
	}

}

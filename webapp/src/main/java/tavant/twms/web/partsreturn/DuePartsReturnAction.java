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

import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.jbpm.taskmgmt.exe.TaskInstance;
import org.springframework.util.StringUtils;

import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.claim.ClaimAudit;
import tavant.twms.domain.claim.OEMPartReplaced;
import tavant.twms.domain.claim.RecoveryClaim;
import tavant.twms.domain.claim.RecoveryClaimAudit;
import tavant.twms.domain.orgmodel.Role;
import tavant.twms.domain.partreturn.PartReturnAction;
import tavant.twms.domain.partreturn.PartReturnStatus;
import tavant.twms.domain.partreturn.PartReturnTaskTriggerStatus;
import tavant.twms.domain.partreturn.Shipment;
import tavant.twms.domain.partreturn.ShipmentStatus;
import tavant.twms.domain.supplier.recovery.RecoverablePart;
import tavant.twms.process.PartReturnProcessingService;
import tavant.twms.process.PartTaskBean;
import tavant.twms.worklist.WorkListCriteria;
import tavant.twms.worklist.partreturn.PartReturnWorkList;

public class DuePartsReturnAction extends PartReturnInboxAction {

	private static final Logger logger = Logger
			.getLogger(DuePartsReturnAction.class);

	private PartReturnProcessingService partReturnProcessingService;

    private String comments;

    public static final String DUE_PARTS_TASK_NAME = "Due Parts";
    
    public static final String DUE_PARTS_WPRA_TASK_NAME = "Due Parts WPRA";   

	public static final String THIRD_PARTY_DUE_PARTS_TASK_NAME = "Third Party Due Parts";
	

	public DuePartsReturnAction() {
		// TODO : Check if this is required.
		setActionUrl("dueParts");
	}

	@Override
	public void validate() {
		super.validate();
		validateData();
		if (hasActionErrors()) {
			generateView();
			setUserSpecifiedQuantity();
		}
	}
	
	

	private void validateData() {
		if (!hasActionErrors()) {
			for (OEMPartReplacedBean partReplacedBean : getPartReplacedBeans()) {
				if (partReplacedBean.isSelected()) {
					if (partReplacedBean.getCannotShip() == 0 && partReplacedBean.getShip() == 0) {
						addActionError("error.partReturnConfiguration.noPartSelected");
					} else if (partReplacedBean.getCannotShip() + partReplacedBean.getShip() > partReplacedBean
							.getToBeShipped()) {
						addActionError("error.partReturnConfiguration.excessPartsShipmentGenerate");
					}
				}
			}
			for (OEMPartReplacedBean partReplacedBean : getPartReplacedBeans()) {
				if (partReplacedBean.getCannotShip() > 0 && !StringUtils.hasText(getComments())) {
					addActionError("error.manageFleetCoverage.commentsMandatory");
					break;
				}
			}
		}
	}
            
      
	
	
	protected void setUserSpecifiedQuantity() {
		for (ClaimWithPartBeans claimWithPartBeans : getClaimWithPartBeans()) {
			for (OEMPartReplacedBean partReplacedBean : claimWithPartBeans.getPartReplacedBeans()) {
				for (OEMPartReplacedBean uiPartReplacedBean : this.getPartReplacedBeans()) {
					if (partReplacedBean.getPartReplacedId() == uiPartReplacedBean.getPartReplacedId()) {
						partReplacedBean.setSelected(uiPartReplacedBean.isSelected());
						partReplacedBean.setShip(uiPartReplacedBean.getShip());
						partReplacedBean.setCannotShip(uiPartReplacedBean.getCannotShip());
					}
				}
			}
		}
	}
		
	public String submitPreview() throws Exception {
        String shipmentNumber=null;
        List<PartTaskBean> selectedPartTasks = getSelectedPartTaskBeans();
		if (!getBeansForShipmentGeneration(selectedPartTasks).isEmpty()) {
			Shipment shipment = partReturnProcessingService.createShipment(
					getBeansForShipmentGeneration(selectedPartTasks),
					transitionTaken);
			setShipmentIdString(shipment.getId().toString());
            shipmentNumber = shipment.getId().toString();
        }
		List<PartTaskBean> partTasksToEnd = getBeansToBeEnded(selectedPartTasks);
		for (PartTaskBean partTaskBean : partTasksToEnd) {
			getWorkListItemService().endTaskWithTransition(
					partTaskBean.getTask(), "toEnd");
			partTaskBean.getPartReturn().setTriggerStatus(
					PartReturnTaskTriggerStatus.ENDED);
		}
		processForOnHoldForPartReturnInboxFlow(selectedPartTasks.get(0));
		for (OEMPartReplacedBean partReplacedBean : getPartReplacedBeans()) {
			OEMPartReplaced part = partReplacedBean.partReturnTasks.get(0)
				.getPart();
			if (partReplacedBean.isSelected()){
				part.setPartAction1(new PartReturnAction(
						PartReturnStatus.SHIPMENT_GENERATED.getStatus(),
						partReplacedBean.getCountOfShip()));
                part.setPartAction2(new PartReturnAction(
						PartReturnStatus.CANNOT_BE_SHIPPED.getStatus(),
						partReplacedBean.getCountOfCannotShip()));
			 	part.getPartAction1().setShipmentId(shipmentNumber);
                part.setComments(getComments());
                updatePartStatus(part);
				getPartReplacedService().updateOEMPartReplaced(part);
			 }
		  }
		return resultingView();
	}

	@Override
	protected PartReturnWorkList getWorkList() {
		if (showWPRA()) {
			if(isLoggedInUserADealer()){
				return this.getPartReturnWorkListService()
						.getPartReturnWorkListForWpraByDealership(createCriteria());	
			}
			else{
			return this.getPartReturnWorkListService()
					.getPartReturnWorkListForWpraByActorId(createCriteria());
			}
		} else {
			return this.getPartReturnWorkListService()
					.getPartReturnWorkListByLocation(createCriteria());
		}
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

	@Override
	protected List<TaskInstance> findAllPartTasksForId(String id) {
		logger.debug("Find Part Tasks for Location[" + id + "]");
		WorkListCriteria criteria = createCriteria();
		criteria.setIdentifier(id);
		if(showWPRA()){
			return getPartReturnWorkListItemService().findAllTasksForWPRA(
					criteria);	
		}
		else
		{
		return getPartReturnWorkListItemService().findAllTasksForLocation(
				criteria);
		}
	}

	public boolean getIsDuePartsTask() {
        if ( DUE_PARTS_TASK_NAME.equals(getTaskName()) || 
        		THIRD_PARTY_DUE_PARTS_TASK_NAME.equalsIgnoreCase(getTaskName()))
            return true;
        else
            return false;
    }

	public void setPartReturnProcessingService(
			PartReturnProcessingService partReturnProcessingService) {
		this.partReturnProcessingService = partReturnProcessingService;
	}


    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
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

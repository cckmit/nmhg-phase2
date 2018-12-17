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

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.jbpm.taskmgmt.exe.TaskInstance;
import org.springframework.transaction.annotation.Transactional;

import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.claim.OEMPartReplaced;
import tavant.twms.domain.claim.RecoveryClaim;
import tavant.twms.domain.orgmodel.User;
import tavant.twms.domain.partreturn.Location;
import tavant.twms.domain.partreturn.PartReturn;
import tavant.twms.domain.partreturn.Shipment;
import tavant.twms.domain.common.RejectionReason;
import tavant.twms.domain.partreturn.Wpra;
import tavant.twms.domain.supplier.recovery.RecoverablePart;
import tavant.twms.domain.supplier.recovery.RecoveryInfo;

@Transactional(readOnly = false)
public interface PartReturnProcessingService {

	public void startPartReturnProcessForAllParts(Claim claim);

	/**
	 * @param transitionTaken
	 * @return Shipment
	 */
	Shipment createShipment(List<PartTaskBean> partTasks, String transitionTaken);

	/**
	 * @param partBeansByLocation
	 * @param transitionTaken
	 * @return List of Shipments
	 */
	List<Shipment> createShipmentsByLocation(
			Collection<List<PartTaskBean>> partBeansByLocation,
			String transitionTaken);

	void removePartsFromItsShipment(List<PartReturn> parts,
			List<TaskInstance> taskInstances, String transitionTaken);

	void addPartsToShipment(Long shipmentId, List<PartReturn> parts,
			List<TaskInstance> taskInstances, String transitionTaken);

	public void startPartReturnProcess(Claim claim, PartReturn partReturn);

	public void startPartReturnProcessForPart(Claim claim, OEMPartReplaced part);
	
	

    public void startRecoveryProcess(RecoveryInfo recoveryInfo);

    public void startRecoveryPartReturnProcess(RecoverablePart recoverablePart,
			RecoveryClaim recClaim);

	public void endRecoveryPartReturnProcess(OEMPartReplaced partReplaced);

	public void endPartReturnNotGenerated(Claim claim);

	public void endPartReturnNotIntiatedBySupplier(RecoveryClaim claim);
	
    public void endAllPartTasksForClaim(Claim claim);
    
    public void focClaimsPendingSubmission() ;

    public Map<String, RejectionReason> getDefaultRejectionReason(String defaultReasonToUse);

    public void endTasksForParts(List<OEMPartReplaced> removedParts);

    public void autoStartRecoveryProcess(Claim claim);
    
    public void initiateRecoveryProcess(RecoveryInfo recoveryInfo);

    //Added for Dealer part back request
    public void initiateDealerRequestedPart(List<PartTaskBean> partTasks, String transitionTaken);

    public void endAllDealerRequestPartReturnTaskForClaim(Long claimId);

    public List<Wpra> movePartToDuePartInbox(Collection<List<PartTaskBean>> partBeansByLocation, String transitionTaken);

    public void endPrepareDuePartsTasksForParts(List<OEMPartReplaced> removedParts);

    public void endPrepareDuePartsAndWpraTasksForParts(List<OEMPartReplaced> removedParts);

    public List<TaskInstance> findClaimedPartReceiptTasks(List<OEMPartReplaced> receivedParts);

    public void endClaimedPartReceiptAndDealerPartsShipped(List<TaskInstance> tasksToEnd);

    public void endTasksForParts(List<Long> partReturnsIds, String taskName);

    public void endWPRATasksForParts(List<Long> partReturnsIds, String taskName, String transitionTaken);
    
    public void initiateReturnToDealer(Claim claim, OEMPartReplaced part);

    public void initiateReturnToDealer(Claim claim, OEMPartReplaced part, Location location);

    public void startRecoveryPartReturnProcessFromSupplier(List<RecoverablePart> recoverableParts, RecoveryClaim recClaim, Location returnLocation) ;
    
    public void startRecoveryRoutingProcess(RecoverablePart recoverablePart, RecoveryClaim recClaim);

    public void startRecoveryPartReturnProcessForRecPart(RecoverablePart recoverablePart, RecoveryClaim recoveryClaim, Location retLocation);

}

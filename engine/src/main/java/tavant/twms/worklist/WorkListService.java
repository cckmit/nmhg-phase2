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
package tavant.twms.worklist;

import java.util.List;
import java.util.Map;

import org.jbpm.taskmgmt.def.Task;
import org.jbpm.taskmgmt.exe.TaskInstance;

import tavant.twms.domain.orgmodel.User;
import tavant.twms.domain.partreturn.PartReturnInboxOrder;
import tavant.twms.domain.partreturn.Shipment;


/**
 * 
 * @author kannan.ekanath
 *
 */
public interface WorkListService extends BaseWorkListService {

    public Map<Task, Long> getAllTasks(WorkListCriteria criteria,User loggedInUser);
    
    public WorkList getWorkList(WorkListCriteria criteria);
    
    public InboxItemList getSupplierRecoveryClaimBasedView(WorkListCriteria criteria);
    
    public InboxItemList getSupplierRecoveryPartBasedView(WorkListCriteria criteria);
    
    public InboxItemList getSupplierShipmentBasedView(WorkListCriteria criteria);
    
    public InboxItemList getSupplierRecoveryLocationBasedView(WorkListCriteria criteria);
    
    public InboxItemList getSupplierRecoverySupplierPartReturnBasedView(WorkListCriteria criteria);
    
    public List<TaskInstance> getPreviewPaneForSupplierLocation(Long locationId, String taskName, String actorId);
    
    public List<TaskInstance> getPreviewPaneForSupplierShipment(Long shipmentId, String taskName, String actorId);
    
    public InboxItemList getSupplierPartReceiptView(WorkListCriteria criteria);
    
	public User getCurrentAssigneeForRecClaim(Long recClaimId);

	public List<String> getPartReturnInboxOrders();

    public InboxItemList getPartShipperRecoveryClaimView(WorkListCriteria criteria);

    public List<Shipment> getAllShipmentsForRecoveryClaim(Long recClaimId, String taskName);

    public List<TaskInstance> getAllTaskInstancesForRecoveryClaim(Long recClaimID, String taskName);

    public InboxItemList getPartReceiverReceiptView(WorkListCriteria criteria);
    
    public InboxItemList getPartInspectorInspectView(WorkListCriteria criteria);

    public InboxItemList getSupplierRecoveryDistinctRecoveryClaimList(WorkListCriteria criteria);
}    


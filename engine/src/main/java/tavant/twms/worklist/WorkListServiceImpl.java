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
import org.springframework.beans.factory.annotation.Required;

import tavant.twms.domain.orgmodel.User;
import tavant.twms.domain.partreturn.PartReturnInboxOrder;
import tavant.twms.domain.partreturn.Shipment;
import tavant.twms.worklist.supplier.SupplierRecoveryWorkListDao;


public class WorkListServiceImpl extends BaseWorkListServiceImpl implements WorkListService {

    private WorkListDao workListDao;

    private SupplierRecoveryWorkListDao supplierRecoveryWorkListDao;
    
    public WorkList getWorkList(WorkListCriteria criteria) {
       return workListDao.getWorkList(criteria);
    }

    public Map<Task, Long> getAllTasks(WorkListCriteria criteria,User loggedInUser) {
        return workListDao.getAllTasks(criteria,loggedInUser);
    }

    public InboxItemList getSupplierRecoveryClaimBasedView(WorkListCriteria criteria) {
        return supplierRecoveryWorkListDao.getSupplierRecoveryClaimList(criteria);
    }
    
    public InboxItemList getSupplierRecoveryPartBasedView(WorkListCriteria criteria) {
        return supplierRecoveryWorkListDao.getSupplierRecoveryPartList(criteria);
    }
    
    public InboxItemList getSupplierShipmentBasedView(WorkListCriteria criteria) {
        return supplierRecoveryWorkListDao.getSupplierShipmentList(criteria);
    }
    
    public InboxItemList getSupplierPartReceiptView(WorkListCriteria criteria) {
        return supplierRecoveryWorkListDao.getSupplierPartReceiptList(criteria);
    }
    
    public InboxItemList getSupplierRecoveryLocationBasedView(WorkListCriteria criteria) {
        return supplierRecoveryWorkListDao.getSupplierLocationList(criteria);
    }
    
    public InboxItemList getSupplierRecoverySupplierPartReturnBasedView(WorkListCriteria criteria){
    	return supplierRecoveryWorkListDao.getSupplierRecoveryTaskList(criteria);
    }
    
    public List<TaskInstance> getPreviewPaneForSupplierLocation(Long locationId, String taskName, String actorId) {
        return supplierRecoveryWorkListDao.getPreviewPaneForSupplier(locationId, taskName, actorId);
    }

    public List<TaskInstance> getPreviewPaneForSupplierShipment(Long shipmentId, String taskName, String actorId) {
        return supplierRecoveryWorkListDao.getPreviewPaneForSupplierShipment(shipmentId, taskName, actorId);
    }
    
    public WorkListDao getWorkListDao() {
        return workListDao;
    }

    @Required
    public void setWorkListDao(WorkListDao workListDao) {
        this.workListDao = workListDao;
    }

    @Required
    public void setSupplierRecoveryWorkListDao(SupplierRecoveryWorkListDao supplierRecoveryWorkListDao) {
        this.supplierRecoveryWorkListDao = supplierRecoveryWorkListDao;
    }
    
	public User getCurrentAssigneeForRecClaim(Long recClaimId){
    	return workListDao.getCurrentAssigneeForRecClaim(recClaimId);
    }

	public List<String> getPartReturnInboxOrders() {
		
		return workListDao.getPartReturnInboxOrders();
	}

    public InboxItemList getPartShipperRecoveryClaimView(WorkListCriteria criteria){
        return supplierRecoveryWorkListDao.getPartShipperRecoveryClaimList(criteria);
    }

    public List<Shipment> getAllShipmentsForRecoveryClaim(final Long recClaimId, final String taskName){
        return supplierRecoveryWorkListDao.getAllShipmentsForRecoveryClaim(recClaimId, taskName);
    }

    public List<TaskInstance> getAllTaskInstancesForRecoveryClaim(final Long recClaimId, final String taskName){
        return supplierRecoveryWorkListDao.getAllTaskInstancesForRecoveryClaim(recClaimId, taskName);
    }

    public InboxItemList getPartReceiverReceiptView(WorkListCriteria criteria){
        return supplierRecoveryWorkListDao.getPartReceiverReceiptView(criteria);
    }
    
    public InboxItemList getPartInspectorInspectView(WorkListCriteria criteria){
    	return supplierRecoveryWorkListDao.getPartInspectorInspectView(criteria);
    }

    public InboxItemList getSupplierRecoveryDistinctRecoveryClaimList(WorkListCriteria criteria){
        return supplierRecoveryWorkListDao.getSupplierRecoveryDistinctRecoveryClaimList(criteria);
    }


}

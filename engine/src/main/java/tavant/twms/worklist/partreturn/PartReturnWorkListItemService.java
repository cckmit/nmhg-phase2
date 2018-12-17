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
package tavant.twms.worklist.partreturn;

import java.util.List;

import org.jbpm.taskmgmt.exe.TaskInstance;
import org.springframework.transaction.annotation.Transactional;

import tavant.twms.domain.partreturn.PartReturn;
import tavant.twms.domain.partreturn.Shipment;
import tavant.twms.worklist.WorkListCriteria;

@Transactional(readOnly=true)
public interface PartReturnWorkListItemService {
    
    public List<TaskInstance> findAllTasksForLocation(WorkListCriteria criteria);
    
    public List<TaskInstance> findAllDueAndOverduePartTasksForLocation(WorkListCriteria criteria);
    
    public List<TaskInstance> findAllTasksForClaim(WorkListCriteria criteria);
    
    public List<TaskInstance> findAllTasksForShipment(WorkListCriteria criteria);
    
    public List<TaskInstance> printAllTasksForShipment(WorkListCriteria criteria);
    
    
    public List<TaskInstance> findPartReturnInspectionTasksForClaim(WorkListCriteria criteria);
    
    public List<TaskInstance> findPartReturnReceiptTasksForClaim(WorkListCriteria criteria);

    //Added for part shipper location view
    public List<TaskInstance> findAllTasksForDealerLocation(WorkListCriteria criteria);
    
    public List<TaskInstance> findAllTasksForDealer(WorkListCriteria criteria);

    public List<TaskInstance> findAllTasksForWPRA(WorkListCriteria criteria);
    
    public List<TaskInstance> printAllTasksForWPRA(WorkListCriteria criteria);
    
    public List<TaskInstance> findAllTasksForWPRAByActorId(WorkListCriteria criteria); 
    
    public List<TaskInstance> findAllTasksForWPRAByDealership(WorkListCriteria criteria); 
    
    public TaskInstance findAllTasksForShipmentForPartReturn(PartReturn partReturn, String taskName);

    public List<TaskInstance> findShipmentGeneratedTasksForWPRA(WorkListCriteria criteria);

    public List<Shipment> findShipmentsForWPRA(String wpraId);

}

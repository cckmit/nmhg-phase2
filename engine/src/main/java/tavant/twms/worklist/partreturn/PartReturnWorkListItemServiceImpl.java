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

import tavant.twms.domain.partreturn.PartReturn;
import tavant.twms.domain.partreturn.Shipment;
import tavant.twms.worklist.WorkListCriteria;

public class PartReturnWorkListItemServiceImpl implements PartReturnWorkListItemService {

    private PartReturnWorkListDao partReturnWorkListDao;  
    
    public List<TaskInstance> findAllTasksForLocation(WorkListCriteria criteria) {
        return partReturnWorkListDao.findAllTasksForLocation(criteria);
    }

    public List<TaskInstance> findAllDueAndOverduePartTasksForLocation(WorkListCriteria criteria){
        return partReturnWorkListDao.findAllDueAndOverduePartTasksForLocation(criteria);
    }
    
    public List<TaskInstance> findAllTasksForClaim(WorkListCriteria criteria) {
        return partReturnWorkListDao.findAllTasksForClaim(criteria);
    }
    
    public List<TaskInstance> findAllTasksForShipment(WorkListCriteria criteria) {
        return partReturnWorkListDao.findAllTasksForShipment(criteria);
    }
    public List<TaskInstance> printAllTasksForShipment(WorkListCriteria criteria) {
        return partReturnWorkListDao.printAllTasksForShipment(criteria);
    }
    
    public void setPartReturnWorkListDao(PartReturnWorkListDao partReturnWorkListDao) {
        this.partReturnWorkListDao = partReturnWorkListDao;
    }
    
    public List<TaskInstance> findPartReturnInspectionTasksForClaim(WorkListCriteria criteria) {
    	return this.partReturnWorkListDao.findPartReturnInspectionTasksForClaim(criteria);
    }
    
    public List<TaskInstance> findPartReturnReceiptTasksForClaim(WorkListCriteria criteria) {
    	return this.partReturnWorkListDao.findPartReturnReceiptTasksForClaim(criteria);
    }

    //Added for part shipper location view
    public List<TaskInstance> findAllTasksForDealerLocation(WorkListCriteria criteria){
      return partReturnWorkListDao.findAllTasksForDealerLocation(criteria);
    }
    
    //Added for Required Parts for dealer inbox
    public List<TaskInstance> findAllTasksForDealer(WorkListCriteria criteria){
      return partReturnWorkListDao.findAllTasksForDealer(criteria);
    }


     public List<TaskInstance> findAllTasksForWPRA(WorkListCriteria criteria){
      return partReturnWorkListDao.findAllTasksForWPRA(criteria);
    }
     
     public List<TaskInstance> printAllTasksForWPRA(WorkListCriteria criteria){
         return partReturnWorkListDao.printAllTasksForWPRA(criteria);
       }
     
     public List<TaskInstance> findAllTasksForWPRAByActorId(WorkListCriteria criteria){
         return partReturnWorkListDao.findAllTasksForWpraByActorId(criteria);
       }

     public List<TaskInstance> findAllTasksForWPRAByDealership(WorkListCriteria criteria){
    	 return partReturnWorkListDao.findAllTasksForWpraByDealership(criteria);
     }
     
    public TaskInstance findAllTasksForShipmentForPartReturn(PartReturn partReturn, String taskName){
        return partReturnWorkListDao.findAllTasksForShipmentForPartReturn(partReturn, taskName);
    }

    public List<TaskInstance> findShipmentGeneratedTasksForWPRA(WorkListCriteria criteria){
        return partReturnWorkListDao.findShipmentGeneratedTasksForWPRA(criteria);
    }

    public List<Shipment> findShipmentsForWPRA(String wpraId){
        return partReturnWorkListDao.findShipmentsForWPRA(wpraId);
    }



}

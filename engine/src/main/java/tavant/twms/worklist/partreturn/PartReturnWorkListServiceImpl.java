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

import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.claim.OEMPartReplaced;
import tavant.twms.worklist.WorkListCriteria;

public class PartReturnWorkListServiceImpl implements PartReturnWorkListService {

	private PartReturnWorkListDao partReturnWorkListDao;

	public PartReturnWorkList getPartReturnWorkListByLocation(
			WorkListCriteria criteria) {
		return partReturnWorkListDao.getPartReturnWorkListByLocation(criteria);
	}

	public PartReturnWorkList getPartReturnWorkListByClaim(
			WorkListCriteria criteria) {
		return partReturnWorkListDao.getPartReturnWorkListByClaim(criteria);
	}

	public PartReturnWorkList getPartReturnWorkListByShipment(
			WorkListCriteria criteria) {
		return partReturnWorkListDao.getPartReturnWorkListByShipment(criteria);
	}

	public void setPartReturnWorkListDao(
			PartReturnWorkListDao partReturnWorkListDao) {
		this.partReturnWorkListDao = partReturnWorkListDao;
	}
	public PartReturnWorkList getPartReturnTaskInstancesByClaim(
			WorkListCriteria criteria) {
		return partReturnWorkListDao.getPartReturnWorkListByClaim(criteria);
	}
	public List<TaskInstance> getNotShippedPartReturnTaskInstancesByClaim(
			Claim claim) {
		return partReturnWorkListDao
				.findAllNotShippedPartTasksForLocation(claim);
	}

     public PartReturnWorkList getPartReturnWorkListByDealerLocation(
			WorkListCriteria criteria){
          return partReturnWorkListDao.getPartReturnWorkListByDealerLocation(criteria);
     }

     public PartReturnWorkList getPartReturnWorkListByWpra(
			WorkListCriteria criteria){
         return partReturnWorkListDao.getPartReturnWorkListByWpra(criteria);
     }
     
     public PartReturnWorkList getPartReturnWorkListForWpraByActorId(
 			WorkListCriteria criteria){
    	 return partReturnWorkListDao.getPartReturnWorkListForWpraByActorId(criteria);
     }
     
     public PartReturnWorkList getPartReturnWorkListForWpraByDealership(
  			WorkListCriteria criteria){
     	 return partReturnWorkListDao.getPartReturnWorkListForWpraByDealership(criteria);
      }     
     
     public PartReturnWorkList getPartReturnWorkListByWpraNumber(
 			WorkListCriteria criteria, String wpraNumber){
    	 return partReturnWorkListDao.getPartReturnWorkListByWpraNumber(criteria , wpraNumber);
     }


    public PartReturnWorkList getShipmentGeneratedWorkListByWpra(
            WorkListCriteria criteria){
        return partReturnWorkListDao.getShipmentGeneratedWorkListByWpra(criteria);
    }

    public PartReturnWorkList getPartReturnWorkListByDealerLocationForPartShipper(
            WorkListCriteria criteria){
        return partReturnWorkListDao.getPartReturnWorkListByDealerLocationForPartShipper(criteria);
    }

    public List<TaskInstance> findAllRejectedPartsForDealer(OEMPartReplaced oemPart){
        return partReturnWorkListDao.findAllRejectedPartsForDealer(oemPart);
    }

    public PartReturnWorkList getCEVAWorkListByWpra(
            WorkListCriteria criteria){
        return partReturnWorkListDao.getCEVAWorkListByWpra(criteria);
    }

}

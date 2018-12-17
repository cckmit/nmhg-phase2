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

public interface PartReturnWorkListService {

	public PartReturnWorkList getPartReturnWorkListByLocation(
			WorkListCriteria criteria);

	public PartReturnWorkList getPartReturnWorkListByClaim(
			WorkListCriteria criteria);

	public PartReturnWorkList getPartReturnWorkListByShipment(
			WorkListCriteria criteria);

	public List<TaskInstance> getNotShippedPartReturnTaskInstancesByClaim(
			Claim claim);

    public PartReturnWorkList getPartReturnWorkListByDealerLocation(
			WorkListCriteria criteria);

    public PartReturnWorkList getPartReturnWorkListByWpra(
			WorkListCriteria criteria);
    
    public PartReturnWorkList getPartReturnWorkListForWpraByActorId(
			WorkListCriteria criteria);
    
    public PartReturnWorkList getPartReturnWorkListForWpraByDealership(
			WorkListCriteria criteria);
    
    public PartReturnWorkList getPartReturnWorkListByWpraNumber(
			WorkListCriteria criteria, String wpraNumber);

    public PartReturnWorkList getShipmentGeneratedWorkListByWpra(WorkListCriteria criteria);

    public PartReturnWorkList getPartReturnWorkListByDealerLocationForPartShipper(
            WorkListCriteria criteria);

    public List<TaskInstance> findAllRejectedPartsForDealer(OEMPartReplaced oemPart);

    public PartReturnWorkList getCEVAWorkListByWpra(WorkListCriteria criteria);
         
    
}

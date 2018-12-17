package tavant.twms.worklist.partreturn;

import java.util.List;

import org.jbpm.scheduler.exe.Timer;
import org.jbpm.taskmgmt.exe.TaskInstance;

import com.domainlanguage.time.CalendarDate;

import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.claim.OEMPartReplaced;
import tavant.twms.domain.claim.RecoveryClaim;
import tavant.twms.domain.partreturn.PartReturn;
import tavant.twms.domain.partreturn.Shipment;
import tavant.twms.worklist.WorkListCriteria;
import java.util.Date;

public interface PartReturnWorkListDao {

	public PartReturnWorkList getPartReturnWorkListByLocation(
			WorkListCriteria criteria);

	public PartReturnWorkList getPartReturnWorkListByClaim(
			WorkListCriteria criteria);

	public PartReturnWorkList getPartReturnWorkListByShipment(
			WorkListCriteria criteria);

	@SuppressWarnings("unchecked")
	public List<TaskInstance> findAllTasksForShipment(WorkListCriteria criteria);
	
	@SuppressWarnings("unchecked")
	public List<TaskInstance> printAllTasksForShipment(WorkListCriteria criteria);

	@SuppressWarnings("unchecked")
	public List<TaskInstance> findAllTasksForLocation(WorkListCriteria criteria);

	@SuppressWarnings("unchecked")
	public List<TaskInstance> findAllTasksForClaim(WorkListCriteria criteria);

	@SuppressWarnings("unchecked")
	public List<TaskInstance> findAllDueAndOverduePartTasksForLocation(
			final WorkListCriteria criteria);

	@SuppressWarnings("unchecked")
	public List<TaskInstance> findAllNotShippedPartTasksForLocation(Claim claim);
	
	@SuppressWarnings("unchecked")
	public List<TaskInstance> findPartReturnInspectionTasksForClaim(WorkListCriteria criteria);

	@SuppressWarnings("unchecked")
	public List<TaskInstance> findPartReturnReceiptTasksForClaim(WorkListCriteria criteria);

    @SuppressWarnings("unchecked")
    public List<TaskInstance> findAllTaskInstanceForParts(List<OEMPartReplaced> removedParts);
    
    public Timer findTimerForTaskInstance(TaskInstance partReturnTaskInstance);

    public Timer findTimerForRecoveryClaim(RecoveryClaim recClaim);

    public PartReturnWorkList getPartReturnWorkListByDealerLocation(WorkListCriteria criteria);

    //Added for part shipper location view
    public List<TaskInstance> findAllTasksForDealerLocation(WorkListCriteria criteria);
    
    public List<TaskInstance> findAllTasksForDealer(WorkListCriteria criteria);

    public PartReturnWorkList getPartReturnWorkListByWpra(WorkListCriteria criteria);
    
    public PartReturnWorkList getPartReturnWorkListForWpraByActorId(WorkListCriteria criteria);  
    
    public PartReturnWorkList getPartReturnWorkListForWpraByDealership(WorkListCriteria criteria);  
    
    public List<TaskInstance>  findAllTasksForWPRA(WorkListCriteria criteria);
    
    public List<TaskInstance>  printAllTasksForWPRA(WorkListCriteria criteria);

    public List<TaskInstance>  findAllTasksForWpraByActorId(WorkListCriteria criteria);
    
    public List<TaskInstance>  findAllTasksForWpraByDealership(WorkListCriteria criteria);
    
    public List<TaskInstance> findAllPrepareDuePartTaskInstanceForParts(List<OEMPartReplaced> removedParts);


    public List<TaskInstance> findAllPrepareDuePartAndWpraTaskInstanceForParts(List<OEMPartReplaced> removedParts);

    public List<TaskInstance> findAllWpraTasks();
    
    public List<TaskInstance> findAllWpraTasksBetweenGivenDate(CalendarDate startDate,CalendarDate endDate);

    public List<TaskInstance> findAllPrepareDuePartsTasks();
    public List<TaskInstance> findAllPrepareDuePartsTasksBetweenGivenDate(CalendarDate startDate,CalendarDate endDate);

    public PartReturnWorkList getPartReturnWorkListByWpraNumber(WorkListCriteria criteria, String wpraNumber);

    public TaskInstance findAllTasksForShipmentForPartReturn(PartReturn partReturn, String taskName);

    public PartReturnWorkList getShipmentGeneratedWorkListByWpra(WorkListCriteria criteria);

    public List<TaskInstance> findShipmentGeneratedTasksForWPRA(WorkListCriteria criteria);

    public List<Shipment> findShipmentsForWPRA(String wpraId);

    public PartReturnWorkList getPartReturnWorkListByDealerLocationForPartShipper(WorkListCriteria criteria);

    public List<TaskInstance> findAllClaimedPartReceiptAndDealerPartShipped(List<OEMPartReplaced> receivedParts);

    public List<TaskInstance> findAllNotCollectedParts(int daysAllowed);

    public List<TaskInstance> findAllRejectedPartsForDealer(OEMPartReplaced oemPart);

    public PartReturnWorkList getCEVAWorkListByWpra(WorkListCriteria criteria);

    public List<TaskInstance> findAllPartTaskInstanceForParts(final List<Long> partReturnsIds, final String taskName);
    
    public List<TaskInstance> findAllRejectedPartsTasks();
}
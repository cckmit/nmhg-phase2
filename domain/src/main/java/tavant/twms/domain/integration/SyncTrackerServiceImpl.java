package tavant.twms.domain.integration;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class SyncTrackerServiceImpl implements SyncTrackerService {

	private SyncTrackerDAO syncTrackerDAO;

	public List<SyncTracker> getRecordsForProcessing(String syncType,Integer maxNoOfRetries) {
		return syncTrackerDAO.getRecordsForProcessing(syncType,maxNoOfRetries);
	}

    public List<Long> getIdsForProcessing(String syncType,Integer maxNoOfRetries){
        return syncTrackerDAO.getIdsForProcessing(syncType,maxNoOfRetries);
    }
	
	public void save(SyncTracker newSyncTracker) {
		syncTrackerDAO.save(newSyncTracker);
	}

	public void update(SyncTracker syncTrackerToBeUpdated) {
		syncTrackerDAO.update(syncTrackerToBeUpdated);
	}

	public void setSyncTrackerDAO(SyncTrackerDAO syncTrackerDAO) {
		this.syncTrackerDAO = syncTrackerDAO;
	}

	public List<SyncTracker> findRecordsByRangeTypeAndStatus(Calendar startDate,
			Calendar endDate, String syncType, String status) {
		Date fromDate= null;
		Date toDate= null;
		
		if(startDate != null){
			fromDate = startDate.getTime();
		}
		
		if(endDate != null){
			toDate = endDate.getTime();
		}
		
		return syncTrackerDAO.search(syncType, status, fromDate, toDate);
	}

	public SyncTracker findById(Long syncTrackerId) {
		return syncTrackerDAO.findById(syncTrackerId);
	}

    public SyncTracker findByStatusSyncTypeAndUniqueIdValue(String status, String syncType, String uniqueIdValue) {
        return syncTrackerDAO.findByStatusAndUniqueValueForSyncType(syncType, status, uniqueIdValue);
    }

    public List<SyncTracker> getRecordsForRetryProcessing(String syncType,
			Integer maxNoOfRetries) {
		return syncTrackerDAO.getRecordsForRetryProcessing(syncType,maxNoOfRetries);
	}
    
    public List<Long> getIdsForRetryProcessing(String syncType,
			Integer maxNoOfRetries) {
		return syncTrackerDAO.getIdsForRetryProcessing(syncType,maxNoOfRetries);
	}

	public void setInactiveStatusForexistingSyncTrackerIds(String claimNumber,
			String syncType) {
		List<Long> ids=syncTrackerDAO.getPreviousSyncTrackerIdsForCreditSubmission(claimNumber, syncType);
		if(!ids.isEmpty()){
		syncTrackerDAO.updateStatus(ids, SyncStatus.CANCELLED);
		}
	}
}

package tavant.twms.domain.integration;

import java.util.Calendar;
import java.util.List;

import org.springframework.transaction.annotation.Transactional;

public interface SyncTrackerService {

	@Transactional(readOnly = false)
	void save(SyncTracker newSyncTracker);

	@Transactional(readOnly = false)
	void update(SyncTracker syncTrackerToBeUpdated);

	@Transactional(readOnly = true)
	public List<SyncTracker> getRecordsForProcessing(String syncType,Integer maxNoOfRetries);

    @Transactional(readOnly = true)
    public List<Long> getIdsForProcessing(String syncType,Integer maxNoOfRetries);
	
	@Transactional(readOnly = true)
	public List<SyncTracker> getRecordsForRetryProcessing(String syncType, Integer maxNoOfRetries);
	
	@Transactional(readOnly = true)
	public List<SyncTracker> findRecordsByRangeTypeAndStatus(Calendar startDate,
			Calendar endDate, String syncType, String status);
	
	@Transactional(readOnly = true)
	public SyncTracker findById(Long syncTrackerId);

    @Transactional(readOnly = true)
	public SyncTracker findByStatusSyncTypeAndUniqueIdValue(String status, String syncType, String uniqueIdValue);
    
    @Transactional(readOnly = true)
	public List<Long> getIdsForRetryProcessing(String syncType, Integer maxNoOfRetries);
    
    @Transactional(readOnly = true)
    public void setInactiveStatusForexistingSyncTrackerIds(String claimNumber,String syncType);
    

}

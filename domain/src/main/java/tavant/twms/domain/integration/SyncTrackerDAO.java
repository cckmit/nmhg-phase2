package tavant.twms.domain.integration;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import tavant.twms.domain.orgmodel.User;

public interface SyncTrackerDAO {

	List<SyncTracker> getBusinessEntitiesToBeSynced(String syncType);

	SyncTracker findSyncTrackerInProgressByBusinessId(final String syncType,
			final String businessId);

	void save(SyncTracker newSyncTracker);

	void save(List<SyncTracker> syncTracks);

	void update(SyncTracker syncTrackerToBeUpdated);

	void updateStatus(SyncTracker syncTracker);

	SyncTracker findById(Long id);

	public List<SyncTracker> findAll();

	List<SyncTracker> search(String syncType, String status, Date fromDate,
			Date toDate);

	public List<SyncTracker> getRecordsForProcessing(String syncType,Integer maxNoOfRetries);

    public List<Long> getIdsForProcessing(String syncType,Integer maxNoOfRetries);
	
	public SyncTracker findByStatusAndUniqueValueForSyncType(final String syncType, 
															 final String status, 
															 final String uniqueIdValue); 

	
	
	public List<SyncTracker> getRecordsForRetryProcessing(String syncType, Integer maxNoOfRetries);
	
    public List<Long> getIdsForRetryProcessing(String syncType, Integer maxNoOfRetries);

    
	public List<String> getSyncTypes();

    /**
     * Returns the list of matching sync tracker objects for the given ids.
     * @param ids - collections of id's which needs to retrived
     * @return
     * Note: The size of returned list may not match with that of passed collection
     * if any of the id is not found in db
     */
    public List<SyncTracker> findByIds(Collection<Long> ids);

    /**
     * Mark's the given collection of id's as deleted
     * @param ids
     * @param loggedInUser user id
     */
    public void delete(Collection<Long> ids, User loggedInUserId);
    
    public void updateAuditableColumns(Collection<Long> ids, User user);
    
    public String getReferenceId(String sequenceName);
    public List<Long> getPreviousSyncTrackerIdsForCreditSubmission(final String claimNumber,final String syncType);
    public void updateStatus(List<Long> syncTrackerIds,SyncStatus status);

	boolean isClaimExistInSyncTracker(String claimNumber, String string);
	
	public void updateNextFireTimeForCreditSubmission(long timetoFire,String CreditSubmission) ;
}

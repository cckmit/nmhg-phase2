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

package tavant.twms.integration.server.common.dataaccess;

import org.springframework.transaction.annotation.Transactional;


import java.util.Date;
import java.util.List;

public interface SyncTrackerDAO {
    @Transactional(readOnly = true)
    List<SyncTracker> getBusinessEntitiesToBeSynced(String syncType);

    @Transactional(readOnly = true)
    SyncTracker findSyncTrackerInProgressByBusinessId(final String syncType, final String businessId);

    @Transactional(readOnly = false)
    void save(SyncTracker newSyncTracker);

    @Transactional(readOnly = false)
    void save(List<SyncTracker> syncTracks);

    @Transactional(readOnly = false)
    SyncTracker save(String syncType, String bodXML,SyncStatus syncStatus);

    @Transactional(readOnly = false)
    void update(SyncTracker syncTrackerToBeUpdated);

    @Transactional(readOnly=false)
    void updateStatus(SyncTracker syncTracker);

    @Transactional(readOnly = false)
    void update(SyncTracker syncTracker, String serverHeader, String response);

    @Transactional(readOnly = true)
    SyncTracker findById(Long id);

    public List<SyncTracker> findAll();

    List<SyncTracker> search(String uniqueIdValue, String syncType, 
            String status, Date fromDate, Date toDate, int page, int rows);
    
    public List<SyncTracker> getClaimsForSubmission(String syncType);
    
    public List<SyncTracker> getBUClaimsForSubmission(String syncType, String buName);
	
    public String getBUForDivisionCode(String divisionCode);
    
    public List<Long> getIdsForProcessing(String buName,Integer maxRetries, String syncType);
    
    public List<Long> getItemIdsForProcessing(String buName,Integer maxRetries, String syncType);
    
    public List<Long> getBatchClaimIdsForProcessing(String buName,Integer maxRetries, String syncType);
    
    public List<Long> getIdsForProcessing(final String buName,final Integer maxRetries,final List<String> syncType);

    public void updateStatus(List<Long> syncTrackerIds,SyncStatus status);
    
    public long getCount(String uniqueIdValue, String syncType, 
            String status, Date fromDate, Date toDate);
    
    public List<SyncType> findAllSyncTypes();

    java.sql.Timestamp getSucccessfulJobDate();

	void updateJobStatus(Date startDate, Date endDate,
			String italyQaNotificationJobStatus);

	List<Long> getSyncIdsForProcessing(String buName, Integer maxNoOfRetries,
			List<String> syncType);
}

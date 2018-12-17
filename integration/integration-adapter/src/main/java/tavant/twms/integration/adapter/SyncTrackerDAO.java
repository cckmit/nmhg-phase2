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

package tavant.twms.integration.adapter;

import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

public interface SyncTrackerDAO {
    @Transactional(readOnly = true)
    List<SyncTracker> getBusinessEntitiesToBeSynced(String syncType);

    @Transactional(readOnly = false)
    void deleteDuplicatesOfBusinessEntitiesToBeSynced(final String syncType);

    @Transactional(readOnly = true)
    SyncTracker findById(final Long id);

    @Transactional(readOnly = false)
    void save(SyncTracker newSyncTracker);

    @Transactional(readOnly = false)
    void update(SyncTracker syncTrackerToBeUpdated);

    @Transactional(readOnly = true)
	List<SummaryDTO> getSummary(final Date startDate,
			final Date endDate);

	@Transactional(readOnly = true)
	List<SyncTracker> getSyncDetails(SyncTrackerSearchCriteria syncTrackerSearchCriteria);

	@Transactional(readOnly = true)
	List<ErrorSummaryDTO> getErrorSummary(SyncTrackerSearchCriteria syncTrackerSearchCriteria);

	@Transactional(readOnly = true)
	List<String> getSyncTypes();

	@Transactional(readOnly = true)
	List<String> getStatuses();

	@Transactional(readOnly = true)
	String getErrorMessageById(Long id);

    @Transactional(readOnly=false)
    void updateStatus(SyncTracker syncTracker);

}

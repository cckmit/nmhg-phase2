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

import java.util.List;
import java.util.Date;

import tavant.oagis.BODDTO;

public class SyncResponseProcessor {

    private SyncTrackerDAO syncTrackerDAO;

    private EventContext eventContext;

    public void setSyncTrackerDAO(SyncTrackerDAO syncTrackerDAO) {
        this.syncTrackerDAO = syncTrackerDAO;
    }

    public void setEventContext(EventContext eventContext) {
        this.eventContext = eventContext;
    }

	@Transactional(readOnly = false)
	public void processResponse(List<BODDTO> bodDTOs) {
        List<SyncTracker> syncTrackers = eventContext.getRecordsBeingSynced();
        Date now = new Date();
        int index = 0;
        for (BODDTO bodDTO : bodDTOs) {
            SyncTracker syncTracker =
                    syncTrackerDAO.findById(syncTrackers.get(index++).getId());
            if (bodDTO.getSuccessful()) {
                syncTracker.setStatus(SyncStatus.COMPLETED);
            } else {
                syncTracker.setStatus(SyncStatus.FAILED);
            }
            syncTracker.setNoOfAttempts(syncTracker.getNoOfAttempts() + 1);
            syncTracker.setErrorMessage(bodDTO.getException());
            syncTracker.setUpdateDate(now);
            syncTrackerDAO.update(syncTracker);
        }
	}
}

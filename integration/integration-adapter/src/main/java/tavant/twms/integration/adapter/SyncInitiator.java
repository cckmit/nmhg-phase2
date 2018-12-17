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

public class SyncInitiator {
    
    private SyncTrackerDAO syncTrackerDAO;

    private EventContext eventContext;

    public void setEventContext(EventContext eventContext) {
        this.eventContext = eventContext;
    }

    public void setSyncTrackerDAO(SyncTrackerDAO syncTrackerDAO) {
        this.syncTrackerDAO = syncTrackerDAO;
    }

    public void initiateSync(String syncType) {
        syncTrackerDAO.deleteDuplicatesOfBusinessEntitiesToBeSynced(syncType);
        List<SyncTracker> list = syncTrackerDAO.getBusinessEntitiesToBeSynced(syncType);
        while (!list.isEmpty()) {
            sendEvent(list);
            updateStatus(list);
            list = syncTrackerDAO.getBusinessEntitiesToBeSynced(syncType);
        }
    }

    @Transactional(readOnly = false)
    public void updateStatus(List<SyncTracker> syncTrackers) {
        Date now = new Date();
        for (SyncTracker syncTracker : syncTrackers) {
            syncTracker.setStatus(SyncStatus.IN_PROGRESS);
            syncTracker.setStartTime(now);
            syncTracker.setUpdateDate(now);
			syncTrackerDAO.updateStatus(syncTracker);
        }
	}

    public void sendEvent(List<SyncTracker> list) {
    	eventContext.sendEvent(list);
	}

}

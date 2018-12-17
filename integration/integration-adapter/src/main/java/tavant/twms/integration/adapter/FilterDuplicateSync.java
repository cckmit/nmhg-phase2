package tavant.twms.integration.adapter;

import java.util.ArrayList;
import java.util.List;

public class FilterDuplicateSync {

    private SyncTrackerDAO syncTrackerDAO;

    private EventContext eventContext;

    public void setSyncTrackerDAO(SyncTrackerDAO dao) {
		this.syncTrackerDAO = dao;
	}

    public void setEventContext(EventContext eventContext) {
        this.eventContext = eventContext;
    }

	public List<SyncTracker> filterDuplicates(List<SyncTracker> syncTrackers){
		List<SyncTracker> filteredSyncTrackers = new ArrayList<SyncTracker>();
		for (SyncTracker syncTracker : syncTrackers) {
			if(!isDuplicate(syncTracker))
				filteredSyncTrackers.add(syncTracker);
		}
        eventContext.setRecordsBeingSynced(filteredSyncTrackers);
        return filteredSyncTrackers;
	}

	public boolean isDuplicate(SyncTracker syncTracker){
        SyncTracker syncTrackerDB = syncTrackerDAO.findById(syncTracker.getId());
        return ((syncTrackerDB != null) &&
                (syncTrackerDB.getNoOfAttempts() == syncTracker.getNoOfAttempts()));
	}

}

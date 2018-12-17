package tavant.twms.integration.server.common.dataaccess;

import org.apache.log4j.Logger;



public class SyncPersisterImpl implements SyncPersister {

	private static final Logger log = Logger
			.getLogger(SyncPersisterImpl.class);

	protected String syncType;

	SyncTrackerDAO syncTrackerDao;

	public String sync(String input) {
		SyncTracker syncTracker = new SyncTracker(this.syncType, input);
		syncTrackerDao.save(syncTracker);
		return input;
	}

	public void setSyncTrackerDao(SyncTrackerDAO syncTrackerDao) {
		this.syncTrackerDao = syncTrackerDao;
	}

	public String getSyncType() {
		return syncType;
	}

	public void setSyncType(String syncType) {
		this.syncType = syncType;
	}

}

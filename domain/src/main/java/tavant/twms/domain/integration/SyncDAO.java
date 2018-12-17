package tavant.twms.domain.integration;

import tavant.twms.infra.ListCriteria;
import tavant.twms.infra.PageResult;

import com.domainlanguage.time.CalendarDate;
import java.util.Collection;
import java.util.List;

public interface SyncDAO {

	public PageResult<SyncTracker> findSyncTrackerObjects(
			String syncType, String transactionId, CalendarDate fromDate,
			CalendarDate toDate, ListCriteria listCriteria, String sortBy,
			String order, String statusSelected);

    public PageResult<SyncTracker> findSyncTrackerObjects(SyncTrackerCriteria criteria);

    public List<SyncTracker> findByIds(Collection<Long> ids);
}

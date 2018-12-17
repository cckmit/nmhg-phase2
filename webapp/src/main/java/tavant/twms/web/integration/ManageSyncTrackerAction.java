package tavant.twms.web.integration;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.struts2.interceptor.validation.SkipValidation;

import tavant.twms.domain.integration.SyncStatus;
import tavant.twms.domain.integration.SyncTracker;
import tavant.twms.domain.integration.SyncTrackerService;
import tavant.twms.web.actions.TwmsActionSupport;

import com.opensymphony.xwork2.Preparable;

@SuppressWarnings("serial")
public final class ManageSyncTrackerAction extends TwmsActionSupport implements
		Preparable {

	private List<SyncTracker> syncTrackerRecords;

	private SyncTrackerService syncTrackerService;

	private Calendar fromDate;

	private Calendar toDate;

	private String syncType;

	private String status;

	protected List<String> statusOptions;

	private List<String> syncTypeOptions;

	private SyncTracker syncTracker;

	private String id;

	@SkipValidation
	public String displaySearchRecords() throws Exception {
		syncTrackerRecords = syncTrackerService
				.findRecordsByRangeTypeAndStatus(fromDate, toDate, syncType,
						status);
		return SUCCESS;
	}

	public List<SyncTracker> getSyncTrackerRecords() {
		return syncTrackerRecords;
	}

	public void setSyncTrackerRecords(List<SyncTracker> syncTrackerRecords) {
		this.syncTrackerRecords = syncTrackerRecords;
	}

	public void setSyncTrackerService(SyncTrackerService syncTrackerService) {
		this.syncTrackerService = syncTrackerService;
	}

	public String getSyncType() {
		return syncType;
	}

	public void setSyncType(String syncType) {
		this.syncType = syncType;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Calendar getFromDate() {
		return fromDate;
	}

	public void setFromDate(Calendar fromDate) {
		this.fromDate = fromDate;
	}

	public Calendar getToDate() {
		return toDate;
	}

	public void setToDate(Calendar toDate) {
		this.toDate = toDate;
	}

	public void prepare() throws Exception {

		statusOptions = new ArrayList<String>();
		statusOptions.add(SyncStatus.COMPLETED.getStatus());
		statusOptions.add(SyncStatus.TO_BE_PROCESSED.getStatus());
		statusOptions.add(SyncStatus.FAILED.getStatus());
		statusOptions.add(SyncStatus.IN_PROGRESS.getStatus());
		statusOptions.add(SyncStatus.ERROR.getStatus());
		statusOptions.add(SyncStatus.RECTIFIED.getStatus());

		syncTypeOptions = new ArrayList<String>();
		syncTypeOptions.add("Claim");
		syncTypeOptions.add("CreditNotification");
		syncTypeOptions.add("Customer");
		syncTypeOptions.add("ExtWarrantyDebitNotification");
		syncTypeOptions.add("ExtWarrantyDebitSubmit");
		syncTypeOptions.add("InstallBase");
		syncTypeOptions.add("Item");
		syncTypeOptions.add("OEMXRef");
		syncTypeOptions.add("SupplierDebitNotification");
		syncTypeOptions.add("SupplierDebitSubmit");
		syncTypeOptions.add("User");
	}

	public List<String> getStatusOptions() {
		return statusOptions;
	}

	public void setStatusOptions(List<String> statusOptions) {
		this.statusOptions = statusOptions;
	}

	public List<String> getSyncTypeOptions() {
		return syncTypeOptions;
	}

	public void setSyncTypeOptions(List<String> syncTypeOptions) {
		this.syncTypeOptions = syncTypeOptions;
	}

	public String showRecordInfo() {
		syncTracker = syncTrackerService.findById(Long.parseLong(id));
		return SUCCESS;
	}

	public SyncTracker getSyncTracker() {
		return syncTracker;
	}

	public void setSyncTracker(SyncTracker syncTracker) {
		this.syncTracker = syncTracker;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
}

package tavant.twms.integration.adapter.web.actions;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import tavant.twms.integration.adapter.SyncTracker;
import tavant.twms.integration.adapter.SyncTrackerDAO;
import tavant.twms.integration.adapter.SyncTrackerSearchCriteria;
import tavant.twms.integration.adapter.util.PropertiesUtil;

import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.Preparable;


/**
 * @author vikas.a
 * 
 */
@SuppressWarnings("serial")

public class SyncDetailsAction extends ActionSupport implements Preparable{
	
	List<String> syncTypeList = new ArrayList<String>();

	List<String> statusList = new ArrayList<String>();

	List<SyncTracker> syncTrackerList = new ArrayList<SyncTracker>();
	
	SyncTrackerSearchCriteria syncTrackerSearchCriteria = new SyncTrackerSearchCriteria();

	private SyncTrackerDAO syncTrackerDAO;
	
	String showMuleLogo = PropertiesUtil.getProperty("mule.displaylogo");
	
	final String ERROR_MESSAGE = "errorMessage";
	
	private String errorMessage;
	
	private Long id;
	
	public String fetchSyncDetails() throws Exception {
		syncTrackerList = syncTrackerDAO
				.getSyncDetails(syncTrackerSearchCriteria);
		syncTypeList = syncTrackerDAO.getSyncTypes();
		statusList = syncTrackerDAO.getStatuses();
		return SUCCESS;
	}

	public String getErrorMessgeById(){
		errorMessage = syncTrackerDAO.getErrorMessageById(id);
		return ERROR_MESSAGE;	
	}
	
	public String displaySync() throws Exception {
		syncTypeList = syncTrackerDAO.getSyncTypes();
		statusList = syncTrackerDAO.getStatuses();
		return SUCCESS;
	}

	public void setSyncTrackerDAO(SyncTrackerDAO syncTrackerDAO) {
		this.syncTrackerDAO = syncTrackerDAO;
	}
	
	public void setStartDate(Date startDate) {
		syncTrackerSearchCriteria.setStartDate(startDate);
	}

	public void setEndDate(Date endDate) {
		syncTrackerSearchCriteria.setEndDate(endDate);
	}

	public Date getStartDate() {
		return syncTrackerSearchCriteria.getStartDate();
	}

	public Date getEndDate() {
		return syncTrackerSearchCriteria.getEndDate();
	}
	
	public SyncTrackerSearchCriteria getSyncSummaryCriteria() {
		return syncTrackerSearchCriteria;
	}

	public void setSyncSummaryCriteria(SyncTrackerSearchCriteria syncTrackerSearchCriteria) {
		this.syncTrackerSearchCriteria = syncTrackerSearchCriteria;
	}

	public List<SyncTracker> getSyncTrackerList() {
		return syncTrackerList;
	}

	public List<String> getStatusList() {
		return statusList;
	}

	public List<String> getSyncTypeList() {
		return syncTypeList;
	}

	public void prepare() throws Exception {
		syncTypeList = syncTrackerDAO.getSyncTypes();
		statusList = syncTrackerDAO.getStatuses();
	}
	
	public String getShowMuleLogo() {
		return showMuleLogo;
	}

	public SyncTrackerSearchCriteria getSyncTrackerSearchCriteria() {
		return syncTrackerSearchCriteria;
	}

	public void setSyncTrackerSearchCriteria(
			SyncTrackerSearchCriteria syncTrackerSearchCriteria) {
		this.syncTrackerSearchCriteria = syncTrackerSearchCriteria;
	}
	
	public void setId(Long id) {
		this.id = id;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
}

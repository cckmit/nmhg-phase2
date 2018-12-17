package tavant.twms.domain.common;

import java.util.List;
import java.util.Map;
import java.util.Set;



public class TaskCriteria {
	private Map<String, List<Object>> daysBUMapConsideredForDenying; 
	private Map<String, List<Object>> params;
	private String configParam;
	private List<String> endTransitions;
	private Set<String> ignoreTasks;
	private Map<String, RejectionReason> rejectionReasonMap;
	private String internalComment;
	private Map<String, String> buWiseFilterColumns;
	private Map<String, List<Object>> pendingOverDueDaysBUMapForEmail; 

	public Map<String, List<Object>> getPendingOverDueDaysBUMapForEmail() {
		return pendingOverDueDaysBUMapForEmail;
	}
	public void setPendingOverDueDaysBUMapForEmail(
			Map<String, List<Object>> pendingOverDueDaysBUMapForEmail) {
		this.pendingOverDueDaysBUMapForEmail = pendingOverDueDaysBUMapForEmail;
	}
	public String getInternalComment() {
		return internalComment;
	}
	public void setInternalComment(String internalComment) {
		this.internalComment = internalComment;
	}
	public Map<String, List<Object>> getDaysBUMapConsideredForDenying() {
		return daysBUMapConsideredForDenying;
	}
	public void setDaysBUMapConsideredForDenying(
			Map<String, List<Object>> daysBUMapConsideredForDenying) {
		this.daysBUMapConsideredForDenying = daysBUMapConsideredForDenying;
	}
	public Map<String, List<Object>> getParams() {
		return params;
	}
	public void setParams(Map<String, List<Object>> params) {
		this.params = params;
	}
	public String getConfigParam() {
		return configParam;
	}
	public void setConfigParam(String configParam) {
		this.configParam = configParam;
	}
	public List<String> getEndTransitions() {
		return endTransitions;
	}
	public void setEndTransitions(List<String> endTransitions) {
		this.endTransitions = endTransitions;
	}
	public Set<String> getIgnoreTasks() {
		return ignoreTasks;
	}
	public void setIgnoreTasks(Set<String> ignoreTasks) {
		this.ignoreTasks = ignoreTasks;
	}
	public Map<String, RejectionReason> getRejectionReasonMap() {
		return rejectionReasonMap;
	}
	public void setRejectionReasonMap(
			Map<String, RejectionReason> rejectionReasonMap) {
		this.rejectionReasonMap = rejectionReasonMap;
	}
	public Map<String, String> getBuWiseFilterColumns() {
		return buWiseFilterColumns;
	}
	public void setBuWiseFilterColumns(Map<String, String> buWiseFilterColumns) {
		this.buWiseFilterColumns = buWiseFilterColumns;
	}
}

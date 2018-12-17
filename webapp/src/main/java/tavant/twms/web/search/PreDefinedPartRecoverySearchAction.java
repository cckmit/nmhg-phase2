package tavant.twms.web.search;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

import tavant.twms.domain.claim.RecoveryClaimService;
import tavant.twms.domain.claim.RecoveryClaimState;
import tavant.twms.domain.partrecovery.PartRecoverySearchCriteria;
import tavant.twms.domain.partreturn.PartReturnSearchCriteria;
import tavant.twms.domain.partreturn.PartReturnStatus;
import tavant.twms.domain.query.SavedQuery;
import tavant.twms.domain.query.SavedQueryService;
import tavant.twms.infra.ListCriteria;
import tavant.twms.infra.PageResult;
import tavant.twms.web.inbox.SummaryTableAction;
import tavant.twms.web.inbox.SummaryTableColumn;

@SuppressWarnings("serial")
public class PreDefinedPartRecoverySearchAction extends SummaryTableAction {

	private PartRecoverySearchCriteria partRecoverySearchCriteria;
	RecoveryClaimService recoveryClaimService;
	private String searchString;
	private Long savedQueryId;
	private SavedQueryService savedQueryService;
	private boolean notATemporaryQuery;
	private String context;
	private String searchQueryName;
	private List<PartReturnStatus> partRecoveryStatus = new ArrayList<PartReturnStatus>();

	private static Logger logger = LogManager
			.getLogger(PreDefinedPartRecoverySearchAction.class);
	
	public String deletePredefinedQuery(){
		if(savedQueryId != null){
			savedQueryService.deleteQueryWithId(savedQueryId);
		}
		return SUCCESS;
	}

	@Override
	protected PageResult<?> getBody() {
		if (partRecoverySearchCriteria == null) {
			if (notATemporaryQuery) {
				SavedQuery savedQuery = savedQueryService
						.findById(savedQueryId);
				searchString = savedQuery.getSearchQuery();
				XStream xstream = new XStream(new DomDriver());
				partRecoverySearchCriteria = (PartRecoverySearchCriteria) xstream
						.fromXML(searchString);
			} else {
				partRecoverySearchCriteria = (PartRecoverySearchCriteria) session
						.get("partRecoverySearchCriteria");
			}
		}
		partRecoverySearchCriteria.setPageSpecification(getCriteria()
				.getPageSpecification());
		addSortCriteria(partRecoverySearchCriteria);
		addFilterCriteria(partRecoverySearchCriteria);
		PageResult<?> partRecovery = null;
		try {
			partRecovery = this.recoveryClaimService
					.findAllRecoveryClaimsMatchingCriteria(partRecoverySearchCriteria);
		} catch (Exception e) {
			logger.error("Exception Occurred is ", e);
			e.printStackTrace();
		}

		return partRecovery;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected List<SummaryTableColumn> getHeader() {

		if (partRecoverySearchCriteria != null) {
			if (notATemporaryQuery) {
				domainCriteriaToString();
			} else {
				session.put("partRecoverySearchCriteria", partRecoverySearchCriteria);
			}
		}
		this.tableHeadData = new ArrayList<SummaryTableColumn>();
		this.tableHeadData.add(new SummaryTableColumn("", "recoveryClaimId", 0, "number", "recoveryClaimId", false,
				true, true, false));
		this.tableHeadData.add(new SummaryTableColumn("columnTitle.common.recClaimNo", "recoveryClaimNumber", 11,
				"string", "recoveryClaimNumber", true, false, false, false));
		this.tableHeadData.add(new SummaryTableColumn("columnTitle.common.model",
				"claim.itemReference.unserializedItem.model.name", 12, "String"));
		this.tableHeadData.add(new SummaryTableColumn("columnTitle.inventorySearchAction.serial_no",
				"claim.itemReference.referredInventoryItem.serialNumber", 11, "String"));
		return tableHeadData;
	}

	public void domainCriteriaToString() {
		if (savedQueryId == null) {
			if (partRecoverySearchCriteria != null) {
				XStream xstream = new XStream(new DomDriver());
				searchString = xstream.toXML(partRecoverySearchCriteria);
				SavedQuery savedQuery = new SavedQuery();
				savedQuery.setSearchQuery(searchString);
				savedQuery.setSearchQueryName(searchQueryName);
				savedQuery.setContext(context);
				try {
					savedQueryService.saveSearchQuery(savedQuery);
					savedQueryId = savedQuery.getId();
				} catch (Exception e) {
					logger.error("Exception occured is", e);
					e.printStackTrace();
				}
			}
		} else {
			try {
				SavedQuery savedQuery = savedQueryService
						.findById(savedQueryId);
				XStream xstream = new XStream(new DomDriver());
				searchString = xstream.toXML(partRecoverySearchCriteria);
				savedQuery.setSearchQuery(searchString);
				savedQuery.setContext(context);
				savedQuery.setSearchQueryName(searchQueryName);
				savedQueryService.update(savedQuery);
			} catch (Exception e) {
				logger.error("Exception occured is", e);
				e.printStackTrace();
			}
		}
	}

	private void addSortCriteria(ListCriteria criteria) {
		for (String[] sort : sorts) {
			String sortOnColumn = sort[0];
			boolean ascending = !sort[1].equals(SORT_DESCENDING);
			criteria.addSortCriteria(sortOnColumn, ascending);
		}
	}

	private void addFilterCriteria(ListCriteria criteria) {
		for (String filterName : filters.keySet()) {
			String filterValue = filters.get(filterName);
			criteria.addFilterCriteria(filterName, filterValue);
		}
	}

	public String showPreDefinedPartRecoverySearchQuery() {
		partRecoveryStatus = recoveryClaimService
				.findAllStatusForPartRecovery();
		if (partRecoverySearchCriteria == null) {
			if (notATemporaryQuery) {
				SavedQuery savedQuery = savedQueryService
						.findById(savedQueryId);
				searchString = savedQuery.getSearchQuery();
				XStream xstream = new XStream(new DomDriver());
				partRecoverySearchCriteria = (PartRecoverySearchCriteria) xstream
						.fromXML(searchString);
			} else {
				partRecoverySearchCriteria = (PartRecoverySearchCriteria) session
						.get("partRecoverySearchCriteria");
			}
		}
		return SUCCESS;
	}

	public String validateSearchFields() throws Exception {
		if (notATemporaryQuery) {
			if (StringUtils.isBlank(searchQueryName)) {
				addActionError("error.predefinedsearch.queryLabel.mandatory");
				return INPUT;
				//Fix for NMHGSLMS-1209
			} else if (null == savedQueryId && savedQueryService
					.isQueryNameUniqueForUserAndContext(searchQueryName,context)) {
				addActionError("error.predefinedsearch.queryLabel.duplicate");
				return INPUT;
			}
		}
		return SUCCESS;
	}

	public String searchExpression() {
		partRecoveryStatus = recoveryClaimService
				.findAllStatusForPartRecovery();
		return SUCCESS;
	}

	public PartRecoverySearchCriteria getPartRecoverySearchCriteria() {
		return partRecoverySearchCriteria;
	}

	public void setPartRecoverySearchCriteria(
			PartRecoverySearchCriteria partRecoverySearchCriteria) {
		this.partRecoverySearchCriteria = partRecoverySearchCriteria;
	}

	public boolean isNotATemporaryQuery() {
		return notATemporaryQuery;
	}

	public void setNotATemporaryQuery(boolean notATemporaryQuery) {
		this.notATemporaryQuery = notATemporaryQuery;
	}

	public String getContext() {
		return context;
	}

	public void setContext(String context) {
		this.context = context;
	}

	public String getSearchQueryName() {
		return searchQueryName;
	}

	/** 
	* @param searchQueryName 
	* Updated for SLMSPROD-747 
	* Handled the single,double quote and other types of single quotes 
	* which can harm opening the saved query. 
	*/ 
	public void setSearchQueryName(String searchQueryName) {
		if (searchQueryName != null) {
			String updatedQueryName = searchQueryName.replaceAll(
					"\\u0022|\u0026|\u0027|\u00B4|\u2018|\u2019|\u201C|\u201D|\u201E", "");
			this.searchQueryName = updatedQueryName;
		} else {
			this.searchQueryName = searchQueryName;
		}
	}

	public String getSearchString() {
		return searchString;
	}

	public void setSearchString(String searchString) {
		this.searchString = searchString;
	}

	public Long getSavedQueryId() {
		return savedQueryId;
	}

	public void setSavedQueryId(Long savedQueryId) {
		this.savedQueryId = savedQueryId;
	}

	public SavedQueryService getSavedQueryService() {
		return savedQueryService;
	}

	public void setSavedQueryService(SavedQueryService savedQueryService) {
		this.savedQueryService = savedQueryService;
	}

	public RecoveryClaimService getRecoveryClaimService() {
		return recoveryClaimService;
	}

	public void setRecoveryClaimService(
			RecoveryClaimService recoveryClaimService) {
		this.recoveryClaimService = recoveryClaimService;
	}

	public List<PartReturnStatus> getPartRecoveryStatus() {
		return partRecoveryStatus;
	}

	public void setPartRecoveryStatus(List<PartReturnStatus> partRecoveryStatus) {
		this.partRecoveryStatus = partRecoveryStatus;
	}

}

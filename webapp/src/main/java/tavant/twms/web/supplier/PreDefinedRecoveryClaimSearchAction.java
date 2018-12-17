package tavant.twms.web.supplier;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import tavant.twms.domain.claim.ClaimSearchCriteria;
import tavant.twms.domain.claim.RecoveryClaimService;
import tavant.twms.domain.claim.RecoveryClaimState;
import tavant.twms.domain.claim.RecoveryClaimStateComparator;
import tavant.twms.domain.query.SavedQuery;
import tavant.twms.domain.query.SavedQueryService;
import tavant.twms.domain.supplier.RecoveryClaimCriteria;
import tavant.twms.infra.PageResult;
import tavant.twms.web.inbox.SummaryTableAction;
import tavant.twms.web.inbox.SummaryTableColumn;
import tavant.twms.web.inbox.SummaryTableColumnOptions;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

@SuppressWarnings("serial")
public class PreDefinedRecoveryClaimSearchAction extends SummaryTableAction {

	private static Logger logger = Logger
			.getLogger(PreDefinedRecoveryClaimSearchAction.class);
	private RecoveryClaimCriteria recoveryClaimCriteria;
	private SavedQueryService savedQueryService;
	private RecoveryClaimService recoveryClaimService;
	
	private Long savedQueryId;
	private boolean notATemporaryQuery;
	private String searchQueryName;
	private String context;
	private boolean isSaveQuery;

	public boolean isSaveQuery() {
		return isSaveQuery;
	}

	public void setSaveQuery(boolean isSaveQuery) {
		this.isSaveQuery = isSaveQuery;
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

	public String showSearchPage() {
		return SUCCESS;
	}
	
	public String deletePredefinedQuery(){
		if(savedQueryId != null){
			savedQueryService.deleteQueryWithId(savedQueryId);
		}
		return SUCCESS;
	}

	@Override
	protected PageResult<?> getBody() {
		if (recoveryClaimCriteria == null) {
			if (notATemporaryQuery) {
				fetchSearchCriteriaFromDB();
			} else {
				recoveryClaimCriteria = (RecoveryClaimCriteria) session
						.get("recClaimSearchCriteria");
			}
		}
		return recoveryClaimService.findRecClaimsForPredefinedSearch(
				recoveryClaimCriteria, getCriteria());
	}

	private void fetchSearchCriteriaFromDB() {
		String xml = savedQueryService.findById(savedQueryId).getSearchQuery();
		XStream xstream = new XStream(new DomDriver());
		recoveryClaimCriteria = (RecoveryClaimCriteria) xstream.fromXML(xml);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected List<SummaryTableColumn> getHeader() {
		if (recoveryClaimCriteria != null) {
			if (notATemporaryQuery && isSaveQuery) {
				persistSearchConditions();
			} else {
				session.put("recClaimSearchCriteria", recoveryClaimCriteria);
			}
		}
		List<SummaryTableColumn> header = new ArrayList<SummaryTableColumn>();
		header.add(new SummaryTableColumn("columnTitle.common.Hidden", "id",
				0, false, true, true, false));
		header.add(new SummaryTableColumn("columnTitle.common.recClaimNo",
				"recoveryClaimNumber", 15, "string", "recoveryClaimNumber", true,
				false, false, false));
		header.add(new SummaryTableColumn("columnTitle.common.status",
				"recoveryClaimState.state", 15, "string", SummaryTableColumnOptions.NO_SORT_NO_FILTER_COL));
		if(getLoggedInUser().isInternalUser()){
		header.add(new SummaryTableColumn("columnTitle.listContracts.supplier_name",
				"contract.supplier.name", 20, "string"));
		header.add(new SummaryTableColumn("columnTitle.supplier.supplierNumber",
				"contract.supplier.supplierNumber", 10, "string"));
		}
		header.add(new SummaryTableColumn("columnTitle.supplier.recoveryAmount",
				"totalRecoveredAmount", 15, "Money", SummaryTableColumnOptions.NO_SORT_NO_FILTER_COL));
		header.add(new SummaryTableColumn("label.inboxView.claimPartReturnStatus",
				"partReturnStatus", 15, "String", SummaryTableColumnOptions.NO_SORT_NO_FILTER_COL));
		header.add(new SummaryTableColumn("columnTitle.newClaim.createdOn",
				"d.createdOn", 10, "date"));
		if(this.isExportAction()) {
			header.add(new SummaryTableColumn("label.section.replacedPartsCost",
					"getCostLineItem('Oem Parts').recoveredCost", 10, "string"));
			header.add(new SummaryTableColumn("label.section.nonReplacedPartsCost",
					"getCostLineItem('Non Oem Parts').recoveredCost", 10, "string"));
			header.add(new SummaryTableColumn("label.recovery.excel.miscTotal",
					"getCostLineItem('Miscellaneous Parts').recoveredCost", 10, "string"));
			header.add(new SummaryTableColumn("label.recovery.excel.LaborTotal",
					"getCostLineItem('Labor').recoveredCost", 10, "string"));
			header.add(new SummaryTableColumn("label.recovery.excel.incidentalTotal",
					"incidentalCost", 10, "string"));
			header.add(new SummaryTableColumn("label.recovery.excel.TravelTotal",
					"travelCost", 10, "string"));
		}
		return header;
	}

	public String showSearchConstraints() {
		if (recoveryClaimCriteria == null) {
			if (notATemporaryQuery) {
				fetchSearchCriteriaFromDB();
			} else {
				recoveryClaimCriteria = (RecoveryClaimCriteria) session
						.get("recClaimSearchCriteria");
			}
		}
		return SUCCESS;
	}

	private void persistSearchConditions() {
		if (savedQueryId == null) {
			if (recoveryClaimCriteria != null) {
				XStream xstream = new XStream(new DomDriver());
				SavedQuery savedQuery = new SavedQuery();
				savedQuery.setSearchQuery(xstream.toXML(recoveryClaimCriteria));
				savedQuery.setContext(context);
				savedQuery.setSearchQueryName(searchQueryName);
				try {
					savedQueryService.saveSearchQuery(savedQuery);
				} catch (Exception e) {
					logger.error("Error saving query in database" + e);
				}
				savedQueryId = savedQuery.getId();

			}
		} else {
			try {
				SavedQuery savedQuery = savedQueryService
						.findById(savedQueryId);
				XStream xstream = new XStream(new DomDriver());
				savedQuery.setSearchQuery(xstream.toXML(recoveryClaimCriteria));
				savedQuery.setContext(context);
				savedQuery.setSearchQueryName(searchQueryName);
				savedQueryService.update(savedQuery);
			} catch (Exception e) {
				logger.error("Exception occured is", e);
				e.printStackTrace();
			}
		}

	}

	public String validateSearchFields() throws Exception {
		if (notATemporaryQuery) {
			if (StringUtils.isBlank(searchQueryName)) {
				addActionError("error.predefinedsearch.queryLabel.mandatory");
				//Fix for NMHGSLMS-1209
			} else if (null == savedQueryId && savedQueryService
					.isQueryNameUniqueForUserAndContext(searchQueryName,context)) {
				addActionError("error.predefinedsearch.queryLabel.duplicate");
			}
		}
		if(recoveryClaimCriteria != null){
			if(recoveryClaimCriteria.getStartClaimPayDate()!=null && recoveryClaimCriteria.getEndClaimPayDate()!=null
	        		&& recoveryClaimCriteria.getStartClaimPayDate().isAfter(recoveryClaimCriteria.getEndClaimPayDate())){
				addActionError("error.partSource.invalidClaimPayFromDate");
			}
			if(recoveryClaimCriteria.getStartClosedDate()!=null && recoveryClaimCriteria.getEndClosedDate()!=null
	        		&& recoveryClaimCriteria.getStartClosedDate().isAfter(recoveryClaimCriteria.getEndClosedDate())){
				addActionError("error.partSource.invalidClosedFromDate");
			}
			if(recoveryClaimCriteria.getStartWarrantyRequestDate()!=null && recoveryClaimCriteria.getEndWarrantyRequestDate()!=null
	        		&& recoveryClaimCriteria.getStartWarrantyRequestDate().isAfter(recoveryClaimCriteria.getEndWarrantyRequestDate())){
				addActionError("error.partSource.invalidWarrantyRequestFromDate");
			}
		}
		if(hasActionErrors()){
			return INPUT;
		}
		else{
			isSaveQuery= true;
			return SUCCESS;
		}
		
	}

	public RecoveryClaimState[] getAllRecoveryClaimStates() {
		RecoveryClaimState[] rcs = RecoveryClaimState.values();
		RecoveryClaimStateComparator recoveryClaimStateComparator = new RecoveryClaimStateComparator();
		Arrays.sort(rcs, recoveryClaimStateComparator);
		return rcs;
	}

	public RecoveryClaimCriteria getRecoveryClaimCriteria() {
		return recoveryClaimCriteria;
	}

	public void setRecoveryClaimCriteria(
			RecoveryClaimCriteria recoveryClaimCriteria) {
		this.recoveryClaimCriteria = recoveryClaimCriteria;
	}

	public void setSavedQueryService(SavedQueryService savedQueryService) {
		this.savedQueryService = savedQueryService;
	}

	public void setRecoveryClaimService(
			RecoveryClaimService recoveryClaimService) {
		this.recoveryClaimService = recoveryClaimService;
	}

	public Long getSavedQueryId() {
		return savedQueryId;
	}

	public void setSavedQueryId(Long savedQueryId) {
		this.savedQueryId = savedQueryId;
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

}

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
package tavant.twms.web.rules;

import com.opensymphony.xwork2.validator.ValidationException;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import tavant.twms.dateutil.TWMSDateFormatUtil;
import tavant.twms.domain.businessobject.BusinessObjectModelFactory;
import tavant.twms.domain.orgmodel.User;
import tavant.twms.domain.query.SavedQuery;
import tavant.twms.domain.rules.DomainPredicate;
import tavant.twms.domain.rules.FieldTraversal;
import tavant.twms.domain.rules.PredicateAdministrationException;

import java.util.List;
import java.util.SortedMap;

/**
 *
 * @author roopali.agrawal
 *
 */
public class SearchExpressionEditor extends ExpressionEditor {
	private static Logger logger = LogManager
			.getLogger(SearchExpressionEditor.class);
	private static final String MULTI_CLAIM_MAINTAIN_SEARCH_SUCCESS = "multiClaimMaintainSearchSuccess";
	private String savedQueryId;
	private String domainPredicateId;
	private SavedQuery savedQuery;
	boolean notATemporaryQuery;
	private static String CLAIM_SEARCH_SUCCESS = SUCCESS;
	private static String INVENTORY_SEARCH_SUCCESS = "inventorysearchsuccess";
	private static String PART_RETURN_SEARCH_SUCCESS = "partreturnsearchsuccess";
	private static String RECOVERY_CLAIM_SEARCH_SUCCESS = "recoveryclaimsearchsuccess";
	private static String ITEM_SEARCH_SUCCESS = "itemsearchsuccess";
	private static String MULTI_REC_CLAIM_SEARCH_SUCCESS = "multiRecClaimMaintainSearchSuccess";
	private static String CREATE_INVENTORY_LABEL_SEARCH_SUCCESS = "createInventoryLabelSearchSuccess";
    private String isMultiClaimMaintenance;
	private String isMultiRecClaimMaintainace;
	private static final String TRANSFER_REPROCESS_SUCCESS = "transferReprocessSuccess";
	private String isTransferOrReProcess;
	private String selectedBusinessUnit;

	public SearchExpressionEditor() {
		super();
	}

	@Override
	public void prepare() throws Exception {
		super.prepare();
		if (this.savedQueryId != null && !"".equals(this.savedQueryId.trim())) {
			this.savedQuery = this.predicateAdministrationService
					.findSavedQueryById(Long.parseLong(this.savedQueryId));
			this.notATemporaryQuery = !(this.savedQuery.isTemporary());
		}
	}

	@Override
	public String saveExpression() throws Exception {
		boolean isUpdate = this.domainPredicate.getId() != null;
		try {
            setInvertPredicate(this.request.getParameter("invertPredicate") != null);
			prepareDomainPredicate(this.notATemporaryQuery);
			String messageKey = "message.manageBusinessCondition.creatSuccess";

			if (isUpdate) {
				if (logger.isDebugEnabled()) {
					logger.debug("Updating expression with id "
							+ this.domainPredicate.getId());
				}
				this.savedQuery.setDomainPredicate(this.domainPredicate);
				this.savedQuery.setTemporary(!this.notATemporaryQuery);
				this.predicateAdministrationService
						.updateSavedQuery(this.savedQuery);
				messageKey = "message.manageBusinessCondition.updateSuccess";
				this.request.setAttribute("domainPredicateId",
						this.domainPredicate.getId());
				this.request.setAttribute("savedQueryId", this.savedQuery
						.getId());
			} else {
				if (logger.isDebugEnabled()) {
					logger.debug("created new intance"
							+ this.domainPredicate.getId());
				}
				createExpression();
			}

			addActionMessage(getText(messageKey));

		} catch (ValidationException e) {
			return INPUT;
		} catch (RuntimeException e) {
			logger.error("Failed to convert back from JSON", e);
			addActionError(getText("error.manageBusinessCondition.unknown"));
			return INPUT;
		} catch (PredicateAdministrationException ex) {
			logger.error("Failed to convert back from JSON", ex);
			addActionError(getText("error.manageBusinessCondition.unknown"));
			return INPUT;
		}

		if (isUpdate) {
			prepare();
			return getSuccessMapping();
		} else {
			return getSuccessMapping();
		}
	}

	public boolean isPageReadOnly() {
		return false;
	}
	
	private String getSuccessMapping() {
        if (BusinessObjectModelFactory.INVENTORY_SEARCHES.equals(this.context)
        		|| BusinessObjectModelFactory.AMER_INVENTORY_SEARCHES.equals(this.context)) {
			return INVENTORY_SEARCH_SUCCESS;
		} else if (BusinessObjectModelFactory.PART_RETURN_SEARCHES
				.equals(this.context)) {
			return PART_RETURN_SEARCH_SUCCESS;
		} else if (BusinessObjectModelFactory.RECOVERY_CLAIM_SEARCHES
				.equals(this.context)
				&& this.isMultiRecClaimMaintainace.equals(ExpressionEditor.TRUE)) {
			return MULTI_REC_CLAIM_SEARCH_SUCCESS;
		} else if (BusinessObjectModelFactory.RECOVERY_CLAIM_SEARCHES
				.equals(this.context)) {
			return RECOVERY_CLAIM_SEARCH_SUCCESS;
		} else if (BusinessObjectModelFactory.CLAIM_SEARCHES
				.equals(this.context)
				&& this.isMultiClaimMaintenance.equals(ExpressionEditor.TRUE)) {
			return MULTI_CLAIM_MAINTAIN_SEARCH_SUCCESS;
		} else if (BusinessObjectModelFactory.CLAIM_SEARCHES
				.equals(this.context)
				&& this.isTransferOrReProcess.equals(TRUE)) {
			return TRANSFER_REPROCESS_SUCCESS;
		} else if (BusinessObjectModelFactory.ITEM_SEARCHES.equals(this.context)
				|| BusinessObjectModelFactory.BRAND_ITEM_SEARCHES.equals(this.context)) {
			return ITEM_SEARCH_SUCCESS;
		}

		return SUCCESS;
	}

	@Override
	protected List<DomainPredicate> findClashingPredicates(
			DomainPredicate examplePredicate) {
		return this.predicateAdministrationService.findClashingPredicates(
				this.context, examplePredicate);
	}

	private void createExpression() throws PredicateAdministrationException {
		SavedQuery scr = new SavedQuery();
		scr.setDomainPredicate(this.domainPredicate);
		scr.setCreatedBy(getLoggedInUser());
		if (!this.notATemporaryQuery) {
			scr.setTemporary(true);
		}
		this.predicateAdministrationService.saveSavedQuery(scr);
		this.request.setAttribute("domainPredicateId", scr
				.getDomainPredicate().getId());
		/*
		 * domainPredicateId=scr.getDomainPredicate().getId().toString();
		 * savedQueryId=scr.getId().toString();
		 */
		this.request.setAttribute("savedQueryId", scr.getId());
	}

	@Override
	protected void filterDataElementsForLoggedInUser(
			SortedMap<String, FieldTraversal> dataElements) {
		User loggedinUser = getLoggedInUser();
		if (loggedinUser != null && loggedinUser.hasOnlyRole("dealer")) {
			dataElements.remove("claim.forDealer");
		}
	}

	public String getDomainPredicateId() {
		return this.domainPredicateId;
	}

	public void setDomainPredicateId(String domainPredicateId) {
		this.domainPredicateId = domainPredicateId;
	}

	@Override
	public String deleteExpression() throws PredicateAdministrationException {
		SavedQuery sq = this.predicateAdministrationService
				.findSavedQueryById(Long.parseLong(this.savedQueryId));
		this.predicateAdministrationService.deleteSavedQuery(sq);
		addActionMessage(getText("message.manageBusinessCondition.deleteSuccess"));
		return SUCCESS;
	}

	public String copyExpression() {
		this.domainPredicate.setName("copy of "
				+ this.domainPredicate.getName());
		this.domainPredicate.setId(null);
		this.domainPredicateId = null;
		this.savedQueryId = null;
		this.id = null;
		return SUCCESS;
	}

	public String getSavedQueryId() {
		return this.savedQueryId;
	}

	public void setSavedQueryId(String savedQueryId) {
		this.savedQueryId = savedQueryId;
	}

	public boolean isNotATemporaryQuery() {
		return this.notATemporaryQuery;
	}

	public void setNotATemporaryQuery(boolean saveQuery) {
		this.notATemporaryQuery = saveQuery;
	}

	public SavedQuery getSavedQuery() {
		return this.savedQuery;
	}

	public void setSavedQuery(SavedQuery savedQuery) {
		this.savedQuery = savedQuery;
	}

	public String getIsMultiClaimMaintenance() {
		return this.isMultiClaimMaintenance;
	}

	public void setIsMultiClaimMaintenance(String isMultiClaimMaintenance) {
		this.isMultiClaimMaintenance = isMultiClaimMaintenance;
	}

	public String getIsMultiRecClaimMaintainace() {
		return isMultiRecClaimMaintainace;
	}

	public void setIsMultiRecClaimMaintainace(String isMultiRecClaimMaintainace) {
		this.isMultiRecClaimMaintainace = isMultiRecClaimMaintainace;
	}

	public String getIsTransferOrReProcess() {
		return isTransferOrReProcess;
	}

	public void setIsTransferOrReProcess(String isTransferOrReProcess) {
		this.isTransferOrReProcess = isTransferOrReProcess;
	}
	
	public String getDateFormatForLoggedInUser() {
		{
			return TWMSDateFormatUtil.getDateFormatForLoggedInUser();
		}
	}

	public String getSelectedBusinessUnit() {
		return selectedBusinessUnit;
	}

	public void setSelectedBusinessUnit(String selectedBusinessUnit) {
		this.selectedBusinessUnit = selectedBusinessUnit;
	}
	
	

}

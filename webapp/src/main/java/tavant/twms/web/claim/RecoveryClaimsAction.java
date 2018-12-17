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
package tavant.twms.web.claim;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import tavant.twms.domain.claim.ClaimService;
import tavant.twms.domain.orgmodel.ServiceProvider;
import tavant.twms.infra.ListCriteria;
import tavant.twms.infra.PageResult;
import tavant.twms.infra.PageSpecification;
import tavant.twms.web.inbox.SummaryTableAction;
import tavant.twms.web.inbox.SummaryTableColumn;
import tavant.twms.web.inbox.SummaryTableColumnOptions;
import tavant.twms.worklist.WorkListCriteria;

/**
 * @author Ghanashyam Das
 * This class is responsible to show Pending Recovery Initiation inbox listing since, listing related to this inbox is no longer shown from the task instance  
 */
public class RecoveryClaimsAction extends SummaryTableAction {
	private static final Logger logger = Logger.getLogger(RecoveryClaimsAction.class);

    private ClaimService claimService;
	
	@Override
	protected PageResult<?> getBody() {
		return claimService.findClaimsForRecovery(getCriteria());
	}

	@Override
	protected List<SummaryTableColumn> getHeader() {
		List<SummaryTableColumn> header = new ArrayList<SummaryTableColumn>();
		
		header.add(new SummaryTableColumn("columnTitle.newClaim.task", "id", 0,
				"String", "id", false, true, true, false));
		header.add(new SummaryTableColumn("label.inboxView.claimNumber",
				"claimNumber", 8, "String", "claimNumber",
				true, false, false, false));
		header.add(new SummaryTableColumn("label.inboxView.product",
				"claimedItems.itemReference.referredInventoryItem.ofType.product.name", 10, "string","claimedItems[0].itemReference.referredInventoryItem.ofType.product.name"));
		header.add(new SummaryTableColumn("label.inboxView.model",
				"claimedItems.itemReference.referredInventoryItem.ofType.model.name", 10, "string","claimedItems[0].itemReference.referredInventoryItem.ofType.model.name"));
		header.add(new SummaryTableColumn("label.inboxView.lastModifiedStatusDate","lastUpdatedOnDate", 10, "date", "lastUpdatedOnDate",
				false, false, false, false));
		header.add(new SummaryTableColumn("label.inboxView.claimPartReturnStatus","partReturnStatus", 16, "string", "partReturnStatus",
				SummaryTableColumnOptions.NO_SORT_NO_FILTER_COL));
		if (isExportAction()) {
			header.add(new SummaryTableColumn("label.inboxView.latestComments",
					"recoveryInfo.comments.first().comment", 10, "string","recoveryInfo.comments.first().comment"));
			header.add(new SummaryTableColumn("label.inboxView.lastCommentedBy",
					"recoveryInfo.comments.first().madeBy.CompleteNameAndLogin", 10, "string","recoveryInfo.comments.first().madeBy.CompleteNameAndLogin"));
			header.add(new SummaryTableColumn("label.inboxView.lastCommentedOn",
					"recoveryInfo.comments.first().madeOn", 10, "time","recoveryInfo.comments.first().madeOn"));
		}
		if (!isLoggedInUserADealer()) {
			header.add(new SummaryTableColumn("label.inboxView.servProviderName",
					"forDealer.name", 24, "string"));
		}
		header.add(new SummaryTableColumn("label.inboxView.claimType",
				"clmTypeName", 10, "string"));	
		header.add(new SummaryTableColumn("columnTitle.newClaim.causalPart",
				"serviceInformation.causalBrandPart.itemNumber", 12, "String"));
		
		return header;
	}
	
	public void addSortCriteria(ListCriteria criteria) {
        for (String[] sort : this.sorts) {
            String sortOnColumn = sort[0];
            if (!sortOnColumn.startsWith("claimedItems"))
                sortOnColumn = "claim." + sort[0];

            boolean ascending = !SORT_DESCENDING.equals(sort[1]);
            if (logger.isInfoEnabled()) {
                logger.info("Adding sort criteria " + sortOnColumn + " "
                        + (ascending ? "ascending" : "descending"));
            }
            criteria.addSortCriteria(sortOnColumn, ascending);
        }
	}

	protected void addFilterCriteria(ListCriteria criteria) {
        for (String filterName : this.filters.keySet()) {
            String filterValue = this.filters.get(filterName);
            if (!filterName.startsWith("claimedItems"))
                filterName = "claim." + filterName;
            if (isBuConfigAMER() && filterName.equals("claim.clmTypeName") && filterValue.toUpperCase().startsWith("U")){
				Pattern pattern = Pattern.compile("\\b(U|UN|UNI|UNIT)\\b");
				Matcher matcher = pattern.matcher(filters.get(filterName)
				.toUpperCase());
				if (matcher.find()){
					filterValue = "machine";
					}
				}
            if (logger.isInfoEnabled()) {
                logger.info("Adding filter criteria " + filterName + " : "
                        + filterValue);
            }
            criteria.addFilterCriteria(filterName, filterValue);
        }
	}
	
	@Override
	public ListCriteria getCriteria(){
		ListCriteria listCriteria = getListCriteria();
		addFilterCriteria(listCriteria);
		addSortCriteria(listCriteria);
		PageSpecification pageSpecification = new PageSpecification();
		pageSpecification.setPageSize(pageSize);
		pageSpecification.setPageNumber(page - 1);
		listCriteria.setPageSpecification(pageSpecification);
		return listCriteria;
	}

    public void setClaimService(ClaimService claimService) {
        this.claimService = claimService;
    }
}
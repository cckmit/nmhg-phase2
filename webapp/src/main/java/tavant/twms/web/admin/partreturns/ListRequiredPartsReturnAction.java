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
package tavant.twms.web.admin.partreturns;

import tavant.twms.domain.bu.BusinessUnit;
import tavant.twms.domain.catalog.CatalogRepository;
import tavant.twms.domain.claim.Criteria;
import tavant.twms.domain.orgmodel.DealerCriterion;
import tavant.twms.domain.orgmodel.ServiceProvider;
import tavant.twms.domain.partreturn.PartReturnDefinitionRepository;
import tavant.twms.infra.ListCriteria;
import tavant.twms.infra.PageResult;
import tavant.twms.infra.PageSpecification;
import tavant.twms.web.admin.ListCriteriaAction;
import tavant.twms.web.common.SessionUtil;
import tavant.twms.web.inbox.SummaryTableColumn;
import tavant.twms.web.inbox.SummaryTableAction;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.logging.Logger;

import org.springframework.util.StringUtils;

@SuppressWarnings("serial")
public class ListRequiredPartsReturnAction extends SummaryTableAction {

	private CatalogRepository catalogRepository;
	/*This is required as the getBody uses an SQL query to generate the result.
	And while filtering and sortingthe column names need to be used and 
	not the field names in the entity*/
	private static Map<String,String> columnAttributeMapping() {
	    Map<String,String> result = new HashMap<String, String>();
	    result.put("number","item_number");
	    result.put("description","description");
	    return result;
	}


	@Override
	protected String getAlias() {
		return "item";
	}
	
	@Override
	protected ListCriteria getCriteria() {
		PageSpecification pageSpecification = new PageSpecification();
		pageSpecification.setPageSize(pageSize);
		pageSpecification.setPageNumber(page - 1);

		ListCriteria listCriteria = new ListCriteria();
		addFilterCriteria(listCriteria);
		addSortCriteria(listCriteria);
		listCriteria.setPageSpecification(pageSpecification);
		return listCriteria;
	}
	
	private void addSortCriteria(ListCriteria criteria) {
		for (String[] sort : sorts) {
			String sortOnColumn = sort[0];
			boolean ascending = !sort[1].equals(SORT_DESCENDING);
			criteria.addSortCriteria(addAlias(columnAttributeMapping().get(sortOnColumn)), ascending);
		}
	}
	
	
	protected void addFilterCriteria(ListCriteria criteria) {
		for (String filterName : filters.keySet()) {
			String filterValue = filters.get(filterName).toUpperCase();
			criteria.addFilterCriteria(addAlias(columnAttributeMapping().get(filterName)), filterValue);
		}
	}
	
	private String addAlias(String sortOnColumn) {
		if (StringUtils.hasText(getAlias())) {
			return getAlias() + "." + sortOnColumn;
		}
		return sortOnColumn;
	}

	@Override
	protected PageResult<?> getBody() {
			Criteria criteria = new Criteria();
			criteria.setDealerCriterion(new DealerCriterion(
					getLoggedInUsersDealership()));
			return this.catalogRepository
					.findPartReturnItemsForDealer(getCriteria(), criteria);
		}

	@Override
	protected List<SummaryTableColumn> getHeader() {
		List<SummaryTableColumn> header = new ArrayList<SummaryTableColumn>();
			header.add(new SummaryTableColumn("Id", "id", 0, "String", "id",
					false, true, true, false));
			header.add(new SummaryTableColumn("", "label", 0, "String",
					"number", true, false, true, false));
			header.add(new SummaryTableColumn(
					"columnTitle.partReturnConfiguration.partNumber",
					"number", 20, "String"));
			header.add(new SummaryTableColumn(
					"columnTitle.common.itemCriterion.description",
					"description", 20, "String"));
		return header;
	}

	public void setCatalogRepository(
			CatalogRepository catalogRepository) {
		this.catalogRepository = catalogRepository;
	}
}
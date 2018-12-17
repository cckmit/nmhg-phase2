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
package tavant.twms.web.actions;

import org.apache.log4j.Logger;
import org.springframework.util.Assert;
import tavant.twms.domain.policy.Warranty;
import tavant.twms.domain.policy.WarrantyRepository;
import tavant.twms.infra.ListCriteria;
import tavant.twms.infra.PageResult;
import tavant.twms.infra.PageSpecification;
import tavant.twms.web.inbox.SummaryTableAction;
import tavant.twms.web.inbox.SummaryTableColumn;
import tavant.twms.worklist.InboxItemList;
import tavant.twms.worklist.WorkListCriteria;
import tavant.twms.worklist.customer.CustomerWorkListDao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author kiran.sg
 */
@SuppressWarnings("serial")
public class CustomerAction extends SummaryTableAction {

	private static Logger logger = Logger.getLogger(CustomerAction.class);

	private Map partReturnFields;

	private Warranty warranty;

	private WarrantyRepository warrantyRepository;

	private CustomerWorkListDao customerWorkListDao;

	private String taskName;

	private boolean preview;

	public String preview() {
		Assert.notNull(getId());
		warranty = warrantyRepository.findById(new Long(getId()));
		return SUCCESS;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected PageResult<?> getBody() {
		WorkListCriteria criteria = createCriteria();
		InboxItemList inboxItemList = customerWorkListDao.getWarrantiesForCustomer(criteria);
		List inboxItems = inboxItemList.getInboxItems();
		PageSpecification pageSpecification = criteria.getPageSpecification();
		int noOfPages = criteria.getPageSpecification().convertRowsToPages(
				inboxItemList.getTotalCount());
		return new PageResult<Warranty>(inboxItems, pageSpecification,
				noOfPages);
	}

	@Override
	protected List<SummaryTableColumn> getHeader() {
		List<SummaryTableColumn> tableHeadData = new ArrayList<SummaryTableColumn>();
		Assert.state(partReturnFields != null,
				"Column definitions are not being configured");
		Assert.hasText(taskName,
				"Task name hasn't been set for getting column definitions");
		Assert.state(partReturnFields.containsKey(taskName),
				"The configured column definitions ["
						+ partReturnFields.keySet()
						+ "] doesnt have a key for [" + taskName + "]");
		tableHeadData
				.addAll((Collection<? extends SummaryTableColumn>) partReturnFields
						.get(taskName));
		return tableHeadData;
	}

	private WorkListCriteria createCriteria() {
		WorkListCriteria criteria = new WorkListCriteria(getLoggedInUser());

		// Copy the ListCriteria to WorkListCriteria
		// TODO find a better way for this
		ListCriteria listCriteria = super.getCriteria();
		Map<String, String> filterCriteria = criteria.getFilterCriteria();
		for (String columnName : filterCriteria.keySet()) {
			criteria.addFilterCriteria(columnName, filterCriteria
					.get(columnName));
		}
		Map<String, String> sortCriteria = listCriteria.getSortCriteria();
		for (String columnName : sortCriteria.keySet()) {
			criteria.addSortCriteria(columnName, sortCriteria.get(columnName)
					.equals("asc") ? true : false);
		}
		criteria.setPageSpecification(listCriteria.getPageSpecification());

		criteria.setTaskName(getFolderName());
		if (logger.isInfoEnabled()) {
			logger.info("Folder Name : " + getFolderName());
		}
		return criteria;
	}

	public final void setPartReturnFields(Map partReturnFields) {
		this.partReturnFields = partReturnFields;
	}

	public final String getTaskName() {
		return taskName;
	}

	public final void setTaskName(String taskName) {
		this.taskName = taskName;
	}

	public Warranty getWarranty() {
		return warranty;
	}

	public void setWarranty(Warranty warranty) {
		this.warranty = warranty;
	}

	public Map getPartReturnFields() {
		return partReturnFields;
	}

	public CustomerWorkListDao getCustomerWorkListDao() {
		return customerWorkListDao;
	}

	public void setCustomerWorkListDao(CustomerWorkListDao customerWorkListDao) {
		this.customerWorkListDao = customerWorkListDao;
	}

	public WarrantyRepository getWarrantyRepository() {
		return warrantyRepository;
	}

	public void setWarrantyRepository(WarrantyRepository warrantyRepository) {
		this.warrantyRepository = warrantyRepository;
	}

	public boolean isPreview() {
		return preview;
	}

	public void setPreview(boolean preview) {
		this.preview = preview;
	}
}
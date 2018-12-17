package tavant.twms.web.inbox;

import static tavant.twms.web.upload.HeaderUtil.EXCEL;
import static tavant.twms.web.upload.HeaderUtil.setHeader;




import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ParameterAware;
import org.apache.struts2.interceptor.ServletResponseAware;
import org.json.JSONArray;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.StringUtils;


import tavant.twms.domain.orgmodel.Organization;
import tavant.twms.domain.orgmodel.ServiceProvider;
import tavant.twms.infra.BeanProvider;
import tavant.twms.infra.InstanceOfUtil;
import tavant.twms.infra.ListCriteria;
import tavant.twms.infra.PageResult;
import tavant.twms.infra.PageSpecification;
import tavant.twms.web.i18n.I18nActionSupport;
import tavant.twms.web.xls.writer.XLSCell;
import tavant.twms.web.xls.writer.XLSWriter;
import tavant.twms.worklist.WorkList;
import tavant.twms.worklist.WorkListService;
import tavant.twms.domain.query.view.InboxField;
import tavant.twms.domain.query.view.InboxView;
import tavant.twms.domain.query.view.InboxViewService;
import tavant.twms.domain.upload.controller.DataUploadConfig;
import tavant.twms.domain.view.DefaultFolderViewService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author janmejay.singh
 */
public abstract class SummaryTableAction extends I18nActionSupport implements
		ParameterAware {

	public static final double LABEL_COLUMN_WIDTH = 13;
	private static final Logger logger = Logger
			.getLogger(SummaryTableAction.class);

	protected List<SummaryTableColumn> tableHeadData;
	protected String jsonString;
	protected int pageSize = PAGE_SIZE_WITHOUT_PREVIEW;
	protected int page = 0;
	protected Long bodySize;
	public Long getBodySize() {
		return bodySize;
	}

	public void setBodySize(Long bodySize) {
		this.bodySize = bodySize;
	}

	protected String folderName;
	protected List<String[]> sorts = new ArrayList<String[]>(5);
	protected Map<String, String> filters = new HashMap<String, String>(5);
	protected WorkListService workListService;
	protected static final String SORT_DESCENDING = "dsc";
	protected String id;// the dataId of the row...
	private static final int PAGE_SIZE_WITH_PREVIEW = 10,
			PAGE_SIZE_WITHOUT_PREVIEW = 20;

	private XLSWriter xlsWriter;
	// Changed the access modifier to Public to have a access in other classes
	public static final int MAX_DOWNLOADABLE_ROWS = 500;
	
	private DataUploadConfig dataUploadConfig;
		
	public DataUploadConfig getDataUploadConfig() {
		return dataUploadConfig;
	}

	public void setDataUploadConfig(DataUploadConfig dataUploadConfig) {
		this.dataUploadConfig = dataUploadConfig;
	}

	private boolean downloadMaxResults;
	private int downloadPageNumber;
	private boolean downloadReport;
	private int reportMaxDownloadableRows;

	private boolean previewVisible;

	private List<InboxView> inboxViews;
	private InboxViewService inboxViewService;
	private DefaultFolderViewService defaultFolderViewService;
	private String inboxViewId;
	private String inboxViewSortField;
	private boolean inboxViewSortOrder;
	private boolean exportAction = false;	
	private static final String LOCATION_CODE = "location.code";
    private static final String WARRANTY_TYPE = "warrantyType.displayValue";
    private static final String WARRANTY_TYPE_CRITERIA = "forCriteria.wntyTypeName";
    private static final String CLAIM_TYPE = "forCriteria.clmTypeName";
	private static final String SHIPMENT_DESTINATION_CODE = "shipment.destination.code";
	private static final Pattern extPattern = Pattern.compile("\\b(E|EP|EPP)\\b");
	public int getMaxDownloadableRows() {
		return MAX_DOWNLOADABLE_ROWS;
	}
	
	public int getReportMaxDownloadableRows() {
		if(reportMaxDownloadableRows == 0)
			reportMaxDownloadableRows = dataUploadConfig.getExportRecordsLimit();
		return reportMaxDownloadableRows;
	}
	
	public int getDownloadPageNumber() {
		return downloadPageNumber;
	}

	public void setDownloadPageNumber(int downloadPageNumber) {
		this.downloadPageNumber = downloadPageNumber;
	}
	
	/**
	 * Override this method in case you want to customize retrieving of property
	 * value from the bean.
	 * 
	 * @return BeanProvider
	 */
	protected BeanProvider getBeanProvider() {
		return new DefaultPropertyResolver();
	}

	@Required
	public void setXlsWriter(XLSWriter xlsWriter) {
		this.xlsWriter = xlsWriter;
	}

	protected abstract List<SummaryTableColumn> getHeader();

	protected abstract PageResult<?> getBody();
	
	protected Long getListingSize() {
		return null;
	}

	private void generateI18NedHeadData() throws Exception {
		generateHeadData();
	}

	@Override
	public String execute() throws Exception {
		generateI18NedHeadData();
		return SUCCESS;
	}

	public String tableBody() throws Exception {
		pageSize = getCurrentComputedPageSize();
		generateI18NedHeadData();
		generateBodyData();
		return SUCCESS;
	}

	private int getCurrentComputedPageSize() {
		return previewVisible ? PAGE_SIZE_WITH_PREVIEW
				: PAGE_SIZE_WITHOUT_PREVIEW;
	}
	
	public String downloadReportData() {
		bodySize = getListingSize();
		if(bodySize > 0 && bodySize <= getReportMaxDownloadableRows()) {
			return "download";
		}else
			return "selectDownloadPage";
	}

	public void downloadListingData() throws Exception {
		exportAction = true;
		pageSize = downloadMaxResults ? (downloadReport ? getReportMaxDownloadableRows() : MAX_DOWNLOADABLE_ROWS)
				: getCurrentComputedPageSize();
		if(downloadMaxResults) {
			this.page = downloadPageNumber;
			setPage(downloadPageNumber);
		}
		setHeader(response, "data.xls", EXCEL);
		generateI18NedHeadData();
		xlsWriter.write(getListingData(), response.getOutputStream());
	}

	List<List<XLSCell>> getListingData() throws Exception {
		List<List<XLSCell>> listingData = new ArrayList<List<XLSCell>>();
		listingData.add(getListingHeader());
		listingData.addAll(getListingBody());
		return listingData;
	}

	List<XLSCell> getListingHeader() throws Exception {
		generateI18NedHeadData();
		List<XLSCell> header = new ArrayList<XLSCell>();
		for (SummaryTableColumn column : tableHeadData) {
			if (!(column.isHidden())) {
				if(column.getDataType().equals(SummaryTableColumn.MONEY))
					header.add(new XLSCell(getText("label.common.currency"), XLSCell.LEFT_ALIGN,true));
				header.add(new XLSCell(getText(column.getTitle()), XLSCell.LEFT_ALIGN,
						true));
			}
		}
		return header;
	}

	List<List<XLSCell>> getListingBody() throws Exception {
		List<List<XLSCell>> body = new ArrayList<List<XLSCell>>();
		PageResult<?> pageResult = getBody();
		if (pageResult != null) {
			for (Object object : pageResult.getResult()) {
				List<XLSCell> aRow = new ArrayList<XLSCell>();
				for (SummaryTableColumn column : tableHeadData) {
					if (!(column.isHidden())) {
						Object columnValue = getBeanProvider().getProperty(
								column.getExpression(), object);
						short alignment = column.getAlignment() == SummaryTableColumn.RIGHT ? XLSCell.RIGHT_ALIGN
								: XLSCell.LEFT_ALIGN;
						if(column.getDataType().equals(SummaryTableColumn.MONEY)) {
							String currency = "";
							String value = "";
							String []moneyParts = ((String)column.getRenderer()
									.valueForXLSCell(columnValue)).split(" ");
							if(moneyParts != null && moneyParts.length==2) {
								currency = moneyParts[0];
								value = moneyParts[1];
							}
							aRow.add(new XLSCell(currency, alignment));
							aRow.add(new XLSCell(value, alignment));
						}
						else if(column.getDataType().equalsIgnoreCase(SummaryTableColumn.DATE)){
								  aRow.add(new XLSCell(column.getRenderer()
										  .valueForUiCell(columnValue), alignment));
						}
						else {
							aRow.add(new XLSCell(column.getRenderer()
								.valueForXLSCell(columnValue), alignment));
						}
					}
				}
				body.add(aRow);
			}
		}
		return body;
	}

	private void generateHeadData(){
		setInboxViews();
		setDefaultInboxView();
		tableHeadData = getHeader();
	}

	@SuppressWarnings("unchecked")
	private void generateBodyData() throws JSONException {
		//setInboxViews();
		PageResult<?> pageResult = getBody();
		int totalPages = pageResult.getNumberOfPagesAvailable();
        long totalRecords = pageResult.getPageSpecification().getTotalRecords();
        boolean isNextPageAvailable = (totalPages > page);
		boolean isPrevPageAvailable = (page > 1);		
		if (logger.isInfoEnabled()) {
			logger.info("Total pages " + totalPages);
			logger.info("Current page " + getPage());
			logger.info("next page available " + isNextPageAvailable);
			logger.info("prev page available " + isPrevPageAvailable);
		}
		JSONArray data = new JSONArray();

		for (Object object : pageResult.getResult()) {
			Map row = new HashMap();
			for (SummaryTableColumn column : tableHeadData) {
				Object columnValue = null;
				if (getExpressionToModifyValue() != null
						&& getExpressionToModifyValue().containsKey(
								column.getExpression())) {
					columnValue = prepareOEMPartCrossRefForDisplay(object,
							column.getExpression());
				} else {
					columnValue = getBeanProvider().getProperty(
							column.getExpression(), object);
				}
				// changing return location to central logistic location for canadian dealer				
				if((column.getExpression().equalsIgnoreCase(LOCATION_CODE) || column.getExpression().equalsIgnoreCase(SHIPMENT_DESTINATION_CODE)) && isLoggedInUserACanadianDealer()){
					row.put(column.getId(), getCentralLogisticLocation());
				}else if((column.getExpression().equalsIgnoreCase(WARRANTY_TYPE) || column
						.getExpression().equalsIgnoreCase(
								WARRANTY_TYPE_CRITERIA))
						&& column.isDisplayDataFromProperty()){
					row.put(column.getId(), getText(column.getRenderer()
							.valueForUiCell(columnValue).toString()));
				}else if((column.getExpression().equalsIgnoreCase(CLAIM_TYPE)) && column.isDisplayDataFromProperty()){
					row.put(column.getId(), getText(column.getRenderer()
							.valueForUiCell(columnValue).toString()));
				}else{
					row.put(column.getId(), column.getRenderer().valueForUiCell(
							columnValue));
				}				
            }
			data.put(row);
		}

		setBodyData(data, isNextPageAvailable, isPrevPageAvailable, totalPages,totalRecords);
	}

	@SuppressWarnings("unchecked")
	public void setParameters(Map _map) {
		Map<String, String[]> map = _map;
		String[] page = map.get("page");
		if (page != null) {
			this.page = Integer.parseInt(page[0]); // TODO: Errors!
		}

		for (String key : map.keySet()) {
			if (key.startsWith("column")) {
				if (key.length() > "column".length()) {
					String index = key.substring("column".length());
					if (map.containsKey("filter" + index)) {
						filters.put(map.get(key)[0],
								map.get("filter" + index)[0]);
					}
				}
			} else if (key.startsWith("sort")) {
				if (key.length() > "sort".length()) {
					String index = key.substring("sort".length());
					if (map.containsKey("as" + index)) {
						sorts.add(new String[] { map.get(key)[0],
								map.get("as" + index)[0] });
					}
				}
			}
		}
	}

	public int getTotalNumberOfPages(WorkList workList) {
		return getTotalNumberOfPages(workList.getTaskListCount());
	}

	// This is required for the part return flow.
	public int getTotalNumberOfPages(int taskCount) {
		return (taskCount / pageSize) + ((taskCount % pageSize) > 0 ? 1 : 0);
	}

	public void setBodyData(JSONArray tableData, boolean isNextPageAvailable,
			boolean isPrevPageAvailable, int totalPages,long totalRecords) {
		if (totalPages < 0) {
			throw new IllegalArgumentException(
					"Total pages must be greater than or equal to 0, '"
							+ totalPages + "' is an illegal value.");
		}
		JSONArray response = new JSONArray();
		response.put(tableData);
		response.put(isNextPageAvailable);
		response.put(isPrevPageAvailable);
		response.put(totalPages);
        response.put(totalRecords);
        jsonString = response.toString();
	}

	public void setPreviewVisible(boolean previewVisible) {
		this.previewVisible = previewVisible;
	}

	// Added to access from Inherited child classes
	public boolean isDownloadMaxResults() {
		return downloadMaxResults;
	}

	public void setDownloadMaxResults(boolean downloadMaxResults) {
		this.downloadMaxResults = downloadMaxResults;
	}

	public String getJsonString() {
		return jsonString;
	}

	public List<SummaryTableColumn> getTableHeadData() {
		return tableHeadData;
	}

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public String getFolderName() {
		return folderName;
	}

	public void setFolderName(String folderName) {
		this.folderName = folderName;
	}

	public WorkListService getWorkListService() {
		return workListService;
	}

	public void setWorkListService(WorkListService workListService) {
		this.workListService = workListService;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	/**
	 * To be overridden by the derived classes if they want to append an alias
	 * to the property names before adding to the sort criteria filter criteria
	 * 
	 * @return String
	 */
	protected String getAlias() {
		return null;
	}

	protected ListCriteria getCriteria(){
		if (downloadMaxResults) {
			pageSize = MAX_DOWNLOADABLE_ROWS;
		}
		PageSpecification pageSpecification = new PageSpecification();
		pageSpecification.setPageSize(pageSize);
		pageSpecification.setPageNumber(page - 1);

		ListCriteria listCriteria = getListCriteria();
		addFilterCriteria(listCriteria);
		addSortCriteria(listCriteria);
		addParamTypes(listCriteria);
		listCriteria.setPageSpecification(pageSpecification);
		return listCriteria;
	}

	private void addSortCriteria(ListCriteria criteria) {
		for (String[] sort : sorts) {
			String sortOnColumn = sort[0];
			boolean ascending = !sort[1].equals(SORT_DESCENDING);
			criteria.addSortCriteria(addAlias(sortOnColumn), ascending);
		}
	}

	private void addFilterCriteria(ListCriteria criteria) {
		String filterValue = null;
		boolean wrntyTypeExt = false;
		for (String filterName : filters.keySet()) {
			//This is to allow filtering based on the warranty type selected in the policy definition page
			if (filterName.equals("warrantyType.displayValue") || filterName.equals("forCriteria.wntyTypeName")) {
					if(filterName.equals("warrantyType.displayValue")){
						filters.put("warrantyType.type",
								filters.get("warrantyType.displayValue"));
						filterName = "warrantyType.type";
					}
				if (filters.get(filterName).toUpperCase().startsWith("E")) {
					Matcher matcher = extPattern.matcher(filters.get(filterName)
							.toUpperCase());
					if (matcher.find()) {
						filterValue = "EXTENDED";
						wrntyTypeExt = true;
					}
				} 
				if(!wrntyTypeExt)
					filterValue = filters.get(filterName).toUpperCase();
			}
			else if (isBuConfigAMER() && filterName.contains("clmTypeName") && filters.get(filterName).toUpperCase().startsWith("U")){
				Pattern pattern = Pattern.compile("\\b(U|UN|UNI|UNIT)\\b");
				Matcher matcher = pattern.matcher(filters.get(filterName)
				.toUpperCase());
				if (matcher.find()){
					filterValue = "machine";
					}
				}
			else
				filterValue = filters.get(filterName).toUpperCase();
			criteria.addFilterCriteria(addAlias(filterName), filterValue);
		}
	}

	private String addAlias(String sortOnColumn) {
		if (StringUtils.hasText(getAlias())) {
			return getAlias() + "." + sortOnColumn;
		}
		return sortOnColumn;
	}

	
	private Object prepareOEMPartCrossRefForDisplay(Object object,
			String expression) {
		Object columnValue = null;
		Object causalPartNumber = getBeanProvider().getProperty(expression,
				object);
		int index = expression.indexOf("causalBrandPart");
		String oEMDealerCausalPartString = expression.substring(0, index)
				+ "oemDealerCausalPart.number";
		Object OEMDealerCausalPartNumber = getBeanProvider().getProperty(
				oEMDealerCausalPartString, object);
		Organization organization = getLoggedInUser()
				.getBelongsToOrganization();
		if (causalPartNumber != null && OEMDealerCausalPartNumber != null) {
			if (InstanceOfUtil.isInstanceOfClass(ServiceProvider.class,
					organization)) {
				columnValue = OEMDealerCausalPartNumber;
			} else {
				columnValue = causalPartNumber + "("
						+ OEMDealerCausalPartNumber + ")";
			}
		} else {
			columnValue = causalPartNumber;
		}
		return columnValue;
	}

	private Map<String, String> getExpressionToModifyValue() {
		Map<String, String> expressionToCheck = new HashMap<String, String>();
		expressionToCheck.put("claim.activeClaimAudit.serviceInformation.causalBrandPart.itemNumber",
				"claim.activeClaimAudit.serviceInformation.causalBrandPart.itemNumber");
		expressionToCheck.put("activeClaimAudit.serviceInformation.causalBrandPart.itemNumber",
				"activeClaimAudit.serviceInformation.causalBrandPart.itemNumber");
		return expressionToCheck;
	}

	protected void addInboxViewFieldsToHeader(List<SummaryTableColumn> header,
			double labelColumnWidth) {
		InboxView inboxView = inboxViewService.findById(Long
				.parseLong(inboxViewId));
		String prefix = this.getInboxViewFieldExpressionPrefix();

		if (this.exportAction) {
			prepareDataForDownLoadToExcel(header, labelColumnWidth, inboxView,
					prefix);
		} else {
			/**
			 * We will be showing only first 6 columns in the folder view. The
			 * remaining cols will be a part of DownLoad to Excel
			 */
			prepareDataForFolderViewDisplay(header, labelColumnWidth,
					inboxView, prefix);
		}

		// check if there is no sort criteria, add inbox view default
		// sort criteria
		if ((sorts != null) && (sorts.size() == 0)) {
			inboxViewSortField = prefix + inboxView.getSortByField();
			inboxViewSortOrder = inboxView.isSortOrderAscending();
		}

	}

	private void prepareDataForDownLoadToExcel(List<SummaryTableColumn> header,
			double labelColumnWidth, InboxView inboxView, String prefix) {
		int numOfCols = inboxView.getFields().size();
		double columnWidth = (100 - labelColumnWidth) / numOfCols;

		for (InboxField fld : inboxView.getFields()) {
			int columnOption = getColumnOption(fld);

			if (columnOption == SummaryTableColumnOptions.BLANK_COL) {
				header.add(new SummaryTableColumn(fld.getDisplayName(), prefix
						+ fld.getId(), columnWidth, fld.getType(), prefix + fld.getExpression()));
			} else {
				header.add(new SummaryTableColumn(fld.getDisplayName(), prefix
						+ fld.getId(), columnWidth, fld.getType(), prefix + fld.getExpression(),
						columnOption));
			}
		}
	}

	private void prepareDataForFolderViewDisplay(
			List<SummaryTableColumn> header, double labelColumnWidth,
			InboxView inboxView, String prefix) {
		
		int numOfFolderViewCols = inboxView.getFields().size();
		int numOfDefaultViewCols = 0;
		int numOfFolderViewColsForDisplay = 0;
		int numOfColsWithFixedWidth = 0;
		Integer totalFixedWith = new Integer(0);

		/**
		 * Adding the width of default visible columns (other than folder view
		 * columns) 
		 */
		for (SummaryTableColumn headerCol : header) {
			if (headerCol != null && !headerCol.isHidden()) {
				numOfDefaultViewCols++;
				totalFixedWith = totalFixedWith
						+ new Double(headerCol.getWidthPercent()).intValue();			
			}
		}
		/**
		 * Adding up the width of first 12 columns chosen in folder view
		 */
		numOfFolderViewColsForDisplay = (13 - numOfDefaultViewCols) < numOfFolderViewCols ? (13 - numOfDefaultViewCols)
				: numOfFolderViewCols ;
		for (int inboxCounter = 0; inboxCounter < numOfFolderViewColsForDisplay; inboxCounter++) {
			InboxField fld = inboxView.getFields().get(inboxCounter);
			if (fld.getFixedWidth() != null) {
				totalFixedWith = totalFixedWith	+ fld.getFixedWidth();
				numOfColsWithFixedWidth ++;
			}
		}
		double columnWidth = (100 - labelColumnWidth)
				/ numOfFolderViewColsForDisplay;
		if (totalFixedWith.intValue() > 0 && numOfFolderViewColsForDisplay != numOfColsWithFixedWidth)
		{
			columnWidth = (100 - totalFixedWith)
					/ (numOfFolderViewColsForDisplay - numOfColsWithFixedWidth);
		}	
		for (int inboxCounter = 0; inboxCounter < numOfFolderViewColsForDisplay; inboxCounter++) {
			InboxField fld = inboxView.getFields().get(inboxCounter);
			int columnOption = getColumnOption(fld);
			double finalColumnWidth = fld.getFixedWidth() != null ? fld
					.getFixedWidth() : columnWidth;
			if (columnOption == SummaryTableColumnOptions.BLANK_COL) {
				header
						.add(new SummaryTableColumn(fld.getDisplayName(),
								prefix + fld.getId(), finalColumnWidth,
								fld.getType(), prefix + fld.getExpression()));
			} else {
				header.add(new SummaryTableColumn(fld.getDisplayName(), prefix
						+ fld.getId(), finalColumnWidth, fld.getType(),
						prefix + fld.getExpression(),columnOption));
			}
		}
	}

	protected int getColumnOption(InboxField fld) {
		int columnOption = SummaryTableColumnOptions.BLANK_COL;

		if (!fld.isAllowSort())
			columnOption = SummaryTableColumnOptions.NO_SORT;
		if (!fld.isAllowFilter())
			columnOption = columnOption | SummaryTableColumnOptions.NO_FILTER;
		return columnOption;
	}

	protected boolean inboxViewFields() {
		if (inboxViewId != null && !inboxViewId.trim().equals("")
				&& !inboxViewId.trim().equals("-1"))
			return true;
		else
			return false;
	}

	protected void setInboxViews() {
		if (this.getInboxViewContext() == null)
			return;
		inboxViews = inboxViewService.findInboxViewForUser(getLoggedInUser()
				.getId(), this.getInboxViewContext(), folderName);
	}

	protected void setDefaultInboxView() {
		// set default inbox view for this folder, if inbox view not already set
		// for this page
		if (folderName == null)
			return;
		if ((inboxViewId == null) || inboxViewId.trim().equals("")) {
			InboxView defaultInboxView = inboxViewService
					.findDefaultInboxViewForUserAndFolder(getLoggedInUser()
							.getId(), folderName);
			if (defaultInboxView != null) {
				inboxViewId = String.valueOf(defaultInboxView.getId());
			}
		} else if (!inboxViewId.trim().equals("-1")){
			defaultFolderViewService.setDefaultInboxViewForUserAndFolder(
					getLoggedInUser(), folderName, Long.parseLong(inboxViewId));
		}
	}

	public DefaultFolderViewService getDefaultFolderViewService() {
		return defaultFolderViewService;
	}

	public void setDefaultFolderViewService(
			DefaultFolderViewService defaultFolderViewService) {
		this.defaultFolderViewService = defaultFolderViewService;
	}

	public String getInboxViewId() {
		return inboxViewId;
	}

	public void setInboxViewId(String inboxViewId) {
		this.inboxViewId = inboxViewId;
	}

	public List<InboxView> getInboxViews() {
		return inboxViews;
	}

	public void setInboxViews(List<InboxView> inboxViews) {
		this.inboxViews = inboxViews;
	}

	public InboxViewService getInboxViewService() {
		return inboxViewService;
	}

	public void setInboxViewService(InboxViewService inboxViewService) {
		this.inboxViewService = inboxViewService;
	}

	public String getInboxViewSortField() {
		return inboxViewSortField;
	}

	public void setInboxViewSortField(String inboxViewSortField) {
		this.inboxViewSortField = inboxViewSortField;
	}

	public boolean isInboxViewSortOrder() {
		return inboxViewSortOrder;
	}

	public void setInboxViewSortOrder(boolean inboxViewSortOrder) {
		this.inboxViewSortOrder = inboxViewSortOrder;
	}

	/*
	 * this method should be overridden by listing action classes that support
	 * inbox views to return serach context like "ClaimSearches",
	 * "InventorySearches" etc.
	 */
	protected String getInboxViewContext() {
		return null;
	}

	/*
	 * this method should be overridden by listing action classes that support
	 * inbox views and require an expression prefix for inbox view fields
	 */
	protected String getInboxViewFieldExpressionPrefix() {
		return "";
	}

	public boolean isExportAction() {
		return exportAction;
	}

	public void setExportAction(boolean exportAction) {
		this.exportAction = exportAction;
	}
	
	public boolean isDownloadReport() {
		return downloadReport;
	}

	public void setDownloadReport(boolean downloadReport) {
		this.downloadReport = downloadReport;
	}

        // Overridable method to provide custom list criteria
        protected ListCriteria getListCriteria() {
            return new ListCriteria();
        }
        
	protected void addParamTypes(ListCriteria criteria) {
		if (tableHeadData == null) {
			generateHeadData();
		}
		for (SummaryTableColumn summaryTableColumn : tableHeadData) {
			String paramType = summaryTableColumn.getDataType();

			if (StringUtils.hasText(paramType)) {
				criteria.addParamType(addAlias(summaryTableColumn.getId()),
						paramType);
			}
		}
	}

     public List<ServiceProvider> fetchChildDealerShips() {
        if(getLoggedInUsersDealership() != null && getLoggedInUsersDealership().isEnterpriseDealer()) {
            return orgService.findDealersByIds(getLoggedInUsersDealership().getChildDealersIds());
        }
        return new ArrayList<ServiceProvider>();
    }
}

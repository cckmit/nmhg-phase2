package tavant.twms.web.actions;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import tavant.twms.domain.integration.SyncTracker;
import tavant.twms.domain.integration.SyncTrackerDAO;
import tavant.twms.domain.integration.SyncDAO;
import tavant.twms.infra.BeanProvider;
		
import tavant.twms.infra.ListCriteria;
import tavant.twms.infra.PageResult;
import tavant.twms.integration.layer.component.global.CommonSyncProcessor;
import tavant.twms.web.TWMSWebConstants;

import com.domainlanguage.time.CalendarDate;
import com.opensymphony.xwork2.Preparable;
import java.text.SimpleDateFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.StringUtils;
import tavant.twms.dateutil.TWMSDateFormatUtil;
import tavant.twms.domain.bu.BusinessUnit;
import tavant.twms.domain.integration.SyncStatus;
import tavant.twms.domain.integration.SyncTrackerCriteria;
import tavant.twms.security.SelectedBusinessUnitsHolder;
import tavant.twms.web.common.CheckBoxPropertyResolver;
import tavant.twms.web.inbox.SummaryTableAction;
import tavant.twms.web.inbox.SummaryTableColumn;
import tavant.twms.web.inbox.SummaryTableColumnOptions;

@SuppressWarnings("serial")
public class RemoteInteractionLogsAction extends SummaryTableAction implements Preparable {
	
	private static final Logger logger = Logger.getLogger(RemoteInteractionLogsAction.class);
	
	private CalendarDate fromDate;

    private CalendarDate toDate;
    
    private String syncType;
    
    private String transactionId;
    
    private String remoteSystemNameSelect;
    
    private List<String> remoteSystemNameList;

    private List<Long> reprocessCheckBoxIdList;
    
    private SyncTrackerDAO syncTrackerDAO;
    
    private SyncDAO syncDAO;
    
    private String statusSelected;

    private SyncTracker previewSyncObj;
    
    private String businessId;

    private String syncTrackerIds;
    
    private static final Pattern errorMsgPattren1 =
            Pattern.compile("(<errorMessage>)(.*?)(The Reason for the Error is :)(.*?)(<\\/errorMessage>)",
                Pattern.CASE_INSENSITIVE | Pattern.DOTALL);

    private static final Pattern errorMsgPattren2 =
            Pattern.compile("(<errorMessage>)(.*?)(<\\/errorMessage>)",
                Pattern.CASE_INSENSITIVE | Pattern.DOTALL);

    private static final Pattern errorMsgPattren3 =
        Pattern.compile("(Error Syncing Item Base, with Item Number )(.*?)( The Reason for the Error is)",
                Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
    
    private CommonSyncProcessor commonSyncProcessor;
    
    List<SyncTracker> syncTrackerList =new ArrayList<SyncTracker>();
    
    protected ListCriteria listCriteria;
    
    private boolean includeDeleted;
    
    private String actionPerformed;
    
    private List<String> buNameList = new ArrayList<String>();

    public String getActionPerformed() {
        return actionPerformed;
    }

    public void setActionPerformed(String actionPerformed) {
        this.actionPerformed = actionPerformed;
    }


    public SyncTracker getPreviewSyncObj() {
        return previewSyncObj;
    }

    public void setPreviewSyncObj(SyncTracker previewSyncObj) {
        this.previewSyncObj = previewSyncObj;
    }

    public List<Long> getReprocessCheckBoxIdList() {
        return reprocessCheckBoxIdList;
    }

    public void setReprocessCheckBoxIdList(List<Long> reprocessCheckBox) {
        this.reprocessCheckBoxIdList = reprocessCheckBox;
    }
    
    public String getBusinessId() {
        return businessId;
    }

    public void setBusinessId(String businessId) {
        this.businessId = businessId;
    }

	public SyncTrackerDAO getSyncTrackerDAO() {
		return syncTrackerDAO;
	}

	public void setSyncTrackerDAO(SyncTrackerDAO syncTrackerDAO) {
		this.syncTrackerDAO = syncTrackerDAO;
	}
	
	public void setSyncDAO(SyncDAO syncDAO){
		this.syncDAO = syncDAO;
	}
	
	public SyncDAO getSyncDAO(){
		return syncDAO;
	}

	public String getSyncType() {
		return syncType;
	}


	public String getTransactionId() {
		return transactionId;
	}

	public CalendarDate getFromDate() {
		return fromDate;
	}


	public CalendarDate getToDate() {
		return toDate;
	}

	public String getRemoteSystemNameSelect() {
		return remoteSystemNameSelect;
	}

	public void setRemoteSystemNameSelect(String remoteSystemNameSelect) {
		this.remoteSystemNameSelect = remoteSystemNameSelect;
	}

	public List<String> getRemoteSystemNameList() {
        if(remoteSystemNameList == null)
            remoteSystemNameList = syncTrackerDAO.getSyncTypes();
        return remoteSystemNameList;
	}

	public void setRemoteSystemNameList(List<String> remoteSystemNameList) {
		this.remoteSystemNameList = remoteSystemNameList;
	}

	public void setFromDate(CalendarDate fromDate) {
		this.fromDate = fromDate;
	}

	public void setToDate(CalendarDate toDate) {
		this.toDate = toDate;
	}

	public void setSyncType(String syncType) {
		this.syncType = syncType;
	}

	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}
	
	public String reProcessIds(){
        if(getReprocessCheckBoxIdList() != null && getReprocessCheckBoxIdList().size() > 0){
            List<Long> idsToBeProcessed = new ArrayList<Long>(getReprocessCheckBoxIdList().size());
            List<SyncTracker> list = syncTrackerDAO.findByIds(getReprocessCheckBoxIdList());
            for (SyncTracker syncTracker : list) {
                if(!SyncStatus.COMPLETED.equals(syncTracker.getStatus()))
                    idsToBeProcessed.add(syncTracker.getId());
            }
            if(!idsToBeProcessed.isEmpty())
            {
                commonSyncProcessor.syncProcessor(syncType, idsToBeProcessed, true);
                syncTrackerDAO.updateAuditableColumns(idsToBeProcessed, getLoggedInUser());
//              TWMS4.3-965: if user belongs to multiple BU SelectedBusinessUnitsHolder will have the latest processed BU name, clear it.
                if(StringUtils.hasText(SelectedBusinessUnitsHolder.getSelectedBusinessUnit()) && buNameList.size() > 1)
                    SelectedBusinessUnitsHolder.setSelectedBusinessUnit(null);
                syncTrackerList = syncDAO.findByIds(idsToBeProcessed);
            }
        }
        return SUCCESS;
	}
	
    public String deleteRecords(){
        if (getReprocessCheckBoxIdList() != null && getReprocessCheckBoxIdList().size() > 0) {
            List<Long> idsToBeDeleted = new ArrayList<Long>(getReprocessCheckBoxIdList().size());
            List<SyncTracker> list = syncTrackerDAO.findByIds(getReprocessCheckBoxIdList());
            for (SyncTracker syncTracker : list) {
                if(!syncTracker.isDeleted() && !TWMSWebConstants.CLAIM.equals(syncType))
                    idsToBeDeleted.add(syncTracker.getId());
            }
            if(!idsToBeDeleted.isEmpty()){
                syncTrackerDAO.delete(idsToBeDeleted,getLoggedInUser());
                syncTrackerList.clear();
                syncTrackerList = syncTrackerDAO.findByIds(idsToBeDeleted);
            }
        }
        return SUCCESS;
    }
	
	public String getSyncTypes(){
		return SUCCESS;		
	}
	
    public boolean isDeleteButtonRequired(){
    	return !"Select".equals(syncType) && !TWMSWebConstants.CLAIM.equals(syncType) &&
        		!"Completed".equals(statusSelected) && !includeDeleted;
    }
	
	public String formatErrorMsg(String errorMsg){
		if (StringUtils.hasText(errorMsg)) {
			logger.info("ErrorMsg:-"+errorMsg);
		if(TWMSWebConstants.ITEM.equalsIgnoreCase(syncType)){
			StringBuilder sb = new StringBuilder(256);
            Matcher m = errorMsgPattren3.matcher(errorMsg);
            while(m.find()){
                sb.append(StringUtils.deleteAny(m.group(2).trim(),"\n")).append(",");
            }
            if(sb.length() > 0)
                return sb.deleteCharAt(sb.length()-1).append(" item number(s) failed").toString();
            return errorMsg;
		}
		else{
			return createErrorMessage(errorMsg);
			}
		}
		return "";
	}
	
	private String createErrorMessage(String errorMsg){
        if(StringUtils.hasText(errorMsg)){
            Matcher m = errorMsgPattren1.matcher(errorMsg);
            if(m.find()){
                return m.group(4);
            }else{ // Lets check in the second format
                m = errorMsgPattren2.matcher(errorMsg);
                if(m.find()){
                    return m.group(2);
                } else{
                    StringBuilder sb = new StringBuilder(256);
                    m = errorMsgPattren3.matcher(errorMsg);
                    while(m.find()){
                        sb.append(StringUtils.deleteAny(m.group(2).trim(),"\n")).append(",");
                    }
                    if(sb.length() > 0)
                        return sb.deleteCharAt(sb.length()-1).append(" item numbers failed to sync.").toString();
                }
            }
            return errorMsg;
        }
        return "";	
	}
	
	@Override
	 public void validate() {
        includeDeleted = StringUtils.hasText(request.getParameter("includeDeleted"));
        boolean isSyncStatusSelected = !"Select".equals(statusSelected);
        boolean isSyncTypeSelected = !"Select".equals(syncType);
        boolean isTransactionIdSet = StringUtils.hasText(transactionId);
        boolean isFromDateSet = fromDate != null;
        boolean isToDateSet = toDate != null;
        boolean isBusinessIdSet = StringUtils.hasText(businessId);
        if(!(isTransactionIdSet || isFromDateSet || isToDateSet || isBusinessIdSet)){
            if(!isSyncTypeSelected && !isSyncStatusSelected){
                addActionError("error.manageRemoteInteractions.statusSelected");
                addActionError("error.manageRemoteInteractions.syncType");
            }else if(isSyncTypeSelected && !isSyncStatusSelected){
                addActionError("error.manageRemoteInteractions.statusSelected");
            }else if(isSyncStatusSelected && !isSyncTypeSelected){
                addActionError("error.manageRemoteInteractions.syncType");
            }
        }
	 }
	
	public boolean isReprocessingRequired(){
        return !"Select".equals(syncType) && !"Completed".equals(statusSelected)
        		&& isRPRequired(syncType.trim());		
	}
    
    private boolean isRPRequired(String syncType){
        return (TWMSWebConstants.CLAIM.equalsIgnoreCase(syncType) 
                    || TWMSWebConstants.ITEM.equalsIgnoreCase(syncType)
                    || TWMSWebConstants.INSTALLBASE.equalsIgnoreCase(syncType)
                    || TWMSWebConstants.EXTWARRANTYPURCHASENOTIFICATION.equalsIgnoreCase(syncType)
                    || TWMSWebConstants.TECHNICIAN.equalsIgnoreCase(syncType));
    }
	
	public CommonSyncProcessor getCommonSyncProcessor() {
		return commonSyncProcessor;
	}

	public void setCommonSyncProcessor(CommonSyncProcessor commonSyncProcessor) {
		this.commonSyncProcessor = commonSyncProcessor;
	}

	public String getStatusSelected() {
		return statusSelected;
	}

	public void setStatusSelected(String statusSelected) {
		this.statusSelected = statusSelected;
	}

	public List<SyncTracker> getSyncTrackerList() {
		return syncTrackerList;
	}

	public void setSyncTrackerList(List<SyncTracker> syncTrackerList) {
		this.syncTrackerList = syncTrackerList;
	}
	
    public String showPreview(){
        if(StringUtils.hasText(id)){
            previewSyncObj = syncTrackerDAO.findById(Long.parseLong(id));
        }
        return SUCCESS;
    }
		
    @Override
    protected List<SummaryTableColumn> getHeader() {
        List<SummaryTableColumn> tableColumns = new ArrayList<SummaryTableColumn>();
        tableColumns.add(new SummaryTableColumn("label.manageRemoteInteractions.id", "id",
                1, SummaryTableColumn.NUMBER, "id",false,true,true,false));
        tableColumns.add(new SummaryTableColumn(isExportAction() ? "columnTitle.common.id" : "", "checkBoxCol",
                8, SummaryTableColumn.CHECK_BOX, "checkBoxColumn",SummaryTableColumnOptions.NO_SORT_NO_FILTER_LABEL_COL));
        tableColumns.add(new SummaryTableColumn("label.manageRemoteInteractions.syncType", "syncType",
                10, SummaryTableColumn.STRING, "syncType",true,false,false,false));
        tableColumns.add(new SummaryTableColumn("label.common.businessUnit", "businessUnitInfo",
                12, SummaryTableColumn.STRING, "businessUnitInfo",true,false,false,false));
        tableColumns.add(new SummaryTableColumn("label.manageRemoteInteractions.status", "status.status",
                7, SummaryTableColumn.STRING, "status.status",false,false,false,false));
        tableColumns.add(new SummaryTableColumn("label.manageRemoteInteractions.updateDate", "updateDate",
                10, SummaryTableColumn.DATE, "updateDate",false,false,false,false));
        tableColumns.add(new SummaryTableColumn("label.manageRemoteInteractions.transactionId", "uniqueIdValue",
                12, SummaryTableColumn.STRING, "uniqueIdValue",false,false,false,false));
        tableColumns.add(new SummaryTableColumn("label.integration.errorType", "errorType",
                15, SummaryTableColumn.STRING, "errorType",false,false,false,false));
        tableColumns.add(new SummaryTableColumn("label.manageRemoteInteractions.ErrorMsg", "errorMessage",
                30, SummaryTableColumn.STRING, "errorMessage",SummaryTableColumnOptions.NO_SORT_NO_FILTER_COL));
        if(isExportAction())
            tableColumns.add(new SummaryTableColumn("label.manageRemoteInteractions.ErrorXML", "errorXml",
                    4, SummaryTableColumn.STRING, "errorXml", SummaryTableColumnOptions.NO_SORT_NO_FILTER_COL));
        setInboxViewSortField("updateDate");
        return tableColumns;
    }
	
    
    @Override
    protected PageResult<?> getBody() {
        return syncDAO.findSyncTrackerObjects((SyncTrackerCriteria)getCriteria());
    }

    @Override
    protected String getAlias() {
        return "syncTracker";
    }

	
    @Override
    protected ListCriteria getListCriteria() {
         SyncTrackerCriteria criteria = new SyncTrackerCriteria();
         criteria.setCaseSensitiveSort(true);
         if(!"Select".equals(syncType)) {
            criteria.setSyncType(syncType);
         }
         criteria.setTransactionId(transactionId);
         if(!"Select".equals(statusSelected)) {
            criteria.setSyncStatus(statusSelected);
         }
         criteria.setFromDate(fromDate);
         criteria.setToDate(toDate);
         criteria.setBusinessId(businessId);
         criteria.setIncludeDeleted(includeDeleted);
         return criteria;
    }
    
    @Override
    protected BeanProvider getBeanProvider() {
        return new CheckBoxPropertyResolver(){
            @Override
            public Object getProperty(String propertyPath, Object root) {
                if("updateDate".equals(propertyPath)) {
                    return new SimpleDateFormat(TWMSDateFormatUtil.getDateFormatForLoggedInUser())
                            .format(((SyncTracker)root).getUpdateDate());
                } else if("errorMessage".equals(propertyPath)) {
                    return createErrorMessage(((SyncTracker)root).getErrorMessage());
                } else if("checkBoxColumn".equals(propertyPath)){
                    SyncTracker o = (SyncTracker)root;
                    return getCheckBoxColValue("", "reprocessCheckBoxIdList", 
                            String.valueOf(o.getId()),
                            (!SyncStatus.COMPLETED.equals(o.getStatus())) && buNameList.contains(o.getBusinessUnitInfo().getName()));
                } else if("errorXml".equals(propertyPath)){
                    return ((SyncTracker) root).getErrorMessage();
                }
                return super.getProperty(propertyPath, root);
            }

        };
    }
	 
    public void downloadRequestMessage() {
        SyncTracker syncTracker = syncTrackerDAO.findById(Long.parseLong(id));
        String fileName =  syncTracker.getUniqueIdValue() + ".xml";
        String requestMessage = syncTracker.getBodXML();
        streamFile(requestMessage, fileName);
     }

    void streamFile(String requestMessage, String fileName) {
        this.response.setContentType("text/xml");
        this.response.setHeader("Content-disposition", "attachment; filename=" + fileName);
        try {
            FileCopyUtils.copy(requestMessage.getBytes(), this.response.getOutputStream());
        } catch (Exception e) {
            logger.error("Failed to write file to output stream", e);
        }
    }

    public boolean isIncludeDeleted() {
        return includeDeleted;
    }

    public void setIncludeDeleted(boolean includeDeleted) {
        this.includeDeleted = includeDeleted;
    }

    public boolean isDeleteAction(){
        return "Delete".equals(getActionPerformed());
    }

    public void prepare() throws Exception {
        for(BusinessUnit bu : getBusinessUnits()){
            buNameList.add(bu.getName());
        }
    }

    public String getSyncTrackerIds() {
        return syncTrackerIds;
    }

    public void setSyncTrackerIds(String syncTrackerIds) {
        this.syncTrackerIds = syncTrackerIds;
    }

    
    public String syncFailedCreditNotifications(){
        if(StringUtils.hasText(syncTrackerIds)){
            try{
                String[] ids = StringUtils.commaDelimitedListToStringArray(syncTrackerIds);
                List<Long> stIds = new ArrayList<Long>(ids.length);
                for (String idToProcess : ids) {
                    stIds.add(Long.valueOf(idToProcess));
                }
                commonSyncProcessor.syncProcessor("CreditNotification", stIds, false);
            }catch(Exception e){
                logger.error("Error reprocessing Credit Notifications", e);
                addActionError("Error reprocessing Credit Notifications - " + e.getMessage());
            }
        }
        return SUCCESS;
    }
    
}

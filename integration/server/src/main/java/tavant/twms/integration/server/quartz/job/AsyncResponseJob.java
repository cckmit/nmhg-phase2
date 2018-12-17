package tavant.twms.integration.server.quartz.job;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.axis2.AxisFault;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.Options;
import org.apache.axis2.transport.http.HTTPConstants;
import org.apache.axis2.transport.http.HttpTransportProperties;
import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlException;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.StatefulJob;
import org.springframework.scheduling.quartz.QuartzJobBean;

import tavant.oagis.ServerResponseHeaderDocumentDTO;
import tavant.twms.integration.server.common.SendEmailService;
import tavant.twms.integration.server.common.dataaccess.SyncStatus;
import tavant.twms.integration.server.common.dataaccess.SyncTracker;
import tavant.twms.integration.server.common.dataaccess.SyncTrackerDAO;
import tavant.twms.integration.server.util.IntegrationServerConstants;
import tavant.twms.integration.layer.IntegrationService;

import com.nmhg.synch_response.MTOraStatusBeanDocument;
import com.nmhg.synch_response.OraStatusBean;
import org.springframework.util.StringUtils;

public class AsyncResponseJob extends QuartzJobBean implements StatefulJob {
    protected static final Logger logger = Logger.getLogger(AsyncResponseJob.class);
    private Integer maxNoOfRetries;
    private SyncTrackerDAO syncTrackerDao;
    private SendEmailService sendEmailService;
    private String buName;
    private EmailReportDetail emailReportDetail;
    private IntegrationService integrationServiceProxy;

    public String updateSyncDetails(SyncTracker syncTracker) {
    	MTOraStatusBeanDocument commonSyncResponse = processRequestXml(syncTracker);;
        populateSyncTracker(syncTracker, commonSyncResponse);
        return sendRequest(syncTracker,commonSyncResponse);
    }
    
    private MTOraStatusBeanDocument processRequestXml(SyncTracker syncTracker) {
    	String[] responses=null;
    	if(IntegrationServerConstants.INSTALLBASE_SYNC_JOB_UNIQUE_IDENTIFIER.equalsIgnoreCase(syncTracker.getSyncType())){
    	 responses = (String[]) integrationServiceProxy.syncInstallBase(syncTracker.getBodXML());
    	}else if(IntegrationServerConstants.CUSTOMER_SYNC_JOB_UNIQUE_IDENTIFIER.equalsIgnoreCase(syncTracker.getSyncType())){
    		 responses = (String[]) integrationServiceProxy.syncCustomer(syncTracker.getBodXML());
    	}else if(IntegrationServerConstants.CREDITNOTIFICATION_SYNC_JOB_UNIQUE_IDENTIFIER.equalsIgnoreCase(syncTracker.getSyncType())){
    		 responses = (String[]) integrationServiceProxy.syncWarrantyClaimCreditNotification(syncTracker.getBodXML());
    	}else if(IntegrationServerConstants.TECHNICIAN_SYNC_JOB_UNIQUE_IDENTIFIER.equalsIgnoreCase(syncTracker.getSyncType())){
    		responses=(String[]) integrationServiceProxy.syncTechnicians(syncTracker.getBodXML());
    	}
    	else if(IntegrationServerConstants.BOOKING_SYNC_JOB_UNIQUE_IDENTIFIER.equalsIgnoreCase(syncTracker.getSyncType())){
    		responses=(String[]) integrationServiceProxy.syncUnitBooking(syncTracker.getBodXML());
    	}
    	else if(IntegrationServerConstants.CURRENCY_EXCHANGE_RATE.equalsIgnoreCase(syncTracker.getSyncType())){
    		responses=(String[]) integrationServiceProxy.syncCurrencyExchangeRate(syncTracker.getBodXML());
    	}
		try {
			syncTracker.setRecord(responses[0]);
			ServerResponseHeaderDocumentDTO doc;
			doc = ServerResponseHeaderDocumentDTO.Factory.parse(responses[1]);
			syncTracker.setUniqueIdName(doc.getServerResponseHeader()
					.getUniqueIdentifier().getName());
			syncTracker.setUniqueIdValue(doc.getServerResponseHeader()
					.getUniqueIdentifier().getValue());
			String businessUnitName = doc.getServerResponseHeader()
					.getUniqueIdentifier().getBusinessUnitName();
			if(businessUnitName!=null&&StringUtils.hasText(businessUnitName))
				syncTracker.setBusinessUnitInfo(businessUnitName);
			return MTOraStatusBeanDocument.Factory.parse(responses[0]);
		} catch (XmlException e) {
			throw new RuntimeException(e);
		}
	}
    
    private String sendRequest(SyncTracker syncTracker,MTOraStatusBeanDocument commonSyncResponse)  {
    	com.nmhg.synch_response.MTOraStatusBeanDocument commonsyncresponedoc;
		try {
			logger.error("AsyncResponse: Temp location while sending the request xml's to SAP : "+System.getProperty("java.io.tmpdir"));
			commonsyncresponedoc = com.nmhg.synch_response.MTOraStatusBeanDocument.Factory.parse(syncTracker.getRecord());
			com.nmhg.MI_OraStatusBean_SLMSServiceStub  stub = getProxyForSync();
			stub.mI_OraStatusBean_SLMS(commonsyncresponedoc);
		} catch (Exception e) {
			logger.error("Exception while processing "+syncTracker.getSyncType()+" "+e.getMessage());
			syncTracker.setProcessing_status(SyncTracker.FAILURE);
			return e.getMessage();
		}
		return null;
	}
    
    private com.nmhg.MI_OraStatusBean_SLMSServiceStub getProxyForSync() throws AxisFault {
		HttpTransportProperties.Authenticator basicAuthentication=new HttpTransportProperties.Authenticator();
		emailReportDetail=getCommonSyncReportDetail();
	   	 EndpointReference targetEPR = 
	   	            new EndpointReference(
	   	            		emailReportDetail.getAsyncResponseURL());
	   	logger.debug("Async URL:"+targetEPR);
		basicAuthentication.setUsername(emailReportDetail.getUserName());
		basicAuthentication.setPassword(emailReportDetail.getPassword());
		com.nmhg.MI_OraStatusBean_SLMSServiceStub proxy = new com.nmhg.MI_OraStatusBean_SLMSServiceStub();
		Options clientOptions = proxy._getServiceClient().getOptions();
		clientOptions.setProperty(
				org.apache.axis2.transport.http.HTTPConstants.AUTHENTICATE,
				basicAuthentication);
		clientOptions.setProperty(HTTPConstants.REUSE_HTTP_CLIENT, "true");
		clientOptions.setTo(targetEPR);
		return proxy;
	}
    
    private EmailReportDetail getCommonSyncReportDetail() {
    	return getEmailReportDetail();
	}

	private void populateSyncTracker(SyncTracker syncTracker,
    		MTOraStatusBeanDocument commonSyncResponse) {
		ArrayList<String> errorMessageList = new ArrayList<String>();
        if (commonSyncResponse.getMTOraStatusBean().getStatus().toString()
                .equalsIgnoreCase(IntegrationServerConstants.PROCESSING_STATUS_FAILURE)) {
            syncTracker.setStatus(SyncStatus.FAILED);
            for(OraStatusBean.Exceptions.Error error:commonSyncResponse.getMTOraStatusBean().getExceptions().getErrorArray()){
				errorMessageList.add(error.getErrorMessage());
			}
            String errorMessage = StringUtils.collectionToDelimitedString(
					errorMessageList, "$$$$");
            StringBuffer errorMessageHolder = new StringBuffer();
			errorMessageHolder.append("-------------Errors While Proceesing "+syncTracker.getSyncType()+" Sync-------------");
			errorMessageHolder.append("\n");
			if (syncTracker.getErrorMessage() != null) {
				errorMessageHolder.append(syncTracker.getErrorMessage());
				errorMessageHolder.append("\n");
			}
			if (errorMessage != null) {
				errorMessageHolder.append(errorMessage);
				errorMessageHolder.append("\n");
			}
			errorMessageHolder.append("-----------------------------------------------------------");
			syncTracker.setErrorMessage(errorMessageHolder.toString());
			syncTracker.setNoOfAttempts(syncTracker.getNoOfAttempts() + 1);
        } else {
            syncTracker.setStatus(SyncStatus.COMPLETED);
            syncTracker.setRecord(commonSyncResponse.toString());
            if (syncTracker.getErrorMessage() != null) {
				syncTracker.setErrorMessage(null);
				syncTracker.setNoOfAttempts(syncTracker.getNoOfAttempts() + 1);
			}
        }
    }
    
  
    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
    	List<Long> idsTobeProcessed = syncTrackerDao.getSyncIdsForProcessing(buName, maxNoOfRetries,getSyncType());
    	
    	logger.error("Ids which are needs to be processed....."+idsTobeProcessed);
    	/*List<Long> idsTobeProcessed = syncTrackerDao.getIdsForProcessing(buName, maxNoOfRetries,getSyncType());*/
    	
        if (logger.isDebugEnabled()) {
            logger.debug("Fetched records of Type Common - " + idsTobeProcessed.size());
        }
        Date now = new Date();
        if (!idsTobeProcessed.isEmpty()) {
        	System.setProperty("java.io.tmpdir",IntegrationServerConstants.AXIS2_TEMP_LOCATION);
        	syncTrackerDao.updateStatus(idsTobeProcessed, SyncStatus.IN_PROGRESS);
			for (Long syncTrackerId : idsTobeProcessed) {
				SyncTracker syncTracker = syncTrackerDao.findById(syncTrackerId);
				try {
					syncTracker.setUpdateDate(now);
					String status=updateSyncDetails(syncTracker);
					if(status!=null){
						syncTracker.setProcessing_status(SyncTracker.FAILURE);
						if(syncTracker.getErrorMessage()==null)
						syncTracker.setErrorMessage(status);
					}else{
						syncTracker.setProcessing_status(SyncTracker.SUCCESS);
					}
				}catch (Exception e) {
					logger.error("Exception while Processing the  Sync  for synctrakerid:"+syncTrackerId);
					populateFailureDetails(syncTracker,e.getMessage());
				} finally {
					syncTracker.setUpdateDate(new Date());
					syncTrackerDao.update(syncTracker);
				}
            }
        }
    }
    private void populateFailureDetails(SyncTracker syncTracker,
			 String errorMessage) {
		 syncTracker.setStatus(SyncStatus.FAILED);
		 if(getMaxNoOfRetries() >= syncTracker.getNoOfAttempts())
		 	syncTracker.setProcessing_status(SyncTracker.FAILURE);
		 syncTracker.setErrorMessage(errorMessage);
		 syncTracker.setNoOfAttempts(syncTracker.getNoOfAttempts() + 1);
	 }
    
    private List<String> getSyncType() {
		List<String> syncTypes = new ArrayList<String>(); 
		syncTypes.add(IntegrationServerConstants.CUSTOMER_SYNC_JOB_UNIQUE_IDENTIFIER);
		syncTypes.add(IntegrationServerConstants.INSTALLBASE_SYNC_JOB_UNIQUE_IDENTIFIER);
		syncTypes.add(IntegrationServerConstants.CREDITNOTIFICATION_SYNC_JOB_UNIQUE_IDENTIFIER);
		syncTypes.add(IntegrationServerConstants.TECHNICIAN_SYNC_JOB_UNIQUE_IDENTIFIER);
		syncTypes.add(IntegrationServerConstants.BOOKING_SYNC_JOB_UNIQUE_IDENTIFIER);
		syncTypes.add(IntegrationServerConstants.CURRENCY_EXCHANGE_RATE);
		return syncTypes;
	}

    protected boolean postProcessSuccessfulResponse(SyncTracker syncTracker) {
        return true;
    }
    
    public void setAsyncResponseReportDetail(EmailReportDetail emailReportDetail) {
        this.emailReportDetail = emailReportDetail;
    }
    
    public EmailReportDetail getEmailReportDetail() {
		return this.emailReportDetail;
	}

    public void setMaxNoOfRetries(Integer maxNoOfRetries) {
        this.maxNoOfRetries = maxNoOfRetries;
    }

    public void setSendEmailService(SendEmailService sendEmailService) {
        this.sendEmailService = sendEmailService;
    }

    public void setSyncTrackerDao(SyncTrackerDAO syncTrackerDao) {
        this.syncTrackerDao = syncTrackerDao;
    }

	public String getBuName() {
		return buName;
	}

	public void setBuName(String buName) {
		this.buName = buName;
	}

	public static Logger getLogger() {
		return logger;
	}

	public Integer getMaxNoOfRetries() {
		return maxNoOfRetries;
	}

	public SyncTrackerDAO getSyncTrackerDao() {
		return syncTrackerDao;
	}

	public SendEmailService getSendEmailService() {
		return sendEmailService;
	}
	public IntegrationService getIntegrationServiceProxy() {
		return integrationServiceProxy;
	}

	public void setIntegrationServiceProxy(
			IntegrationService integrationServiceProxy) {
		this.integrationServiceProxy = integrationServiceProxy;
	}
    
}

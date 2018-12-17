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
import org.springframework.util.StringUtils;

import tavant.twms.integration.layer.IntegrationService;
import tavant.twms.integration.server.common.dataaccess.SyncStatus;
import tavant.twms.integration.server.common.dataaccess.SyncTracker;
import tavant.twms.integration.server.common.dataaccess.SyncTrackerDAO;
import tavant.twms.integration.server.util.IntegrationServerConstants;

import com.nmhg.itemsynch_response.ItemSyncResponse.ItemMasterResponse;
import com.nmhg.itemsynch_response.ItemSyncResponse.ItemMasterResponse.Status.Exceptions.Error;
import com.nmhg.itemsynch_response.MTItemSyncResponseDocument;

public class ItemSyncJob extends QuartzJobBean implements StatefulJob {

	protected static final Logger logger = Logger.getLogger(AsyncResponseJob.class);
	private Integer maxNoOfRetries;
	private SyncTrackerDAO syncTrackerDao;
	private String buName;
	private IntegrationService integrationServiceProxy;

	private EmailReportDetail emailReportDetail;

	public String getSyncType() {
		return IntegrationServerConstants.ITEM_SYNC_JOB_UNIQUE_IDENTIFIER;
	}

	public String updateSyncDetails(SyncTracker syncTracker) {
		MTItemSyncResponseDocument itemSyncResponse = syncItem(syncTracker);
		//Process only internal processing is
		boolean isInternallyProcessed = populateSyncTracker(syncTracker, itemSyncResponse);
		if (isInternallyProcessed
				|| (!isInternallyProcessed && getMaxNoOfRetries() >= syncTracker
						.getNoOfAttempts())
				&& (syncTracker.getProcessing_status() == null || SyncTracker.FAILURE
						.equalsIgnoreCase(syncTracker.getProcessing_status()))) {
			return sendRequest(syncTracker);
		}

		//Which means internal processing is failed and we did not send any response to client.  processing status will be null only
		//no need to worry
		return null;
	}


	private String sendRequest(SyncTracker syncTracker)  {
		com.nmhg.itemsynch_response.MTItemSyncResponseDocument itemsyncresponedoc;
		try {
			logger.error("Item Sync:Temp location while sending the request xml's to SAP : "+System.getProperty("java.io.tmpdir"));
			itemsyncresponedoc = com.nmhg.itemsynch_response.MTItemSyncResponseDocument.Factory.parse(syncTracker.getRecord());
			com.nmhg.MI_ItemSyncResponse_SLMSServiceStub  stub=getProxyForItemSync();
			stub.mI_ItemSyncResponse_SLMS(itemsyncresponedoc);
			//ok done... now make the status SUCCESS
			syncTracker.setProcessing_status(SyncTracker.SUCCESS);
		} catch (Exception e) {
			syncTracker.setProcessing_status(SyncTracker.FAILURE);
			logger.error("Exception while processing Item sync Request"+e.getMessage());
			return e.getMessage();
		}

		//it means response has been sent to them.
		return null;
	}


	private com.nmhg.MI_ItemSyncResponse_SLMSServiceStub getProxyForItemSync() throws AxisFault {
		emailReportDetail=getEmailReportDetail();
		EndpointReference targetEPR = 
			new EndpointReference(
					emailReportDetail.getItemSyncURL());

		HttpTransportProperties.Authenticator basicAuthentication=new HttpTransportProperties.Authenticator();
		basicAuthentication.setUsername(emailReportDetail.getUserName());
		basicAuthentication.setPassword(emailReportDetail.getPassword());
		com.nmhg.MI_ItemSyncResponse_SLMSServiceStub proxy = new com.nmhg.MI_ItemSyncResponse_SLMSServiceStub();
		Options clientOptions = proxy._getServiceClient().getOptions();
		clientOptions.setProperty(
				org.apache.axis2.transport.http.HTTPConstants.AUTHENTICATE,
				basicAuthentication);
		clientOptions.setProperty(HTTPConstants.REUSE_HTTP_CLIENT, "true");
		clientOptions.setTo(targetEPR);
		return proxy;
	}

	private MTItemSyncResponseDocument syncItem(final SyncTracker syncTracker) {
		String response = ((String[]) integrationServiceProxy.syncItem(syncTracker.getBodXML()))[0];
		try {
			syncTracker.setRecord(response);
			return MTItemSyncResponseDocument.Factory.parse(response);
		} catch (XmlException e) {
			throw new RuntimeException(e);
		}
	}

	private boolean populateSyncTracker(SyncTracker syncTracker,
			MTItemSyncResponseDocument itemSyncResponse) {
		ArrayList<String> errorMessageList = new ArrayList<String>();
		syncTracker.setUniqueIdName("ReferenceId");
		if (itemSyncResponse.getMTItemSyncResponse().getApplicationArea() != null) {
			syncTracker.setUniqueIdValue(itemSyncResponse.getMTItemSyncResponse()
					.getApplicationArea().getSender().getReferenceId());
		} else {
			syncTracker.setUniqueIdValue(null);
		}
		if (itemSyncResponse.getMTItemSyncResponse().getStatus().getCode().toString()
				.equalsIgnoreCase("FAILURE")) {
			syncTracker.setStatus(SyncStatus.FAILED);
			//syncTracker.setProcessing_status(SyncTracker.SUCCESS);
			syncTracker.setNoOfAttempts(syncTracker.getNoOfAttempts() + 1);
			if (itemSyncResponse
					.getMTItemSyncResponse().getItemMasterResponseArray() != null) {
				for (ItemMasterResponse item : itemSyncResponse
						.getMTItemSyncResponse().getItemMasterResponseArray()) {
					if (item.getStatus().getCode().toString().equalsIgnoreCase("FAILURE")) {
						for(Error error:item.getStatus().getExceptions().getErrorArray()){
							errorMessageList.add(error.getErrorMessage());
						}
					}
				}
			}
			String errorMessage = StringUtils.collectionToDelimitedString(
					errorMessageList, "$$$$");
			StringBuffer errorMessageHolder = new StringBuffer();
			errorMessageHolder.append("-------------Errors While Proceesing ItemSync-------------");
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
			return false;
		} else {
			syncTracker.setStatus(SyncStatus.COMPLETED);
			//syncTracker.setProcessing_status(SyncTracker.SUCCESS);
			if (syncTracker.getErrorMessage() != null) {
				syncTracker.setErrorMessage(null);
			}
			return true;
		}
	}

	@Override
	protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
		List<Long> idsTobeProcessed = syncTrackerDao.getItemIdsForProcessing(buName, maxNoOfRetries,getSyncType());

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
					updateSyncDetails(syncTracker);
					/*if(processingStatus!=null){
						syncTracker.setProcessing_status(SyncTracker.FAILURE);
					}else{
						syncTracker.setProcessing_status(SyncTracker.SUCCESS);
					}*/
				}catch (Exception e) {
					logger.error("Exception while Processing the Item Sync  for synctrakerid:"+syncTrackerId);
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


	public void setIntegrationServiceProxy(IntegrationService integrationServiceProxy) {
		this.integrationServiceProxy = integrationServiceProxy;
	}

	public Integer getMaxNoOfRetries() {
		return maxNoOfRetries;
	}


	public void setMaxNoOfRetries(Integer maxNoOfRetries) {
		this.maxNoOfRetries = maxNoOfRetries;
	}


	public SyncTrackerDAO getSyncTrackerDao() {
		return syncTrackerDao;
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


	public IntegrationService getIntegrationServiceProxy() {
		return integrationServiceProxy;
	}

	public EmailReportDetail getEmailReportDetail() {
		return emailReportDetail;
	}

	public void setItemSyncReportDetail(EmailReportDetail emailReportDetail) {
		this.emailReportDetail = emailReportDetail;
	}

}

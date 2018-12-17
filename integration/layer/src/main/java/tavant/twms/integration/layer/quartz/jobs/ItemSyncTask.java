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

package tavant.twms.integration.layer.quartz.jobs;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.acegisecurity.context.SecurityContextHolder;
import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlOptions;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.StatefulJob;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.util.StringUtils;

import com.nmhg.itemsynch_response.ItemSyncResponse;
import com.nmhg.itemsynch_response.ItemSyncResponse.ItemMasterResponse;
import com.nmhg.itemsynch_response.ItemSyncResponse.ItemMasterResponse.Status.Code;
import com.nmhg.itemsynch_response.MTItemSyncResponseDocument;

import tavant.globalsync.itemsync.SyncItemMasterDocumentDTO;
import tavant.globalsync.itemsync.SyncItemMasterDocumentDTO.SyncItemMaster;
import tavant.twms.domain.integration.SyncStatus;
import tavant.twms.domain.integration.SyncTracker;
import tavant.twms.domain.integration.SyncTrackerService;
import tavant.twms.integration.layer.IntegrationPropertiesBean;
import tavant.twms.integration.layer.component.global.GlobalItemSync;
import tavant.twms.integration.layer.constants.IntegrationConstants;
import tavant.twms.jbpm.infra.BeanLocator;
import tavant.twms.security.SecurityHelper;

public class ItemSyncTask extends QuartzJobBean implements StatefulJob {

	private static final Logger logger = Logger.getLogger(ItemSyncTask.class);

	private static boolean isTaskRunning = false;

	private GlobalItemSync globalItemSync;

	private SyncTrackerService syncTrackerService;

	private List<Long> syncTrackerIdsToBeProcessed;

	private IntegrationPropertiesBean integrationPropertiesBean;


	private Integer maxNoOfRetries;

	private String xmlHeader = "<?xml version=\"1.0\" encoding=\"utf-8\"?>";

	private final BeanLocator beanLocator = new BeanLocator();

	@Override
	protected void executeInternal(JobExecutionContext context)
			throws JobExecutionException {
		/*if (isTaskRunning) {
			logger.error("ItemSyncTask is still running");
			return;
		}
		isTaskRunning = true;

		try {
			syncTrackerService = (SyncTrackerService) this.beanLocator
					.lookupBean("syncTrackerService");
			globalItemSync = (GlobalItemSync) this.beanLocator
					.lookupBean("globalItemSync");
			integrationPropertiesBean = (IntegrationPropertiesBean) this.beanLocator
					.lookupBean("integrationPropertiesBean");
			populateDummyAuthentication();
			logger.info("Inside ItemSyncTask: Starting item sync process by thread "
							+ Thread.currentThread().getId());
			Calendar calStartTime = Calendar.getInstance();
			Date now = new Date();
			String url = integrationPropertiesBean.getItemSyncURL();
			long startTimeOfItemSyncTask = calStartTime.getTimeInMillis();
			logger.info("Start Time of ItemSyncTask "
					+ startTimeOfItemSyncTask);
			if (maxNoOfRetries == null) {
				maxNoOfRetries = new Integer(1);
			}
			syncTrackerIdsToBeProcessed = syncTrackerService.getIdsForProcessing(IntegrationConstants.ITEM,maxNoOfRetries);
			for (Long syncTrackerId : syncTrackerIdsToBeProcessed) {
               SyncTracker syncTracker = syncTrackerService.findById(syncTrackerId);
                   try {
						syncTracker.setErrorMessage(null);
						syncTracker.setErrorType(null);
						MTItemSyncResponseDocument itemSyncResponse = globalItemSync(syncTracker
								.getBodXML());
						populateSyncTracker(syncTracker, itemSyncResponse);
						
						String responseStr = itemSyncResponse
								.xmlText(createXMLOptions());
						StringBuffer sbResponse = new StringBuffer(xmlHeader);
						sbResponse.append(responseStr);

						WebMethodAxislClient axisClient = new WebMethodAxislClient();
						String itemSyncMethod = integrationPropertiesBean
								.getItemSyncMethod();
						String itemSyncInParam = integrationPropertiesBean
								.getItemSyncInParam();
						String itemSyncOutParam = integrationPropertiesBean
								.getItemSyncOutParam();
						String wmNamespace = integrationPropertiesBean
								.getWmNamespaceForItemResponse();
						String response = axisClient.makeCallWithNameSpace(url,
								sbResponse.toString(), itemSyncMethod,
								itemSyncInParam, itemSyncOutParam, wmNamespace);

						if (sbResponse != null) {
							//storing the item response request send to WebMethods 
							syncTracker.setRecord(sbResponse.toString());
						}
						if (!(response == null)) {
							if(!syncTracker.getStatus().equals(SyncStatus.FAILED)){
								syncTracker.setStatus(SyncStatus.FAILED);
								syncTracker.setNoOfAttempts(syncTracker.getNoOfAttempts() + 1);
							}
							StringBuffer errorMessageHolder= new StringBuffer();
							if(syncTracker.getErrorMessage() !=null){
								errorMessageHolder.append(syncTracker.getErrorMessage());
								errorMessageHolder.append("\n");
							}
							errorMessageHolder.append("----Error While Sending ItemSync Response To WebMethods----");
							errorMessageHolder.append("\n");
							errorMessageHolder.append(response);
							errorMessageHolder.append("\n");
							errorMessageHolder.append("-----------------------------------------------------------");
							syncTracker.setErrorMessage(errorMessageHolder.toString());
						}
					} catch (Exception e) {
						logger.error(" ** Exception submitting Item Response records ** "
										+ e);
						populateFailureDetails(syncTracker, e.getMessage());
					} finally {
						if(syncTracker.getErrorMessage()!=null){
							logger.info("List of Errors:-"+"\n"+syncTracker.getErrorMessage());
					}
						syncTracker.setUpdateDate(now);
						syncTrackerService.update(syncTracker);
						//syncTrackerDAO.update(syncTracker);
					}
				
			}

			Calendar calEndTime = Calendar.getInstance();
			long endTimeOfItemSyncTask = calEndTime.getTimeInMillis();
			logger.info("ItemSyncTask was executed in "
							+ (endTimeOfItemSyncTask - startTimeOfItemSyncTask)
							+ "m sec");
		} finally {
			isTaskRunning = false;
		}*/
	}

	private void populateFailureDetails(SyncTracker syncTracker,
			String errorMessage) {
		if(!syncTracker.getStatus().equals(SyncStatus.FAILED)){
			syncTracker.setStatus(SyncStatus.FAILED);
			syncTracker.setNoOfAttempts(syncTracker.getNoOfAttempts() + 1);
		}
		StringBuffer errorMessageHolder= new StringBuffer();
		if(syncTracker.getErrorMessage() !=null){
			errorMessageHolder.append(syncTracker.getErrorMessage());
			errorMessageHolder.append("\n");
		}
		errorMessageHolder.append("--------------------Exception Occurred---------------------");
		errorMessageHolder.append("\n");
		errorMessageHolder.append(errorMessage);
		errorMessageHolder.append("\n");
		errorMessageHolder.append("-----------------------------------------------------------");
		syncTracker.setErrorMessage(errorMessageHolder.toString());
		if(syncTracker.getProcessing_status()==null || !(StringUtils.hasText(syncTracker.getProcessing_status()))){
			syncTracker.setProcessing_status(SyncTracker.FAILURE);
		}
	}

	public MTItemSyncResponseDocument globalItemSync(String bod) {
		SyncItemMaster items = null;
		try {
			bod = StringUtils
					.replace(bod, "<SyncItemMaster>",
							"<SyncItemMaster xmlns=\"http://www.tavant.com/globalsync/itemsync\">");
            items = SyncItemMasterDocumentDTO.Factory.parse(bod).getSyncItemMaster();
		} catch (XmlException e) {
			logger.error(" Failed to sync global Sync Item:", e);
			return createErrorResponseForGlobalItemSync(e,
					" Error while transforming the global Item sync");
		}
        return globalItemSync.sync(items);
	}

	private MTItemSyncResponseDocument createErrorResponseForGlobalItemSync(
			Exception e, String syncTypeMessage) {
		MTItemSyncResponseDocument itemResponseDoc = MTItemSyncResponseDocument.Factory
				.newInstance();
		ItemSyncResponse itemSyncResponse = itemResponseDoc.addNewMTItemSyncResponse();
		itemSyncResponse.addNewStatus();
		itemSyncResponse.getStatus().setCode(ItemSyncResponse.Status.Code.Enum.forString("FAILURE"));
		/*itemSyncResponse.getStatus().setErrorMessage(
				new StringBuilder().append(syncTypeMessage).append(
						e.getMessage()).toString());*/
		return itemResponseDoc;
	}

	private void populateSyncTracker(SyncTracker syncTracker,
			MTItemSyncResponseDocument itemSyncResponse) {
		String errorMsg=null;
		ArrayList<String> errorMessageList = new ArrayList<String>();
		syncTracker.setUniqueIdName("ReferenceId");
		if(itemSyncResponse.getMTItemSyncResponse().getApplicationArea() !=null){
			syncTracker.setUniqueIdValue(itemSyncResponse.getMTItemSyncResponse()
					.getApplicationArea().getSender().getReferenceId());
			}
			else{
				syncTracker.setUniqueIdValue(null);
			}
		if (itemSyncResponse.getMTItemSyncResponse().getStatus().getCode().toString()
				.equalsIgnoreCase("FAILURE")) {
			syncTracker.setStatus(SyncStatus.FAILED);
			syncTracker.setProcessing_status(SyncTracker.FAILURE);
			syncTracker.setNoOfAttempts(syncTracker.getNoOfAttempts() + 1);
			/*if(itemSyncResponse.getItemSyncResponse().getStatus().getErrorMessage()!=null){
				errorMsg=itemSyncResponse.getItemSyncResponse().getStatus().getErrorMessage();
				syncTracker.setErrorMessage(errorMsg);
			}*/
			if(itemSyncResponse
					.getMTItemSyncResponse().getItemMasterResponseArray()!=null){
			for (ItemMasterResponse item : itemSyncResponse
					.getMTItemSyncResponse().getItemMasterResponseArray()) {
				/*if (item.getStatus().getCode().toString().equalsIgnoreCase("FAILURE")) {
					errorMessageList.add(item.getStatus().getErrorMessage());
				}*/
			}
			}
			String errorMessage = StringUtils.collectionToDelimitedString(
					errorMessageList, "$$$$");
			StringBuffer errorMessageHolder= new StringBuffer();					
			errorMessageHolder.append("-------------Errors While Proceesing ItemSync-------------");
			errorMessageHolder.append("\n");
			if(syncTracker.getErrorMessage() !=null){
				errorMessageHolder.append(syncTracker.getErrorMessage());
				errorMessageHolder.append("\n");
			}	
			if(errorMessage !=null){
			errorMessageHolder.append(errorMessage);
			errorMessageHolder.append("\n");
			}
			errorMessageHolder.append("-----------------------------------------------------------");
			syncTracker.setErrorMessage(errorMessageHolder.toString());
		} else {
			syncTracker.setStatus(SyncStatus.COMPLETED);
			syncTracker.setProcessing_status(SyncTracker.SUCCESS);
			if(syncTracker.getErrorMessage() !=null){
				syncTracker.setErrorMessage(null);
			}
		}
	}

	private XmlOptions createXMLOptions() {
		// Generate the XML document
		XmlOptions xmlOptions = new XmlOptions();
		xmlOptions.setSavePrettyPrint();
		xmlOptions.setSavePrettyPrintIndent(4);
		xmlOptions.setSaveAggressiveNamespaces();
		xmlOptions.setUseDefaultNamespace();
		return xmlOptions;
	}

	public Integer getMaxNoOfRetries() {
		return maxNoOfRetries;
	}

	public void setMaxNoOfRetries(Integer maxNoOfRetries) {
		this.maxNoOfRetries = maxNoOfRetries;
	}

	private void populateDummyAuthentication() {
		SecurityHelper securityHelper = new SecurityHelper();
		if (SecurityContextHolder.getContext().getAuthentication() == null) {
			securityHelper.populateSystemUser();
		}
	}

}

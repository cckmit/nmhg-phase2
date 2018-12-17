
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

package tavant.twms.integration.layer.component.global;

import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.rpc.ServiceException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.acegisecurity.context.SecurityContextHolder;
import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlOptions;
import org.springframework.util.StringUtils;

import tavant.globalsync.extendedwarrantynotification.ExtWarrantyNotificationDocumentDTO;
import tavant.globalsync.installbasesync.InstallBaseSyncDocumentDTO;
import tavant.globalsync.itemsync.SyncItemMasterDocumentDTO;
import tavant.globalsync.itemsync.SyncItemMasterDocumentDTO.SyncItemMaster;
import tavant.globalsync.warrantyclaimcreditnotification.ApplicationAreaTypeDTO;
import tavant.globalsync.warrantyclaimcreditnotification.InvoiceTypeDTO;
import tavant.globalsync.warrantyclaimcreditnotification.SyncInvoiceDocumentDTO;
import tavant.twms.domain.integration.SyncStatus;
import tavant.twms.domain.integration.SyncTracker;
import tavant.twms.domain.integration.SyncTrackerService;
import tavant.twms.integration.layer.IntegrationPropertiesBean;
import tavant.twms.integration.layer.component.SyncResponse;
import tavant.twms.integration.layer.constants.IntegrationConstants;
import tavant.twms.integration.layer.constants.ItemSyncInterfaceErrorConstants;
import tavant.twms.integration.layer.quartz.jobs.WebMethodAxislClient;
import tavant.twms.integration.layer.transformer.SyncResponseTransformer;
import tavant.twms.jbpm.infra.BeanLocator;
import tavant.twms.security.SecurityHelper;

import com.nmhg.itemsynch_response.ItemSyncResponse;
import com.nmhg.itemsynch_response.ItemSyncResponse.ItemMasterResponse;
import com.nmhg.itemsynch_response.ItemSyncResponse.ItemMasterResponse.Status.Exceptions;
import com.nmhg.itemsynch_response.ItemSyncResponse.ItemMasterResponse.Status.Exceptions.Error;
import com.nmhg.itemsynch_response.MTItemSyncResponseDocument;

public class CommonSyncProcessor {

	private GlobalItemSync globalItemSync;

	private IntegrationPropertiesBean integrationPropertiesBean;

	private Integer maxNoOfRetries;

	private Date today = new Date();

	private String xmlHeader = "<?xml version=\"1.0\" encoding=\"utf-8\"?>";

	private GlobalInstallBaseSync globalInstallBaseSync;

	private ProcessGlobalExtWarrantyPurchaseNotification processGlobalExtWarrantyPurchaseNotification;

	private SyncResponseTransformer syncResponseTransformer;

    private ProcessGlobalCreditNotification processGlobalCreditNotification;
    
	private final BeanLocator beanLocator = new BeanLocator();
	
	private SyncTrackerService syncTrackerService;
	
	private ItemSyncInterfaceErrorConstants itemSyncInterfaceErrorConstants;

	private static final Logger logger = Logger.getLogger(CommonSyncProcessor.class);

	public void syncProcessor(String syncType,
			List<Long> syncTrackerIdsList, boolean isReprocess) {
		
		integrationPropertiesBean = (IntegrationPropertiesBean) this.beanLocator
				.lookupBean("integrationPropertiesBean");
		
		syncTrackerService = (SyncTrackerService) this.beanLocator.lookupBean("syncTrackerService");
        
        syncType = syncType.trim();
		
		if (IntegrationConstants.ITEM.equalsIgnoreCase(syncType)) {
			processItemSync(syncTrackerIdsList, isReprocess);
		}

		if (IntegrationConstants.INSTALLBASE.equalsIgnoreCase(syncType)) {
			processInstallBase(syncTrackerIdsList);
		}

		if (IntegrationConstants.CREDITSUBMISSION.equalsIgnoreCase(syncType) ||
                IntegrationConstants.CLAIM.equalsIgnoreCase(syncType)) {
			processCreditSubMission(syncTrackerIdsList);
		}

		if (IntegrationConstants.EXTWARRANTYPURCHASENOTIFICATION.equalsIgnoreCase(syncType)) {
			processExtWarrantyPurchaseNotification(syncTrackerIdsList);
		}
        
        if(IntegrationConstants.CREDIT_NOTIFICATION.equalsIgnoreCase(syncType)){
            processCreditNotification(syncTrackerIdsList);
        }
	}

	private void processExtWarrantyPurchaseNotification(
			List<Long> syncTrackerIdsList) {
		for (Long syncTrackerId : syncTrackerIdsList) {
            SyncTracker syncTracker = syncTrackerService.findById(syncTrackerId);
			try {
				List<SyncResponse> responses = syncGlobalExtWarrantyPurchaseNotification(syncTracker
						.getBodXML());
				populateSyncTracker(syncTracker, responses);
			}  catch (Exception e) {
				logger.error(" ** Exception in ExtWarrantyPurchaseNotification ReProcessing for SyncTracker Record with id"+syncTracker.getId()+"**  " , e);
				if(e.getMessage()!=null){
					populateFailureDetails(syncTracker, e.getMessage());
					}
					else{
						populateFailureDetails(syncTracker, e.toString());
					}
			} finally {
				syncTracker.setUpdateDate(new Date());
				syncTrackerService.update(syncTracker);
				//syncTrackerDAO.update(syncTracker);
			}
		}
	}

	private void processCreditSubMission(List<Long> syncTrackerIdsList) {
		/*String url = integrationPropertiesBean.getCreditSubmitURL();
		String creditSubmitMethod = integrationPropertiesBean
				.getCreditSubmitMethod();
		String creditSubmitInParam = integrationPropertiesBean
				.getCreditSubmitInParam();
		String creditSubmitOutParam = integrationPropertiesBean
				.getCreditSubmitOutParam();

		for (Long syncTrackerid : syncTrackerIdsList) {
            SyncTracker syncTracker = syncTrackerService.findById(syncTrackerid);
			try {
				String bodXML = syncTracker.getBodXML();
				logger.info(" ** BOD XML : ** " + bodXML);

				WebMethodAxislClient axisClient = new WebMethodAxislClient();

				String response = axisClient.makeCall(url, bodXML,
						creditSubmitMethod, creditSubmitInParam,
						creditSubmitOutParam);

				if (response == null) {
					syncTracker.setStatus(SyncStatus.COMPLETED);
				} else {
					syncTracker.setStatus(SyncStatus.FAILED);
					syncTracker.setErrorMessage(response);
				}
			} catch (Exception e) {
				logger.error(" ** Exception in CreditSubMission ReProcessing for SyncTracker Record with id"+syncTracker.getId()+"**  ", e);
				if(e.getMessage()!=null){
					populateFailureDetails(syncTracker, e.getMessage());
					}
					else{
						populateFailureDetails(syncTracker, e.toString());
					}
			} finally {
				syncTracker.setUpdateDate(new Date());
				syncTrackerService.update(syncTracker);
			}
		}*/
	}

	private void processInstallBase(List<Long> syncTrackerIdsList) {
		for (Long syncTrackerId : syncTrackerIdsList) {
            SyncTracker syncTracker = syncTrackerService.findById(syncTrackerId);
            try {
				List<SyncResponse> responses = globalInstallBaseSync(syncTracker
						.getBodXML());
				populateSyncTracker(syncTracker, responses);
				
			} catch (Exception e) {
				logger.error(" ** Exception in InstallBase ReProcessing for SyncTracker Record with id"+syncTracker.getId()+"** ", e);	
				if(e.getMessage()!=null){
				populateFailureDetails(syncTracker, e.getMessage());
				}
				else{
					populateFailureDetails(syncTracker, e.toString());
				}
			} finally {
				syncTracker.setUpdateDate(new Date());
				syncTrackerService.update(syncTracker);
			}
		}
	}

	private void populateSyncTracker(SyncTracker syncTracker,
			List<SyncResponse> responses) {
		SyncResponse syncResponse = responses.get(0);
		syncTracker.setUniqueIdName(syncResponse.getUniqueIdName());
		syncTracker.setUniqueIdValue(syncResponse.getUniqueIdValue());
		//String responseStr =createResponseString(syncResponse);
		String responseStr =syncResponseTransformer.transformResponse(responses);
		String errorType = syncResponse.getErrorType();
		if (errorType == null
				|| org.apache.commons.lang.StringUtils.isEmpty(errorType)) {
			syncTracker.setStatus(SyncStatus.COMPLETED);
		} else {
			syncTracker.setStatus(SyncStatus.FAILED);
			syncTracker.setErrorType(errorType);
			syncTracker.setErrorMessage(responseStr);
			syncTracker.setNoOfAttempts(syncTracker.getNoOfAttempts() + 1);
		}
	}

	private void processItemSync(List<Long> syncTrackerIdsList,
			boolean isReprocess) {
		/*String url = integrationPropertiesBean.getItemSyncURL();
		String itemSyncMethod = integrationPropertiesBean.getItemSyncMethod();
		String itemSyncInParam = integrationPropertiesBean.getItemSyncInParam();
		String itemSyncOutParam = integrationPropertiesBean
				.getItemSyncOutParam();
		String wmNamespace = integrationPropertiesBean
				.getWmNamespaceForItemResponse();
		for (Long syncTrackerId : syncTrackerIdsList) {
            SyncTracker syncTracker = syncTrackerService.findById(syncTrackerId);
            try {
				if (isReprocess) {
					if("SUCCESS".equalsIgnoreCase(syncTracker.getProcessing_status())){
						sendRecordXml(syncTracker,url,itemSyncMethod,itemSyncInParam,itemSyncOutParam,wmNamespace);
					}
					else{
						syncTracker.setStatus(SyncStatus.TO_BE_PROCESSED);
						syncTracker.setNoOfAttempts(0);
						syncTracker.setUpdateDate(today);
						syncTracker.setErrorMessage(null);
						syncTracker.setErrorType(null);
					}
				} else {
					if("SUCCESS".equalsIgnoreCase(syncTracker.getProcessing_status())){
						sendRecordXml(syncTracker,url,itemSyncMethod,itemSyncInParam,itemSyncOutParam,wmNamespace);
					}
					else{
						processItemSyncAgain(syncTracker,url,itemSyncMethod,itemSyncInParam,itemSyncOutParam,wmNamespace);
					}
				}
			}catch (Exception e) {
				logger
						.error(" ** Exception in ItemSync ReProcessing for SyncTracker Record with id"+syncTracker.getId()+"** "
								+ e);
				if( e.getMessage() !=null){
				populateFailureDetailsForItemSync(syncTracker, e.getMessage());
				}
				else{
					populateFailureDetailsForItemSync(syncTracker, e.toString());
				}
			} finally {
				if(syncTracker.getErrorMessage()!=null){
					logger.info("List of Errors:-"+"\n"+syncTracker.getErrorMessage());
			}
				syncTracker.setUpdateDate(new Date());
				syncTrackerService.update(syncTracker);
			}
		}*/
	}

	private void processItemSyncAgain(SyncTracker syncTracker, String url, String itemSyncMethod, String itemSyncInParam, String itemSyncOutParam, String wmNamespace) throws MalformedURLException, RemoteException, ServiceException {
		syncTracker.setErrorMessage(null);
		syncTracker.setErrorType(null);
		MTItemSyncResponseDocument itemSyncResponse = globalItemSync(syncTracker
				.getBodXML());
		populateSyncTrackerForItemSync(syncTracker, itemSyncResponse);
		String responseStr = itemSyncResponse
				.xmlText(createXMLOptions());
		StringBuffer sbResponse = new StringBuffer(xmlHeader);
		sbResponse.append(responseStr);

		WebMethodAxislClient axisClient = new WebMethodAxislClient();
		
		String response = axisClient.makeCallWithNameSpace(url,
				sbResponse.toString(), itemSyncMethod,
				itemSyncInParam, itemSyncOutParam, wmNamespace);

		if (sbResponse != null) {
			// storing the response request send to WebMethods
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
	
	}

	private void sendRecordXml(SyncTracker syncTracker, String url, String itemSyncMethod, String itemSyncInParam, String itemSyncOutParam, String wmNamespace) {
		WebMethodAxislClient axisClient = new WebMethodAxislClient();
		
		String response;
		try {
			response = axisClient.makeCallWithNameSpace(url,
					syncTracker.getRecord(), itemSyncMethod,
					itemSyncInParam, itemSyncOutParam, wmNamespace);
			 if (response == null) {
				 syncTracker.setStatus(SyncStatus.COMPLETED);
			 }
				 else{
					 syncTracker.setStatus(SyncStatus.FAILED);
					 syncTracker.setErrorMessage(response);
					 syncTracker.setNoOfAttempts(syncTracker.getNoOfAttempts() + 1);
				}
		} catch (Exception e) {
			logger
			.error(" ** Exception While Sending The Response to WebMthods** "
					+ e);
			if( e.getMessage() !=null){
			populateFailureDetailsForSendingResponse(syncTracker, e.getMessage());
			}
			else{
				populateFailureDetailsForSendingResponse(syncTracker, e.toString());
			}
		}

		
		
	}

	private void populateFailureDetailsForSendingResponse(SyncTracker record,
			String errorMessage) {
		record.setStatus(SyncStatus.FAILED);
		record.setErrorMessage(errorMessage);
		record.setNoOfAttempts(record.getNoOfAttempts() + 1);
	}
	
	private void populateFailureDetailsForItemSync(SyncTracker syncTracker,
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
			syncTracker.setErrorMessage(errorMessage);
			if(syncTracker.getProcessing_status()==null || !(StringUtils.hasText(syncTracker.getProcessing_status()))){
				syncTracker.setProcessing_status(SyncTracker.FAILURE);
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

	public MTItemSyncResponseDocument globalItemSync(String bod) {
		SyncItemMaster items = null;
		try {
			populateDummyAuthentication();
			bod = StringUtils
					.replace(bod, "<SyncItemMaster>",
							"<SyncItemMaster xmlns=\"http://www.tavant.com/globalsync/itemsync\">");
            items = SyncItemMasterDocumentDTO.Factory.parse(bod).getSyncItemMaster();
		} catch (XmlException e) {
			logger.error(" Failed to sync global Sync Item:", e);
			Map<String, String> errorMessage=new HashMap<String, String>();
			errorMessage
					.put(ItemSyncInterfaceErrorConstants.I0011,
							itemSyncInterfaceErrorConstants
									.getPropertyMessageFromErrorCode(ItemSyncInterfaceErrorConstants.I0011));
			return createErrorResponseForGlobalItemSync(e,
					errorMessage);
		}
        return globalItemSync.sync(items);
	}

	private MTItemSyncResponseDocument createErrorResponseForGlobalItemSync(
			Exception e, Map<String, String> syncTypeMessage) {
		MTItemSyncResponseDocument itemResponseDoc = MTItemSyncResponseDocument.Factory
				.newInstance();
		ItemSyncResponse itemSyncResponse =itemResponseDoc.addNewMTItemSyncResponse();
		itemSyncResponse.addNewStatus();
		itemSyncResponse.getStatus().setCode(ItemSyncResponse.Status.Code.Enum.forString(SyncTracker.FAILURE));
		Exceptions exceptions = itemResponseDoc.addNewMTItemSyncResponse().addNewItemMasterResponse().addNewStatus()
				.addNewExceptions();
		for (Map.Entry<String, String> errorMessage : syncTypeMessage
				.entrySet()) {
			Error error = exceptions.addNewError();
			error.setErrorCode(errorMessage.getKey());
			error.setErrorMessage(errorMessage.getValue());
		}
		/*itemSyncResponse.getStatus().setErrorMessage(
				new StringBuilder().append(syncTypeMessage).append(
						e.getMessage()).toString());*/
		return itemResponseDoc;
	}

	private void populateSyncTrackerForItemSync(SyncTracker syncTracker,
			MTItemSyncResponseDocument itemSyncResponse) {
		ArrayList<String> errorMessageList = new ArrayList<String>();
		String errorMsg=null;
		syncTracker.setUniqueIdName("ReferenceId");
		if(itemSyncResponse.getMTItemSyncResponse().getApplicationArea() !=null){
		syncTracker.setUniqueIdValue(itemSyncResponse.getMTItemSyncResponse()
				.getApplicationArea().getSender().getReferenceId());
		}
		else{
			syncTracker.setUniqueIdValue(null);
		}
		if (itemSyncResponse.getMTItemSyncResponse().getStatus().getCode().toString()
				.equalsIgnoreCase(SyncTracker.FAILURE)) {
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
				/*if (item.getStatus().getCode().toString().equalsIgnoreCase(SyncTracker.FAILURE)) {
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

	private void populateFailureDetails(SyncTracker syncTracker,
			String errorMessage) {
		if(syncTracker.getStatus()==null ||!(syncTracker.getStatus().equals(SyncStatus.FAILED))){
			syncTracker.setStatus(SyncStatus.FAILED);
			syncTracker.setNoOfAttempts(syncTracker.getNoOfAttempts() + 1);
		}
		syncTracker.setErrorMessage(errorMessage);
	}

	public List<SyncResponse> globalInstallBaseSync(String bod) {
        InstallBaseSyncDocumentDTO installBaseSyncDocumentDTO=null;
		try {
			populateDummyAuthentication();
			bod = StringUtils
					.replace(bod, "<InstallBaseSync>",
							"<InstallBaseSync xmlns=\"http://www.tavant.com/globalsync/installbasesync\">");
            installBaseSyncDocumentDTO=InstallBaseSyncDocumentDTO.Factory.parse(bod);
		} catch (XmlException e) {
			logger.error(" Failed to sync Sync InstallBase:", e);
			return createErrorResponse(e,
					" Error while transforming the InstallBase sync");
		}
        return globalInstallBaseSync.sync(installBaseSyncDocumentDTO);
	}

	private void populateDummyAuthentication() {
		SecurityHelper securityHelper = new SecurityHelper();
		if (SecurityContextHolder.getContext().getAuthentication() == null) {
			securityHelper.populateSystemUser();
		}
	}

	private List<SyncResponse> createErrorResponse(Exception e,
			String syncTypeMessage) {
		List<SyncResponse> responses = new ArrayList<SyncResponse>();
		SyncResponse response;
		response = new SyncResponse();
		response.setSuccessful(false);
		response.setException(new StringBuilder().append(syncTypeMessage)
				.append(e.getMessage()).toString());
		response.setErrorCode(SyncResponse.ERROR_CODE_VALIDATION_ERROR);
		response.setErrorType(SyncResponse.ERROR_CODE_VALIDATION_ERROR);
		responses.add(response);

		return responses;
	}

	public List<SyncResponse> syncGlobalExtWarrantyPurchaseNotification(
			String bod) {

        ExtWarrantyNotificationDocumentDTO extnWntyNotificationDocDTO = null;
        try {
			populateDummyAuthentication();
			bod = StringUtils
					.replace(
							bod,
							"<ExtWarrantyNotification>",
							"<ExtWarrantyNotification xmlns=\"http://www.tavant.com/globalsync/extendedwarrantynotification\">");
            extnWntyNotificationDocDTO = ExtWarrantyNotificationDocumentDTO.Factory.parse(bod);
		} catch (XmlException e) {
			logger.error(
					" Failed to sync ExtendedWarranty Purchase Notification:",
					e);
			return createErrorResponse(e,
					" Error while tranforming the ExtendedWarranty Purchase Notification");
		}
		return processGlobalExtWarrantyPurchaseNotification
				.sync(extnWntyNotificationDocDTO);

	}

	public GlobalItemSync getGlobalItemSync() {
		return globalItemSync;
	}

	public void setGlobalItemSync(GlobalItemSync globalItemSync) {
		this.globalItemSync = globalItemSync;
	}

	public GlobalInstallBaseSync getGlobalInstallBaseSync() {
		return globalInstallBaseSync;
	}

	public void setGlobalInstallBaseSync(GlobalInstallBaseSync globalInstallBaseSync) {
		this.globalInstallBaseSync = globalInstallBaseSync;
	}

	public ProcessGlobalExtWarrantyPurchaseNotification getProcessGlobalExtWarrantyPurchaseNotification() {
		return processGlobalExtWarrantyPurchaseNotification;
	}

	public void setProcessGlobalExtWarrantyPurchaseNotification(
			ProcessGlobalExtWarrantyPurchaseNotification processGlobalExtWarrantyPurchaseNotification) {
		this.processGlobalExtWarrantyPurchaseNotification = processGlobalExtWarrantyPurchaseNotification;
	}

	public SyncResponseTransformer getSyncResponseTransformer() {
		return syncResponseTransformer;
	}

	public void setSyncResponseTransformer(
			SyncResponseTransformer syncResponseTransformer) {
		this.syncResponseTransformer = syncResponseTransformer;
	}

    public ProcessGlobalCreditNotification getProcessGlobalCreditNotification() {
        return processGlobalCreditNotification;
    }

    public void setProcessGlobalCreditNotification(ProcessGlobalCreditNotification processGlobalCreditNotification) {
        this.processGlobalCreditNotification = processGlobalCreditNotification;
    }

    private void processCreditNotification(List<Long> syncTrackerIdsList) {
        InputStream xsltStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("xslt/transformCreditNotification.xsl");
        if(xsltStream == null)
            throw new RuntimeException("XSL file for credit notification xml transformation not found !!!");
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = null;
        try {
            transformer = transformerFactory.newTransformer(new StreamSource(xsltStream));
        } catch (TransformerConfigurationException ex) {
            throw new RuntimeException("Could not create XSL transformer !!!", ex);
        }
        for (Long syncTrackerId : syncTrackerIdsList) {
            populateDummyAuthentication();
            SyncTracker syncTracker = syncTrackerService.findById(syncTrackerId);
            if(SyncStatus.COMPLETED.equals(syncTracker.getStatus())) continue;
            try {
                StringWriter s = new StringWriter();
                transformer.transform(new StreamSource(new StringReader(syncTracker.getBodXML())), new StreamResult(s));
                InvoiceTypeDTO invoice = SyncInvoiceDocumentDTO.Factory.parse(s.toString()).getSyncInvoice().getDataArea().getInvoice();
                ApplicationAreaTypeDTO applicationArea = SyncInvoiceDocumentDTO.Factory.parse(s.toString()).getSyncInvoice().getApplicationArea();
                List<InvoiceTypeDTO> invoices = new ArrayList<InvoiceTypeDTO>();
                invoices.add(invoice);
                List<SyncResponse> responses = processGlobalCreditNotification.sync(invoices,applicationArea);
				populateSyncTracker(syncTracker, responses);
			} catch (Exception e) {
				logger.error(" ** Exception in CreditNotification ReProcessing for SyncTracker Record with id"+syncTracker.getId()+"** ", e);	
				if(e.getMessage()!=null){
                    populateFailureDetails(syncTracker, e.getMessage());
				}
				else{
					populateFailureDetails(syncTracker, e.toString());
				}
			} finally {
				syncTracker.setUpdateDate(new Date());
				syncTrackerService.update(syncTracker);
			}
		}
    }
    public ItemSyncInterfaceErrorConstants getItemSyncInterfaceErrorConstants() {
		return itemSyncInterfaceErrorConstants;
	}

	public void setItemSyncInterfaceErrorConstants(
			ItemSyncInterfaceErrorConstants itemSyncInterfaceErrorConstants) {
		this.itemSyncInterfaceErrorConstants = itemSyncInterfaceErrorConstants;
	}
}

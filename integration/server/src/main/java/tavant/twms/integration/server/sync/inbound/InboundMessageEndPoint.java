/*
 *   Copyright (c)2007 Tavant Technologies
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
 *
 */
package tavant.twms.integration.server.sync.inbound;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.xml.source.DomSourceFactory;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import tavant.twms.integration.layer.IntegrationService;
import tavant.twms.integration.server.common.dataaccess.SyncStatus;
import tavant.twms.integration.server.common.dataaccess.SyncTrackerDAO;
import tavant.twms.integration.server.util.IntegrationServerConstants;

/**
 * An service activator for Inbound messages, does nothing just acts as an subscriber
 * for incoming messages and forwards the messages to outbound channel
 * 
 * @author prasad.r
 */

@Endpoint
public class InboundMessageEndPoint {

    private static final Logger logger = Logger.getLogger(InboundMessageEndPoint.class.getName());
    private static final String SUCCESS_XML = "<?xml version=\"1.0\" encoding=\"utf-8\"?><OraStatusBean><status>SUCCESS</status><errorMessage/><errorCode/></OraStatusBean>";

    @Autowired
    private IntegrationService integrationService;

    @Autowired
    private SyncTrackerDAO syncTrackerDao;

	@ServiceActivator(inputChannel = "inboundChannel")
    @PayloadRoot(localPart = "sync", namespace = "http://www.tavant.com/globalsync/failurecodesesync/definition")
    public Source syncFailureCode(DOMSource src) {
        return soapResponse(integrationService.syncFailureCode(src.getNode().getFirstChild().getTextContent()));
    }

    @ServiceActivator(inputChannel = "inboundChannel")
    @PayloadRoot(localPart = "sync", namespace = "http://www.tavant.com/globalsync/installbasesync/definition")
    public Source syncInstallBase(DOMSource src) {
    	logger.debug("XML TEXT NODE FIRST CHAILD TEXT CONTENT-------"+src.getNode().getFirstChild().getTextContent());
    	String modifiedSrc=getModifiedSrcString(src);
  	syncTrackerDao.save(IntegrationServerConstants.INSTALLBASE_SYNC_JOB_UNIQUE_IDENTIFIER, modifiedSrc, SyncStatus.TO_BE_PROCESSED);
  	return soapResponse(SUCCESS_XML);
    }

    @ServiceActivator(inputChannel = "inboundChannel")
    @PayloadRoot(localPart = "sync", namespace = "http://www.tavant.com/globalsync/itemsync/definition")
    public Source syncItem(DOMSource src) {
		String modifiedSrc = StringUtils
				.replace(
						StringUtils
								.replace(
										src.getNode().getFirstChild()
												.getTextContent(),
										"<ns0:SyncItemMaster xmlns:ns0=\"http://www.tavant.com/globalsync/itemsync\">",
										"<SyncItemMaster xmlns=\"http://www.tavant.com/globalsync/itemsync\">"),
						"</ns0:SyncItemMaster>", "</SyncItemMaster>");
        syncTrackerDao.save("Item", modifiedSrc, SyncStatus.TO_BE_PROCESSED);
       return soapResponse(SUCCESS_XML);
    }
     @ServiceActivator(inputChannel = "inboundChannel")
    @PayloadRoot(localPart = "sync", namespace = "http://www.tavant.com/globalsync/techniciansync/definition")
    public Source syncTechnicians(DOMSource src) {
    		syncTrackerDao.save(IntegrationServerConstants.TECHNICIAN_SYNC_JOB_UNIQUE_IDENTIFIER, src.getNode().getFirstChild()
    				.getTextContent(), SyncStatus.TO_BE_PROCESSED);
        /*return soapResponse(integrationService.syncTechnicians(src.getNode().getFirstChild()
    		.getTextContent()));*/
      return soapResponse(SUCCESS_XML);
    }
     
 	@ServiceActivator(inputChannel = "inboundChannel")
    @PayloadRoot(localPart = "sync", namespace = "http://www.tavant.com/globalsync/exchangeratesync/definition")
    public Source syncExchangeRate(DOMSource src)  {
		syncTrackerDao.save(IntegrationServerConstants.CURRENCY_EXCHANGE_RATE, src.getNode().getFirstChild().getTextContent(), SyncStatus.TO_BE_PROCESSED);
        return soapResponse(SUCCESS_XML);
    }

     @ServiceActivator(inputChannel = "inboundChannel")
     @PayloadRoot(localPart = "sync", namespace = "http://www.tavant.com/globalsync/bookingsync/definition")
     public Source syncBooking(DOMSource src) {
     		syncTrackerDao.save(IntegrationServerConstants.BOOKING_SYNC_JOB_UNIQUE_IDENTIFIER, src.getNode().getFirstChild()
     				.getTextContent(), SyncStatus.TO_BE_PROCESSED);
        return soapResponse(SUCCESS_XML);
     }
     

	@ServiceActivator(inputChannel = "inboundChannel")
	@PayloadRoot(localPart = "sync", namespace = "http://www.tavant.com/dealerinterfaces/claimsubmission/definition")
	public Source syncBatchClaims(DOMSource src) {
        logger.error("##############################Btach ClaiM xml Starting first chaild record ##########");
		
		logger.trace(src.getNode().getTextContent());
		
		logger.error(src.getNode().getTextContent());
		
		logger.error("##############################Btach ClaiM xml Ending first chaild record ##########");
		
		String modifiedSrc = StringUtils
				.replace(
						StringUtils
								.replace(
										src.getNode()
												.getTextContent(),
										"<ns0:ClaimSubmission xmlns:ns0=\"http://www.tavant.com/dealerinterfaces/claimsubmission/request\">",
										"<ClaimSubmission xmlns=\"http://www.tavant.com/dealerinterfaces/claimsubmission/request\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.tavant.com/dealerinterfaces/claimsubmission/request ClaimSubmissionRequest.xsd \">"),
						"</ns0:ClaimSubmission>", "</ClaimSubmission>");
		
		logger.error("######################MBatch Claim MOdified xml starting########################");
		
		logger.error(modifiedSrc);
		
		logger.error("##############################Btach ClaiM Modifed xml Ending##########");
		syncTrackerDao
				.save(IntegrationServerConstants.BATCH_CLAIM_SYNC_JOB_UNIQUE_IDENTIFIER,
						modifiedSrc,
						SyncStatus.TO_BE_PROCESSED);
		//return soapResponse(integrationService.syncBatchClaim(modifiedSource));
	return soapResponse(SUCCESS_XML);
	}
	
    @ServiceActivator(inputChannel = "inboundChannel")
    @PayloadRoot(localPart = "sync", namespace = "http://www.tavant.com/globalsync/customersync/definition")
    public Source syncCustomer(DOMSource src) {
    	String modifiedSrc = StringUtils
				.replace(
						StringUtils
								.replace(
										src.getNode().getFirstChild()
												.getTextContent(),
										"<ns0:CustomerSyncRequest xmlns:ns0=\"http://www.tavant.com/globalsync/customersync\">",
										"<CustomerSyncRequest xmlns=\"http://www.tavant.com/globalsync/customersync\">"),
						"</ns0:CustomerSyncRequest>", "</CustomerSyncRequest>");
	syncTrackerDao.save(IntegrationServerConstants.CUSTOMER_SYNC_JOB_UNIQUE_IDENTIFIER, modifiedSrc, SyncStatus.TO_BE_PROCESSED);
	return soapResponse(SUCCESS_XML); 
    }


	
    @ServiceActivator(inputChannel = "inboundChannel")
    @PayloadRoot(localPart = "sync", namespace = "http://www.tavant.com/globalsync/extwarrantynotification/definition")
    public Source syncExtWarrantyPurchaseNotification(DOMSource src) {
        return soapResponse(integrationService.syncExtWarrantyPurchaseNotification(src.getNode().getFirstChild().getTextContent()));
    }

    @ServiceActivator(inputChannel = "inboundChannel")
    @PayloadRoot(localPart = "sync", namespace = "http://www.tavant.com/globalsync/warrantyclaimcreditnotification/definition")
    public Source syncWarrantyClaimCreditNotification(DOMSource src) {
    	logger.trace(src);
    	String modifiedSrc = StringUtils
				.replace(
						StringUtils
								.replace(
										src.getNode().getFirstChild()
												.getTextContent(),
										"<ns0:SyncInvoice xmlns:ns0=\"http://www.tavant.com/globalsync/warrantyclaimcreditnotification\">",
										"<SyncInvoice xmlns=\"http://www.tavant.com/globalsync/warrantyclaimcreditnotification\">"),
						"</ns0:SyncInvoice>", "</SyncInvoice>");
    	syncTrackerDao.save(IntegrationServerConstants.CREDITNOTIFICATION_SYNC_JOB_UNIQUE_IDENTIFIER, modifiedSrc, SyncStatus.TO_BE_PROCESSED);
    	//return soapResponse(integrationService.syncWarrantyClaimCreditNotification(modifiedSrc));
    	return soapResponse(SUCCESS_XML);
    }

    private String getModifiedSrcString(DOMSource src) {
    	String modifiedSrc1=StringUtils
				.replace(
						src.getNode().getFirstChild()
						.getTextContent(),
						"<ns0:sync xmlns:ns0=\"http://www.tavant.com/globalsync/installbasesync/definition\">",
						"");
    	String modifiedSrc2 = StringUtils
				.replace(modifiedSrc1,"<ns0:SyncRequest>", "");
    	String modifiedSrc3=StringUtils
				.replace(
						modifiedSrc2,
						"<![CDATA[",
						"");
    	String modifiedSrc4=StringUtils
				.replace(
						modifiedSrc3,
						"<ns0:InstallBaseSync xmlns:ns0=\"http://www.tavant.com/globalsync/installbasesync\">",
						"<ins:InstallBaseSync xmlns:ins=\"http://www.tavant.com/globalsync/installbasesync\">");
    	String modifiedSrc5=StringUtils
				.replace(
						modifiedSrc4,
						"]]>",
						"");
    	String modifiedSrc6=StringUtils
				.replace(
						modifiedSrc5,
						"</ns0:SyncRequest>",
						"");
    	String ModifiedSrc7=StringUtils
				.replace(
						modifiedSrc6,
						"</ns0:sync>",
						"");
    	String ModifiedSrc8=StringUtils
				.replace(
						ModifiedSrc7,
						"</ns0:InstallBaseSync>",
						"</ins:InstallBaseSync>");
    	return ModifiedSrc8;
	} 
    


    private Source soapResponse(Object response) {
        try {
            Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
            Element root = document.createElement("syncResponse");
            document.appendChild(root);
            Element child = document.createElement("syncReturn");
            child.appendChild(document.createTextNode((String) response));
            root.appendChild(child);
            return new DomSourceFactory().createSource(document);
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        }
    }
}

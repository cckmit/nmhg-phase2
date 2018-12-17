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

/**
 * Created Mar 14, 2007 5:01:35 PM
 * @author kapil.pandit
 */

package tavant.twms.integration.layer;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.RemoteException;
import java.util.Calendar;

import javax.xml.namespace.QName;
import javax.xml.rpc.ParameterMode;
import javax.xml.rpc.ServiceException;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.transform.TransformerException;

import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import org.springframework.remoting.soap.SoapFaultException;
import org.springframework.ws.soap.client.SoapFaultClientException;
import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.claim.RecoveryClaim;
import tavant.twms.domain.configuration.ConfigName;
import tavant.twms.domain.configuration.ConfigParamService;
import tavant.twms.domain.integration.SyncTracker;
import tavant.twms.domain.integration.SyncTrackerService;
import tavant.twms.external.ExtWarrantyPriceCheckResponse;
import tavant.twms.external.ExtWarrantyRequest;
import tavant.twms.external.IntegrationBridge;
import tavant.twms.external.PriceCheckRequest;
import tavant.twms.external.PriceCheckResponse;
import tavant.twms.integration.layer.component.ProcessExtWarrantyDebitSubmission;
import tavant.twms.integration.layer.component.ProcessExtWarrantyPriceCheck;
import tavant.twms.integration.layer.component.global.ProcessGlobalClaim;
import tavant.twms.integration.layer.component.global.ProcessGlobalPriceCheck;
import tavant.twms.integration.layer.component.global.ProcessGlobalSupplierDebitSubmission;
import tavant.twms.integration.layer.constants.IntegrationConstants;
import tavant.twms.integration.layer.transformer.ExtWarrantyPriceCheckResponseTransformer;
import tavant.twms.integration.layer.transformer.global.GlobalPriceCheckResponseTransformer;
import tavant.twms.security.SelectedBusinessUnitsHolder;

import org.springframework.ws.WebServiceMessage;
import org.springframework.ws.client.core.WebServiceMessageCallback;
import org.springframework.ws.client.core.WebServiceMessageExtractor;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.soap.saaj.SaajSoapMessage;

public class IntegrationBridgeImpl implements IntegrationBridge {

	private final Logger logger = Logger.getLogger(IntegrationBridgeImpl.class.getName());

	private final Logger priceCheckLogger = Logger.getLogger("priceCheck");
	
	private ProcessGlobalPriceCheck processGlobalPriceCheck;

	private ProcessExtWarrantyPriceCheck processExtWarrantyPriceCheck;

	private ProcessExtWarrantyDebitSubmission processExtWarrantyDebitSubmission;

	private ProcessGlobalSupplierDebitSubmission processGlobalSupplierDebitSubmission;
													
	private IntegrationPropertiesBean integrationPropertiesBean;
	
	private CustomWebServiceTemplate customWebServiceTemplate;

	private ExtWarrantyPriceCheckResponseTransformer extWarrantyPriceCheckResponseTransformer;

	private String tavantNameSpace = "xmlns=\"http://www.tavant.com/oagis\"";

	private String tavantNameSpaceExtWarranty = "xmlns=\"http://www.tavant.com/extwarranty\"";

	private String tavantNameSpaceSupplier = "xmlns=\"http://www.tavant.com/supplierdebitsubmission\"";
	
	private String webmethodsNameSpace = "xsi:schemaLocation=\"http://www.tavant.com/globalsync/pricefetchrequest PriceFetchRequest.xsd\"";
	private String tavantNameSpaceForPriceFetch = "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"";

	private StringBuffer xmlHeader = new StringBuffer(
			"<?xml version=\"1.0\" encoding=\"utf-8\"?>");

	private SyncTrackerService syncTrackerService;
	
	private ProcessGlobalClaim processGlobalClaim;
	
	private GlobalPriceCheckResponseTransformer globalPriceCheckResponseTransformer;
		
	private ConfigParamService configParamService;

	private Object processPriceCheckRequest(String xml, String url,
			String methodName, String inParam, String outParam)
			throws ServiceException, MalformedURLException, RemoteException {
		String wmNamespace = integrationPropertiesBean.getNamespace();
		Service service = new Service();
		Call call = (Call) service.createCall();
		call.setTargetEndpointAddress(new java.net.URL(url));
		call.setOperationName(new QName(wmNamespace, methodName));
		call.addParameter(new QName(wmNamespace, inParam), new QName(
				wmNamespace, inParam), ParameterMode.IN);
		call.setReturnType(new QName(wmNamespace, outParam));
		call.setUsername(integrationPropertiesBean.getWebmethodsUserName());
		call.setPassword(integrationPropertiesBean.getWebmethodsPassword());
		call.setTimeout(60000);
        return (String) call.invoke(new Object[] { xml });
	}

	public PriceCheckResponse checkPrice(Claim claim) {
		if (this.logger.isDebugEnabled()) {
			this.logger
					.debug("Received Price Check request # : Sending it to webservice at "
							+ integrationPropertiesBean.getPriceCheckURL());
		}
		PriceCheckResponse priceCheckResponse;
        String xml = null;
		boolean isGlobalPriceCheckResponse = true;
		long startTime = 0;
		try {
			startTime=Calendar.getInstance().getTimeInMillis();	
            // this is used to decide whether Response receive would of Global format type
            if (!integrationPropertiesBean.isPriceCheckEnabled()) {
                boolean isMockResponse = true;
                return globalPriceCheckResponseTransformer.transformErrorResponse(claim, isMockResponse,
                        isGlobalPriceCheckResponse);
            }
            priceCheckResponse= getPriceCheckResponse(claim);
		} catch (Exception e) {
			this.logger.error("Price Check failed.", e);
			this.logger.error(" ***** The Request XML was : ***** " + xml);
            priceCheckResponse = globalPriceCheckResponseTransformer
                        .transformErrorResponse(claim, false, isGlobalPriceCheckResponse);
		}
		if(claim.getClaimNumber()== null)
		{
		this.logger.error("Price Fetch for Work Order Number " + claim.getWorkOrderNumber()
				+ " took " + (Calendar.getInstance().getTimeInMillis()-startTime) + " ms");
		}
		else
		{
			this.logger.error("Price Fetch for Claim Number " + claim.getClaimNumber()
					+ " took " + (Calendar.getInstance().getTimeInMillis()-startTime) + " ms");
			
		}
		return priceCheckResponse;
	}

    public PriceCheckResponse checkPrice(PriceCheckRequest priceCheckRequest,Claim claim) {
        if (this.logger.isDebugEnabled()) {
            this.logger
                    .debug("Received Price Check request # : Sending it to webservice at "
                            + integrationPropertiesBean.getPriceCheckURL());
        }
        PriceCheckResponse priceCheckResponse;
        String xml = null;
        boolean isGlobalPriceCheckResponse = true;
        long startTime = 0;
        try {
            startTime=Calendar.getInstance().getTimeInMillis();
            String errorMsg = processGlobalPriceCheck.validatePriceCheckRequest(priceCheckRequest);
            boolean isError = org.springframework.util.StringUtils.hasText(errorMsg);
            // this is used to decide whether Response receive would of Global format type
            if (!integrationPropertiesBean.isPriceCheckEnabled() || isError) {
                boolean isMockResponse = true;
                //Below api is called just for the purpose of generating and logging the XML
                processGlobalPriceCheck.syncGlobalPriceCheck(priceCheckRequest,claim);
                priceCheckResponse = globalPriceCheckResponseTransformer.transformErrorResponse(
                        priceCheckRequest, isMockResponse, isGlobalPriceCheckResponse);
                if (isError) {
                    priceCheckResponse.setErrorMessage(errorMsg);
                    priceCheckResponse.setStatusCode(IntegrationConstants.ERROR);
                }
                return priceCheckResponse;
            }
            priceCheckResponse= getPriceCheckResponse(priceCheckRequest,claim);
            claim.getActiveClaimAudit().setIsPriceFetchDown(false);
            claim.getActiveClaimAudit().setPriceFetchErrorMessage(null);
        } catch (Exception e) {
            this.logger.error("Price Check failed.", e);
            this.logger.error(" ***** The Request XML was : ***** " + xml);
            claim.getActiveClaimAudit().setIsPriceFetchDown(Boolean.TRUE);
            claim.getActiveClaimAudit().setIsPriceFetchReturnZero(Boolean.FALSE);
            if(e.getMessage()!=null)
            claim.getActiveClaimAudit().setPriceFetchErrorMessage(e.getMessage().length()>3999?e.getMessage().substring(0, 3999):e.getMessage());
            priceCheckResponse = globalPriceCheckResponseTransformer
                        .transformErrorResponse(priceCheckRequest, false, isGlobalPriceCheckResponse);
        }
        this.logger.error("Price Fetch for Claim " + priceCheckRequest.getUniqueId()
                + " took " + (Calendar.getInstance().getTimeInMillis()-startTime) + " ms");
        return priceCheckResponse;
    }
    
    


	private PriceCheckResponse getPriceCheckResponse(Claim claim) throws ServiceException,
			MalformedURLException, RemoteException {
		PriceCheckResponse priceCheckResponse;
		String claimAsXml;
		String xml;
		claimAsXml = processGlobalPriceCheck.syncGlobalPriceCheck(claim);
		xml = massageXML(claimAsXml, tavantNameSpace);
		
		priceCheckLogger.info("XML representation of Price check request is.. \n" + xml);
		
		/*String response = (String) processGlobalPriceCheckRequest(xml,
				integrationPropertiesBean.getTwmsToIntegrationServerURL(),
				integrationPropertiesBean.getIntegrationServerMethod());*/
		long currentTime = System.currentTimeMillis();
		String response = (String) processPriceCheckRequest(xml,
				integrationPropertiesBean.getPriceCheckURL(),
				integrationPropertiesBean.getPriceChkMethod(),
				integrationPropertiesBean.getPriceChkInParam(),
				integrationPropertiesBean.getPriceChkOutParam());
		 priceCheckLogger.info("Price check for claim " + claim.getId() + " took (milliseconds): "
                 + (System.currentTimeMillis() - currentTime));
		// We should remove this once XSLs are implemented
		
		 priceCheckLogger.info("XML representation of Price check response is.. \n" + response);
		
		String modifiedResponse = StringUtils.replace(response,"<PriceFetchResponse>" , 
				"<PriceFetchResponse xmlns=\"http://www.tavant.com/globalsync/pricefetchresponse\">");
		priceCheckResponse = globalPriceCheckResponseTransformer.transform(modifiedResponse, claim);
		return priceCheckResponse;
	}

    //New price check API so that it can be invoked from multiple places
    private PriceCheckResponse getPriceCheckResponse(PriceCheckRequest priceCheckRequest,Claim claim) throws ServiceException,
			MalformedURLException, RemoteException {
		PriceCheckResponse priceCheckResponse;
		String requestXml;
		String xml;
		requestXml = processGlobalPriceCheck.syncGlobalPriceCheck(priceCheckRequest,claim);
		xml = priceFetchMessagexml(requestXml);
		long currentTime = System.currentTimeMillis();
		 priceCheckLogger.info("Price check for claim " + priceCheckRequest.getUniqueId() + " took (milliseconds): "
                 + (System.currentTimeMillis() - currentTime));
		 priceCheckLogger.info("#############PRICE FETCH REQUEST#################");
		 priceCheckLogger.info(xml);
		String response=sendpriceFetchRequest(xml);
		String modifiedResponse = getPriceFetchModifiedResponse(response);
		priceCheckResponse = globalPriceCheckResponseTransformer.transform(modifiedResponse, priceCheckRequest);
		return priceCheckResponse;
	}

	private String getPriceFetchModifiedResponse(String response) {
		priceCheckLogger.info("#############PRICE FETCH RESPONSE#################");
		priceCheckLogger.info(response);
		String modifiedSrc = StringUtils.replace(response,
				"<?xml version=\"1.0\" encoding=\"utf-16\"?>", " ");
		String modifiedSrc1 = StringUtils
				.replace(
						modifiedSrc,
						"<PriceFetchResponse xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns=\"http://www.tavant.com/globalsync/pricefetchresponse\">",
						"<PriceFetchResponse xmlns=\"http://www.tavant.com/globalsync/pricefetchresponse\"> ");
		String modifiedSrc2 = StringUtils.replace(modifiedSrc1,
				"</PriceFetchResponse>true", "</PriceFetchResponse>");
		return modifiedSrc2;
	}

	public boolean isExtWarrantyPriceCheckEnabled() {
		return this.configParamService
				.getBooleanValue(ConfigName.EXT_WARRANTY_PRICE_CHECK_ENABLED.getName());
	}

	public ExtWarrantyPriceCheckResponse checkPrice(
			ExtWarrantyRequest extWarrantyRequest) {

		if (!isExtWarrantyPriceCheckEnabled()) {
			return extWarrantyPriceCheckResponseTransformer
					.transformMock(extWarrantyRequest);
		}
		if (this.logger.isDebugEnabled()) {
			this.logger
					.debug("Received Ext Warranty Price Check request # : Sending it to webservice at "
							+ integrationPropertiesBean
									.getExtWarrantyPriceCheckURL());
		}

		String claimAsXml = processExtWarrantyPriceCheck
				.syncPriceCheck(extWarrantyRequest);

		String xml = massageXML(claimAsXml, tavantNameSpaceExtWarranty);

		if (this.logger.isDebugEnabled()) {
			this.logger
					.debug("XML representation of ExtWarranty Price check is.. \n"
							+ xml);
		}

		ExtWarrantyPriceCheckResponse extWarrantyPriceCheckResponse;
		try {
			String response = (String) processPriceCheckRequest(xml,
					integrationPropertiesBean.getExtWarrantyPriceCheckURL(),
					integrationPropertiesBean.getEwPriceChkMethod(),
					integrationPropertiesBean.getEwPriceChkInParam(),
					integrationPropertiesBean.getEwPriceChkOutParam());
			extWarrantyPriceCheckResponse = extWarrantyPriceCheckResponseTransformer
					.transform(response);
		} catch (Exception e) {
			this.logger.error(" EW Price Check failed.", e);
			this.logger.error(" ***** The Request XML was : ***** " + xml);
			throw new RuntimeException(e);
		}

		return extWarrantyPriceCheckResponse;
	}

	public void sendClaim(Claim claim) {
		sendClaim(claim, claim.getClaimNumber(),
				IntegrationConstants.CLAIM_SUBMISSION_UNIQUE_ID, false);
		if ((claim.isNcr() != null && claim.isNcr() == true)
				|| (claim.isNcrWith30Days() != null && claim.isNcrWith30Days() == true)) {
			sendClaim(
					claim,
					IntegrationConstants.CLAIM_NUMBER_MANUFACTURER
							+ claim.getClaimNumber(),
					IntegrationConstants.CLAIM_SUBMISSION_UNIQUE_ID_MANUFACTURER,
					true);
		}
	}

	private void sendClaim(Claim claim, String claimNumber,
			String uniqueIdName, boolean isManufactereClaim) {
		String claimAsXml = this.processGlobalClaim.syncClaim(claim,
				isManufactereClaim);
		String xml = massageXML(claimAsXml, tavantNameSpace);

		if (this.logger.isInfoEnabled()) {
			this.logger.info("XML representation of Credit Submission is.. \n"
					+ xml);
		}
		try {
			SyncTracker syncTracker = new SyncTracker(
					IntegrationConstants.SYNC_TYPE_CLAIM_SUBMISSION, xml);
			syncTracker.setUniqueIdName(uniqueIdName);
			syncTracker.setUniqueIdValue(claimNumber);
			SelectedBusinessUnitsHolder.setSelectedBusinessUnit(claim
					.getBusinessUnitInfo().getName());
			syncTrackerService.save(syncTracker);
		} catch (Exception e) {
			this.logger.error("Submission of claim # " + claimNumber
					+ " failed.", e);
			throw new RuntimeException(e);
		}

	}

	private String massageXML(String xml, String nameSpace) {
		String xsi = "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"";

		// removing any previous xsi if any
		String newXML = StringUtils.remove(xml, xsi);
		// replacing the Tavant specific name spaces with the ones required by
		// CCI
		String substitutedXML = StringUtils.replace(newXML, nameSpace,
				webmethodsNameSpace);
		StringBuffer processedXML = new StringBuffer(xmlHeader.toString());
		processedXML.append(substitutedXML);
		return processedXML.toString();
	}
	private String priceFetchMessagexml(String xml) {
		String newXML = StringUtils.replace(xml, "<PriceFetch xmlns=\"http://www.tavant.com/globalsync/pricefetchrequest\">",
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?><PriceFetch xmlns=\"http://www.tavant.com/globalsync/pricefetchrequest\" " +
				"xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" " +
				"xsi:schemaLocation=\"http://www.tavant.com/globalsync/pricefetchrequest PriceFetchRequest.xsd \">");
		StringBuffer processedXML = new StringBuffer(xmlHeader.toString());
		processedXML.append(newXML);
		return processedXML.toString();
	}

	public void submitExtWarrantyDebit(
			ExtWarrantyRequest extWarrantyDebitSubmitRequest) {
		String claimNumber = extWarrantyDebitSubmitRequest.getClaimNumber();
		this.logger
				.debug("Received request for Extended Warranty Debit Submission with Claim Number :  "
						+ claimNumber
						+ " Sending it to webservice at "
						+ integrationPropertiesBean
								.getExtWarrantyDebitSubmitURL());

		String claimAsXml = processExtWarrantyDebitSubmission
				.syncClaim(extWarrantyDebitSubmitRequest);
		String xml = massageXML(claimAsXml, tavantNameSpaceExtWarranty);

		if (this.logger.isInfoEnabled()) {
			this.logger.info("XML representation of Debit Submission is.. \n"
					+ xml);
		}
		try {
			SyncTracker syncTracker = new SyncTracker("ExtWarrantyDebitSubmit",
					xml);
			syncTracker.setUniqueIdName("Serial Number");
			syncTracker.setUniqueIdValue(extWarrantyDebitSubmitRequest.getSerialNumber());
			syncTrackerService.save(syncTracker);
		} catch (Exception e) {
			this.logger.error("Submission of claim # " + claimNumber
					+ " failed.", e);
			throw new RuntimeException(e);
		}

	}

	public void sendRecoveryClaim(RecoveryClaim recoveryClaim) {
		String claimNumber = recoveryClaim.getClaim().getClaimNumber();
		if(logger.isInfoEnabled()){
			this.logger
					.info("Received request for Supplier Debit Submission with Claim Number :  "
							+ claimNumber
							+ " Sending it to webservice at "
							+ integrationPropertiesBean.getSupplierDebitSubmitURL());
		}

		String claimAsXml;
        claimAsXml = processGlobalSupplierDebitSubmission.syncGlobalSupplierDebit(recoveryClaim);
        claimAsXml = massageXML(claimAsXml, tavantNameSpaceSupplier);

        if (this.logger.isInfoEnabled()) {
            this.logger.info("XML representation of Debit Submission is.. \n"
                    + claimAsXml);
        }
        try {
            SyncTracker syncTracker = new SyncTracker("SupplierDebitSubmit",
                    claimAsXml);
            syncTracker.setUniqueIdName("Claim Number|Recovery Id");
            String uniqueIdValue = claimNumber + "|" +recoveryClaim.getId();
            syncTracker.setUniqueIdValue(uniqueIdValue);
            syncTrackerService.save(syncTracker);
        } catch (Exception e) {
            this.logger.error(
                    "Submission of supplier Debit with Claim number # "
                            + claimNumber + " failed.", e);
            throw new RuntimeException(e);
        }
	}
	/**
	 * This will take the pricefecth request xml and send it to EPO system and get prices for items
	 * @param xmlData
	 * @return
	 */
    public String sendpriceFetchRequest(final String xmlData) {
        return customWebServiceTemplate.sendAndReceive(integrationPropertiesBean.getPriceCheckURL(),
                new WebServiceMessageCallback() {

                    public void doWithMessage(WebServiceMessage message) throws IOException, TransformerException {
                    	SaajSoapMessage saajSoapMessage=(SaajSoapMessage) message;
                    	saajSoapMessage.setSoapAction(integrationPropertiesBean.getPriceFetchAciton());
                        SOAPMessage soapMessage = saajSoapMessage.getSaajMessage();
                        try {
                            SOAPEnvelope envelope = addNSToMessage(soapMessage);
                            addInputToSOAPMessage(envelope, xmlData);
                            soapMessage.saveChanges();
                            
                        } catch (SOAPException ex) {
                            logger.error("Price fetch soap exception -- " + ex.fillInStackTrace());
                            throw new TransformerException("Exception while transforming bod xml to soap message", ex);
                        }

                    }
                },
                new WebServiceMessageExtractor<String>() {

                    public String extractData(WebServiceMessage message) throws IOException, TransformerException {
                        try {
                            SOAPMessage soapMessage = ((SaajSoapMessage) message).getSaajMessage();
                            return soapMessage.getSOAPPart().getEnvelope().getBody().getTextContent();
                        } catch (Exception ex) {
                            logger.error("WebServiceMessageExtractor exception -- " + ex.fillInStackTrace());
                            throw new TransformerException("Exception while transforming/extracting response xml from Web-Methods", ex);
                        }
                    }
                }
        );
    }
    public SOAPEnvelope addNSToMessage(SOAPMessage soapMessage) throws SOAPException, IOException {
        SOAPEnvelope envelope = soapMessage.getSOAPPart().getEnvelope();
        envelope.addNamespaceDeclaration("SOAP-ENV", "http://schemas.xmlsoap.org/soap/envelope/");
        envelope.addNamespaceDeclaration("tem", "http://tempuri.org/");
        return envelope;
    }
    public void addInputToSOAPMessage(SOAPEnvelope soapEnvelope, String xmlData) throws SOAPException {
        SOAPElement processElement = soapEnvelope.addChildElement("GetPriceFetch", "tem");
        SOAPElement inDataElement = processElement.addChildElement("priceFetchRequestXML", "tem");
        inDataElement.setTextContent(xmlData);
        soapEnvelope.getBody().addChildElement(processElement); 
        logger.trace(soapEnvelope.getHeader());
        logger.trace(soapEnvelope.getBody().getTextContent());
    }
  
	  
	public void setProcessExtWarrantyPriceCheck(ProcessExtWarrantyPriceCheck processExtWarrantyPriceCheck) {
		this.processExtWarrantyPriceCheck = processExtWarrantyPriceCheck;
	}

	public void setProcessExtWarrantyDebitSubmission(ProcessExtWarrantyDebitSubmission processExtWarrantyDebitSubmission) {
		this.processExtWarrantyDebitSubmission = processExtWarrantyDebitSubmission;
	}

	public void setExtWarrantyPriceCheckResponseTransformer(ExtWarrantyPriceCheckResponseTransformer extWarrantyPriceCheckResponseTransformer) {
		this.extWarrantyPriceCheckResponseTransformer = extWarrantyPriceCheckResponseTransformer;
	}

	public void setIntegrationPropertiesBean(IntegrationPropertiesBean integrationPropertiesBean) {
		this.integrationPropertiesBean = integrationPropertiesBean;
	}

	public void setSyncTrackerService(SyncTrackerService syncTrackerService) {
		this.syncTrackerService = syncTrackerService;
	}
	
	public void setProcessGlobalClaim(ProcessGlobalClaim processGlobalClaim) {
		this.processGlobalClaim = processGlobalClaim;
	}

	public void setProcessGlobalSupplierDebitSubmission(ProcessGlobalSupplierDebitSubmission processGlobalSupplierDebitSubmission) {
		this.processGlobalSupplierDebitSubmission = processGlobalSupplierDebitSubmission;
	}

	public void setGlobalPriceCheckResponseTransformer(GlobalPriceCheckResponseTransformer globalPriceCheckResponseTransformer) {
		this.globalPriceCheckResponseTransformer = globalPriceCheckResponseTransformer;
	}
	
	public void setProcessGlobalPriceCheck(ProcessGlobalPriceCheck processGlobalPriceCheck) {
		this.processGlobalPriceCheck = processGlobalPriceCheck;
	}

	public void setConfigParamService(ConfigParamService configParamService) {
		this.configParamService = configParamService;
	}

    public CustomWebServiceTemplate getCustomWebServiceTemplate() {
        return customWebServiceTemplate;
    }

    public void setCustomWebServiceTemplate(CustomWebServiceTemplate customWebServiceTemplate) {
        this.customWebServiceTemplate = customWebServiceTemplate;
    }
}

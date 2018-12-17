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
 * Created Feb 28, 2007 8:10:45 PM
 * @author kapil.pandit
 */

package tavant.twms.integration.layer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import org.acegisecurity.context.SecurityContextHolder;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlException;

import tavant.globalsync.customersync.ApplicationAreaTypeDTO;
import tavant.globalsync.customersync.CustomerSyncRequestDocumentDTO;
import tavant.globalsync.customersync.CustomerTypeDTO;
import tavant.globalsync.exchangeratesync.ExchangeRateDocumentDTO;
import tavant.globalsync.extendedwarrantynotification.ExtWarrantyNotificationDocumentDTO;
import tavant.globalsync.foc.FocDocument;
import tavant.globalsync.installbasesync.InstallBaseSyncDocumentDTO;
import tavant.globalsync.itemsync.SyncItemMasterDocumentDTO;
import tavant.globalsync.itemsync.SyncItemMasterDocumentDTO.SyncItemMaster;
import tavant.globalsync.supplierdebitnotification.SupplierDebitNotificationDocumentDTO;
import tavant.globalsync.usersync.UserSyncRequestDocumentDTO;
import tavant.globalsync.warrantyclaimcreditnotification.SyncInvoiceDocumentDTO;
import tavant.oagis.OEMXREFDocumentDTO.OEMXREF;
import tavant.twms.domain.bookings.BookingsService;
import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.claim.ClaimService;
import tavant.twms.domain.claim.ClaimState;
import tavant.twms.domain.common.BookingsReport;
import tavant.twms.domain.integration.SyncTracker;
import tavant.twms.domain.integration.SyncTrackerService;
import tavant.twms.domain.orgmodel.OrgService;
import tavant.twms.domain.orgmodel.Role;
import tavant.twms.domain.orgmodel.ServiceProvider;
import tavant.twms.domain.orgmodel.User;
import tavant.twms.integration.layer.component.ProcessExtWarrantyDebitNotification;
import tavant.twms.integration.layer.component.SyncOEMXReference;
import tavant.twms.integration.layer.component.SyncResponse;
import tavant.twms.integration.layer.component.global.FailureCodeSync;
import tavant.twms.integration.layer.component.global.FocSync;
import tavant.twms.integration.layer.component.global.GlobalBookingsInboundSync;
import tavant.twms.integration.layer.component.global.GlobalBookingsOutboundSync;
import tavant.twms.integration.layer.component.global.GlobalCustomerSync;
import tavant.twms.integration.layer.component.global.GlobalDealerBatchClaimSync;
import tavant.twms.integration.layer.component.global.GlobalInstallBaseSync;
import tavant.twms.integration.layer.component.global.GlobalItemSync;
import tavant.twms.integration.layer.component.global.GlobalSyncUser;
import tavant.twms.integration.layer.component.global.GlobalTechnicianSync;
import tavant.twms.integration.layer.component.global.ProcessGlobalCreditNotification;
import tavant.twms.integration.layer.component.global.ProcessGlobalExchangeRate;
import tavant.twms.integration.layer.component.global.ProcessGlobalExtWarrantyPurchaseNotification;
import tavant.twms.integration.layer.component.global.ProcessGlobalSupplierDebitNotification;
import tavant.twms.integration.layer.component.global.ProcessItalyClaimNotification;
import tavant.twms.integration.layer.constants.IntegrationConstants;
import tavant.twms.integration.layer.transformer.ExtWarrantyDebitNotificationTransformer;
import tavant.twms.integration.layer.transformer.SyncOEMXRefTransformer;
import tavant.twms.integration.layer.transformer.global.FocClaimTransformer;
import tavant.twms.security.SecurityHelper;
import tavant.twms.security.SelectedBusinessUnitsHolder;

import com.nmhg.batchclaim_response.MTClaimSubmissionResponseDocument;
import com.nmhg.itemsynch_response.ItemSyncResponse;
import com.nmhg.itemsynch_response.ItemSyncResponse.ItemMasterResponse.Status.Exceptions;
import com.nmhg.itemsynch_response.MTItemSyncResponseDocument;
import com.nmhg.syncitalyqanotification.MTSyncItalyQANotificationSLMSDocument;
import com.tavant.globalsync.bookingsync.BookingsDocument;
import com.tavant.globalsync.failurecodesresponse.FailureCodesResponseSyncDocument;
import com.tavant.globalsync.failurecodessync.FailureCodesSyncDocument;
import com.tavant.globalsync.failurecodessync.FailureCodesSyncDocument.FailureCodesSync;
import com.tavant.globalsync.techniciansync.TechnicianSyncRequestDocument;

public class IntegrationServiceImpl implements IntegrationService {

	private static Logger logger = Logger.getLogger(IntegrationServiceImpl.class.getName());

	private GlobalSyncUser globalSyncUser;
	private SyncOEMXReference syncOEMXReference;
	private ProcessExtWarrantyDebitNotification processExtWarrantyDebitNotification;
	private ProcessGlobalCreditNotification processGlobalCreditNotification;
	private ProcessGlobalSupplierDebitNotification processGlobalSupplierDebitNotification;
	private ExtWarrantyDebitNotificationTransformer extWarrantyDebitNotificationTransformer;
	private SyncOEMXRefTransformer oemXRefTransformer;
	private SecurityHelper securityHelper;
	private ProcessGlobalExtWarrantyPurchaseNotification processGlobalExtWarrantyPurchaseNotification;
	private ProcessGlobalExchangeRate processGlobalExchangeRate;
	private GlobalItemSync globalItemSync;
	private GlobalInstallBaseSync globalInstallBaseSync;
	private FailureCodeSync failureCodeSync;
    private GlobalCustomerSync globalCustomerSync;
	private FocClaimTransformer focClaimTransformer;
	private FocSync focSync;
	private OrgService orgService;
	private ClaimService claimService;
	private SyncTrackerService syncTrackerService;
	private ProcessItalyClaimNotification processItalyClaimNotification;
	private GlobalTechnicianSync globalTechnicianSync;
	private GlobalBookingsInboundSync globalBookingsInboundSync;
	private GlobalBookingsOutboundSync globalBookingsOutboundSync;
	private DealerIntegrationService dealerIntegrationService;
	private GlobalDealerBatchClaimSync globalDealerBatchClaimSync;

	private BookingsService bookingsService;

	public List<SyncResponse> syncInstallBase(String bod) {
		securityHelper.populateIntegrationUser();
        InstallBaseSyncDocumentDTO installBaseSyncDocumentDTO=null;
		try {
            installBaseSyncDocumentDTO=InstallBaseSyncDocumentDTO.Factory.parse(bod);
		} catch (XmlException e) {
			logger.error(" Failed to sync Sync InstallBase:", e);
			return createErrorResponse(e," Error while transforming the InstallBase sync");
		}
        return globalInstallBaseSync.sync(installBaseSyncDocumentDTO);
	}
	
	public List<SyncResponse> syncUnitBooking(String bod) {
		securityHelper.populateIntegrationUser();
		BookingsDocument bookingsDocument=null;
		try {
			bookingsDocument=BookingsDocument.Factory.parse(bod);
		} catch (XmlException e) {
			logger.error(" Failed to Sync User:", e);
		}
		List<SyncResponse> responses = globalBookingsInboundSync.syncUnitBooking(bookingsDocument);
		return responses;
	}
	

	
	public void saveAndsendUnitTransaction() {
		securityHelper.populateIntegrationUser();
		java.sql.Timestamp lastUpdatedTimeForinvTrans=bookingsService.findLastReportingTimeForInvTransactions();
		java.sql.Timestamp lastUpdatedTimeForWarranties=bookingsService.findLastReportingTimeForWarranties();
		if(lastUpdatedTimeForinvTrans==null){
			bookingsService.createDummyReportObjectForInventory();
			lastUpdatedTimeForinvTrans=bookingsService.findLastReportingTimeForInvTransactions();
		}
		if(lastUpdatedTimeForWarranties==null){
		bookingsService.createDummyReportObjectForWarranty();
		lastUpdatedTimeForWarranties=lastUpdatedTimeForinvTrans=bookingsService.findLastReportingTimeForWarranties();
		}
		globalBookingsOutboundSync.saveAndsendUnitTransaction(lastUpdatedTimeForinvTrans,lastUpdatedTimeForWarranties);
		
	}

	public Object syncItem(String bod) {
		securityHelper.populateIntegrationUser();
		SyncItemMaster items;
		try {
            items = SyncItemMasterDocumentDTO.Factory.parse(bod).getSyncItemMaster();
		}catch (XmlException e) {
			logger.error(" Failed to sync global Sync Item:", e);
			return createErrorResponseForGlobalItemSync(e," Error while transforming the global Item sync");
		}
        return globalItemSync.sync(items);
	}

	public Object syncBatchClaim(String bod) {
		securityHelper.populateIntegrationUser();
		MTClaimSubmissionResponseDocument claimSubmissionRespDocDTO = globalDealerBatchClaimSync
				.processBatchClaim(bod);
		return claimSubmissionRespDocDTO;
	}
	
	public Object syncOEMXReference(String bod) {
		securityHelper.populateIntegrationUser();
		OEMXREF oemXRef = null;
		try {
			oemXRef = oemXRefTransformer.transform(bod);
		} catch (RuntimeException e) {
			logger.error(" Failed to sync Sync OEMXReference:", e);
			return createErrorResponse(e," Error while transforming the OEMXReference sync");
		}
		List<SyncResponse> responses = syncOEMXReference.sync(oemXRef);
		return responses;
	}
	
	public List<SyncResponse> syncCustomer(String bod) {
		securityHelper.populateIntegrationUser();
		List<CustomerTypeDTO> customers = null;
		ApplicationAreaTypeDTO applicationArea  = null;
		try {
			customers = Arrays.asList(CustomerSyncRequestDocumentDTO.Factory.parse(bod).getCustomerSyncRequest().getDataArea().getCustomer());
			applicationArea = CustomerSyncRequestDocumentDTO.Factory.parse(bod).getCustomerSyncRequest().getApplicationArea();
		} catch (XmlException e) {
			logger.error(" Failed to sync Sync Customer:", e);
			return createErrorResponse(e," Error while transforming the Customer sync");
		}
        return globalCustomerSync.sync(customers,applicationArea);
	}
	
	public List<SyncResponse> syncUser(String bod) {
		securityHelper.populateIntegrationUser();
		List<tavant.globalsync.usersync.UserTypeDTO> users = null;
		try {
            users = Arrays.asList(UserSyncRequestDocumentDTO.Factory.parse(bod).getUserSyncRequest().getDataArea().getUser());
		} catch (XmlException e) {
			logger.error(" Failed to Sync User:", e);
			return createErrorResponse(e," Error while transforming the User sync");
		}
		List<SyncResponse> responses = globalSyncUser.sync(users);
		return responses;
	}
	
	public List<SyncResponse> syncTechnicians(String bod) {
		securityHelper.populateIntegrationUser();
		TechnicianSyncRequestDocument technicianSyncRequestDoc=null;
		try {
			 technicianSyncRequestDoc=TechnicianSyncRequestDocument.Factory.parse(bod);
		} catch (XmlException e) {
			logger.error(" Failed to Sync User:", e);
		}
		//List<SyncResponse> responses = globalSyncUser.syncTechnicianUser(technicianSyncRequestDoc);
		List<SyncResponse> responses = globalTechnicianSync.syncTechnicianUser(technicianSyncRequestDoc);
		return responses;
	}


	
	public Object syncExtWarrantyDebitNotification(String bod) {
		securityHelper.populateIntegrationUser();
		tavant.extwarranty.InvoiceTypeDTO invoice = null;
		try {
			invoice = extWarrantyDebitNotificationTransformer.transform(bod);
		} catch (XmlException e) {
			logger.error(" Failed to sync ExtendedWarranty Debit Notification:", e);
			return createErrorResponse(e," Error while tranforming the ExtendedWarranty Debit Notification");
		}
		List<tavant.extwarranty.InvoiceTypeDTO > invoices = new ArrayList<tavant.extwarranty.InvoiceTypeDTO >();
		invoices.add(invoice);
		return processExtWarrantyDebitNotification.sync(invoices);
	}

	
	public Object syncSupplierDebitNotification(String bod){
		securityHelper.populateIntegrationUser();
		tavant.globalsync.supplierdebitnotification.InvoiceTypeDTO invoice = null;
		try{
            invoice = SupplierDebitNotificationDocumentDTO.Factory.parse(bod).getSupplierDebitNotification().getDataArea().getInvoice();
		}catch (XmlException e) {
			logger.error(" Failed to sync Global Supplier Debit Notification:", e);
			return createErrorResponse(e," Error while tranforming the Global Supplier Debit Notification");
		}
		List<tavant.globalsync.supplierdebitnotification.InvoiceTypeDTO> invoices = new ArrayList<tavant.globalsync.supplierdebitnotification.InvoiceTypeDTO>();
		invoices.add(invoice);
		return processGlobalSupplierDebitNotification.sync(invoices);
	}
	private List<SyncResponse> createErrorResponse(
			 Exception e,String syncTypeMessage) {
		List<SyncResponse> responses = new ArrayList<SyncResponse>();
		SyncResponse response;
		response = new SyncResponse();
		response.setSuccessful(false);
		response.setException(new StringBuilder().append(
				syncTypeMessage).append(e.getMessage()).toString());
		response.setErrorCode(SyncResponse.ERROR_CODE_VALIDATION_ERROR);
		response.setErrorType(SyncResponse.ERROR_CODE_VALIDATION_ERROR);
		responses.add(response);
		
		return responses;
	}

	private MTItemSyncResponseDocument createErrorResponseForGlobalItemSync(
			 Exception e,String syncTypeMessage) {
		MTItemSyncResponseDocument itemResponseDoc = MTItemSyncResponseDocument.Factory.newInstance();
		ItemSyncResponse itemSyncResponse = itemResponseDoc.addNewMTItemSyncResponse();
		itemSyncResponse.addNewStatus();
		itemSyncResponse.getStatus().setCode(ItemSyncResponse.Status.Code.Enum.forString("FAILURE"));
		Exceptions exceptions =itemSyncResponse.addNewItemMasterResponse().addNewStatus()
				.addNewExceptions();
		/*for (Map.Entry<String, String> errorMessage : errorMessageCodesMap
				.entrySet()) {
			Error error = exceptions.addNewError();
			error.setErrorCode(errorMessage.getKey());
			error.setErrorMessage(errorMessage.getValue());
		}*/
		return itemResponseDoc;
	}

	public Object syncExtWarrantyPurchaseNotification(String bod){
		securityHelper.populateIntegrationUser();		
        ExtWarrantyNotificationDocumentDTO extnWntyNotificationDocumentDTO = null;
        try{
            extnWntyNotificationDocumentDTO = ExtWarrantyNotificationDocumentDTO.Factory.parse(bod);
		}catch(XmlException e){
			logger.error(" Failed to sync ExtendedWarranty Purchase Notification:", e);
			return createErrorResponse(e," Error while tranforming the ExtendedWarranty Purchase Notification");		
		}
		return processGlobalExtWarrantyPurchaseNotification.sync(extnWntyNotificationDocumentDTO);
	}
	
	public List<SyncResponse> syncCurrencyExchangeRate(String bod) {
		securityHelper.populateIntegrationUser();
		ExchangeRateDocumentDTO exchangeRateDocumentDTO=null;
		try { 
			exchangeRateDocumentDTO = ExchangeRateDocumentDTO.Factory.parse(bod);
		} catch(XmlException e) {
			logger.error("Failed to sync Exchange Rate Request: ", e);
			return createErrorResponse(e, "Error while transforming the Exchange Rate Request");
		}
		List<SyncResponse> responses = processGlobalExchangeRate.sync(exchangeRateDocumentDTO);
		return responses;
	}
	
	
	/**
	 * Expected xml format is foc.xsd
	 * Namespace must
	 */
	
	public Object fetchFocOrderDetails(String orderNo) {
		securityHelper.populateFocUser();
		FocDocument foc = this.focClaimTransformer.transform(orderNo);
		if(foc==null){
			return FocDocument.Factory.newInstance();
		}
		FocDocument db = this.focSync.fetchFocOrder(foc.getFoc().getDataArea().getOrderNo());
		return db;
	}
	

	public Object postFocOrderDetails(String orderXml) {
		securityHelper.populateFocUser();
		orderXml =  StringUtils.replace(orderXml,"<foc>" , 
		"<foc xmlns=\"http://www.tavant.com/globalsync/foc\">");		
		FocDocument focDocument = this.focClaimTransformer.transform(orderXml);
		SyncResponse syncResponse = focSync.storeOrderDetails(focDocument);
		List<SyncResponse > list = new ArrayList<SyncResponse>();
		list.add(syncResponse);		
		return list;
	}	

	public Object syncFocClaimDetails (String claimXml) {
		securityHelper.populateFocUser();
		claimXml =  StringUtils.replace(claimXml,"<foc>" , 
		"<foc xmlns=\"http://www.tavant.com/globalsync/foc\">");
		FocDocument foc = null;
		try {
			foc = this.focClaimTransformer.transform(claimXml);
		} catch (RuntimeException e1) {
			return createErrorResponse(e1, "");
		}		
		String serviceProviderNo = "";
		if (foc == null) {
			return createErrorResponse(new RuntimeException(),
					"Document is null");
		} else {
			serviceProviderNo = foc.getFoc().getDataArea()
					.getServiceProviderNo();
		}
		
		if(StringUtils.isEmpty(serviceProviderNo)){
			return createErrorResponse(new RuntimeException(),
			"Service Provider No cannot be null");
		}
		
		ServiceProvider serviceProvider = this.orgService.findServiceProviderByNumber(serviceProviderNo);
		if(serviceProvider == null){
			return createErrorResponse(new RuntimeException(),
			"No service provider in system with no : "+serviceProviderNo);

		}
		
		User user=null;
		if (serviceProvider.isThirdParty()) {
			// case - When a Third Party FOC claim is created assigned to a Third Party Login
			List<User> thirdPartyUsers = this.orgService.findUsersWithRoleInServiceProviderOrganization(
					serviceProviderNo, Role.DEALER);
			if(thirdPartyUsers != null && !thirdPartyUsers.isEmpty() && thirdPartyUsers.size() > 0){
				for (User element : thirdPartyUsers) {
					Set<Role> roles = element.getRoles();
					for(Role role : roles){
						if ((Role.VIEW_FOC_CLAIMS).equals(role.getName())) {
							user = element;
							break;
						}						
					}					
				}
				if (user == null) {
					user = thirdPartyUsers.iterator().next();
				}
			}else{
				//case - When a Third Party FOC claim is created with Anonymous ThirdParty
				Set<User> users = this.orgService
						.findUsersBelongingToRole(Role.VIEW_FOC_INBOX);
				if (users == null || users.size() == 0) {
					Exception e = new RuntimeException(
							"NO FOC PROCESSOR ROLE DEFINED");
					return createErrorResponse(e, "");
				} else {
					user = users.iterator().next();
				}
			}
		} else {
			//case - When a Branch/Dealer FOC claim is created assigned to a Dealer Login
			List<User> users = this.orgService
					.findUsersWithRoleInServiceProviderOrganization(
							serviceProviderNo, Role.DEALER);
			if (users == null || users.size() == 0) {
				Exception e = new RuntimeException("NO DEALER IN BRANCH");
				return createErrorResponse(e, "");
			} else {
				// preference is to choose a dealer with viewfoc flag
				for (User element : users) {
					Set<Role> roles = element.getRoles();
					for(Role role : roles){
						if ((Role.VIEW_FOC_CLAIMS).equals(role.getName())) {
							user = element;
							break;
						}						
					}					
				}
				if (user == null) {
					user = users.iterator().next();
				}
			}

		}
		new tavant.twms.security.authz.infra.SecurityHelper().populateTestUserCredentials(user);
		SyncResponse syncResponse = focSync.storeClaim(foc);
		List<SyncResponse > list = new ArrayList<SyncResponse>();
		list.add(syncResponse);		
		return list;		
	}


	public void setOemXRefTransformer(SyncOEMXRefTransformer oemXRefTransformer) {
		this.oemXRefTransformer = oemXRefTransformer;
	}

	public void setSyncOEMXReference(SyncOEMXReference syncOEMXReference) {
		this.syncOEMXReference = syncOEMXReference;
	}

	public void setProcessExtWarrantyDebitNotification(
			ProcessExtWarrantyDebitNotification processExtWarrantyDebitNotification) {
		this.processExtWarrantyDebitNotification = processExtWarrantyDebitNotification;
	}

	public void setExtWarrantyDebitNotificationTransformer(
			ExtWarrantyDebitNotificationTransformer extWarrantyDebitNotificationTransformer) {
		this.extWarrantyDebitNotificationTransformer = extWarrantyDebitNotificationTransformer;
	}

	public void setSecurityHelper(SecurityHelper securityHelper) {
		this.securityHelper = securityHelper;
	}
	
	public List<SyncResponse> syncWarrantyClaimCreditNotification(String bod) {
		securityHelper.populateIntegrationUser();
		tavant.globalsync.warrantyclaimcreditnotification.InvoiceTypeDTO invoice = null;
		tavant.globalsync.warrantyclaimcreditnotification.ApplicationAreaTypeDTO applicationArea = null;
		try {
            invoice = SyncInvoiceDocumentDTO.Factory.parse(bod).getSyncInvoice().getDataArea().getInvoice();
            applicationArea = SyncInvoiceDocumentDTO.Factory.parse(bod).getSyncInvoice().getApplicationArea();
		} catch (XmlException e) {
			logger.error(" Failed to sync credit notification:", e);
			return createErrorResponse(e," Error while tranforming the Credit Notification");
		}
		List<tavant.globalsync.warrantyclaimcreditnotification.InvoiceTypeDTO> invoices = new ArrayList<tavant.globalsync.warrantyclaimcreditnotification.InvoiceTypeDTO>();
		invoices.add(invoice);
		return processGlobalCreditNotification.sync(invoices , applicationArea);
	}
	
	public void setProcessGlobalCreditNotification(
			ProcessGlobalCreditNotification processGlobalCreditNotification) {
		this.processGlobalCreditNotification = processGlobalCreditNotification;
	}	

	public void setGlobalSyncUser(
			GlobalSyncUser globalSyncUser) {
		this.globalSyncUser = globalSyncUser;
	}

	public void setGlobalItemSync(GlobalItemSync globalItemSync){
		this.globalItemSync = globalItemSync;
	}
	
	public void setGlobalInstallBaseSync(GlobalInstallBaseSync globalInstallBaseSync) {
		this.globalInstallBaseSync = globalInstallBaseSync;
	}

	public void setProcessGlobalSupplierDebitNotification(
			ProcessGlobalSupplierDebitNotification processGlobalSupplierDebitNotification) {
		this.processGlobalSupplierDebitNotification = processGlobalSupplierDebitNotification;
	}

	public void setFocClaimTransformer(FocClaimTransformer focClaimTransformer) {
		this.focClaimTransformer = focClaimTransformer;
	}
	public void setGlobalCustomerSync(GlobalCustomerSync globalCustomerSync) {
		this.globalCustomerSync = globalCustomerSync;
	}
		
	public void setProcessGlobalExtWarrantyPurchaseNotification(
			ProcessGlobalExtWarrantyPurchaseNotification processGlobalExtWarrantyPurchaseNotification) {
		this.processGlobalExtWarrantyPurchaseNotification = processGlobalExtWarrantyPurchaseNotification;
	}

	public void setFocSync(FocSync focSync) {
		this.focSync = focSync;
	}

	public ProcessGlobalExchangeRate getProcessGlobalExchangeRate() {
		return processGlobalExchangeRate;
	}

	public void setProcessGlobalExchangeRate(
			ProcessGlobalExchangeRate processGlobalExchangeRate) {
		this.processGlobalExchangeRate = processGlobalExchangeRate;
	}

	public void setOrgService(OrgService orgService) {
		this.orgService = orgService;
	}

	public ProcessItalyClaimNotification getProcessItalyClaimNotification() {
		return processItalyClaimNotification;
	}

	public void setProcessItalyClaimNotification(
			ProcessItalyClaimNotification processItalyClaimNotification) {
		this.processItalyClaimNotification = processItalyClaimNotification;
	}

	public String updateClaimStatePostSubmission(String bod) {
		securityHelper.populateIntegrationUser();
		try {
			StringTokenizer strToken = new StringTokenizer(bod,":");
			String clmNumber = null;
			while(strToken.hasMoreTokens()){
				clmNumber = strToken.nextToken();
			}
			Claim claim = claimService.findClaimByNumber(clmNumber);
			if(claim != null && ClaimState.PENDING_PAYMENT_SUBMISSION.equals(claim.getState())){
				claim.setState(ClaimState.PENDING_PAYMENT_RESPONSE);
				claimService.updateClaim(claim);
			}
			return "COMPLETED";
		} catch (RuntimeException e) {
			logger.error(" Failed to update claim state post submission:", e);
			return "FAILED";
		}
	}

	public void setClaimService(ClaimService claimService) {
		this.claimService = claimService;
	}

    public Object syncFailureCode(String bod) {
        securityHelper.populateIntegrationUser();
        FailureCodesSync failureCodesMasterData = null;
		try {
            failureCodesMasterData = FailureCodesSyncDocument.Factory.parse(bod).getFailureCodesSync();
		} catch (Exception e) {
			logger.error(" Failed to Sync Failure Codes:", e);
			return createErrorResponseForFailureCodeSync(e," Error while transforming the Failure Code sync");
		}
        return failureCodeSync.sync(failureCodesMasterData);
	}


    private FailureCodesResponseSyncDocument createErrorResponseForFailureCodeSync(
			 Exception e,String syncTypeMessage) {
		FailureCodesResponseSyncDocument failureCodesResponseSyncDocument = FailureCodesResponseSyncDocument.Factory.newInstance();
		failureCodesResponseSyncDocument.addNewFailureCodesResponseSync();
		FailureCodesResponseSyncDocument.FailureCodesResponseSync failureCodesResponseSync = failureCodesResponseSyncDocument.getFailureCodesResponseSync();
		failureCodesResponseSync.addNewStatus();
		failureCodesResponseSync.getStatus().setCode(com.tavant.globalsync.failurecodesresponse.CodeDocument.Code.Enum.forString("ERROR"));
		failureCodesResponseSync.getStatus().setErrorMessage(new StringBuilder().append(
				syncTypeMessage).append(e.getMessage()).toString());
		return failureCodesResponseSyncDocument;
	}


    public void setFailureCodeSync(FailureCodeSync failureCodeSync) {
        this.failureCodeSync = failureCodeSync;
    }
    
    public Map<Map<Long, MTSyncItalyQANotificationSLMSDocument>, java.sql.Timestamp> getClaimsForItalyClaimNotification(java.sql.Timestamp lastSchedulerTime){
    	populateDummyAuthentication();
    	List<Claim> claims = claimService.getClaimsForItalyClaimNotification(lastSchedulerTime);
    	Calendar cal = Calendar.getInstance(); 
    	java.sql.Timestamp currentFireTime = new java.sql.Timestamp(cal.getTimeInMillis());
    	Map<Long,MTSyncItalyQANotificationSLMSDocument> xmls = new HashMap<Long,MTSyncItalyQANotificationSLMSDocument>();
    	Map<Map<Long, MTSyncItalyQANotificationSLMSDocument>, java.sql.Timestamp> italyClaimSyncXmlsWithDate=new HashMap<Map<Long, MTSyncItalyQANotificationSLMSDocument>, java.sql.Timestamp>();
    	List<String> failedClaimNumbers=new ArrayList<String>();
    	for(Claim claim : claims){
    		try{
				if (claim.getBusinessUnitInfo() != null
						&& claim.getBusinessUnitInfo().getName() != null
						&& StringUtils.isNotBlank(claim.getBusinessUnitInfo()
								.getName())
						&& IntegrationConstants.BUSINESS_UNIT_EMEA
								.equalsIgnoreCase(claim.getBusinessUnitInfo()
										.getName())){
    		if(claim.getClaimNumber()!=null){
    			logger.error("Italy Claim Process has been started for Claim  -------"+claim.getClaimNumber());
    		MTSyncItalyQANotificationSLMSDocument xml = (MTSyncItalyQANotificationSLMSDocument)processItalyClaimNotification.syncNotificationClaimDetails(claim);
    		SyncTracker syncTracker = new SyncTracker(IntegrationConstants.ITALY_CLAIM_SYNC_TYPE,xml.toString());
			syncTracker.setUniqueIdName("Claim Number");
			syncTracker.setUniqueIdValue(claim.getClaimNumber());
			/*if(claim.getBusinessUnitInfo()!=null && claim.getBusinessUnitInfo().getName()!=null){
				syncTracker.setBusinessUnitInfo(claim.getBusinessUnitInfo());
			}else{
				BusinessUnitInfo bu = new BusinessUnitInfo();
				bu.setName("EMEA");
				syncTracker.setBusinessUnitInfo(bu);
			}*/
			
            SelectedBusinessUnitsHolder.setSelectedBusinessUnit(claim.getBusinessUnitInfo().getName());
			syncTrackerService.save(syncTracker);
			xmls.put(syncTracker.getId(),xml);
			logger.error("Italy Claim XML has been created for Claim -------"+claim.getClaimNumber());
    		}
				}
    		}catch(Exception e){
    			failedClaimNumbers.add(claim.getClaimNumber());
				logger.error("Italy Claim notification has been failed to send the claim number "
						+ claim.getClaimNumber()
						+ " reason is .........."
						+ e.getStackTrace() + " Message..." + e.getMessage());
        		
        	}
    	}
    	
    	if(failedClaimNumbers.size()>0)
    	logger.error("number of claims which are failed  -------"+failedClaimNumbers.size());
    	italyClaimSyncXmlsWithDate.put(xmls, currentFireTime);
    	return italyClaimSyncXmlsWithDate;
    }

    private void populateDummyAuthentication() {
		SecurityHelper securityHelper = new SecurityHelper();
		if (SecurityContextHolder.getContext().getAuthentication() == null) {
			securityHelper.populateSystemUser();
		}
	}

	public SyncTrackerService getSyncTrackerService() {
		return syncTrackerService;
	}

	public void setSyncTrackerService(SyncTrackerService syncTrackerService) {
		this.syncTrackerService = syncTrackerService;
	}
	
	public int updateCreditSubmissionDate(String claimNumber) {
			securityHelper.populateIntegrationUser();
			return this.claimService.updateCreditSubmissionDate(claimNumber);
		}

	public void updateCreditForAcceptedAndDeniedClaims(String buName
			) {
		securityHelper.populateIntegrationUser();
		SelectedBusinessUnitsHolder.setSelectedBusinessUnit(buName);
		claimService.updateCreditDateOfAcceptedAndDeniedClaims();
	}

	public GlobalTechnicianSync getGlobalTechnicianSync() {
		return globalTechnicianSync;
	}

	public void setGlobalTechnicianSync(GlobalTechnicianSync globalTechnicianSync) {
		this.globalTechnicianSync = globalTechnicianSync;
	}

	public GlobalBookingsInboundSync getGlobalBookingsInboundSync() {
		return globalBookingsInboundSync;
	}

	public void setGlobalBookingsInboundSync(
			GlobalBookingsInboundSync globalBookingsInboundSync) {
		this.globalBookingsInboundSync = globalBookingsInboundSync;
	}

	public BookingsService getBookingsService() {
		return bookingsService;
	}

	public void setBookingsService(BookingsService bookingsService) {
		this.bookingsService = bookingsService;
	}

	public GlobalBookingsOutboundSync getGlobalBookingsOutboundSync() {
		return globalBookingsOutboundSync;
	}

	public void setGlobalBookingsOutboundSync(
			GlobalBookingsOutboundSync globalBookingsOutboundSync) {
		this.globalBookingsOutboundSync = globalBookingsOutboundSync;
	}

	public DealerIntegrationService getDealerIntegrationService() {
		return dealerIntegrationService;
	}

	public void setDealerIntegrationService(
			DealerIntegrationService dealerIntegrationService) {
		this.dealerIntegrationService = dealerIntegrationService;
	}

	public GlobalDealerBatchClaimSync getGlobalDealerBatchClaimSync() {
		return globalDealerBatchClaimSync;
	}

	public void setGlobalDealerBatchClaimSync(
			GlobalDealerBatchClaimSync globalDealerBatchClaimSync) {
		this.globalDealerBatchClaimSync = globalDealerBatchClaimSync;
	}
	

}

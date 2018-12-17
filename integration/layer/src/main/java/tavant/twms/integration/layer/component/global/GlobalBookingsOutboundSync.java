package tavant.twms.integration.layer.component.global;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlOptions;
import org.springframework.transaction.support.TransactionTemplate;

import tavant.twms.domain.bookings.BookingsService;
import tavant.twms.domain.bu.BusinessUnitInfo;
import tavant.twms.domain.common.BookingsReport;
import tavant.twms.domain.integration.SyncStatus;
import tavant.twms.domain.integration.SyncTracker;
import tavant.twms.domain.integration.SyncTrackerDAO;
import tavant.twms.domain.inventory.ContractCode;
import tavant.twms.domain.inventory.InventoryService;
import tavant.twms.domain.inventory.InventoryTransaction;
import tavant.twms.domain.inventory.InventoryTransactionService;
import tavant.twms.domain.orgmodel.Address;
import tavant.twms.domain.orgmodel.DealershipRepository;
import tavant.twms.domain.orgmodel.Party;
import tavant.twms.domain.orgmodel.ServiceProvider;
import tavant.twms.domain.policy.Customer;
import tavant.twms.domain.policy.CustomerService;
import tavant.twms.domain.policy.MarketingInformation;
import tavant.twms.domain.policy.Warranty;
import tavant.twms.domain.policy.WarrantyAudit;
import tavant.twms.domain.policy.WarrantyService;
import tavant.twms.integration.layer.component.global.InstallBase.BookingsValidator;
import tavant.twms.integration.layer.constants.IntegrationConstants;
import tavant.twms.integration.layer.util.CalendarUtil;
import tavant.twms.integration.layer.util.IntegrationLayerUtil;
import tavant.twms.security.SecurityHelper;


public class GlobalBookingsOutboundSync {

	private static final Logger logger = Logger
			.getLogger(GlobalBookingsOutboundSync.class.getName());

	private InventoryService inventoryService;

	private TransactionTemplate transactionTemplate;

	private BookingsValidator bookingsValidator;

	private SecurityHelper securityHelper;
	
	private InventoryTransactionService invTransactionService;
	
	

	SyncTrackerDAO syncTrackerDAO;
	
	
	com.nmhg.www.installationrequest.MTInstallationDocument installationDocument;
	com.nmhg.www.installationrequest.DTInstallation installation;
	com.nmhg.www.installationrequest.DTInstallation.DataArea dataArea;
	com.nmhg.www.installationrequest.DTInstallation.DataArea.CustomerInfo customerInfo;
	
	DealershipRepository dealershipRepository;
	WarrantyService warrantyService;
	CustomerService customerService;
	MarketingInformation  marketingInformation;
	private BookingsService bookingsService;
	


	public void saveAndsendUnitTransaction(
			final Date lastUpdatedTimeForinvTrans,
			final Date lastUpdatedTimeForWarranties) {

		try {
			syncInventoryBooking(lastUpdatedTimeForinvTrans,
					lastUpdatedTimeForWarranties);

		} catch (RuntimeException ex) {
			logger.error(ex);
		}
	}


	@SuppressWarnings("unused")
	private void syncInventoryBooking(Date lastUpdatedTimeForinvTrans, Date lastUpdatedTimeForWarranties) {
		try{
		int drCount = 0;
		int d2dCount = 0;
		int ssCount = 0;
		Date lastupdatefROMTX = new Date(); 
		final BusinessUnitInfo businessUnitInfo=new BusinessUnitInfo();
		businessUnitInfo.setName("AMER");
		createBookingsXmlForWarranty(lastUpdatedTimeForWarranties,businessUnitInfo,drCount);
		List<InventoryTransaction> inventoryTransactions = invTransactionService
					.getTransactionsOfDRAndD2d(lastUpdatedTimeForinvTrans,
							IntegrationConstants.NMHG_US);
		StringBuffer failedInventoryTransactionIds=new StringBuffer();
		for (InventoryTransaction inventoryTransaction : inventoryTransactions) {
			try{
			if(inventoryTransaction.getInvTransactionType().getTrnxTypeKey().equalsIgnoreCase("D2D")){
				prepareD2DXml(inventoryTransaction);
				String xml = installationDocument.xmlText(createXMLOptions());
				SyncTracker syncTracker = new SyncTracker(IntegrationConstants.INSTALLATION_SUBMISSION_SYNC_TYPE, xml);
				syncTracker.setStatus(SyncStatus.TO_BE_PROCESSED);
				syncTracker.setBusinessUnitInfo(businessUnitInfo);
				syncTrackerDAO.save(syncTracker);			
				drCount++;
				
			}
			if (inventoryTransaction.getInvTransactionType()
							.getTrnxTypeKey().equalsIgnoreCase("DR")) {
						prepareSignatureSheetXml(inventoryTransaction);
						String xml = installationDocument
								.xmlText(createXMLOptions());
						SyncTracker syncTracker = new SyncTracker(
								IntegrationConstants.INSTALLATION_SUBMISSION_SYNC_TYPE,
								xml);
						syncTracker.setStatus(SyncStatus.TO_BE_PROCESSED);
						syncTracker.setBusinessUnitInfo(businessUnitInfo);
						syncTrackerDAO.save(syncTracker);
						ssCount++;
					}
			}catch(RuntimeException e){
				logger.error("Error While Creating Bokking request for ........"+inventoryTransaction.getId() +" Error is "+e.getMessage(),e);
				failedInventoryTransactionIds.append(inventoryTransaction.getId());
				failedInventoryTransactionIds.append(",");
			}
		}
		if(failedInventoryTransactionIds!=null&&StringUtils.isNotBlank(failedInventoryTransactionIds.toString())){
			logger.error("List Of Inventory transaction id's which are failed while creating the Bookings request ..."+failedInventoryTransactionIds);	
		} 
		if(inventoryTransactions!=null&&!inventoryTransactions.isEmpty()){
		createBookingnsReportForInvTransactions(lastupdatefROMTX,d2dCount,ssCount,failedInventoryTransactionIds);
		}
		}catch(RuntimeException e){
			logger.error("Error While Creating Bokking request for "+e.getMessage(),e);			
		}
		}		
			
		
	private void createBookingsXmlForWarranty(
			Date lastUpdatedTimeForWarranties,
			BusinessUnitInfo businessUnitInfo, int drCount) {
		Date lastupdatefROMTX = new Date();
		List<Warranty> warranties = warrantyService
				.getwarrantyesByUpdateDateTime(lastUpdatedTimeForWarranties,
						IntegrationConstants.NMHG_US);
		StringBuffer failedWarrantyTransactionIds = new StringBuffer();
		for (Warranty warranty : warranties) {
			try {
				if ((warranty.getForTransaction()==null)||warranty.getForTransaction() != null
						&& warranty.getForTransaction().getInvTransactionType() != null
						&& warranty.getForTransaction().getInvTransactionType()
								.getTrnxTypeKey() != null
						&& warranty.getForTransaction().getInvTransactionType()
								.getTrnxTypeKey().equalsIgnoreCase("DR")) {
					prepareDrXml(warranty,lastUpdatedTimeForWarranties);
					String xml = installationDocument
							.xmlText(createXMLOptions());
					SyncTracker syncTracker = new SyncTracker(
							IntegrationConstants.INSTALLATION_SUBMISSION_SYNC_TYPE,
							xml);
					syncTracker.setStatus(SyncStatus.TO_BE_PROCESSED);
					syncTracker.setBusinessUnitInfo(businessUnitInfo);
					syncTrackerDAO.save(syncTracker);
					drCount++;
				}
			} catch (RuntimeException e) {
				logger.error("Error While Processing the Warranty id "
						+ warranty.getId() + " ..........." + e, e);
				logger.error(e.getMessage(), e);
				failedWarrantyTransactionIds.append(warranty.getId());
				failedWarrantyTransactionIds.append(",");
			}
		}
		if (failedWarrantyTransactionIds != null
				&& StringUtils.isNotBlank(failedWarrantyTransactionIds
						.toString())) {
			logger
					.error("List Of IWarranty id's which are failed while creating the Bookings request ..."
							+ failedWarrantyTransactionIds);
		}
		if (warranties != null && !warranties.isEmpty()) {
			createBookingnsReportForWarranty(lastupdatefROMTX, drCount,
					failedWarrantyTransactionIds);
		}
	}


	private void createBookingnsReportForWarranty(
			Date lastUpdatedTimeForWarranties, int drCount,
			StringBuffer failedWarrantyTransactionIds) {
		BookingsReport bookingreport = new BookingsReport();
		bookingreport
				.setWarrantyLastProcessedTime(lastUpdatedTimeForWarranties);
		bookingreport.setNoOfD2D(0);
		bookingreport.setNoOfDR(drCount);
		bookingreport.setNoOfSignatureSheet(0);
		if (failedWarrantyTransactionIds != null
				&& StringUtils.isNotBlank(failedWarrantyTransactionIds
						.toString()))
			bookingreport.setFailedBookingIds(failedWarrantyTransactionIds
					.toString());
		bookingsService.save(bookingreport);

	}


	private void createBookingnsReportForInvTransactions(
			Date lastUpdatedTimeForinvTrans, int d2dCount, int ssCount,
			StringBuffer failedInventoryTransactionIds) {
		BookingsReport bookingreport = new BookingsReport();
		bookingreport.setInvTransLastProcessedTime(lastUpdatedTimeForinvTrans);
		bookingreport.setNoOfD2D(d2dCount);
		if (failedInventoryTransactionIds != null
				&& StringUtils.isNotBlank(failedInventoryTransactionIds
						.toString()))
			bookingreport.setFailedBookingIds(failedInventoryTransactionIds
					.toString());
		bookingreport.setNoOfSignatureSheet(ssCount);
		bookingsService.save(bookingreport);
	}


	private void prepareD2DXml(InventoryTransaction inventoryTransaction) {
		installationDocument=com.nmhg.www.installationrequest.MTInstallationDocument.Factory.newInstance();
		installation=com.nmhg.www.installationrequest.DTInstallation.Factory.newInstance();
		 dataArea=com.nmhg.www.installationrequest.DTInstallation.DataArea.Factory.newInstance();
		installation.setApplicationArea(createApplicationArea());
		dataArea.setTransactionType("D");
		ServiceProvider seller=dealershipRepository.findByDealerId(inventoryTransaction.getSeller().getId());
		ServiceProvider buyer=dealershipRepository.findByDealerId(inventoryTransaction.getBuyer().getId());
		dataArea.setDealerCode(IntegrationLayerUtil.getSubString(seller.getDealerNumber(),9));
		dataArea.setTTDealerCode(IntegrationLayerUtil.getSubString(buyer.getDealerNumber(),9));
		Calendar calendar=Calendar.getInstance();
		calendar.setTime(inventoryTransaction.getD().getUpdatedTime());
		dataArea.setTransactionDateTime(CalendarUtil.convertToDateTimeToString(calendar.getTime()));
		dataArea.setMKTGroup(seller.getDealerNumber().substring(0,3));
		dataArea.setJustDealerCode(seller.getDealerNumber().substring(3,9));
		dataArea.setTTJustDealerCode(buyer.getDealerNumber().substring(3,9));
		if(inventoryTransaction.getD()!=null&&inventoryTransaction.getD().getLastUpdatedBy()!=null){
			dataArea.setTransactionUserID((inventoryTransaction.getD().getLastUpdatedBy().getName()));

		}else{
			dataArea.setTransactionUserID("system");
		}		dataArea.setUnitSerialNumber(inventoryTransaction.getTransactedItem().getSerialNumber());
		installation.setDataArea(dataArea);
		installationDocument.setMTInstallation(installation);
		
		
	}


	private void prepareSignatureSheetXml(InventoryTransaction inventoryTransaction) {
		installationDocument=com.nmhg.www.installationrequest.MTInstallationDocument.Factory.newInstance();
		installation=com.nmhg.www.installationrequest.DTInstallation.Factory.newInstance();
		 dataArea=com.nmhg.www.installationrequest.DTInstallation.DataArea.Factory.newInstance();
		 installation.setApplicationArea(createApplicationArea());
		 dataArea.setTransactionType("S");
		ServiceProvider seller=dealershipRepository.findByDealerId(inventoryTransaction.getSeller().getId());
		dataArea.setDealerCode(IntegrationLayerUtil.getSubString(seller.getDealerNumber(),9));
		dataArea.setMKTGroup(seller.getDealerNumber().substring(0,3));
		dataArea.setJustDealerCode(seller.getDealerNumber().substring(3,9));
		dataArea.setUnitSerialNumber(inventoryTransaction.getTransactedItem().getSerialNumber());
		Calendar calendar=Calendar.getInstance();
		calendar.setTime(inventoryTransaction.getD().getUpdatedTime());
		if (inventoryTransaction.getTransactedItem().getDeliveryDate() != null)
			dataArea.setTransactionDateTime(CalendarUtil
					.convertToDateTimeToString(CalendarUtil
							.convertToJavaDate(inventoryTransaction
									.getTransactedItem().getDeliveryDate())));
		dataArea.setSignatureSheetDateTime(CalendarUtil.convertToDateTimeToString(calendar.getTime()));
		 customerInfo=com.nmhg.www.installationrequest.DTInstallation.DataArea.CustomerInfo.Factory.newInstance();
		Warranty warranty=warrantyService.findByTransactionId(inventoryTransaction.getId());
		dataArea.setInstallationType(getInstallType(warranty));
		Party customer=warranty.getCustomer();
		Customer cust=customerService.findCustomerById(customer.getId());
		if(inventoryTransaction.getD()!=null&&inventoryTransaction.getD().getLastUpdatedBy()!=null)
		dataArea.setTransactionUserID((inventoryTransaction.getD().getLastUpdatedBy().getName()));
		Address address;
		if(customer!=null&&cust!=null){
		 address=customer.getAddress();
		customerInfo.setCustomerName(IntegrationLayerUtil.getSubString(customer.getName(),25));
		customerInfo.setAddressLine1(IntegrationLayerUtil.getSubString(address.getAddressLine1(),30));
		customerInfo.setAddressLine2(IntegrationLayerUtil.getSubString(address.getAddressLine2(),30));
		customerInfo.setCity(IntegrationLayerUtil.getSubString(address.getCity(),25));
		customerInfo.setState(address.getState());
		customerInfo.setPostalCode(IntegrationLayerUtil.getSubString(address.getZipCode(),10));
		customerInfo.setSICode(IntegrationLayerUtil.getSubString(cust.getSiCode(),4));
		customerInfo.setCountry(address.getCountry());
			if (inventoryTransaction.getD() != null
					&& inventoryTransaction.getD().getLastUpdatedBy() != null
					&& inventoryTransaction.getD().getLastUpdatedBy().getName() != null
					&& StringUtils.isNotBlank(inventoryTransaction.getD()
							.getLastUpdatedBy().getName())
					&& (inventoryTransaction.getD().getLastUpdatedBy()
							.getName().equalsIgnoreCase("integration") || inventoryTransaction
							.getD().getLastUpdatedBy().getName()
							.equalsIgnoreCase("system"))) {
				dataArea.setTransactionUserID("AAMRKWEB");
				customerInfo.setCountyCode(IntegrationLayerUtil.getSubString(address.getCounty(),3));
				dataArea.setInstallationType("G");
			} else {
				if(address.getCounty()!=null){
						customerInfo.setCountyCode(IntegrationLayerUtil.getSubString(address.getCounty(),3));	
				}
			}
		}
		dataArea.setCustomerInfo(customerInfo);
		installation.setDataArea(dataArea);
		installationDocument.setMTInstallation(installation);
	
	}

	private void prepareDrXml(Warranty warranty,Date lastUpdatedTimeForWarranties) {
		installationDocument=com.nmhg.www.installationrequest.MTInstallationDocument.Factory.newInstance();
		installation=com.nmhg.www.installationrequest.DTInstallation.Factory.newInstance();
		 dataArea=com.nmhg.www.installationrequest.DTInstallation.DataArea.Factory.newInstance();
		 installation.setApplicationArea(createApplicationArea());
		 dataArea.setTransactionType("I");
		ServiceProvider seller=warranty.getForDealer();
		dataArea.setDealerCode(IntegrationLayerUtil.getSubString(seller.getDealerNumber(),9));
		dataArea.setMKTGroup(seller.getDealerNumber().substring(0,3));
		dataArea.setJustDealerCode(seller.getDealerNumber().substring(3,9));
		dataArea.setUnitSerialNumber(warranty.getForItem().getSerialNumber());

		WarrantyAudit warrantyAudit = warrantyService.findWarrantyAuditFromWarranty(warranty, lastUpdatedTimeForWarranties);
		if (warrantyAudit !=null && warrantyAudit.getD()!=null&&warrantyAudit.getD().getLastUpdatedBy()!=null)
		{
			dataArea.setTransactionUserID((warrantyAudit.getD().getLastUpdatedBy().getName()));
		}
		else if(warranty.getD()!=null&&warranty.getD().getLastUpdatedBy()!=null)
		{
			dataArea.setTransactionUserID((warranty.getD().getLastUpdatedBy().getName()));	
		}
		
		Calendar calendar=Calendar.getInstance();
		calendar.setTime(warranty.getD().getUpdatedTime());
		if (warranty.getDeliveryDate() != null)
			dataArea.setTransactionDateTime(CalendarUtil
					.convertToDateTimeToString(CalendarUtil
							.convertToJavaDate(warranty.getDeliveryDate())));
		 customerInfo=com.nmhg.www.installationrequest.DTInstallation.DataArea.CustomerInfo.Factory.newInstance();
		 dataArea.setInstallationType(getInstallType(warranty));
		 Party customer=warranty.getCustomer();
		Customer cust=customerService.findCustomerById(customer.getId());

		Address address;
		if(customer!=null&&cust!=null){
		 address=customer.getAddress();
		customerInfo.setCustomerName(IntegrationLayerUtil.getSubString(customer.getName(),25));
		customerInfo.setAddressLine1(IntegrationLayerUtil.getSubString(address.getAddressLine1(),30));
		customerInfo.setAddressLine2(IntegrationLayerUtil.getSubString(address.getAddressLine2(),30));
		customerInfo.setCity(IntegrationLayerUtil.getSubString(address.getCity(),25));
		customerInfo.setState(address.getState());
		customerInfo.setPostalCode(IntegrationLayerUtil.getSubString(address.getZipCode(),10));
		customerInfo.setSICode(IntegrationLayerUtil.getSubString(cust.getSiCode(),4));
		customerInfo.setCountry(address.getCountry());
			if (warranty.getD() != null
					&& warranty.getD().getLastUpdatedBy() != null
					&& warranty.getD().getLastUpdatedBy().getName() != null
					&& StringUtils.isNotBlank(warranty.getD()
							.getLastUpdatedBy().getName())
					&& (warranty.getD().getLastUpdatedBy().getName()
							.equalsIgnoreCase("integration") || warranty.getD()
							.getLastUpdatedBy().getName().equalsIgnoreCase(
									"system"))) {
				dataArea.setTransactionUserID("AAMRKWEB");
				customerInfo.setCountyCode(IntegrationLayerUtil.getSubString(address.getCounty(),3));
				dataArea.setInstallationType("G");

			} else if(address.getCounty()!=null){
			customerInfo.setCountyCode(IntegrationLayerUtil.getSubString(address.getCounty(),3));

		}		}
		dataArea.setCustomerInfo(customerInfo);
		installation.setDataArea(dataArea);
		installationDocument.setMTInstallation(installation);
		
	}

	
	private String getInstallType(Warranty warranty) {
		String installationType = null;
		ContractCode contractCode = warranty.getMarketingInformation()
				.getContractCode();
		if (contractCode != null) {
			installationType = contractCode.getContractCode();
			if (installationType.equalsIgnoreCase("Demo")) {
				installationType = "D";
			} else if (installationType.equalsIgnoreCase("Rental Long Term")
					|| installationType.equalsIgnoreCase("Rental Short Term")) {
				installationType = "R";
			} else if (installationType.equalsIgnoreCase("Sale")
					|| installationType.equalsIgnoreCase("Refurbished")) {
				installationType = "C";
			} else {
				installationType = "C";
			}
		}
		if (installationType == null
				&& warranty.getMarketingInformation() != null
				&& warranty.getMarketingInformation().getInternalInstallType() != null
				&& warranty.getMarketingInformation().getInternalInstallType()
						.getInternalInstallType() != null) {
			if (IntegrationConstants.INSTALL_TYPE_FOR_NATIONAL_ACCOUNTS
					.equalsIgnoreCase(warranty.getMarketingInformation()
							.getInternalInstallType().getInternalInstallType())) {
				installationType = "C";
			} else {
				installationType = "G";
			}

		}
		return installationType;
	}


	   private  com.nmhg.www.installationrequest.DTInstallation.ApplicationArea createApplicationArea(){
		   com.nmhg.www.installationrequest.DTInstallation.ApplicationArea applicationAreaTypeDTO =  com.nmhg.www.installationrequest.DTInstallation.ApplicationArea.Factory.newInstance();
		   com.nmhg.www.installationrequest.DTInstallation.ApplicationArea.Sender sender =  com.nmhg.www.installationrequest.DTInstallation.ApplicationArea.Sender.Factory.newInstance();     
		   sender.setReferenceId("BookINT91"+new Date().getTime());
		   sender.setLogicalId("SLMS");
		   sender.setTask("NWO");
	        applicationAreaTypeDTO.setSender(sender);
	       applicationAreaTypeDTO.setCreationDateTime(CalendarUtil
					.convertToDateTimeToString(Calendar.getInstance().getTime()));
	         applicationAreaTypeDTO.setInterfaceNumber("INT91");
	        // this field doesn't have any Business logic in the system
	        applicationAreaTypeDTO.setBODId("INT91");
	        return applicationAreaTypeDTO;
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


	public InventoryService getInventoryService() {
		return inventoryService;
	}

	public void setInventoryService(InventoryService inventoryService) {
		this.inventoryService = inventoryService;
	}

	public TransactionTemplate getTransactionTemplate() {
		return transactionTemplate;
	}

	public void setTransactionTemplate(TransactionTemplate transactionTemplate) {
		this.transactionTemplate = transactionTemplate;
	}

	public BookingsValidator getBookingsValidator() {
		return bookingsValidator;
	}

	public void setBookingsValidator(BookingsValidator bookingsValidator) {
		this.bookingsValidator = bookingsValidator;
	}

	public SecurityHelper getSecurityHelper() {
		return securityHelper;
	}

	public void setSecurityHelper(SecurityHelper securityHelper) {
		this.securityHelper = securityHelper;
	}

	public InventoryTransactionService getInvTransactionService() {
		return invTransactionService;
	}

	public void setInvTransactionService(
			InventoryTransactionService invTransactionService) {
		this.invTransactionService = invTransactionService;
	}

	public static Logger getLogger() {
		return logger;
	}


	public DealershipRepository getDealershipRepository() {
		return dealershipRepository;
	}


	public void setDealershipRepository(DealershipRepository dealershipRepository) {
		this.dealershipRepository = dealershipRepository;
	}


	public WarrantyService getWarrantyService() {
		return warrantyService;
	}


	public void setWarrantyService(WarrantyService warrantyService) {
		this.warrantyService = warrantyService;
	}


	public CustomerService getCustomerService() {
		return customerService;
	}


	public void setCustomerService(CustomerService customerService) {
		this.customerService = customerService;
	}


	public BookingsService getBookingsService() {
		return bookingsService;
	}


	public void setBookingsService(BookingsService bookingsService) {
		this.bookingsService = bookingsService;
	}


	public SyncTrackerDAO getSyncTrackerDAO() {
		return syncTrackerDAO;
	}


	public void setSyncTrackerDAO(SyncTrackerDAO syncTrackerDAO) {
		this.syncTrackerDAO = syncTrackerDAO;
	}



}

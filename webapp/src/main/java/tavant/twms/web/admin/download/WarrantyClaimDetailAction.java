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
 */

package tavant.twms.web.admin.download;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.mail.MethodNotSupportedException;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.StringUtils;

import tavant.twms.domain.bu.BusinessUnit;
import tavant.twms.domain.claim.RecoveryClaimState;
import tavant.twms.domain.configuration.ConfigName;
import tavant.twms.domain.configuration.ConfigParamService;
import tavant.twms.domain.download.DownloadClaimState;
import tavant.twms.domain.download.InventoryReportSearchBean;
import tavant.twms.domain.download.ReportSearchBean;
import tavant.twms.domain.download.WarrantyReport;
import tavant.twms.domain.download.WarrantyReportGenerator;
import tavant.twms.domain.download.WarrantyReportGeneratorFactory;
import tavant.twms.domain.orgmodel.ServiceProvider;
import tavant.twms.domain.policy.PolicyDefinition;
import tavant.twms.domain.policy.PolicyService;
import tavant.twms.domain.upload.controller.DataUploadConfig;
import tavant.twms.web.upload.HeaderUtil;

import com.domainlanguage.timeutil.Clock;
import com.opensymphony.xwork2.Preparable;
import tavant.twms.domain.claim.RecoveryClaim;
import tavant.twms.domain.claim.RecoveryClaimService;
import tavant.twms.domain.partrecovery.PartRecoverySearchCriteria;

/**
 * @author jhulfikar.ali
 *
 */
@SuppressWarnings("serial")
public class WarrantyClaimDetailAction extends ExportAction implements Preparable, WarrantyReport {

	private Logger logger = Logger.getLogger(WarrantyClaimDetailAction.class);
	
	private WarrantyReportGenerator warrantyReportGenerator;
	
	private ReportSearchBean reportSearchBean;
	
	private List<DownloadClaimState> claimStates;
	
	private List<String> delimiters;
	
	private Map<String, String> availableBusinessUnits;
	
	private WarrantyReportGeneratorFactory warrantyReportGeneratorFactory;
	
	private String context;
	
	private String displayContext;
	
	private DataUploadConfig dataUploadConfig;
	
	private String userNotes;
	
	private long resultSize;
	
	private int downloadPageNumber;
	
	private List<PolicyDefinition> extendedPolicyList = new ArrayList<PolicyDefinition>();
	private Map<Object, Object> customerTypes = new HashMap<Object, Object>();
		
	private PolicyService policyService;
	private ConfigParamService configParamService;
    private RecoveryClaimService recoveryClaimService;
//	protected DealershipRepository dealershipRepository;
//	protected List<Customer> customers;
	
	public ConfigParamService getConfigParamService() {
		return configParamService;
	}
	
	public boolean isPageReadOnly() {
		return false;
	}

	public void setConfigParamService(ConfigParamService configParamService) {
		this.configParamService = configParamService;
	}

	public PolicyService getPolicyService() {
		return policyService;
	}

	public void setPolicyService(PolicyService policyService) {
		this.policyService = policyService;
	}

	public List<PolicyDefinition> getExtendedPolicyList() {
		return extendedPolicyList;
	}

	public void setExtendedPolicyList(List<PolicyDefinition> extendedPolicyList) {
		this.extendedPolicyList = extendedPolicyList;
	}

	public int getDownloadPageNumber() {
		return downloadPageNumber;
	}

	public void setDownloadPageNumber(int downloadPageNumber) {
		this.downloadPageNumber = downloadPageNumber;
	}

	public long getResultSize() {
		return resultSize;
	}

	public void setResultSize(long resultSize) {
		this.resultSize = resultSize;
	}

	public long getMaxDownloadSize() {
		return maxDownloadSize;
	}

	public void setMaxDownloadSize(long maxDownloadSize) {
		this.maxDownloadSize = maxDownloadSize;
	}

	public Map<Object, Object> getCustomerTypes() {
		return customerTypes;
	}

	public void setCustomerTypes(Map<Object, Object> customerTypes) {
		this.customerTypes = customerTypes;
	}

	private long maxDownloadSize;
	

	public void prepare() throws Exception {
		// Populating All the required claim States
		populateClaimStates();

		setUserNotes(getText("label.downloadMgt.downloadMaximumLimit",
                new String[] {String.valueOf(dataUploadConfig.getExportRecordsLimit())}));
		
		resultSize = -1;
		maxDownloadSize = dataUploadConfig.getExportRecordsLimit();
		
		// Populating De-limiters
		populateAvailableDelimiters();
		
		// Populating All Business Unit
		populateAllBusinessUnits();
	}

    private PartRecoverySearchCriteria getCriteria() {
        PartRecoverySearchCriteria criteria = new PartRecoverySearchCriteria();
        criteria.setSupplierNumber(reportSearchBean.getDealerNumber());
        if(!"ALL".equalsIgnoreCase(reportSearchBean.getClaimStatus()))
            criteria.setRecoveryClaimState(RecoveryClaimState.valueOf(reportSearchBean.getClaimStatus()));
        if("submitDate".equalsIgnoreCase(reportSearchBean.getSubmitOrCreditOrUpdateDate())){
            criteria.setFromCreatedDate(reportSearchBean.getFromDate());
            criteria.setToCreatedDate(reportSearchBean.getToDate());
        }else if("updateDate".equalsIgnoreCase(reportSearchBean.getSubmitOrCreditOrUpdateDate())){
            criteria.setFromUpdatedDate(reportSearchBean.getFromDate());
            criteria.setToUpdatedDate(reportSearchBean.getToDate());
        }
        List<String> buNameList = new ArrayList(StringUtils.commaDelimitedListToSet(reportSearchBean.getBusinessUnitName()));
        criteria.setSelectedBusinessUnits(buNameList);
        return criteria;
    }
	
	private void loadExtendedPolicies() {
		extendedPolicyList = policyService
				.findAllExtendedPoliciesAvailable(isLoggedInUserAnInternalUser());
		ServiceProvider loggedInUsersDealership = getLoggedInUsersDealership();
		if (isLoggedInUserADealer() && !isLoggedInUserAnAdmin() && !isLoggedInUserAnInvAdmin()
				&& loggedInUsersDealership != null) {
            for (Iterator<PolicyDefinition> policyIterator = extendedPolicyList
                    .iterator(); policyIterator.hasNext();) {
                PolicyDefinition policyDefinition = policyIterator.next();
                if (policyDefinition.isServiceProviderBlackListed(loggedInUsersDealership)) {
                    policyIterator.remove();
                }
            }
        }
		if(availableBusinessUnits != null && availableBusinessUnits.size() > 1) {
			for(PolicyDefinition policyDefinition : extendedPolicyList){
				policyDefinition.setDescription(policyDefinition.getBusinessUnitInfo()+" - "+
						policyDefinition.getDescription());				
			}
		}
		Collections.sort(extendedPolicyList, new Comparator(){
			public int compare(Object obj0, Object obj1) {
				PolicyDefinition policyDefinition0 =(PolicyDefinition) obj0;
				PolicyDefinition policyDefinition1 =(PolicyDefinition) obj1;
				return policyDefinition0.getDescription().compareTo(policyDefinition1.getDescription());
			}
		});
	}

	private void populateAllBusinessUnits() {
		// Populating Business Units in the system
		Set<BusinessUnit> buList = getBusinessUnits();
		if (availableBusinessUnits==null)
			availableBusinessUnits = new HashMap<String, String>(5);
		for (Iterator<BusinessUnit> iterator = buList.iterator(); iterator.hasNext();) {
			BusinessUnit businessUnit = (BusinessUnit) iterator.next();
			availableBusinessUnits.put(businessUnit.getName(), businessUnit.getDisplayName());
		}
	}

	private void populateAvailableDelimiters() {
		// Populating De-limiters
		if (delimiters==null)
			delimiters = new ArrayList<String>(3);
		delimiters.add(",");
		delimiters.add(".");
		delimiters.add(";");
	}

	private void populateClaimStates() {
		// Populating All the required claim States
		if (claimStates==null)
			claimStates = new ArrayList<DownloadClaimState>();
		claimStates.add(DownloadClaimState.CREDITED);
		claimStates.add(DownloadClaimState.DENIED);
		claimStates.add(DownloadClaimState.FORWARDED);
		claimStates.add(DownloadClaimState.IN_PROGRESS);
		claimStates.add(DownloadClaimState.NEW);
	}
	
	public String displayDownloadClaimDetailData()
	{
		initReportSearchBean(getContext());
		if(availableBusinessUnits != null && availableBusinessUnits.size() ==1)
			this.getReportSearchBean().setBusinessUnitName(
					availableBusinessUnits.keySet().iterator().next());
		setDisplayContext();
		if (DOWNLOAD_CONTEXT_EWP_REPORT.equalsIgnoreCase(getContext()))
			loadExtendedPolicies();
		if(DOWNLOAD_CONTEXT_CUSTOMER_DATA.equalsIgnoreCase(getContext()))
			loadCustomerTypes();
		return SUCCESS;
	}

	private void initReportSearchBean(String context) {
		if (this.getReportSearchBean()==null) {
			if (DOWNLOAD_CONTEXT_EWP_REPORT.equalsIgnoreCase(context)) {
				this.reportSearchBean = new InventoryReportSearchBean();
				((InventoryReportSearchBean)this.reportSearchBean).setAllExtendedPlansSelected(true);
				((InventoryReportSearchBean)this.reportSearchBean).setCoveredOrTerminated(InventoryReportSearchBean.CVG_NOT_COVERED);
			}
			else
				this.reportSearchBean = new ReportSearchBean();
		}
		this.getReportSearchBean().setFromDate(Clock.today()); 
		this.getReportSearchBean().setToDate(Clock.today());
	}
	
	private void setDisplayContext() {
		if (DOWNLOAD_CONTEXT_CLAIM_DATA.equalsIgnoreCase(getContext()))
			setDisplayContext(getText("label.downloadMgt.wntyClmDownload"));
		else if (DOWNLOAD_CONTEXT_CLAIM_PARTS_DATA.equalsIgnoreCase(getContext()))
			setDisplayContext(getText("label.downloadMgt.wntyClmPartsDownload"));
		else if (DOWNLOAD_CONTEXT_CLAIM_DETAIL_DATA.equalsIgnoreCase(getContext()))
			setDisplayContext(getText("label.downloadMgt.wntyClmDetailDownload"));
		else if (DOWNLOAD_CONTEXT_RECOVERY_REPORT.equalsIgnoreCase(getContext()))
			setDisplayContext(getText("label.downloadMgt.suppRecReport"));
		else if (DOWNLOAD_CONTEXT_RECOVERY_PARTS_REPORT.equalsIgnoreCase(getContext()))
			setDisplayContext(getText("label.downloadMgt.suppRecPartsReport"));
		else if (DOWNLOAD_CONTEXT_UNDERWRITER_CLAIM_DATA.equalsIgnoreCase(getContext()))
			setDisplayContext(getText("label.downloadMgt.underWriterClaimData"));
		else if (DOWNLOAD_CONTEXT_EXT_WNTY_CLAIM_DATA.equalsIgnoreCase(getContext()))
			setDisplayContext(getText("label.downloadMgt.extWntyClmData"));
		else if (DOWNLOAD_CONTEXT_MACHINE_RETAIL_REPORT.equalsIgnoreCase(getContext()))
			setDisplayContext(getText("label.downloadMgt.machineRetailDownload"));
		else if (DOWNLOAD_CONTEXT_EWP_REPORT.equalsIgnoreCase(getContext()))
			setDisplayContext(getText("label.downloadMgt.ewpDownload"));
		else if(DOWNLOAD_CONTEXT_CUSTOMER_DATA.equalsIgnoreCase(getContext()))
			setDisplayContext(getText("label.downloadMgt.customerDataDownload"));
		else if(DOWNLOAD_CONTEXT_PENDING_EXTENSIONS.equalsIgnoreCase(getContext()))
			setDisplayContext(getText("label.downloadMgt.pendingExtensionsDownload"));
	}
	
	private void loadCustomerTypes(){
		setCustomerTypes(this.getConfigParamService().getKeyValuePairOfObjects(ConfigName.CUSTOMERS_FILING_DR.getName()));
	}
	
	@Override
	protected HSSFWorkbook getDownloadData() {
		HSSFWorkbook workBook = null;
		if(reportSearchBean.getBusinessUnitName() == null)
		reportSearchBean.setBusinessUnitName(StringUtils.collectionToCommaDelimitedString(
				availableBusinessUnits.values()));
		if (validateDownloadRequest())
		{
			try {
				workBook = warrantyReportGeneratorFactory.getUserReportGenerator(getContext()).exportReportData(getReportSearchBean());
			} catch (Exception exception) {
				logger.error("Error in downloading report - " + getContext(), exception);
			}
		}
		setDisplayContext();
		if (DOWNLOAD_CONTEXT_EWP_REPORT.equalsIgnoreCase(getContext()))
			loadExtendedPolicies();
		return workBook;
	}

	@Override
	public String downloadData() throws Exception {	
		try {
			if(!validateDownloadRequest()) {
				setDisplayContext();
				if (DOWNLOAD_CONTEXT_EWP_REPORT.equalsIgnoreCase(getContext()))
					loadExtendedPolicies();
				if(DOWNLOAD_CONTEXT_CUSTOMER_DATA.equalsIgnoreCase(getContext()))
					loadCustomerTypes();
				return INPUT;
			}
			if(DOWNLOAD_CONTEXT_RECOVERY_REPORT.equals(getContext())){
                resultSize = recoveryClaimService.findRecClmsCountForVRRDownload(getCriteria());
                if(resultSize > dataUploadConfig.getExportRecordsLimit() || resultSize == 0)
                    return "pagination";
                return downloadVendorRecoveryReport();
            }else{
                long rowCount = -1;
                if(warrantyReportGeneratorFactory.getUserReportGenerator(getContext()).isPaginationSupported())
                    rowCount = getDownloadDataCount();
                if(rowCount > dataUploadConfig.getExportRecordsLimit() || rowCount == 0)
                    return "pagination";
                downloadPageNumber = 0;
                downloadPage();
            }
		} catch (Exception exception) {
            logger.error("Error in downloading report - " + getContext(), exception);
            String message = exception.getMessage() == null ? 
                    "An error occured while downloading the report." : exception.getMessage();
            addActionError(message);
			return INPUT;
		}
		return null;		
	}
		
	public String downloadPage() throws Exception {
        if(DOWNLOAD_CONTEXT_RECOVERY_REPORT.equals(getContext())){
            return downloadVendorRecoveryReport();
        }
		try {
			File tempFile = File.createTempFile("reportData", "csv");
			FileOutputStream os = new FileOutputStream(tempFile);
			getDownloadData(os, downloadPageNumber);
			//os.close();
			setHeader(this.response, getDataFileName() + ".csv", HeaderUtil.CSV);
			addActionMessage(getText("label.downloadMgt.downloadSuccess"));
			FileInputStream in = new FileInputStream(tempFile);
			FileCopyUtils.copy(in, this.response.getOutputStream());
			in.close();
			tempFile.delete();
		} catch (Exception exception) {
            logger.error("Error in downloading report - " + getContext(), exception);
            String message = exception.getMessage() == null ? 
                    "An error occured while downloading the report." : exception.getMessage();
            addActionError(message);
			return INPUT;
		}
		return null;
	}
	
	@Override
	protected long getDownloadDataCount() {
		if(reportSearchBean.getBusinessUnitName() == null)
		reportSearchBean.setBusinessUnitName(StringUtils.collectionToCommaDelimitedString(
				availableBusinessUnits.values()));
		resultSize = -1;
		if (validateDownloadRequest())
		{
			try {
				resultSize = warrantyReportGeneratorFactory.getUserReportGenerator(getContext()).getReportDataCount(getReportSearchBean());
			} catch (Exception exception) {
				logger.error("Error in downloading report - " + getContext(), exception);
			}
		}
		setDisplayContext();
		if (DOWNLOAD_CONTEXT_EWP_REPORT.equalsIgnoreCase(getContext()))
			loadExtendedPolicies();
		return resultSize;
	}
	
	@Override
	protected void getDownloadData(OutputStream os, int downloadPageNumber) {
		if(reportSearchBean.getBusinessUnitName() == null)
		reportSearchBean.setBusinessUnitName(StringUtils.collectionToCommaDelimitedString(
				availableBusinessUnits.values()));
		if (validateDownloadRequest())
		{
			try {
				if(DOWNLOAD_CONTEXT_CUSTOMER_DATA.equals(getContext())){
					warrantyReportGeneratorFactory.getUserReportGenerator(getContext()).exportReportData(getReportSearchBean(), os);
				} else {
					warrantyReportGeneratorFactory.getUserReportGenerator(getContext()).exportReportData(getReportSearchBean(), os, downloadPageNumber);
				}
			} catch (Exception exception) {
				logger.error("Error in downloading report - " + getContext(), exception);
			}
		}
		setDisplayContext();
		if (DOWNLOAD_CONTEXT_EWP_REPORT.equalsIgnoreCase(getContext()))
			loadExtendedPolicies();
	}

	
	@Override
	protected String getDataFileName() throws MethodNotSupportedException {
		return warrantyReportGeneratorFactory.getUserReportGenerator(getContext()).getReportFileName();
	}

	private boolean validateToAndFromDates() {
		boolean validForDownload = Boolean.TRUE;
		if (getReportSearchBean().getFromDate()==null) {
			addActionError("error.downloadMgt.fromDateRequired");
			validForDownload = false;
		}
		if (getReportSearchBean().getToDate()==null) {
			addActionError("error.downloadMgt.toDateRequired");
			validForDownload = false;
		}
		if (getReportSearchBean().getFromDate()!=null && getReportSearchBean().getToDate()!=null && 
				getReportSearchBean().getFromDate().isAfter(getReportSearchBean().getToDate())) {
			addActionError("error.downloadMgt.fromDateToDateMismatch");
			validForDownload = false;
		}
		return validForDownload;
	}
	
	private boolean validateDealerNumber() {
		if (!getReportSearchBean().isAllDealerSelected() && 
				(getReportSearchBean().getDealerNumber()==null 
						|| "".equals(getReportSearchBean().getDealerNumber())))
		{
			addActionError("error.downloadMgt.dealerNumberOrAllDealerRequired");
			return false;
		}
		return true;
	}
	
	private boolean validateSubmitOrCreditOrUpdate() {
		if (getReportSearchBean().getSubmitOrCreditOrUpdateDate()==null)
		{
			addActionError("error.downloadMgt.submitOrCreditDateRequired");
			return false;
		}
		return true;
	}
	
	private boolean validateEWPReport() {
		boolean validForDownload = Boolean.TRUE;
		InventoryReportSearchBean invSearchBean = (InventoryReportSearchBean)getReportSearchBean();
		if(invSearchBean.getCoveredOrTerminated() == null) {
			addActionError("error.downloadMgt.ewp.coveredOrTerminatedRequired");
			validForDownload = false;
		}
		if(invSearchBean.getStartWindowPeriodFromDeliveryDate() != null
				&& invSearchBean.getStartWindowPeriodFromDeliveryDate() < 0) {
			addActionError("error.downloadMgt.ewp.startWindowPeriodRequired");
			validForDownload = false;
		}
		if(invSearchBean.getEndWindowPeriodFromDeliveryDate() != null
				&& invSearchBean.getEndWindowPeriodFromDeliveryDate() < 0) {
			addActionError("error.downloadMgt.ewp.endWindowPeriodRequired");
			validForDownload = false;
		}
		if(invSearchBean.getStartWindowPeriodFromDeliveryDate() != null
				&& invSearchBean.getEndWindowPeriodFromDeliveryDate() != null
				&& (invSearchBean.getEndWindowPeriodFromDeliveryDate() 
					< invSearchBean.getStartWindowPeriodFromDeliveryDate())) {
			addActionError("error.downloadMgt.ewp.windowPeriodInvalid");
			validForDownload = false;
		}
		validForDownload  = validForDownload && validateDealerNumber();
		return validForDownload;
	}
	
	private boolean validateDownloadRequest() {
		
		if(DOWNLOAD_CONTEXT_EWP_REPORT.equalsIgnoreCase(getContext())) {
			return validateEWPReport();
		} else if(DOWNLOAD_CONTEXT_CUSTOMER_DATA.equalsIgnoreCase(getContext())){
			if(!StringUtils.hasText(getReportSearchBean().getDealerNumber())){
				addActionError("error.downloadMgt.dealerNumberRequired");
				return false;
			}
			if(!StringUtils.hasText(getReportSearchBean().getCustomerType())){
				addActionError("error.customerTypeNotSelected");
				return false;
			}
			return true;
		} else if(DOWNLOAD_CONTEXT_PENDING_EXTENSIONS.equalsIgnoreCase(getContext())){
			if(!StringUtils.hasText(getReportSearchBean().getDealerNumber())){
				addActionError("error.downloadMgt.dealerNumberRequired");
				return false;
			}
			return true;
		}
		boolean validForDownload = Boolean.TRUE;
		validForDownload = validForDownload && validateToAndFromDates();
		if (DOWNLOAD_CONTEXT_CLAIM_DATA.equalsIgnoreCase(getContext()) ||
				DOWNLOAD_CONTEXT_CLAIM_PARTS_DATA.equalsIgnoreCase(getContext()) || 
				DOWNLOAD_CONTEXT_CLAIM_DETAIL_DATA.equalsIgnoreCase(getContext()))
		{
			validForDownload = validForDownload && validateSubmitOrCreditOrUpdate();
			validForDownload = validForDownload && validateDealerNumber();
		}
		else if (DOWNLOAD_CONTEXT_RECOVERY_REPORT.equalsIgnoreCase(getContext()))
		{
			validForDownload = validForDownload && validateSubmitOrCreditOrUpdate();
		}
		else if (DOWNLOAD_CONTEXT_CLAIM_FINANCIAL_REPORT.equalsIgnoreCase(getContext()))
		{
			if (getReportSearchBean().getDealerNumber()==null 
					|| "".equals(getReportSearchBean().getDealerNumber()))
			{
				addActionError("error.downloadMgt.dealerNumberRequired");
				validForDownload = false;
			}
		}
		return validForDownload;
	}	

	public ReportSearchBean getReportSearchBean() {
		return reportSearchBean;
	}

	public void setReportSearchBean(ReportSearchBean reportSearchBean) {
		this.reportSearchBean = reportSearchBean;
	}

	public List<DownloadClaimState> getClaimStates() {
		return claimStates;
	}

	public void setClaimStates(List<DownloadClaimState> claimStates) {
		this.claimStates = claimStates;
	}

	public List<String> getDelimiters() {
		return delimiters;
	}

	public void setDelimiters(List<String> delimiters) {
		this.delimiters = delimiters;
	}

	public String getContext() {
		return context;
	}

	public void setContext(String context) {
		this.context = context;
	}

	public String getDisplayContext() {
		return displayContext;
	}

	public void setDisplayContext(String displayContext) {
		this.displayContext = displayContext;
	}

	public WarrantyReport getWarrantyReportGenerator() {
		return warrantyReportGenerator;
	}

	public void setWarrantyReportGenerator(
			WarrantyReportGenerator warrantyReportGenerator) {
		this.warrantyReportGenerator = warrantyReportGenerator;
	}

	public Map<String, String> getAvailableBusinessUnits() {
		return availableBusinessUnits;
	}

	public void setAvailableBusinessUnits(Map<String, String> availableBusinessUnits) {
		this.availableBusinessUnits = availableBusinessUnits;
	}

	public WarrantyReportGeneratorFactory getWarrantyReportGeneratorFactory() {
		return warrantyReportGeneratorFactory;
	}

	public void setWarrantyReportGeneratorFactory(
			WarrantyReportGeneratorFactory warrantyReportGeneratorFactory) {
		this.warrantyReportGeneratorFactory = warrantyReportGeneratorFactory;
	}

	public DataUploadConfig getDataUploadConfig() {
		return dataUploadConfig;
	}

	public void setDataUploadConfig(DataUploadConfig dataUploadConfig) {
		this.dataUploadConfig = dataUploadConfig;
	}

	public String getUserNotes() {
		return userNotes;
	}

	public void setUserNotes(String userNotes) {
		this.userNotes = userNotes;
	}
	
	public RecoveryClaimState[] getRecoveryClaimStates() {
		return RecoveryClaimState.values();
	}
	
	public InventoryReportSearchBean getInventoryReportSearchBean() {
		if(this.reportSearchBean instanceof InventoryReportSearchBean)
			return (InventoryReportSearchBean)reportSearchBean;
		return null;
	}
	
	public void setInventoryReportSearchBean(InventoryReportSearchBean iRSBean) {
		this.reportSearchBean = iRSBean;
	}
	
	//TODO : Remove all below code if not necessary	
//	protected ListCriteria listCriteria;
//	private Integer pageNo=new Integer(0);
//	protected CustomerService customerService;
//	
//	public CustomerService getCustomerService() {
//		return customerService;
//	}
//
//	public void setCustomerService(CustomerService customerService) {
//		this.customerService = customerService;
//	}
//
//	public Integer getPageNo() {
//		return pageNo;
//	}
//
//	public void setPageNo(Integer pageNo) {
//		this.pageNo = pageNo;
//	}
//
//	public ListCriteria getListCriteria() {
//		return listCriteria;
//	}
//
//	public void setListCriteria(ListCriteria listCriteria) {
//		this.listCriteria = listCriteria;
//	}
//	
//	public List<Customer> getCustomers() {
//		return customers;
//	}
//
//	public void setCustomers(List<Customer> customers) {
//		this.customers = customers;
//	}
//
//	public void setListCriteria() {
//		ListCriteria criteria = new ListCriteria();
//		PageSpecification pageSpecification = new PageSpecification();
//		pageSpecification.setPageNumber(this.pageNo.intValue());
//		pageSpecification.setPageSize(10);
//		criteria.setPageSpecification(pageSpecification);
//		this.listCriteria=criteria;
//	}
//	
//	protected AddressBookType getAddressBookType(String addressBookType){
//    	if(AddressBookType.ENDCUSTOMER.getType().equalsIgnoreCase(addressBookType)){
//    		return AddressBookType.ENDCUSTOMER;
//    	}else if(AddressBookType.NATIONALACCOUNT.getType().equalsIgnoreCase(addressBookType)){
//        	return AddressBookType.NATIONALACCOUNT;
//        }else if(AddressBookType.GOVERNMENTACCOUNT.getType().equalsIgnoreCase(addressBookType)){
//        	return AddressBookType.GOVERNMENTACCOUNT;
//        }else if(AddressBookType.INTERCOMPANY.getType().equalsIgnoreCase(addressBookType)){
//        	return AddressBookType.INTERCOMPANY;
//        }else if(AddressBookType.DIRECTCUSTOMER.getType().equalsIgnoreCase(addressBookType)){
//            return AddressBookType.DIRECTCUSTOMER;
//        }else if(AddressBookType.DEALER.getType().equalsIgnoreCase(addressBookType)){
//            return AddressBookType.SELF; //note: SELF type is returned
//        }else if(AddressBookType.DEALERRENTAL.getType().equalsIgnoreCase(addressBookType)){
//        	return AddressBookType.SELF; //note: SELF type is returned
//        }else if(AddressBookType.DEALERUSER.getType().equalsIgnoreCase(addressBookType)){
//        	return AddressBookType.SELF; //note: SELF type is returned
//        }else if(AddressBookType.SELF.getType().equalsIgnoreCase(addressBookType)){
//        	return AddressBookType.SELF;
//       }else {
//        	return null;
//        }
//
//    }

    private String downloadVendorRecoveryReport() {
        PartRecoverySearchCriteria criteria = getCriteria();
        List<RecoveryClaim> list = recoveryClaimService.findRecoveryClaimsForVRRDownload(criteria, 
                downloadPageNumber, dataUploadConfig.getExportRecordsLimit());
        try {
            File tempFile = File.createTempFile("reportData", "csv");
            FileOutputStream os = new FileOutputStream(tempFile);
            warrantyReportGeneratorFactory.getUserReportGenerator(getContext()).exportData(reportSearchBean, os, list);
            //os.close();
            setHeader(this.response, getDataFileName() + ".csv", HeaderUtil.CSV);
            addActionMessage(getText("label.downloadMgt.downloadSuccess"));
            FileInputStream in = new FileInputStream(tempFile);
            FileCopyUtils.copy(in, this.response.getOutputStream());
            in.close();
            tempFile.delete();
        } catch (Exception exception) {
            logger.error("Error in downloading report - " + getContext(), exception);
            String message = exception.getMessage() == null ? 
                    "An error occured while downloading the report." : exception.getMessage();
            addActionError(message);
            return INPUT;
        }
		return null;
    }

    public void setRecoveryClaimService(RecoveryClaimService recoveryClaimService) {
        this.recoveryClaimService = recoveryClaimService;
    }
    
    
}

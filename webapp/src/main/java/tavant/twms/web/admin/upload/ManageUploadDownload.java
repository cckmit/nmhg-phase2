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

package tavant.twms.web.admin.upload;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.util.StringUtils;

import tavant.twms.domain.configuration.ConfigName;
import tavant.twms.domain.configuration.ConfigParamService;
import tavant.twms.domain.orgmodel.Role;
import tavant.twms.domain.upload.controller.UploadManagement;
import tavant.twms.domain.upload.controller.UploadManagementService;
import tavant.twms.domain.upload.staging.FileReceiver;
import tavant.twms.security.SelectedBusinessUnitsHolder;
import tavant.twms.web.TWMSWebConstants;
import tavant.twms.web.i18n.I18nActionSupport;

/**
 * @author jhulfikar.ali
 *
 */
@SuppressWarnings("serial")
public class ManageUploadDownload extends I18nActionSupport implements UploadDownloadConstants {
	
	private static Logger logger = Logger.getLogger(ManageUploadDownload.class);
	
	private UploadManagementService uploadManagementService;
	
	private String context;
	
	private FileReceiver fileReceiver;
	
	private Map<String, String> downloadList;
	
	private Map<String, String> uploadTemplateList;
	private Map<String, String> uploadHistoryList;
	private String selectedBusinessUnit;
	private ConfigParamService configParamService;
	
	public ConfigParamService getConfigParamService() {
		return configParamService;
	}

	public void setConfigParamService(ConfigParamService configParamService) {
		this.configParamService = configParamService;
	}

	public String getSelectedBusinessUnit() {
		return selectedBusinessUnit;
	}

	public void setSelectedBusinessUnit(String businessUnit) {
		this.selectedBusinessUnit = businessUnit;
	}

	public Map<String, String> getUploadHistoryList() {
		return uploadHistoryList;
	}

	public void setUploadHistoryList(Map<String, String> uploadHistoryList) {
		this.uploadHistoryList = uploadHistoryList;
	}

	private String templateName;
	
	private File upload;// The actual file uploaded by user
	
	private String uploadFileName; // The uploaded file name
	
	private String downloadContext;
	
	public String uploadDownload()
	{
		if (CONTEXT_DOWNLOAD.equalsIgnoreCase(getContext()))
		{
			// Adding all available downloads to the list for UI. 
			populateDownloadList();
			return DOWNLOAD_SUCCESS;
		}
		
		// Adding all available upload templates and its path to the list for UI. 
		populateUploadTemplateList();
		if (!CollectionUtils.isNotEmpty(uploadHistoryList.keySet()))
		{
			addActionError(ERROR_UPLOAD_MGT_NO_UPLOAD);
			return INPUT;
		}
		return SUCCESS;
	}

	private void populateUploadTemplateList() {
		// Adding all available upload templates and its path to the list for UI.
		if(getLoggedInUser().getBusinessUnits().size() == 1)
			selectedBusinessUnit = getLoggedInUser().getBusinessUnits().first().getName();
		if (uploadTemplateList==null)
			uploadTemplateList = new HashMap<String, String>(10);
		uploadHistoryList = new HashMap<String, String>(10);
		uploadTemplateList = populateDisplayNameAndPath(uploadManagementService.findAll());
		if(!"Hussmann".equals(getSelectedBusinessUnit()) 
				|| (getLoggedInUser().getBusinessUnits().size() == 1 && !"Hussmann".equals(getLoggedInUser().getBusinessUnits().first().getName())) )
		{
			if(uploadTemplateList.containsKey("./pages/secure/admin/upload/templates/Template-ThirdPartyUpload.xls"))
				uploadTemplateList.remove("./pages/secure/admin/upload/templates/Template-ThirdPartyUpload.xls");
			
		}
	}

	private void populateDownloadList() {
		if (downloadList==null)
			downloadList = new HashMap<String, String>(10);
		// Adding all available downloads to the list for UI. 
		downloadList.put(DOWNLOAD_TYPE_WNTY_CLM_DATA, DOWNLOAD_VALUE_WARRANTY_CLAIMS_DATA);
		downloadList.put(DOWNLOAD_TYPE_WNTY_CLM_PARTS_DATA, DOWNLOAD_VALUE_WARRANTY_CLAIM_PARTS_DATA);
		downloadList.put(DOWNLOAD_TYPE_WNTY_CLM_DETAIL_DATA, DOWNLOAD_VALUE_WARRANTY_CLAIM_DETAIL_DATA);
		if (!isLoggedInUserADealer())
		{
			downloadList.put(DOWNLOAD_TYPE_MACHINE_RETAIL, DOWNLOAD_VALUE_MACHINE_RETAIL);
			//downloadList.put(DOWNLOAD_TYPE_UNDERWRITER_CLAIMS_DATA, DOWNLOAD_VALUE_UNDERWRITER_CLAIMS_DATA);
			//downloadList.put(DOWNLOAD_TYPE_EXT_WNTY_CLAIMS_PARTS_DATA, DOWNLOAD_VALUE_EXTENDED_WARRANTY_CLAIM_PARTS_DATA);
			downloadList.put(DOWNLOAD_TYPE_WNTY_FINANCIAL_REPORT_DATA, DOWNLOAD_VALUE_WARRANTY_FINANCIAL_REPORT_DATA);
		}
		if(getLoggedInUser().hasRole(Role.RECOVERYPROCESSOR) || getLoggedInUser().hasRole(Role.ADMIN)) {
			downloadList.put(DOWNLOAD_TYPE_SUPPLIER_REC_REPORT, DOWNLOAD_VALUE_SUPPLIER_RECOVERY_REPORT);
			downloadList.put(DOWNLOAD_TYPE_SUPPLIER_REC_PARTS_REPORT, DOWNLOAD_VALUE_SUPPLIER_RECOVERY_PARTS_REPORT);
		}
		downloadList.put(DOWNLOAD_TYPE_EWP, DOWNLOAD_VALUE_EWP);
		downloadList.put(DOWNLOAD_TYPE_CUSTOMER_DATA, DOWNLOAD_VALUE_CUSTOMER_DATA);
		
		if(getLoggedInUser().hasRole(Role.REDUCED_COVERAGE_REQUESTS_APPROVER)){
			downloadList.put(DOWNLOAD_TYPE_PENDING_EXTENSIONS, DOWNLOAD_VALUE_PENDING_EXTENSIONS);
		}
	}
	
	private Map<String, String> populateDisplayNameAndPath(List<UploadManagement> allUploadTemplates) {
		if (allUploadTemplates==null)
			return new HashMap<String, String>();
		
		Map<String, String> uploadTemplatePathAndName = new HashMap<String, String>(allUploadTemplates.size());
		Set<Role> userRoles = getLoggedInUser().getRoles();//orgService.getRolesForUser(getLoggedInUser());
		boolean isInternalUser = getLoggedInUser().isInternalUser();
		for (Iterator<UploadManagement> iterator = allUploadTemplates.iterator(); iterator.hasNext();) {
			UploadManagement uploadTemplate = (UploadManagement) iterator.next();
			String nameOfTemplate = uploadTemplate.getNameOfTemplate();
			if("draftWarrantyClaims".equals(nameOfTemplate) && isInternalUser) {
				uploadHistoryList.put(uploadTemplate.getTemplatePath(), 
						uploadTemplate.getNameToDisplay());
			}
			else if (CollectionUtils.containsAny(uploadTemplate.getRoles(), userRoles)) {			
				uploadTemplatePathAndName.put(uploadTemplate.getTemplatePath(), 
						uploadTemplate.getNameToDisplay());
				uploadHistoryList.put(uploadTemplate.getTemplatePath(), 
						uploadTemplate.getNameToDisplay());
			}
		}
		if(!"Hussmann".equals(getSelectedBusinessUnit()) 
				|| (getLoggedInUser().getBusinessUnits().size() == 1 && !"Hussmann".equals(getLoggedInUser().getBusinessUnits().first().getName())) )
		{
			if(uploadHistoryList.containsKey("./pages/secure/admin/upload/templates/Template-ThirdPartyUpload.xls"))
				uploadHistoryList.remove("./pages/secure/admin/upload/templates/Template-ThirdPartyUpload.xls");
			
		}
		
		return uploadTemplatePathAndName;
	}

	public String downloadData()
	{
		populateDownloadList();
		if (StringUtils.hasText(getDownloadContext()))
			return getDownloadContext();
		return SUCCESS; 
	}
	
	public String uploadTemplateData()
	{
		try {
			
			if(!validateUpload())
			{
				populateUploadTemplateList();
				return INPUT;
			}
			SelectedBusinessUnitsHolder.setSelectedBusinessUnit(this.getSelectedBusinessUnit());
			fileReceiver.persistFileContents(this.upload, getTemplateName(), uploadFileName, 
					SelectedBusinessUnitsHolder.getSelectedBusinessUnit(), 
								getLoggedInUser().getId());
			addActionMessage(getText(MESSAGE_DOCUMENT_UPLOAD_SUCCESSFUL));
		}
		catch(IOException e){
			logger.error("Invalid input. Please upload the template and proceed.", e);
		}
		catch (Exception ex) {
			logger.error("Exception in ManageUploadDownload.uploadTemplateData(): " + ex.getMessage(), ex);
		}
		populateUploadTemplateList();
		return SUCCESS;
	}
	
	public boolean validateUpload() {
		try {
			if (HEADER_VALUE_OF_TEMPLATE_TYPE.equalsIgnoreCase(getTemplateName()))
			{
				addActionError(ERROR_UPLOAD_MGT_SELECT_TYPE_OF_UPLOAD);
				throw new Exception();
			}
			if (HEADER_VALUE_OF_BUSINESS_UNIT.equalsIgnoreCase(getSelectedBusinessUnit()))
			{
				addActionError(ERROR_UPLOAD_MGT_SELECT_BUSINESS_UNIT);
				throw new Exception();
			}
			if (this.upload == null) {
				throw new IOException();
			}

			String ext = (this.uploadFileName.lastIndexOf(".") == -1) ? ""
					: this.uploadFileName.substring(
									this.uploadFileName.lastIndexOf(".") + 1,
									this.uploadFileName.length());

			if (!EXCEL_FILE_EXTENSION.equalsIgnoreCase(ext)) {
				addActionError(ERROR_UPLOAD_MGT_INVALID_FILE_EXTENSION);
				throw new IOException();
			}

		} catch (Exception e) {
			logger.error("Invalid input. Please download the template and proceed.", e);
			addActionError(ERROR_UPLOAD_MGT_INVALID_UPLOAD);
			return false;
		}
		return true;
	}

	public String getContext() {
		return context;
	}

	public void setContext(String context) {
		this.context = context;
	}

	public Map<String, String> getDownloadList() {
		return downloadList;
	}

	public void setDownloadList(Map<String, String> downloadList) {
		this.downloadList = downloadList;
	}

	public Map<String, String> getUploadTemplateList() {
		return uploadTemplateList;
	}

	public void setUploadTemplateList(Map<String, String> uploadTemplateList) {
		this.uploadTemplateList = uploadTemplateList;
	}

	public FileReceiver getFileReceiver() {
		return fileReceiver;
	}

	public void setFileReceiver(FileReceiver fileReceiver) {
		this.fileReceiver = fileReceiver;
	}

	public String getTemplateName() {
		return templateName;
	}

	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}

	public File getUpload() {
		return upload;
	}

	public void setUpload(File upload) {
		this.upload = upload;
	}

	public String getUploadFileName() {
		return uploadFileName;
	}

	public void setUploadFileName(String uploadFileName) {
		this.uploadFileName = uploadFileName;
	}

	public UploadManagementService getUploadManagementService() {
		return uploadManagementService;
	}

	public void setUploadManagementService(
			UploadManagementService uploadManagementService) {
		this.uploadManagementService = uploadManagementService;
	}

	public String getDownloadContext() {
		return downloadContext;
	}

	public void setDownloadContext(String downloadContext) {
		this.downloadContext = downloadContext;
	}

	public boolean canSupplierUploadDecision() {
/*		if (getLoggedInUser().hasRole("supplier")
				&& getConfigParamService()
						.getBooleanValue(
								ConfigName.CAN_SUPPLIER_UPLOAD_DECISION_THROUGH_TEMPLATE
										.getName()))
			return true;
		else*/
			return false;
	}
}

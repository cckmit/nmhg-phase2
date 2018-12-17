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
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Blob;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.util.FileCopyUtils;

import tavant.twms.domain.orgmodel.User;
import tavant.twms.domain.upload.controller.UploadManagementService;
import tavant.twms.domain.upload.controller.UploadStatusDetail;
import tavant.twms.domain.upload.controller.UserFileUpload;
import tavant.twms.infra.BeanProvider;
import tavant.twms.infra.ListCriteria;
import tavant.twms.infra.PageResult;
import tavant.twms.security.SelectedBusinessUnitsHolder;
import tavant.twms.web.inbox.DefaultPropertyResolver;
import tavant.twms.web.inbox.SummaryTableAction;
import tavant.twms.web.inbox.SummaryTableColumn;
import tavant.twms.web.inbox.SummaryTableColumnOptions;

/**
 * @author jhulfikar.ali
 *
 */
@SuppressWarnings("serial")
public class ManageUploadHistory extends SummaryTableAction {

	private static Logger logger = Logger.getLogger(ManageUploadHistory.class); 
	
	// Context - which specifies upload history's context
	private String context;
	
	private UploadManagementService uploadManagementService;
	
	private UserFileUpload userFileUpload;
	
	private HttpServletResponse servletResponse;
	
	@Override
	protected PageResult<UserFileUpload> getBody() {
		User user = getLoggedInUser();
		if(user.isInternalUser()){
			return uploadManagementService.findAllUploadHistory(getCriteria(), context);
		}	
		return uploadManagementService.findAllUploadHistory(getCriteria(), context, user.getId());
	}

	@Override
	protected List<SummaryTableColumn> getHeader() {
		List<SummaryTableColumn> tableHeadData = new ArrayList<SummaryTableColumn>(5);
		tableHeadData.add(new SummaryTableColumn("",
				"id", 0, "number", "id", false, true, true, false));
		tableHeadData.add(new SummaryTableColumn("columnTitle.userUpload.fileName",
				"fileName", 35, "string", "fileName", true, false, false, false));
		tableHeadData.add(new SummaryTableColumn("columnTitle.userUpload.uploadStatus",
				"uploadStatusDisplay", 15, "string", "uploadStatusDisplay", SummaryTableColumnOptions.NO_SORT_NO_FILTER_COL));
		tableHeadData.add(new SummaryTableColumn("columnTitle.userUpload.totalRecords",
				"totalRecords", 10, "Number", "totalRecords", false, false, false, false));
		tableHeadData.add(new SummaryTableColumn("columnTitle.userUpload.successRecords",
				"successRecords", 10, "Number", "successRecords", false, false, false, false));
		tableHeadData.add(new SummaryTableColumn("columnTitle.userUpload.errorRecords",
				"errorRecords", 10, "Number", "errorRecords", false, false, false, false));
		tableHeadData.add(new SummaryTableColumn("columnTitle.userUpload.receivedOn",
				"receivedOn", 10, "Date", "receivedOn", false, false, false, false));
		//tableHeadData.add(new SummaryTableColumn("columnTitle.userUpload.errorMessage",
		//		"errorMessage", 30, "string", "errorMessage", true, false, false, false));
		User user = getLoggedInUser();
		if(user.isInternalUser()) {
//                    Modified to fix TWMS4.3-710
			tableHeadData.add(new SummaryTableColumn("columnTitle.userUpload.uploadedBy",
					"uploadedBy.lastName", 10, "string", "uploadedBy.lastName", false, false, false, false));
		}
		
		return tableHeadData;
	}

	@Override
	protected BeanProvider getBeanProvider() {
		return new DefaultPropertyResolver() {
			public Object getProperty(String propertyPath, Object root) {
				Object value = null;
				if ("uploadStatusDisplay".equals(propertyPath)) {
					Integer uploadStatus = (Integer) super.getProperty("uploadStatus", root);
					value = getText(UploadStatusDetail.getUploadStatusText(uploadStatus));
					return value;
				} 
				else if("uploadedBy.lastName".equals(propertyPath)){
                     value = super.getProperty("uploadedBy.completeNameAndLogin", root);
					return value;
				}
				else {
					return super.getProperty(propertyPath, root);
				}
			}
		};
	}

	public String detail()
	{
		SelectedBusinessUnitsHolder.clearChosenBusinessUnitFilter();
		userFileUpload = uploadManagementService.findUserFileUploadById(new Long(getId()));
		return SUCCESS;
	}

    public void downloadErrorContent() {
    	try {
	    	UserFileUpload userUpload = uploadManagementService.findUserFileUploadById(new Long(getId()));
	    	
	    	String name = userUpload.getFileName();
	    	int idx = name.lastIndexOf(".");
	    	String ext = idx > 0 ? name.substring(idx) : "";
	    	name = idx>0 ? name.substring(0, idx) : name;
 	    	int errorIdx = 0;
    		Pattern p = Pattern.compile("_error_([0-9]+)");
            Matcher m = p.matcher(name);
            if (m.find()) {
            	errorIdx = new Integer(m.group(1));
            	name = name.substring(0,name.lastIndexOf("_error_"));
            }

            
	        String fileName = new StringBuffer(name)
	        		.append("_Error_").append(errorIdx+1)
	        		.append(ext).toString();
	        File errorFile = uploadManagementService.getErrorFileById(userUpload.getId());
	        streamFile(errorFile, fileName);
        } catch (Exception exception) {
            logger.error("Failed to write file to output stream", exception);
        }
    }

    public void downloadUploadedContent() {
    	try {
	    	UserFileUpload userUpload = uploadManagementService.findUserFileUploadById(new Long(getId()));
	        File uploadFile = uploadManagementService.getUploadedFileById(userUpload.getId());
	        streamFile(uploadFile, userUpload.getFileName());
        } catch (Exception exception) {
            logger.error("Failed to write file to output stream", exception);
        }
    }
    
    private void streamFile(File file, String fileName) {
        servletResponse.setContentType("excel/ms-excel");
        servletResponse.setHeader("Content-disposition", "attachment; filename=" + fileName.replaceAll(" ", ""));
        try {
        	InputStream in = new FileInputStream(file);
            FileCopyUtils.copy(in, servletResponse.getOutputStream());
            in.close();
        } catch (Exception e) {
            logger.error("Failed to write file to output stream", e);
        }
    }
    
    private void streamFile(Blob blob, String fileName) {
        servletResponse.setContentType("excel/ms-excel");
        servletResponse.setHeader("Content-disposition", "attachment; filename=" + fileName.replaceAll(" ", ""));
        try {
            FileCopyUtils.copy(blob.getBinaryStream(), servletResponse.getOutputStream());
        } catch (Exception e) {
            logger.error("Failed to write file to output stream", e);
        }
    }

	public String getContext() {
		return context;
	}

	public void setContext(String context) {
		this.context = context;
	}

	public UploadManagementService getUploadManagementService() {
		return uploadManagementService;
	}

	public void setUploadManagementService(
			UploadManagementService uploadManagementService) {
		this.uploadManagementService = uploadManagementService;
	}

	public UserFileUpload getUserFileUpload() {
		return userFileUpload;
	}

	public void setUserFileUpload(UserFileUpload userFileUpload) {
		this.userFileUpload = userFileUpload;
	}

	public HttpServletResponse getServletResponse() {
		return servletResponse;
	}

	public void setServletResponse(HttpServletResponse servletResponse) {
		this.servletResponse = servletResponse;
	}

    @Override
        protected ListCriteria getListCriteria() {
            return new ListCriteria(){
            @Override
                protected boolean isNumberProperty(String propertyExpression){
                    return ("totalRecords".equals(propertyExpression) ||
                            "successRecords".equals(propertyExpression) ||
                            "errorRecords".equals(propertyExpression)) ||
                            "uploadedBy.id".equals(propertyExpression);
                }

            @Override
                protected boolean isDateProperty(String propertyExpression) {
                    return "receivedOn".equals(propertyExpression);
                }

            };
        }
	
}
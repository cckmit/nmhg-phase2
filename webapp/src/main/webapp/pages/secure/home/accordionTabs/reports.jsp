<%--

   Copyright (c)2006 Tavant Technologies
   All Rights Reserved.

   This software is furnished under a license and may be used and copied
   only  in  accordance  with  the  terms  of such  license and with the
   inclusion of the above copyright notice. This software or  any  other
   copies thereof may not be provided or otherwise made available to any
   other person. No title to and ownership of  the  software  is  hereby
   transferred.

   The information in this software is subject to change without  notice
   and  should  not be  construed as a commitment  by Tavant Technologies.

--%>


<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="t" uri="twms"%><%@taglib prefix="u" uri="/ui-ext"%>
<%@taglib prefix="authz" uri="authz"%>
<%
    response.setHeader("Pragma", "no-cache");
    response.addHeader("Cache-Control", "must-revalidate");
    response.addHeader("Cache-Control", "no-cache");
    response.addHeader("Cache-Control", "no-store");
    response.setDateHeader("Expires", 0);
%>
<div dojoType="dijit.layout.ContentPane" id="reports"
        title="<s:text name="accordion_jsp.accordionPane.reports"/>"
        iconClass="reports" >
<div dojoType="dijit.layout.ContentPane" layoutAlign="client">
    <ol>
      <!--<authz:ifAdmin>	
        <u:openTab decendentOf="%{getText('home_jsp.tabs.home')}"
            id="claimreports" tagType="li" cssClass="reports_folder folder"
            tabLabel="%{getText('accordion_jsp.accordionPane.report_claims')}"
            url="claimReports.action?showReport=false" catagory="reports">
            <s:text name="accordion_jsp.accordionPane.claims" />
        </u:openTab>
        <u:openTab decendentOf="%{getText('home_jsp.tabs.home')}"
            id="partreturnsreports" tagType="li" cssClass="reports_folder folder"
            tabLabel="%{getText('accordion_jsp.accordionPane.report_partReturns')}"
            url="partReturnsReports.action?showReport=false" catagory="reports">
            <s:text name="accordion_jsp.accordionPane.partReturns" />
        </u:openTab>
        <u:openTab decendentOf="%{getText('home_jsp.tabs.home')}"
            id="supplierreports" tagType="li" cssClass="reports_folder folder"
            tabLabel="%{getText('accordion_jsp.accordionPane.report_supplier')}"
            url="supplierRecoveryReports.action?showReport=false" catagory="reports">
            <s:text name="accordion_jsp.accordionPane.supplier" />
        </u:openTab> 
        
        </authz:ifAdmin>

	    --><authz:ifPermitted resource="reportsVendorRecoveryReport">	
			<u:openTab decendentOf="%{getText('home_jsp.tabs.home')}"
					id="vendor_recovery_extract" tagType="li" cssClass="reports_folder folder"
					tabLabel="%{getText('label.title.recovery.extract')}"
					url="displatExtractFilterAction.action" catagory="reports" helpCategory="Reports/Vendor_Recovery_Report.htm">
					<s:text name="label.title.recovery.extract" />
			</u:openTab>
		</authz:ifPermitted>

        <!-- Manage Upload/Download Accordion -->
        <authz:ifPermitted resource="reportsManageUploads/Downloads">
        <u:fold label="%{getText('accordionLabel.manageUploadDownload')}"
            id="manage_uploads_downloads" tagType="li" cssClass="uFoldStyle folder"
            foldableClass="foldableUploadDownload" />
            <authz:ifPermitted resource="reportsUploadManagement">
	        <u:openTab decendentOf="%{getText('home_jsp.tabs.home')}"
	            id="admin_manage_uploads" tagType="li" cssClass="reports_folder folder foldableUploadDownload sublist"
	            tabLabel="%{getText('title.manageUploadDownload.upload')}"
	            url="manageUploadDownload.action?context=UPLOAD" catagory="admin" helpCategory="Reports/Upload_Management.htm">
	            <s:text name="accordionLabel.manageUploadDownload.upload" />
	        </u:openTab>
	        </authz:ifPermitted>
	        <authz:ifPermitted resource="reportsDownloadManagement">
	        <authz:ifUserNotInRole roles="supplier">
	        <u:openTab decendentOf="%{getText('home_jsp.tabs.home')}"
	            id="admin_manage_downloads" tagType="li" cssClass="reports_folder folder foldableUploadDownload sublist"
	            tabLabel="%{getText('title.manageUploadDownload.download')}"
	            url="manageUploadDownload.action?context=DOWNLOAD" catagory="admin" helpCategory="Reports/Download_Management.htm">
	            <s:text name="accordionLabel.manageUploadDownload.download" />
	        </u:openTab>
	        </authz:ifUserNotInRole>
	        </authz:ifPermitted>
	      </authz:ifPermitted>
    </ol>
</div>
</div>
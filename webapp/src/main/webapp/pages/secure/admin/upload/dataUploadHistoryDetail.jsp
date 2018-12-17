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
<%--
 @author: Jhulfikar Ali
--%>


<%@ page contentType="text/html"%>
<%@ page pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="u" uri="/ui-ext"%>

<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<s:head theme="twms" />
<title><s:text name="title.userUpload.uploadHistory"/></title>
<u:stylePicker fileName="yui/reset.css" common="true" />
<u:stylePicker fileName="layout.css" common="true" />
<u:stylePicker fileName="common.css" />
<u:stylePicker fileName="form.css" />
<u:stylePicker fileName="adminPayment.css" />
<u:stylePicker fileName="base.css" />
    <script type="text/javascript">
  	  	dojo.require("dijit.layout.ContentPane");
      	dojo.require("dijit.layout.LayoutContainer"); 
	</script>
	<style>
	.grid td{
	padding-bottom:10px;
	}
	</style>
</head>

<u:body>

<div dojoType="dijit.layout.LayoutContainer"
	style="width: 100%; height: 100%; background: white; overflow-y: auto;">
<div dojoType="dijit.layout.ContentPane" layoutAlign="client">
<u:actionResults />
<div class="policyRegn_section_div">
<div class="admin_section_heading">
   <s:text name="title.userUpload.uploadHistory"/> (<s:property value="context" />)
</div>
<s:hidden name="context" />
 <div class="mainTitle" style="margin-top:5px;">
	    <s:text name="title.userUpload.uploadHistory"/>
 </div>
<div class="borderTable">&nbsp;</div>
		<table  width="98%" cellspacing="0" cellpadding="0" class="grid" style="margin-top:-10px;">
	
       	<tr class="errorMessage">
	      	<td colspan="4"><u:actionResults /></td>
      	</tr>
	    <tr width="100%">
	    	<td class="labelBold" width="20%" nowrap="nowrap"> <s:text name="label.userUpload.fileName" />:</td>
	    	<td class="label" width="35%"><s:property value="userFileUpload.fileName" /></td>
	    	<td class="labelBold" width="20%" nowrap="nowrap"><s:text name="label.userUpload.totalRecords" />:</td>
			<td class="label"><s:property value="userFileUpload.totalRecords" /></td>
	    </tr>
	    <tr width="100%">
	    	<td class="labelBold" nowrap="nowrap"><s:text name="label.userUpload.receivedOn" />:</td>
	    	<td class="label"><s:property value="userFileUpload.receivedOn" /></td>
			<td class="labelBold" nowrap="nowrap"><s:text name="label.userUpload.successRecords" />:</td>
			<td class="label"><s:property value="userFileUpload.successRecords" /></td>
    	</tr>
	    <tr width="100%">
	    	<td class="labelBold" nowrap="nowrap"><s:text name="label.userUpload.uploadStatus" />:</td>
			<td class="label">
				<s:property value="%{getText(userFileUpload.uploadStatusText)}"/>
			</td>
			<td class="labelBold" nowrap="nowrap"><s:text name="label.userUpload.errorRecords" />:</td>
			<td class="label"><s:property value="userFileUpload.errorRecords" /></td>
		</tr>
		<s:if test="getLoggedInUser().getBusinessUnits().size() > 1">
			<tr width="100%">
		    	<td class="labelBold" nowrap="nowrap"><s:text name="label.userUpload.businessUnit" />:</td>
		    	<td class="label"><s:property value="userFileUpload.businessUnitInfo" /></td>
		    	<td colspan="2">&nbsp;</td>
		    </tr>
	    </s:if>
	    <s:if test="getLoggedInUser().isInternalUser()">
		    <tr width="100%">
				<td class="labelBold" nowrap="nowrap"><s:text name="label.userUpload.uploadedBy" />:</td>
				<td class="label"><s:property value="userFileUpload.uploadedBy.name" /></td>
				<td colspan="2">&nbsp;</td>
	    	</tr>
    	</s:if>
    	<s:if test="userFileUpload.errorMessage!=null && userFileUpload.errorMessage!=''">
		    <tr width="100%">
				<td class="labelBold" nowrap="nowrap"><s:text name="label.userUpload.errorMessage" />:</td>
				<td class="label"><s:property value="userFileUpload.errorMessage" /></td>
				<td colspan="2">&nbsp;</td>
	    	</tr>
    	</s:if>
    	<tr width="100%">
			<td class="label" colspan="2" style="padding-top:10px;">
				<s:form name="downloadUploadHistUploadContent">
		             <s:url action="download_hist_uploaded_content_file" id="url">
		               <s:param name="id" value="%{id}"/>
		             </s:url>
		             <a href="<s:property value="#url" escape="false"/>">
		             <s:text name="link.userUpload.downloadUploadedFile"/> </a>
		        </s:form>
			</td>
	    	<s:if test="userFileUpload.errorRecords>0 && userFileUpload.errorReportGenerated">
					<td class="label" colspan="2">
						<s:form name="downloadUploadHistErrorContent">
				             <s:url action="download_hist_error_content_file" id="url">
				               <s:param name="id" value="%{id}"/>
				             </s:url>
				             <a href="<s:property value="#url" escape="false"/>">
				             <s:text name="link.userUpload.downloadErrorFile"/> </a>
				        </s:form>
					</td>
			</s:if>
			</tr>	
			
		</table>
</div>
</div>
</div>
</u:body>
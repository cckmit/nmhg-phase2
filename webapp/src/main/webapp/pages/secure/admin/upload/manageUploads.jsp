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

<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="t" uri="twms"%>
<%@ taglib prefix="u" uri="/ui-ext" %>
<%@ taglib prefix="authz" uri="authz" %>

<%@ include file="/i18N_javascript_vars.jsp" %>
<u:stylePicker fileName="base.css"/>
<u:stylePicker fileName="common.css"/>
<u:stylePicker fileName="adminPayment.css"/>
<html>
<title>::<s:text name="title.common.warranty" />::</title>
<s:head theme="twms" />
<form name="baseForm" id="baseForm" enctype="multipart/form-data" method="post">
          <s:if test="uploadHistoryList!=null && !uploadHistoryList.isEmpty()">
	       <div class="policy_section_div">
	       <div class="section_header">
	     	    	&nbsp;<s:text name="label.uploadMgt.downloadTemplate"/>
	        </div>
	      <table class="grid" style="width:100%" cellspacing="0" cellpadding="0" >
                
          <tr>
          	<td colspan="2" class="labelBold,errorMessage"><u:actionResults /></td>
	      </tr>
		         <tr>
			      	<td colspan="2" class="labelNormal">	
			      		&nbsp;<s:text name="label.uploadMgt.downloadTemplateNote" />
			      	</td>
			      </tr>
		        <s:iterator status="stat" value="uploadHistoryList">
		        <tr>
		        	<td class="labelBold" style="padding-top:10px;"> &nbsp;&nbsp;
		        	  <s:url id="url" includeParams="none" /> 
			          <s:a href="%{key}"> <s:text name="%{value}" /> </s:a>
			        </td>
			        <s:if test="canSupplierUploadDecision() || !getLoggedInUser().hasRole('supplier')">
		        	<td class="labelBold" style="padding-top:10px;" style="color:#0000FF;cursor:pointer"> &nbsp;&nbsp;
				        <u:openTab autoPickDecendentOf="true"  
			            id="datUploadHistory_%{#stat.index}" 
			            tabLabel="Upload History %{value}"
			            url="dataUploadHistory.action?context=%{value}">
							<s:text name="label.uploadMgt.viewUploadHistory" />
				        </u:openTab>
			        </td>
			        </s:if>
			      </tr>
		        </s:iterator>
		        </table>
		     </div>
		     </s:if>
		     <s:if test="canSupplierUploadDecision() || !getLoggedInUser().hasRole('supplier')">
		     <div id="seperator"></div>
		        <s:if test="uploadTemplateList!=null && !uploadTemplateList.isEmpty()">
		           <div class="policy_section_div">
		           <div class="section_header" >
		       	    	&nbsp;<s:text name="label.uploadMgt.attachTemplate"/>
				        </div>
		        <table class="grid" style="width:100%" cellspacing="0" cellpadding="0">
			     
		       	    	
			
		         <s:if test="getLoggedInUser().getBusinessUnits().size() > 1">
		          <tr>
		        	<!-- Uploading file dialogue area -->
		        	<td class="labelBold" style="padding-top:10px;">
			          	&nbsp;<s:text name="label.uploadMgt.selectBusinessUnit" />
			        </td>
			        <td class="labelBold" style="padding-top:10px;"> 
			          	<s:select name="selectedBusinessUnit" list="getLoggedInUser().getBusinessUnits()"
			          		headerKey="-- Select Business Unit --" headerValue="-- Select Business Unit --"
			          		listKey="name" listValue="displayName" />
					</td>
				  </tr>
		         </s:if>
		         <s:else>
		         	<s:hidden name="selectedBusinessUnit" />
		         </s:else>
		         <tr>
		        	<!-- Uploading file dialogue area -->
		        	<td  class="labelBold"  style="padding-top:10px;">
			          	&nbsp;<s:text name="label.uploadMgt.selectTypeAndFile" />
			        </td>
			        <td  class="labelBold"  style="padding-top:10px;"> 
		        		<%-- <!-- Note: Please refer/change to ManageUploadDownload's constant
		        		before changing the value of "-- Select type of Upload --"--> --%>
			          	<s:select name="templateName" list="uploadTemplateList"
			          		headerKey="-- Select type of Upload --" headerValue="-- Select type of Upload --"
			          		listKey="value" listValue="value" />
			          	<s:file id="uploadFile" name="upload" /> 
 
					</td>
				  </tr>
				  </table>
        </div>
         </s:if>
        <div id="seperator"></div>
        <div align="center" class="spacingAtTop">
        
		    			&nbsp;<s:submit cssClass="buttonGeneric" value="%{getText('button.common.upload')}" 
		    				type="input" id="uploadData" action="uploadTemplateData" />
		    		
        </div>
	          	<script>
		          	dojo.addOnLoad(function() {
		          		dojo.connect(dojo.byId("uploadFile"), "onChange", function()
		          		{
							if (dojo.byId("uploadFile").value=="")
								dojo.byId("uploadData").disabled = true;
							else
								dojo.byId("uploadData").disabled = false;
		          		});
		          		
		          	});
				</script>
</s:if>
</form>
<authz:ifPermitted resource="reportsUploadManagementReadOnlyView">
	<script type="text/javascript">
	    dojo.addOnLoad(function() {
	        for ( var i = 0; i < dojo.query("input, button, textarea, select, text", dojo.byId('baseForm')).length; i++) {
	            dojo.query("input, button, textarea, select, text", dojo.byId('baseForm'))[i].disabled=true;
	        }
	    });
	</script>
</authz:ifPermitted>
</html>
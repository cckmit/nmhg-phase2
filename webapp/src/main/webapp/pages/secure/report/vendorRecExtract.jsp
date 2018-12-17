<%--

   Copyright (c)2006 Tavant Technologies All Rights Reserved.

   This software is furnished under a license and may be used and copied
   only  in  accordance  with  the  terms  of such  license and with the
   inclusion of the above copyright notice. This software or  any  other
   copies thereof may not be provided or otherwise made available to any
   other person. No title to and ownership of  the  software  is  hereby
   transferred.

   The information in this software is subject to change without  notice
   and  should  not be  construed as a commitment  by Tavant Technologies.

--%>
<%@taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="sd" uri="/struts-dojo-tags"%>
<%@taglib prefix="u" uri="/ui-ext" %>
<%@ taglib prefix="authz" uri="authz" %>
<html>
<head>
<u:stylePicker fileName="base.css"/>
<u:stylePicker fileName="adminPayment.css"/>
<s:head theme="twms"/>
</head>

<u:body>
<s:form name="baseForm" id="baseForm">
<s:hidden name="downloadMaxResults" value="true"/>
<s:hidden name="downloadReport" value="true"/>
<s:hidden name="downloadPageNumber" id="downloadPageNumber" value=""/>

<script type="text/javascript">
    dojo.addOnLoad(function() {
        dijit.byId("from_date").onChange = function(){
            if(dojo.byId('report_pages'))
            	dojo.html.hide(dojo.byId('report_pages'));
        }
        dijit.byId("to_date").onChange = function(){
            if(dojo.byId('report_pages'))
            	dojo.html.hide(dojo.byId('report_pages'));
        }
        dojo.connect(dojo.byId("recovery_state"), "onchange", function(){
            if(dojo.byId('report_pages'))
            	dojo.html.hide(dojo.byId('report_pages'));
        });
    });
</script>

<s:hidden name="context" />
  <div class="admin_section_div" style="margin:5px 0px 5px 5px;;width:102%">
    <div class="admin_section_heading"><s:text name="label.title.recovery.extract"/></div>
    <table  width="100%" cellspacing="0" cellpadding="0" class="grid">
      
      <tr><td class="errorMessage" colspan="5"><u:actionResults /></td></tr>
      <tr>
    	<td class="labelBold" width="13%" nowrap="nowrap" colspan="5">
    	<s:text name="column.title.recovery.extract.createdOn" />:</td>
    	
      </tr>
     <tr>
     <td width="13%">&nbsp;</td>
     <td class="labelBold" style="width:10%" nowrap="nowrap"><s:text name="label.downloadMgt.fromDate" /></td>
      	<td width="35%"><sd:datetimepicker id='from_date' name='reportSearchBean.fromDate' value='%{reportSearchBean.fromDate}' /></td>
      	<td colspan="2">&nbsp;
      	</td>
     </tr>
     <tr>
     <td width="13%">&nbsp;</td>
     <td class="labelBold" width="10%" nowrap="nowrap"><s:text name="label.downloadMgt.toDate" /></td>
      	<td><sd:datetimepicker id='to_date' name='reportSearchBean.toDate' value='%{reportSearchBean.toDate}' /></td>
     	<td colspan="2">&nbsp;
      	</td>
     </tr>
      <tr>
    	<td class="labelBold" colspan="5"> <s:text name="label.downloadMgt.claimStatus" />
    	<s:select id="recovery_state" name="reportSearchBean.claimStatus" list="recoveryClaimStates" headerKey="All" headerValue="All"
          		listValue="state" />
          		
      </tr>
      
 
    
      </table>
      
  </div>
<div class="spacingAtTop" align="center">
			<s:submit cssClass="buttonGeneric" value="%{getText('button.common.submit')}" 
				type="input" id="downloadVendorRecoveryExtract" action="vendorRecoveryExtractAction" />
			</div>
	<s:if test="bodySize != null">
	<div id="report_pages" class="admin_section_div" style="margin:5px 0px 5px 5px;;width:102%">
    <div class="admin_section_heading"><s:text name="Download"/></div>
		<s:if test="bodySize == 0">
		<table width="100%" cellspacing="0" cellpadding="0" class="grid">
		<tr align="center"><td>No results found</td></tr>
		</table>
		
		</s:if>
		<s:else>
		<script>
			function downloadPage(pageNumber) {
				dojo.byId('downloadPageNumber').value=pageNumber;
				var form = dojo.byId("baseForm");
                form.action = "downloadVendorRecoveryExtractAction.action";
                form.submit();
			}
		</script>
		<table width="100%" cellspacing="0" cellpadding="0" class="grid">
		<tr align="right"><td>Totals Records <s:property value="bodySize"/></td></tr>
		<s:bean name="org.apache.struts2.util.Counter"  id="pgCounter"> 
		  <s:param name="last" value="%{(bodySize-1)/reportMaxDownloadableRows+1}" /> 
		</s:bean> 
		
		<s:iterator value="#pgCounter">
			<tr><td>
			<s:if test="top == #pgCounter.last">
				<s:a onclick="downloadPage(%{top}-1)" href="#">
					<s:property value="(top-1) * reportMaxDownloadableRows + 1"/> to <s:property value="bodySize"/>
				</s:a>
			</s:if>
			<s:else>
			<s:a onclick="downloadPage(%{top}-1)" href="#">
				<s:property value="(top-1) * reportMaxDownloadableRows + 1"/> to <s:property value="top * reportMaxDownloadableRows"/>
			</s:a>
			</s:else>
			</td></tr>
		</s:iterator>
		</table>
		</s:else>
	</div>
	</s:if>

</s:form>
<authz:ifPermitted resource="reportsVendorRecoveryReportReadOnlyView">
	<script type="text/javascript">
	    dojo.addOnLoad(function() {
	        for ( var i = 0; i < dojo.query("input, button, textarea, select, text", dojo.byId('baseForm')).length; i++) {
	            dojo.query("input, button, textarea, select, text", dojo.byId('baseForm'))[i].disabled=true;
	        }
	    });
	</script>
</authz:ifPermitted>
</u:body>
</html>
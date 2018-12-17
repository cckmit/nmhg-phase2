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
<%@page pageEncoding="UTF-8"%>
<%@ taglib prefix="sd" uri="/struts-dojo-tags"%>
<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="u" uri="/ui-ext" %>
<%@taglib prefix="authz" uri="authz"%>
<head>
<u:stylePicker fileName="base.css"/>
<u:stylePicker fileName="adminPayment.css"/>
<s:head theme="twms"/>
</head>

	<script type="text/javascript">  
      	dojo.require("twms.widget.Dialog");
      	dojo.addOnLoad(function(){
          	<s:if test="resultSize >= 0">
          	dijit.byId("downloadDlg").show();
          	</s:if>
        });
    </script>
<u:body>
<s:form name="baseForm" id="baseForm">
<s:hidden name="context" />
<s:hidden name="downloadPageNumber" id="downloadPageNumber"/>
  <div class="admin_section_div" >
    
    <table width="100%" cellspacing="0" cellpadding="0">
      <tr width="100%">
       	<td colspan="2"><div class="admin_section_heading"><s:text name="label.downloadMgt.manageDownloads"/></div></td>
      </tr> 
      <tr><td class="errorMessage" colspan="2"><u:actionResults /></td></tr>
      <tr rowspan="3"><td colspan="2">&nbsp;</td></tr>
      <tr width="100%">
      	<td colspan="2"><div class="admin_section_subheading">
      	<s:property value="displayContext"/></div></td>
      </tr> 
      
      <tr rowspan="3"><td colspan="2">&nbsp;</td></tr>
      <tr>
      	<td class="labelBold"> &nbsp;&nbsp;<s:text name="label.downloadMgt.fromDate" /></td>
      	<td><sd:datetimepicker name='reportSearchBean.fromDate' value='%{reportSearchBean.fromDate}' /></td>
      </tr>
      <tr><td colspan="2">&nbsp;</td></tr>
      <tr>
      	<td class="labelBold"> &nbsp;&nbsp;<s:text name="label.downloadMgt.toDate" /></td>
      	<td><sd:datetimepicker name='reportSearchBean.toDate' value='%{reportSearchBean.toDate}' /></td>
      </tr>
      <tr rowspan="2"><td colspan="2">&nbsp;</td></tr>
      <tr>
    	<td class="labelBold" colspan="2">&nbsp;&nbsp;
    		<s:radio name="reportSearchBean.submitOrCreditOrUpdateDate" list="#{'submitDate':'Submit Date&nbsp;&nbsp;&nbsp;', 'updateDate':'Update Date'}" listKey="key"
			listValue="value" theme="twms" value="'submitDate'" />
		</td>
	  </tr>
	  
	  <s:if test="availableBusinessUnits != null && availableBusinessUnits.size() == 1">
	  	<s:hidden name="reportSearchBean.businessUnitName" />
	  </s:if>
	  <s:if test="availableBusinessUnits != null && availableBusinessUnits.size() > 1">
	  <tr rowspan="2"><td colspan="2">&nbsp;</td></tr>
	  <tr>
    	<td class="labelBold"> <s:text name="label.common.businessUnit" /></td>
    	<td>
    	<s:select name="reportSearchBean.businessUnitName" list="availableBusinessUnits" 
          		listKey="key" listValue="value" /></td>
      </tr>
	  </s:if>
	  <tr rowspan="2"><td colspan="2">&nbsp;</td></tr>
	  <tr>
    	<td class="labelBold"> <s:text name="label.downloadMgt.claimStatus" /></td>
    	<td>
    	<s:select name="reportSearchBean.claimStatus" list="recoveryClaimStates" headerKey="All" headerValue="All"
          		listValue="state" /></td>
      </tr>
      <tr rowspan="2"><td colspan="2">&nbsp;</td></tr>
      <tr>
      	
	      <td class="labelBold"> &nbsp;&nbsp;<s:text name="label.downloadMgt.supplierNumber" /></td>
	    
	  		<td class="labelBold"><s:textfield name="reportSearchBean.dealerNumber" />
	  			&nbsp;<s:text name="label.downloadMgt.supplierNumber.note" /></td>
	  	
  	  </tr>
      <tr rowspan="2"><td colspan="2">&nbsp;</td></tr>
      <tr>
    	<td class="labelBold"> &nbsp;&nbsp;<s:text name="label.downloadMgt.delimiter" /></td>
      	<td><s:select name="reportSearchBean.delimiter" list="delimiters"  /></td>
      </tr>
      <tr rowspan="2"><td colspan="2">&nbsp;</td></tr>
      <tr>
		<td colspan="2" align="center">
			<s:submit cssClass="buttonGeneric" value="%{getText('button.common.submit')}" 
				type="input" id="downloadClaimDetailData" action="downloadSupplierRecReport" />
		</td>
	  </tr>
    
      </table>
      
  </div>
  
<div dojoType="twms.widget.Dialog" id="downloadDlg" bgColor="white" bgOpacity="0.5" toggle="fade"
     toggleDuration="250" title="Download" style="width: 80%; height: 150px">
	<div class="dialogContent" dojoType="dijit.layout.LayoutContainer" style="border : 1px solid #EFEBF7">
		<div dojoType="dijit.layout.ContentPane" layoutAlign="client">
            <center>
            	<div class="quick_page_switch" style="float:center">
            		
        <s:if test="resultSize == 0">
			<table width="100%" cellspacing="0" cellpadding="0" class="grid">
			<tr align="center"><td>No results found</td></tr>
			</table>
		</s:if>
		<s:elseif test="resultSize > 0 ">
		
		<script>
			function downloadPage(pageNumber) {
				dojo.byId('downloadPageNumber').value=pageNumber;
				var form = dojo.byId("baseForm");
                form.action = "downloadSupplierRecReportPage.action";
                form.submit();
			}
		</script>
		<table  cellspacing="10" cellpadding="0" >
		<tr align="center"><td colspan="3">Totals Records <s:property value="resultSize"/></td></tr>
		<s:bean name="org.apache.struts2.util.Counter"  id="pgCounter"> 
		  <s:param name="last" value="%{(resultSize-1)/maxDownloadSize+1}" /> 
		</s:bean> 
		
		<s:iterator value="#pgCounter">
			<s:if test="top%3 == 1">
			<tr>
			</s:if>
			<td>
			<s:if test="top == #pgCounter.last">
				<s:a onclick="downloadPage(%{top}-1)">
					<s:property value="(top-1) * maxDownloadSize + 1"/> to <s:property value="resultSize"/>
				</s:a>
			</s:if>
			<s:else>
			<s:a onclick="downloadPage(%{top}-1)" href="#">
				<s:property value="(top-1) * maxDownloadSize + 1"/> to <s:property value="top * maxDownloadSize"/>
			</s:a>
			</s:else>
			</td>
			<s:if test="top%3 == 0 || top == #pgCounter.last">
			</tr>
			</s:if>
		</s:iterator>
		</table>
		</s:elseif>
            	</div>
            </center>
		</div>
	</div>
</div>

</s:form>
</u:body>
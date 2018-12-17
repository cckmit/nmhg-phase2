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
<%@taglib prefix="authz" uri="authz"%><head>
<u:stylePicker fileName="base.css"/>
<u:stylePicker fileName="adminPayment.css"/>
<s:head theme="twms"/>
</head>
<u:body>
<s:form name="baseForm" id="baseForm">
<s:hidden name="context" />
  
<div class="admin_section_div" style="width:102%;margin-right:0px;" >
     <div class="admin_section_heading"><s:text name="label.downloadMgt.manageDownloads"/></div>
     <div class="errorMessage" ><u:actionResults /></div>
   	<div class="mainTitle" style="margin-top:5px;">
      	<s:property value="displayContext"/></div>
   
    <table width="100%" cellspacing="0" cellpadding="0" class="grid">
       <tr width="100%">
    	<td class="labelBold" colspan="2"><s:property value="userNotes"/></td>
      </tr> 
   
      <tr>
      	<td class="labelBold" width="16%" nowrap="nowrap"> &nbsp;&nbsp;<s:text name="label.downloadMgt.fromDate" /></td>
      	<td><sd:datetimepicker name='reportSearchBean.fromDate' value='%{reportSearchBean.fromDate}' /></td>
      </tr>
      
      <tr>
      	<td class="labelBold"> &nbsp;&nbsp;<s:text name="label.downloadMgt.toDate" /></td>
      	<td><sd:datetimepicker name='reportSearchBean.toDate' value='%{reportSearchBean.toDate}' /></td>
      </tr>
    
      <tr>
    	<td class="labelBold" colspan="2" align="left">&nbsp;&nbsp;
    		<s:radio name="reportSearchBean.submitOrCreditOrUpdateDate" list="#{'submitDate':'Submit Date&nbsp;&nbsp;&nbsp;', 'creditDate':'Credit Date'}" listKey="key"
			listValue="value" theme="twms" value="'submitDate'" /></td>
	  </tr>
	  
      <tr>
    	<td class="labelBold"> &nbsp;&nbsp;<s:text name="label.downloadMgt.claimStatus" /></td>
    	<td><s:select name="reportSearchBean.claimStatus" list="claimStates" headerKey="All" headerValue="All"
          		listKey="state" listValue="state" cssStyle="width:120px;" /></td>
      </tr>
      
      <tr>
	      <td class="labelBold"> &nbsp;&nbsp;<s:text name="label.downloadMgt.dealerNumber" /></td>
	      <s:if test="loggedInUserAnInternalUser">
	  		<td class="labelBold"><s:textfield name="reportSearchBean.dealerNumber" />
	  			&nbsp;<s:text name="label.downloadMgt.dealerNumber.note" /></td>
	  	  </s:if>
	  	  
	  	  <s:if test="!loggedInUserAnInternalUser">
	  		<td class="labelBold">&nbsp;&nbsp;
	  		<s:hidden name="reportSearchBean.dealerNumber" value="%{loggedInUsersDealership.dealerNumber}" />
	  		<s:property value="%{loggedInUsersDealership.dealerNumber}" /></td>
	  	  </s:if>
  	  </tr>
  	  <authz:ifAdmin>
	      <s:if test="context != 'RecoveryReport'">
		      <tr>
		    	<td>&nbsp;</td>
				<td class="labelBold"><s:checkbox name="reportSearchBean.allDealerSelected" cssClass="checkbox"/>&nbsp;&nbsp;<s:text name="label.downloadMgt.allDealer" /></td>
			  </tr>
		  </s:if>
	  </authz:ifAdmin>
      
      <tr>
    	<td class="labelBold"> &nbsp;&nbsp;<s:text name="label.downloadMgt.delimiter" /></td>
      	<td><s:select name="reportSearchBean.delimiter" list="delimiters" cssStyle="width:120px;"  /></td>
      </tr>
      
 
    
      </table>
      
  </div>
       <div align="center" class="spacingAtTop">
			<s:submit cssClass="buttonGeneric" value="%{getText('button.common.submit')}" 
				type="input" id="downloadClaimDetailData" action="downloadClaimData" />
		</div>
</s:form>
</u:body>
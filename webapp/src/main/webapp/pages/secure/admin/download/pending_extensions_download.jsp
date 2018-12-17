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
@author: Kuldeep Krishna Patil
--%>
<%@page pageEncoding="UTF-8"%>
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
      <tr>
	      <td class="labelBold"> &nbsp;&nbsp;<s:text name="label.downloadMgt.dealerNumber" /></td>
	      <td class="labelBold">
		    <s:textfield name="reportSearchBean.dealerNumber" />
		  			&nbsp;<s:text name="label.downloadMgt.dealerNumber.note" />
	  	  </td>
  	  </tr>
      <tr>
    	<td class="labelBold"> &nbsp;&nbsp;<s:text name="label.downloadMgt.delimiter" /></td>
      	<td><s:select name="reportSearchBean.delimiter" list="delimiters" cssStyle="width:120px;"  /></td>
      </tr>
     </table>
  </div>
       <div align="center" class="spacingAtTop">
			<s:submit cssClass="buttonGeneric" value="%{getText('button.common.submit')}" 
				type="input" id="downloadPendingExtensions" action="downloadPendingExtensions" />
		</div>
</s:form>
</u:body>
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
<%@ taglib prefix="sd" uri="/struts-dojo-tags"%>
<%@taglib prefix="u" uri="/ui-ext" %>

<head>
<u:stylePicker fileName="base.css"/>
<u:stylePicker fileName="adminPayment.css"/>
<s:head theme="twms"/>
</head>

<u:body>
<s:form name="baseForm" id="baseForm">
<s:hidden name="context" />
  <div class="admin_section_div">
    
    <table  width="100%" cellspacing="0" cellpadding="0">
      <tr width="100%">
       	<td colspan="5"><div class="admin_section_heading"><s:text name="label.downloadMgt.manageDownloads"/></div></td>
      </tr> 
      <tr><td class="errorMessage" colspan="5"><u:actionResults /></td></tr>
      <tr rowspan="3"><td colspan="5">&nbsp;</td></tr>
      <tr width="100%">
      	<td colspan="5"><div class="admin_section_subheading">
      	<s:property value="displayContext"/></div></td>
      </tr> 
      <tr rowspan="3"><td colspan="5">&nbsp;</td></tr>
      <tr width="100%">
      	<td class="labelBold" colspan="5"><s:property value="userNotes"/></td>
      </tr> 
      <tr rowspan="3"><td colspan="5">&nbsp;</td></tr>
      <tr>
    	<td class="labelBold"> &nbsp;&nbsp;<s:text name="label.downloadMgt.acceptanceDate" /></td>
    	<td class="labelBold"><s:text name="label.downloadMgt.fromDate" /></td>
      	<td><sd:datetimepicker name='reportSearchBean.fromDate' value='%{reportSearchBean.fromDate}' /></td>
      	<td class="labelBold"><s:text name="label.downloadMgt.toDate" /></td>
      	<td><sd:datetimepicker name='reportSearchBean.toDate' value='%{reportSearchBean.toDate}' /></td>
      </tr>
      <tr rowspan="2"><td colspan="5">&nbsp;</td></tr>
      <tr>
    	<td class="labelBold"> &nbsp;&nbsp;<s:text name="label.downloadMgt.delimiter" /></td>
      	<td><s:select name="reportSearchBean.delimiter" list="delimiters"/></td>
      	<td colspan="3">&nbsp;</td>
      </tr>
      <tr rowspan="2"><td colspan="5">&nbsp;</td></tr>
      <tr>
		<td colspan="3" align="center">
			<s:submit cssClass="buttonGeneric" value="%{getText('button.common.submit')}" 
				type="input" id="downloadClaimDetailData" action="downloadClaimReportData" />
			<td colspan="2" align="center">&nbsp;</td>
	  </tr>
      <tr><td colspan="5">&nbsp;</td></tr>
    
      </table>
      
  </div>
</s:form>
</u:body>
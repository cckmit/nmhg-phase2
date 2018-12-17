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
<%@taglib prefix="t" uri="twms"%>
<%@taglib prefix="u" uri="/ui-ext"%>

<html>
<head>
    <meta http-equiv="Context-Type" content="text/html; charset=ISO-8859-1">
    <title><s:text name="title.common.warranty"/></title>
    <s:head theme="twms"/>
    <u:stylePicker fileName="common.css"/>
    <u:stylePicker fileName="form.css"/>
    <u:stylePicker fileName="adminPayment.css"/>

</head>
	
	<u:body>
		<u:actionResults/>
		<s:form name="baseform" id="baseform" method="post">
			<div class="admin_section_div" style="width:100%;margin:5px;">
			  <div class="admin_section_heading">
			    <s:text name="label.shipper" />
			  </div>
			   <div class="borderTable">&nbsp;</div>
				<div  style="margin-top:-10px; ">
					<table width="100%" border="0" cellspacing="0" cellpadding="0" class="grid">
					<tbody>
					<tr>
					  <td class="admin_data_table" nowrap="nowrap"><s:text name="label.manageShippers.name"/>:</td>
					  <td ><s:textfield name="carrier.name" size="94" value="%{carrier.name}"/></td>
					 </tr>
					 <tr>
					  <td  class="admin_data_table" nowrap="nowrap"><s:text name="label.manageShippers.description"/>:</td>
					  <td ><s:textfield name="carrier.description" size="94"  value="%{carrier.description}"/></td>
					 </tr>
					  <tr>
					  <td  class="admin_data_table" nowrap="nowrap"><s:text name="label.manageShippers.url"/>:</td>
					  <td ><s:textfield name="carrier.url" size="94" value="%{carrier.url}"/></td>
					 </tr>
					</tbody>
					</table>
				</div>
				<div align="center" class="spacingAtTop">
					<input id="cancel_btn" class="buttonGeneric" type="button" value="<s:text name='button.common.cancel'/>"
					    onclick="javascript:closeTab(getTabHavingLabel(getMyTabLabel()));" />
					<s:submit cssClass="buttonGeneric" value="%{getText('button.common.save')}" action="save_shipper"/>
				</div>
			</div>
		</s:form>
	</u:body>
</html>
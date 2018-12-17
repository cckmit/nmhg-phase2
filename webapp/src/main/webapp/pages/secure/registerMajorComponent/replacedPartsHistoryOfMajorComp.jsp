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

<%@ taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="t" uri="twms"%>
<%@taglib prefix="u" uri="/ui-ext"%>

<%response.setHeader( "Pragma", "no-cache" );
response.addHeader( "Cache-Control", "must-revalidate" );
response.addHeader( "Cache-Control", "no-cache" );
response.addHeader( "Cache-Control", "no-store" );
response.setDateHeader("Expires", 0); %>
<html>
 <head>   
    <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1" />
    <u:stylePicker fileName="yui/reset.css" common="true"/>
    <s:head theme="twms"/>
    <u:stylePicker fileName="common.css"/>
    <u:stylePicker fileName="form.css"/>
    <u:stylePicker fileName="base.css" />
    <u:stylePicker fileName="warrantyForm.css"/>     
 </head>
  <script type="text/javascript"> 
     dojo.require("dijit.layout.LayoutContainer");
  </script> 
 
 <div dojoType="dijit.layout.LayoutContainer" style="width: 100%; height: 100%; overflow-y:auto;" id="root">
  <div class="policy_section_div">
    <div class="section_header"><s:text name="Component Audit"/>
   </div>
   <table width="98%" class="grid borderForTable" cellpadding="0" cellspacing="0" align="center" style="margin-top:10px;">
	<thead>
		<tr class="row_head">
			<th><s:text name="columnTitle.manageBusinessRule.history.DateLastModified" /></th>
			<th><s:text name="label.claim.partSerialNumber" /></th>
			<th><s:text name="columnTitle.common.componentDescription" /></th>
			<th><s:text name="label.majorComponent.componentPartNo" /></th>
			<th><s:text name="label.majorComponent.componentManufacturer" /></th>
			<th><s:text name="label.majorComponent.componentTransactionType" /></th>
		</tr>
	</thead>
	<tbody>
		<s:iterator value="componentAuditList">
			<tr>
				<td>
					<s:property value="d.updatedOn" />
				</td>
				<td>
					<s:property value="componentPartSerialNumber" />
				</td>
				<td>
					<s:property value="serialTypeDescription" />
				</td>
				<td>
					<s:property value="componentPartNumber" />
				</td>
				<td>
					<s:property value="manufacturer" />
				</td>
				<td>
				<s:property value="transactionType" />
				</td>
			</tr>
		</s:iterator>
	</tbody>
	</table>
 </div>
</div>
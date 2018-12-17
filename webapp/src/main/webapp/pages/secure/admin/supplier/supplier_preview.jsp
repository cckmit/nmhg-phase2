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
  @author mritunjay.kumar
--%>
<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="t" uri="twms"%><%@taglib prefix="u" uri="/ui-ext"%>


<html>
<head>
    <meta http-equiv="Context-Type" content="text/html; charset=ISO-8859-1"/>
    <s:head theme="twms"/>
     <script type="text/javascript">
        dojo.require("dijit.layout.LayoutContainer");
        dojo.require("dijit.layout.TabContainer");
        dojo.require("dijit.layout.ContentPane");
    </script>
    <u:stylePicker fileName="master.css"/>
    <u:stylePicker fileName="base.css"/>
    <u:stylePicker fileName="common.css"/>
    <u:stylePicker fileName="inventory.css"/>


</head>

<u:body>
 <div dojoType="dijit.layout.LayoutContainer"
     layoutChildPriority='top-bottom'
     style="width: 100%; height: 100%; margin: 0; padding: 0; overflow-X: hidden;">
     
  <div class = "preview_supplier_tab" dojoType="dijit.layout.TabContainer" labelPosition="bottom" layoutAlign="client">
	   <div dojoType="dijit.layout.ContentPane" label="<s:text name="label.supplierDetail"/>"
				labelNodeClass="section_header" containerNodeClass="content">
  <div id="supplier_details" class="policy_section_div">
	<div id="supplier_details_title" class="section_header">
		<s:property value="%{getText('label.supplierDetail')}" />
	</div>				
	   <table width="96%" cellpadding="0" cellspacing="0" class="grid"
				align="center">
			<tr>
					<td class="labelStyle" width="20%" nowrap="nowrap"><s:text name="label.name" /></td>
					<td ><s:property value="supplier.name" /></td>
					<td class="labelStyle" nowrap="nowrap"><s:text name="label.supplierNumber" /></td>
					<td><s:property value="supplier.supplierNumber" /></td>
			</tr>
			<tr>
					<td class="labelStyle" nowrap="nowrap"><s:text name="label.address" /></td>
					<td><s:property value="supplier.address.addressLine1"/></td>
			</tr>
			<tr>
					<td class="labelStyle" nowrap="nowrap"><s:text name="label.country" /></td>
					<td><s:property value="supplier.address.country" /></td>
					<td class="labelStyle" nowrap="nowrap"><s:text name="label.state" /></td>
					<td><s:property value="supplier.address.state" /></td>
			</tr>
			<tr>
					<td class="labelStyle" nowrap="nowrap"><s:text name="label.city" /></td>
					<td><s:property value="supplier.address.city" /></td>
					<td class="labelStyle" nowrap="nowrap"><s:text name="label.zip" /></td>
					<td><s:property value="supplier.address.zipCode" /></td>
			</tr>
			<tr>
					<td class="labelStyle" nowrap="nowrap"><s:text name="label.phone" /></td>
					<td><s:property value="supplier.address.phone" /></td>
					<td class="labelStyle" nowrap="nowrap"><s:text name="label.email" /></td>
					<td><s:property value="supplier.address.email" /></td>
			</tr>
				<tr>
					<td class="labelStyle" nowrap="nowrap"><s:text name="label.fax" /></td>
					<td><s:property value="supplier.address.secondryPhone" /></td>
			</tr>
	   </table>	
     </div>	
   </div>
  </div> 
 </div>  
</u:body>
</html>
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

<%@ taglib prefix="s" uri="/struts-tags" %>
<%@taglib prefix="t" uri="twms"%><%@taglib prefix="u" uri="/ui-ext"%>

<html>
<head>
    <s:head theme="twms"/>
    <meta http-equiv="Context-Type" content="text/html; charset=ISO-8859-1"/>
    <title><s:text name="title.common.warranty"/></title>
    <u:stylePicker fileName="adminPayment.css"/>
</head>
<u:body>
<form name="baseForm" id="baseForm" style="width: 99%;">
<div class="admin_section_div" >
    <div class="admin_section_heading"><s:text name="label.manageRates.itemPriceModifierConfig" /></div>
    <div class="admin_subsection_div">
    	<div class="admin_section_subheading"><s:text name="label.manageRates.conditions" /></div>
    	<table width="98.5%" class="admin_selections">
    		<tr>
    			<td class="admin_data_table" width="23%" height="20"><s:text name="label.manageRates.itemNumberGroup" />:</td>
        		<td width="77%"><s:property value="itemPrice.itemCriterion.identifier" /></td>
			</tr>
			<tr>
    			<td class="admin_data_table" width="23%"><s:text name="label.manageRates.warrantyType" />:</td>
        		<td width="77%"><s:property value="%{getText(warrantyTypeString)}" /></td>
    		</tr>
    	</table>
    </div>
    <div class="admin_subsection_div">
	<div class="admin_section_subheading"><s:text name="label.manageRates.modifierValue"/></div>
    	<table width="100%" cellpadding="2" cellspacing="0" class="admin_entry_table">
    		<tr class="admin_table_header">
    			<th width="50%"><div align="left"><s:text name="label.manageRates.repairDate"/></div></th>
				<th width="50%"><s:text name="label.manageRates.priceModifier"/></th>
			</tr>
			<s:iterator value="priceEntries">
    	    <tr>
    	    	<td width="50%">
    	    		<table width="100%">
    					<tr>
    						<td class="admin_selections" width="8%" style="color:grey; font-weight:bold;"><s:text name="label.common.from"/>:</td>
    						<td class="admin_selections" width="40%"><s:property value="duration.fromDate" /></td>
    						<td class="admin_selections" width="4%"  style="color:grey; font-weight:bold;"><s:text name="label.common.to"/>:</td>
    						<td class="admin_selections" width="52%"><s:property value="duration.tillDate" /></td>
    					</tr>
    				</table>
    			</td>		
    			<td class="admin_selections"  style="border-left:1px solid #DCD5CC;"><s:property value="scalingFactor" /></td>
    		</tr>
    		</s:iterator>
		</table>	
    </div>
</div>    
</form>
</u:body>
</html>
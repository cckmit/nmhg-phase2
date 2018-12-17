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
<form name="baseForm" id="baseForm" style="width: 99%">
<div class="admin_section_div" style="margin:5px;">
    <div class="admin_section_heading"><s:text name="label.managePayment.modifier" /></div>
    
  
    	<div  class="mainTitle" style="margin:10px 0px 0px 0px"><s:text name="label.common.conditions" /></div>
    	<div class="borderTable">&nbsp;</div>
    	<div style="margin-top:-10px;"> 
    	    	<table width="100%" class="admin_selections">
    		<tr>
        		<td class="admin_data_table" width="25%"><s:text name="label.common.dealerCriterion" /></td>
        		<td  width="25%"><s:property value="dealerString" /></td>
        		<td class="admin_data_table"  width="25%"><s:text name="label.common.warrantyType" /></td>
        		<td width="25%"><s:property value="%{getText(warrantyTypeString)}" /></td>
      		</tr>
      		<tr>
    	    	<td class="admin_data_table"><s:text name="label.common.claimType" /></td>
    	    	<td><s:property value="claimTypeString" /></td>
    			<td class="admin_data_table"><s:text name="label.common.seriesDescription" /></td>
        		<td><s:property value="productTypeString" /></td>
    		</tr>
    	</table>
  
   </div>
   
   
    	<div class="mainTitle" style="margin:10px 0px 0px 0px" ><s:text name="label.common.rates"/></div>
    	<table width="100%" class="grid borderForTable">
    		<tr>
    			<th class="colHeader"><s:text name="label.managePayment.validityDate"/></th>
    			<th class="colHeader"><s:text name="label.managePayment.percentage"/></th>
    		</tr>
    		<s:iterator value="entries">
    	    <tr>
    	    	<td width="50%">
    	    		<table>
    					<tr>
    						<td class="admin_selections"><s:text name="label.common.from"/></td>
    						<td class="admin_selections"> : <s:property value="duration.fromDate" /></td>
    					</tr>
    					<tr>
    						<td class="admin_selections"><s:text name="label.common.to"/></td>
    						<td class="admin_selections"> : <s:property value="duration.tillDate" /></td>
    					</tr>
    				</table>
    			</td>		
    			<td class="admin_selections">
    				<s:property value="value" />
    				<s:if test="entries.isFlatRate == 'false'">
    				<span id="percentageLable_#myindex">%</span>
    				</s:if>
    			</td>
    		</tr>
    		</s:iterator>
    	</table>	
    </div>
   
</form>
</u:body>
</html>
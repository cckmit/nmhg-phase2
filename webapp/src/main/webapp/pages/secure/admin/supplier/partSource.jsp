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
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="t" uri="twms"%><%@taglib prefix="u" uri="/ui-ext"%>
<%@taglib prefix="authz" uri="authz"%>



<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Context-Type" content="text/html; charset=ISO-8859-1"/>
    <title>:: WARRANTY ::</title>
    <s:head theme="twms"/>
    <u:stylePicker fileName="detailDesign.css"/>
    <u:stylePicker fileName="yui/reset.css" common="true"/>
    <u:stylePicker fileName="common.css"/>
    <u:stylePicker fileName="claimForm.css"/>
    <u:stylePicker fileName="base.css"/>
    <style type="text/css">
        .addRow {
            margin-top: -14px;
            height: 14px;
            text-align: right;
            padding-right: 17px;
        }
    </style>
</head>

<script type="text/javascript" src="scripts/ui-ext/common/tabs.js"></script>
 <div class="policy_section_div">
			<div class="section_header"><s:text name="label.contractAdmin.supplierItemsCovered" /></div>
     <table width="100%" border="0" cellspacing="0" cellpadding="0" 
     class="grid borderForTable" align="center" style="margin:5px;">
 
            <tr class="row_head">
        	<th width="40%" ><s:text name="columnTitle.listContracts.supplier_name"/></th>
        	<th width="15%" ><s:text name="columnTitle.duePartsInspection.supplier_part_no"/></th>
            <th width="15%" ><s:text name="label.sra.partSouce.supPartDescription"/></th>
            <th width="15%" ><s:text name="label.common.location_code"/></th>
            <th width="10%" ><s:text name="label.sra.partSource.fromDate"/></th>
            <th width="10%"><s:text name="label.sra.partSource.toDate"/></th>
            </tr>
       
            <s:iterator value="itemMappings" status="status">
	            <s:iterator value="supplierItemLocations" status="supplierItemLocation">
	              <tr>
		        	<td >
						<s:property value="toItem.ownedBy.name"/>	
		            </td>
		            
		            <td >
		            	<s:property value="toItem.number"/>
		            </td>
		            <td >
		                <s:property value="toItem.description"/>
		            </td>
		            <td >
		            	<s:property value="locationCode" />
		            </td>
		            <td >
		                <s:property value="fromDate"/>
		            </td>
		            <td >
		                <s:property value="toDate"/>
		            </td>
		           </tr> 	
	            </s:iterator>
            </s:iterator>            
        
    </table>
</div>
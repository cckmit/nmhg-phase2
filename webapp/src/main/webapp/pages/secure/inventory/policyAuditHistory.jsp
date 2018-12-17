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
<%@ taglib prefix="t" uri="twms" %>
<%@ taglib prefix="u" uri="/ui-ext" %>
<%@page pageEncoding="UTF-8"%>

<%--
This script is to close the pop up page to display the policy audit histories.
--%>
<u:stylePicker fileName="multiCar.css" />
<div >
<table  class="grid borderForTable" style="width:98.5%;" cellspacing="0" cellpadding="0">
	    <thead>        
	        <tr class="row_head">        	
	            <th align = "center" width="15%"><s:text name="label.policyAudit.createdOn"/></th>
	            <th align = "center" width="15%"><s:text name="label.policyAudit.startDate"/></th>
	            <th align = "center" width="15%"><s:text name="label.policyAudit.endDate"/></th>	            
	            <th align = "center" width="10%"><s:text name="label.policyAudit.createdBy"/></th>
	            <th align = "center" width="10%"><s:text name="label.policyAudit.status"/></th>
	            <th align = "center" width="35%"><s:text name="label.policyAudit.comments"/></th>                    
	        </tr>
	    </thead>	  
	    <tbody>
	        <s:iterator value="policyAudits" status="audit" id="auditHistoryList">
	            <tr id="listCounter">            	
	                <td width="15%" ><s:property value="d.createdOn"/></td>
	                <td width="15%"><s:property value="warrantyPeriod.fromDate"/></td>
	                <td width="15%"><s:property value="warrantyPeriod.tillDate"/></td>
	                <td width="10%"><s:property value="createdBy.completeNameAndLogin"/></td>                
	                <td width="10%"><s:property value="getPolicyStatus(warrantyPeriod.fromDate, warrantyPeriod.tillDate, status)"/></td>               
	                <td width="35%"><s:property value="comments" /></td>                
	            </tr>
	        </s:iterator>
	    </tbody>
</table>
</div>
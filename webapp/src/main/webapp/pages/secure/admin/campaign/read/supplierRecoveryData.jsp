<%--
  Created by IntelliJ IDEA.
  User: pradyot.rout
  Date: Mar 30, 2009
  Time: 6:46:37 PM
  To change this template use File | Settings | File Templates.

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
<%@taglib prefix="tda" uri="twmsDomainAware"%>
<%@taglib prefix="authz" uri="authz"%>
<%@taglib prefix="u" uri="/ui-ext"%>
<%response.setHeader( "Pragma", "no-cache" );
  response.addHeader( "Cache-Control", "must-revalidate" );
  response.addHeader( "Cache-Control", "no-cache" );
  response.addHeader( "Cache-Control", "no-store" );
  response.setDateHeader("Expires", 0);
%>
<div style="background:#F3FBFE ">
<div class="mainTitle" style="margin:10px 0px 10px 0px; ">
	<s:text name="title.common.supplier.recoveryInfo"></s:text>
</div>
<table width="96%" class="grid borderForTable" cellspacing="0" cellpadding="0">
   
	    		<thead>
		    		<tr class="row_head">
				        <th width="40%">
				              <s:text name="columnTitle.listContracts.contract_name"></s:text>
				        </th>
				        <th width="30%">
				        	<s:text name="columnTitle.listContracts.supplier_name"></s:text>
				        </th>
				        <th width="30%">
				        	<s:text name="label.supplierNumber"></s:text>
				        </th>
		    		</tr>
	    		</thead>
	    		<tbody>
	    			<tr>
	    				<td><s:property value="contract.name"/> </td>
						<td><s:property value="contract.supplier.name"/> </td>
					    <td><s:property value="contract.supplier.supplierNumber"/> </td>
	    			</tr>
	    		</tbody>
	    	</table>
	  
 </div>

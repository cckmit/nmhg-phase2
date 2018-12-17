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
<%@ taglib prefix="authz" uri="authz" %>
	<div dojoType="dijit.layout.ContentPane" layoutAlign="client" id="availableResults">
	<div style="overflow:auto;height:80%;width:100%">
		<table cellspacing="0" cellpadding="0" class="grid borderForTable" style="margin:5px;width:98%;">
	   <thead>
	   	<tr class="title">
	   		<td colspan="4"><s:text name="label.warrantyAdmin.addNewPolicies"/></td>
	   	</tr>
	    <tr class="row_head">
	    	<th align="center"><input id="allBoxes" type="checkbox"/></th>
			<th><s:text name="label.warrantyAdmin.policyCode"/></th>
		    <th><s:text name="label.warrantyAdmin.policyName"/></th>
		    <th><s:text name="label.warrantyAdmin.type"/></th>
		   </tr>
	   </thead>
	   <tbody id="addPoliciesBody">
	   </tbody>
	  </table>
	  </div>
	</div>
	<div dojoType="dijit.layout.ContentPane" layoutAlign="bottom">
		<div align="center">
			<input type="button" id="add_policies_button" class="button" 
					value="<s:text name="button.warrantyAdmin.addRemovePolicies"/>"/>
			<input type="button" id="closeDialog" class="button" 
					value="<s:text name="button.common.close"/>" />
		</div>
	</div>


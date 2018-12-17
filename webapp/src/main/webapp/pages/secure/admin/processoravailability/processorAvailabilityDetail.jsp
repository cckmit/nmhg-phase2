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


<%@ page contentType="text/html"%>
<%@ page pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="u" uri="/ui-ext"%>
<%@ taglib prefix="authz" uri="authz" %>

<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<s:head theme="twms" />
<title><s:text name="title.partInventory.partAvailInfo"/></title>
<u:stylePicker fileName="yui/reset.css" common="true" />
<u:stylePicker fileName="layout.css" common="true" />
<u:stylePicker fileName="common.css" />
<u:stylePicker fileName="form.css" />
<u:stylePicker fileName="adminPayment.css" />
<u:stylePicker fileName="base.css" />
    <script type="text/javascript">
  	  	dojo.require("dijit.layout.ContentPane");
      	dojo.require("dijit.layout.LayoutContainer"); 
	</script>
</head>

<u:body>

<div dojoType="dijit.layout.LayoutContainer"
	style="width: 100%; height: 100%; background: white; overflow-y: auto;">
<div dojoType="dijit.layout.ContentPane" layoutAlign="client">
<u:actionResults />
<div class="policyRegn_section_div">
<div class="admin_section_heading">
   <s:text name="label.processorAvail.processorAvailInfo"/>
</div>
<s:form name="updateProcessorAvailForm" action="update_processor_avail.action" id="baseForm">
<s:hidden name="userForAvailability.id" />
<s:hidden name="role" />


		<table  width="100%" cellspacing="0" cellpadding="0" class="grid">
		<tr>
		     <td class="labelStyle" width="20%" nowrap="nowrap" ><s:text name="label.processorAvail.processor" />:</td> 
		     <td><s:property value="userForAvailability.firstName" />&nbsp;
		     <s:property value="userForAvailability.lastName" />&nbsp;
		     (<s:property value="userForAvailability.name" />)
		     </td> 
		</tr> 
		<tr>
		     <td class="labelStyle" nowrap="nowrap" ><s:text name="label.processorAvail.availability" />:</td> 
		     <td>
			     <input type="radio" name="userAvailable" value="true" <s:if test="userAvailable">checked="checked"</s:if>/> <s:text name="label.common.yes" /> 
			     <input type="radio" name="userAvailable" value="false" <s:if test="!userAvailable">checked="checked"</s:if>/> <s:text name="label.common.no" />
		     </td> 
		</tr> 
		<tr>
	     <td class="labelStyle" nowrap="nowrap"><s:text name="label.processorAvail.defaultUser" />:</td> 
	     <td>
  			<s:hidden name="isDefaultUser" value="%{userDefaultRole}" />
	     	 <input type="radio" name="userDefaultRole" value="true" <s:if test="userDefaultRole">checked="checked"</s:if>/> <s:text name="label.common.yes" /> 
			     <input type="radio" name="userDefaultRole" value="false" <s:if test="!userDefaultRole">checked="checked"</s:if>/> <s:text name="label.common.no" />
	     </td> 
	    </tr> 
	    <s:hidden name="selectedBusinessUnit" />
        <tr>
            <td colspan="2">
                <div class="spacingAtTop" align="center">
                    <s:submit align="middle" cssClass="button" value="%{getText('button.common.save')}" action="update_processor_avail"></s:submit>
                </div>
            </td>
        </tr>
		</table> 
</s:form>
		</div>
 </div>
</div>
<authz:ifPermitted resource="warrantyAdminManageUserAvailabilityReadOnlyView">
	<script type="text/javascript">
	    dojo.addOnLoad(function() {
	        for ( var i = 0; i < dojo.query("input, button, textarea, select, text", dojo.byId('baseForm')).length; i++) {
	            dojo.query("input, button, textarea, select, text", dojo.byId('baseForm'))[i].disabled=true;
	        }
	    });
	</script>
</authz:ifPermitted>
</u:body>
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
<style>
td{
padding-left:5px;
}
</style>

	<table width="100%" border="0" cellspacing="0" cellpadding="0" class="grid" style="margin-top:5px;">
	
	<tr>
	<td width="10%" class="labelStyle">
	<s:text name="label.additionalAttribute.name"></s:text>
	</td>
	<td width="20%" >
	<s:property value= "additionalAttributes.attributeName"/>
	</td>
	</tr>
	<tr>
	<td width="10%" class="labelStyle">
	<s:text name="label.additionalAttribute.claimType">:</s:text>
	</td>
	<td width="20%" >
	<s:property  value="additionalAttributes.claimTypes" />
	</td>
	</tr>
	<tr>
	<td width="10%" class="labelStyle">
	<s:text name="label.additionalAttribute.attributeType">:</s:text>
	</td>
	<td width="20%" >
	
	<s:property  value= "additionalAttributes.attributeType" />
	</td>
	</tr>
	
	<tr>
	<td width="10%" class="labelStyle">
	<s:text name="label.additionalAttribute.purpose">:</s:text>
	</td>
	<td width="20%" >
	
	<s:property value= "additionalAttributes.attributePurpose.purpose"  />
	</td>
	</tr>
	
	<tr>
	<td width="10%" class="labelStyle">
	<s:text name="label.additionalAttribute.mandatory">:</s:text>
	</td>
	<td width="5%" >
	<s:if test="additionalAttributes.mandatory">
	<s:text name="label.common.yes"></s:text>
	</s:if>
	<s:else>
	<s:text name="label.common.no"></s:text>
	</s:else>
	</td>
	</tr>
	
</table>

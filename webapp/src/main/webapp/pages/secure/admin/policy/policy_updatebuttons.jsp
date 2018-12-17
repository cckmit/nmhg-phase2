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
<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="t" uri="twms"%><%@taglib prefix="u" uri="/ui-ext"%>

<div align="center">
	<s:hidden name="preview" value="true"/>
	<s:if test="policyDefinition.currentlyInactive">
		<s:submit cssClass="buttonGeneric" value="%{getText('button.managePolicy.activate')}" action="policy_activate_preview"/>
	</s:if>
	<s:else>
	    <s:submit cssClass="buttonGeneric" value="%{getText('button.managePolicy.deActivate')}" action="policy_deactivate_preview"/>
	</s:else>
	<input type="submit" value="<s:text name="button.common.save"/>" id="submitUpdatePolicy" class="buttonGeneric"/>
</div>
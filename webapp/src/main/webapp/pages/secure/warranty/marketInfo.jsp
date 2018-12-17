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
<%@ page contentType="text/html;charset=UTF-8" language="java"%>

<%@ taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="t" uri="twms"%>
<%@taglib prefix="u" uri="/ui-ext"%>
<%
	response.setHeader("Pragma", "no-cache");
	response.addHeader("Cache-Control", "must-revalidate");
	response.addHeader("Cache-Control", "no-cache");
	response.addHeader("Cache-Control", "no-store");
	response.setDateHeader("Expires", 0);
%>



<div style="display: table; width: 100%; white-space: nowrap;"
	class="grid borderForTable"><s:iterator status="stat"
	value="marketingInfo">
	<s:if test="#stat.odd == true ">
		<div style="display: table-row; width: 100%; height: 1%;">&nbsp;</div>
		<div style="display: table-row; width: 100%; border: 1px;">
	</s:if>

	<div style="display: table-cell; width: 25%; float: left;"><s:label
		value="%{key.fieldName}" cssClass="labelStyle" /></div>
	<div style="display: table-cell; width: 1%; float: left;"><s:textfield
		cssStyle="display:none;" value="%{key}"
		name='%{#nListName}.selectedMarketingInfo[%{#stat.index}].addtlMarketingInfo' /></div>
	<s:iterator status="status" value="value">
		<div style="display: table-cell; width: 25%; float: left;"><s:if
			test="key=='DROPDOWN'">
			<s:select
				name='%{#nListName}.selectedMarketingInfo[%{#stat.index}].value'
				id="%{qualifyId(\"select\")}%{#stat.index}"
				cssClass="processor_decesion" list="value" listKey="optionValue"
				listValue="optionValue" headerKey=""
				headerValue="%{getText('label.common.selectHeader')}" />
		</s:if> <s:else>
			<s:textfield
				name='%{#nListName}.selectedMarketingInfo[%{#stat.index}].value'
				id="%{qualifyId(\"selectedMarketingInfo\")}"></s:textfield>

		</s:else></div>
	</s:iterator>
	<s:if test="#stat.even == true "></div>
</s:if>
</s:iterator>
</div>
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
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="t" uri="twms"%><%@taglib prefix="u" uri="/ui-ext"%>

<script type="text/javascript">
	dojo.require("dijit.layout.ContentPane");
</script>

<div dojoType="dijit.layout.ContentPane" layoutAlign="client"
	style="width: 99.2%; background: #F3FBFE; border:#EFEBF7 1px solid; margin-left: 5px;margin-top: 5px;  padding: 0px; overflow-x: hidden">
<table width="100%" border="0" cellspacing="0" cellpadding="0">
	<tr>
		<td colspan="3" nowrap="nowrap" class="admin_section_heading"><s:text name="label.managePayment.paymentDefinition"/></td>
	</tr>

	<s:iterator value="paymentDefinition.paymentSections" id="section"
		status="sectionStatus">
		<tr>
			<td class="pmtHeading" colspan="3"><s:hidden
				name="paymentDefinition.paymentSections[%{#sectionStatus.index}].section"></s:hidden>
			<s:text name="%{getI18NMessageKey(section.name)}"/></td>

		</tr>
		 <s:iterator value="#section.paymentVariableLevels" id="level" status="levelStatus">
		        	<s:hidden name="paymentDefinition.paymentSections[%{#sectionStatus.index}].paymentVariableLevels[%{#levelStatus.index}].level"></s:hidden>
		        	<s:hidden name="paymentDefinition.paymentSections[%{#sectionStatus.index}].paymentVariableLevels[%{#levelStatus.index}].paymentVariable" 
		        		value="%{#level.paymentVariable.id}"></s:hidden>
		        </s:iterator>
		<s:iterator value="%{getPrettyPrintLineItems(section,#section)}">
			<tr>
				<td class="labelNormal" width="15%">&nbsp;</td>
				<td class="label" nowrap="nowrap" width="35%" align="right"><s:property
					value="getKey()"></s:property>:</td>
				<td class="labelNormal"><s:property value="getValue()"></s:property></td>
			</tr>

		</s:iterator>
		<tr>
			<td class="labelNormal" width="15%">&nbsp;</td>
			<td width="35%" class="label"><s:text name="%{getI18NMessageKey(section.name)}"/> <s:text name="label.common.total"/>:</td>
			<td class="labelNormal">&nbsp;<s:property value="@org.springframework.util.StringUtils@collectionToDelimitedString(getPrettyPrintLineItems(section,#section).keySet(), ' + ')"/></td>
		</tr>
	</s:iterator>
</table>

</div>

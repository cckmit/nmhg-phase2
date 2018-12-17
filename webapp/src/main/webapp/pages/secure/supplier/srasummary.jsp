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


<html class="Rule">
<head>
    <meta http-equiv="Context-Type" content="text/html; charset=ISO-8859-1"/>
    <title><s:text name="title.common.warranty"/></title>

    <script type="text/javascript">
        var djConfig = { isDebug: true };
    </script>
    <script type="text/javascript" src="scripts/vendor/dojo-widget/dojo.js"></script>
    <script type="text/javascript" src="scripts/tabcontent.js"></script>
    <script type="text/javascript">
        dojo.require("dijit.layout.LayoutContainer");
        dojo.require("dijit.layout.TabContainer");
        dojo.require("dijit.layout.ContentPane");
    </script>


    <script>
        function closeCurrentTab() {
            closeTab(getTabHavingId(getTabDetailsForIframe().tabId));
        }
        function back() {
            with (document.forms.supplierAdminForm_form) {
                action = '<s:property value="%{taskName.equals(@tavant.twms.jbpm.WorkflowConstants@SHIPMENT_FROM_DEALER_TASK)?'supplierRecoveryAdminDirectShipment_detail.action':'supplierRecoveryAdmin_detail.action'}"/>';
                submit();
            }
        }
        function submitForm() {
            with (document.forms.supplierAdminForm_form) {
                action = '<s:property value="%{taskName.equals(@tavant.twms.jbpm.WorkflowConstants@SHIPMENT_FROM_DEALER_TASK)?'supplierRecoveryAdminDirectShipment_submit.action':'supplierRecoveryAdmin_submit.action'}"/>';
                submit();
            }
        }
    </script>

    <%-- FIXME: something like <s:property value="#session['session.cssTheme']"/>, someone plz set session.cssTheme it in session and change this --%>
    <u:stylePicker fileName="base.css"/>
    <u:stylePicker fileName="inventory.css"/>
    <u:stylePicker fileName="detailDesign.css"/>
    <u:stylePicker fileName="master.css"/>
    <u:stylePicker fileName="tabcontent.css"/>

    <style type="text/css">
        .totalAmountTextRightalign {
            font-family: Verdana, sans-serif, Arial, Helvetica;
            color: #666666;
            font-size: 10px;
            font-weight: bold;
            line-height: 25px;
            border: 0px;
            padding-right: 5px;
            text-align: right
        }
    </style>

</head>

<body class="Rule">

<div dojoType="dijit.layout.LayoutContainer" layoutAlign="client"
	style="overflow-X: auto; overflow-Y: auto; width:100%;">

<form id="supplierAdminForm_form" name="supplierAdminForm_form"
	method="post"><input type="hidden" name="id"
	value="<s:property value="id"/>" /> <input type="hidden" name="claim"
	value="<s:property value="id"/>" /> <s:iterator value="shipOEMParts"
	id="partId" status="partsStatus">
	<input type="hidden" value="<s:property value="#partId"/>"
		name="shipOEMParts" />
</s:iterator>

<table width="100%" border="0" cellspacing="0" cellpadding="0">
	<tr>
		<td class="HeaderTxt"><s:text name="label.common.summary"/></td>
	</tr>
</table>

<table width="100%" border="0" cellspacing="0" cellpadding="0">
	<tr>
		<td class="sectionbgPadding">
		<div class="noBorderCellbg">

		<table width="100%" border="0" cellspacing="1" cellpadding="0">

			<tr>
				<td width="40%" class="colHeaderTop"><s:text name="label.supplier.costCategory"/></td>
				<td width="15%" class="colHeaderRightAlignTop"><s:text name="label.supplier.claimAmount"/></td>
				<td width="15%" class="colHeaderRightAlignTop"><s:text name="label.contract.contractValue"/><ahref="#" class="colHeaderTop"></a></td>
				<td width="15%" class="colHeaderRightAlignTop"><s:text name="label.common.actualValue"/></td>
			</tr>
			<s:iterator value="allSections" status="lineItemStatus" id="section">
				<s:if
					test="#section.name != @tavant.twms.domain.claim.payment.definition.Section@TOTAL_CLAIM">
					<tr>
						<td width="20%" nowrap="nowrap" class="tableDataWhiteBgTopAlign"><s:property
							value="%{#section.name}" /></td>
						<td width="15%" nowrap="nowrap" class="tableDataAmount"><s:property
							value="getCostForSection('actualCost',#section.name)" /></td>
						<td width="15%" nowrap="nowrap" class="tableDataAmount"><s:property
							value="getCostForSection('costAfterApplyingContract',#section.name)" /></td>
						<td width="15%" class="tableDataAmount"><s:property
							value="getCostForSection('recoveredCost',#section.name)" /></td>
					</tr>
				</s:if>
			</s:iterator>
			<tr>
				<td nowrap="nowrap" class="totalAmountTextRightalign"><s:text name="label.common.totalValue"/></td>
				<td nowrap="nowrap" class="totalAmountTextRightalign"><s:property
					value="getTotalCostForSection('actualCost')" /></td>
				<td width="15%" nowrap="nowrap" class="totalAmountTextRightalign"><s:property
					value="getTotalCostForSection('costAfterApplyingContract')" /></td>
				<td class="totalAmountTextRightalign"><s:property
					value="getTotalCostForSection('recoveredCost')" /></td>
			</tr>
		</table>
		</div>
		</td>
	</tr>
</table>

<%-- Set the appropriate transition depending on the Task name--%> <s:hidden
	name="transitionTaken"
	value="%{taskName.equals(@tavant.twms.jbpm.WorkflowConstants@SHIPMENT_FROM_DEALER_TASK)?@tavant.twms.jbpm.WorkflowConstants@SEND_TO_DEALER:@tavant.twms.jbpm.WorkflowConstants@SEND_TO_SUPPLIER}" />

<div class="buttonWrapperPrimary"><input type="button"
	value="&lt;&lt; Back" class="buttonGeneric" onclick="back()" /> <input
	type="button" onClick="closeCurrentTab()" value="Cancel"
	class="buttonGeneric" /> <input type="button" value="Submit"
	class="buttonGeneric" onclick="submitForm();" /></div>

</form>
</div>

</body>
</html>

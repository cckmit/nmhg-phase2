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

<html>
<head>
    <meta http-equiv="Context-Type" content="text/html; charset=ISO-8859-1"/>
    <title><s:text name="title.common.warranty"/></title>
    <s:head theme="twms"/>
    <script type="text/javascript">
        dojo.require("dijit.layout.LayoutContainer");
        dojo.require("dijit.layout.TabContainer");
        dojo.require("dijit.layout.ContentPane");
    </script>

    <script type="text/javascript" src="scripts/ui-ext/common/tabs.js"></script>

    <%-- FIXME: something like <s:property value="#session['session.cssTheme']"/>, someone plz set session.cssTheme it in session and change this --%>
    <u:stylePicker fileName="base.css"/>
    <u:stylePicker fileName="inventory.css"/>
    <u:stylePicker fileName="detailDesign.css"/>
    <u:stylePicker fileName="master.css"/>
    <style>
        .endTotalAmountText {
            font-family: Verdana, sans-serif, Arial, Helvetica;
            font-size: 10px;
            font-weight: bold;
            color: #666666;
            line-height: 15px;
            border: 0px;
            padding-left: 3px;
            text-align: left;
            border-bottom: 0px;
            border-color: #AAAAAA;
            border-style: solid;
            background-color: #FFFFFF
        }

        .endTotalAmount {
            font-family: Verdana, sans-serif, Arial, Helvetica;
            font-size: 10px;
            font-weight: bold;
            color: #666666;
            line-height: 15px;
            border: 0px;
            padding-right: 15px;
            text-align: right;
            border-bottom: 0px;
            border-color: #AAAAAA;
            border-style: solid;
            background-color: #FFFFFF
        }
    </style>

</head>

<u:body>
<div dojoType="dijit.layout.LayoutContainer"
	style="overflow-X: hidden; overflow-Y: auto; width:100%; height: 100%">

<table width="98%" border="0" cellspacing="0" cellpadding="0">
	<tr id="hideRowT0" style="display:''">
		<td class="sectionbgPadding">
		<div class="noBorderCellbg">
		<table width="100%" border="0" cellspacing="1" cellpadding="0">
			<tr>
				<td colspan="6" nowrap="nowrap" class="subSectionTitle">
				Details</td>
			</tr>
			<tr>
				<td>
				<table width="100%" border="0" cellspacing="0" cellpadding="0">
					<tr id="hideRowB0" style="display:''">
						<td class="noBorderCellbg">
						<table width="100%" border="0" cellpadding="0" cellspacing="1"
							bgcolor="#DCD5CC">
							<tr>
								<td width="100%" valign="middle" nowrap="nowrap"
									>
								<table width="100%" border="0" cellpadding="0" cellspacing="0">
									<tr>
										<td width="15%" class="label"><s:text name="label.common.partNumber"/></td>
										<td width="20%" class="labelNormal"><s:property
											value="oemPartReplaced.supplierPartReturn.supplierItem.number" /></td>
										<td width="15%" class="label"><s:text name="label.supplier.description"/></td>
										<td width="50%" class="labelNormal"><s:property
											value="oemPartReplaced.supplierPartReturn.supplierItem.description" /></td>
									</tr>
								</table>
								<table width="100%" border="0" cellspacing="1" cellpadding="0"
									bgcolor="#F3FBFE">

									<tr>
										<td width="65%" class="colHeader"><s:text name="label.supplier.costCategory"/></td>
										<td width="35%" class="colHeaderRightAlign"><s:text name="amount"/></td>
									</tr>
									<s:iterator
										value="oemPartReplaced.supplierPartReturn.costLineItems">
										<tr>
											<td width="65%" nowrap="nowrap"
												class="tableDataWhiteBgTopAlign"><s:property
												value="section.name" /></td>
											<td width="35%" nowrap="nowrap" class="tableDataAmount"><s:property
												value="oemPartReplaced.supplierPartReturn.getRecoveredCostForSection(section.name)" /></td>
										</tr>
									</s:iterator>
									<tr>
										<td width="65%" nowrap="nowrap" class="endTotalAmountText"><s:text name="label.common.totalValue"/></td>
										<td width="35%" nowrap="nowrap" class="endTotalAmount"><s:property
											value="oemPartReplaced.supplierPartReturn.getTotalRecoveredCost()" /></td>
									</tr>
								</table>
								</td>
							</tr>
						</table>
						</td>
					</tr>
				</table>
				</td>
			</tr>
		</table>
		</td>
	</tr>
</table>
</div>
</u:body>
</html>



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



<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title><s:text
	name="title.partReturnConfiguration.duePartReturns" /></title>
<meta http-equiv="Context-Type" content="text/html; charset=ISO-8859-1" />
<s:head theme="twms" />

<script type="text/javascript">
  dojo.require("dijit.layout.LayoutContainer");
  dojo.require("dijit.layout.ContentPane");  
</script>

<!--  TODO:  Need to move out the styles to a css file.  -->
<style type="text/css">
html {
	margin: 0;
	padding: 0;
	background-color: #FFFFFF
}

body {
	margin: 0px;
	background-color: #FFFFFF;
	scrollbar-base-color: #E8EDEF;
	scrollbar-arrow-color: #000000;
	scrollbar-track-color: #f6f6f6;
	scrollbar-3dlight-color: #133173;
	scrollbar-highlight-color: #FFFFFF;
	scrollbar-darkshadow-color: #666666;
	scrollbar-shadow-color: #EFEBF7;
	scrollbar-face-color: #E8EDEF;
	height: 100%;
	padding: 0px;	
}

.centresubtitle {
	background-color: #DCE9F7;
	border-bottom: 2px solid #EFEBF7;
	font-family: Arial, Helvetica, sans-serif;
	text-transform: uppercase;
	font-size: 9pt;
	font-style: normal;
	font-weight: 700;
	padding: 2px;
	text-align: center;
	height: 25px;
	color: #5577B4;
}

.partsHeadertitle {
	background-color: #DCE9F7;
	font-family: Arial, Helvetica, sans-serif;
	text-transform: uppercase;
	font-size: 8.5pt;
	font-style: normal;
	font-weight: 700;
	padding: 2px;
	text-align: center;
	height: 20px;
	color: #5577B4;
}

.fullsubtitle,.partssubtitle {
	font-family: Arial, Helvetica, sans-serif;
	font-size: 8.5pt;
	font-weight: normal;
	text-transform: uppercase;
	color: #636363;
	padding-left: 5px;
	line-height: 25px;
	border: 1px solid #EFEBF7;
}

.fullsubtitle {
	background-color: #DCE9F7;
	margin-left: 0px;
	margin-right: 0px;
	margin-top: 15px;
}

.dealersubtitle {
	background-color: #DCE9F7;
	margin-left: 5px;
	margin-right: 5px;
	margin-top: 15px;
	width: 100%;
}

table.dealertable,table.parttable {
	margin-left: 1px;
	margin-right: 1px;
	border: 1px solid #c2c2c2;
	padding-left: 0px;
	background-color: #F3FBFE;
}

table.dealertable {
	width: 100%;
}

.dealertable tr th {
	border: 1px solid #ffffff;
}

.dealertable tr td {
	border: 1px solid #EFEBF7;
}

table.parttable {
	width: 70%;
	margin-left: 50px;
}

.partssubtitle {
	background-color: #DCE9F7;
	width: 100%;
}

.tableaddress {
	float: right;
	color: #545454;
	font-family: Arial, Helvetica, sans-serif;
	font-size: 8.5pt;
	font-weight: bold;
	font-style: normal;
	text-align: left;
	line-height: 10px;
	align: left;
	border: 2px;
	vertical-align: top;
	width: 100%;
}

.returnaddressdetails {
	border-bottom: 1px solid #EFEBF7;
	width: 100%;
}

.detailswithoutborder,.returnaddressdetails {
	width: 100%;
	height: 75px;
	margin-top: 5px;
}


.colHeader {
	font-family: Arial, Helvetica, sans-serif;
	font-size: 8.5pt;
	color: #545454;
	font-weight: bold;
	text-align: left;
	text-decoration: none;
	background-color: #F3FBFE;
	padding-left: 2px;
}

.tableDataWhiteBg {
	font-family: Arial, Helvetica, sans-serif;
	font-size: 9pt;
	line-height: 15px;
	border: 0px;
	padding-left: 0px;
	text-align: left;
}

.tableDataAltRow {
	font-family: Arial, Helvetica, sans-serif;
	font-size: 9pt;
	line-height: 15px;
	border: 0px;
	padding-left: 2px;
	text-align: left;
	background-color: #F3FBFE;
}

.lineseparator {
	border-bottom: 1px solid #EFEBF7;
	margin-top: 10px;
}

.separator {
	padding-top: 30px;
}

.dashedLine {
	padding-top: 20px;
	border-bottom: 1px dashed #EFEBF7;
	margin-bottom: 40px;
}

.buttonWrapperPrimary {
	padding-top: 7px;
	padding-bottom: 7px;
	text-align: center
}

.buttonGeneric {
	background: transparent url(../../../image/buttonBg_new.gif) repeat-x
		scroll left center;
	border: 1px solid #EFEBF7;
	color: #545454;
	cursor: pointer;
	font-family: Arial, Helvetica, sans-serif;
	font-size: 8.5pt;
	font-style: normal;
	overflow: visible;
	height: 20px;
}

.labelBold {
	color: #545454;
	font-family: Arial, Helvetica, sans-serif;
	font-size: 9pt;
	font-weight: bold;
	font-style: normal;
	vertical-align: top;
	padding-left: 20px;
	text-align: left;
	line-height: 20px
}

.labelBoldAndBig {
	color: #545454;
	font-family: Arial, Helvetica, sans-serif;
	font-size: 20pt;
	font-weight: bold;
	font-style: normal;
	vertical-align: top;
	padding-left: 20px;
	text-align: left;
	line-height: 20px
}

.labelLargeFont {
	color: #545454;
	font-family: Arial, Helvetica, sans-serif;
	font-size: 11pt;
	font-weight: bold;
	font-style: normal;
	vertical-align: top;
	padding-left: 20px;
	text-align: left;
	line-height: 20px
}

.labelNormal {
	color: #545454;
	font-family: Arial, Helvetica, sans-serif;
	font-size: 9pt;
	font-weight: normal;
	font-style: normal;
	vertical-align: middle;
	padding-right: 10px;
	padding-top: 5px;
	text-align: left
}

@media print {
	input.noPrint {
		display: none;
	}
}
</style>
</head>
<script type="text/javascript">
dojo.require("dijit.layout.ContentPane");
dojo.require("dijit.layout.LayoutContainer");       
</script>
<u:body>

	<tr dojoType="dijit.layout.LayoutContainer"
		style="width: 100%; height: 100%; overflow: auto">
	<tr dojoType="dijit.layout.ContentPane" layoutAlign="client">
		<br>
		<s:iterator value="shipmentsForPrintTag">
			<tr>
				<table class="dealertable" width="100%" cellspacing="0"
					cellpadding="0">
					<tr>
						<td width="59%" class="centresubtitle"><s:text
							name="label.common.internalUseOnly" /></td>

						<td width="40%" class="centresubtitle"><s:text
							name="label.returnToAddress" /></td>
					</tr>
				</table>
				<table class="dealertable" width="100%" cellspacing="0"
					cellpadding="0">
					<tr>
						<td width="59%">
						<table>
							<tr>
								<td class="labelBold"><s:text
									name="columnTitle.partShipperShipmentGenerated.shipmentNumber" />
								:</td>
								<td class="labelBoldAndBig"><s:property value="shipmentId" /></td>
							</tr>
							
						</table>
						</td>

						<td width="40%" class="labelLargeFont"><s:text
							name="label.shipment.returnPartTo" />:<br>
							<s:property value="dealer.name" /><br>
							<s:property value="dealer.address.addressLine1" /><br>
						<s:property value="dealer.address.addressLine2" /><br>
						<s:property value="dealer.address.city" />, <s:property
							value="dealer.address.state" /><br>
						<s:property value="dealer.address.country" /> - <s:property
							value="dealer.address.zipCode" />
						<br>
						</td>
					</tr>
				</table>
			</tr>

			<!-- Dealer is same for all the parts in the partReturnVOs -->
			<br>
			<br>
			<tr>
				<table class="centresubtitle" width="100%" cellspacing="0"
					cellpadding="0">
					<tr>
						<td><s:text	name="columnTitle.manageWarehouse.wareHouseName" /></td>
					</tr>
				</table>
			</tr>
			<tr>
				<table class="dealertable" cellspacing="0" cellpadding="0">
					<tr>
						<td><br>
						</td>
					</tr>
					<tr>
						<td class="labelBold" width="20%"><s:text
							name="columnTitle.manageWarehouse.wareHouseName" />:</td>
						<td class="labelNormal" width="80%"><s:property
							value="returnLocation.code" /></td>
					</tr>
					<tr>
						<td class="labelBold"><s:text name="label.common.address" />:</td>
						<td class="labelNormal"><!-- CAn we do away with <br> without making use of additional <td>s ?? -->
						<s:property value="returnLocation.address.addressLine1" /><br>
						<s:property value="returnLocation.address.addressLine2" /> <br>
						<s:property value="returnLocation.address.addressLine3" /> <br>
						<s:property value="returnLocation.address.city" />, <s:property
							value="returnLocation.address.state" /><br>
						<s:property value="returnLocation.address.country" /> - <s:property
							value="returnLocation.address.zipCode" />
						</td>
					</tr>
					<tr>
						<td><br>
						</td>
					</tr>
					<tr>
						<td class="labelBold"><s:text
							name="label.common.dealerNumber" />:</td>
						<td class="labelNormal"><s:property
							value="dealer.dealerNumber" /></td>

					</tr>

				</table>
			</tr>
			<br>
			<br>
			<tr class="centresubtitle"></tr>
			<!-- HACK. Need to fix it -->
			<tr>
				<table class="centresubtitle" width="100%" cellspacing="0"
					cellpadding="0">
					<tr>
						<td><s:text name="title.partReturnConfiguration.listOfParts" /></td>
					</tr>
				</table>
			</tr>
			<s:iterator value="claimsInShipment">
				<table class="dealertable" cellpadding="0" cellspacing="0">
					<tr>
						<td><br>
						</td>
					</tr>
					<tr>
						<td>
						<table cellspacing="0" cellpadding="0" width="100%">
							<tr>
								<td class="labelBold" width="20%"><s:text
									name="label.common.claimNumber" />:</td>
								<b>
								<td class="labelBoldAndBig" width="100%"><s:property
									value="claim.claimNumber" /></td>
								</b>
							</tr>
							<tr>
								<td><br>
								</td>
							</tr>
							<tr>
								<td class="labelBold" width="20%"><s:text
									name="label.common.workOrderNumber" />:</td>
								<td class="labelNormal" width="100%"><s:property
									value="claim.workOrderNumber" /></td>
							</tr>
							<tr>
								<td class="labelBold" width="20%"><s:text
									name="label.common.serialNumber" />:</td>
								<td height="15" colspan="5">
								<table>
									<s:set name="rowCounter" value="0" />
									<s:iterator value="claim.claimedItems">
										<s:if test="#rowCounter==0">
											<tr>
										</s:if>
										<s:if test="#rowCounter<4">
											<td class="labelNormal" width="100%"><s:property
												value="itemReference.referredInventoryItem.serialNumber" />
											</td>
											<s:set name="rowCounter" value="#rowCounter+1" />
										</s:if>
										<s:else>
											<td class="labelNormal" width="100%"><s:property
												value="itemReference.referredInventoryItem.serialNumber" />
											</td>
											<s:set name="rowCounter" value="0" />
										</s:else>
										<s:if test="#rowCounter!=0">
											</tr>
										</s:if>
									</s:iterator>
								</table>
								</td>
							</tr>

							<tr>
								<td class="labelBold" width="20%"><s:text
									name="columnTitle.common.model" />:</td>
								<td class="labelNormal" width="100%">
									<s:if test="claim.itemReference.referredInventoryItem != null">
										<s:property	value="claim.itemReference.referredInventoryItem.ofType.model.name" />
									</s:if>
									<s:else>
										<s:property	value="claim.itemReference.model.name" />
									</s:else>
								</td>
							</tr>
							<tr>
								<td class="labelBold" width="20%"><s:text
									name="label.common.shipmentDate" />:</td>
								<td class="labelNormal" width="100%"><s:property
									value="claim.itemReference.referredInventoryItem.shipmentDate" />
								</td>
							</tr>
							<tr>
								<td class="labelBold" width="20%"><s:text
									name="label.common.submitDate" />:</td>
								<td class="labelNormal" width="100%"><s:property
									value="claim.filedOnDate" /></td>
							</tr>
							<tr>
								<td class="labelBold" width="20%"><s:text
									name="label.supplier.partDetails" /></td>
								<td>
								<table>
									<tr>
										<td class="partsHeadertitle" width="13%"><s:text
											name="columnTitle.dueParts.part_no" /></td>
										<td class="partsHeadertitle" width="17%"><s:text
											name="columnTitle.dueParts.description" /></td>
										<td class="partsHeadertitle" width="11%"><s:text
											name="columnTitle.dueParts.qty" /></td>
										
									</tr>
									<s:iterator value="partReturnVOList">
										<tr>
											<td class="labelNormal"><s:property value="partNumber" />
											</td>
											<td class="labelNormal"><s:property value="description" />
											</td>
											<td class="labelNormal"><s:property	value="numberOfParts" />
											</td>
										</tr>
									</s:iterator>
								</table>
								</td>
							</tr>
						</table>
						</td>
					</tr>
				</table>
			</s:iterator>
		</s:iterator>	
	</tr>
		<br></br>
<!--	Need to put a pageBreak here-->	
	
	<div class="buttonWrapperPrimary"><input type="button"
		name="Submit2232" value='<s:text name="button.common.cancel" />'
		class="buttonGeneric" onClick="window.close()" /> <input
		type="button" name="Submit2231"
		value="<s:text name="button.common.print"/>" class="buttonGeneric"
		onclick="window.print()" /></div>


</u:body>
</html>
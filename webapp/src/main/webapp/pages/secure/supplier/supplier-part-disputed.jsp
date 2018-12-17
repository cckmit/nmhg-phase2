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

<html>
<head>
    <title><s:text name="title.common.warranty"/></title>
    <s:head theme="twms"/>
    <script type="text/javascript" src="scripts/ui-ext/common/tabs.js"></script>
    <script>
        function closeTab() {
            top.tabManager._closeAndCleanUpTab(getTabHavingId(getTabDetailsForIframe().tabId));
        }
        function popupRecoveryAmountDetails(win_name) {
            window.open(win_name, 'RecoveryAmountDetails',
                    'toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbar=yes,resizable=no,copyhistory=no,Width=630,height=210,top=183,left=230');
        }
        function expandParts(partIndex) {
            var tableData = document.getElementById("td" + partIndex);
            var section = document.getElementById("div" + partIndex);
            if (section.style.display == "none") {
                section.style.display = "block";
                tableData.className = "sectionOpenSM";
            } else {
                section.style.display = "none";
                tableData.className = "sectionCloseSM";
            }
        }
        function expandPartsSection(partSection, count) {
            var partClass = document.getElementById("td" + partSection).className;
            if (partClass == "tdsectionCloseSM") {
                for (var i = 0; i != count; i++)
                {
                    document.getElementById("div" + i).style.display = "block";
                    ;
                    document.getElementById("td" + i).className = "sectionOpenSM";
                    document.getElementById("td" + partSection).className = "tdsectionOpenSM";
                }
            } else {
                for (var i = 0; i != count; i++)
                {
                    document.getElementById("div" + i).style.display = "none";
                    ;
                    document.getElementById("td" + i).className = "sectionCloseSM";
                    document.getElementById("td" + partSection).className = "tdsectionCloseSM";
                }
            }
        }
    </script>
    <u:stylePicker fileName="base.css"/>
    <u:stylePicker fileName="inventory.css"/>
    <u:stylePicker fileName="detailDesign.css"/>
    <u:stylePicker fileName="master.css"/>
</head>

<u:body>
	<div dojoType="dijit.layout.LayoutContainer"
		style="width: 100%; height: 100%; margin: 0; padding: 0; overflow-X: hidden;">
	<div dojoType="dijit.layout.ContentPane" layoutAlign="client">
	<div id="separator"></div>
	<div class="bgColor" >
	
	<table width="100%" border="0" cellspacing="0" cellpadding="0" class="grid">
		<tr>
			<td >
			<div >
			<table width="100%" border="0" cellspacing="1" cellpadding="0">
				<tr>
					<td colspan="6" nowrap="nowrap" class="sectionTitle"><s:text name="label.supplier.partDetails"/></td>
				</tr>
				<tr>
					<td>
					<table width="100%" border="0" cellpadding="0" cellspacing="0" class="grid borderForTable">
						<tr>
							<td width="5%" class="tdsectionCloseSM" id="tdoemPartReplaced" 
								onclick="expandPartsSection('oemPartReplaced','<s:property value="partsToBeShown.size()"/>');">&nbsp;&nbsp;&nbsp;&nbsp;</td>
							<td width="18%" valign="middle" nowrap="nowrap" class="colHeader"><s:text name="label.supplier.supplierPartNumber"/></td>
							<td width="12%" valign="middle" nowrap="nowrap" class="colHeader"><s:text name="label.common.quantity"/></td>
							<td width="20%" valign="middle" nowrap="nowrap" class="colHeader"><s:text name="label.common.description"/>&nbsp;</td>
							<td width="20%" valign="middle" nowrap="nowrap" class="colHeader"><s:text name="label.supplier.commentSRA"/></td>
							<td width="10%" valign="middle" nowrap="nowrap"
								class="colHeaderRightAlign"><s:text name="label.supplier.recoveryAmount"/></td>
							<td width="15%" nowrap="nowrap" class="colHeader"><s:text name="label.common.action"/></td>
							<td width="22%" nowrap="nowrap" class="colHeader"><s:text name="label.common.reason"/></td>
						</tr>
						<s:iterator value="partsToBeShown" status="partStatus"
							id="oemPartReplaced">
							<tr>
								<td width="5%" id="td<s:property value="%{#partStatus.index}"/>"
									class="sectionCloseSM" align="left"
									onclick="expandParts('<s:property value="%{#partStatus.index}"/>');">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
								<td width="18%" nowrap="nowrap" ><s:property
									value="#oemPartReplaced.supplierPartReturn.supplierItem.number" /></td>
								<td width="12%" ><s:property
									value="#oemPartReplaced.numberOfUnits" /></td>
								<td width="20%" ><s:property
									value="#oemPartReplaced.supplierPartReturn.supplierItem.description" /></td>
								<td width="20%" ><s:property
									value="#oemPartReplaced.supplierPartReturn.sraComment" /></td>
								<td width="10%" class="tableDataAmount"><s:property
									value="#oemPartReplaced.supplierPartReturn.totalRecoveredCost" />
									</td>
								<td width="15%" ><s:property
									value="#oemPartReplaced.supplierPartReturn.status.status" /></td>
								<td width="22%" ><s:property
									value="#oemPartReplaced.supplierPartReturn.supplierComment" /></td>
							</tr>
							<tr>
								<td colspan="7">
								<div id="div<s:property value="%{#partStatus.index}"/>"
									style="display:none">
								<table width="100%" border="0" cellspacing="1" cellpadding="0">
									<tr>
										<td width="4%" >&nbsp;</td>
										<td width="28%" class="subSectionTitle">
										<div align="center"><s:text name="label.common.details"/></div>
										</td>
										<td width="19%" >&nbsp;</td>
										<td width="49%" class="subSectionTitle">
										<div align="center"><s:text name="label.common.individual"/></div>
										</td>
									</tr>
									<tr>
										<td width="4%" >&nbsp;</td>
										<td width="28%" valign="top">
										<table width="100%" border="0" cellspacing="1" cellpadding="0" class="noBorderCellbg">
											<tr>
												<td width="60%" ><s:text name="label.supplier.supplierPartNumber"/></td>
												<td width="40%" ><s:property
													value="supplierPartReturn.supplierItem.number"></s:property></td>
											</tr>
											<tr>
												<td width="60%" ><s:text name="label.common.cost"/></td>
												<td width="40%" ><s:property
													value="#oemPartReplaced.cost()"></s:property></td>
											</tr>
											<tr>
												<td width="60%" ><s:text name="label.common.price"/></td>
												<td width="40%" ><s:property
													value="#oemPartReplaced.getSupplierPartCost()"></s:property></td>
											</tr>
											<tr>
												<td width="60%" ><s:text name="label.common.shipcontract"/></td>
												<td width="40%" ><s:property
													value="#oemPartReplaced.supplierPartReturn.contract.name"></s:property></td>
											</tr>
											<tr>
												<td width="60%" ><s:text name="label.common.ship"/></td>
												<td width="40%" ><input type="checkbox"
													checked="checked" disabled="disabled"></td>
											</tr>
										</table>
										</td>
										<td width="19%" >&nbsp;</td>
										<td width="49%">
										<table width="100%" border="0" cellspacing="1" cellpadding="0" class="noBorderCellbg">
											<tr>
												<td width="25%" class="subSectionTitle"><s:text name="label.common.costElement"/></td>
												<td width="15%" class="subSectionTitle">
												<div align="right"><s:text name="label.common.share"/></div>
												</td>
												<td width="30%" class="subSectionTitle"><s:text name="label.contract.contractValue"/></td>
												<td width="25%" class="subSectionTitle"><s:text name="label.common.actualValue"/></td>
											</tr>
											<s:set name="totalCount%{#partsStatus.index}"
												value="%{#oemPartReplaced.supplierPartReturn.costLineItems.size()}"
												scope="page"></s:set>

											<s:iterator
												value="#oemPartReplaced.supplierPartReturn.costLineItems"
												status="lineItemStatus" id="costLineItem">
												<s:hidden
													name="oemPartsReplaced[%{#partsStatus.index}].supplierPartReturn.costLineItems[%{#lineItemStatus.index}]"
													value="%{#costLineItem.id}" />
												<tr>
													<td ><s:property
														value="%{#costLineItem.section.name}" /></td>
													<td class="tableDataAmount" valign="middle"><s:property
														value="#oemPartReplaced.supplierPartReturn.getAcutalCostForSection(#costLineItem.section.name)" />
													</td>
													<td class="tableDataAmount" valign="middle"><s:property
														value="#oemPartReplaced.supplierPartReturn.getCostAfterApplyingContractForSection(#costLineItem.section.name)" />
													</td>
													<td class="tableDataAmount" valign="middle"><s:property
														value="#oemPartReplaced.supplierPartReturn.getRecoveredCostForSection(#costLineItem.section.name)" />
													</td>
												</tr>
											</s:iterator>
											<tr>
												<td width="25%" class="totalAmountRightalign"><s:text name="label.common.total"/></td>
												<td width="15%" class="totalAmountTextRightalign"><s:property
													value="#oemPartReplaced.supplierPartReturn.getTotalActualCost()" />
												</td>
												<td width="30%" class="totalAmountTextRightalign"><s:property
													value="#oemPartReplaced.supplierPartReturn.getTotalCostAfterApplyingContract()" />
												</td>
												<td width="25%" class="totalAmountTextRightalign"><s:property
													value="#oemPartReplaced.supplierPartReturn.getTotalRecoveredCost()" />
												</td>
											</tr>
										</table>
										</td>
									</tr>
								</table>
								</div>
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
</div>
</div>
</div>
</u:body>
</html>

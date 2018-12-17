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
<script type="text/javascript">
function expandParts(partIndex){
	var tableData = document.getElementById("td"+partIndex);
	var section = document.getElementById("div"+partIndex);
	if(section.style.display=="none"){
		section.style.display="block";
		tableData.className = "sectionOpenSM";
	}else{
		section.style.display="none";
		tableData.className = "sectionCloseSM";
	}
}


</script>
<div class="section_header"><s:text name="label.supplier.partDetails" /></div>
<table width="100%" border="0" cellpadding="0" cellspacing="0" class="grid borderForTable" align="center" style="margin:5px;width:99%">
					<tr>
						<td width="17%" valign="middle" nowrap="nowrap" class="warColHeader"><s:text name="supplierPartNumber"/></td>
						<td width="12%" valign="middle" nowrap="nowrap" class="warColHeader"><s:text name="quantity" /></td>
						<td width="20%" valign="middle" nowrap="nowrap" class="warColHeader"><s:text name="description" /></td>
						<td width="20%" valign="middle" nowrap="nowrap" class="warColHeader"><s:text name="SRAComment" /></td>
						<td width="10%" valign="middle" nowrap="nowrap"
							class="warColHeader"><s:text name="recoveryAmount" /></td>
						<td width="15%" nowrap="nowrap" class="warColHeader"><s:text name="location" /></td>
						<td width="22%" nowrap="nowrap" class="warColHeader"><s:text name="dueDays" /></td>
					</tr>
					<s:iterator value="recoveredPartsToBeShipped" status="partStatus"
						id="recoverablePart">
						<tr>
							<td width="17%" nowrap="nowrap" ><s:property
								value="supplierItem.number" /></td>
							<td width="12%" ><s:property
								value="quantity" /></td>
							<td width="20%" ><s:property
								value="supplierItem.description" /></td>
							<td width="20%" ><s:property
								value="recoveryClaim.comments" />&nbsp;</td>
							<td width="10%"><s:property
								value="recoveryClaim.totalRecoveredCost" />
								</td>
							<td width="15%" >
							<s:property value="oemPart.partReturns[0].returnLocation.code"/>
							</td>
							<td width="22%" >
							<s:property value="oemPart.partReturns[0].dueDays"/>
							</td>
						</tr>
						
					</s:iterator>
				</table>
				

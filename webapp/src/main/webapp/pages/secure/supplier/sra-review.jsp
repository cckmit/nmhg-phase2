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

    <u:stylePicker fileName="master.css"/>
    <u:stylePicker fileName="base.css"/>
    <u:stylePicker fileName="inventory.css"/>
    <u:stylePicker fileName="detailDesign.css"/>
</head>
<u:body>
<div dojoType="dijit.layout.LayoutContainer"
     style="width: 100%; height: 100%; margin: 0; padding: 0; overflow-X: hidden;">
     
<s:form action="supplierRecoveryAdminReview_submit.action">    
<div dojoType="dijit.layout.ContentPane" layoutAlign="client" style="width: 97%; background: #FCFCFC; border: #EFEBF7 1px solid; margin: 5px; padding: 5px; overflow-x: hidden">

 <table width="100%" border="0" cellspacing="0" cellpadding="0">
        <tr>
          <td colspan="4" nowrap="nowrap" class="sectionTitle"><s:text name="label.claim.claimDetails"/> </td>
        </tr>
			<tr>
				<td>
				<table width="100%" border="0" cellspacing="0" cellpadding="0">
					
					<s:set name="lastClaimId" value=""/>
					
					<s:iterator id="taskInstance" value="taskInstances"
						status="taskInstanceStatus">
						<s:set value="%{#taskInstance.getVariable('part')}"
							name="oemPartReplaced" />
						<s:set value="%{#taskInstance.getVariable('claim')}"
							name="claim" />
					<s:if test="#lastClaimId != #claim.id">
						<tr>
							<td>
							<table width="100%" border="0" cellpadding="0" cellspacing="1"
								bgcolor="#DCD5CC">
								<tr>
									<td colspan="7" valign="middle" nowrap="nowrap"
										>
									<table width="100%" border="0" cellpadding="0" cellspacing="0">
										<tr>
											<td width="15%" class="label"><s:text name="label.common.claimNumber"/>:</td>
											<td width="20%" class="labelNormal"><s:property
												value="%{#claim.id}" /></td>
											<td width="18%" class="label"><s:text name="label.common.serialNumber"/></td>
											<td class="labelNormal">TODO</td>
										</tr>
										<tr>
											<td width="15%" nowrap="nowrap" class="label"><s:text name="label.inventory.modelNumber"/>:</td>
											<td width="20%" class="labelNormal">TODO</td>
											<td width="18%" nowrap="nowrap" class="label"><s:text name="label.claim.workOrderNumber"/>:</td>
											<td class="labelNormal">TODO</td>
										</tr>
									</table>
									</td>
								</tr>
								<tr>
									<td width="3%" valign="middle" nowrap="nowrap"
										class="colHeader" align="center"><input name="checkbox232"
										type="checkbox" value="checkbox" checked="checked" /></td>
									<td width="12%" valign="middle" nowrap="nowrap"
										class="colHeader"><s:text name="label.common.serialNumber"/></td>
									<td width="12%" valign="middle" nowrap="nowrap"
										class="colHeader"><s:text name="label.common.partNumber"/></td>
									<td width="50%" valign="middle" nowrap="nowrap"
										class="colHeader"><s:text name="label.common.description"/></td>
									<td width="7%" valign="middle" nowrap="nowrap"
										class="colHeader"><s:text name="label.common.quantity"/></td>
									<td width="14%" valign="middle" nowrap="nowrap"
										class="colHeaderCenterAlign"><s:text name="label.common.action"/></td>
									<td width="14%" nowrap="nowrap" class="colHeader"><s:text name="label.common.reason"/></td>
								</tr>
							<s:set name="lastClaimId" value="#claim.id"/>
				</s:if>
							
								<tr>
									<td width="3%" nowrap="nowrap" valign="middle" align="center"
										><s:checkbox
										name="oemPartsReplaced[%{#taskInstanceStatus.index}]"
										fieldValue="%{#oemPartReplaced.id}" value="true"/></td>
									<td width="12%" nowrap="nowrap" ><s:property
										value="#oemPartReplaced.itemReference.referredInventoryItem.serialNumber" /></td>
									<td width="12%" nowrap="nowrap" ><s:property
										value="#oemPartReplaced.supplierPartReturn.supplierItem.number" /></td>
									<td width="50%" ><s:property
										value="#oemPartReplaced.itemReference.unserializedItem.description" /></td>
									<td width="7%" ><s:property
										value="#oemPartReplaced.numberOfUnits" /></td>
									<td width="14%"  valign="center">
									<select name="taskName" >
										<option value="RECOVERY_COMPLETE"><s:text name="label.supplier.recoveryComplete"/></option>
						                <option value="SEND_FOR_RECOVERY"><s:text name="label.supplier.sendForRecovery"/></option>
									</select></td>
									<td width="14%"  valign="cenger">
									<s:textfield name="taskName"></s:textfield></td>
								</tr>
						<s:if test="#lastClaimId != #claim.id">												
						</table>
						</td>
						</tr>
						<tr>
							<td height="4">
							<div id="separatorbgColor"></div>
							</td>
						</tr>
						
						</s:if>
								
					</s:iterator>
				</table>
				</td>
			</tr>
			</table>
			</div>
			</td>
		</tr>

</table>
	<div class="buttonWrapperPrimary">

		<input type="submit" class="buttonGeneric" name="transitionTaken" value="<s:text name="label.common.submit"/>" />
	</div>

</div>
</s:form>
</div>
	
</u:body>
</html>

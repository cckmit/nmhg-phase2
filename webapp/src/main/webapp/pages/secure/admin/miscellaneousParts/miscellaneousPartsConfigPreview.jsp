<%@ page contentType="text/html"%>
<%@ page pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="t" uri="twms"%>
<%@ taglib prefix="u" uri="/ui-ext"%>
<%@ taglib prefix="authz" uri="authz"%>

<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<s:head theme="twms" />
<title><s:text name="label.uom.mapping"/></title>
<u:stylePicker fileName="yui/reset.css" common="true" />
<u:stylePicker fileName="layout.css" common="true" />
<u:stylePicker fileName="common.css" />
<u:stylePicker fileName="form.css" />
<u:stylePicker fileName="adminPayment.css" />
<u:stylePicker fileName="base.css" />
    <script type="text/javascript">
  	  	dojo.require("dijit.layout.ContentPane");
      	dojo.require("dijit.layout.LayoutContainer"); 
	</script>
</head>
<u:body>
	<div dojoType="dijit.layout.LayoutContainer"
		style="width: 100%; height: 100%; background: white; overflow-y: auto;">
	<div dojoType="dijit.layout.ContentPane" layoutAlign="client">
	<u:actionResults />		
		<div class="policyRegn_section_div">
		<div class="policy_section_heading"><s:text name="section.heading.Miscellaneoous.part.definition"/> </div>
		
		<div class="mainTitle" style="margin-top:5px;"><s:text name="section.heading.Miscellaneoous.part.criteria"/> </div>
		<div class="borderTable">&nbsp;</div>
		<table width="100%" border="0" cellspacing="0" cellpadding="0" class="grid" style="margin-top:-10px;">

				<tr>
					<td ><s:text name="label.miscellaneousParts.configName"/> : </td>							
					<td><s:property value="miscItemCrit.configName" /></td>
				</tr>
				<tr>
					<td ><s:text name="label.miscellaneousParts.dealerOrDealerGroup"/> : </td>
					<s:if test="!(miscItemCrit.serviceProvider.name == null)" >							
					<td><s:property value="miscItemCrit.serviceProvider.name" /></td>
					</s:if>
					<s:else>
					<td><s:property value="miscItemCrit.dealerGroup.name" /></td>
					</s:else>
			    </tr>												  
		</table>
			
		
		<div class="mainTitle"><s:text name="section.heading.Miscellaneoous.part.configuration"/> </div>
		<table width="100%" border="0" cellspacing="0" cellpadding="0" class="grid borderForTable">
				<tr class="row_head">						
					<td ><s:text name="label.miscPart.partNumber"/>  </td>
					<td ><s:text name="label.miscPart.partDescription"/>  </td>
					<td ><s:text name="label.miscPart.partPrice"/>  </td>
					<td ><s:text name="label.miscPart.partUom"/>  </td>
					<td ><s:text name="label.miscPart.partQuantity"/>  </td>
					
				</tr>
				<s:iterator value="miscItemCrit.itemConfigs" status="configIterator">
				<tr>
					<td><s:property value="miscellaneousItem.partNumber" /></td>							
					<td><s:property value="miscellaneousItem.description" /></td>
					<td>
						<s:if test="miscItemRates != null">
							<s:iterator value="miscItemRates" status="iter">
								<s:property value="rate" /><BR>
							</s:iterator>		
						</s:if>
					</td>
					<td><s:property value="uom" /></td>
					<td><s:property value="tresholdQuantity" /></td>
				</tr>
				</s:iterator>
		</table>
	</div>	
	</div>
	</div>
</u:body>
</html>
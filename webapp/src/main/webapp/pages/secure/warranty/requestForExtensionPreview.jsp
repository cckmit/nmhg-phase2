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
<title><s:text name="title.reduced.coverage.request.Extension"/></title>
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
	<s:form name="requestForExtensionForm" action="saveReducedCoverageDealerInput">
		<div class="policyRegn_section_div">
		<div class="policy_section_heading"><s:text name="label.reduced.coverage"/> : <s:text name="title.reduced.coverage.request.Extension"/></div>
		<div style="padding:10px 0px 10px 0px;">
		<table width="100%" border="0" cellspacing="0" cellpadding="0" class="grid ">

					<tr>						
						<td width="15%" class="labelStyle"><s:text name="label.common.serialNumber"/> : 
						</td>
						<td width="75%"><s:property value="itemsWithReducedCoverage[0].inventoryItem.serialNumber" />
						</td>
					</tr>
					
					<s:if test = "(getLoggedInUser().isInternalUser())">
					<tr>						
						<td width="15%" class="labelStyle"><s:text name="label.common.dealerName"/> : 
						</td>
						<td width="75%"><s:property value="itemsWithReducedCoverage[0].inventoryItem.currentOwner.name" />
						</td>
					</tr>
					
					
					
					
					<tr>						
						<td width="15%" class="labelStyle"><s:text name="label.common.dealerNumber"/> : 
						</td>
						<td width="75%"><s:property value="itemsWithReducedCoverage[0].inventoryItem.dealer.dealerNumber" />
						</td>
					</tr>	
					</s:if>
							
					
					<tr>
						<td colspan="2" >						
							<table width="95%" border="0" cellspacing="0" cellpadding="0"  
								class="grid borderForTable">
								<tr><td style="padding:0">&nbsp;</td></tr>
									<tr class="row_head">
										<th><s:text name="columnTitle.listPolicies.warranty_type"/></th>
										<th><s:text name="columnTitle.listPolicies.standard_coverage"/></th>
										<th><s:text name="columnTitle.listPolicies.reduction_in_coverage"/></th>
										<th><s:text name="columnTitle.listPolicies.plan_name"/></th>
									</tr>
									<s:iterator value="itemsWithReducedCoverage[0].policiesWithReducedCoverage">
										<tr>
											<td><s:property value="%{getText(policyDefinition.warrantyType.displayValue)}" /></td>
											<td><s:property
												value="policyDefinition.coverageTerms.monthsCoveredFromDelivery" />&nbsp;
													</td>																	
											<td>
											<s:if test="reductionInCvg[policyDefinition.code] != null">											
											<s:property
												value="reductionInCvg[policyDefinition.code]" />
											Months
											</s:if>
											<s:else>
											<s:property
												value="reductionInCvgDays[policyDefinition.code]" />
												Days																						
											</s:else>
											</td>
											<td><s:property value="policyDefinition.description" /></td>
										</tr>
									</s:iterator>
									<tr><td style="padding:0">&nbsp;</td></tr>
							</table>						
						</td>
					</tr>
					<tr>
					<td colspan="2" >					
					<table width="60%" border="0" cellspacing="0" cellpadding="0"  
								class="grid borderForTable">
								
					  <thead >
						  <th class="warColHeader non_editable"><s:text name="label.common.user"/>  </th>
						  <th class="warColHeader non_editable"><s:text name="columnTitle.common.status"/>  </th>
						  <th class="warColHeader non_editable"><s:text name="label.common.comments"/> </th>
						  <th class="warColHeader non_editable"> <s:text name="columnTitle.common.updateDate" /></th>
					  </thead>			
					  <s:iterator value="itemsWithReducedCoverage[0].audits" >
					  <tr>
							<td>
							<s:if test="assignedBy != null">
								<s:property value="assignedBy.name" />
							</s:if> <s:else>
							    SYSTEM
						    </s:else>
						    </td>
							<td><s:property value="status" /></td>
							<td><s:property value="comments" /></td>
							<td><s:property value="createDateAsCalendarDate" /> </td>
						</tr>
					  </s:iterator>			
					</table>
					</td>
					</tr>									  
		</table>
		</div>
		</div>
	</s:form>	
	</div>
	</div>
</u:body>
</html>
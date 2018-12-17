<%@ page contentType="text/html"%>
<%@ taglib prefix="sd" uri="/struts-dojo-tags"%>
<%@ page pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="t" uri="twms"%>
<%@ taglib prefix="u" uri="/ui-ext"%>

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
<script type="text/javascript" src="scripts/RepeatTable.js"></script>
<script type="text/javascript" src="scripts/AdminToggle.js"></script>
<script type="text/javascript" src="scripts/adminAutocompleterValidation.js"></script>

    <script type="text/javascript">
  	  	dojo.require("dijit.layout.ContentPane");
      	dojo.require("dijit.layout.LayoutContainer"); 
	</script>
</head>
<u:body>
	<div dojoType="dijit.layout.LayoutContainer"
		style="width: 100%; height: 100%; background: white; overflow-y: auto;">
	<div dojoType="dijit.layout.ContentPane" layoutAlign="client">
	<s:form name="miscConfigForm" action="saveMiscellaneousParts">
	<u:actionResults />			
	<s:hidden name="dealerGroupSelected" id="isDealerGroup" />
		<div class="policy_section_heading"><s:text name="section.heading.Miscellaneoous.part.definition"/> </div>
		<div class="policyRegn_section_div">
		<div class="admin_section_subheading"><s:text name="section.heading.Miscellaneoous.part.criteria"/> </div>
		<table width="100%" border="0" cellspacing="0" cellpadding="0" class="policyRegn_table">
				<tr>
					<td ><s:text name="label.miscellaneousParts.configName"/> : </td>							
				    <td><s:textfield name="miscItemCrit.configName" /></td>
				</tr>

				<tr>
					<td class="admin_data_table" width="20%">
					<div id="dealerLabel"><s:text name="label.common.dealer" />:</div>
					<div id="dealerGroupLabel"><s:text
						name="label.common.dealerGroupLabel" />:</div>
					<div id="toggle" style="cursor: pointer;">
					<div id="toggleToDealerGroup" class="clickable"><s:text
						name="toggle.common.toDealerGroup" /></div>
					<div id="toggleToDealer" class="clickable"><s:text
						name="toggle.common.toDealer" /></div>
					</div>
					</td>
					<td width="80%">
					<div id="dealer"><sd:autocompleter href='list_part_return_Dealers.action' id='dealerAutoComplete' name='dealerCriterion' loadOnTextChange='true' loadMinimumCount='1' showDownArrow='false' autoComplete='false' /></div>
					<div id="dealerGroup"><sd:autocompleter href='list_part_return_DealerGroupsInDealerRates.action' id='dealerGroupAutoComplete' name='dealerGroupName' loadOnTextChange='true' loadMinimumCount='1' showDownArrow='false' autoComplete='false' /></div>
					</td>
				</tr>
        		              
		</table>
		</div>		
		<div class="policyRegn_section_div">
		<div class="admin_section_subheading"><s:text name="section.heading.Miscellaneoous.part.configuration"/> </div>
		<table width="100%" border="0" cellspacing="0" cellpadding="0" class="policyRegn_table">
				<tr>						
					<td ><s:text name="label.miscPart.partNumber"/>  </td>
					<td ><s:text name="label.miscPart.partDescription"/>  </td>
					<td ><s:text name="label.miscPart.partPrice"/>  </td>
					<td ><s:text name="label.miscPart.partUom"/>  </td>
					<td ><s:text name="label.miscPart.partQuantity"/>  </td>
					
				</tr>
				<s:iterator value="miscItemCrit.itemConfigs" status="iter">
				<tr>
						<td style="padding-left:5px; height:20px">
			            	<sd:autocompleter id='miscItemCrit.itemConfigs_%{#iter.index}_partNumber' href='list_miscellaneousItems.action' keyName='products[#myindex]' name='miscItemCrit.itemConfigs[#myindex]' loadOnTextChange='true' loadMinimumCount='0' showDownArrow='false' autoComplete='off' cssStyle='width: 80%' cssClass='admin_selections showSuggestionsOnTop' value='%{miscItemCrit.itemConfigs[#myindex].partNumber}' />
			            </td>								   		                 																				
						<td><s:property value="miscellaneousItem.description" /></td>													    		
						<td><s:textfield name="miscItemCrit.itemConfigs[#iter].costPrice" /></td>
						<td><s:select list="mappedUomList" listKey="baseUom" listValue="mappedUomDescription" name="miscItemCrit.itemConfigs[#iter].uom" /></td>
						<td><s:textfield name="miscItemCrit.itemConfigs[#iter].tresholdQuantity" /></td>							

				</tr>
				</s:iterator>
		</table>
		</div>
	</s:form>
	<script type="text/javascript">
	    dojo.addOnLoad(function() {
	      <s:if test="dealerGroupSelected">
	          showDealerGroup();
	      </s:if>
	      <s:else>
	          showDealer();
	      </s:else>
	
	        dojo.connect(dojo.byId("toggleToDealer"), "onclick", function() {
	            showDealer();
	        });
	        dojo.connect(dojo.byId("toggleToDealerGroup"), "onclick", function() {
	            showDealerGroup();
	        });
	    });
	</script>				
	</div>
	</div>
</u:body>
</html>
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
<%@ taglib prefix="sd" uri="/struts-dojo-tags"%>
<%@page pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="t" uri="twms"%><%@taglib prefix="u" uri="/ui-ext"%>
<%@ taglib prefix="authz" uri="authz" %>

<html>
<head>
    
    <title><s:text name="title.common.warranty"/></title>
    <s:head theme="twms"/>
    
    <script type="text/javascript" src="scripts/jscalendar/calendar.js"></script>
    <script type="text/javascript" src="scripts/jscalendar/lang/calendar-en.js"></script>
    <script type="text/javascript" src="scripts/jscalendar/calendar-setup.js"></script>

    <link href="scripts/jscalendar/calendar-brown.css" rel="stylesheet" type="text/css">
    <u:stylePicker fileName="adminPayment.css"/>
    <u:stylePicker fileName="common.css"/>
    <u:stylePicker fileName="form.css"/>    

    
    <script type="text/javascript" src="scripts/AdminToggle.js"></script>
</head>
<u:body>
<u:actionResults/>
  <s:form name="baseForm" id="baseForm" theme="twms" action="save_part_return_definition" method="post" validate="true">
  
  <s:hidden name="id" /> 
  <s:hidden name="dealerGroupSelected" id="isDealerGroup"/>
  <s:hidden name="itemGroupSelected" id="isItemGroup"/>
  <div class="admin_section_div" style="width:100%;margin:5px;">
  <div class="admin_section_heading">
    <s:text name="label.partReturnConfiguration" />
  </div>
 
  <div class="mainTitle" style="margin:10px 0px 10px 0px;">
    <s:text name="label.partReturnConfiguration.criteria" />
  </div>
  <div class="borderTable">&nbsp;</div>
  <div style="margin-top:-10px; ">
  <table width="100%" class="grid" cellpadding="0" cellspacing="0">
    <tr>
      <td class="admin_data_table" width="20%">
        <div id="dealerLabel"><s:text name="label.common.dealer" />:</div>
		<div id="dealerGroupLabel"><s:text name="label.common.dealerGroupLabel" />:</div>
		<div id="toggle" style="cursor: pointer;">
		  <div id="toggleToDealerGroup" class="clickable">
		    <s:text name="toggle.partReturnConfiguration.toDealerGroup" />
		  </div>
		  <div id="toggleToDealer" class="clickable">
		    <s:text name="toggle.partReturnConfiguration.toDealer" />
		  </div>
		</div>
	  </td>
	  
	  <td>
		<div id="dealer">
		  <sd:autocompleter href='list_part_return_service_providers.action' id='dealerAutoComplete' name='chosenDealer' keyValue="%{partReturnDefinition.forCriteria.dealerCriterion.dealer.id}" key="%{partReturnDefinition.forCriteria.dealerCriterion.dealer.id}" loadOnTextChange='true' loadMinimumCount='2' value='%{chosenDealer}' showDownArrow='false' autoComplete='false' />
		</div>
		<div id="dealerGroup">
		  <sd:autocompleter href='list_part_return_DealerGroupsInPartReturn.action' id='dealerGroupAutoComplete' name='dealerGroupName' value='%{dealerGroupName}' loadOnTextChange='true' showDownArrow='false' emptyOption='true' autoComplete='false' />
		</div>
	  </td>
      
      <td class="admin_data_table" width="15%"><s:text name="label.partReturnConfiguration.warrantyType" />:</td>
	  <td><s:select list="warrantyTypes" name="partReturnDefinition.forCriteria.warrantyType" listKey="type" listValue="%{getText(displayValue)}" headerKey="ALL" headerValue="All" /></td>
	</tr>
	<tr>
	  <td class="admin_data_table"><s:text name="label.partReturnConfiguration.claimType" />:</td>
	  <td>
	  <s:select list="claimTypes" name="partReturnDefinition.forCriteria.claimType" listKey="type.toUpperCase()" listValue="%{getText(displayType)}" headerKey="ALL" headerValue="All"  value="%{currentClaimType}"/>
	  </td>
	  <td class="admin_data_table"><s:text name="label.partReturnConfiguration.productType" />:</td>
	  <td>
	    <sd:autocompleter href='list_part_return_Products.action' name='chosenProduct' loadOnTextChange='true' loadMinimumCount='1' value='%{chosenProduct}' showDownArrow='false' autoComplete='false' cssClass='admin_selections' />
	  </td>
	</tr>
	
	<tr>
	  <td class="admin_data_table" width="20%">
	    <div id="itemLabel"><s:text name="label.common.itemLabel" />:</div>
		<div id="itemGroupLabel"><s:text name="label.common.itemGroupLabel" />:</div>
		<div id="toggleToItemGroup" class="clickable"><s:text name="toggle.common.toItemGroup" /></div>
		<div id="toggleToItem" class="clickable"><s:text name="toggle.common.toPart" /></div>
	  </td>
	  <td>
	    <div id="item">
	      <sd:autocompleter href='list_part_return_ItemCriterions.action' id='itemAutoComplete' name='partCriterion' loadOnTextChange='true' loadMinimumCount='1' keyName='partReturnDefinition.itemCriterion.item.number' value='%{partReturnDefinition.itemCriterion.item.number}' showDownArrow='false' autoComplete='false' />
		</div>
		<div id="itemGroup">
		  <sd:autocompleter href='list_part_return_ItemGroupsForPartReturns.action' id='itemGroupAutoComplete' name='itemGroupName' keyName='partReturnDefinition.itemCriterion.itemGroup.name' value='%{partReturnDefinition.itemCriterion.itemGroup.name}' loadOnTextChange='true' loadMinimumCount='1' showDownArrow='false' autoComplete='false' />
	    </div>
	  </td>
	</tr>
	<tr>
		<td class="labelStyle"> <s:text name="label.partReturnConfiguration.shippingInstructions"/>:</td>
		<td>	        
	        <t:textarea id="shipping_instructions" rows="4" cols="40" maxLength="4000" name="partReturnDefinition.shippingInstructions"/>
	    </td>
	    <td class="labelStyle"> <s:text name="label.partReturnConfiguration.receiverInstructions"/>:</td>
	    <td>
	        <t:textarea id="receiver_instructions" rows="4" cols="40" maxLength="4000" name="partReturnDefinition.receiverInstructions"/>
	    </td>
	</tr>
	<tr>
		<td class="labelStyle"><s:text name="label.partReturnConfiguration.comments"/>:</td>
		<td colspan="3">	        
	        <t:textarea id="comments" rows="4" cols="40" maxLength="4000" name="partReturnDefinition.comments"/>
	    </td>
	</tr>
  </table>
  <div class="policy_section_div" width="20%">
	<div class="section_header"  width="20%">
		<s:text name="label.partReturnConfiguration.dealerExclusion"></s:text>
	</div>
	<u:repeatTable id="dealers_exclude_table" cssClass="grid borderForTable" theme="twms"  cellspacing="0" cellpadding="0" cssStyle="margin:5px;width:20%">
		<thead>
			<tr class="title">
				<th class="warColHeader" width="40%"><s:text name="columnTitle.common.dealer"/></th>
				<th class="warColHeader" width="5%">
					<u:repeatAdd id="dealers_adder" theme="twms">
						<img id="addDealer" src="image/addRow_new.gif" border="0" style="cursor: pointer;" title="<s:text name="label.common.addRow"/>"/>
					</u:repeatAdd>
				</th>
			</tr>
		</thead>
		<u:repeatTemplate id="mybody" value="partReturnDefinition.excludedDealers" index="index" theme="twms">
	        <tr index="#index">
				<td><sd:autocompleter
						href='list_part_return_service_providers.action'
						id='dealerExcludeAutoComplete_#index'
						name='selectedDealerExclusions[#index]'
						keyName="selectedDealerExclusions[#index]" keyValue="%{id}"
						key="%{id}" loadOnTextChange='true' emptyOption='true'
						value='%{name}' showDownArrow='false' autoComplete='false' />
				</td>
				<td>
	                <u:repeatDelete id="dealers_deleter_#index"  theme="twms">
						<img id="deleteDealer" src="image/remove.gif" border="0" style="cursor: pointer;" title="<s:text name="label.common.deleteRow" />"/>
	                </u:repeatDelete>
            	</td>
			</tr>
		</u:repeatTemplate>
	</u:repeatTable>
</div>
<div class="policy_section_div" width="20%">
	<div class="section_header"  width="20%">
		<s:text
			name="label.partReturnConfiguration.dealerGroupExclusion"></s:text>
	</div>
	<u:repeatTable id="dealer_groups_exclude_table"
		cssClass="grid borderForTable" theme="twms" cellspacing="0"
		cellpadding="0" cssStyle="margin:5px;width:20%">
		<thead>
			<tr class="title">
				<th class="warColHeader" width="40%"><s:text
						name="label.common.dealerGroupLabel" /></th>
				<th class="warColHeader" width="5%"><u:repeatAdd
						id="dealerGroup_adder" theme="twms">
						<img id="addDealerGroup" src="image/addRow_new.gif"
							border="0" style="cursor: pointer;"
							title="<s:text name="label.common.addRow"/>" />
					</u:repeatAdd></th>
			</tr>
		</thead>
		<u:repeatTemplate id="mybody"
			value="partReturnDefinition.excludedDealerGroups"
			index="index" theme="twms">
			<tr index="#index">
				<td><sd:autocompleter
						href='list_part_return_dealer_groups.action'
						id='dealerGroupAutoComplete_#index'
						name='selectedDealerGroupExclusions[#index]' 
						keyName='selectedDealerGroupExclusions[#index]' 
						keyValue="%{id}" key="%{id}" value='%{selectedDealerGroupExclusions[#index].name}'
						loadOnTextChange='true' showDownArrow='false'
						emptyOption='true' autoComplete='false' /></td>
				<td><u:repeatDelete id="dealer_groups_deleter_#index"
						theme="twms">
						<img id="deleteDealerGroup" src="image/remove.gif" border="0"
							style="cursor: pointer;"
							title="<s:text name="label.common.deleteRow" />" />
					</u:repeatDelete></td>
			</tr>
		</u:repeatTemplate>
	</u:repeatTable>
</div>
</div>

  <jsp:include flush="true" page="partReturnConfiguration.jsp"></jsp:include>
  </div>
  <br/>
  <div align="center"><input id="cancel_btn" class="buttonGeneric"
      type="button" value="<s:text name='button.common.cancel'/>"
	  onclick="javascript:closeTab(getTabHavingLabel(getMyTabLabel()));" />
  <s:submit cssClass="buttonGeneric"
	  value="%{getText('button.common.save')}" action="save_part_return_definition" /></div>
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
  <script type="text/javascript">
      dojo.addOnLoad(function() {
	      <s:if test="itemGroupSelected">
	          showItemGroup();
	      </s:if>
	      <s:else>
	  	      showItem();
	      </s:else>
	      dojo.connect(dojo.byId("toggleToItem"), "onclick", function() {
	          showItem();
	      });
	      dojo.connect(dojo.byId("toggleToItemGroup"), "onclick", function() {
	          showItemGroup();
	      });
	  });
  </script>
<authz:ifPermitted resource="warrantyAdminPartReturnConfigurationReadOnlyView">
	<script type="text/javascript">
	    dojo.addOnLoad(function() {
	        for ( var i = 0; i < dojo.query("input, button, textarea, select, text", dojo.byId('baseForm')).length; i++) {
	            dojo.query("input, button, textarea, select, text", dojo.byId('baseForm'))[i].disabled=true;
	        }
	    });
	</script>
</authz:ifPermitted>
</u:body>
</html>
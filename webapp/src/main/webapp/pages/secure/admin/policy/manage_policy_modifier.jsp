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
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@taglib prefix="t" uri="twms"%><%@taglib prefix="u" uri="/ui-ext"%>

<html>
<head>
    <title><s:text name="title.common.warranty"/></title>
    <s:head theme="twms"/>
    <u:stylePicker fileName="adminPayment.css"/>
    
    <script type="text/javascript" src="scripts/AdminToggle.js"></script>
    <script type="text/javascript" src="scripts/adminAutocompleterValidation.js"></script>
    <script type="text/javascript">
        function validate(inputComponent) {

        }

    </script>
    <style type="text/css">
        div.noTopBorder {
            border: 1px solid #888888;
            border-top: none !important;
        }

        div.padded {
            padding: 3px;
            padding-bottom: 5px;
        }
    </style>
</head>
<u:body>
<u:actionResults/>
<s:form action="create_policy_price_modifier" onsubmit="return  validateAutocompleters();">
<s:hidden name="id" />
<s:hidden name="dealerGroupSelected" id="isDealerGroup"></s:hidden>
<s:hidden name="policyNameSelected" id="isPolicyName"></s:hidden>
<div class="admin_section_div" style="border:0">
    <div class="admin_section_heading"><s:text name="label.managePolicy.policyModifier" /></div>
    <div class="admin_subsection_div">
        <div class="admin_section_subheading"><s:text name="label.managePolicy.criteria" /></div>
        <table width="100%" class="admin_selections">
        		<tr>
			<td colspan="2">
				<div id="dealerDiv">
					<table width="100%">
					<tr>
						<td class="admin_data_table" width="20%">
							<s:text name="label.common.dealer"/>:
							<div id="toggleToDealerGroup" class="clickable">
								<s:text name="toggle.common.toDealerGroup" />
							</div>
						</td>
						<td width="80%">
							<sd:autocompleter href='list_part_return_Dealers.action' id='dealerAutoComplete' name='dealerCriterion' loadOnTextChange='true' loadMinimumCount='1' showDownArrow='false' autoComplete='false' />
						</td>	
					</tr>
					</table>	
				</div>
				<div id="dealerGroupDiv">
				<table width="100%">
					<tr>
						<td class="admin_data_table" width="20%">
							<s:text name="label.common.dealerGroupLabel"/>:
							<div id="toggleToDealer" class="clickable">
								<s:text name="toggle.common.toDealer"/>
							</div>
						</td>
						<td width="80%">	
							<sd:autocompleter href='list_part_return_DealerGroupsInDealerRates.action' id='dealerGroupAutoComplete' name='dealerGroupName' loadOnTextChange='true' loadMinimumCount='1' showDownArrow='false' autoComplete='false' />
						</td>
					</tr>	
					</table>			
				</div>
			</td>
		</tr>
		<tr>
			<td class="admin_data_table">
				<s:text name="label.managePolicy.productType" />:
			</td>
			<td >
				<sd:autocompleter id='productType' href='list_part_return_Products.action' name='productType' loadOnTextChange='true' loadMinimumCount='1' showDownArrow='false' autoComplete='false' cssClass='admin_selections' />
			</td>
		</tr>
		<tr>
			<td class="admin_data_table">
				<s:text name="columnTitle.managePolicy.warrantyType" />:
			</td>
			<td>
				<s:select id="warrantyType" cssClass="dojoComboBox" name="definition.forCriteria.warrantyType" list="warrantyTypes" listkey="type" listValue="%{getText(displayValue)}"/>
			</td>
		</tr>
		<tr>
			<td class="admin_data_table">
			  <s:text name="columnTitle.managePolicy.registrationType" />:
			</td>
			<td >
			  <s:select id="registrationType" cssClass="dojoComboBox" 
			  	name="definition.forCriteria.warrantyRegistrationType" 
			  	list="registrationTypes"/>
			</td>
		</tr>
		<tr>
			<td class="admin_data_table" width="20%">
				<s:text name="label.managePolicy.customerState" />:
			</td>
			<td width="80%">
				<s:textfield name="definition.forCriteria.customerState"/>
			</td>
		</tr>
        

			<tbody>
			<tr>
			<td colspan="2">
			<div id="policyDefDiv">
				<u:repeatTable id="polcyDefinitionsTable" cellpadding="2px" cssStyle="border : 1px solid #EFEBF7; border-collapse: collapse;"
					cellspacing="0" width="100%" theme="twms" cssClass="repeat">
					<thead class="admin_entry_table" style="border:1px solid #EFEBF7">
						<tr class="admin_table_header">
							<th>
								<s:text name="label.common.policyNames"/>
							</th>
							<th width="2%">
							<u:repeatAdd id="policyDefinitionAdder" theme="twms">
								<img id="addPolicyDefinition" src="image/addRow_new.gif" border="0" style="cursor: pointer;" title="<s:text name="label.managePolicy.add" />" />
							</u:repeatAdd>
							</th>
						</tr>
					</thead>
					<u:repeatTemplate id="polcyDefinitionsBody" value="policyDefinitions" index="policyDefinitionIndex" theme="twms">
						<tr index="#policyDefinitionIndex" style="border: 1px solid #EFEBF7">
							<td style="border-left:1px solid #DCD5CC;">
								<sd:autocompleter href='list_policy_definition_codes.action' id='policyDefinitionsAutoComplete#policyDefinitionIndex' name='policyDefinitions' loadOnTextChange='true' loadMinimumCount='0' showDownArrow='false' autoComplete='false' value='%{policyDefinitions[#policyDefinitionIndex]}' />
							</td>
							<td width="2%" style="border-left:1px solid #DCD5CC;">
								<u:repeatDelete id="policyDefinitionDeleter_#policyDefinitionIndex" theme="twms">
									<img id="deletePolicyDefinition" src="image/remove.gif" border="0" style="cursor: pointer;" title="<s:text name="label.common.deleteEntry" />"/>
								</u:repeatDelete>
							</td>
						</tr>
					</u:repeatTemplate>
				</u:repeatTable>
				<div id="toggleToPolicyLabel" class="clickable noTopBorder padded">
					<s:text name="toggle.common.toPolicyLabel"/>
				</div>
			</div>
		</td>
		</tr>
		<tr>
		<td colspan="2">
			<div id="policyLabDiv">
				<u:repeatTable id="polcyLabelsTable" cssStyle="border : 1px solid #EFEBF7; border-collapse: collapse;" cssClass="repeat"
					cellpadding="2px" cellspacing="0" width="100%" theme="twms">
					<thead class="admin_entry_table" style="border:1px solid #EFEBF7; margin:3px;">
						<tr class="admin_table_header">
							<th>
								<s:text name="label.managePolicy.policyLabels"/>
							</th>
							<th width="2%">
								<u:repeatAdd id="policyLabelAdder" theme="twms">
									<img id="addPolicyLabel" src="image/addRow_new.gif" border="0" style="cursor: pointer;" title="<s:text name="label.managePolicy.add" />" />
								</u:repeatAdd>
							</th>
						</tr>
					</thead>
					<u:repeatTemplate id="polcyLabelsBody" value="policyLabels" index="policyLabelIndex" theme="twms">
					<tr index="#policyLabelIndex" style="border: 1px solid #EFEBF7">
						<td style="border-left:1px solid #DCD5CC;">
							<sd:autocompleter href='list_policy_label_names.action' id='policyLabelsAutoComplete_#policyLabelIndex' name='policyLabels' loadOnTextChange='true' loadMinimumCount='0' showDownArrow='false' autoComplete='false' value='%{policyLabels[#policyLabelIndex]}' />
						</td>
						<td width="2%" style="border-left:1px solid #DCD5CC;">
							<u:repeatDelete id="policyLabelDeleter_#policyLabelIndex" theme="twms">
								<img id="deletePolicyLabel" src="image/remove.gif" border="0" style="cursor: pointer;" title="<s:text name="label.common.deleteEntry" />"/>
							</u:repeatDelete>
						</td>
					</tr>
					</u:repeatTemplate>
				</u:repeatTable>
				<div id="toggleToPolicyName" class="clickable noTopBorder padded">
					<s:text name="toggle.common.toPolicyName"/>
				</div>
			</div>
		</td>
		</tr>
		</tbody>
          			
        </table>
    </div>
    
    <div class="admin_subsection_div">
    	<div class="admin_section_subheading"><s:text name="label.common.rates"/></div>
        <u:repeatTable id="myTable" cssClass="admin_entry_table" cellpadding="0" cellspacing="0" width="100%" theme="twms">
            <thead>
                <tr class="admin_table_header">
                    <th width="68%"><s:text name="label.managePolicy.purchaseDate"/></th>
                    <th width="29%"><s:text name="label.managePolicy.rate"/></th>
                    <th width="3%" aligh="center">
                        <u:repeatAdd id="adder" theme="twms">
                            <img id="addPrice" src="image/addRow_new.gif" border="0" style="cursor: pointer; padding-right:4px;" title="<s:text name="label.managePolicy.add" />" />
                      </u:repeatAdd>
                    </th>
                </tr>
            </thead>
            <u:repeatTemplate id="mybody" value="rates" index="myindex" theme="twms">
                <tr index="#myindex">
                    <td  width="68%">
                        <s:hidden name="rates[#myindex]" value="%{rates[#myindex].id}"></s:hidden>
                        <table width="100%" border="0" cellspacing="0" cellpadding="0">
                            <tr>
                                <td width="8%" style="color:grey; font-weight:bold;"><s:text name="label.common.from"/>:</td>
                                <td width="25%">
                                    <sd:datetimepicker name='rates[#myindex].duration.fromDate' value='%{rates[#myindex].duration.fromDate}' id='startDate_#myindex' />
                                </td>
                                <td width="4%" style="color:grey; font-weight:bold;"><s:text name="label.common.to"/>:</td>
                                <td width="63%">
                                    <sd:datetimepicker name='rates[#myindex].duration.tillDate' value='%{rates[#myindex].duration.tillDate}' id='endDate_#myindex' />
                                </td>
                            </tr>
                        </table>
                    </td>
                    <td width="30%" style="border-left:1px solid #DCD5CC;">
                    	<s:textfield id="rates[#myindex].rate" name="rates[#myindex].rate" value="%{value}" /> %
                    </td>
                    <td width="2%" style="border-left:1px solid #DCD5CC;">
                        <u:repeatDelete id="deleter_#myindex" theme="twms">
                            <img id="deletePrice" src="image/remove.gif" border="0" style="cursor: pointer;padding-right:4px;" title="<s:text name="label.common.deleteEntry" />"/>
                        </u:repeatDelete>
                    </td>
                </tr>
            </u:repeatTemplate>
        </u:repeatTable>
    </div>
</div>
    <div align="center">
	  <input id="cancel_btn" class="buttonGeneric" type="button" value="<s:text name='button.common.cancel'/>"
				onclick="javascript:closeTab(getTabHavingLabel(getMyTabLabel()));" />	  
      <s:if test="%{definition.id == null}">
            <s:submit cssClass="buttonGeneric" id="createButton" value="%{getText('button.common.save')}"  action="create_policy_price_modifier"/>
      </s:if>
      <s:else>
            <s:submit cssClass="buttonGeneric" id="deleteButton" value="%{getText('button.common.delete')}"  action="delete_policy_price_modifier"/>
            <s:submit cssClass="buttonGeneric" id="updateButton" value="%{getText('button.common.save')}"  action="update_policy_price_modifier"/>
      </s:else>
    </div>

</s:form>
<script type="text/javascript">
	function showDealersGrp(){
		dojo.html.show(dojo.byId("dealerGroupDiv"));
		dojo.html.hide(dojo.byId("dealerDiv"));
	}
	
	function showDealers(){
		dojo.html.show(dojo.byId("dealerDiv"));
		dojo.html.hide(dojo.byId("dealerGroupDiv"));
	}

	function showPolicyDefinition(){
		dojo.html.show(dojo.byId("policyDefDiv"));
		dojo.html.hide(dojo.byId("policyLabDiv"));
	}	
	
	function showPolicyLabel() {
		dojo.html.show(dojo.byId("policyLabDiv"));
		dojo.html.hide(dojo.byId("policyDefDiv"));
	}

	dojo.addOnLoad(function(){
		<s:if test="dealerGroupSelected">
    		showDealersGrp();
    	</s:if>
		<s:else>
    		showDealers();
    	</s:else>
    	
		<s:if test="policyNameSelected">
			showPolicyDefinition();
		</s:if>
		<s:else>
			showPolicyLabel();
		</s:else>
	});
	
	 dojo.connect(dojo.byId("toggleToPolicyName"), "onclick" , function(){
	 	dojo.byId('isPolicyName').value = true;
	 	showPolicyDefinition();
	 });
	 
	 dojo.connect(dojo.byId("toggleToPolicyLabel"), "onclick", function(){
	 	dojo.byId('isPolicyName').value = false;
	 	showPolicyLabel();
	 });
	
	dojo.connect(dojo.byId("toggleToDealer"), "onclick", function() {
	    showDealers();
	});
	dojo.connect(dojo.byId("toggleToDealerGroup"), "onclick", function() {
	    showDealersGrp();
	});
</script>
</u:body>
</html>

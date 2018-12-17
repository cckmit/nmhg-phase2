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
<%@ taglib prefix="authz" uri="authz" %>

<html>
<head>
    <title><s:text name="title.common.warranty"/></title>
    <s:head theme="twms"/>
    <u:stylePicker fileName="adminPayment.css"/>
     <u:stylePicker fileName="common.css"/>
      <u:stylePicker fileName="base.css"/>
    <!-- Javascripts and stylesheets for Admin section -->
    <script type="text/javascript" src="scripts/AdminToggle.js"></script>
    <script type="text/javascript" src="scripts/adminAutocompleterValidation.js"></script>
    <script type="text/javascript">
        dojo.require("dijit.layout.LayoutContainer");
        dojo.require("dijit.layout.ContentPane");
        function validate(inputComponent) {

        }
    </script>
    <script type="text/javascript">
        dojo.addOnLoad(function(){
            dojo.query("input[id^=hrate]").forEach(function(i){
                resetCurrency(i);
            });
            dojo.query("input[id^=trate]").forEach(function(i){
                resetCurrency(i);
            });
            dojo.query("input[id^=drate]").forEach(function(i){
                resetCurrency(i);
            });

        });
        function resetCurrency(item){
            var rateId = item.id;
            var currencyCode = dojo.byId("curr_"+rateId).value;
            setCurrency(rateId, currencyCode);
        }
    	function setCurrency(rateId, currencyCode, unset) {
        	if(dijit.byId(rateId).get('value') == -1.00) {
        		dijit.byId(rateId).set('value','');
        		dojo.byId("curr_"+rateId).value = '';
        	}
    		dojo.connect(dijit.byId(rateId),"onChange",function(changedVal){
                console.log("Value Changed - " + rateId + " - " + changedVal + " - " + dojo.byId("curr_"+rateId).value);
                if(isNaN(parseInt(changedVal)))return;
		        if(typeof dijit.byId(rateId).get('value') == 'undefined') {
		        	dojo.byId("curr_"+rateId).value = '';
		        }else if(dojo.byId("curr_"+rateId).value == '') {
		        	dojo.byId("curr_"+rateId).value = currencyCode;
		        }
	        });
    	}
    </script>
     <style>
    .admin_selections td{
    padding-bottom:10px;
    }
    </style>
</head>
<u:body>
    <div dojoType="dijit.layout.LayoutContainer" >
        <div dojoType="dijit.layout.ContentPane" layoutAlign="center" style="overflow-x: hidden">
<s:form name="baseForm" id="baseFormId" theme="twms" validate="true" method="post">
<u:actionResults/>
<s:hidden name="id" id="blahblah"/>
<s:hidden name="dealerGroupSelected" id="isDealerGroup"></s:hidden>
<div class="admin_section_div" style="margin:5px;width:100%;">
    <div class="admin_section_heading"><s:text name="label.manageRates.travelRateConfig" /></div>
   
        <div class="mainTitle">
        <s:text name="label.manageRates.conditions" /></div>
        <div class="borderTable">&nbsp;</div>
       <div style="margin-top:-10px;">
        <table width="99%" class="grid" cellspacing="0" cellpadding="0">
            <tr>
                <td class="admin_data_table" width="20%">
                    <div id="dealerLabel">
                               <s:text name="label.common.dealer"/>:
                           </div>
                           <div id="dealerGroupLabel">
                               <s:text name="label.common.dealerGroupLabel"/>:
                           </div>
                           <div id="toggle" style="cursor:pointer;">
                               <div id="toggleToDealerGroup"  class="clickable">
                                   <s:text name="toggle.common.toDealerGroup"/>
                               </div>
                               <div id="toggleToDealer" class="clickable">
                                   <s:text name="toggle.common.toDealer"/>
                               </div>
                           </div>
                </td>
                <td width="80%">
                    <div id="dealer">
                      <sd:autocompleter href='list_part_return_Dealers.action' id='dealerAutoComplete' name='dealerCriterion' loadOnTextChange='true' loadMinimumCount='1' showDownArrow='false' autoComplete='false' />
              </div>
              <div id="dealerGroup">
                  <sd:autocompleter href='list_part_return_DealerGroupsInDealerRates.action' id='dealerGroupAutoComplete' name='dealerGroupName' loadOnTextChange='true' loadMinimumCount='1' showDownArrow='false' autoComplete='false' />
              </div>
                </td>
                </tr>
            <tr>
            <td class="admin_data_table">
                    <s:text name="label.common.seriesDescription" />:
                </td>
                <td >
                    <sd:autocompleter id='productType' href='list_part_return_Products.action' name='productType' loadOnTextChange='true' loadMinimumCount='1' showDownArrow='false' autoComplete='false' />
                </td>
            </tr>

                <tr>
                <td class="admin_data_table">
                    <s:text name="label.manageRates.warrantyType" />:
                </td>
                <td >
                      <s:select name="definition.forCriteria.warrantyType" theme="twms" list="warrantyTypes"
                          id="warrantyType" cssStyle="width:130px;" listkey="type" listValue="%{getText(displayValue)}" headerKey="" 
                          headerValue='%{getText("dropdown.common.all")}' />
                    <script type="text/javascript">
                        dojo.byId("warrantyType").value = "<s:property value="definition.forCriteria.warrantyType" />";
                    </script>
                </td>
                </tr>

              <tr>
                <td class="admin_data_table">
                    <s:text name="label.manageRates.claimType" />:
                </td>
                <td >
                    <select id="claimType" name="definition.forCriteria.claimType" style="width:15%;">
                           <option value="ALL" ><s:text name="dropdown.common.all"/></option>
                         <s:iterator value="claimTypes">
                            <option value='<s:property value="type.toUpperCase()" />'><s:text name="getText(getDisplayType())"/></option>
                         </s:iterator>
                    </select>
                    <script type="text/javascript">
                        <s:if test="definition.id==null">
                            dojo.byId("claimType").value="ALL";
                        </s:if>
                        <s:else>
                        dojo.byId("claimType").value = "<s:property value="definition.forCriteria.claimType.type.toUpperCase()" />";
                        </s:else>
                    </script>
                </td>                
            </tr>
            <tr>
	                    <td class="admin_data_table">
	                        <s:text name="label.warrantyAdmin.customerType"/>:
	                    </td>
	                    <td >
	                        <s:select list="customerTypes" name="definition.customerType" listKey="key" listValue="value" headerKey="ALL" headerValue="All"  />	                        
	                    </td>
            </tr>

        </table>
 
  </div>
    <div class="mainTitle"><s:text name="label.common.rates"/></div>
    
        <u:repeatTable id="myTable" cssClass="grid borderForTable" cellpadding="0" cellspacing="0" width="100%" theme="twms">
            <thead>
                <tr class="admin_data_table">
                    <th rowspan="2" class="colHeader"><div align="left"><s:text name="label.manageRates.repairDate"/></div></th>
                    <th colspan="4" class="colHeader"><div align="center"><s:text name="label.manageRates.travelRate"/></div></th>
                </tr>
                    <tr class="admin_data_table">
                    <th width="20%" class="colHeader"><s:text name="label.manageRates.perHour"/></th>
                    <th width="20%" class="colHeader"><s:text name="label.manageRates.byDistance"/></th>
                    <th width="20%" class="colHeader"><s:text name="label.manageRates.byTrip"/></th>
                    <th width="2%"  class="colHeader">
                        <u:repeatAdd id="adder" theme="twms">
                            <img id="addPrice" src="image/addRow_new.gif" border="0" style="cursor: pointer; padding-right:4px; " title="<s:text name="label.manageRates.addRateEntry" />" />
                        </u:repeatAdd>
                    </th>
                </tr>
            </thead>
            <u:repeatTemplate id="mybody" value="rates" index="myindex" theme="twms">
                <tr index="#myindex">
                    <td style="border:1px solid #EFEBF7;">
                        <s:hidden name="rates[#myindex]"></s:hidden>
                        <table width="50%" border="0" cellpadding="0" cellspacing="0" class="grid">
                            <tr style="height:20px;">
                                <td width="8%"  style="color:grey; font-weight:bold"><s:text name="label.common.from"/>:</td>
                                <td width="45%">
                                    <sd:datetimepicker name='rates[#myindex].duration.fromDate' value='%{rates[#myindex].duration.fromDate}' id='startDate_#myindex' />
                                </td>
                                
                            </tr>
                            <tr>
                            <td width="4%"  style="color:grey; font-weight:bold"><s:text name="label.common.to"/>:</td>
                                <td width="43%">
                                    <sd:datetimepicker name='rates[#myindex].duration.tillDate' value='%{rates[#myindex].duration.tillDate}' id='endDate_#myindex' />
                                </td>
                            </tr>
                        </table>
                    </td>
                 
                    <td style="border:1px solid #EFEBF7;" align="center">
                     <s:if test="travelRateValues != null">                            
                            <s:iterator value="travelRateValues" status="iter">
	                         <t:money id="hrate_%{#iter.index}_#myindex" 
	                         	name="rates[#myindex].travelRateValues[%{#iter.index}].hourlyRate" 
	                         	value="%{hourlyRate}" defaultSymbol="travelRate.currency.symbol"></t:money>
	                         	<BR>
                        </s:iterator><BR>
                        </s:if>
                        <s:else>
                          	<s:iterator value="tRateValues" status="iter">
                            <t:money id="hrate_%{#iter.index}_#myindex" 
	                         	name="rates[#myindex].travelRateValues[%{#iter.index}].hourlyRate" 
	                         	value="%{hourlyRate}" defaultSymbol="travelRate.currency.symbol"></t:money><BR>
                        	</s:iterator><BR>
                        </s:else>
                          <s:checkbox name="rates[#myindex].valueIsHourFlatRate" />
                        <s:text name ="label.admin.travelFlatRate" /><BR><BR>
                     </td>
                      <td style="border:1px solid #EFEBF7;" align="center">
                     <s:if test="travelRateValues != null">
                        <s:iterator value="travelRateValues" status="iter">
	                         <t:money id="drate_%{#iter.index}_#myindex" 
	                         	name="rates[#myindex].travelRateValues[%{#iter.index}].distanceRate" 
	                         	value="%{distanceRate}" defaultSymbol="travelRate.currency.symbol"></t:money><BR>
                        </s:iterator><BR>
                        </s:if>
                        <s:else>
                          	<s:iterator value="tRateValues" status="iter">
	                          <t:money id="drate_%{#iter.index}_#myindex" 
	                         	name="rates[#myindex].travelRateValues[%{#iter.index}].distanceRate" 
	                         	value="%{distanceRate}" defaultSymbol="travelRate.currency.symbol"></t:money><BR>
                        	</s:iterator><BR>
                        </s:else>
                          <s:checkbox name="rates[#myindex].valueIsDistanceFlatRate" />
                        <s:text name ="label.admin.travelFlatRate" /><BR><BR>
                     </td>
                      <td style="border:1px solid #EFEBF7;" align="center">
                     <s:if test="travelRateValues != null">
                        <s:iterator value="travelRateValues" status="iter">
								<t:money id="trate_%{#iter.index}_#myindex" 
	                         	name="rates[#myindex].travelRateValues[%{#iter.index}].tripRate" 
	                         	value="%{tripRate}" defaultSymbol="travelRate.currency.symbol"></t:money><BR>
                        </s:iterator><BR>
                        </s:if>
                        <s:else>
                          	<s:iterator value="tRateValues" status="iter">
	                          <t:money id="trate_%{#iter.index}_#myindex" 
	                         	name="rates[#myindex].travelRateValues[%{#iter.index}].tripRate" 
	                         	value="%{tripRate}" defaultSymbol="travelRate.currency.symbol"></t:money><BR>
                        	</s:iterator><BR>
                        </s:else>
                        <s:checkbox name="rates[#myindex].valueIsTripFlatRate" />
                        <s:text name ="label.admin.travelFlatRate" /> <BR><BR>
                     </td>
                    
                    <td style="border:1px solid #EFEBF7;">
                        <u:repeatDelete id="deleter_#myindex" theme="twms">
                            <img id="deletePrice" src="image/remove.gif" border="0" style="cursor: pointer;padding-right:4px;" title="<s:text name="label.manageRates.deleteRateEntry" />"/>
                        </u:repeatDelete>
                    </td>
                </tr>
            </u:repeatTemplate>
        </u:repeatTable>
        <br/>
    </div>

    <div align="center" style="margin-top:10px;">
    	<input id="cancel_btn" class="buttonGeneric" type="button" value="<s:text name='button.common.cancel'/>"
				onclick="javascript:closeTab(getTabHavingLabel(getMyTabLabel()));" />
        <s:if test="%{definition.id == null}">
            <s:submit cssClass="buttonGeneric" id="createButton" value="%{getText('button.common.save')}"
                      action="create_tr_configuration"/>
        </s:if>
        <s:else>
            <s:submit cssClass="buttonGeneric" id="deleteButton" value="%{getText('button.common.delete')}"  action="delete_tr_configuration"/>
            <s:submit cssClass="buttonGeneric" id="updateButton" value="%{getText('button.common.save')}"
                      action="update_tr_configuration" />
        </s:else>
    </div>
</s:form>
</div>    
        </div>
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

<authz:ifPermitted resource="warrantyAdminTravelRatesReadOnlyView">
	<script type="text/javascript">
	    dojo.addOnLoad(function() {
	        for ( var i = 0; i < dojo.query("input, button, textarea, select, text", dojo.byId('baseFormId')).length; i++) {
	            dojo.query("input, button, textarea, select, text", dojo.byId('baseFormId'))[i].disabled=true;
	        }
	    });
	</script>
</authz:ifPermitted>
</u:body>
</html>

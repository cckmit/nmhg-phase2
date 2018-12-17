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
<%@taglib prefix="s" uri="/struts-tags" %>
<%@taglib prefix="t" uri="twms"%><%@taglib prefix="u" uri="/ui-ext"%>
<%@ taglib prefix="authz" uri="authz" %>
<%
	response.setHeader("Pragma", "no-cache");
	response.addHeader("Cache-Control", "must-revalidate");
	response.addHeader("Cache-Control", "no-cache");
	response.addHeader("Cache-Control", "no-store");
	response.setDateHeader("Expires", 0);
%>
<html>
<head>
    <title><s:text name="title.common.warranty"/></title>
    <s:head theme="twms"/>
    <!-- Scripts and stylesheet for the Calendar -->
    <script type="text/javascript" src="scripts/jscalendar/calendar.js"></script>
    <script type="text/javascript" src="scripts/jscalendar/lang/calendar-en.js"></script>
    <script type="text/javascript" src="scripts/jscalendar/calendar-setup.js"></script>

    <link href="scripts/jscalendar/calendar-brown.css" rel="stylesheet" type="text/css">
    <u:stylePicker fileName="adminPayment.css"/>
    <!-- End of scripts and stylesheet for the Calendar -->
    
    <!-- Javascripts and stylesheets for Admin section -->
    <script type="text/javascript" src="scripts/AdminToggle.js"></script>
    <script type="text/javascript" src="scripts/adminAutocompleterValidation.js"></script>
    <script type="text/javascript">
    	dojo.require("dijit.form.DateTextBox");
        function validate(inputComponent) {

        }
    </script>
    <style>
    .admin_selections td{
    padding-bottom:10px;
    }
    .widthAutoTextField .dijitTextBox{width:auto !important;}
	.money_symbol{width:42px;float:left;}
    </style>
</head>
<u:body smudgeAlert="false">
<s:form name="baseForm" id="baseFormId" theme="twms" validate="true" action="create_part_prices_configuration" method="post">
<u:actionResults/>
<s:hidden name="id" />
<div class="admin_section_div" style="margin:5px;padding-bottom:10px;width:98.8%">
    <div class="admin_section_heading"><s:text name="label.manageRates.partPriceConfig" /></div>
  
        <div class="borderTable">&nbsp;</div>
        <div style="margin-top:-10px;">
        <table width="100%" class="grid" cellspacing="0" cellpadding="0">
            <tr>
                <td class="mainTitle" width="20%"  >
                    <div >
                               <s:text name="label.nmhg.part.number"/>:
                  </div>
                </td>
                <td width="80%">
                    <div id="partNumber">
                      <sd:autocompleter href='list_nmhg_part_number.action' id='partNumberAutoComplete' name='nmhgPartNumber' keyName='nmhgPartNumber' keyValue='%{nmhgPartNumber}'  value='%{definition.nmhg_part_number.number}' loadOnTextChange='true' loadMinimumCount='1' showDownArrow='false' value="%{nmhgPartNumber}" notifyTopics="/part/changed" />
              </div>
                </td>
             </tr>
                <tr>
            <td class="mainTitle">
                        <s:text name="label.miscellaneous.partDescription"/>:
                </td>
                <td >
                <span id="partDescription">
                 <s:property value="definition.nmhg_part_number.description" />
                 </span>
                </td>
            </tr>
                 <tr>
            <td class="mainTitle">
                        <s:text name="label.warrantyAdmin.comments"/>:
                </td>
                <td >
                 <s:textarea name="definition.comments" cols="100" rows="6"/>
                </td>
            </tr>
        </table>
  </div>
  <br>
    <div class="mainTitle" >
      <br>
    <s:text name="label.common.rates"/></div>
         <u:repeatTable id="myTable" cssClass="admin_entry_table borderForTable widthAutoTextField" 
     cellpadding="0" cellspacing="0" width="98%" theme="twms" cssStyle="margin:5px;">
            <thead>
                <tr class="admin_table_header">
                    <th width="37%" class="colHeader"><s:text name="label.manageRates.repairDate"/></th>
                    <th width="15%" class="colHeader"><s:text name="label.manageRates.standardCostPrice"/></th>
                    <th width="15%" class="colHeader"><s:text name="label.manageRates.dealerNetPrice"/></th>
                    <th width="15%" class="colHeader"><s:text name="label.manageRates.plantCostPrice"/></th>
                   <th width="15%" class="colHeader">
                        <u:repeatAdd id="adder" theme="twms">
                            <img id="addPrice" src="image/addRow_new.gif" border="0" style="cursor: pointer; padding-right:4px;" title="<s:text name="label.manageRates.addRateEntry" />" />
                      </u:repeatAdd>
                    </th>
                </tr>
            </thead>
              <u:repeatTemplate id="mybody" value="rates" index="myindex" theme="twms">
               <tr index="#myindex">
               <td style="border:1px solid #EFEBF7;" >
                        <s:hidden name="rates[#myindex]"></s:hidden>
                        
                        <table  border="0" cellspacing="0" cellpadding="0" class="grid">
                            <tr>
                                <td width="5%"  style="color:#636363; font-weight:bold"><s:text name="label.common.from"/>:</td>
                                <td width="8%">
                                 <sd:datetimepicker name='rates[#myindex].duration.fromDate' value='%{rates[#myindex].duration.fromDate}' id='startDate_#myindex' />                   									 
                   				 					 	
                                </td>
                                <td width="4%"  style="color:#636363; font-weight:bold"><s:text name="label.common.to"/>:</td>
                                <td width="8%">
                                <sd:datetimepicker name='rates[#myindex].duration.tillDate' value='%{rates[#myindex].duration.tillDate}' id='endDate_#myindex' />
                                </td>
                            </tr>
                        </table>
                    </td>
                    <td style="border:1px solid #EFEBF7;" >
                        <s:if test="partPriceValues != null">
                        <s:iterator value="partPriceValues" status="iter">
	                           <t:money id="standardCostPrice_%{#iter.index}_#myindex" 
		                         	name="rates[#myindex].partPriceValues[%{#iter.index}].standardCostPrice"  
		                         	value="%{standardCostPrice}" 
		                         	defaultSymbol="%{partPrice.standardCostPrice.breachEncapsulationOfCurrency()}"></t:money><BR>
                        </s:iterator><BR>
                        </s:if>
                        <s:else>
                          	<s:iterator value="partRateValues" status="iter">
	                           <t:money id="standardCostPrice_%{#iter.index}_#myindex" 
	                         	name="rates[#myindex].partPriceValues[%{#iter.index}].standardCostPrice" 
	                         	value="%{standardCostPrice}"  defaultSymbol="%{partPrice.standardCostPrice.breachEncapsulationOfCurrency()}"></t:money><BR>
                        	</s:iterator><BR>
                        </s:else>
                        
                    </td>
                          <td style="border:1px solid #EFEBF7;" >
                        <s:if test="partPriceValues != null">
                        <s:iterator value="partPriceValues" status="iter">
	                           <t:money id="dNet_%{#iter.index}_#myindex" 
		                         	name="rates[#myindex].partPriceValues[%{#iter.index}].dealerNetPrice"  
		                         	value="%{dealerNetPrice}" 
		                         	defaultSymbol="%{partPrice.dealerNetPrice.breachEncapsulationOfCurrency()}"></t:money><BR>
                        </s:iterator><BR>
                        </s:if>
                        <s:else>
                          	<s:iterator value="partRateValues" status="iter">
	                           <t:money id="dNet_%{#iter.index}_#myindex" 
	                         	name="rates[#myindex].partPriceValues[%{#iter.index}].dealerNetPrice" 
	                         	value="%{dealerNetPrice}"  defaultSymbol="%{partPrice.dealerNetPrice.breachEncapsulationOfCurrency()}"></t:money><BR>
                        	</s:iterator><BR>
                        </s:else>
                        
                    </td>
					
					<td style="border:1px solid #EFEBF7;" >
                        <s:if test="partPriceValues != null">
                        <s:iterator value="partPriceValues" status="iter">
	                           <t:money id="plantCostPrice_%{#iter.index}_#myindex" 
		                         	name="rates[#myindex].partPriceValues[%{#iter.index}].plantCostPrice"  
		                         	value="%{plantCostPrice}" 
		                         	defaultSymbol="%{partPrice.plantCostPrice.breachEncapsulationOfCurrency()}"></t:money><BR>
                        </s:iterator><BR>
                        </s:if>
                        <s:else>
                          	<s:iterator value="partRateValues" status="iter">
	                           <t:money id="plantCostPrice_%{#iter.index}_#myindex" 
	                         	name="rates[#myindex].partPriceValues[%{#iter.index}].plantCostPrice" 
	                         	value="%{plantCostPrice}"  defaultSymbol="%{partPrice.plantCostPrice.breachEncapsulationOfCurrency()}"></t:money><BR>
                        	</s:iterator><BR>
                        </s:else>
                        
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
    <div id="separator"></div>
    
      <div dojoType="twms.widget.TitlePane" style="margin:5px" title="<s:text name="title.laborHistory.partPriceAuditHistory"/>"
	            labelNodeClass="section_header">
	                <jsp:include flush="true" page="partPriceHistory.jsp"/>
	  </div>
	
<div id="separator"></div>
    <div align="center" style="margin-top:10px;">

	  <input id="cancel_btn" class="buttonGeneric" type="button" value="<s:text name='button.common.cancel'/>"
				onclick="javascript:closeTab(getTabHavingLabel(getMyTabLabel()));" />
	  <s:if test="%{definition.id != null}">
	    <s:submit cssClass="buttonGeneric" id="deleteButton" value="%{getText('button.common.delete')}"  action="delete_part_prices_configuration"/>
	  </s:if>
      <s:if test="%{definition.id == null}">
            <s:submit cssClass="buttonGeneric" id="createButton" value="%{getText('button.common.save')}" action="create_part_prices_configuration"/>
      </s:if>
        <s:else>
            <s:submit cssClass="buttonGeneric" id="updateButton" value="%{getText('button.common.update')}" action="update_part_prices_configuration"/>
        </s:else>
    </div>

</s:form>

<script type="text/javascript">

function fillPartDescription(data, type, request) {
	var params = {};
	var itemNumber;
	itemNumber=dijit.byId("partNumberAutoComplete").getValue();
	params["nmhgPartNumber"]= itemNumber;
	twms.ajax.fireJsonRequest("findPartDetails.action?selectedBusinessUnit=<s:property value="%{selectedBusinessUnit}" />", params, function(details) {
                     if(details){
                         dojo.byId("partDescription").innerHTML=details[0];
                 }
				}
             );
     }

dojo.subscribe("/part/changed", null, fillPartDescription);
    dojo.addOnLoad(function() {
    	 <s:if test="%{definition!=null && definition.id != null}">
    	  dijit.byId("partNumberAutoComplete").setDisabled(true);
    	  dijit.byId("partNumberAutoComplete").text="1/11/2013";
    	 </s:if>
    });
</script>

<authz:ifPermitted resource="warrantyAdminManagePartsCostReadOnlyView">
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

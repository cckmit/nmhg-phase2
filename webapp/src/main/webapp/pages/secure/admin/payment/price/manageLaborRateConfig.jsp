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
    </style>
</head>
<u:body smudgeAlert="false">
<s:form name="baseForm" id="baseFormId" theme="twms" validate="true" action="create_lr_configuration" method="post">
<u:actionResults/>
<s:hidden name="id" />
<s:hidden name="dealerGroupSelected" id="isDealerGroup"></s:hidden>
<div class="admin_section_div" style="margin:5px;padding-bottom:10px;width:98.8%">
    <div class="admin_section_heading"><s:text name="label.manageRates.labourRateConfig" /></div>
  
        <div class="mainTitle" style="margin-top:5px;">
        <s:text name="label.manageRates.conditions" /></div>
        <div class="borderTable">&nbsp;</div>
        <div style="margin-top:-10px;">
        <table width="100%" class="grid" cellspacing="0" cellpadding="0">
            <tr>
                <td class="admin_data_table" width="20%"  >
                    <div id="dealerLabel">
                               <s:text name="label.common.dealer"/>:
                  </div>
                           <div id="dealerGroupLabel">
                               <s:text name="label.common.dealerGroupLabel"/>:
                           </div>
                           <div id="toggle" style="cursor:pointer;">
                               <div id="toggleToDealerGroup" class="clickable">
                                   <s:text name="toggle.common.toDealerGroup" />
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
                    <sd:autocompleter id='productType' href='list_part_return_Products.action' name='productType' loadOnTextChange='true' loadMinimumCount='1' showDownArrow='false' autoComplete='false' cssClass='admin_selections' />
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
                    <select id="claimType" size="1" name="definition.forCriteria.claimType" style="width:15%;" >
                    <option value="ALL"><s:text name="dropdown.common.all"/></option>
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
  <br>
  <div class="mainTitle" > <s:text name="label.warrantyAdmin.comments"/>:</div>
  <s:textarea name="laborComments" cols="100" rows="6"/>

    <div class="mainTitle" >
      <br>
    <s:text name="label.common.rates"/></div>
    
     <u:repeatTable id="myTable" cssClass="admin_entry_table borderForTable" 
     cellpadding="0" cellspacing="0" width="98%" theme="twms" cssStyle="margin:5px;">
            <thead>
                <tr class="admin_table_header">
                    <th width="70%" class="colHeader"><s:text name="label.manageRates.repairDate"/></th>
                    <s:if test="buConfigAMER">
	                    <th width="10%" class="colHeader"><s:text name="label.manageRates.customerRate"/></th>
	                    <th width="10%" class="colHeader"><s:text name="label.manageRates.warrantyRate"/></th>
	                    <th width="10%" class="colHeader"><s:text name="label.manageRates.certifiedRate"/></th>
                    </s:if>
                    <s:else>
						<th width="28%" class="colHeader"><s:text name="label.manageRates.labourRate"/></th>
                    </s:else>
                    <th width="4%" class="colHeader">
                        <u:repeatAdd id="adder" theme="twms">
                            <img id="addPrice" src="image/addRow_new.gif" border="0" style="cursor: pointer; padding-right:4px;" title="<s:text name="label.manageRates.addRateEntry" />" />
                      </u:repeatAdd>
                    </th>
                </tr>
            </thead>
            <u:repeatTemplate id="mybody" value="rates" index="myindex" theme="twms">
                <tr index="#myindex">
                    <td  width="61%" style="border:1px solid #EFEBF7;" >
                        <s:hidden name="rates[#myindex]"></s:hidden>
                        
                        <table width="100%" border="0" cellspacing="0" cellpadding="0" class="grid">
                            <tr>
                                <td width="8%"  style="color:#636363; font-weight:bold"><s:text name="label.common.from"/>:</td>
                                <td width="25%">
                                 <sd:datetimepicker name='rates[#myindex].duration.fromDate' value='%{rates[#myindex].duration.fromDate}' id='startDate_#myindex' />                   									 
                   				 					 	
                                </td>
                                <td width="4%"  style="color:#636363; font-weight:bold"><s:text name="label.common.to"/>:</td>
                                <td width="63%">
                                <sd:datetimepicker name='rates[#myindex].duration.tillDate' value='%{rates[#myindex].duration.tillDate}' id='endDate_#myindex' />
                                </td>
                            </tr>
                        </table>
                    </td>
                    
                    <s:if test="buConfigAMER">
                    <td width="13%" style="border:1px solid #EFEBF7;" >
                        <s:if test="laborRateValues != null">
                        <s:iterator value="laborRateValues" status="iter">
	                         <t:money id="rate_%{#iter.index}_#myindex" 
	                         	name="rates[#myindex].laborRateValues[%{#iter.index}].rate" 
	                         	value="%{rate}" defaultSymbol="laborRate.currency.symbol"></t:money><BR>
	                         	<script type="text/javascript">
	                         	dojo.addOnLoad(function() {
	                                dojo.connect(dojo.byId("rate_"+<s:property value="%{#iter.index}"/>+"_"+#myindex), "onchange", "dojo/number", function() {
	                                	var certifiedPercentage = <s:property value="@tavant.twms.domain.common.AdminConstants@CERTIFIED_TECHNICIAN_LABORRATE_PERCENTAGE" />;
	                                	var nonCertifiedPercentage = <s:property value="@tavant.twms.domain.common.AdminConstants@NON_CERTIFIED_TECHNICIAN_LABORRATE_PERCENTAGE" />;
                                		dojo.byId("rate_wr_"+<s:property value="%{#iter.index}"/>+"_"+#myindex).value = ((dojo.byId("rate_"+<s:property value="%{#iter.index}"/>+"_"+#myindex).value)*(nonCertifiedPercentage/100)).toFixed(2);
                                		dojo.byId("rate_cr_"+<s:property value="%{#iter.index}"/>+"_"+#myindex).value = ((dojo.byId("rate_"+<s:property value="%{#iter.index}"/>+"_"+#myindex).value)*(certifiedPercentage/100)).toFixed(2);
	                                });
	                         	});
	                         	</script>
                        </s:iterator><BR>
                        </s:if>
                        <s:else>
                          	<s:iterator value="lRateValues" status="iter">
	                           <t:money id="rate_%{#iter.index}_#myindex" 
	                         	name="rates[#myindex].laborRateValues[%{#iter.index}].rate" 
	                         	value="%{rate}" defaultSymbol="laborRate.currency.symbol"></t:money><BR>
	                         	<script type="text/javascript">
	                                dojo.connect(dojo.byId("rate_"+<s:property value="%{#iter.index}"/>+"_"+#myindex), "onchange", "dojo/number", function() {
	                                	var certifiedPercentage = <s:property value="@tavant.twms.domain.common.AdminConstants@CERTIFIED_TECHNICIAN_LABORRATE_PERCENTAGE" />;
	                                	var nonCertifiedPercentage = <s:property value="@tavant.twms.domain.common.AdminConstants@NON_CERTIFIED_TECHNICIAN_LABORRATE_PERCENTAGE" />;
                                		dojo.byId("rate_wr_"+<s:property value="%{#iter.index}"/>+"_"+#myindex).value = ((dojo.byId("rate_"+<s:property value="%{#iter.index}"/>+"_"+#myindex).value)*(nonCertifiedPercentage/100)).toFixed(2);
                                		dojo.byId("rate_cr_"+<s:property value="%{#iter.index}"/>+"_"+#myindex).value = ((dojo.byId("rate_"+<s:property value="%{#iter.index}"/>+"_"+#myindex).value)*(certifiedPercentage/100)).toFixed(2);
	                                });
	                         	</script>
                        	</s:iterator><BR>
                        </s:else>
                        
                    </td>
                    
                    <td width="13%" style="border:1px solid #EFEBF7;" >
                        <s:if test="laborRateValues != null">
                        <s:iterator value="laborRateValues" status="iter">
	                         <t:money id="rate_wr_%{#iter.index}_#myindex" 
	                         	name="" 
	                         	value="%{warrantyRate}" defaultSymbol="laborRate.currency.symbol" disabled="true"></t:money><BR>
                        </s:iterator><BR>
                        </s:if>
                        <s:else>
                          	<s:iterator value="lRateValues" status="iter">
	                           <t:money id="rate_wr_%{#iter.index}_#myindex" 
	                         	name="" 
	                         	value="%{warrantyRate}" defaultSymbol="laborRate.currency.symbol" disabled="true"></t:money><BR>
                        	</s:iterator><BR>
                        </s:else>
                        
                    </td>
                    
                    <td width="13%" style="border:1px solid #EFEBF7;" >
                        <s:if test="laborRateValues != null">
                        <s:iterator value="laborRateValues" status="iter">
	                         <t:money id="rate_cr_%{#iter.index}_#myindex" 
	                         	name="" 
	                         	value="%{certifiedRate}" defaultSymbol="laborRate.currency.symbol" disabled="true"></t:money><BR>
                        </s:iterator><BR>
                        </s:if>
                        <s:else>
                          	<s:iterator value="lRateValues" status="iter">
	                           <t:money id="rate_cr_%{#iter.index}_#myindex" 
	                         	name="" 
	                         	value="%{certifiedRate}" defaultSymbol="laborRate.currency.symbol" disabled="true"></t:money><BR>
                        	</s:iterator><BR>
                        </s:else>
                        
                    </td>
                    </s:if>
                    <s:else>
                    <td width="13%" style="border:1px solid #EFEBF7;" >
                        <s:if test="laborRateValues != null">
                        <s:iterator value="laborRateValues" status="iter">
	                         <t:money id="rate_%{#iter.index}_#myindex" 
	                         	name="rates[#myindex].laborRateValues[%{#iter.index}].rate" 
	                         	value="%{rate}" defaultSymbol="laborRate.currency.symbol"></t:money><BR>
                        </s:iterator><BR>
                        </s:if>
                        <s:else>
                          	<s:iterator value="lRateValues" status="iter">
	                           <t:money id="rate_%{#iter.index}_#myindex" 
	                         	name="rates[#myindex].laborRateValues[%{#iter.index}].rate" 
	                         	value="%{rate}" defaultSymbol="laborRate.currency.symbol"></t:money><BR>
                        	</s:iterator><BR>
                        </s:else>
                        
                    </td>
                    </s:else>
                    
                    <td width="2%" style="border:1px solid #EFEBF7;">
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
    
      <div dojoType="twms.widget.TitlePane" style="margin:5px" title="<s:text name="title.laborHistory.laborRateAuditHistory"/>"
	            labelNodeClass="section_header">
	                <jsp:include flush="true" page="laborHistory.jsp"/>
	  </div>
	
<div id="separator"></div>
    <div align="center" style="margin-top:10px;">

	  <input id="cancel_btn" class="buttonGeneric" type="button" value="<s:text name='button.common.cancel'/>"
				onclick="javascript:closeTab(getTabHavingLabel(getMyTabLabel()));" />
	  <s:if test="%{definition.id != null}">
	    <s:submit cssClass="buttonGeneric" id="deleteButton" value="%{getText('button.common.delete')}"  action="delete_lr_configuration"/>
	  </s:if>
      <s:if test="%{definition.id == null}">
            <s:submit cssClass="buttonGeneric" id="createButton" value="%{getText('button.common.save')}" action="create_lr_configuration"/>
      </s:if>
        <s:else>
            <s:submit cssClass="buttonGeneric" id="updateButton" value="%{getText('button.common.save')}" action="update_lr_configuration"/>
        </s:else>
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

<authz:ifPermitted resource="warrantyAdminLaborRatesReadOnlyView">
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

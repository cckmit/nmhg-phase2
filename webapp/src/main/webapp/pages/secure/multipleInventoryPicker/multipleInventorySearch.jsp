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

<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="authz" uri="authz" %>
<%
	response.setHeader("Pragma", "no-cache");
	response.addHeader("Cache-Control", "must-revalidate");
	response.addHeader("Cache-Control", "no-cache");
	response.addHeader("Cache-Control", "no-store");
	response.setDateHeader("Expires", 0);
%>
<script type="text/javascript" src="scripts/multiInventoryPicker/multipleInventorySearch.js"></script>

<style type="text/css">
    .buttons {
        float: none;
    }
</style>

<div id="noSearchParamsErrorSection" class="twmsActionResults" style="display:none">
    <div class="twmsActionResultsSectionWrapper twmsActionResultsErrors">
        <h4 class="twmsActionResultActionHead"><s:text name="errors"/></h4>
        <ol>
            <li><s:text name="error.multiCar.searchParametersRequired"/></li>
        </ol>
        <hr/>
    </div>
</div>

<!-- The Form doesn't have any action defined, since that is set dynamically by the MultipleInventoryPicker widget
which uses this -->
<s:form method="post" theme="twms" id="multiInventorySearchForm" name="searchMultipleInventories">

    <table class="form" cellpadding="0" cellspacing="0" style="margin:5px 0px 0px 5px; width: 98%; padding-bottom: 10px; height:90px;">
        <tbody>
            <tr>
                <td width="20%">
                    <label id="serialNumberLabel" class="labelStyle">
                        <s:text name="label.common.serialNumber"/>
                        :
                    </label>
                </td>
                <td>
                    <s:textfield name="inventorySearchCriteria.serialNumber" id="searchSerialNumber"/>
                </td>
                <td  width="20%">
                    <label id="modelNumberLabel" class="labelStyle">
                        <s:text name="label.common.model"/>
                        :
                    </label>
                </td>
                <td>
                    <s:textfield name="inventorySearchCriteria.modelNumber" id="searchModelNumber"/>
                </td>
            </tr>
            
            <s:if test="!loggedInUserADealer">
            <authz:ifUserInRole roles="inventoryAdmin,processor,admin">
            <tr>
                <td>
                    <label id="dealerNumberLabel" class="labelStyle">
                        <s:text name="label.common.dealerNumber"/>
                        :
                    </label>
                </td>
                <td>
                    <s:textfield name="inventorySearchCriteria.dealerNumber" id="searchDealerNumber"/>
                </td>
                <td>
                    <label id="dealerNameLabel" class="labelStyle">
                        <s:text name="label.common.dealerName"/>
                        :
                    </label>
                </td>
                <td>
                    <s:textfield name="inventorySearchCriteria.dealerName" id="searchDealerName"/>
                </td>
            </tr>
            </authz:ifUserInRole>
                <s:hidden name="inventorySearchCriteria.dealerId" id="inventorySearchCriteria.dealerId"/>
            </s:if>
            <s:else>
                <s:hidden name="inventorySearchCriteria.dealerId" value="%{loggedInUsersDealership.id}" />
            </s:else>
			
            <tr id="endCustomerSearch">
                <td>
                    <label id="companyNameLabel" class="labelStyle">
                        <s:text name="inventorySearchCriteria.customer.endCustomerCompanyName"/>
                        :
                    </label>
                </td>
                <td>
                    <s:textfield name="inventorySearchCriteria.customer.companyName" id="searchEndCustomer"/>
                </td>                
            </tr>
            <!--Added factory order number for Hussman IR-HUS-INV-CR02 -->
            <s:if test="isFactoryOrderNumberRequired()">
            <tr id="factoryOrderNumberSearch">
              <td>
                    <label id="factoryOrderNumber" class="labelStyle">
                        <s:text name="label.common.factoryOrderNumber"/>
                        :
                    </label>
                </td>
                <td>
                    <s:textfield name="inventorySearchCriteria.factoryOrderNumber" id="searchFactoryOrderNumber"/>
                </td>
            </tr>
            </s:if>
            <s:if test="(getLoggedInUser().businessUnits).size>1 && getSelectedBusinessUnit()!=null">
                <s:hidden name="inventorySearchCriteria.selectedBusinessUnits" value="%{getSelectedBusinessUnit()}"/>
            </s:if>

            <tr id="buSearchList" style="display:none">
                <s:if test="(getLoggedInUser().businessUnits).size>1">
                    <td>
                    <label id="searchLabel" class="labelStyle">
                        <s:text name="label.common.businessUnit"/>:
                    </label>
                    </td>
                    <td>
                        <s:iterator value="businessUnits" status="buItr">
                            <input type="radio" name="inventorySearchCriteria.selectedBusinessUnits"
                                   value="<s:property value="name"/>" id="bu_<s:property value="%{#buItr.index}"/>"/>
                            <s:property value="name"/>
                        </s:iterator>
                        <script type="text/javascript">
                            dojo.addOnLoad(function() {
                                if(dojo.byId("bu_0")){
                                    dojo.byId("bu_0").checked = true;
                                }
                            })
                        </script>
                    </td>
                </s:if>
            </tr>

            <tr id="restrictedBuSearchList" style="display:none;">
                    <td>
                    <label id="restrictedBuLabel" class="labelStyle">
                        <s:text name="label.common.businessUnit"/>:
                    </label>
                    </td>
                    <td>
                        <s:iterator value="restrictedBu" status="buItr">
                            <input type="radio" name="inventorySearchCriteria.selectedBusinessUnits"
                                   value="<s:property value="name"/>" id="restricted_bu_<s:property value="%{#buItr.index}"/>"/>
                            <s:property value="name"/>
                        </s:iterator>
                        <script type="text/javascript">
                            dojo.addOnLoad(function() {
                                if(dojo.byId("restricted_bu_0")){
                                    dojo.byId("restricted_bu_0").checked = true;
                                }
                            })
                        </script>
                    </td>
            </tr>

            <tr>
                <td colspan="4" class="buttons" style="padding-top: 20px; width: 100%; float:none">
                    <div align="center">
                    	<input type="button" id="resetSearch" 
                			value="<s:property value="%{getText('button.common.reset')}"/>" />
                        <s:submit id="searchInventories" value="%{getText('button.common.search')}"/>
                    </div>
                </td>
            </tr>
        </tbody>
    </table>

</s:form>

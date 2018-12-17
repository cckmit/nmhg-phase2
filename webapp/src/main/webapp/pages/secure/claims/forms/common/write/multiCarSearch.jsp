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
<%@ taglib prefix="t" uri="twms" %>
<%@ taglib prefix="u" uri="/ui-ext" %>

<script type="text/javascript">
    dojo.require("twms.widget.Dialog");    
    dojo.require("dijit.layout.LayoutContainer");
    dojo.require("dijit.layout.ContentPane"); 
    dojo.require("dojox.layout.ContentPane");    
</script>

<%--
This script is to display the pop up page to search the inventories for MULTICLAIM.
It handles the display of the search result in the same page using FormBind.
And also finally renders the selected inventories to the claim new_machine_claim1.jsp
while hiding the pop up search page
--%>
<u:stylePicker fileName="multiCar.css" />


<script type="text/javascript" src="scripts/multiCar/multiCarInventorySearch.js"></script>
<div dojoType="twms.widget.Dialog" id="searchCriteria" bgColor="#FFF" style="width:95%;height:95%"  bgOpacity="1.0" toggle="fade"
     toggleDuration="250" title="<s:text name="label.multiCar.searchSerialNumbers" />" >
<div id="noSearchParamsErrorSection" class="twmsActionResults" style="display:none">
	<div class="twmsActionResultsSectionWrapper twmsActionResultsErrors">
		<h4 class="twmsActionResultActionHead">ERRORS</h4>
		<ol>
			<li><s:text name="error.multiCar.searchParametersRequired" /></li>
		</ol>
		<hr/>
	</div>
</div>
    <div dojoType="dojox.layout.ContentPane" layoutAlign="top">
    
    <s:form method="post" theme="twms" id="multiCarSearchform" name="searchMultipleCars"
            action="searchMultiCarInventories.action">
    
   	<s:hidden name="claimType" id="claimTypForSearch"/> 
   	
   	<s:hidden name="campaignCode" id="campaign_code"/>  
   	<s:hidden name="selectedBusinessUnit" id="selectedBusinessUnit"/>  

    <table class="form" cellpadding="0" cellspacing="0" 
    style="margin-top: 5px; width: 98%; padding-bottom: 10px;border:none;margin-left:5px;">
    <tbody>
        <tr>
            <td width="20%">
                <label id="inventoryStatusLabel" class="labelStyle">
                    <s:text name="label.common.inventoryType"/>:
                </label>
            </td>
            <td colspan="3">            	
            <input type="radio"  value="STOCK" id="inventory_type_stock" name="multiCarSearch.inventoryType"/>
            <s:text name="accordion_jsp.inventory.stock"/>
            <input type="radio" checked="checked" id="inventory_type_retail" value="RETAIL" name="multiCarSearch.inventoryType"/>
            <s:text name="accordion_jsp.inventory.retailed"/>                       
            </td>

        </tr>
        <tr>
            <td nowrap="nowrap">
                <label id="serialNumberLabelForSearch" class="labelStyle">
                    <s:text name="label.common.serialNumber"/>
                    :
                </label>
            </td>
            <td>
                <s:textfield name="multiCarSearch.serialNumber" id="searchSerialNumber"/>
            </td>
            <td width="20%">
                <label id="modelNumberLabel" class="labelStyle">
                    <s:text name="label.common.model"/>
                    :
                </label>
            </td>
            <td>
                <s:textfield name="multiCarSearch.modelNumber" id="searchModelNumber"/>
            </td>
        </tr>

        <tr>
        	<td nowrap="nowrap">
                <label id="dealerNumberLabel" class="labelStyle">
                    <s:text name="label.common.dealerNumber"/>
                    :
                </label>
            </td>
            <td>
                <s:textfield name="multiCarSearch.dealerNumber" id="searchDealerNumber"/>
            </td>
            <td nowrap="nowrap" nowrap="nowrap">
                <label id="companyNameLabel" class="labelStyle">
                    <s:text name="inventorySearchCriteria.customer.endCustomerCompanyName"/>
                    :
                </label>
            </td>
            <td>
                <s:textfield name="multiCarSearch.customer.companyName" id="searchCompanyName"/>
            </td>           
        </tr>
        
        <tr>
        	<td>
                <label id="shipmentDateYearLabel" class="labelStyle">
                    <s:text name="label.common.year"/>
                </label>
            </td>
            <td>
            <s:textfield name="multiCarSearch.yearOfShipment" id="searchYear" maxlength="4"/>                                 
            </td>
            <td  nowrap="nowrap">
                <label id="endCustomerSelectLabel" class="labelStyle">
                    <s:text name="label.multiClaim.endCustomer"/>
                </label>
            </td>
            <td >
            <input type="checkbox" name="multiCarSearch.customerSelected" id="endCustomerSelect"/>                                 
            </td>
        </tr>
        
        <tr>
	        <td style="display:none" id="addressBookDiv" colspan="4">
	        <jsp:include flush="true" page="multiClaimSearchByOwner.jsp" />
	        </td>
        </tr>       

        <tr>
            <td colspan="4" align="center" class="buttons">
                <input type="button" id="resetSearch" 
                	value="<s:property value="%{getText('button.common.reset')}"/>" />
                <s:submit id="searchInventories" value="%{getText('button.common.search')}" />
            </td>
        </tr>
        </tbody>
    </table>
 
<s:hidden name="dealerId" id="dealerIdForSearch" value="%{loggedInUser.getBelongsToOrganization()}"/>
<input type="hidden" name ="claim" value="<s:property value="claim"/>"/>
<s:hidden name="multiCarSearch.customer.id" id="ownerId"/>
</s:form>
</div>

<div id="searchResultTag" dojoType="dojox.layout.ContentPane" layoutAlign="client" executeScripts="true" style="margin-top: 5px; width: 98%; height: 450px">
</div>


</div>

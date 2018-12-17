<%--
  Created by IntelliJ IDEA.
  User: pradyot.rout
  Date: Nov 20, 2008
  Time: 7:24:49 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html" %>
<%@ page pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="t" uri="twms" %>
<%@ taglib prefix="u" uri="/ui-ext" %>
<%@ taglib prefix="authz" uri="authz" %>
<%
    response.setHeader("Pragma", "no-cache");
    response.addHeader("Cache-Control", "must-revalidate");
    response.addHeader("Cache-Control", "no-cache");
    response.addHeader("Cache-Control", "no-store");
    response.setDateHeader("Expires", 0);
%>
<table width="100%" border="0" cellspacing="0" cellpadding="0">
<tr><td>&nbsp;</td></tr>
<tr><td>&nbsp;</td></tr>
<tr>
    <td class="ItemsHdrGoto"><s:text name="label.common.Goto"/></td>
</tr>
<authz:ifUserNotInRole roles="readOnlyDealer">
<authz:ifUserInRole roles="dealerWarrantyAdmin">
    <tr>
        <td class="ItemsLabels">
			<img src="image/bullets_newDesign.gif" border="0" align="absmiddle"/>
            <a id="homeCreateClaim" style="padding-left:5px"><s:text name="label.newClaim.createClaim"/></a>
        </td>
    </tr>
</authz:ifUserInRole>
</authz:ifUserNotInRole>
	<authz:ifUserInRole roles="thirdPartyPrivilege">
	  <tr>
		 <td class="ItemsLabels">
			<img src="image/bullets_newDesign.gif" border="0" align="absmiddle"/>
			<a id="homeCreateThirdPartyClaim" style="padding-left:5px"><s:text name="label.newClaim.createThirdPartyClaim"/></a>
		  </td>
	  </tr>
	</authz:ifUserInRole>

<authz:ifUserNotInRole roles="readOnlyDealer">
<s:if test="loggedInUserADealer">
 <authz:ifUserInRole roles="dealerSalesAdministration,inventoryAdmin">
    <tr>
        <td class="ItemsLabels">
			<img src="image/bullets_newDesign.gif" border="0" align="absmiddle"/>
            <a id="homeRegisterNewWarranty" style="padding-left:5px"><s:text name="title.managePolicy.warrantyRegistration"></s:text></a>
        </td>
    </tr>
    </authz:ifUserInRole>
    <authz:ifUserInRole roles="dealerSalesAdministration,inventoryAdmin">
    <tr>
        <td class="ItemsLabels">
			<img src="image/bullets_newDesign.gif" border="0" align="absmiddle"/>
            <a id="homeWarrantyTransfer" style="padding-left:5px"><s:text name="home_jsp.fileMenu.warrantyTransfer"></s:text></a>
        </td>
    </tr>
    
    </authz:ifUserInRole>
</s:if>
</authz:ifUserNotInRole>
<s:else>
<authz:ifUserInRole roles="inventoryAdmin, enterpriseDealership">
    <tr>
        <td class="ItemsLabels">
			<img src="image/bullets_newDesign.gif" border="0" align="absmiddle"/>
            <a id="homeRegisterNewWarranty" style="padding-left:5px"><s:text name="title.managePolicy.warrantyRegistration"></s:text></a>
        </td>
    </tr>
    <tr>
        <td class="ItemsLabels">
			<img src="image/bullets_newDesign.gif" border="0" align="absmiddle"/>
            <a id="homeWarrantyTransfer" style="padding-left:5px"><s:text name="home_jsp.fileMenu.warrantyTransfer"></s:text></a>
        </td>
    </tr>
</authz:ifUserInRole>
</s:else>
<authz:ifUserNotInRole roles="readOnlyDealer">
    <authz:ifUserInRole roles="admin,processor,dsmAdvisor,recoveryProcessor,recoveryProcessor,dealerSalesAdministration">
        <tr>
            <td class="ItemsLabels">
				<img src="image/bullets_newDesign.gif" border="0" align="absmiddle"/>
                <a id="homeCreateCustomer" style="padding-left:5px"><s:text name="home_jsp.fileMenu.createCustomer"></s:text></a>
            </td>
        </tr>
    </authz:ifUserInRole>
    </authz:ifUserNotInRole>
    <authz:ifUserInRole roles="admin,processor,dsmAdvisor,recoveryProcessor,dsm,recoveryProcessor,dealerSalesAdministration">
        <tr>
            <td class="ItemsLabels">
				<img src="image/bullets_newDesign.gif" border="0" align="absmiddle"/>
                <u:openTab decendentOf="%{getText('home_jsp.tabs.home')}"
                           id="customerSearch" tagType="a"
                           tabLabel="%{getText('accordion_jsp.accordionPane.searchCustomer')}"
                           url="input_customer_search.action">
                    <s:text name="label.customer.searchCustomer"/>
                </u:openTab>
            </td>
        </tr>
    </authz:ifUserInRole>
    <authz:ifUserNotInRole roles="receiverLimitedView, inspectorLimitedView, partShipperLimitedView">
	    <authz:ifUserInRole roles="admin,processor,dsmAdvisor,recoveryProcessor,dsm,recoveryProcessor,inspector,readOnly,dealerWarrantyAdmin">
	        <tr>
	            <td class="ItemsLabels">
					<img src="image/bullets_newDesign.gif" border="0" align="absmiddle"/>
	                <u:openTab decendentOf="%{getText('home_jsp.tabs.home')}"
	                           id="clmSearch" tagType="a" forceNewTab="true" catagory="claimAdvancedSearch"
	                           tabLabel="%{getText('accordion_jsp.accordionPane.myClaims.defineSearch')}"
	                           url="new_search_expression.action?context=ClaimSearches&folderName=Search">
	                    <s:text name="title.viewClaim.searchClaim"/>
	                </u:openTab>
	            </td>
	        </tr>
	    </authz:ifUserInRole>
    </authz:ifUserNotInRole>
    <authz:ifUserInRole roles="admin,processor,dsmAdvisor,recoveryProcessor,dsm,recoveryProcessor,dealerSalesAdministration,readOnly">
        <tr>
            <td class="ItemsLabels">
				<img src="image/bullets_newDesign.gif" border="0" align="absmiddle"/>
                <u:openTab decendentOf="%{getText('home_jsp.tabs.home')}"
                           id="invSearch" tagType="a" forceNewTab="true" catagory="inventoryAdvancedSearch"
                           tabLabel="%{getText('accordion_jsp.accordionPane.myClaims.defineSearch')}"
                           url="new_search_expression.action?context=InventorySearches">
                    <s:text name="title.claim.searchInventory"/>
                </u:openTab>
            </td>
        </tr>
    </authz:ifUserInRole>

	<s:if test="isInternalUser()">
		<tr>
        <td class="ItemsLabels">
			<img src="image/bullets_newDesign.gif" border="0" align="absmiddle"/>
            <u:openTab decendentOf="%{getText('home_jsp.tabs.home')}"
                       id="itemSearch" tagType="a" forceNewTab="true" catagory="itemAdvancedSearch"
                       tabLabel="%{getText('accordion_jsp.accordionPane.myClaims.defineSearch')}"
                       url="new_search_expression.action?context=ItemSearches">
                <s:text name="title.claim.searchItem"/>
            </u:openTab>
        </td>
    </tr>
	</s:if>
	<s:else>
		<authz:ifUserInRole
			roles="dealerWarrantyAdmin,dealerSalesAdministration">
			<tr>
        <td class="ItemsLabels">
			<img src="image/bullets_newDesign.gif" border="0" align="absmiddle"/>
            <u:openTab decendentOf="%{getText('home_jsp.tabs.home')}"
                       id="itemSearch" tagType="a" forceNewTab="true" catagory="itemAdvancedSearch"
                       tabLabel="%{getText('accordion_jsp.accordionPane.myClaims.defineSearch')}"
                       url="new_search_expression.action?context=ItemSearches">
                <s:text name="title.claim.searchItem"/>
            </u:openTab>
        </td>
    </tr>
		</authz:ifUserInRole>
	</s:else>

<authz:ifAdmin>
    <tr>
        <td class="ItemsLabels">
			<img src="image/bullets_newDesign.gif" border="0" align="absmiddle" />
            <a id="homeOpenPolicyCreation" style="padding-left:5px"><s:text name="home_jsp.fileMenu.createPolicy"></s:text></a>
        </td>
    </tr>
    <tr>
        <td class="ItemsLabels">
			<img src="image/bullets_newDesign.gif" border="0" align="absmiddle" />
            <a id="homeCreatePaymentDefinition" style="padding-left:5px"><s:text name="home_jsp.fileMenu.createPaymentDefinition"></s:text></a>
        </td>
    </tr>
</authz:ifAdmin>
<s:if test="'true'.equals(applicationSettings.logoutRequired)">
    <tr>
        <td class="ItemsLabels">
			<img src="image/bullets_newDesign.gif" border="0" align="absmiddle" />
            <a id="homeLogout" style="padding-left:5px"><s:text name="home_jsp.fileMenu.logout"></s:text></a>
        </td>
    </tr>
</s:if>
<s:if test="(getLoggedInUser().businessUnits).size>1">
    <tr>
        <td>
            <div dojoType="twms.widget.Dialog" id="selectBusinesUnitForDR/ETR" title="<s:text name="label.common.businessUnit"/>" style="height:130px;width:300px">
                    <table>
                        <tr>
                            <td style="padding-left:15px"><s:text name="message.selectBusinessUnitForDR/ETR"/></td>
                        </tr>
                        <tr><td>&nbsp</td></tr>
                        <tr>
                            <td style="padding-left:30px">
                                <s:select id="businessUnitNames" list="businessUnits" theme="twms" listKey="name" listValue="name" emptyOption="false"/>
                            </td>
                        </tr>
                        <tr><td>&nbsp</td></tr>
                        <tr>
                            <td align="center" style="padding-left:40px"><input class="buttonGeneric" type="button" id="continue" value="<s:text name="button.common.continue"/>" /></td>
                            <script type="text/javascript">
                                dojo.addOnLoad(function() {
                                    dojo.connect(dojo.byId("continue"), "onclick", function() {
                                        dijit.byId("selectBusinesUnitForDR/ETR").hide();;
                                        if(dojo.byId("transactionTypeDialog").value=='DR'){
                                            deliveryReport(dijit.byId("businessUnitNames").getValue());
                                        }else{
                                            equipmentTransfer(dijit.byId("businessUnitNames").getValue());
                                        }
                                    });
                                });
                            </script>
                        </tr>
                    </table>
            </div>
        </td>
    </tr>
</s:if>
<s:hidden name="transactionType" value="" id="transactionTypeDialog"/>
</table>
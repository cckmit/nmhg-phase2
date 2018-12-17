<%--
  Created by IntelliJ IDEA.
  User: irdemo
  Date: May 29, 2009
  Time: 4:35:31 PM
  To change this template use File | Settings | File Templates.


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

<%@ page contentType="text/html" %>
<%@ page pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="t" uri="twms" %>
<%@ taglib prefix="u" uri="/ui-ext" %>
<%@ taglib prefix="authz" uri="authz" %>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <s:head theme="twms"/>
    <u:stylePicker fileName="common.css"/>
    <u:stylePicker fileName="form.css"/>
    <u:stylePicker fileName="base.css"/>
    <script type="text/javascript">
        dojo.require("dijit.layout.ContentPane");
        dojo.require("dijit.layout.LayoutContainer");
        dojo.require("twms.widget.TitlePane");
    </script>
</head>
<u:body>
<div style="margin:5px;border:1px solid #EFEBF7;background:#F3FBFE">
<div style="margin-top: 10px; padding-bottom: 10px;" class="mainTitle">
    <s:text name="label.common.generalInfo"/>
</div>
<div class="borderTable"> &nbsp;</div>
<table class="grid" width="100%" cellpadding="0" cellspacing="0">
    <tr>
        <td nowrap="nowrap" class="labelStyle" width="20%">
            <s:property value="%{getText('label.managePolicy.code')}"/>:
        </td>
        <td>
            <div>
                <s:property value="policyDefinition.code"/>
            </div>
        </td>
        <td width="20%" class="labelStyle">
            <s:property value="%{getText('label.managePolicy.name')}"/>:
        </td>
        <td>
            <s:property value="policyDefinition.description"/>
        </td>
    </tr>
    <tr>
        <td nowrap="nowrap" class="labelStyle">
            <s:text name="label.managePolicy.priority"/>:
        </td>
        <td>
            <s:property value="policyDefinition.priority"/>
        </td>
        <td nowrap="nowrap" class="labelStyle">
            <s:property value="%{getText('label.managePolicy.warrantyType')}"/>:
        </td>
        
        <td>
            <s:property value="%{getText(policyDefinition.warrantyType.displayValue)}"/>
        </td>
    </tr>
    <s:if test="loggedInUserAnInternalUser">
        <tr>
            <td nowrap="nowrap" class="labelStyle">
                <s:property value="%{getText('label.managePolicy.activeFrom')}"/>:
            </td>
            <td>
                <s:property value="policyDefinition.availability.duration.fromDate"/>
            </td>
            <td nowrap="nowrap" class="labelStyle">
                <s:property value="%{getText('label.managePolicy.activeTill')}"/>:
            </td>
            <td>
                <s:property value="policyDefinition.availability.duration.tillDate"/>

            </td>
        </tr>
        <tr>
            <td nowrap="nowrap" class="labelStyle">
                <s:property value="%{getText('title.policy.buildDateApplicable')}"/>:
            </td>
            <td>
                <s:if test="%{policyDefinition.buildDateApplicable == false}">
                    <s:text name="label.common.no"/>
                </s:if>
                <s:else>
                    <s:text name="label.common.yes"/>
                </s:else>
            </td>
            <td nowrap="nowrap" class="labelStyle">
                <s:property value="%{getText('title.policy.isThirdPartyPolicy')}"/>:
            </td>
            <td>
                <s:if test="%{policyDefinition.isThirdPartyPolicy == false}">
                    <s:text name="label.common.no"/>
                </s:if>
                <s:else>
                    <s:text name="label.common.yes"/>
                </s:else>
            </td>
        </tr>
    </s:if>
    <tr>
        <td nowrap="nowrap" class="labelStyle" id="applicableLabel">
            <s:text name="title.policy.isPolicyApplicable"/>:
        </td>
        <td>
            <s:if test="%{policyDefinition.isPolicyApplicableForWr == false}">
                <s:text name="label.common.no"/>
            </s:if>
            <s:else>
                <s:text name="label.common.yes"/>
            </s:else>
        </td>
    </tr>
    <tr>
        <td nowrap="nowrap" class="labelStyle" id="invisibleDRLabel">
            <s:text name="title.policy.isInvisibleFilingDR"/>:
        </td>
        <td>
            <s:if test="%{policyDefinition.invisibleFilingDr == false}">
                <s:text name="label.common.no"/>
            </s:if>
            <s:else>
                <s:text name="label.common.yes"/>
            </s:else>
        </td>
    </tr>
    <tr>
        <td class="labelStyle" nowrap="nowrap">
            <s:text name="label.policy.termsAndConditions"/>:
        </td>
        <td>
            <s:property value="policyDefinition.termsAndConditions"/>
        </td>
    </tr>
</table>


<s:if test="!policyDefinition.warrantyType.type.equals('POLICY')">

    <div style="margin-top: 10px; padding-bottom: 10px;" class="mainTitle">
        <s:text name="label.contractAdmin.coverage"/>
    </div>
    <div class="borderTable"> &nbsp;</div>
    <table class="grid" width="100%" cellpadding="0" cellspacing="0">

        <tr>
            <td nowrap="nowrap" class="labelStyle" width="20%">
                <s:property value="%{getText('label.managePolicy.serviceHoursCovered')}"/>:
            </td>
            <td>
                <s:property value="policyDefinition.coverageTerms.serviceHoursCovered"/>
            </td>
        </tr>
        <tr>
            <td nowrap="nowrap" class="labelStyle"><s:property
                    value="%{getText('label.managePolicy.monthsCoveredFromRegistration')}"/>:
            </td>
            <td><s:property value="policyDefinition.coverageTerms.monthsCoveredFromDelivery"/></td>
        </tr>
        <tr>
            <td nowrap="nowrap" class="labelStyle"><s:property
                    value="%{getText('label.managePolicy.monthsCoveredFromDateOfShipment')}"/>:
            </td>
            <td><s:property value="policyDefinition.coverageTerms.monthsCoveredFromShipment"/></td>
        </tr>
         <%-- <tr>
                  <td class="labelStyle" nowrap="nowrap"><s:property
                      value="%{getText('label.managePolicy.monthsCoveredFromDateOfOriginal')}"/> : </td>
                  <td><s:property value="policyDefinition.coverageTerms.monthsCoveredFromOriginalDeliveryDate"/></td>
               </tr> --%>
               <tr>
                <td class="labelStyle" nowrap="nowrap"><s:property
                        value="%{getText('label.managePolicy.monthsCoveredFromDateOfBuild')}"/> : </td>
                <td><s:property value="policyDefinition.coverageTerms.monthsCoveredFromBuildDate"/>   
               </td>
               </tr>
        <s:if test="policyDefinition.warrantyType.type.equals('EXTENDED')">
            <tr>
                <td nowrap="nowrap" class="labelStyle"><s:property
                        value="%{getText('label.managePolicy.monthsCoveredFromRegistrationForEWP')}"/>:
                </td>
                <td><s:property value="policyDefinition.coverageTerms.monthsFromDeliveryForEWP"/></td>
            </tr>
            <tr>
                <td nowrap="nowrap" class="labelStyle"><s:property
                        value="%{getText('label.managePolicy.monthsCoveredFromDateOfShipmentForEWP')}"/>:
                </td>
                <td><s:property value="policyDefinition.coverageTerms.monthsFromShipmentForEWP"/></td>
            </tr>
        </s:if>
    </table>

</s:if>

<div style="margin-top: 10px; padding-bottom: 10px;" class="mainTitle">
    <s:text name="label.managePolicy.registrationTerms"/>
</div>
<div class="borderTable"> &nbsp;</div>

<table class="grid" width="100%" cellpadding="0" cellspacing="0">

    <tr>
        <td width="20%" style="padding-left:4px;" nowrap="nowrap" class="labelStyle">
            <s:text name="label.managePolicy.policyPrice"/>:
        </td>
        <td>
            <table>
                <s:iterator value="policyDefinition.policyFees" status="regFeeIter">
                    <s:if test="!policyDefinition.policyFees[#regFeeIter.index].isTransferable">
                        <tr>
                            <td><s:property value="policyFee.breachEncapsulationOfCurrency()"/> <s:property value="policyFee.breachEncapsulationOfAmount()"/></td>
                        </tr>
                    </s:if>
                </s:iterator>
            </table>
        </td>
    </tr>
</table>


<div style="margin-top: 10px; padding-bottom: 10px;" class="mainTitle">
    <s:text name="label.warranty.transfer"/>
</div>
<div class="borderTable"> &nbsp;</div>
<table class="grid" width="100%" cellpadding="0" cellspacing="0">

    <tr>
        <td width="20%" style="padding-left:4px;" nowrap="nowrap" class="labelStyle">
            <s:text name="label.managePolicy.transferable"/>:
        </td>
        <td width="82%">
            <s:if test="policyDefinition.isTransferable()">
                <s:text name="label.common.yes"/>
            </s:if>
            <s:else>
                <s:text name="label.common.no"/>
            </s:else>
        </td>
    </tr>
    <tr>
        <td nowrap="nowrap" class="labelStyle" style="padding-left:4px;">
            <s:text name="label.managePolicy.transferFee"/>:
        </td>
        <td>
            <table>
                <s:iterator value="policyDefinition.policyFees" status="regFeeIter">
                    <s:if test="policyDefinition.policyFees[#regFeeIter.index].isTransferable">
                        <tr>
                            <td><s:property value="policyFee.breachEncapsulationOfCurrency()"/> <s:property value="policyFee.breachEncapsulationOfAmount()"/></td>
                        </tr>
                    </s:if>
                </s:iterator>
            </table>
        </td>
    </tr>
    <s:if test="loggedInUserAnInternalUser">
        <tr>
            <td nowrap="nowrap" class="labelStyle">
                <s:text name="label.policy.windowPeriod"/>:
            </td>
            <td>
                <s:property value="policyDefinition.transferDetails.windowPeriod"/>
            </td>
        </tr>
        <tr>
            <td nowrap="nowrap" class="labelStyle">
                <s:text name="label.policy.maxNumberOfTransfer"/>:
            </td>
            <td>
                <s:property value="policyDefinition.transferDetails.maxTransfer"/>
            </td>
        </tr>
    </s:if>
</table>


<div style="margin-top: 10px; padding-bottom: 10px;" class="mainTitle">
    <s:text name="label.managePolicy.applicabilityTerms"/>
</div>
<div class="borderTable"> &nbsp;</div>
<table class="grid" width="100%" cellpadding="0" cellspacing="0">

    <tr>
        <td width="20%" height="20" style="padding-left:5px;" nowrap="nowrap" class="labelStyle"><s:text
                name="label.managePolicy.ownershipState"/>:
        </td>
        <td width="86%">
            <s:property value="policyDefinition.availability.ownershipState.name"/>
        </td>
    </tr>
    <tr>
        <td width="20%" height="20" style="padding-left:5px;" nowrap="nowrap" class="labelStyle"><s:text
                name="label.managePolicy.itemCondition"/>:
        </td>
        <td width="86%">
            <s:iterator value="policyDefinition.availability.itemConditions" status="itemsIterator">
                <s:if test="#itemsIterator.index==0">
                    <s:property value="itemCondition"/>
                </s:if>
                <s:else>
                    <s:property value="itemCondition"/>
                </s:else>
            </s:iterator>
        </td>
    </tr>
    <tr>
        <td width="20%" height="20" style="padding-left:5px;" nowrap="nowrap" class="labelStyle" VALIGN="top"><s:text
                name="label.managePolicy.productsCovered"/>:
        </td>
        <td>
            <s:iterator value="policyDefinition.availability.products" status="productsIterator">
                <s:if test="#productsIterator.index==0">
                    <s:property value="product.name"/>
                </s:if>
                <s:else>
                    <br>
                    <s:property value="product.name"/>
                </s:else>
            </s:iterator>
        </td>
    </tr>
</table>
</div>
</u:body>
</html>
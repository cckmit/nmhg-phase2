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
<%@taglib prefix="t" uri="twms"%>
<%@taglib prefix="u" uri="/ui-ext"%>
<style>
.money_symbol{
padding:2px;
width:30px;
float:left;
/*\*/
 * width:30px;
 * border:none;
 * float:none;
/**/
text-align:right;
}
.sample input{
width:54px;
clear:both;
padding:2px;
}
#widget_dealerAutocompleter_dropdown{
z-index: 1001 !important;
}
#widget_productAutocompleter_dropdown{
z-index: 1001 !important;
}
</style>
<div dojoType="twms.widget.TitlePane" title="<s:property value="%{getText('label.managePolicy.registrationTerms')}"/>"
	 labelNodeClass="section_header" open="true" id="policy_registrationTerms">
  
    <div class="mainTitle">
       <s:text name="label.managePolicy.policyPriceDetails"/>
    </div>
<div class="spacer3"></div>
		<table width="96%" border="0" cellpadding="0" cellspacing="0">
    <!-- Registration price section -->
    <tr>
        <td valign="top" class="labelStyle">
            <s:text name="label.managePolicy.policyPrice"/>:
        </td>
    
        <td>
            <s:if test="policyDefinition.policyFees.size!=0">
                <table>
                    <s:iterator value="policyDefinition.policyFees" status="regFeeIter">
                        <s:if test="!policyDefinition.policyFees[#regFeeIter.index].isTransferable">
                           <tr>
                                <td><t:money id="reg_price_%{#regFeeIter.index}"
                                             name="policyDefinition.policyFees[%{#regFeeIter.index}].policyFee"
                                             value="%{policyDefinition.policyFees[#regFeeIter.index].policyFee}"
                                             size="10" defaultSymbol="%{currencyCode}"/></td>
                                <s:hidden name="policyDefinition.policyFees[%{#regFeeIter.index}].isTransferable"
                                          value="false"/>
                            </tr>
                        </s:if>
                    </s:iterator>
                </table>
            </s:if>
            <s:else>
                <s:iterator>
                    <table>
                        <s:iterator value="currencyList" status="currIter">
                            <tr>
                                <td><t:money id="reg_price_%{#currIter.index}"
                                             name="policyDefinition.policyFees[%{#currIter.index}].policyFee"
                                             value="%{policyDefinition.policyFees[#currIter.index].policyFee}"
                                             size="10" defaultSymbol="%{currencyCode}"/>
                                    <s:hidden name="policyDefinition.policyFees[%{#currIter.index}].isTransferable"
                                          value="false"/>
                                </td>
                            </tr>
                        </s:iterator>
                    </table>
                </s:iterator>
            </s:else>
        </td>
    </tr>

    <!-- Tranferable price section -->
    <tr>
        <td class="labelStyle">
            <s:text name="label.managePolicy.transferable"/>:
        </td>
        <td>
            <input type="radio" value="true" name="policyDefinition.transferDetails.transferable" id="transferableYes" class="radiobtn" /> Yes
            <input type="radio" value="false" name="policyDefinition.transferDetails.transferable" id="transferableNo" class="radiobtn" /> No
        </td>
        <script type="text/javascript">
            dojo.addOnLoad(function(){
                var isTransferable = <s:property value="%{policyDefinition.transferDetails.transferable}"/>;
                if(!isTransferable){
                   dojo.byId("transferableNo").checked=true;
                   enableDisableFields(policyFeeListSize,currencyListSize,true);
                }else{
                   dojo.byId("transferableYes").checked=true;
                }
            });
        </script>
    </tr>

    <tr>
        <td class="labelStyle">
            <s:text name="label.managePolicy.transferFee"/>:
        </td>
    
        <td>
            <s:if test="policyDefinition.policyFees.size!=0">
                <table>
                    <s:iterator value="policyDefinition.policyFees" status="transferFeeIter">
                        <s:if test="policyDefinition.policyFees[#transferFeeIter.index].isTransferable">
                            <tr>
                                <td><t:money id="transfer_price_%{#transferFeeIter.index}"
                                             name="policyDefinition.policyFees[%{#transferFeeIter.index}].policyFee"
                                             value="%{policyDefinition.policyFees[#transferFeeIter.index].policyFee}"
                                             size="10" defaultSymbol="%{currencyCode}"/></td>
                                <s:hidden name="policyDefinition.policyFees[%{#transferFeeIter.index}].isTransferable"
                                          value="true"/>
                            </tr>
                        </s:if>
                    </s:iterator>
                </table>
            </s:if>
            <s:else>
                <s:set name="transferCurrIter" value="%{currencyList.size}"/>
                <s:iterator>
                    <table>
                        <s:iterator value="currencyList" status="currIter">
                            <s:set name="CurrIter" value="%{#currIter.index+currencyList.size}"/>
                            <tr>
                                <td><t:money id="transfer_price_%{#CurrIter}"
                                             name="policyDefinition.policyFees[%{#CurrIter}].policyFee"
                                             value="%{policyDefinition.policyFees[#CurrIter].policyFee}"
                                             size="10" defaultSymbol="%{currencyCode}"/>
                                    <s:hidden name="policyDefinition.policyFees[%{#CurrIter}].isTransferable"
                                          value="true"/>
                                </td>
                            </tr>
                        </s:iterator>
                    </table>
                </s:iterator>
            </s:else>
        </td>
    </tr>

    <tr>
        <td class="labelStyle" width="40%" nowrap="nowrap">
            <s:text name="label.policy.windowPeriod"/>
        </td>
        <td>
            <s:textfield name="policyDefinition.transferDetails.windowPeriod" id="windowPeriod"/>
        </td>
    </tr>
    <tr>
        <td class="labelStyle">
            <s:text name="label.policy.maxNumberOfTransfer"/>
        </td>
        <td>
            <s:textfield name="policyDefinition.transferDetails.maxTransfer" id="noOfTransfer"/>
        </td>
    </tr>


    </table>


    <!--javascript to handle the transferable logic -->
    <script type="text/javascript">
                var currencyListSize = "<s:property value="%{currencyList.size+currencyList.size}" />";
                var policyFeeListSize = null;
                <s:if test="policyDefinition.policyFees.size!=0">
                    policyFeeListSize = '<s:property value="policyDefinition.policyFees.size"/>'
                </s:if>
                dojo.addOnLoad(function() {
                    dojo.connect(dojo.byId("transferableNo"), "onclick", function() {
                        enableDisableFields(policyFeeListSize,currencyListSize,true);

                    });
                    dojo.connect(dojo.byId("transferableYes"), "onclick", function() {
                        enableDisableFields(policyFeeListSize,currencyListSize,false);
                    });
                });
                function enableDisableFields(policyFeeListSize,currencyListSize,disable){
                    dojo.byId('windowPeriod').disabled = disable;
                    dojo.byId('noOfTransfer').disabled = disable;
                    dojo.byId('windowPeriod').value = '';
                    dojo.byId('noOfTransfer').value = '';
                        if(policyFeeListSize!=null){
                            for (var i = 0; i < policyFeeListSize; i++){
                                var transferFeeField = dojo.byId("transfer_price_"+i);
                                if(transferFeeField){
                                    dojo.byId(transferFeeField).readOnly=disable;
                                    dojo.byId(transferFeeField).value='';
                                }
                            }
                        }else{
                            for (var i = 0; i < currencyListSize; i++){
                                var transferFeeField = dojo.byId("transfer_price_"+i);
                                if(transferFeeField){
                                    dojo.byId(transferFeeField).readOnly=disable;
                                    dojo.byId(transferFeeField).value='';
                                }
                            }
                        }

                }
            </script>


        <div class="mainTitle" >
            <s:text name="label.managePolicy.productsCovered"/>
        </div>
		<div class="spacer5"></div>
		<jsp:include flush="true" page="policy_productTypes.jsp"/>
                    <br/>
                    
                     <div class="mainTitle" id="divDealersCoveredTitle" >
            <s:text name="label.managePolicy.dealersCovered"/>
        </div>
		<div class="spacer5"></div>
		<jsp:include flush="true" page="policy_applicableServiceProviders.jsp"/>
                    <br/>
                    
                    
             <table width="97.2%" cellpadding="0" cellspacing="0" class="policyRegn_table">
           	 <tr>
                <td colspan="2">
                    <table width="100%" border="0" cellpadding="0" cellspacing="0">
                        <tr>
                            <td width="15%" class="labelStyle" valign="top"><s:property
                                    value="%{getText('label.managePolicy.itemCondition')}"/>:
                            </td>
                            <td width="25%">
                                <s:select cssClass="admin_selections" name="policyDefinition.availability.itemConditions"
                                          list="allInventoryItemCondition" multiple="true"
                                          listKey="itemCondition" listValue="itemCondition"
                                          value="%{policyDefinition.availability.itemConditions.{itemCondition}}"
                                          size="3" theme="simple" />
                            </td>
                            <td width="15%" nowrap="nowrap" valign="top" class="labelStyle">
                                <s:text name="label.managePolicy.ownershipState"/>:
                            </td>
                            <td width="45%" valign="top">
                                    <s:select cssClass="admin_selections" theme="twms"
                                              name="policyDefinition.availability.ownershipState"
                                              value="%{policyDefinition.availability.ownershipState.id}"
                                              list="allOwnershipStates" listKey="id" listValue="name"
                                              emptyOption="false"/>
                            </td>
                        </tr>
                    </table>
                </td>
            </tr>
                   </table>
    </div>


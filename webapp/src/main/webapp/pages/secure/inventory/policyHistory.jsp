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
<%@ taglib prefix="u" uri="/ui-ext" %><head>
	<u:stylePicker fileName="common.css"/>
	<u:stylePicker fileName="form.css"/>
	<u:stylePicker fileName="base.css" />
	<script type="text/javascript">
		dojo.require("dijit.layout.ContentPane");
		dojo.require("dijit.layout.LayoutContainer");
		dojo.require("twms.widget.TitlePane");
	</script>  
</head>

  
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
                  <td nowrap="nowrap" class="labelStyle" width="20%">
                      <s:property value="%{getText('label.common.nomsPolicyOptionCode')}"/>:
                  </td>
                  <td>
                      <div>
                          <s:property value="policyDefinition.nomsPolicyOptionCode"/>
                      </div>
                  </td>
                  <td width="20%" class="labelStyle">
                      <s:property value="%{getText('label.common.nomsTierDescription')}"/>:
                  </td>
                  <td>
                      <s:property value="policyDefinition.nomsTierDescription"/>
                  </td>
              </tr>
              <tr>
                  <td nowrap="nowrap" class="labelStyle">
                      <s:property value="%{getText('columnTitle.listPolicies.priority_code')}"/>:
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
              <s:if test="policyDefinition.warrantyType.type.equals('EXTENDED')">
			  <tr>
		    	<s:if test="loggedInUserAnInternalUser">
		        <td nowrap="nowrap" class="labelStyle">
		            <s:text name="label.managePolicy.forInternalUsersOnly"/>:
		        </td>
		        <td>
		             <s:if test="policyDefinition.forInternalUsersOnly">
		                <s:text name="label.common.yes"/>		                
		            </s:if>
		            <s:else>
		                <s:text name="label.common.no"/>
		            </s:else>
		        </td>
		        </s:if>
		        <td nowrap="nowrap" class="labelStyle">
		            <s:text name="label.managePolicy.attachmentMandatory"/>:
		        </td>
		        <td>
		             <s:if test="policyDefinition.attachmentMandatory">
		                 <s:text name="label.common.yes"/>
		            </s:if>
		            <s:else>
		                <s:text name="label.common.no"/>
		            </s:else>
		        </td>
			  </tr>
			  </s:if>
              <tr>
                  <td nowrap="nowrap" class="labelStyle">
                      <s:property value="%{getText('title.policy.buildDateApplicable')}"/>:
                  </td>
                  <td>
                  	<s:if test="policyDefinition.buildDateApplicable">
                  		<s:text name="label.common.yes"/>
                  	</s:if>
                  	<s:else>
                  		<s:text name="label.common.no"/>
                  	</s:else>                     
                  </td>
                  <td nowrap="nowrap" class="labelStyle">
                      <s:property value="%{getText('title.policy.isThirdPartyPolicy')}"/>:
                  </td>
                  <td>                      
                    <s:if test="policyDefinition.getIsThirdPartyPolicy()">
                  		<s:text name="label.common.yes"/>
                  	</s:if>
                  	<s:else>
                  		<s:text name="label.common.no"/>
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
      </div>
 
   
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
                   <td ><s:property value="policyDefinition.coverageTerms.monthsCoveredFromDelivery"/></td>
               </tr>
               <tr>
                   <td nowrap="nowrap" class="labelStyle"><s:property
                           value="%{getText('label.managePolicy.monthsCoveredFromDateOfShipment')}"/>:
                   </td>
                   <td><s:property value="policyDefinition.coverageTerms.monthsCoveredFromShipment"/></td>
               </tr>
              <%--  <tr>
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
                   <td nowrap="nowrap" class="labelStyle">
                   		<s:text name="label.managePolicy.monthsCoveredFromRegistrationForEWP"/>:
                   </td>
                   <td><s:property value="policyDefinition.coverageTerms.monthsFromDeliveryForEWP"/></td>
               </tr>
               <tr>
                   <td nowrap="nowrap" class="labelStyle">
                   		<s:text name="label.managePolicy.monthsCoveredFromDateOfShipmentForEWP"/>:
                   </td>
                   <td><s:property value="policyDefinition.coverageTerms.monthsFromShipmentForEWP"/></td>
               </tr>
               <tr>
                   <td nowrap="nowrap" class="labelStyle">
                   		<s:text name="label.managePolicy.minMonthsFromDeliveryForEWP"/>:
                   </td>
                   <td><s:property value="policyDefinition.coverageTerms.minMonthsFromDeliveryForEWP"/></td>
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
                        <s:property value="policy.price"/>
                    </td>
                </tr>
                <tr>
                    <td nowrap="nowrap" class="labelStyle">
                        <s:text name="windowPeriod"/>:
                    </td>
                    <td>
                        <s:property value="policyDefinition.transferDetails.windowPeriod"/>
                    </td>
                </tr>
                <tr>
                    <td nowrap="nowrap" class="labelStyle">
                        <s:text name="maxNumberOfTransfer"/>:
                    </td>
                    <td>
                        <s:property value="policyDefinition.transferDetails.maxTransfer"/>
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
                        <s:property value="%{policyDefinition.getTransferFee(getLoggedInUser().getCurrentlyActiveOrganization().getPreferredCurrency().getCurrencyCode())}"/>
                    </td>
                </tr>
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
                                  <br></br>
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
       

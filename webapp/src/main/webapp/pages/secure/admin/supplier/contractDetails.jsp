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

	
	   <div class="policy_section_div">
	   <div class="section_header"><s:text name="label.contractAdmin.contractDetails"/></div>
     <table width="100%" border="0" cellspacing="0" cellpadding="0" class="grid">
     
         <tr>
          <td width="16%" nowrap="nowrap" class="labelStyle"><s:text name="label.contractAdmin.contractCode"/>:</td>
          <td width="34%"><s:property value="contract.id"/></td>
          <td width="18%" class="labelStyle"><s:text name="label.contractAdmin.contractName"/>: </td>
          <td width="32%" ><s:property value="contract.name"/></td>
        </tr>
		<tr>
          <td width="16%" nowrap="nowrap" class="labelStyle"><s:text name="label.contractAdmin.supplier"/>:</td>
          <td width="34%"><s:property value="contract.supplier.name"/></td>
          <td width="18%" class="labelStyle"><s:text name="label.contractAdmin.shipmentLocation"/>:</td>
          <td width="32%"><s:property value="contract.location.code"/></td>
        </tr>
        
        
        <tr>
          <td width="16%" nowrap="nowrap" class="labelStyle"><s:text name="label.contractAdmin.validityFrom"/>: </td>
          <td width="34%"><s:property value="contract.validityPeriod.fromDate"/></td>
          <td width="18%" class="labelStyle"><s:text name="label.contractAdmin.validityTo"/>: </td>
          <td width="32%"><s:property value="contract.validityPeriod.tillDate"/></td>
        </tr>
        <tr>
          <td width="16%" nowrap="nowrap" class="labelStyle"><s:text name="label.contractAdmin.coverage"/>:</td>
          <td width="34%">
          <table><tr>
          <td width="34%" >
          <s:if test="contract.coverageConditions[0].units !=null">
          <s:property value="contract.coverageConditions[0].units"/><s:text name="label.contract.monthsfrom" />
              <s:if test="contract.coverageConditions[0].comparedWith.name() == 'DATE_OF_MANUFACTURE'">
                  <s:text name="label.inventory.dateofManufacture"/>
              </s:if>
              <s:else>
                  <s:text name="label.inventory.dateofServicePurchaseInstallation"/>
              </s:else>          
          </s:if>
          </td>
          </tr>
          <tr>
          <td width="34%">
          <s:if test="contract.coverageConditions[1].units !=null">
          <s:property value="contract.coverageConditions[1].units"/> <s:text name="label.inventory.energyUnits" />
          </s:if>
          </td>
          </tr>
          </table>
          </td>
          <td width="18%" class="labelStyle" title='<s:text name="label.contractAdmin.isPhysicalShipmentNeeded.tootip"/>' style="cursor:default;">
          	<s:text name="label.contractAdmin.isPhysicalShipmentNeeded"/>:</td>
          <td width="34%" >
          <table ><tr ></tr><tr/><tr>
          <td width="32%" class="label"><s:checkbox name="contract.physicalShipmentRequired"
                  disabled="true" value="%{contract.physicalShipmentRequired}" />
          </td>
          </tr></table>
          </td>
        </tr>  
        <tr>
        <td width="16%" class="labelStyle"><s:text name="label.contractAdmin.responsePeriod"/>:</td>
        <td width="34%"><s:property value="contract.supplierResponsePeriod"/></td>
        <td width="16%" class="labelStyle"><s:text name="label.contractAdmin.supplierDisputePeriod"/>:</td>
        <td width="34%"><s:property value="contract.supplierDisputePeriod"/></td>
        </tr>
        <tr>
        <td width="16%" class="labelStyle"><s:text name="label.supplierRecovery.recoveryBasedOn"/>:</td>
        <s:if test="contract.recoveryBasedOnCausalPart">
           <td width="34%" ><s:text name="label.common.causalPart" /></td>
        </s:if>
        <s:else>
            <td width="34%" ><s:text name="label.claim.removedParts" /></td>
        </s:else>
         <td width="16%" class="labelStyle"><s:text name="label.contractAdmin.autodebitEnabled"/>:</td>
         <td width="34%"  ><s:checkbox name="contract.autoDebitEnabled"
                  disabled="true" value="%{contract.autoDebitEnabled}"/></td>
        </tr>
        <tr>       
        <td width="16%" class="labelStyle"><s:text name="label.contractAdmin.processorReviewNeeded"/>:</td>
        <td width="34%" ><s:checkbox name="contract.sraReviewRequired"
                  disabled="true" value="%{contract.sraReviewRequired}"/></td>
        <td width="16%" class="labelStyle"><s:text 
								name="label.contractAdmin.offlineDebit"/></td>
        <td width="34%"  ><s:checkbox name="contract.offlineDebitEnabled"
                  disabled="true" value="%{contract.offlineDebitEnabled}" /></td>          
        
        </tr>
        <s:if test="!contract.rmaNumber.isEmpty()">
         <tr>
          <td width="16%" nowrap="nowrap" class="labelStyle"><s:text name="label.partReturn.RmaNo"/>:</td>
          <td width="34%"><s:property value="contract.rmaNumber"/></td>
        </tr>
        </s:if>	
        <tr>
              <td><label class="labelStyle"><s:text name="label.partReturn.shippingInstruction"/>:</label></td>
        	   <td><s:textarea name="contract.shippingInstruction" value="%{contract.shippingInstruction}" cols="100" rows="4" disabled="true"/></td>
		</tr>	
      </table>
      </div>


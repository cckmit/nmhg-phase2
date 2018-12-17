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
<%@page pageEncoding="UTF-8"%>
<%response.setHeader( "Pragma", "no-cache" );
response.addHeader( "Cache-Control", "must-revalidate" );
response.addHeader( "Cache-Control", "no-cache" );
response.addHeader( "Cache-Control", "no-store" );
response.setDateHeader("Expires", 0); %>

<%@ taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="t" uri="twms"%><%@taglib prefix="u" uri="/ui-ext"%>
<%@taglib prefix="authz" uri="authz"%>

<script type="text/javascript" src="scripts/ui-ext/common/tabs.js">
</script>

<script type="text/javascript">
function computeTotalValue(totalCount,field){
    var fieldValue = parseFloat(field.value);
    if(isNaN(fieldValue)){
        field.value="0.00";
    }
    else {
        field.value=parseFloat(fieldValue).toFixed(2);
    }
    var totalvalue=0.00;
    var totalCnt = parseFloat(totalCount);
    var sectionCost="0";
    for(var i=0; i!=totalCnt; i++)
    {
        if(document.getElementById("Actual_Value_"+i).value!=""){
            sectionCost=document.getElementById("Actual_Value_"+i).value;
        }
        var sectionCost = parseFloat(sectionCost);
        if(!isNaN(sectionCost)){
            totalvalue = totalvalue + sectionCost;
        }
    }
    document.getElementById("totalValue").innerHTML=totalvalue.toFixed(2);
    
}
</script>

<table cellspacing="0" border="0" cellpadding="0" class="grid">
	<tr>
		<td class="labelStyle" width="20%"  nowrap="nowrap"><s:text name="label.common.recoveryClaimNumber" />:</td>
		<td class="labelNormal" >
		<s:if test="recoveryClaim.documentNumber !=null && recoveryClaim.documentNumber.length() > 0" >
			<s:property value="recoveryClaim.recoveryClaimNumber" />-<s:property
				value="recoveryClaim.documentNumber" />
		</s:if>
		<s:else>
		 <s:property value="recoveryClaim.recoveryClaimNumber" />
		</s:else>		
		</td>
	</tr>
	<tr></tr>
</table>
<table width="96%" class="grid borderForTable" cellspacing="0" cellpadding="0">
   <tr class="row_head">
       <th width="28%" align="center"  >
          <s:text name="label.common.detail"/>
       </th>
       <authz:ifUserNotInRole roles="supplier">
       </authz:ifUserNotInRole>
       <authz:ifUserInRole roles="supplier">
       <th width="21%" >&nbsp;</th>
       </authz:ifUserInRole>
       <th width="48%" align="center">
          <s:text name="label.common.individual" />
       </th>
   </tr>
   <tr>
       <td width="28%" valign="top" >
       <table width="100%" cellspacing="0" cellpadding="0" class="NoborderForTable">
           <tr>
               <td width="25%"><b><s:text name="label.common.supplier"/>:</b></td>
               <td><s:property
                   value="recoveryClaim.contract.supplier.name"></s:property></td>
           </tr>
          
       <tr>
           <td><b><s:text name="label.common.contract"/>:</b></td>
           <td>
          <s:if test="(isLoggedInUserASupplier() && !buConfigAMER) || getLoggedInUser().isInternalUser()">
           <u:openTab tabLabel="Contract"
	      		   url="contract_view.action?id=%{recoveryClaim.contract.id}"
	      		   id="Contract"
	      		   cssClass="link">	      
	      	<s:property value="recoveryClaim.contract.name" />
	    </u:openTab>
	    </s:if>
	    <s:else><s:property value="recoveryClaim.contract.name" /></s:else>
           </td>
       </tr>

           <tr>
               <td><b><s:text name="label.common.ship" />:</b></td>
               <td align="center" >
               	<s:checkbox value="recoveryClaim.contract.physicalShipmentRequired" name="physicalShipmentRequiredCheckBox"
               	 disabled="true"></s:checkbox>
               </td>
           </tr>

           <tr>
                 <td><b><s:text name="label.supplier.part.recovery" />:</b></td>
                 <td align="center" >
                   <s:if test="(recoveryClaim.partRecoveryStatus=='Shipment Generated' || recoveryClaim.partRecoveryStatus=='Partially Shipment Generated' || recoveryClaim.partRecoveryStatus=='VPRA Generated')">
                       <s:text name="part.recovery.shipment.generated.status"/>
                  </s:if>
                  <s:else>
                      <s:property value="recoveryClaim.partRecoveryStatus" />
                  </s:else>
                 </td>
             </tr>
       </table>
       </td>
       <authz:ifUserNotInRole roles="supplier">
       </authz:ifUserNotInRole>
       <authz:ifUserInRole roles="supplier">
       <td width="21%" >&nbsp;</td>
       </authz:ifUserInRole>
       <td width="48%">
       <table width="100%" class="NoborderForTable" cellspacing="1" cellpadding="1">
           <tr>
               <td width="25%" class="sectHead"><s:text name="label.common.costElement" /></td>
               <authz:ifUserNotInRole roles="supplier">
               <td width="25%" class="sectHead"><s:text name="label.common.share"/></td>
               </authz:ifUserNotInRole>
               <s:if test="(isLoggedInUserASupplier() && !buConfigAMER) || getLoggedInUser().isInternalUser()">
               <td width="25%" class="sectHead"><s:text name="label.contract.contractValue"/></td>
               </s:if>
               <td width="25%" class="sectHead"><s:text name="label.common.actualValue"/></td>
           </tr>
           <s:set name="totalCount"
               value="%{recoveryClaim.costLineItems.size()}"
               scope="page"></s:set>
           
           <s:set name="supplierCurrency" 
           	value="%{recoveryClaim.contract.supplier.preferredCurrency.currencyCode}" scope="page"></s:set>    

           <s:iterator
               value="recoveryClaim.costLineItems"
               status="lineItemStatus" id="costLineItem">
               <s:hidden
                   name="recoveryClaim.costLineItems[%{#lineItemStatus.index}]"
                   value="%{#costLineItem.id}" />
               <s:hidden
                   name="recoveryClaim.costLineItems[%{#lineItemStatus.index}].section"
                   value="%{#costLineItem.section.id}" />
               <s:hidden
                   name="recoveryClaim.costLineItems[%{#lineItemStatus.index}].actualCost"
                   value="%{#costLineItem.actualCost.breachEncapsulationOfCurrency().getCurrencyCode()}" />
               <s:hidden
                   name="recoveryClaim.costLineItems[%{#lineItemStatus.index}].actualCost"
                   value="%{#costLineItem.actualCost.breachEncapsulationOfAmount()}" />
               <s:hidden
                   name="recoveryClaim.costLineItems[%{#lineItemStatus.index}].supplierCost"
                   value="%{#costLineItem.supplierCost.breachEncapsulationOfCurrency().getCurrencyCode()}" />
               <s:hidden
                   name="recoveryClaim.costLineItems[%{#lineItemStatus.index}].supplierCost"
                   value="%{#costLineItem.supplierCost.breachEncapsulationOfAmount()}" />    
               <s:hidden
                   name="recoveryClaim.costLineItems[%{#lineItemStatus.index}].costAfterApplyingContract"
                   value="%{#costLineItem.costAfterApplyingContract.breachEncapsulationOfCurrency().getCurrencyCode()}" />
               <s:hidden
                   name="recoveryClaim.costLineItems[%{#lineItemStatus.index}].costAfterApplyingContract"
                   value="%{#costLineItem.costAfterApplyingContract.breachEncapsulationOfAmount()}" />
               <tr>
                   <authz:ifUserInRole roles="supplier">            
               			<s:if test="#costLineItem.recoveredCost.isPositive()">
               			  <td class="labelStyle">
               			    <s:text name="%{getMessageKey(#costLineItem.section.name)}"/>
               			  </td> 
               			   <s:if test="!buConfigAMER"> 
               			  	 <td  valign="middle"><s:property
                               value="getCostForSection('costAfterApplyingContract',#costLineItem.section.name,#attr['supplierCurrency'])" />
                            </td>  
                            </s:if>           			   
                            <td  valign="middle">
                               <s:property value="%{#costLineItem.recoveredCost}"/>
                            </td>                  
               		     </s:if>
               	   </authz:ifUserInRole>
               	   <authz:ifUserNotInRole roles="supplier">
	                   <td class="labelStyle"><s:text name="%{getMessageKey(#costLineItem.section.name)}"/>
	                   </td>        	         
        	            <td  valign="middle"><s:property
                	       value="getCostForSection('actualCost',#costLineItem.section.name,#attr['supplierCurrency'])" />
                	   </td>	                 	                  
	                   <td  valign="middle"><s:property
	                       value="getCostForSection('costAfterApplyingContract',#costLineItem.section.name,#attr['supplierCurrency'])" />
	                   </td>
	                   <td  valign="middle">
		                   <s:property value="%{#costLineItem.recoveredCost}"/>
	                   </td>
                   </authz:ifUserNotInRole>
               </tr>
           </s:iterator>
           <tr>
               <td width="25%" class="labelStyle">Total:</td>
               <authz:ifUserNotInRole roles="supplier">
               <td width="15%" >
                   <s:property value="%{getTotalCostForSection('actualCost',#attr['supplierCurrency'])}" />
               </td>
               </authz:ifUserNotInRole>
                <s:if test="(isLoggedInUserASupplier() && !buConfigAMER) || getLoggedInUser().isInternalUser()">
               <td width="30%"><s:property
                   value="%{getTotalCostForSection('costAfterApplyingContract',#attr['supplierCurrency'])}"></s:property>
               </td>
               </s:if>
               <td width="25%" class="labelStyle"
                   nowrap="nowrap"><s:set name="totalCostOfSection"
                   value="%{getTotalCostForSection('recoveredCost',#attr['supplierCurrency'])}" /><s:property
                   value="%{#totalCostOfSection.breachEncapsulationOfCurrency().getCurrencyCode()}" />
               <span
                   id="totalValue"><s:property
                   value="%{#totalCostOfSection.breachEncapsulationOfAmount()}" /></span>
               </td>
           </tr>
            <tr>             
               <td width="25%" class="labelStyle"><s:text name="label.revoveryClaim.acceptedAmount" /></td>
               <authz:ifUserNotInRole roles="supplier">
               <td width="15%" >
                   <s:property value="%{getTotalCostForSection('actualCost',#attr['supplierCurrency'])}" />
               </td>
               </authz:ifUserNotInRole>
                <s:if test="(isLoggedInUserASupplier() && !buConfigAMER) || getLoggedInUser().isInternalUser()">
               <td width="30%"><s:property
                   value="%{getTotalCostForSection('costAfterApplyingContract',#attr['supplierCurrency'])}"></s:property>
               </td>
          		</s:if>
                <td>
                      <s:property value="%{getAcceptedCostForSection('recoveredCost',#attr['supplierCurrency'])}"/>
			           
			     </td>
           </tr>
           
       </table>
        </td>
   </tr>
</table>




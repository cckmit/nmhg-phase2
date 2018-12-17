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
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="t" uri="twms"%>
<%@taglib prefix="u" uri="/ui-ext"%>
<%@taglib prefix="authz" uri="authz"%>

<div class="policy_section_div">
  <div class="section_header">
    <s:text 
				name="section.label.claimDetails" />
  </div>
  <table width="100%" border="0" cellspacing="0" cellpadding="0">
    <tr>
      <td><s:set name="lastClaimId" value="" />
        <s:set name="firstClaim" value="true" />
        <s:iterator value="taskInstancesForShipment" status="taskInstanceStatus" id="taskInstance">
        <s:set value="#taskInstance.getVariable('recoveryClaim')" name="recClaim" />
        <s:set value="#taskInstance.getVariable('supplierPartReturn')"	name="supplierPartReturn" />
        <s:if test="!firstClaim && #lastClaimId != #recClaim.id">
      </td>
    </tr>
  </table>
  </td>
  </tr>
  </s:if>
  <s:if test="#lastClaimId != #recClaim.id">
  <s:set name="lastClaimId" value="%{#recClaim.id}" />
  <s:set name="firstClaim" value="false" />
  <s:if test="inboxViewId != -1">
       <s:hidden name="id" value="%{#recClaim.id}"/>
   </s:if>
  <tr>
    <td><table border="0" cellpadding="0" cellspacing="0"  class="gridTab" align="center" width="100%">
        <tr>
          <td colspan="9" valign="middle" nowrap="nowrap"><table width="100%" border="0" cellpadding="0" cellspacing="0">
              <tr>
                <td width="15%" class="label"><s:text 
										name="label.claimNumber" /></td>
                <td width="20%" class="labelNormal">
                	<authz:ifUserNotInRole roles="partShipperLimitedView">
	                	<u:openTab tabLabel="Claim Number %{claim.claimNumber}"
							      			 url="view_search_detail.action?id=%{#recClaim.claim.id}"
							      			 id="claimIdForPart%{#recClaim.claim.id}"
							      			 cssClass="Inboxlink"
	      			 						 decendentOf="%{getText('label.invTransaction.tabHeading')}">
	                    <s:property value="%{#recClaim.claim.claimNumber}" />
	                  </u:openTab>
                  </authz:ifUserNotInRole>
                   <authz:else><s:property value="%{#recClaim.claim.claimNumber}" /></authz:else>
                </td>
                <td width="18%" class="label"><s:text 
										name="label.serialNumber" />
                  :</td>
                <td class="labelNormal">
                	<authz:ifUserNotInRole roles="partShipperLimitedView">
	                	<u:openTab tabLabel="%{getText('label.serialNumber'} %{#recClaim.claim.itemReference.referredInventoryItem.serialNumber}"
							      			 url="inventoryDetail.action?id=%{#recClaim.claim.itemReference.referredInventoryItem.id}"
							      			 id="SerialNoForPart%{#recClaim.claim.id}" cssClass="Inboxlink">
	                    <s:property value="%{#recClaim.claim.itemReference.referredInventoryItem.serialNumber}" />
	                  </u:openTab>
                  </authz:ifUserNotInRole>
                  <authz:else><s:property value="%{#recClaim.claim.itemReference.referredInventoryItem.serialNumber}" /></authz:else>
                  </td>
              </tr>
              <tr>
                <td width="15%" nowrap="nowrap" class="label"><s:text 
										name="label.modelNumber" />
                  :</td>
                <td width="20%" class="labelNormal"><s:property
										value="%{#recClaim.claim.itemReference.unserializedItem.model.name}" /></td>
                <td width="18%" nowrap="nowrap" class="label"><s:text 
										name="label.claim.workOrderNumber" />
                  :</td>
                <td class="labelNormal"><s:property
										value="%{#recClaim.claim.workOrderNumber}" /></td>
              </tr>
            </table></td>
        </tr>
      </table></td>
  </tr>
  <tr>
    <td><table width="98%" border="0" cellpadding="1" cellspacing="1" class="gridIssue" align="center" bgcolor="#CCCCCC">
        <tr>
          <td width="3%" valign="middle" nowrap="nowrap"
								class="warColHeader" align="center"><input 
								id='selectAll_<s:property value="%{#lastClaimId}"/>' type="checkbox"
								value="checkbox" checked="checked" />
            <script>
									var _<s:property value="%{#lastClaimId}"/> = 
										new CheckBoxListControl( dojo.byId('selectAll_<s:property value="%{#lastClaimId}"/>') );																		
								</script></td>
          <td width="9%" valign="middle" nowrap="nowrap"
								class="warColHeader"><s:text 
								name="label.supplierPartNumber" /></td>
          <td width="9%" valign="middle" nowrap="nowrap"
								class="warColHeader"><s:text 
								name="label.partNumber" /></td>
          <td width="40%" valign="middle" nowrap="nowrap"
								class="warColHeader"><s:text 
								name="label.description" /></td>
          
            <td width="6%" valign="middle" nowrap="nowrap"
								class="warColHeader"><s:text 
								name="label.quantity" /></td>
         
          <td width="12%" valign="middle" nowrap="nowrap"
								class="warColHeader"><s:text 
								name="label.dueDate" /></td>
          <td width="11%" nowrap="nowrap" class="warColHeader"><s:text 
								name="label.dueDays" /></td>
          <td width="11%" nowrap="nowrap" class="warColHeader"><s:text 
								name="label.overdueDays" /></td>
          <td width="11%" nowrap="nowrap" class="warColHeader"><s:text name="columnTitle.recoveryClaim.rgaNumber"/></td>
        </tr>
        </s:if>
        
        <tr>
          <td width="3%" nowrap="nowrap" valign="middle" align="center"
								class="warColDataBg"><s:checkbox onchange="enableOrDisableRGA(this.checked,%{#taskInstanceStatus.index})"
								id="_%{#taskInstanceStatus.index}"
								name="taskInstances"
								fieldValue="%{id}" value="true" />
            <script>							
										var selectElement = 
											dojo.byId('_<s:property value="%{#taskInstanceStatus.index}"/>');
										_<s:property value="%{#lastClaimId}" />.addListElement(selectElement);
								</script></td>
          <td width="9%" nowrap="nowrap" class="warColDataBg"><s:property
								value="#supplierPartReturn.recoverablePart.supplierItem.number" /></td>
          <td width="9%" nowrap="nowrap" class="warColDataBg"><s:property
								value="#supplierPartReturn.recoverablePart.oemPart.itemReference.unserializedItem.number" /></td>
          <td width="28%" class="warColDataBg"><s:property
								value="#supplierPartReturn.recoverablePart.oemPart.itemReference.unserializedItem.description" /></td>
          
            <td width="6%" class="warColDataBg"><s:property
									value="#supplierPartReturn.recoverablePart.receivedFromSupplier" /></td>
        
          <td width="12%"  align="center" class="warColDataBg"><s:property
								value="#supplierPartReturn.dueDate" /></td>
          <td width="11%"  align="center" class="warColDataBg"><s:property
								value="%{#supplierPartReturn.dueDays <0 ? '-' :#supplierPartReturn.dueDays}" /></td>
          <td width="11%"  align="center" class="warColDataBg"><s:property
								value="%{#supplierPartReturn.dueDays >0 ? '-' :-#supplierPartReturn.dueDays}" /></td>
          <td width="12%"  align="center" class="warColDataBg">
              <s:textfield cssStyle="margin:5px;" size="16" id="rgaNumber_%{#taskInstanceStatus.index}"
                                  	                   name="supplierPartReturnBeans[%{#taskInstanceStatus.index}].rgaNumber" value="%{#supplierPartReturn.rgaNumber}"/>
              <s:hidden name="supplierPartReturnBeans[%{#taskInstanceStatus.index}]" value="%{#supplierPartReturn}" />
          </td>
        </tr>
        <script type="text/javascript">					
           function enableOrDisableRGA(status,index)
              {
  	           var index = index;
               status=!status;
               if(dojo.byId("rgaNumber_"+index)){
            	  dojo.byId("rgaNumber_"+index).disabled = status;
                }
              }
			</script>
        </s:iterator>
        
        <%-- Complete the table Begin --%>
      </table></td>
  </tr>
  <%-- Complete the table End --%>
  </table>
</div>

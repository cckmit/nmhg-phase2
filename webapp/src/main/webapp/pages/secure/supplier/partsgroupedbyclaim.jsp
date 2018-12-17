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
<%@ taglib prefix="sd" uri="/struts-dojo-tags"%>

<div  class="policy_section_div">
  <div class="section_header">
    <s:text	name="section.label.claimDetails" />
  </div>
  <table  border="0" cellspacing="0" cellpadding="0"  width="100%">
    <tr>
      <td><s:if test="%{taskInstances.size() > 0}">
        <table border="0" cellspacing="0" cellpadding="0" width="100%">
          <tr>
            <td><table width="100%" border="0" cellspacing="0" cellpadding="0">
                <s:set name="lastClaimId" value="" />
                <s:set name="firstClaim" value="true" />
                <s:set name="partsCounter" value="0"/>
                <s:iterator id="taskInstance" value="taskInstances"
						status="taskInstanceStatus">
                <s:set name="partsCounter" value="%{#partsCounter + 1}"/>
                <s:set value="%{#taskInstance.getVariable('recoveryClaim')}" name="recClaim" />
                <s:set value="%{#taskInstance.getVariable('supplierPartReturn')}" name="supplierPartReturn"/>
                <s:if test="!firstClaim && #lastClaimId != #claim.id">
                
              </table></td>
          </tr>
          </s:if>
          
          <s:if test="#lastClaimId != #recClaim.id">
          
          <s:set name="lastClaimId" value="%{#recClaim.id}" />
          <s:set name="firstClaim" value="false" />
          <tr>
            <td><table class="grid" align="left" width="100%" cellpadding="0" cellspacing="0">
                <tr>
                  <td colspan="9" valign="middle" nowrap="nowrap"><table width="100%" border="0" cellpadding="0" cellspacing="0">
                      <tr>
                        <td width="15%" class="label" ><s:text
										name="label.claimNumber" /></td>
                        <td width="20%" class="labelNormal">
                        	<authz:ifUserNotInRole roles="partShipperLimitedView">
	                        	<u:openTab
											tabLabel="%{getText('label.claimNumber')} %{#recClaim.claim.claimNumber}"
											url="view_search_detail.action?id=%{#recClaim.claim.id}"
											id="claimIdForPart%{#claim.id}" cssClass="alinkclickable">
	                            <s:property value="%{#recClaim.claim.claimNumber}" />
	                          </u:openTab>
                          </authz:ifUserNotInRole>
                          <authz:else><s:property value="%{#recClaim.claim.claimNumber}" /></authz:else>
                          </td>
                        <td width="18%" class="label"><s:text
										name="label.serialNumber" /></td>
                        <td class="labelNormal">
                        	<authz:ifUserNotInRole roles="partShipperLimitedView">
	                        	<u:openTab
											tabLabel="%{getText('label.serialNumber')} %{#claim.itemReference.referredInventoryItem.serialNumber}"
											url="inventoryDetail.action?id=%{#recClaim.claim.itemReference.referredInventoryItem.id}"
											id="SerialNoForPart%{#claim.id}" cssClass="alinkclickable">
	                            <s:property value="%{#recClaim.claim.itemReference.referredInventoryItem.serialNumber}" />
	                          </u:openTab>
                           </authz:ifUserNotInRole>
                          <authz:else><s:property value="%{#recClaim.claim.itemReference.referredInventoryItem.serialNumber}" /></authz:else>
                          </td>
                      </tr>
                      <tr>
                        <td width="15%" nowrap="nowrap" class="label"><s:text
										name="label.modelNumber" /></td>
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
                <tr>
                  <td width="3%" valign="middle" nowrap="nowrap" class="warColHeader" align="center" style="border:1px solid #b6c2cf">
                  <input id='selectAll_<s:property value="%{#lastClaimId}"/>'
								type="checkbox" value="checkbox" checked="checked" />
                    <script>
									var _<s:property value="%{#lastClaimId}"/> = 
										new CheckBoxListControl( dojo.byId('selectAll_<s:property value="%{#lastClaimId}"/>') );																		
								</script></td>
                  <td width="9%" valign="middle" nowrap="nowrap" class="warColHeader"  style="border:1px solid #b6c2cf"><s:text
										name="label.supplierPartNumber" /></td>
                  <td width="9%" valign="middle" nowrap="nowrap" class="warColHeader"  style="border:1px solid #b6c2cf"><s:text
										name="label.partNumber" /></td>
                  <td width="28%" valign="middle" nowrap="nowrap" class="warColHeader"  style="border:1px solid #b6c2cf"><s:text
										name="label.description" /></td>
                 
                    <td width="6%" valign="middle" nowrap="nowrap" class="warColHeader"  style="border:1px solid #b6c2cf"><s:text
											name="label.quantity" /></td>
                
                  <td width="12%" valign="middle" nowrap="nowrap"
								class="warColHeader" style="border:1px solid #b6c2cf""><s:text
										name="label.common.returnlocation" /></td>
                  <td width="12%" valign="middle" nowrap="nowrap"
								class="warColHeader" style="border:1px solid #b6c2cf"><s:text
										name="label.recoveryClaim.binInfo" /></td>
                  <td width="12%" valign="middle" nowrap="nowrap"
								class="warColHeader"  style="border:1px solid #b6c2cf"><s:text name="columnTitle.recoveryClaim.rgaNumber"/></td>
                </tr>
                </s:if>
                
                <tr>
                  <td width="3%" nowrap="nowrap" valign="middle"
								 style="border:1px solid #b6c2cf" align="center"><s:checkbox
					onchange="enableOrDisableRGA(this.checked,%{#taskInstanceStatus.index})" id='_%{#taskInstanceStatus.index}' name="taskInstances"
								fieldValue="%{id}" value="true" />
                    <script>							
										var selectElement = 
											dojo.byId('_<s:property value="%{#taskInstanceStatus.index}"/>');
										_<s:property value="%{#lastClaimId}" />.addListElement(selectElement);
								</script>
								</td>
                  <td width="9%" nowrap="nowrap"  style="border:1px solid #b6c2cf"><s:property
								value="#supplierPartReturn.recoverablePart.supplierItem.number" /></td>
                  
                  <td width="9%" nowrap="nowrap"  style="border:1px solid #b6c2cf"><s:property
								value="#supplierPartReturn.recoverablePart.oemPart.itemReference.unserializedItem.number" />
				   <s:hidden name="recoverableParts" value="#supplierPartReturn.recoverablePart"/>
				   </td>
                  <td width="40%"  style="border:1px solid #b6c2cf"><s:property
								value="#supplierPartReturn.recoverablePart.oemPart.itemReference.unserializedItem.description" /></td>
                 
                    
                    <td width="6%"  style="border:1px solid #b6c2cf"><s:property
									value="#supplierPartReturn.recoverablePart.receivedFromSupplier" /></td>
                  
                  <td width="12%"  style="border:1px solid #b6c2cf" valign="center">
                     <%@ include file="preview/selectSupplierLocation.jsp" %>
                  </td>
                  <td width="12%"  style="border:1px solid #b6c2cf" valign="center"><s:property
								value="#supplierPartReturn.returnLocation.code" /></td>
                  <td width="12%"  style="border:1px solid #b6c2cf" valign="center">  
                  <s:textfield cssStyle="margin:5px;" size="16" id="rgaNumber_%{#taskInstanceStatus.index}"
                                                    	                   name="supplierPartReturnBeans[%{#taskInstanceStatus.index}].rgaNumber" value="%{#supplierPartReturn.rgaNumber}"/>
                                <s:hidden name="supplierPartReturnBeans[%{#taskInstanceStatus.index}]" value="%{#supplierPartReturn}" />
                    &nbsp;</td>
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
        </table></td>
    </tr>
  </table>
  </s:if>
  <s:else> No Part available. </s:else>
</div>


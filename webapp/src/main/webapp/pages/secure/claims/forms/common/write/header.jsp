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
<%@ taglib prefix="sd" uri="/struts-dojo-tags"%>
<%@taglib prefix="t" uri="twms"%><%@taglib prefix="u" uri="/ui-ext"%>
<%@taglib prefix="authz" uri="authz"%>
<%@ taglib prefix="tda" uri="twmsDomainAware" %>
<script type="text/javascript" src="scripts/thirdParty/thirdPartySearch.js"></script>

<table class="grid" cellspacing="0" cellpadding="0" id="claim_header_table">

	<tr>
	   <td width="20%" class="labelStyle" nowrap="nowrap"><s:text name="label.common.claimNumber"/>: </td>
	   <s:if test="claim.claimNumber==null && claim.state.state.equalsIgnoreCase('draft')">
	   <td width="30%"><s:text name="label.viewClaim.draft"/></td>
	   </s:if>
	   <s:else>
	   <td width="30%">
      	<s:property value="claim.claimNumber"/>

       <s:if test="claim.histClmNo!=null">
       (<s:property value="claim.histClmNo"/>)
       </s:if>
       </td>
       </s:else>

       <td width="20%" class="labelStyle" nowrap="nowrap"><s:text name="label.common.claimType"/>:</td>
       <td width="30%"><s:property value="getText(claim.type.displayType)"/></td>

   </tr>
      <s:if test="isLoggedInUserAnInternalUser()">
     <tr>
        <td class="labelStyle" nowrap="nowrap"><s:text name="columnTitle.viewClaim.claimStatus"/>:</td>
        <td><s:property value="claim.state.state"/></td>
        <s:if test="claim.recoveryClaims.size() > 0">
	        <td class="labelStyle" nowrap="nowrap"><s:text name="label.recoveryClaim.recoveryClaimState"/>:</td>
	          <td><s:property value="claim.getLatestRecoveryClaim().recoveryClaimState.state"/></td>
	    </s:if>
     </tr>
  </s:if>
    <tr>
        <s:if test="!isLoggedInUserAnInternalUser()">
	         <authz:ifUserInRole roles="dealer">
	         <td colspan="2">&nbsp;</td>
	             <td class="labelStyle" nowrap="nowrap" style="padding-bottom:5px;"><s:text name="columnTitle.viewClaim.claimStatus"/>:</td>
	                <s:if test="claim.statusListInProgressForDealer">
	                   <td><s:text name="label.claim.inProgress" /></td>
	                </s:if>
	                <s:else>
		            	 <td><s:property value="claim.state.state"/></td>
		           	</s:else>
	         </authz:ifUserInRole>
        </s:if>
    </tr>
   <tr>
    <s:if test="claim.hoursOnTruck != null">
		<td class="labelStyle" nowrap="nowrap"><s:text name="label.common.hoursOnTruckDuringIstallation"/>:</td>
		<td><s:property value="claim.hoursOnTruck"/></td>
	</s:if>
	</tr>
	<s:if test="dealerEligibleToFillSmrClaim">
   <tr>
   <td  class="labelStyle" nowrap="nowrap"  style="padding-bottom:5px;"><s:text name="label.viewClaim.requestSMR"/>:</td>
       <s:if test="claim.serviceManagerRequest">
           <td><s:text name="label.common.yes"/></td>
       </s:if>
       <s:else>
           <td colspan="3"><s:text name="label.common.no"/></td>
       </s:else>

       <s:if test="claim.serviceManagerRequest">
           <td  class="labelStyle" nowrap="nowrap" style="padding-bottom:15px;"><s:text name="label.viewClaim.smrReason"/>:</td>
           <td style="padding-bottom:15px;">
               <s:property value="claim.reasonForServiceManagerRequest.description"/>
           </td>
       </s:if>
   </tr>
   </s:if>
   <tr>
	<s:if test="claim.cmsAuthCheck">
       <td style="padding-bottom:5px;" nowrap="nowrap">
            <label id="authNumberLabel" class="labelStyle"> <s:text name="label.viewClaim.authNumber"/>: </label>
        </td>
        <td><s:property value="claim.authNumber" />
        </td>
   </s:if>
    <s:if test="claim.getCmsTicketNumber()!= null && !claim.getCmsTicketNumber().isEmpty()">
        <td class="labelStyle" style="padding-bottom:5px;" nowrap="nowrap"><s:text name="label.viewClaim.cmsTicket"/>:
        </td>
        <td style="padding-bottom:5px;" nowrap="nowrap">
        <%-- <a href="<s:property value='applicationSettings.cmsUrl'/><s:property value='claim.cmsTicketNumber'/>" target="_new"> --%>
        <s:property value="claim.cmsTicketNumber"/>
       <!--  </a> -->
        </td>
    </s:if>
   </tr>
    <s:if test="%{showAuthorizationReceived()}">
    <tr>
      <td  class="labelStyle"  style="padding-bottom:5px;" nowrap="nowrap"><s:text name="label.viewClaim.cmsAuth"/>:</td>
       <s:if test="claim.cmsAuthCheck">
           <td><s:text name="label.common.yes"/></td>
       </s:if>
          <s:else>
	           <td><s:text name="label.common.no"/></td>
	       </s:else>
     </tr>
     </s:if>
     <tr>
     	<s:if test="isLoggedInUserAnAdmin() && enableWarrantyOrderClaims()">
        	<td width="20%" class="labelStyle"  style="padding-bottom:5px;" nowrap="nowrap"><s:text name="label.viewClaim.warrantyOrderClaim"/>:</td>
                   <s:if test="claim.warrantyOrder">
                       <td width="30%"><s:text name="label.common.yes"/></td>
                   </s:if>
                      <s:else>
            	           <td width="30%"><s:text name="label.common.no"/></td>
            	       </s:else>
       	</s:if>
			<td width="20%" class="labelStyle" nowrap="nowrap"><s:text
						name="columnTitle.viewClaim.auditClaim" />:</td>
		       <s:if test="claim.manualReviewConfigured">
		           <td width="30%"><s:text name="label.common.yes"/></td>
		       </s:if>
		       <s:else>
		           <td width="30%"><s:text name="label.common.no"/></td>		
		       </s:else>
      </tr>
     <tr>
     <s:if test="(isLoggedInUserAnInternalUser() || context.equals('ClaimSearches')) && displayNCRandBT30DayNCROnClaimPage()" >
           <td  class="labelStyle"  style="padding-bottom:5px;" nowrap="nowrap"><s:text name="label.claim.ncr"/>:</td>
	       <s:if test="claim.ncr">
	           <td><s:text name="label.common.yes"/></td>
	       </s:if>
	       <s:else>
		           <td><s:text name="label.common.no"/></td>
		   </s:else></s:if>
		<s:if test="displayNCRandBT30DayNCROnClaimPage()">
			<td class="labelStyle" style="padding-bottom: 5px;" nowrap="nowrap"><s:text
					name="label.claim.ncrWith30Days" />:</td>
			<s:if test="claim.ncrWith30Days">
				<td><s:text name="label.common.yes" />&nbsp;(<s:property value='%{claim.inventoryClassFor30DayNcr.name}'/>)</td>
			</s:if>
			<s:else>
				<td><s:text name="label.common.no" /></td>
			</s:else>
		</s:if>
	</tr>
   <s:if test="%{displayCPFlagOnClaimPgOne}">
   <tr>
   <td class="labelStyle"  style="padding-bottom:5px;" nowrap="nowrap"><s:text name="label.newClaim.commercialPolicy"/>:</td>
    		<s:if test ="claim.commercialPolicy">
	             <td><s:text name="label.common.yes"/></td>
	        </s:if>
	        <s:else>
	            <td><s:text name="label.common.no"/></td>
	        </s:else>
   </tr>
   </s:if>
   <s:if test="displayEmissionOnClaimPage()">   
          <tr>  
	           <td  class="labelStyle"  style="padding-bottom:5px;" nowrap="nowrap"><s:text name="label.viewClaim.emission"/>:</td>
		       <s:if test="claim.emission">
		           <td><s:text name="label.common.yes"/></td>
		       </s:if>
		       <s:else>
			           <td><s:text name="label.common.no"/></td>
			   </s:else>
     	  </tr>     
     </s:if> 
   	<tr><td colspan="4" class="borderTable"></td></tr>
	<tr style="height:4px; "><td ></td></tr>
   <tr>
       <td class="labelStyle" nowrap="nowrap"><s:text name="label.common.dealer"/>: </td>
       	<authz:ifUserInRole roles="processor">
	       	<s:if test="isThirdPartyClaim()">
		   	<td id = "selectThirdParty" >
		   			<s:hidden name="thirdPartyDealerName" id="dealer"/>
		        	<div id="selectedThirdPartyDisplayName"/>
					<s:hidden id="thirdPartyForDealer" value="%{claim.forDealerShip.name}" />
			        		<s:property value="claim.forDealerShip.name"/>
			    	</div>
			    	<s:if test="claim.eligibleForEdit">
				      	<a id="thirdPartyAddLinkId" class="link" >
				        	<s:text name="label.viewClaim.thirdPartyAddLink"/>
						</a>
						<script type="text/javascript">
			             dojo.connect(dojo.byId("thirdPartyAddLinkId"), "onclick", function() {
			            		dojo.publish("/thirdPartySearch/show");
							});
					    </script>
					</s:if>
			</td>
			</s:if>
	       	<s:else>
	       	<td id = "viewLink" >
				<a id="show_dealer_info" class="link">
					<s:property value="claim.forDealerShip.name"/>
				</a>
			</td>
		</s:else>
       	</authz:ifUserInRole>
       	<authz:ifUserNotInRole roles="processor">
		       	<td id = "viewLink" >
					<a id="show_dealer_info" class="link">
						<s:property value="claim.forDealerShip.name"/>
						<s:hidden id="thirdPartyForDealer" value="%{claim.forDealerShip.name}" />
					</a>
				</td>
		</authz:ifUserNotInRole>

		<s:if test="!isMatchReadApplicable()">
			<s:if test="claim.claimedItems.size() > 0 && claim.claimedItems[0].itemReference.referredInventoryItem != null &&
			claim.claimedItems[0].itemReference.referredInventoryItem.type.type.equals('RETAIL')">
			   <td  class="labelStyle" nowrap="nowrap"><s:text name="label.warrantyAdmin.ownerName"/>: </td>
	           <td id = "viewLink" >
				  <a id="show_owner_info" class="link">
					<s:property value="claim.claimedItems[0].itemReference.referredInventoryItem.ownedBy.name"/>
				  </a>
			   </td>
		 	</s:if>
		</s:if>
		<s:if test="((claim.type.type == 'Machine' || claim.type.type == 'Attachment') && !claim.itemReference.isSerialized())
      		|| (claim.type.type == 'Parts' && claim.partInstalled && !claim.itemReference.isSerialized())">
	 		<s:if test=" (claim.claimNumber==null && claim.state.state.equalsIgnoreCase('draft') )
	 			|| claim.state.state.equalsIgnoreCase('forwarded') || baseFormName == 'processor_review'">
					<jsp:include flush="true" page="../write/nonSerializedOwnerInfoInclude.jsp"/>
			</s:if>
			<s:else>
				<s:set name="displayNonSerializedOwnerInfo" value="1"/>
				<td  class="labelStyle" nowrap="nowrap"><s:text name="label.warrantyAdmin.ownerName"/>: </td>
				<td nowrap="nowrap">
				  <a id="show_nonserialized_owner_info" class="link">
					<s:property value="claim.ownerInformation.belongsTo.name"/>
				  </a>
			   </td>
			</s:else>
		</s:if>


    </tr>

   <tr>
       <td  class="labelStyle" nowrap="nowrap"><s:text name="label.common.dateOfClaim"/>:</td>
       <td>
	       	<sd:datetimepicker name='task.claim.filedOnDate' value='%{claim.filedOnDate}' id='filedOnDate' />
       </td>
       <s:if test="%{showDealerJobNumber()}">
       <td class="labelStyle" nowrap="nowrap"><s:text name="label.claim.workOrderNumber"/>:</td>
	   <td class="labelStyle" nowrap="nowrap">
			<s:hidden name="claim"/>
			<s:textfield name="task.claim.workOrderNumber" value ="%{claim.workOrderNumber}"/>
	   </td>
	   </s:if>
   </tr>

	<s:if test="claim.type.type == 'Parts'">
	<s:if test="claim.partSerialNumber!=null && !claim.partSerialNumber.isEmpty()">
		 <tr>
	        <td  class="labelStyle" nowrap="nowrap">
            	 <s:text name="label.newClaim.partSerialNo" />:
	        </td>

	        <td>
           		<s:property value="claim.partSerialNumber" />
	        </td>

	    </tr>
	    </s:if>
		<tr>

	        <td  class="labelStyle" nowrap="nowrap">
            		<s:text name="label.common.partNumber"/>:
	        </td>

	        <td>
           		<s:property value="claim.brandPartItem.itemNumber"/>
	        </td>
			 <td  class="labelStyle" nowrap="nowrap">
            		<s:text name="label.partInventory.searchParts.partDescription"/>
	        </td>

	        <td>
           		 <s:property value="claim.partItemReference.referredItem.description"/>
	        </td>

	    </tr>

	   </s:if>
   <%--  <s:if test="claim.type.type == 'Parts'">
		<tr>
	        <td  class="labelStyle" nowrap="nowrap"><s:text name="label.common.isPartInstalled"/>:</td>
	        <td>
	        	<s:if test="claim.partInstalled">
            		<s:text name="label.common.yes"/>
            	</s:if>
            	<s:else>
            		<s:text name="label.common.no"/>
            	</s:else>
	        </td>
	    </tr>
    </s:if>  --%>
   <s:if test="claim.type.type != 'Campaign'">
      <tr>
         <td  class="labelStyle" nowrap="nowrap"><s:text name="label.viewClaim.dateOfFailure"/>:</td>
         <td>
	       	<sd:datetimepicker name='task.claim.failureDate' value='%{claim.failureDate}' id='failureDate' />
         </td>
       </tr>
             <tr>
         <td  class="labelStyle" nowrap="nowrap"><s:text name="label.viewClaim.repairStartDate"/>:</td>
         <td>
	       	<sd:datetimepicker name='task.claim.repairStartDate' value='%{claim.repairStartDate}' id='repairStartDate' />
         </td>

          <td  class="labelStyle" nowrap="nowrap"><s:text name="label.viewClaim.dateOfRepair"/>:</td>
          <td>
    	       <sd:datetimepicker name='task.claim.repairDate' value='%{claim.repairDate}' id='claimRepairDate' />
	      </td>
       </tr>

 		<s:if test="claim.purchaseDate != null">
             <tr>
		        <td class="labelStyle" nowrap="nowrap"><s:text name="label.common.dateOfPurchase"/>:</td>
		        <td>
		        	<sd:datetimepicker name='task.claim.purchaseDate' value='%{claim.purchaseDate}' id='purchaseDate' />
		        </td>
	        </tr>
	    </s:if>
	    <tr>
	    <s:if test="!claim.getCompetitorModelDescription().isEmpty()">
				<td class="labelStyle" nowrap="nowrap" ><s:text name="label.newClaim.productModel"/>:</td>
				<td>
					<s:property value='claim.competitorModelDescription' />
				</td>	
					 
				 <tr>
				<td class="labelStyle" nowrap="nowrap" ><s:text name="label.newClaim.competitorModelBrand"/>:</td>
				<td>
					 <s:property value='claim.competitorModelBrand' /> 
				 
				</td>	 </tr>
				 <tr>
				<td class="labelStyle" nowrap="nowrap" ><s:text name="label.newClaim.competitorModelTruckSerialNumber"/>:</td>
				<td>
					<s:property value='claim.competitorModelTruckSerialnumber' />
				 
				</td>	 </tr>	
						        
			</s:if>
			
			<s:if test="claim.installationDate != null">
				<td class="labelStyle" nowrap="nowrap"><s:text name="label.common.installationDate"/>:</td>
				<td>
					<sd:datetimepicker name='task.claim.installationDate' value='%{claim.installationDate}' id='installationDate' />
				</td>
			</s:if>
			<s:if test="claim.invoiceNumber != null">
				<td class="labelStyle" nowrap="nowrap"><s:text name="label.claim.invoiceNumber"/>:</td>
				<td><s:property value="claim.invoiceNumber"/></td>
			</s:if>
	    </tr>
	    <s:if test="claim.hoursOnPart != null">
	             <tr>
			        <td class="labelStyle" nowrap="nowrap"><s:text name="label.common.hoursOnPart"/>:</td>
			        <td><s:property value="claim.hoursOnPart"/></td>
		        </tr>
	    </s:if>
	  	<s:if test="claim.type.type == 'Parts'">
	  		<tr>
	  			<td class="labelStyle" nowrap="nowrap"><s:text name="label.common.hoursOnTruck"/>:</td>
	  			<td><s:property value="claim.getClaimedItems().get(0).getHoursInService()" /></td>
	  		</tr>
	  	</s:if>
   </s:if>
   <s:else>
         <tr>
<td  class="labelStyle" nowrap="nowrap"><s:text name="label.viewClaim.repairStartDate"/>:</td>
         <td>
	       	<sd:datetimepicker name='task.claim.repairStartDate' value='%{claim.repairStartDate}' id='repairStartDate' />
         </td>

          <td  class="labelStyle" nowrap="nowrap"><s:text name="label.viewClaim.dateOfRepair"/>:</td>
          <td>
    	       <sd:datetimepicker name='task.claim.repairDate' value='%{claim.repairDate}' id='claimRepairDate' />
	      </td>
        </tr>
   </s:else>
   	<authz:ifUserInRole roles="processor">
   	<s:if test="policyCodes.size()>0 && !amerClaimAcceptedEarlier(claim)">
          <tr>
          <td width="22%">
            <label for="policyCode"  class="labelStyle"><s:text name="label.viewClaim.policyCode"/>:</label>
        </td>
<td><s:if test="claim.policyCode!=null">
						<s:select id="policy_code" cssStyle="width:180px;"
							headerKey="" headerValue="--SELECT--"
							name="task.claim.policyCode"
							value="%{claim.policyCode}"
							list="getPolicyCodes()" />
					</s:if> <s:else>
						<s:select id="policy_code" cssStyle="width:180px;"
							headerKey="" headerValue="--SELECT--"
							name="task.claim.policyCode"
							value="%{getClaimProcessedAsForDisplay(claim)}"
							list="getPolicyCodes()" />
					</s:else></td></tr></s:if></authz:ifUserInRole>
    	<tr><td colspan="4" class="borderTable"></td></tr>
		<tr style="height:4px; "><td ></td></tr>
    <tr>
	    <s:if test="claim.isServiceManagerRequest()
	    &&! 'SERVICE_MANAGER_REVIEW'.equalsIgnoreCase(claim.state)">
	      <s:if test="!(claim.state.state.equalsIgnoreCase('draft'))">
	        <td class="labelStyle" nowrap="nowrap"><s:text name="columnTitle.viewClaim.smrApproved"/>:</td>
	          <td>
	             <s:if test="claim.isServiceManagerAccepted()">
	                   <s:text name="label.common.yes"/>
	              </s:if>
	           	<s:else>
	            		<s:text name="label.common.no"/>
	           	</s:else>
	          </td>
	       </s:if>
	    </s:if>
    </tr>
    <tr>
      <authz:ifUserNotInRole roles="supplier">
      <s:if test="!(claim.state.state.equalsIgnoreCase('draft'))">
          <td class="labelStyle" nowrap="nowrap"><s:text name="label.viewClaim.claimProcessed"/>:</td>
          <td><s:property value="%{getClaimProcessedAsForDisplay(claim)}"/></td>
      </s:if>
      </authz:ifUserNotInRole>

      <s:if test="claim.partReturnStatus != null">
          <td class="labelStyle" nowrap="nowrap"><s:text name="label.integration.returnPartStatus"/>:</td>
          <td><s:property value="claim.partReturnStatus"/></td>
      </s:if>

      <s:if test="claim.foc">
           <td class="labelStyle" nowrap="nowrap"><s:text name="foc.claim.orderNo"/>:</td>
          <td><s:property value="claim.focOrderNo"/></td>
      </s:if>
    </tr>

	<!-- Bases on 31st May Discussion , the customer has confirmed to remove this for EMEA , we need to confirm if this might be used in phase 2 -->

	<!--
    <authz:ifUserNotInRole roles="supplier">
      <s:if test="!(claim.state.state.equalsIgnoreCase('draft')) && isClaimProcessedAsOutOfWarranty(claim)">
        <tr>
          <td class="labelStyle" nowrap="nowrap"><s:text name="columnTitle.common.warrantyEndDate"/>:</td>
          <td><s:property value="claim.coverageEndDate"/></td>
          <td class="labelStyle" nowrap="nowrap"><s:text name="label.common.hoursCovered"/>:</td>
          <td><s:property value="claim.hoursCovered"/></td>
        </tr>
      </s:if>
    </authz:ifUserNotInRole> !-->

	<tr>
      <s:if test="isSellingEntityToBeCaptured(claim)">
          <td class="labelStyle" nowrap="nowrap"><s:text name="label.claim.sellingEntity"/>:</td>
          <td><tda:lov name="task.claim.sellingEntity" id="sellingEntity"
                       className="SellingEntity" businessUnitName="claim.businessUnitInfo.name" /></td>

          <script type="text/javascript">
		      dojo.addOnLoad(function(){
		      	<s:if test = "claim.sellingEntity != null">
		      		dijit.byId("sellingEntity").setDisplayedValue("<s:property value = "claim.sellingEntity.description"/>");
		      	</s:if>
		      }
		      )
	      </script>
      </s:if>
      <s:elseif test="claim.sellingEntity!=null">
          <td class="labelStyle" nowrap="nowrap"><s:text name="label.claim.sellingEntity"/>:</td>
          <td><s:property value="claim.sellingEntity.description"/> </td>
      </s:elseif>
   </tr>
   <tr>
      <s:if test="isSourceWarehouseToBeCaptured(claim)">
          <td class="labelStyle" nowrap="nowrap"><s:text name="label.claim.sourceWarehouse"/>:</td>
          <td><s:select id="sourceWarehouse" list="findAllSourceWareHouse()" listKey="id" listValue="name"
	           value="%{claim.sourceWarehouse}" name="task.claim.sourceWarehouse"
    	       headerKey="null" headerValue="%{getText('label.common.selectHeader')}" /> </td>

            <script type="text/javascript">
      dojo.addOnLoad(function(){
      	<s:if test = "claim.sourceWarehouse != null">
      		dijit.byId("sourceWarehouse").setValue("<s:property value = "claim.sourceWarehouse.id"/>")
      	</s:if>
      }
      )
      </script>
      </s:if>
      <s:elseif test="claim.sourceWarehouse!=null">
          <td class="labelStyle" nowrap="nowrap"><s:text name="label.claim.sourceWarehouse"/>:</td>
          <td><s:property value="claim.sourceWarehouse.name"/> </td>
      </s:elseif>

      <s:if test="((claim.type.type == 'Machine' || claim.type.type == 'Attachment') && !claim.itemReference.isSerialized())
      		|| (claim.type.type == 'Parts' && claim.partInstalled && !claim.itemReference.isSerialized())">
	      <s:if test="isDateCodeEnabled()">
		        <td class="labelStyle" nowrap="nowrap">
		            <label for="dateCode" id="dateCodeLabel">
		                <s:text name="label.common.dateCode"/>:
		            </label>
		        </td>
		        <td class="labelStyle" nowrap="nowrap">
		            <s:property value='claim.dateCode' />
		        </td>
		        <td colspan="2">&nbsp;</td>
	      </s:if>
      </s:if>
   </tr>

 </table>

<s:if test="claim.forDealer.name!=null">
  <jsp:include page="../read/dealerInfoPage.jsp"/>
</s:if>
<s:if test="claim.claimedItems[0].itemReference.referredInventoryItem.ownedBy.name!=null && !isMatchReadApplicable() &&
			claim.claimedItems[0].itemReference.referredInventoryItem.type.type.equals('RETAIL')">
  <jsp:include page="../read/ownerInfoPage.jsp"/>
</s:if>
<s:if test="#displayNonSerializedOwnerInfo == 1">
	 <jsp:include page="../read/nonSerializedOwnerInfoPage.jsp"/>
</s:if>


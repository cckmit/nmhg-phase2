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
<%@taglib prefix="t" uri="twms"%><%@taglib prefix="u" uri="/ui-ext"%>
<%@taglib prefix="authz" uri="authz"%>
<%@ taglib prefix="tda" uri="twmsDomainAware" %>
<script type="text/javascript" src="scripts/thirdParty/thirdPartySearch.js"></script>

<table class="grid" cellspacing="0" cellpadding="0" id="claim_header_table">
	<tr>
	   <td width="17%" class="labelStyle" nowrap="nowrap"><s:text name="label.common.claimNumber"/>: </td>
	   <s:if test="claim.claimNumber==null && claim.state.state.equalsIgnoreCase('draft')">
	   <td width="36%"><s:text name="label.viewClaim.draft"/></td>
	  <%--  <td width="17%" class="labelStyle" nowrap="nowrap">Historical Claim Number : </td> 
	    <td width="36%"><s:if test="claim.histClmNo!=null">
       <s:property value="claim.histClmNo"/>
       </s:if></td> --%>
	   </s:if>
	   <s:else>
	   <td width="36%">  
	    <s:if test="isLoggedInUserAnInternalUser()">
	   <authz:ifUserNotInRole roles="supplier">
	                       <u:openTab id="claims_Details[%{claim.id}]" cssClass="claims_folder folder"
                           tabLabel="Claim Number %{claimNumber}"
                           url="view_search_detail.action?id=%{claim.id}"
                           catagory="myClaims">
                    <s:property value="claim.claimNumber"/>
                     </u:openTab>
                     </authz:ifUserNotInRole>
          </s:if>
          <s:else>
                    <s:property value="claim.claimNumber"/>
		  </s:else>      
         	
       <%-- <s:if test="claim.histClmNo!=null">
       (<s:property value="claim.histClmNo"/>)
       </s:if> --%>
       </td>
       </s:else>
       
       <td width="20%" class="labelStyle" nowrap="nowrap"><s:text name="label.common.claimType"/>:</td>
       <td width="27%"><s:property value="getText(claim.type.displayType)"/></td>
       
   </tr>
   <s:if test="!showClaimAudit">
      <s:if test="isLoggedInUserAnInternalUser()">  
	     <tr>
	     <s:if test="claim.histClmNo!=null">
	     	 <td width="17%" class="labelStyle" nowrap="nowrap"><s:text name="label.common.historicalClaimNumber"/> : </td> 
			    <td width="36%">
		       <s:property value="claim.histClmNo"/>
		       </td>
		       </s:if>
	        <td class="labelStyle" nowrap="nowrap"><s:text name="columnTitle.viewClaim.claimStatus"/>:</td>
            <td><s:property value="claim.state.state"/></td>
	        <%-- <s:if test="claim.recoveryClaims.size() > 0">
		       <td class="labelStyle"><s:text name="label.recoveryClaim.recoveryClaimState"/>:</td>
		          <td><s:property value="claim.getLatestRecoveryClaim().recoveryClaimState.state"/></td>
		    </s:if>     --%>  
	     </tr>
  	</s:if>
  
  
    <tr>
        <s:if test="!isLoggedInUserAnInternalUser()">  
	         <authz:ifUserInRole roles="dealer">
	         <td width="17%" class="labelStyle" nowrap="nowrap"><s:text name="label.common.historicalClaimNumber"/> : </td> 
			    <td width="36%"><s:if test="claim.histClmNo!=null">
		       <s:property value="claim.histClmNo"/>
		       </s:if></td>
	             <td class="labelStyle" style="padding-bottom:5px;" nowrap="nowrap"><s:text name="columnTitle.viewClaim.claimStatus"/>:</td>
	             
	                <s:if test="isLoggedInUserAnInternalUser() || isClaimStatusShownToDealer()">
	                   <td><s:property value="claim.state.state"/></td>
	                </s:if>
	               	                
	                <s:else>
	                	<td><s:property value="claim.state.displayStatus" /></td>
		           	</s:else>
	         </authz:ifUserInRole>
        </s:if>   
    </tr>
   </s:if>
 <s:if test="dealerEligibleToFillSmrClaim || claim.serviceManagerRequest">
   <tr>
   <td  class="labelStyle"  style="padding-bottom:5px;" nowrap="nowrap"><s:text name="label.viewClaim.requestSMR"/>:</td>
       <s:if test="claim.serviceManagerRequest">
           <td><s:text name="label.common.yes"/></td>
       </s:if>
       <s:else>
			<td colspan="1"><s:text name="label.common.no"/></td>		
       </s:else>
       <s:if test="claim.serviceManagerRequest">
       <td  class="labelStyle" style="padding-bottom:15px;" nowrap="nowrap"><s:text name="label.viewClaim.smrReason"/>:</td>
       <td style="padding-bottom:15px;">
             <s:property value="claim.reasonForServiceManagerRequest.description"/>
       </td>
       </s:if>
     </tr>
</s:if> 
      <s:if test="!claim.state.state.equalsIgnoreCase('draft') && (isLoggedInUserAnInternalUser() || (isClaimAssigneeShownToDealer() && !isLoggedInUserAnInternalUser()))">	
      			<tr>
			   <s:if test="getCurrentClaimAssignee().length() > 0">
			   <td class="labelStyle" nowrap="nowrap"><s:text name="label.claim.assignedTo"/>:</td>
	            <td><s:property value="currentClaimAssignee"/></td>
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
        <!-- </a> -->
        </td>
   </s:if>
   </tr>
    
   <tr>
     <s:if test="%{showAuthorizationReceived()}">
      <td  class="labelStyle"  style="padding-bottom:5px;" nowrap="nowrap"><s:text name="label.viewClaim.cmsAuth"/>:</td>
       <s:if test="claim.cmsAuthCheck">
           <td><s:text name="label.common.yes"/></td>
       </s:if>
          <s:else>
	           <td><s:text name="label.common.no"/></td>
	       </s:else>
	  </s:if>     
	   <s:if test="isLoggedInUserAnInternalUser() && claim.recoveryClaims.size() > 0 && claim.getLatestRecoveryClaim()!=null">
	   		<td class="labelStyle"><s:text name="label.recoveryClaim.assignedTo" />:</td>
	   		<td><s:property value="claim.getLatestRecoveryClaim().currentAssignee"/></td>
	   </s:if>
	     <s:if test="isLoggedInUserAnAdmin() && enableWarrantyOrderClaims()">
             <td  class="labelStyle"  style="padding-bottom:5px;" nowrap="nowrap"><s:text name="label.viewClaim.warrantyOrderClaim"/>:</td>
              <s:if test="claim.warrantyOrder">
                  <td><s:text name="label.common.yes"/></td>
              </s:if>
                 <s:else>
       	           <td><s:text name="label.common.no"/></td>
       	       </s:else>
       	  </s:if>
     </tr>  
     <tr>
     <s:if test="isLoggedInUserAnInternalUser() || context.equals('ClaimSearches')" >
     		<s:if test="isLoggedInUserAnInternalUser()">
           <td  class="labelStyle"  style="padding-bottom:5px;" nowrap="nowrap"><s:text name="label.claim.ncr"/>:</td>
	       <s:if test="claim.ncr">
	           <td><s:text name="label.common.yes"/></td>
	       </s:if>
	       <s:else>
		           <td><s:text name="label.common.no"/></td>
		   </s:else>
		   </s:if>
		   	   </s:if>
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
            	
            
	<authz:ifUserNotInRole roles="receiverLimitedView,inspectorLimitedView,partShipperLimitedView">
   	<tr><td colspan="4"><hr/></td></tr>
	   <tr>       		
       <td class="labelStyle" nowrap="nowrap"><s:text name="label.common.dealer"/>: </td>
       
       <s:set name="hideOwnerForSupplier" value="0"/>
       <s:if test="!isLoggedInUserAnInternalUser()">
       		<authz:ifUserInRole roles="supplier">
       			<s:set name="hideOwnerForSupplier" value="1"/>
       		</authz:ifUserInRole>
       </s:if>
       
       	<authz:ifUserInRole roles="processor">
	       	<s:if test="isThirdPartyClaim()">
		   	<td id = "selectThirdParty" style="word-wrap: break-word " >
		   			<s:hidden name="thirdPartyDealerName" id="dealer"/>
		   			<s:hidden id="thirdPartyForDealer" value="%{claim.forDealerShip.name}" />   			
		        	<div id="selectedThirdPartyDisplayName"/>
			        		<s:property value="claim.forDealerShip.name"/>
			    	</div>
			    	<s:if test="claim.eligibleForEdit">
				      	<a id="thirdPartyAddLinkId" class="link" >
				        	<s:text name="label.viewClaim.thirdPartyAddLink"/>
						</a>				        
				    </s:if>
						<script type="text/javascript">
			             dojo.connect(dojo.byId("thirdPartyAddLinkId"), "onclick", function() {
			            		dojo.publish("/thirdPartySearch/show");
							});
					    </script>    
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
       	<s:hidden id="thirdPartyForDealer" value="%{claim.forDealerShip.name}" /> 
       		<td id = "viewLink" >
       		<s:if test="#hideOwnerForSupplier == 1">
       			<s:property value="claim.forDealerShip.name"/>
       		</s:if>
       		<s:else>
       			<authz:ifUserNotInRole roles="receiverLimitedView,inspectorLimitedView,partShipperLimitedView">
	       			<a id="show_dealer_info" class="link">				
						<s:property value="claim.forDealerShip.name"/>  		
					</a>
				</authz:ifUserNotInRole>
				<authz:else><s:property value="claim.forDealerShip.name"/></authz:else>
       		</s:else>
			</td>
		</authz:ifUserNotInRole>

		<s:if test="#hideOwnerForSupplier == 1">
			<td class="labelStyle" colspan="2"></td>
		</s:if>
		<s:else>
		<s:if test="!isMatchReadApplicable()">
			<s:if test="claim.claimedItems.size() > 0 && claim.claimedItems[0].itemReference.referredInventoryItem != null && 
			claim.claimedItems[0].itemReference.referredInventoryItem.type.type.equals('RETAIL')">
			   <td  class="labelStyle" nowrap="nowrap"><s:text name="label.warrantyAdmin.ownerName"/>: </td>
	           <td id = "viewLink" >
	           	  <authz:ifUserNotInRole roles="receiverLimitedView,inspectorLimitedView,partShipperLimitedView">
					  <a id="show_owner_info" class="link">				
						<s:property value="claim.claimedItems[0].itemReference.referredInventoryItem.ownedBy.name"/>				
					  </a>
				  </authz:ifUserNotInRole>
				  <authz:else>
					<s:property value="claim.claimedItems[0].itemReference.referredInventoryItem.ownedBy.name"/>
				  </authz:else>
			   </td>
		 	</s:if>
		 	<s:elseif test="claim.type.type == 'Parts' && claim.partItemReference.referredInventoryItem != null">
		 	 <td  class="labelStyle" nowrap="nowrap"><s:text name="label.warrantyAdmin.ownerName"/>: </td>
	           <td id = "viewLink" >
	           	  <authz:ifUserNotInRole roles="receiverLimitedView,inspectorLimitedView,partShipperLimitedView">
					  <a id="show_owner_info_for_part" class="link">				
						<s:property value="claim.partItemReference.referredInventoryItem.ownedBy.name"/>				
					  </a>
				  </authz:ifUserNotInRole>
				  <authz:else>
					<s:property value="claim.partItemReference.referredInventoryItem.ownedBy.name"/>
				  </authz:else>
			   </td>
		 	</s:elseif>
		</s:if>                                                    	
		<s:if test="((claim.type.type == 'Machine' || claim.type.type == 'Attachment') && !claim.itemReference.isSerialized())
      		|| (claim.type.type == 'Parts'  && claim.partInstalled && !claim.itemReference.isSerialized() && !claim.partItemReference.isSerialized())">
	 		<s:if test=" (claim.claimNumber==null && claim.state.state.equalsIgnoreCase('draft') )
	 			|| claim.state.state.equalsIgnoreCase('forwarded') || baseFormName == 'processor_review'">
					<jsp:include flush="true" page="../common/write/nonSerializedOwnerInfoInclude.jsp"/>
			</s:if>	
			<s:else>
				<s:set name="displayNonSerializedOwnerInfo" value="1"/>
				<td  class="labelStyle" nowrap="nowrap"><s:text name="label.warrantyAdmin.ownerName"/>: </td>
				<td nowrap="nowrap">
					<authz:ifUserNotInRole roles="receiverLimitedView,inspectorLimitedView,partShipperLimitedView">
					  <a id="show_nonserialized_owner_info" class="link">				
						<s:property value="claim.ownerInformation.belongsTo.name"/>				
					  </a>
					</authz:ifUserNotInRole>
					<authz:else>
						<s:property value="claim.ownerInformation.belongsTo.name"/>
				   </authz:else>
			   </td>					
			</s:else>
		</s:if>
		</s:else>
    </tr>

   <tr>
       <td  class="labelStyle" nowrap="nowrap"><s:text name="label.common.dateOfClaim"/>:</td>
       <td>
			<s:property value="claim.filedOnDate"/>
	   </td>
     <s:if test="%{showDealerJobNumber()}">
       <td class="labelStyle" nowrap="nowrap"><s:text name="label.claim.workOrderNumber"/>:</td>
	   <td>
	   	  <s:if test="claim.foc && claim.state.equals('draft')">
			<s:textfield name="task.claim.workOrderNumber" value ="%{claim.workOrderNumber}"/>
	   	  </s:if>
	   	  <s:else>
	   	  	<s:property value="claim.workOrderNumber"/>
	   	  </s:else>
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
	    <s:else>
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
	    </s:else>
	    
	    
    </s:if> 
       <s:if test="claim.invoiceNumber!= null">
				        <td class="labelStyle" nowrap="nowrap"><s:text name="label.claim.invoiceNumber"/>: 
				       <s:property value="claim.invoiceNumber"/></td>   </s:if>  
    <%-- <s:if test="claim.type.type == 'Parts'">
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
    
    <td> </td>
   
   <s:if test="claim.type.type != 'Campaign'">   
     
     <tr>
      
         <td  class="labelStyle" nowrap="nowrap"><s:text name="label.viewClaim.dateOfFailure"/>:</td>
         <td>                            
			<s:property value='claim.failureDate' />
         </td>
         <s:if test="claim.creditDate != null">
         <td  class="labelStyle" nowrap="nowrap"><s:text name="label.common.creditDate"/>:</td>
         <td>
			<s:property value='claim.creditDate' />
         </td>
         </s:if>
      </tr>
      <tr>
      <td  class="labelStyle" nowrap="nowrap" ><s:text name="label.viewClaim.repairStartDate"/>:</td>
          <td>                         
			<s:property value="claim.repairStartDate"/>  
          </td>       
          <td  class="labelStyle" nowrap="nowrap" ><s:text name="label.viewClaim.dateOfRepair"/>:</td>
          <td>                         
			<s:property value="claim.repairDate"/>  
			<s:hidden id="claimRepairDate" value="%{claim.repairDate}" /> 
          </td>
        </tr>

 		<s:if test="claim.purchaseDate != null">
             <tr>
		        <td class="labelStyle" nowrap="nowrap"><s:text name="label.common.dateOfPurchase"/>:</td>
		        <td>
	        		<s:property value='claim.purchaseDate' />
		       </td>
	        </tr>
	    </s:if>

		    <tr>
			<s:if test="claim.installationDate != null">
				<td class="labelStyle" nowrap="nowrap" ><s:text name="label.common.installationDate"/>:</td>
				<td>
					<s:property value='claim.installationDate' />
				</td>			        
			</s:if>
			
			<s:if test="!claim.getCompetitorModelDescription().isEmpty()">   
				<td class="labelStyle" nowrap="nowrap" ><s:text name="label.newClaim.productModel"/>:</td>
				<td>
					<s:property value='claim.competitorModelDescription' />
				</td>	
					 
				 <tr>
				<td class="labelStyle" nowrap="nowrap" ><s:text name="label.newClaim.competitorModelBrand"/>:</td>
				<td>
					 <s:property value='claim.competitorModelBrand' /> 
				 
				</td>	
				<td class="labelStyle" nowrap="nowrap" ><s:text name="label.newClaim.competitorModelTruckSerialNumber"/>:</td>
				<td>
					<s:property value='claim.competitorModelTruckSerialnumber' />
				 
				</td>	
				 </tr>		        
			</s:if>
			<s:if test="claim.itemReference.referredInventoryItem.serialNumber != null">
				<td class="labelStyle" nowrap="nowrap" ><s:text name="label.newClaim.equipmentSerialNo"/>:</td>
				<td>
					<s:property value="claim.itemReference.referredInventoryItem.serialNumber " />
				</td>			        
			</s:if>
		    </tr>
		    <tr>
		    <s:if test="claim.hoursOnPart != null">
				        <td class="labelStyle" nowrap="nowrap"><s:text name="label.common.hoursOnPart"/>:</td>
				        <td><s:property value="claim.hoursOnPart"/></td>
		    </s:if>
		    <s:if test="claim.hoursOnTruck != null">
				        <td class="labelStyle" nowrap="nowrap"><s:text name="label.common.hoursOnTruckDuringIstallation"/>:</td>
				        <td><s:property value="claim.hoursOnTruck"/></td>
		    </s:if>
		    </tr>
   			</s:if>
   			<s:if test="claim.type.type == 'Parts'">
	  			<tr>
	  				<td class="labelStyle" nowrap="nowrap"><s:text name="label.common.hoursOnTruck"/>:</td>
	  				<td><s:property value="claim.getClaimedItems().get(0).getHoursInService()" /></td>
	  			</tr>
	  		</s:if> 
   		<s:else>
      
                       <s:if test="claim.creditDate != null">
         <td  class="labelStyle" nowrap="nowrap"><s:text name="label.common.creditDate"/>:</td>
         <td>
			<s:property value='claim.creditDate' />
         </td>
         </s:if> 
          </tr>
        </tr>
   		</s:else>
   		   	<authz:ifUserInRole roles="processor">
   	<s:if test="claim.policyCode!=null && !claim.policyCode.isEmpty() && #policyCodeEditable != true">
          <tr>
          <td width="22%">
            <label for="policyCode"  class="labelStyle"><s:text name="label.viewClaim.policyCode"/>:</label>
        </td>
<td  >
<s:property value="claim.policyCode"/>
 
  </td></tr></s:if></authz:ifUserInRole>
         <s:if test="%{displayCPFlagOnClaimPgOne}">     
         <tr>
          <td class="labelStyle"  style="padding-bottom:5px;" nowrap="nowrap"><s:text name="label.newClaim.commercialPolicy"/>:</td>
	          <s:if test ="claim.commercialPolicy">
	                    <td><s:text name="label.common.yes"/></td>   
	                  </s:if>
	                  <s:else>
	                    <td><s:text name="label.common.no"/></td>
	                  </s:else>
	                  
	          <s:if test="claim.invoiceNumber != null">
				<td class="labelStyle" nowrap="nowrap"><s:text name="label.claim.invoiceNumber"/>:</td>
				<td><s:property value="claim.invoiceNumber"/></td>			        
			</s:if>             
         </tr>
	     </s:if>  
    	   	<tr><td colspan="4"><hr/></td></tr>
    	   	
    	   	<authz:ifUserInRole roles="processor">
   	<s:if test="policyCodes.size()>0 && #policyCodeEditable == true">
          <tr>
          <td width="22%">
            <label for="policyCode"  class="labelStyle"><s:text name="label.viewClaim.policyCode"/>:</label>
        </td>
<td><s:if test="claim.policyCode!=null && claim.policyCode!=''">
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
          <td class="labelStyle" nowrap="nowrap" ><s:text name="label.viewClaim.claimProcessed"/>:</td>
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
    </authz:ifUserNotInRole>
 -->
    <tr>
      <s:if test="isSellingEntityToBeCaptured(claim)">
          <td class="labelStyle" nowrap="nowrap"><s:text name="label.claim.sellingEntity"/>:</td>
          <td><tda:lov name="task.claim.sellingEntity" id="sellingEntity"
                       className="SellingEntity" businessUnitName="claim.businessUnitInfo.name"/></td>
                       
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
    	       headerKey="" headerValue="%{getText('label.common.selectHeader')}" /> </td>
    	  <script type="text/javascript">
		      dojo.addOnLoad(function(){
		      	<s:if test = "claim.sourceWarehouse != null">
		      		dijit.byId("sourceWarehouse").setValue("<s:property value = "claim.sourceWarehouse.id"/>");
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
  </authz:ifUserNotInRole>
 </table>

<s:if test="claim.forDealer.name!=null">
  <jsp:include page="../common/read/dealerInfoPage.jsp"/>
</s:if>
<s:if test="claim.claimedItems[0].itemReference.referredInventoryItem.ownedBy.name!=null && !isMatchReadApplicable() && claim.claimedItems[0].itemReference.referredInventoryItem.type.type.equals('RETAIL')">
<jsp:include page="../common/read/ownerInfoPage.jsp"/>
</s:if>
<s:elseif test="claim.partItemReference.referredInventoryItem.ownedBy.name!=null && !isMatchReadApplicable()">
<jsp:include page="../common/read/ownerInfoPageForPart.jsp"/>
</s:elseif>
<s:if test="#displayNonSerializedOwnerInfo == 1"> 
	 <jsp:include page="../common/read/nonSerializedOwnerInfoPage.jsp"/> 
</s:if>


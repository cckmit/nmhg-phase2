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
<script type="text/javascript">
    dojo.require("dijit.Tooltip")
</script>
<style>
.nonclickHighlite{
color:#545454;
font-weight:700;
cursor:pointer;
text-decoration:none;
text-transform:uppercase;
}
.clickable{
text-transform:uppercase;
color:blue;
font-weight:700;
cursor:pointer;
text-decoration:underline;
}
</style>
<script type="text/javascript">
dojo.addOnLoad(function(){
	dojo.html.hide(dojo.byId("flat_processor_headerLabel"));
	dojo.html.hide(dojo.byId("flat_cp_headerLabel"));
	<s:if test="claim.payment.isFlatAmountApplied()">
		dojo.html.hide(dojo.byId("percentage_processor_headerLabel"));
		dojo.html.hide(dojo.byId("percentage_cp_headerLabel"));
		dojo.html.show(dojo.byId("flat_processor_headerLabel"));
		dojo.html.show(dojo.byId("flat_cp_headerLabel"));
	</s:if>
});
function paymentView(idval){

	document.getElementById('paymentLowLevel_processor_read').className='clickable';
	document.getElementById('paymentDetailed_processor_read').className='clickable';
	document.getElementById(idval).className='nonclickHighlite';

	if(idval=='paymentDetailed_processor_read'){
		document.getElementById('detailedlevelpopup_processor_read').style.display='';
		document.getElementById('lowLeveltablepopup_processor_read').style.display='none';
	}

	if(idval=='paymentLowLevel_processor_read'){
		document.getElementById('detailedlevelpopup_processor_read').style.display='none';
		document.getElementById('lowLeveltablepopup_processor_read').style.display='';
	}
}
</script>
<s:if test="!claim.payment.lineItemGroups.isEmpty()">
    <s:set name="claimAmountDisplayAudit" value="%{getLineItemGroupAuditForGlobalLevel(claim)}"/>
    <s:set name="isLineItemChanged" value="%{isLineItemPercentageChanged(claim)}"/>
   <div class="labelStyle" style="margin:10px 0px 10px 5px;">
	<%-- <s:text name="message.claim.payment.selectLevel"/> --%>
	<span  style="display:none; padding-left:20px;"><a class="nonclickHighlite" onclick="paymentView(id);" id="paymentDetailed_processor_read"><s:text name="message.claim.payment.detailed"/></a></span>
	<span  style="display:none; padding-left:20px;"><a class="clickable" onclick="paymentView(id);" id="paymentLowLevel_processor_read"><s:text name="message.claim.payment.lowlevel"/></a></span>
	</div>
	<div id="detailedlevelpopup_processor_read">
	<table id="percentage_payment" style="width: 98%;">
        <thead>
            <tr>
                <th style="text-align:left;" class="labelStyle" width="15%"><s:text name="label.common.category"/></th>
                 <th style="text-align:right;" width="15%" class="labelStyle">&nbsp;&nbsp;&nbsp;&nbsp;<s:text name="label.common.payment.reqQty"/></th> 
                <th style="text-align:right;" class="labelStyle" width="10%">&nbsp;&nbsp;&nbsp;&nbsp;<s:text name="label.claim.amountAsked" /></th>
                <th style="text-align:right;" class="labelStyle" width="15%">&nbsp;&nbsp;&nbsp;&nbsp;<s:text name="label.common.currentAmount" /></th>
                <th style="text-align:right;" class="labelStyle" width="15%">&nbsp;&nbsp;&nbsp;&nbsp;<s:text name="label.common.payment.acceptedQty"/></th>
                <th style="text-align:right;" class="labelStyle" width="15%" id="percentage_processor_headerLabel"><s:text name="label.newClaim.percentageAccepted.forWarranty"/></th>
                <th style="text-align:center;" width="15%" class="labelStyle" id="flat_processor_headerLabel"><s:text name="label.newClaim.flatAmount.forWarranty"/></th>
                <th style="text-align:right;" class="labelStyle" width="15%"><s:text name="label.newClaim.acceptedAmount.for.warranty"/></th>
                <s:if test="claim.payment.stateMandateActive && !claim.isGoodWillPolicy()">
                 	<th style="text-align:center;" width="15%" class="labelStyle" id="stateMandateAmount"><s:text name="label.common.payment.stateMandateAmount"/></th>
                </s:if>	
               
            </tr>
        </thead>
        <tbody>
        	<s:set var="counter" value="0"/> 
        	<s:set var="othersCounter" value="0"/>
            <s:iterator value="claim.payment.lineItemGroups" status="status">
                <s:set name="setDealerPayment" value="%{claim.getPaymentForDealerAudit()}"/> 
				<s:set name="setDealerAudit" value="#setDealerPayment.getLineItemGroup(name)"/>
				 <s:set name="lineItem" value="%{claim.payment.getLineItemGroup(name)}"/>
				  <s:set name="travelByHrs" value="%{claim.payment.getLineItemGroup('Travel by Hours')}"/>
                   <s:set name="travelAddHrs" value="%{claim.payment.getLineItemGroup('Additional Travel Hours')}"/>                       
                	<s:set name="TravelByHrsAudit" value="#setDealerPayment.getLineItemGroup('Travel by Hours')"/>
                	<s:set name="TravelByAddHrsAudit" value="#setDealerPayment.getLineItemGroup('Additional Travel Hours')"/>
                <s:if test="name != 'Claim Amount'">
                	<s:if test="name=='Labor'">
                        <s:if test="isLaborSplitEnabled()">
                        	<s:if test="forLaborSplitAudit.size>=#setDealerAudit.forLaborSplitAudit.size && !forLaborSplitAudit.empty">
                        		<s:iterator value="forLaborSplitAudit" status="latestAuditStatus">
                        		<s:set name="doesExists" value="false"/>
                        			<tr>
                        				<td nowrap="nowrap" style="padding-left: 30px;   padding-bottom: 10px;"><s:property value="name"/></td> 
                        				<s:iterator value="#setDealerAudit.forLaborSplitAudit" status="dealerAuditStatus">
                        					<s:if test="forLaborSplitAudit[#latestAuditStatus.index].name.equals(name)">
                        					<s:set name="doesExists" value="true"/>										
												<td nowrap="nowrap" style="padding-left: 10px;   padding-bottom: 10px;text-align:right;" class="numeric">
													<span id="dealerAudit1DoesExist[<s:property value="#dealerAuditStatus.index"/>]">
														<s:property value="paymentDetailForSplitAudit"/>
													</span>
				  	                      			<span dojoType="dijit.Tooltip" connectId="dealerAudit1DoesExist[<s:property value="#dealerAuditStatus.index"/>]">

                                                             @<s:property value="laborRateForSplitAudit"/>
													</span>													
												</td>
                        						<td nowrap="nowrap" style="padding-left: 10px;   padding-bottom: 10px;text-align:right;" class="numeric">
													<span id="latestAudit1DoesExist[<s:property value="#dealerAuditStatus.index"/>]">
														<s:property value="forLaborSplitAudit[#latestAuditStatus.index].paymentDetailForSplitAudit"/> 
													</span>
				  	                      			<span dojoType="dijit.Tooltip" connectId="latestAudit1DoesExist[<s:property value="#dealerAuditStatus.index"/>]">

                                                            @<s:property value="forLaborSplitAudit[#latestAuditStatus.index].laborRateForSplitAudit"/>
													</span>														                        						
                        						</td>                        				
                        					</s:if>
                        				</s:iterator>
                        				<s:if test="!#doesExists">
                        					<td nowrap="nowrap" style="padding-left: 10px;   padding-bottom: 10px;text-align:right;" class="numeric"></td>
                        					<td nowrap="nowrap" style="padding-left: 10px;   padding-bottom: 10px;text-align:right;" class="numeric">
              		        					<span id="dealerAudit1DoesntExist[<s:property value="#latestAuditStatus.index"/>]">
													<s:property value="paymentDetailForSplitAudit"/>
												</span>
			  	                      			<span dojoType="dijit.Tooltip" connectId="dealerAudit1DoesntExist[<s:property value="#latestAuditStatus.index"/>]">
													@<s:property value="laborRateForSplitAudit"/>
												</span>													              		          					
                        					</td>
                        				</s:if>
                        			</tr>                     			
                        			
                        		</s:iterator>
                        	</s:if>
                        	<s:if test="forLaborSplitAudit.size<#setDealerAudit.forLaborSplitAudit.size && !#setDealerAudit.forLaborSplitAudit.empty">
                        		<s:iterator value="#setDealerAudit.forLaborSplitAudit" status="dealerAuditStatus">
                        		<s:set name="doesExists" value="false"/>
                        			<tr>
                        				<td nowrap="nowrap" style="padding-left: 30px;   padding-bottom: 10px;"><s:property value="name"/></td> 
                        				<s:iterator value="forLaborSplitAudit" status="latestAuditStatus">
                        					<s:if test="#setDealerAudit.forLaborSplitAudit[#dealerAuditStatus.index].name.equals(name)">
                        					<s:set name="doesExists" value="true"/>											
												<td nowrap="nowrap" style="padding-left: 10px;   padding-bottom: 10px;text-align:right;" class="numeric">
													<span id="dealerAudit1DoesExist[<s:property value="#latestAuditStatus.index"/>]">
														<s:property value="#setDealerAudit.forLaborSplitAudit[#dealerAuditStatus.index].paymentDetailForSplitAudit"/>
													</span>
				  	                      			<span dojoType="dijit.Tooltip" connectId="dealerAudit1DoesExist[<s:property value="#latestAuditStatus.index"/>]">

                                                             @<s:property value="forLaborSplitAudit[#latestAuditStatus.index].laborRateForSplitAudit"/> 
													</span>																										
												</td>
                        						<td nowrap="nowrap" style="padding-left: 10px;   padding-bottom: 10px;text-align:right;" class="numeric">
													<span id="latestAudit1DoesExist[<s:property value="#latestAuditStatus.index"/>]">
														<s:property value="paymentDetailForSplitAudit"/>
													</span>
				  	                      			<span dojoType="dijit.Tooltip" connectId="latestAudit1DoesExist[<s:property value="#latestAuditStatus.index"/>]">

                                                            @<s:property value="laborRateForSplitAudit"/> 
													</span>													
                        						</td>                        				
                        					</s:if>
                        				</s:iterator>
                        				<s:if test="!#doesExists">
                        					<td nowrap="nowrap" style="padding-left: 10px;   padding-bottom: 10px;text-align:right;" class="numeric">
              		        					<span id="dealerAudit1DoesntExist[<s:property value="#dealerAuditStatus.index"/>]">
													<s:property value="paymentDetailForSplitAudit"/>
												</span>
			  	                      			<span dojoType="dijit.Tooltip" connectId="dealerAudit1DoesntExist[<s:property value="#dealerAuditStatus.index"/>]">
													@<s:property value="laborRateForSplitAudit"/>
												</span>													              		          					
                        					</td>                        					
                        					<td nowrap="nowrap" style="padding-left: 10px;   padding-bottom: 10px;text-align:right;" class="numeric"></td>
                        				</s:if>
                        			</tr>
                        		</s:iterator>
                        	</s:if>
                        </s:if>
                        <s:else>
                        	<tr>
                            	<td nowrap="nowrap" style="padding-left: 10px;   padding-bottom: 10px;"><s:text name="%{getMessageKey(name)}"/></td>
                        		<%-- <td nowrap="nowrap" style="padding-left: 10px;   padding-bottom: 10px;text-align:right;" class="numeric">
  	                      			<span id="dealerAudit1[<s:property value="#status.index"/>]">
  	                      				<s:property value="#setDealerAudit.baseAmount"/>
  	                      			</span>
  	                      			<s:if test="#setDealerAudit.rate != null">
	  	                      			<span dojoType="dijit.Tooltip" connectId="dealerAudit1[<s:property value="#status.index"/>]">
											@<s:property value="#setDealerAudit.rate"/>
										</span>
										<br>{@<s:property value="#setDealerAudit.rate"/>}
  	                      			</s:if>				
                        		</td>
                        		<td nowrap="nowrap" style="padding-left: 10px;   padding-bottom: 10px;text-align:right;" class="numeric">
  	                      			<span id="latestAudit1[<s:property value="#status.index"/>]">
  	                      				<s:property value="baseAmount"/>
  	                      			</span>
  	                      			<s:if test="rate != null">
	  	                      			<span dojoType="dijit.Tooltip" connectId="latestAudit1[<s:property value="#status.index"/>]">
											@<s:property value="rate"/>
										</span>
										<br>{@<s:property value="rate"/>}
  	                      			</s:if>	                        		
                        		</td> --%>
                       		</tr>
                       		
                       			  <!--  NMHGSLMS425 Labor Changes -->
                     <s:iterator value="individualLineItems" status="indItemStatus">                                       
                 	 <tr>               
                  		<td nowrap="nowrap" style="padding-left: 30px;   padding-bottom: 10px;text-align:left;" class="numeric">
                  		 <s:if test="claim.competitorModelBrand!=null" >           
                  		 <s:text name="message.competitor.model.claim.labor.jobcode"/> <%-- ( <s:text name="message.competitor.model.claim.labor.description"/>) --%>
                  		 </s:if>
                  		 <s:else>                  		                 		
                  		 <s:property value="serviceProcedureDefinition.code"/> ( <s:property value="serviceProcedureDefinition.description"/> )  
                  		 </s:else>     </td>
                  		 <td nowrap="nowrap" style="padding-left: 10px;   padding-bottom: 10px;text-align:right;" class="numeric"><s:property value="#setDealerAudit.individualLineItems[#indItemStatus.index].askedHrs"/>	</td>
              			<td nowrap="nowrap" style="padding-left: 10px;   padding-bottom: 10px;text-align:right;" class="numeric"><s:property value="#setDealerAudit.individualLineItems[#indItemStatus.index].baseAmount"/></td>
						<td nowrap="nowrap" style="padding-left: 10px;   padding-bottom: 10px;text-align:right;" class="numeric"><s:property value="baseAmount"/> </td>
 						<td nowrap="nowrap" style="padding-left: 2px;   padding-bottom: 10px;text-align:right;" class="numeric"><s:property value="acceptedHrs"/> </td>
 						<s:if test="!claim.payment.isFlatAmountApplied()"> 	 	  
                        <td nowrap="nowrap" style="padding-left: 10px;   padding-bottom: 10px;text-align:right;" class="numeric"><s:property value="percentageAcceptance"/>%</td>
                        </s:if>
                        <s:else>
					    <td nowrap="nowrap" style="padding-left: 10px;   padding-bottom: 10px;text-align:right;" class="numeric"><s:property value="acceptedAmount"/></td>
					    </s:else>
					    <td nowrap="nowrap" style="padding-left: 10px;   padding-bottom: 10px;text-align:right;" class="numeric"><s:property value="acceptedAmount"/></td>
					    <s:if test="claim.payment.stateMandateActive && !claim.isGoodWillPolicy()">
							<td nowrap="nowrap"	style="padding-left: 10px; padding-bottom: 10px; text-align: right;" class="numeric"><s:property value="stateMandateAmount" />	</td>
						</s:if>	
                    </tr>                                         
                 </s:iterator>  
                 <!-- Labor Rate Changes -->
                  <tr>
               			 <td nowrap="nowrap"	style="padding-left: 60px; padding-bottom: 10px; text-align: left;" class="numeric"><s:text name="label.common.rate"/>:</td>		
                		<td nowrap="nowrap" style="padding-left: 10px;   padding-bottom: 10px; text-align: right;"> </td>		
                		<td nowrap="nowrap"	style="padding-left: 10px; padding-bottom: 10px; text-align: right;" class="numeric"><s:if test="buConfigAMER"> <s:property value="#setDealerAudit.percentageApplicable"/>%=</s:if> <s:else>@</s:else> <s:property value="#setDealerAudit.rate"/>	</td>
						<td nowrap="nowrap"	style="padding-left: 10px; padding-bottom: 10px; text-align: right;" class="numeric"><s:if test="buConfigAMER"> <s:property value="percentageApplicable"/>%=</s:if> <s:else>@</s:else><s:property value="rate"/></td>
						 <td nowrap="nowrap" style="padding-left: 10px;   padding-bottom: 10px; text-align: right;"> </td>
						  <td nowrap="nowrap" style="padding-left: 10px;   padding-bottom: 10px; text-align: right;"></td>
						   <td nowrap="nowrap" style="padding-left: 10px;   padding-bottom: 10px; text-align: right;"><s:if test="buConfigAMER"> <s:property value="percentageApplicable"/>%=</s:if> <s:else>@</s:else> <s:property value="rate"/></td>
						     <s:if test="claim.payment.stateMandateActive && !claim.isGoodWillPolicy()">
						   <td nowrap="nowrap" style="padding-left: 10px;   padding-bottom: 10px; text-align: right;"><s:property value="stateMandateRatePercentage"/>% = <s:property value="stateMandateRate"/></td>
						   </s:if>
				 </tr>
                 
                   <!--  NMHGSLMS425 Changes -->  
                       	</s:else>	
                    </s:if>
                    <s:else>
                     <!--   Changes for NMHGSLMS-425 (Travel related changes)-->      
                     <s:if test="name=='Travel by Hours' || name=='Travel By Trip' ||name=='Additional Travel Hours'||name=='Travel'">                                        
                      	<s:if test="#counter==0">
                        	<tr>
                  				<td nowrap="nowrap" style="padding-left: 10px;   padding-bottom: 10px;"><s:text name="label.common.travel"/></td>
                  			</tr>
                  		</s:if>
                  		  <s:set var="counter" value="%{#counter+1}"/>
                  	</s:if>
                  	  <s:elseif test="name=='Item Freight And Duty' || name=='Handling Fee' ||name=='Transportation'||name=='Late Fee'||name=='Deductible'||name=='Others'">                    	                                    
                      <s:if test="#othersCounter==0">
                        	<tr>
                  				<td nowrap="nowrap" style="padding-left: 10px;   padding-bottom: 10px;"><s:text name="label.costcategory.others"/></td>
                  			</tr>
                  		</s:if>
                  		  <s:set var="othersCounter" value="%{#othersCounter+1}"/>
                  	</s:elseif>        
                     <s:else>
                  	 <!-- Condition to not include row if it is travel or others-->
                  	  <s:if test="name=='Travel'||name=='Others'||name=='Late Fee'||name=='Deductible'"> 
                  	   </s:if>
                  	   <s:else>
                   		 <tr>                 
                        	<td nowrap="nowrap" style="padding-left: 10px;   padding-bottom: 10px;"><s:text name="%{getMessageKey(name)}"/></td>
                   			<td nowrap="nowrap" style="padding-left: 10px;   padding-bottom: 10px;text-align:right;" class="numeric">                    					
                   			</td>
                   			  <s:if test="name=='Oem Parts' || name=='Non Oem Parts' ||name=='Labor' ||name=='Travel'">        
                   			  </s:if>
                   			  <s:else>
                   			<td nowrap="nowrap" style="padding-left: 10px;text-align:right;">
                   			<span id="dealerAudit[<s:property value="#status.index"/>]" >
                    			<s:property value="#setDealerAudit.baseAmount"/>
                    		</span>
                    		<s:if test="#setDealerAudit.rate != null">
                     			<%-- <span dojoType="dijit.Tooltip" connectId="dealerAudit[<s:property value="#status.index"/>]">
									@<s:property value="#setDealerAudit.rate"/>
								</span> --%>
								<br>{@<s:property value="#setDealerAudit.rate"/>}
                    		</s:if>				
                   		</td>
                   		<td nowrap="nowrap" style="padding-left: 10px;   padding-bottom: 10px;text-align:right;" class="numeric">
                    		<span id="latestAudit[<s:property value="#status.index"/>]">
                    			<s:property value="baseAmount"/>
                    		</span>
                    		<s:if test="rate != null">
                     			<%-- <span dojoType="dijit.Tooltip" connectId="latestAudit[<s:property value="#status.index"/>]">
									@<s:property value="rate"/>
								</span> --%>
								<br>{@<s:property value="rate"/>}
                    		</s:if>	                        		
                   		</td>
                   			
                    	  </s:else>
                    	  </tr>
                    	  
                    </s:else>
                    </s:else>        
                    
                   <!--  NMHGSLMS425 Changes -->
                     <s:iterator value="individualLineItems" status="indItemStatus">                                       
                 	 <tr>               
                  		<td nowrap="nowrap" style="padding-left: 30px;   padding-bottom: 10px;text-align:left;" class="numeric">
                  		                  		
                  		 <s:if test="name=='Oem Parts'">                  		 	
                  			<s:property value="brandItem.itemNumber"/>-
                  			<s:property value="brandItem.item.description"/>
                  		</s:if>
                  		<s:elseif test="name=='Non Oem Parts'">
                  		<s:property value="nonOemPartReplaced"/>   
                  		</s:elseif>                   		
                  		 </td>
                  		 <td nowrap="nowrap" style="padding-left: 10px;   padding-bottom: 10px;text-align:right;" class="numeric"><s:property value="#setDealerAudit.individualLineItems[#indItemStatus.index].askedQty"/>	</td>
              			<td nowrap="nowrap" style="padding-left: 10px;   padding-bottom: 10px;text-align:right;" class="numeric"><s:property value="#setDealerAudit.individualLineItems[#indItemStatus.index].baseAmount"/></td>
						<td nowrap="nowrap" style="padding-left: 10px;   padding-bottom: 10px;text-align:right;" class="numeric"><s:property value="baseAmount"/> </td>
 						<td nowrap="nowrap" style="padding-left: 2px;   padding-bottom: 10px;text-align:right;" class="numeric"><s:property value="acceptedQty"/> </td>
 						 <s:if test="!claim.payment.isFlatAmountApplied()"> 	  
                        <td nowrap="nowrap" style="padding-left: 10px;   padding-bottom: 10px;text-align:right;" class="numeric"><s:property value="percentageAcceptance"/>%</td>
                        </s:if>
                        <s:else>
					    <td nowrap="nowrap" style="padding-left: 10px;   padding-bottom: 10px;text-align:right;" class="numeric"><s:property value="acceptedAmount"/></td>
					   </s:else>
					   <td nowrap="nowrap" style="padding-left: 10px;   padding-bottom: 10px;text-align:right;" class="numeric"><s:property value="acceptedAmount"/></td>
					    <s:if test="claim.payment.stateMandateActive && !claim.isGoodWillPolicy()">
						<td nowrap="nowrap"	style="padding-left: 10px; padding-bottom: 10px; text-align: right;" class="numeric"><s:property value="stateMandateAmount" />	</td>
						</s:if>
                      </tr>                                         
                 </s:iterator>           
                   <!--  NMHGSLMS425 Changes End-->  
                    </s:else>
                    <s:if test="!modifiers.empty">
                        <s:iterator value="modifiers" status="latestAuditStatus" id="modifier">
                        <tr>
                            <td nowrap="nowrap" style="padding-left: 30px;   padding-bottom: 10px;"><s:property value="paymentVariable.displayName"/>&nbsp;&nbsp;<s:property value="percentageConfigured"/>%	</td>
                            <td nowrap="nowrap" style="padding-left: 10px;   padding-bottom: 10px;"></td>
                            <td nowrap="nowrap" style="padding-left: 10px;   padding-bottom: 10px;text-align:right;" class="numeric">
                                <s:if test="#setDealerAudit.modifierMap[#modifier.name] != null">
           		        			<span id="dealer_[<s:property value="#status.index"/>]_[<s:property value="#latestAuditStatus.index"/>]">
										<s:property value="#setDealerAudit.modifierMap[#modifier.name].value"/>
									</span>
  	                      			<span dojoType="dijit.Tooltip" connectId="dealer_[<s:property value="#status.index"/>]_[<s:property value="#latestAuditStatus.index"/>]">
										<s:if test="#setDealerAudit.modifierMap[#modifier.name].isFlatRate">
											<s:property value="#setDealerAudit.modifierMap[#modifier.name].modifierPercentage"/>
										</s:if>
										<s:else>
											<s:property value="#setDealerAudit.modifierMap[#modifier.name].modifierPercentage"/>%											
										</s:else>
									</span>	                                    
                                </s:if>
                            </td>
                            <td nowrap="nowrap" style="padding-left: 10px;   padding-bottom: 10px;text-align:right;" class="numeric">
                            	<s:if test="%{paymentVariable.getD().isActive()}">
                            		<span id="latest_[<s:property value="#status.index"/>]_[<s:property value="#latestAuditStatus.index"/>]">
										<s:property value="value"/>
									</span>
	 	                      		<span dojoType="dijit.Tooltip" connectId="latest_[<s:property value="#status.index"/>]_[<s:property value="#latestAuditStatus.index"/>]">
										<s:if test="#modifier.isFlatRate">
											<s:property value="modifierPercentage"/>
										</s:if>
										<s:else>
											<s:property value="modifierPercentage"/>%											
										</s:else>
									</span>	
                            	</s:if>          		        		                             	
                            </td>
                             <td nowrap="nowrap" style="padding-left: 10px;   padding-bottom: 10px;text-align:right;" class="numeric"></td>
                             	<s:if test="!claim.payment.isFlatAmountApplied()"> 	 
	                         		<td nowrap="nowrap" style="padding-left: 10px;   padding-bottom: 10px;text-align:right;" class="numeric"><s:property value="percentageAcceptance"/>%</td>
	                         </s:if>
	                         <s:else>
	                         <td nowrap="nowrap" style="padding-left: 10px;   padding-bottom: 10px;text-align:right;" class="numeric"><s:property value="acceptedCost"/></td>
	                         </s:else>
               			 	<td nowrap="nowrap" style="padding-left: 10px;   padding-bottom: 10px;text-align:right;" class="numeric"><s:property value="acceptedCost"/></td>
               			 	<%--  <s:if test="claim.payment.stateMandateActive && !claim.isGoodWillPolicy()">    					
								<td nowrap="nowrap" style="padding-left: 10px;   padding-bottom: 10px;text-align:right;" class="numeric"><s:property value="stateMandateAmount"/></td>
							</s:if>	 --%>				
                     
               			 </tr>
                            
                 <s:if test="claim.payment.stateMandateActive && !claim.isGoodWillPolicy() && #lineItem.name=='Oem Parts' ">       
                 		 <tr> 
                       		<td nowrap="nowrap" style="padding-left: 30px;   padding-bottom: 10px;">(<s:text name="label.payment.stateMandate"/>)<s:property value="paymentVariable.displayName"/>&nbsp;&nbsp;<s:property value="percentageConfiguredSMandate"/>%</td>
                       		<td nowrap="nowrap" style="padding-left: 30px;   padding-bottom: 10px;"></td>
                      		 <td nowrap="nowrap" style="padding-left: 10px;   padding-bottom: 10px;text-align:right;" class="numeric">    </td>
                       		<td nowrap="nowrap" style="padding-left: 10px;   padding-bottom: 10px;text-align:right;" class="numeric">  </td>                         
			           		<td nowrap="nowrap" style="padding-left: 10px;   padding-bottom: 10px;text-align:right;" class="numeric"></td>                        
					   		<td nowrap="nowrap" style="padding-left: 10px;   padding-bottom: 10px;text-align:right;" class="numeric">	</td>
					  		 <td nowrap="nowrap" style="padding-left: 10px;   padding-bottom: 10px;text-align:right;" class="numeric"></td>
					   		<td nowrap="nowrap" style="padding-left: 10px;   padding-bottom: 10px;text-align:right;" class="numeric"><s:property value="stateMandateAmount"/></td>				
				 		 </tr>    
          			 </s:if> 
                          
                        </s:iterator>
                    </s:if>
                    <s:if test="!#setDealerAudit.modifiers.empty">
                        <s:iterator value="#setDealerAudit.modifiers" status="dealerAuditStatus" id="modifier">
	                        <s:if test="modifierMap[#modifier.name] == null">
		                        <tr>
		                            <td nowrap="nowrap" style="padding-left: 30px;   padding-bottom: 10px;"><s:property value="paymentVariable.displayName"/></td>
		                            <td nowrap="nowrap" style="padding-left: 10px;   padding-bottom: 10px;text-align:right;" class="numeric">
		          		        		<span id="dealerAuditModifier1[<s:property value="#dealerAuditStatus.index"/>]">
											<s:property value="value"/>
										</span>
		 	                      		<span dojoType="dijit.Tooltip" connectId="dealerAuditModifier1[<s:property value="#dealerAuditStatus.index"/>]">
											<s:if test="#modifier.isFlatRate">
												<s:property value="modifierPercentage"/>
											</s:if>
											<s:else>
												<s:property value="modifierPercentage"/>%											
											</s:else>
										</span>	                             	
		                            </td>
		                            <td nowrap="nowrap" style="padding-left: 10px;   padding-bottom: 10px;text-align:right;" class="numeric">
		                                <s:if test="modifierMap[#modifier.name] != null">
		           		        			<span id="latestAuditModifier1[<s:property value="#dealerAuditStatus.index"/>]">
												<s:property value="modifierMap[#modifier.name].value"/>
											</span>
		  	                      			<span dojoType="dijit.Tooltip" connectId="latestAuditModifier1[<s:property value="#dealerAuditStatus.index"/>]">
												<s:if test="modifierMap[#modifier.name].isFlatRate">
													<s:property value="modifierMap[#modifier.name].modifierPercentage"/>
												</s:if>
												<s:else>
													<s:property value="modifierMap[#modifier.name].modifierPercentage"/>%											
												</s:else>
											</span>	                                    
		                                </s:if>
		                            </td>
		                        </tr>                
		              
	                        </s:if>
                        </s:iterator>
                    </s:if>
                    
                <%--  <s:if test="name=='Late Fee'">
                 	<s:if test="claim.payment.deductibleAmount!=null">
                 		<tr > 
                    		 <td nowrap="nowrap" style="padding-left: 30px;   padding-bottom: 10px;"> <s:text name="label.common.payment.deductable"/>:</td>
                     		<td nowrap="nowrap" style="padding-left: 30px;   padding-bottom: 10px;"></td>
                       		 <td nowrap="nowrap" style="padding-left: 30px;   padding-bottom: 10px;"></td>
                         	<td nowrap="nowrap" style="padding-left: 30px;   padding-bottom: 10px;"><s:property value="claim.payment.deductibleAmount"/></td>   
                         	<td nowrap="nowrap" style="padding-left: 30px;   padding-bottom: 10px;"></td>              	
                       		 <td nowrap="nowrap" style="padding-left: 30px;   padding-bottom: 10px;"></td>
                         	<td nowrap="nowrap" style="padding-left: 30px;   padding-bottom: 10px;text-align:right;"><s:property value="claim.payment.deductibleAmount"/></td>
                         	 <s:if test="claim.payment.stateMandateActive && !claim.isGoodWillPolicy()">  
                         	 <td nowrap="nowrap" style="padding-left: 30px;   padding-bottom: 10px;text-align:right;"><s:property value="claim.payment.deductibleAmount"/></td> 
                         	 </s:if>   
                         	</tr>                             
                 	</s:if>
                 </s:if>  --%>
                    
                    
                  <s:if test="name=='Travel'">
                 
                     <s:if test="#travelAddHrs!=null||#travelByHrs!=null">                 
                       <s:if test="#travelAddHrs!=null">
                        <s:set name="travelRate" value="#travelAddHrs" />
                         <s:set name="travelAuditRate" value="#TravelByAddHrsAudit" />
                          </s:if>
                          <s:else>
                           <s:set name="travelRate" value="#travelByHrs" />
                            <s:set name="travelAuditRate" value="#TravelByHrsAudit" />
                          </s:else>
                   			         <tr>
                			<td nowrap="nowrap"	style="padding-left: 60px; padding-bottom: 10px; text-align: left;" class="numeric"><s:text name="label.common.rate"/>:</td>		
                			<td nowrap="nowrap" style="padding-left: 10px;   padding-bottom: 10px; text-align: right;"> </td>		
               				 <td nowrap="nowrap"	style="padding-left: 10px; padding-bottom: 10px; text-align: right;" class="numeric">{<s:if test="!buConfigAMER">@</s:if><s:property value="#travelAuditRate.rate"/>}	</td>				
							<td nowrap="nowrap"	style="padding-left: 10px; padding-bottom: 10px; text-align: right;" class="numeric">{<s:if test="!buConfigAMER">@</s:if><s:property value="#travelRate.rate"/>}</td>
							<td nowrap="nowrap" style="padding-left: 10px;   padding-bottom: 10px; text-align: right;"> </td>
						 	 <td nowrap="nowrap" style="padding-left: 10px;   padding-bottom: 10px; text-align: right;"></td>
						  	<td nowrap="nowrap" style="padding-left: 10px;   padding-bottom: 10px; text-align: right;">{<s:if test="!buConfigAMER">@</s:if><s:property value="#travelRate.rate"/>}</td>
						  <s:if test="claim.payment.stateMandateActive && !claim.isGoodWillPolicy()">
						   <td nowrap="nowrap" style="padding-left: 10px;   padding-bottom: 10px; text-align: right;"><%-- <s:property value="#travelRate.stateMandateRatePercentage"/>% --%> {<s:if test="!buConfigAMER">@</s:if><s:property value="#travelRate.stateMandateRate"/>}</td>
						   </s:if>
						    
                		</tr>
                </s:if>
                </s:if>    
                    
                    
                          <!-- Total code -->
                   <s:if test="name!='Others'">  
                     <s:if test="name=='Travel by Hours' || name=='Travel By Trip' ||name=='Additional Travel Hours'||name=='Item Freight And Duty' || name=='Handling Fee' ||name=='Transportation'||name=='Late Fee'||name=='Deductible'"> 
                     <tr > 
                     
                     <td nowrap="nowrap" style="padding-left: 30px;   padding-bottom: 10px;"><s:if test="name!='Travel By Trip'&&name!='Late Fee'&&name!='Deductible' "><s:text name="label.common.total"/> </s:if> <s:text name="%{getMessageKey(name)}"/>:  <s:if test="name=='Late Fee' "><s:property value="percentageApplicable"/>%</s:if></td>
                     </s:if>
                     <s:else>
                        <!-- Total code -->
                     <tr class="total" >
                     <td nowrap="nowrap" style="padding-left: 60px;   padding-bottom: 10px;"><s:text name="label.common.total"/> <s:text name="%{getMessageKey(name)}"/>:</td>
                     </s:else>                       
                        <td nowrap="nowrap" style="padding-left: 10px;   padding-bottom: 10px;text-align:right;" class="numeric">
                        <!-- fix for SLMSPROD-1392 -->
                        <s:if test="name=='Travel by Hours' || name=='Additional Travel Hours' || name=='Travel'">
                        	<s:property value="#setDealerAudit.askedQtyHrs.replace('.',':')"/>
                        </s:if>
                        <s:else>
                        	<s:property value="#setDealerAudit.askedQtyHrs"/>
                        </s:else>
                        </td>
                        <td nowrap="nowrap" style="padding-left: 10px;   padding-bottom: 10px;text-align:right;" class="numeric">
                         <s:if test="name=='Late Fee' ||name=='Travel By Trip'  ">
                        	 </s:if>
                        	 <s:else>
                        	 <s:property value="#setDealerAudit.groupTotal"/>
                        	 </s:else></td>
                        <td nowrap="nowrap" style="padding-left: 10px;   padding-bottom: 10px;text-align:right;" class="numeric">
                         <s:if test="name!='Travel By Trip' "><s:property value="groupTotal"/></s:if></td>
                          <td nowrap="nowrap" style="padding-left: 10px;   padding-bottom: 10px;text-align:right;" class="numeric">
                           <s:if test="name=='Travel by Hours' || name=='Additional Travel Hours' || name=='Travel'">
                        		<s:property value="acceptedQtyHrs.replace('.',':')"/>
	                       </s:if>
	                       <s:else>
	                        	<s:property value="acceptedQtyHrs"/>
	                       </s:else>                          
							</td>                     
                        <s:if test="!claim.payment.isFlatAmountApplied()">	
                       	<td nowrap="nowrap" style="padding-left: 10px;   padding-bottom: 10px;text-align:right;" class="numeric">
                            	<s:if test="name=='Travel By Trip' || name=='Deductible'">  
                            	</s:if>
                            	<s:else>
                            	<s:property value="percentageAcceptance"/>%
                            	</s:else>
                        	</td>      
                        	</s:if>
                        	<s:else>            
                        	<td nowrap="nowrap" style="padding-left: 10px;   padding-bottom: 10px;text-align:right;" class="numeric">
                        	   <s:if test="name!='Travel By Trip'||name!='Deductible' ">  
                        <s:property value="acceptedTotal"/> 
                        </s:if>                        
                        	</td>  
                        	</s:else>   
                        	<td nowrap="nowrap" style="padding-left: 10px;   padding-bottom: 10px;text-align:right;" class="numeric">
                        	   <s:if test="name=='Travel By Trip' ">  
                        <s:property value="acceptedQtyHrs"/> 
                        </s:if>
                        <s:else>
                            	<s:property value="acceptedTotal"/>
                            	</s:else>
                        	</td>
                        <s:if test="claim.payment.stateMandateActive && !claim.isGoodWillPolicy()">         
                         <td class="numeric" nowrap="nowrap" style="text-align:right;">
                            <s:if test="name=='Travel By Trip' ">  
                        <s:property value="acceptedQtyHrs"/> 
                        </s:if>
                        <s:else>
                        <s:property value="groupTotalStateMandateAmount"/>
                        </s:else>
                        </td>
                        </s:if>                 
                    </tr>
                    </s:if>            
                 
                <%--        <s:if test="name=='Additional Travel Hours'">
                   			         <tr>
                			<td nowrap="nowrap"	style="padding-left: 60px; padding-bottom: 10px; text-align: left;" class="numeric"><s:text name="label.common.rate"/>:</td>		
                			<td nowrap="nowrap" style="padding-left: 10px;   padding-bottom: 10px; text-align: right;"> </td>		
               				 <td nowrap="nowrap"	style="padding-left: 10px; padding-bottom: 10px; text-align: right;" class="numeric">@<s:property value="#setDealerAudit.rate"/>	</td>				
							<td nowrap="nowrap"	style="padding-left: 10px; padding-bottom: 10px; text-align: right;" class="numeric">@<s:property value="rate"/></td>
							<td nowrap="nowrap" style="padding-left: 10px;   padding-bottom: 10px; text-align: right;"> </td>
						 	 <td nowrap="nowrap" style="padding-left: 10px;   padding-bottom: 10px; text-align: right;"></td>
						  	<td nowrap="nowrap" style="padding-left: 10px;   padding-bottom: 10px; text-align: right;">@<s:property value="rate"/></td>
						  <s:if test="claim.payment.stateMandateActive && !claim.isGoodWillPolicy()">
						   <td nowrap="nowrap" style="padding-left: 10px;   padding-bottom: 10px; text-align: right;"><s:property value="stateMandateRatePercentage"/>% @<s:property value="stateMandateRate"/></td>
						   </s:if>						    
                				</tr>
               		 </s:if> --%>
                    
                </s:if>
             </s:iterator>
             <s:iterator value="claim.payment.lineItemGroups" status="status">
                <s:set name="setDealerPayment" value="%{claim.getPaymentForDealerAudit()}"/> 
				<s:set name="setDealerAudit" value="#setDealerPayment.getLineItemGroup(name)"/>
                    <s:if test="name=='Claim Amount'">
                        <s:set name="claimAmountAudit" value="latestAudit"/>
                     <%--    <tr>
                            <td nowrap="nowrap" style="padding-left: 10px;   padding-bottom: 10px;"><s:text name="%{getMessageKey(name)}"/></td>
                             <td nowrap="nowrap" style="padding-left: 10px;   padding-bottom: 10px;text-align:right;" class="numeric"></td>
                            <td nowrap="nowrap" style="padding-left: 10px;   padding-bottom: 10px;text-align:right;" class="numeric"><s:property value="#setDealerAudit.baseAmount"/></td>
                            <td nowrap="nowrap" style="padding-left: 10px;   padding-bottom: 10px;text-align:right;" class="numeric"><s:property value="#claimAmountDisplayAudit.baseAmount"/></td>
                            <td nowrap="nowrap" style="padding-left: 10px;   padding-bottom: 10px;text-align:right;"class="numeric"></td>
                            <td nowrap="nowrap" style="padding-left: 10px;   padding-bottom: 10px;text-align:right;" class="numeric"></td>
                            <s:if test="#isLineItemChanged">
                            	<td class="numeric" nowrap="nowrap" style="text-align:right;"><s:property value="baseAmount"/></td>
                            	<s:if test="isCPAdvisorEnabled()">
	                        		<td nowrap="nowrap" style="padding-left: 10px;   padding-bottom: 10px;text-align:right;" class="numeric"></td>
	                        		<td class="numeric" nowrap="nowrap" style="text-align:right;"><s:property value="baseAmountForCp"/></td>
                        		</s:if>
                            </s:if>
                        </tr> --%>
                    <s:if test="!#setDealerAudit.modifiers.empty">
                        <s:iterator value="#setDealerAudit.modifiers" status="dealerAuditStatus" id="modifier">
                        <tr>
                            <td nowrap="nowrap" style="padding-left: 30px;   padding-bottom: 10px;"><s:property value="paymentVariable.displayName"/></td>
                            <td nowrap="nowrap" style="padding-left: 10px;   padding-bottom: 10px;text-align:right;" class="numeric">
          		        		<span id="dealerAudit1TotalModifier[<s:property value="#dealerAuditStatus.index"/>]">
									<s:property value="value"/>
								</span>
 	                      		<span dojoType="dijit.Tooltip" connectId="dealerAudit1TotalModifier[<s:property value="#dealerAuditStatus.index"/>]">
									<s:if test="#modifier.isFlatRate">
										<s:property value="modifierPercentage"/> 
									</s:if>
									<s:else>
										<s:property value="modifierPercentage"/>% 
									</s:else>
								</span>	                             	
                            </td>
                            <s:if test="#claimAmountDisplayAudit.modifierMap[#modifier.name] != null">
                                <td nowrap="nowrap" style="padding-left: 10px;   padding-bottom: 10px;text-align:right;" class="numeric">
           		        			<span id="claimAmount1Modifier[<s:property value="#dealerAuditStatus.index"/>]">
										<s:property value="#claimAmountDisplayAudit.modifierMap[#modifier.name].value"/>
									</span>
  	                      			<span dojoType="dijit.Tooltip" connectId="claimAmount1Modifier[<s:property value="#dealerAuditStatus.index"/>]">
										<s:if test="#claimAmountDisplayAudit.modifierMap[#modifier.name].isFlatRate">
											<s:property value="#claimAmountDisplayAudit.modifierMap[#modifier.name].modifierPercentage"/>  
										</s:if>
										<s:else>
											<s:property value="#claimAmountDisplayAudit.modifierMap[#modifier.name].modifierPercentage"/>%										
										</s:else>
									</span>	                                    
                                </td>
                            </s:if>                            
                        </tr>
                        </s:iterator>
                    </s:if>
                    <s:if test="!modifiers.empty">
                        <s:iterator value="modifiers" status="latestAuditStatus" id="modifier">
	                        <s:if test="#setDealerAudit.modifierMap[#modifier.name] == null">
		                        <tr>
		                            <td nowrap="nowrap" style="padding-left: 30px;   padding-bottom: 10px;"><s:property value="paymentVariable.displayName"/></td>
		                            <td nowrap="nowrap" style="padding-left: 10px;   padding-bottom: 10px;text-align:right;" class="numeric">
		                                <s:if test="#setDealerAudit.modifierMap[#modifier.name] != null">
			          		        		<span id="dealerAuditTotalModifier1[<s:property value="#latestAuditStatus.index"/>]">
												<s:property value="#setDealerAudit.modifierMap[#modifier.name].value"/>
											</span>
			 	                      		<span dojoType="dijit.Tooltip" connectId="dealerAuditTotalModifier1[<s:property value="#latestAuditStatus.index"/>]">
												<s:if test="#setDealerAudit.modifierMap[#modifier.name].isFlatRate">
													<s:property value="#setDealerAudit.modifierMap[#modifier.name].modifierPercentage"/>
												</s:if>
												<s:else>
													<s:property value="#setDealerAudit.modifierMap[#modifier.name].modifierPercentage"/>%											
												</s:else>
											</span>	                             	
		                                </s:if>
		                            </td>
		                            <s:if test="#claimAmountDisplayAudit.modifierMap[#modifier.name] != null">
		                                <td nowrap="nowrap" style="padding-left: 10px;   padding-bottom: 10px;text-align:right;" class="numeric">
		           		        			<span id="claimAmountMod1[<s:property value="#latestAuditStatus.index"/>]">
												<s:property value="#claimAmountDisplayAudit.modifierMap[#modifier.name].value"/>
											</span>
		  	                      			<span dojoType="dijit.Tooltip" connectId="claimAmountMod1[<s:property value="#latestAuditStatus.index"/>]">
												<s:if test="#claimAmountDisplayAudit.modifierMap[#modifier.name].isFlatRate">
													<s:property value="#claimAmountDisplayAudit.modifierMap[#modifier.name].modifierPercentage"/>
												</s:if>
												<s:else>
													<s:property value="#claimAmountDisplayAudit.modifierMap[#modifier.name].modifierPercentage"/>%											
												</s:else>
											</span>	                                    
		                                </td>
		                            </s:if>		                            
		                            <td></td>
		                        </tr>
	                        </s:if>
                        </s:iterator>
                    </s:if>
                    <tr class="total">
                        <td nowrap="nowrap" style="padding-left: 50px;   padding-bottom: 10px;"><s:text name="label.common.total"/> <s:text name="%{getMessageKey(name)}"/></td>
                        <td nowrap="nowrap" style="padding-left: 10px;   padding-bottom: 10px;text-align:right;" class="numeric"></td>
                        <td nowrap="nowrap" style="padding-left: 10px;   padding-bottom: 10px;text-align:right;" class="numeric"><s:property value="#setDealerAudit.groupTotal"/></td>
                        <td nowrap="nowrap" style="padding-left: 10px;   padding-bottom: 10px;text-align:right;" class="numeric"><s:property value="groupTotal"/></td>
                        <td nowrap="nowrap" style="padding-left: 10px;   padding-bottom: 10px;text-align:right;" class="numeric"></td>
                        <td nowrap="nowrap" style="padding-left: 10px;   padding-bottom: 10px;text-align:right;" class="numeric" >
                            <s:if test="!claim.payment.isFlatAmountApplied()">	
                                <s:property value="percentageAcceptance"/>%
                                </s:if>
                                <s:else>
                                <s:property value="acceptedTotal"/>
                                </s:else>
                         
                        </td>
                        <td class="numeric" nowrap="nowrap" style="text-align:right;"><s:property value="acceptedTotal"/></td>
                        <s:if test="claim.payment.stateMandateActive && !claim.isGoodWillPolicy()">         
                         <td class="numeric" nowrap="nowrap" style="text-align:right;"><s:property value="groupTotalStateMandateAmount"/></td>
                         </s:if>  
                       </tr>
                       
                         <tr class="total">
		            <td nowrap="nowrap" style="padding-left: 60px;" colspan="2">
		                <s:text name="label.common.approvedClaimAmount"/>
		            </td>
		            <td nowrap="nowrap" style="padding-left: 60px;" class="numeric" style="padding-left: 10px;">
		               <%--  <s:property value="acceptedTotalForWnty"/> --%>
		                 <s:property value="getAdditionalPaymentInfoOfType(@tavant.twms.domain.claim.payment.AdditionalPaymentType@ACCEPTED_FOR_WNTY)"/>
		            </td>
			     </tr>
			     <tr class="total">
		            <td nowrap="nowrap" style="padding-left: 60px;" colspan="2">
		                <s:text name="label.common.claimCost"/>  
		            </td>
		            <td nowrap="nowrap" style="padding-left: 60px;" class="numeric">
		               <%--  <s:property value="acceptedTotalForCp"/> --%>
		              <s:property value="getAdditionalPaymentInfoOfType(@tavant.twms.domain.claim.payment.AdditionalPaymentType@ACCEPTED_FOR_CP)"/>
		            </td>
			     </tr>
		   
                    </s:if>
             </s:iterator>
       <%--  <tr class="total">
            <td colspan="2">
                <s:text name="claim.payment.netPercentageAccepted"/>  
            </td>
            <td class="numeric">
                <s:property value="claim.netAcceptancePercentageForPayment"/>% 
            </td>
        </tr>
        <s:if test="isCPAdvisorEnabled()">
            <s:set name="setPayment" value="%{claim.getPayment()}"/>
            <s:set name="setSummationLig" value="#setPayment.getLineItemGroup('Claim Amount')"/>
		    <tr class="total">
		        <td colspan="2">
		            <s:text name="label.common.amountAccepted.warranty"/>  
		        </td>
		        <td class="numeric">
	                <s:property value="#setSummationLig.getAdditionalPaymentInfoOfType(@tavant.twms.domain.claim.payment.AdditionalPaymentType@ACCEPTED_FOR_WNTY)"/>
		        </td>
		    </tr>
		  <tr class="total">
		        <td colspan="2">
		            <s:text name="label.common.amountAccepted.cp"/>  
		        </td>
		        <td class="numeric">
		           <s:property value="#setSummationLig.getAdditionalPaymentInfoOfType(@tavant.twms.domain.claim.payment.AdditionalPaymentType@ACCEPTED_FOR_CP)"/>
		        </td>
		    </tr> 
	    </s:if>  --%>
	    <s:if test="claim.payment.activeCreditMemo != null && (claim.reopened || claim.appealed)">
	        <tr class="total">
	            <td nowrap="nowrap">
	                <s:text name="label.additional.amount.accepted"/>:  
	            </td>
	            <td class="numeric" colspan="2">
	                <s:property value="%{claim.payment.effectiveAmountToBePaid.breachEncapsulationOfCurrency()}" />
	                &#160;<s:property value="%{claim.payment.effectiveAmountToBePaid.breachEncapsulationOfAmount().abs()}" />
	                <s:if test="claim.payment.effectiveAmountToBePaid.isNegative()">
	                	&#160;&#160;<s:text name="label.claim.drflag"></s:text>
	                </s:if>
	                <s:else>
	                	&#160;&#160;<s:text name="label.claim.crflag"></s:text>
	                </s:else>
	            </td>
	        </tr>
	    </s:if>
   </tbody>
  </table>
</div>

<div id="lowLeveltablepopup_processor_read" style="display:none;">
    <table id="percentage_payment" style="width: 98%;">
        <thead>
            <tr>
                <th style="text-align:left;" class="labelStyle" width="15%"><s:text name="label.common.category"/></th>
                <th style="text-align:right;" class="labelStyle" width="10%">&nbsp;&nbsp;&nbsp;&nbsp;<s:text name="label.claim.amountAsked" /></th>
                <th style="text-align:right;" class="labelStyle" width="15%">&nbsp;&nbsp;&nbsp;&nbsp;<s:text name="label.common.currentAmount" /></th>
                <th style="text-align:right;" class="labelStyle" width="15%">&nbsp;&nbsp;&nbsp;&nbsp;<s:text name="label.common.costPrice"/></th>
                <th style="text-align:right;" class="labelStyle" width="15%"><s:text name="label.newClaim.percentageAccepted.forWarranty"/></th>
                <th style="text-align:right;" class="labelStyle" width="15%"><s:text name="label.newClaim.acceptedAmount.for.warranty"/></th>
                <s:if test="isCPAdvisorEnabled()">
                <th style="text-align:center;" class="labelStyle" width="15%"><s:text name="label.newClaim.percentageAcceptedForCP"/></th>
                <th style="text-align:center;" class="labelStyle" width="15%"><s:text name="label.newClaim.acceptedAmount.for.cp"/></th>
                </s:if>
            </tr>
        </thead>
        <tbody>
            <s:iterator value="claim.payment.lineItemGroups" status="status">
                <s:set name="setDealerPayment" value="%{claim.getPaymentForDealerAudit()}"/> 
				<s:set name="setDealerAudit" value="#setDealerPayment.getLineItemGroup(name)"/>
                <s:if test="name != 'Claim Amount'">
                <tr class="total">
                        <td nowrap="nowrap" style="padding-left: 10px;   padding-bottom: 10px;"><s:text name="label.common.total"/> <s:text name="%{getMessageKey(name)}"/></td>
                        <td nowrap="nowrap" style="padding-left: 10px;   padding-bottom: 10px;text-align:right;" class="numeric"><s:property value="#setDealerAudit.groupTotal"/></td>
                        <td nowrap="nowrap" style="padding-left: 10px;   padding-bottom: 10px;text-align:right;" class="numeric"><s:property value="groupTotal"/></td>
                        <s:if test="name == 'Oem Parts'">
                        	<td nowrap="nowrap" style="padding-left: 10px;   padding-bottom: 10px;text-align:right;" class="numeric"><s:property value="acceptedCpTotal"/></td>
                        </s:if>
                        <s:else>
                        	<td></td>
                        </s:else>
                        <td nowrap="nowrap" style="padding-left: 10px;   padding-bottom: 10px;text-align:right;" class="numeric">
                            <s:property value="percentageAcceptance"/>%
                        </td>
                        <td class="numeric" nowrap="nowrap" style="text-align:right;"><s:property value="acceptedTotal"/></td>
                        <s:if test="isCPAdvisorEnabled()">
                        <td nowrap="nowrap" style="padding-left: 10px;   padding-bottom: 10px;text-align:right;" class="numeric">
                            <s:property value="getPercentageAcceptedForAdditionalInfo(@tavant.twms.domain.claim.payment.AdditionalPaymentType@ACCEPTED_FOR_CP)"/>%
                        </td>
                        <td class="numeric" nowrap="nowrap" style="text-align:right;">
	                        <s:property value="getAdditionalPaymentInfoOfType(@tavant.twms.domain.claim.payment.AdditionalPaymentType@ACCEPTED_FOR_CP)"/>
                        </td>
                        </s:if>
                    </tr>
                </s:if>
             </s:iterator>
             <s:iterator value="claim.payment.lineItemGroups" status="status">
                 <s:set name="setDealerPayment" value="%{claim.getPaymentForDealerAudit()}"/>
                 <s:set name="setDealerAudit" value="#setDealerPayment.getLineItemGroup(name)"/>
                 <s:set name="setLatestAudit" value="latestAudit"/>
                    <s:if test="name=='Claim Amount'">
                        <s:set name="claimAmountAudit" value="latestAudit"/>
                        <tr class="total">
                            <td nowrap="nowrap" style="padding-left: 10px;   padding-bottom: 10px;"><s:text name="label.common.total"/> <s:text name="%{getMessageKey(name)}"/></td>
                            <td nowrap="nowrap" style="padding-left: 10px;   padding-bottom: 10px;text-align:right;" class="numeric"><s:property value="#setDealerAudit.groupTotal"/></td>
                            <td nowrap="nowrap" style="padding-left: 10px;   padding-bottom: 10px;text-align:right;" class="numeric"><s:property value="#claimAmountDisplayAudit.groupTotal"/></td>
                            <td nowrap="nowrap" style="padding-left: 10px;   padding-bottom: 10px;text-align:right;" class="numeric"><s:property value="#setLatestAudit.acceptedCpTotal"/></td>
                            <td nowrap="nowrap" style="padding-left: 10px;   padding-bottom: 10px;text-align:right;" class="numeric" >
                                <s:if test="!#isLineItemChanged">
                                    <s:property value="#setLatestAudit.percentageAcceptance"/>%
                                </s:if>
                            </td>
                            <td class="numeric" nowrap="nowrap" style="text-align:right;"><s:property value="#setLatestAudit.acceptedTotal"/></td>
                            <s:if test="isCPAdvisorEnabled()">
                            <td nowrap="nowrap" style="padding-left: 10px;   padding-bottom: 10px;text-align:right;" class="numeric" >
                                <s:if test="!#isLineItemChanged">
                                    <s:property value="#setLatestAudit.getPercentageAcceptedForAdditionalInfo(@tavant.twms.domain.claim.payment.AdditionalPaymentType@ACCEPTED_FOR_CP)"/>%
                                </s:if>
                            </td>
                            <td class="numeric" nowrap="nowrap" style="text-align:right;">
                                <s:property value="#setLatestAudit.getAdditionalPaymentInfoOfType(@tavant.twms.domain.claim.payment.AdditionalPaymentType@ACCEPTED_FOR_CP)"/>
                            </td>
                            </s:if>
                        </tr>
                    </s:if>
             </s:iterator>
              <tr class="total">
            <td colspan="2">
                <s:text name="claim.payment.netPercentageAccepted"/>   
            </td>
            <td class="numeric">
                <s:property value="claim.netAcceptancePercentageForPayment"/>% 
            </td>
        </tr>
        <s:if test="isCPAdvisorEnabled()">
		    <tr class="total">
		        <td colspan="2">
		            <s:text name="label.common.amountAccepted.warranty"/>  
		        </td>
		        <td class="numeric">
	               <s:property value="#setSummationLig.getAdditionalPaymentInfoOfType(@tavant.twms.domain.claim.payment.AdditionalPaymentType@ACCEPTED_FOR_WNTY)"/>		        
	            </td>
		    </tr>
		    <tr class="total">
		        <td colspan="2">
		            <s:text name="label.common.amountAccepted.cp"/>  
		        </td>
		        <td class="numeric">
	               <s:property value="#setSummationLig.getAdditionalPaymentInfoOfType(@tavant.twms.domain.claim.payment.AdditionalPaymentType@ACCEPTED_FOR_CP)"/>		        
	            </td>
		    </tr>
	    </s:if> 
	    <s:if test="claim.payment.activeCreditMemo != null && (claim.reopened || claim.appealed)">
	        <tr class="total">
	            <td nowrap="nowrap">
	                <s:text name="label.additional.amount.accepted"/>:  
	            </td>
	            <td class="numeric" colspan="2">
	                <s:property value="%{claim.payment.effectiveAmountToBePaid.breachEncapsulationOfCurrency()}" />
	                &#160;<s:property value="%{claim.payment.effectiveAmountToBePaid.breachEncapsulationOfAmount().abs()}" />
	                <s:if test="claim.payment.effectiveAmountToBePaid.isNegative()">
	                	&#160;&#160;<s:text name="label.claim.drflag"></s:text>
	                </s:if>
	                <s:else>
	                	&#160;&#160;<s:text name="label.claim.crflag"></s:text>
	                </s:else>
	            </td>
	        </tr>
	    </s:if>
                </tbody>
                </table>
                </div>
</s:if>
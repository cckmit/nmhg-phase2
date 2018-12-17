<%--
  Created by IntelliJ IDEA.
  User: pradyot.rout
  Date: Oct 28, 2008
  Time: 11:19:14 AM
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
function paymentView(idval){

	document.getElementById('paymentLowLevelpopup').className='clickable';
	document.getElementById('paymentDetailedpopup').className='clickable';
	document.getElementById(idval).className='nonclickHighlite';

	if(idval=='paymentDetailedpopup'){
		document.getElementById('detailedlevelpopup').style.display='';
		document.getElementById('lowLeveltablepopup').style.display='none';
	}

	if(idval=='paymentLowLevelpopup'){
		document.getElementById('detailedlevelpopup').style.display='none';
		document.getElementById('lowLeveltablepopup').style.display='';
	}
}
</script>
<s:if test="!claim.payment.lineItemGroups.isEmpty()">
    <s:set name="claimAmountDisplayAudit" value="%{getLineItemGroupAuditForGlobalLevel(claim)}"/>
    <s:set name="isLineItemChanged" value="%{isLineItemPercentageChanged(claim)}"/>
	<div style="width:100%;overflow:auto">
	<div class="labelStyle" style="margin:10px 0px 10px 5px;">
	<%-- <s:text name="message.claim.payment.selectLevel"/> --%>
	<span  style="display:none; padding-left:20px;"><a class="nonclickHighlite" onclick="paymentView(id);" id="paymentDetailedpopup"><s:text name="message.claim.payment.detailed"/></a></span>
	<span  style="display:none; padding-left:20px;"><a class="clickable" onclick="paymentView(id);" id="paymentLowLevelpopup"><s:text name="message.claim.payment.lowlevel"/></a></span>
	</div>
	<div id="detailedlevelpopup">
    <table id="percentage_payment" width="96%" >
          <thead>
              <tr>
                <th style="text-align:left;" width="15%" class="labelStyle"><s:text name="label.common.category"/></th>
                <th style="text-align:right;" width="10%" class="labelStyle">&nbsp;&nbsp;&nbsp;&nbsp;<s:text name="label.claim.amountAsked" /></th>
                <th style="text-align:right;" width="15%" class="labelStyle">&nbsp;&nbsp;&nbsp;&nbsp;<s:text name="label.claim.currentAmount" /></th>
                <th style="text-align:right;" width="15%" class="labelStyle">&nbsp;&nbsp;&nbsp;&nbsp;<s:text name="label.common.costPrice"/></th>
                <th style="text-align:right;" width="15%" class="labelStyle"><s:text name="label.newClaim.percentageAccepted.SMReview"/></th>
                <th style="text-align:right;" width="15%" class="labelStyle"><s:text name="label.newClaim.acceptedAmount.forSMR"/></th>               
            </tr>
         </thead>
         <tbody>
             <s:iterator value="claim.payment.lineItemGroups" status="status">
                <s:if test="claim.state.state=='Service Manager Response'">
                    <s:set name="setDealerPayment" value="%{claim.getPaymentForClaimState('Service Manager Review')}"/>
                </s:if>
                <s:else>
                    <s:set name="setDealerPayment" value="%{claim.getPaymentForDealerAudit()}"/> 
                </s:else>
				<s:set name="setDealerAudit" value="#setDealerPayment.getLineItemGroup(#name)"/>
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
													<span id="dealerAuditDoesExist[<s:property value="#dealerAuditStatus.index"/>]">
														<s:property value="paymentDetailForSplitAudit"/>
													</span>
				  	                      			<span dojoType="dijit.Tooltip" connectId="dealerAuditDoesExist[<s:property value="#dealerAuditStatus.index"/>]">

														     @<s:property value="laborRateForSplitAudit"/>
													</span>													
												</td>
                        						<td nowrap="nowrap" style="padding-left: 10px;   padding-bottom: 10px;text-align:right;" class="numeric">
													<span id="latestAuditDoesExist[<s:property value="#dealerAuditStatus.index"/>]">
														<s:property value="forLaborSplitAudit[#latestAuditStatus.index].paymentDetailForSplitAudit"/>
													</span>
				  	                      			<span dojoType="dijit.Tooltip" connectId="latestAuditDoesExist[<s:property value="#dealerAuditStatus.index"/>]">

                                                           @<s:property value="forLaborSplitAudit[#latestAuditStatus.index].laborRateForSplitAudit"/>  
													</span>														                        						
                        						</td>                        				
                        					</s:if>
                        				</s:iterator>
                        				<s:if test="!#doesExists">
                        					<td nowrap="nowrap" style="padding-left: 10px;   padding-bottom: 10px;text-align:right;" class="numeric"></td>
                        					<td nowrap="nowrap" style="padding-left: 10px;   padding-bottom: 10px;text-align:right;" class="numeric">
              		        					<span id="dealerAuditDoesntExist[<s:property value="#latestAuditStatus.index"/>]">
													<s:property value="paymentDetailForSplitAudit"/>
												</span>
			  	                      			<span dojoType="dijit.Tooltip" connectId="dealerAuditDoesntExist[<s:property value="#latestAuditStatus.index"/>]">
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
													<span id="dealerAuditDoesExist[<s:property value="#latestAuditStatus.index"/>]">
														<s:property value="#setDealerAudit.forLaborSplitAudit[#dealerAuditStatus.index].paymentDetailForSplitAudit"/>
													</span>
				  	                      			<span dojoType="dijit.Tooltip" connectId="dealerAuditDoesExist[<s:property value="#latestAuditStatus.index"/>]">

                                                             @<s:property value="forLaborSplitAudit[#latestAuditStatus.index].laborRateForSplitAudit"/> 
													</span>																										
												</td>
                        						<td nowrap="nowrap" style="padding-left: 10px;   padding-bottom: 10px;text-align:right;" class="numeric">
													<span id="latestAuditDoesExist[<s:property value="#latestAuditStatus.index"/>]">
														<s:property value="paymentDetailForSplitAudit"/>
													</span>
				  	                      			<span dojoType="dijit.Tooltip" connectId="latestAuditDoesExist[<s:property value="#latestAuditStatus.index"/>]">

                                                            @<s:property value="laborRateForSplitAudit"/> 
													</span>													
                        						</td>                        				
                        					</s:if>
                        				</s:iterator>
                        				<s:if test="!#doesExists">
                        					<td nowrap="nowrap" style="padding-left: 10px;   padding-bottom: 10px;text-align:right;" class="numeric">
              		        					<span id="dealerAuditDoesntExist[<s:property value="#dealerAuditStatus.index"/>]">
													<s:property value="paymentDetailForSplitAudit"/>
												</span>
			  	                      			<span dojoType="dijit.Tooltip" connectId="dealerAuditDoesntExist[<s:property value="#dealerAuditStatus.index"/>]">
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
                            	<td nowrap="nowrap" style="padding-left: 30px;   padding-bottom: 10px;"><s:text name="%{getMessageKey(name)}"/></td>
                        		<td nowrap="nowrap" style="padding-left: 10px;   padding-bottom: 10px;text-align:right;" class="numeric">
  	                      			<span id="dealerAudit[<s:property value="#status.index"/>]">
  	                      				<s:property value="#setDealerAudit.baseAmount"/>
  	                      			</span>
  	                      			<s:if test="#setDealerAudit.rate != null">
	  	                      			<span dojoType="dijit.Tooltip" connectId="dealerAudit[<s:property value="#status.index"/>]">
											@<s:property value="#setDealerAudit.rate"/>
										</span>
  	                      			</s:if>				
                        		</td>
                        		<td nowrap="nowrap" style="padding-left: 10px;   padding-bottom: 10px;text-align:right;" class="numeric">
  	                      			<span id="latestAudit[<s:property value="#status.index"/>]">
  	                      				<s:property value="baseAmount"/>
  	                      			</span>
  	                      			<s:if test="rate != null">
	  	                      			<span dojoType="dijit.Tooltip" connectId="latestAudit[<s:property value="#status.index"/>]">
											@<s:property value="rate"/>
										</span>
  	                      			</s:if>	                        		
                        		</td>
                       		</tr>
                       	</s:else>	
                    </s:if> 
                    <s:else>	
                    <tr>
                        <td nowrap="nowrap" style="padding-left: 30px;   padding-bottom: 10px;"><s:text name="%{getMessageKey(name)}"/></td>
                   		<td nowrap="nowrap" style="padding-left: 10px;   padding-bottom: 10px;text-align:right;" class="numeric">
                    		<span id="dealerAudit[<s:property value="#status.index"/>]" >
                    			<s:property value="#setDealerAudit.baseAmount"/>
                    		</span>
                    		<s:if test="#setDealerAudit.rate != null">
                     			<span dojoType="dijit.Tooltip" connectId="dealerAudit[<s:property value="#status.index"/>]">
									@<s:property value="#setDealerAudit.rate"/>
								</span>
                    		</s:if>				
                   		</td>
                   		<td nowrap="nowrap" style="padding-left: 10px;   padding-bottom: 10px;text-align:right;" class="numeric">
                    		<span id="latestAudit[<s:property value="#status.index"/>]">
                    			<s:property value="baseAmount"/>
                    		</span>
                    		<s:if test="rate != null">
                     			<span dojoType="dijit.Tooltip" connectId="latestAudit[<s:property value="#status.index"/>]">
									@<s:property value="rate"/>
								</span>
                    		</s:if>	                        		
                   		</td>
                    </tr>
                    </s:else>
                    <s:if test="!modifiers.empty">
                        <s:iterator value="modifiers" status="latestAuditStatus" id="modifier">
                        <tr>
                            <td nowrap="nowrap" style="padding-left:30px;   padding-bottom: 10px;"><s:property value="paymentVariable.displayName"/></td>
                            <td nowrap="nowrap" style="padding-left: 10px;   padding-bottom: 10px;text-align:right;" class="numeric">
                                <s:if test="#setDealerAudit.modifierMap[#modifier.name] != null">
           		        			<span id="dealerAuditModifier[<s:property value="#latestAuditStatus.index"/>]">
										<s:property value="#setDealerAudit.modifierMap[#modifier.name].value"/>
									</span>
  	                      			<span dojoType="dijit.Tooltip" connectId="dealerAuditModifier[<s:property value="#latestAuditStatus.index"/>]">
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
          		        		<span id="latestAuditModifier[<s:property value="#latestAuditStatus.index"/>]">
									<s:property value="value"/>
								</span>
 	                      		<span dojoType="dijit.Tooltip" connectId="latestAuditModifier[<s:property value="#latestAuditStatus.index"/>]">
									<s:if test="#modifier.isFlatRate">
										<s:property value="modifierPercentage"/>
									</s:if>
									<s:else>
										<s:property value="modifierPercentage"/>%											
									</s:else>
								</span>	                             	
                            </td>
                        </tr>
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
                     <tr class="total">
                        <td nowrap="nowrap" style="padding-left: 10px;   padding-bottom: 10px;"><s:text name="label.common.total"/> <s:text name="%{getMessageKey(name)}"/></td>
                        <td nowrap="nowrap" style="padding-left: 10px;   padding-bottom: 10px;text-align:right;" class="numeric"><s:property value="#setDealerAudit.groupTotal"/></td>
                        <td nowrap="nowrap" style="padding-left: 10px;   padding-bottom: 10px;text-align:right;" class="numeric"><s:property value="groupTotal"/></td>
                        <td nowrap="nowrap" style="padding-left: 10px;   padding-bottom: 10px;text-align:right;" class="numeric"><s:property value="acceptedCpTotal"/></td>
                        <td nowrap="nowrap" style="padding-left: 10px;   padding-bottom: 10px;text-align:right;" class="numeric">
                            <s:property value="percentageAcceptance"/>%
                        </td>
                        <td class="numeric" nowrap="nowrap" style="text-align:right;"><s:property value="acceptedTotal"/></td>                       
                    </tr>     	
                </s:if>
            </s:iterator>
             <s:iterator value="claim.payment.lineItemGroups" status="status">
                <s:if test="claim.state.state=='Service Manager Response'">
                    <s:set name="setDealerPayment" value="%{claim.getPaymentForClaimState('Service Manager Review')}"/>
                </s:if>
                <s:else>
                    <s:set name="setDealerPayment" value="%{claim.getPaymentForDealerAudit()}"/> 
                </s:else>
				<s:set name="setDealerAudit" value="#setDealerPayment.getLineItemGroup(#name)"/>
                    <s:if test="name=='Claim Amount'">
                        <tr>
                            <td nowrap="nowrap" style="padding-left: 30px;   padding-bottom: 10px;"><s:text name="%{getMessageKey(name)}"/></td>
                            <td nowrap="nowrap" style="padding-left: 10px;   padding-bottom: 10px;text-align:right;" class="numeric"><s:property value="#setDealerAudit.baseAmount"/></td>
                            <td nowrap="nowrap" style="padding-left: 10px;   padding-bottom: 10px;text-align:right;" class="numeric"><s:property value="#claimAmountDisplayAudit.baseAmount"/></td>
                            <td nowrap="nowrap" style="padding-left: 10px;   padding-bottom: 10px;text-align:right;" class="numeric"></td>
                            <td nowrap="nowrap" style="padding-left: 10px;   padding-bottom: 10px;text-align:right;" class="numeric"></td>
                            <s:if test="#isLineItemChanged">
                              <td class="numeric" nowrap="nowrap" style="text-align:right;"><s:property value="baseAmount"/></td>                            
                            </s:if>
                        </tr>
                        <s:if test="!#setDealerAudit.modifiers.empty">
                           <s:iterator value="#setDealerAudit.modifiers" status="dealerAuditStatus" id="modifier">
	                          <tr>
		                            <td nowrap="nowrap" style="padding-left: 30px;   padding-bottom: 10px;"><s:property value="paymentVariable.displayName"/></td>
		                            <td nowrap="nowrap" style="padding-left: 10px;   padding-bottom: 10px;text-align:right;" class="numeric">
		          		        		<span id="dealerAuditTotalModifier[<s:property value="#dealerAuditStatus.index"/>]">
											<s:property value="value"/>
										</span>
		 	                      		<span dojoType="dijit.Tooltip" connectId="dealerAuditTotalModifier[<s:property value="#dealerAuditStatus.index"/>]">
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
		           		        			<span id="claimAmountModifier[<s:property value="#dealerAuditStatus.index"/>]">
												<s:property value="#claimAmountDisplayAudit.modifierMap[#modifier.name].value"/>
											</span>
		  	                      			<span dojoType="dijit.Tooltip" connectId="claimAmountModifier[<s:property value="#dealerAuditStatus.index"/>]">
												<s:if test="#claimAmountDisplayAudit.modifierMap[#modifier.name].isFlatRate">
													<s:property value="#claimAmountDisplayAudit.modifierMap[#modifier.name].modifierPercentage"/>
												</s:if>
												<s:else>
													<s:property value="#claimAmountDisplayAudit.modifierMap[#modifier.name].modifierPercentage"/>%											
												</s:else>
											</span>	                                    
		                                </td>
		                            </s:if>
		                            <td nowrap="nowrap" style="padding-left: 10px;   padding-bottom: 10px;text-align:right;" class="numeric"></td>
		                            <td nowrap="nowrap" style="padding-left: 10px;   padding-bottom: 10px;text-align:right;" class="numeric"></td>
		                            <s:if test="#isLineItemChanged">
			                            <td class="numeric" nowrap="nowrap" style="text-align:right;">
			                                <s:if test="modifierMap[#modifier.name] != null">
			           		        			<span id="latestAuditTotalModifier[<s:property value="#dealerAuditStatus.index"/>]">
													<s:property value="modifierMap[#modifier.name].value"/>
												</span>
			  	                      			<span dojoType="dijit.Tooltip" connectId="latestAuditTotalModifier[<s:property value="#dealerAuditStatus.index"/>]">
													<s:if test="modifierMap[#modifier.name].isFlatRate">
														<s:property value="modifierMap[#modifier.name].modifierPercentage"/>
													</s:if>
													<s:else>
														<s:property value="modifierMap[#modifier.name].modifierPercentage"/>%											
													</s:else>
												</span>	                                    
			                                </s:if>
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
	           		        			<span id="claimAmountModifier1[<s:property value="#latestAuditStatus.index"/>]">
											<s:property value="#claimAmountDisplayAudit.modifierMap[#modifier.name].value"/>
										</span>
	  	                      			<span dojoType="dijit.Tooltip" connectId="claimAmountModifier1[<s:property value="#latestAuditStatus.index"/>]">
											<s:if test="#claimAmountDisplayAudit.modifierMap[#modifier.name].isFlatRate">
												<s:property value="#claimAmountDisplayAudit.modifierMap[#modifier.name].modifierPercentage"/>
											</s:if>
											<s:else>
												<s:property value="#claimAmountDisplayAudit.modifierMap[#modifier.name].modifierPercentage"/>%											
											</s:else>
										</span>	                                    
	                                </td>
	                            </s:if>
	                            <td nowrap="nowrap" style="padding-left: 10px;   padding-bottom: 10px;text-align:right;" class="numeric"></td>
	                            <td nowrap="nowrap" style="padding-left: 10px;   padding-bottom: 10px;text-align:right;" class="numeric"></td>
	                            <s:if test="#isLineItemChanged">
	                            <td class="numeric" nowrap="nowrap" style="text-align:right;">
	                                <s:if test="modifierMap[#modifier.name] != null">
	                                    <s:property value="modifierMap[#modifier.name].value"/>
	           		        			<span id="latestAuditTotalModifier1[<s:property value="#latestAuditStatus.index"/>]">
											<s:property value="modifierMap[#modifier.name].value"/>
										</span>
	  	                      			<span dojoType="dijit.Tooltip" connectId="latestAuditTotalModifier1[<s:property value="#latestAuditStatus.index"/>]">
											<s:if test="modifierMap[#modifier.name].isFlatRate">
												<s:property value="modifierMap[#modifier.name].modifierPercentage"/>
											</s:if>
											<s:else>
												<s:property value="modifierMap[#modifier.name].modifierPercentage"/>%											
											</s:else>
										</span>	                                    
	                                </s:if>
	                            </td>
	                            </s:if>
                             </tr>
                             </s:if>                           
                          </s:iterator>
                        </s:if>
                        <tr class="total">
	                        <td nowrap="nowrap" style="padding-left: 10px;   padding-bottom: 10px;"><s:text name="label.common.total"/> <s:text name="%{getMessageKey(name)}"/></td>
	                        <td nowrap="nowrap" style="padding-left: 10px;   padding-bottom: 10px;text-align:right;" class="numeric"><s:property value="#setDealerAudit.groupTotal"/></td>
	                        <td nowrap="nowrap" style="padding-left: 10px;   padding-bottom: 10px;text-align:right;" class="numeric"><s:property value="#claimAmountDisplayAudit.groupTotal"/></td>
	                        <td nowrap="nowrap" style="padding-left: 10px;   padding-bottom: 10px;text-align:right;" class="numeric"><s:property value="acceptedCpTotal"/></td>
	                        <td nowrap="nowrap" style="padding-left: 10px;   padding-bottom: 10px;text-align:right;" class="numeric" >
	                            <s:if test="!#isLineItemChanged">
	                                <s:property value="percentageAcceptance"/>%
	                            </s:if>
	                        </td>
	                        <td class="numeric" nowrap="nowrap" style="text-align:right;"><s:property value="acceptedTotal"/></td>	                        
                        </tr>                    
                    </s:if>
            </s:iterator>
   			<s:if test="claim.payment.activeCreditMemo != null">
	            <tr>
	                <td style="padding-left: 30px;   padding-bottom: 10px;"><s:text  name="label.viewClaim.creditMemoNumber"/></td>
	                <td style="padding-left: 10px;   padding-bottom: 10px;text-align:right;" class="numeric">-</td>
	                <td nowrap="nowrap" style="padding-left: 10px;   padding-bottom: 10px;text-align:right;" class="numeric"></td>
	                <td nowrap="nowrap" style="padding-left: 10px;   padding-bottom: 10px;text-align:right;" class="numeric"></td>
	                <td nowrap="nowrap" style="padding-left: 10px;   padding-bottom: 10px;text-align:right;" class="numeric" ></td>
	                <td style="padding-left: 10px;   padding-bottom: 10px;text-align:right;" class="numeric"><s:property
	                        value="claim.payment.activeCreditMemo.creditMemoNumber"/></td>
	            </tr>
	
	            <tr>
	                <td style="padding-left: 30px;   padding-bottom: 10px;"><s:text name="label.viewClaim.tax"/></td>
	                <td style="padding-left: 10px;   padding-bottom: 10px;text-align:right;" class="numeric">-</td>
	                <td nowrap="nowrap" style="padding-left: 10px;   padding-bottom: 10px;text-align:right;" class="numeric"></td>
	                <td nowrap="nowrap" style="padding-left: 10px;   padding-bottom: 10px;text-align:right;" class="numeric"></td>
	                <td nowrap="nowrap" style="padding-left: 10px;   padding-bottom: 10px;text-align:right;" class="numeric" ></td>
	                <td style="padding-left: 10px;   padding-bottom: 10px;text-align:right;" class="numeric"><s:property
	                        value="claim.payment.activeCreditMemo.taxAmount"/></td>
	            </tr>
			</s:if>         	            
             <tr class="total">
                <td  style="padding-left: 10px; padding-bottom: 10px;text-align:left;"><s:text name="label.viewClaim.amountPaid"/></td>
                <td class="numeric" style="text-align:right;">-</td>
                <td nowrap="nowrap" style="padding-left: 10px;   padding-bottom: 10px;text-align:right;" class="numeric"></td>
                <td nowrap="nowrap" style="padding-left: 10px;   padding-bottom: 10px;text-align:right;" class="numeric"></td>
                <td nowrap="nowrap" style="padding-left: 10px;   padding-bottom: 10px;text-align:right;" class="numeric" ></td>
                <td class="numeric" style="text-align:left;"><s:property
                        value="claim.payment.totalAmountPaidAfterTax"/></td>
            </tr>

            <tr class="total">
	            <td colspan="2" style="text-align:right;">
	                <s:text name="claim.payment.netPercentageAccepted"/>
	            </td>
	            <td class="numeric" style="text-align:right;">
                    <s:property value="claim.netAcceptancePercentageForPayment"/>
                    %
	            </td>
	        </tr>
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
<div id="lowLeveltablepopup" style="display:none">
    <table id="percentage_payment" width="96%" >
          <thead>
              <tr>
                <th style="text-align:left;" width="15%" class="labelStyle"><s:text name="label.common.category"/></th>
                <th style="text-align:right;" width="10%" class="labelStyle">&nbsp;&nbsp;&nbsp;&nbsp;<s:text name="label.claim.amountAsked" /></th>
                <th style="text-align:right;" width="15%" class="labelStyle">&nbsp;&nbsp;&nbsp;&nbsp;<s:text name="label.claim.currentAmount" /></th>
                <th style="text-align:right;" width="15%" class="labelStyle">&nbsp;&nbsp;&nbsp;&nbsp;<s:text name="label.common.costPrice"/></th>
                <th style="text-align:center;" width="15%" class="labelStyle"><s:text name="label.newClaim.percentageAccepted.SMReview"/></th>
                <th style="text-align:center;" width="15%" class="labelStyle"><s:text name="label.newClaim.acceptedAmount.forSMR"/></th>               
            </tr>
         </thead>
         <tbody>
             <s:iterator value="claim.payment.lineItemGroups" status="status">
                <s:if test="claim.state.state=='Service Manager Response'">
                    <s:set name="setDealerPayment" value="%{claim.getPaymentForClaimState('Service Manager Review')}"/>
                </s:if>
                <s:else>
                    <s:set name="setDealerPayment" value="%{claim.getPaymentForDealerAudit()}"/> 
                </s:else>
				<s:set name="setDealerAudit" value="#setDealerPayment.getLineItemGroup(#name)"/>
                <s:if test="name != 'Claim Amount'">
                <tr class="total">
                        <td nowrap="nowrap" style="padding-left: 10px;   padding-bottom: 10px;"><s:text name="label.common.total"/> <s:text name="%{getMessageKey(name)}"/></td>
                        <td nowrap="nowrap" style="padding-left: 10px;   padding-bottom: 10px;text-align:right;" class="numeric"><s:property value="#setDealerAudit.groupTotal"/></td>
                        <td nowrap="nowrap" style="padding-left: 10px;   padding-bottom: 10px;text-align:right;" class="numeric"><s:property value="groupTotal"/></td>
                        <td nowrap="nowrap" style="padding-left: 10px;   padding-bottom: 10px;text-align:right;" class="numeric"><s:property value="acceptedCpTotal"/></td>
                        <td nowrap="nowrap" style="padding-left: 10px;   padding-bottom: 10px;text-align:right;" class="numeric">
                            <s:property value="percentageAcceptance"/>%
                        </td>
                        <td class="numeric" nowrap="nowrap" style="text-align:right;"><s:property value="acceptedTotal"/></td>                       
                    </tr>     	
                    </s:if>
                    </s:iterator>
                    <s:iterator value="claim.payment.lineItemGroups" status="status">
	             <s:if test="claim.state.state=='Service Manager Response'">
	                 <s:set name="setDealerAudit" value="%{getAuditForClaimState('Service Manager Review')}"/>
	             </s:if>
	             <s:else>
	                 <s:set name="setDealerAudit" value="dealerAudit"/> 
	             </s:else>
	             <s:set name="setLatestAudit" value="latestAudit"/>
             
                    <s:if test="name=='Claim Amount'">
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
                        </tr>                    
                    </s:if>
            </s:iterator>
                    
                    <tr class="total">
                <td  style="padding-left: 10px; padding-bottom: 10px;text-align:left;"><s:text name="label.viewClaim.amountPaid"/></td>
                <td class="numeric" style="text-align:right;">-</td>
                <td nowrap="nowrap" style="padding-left: 10px;   padding-bottom: 10px;text-align:right;" class="numeric"></td>
                <td nowrap="nowrap" style="padding-left: 10px;   padding-bottom: 10px;text-align:right;" class="numeric"></td>
                <td nowrap="nowrap" style="padding-left: 10px;   padding-bottom: 10px;text-align:right;" class="numeric" ></td>
                <td class="numeric" style="text-align:left;"><s:property
                        value="claim.payment.totalAmountPaidAfterTax"/></td>
            </tr>

            <tr class="total">
	            <td colspan="2" style="text-align:right;">
	                <s:text name="claim.payment.netPercentageAccepted"/>
	            </td>
	            <td class="numeric" style="text-align:right;">
                    <s:property value="claim.netAcceptancePercentageForPayment"/>
                   %
	            </td>
	        </tr>
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
</div>
</s:if>    

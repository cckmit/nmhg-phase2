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
<script type="text/javascript">
    dojo.require("dijit.Tooltip");
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
	document.getElementById('paymentLowLevel').className='clickable';
	document.getElementById('paymentDetailed').className='clickable';
	document.getElementById(idval).className='nonclickHighlite';

	if(idval=='paymentDetailed'){
		document.getElementById('detailedlevel').style.display='';
		document.getElementById('lowLeveltable').style.display='none';
	}
	if(idval=='paymentLowLevel'){
		document.getElementById('detailedlevel').style.display='none';
		document.getElementById('lowLeveltable').style.display='';
	}
}
</script>
<s:if test="!claim.payment.lineItemGroups.isEmpty()">
<div style="width:100%;overflow:auto">
<div class="labelStyle" style="margin:10px 0px 10px 5px;">
	<%-- <s:text name="message.claim.payment.selectLevel"/> --%>
	<span  style="display:none; padding-left:20px;"><a class="nonclickHighlite" onclick="paymentView(id);" id="paymentDetailed"><s:text name="message.claim.payment.detailed"/></a></span>
	<span  style="display:none; padding-left:20px;"><a class="clickable" onclick="paymentView(id);" id="paymentLowLevel"><s:text name="message.claim.payment.lowlevel"/></a></span>
	</div>
	<div id="detailedlevel">
    <table id="payment" width="96%" style="width:96%; margin-left:6px;">
        <thead>
            <tr>
                <th style="text-align:left;" class="labelStyle"><s:text name="label.common.category"/>:</th>
                 <th style="text-align:left;" class="labelStyle"><s:text name="label.newClaim.hrQty"/>:</th>
                <th style="text-align:right;" class="labelStyle"><s:text name="label.newClaim.amount"/>:</th>
             <%--     <s:if test="claim.payment.stateMandateActive && !claim.isGoodWillPolicy()">
                 	<th style="text-align:center;" class="labelStyle" id="stateMandateAmount"><s:text name="label.common.payment.stateMandateAmount"/></th>
                </s:if>	 --%>
            </tr>
        </thead>
        <tbody>
            <s:if test="fullyRejectedMultiCarClaim">
                <tr>
                    <td colspan="2" align="center">
                        <div style="padding: 5px 0 5px 0; color:red; font-weight: bold;">
                            <s:text name="message.multiCar.allSerialNoRejected" />
                        </div>
                    </td>
                </tr>
                <tr class="total">
                    <td class="labelStyle"><s:text name="label.multiCar.totalClaimAmount"/></td>
                     <td>
                       </td>
                    <td class="labelStyle" style="text-align:right;"><s:text name="label.common.zeroPointzero"/></td>
                </tr>
            </s:if>
            <s:else>
               <s:if test="loggedInUserADealer && claim.warrantyOrder==true">
                     <tr class="total">
                                              <td class="labelStyle"><s:text name="label.multiCar.totalClaimAmount"/></td>
                                               <td>
                       </td>
                                              <td class="labelStyle" style="text-align:right;"><s:text name="label.common.zeroPointzero"/></td>
                     </tr>
               </s:if>
               <s:else>
               <s:set var="counter" value="0"/> 
               <s:set var="othersCounter" value="0"/>
                        <s:iterator value="claim.payment.lineItemGroups" status="ligstatus">
                        <s:set name="lineItem" value="%{claim.payment.getLineItemGroup(name)}"/>
                        <s:set name="travelByHrs" value="%{claim.payment.getLineItemGroup('Travel by Hours')}"/>
                  		 <s:set name="travelAddHrs" value="%{claim.payment.getLineItemGroup('Additional Travel Hours')}"/> 
                		    <s:if test="name != 'Claim Amount'">
                                <s:if test="name=='Labor'">
                                        <s:if test="isLaborSplitEnabled()">
                                            <s:iterator value="forLaborSplitAudit" status="latestAuditStatus">
                                                <tr>
                                                    <td style="padding-left:30px;"><s:property value="name"/></td>
                                                    <td class="numeric" style="text-align:right;">
                                                        <span id="latestLaborAudit[<s:property value="#latestAuditStatus.index"/>]">
                                                            <s:property value="getPaymentDetailForSplitAudit()"/>
                                                        </span>
                                                        <span dojoType="dijit.Tooltip" connectId="latestLaborAudit[<s:property value="#latestAuditStatus.index"/>]">

                                                                @<s:property value="forLaborSplitAudit[#latestAuditStatus.index].laborRateForSplitAudit"/>
                                                        </span>
                                                    </td>
                                                </tr>
                                            </s:iterator>
                                        </s:if>
                                        <s:else>
                                            <tr>
                                                <td style="padding-left:10px;"><s:text name="%{getMessageKey(name)}"/></td>
                                                <%-- <td class="numeric" style="text-align:right;">
                                                    <span id="latestAudit[<s:property value="#ligstatus.index"/>]">
                                                        <s:property value="baseAmount"/>
                                                    </span>
                                                    <s:if test="rate != null">
                                                        <span dojoType="dijit.Tooltip" connectId="latestAudit[<s:property value="#ligstatus.index"/>]">
                                                            @<s:property value="rate"/>
                                                            </span>
                                                               <br>{@<s:property value="rate"/>}

                                                    </s:if>
                                                </td> --%>
                                            </tr>
                                            
                  <s:iterator value="individualLineItems" status="indItemStatus">                                   
                  	<tr>         
                  		<td nowrap="nowrap" style="padding-left: 30px;   padding-bottom: 10px;text-align:left;" class="numeric"> 
                  		 <s:if test="claim.competitorModelBrand!=null" >           
                  		 <s:text name="message.competitor.model.claim.labor.jobcode"/> <%-- ( <s:text name="message.competitor.model.claim.labor.description"/>) --%>
                  		 </s:if>
                  		 <s:else>                  		                 		
                  		 <s:property value="serviceProcedureDefinition.code"/> ( <s:property value="serviceProcedureDefinition.description"/> )  
                  		 </s:else>            		 	
                  		   </td>
              			<td nowrap="nowrap" style="padding-left: 10px;   padding-bottom: 10px;text-align:left;" class="numeric"><s:property value="askedHrs"/>              			              			
						<td nowrap="nowrap" style="padding-left: 10px;   padding-bottom: 10px;text-align:right;" class="numeric"><s:property value="baseAmount"/> </td>
					<%-- 	<s:if test="claim.payment.stateMandateActive && !claim.isGoodWillPolicy()">
							<td nowrap="nowrap"	style="padding-left: 10px; padding-bottom: 10px; text-align: center;" class="numeric"><s:property value="stateMandateAmount" />	</td>
						</s:if>			
						     --%>
 					</tr>                   
                              
                </s:iterator>
                
                 <tr>
                <td nowrap="nowrap"	style="padding-left: 60px; padding-bottom: 10px; text-align: left;" class="numeric"><s:text name="label.common.rate"/>:</td>		
                <td nowrap="nowrap" style="padding-left: 10px;   padding-bottom: 10px; text-align: right;"> </td>		
                <td nowrap="nowrap"	style="padding-left: 10px; padding-bottom: 10px; text-align: right;" class="numeric"><s:if test="buConfigAMER"> <s:property value="percentageApplicable"/>% =</s:if> <s:else>@</s:else>  <s:property value="rate"/>	</td>
                <%--  <s:if test="claim.payment.stateMandateActive && !claim.isGoodWillPolicy()">
						   <td nowrap="nowrap" style="padding-left: 10px;   padding-bottom: 10px; text-align: center;"><s:property value="stateMandateRatePercentage"/>= @<s:property value="stateMandateRate"/></td>
				 </s:if> --%>		
                </tr>              
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
                   	<%-- 		<td nowrap="nowrap" style="padding-left: 10px;text-align:right;">
                   			<span id="dealerAudit[<s:property value="#status.index"/>]" >
                    			<s:property value="#setDealerAudit.baseAmount"/>
                    		</span>
                    		<s:if test="#setDealerAudit.rate != null">
                     			<span dojoType="dijit.Tooltip" connectId="dealerAudit[<s:property value="#status.index"/>]">
									@<s:property value="#setDealerAudit.rate"/>
								</span>
								<br>{@<s:property value="#setDealerAudit.rate"/>}
                    		</s:if>				
                   		</td> --%>
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
                                
                                
                                
                                
                                
                                    <%-- <tr>
                                        <td style="padding-left:10px;"><s:text name="%{getMessageKey(name)}"/></td>
                                          <s:if test="name=='Oem Parts' || name=='Non Oem Parts' ||name=='Labor'">  
                       					  </s:if>
                         				<s:else>
                         				<td class="numeric" style="text-align:right;"></td>
                                        <td class="numeric" style="text-align:right;">
                                            <span id="latestAudit[<s:property value="#ligstatus.index"/>]">
                                                <s:property value="baseAmount"/>
                                            </span>
                                            <s:if test="rate != null">
                                                <span dojoType="dijit.Tooltip" connectId="latestAudit[<s:property value="#ligstatus.index"/>]">
                                                    @<s:property value="rate"/>
                                                </span>
                                                <br>{@<s:property value="rate"/>}
                                            </s:if>
                                        </td>
                                        </s:else>
                                    </tr> --%>
                                    
                                    
                                    
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
              			<td nowrap="nowrap" style="padding-left: 10px;   padding-bottom: 10px;text-align:left;" class="numeric"><s:property value="askedQty"/> 
              			<td nowrap="nowrap" style="padding-left: 10px;   padding-bottom: 10px;text-align:right;" class="numeric"><s:property value="baseAmount"/> </td>
              			<%-- <s:if test="claim.payment.stateMandateActive && !claim.isGoodWillPolicy()">
						<td nowrap="nowrap"	style="padding-left: 10px; padding-bottom: 10px; text-align: center;" class="numeric"><s:property value="stateMandateAmount" />	</td>
						</s:if>	 --%>	
 					</tr>                                         
                 </s:iterator>
                                    
                                    
                                </s:else>
                                <s:if test="!modifiers.empty">
                                    <s:iterator value="modifiers" status="status" id="modifier">
                                        <tr>
                                            <td style="padding-left:30px;"><s:property value="paymentVariable.displayName"/>&nbsp;&nbsp;<s:property value="percentageConfigured"/>%</td>
                                             <td class="numeric" style="text-align:right;"></td>
                                            <td class="numeric" style="text-align:right;">
                                                <span id="latestAuditModifier_[<s:property value="#ligstatus.index"/>]_[<s:property value="#status.index"/>]">
                                                    <s:property value="modifierMap[#modifier.name].value"/>
                                                </span>
                                                <span dojoType="dijit.Tooltip" connectId="latestAuditModifier_[<s:property value="#ligstatus.index"/>]_[<s:property value="#status.index"/>]">
                                                    <s:if test="modifierMap[#modifier.name].isFlatRate">
                                                        <s:property value="modifierMap[#modifier.name].modifierPercentage"/>
                                                    </s:if>
                                                    <s:else>
                                                        <s:property value="modifierMap[#modifier.name].modifierPercentage"/>%
                                                    </s:else>
                                                </span>
                                            </td>
                                          <%--   <s:if test="#lineItem.name!='Oem Parts' ">
                                              <td nowrap="nowrap" style="padding-left: 10px;   padding-bottom: 10px;text-align:center;">
                                             <s:property value="stateMandateAmount"/>
                                             </td>
                                             </s:if> --%>
                                        </tr>
                                        
                               <%--        <s:if test="claim.payment.stateMandateActive && !claim.isGoodWillPolicy() && #lineItem.name=='Oem Parts' ">       
						                  <tr> 
						                       <td nowrap="nowrap" style="padding-left: 30px;   padding-bottom: 10px;">(<s:text name="label.payment.stateMandate"/>)<s:property value="paymentVariable.displayName"/>&nbsp;&nbsp;<s:property value="SMandateModifierPercent"/>%</td>
						                       <td nowrap="nowrap" style="padding-left: 30px;   padding-bottom: 10px;"></td>
						                       <td nowrap="nowrap" style="padding-left: 10px;   padding-bottom: 10px;text-align:right;" class="numeric">    </td>         
											   <td nowrap="nowrap" style="padding-left: 10px;   padding-bottom: 10px;text-align:center;" class="numeric"><s:property value="stateMandateAmount"/></td>				
										  </tr>    
           							</s:if>  --%>
                    
                                    </s:iterator>
                                </s:if>
                                
                                                                 
                               <!--  Travel Rate changes -->
                        <s:if test="name=='Travel'">  
                          <s:if test="#travelAddHrs!=null||#travelByHrs!=null">                 
                       <s:if test="#travelAddHrs!=null">
                        <s:set name="travelRate" value="#travelAddHrs" />
                        </s:if>
                          <s:else>
                           <s:set name="travelRate" value="#travelByHrs" />
                            </s:else>             
                 
                   			         <tr>
                			<td nowrap="nowrap"	style="padding-left: 60px; padding-bottom: 10px; text-align: left;" class="numeric"><s:text name="label.common.rate"/>:</td>		
                			<td nowrap="nowrap" style="padding-left: 10px;   padding-bottom: 10px; text-align: right;"> </td>		
               				 <td nowrap="nowrap"	style="padding-left: 10px; padding-bottom: 10px; text-align: right;" class="numeric">{<s:if test="!buConfigAMER">@</s:if><s:property value="#travelRate.rate"/>}	</td>			
               		<%--   <s:if test="claim.payment.stateMandateActive && !claim.isGoodWillPolicy()">
						   <td nowrap="nowrap" style="padding-left: 10px;   padding-bottom: 10px; text-align: center;"><s:property value="stateMandateRatePercentage"/>% @<s:property value="stateMandateRate"/></td>
					 </s:if> --%>						    
                </tr>
                </s:if>   
                 </s:if>  
                                                    <!-- Total code -->
                     <s:if test="name=='Others'||name=='Late Fee'||name=='Deductible'"> 
                  </s:if>
                  <s:else>
                     <s:if test="name=='Travel by Hours' || name=='Travel By Trip' ||name=='Additional Travel Hours'||name=='Item Freight And Duty' || name=='Handling Fee' ||name=='Transportation'||name=='Late Fee'"> 
                     <tr > 
                    <td style="padding-left:30px"> <s:if test="name!='Travel By Trip'&&name!='Late Fee'"><s:text name="label.common.total"/> </s:if> <s:text name="%{getMessageKey(name)}"/>:</td>
                     </s:if>
                     <s:else>                       
                     <tr class="total" >
                    <td class="labelStyle" style="padding-left:50px"><s:text name="label.common.total"/> <s:text name="%{getMessageKey(name)}"/></td>
                     </s:else>                              
                        <td  style="padding-left:10px;text-align:left;">
                        <!-- fix for SLMSPROD-1392 -->
                        	<s:if test="name=='Travel by Hours' || name=='Additional Travel Hours' || name=='Travel'"> 
                        		<s:property value="askedQtyHrs.replace('.',':')"/>	
                        	</s:if>
                        	<s:else>
                        		<s:property value="askedQtyHrs"/>	
                        	</s:else>
                        
                        </td>
                       <td  style="text-align:right;">
                       	<s:if test="name=='Travel By Trip'">
                       	<s:property value="askedQtyHrs"/>
                    		</s:if>
                    		<s:else>
                       <s:property value="groupTotal"/>
                       </s:else></td>
                         <%--    <s:if test="claim.payment.stateMandateActive && !claim.isGoodWillPolicy()">         
                         <td style="text-align:center;"><s:property value="groupTotalStateMandateAmount"/></td>
                         </s:if>  --%>
                     </tr>   
                  </s:else>                     
            
                        
                        
                            </s:if>
                        </s:iterator>
                        <s:iterator value="claim.payment.lineItemGroups">
                            <s:if test="name == 'Claim Amount'">
                               <%--  <tr >
                                    <td style="padding-left:30px;"><s:text name="%{getMessageKey(name)}"/></td>
                                     <td class="numeric" style="text-align:right;"></td>
                                    <td class="numeric" style="text-align:right;"><s:property
                                            value="baseAmount"/></td>
                                  <s:if test="claim.payment.stateMandateActive && !claim.isGoodWillPolicy()">         
                         		<td style="text-align:center;"><s:property value="groupTotalStateMandateAmount"/></td>
                         	   </s:if>
                                </tr> --%>
                                <s:if test="!modifiers.empty">
                                    <s:iterator value="modifiers" status="status" id="modifier">
                                        <tr>
                                            <td style="padding-left:30px;"><s:property value="paymentVariable.displayName"/></td>
                                             <td class="numeric" style="text-align:right;"></td>
                                            <td class="numeric" style="text-align:right;">
                                                <span id="latestAuditTotalModifier[<s:property value="#status.index"/>]">
                                                    <s:property value="modifierMap[#modifier.name].value"/>
                                                </span>
                                                <span dojoType="dijit.Tooltip" connectId="latestAuditTotalModifier[<s:property value="#status.index"/>]">
                                                    <s:if test="modifierMap[#modifier.name].isFlatRate">
                                                        <s:property value="modifierMap[#modifier.name].modifierPercentage"/>
                                                    </s:if>
                                                    <s:else>
                                                        <s:property value="modifierMap[#modifier.name].modifierPercentage"/>%
                                                    </s:else>
                                                </span>
                                            </td>
                                        </tr>
                                    </s:iterator>
                                </s:if>
                                <tr class="total">
                                    <td class="labelStyle"><s:text name="label.common.total"/> <s:text name="%{getMessageKey(name)}"/></td>
                                    <td class="labelStyle" style="text-align:right;"></td>
                                    <td class="labelStyle" style="text-align:right;"><s:property
                                            value="groupTotal"/></td>
                             <%--                <s:if test="claim.payment.stateMandateActive && !claim.isGoodWillPolicy()">         
                         		<td style="text-align:center;"><s:property value="groupTotalStateMandateAmount"/></td>
                         	   </s:if> --%>
                                </tr>
                            </s:if>
                        </s:iterator>
               </s:else>
             </s:else>
        </tbody>
    </table>
    </div>
    <div id="lowLeveltable" style="display:none">
    <table id="payment" width="96%" style="width:96%; margin-left:6px;">
        <thead>
            <tr>
                <th style="text-align:left;"><s:text name="label.common.category"/>:</th>
                <th style="text-align:right;"><s:text name="label.newClaim.amount"/>:</th>
            </tr>
        </thead>
        <tbody>
            <s:if test="fullyRejectedMultiCarClaim">
	            <tr class="total">
                    <td><s:text name="label.multiCar.totalClaimAmount"/></td>
                    <td class="numeric" style="text-align:right;"><s:text name="label.common.zeroPointzero"/></td>
                </tr>
            </s:if>
            <s:else>
               <s:if test="!loggedInUserAnInternalUser && claim.warrantyOrder==true">
                      <tr class="total">
                             <td class="labelStyle"><s:text name="label.multiCar.totalClaimAmount"/></td>
                             <td class="labelStyle" style="text-align:right;"><s:text name="label.common.zeroPointzero"/></td>
                           <%--   <s:if test="claim.payment.stateMandateActive && !claim.isGoodWillPolicy()">         
                         		<td style="text-align:center;"><s:property value="groupTotalStateMandateAmount"/></td>
                         	  </s:if> --%>
                      </tr>
               </s:if>
               <s:else>
                    <s:iterator value="claim.payment.lineItemGroups" status="ligstatus">

                        <s:if test="name != 'Claim Amount'">
                         <tr class="total">
                                <td class="labelStyle"><s:text name="label.common.total"/> <s:text name="%{getMessageKey(name)}"/></td>
                                <td class="labelStyle" style="text-align:right;"><s:property
                                        value="groupTotal"/></td>
                               <%--  <s:if test="claim.payment.stateMandateActive && !claim.isGoodWillPolicy()">         
                         		<td style="text-align:center;"><s:property value="groupTotalStateMandateAmount"/></td>
                         	   </s:if> --%>
                         </tr>
                        </s:if>
                    </s:iterator>
                    <s:iterator value="claim.payment.lineItemGroups">
                        <s:if test="name == 'Claim Amount'">
                            <tr class="total">
                                <td class="labelStyle"><s:text name="label.common.total"/> <s:text name="%{getMessageKey(name)}"/></td>
                                <td class="labelStyle" style="text-align:right;"><s:property
                                        value="groupTotal"/></td>
                            	<%-- <s:if test="claim.payment.stateMandateActive && !claim.isGoodWillPolicy()">         
                         		<td style="text-align:center;"><s:property value="groupTotalStateMandateAmount"/></td>
                         	   </s:if>  --%>
                           </tr>
                        </s:if>
                    </s:iterator>
                </s:else>
            </s:else>
          </tbody>
    </table>
    </div>
</div>
</s:if>

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
<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="u" uri="/ui-ext"%>
<%@taglib prefix="t" uri="twms"%>
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
dojo.addOnLoad(function() {
	enableCPAcceptanceReasonOnLoad();
	dojo.html.hide(dojo.byId('flat_cp_label'));
    dojo.html.show(dojo.byId('percentage_cp_label'));
var lineItemGroupSizess = <s:property value="task.claim.payment.lineItemGroups.size"/>;	

<s:if test="task.claim.payment.flatAmountApplied == true">
							if(document.getElementById('flat_cp_label') != null)
								dojo.html.show(dojo.byId('flat_cp_label'));
							dojo.html.hide(dojo.byId('percentage_cp_label'));
                           document.getElementById('flatAmount_processor_write').className='nonclickHighlite';
	                        document.getElementById('percentageAccepted_processor_write').className='clickable';
	                        for (var i = 0; i < lineItemGroupSizess; i++){
	                      if(document.getElementById('percentage_processor_write_'+i) != null)
		                		 document.getElementById('percentage_processor_write_'+i).style.display='none';
	                      if(document.getElementById('flat_processor_write_'+i) != null)
		                 document.getElementById('flat_processor_write_'+i).style.display='';
		             	if(document.getElementById('percentage_processor_modifier_write_'+i) != null)
		                    document.getElementById('percentage_processor_modifier_write_'+i).style.display='none';
		            	if(document.getElementById('flat_processor_modifier_write_'+i) != null)
		                    document.getElementById('flat_processor_modifier_write_'+i).style.display='';
		                 if(document.getElementById('percentage_cp_write_'+i) != null){
		                 	document.getElementById('percentage_cp_write_'+i).style.display='none';
		                 }
		                 if(document.getElementById('flat_cp_write_'+i) != null){
		                	document.getElementById('flat_cp_write_'+i).style.display='';
						}
		               }
	                        
	                     // NMHGSLMS425 Changes
	                        <s:iterator value="task.claim.payment.lineItemGroups" status="status">
	                      		 	var lineItemIndex= <s:property value='#status.index'/>    
	                        	<s:iterator value="individualLineItems" status="indItemStatus">
	                        		var individualLineItemIndex= <s:property value='#indItemStatus.index'/>          
	                      				  document.getElementById('percentage_processor_write_'+lineItemIndex+'_'+individualLineItemIndex).style.display='none';
	               					  document.getElementById('flat_processor_write_'+lineItemIndex+'_'+individualLineItemIndex).style.display='';         
	                        	</s:iterator>     
	                        </s:iterator>
	                        
	                        
	                        
	                        
	                        
                           </s:if>
 });

function paymentView(idval){

	document.getElementById('paymentLowLevel_processor_write').className='clickable';
	document.getElementById('paymentDetailed_processor_write').className='clickable';
	document.getElementById(idval).className='nonclickHighlite';

	if(idval=='paymentDetailed_processor_write'){
		document.getElementById('detailedlevel_processor_write').style.display='';
		document.getElementById('lowLeveltable_processor_write').style.display='none';
	}

	if(idval=='paymentLowLevel_processor_write'){
		document.getElementById('detailedlevel_processor_write').style.display='none';
		document.getElementById('lowLeveltable_processor_write').style.display='';
	}
}

function amountView(idval){
	var lineItemGroupSizes = <s:property value="task.claim.payment.lineItemGroups.size"/>;
	document.getElementById('flatAmount_processor_write').className='clickable';
	document.getElementById('percentageAccepted_processor_write').className='clickable';
	document.getElementById(idval).className='nonclickHighlite';
	if(idval=='percentageAccepted_processor_write'){
		enableCPAcceptanceReason('percentage');
		if(document.getElementById('flat_cp_label') != null)
			dojo.html.hide(dojo.byId('flat_cp_label'));
		dojo.html.show(dojo.byId('percentage_cp_label'));
	dojo.byId('flatAmountApp').value=false;
	 for (var i = 0; i < lineItemGroupSizes; i++){
		 if(document.getElementById('percentage_processor_write_'+i) != null)
			document.getElementById('percentage_processor_write_'+i).style.display='';
		 if(document.getElementById('flat_processor_write_'+i) != null)
		   document.getElementById('flat_processor_write_'+i).style.display='none';
		if(document.getElementById('percentage_processor_modifier_write_'+i) != null)
		   document.getElementById('percentage_processor_modifier_write_'+i).style.display='';
		if(document.getElementById('flat_processor_modifier_write_'+i) != null)
		   document.getElementById('flat_processor_modifier_write_'+i).style.display='none';
		if(document.getElementById('percentage_cp_write_'+i) != null)
			document.getElementById('percentage_cp_write_'+i).style.display='';
		if(document.getElementById('flat_cp_write_'+i) != null)
			document.getElementById('flat_cp_write_'+i).style.display='none';
		}
	// NMHGSLMS425 Changes
     <s:iterator value="task.claim.payment.lineItemGroups" status="status">
   		 	var lineItemIndex= <s:property value='#status.index'/>    
     	<s:iterator value="individualLineItems" status="indItemStatus">
     		var individualLineItemIndex= <s:property value='#indItemStatus.index'/>          
   				  document.getElementById('percentage_processor_write_'+lineItemIndex+'_'+individualLineItemIndex).style.display='';
				  document.getElementById('flat_processor_write_'+lineItemIndex+'_'+individualLineItemIndex).style.display='none';         
     	</s:iterator>     
     </s:iterator>
	
	}

	if(idval=='flatAmount_processor_write'){
		enableCPAcceptanceReason('flat');
		dojo.byId('flatAmountApp').value=true;
		for (var i = 0; i < lineItemGroupSizes; i++){
			
			dojo.html.show(dojo.byId('flat_cp_label'));
			dojo.html.hide(dojo.byId('percentage_cp_label'));
			if(document.getElementById('percentage_processor_write_'+i) != null)
			document.getElementById('percentage_processor_write_'+i).style.display='none';
			if(document.getElementById('flat_processor_write_'+i) != null)
			document.getElementById('flat_processor_write_'+i).style.display='';
			if(document.getElementById('percentage_processor_modifier_write_'+i) != null)
			 	document.getElementById('percentage_processor_modifier_write_'+i).style.display='none';
			if(document.getElementById('flat_processor_modifier_write_'+i) != null)
				document.getElementById('flat_processor_modifier_write_'+i).style.display='';
			if(document.getElementById('percentage_cp_write_'+i) != null)
				document.getElementById('percentage_cp_write_'+i).style.display='none';
			if(document.getElementById('flat_cp_write_'+i) != null)
				document.getElementById('flat_cp_write_'+i).style.display='';
			}		
		// NMHGSLMS425 Changes
         <s:iterator value="task.claim.payment.lineItemGroups" status="status">
       		 	var lineItemIndex= <s:property value='#status.index'/>    
         	<s:iterator value="individualLineItems" status="indItemStatus">
         		var individualLineItemIndex= <s:property value='#indItemStatus.index'/>          
       				  document.getElementById('percentage_processor_write_'+lineItemIndex+'_'+individualLineItemIndex).style.display='none';
					  document.getElementById('flat_processor_write_'+lineItemIndex+'_'+individualLineItemIndex).style.display='';         
         	</s:iterator>     
         </s:iterator>
         
         
         
	
		
	}
}
</script>
<div dojoType="twms.widget.TitlePane" id="paymentDetails" title="<s:text name="title.viewClaim.paymentDetails"/>"
                labelNodeClass="section_header">
<u:actionResults/>
<p style="text-indent: 20px; margin-left: 10px; margin-top: 10px; margin-bottom: 20px; padding-top: 3px; font-weight: bold; background: url('image/warningsImg.gif') no-repeat;">
<s:text name="label.newClaim.percentageAcceptedNote"/>
</p>
<p style="text-indent: 20px; margin-left: 10px; margin-top: 10px; margin-bottom: 20px; padding-top: 3px; font-weight: bold; background: url('image/warningsImg.gif') no-repeat;">
<a id="refreshPayment"><s:text name="label.common.refreshPayment"/>  </a>
</p>
<script type="text/javascript">
    var claimAmountRow = <s:property value="%{task.claim.payment.lineItemGroups.size-1}"/>;
    dojo.addOnLoad(function(){
       dojo.connect(dojo.byId("refreshPayment"),"onclick",function(event){
    	   /* checkSlectedCheckBox(); */
           var form = document.getElementById("claim_form");
           var calculationIndicator = "<center><img src=\"image/indicator.gif\" class=\"indicator\"/><s:text name="label.common.calculatingPayment"/></center>";
               dojo.stopEvent(event);
                var paymentSection = dijit.byId("paymentDetailsEditable");
                var content = {};
           form.action="refresh_payment.action";
                dojo.xhrPost({
                    form: form,
                    content: content,
                    load: function(data) {                    
                        paymentSection.setContent(data);
                        form.action="claim_submit.action";
                        dojo.publish("/refresh/paymentSection");
                    },
                    error: function(error) {
                    }
                });
           paymentSection.setContent(calculationIndicator);
       });
    });
    
    
    
  /*  function  checkSlectedCheckBox()
    {
	   var acceptedAmount = <s:property value="task.claim.payment.getLineItemGroup('Claim Amount').acceptedTotal.breachEncapsulationOfAmount().intValue()"/>;	
	   var groupTotalStateMandateAmount = <s:property value="task.claim.payment.getLineItemGroup('Claim Amount').groupTotalStateMandateAmount.breachEncapsulationOfAmount().intValue()"/>;
	   var totalAccptedChkBox;
	  var maxAmountSelected;
	   console.debug(acceptedAmount);
	   if(dojo.byId('totalPercentageAcceptanceCheckBox')!=null && dojo.byId('totalPercentageAcceptanceCheckBox').checked == true)
		   {
		   totalAccptedChkBox=true;
		   }
	   else
		   {
		   totalAccptedChkBox=false;
		   }
	   if(acceptedAmount<=groupTotalStateMandateAmount)
		   {
		   if(totalAccptedChkBox)
		   		maxAmountSelected=false;
		   else
			   maxAmountSelected=true;
		   }
	   else
		   {
		   if(totalAccptedChkBox)
		   		maxAmountSelected=true;
		   else
			   maxAmountSelected=false;
		   }
	   console.debug(maxAmountSelected);
	   dojo.byId('isMaxAmountSelected').value=maxAmountSelected;
    } */
</script>
<s:if test="!task.claim.payment.lineItemGroups.isEmpty()">
    <s:set name="claimAmountDisplayAudit" value="%{getLineItemGroupAuditForGlobalLevel(task.claim)}"/>
    <s:set name="isLineItemChanged" value="%{isLineItemPercentageChanged(task.claim)}"/>
    <s:set name="isLineItemForCPChanged" value="%{isLineItemForCPPercentageChanged(task.claim)}"/>
	<div style="overflow:auto; width:100%">
	<div class="labelStyle" style="margin:10px 0px 10px 5px;">
	<%-- <s:text name="message.claim.payment.selectLevel"/> --%>
	<span  style="display:none; padding-left:20px;"><a class="nonclickHighlite" onclick="paymentView(id);" id="paymentDetailed_processor_write"><s:text name="message.claim.payment.detailed"/></a></span>
	<span  style="display:none; padding-left:20px;"><a class="clickable" onclick="paymentView(id);" id="paymentLowLevel_processor_write"><s:text name="message.claim.payment.lowlevel"/></a></span>

	</div>
	<div id="detailedlevel_processor_write">
    <table id="percentage_payment" style="width:95%">
        <thead>
            <tr>
                <th style="text-align:left;" width="10%" class="labelStyle"><s:text name="label.common.category"/></th>
                <th style="text-align:right;" width="15%" class="labelStyle">&nbsp;&nbsp;&nbsp;&nbsp;<s:text name="label.common.payment.reqQty"/></th> 
                <th style="text-align:right;" width="10%" class="labelStyle">&nbsp;&nbsp;&nbsp;&nbsp;<s:text name="label.claim.amountAsked" /></th>
                <th style="text-align:right;" width="10%" class="labelStyle">&nbsp;&nbsp;&nbsp;&nbsp;<s:text name="label.common.currentAmount" /></th>
                <th style="text-align:right;" width="10%" class="labelStyle">&nbsp;&nbsp;&nbsp;&nbsp;<s:text name="label.common.payment.acceptedQty"/></th> 
                <th style="text-align:right;" width="20%" class="labelStyle">
                	<a class="clickable" onclick="amountView(id);" id="flatAmount_processor_write"> <s:text name="label.newClaim.flatAmount.forWarranty"/></a>
					<a class="nonclickHighlite" onclick="amountView(id);" id="percentageAccepted_processor_write"> <s:text name="label.newClaim.percentageAccepted.forWarranty"/></a>
                <th style="text-align:right;" width="10%" class="labelStyle"><s:text name="label.newClaim.acceptedAmount.for.warranty"/></th>
                <s:if test="task.claim.payment.stateMandateActive && !task.claim.isGoodWillPolicy()">
                 	<th style="text-align:center;" width="15%" class="labelStyle" id="stateMandateAmount"><s:text name="label.common.payment.stateMandateAmount"/></th>
                </s:if>	
                
           </tr>
        </thead>
        <tbody >
         	 <s:set var="counter" value="0"/> 
         	 <s:set var="othersCounter" value="0"/>
         	  <s:set var="counterForDeductible" value="0"/> 
            <s:iterator value="task.claim.payment.lineItemGroups" status="status">
                <s:set name="setDealerPayment" value="%{task.claim.getPaymentForDealerAudit()}"/>
                <s:set name="setDealerAudit" value="#setDealerPayment.getLineItemGroup(name)"/>
                <s:set name="lineItem" value="%{task.claim.payment.getLineItemGroup(name)}"/>
                 <s:set name="totalLineItem" value="%{task.claim.payment.getLineItemGroup('Claim Amount')}"/>
                  <s:set name="travelByHrs" value="%{task.claim.payment.getLineItemGroup('Travel by Hours')}"/>
                   <s:set name="travelAddHrs" value="%{task.claim.payment.getLineItemGroup('Additional Travel Hours')}"/>                       
                	<s:set name="TravelByHrsAudit" value="#setDealerPayment.getLineItemGroup('Travel by Hours')"/>
                	<s:set name="TravelByAddHrsAudit" value="#setDealerPayment.getLineItemGroup('Additional Travel Hours')"/>
                <s:if test="name != 'Claim Amount'">
                    <!--This logic has been added only if labor split Bu based configuration is enabled.
                    1)If the latestAudit has more laborsplit as compared to dealerAudit then we iterate
                      over the latestAudit. Variable "doesExists" checks if the dealerAudit has laborsplit
                      same as that of latestAudit .If it is not present then we display only latestAudit payment
                    2)If the dealerAudit has more laborsplit as compared to latestAudit then we iterate
                      over the dealerAudit. Variable "doesExists" checks if the latestAudit has laborsplit
                      same as that of dealerAudit .If it is not present then we display only dealerAudit payment
                    -->
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
                          <td nowrap="nowrap" style="padding-left: 10px;   padding-bottom: 10px;"><s:text name="%{getMessageKey(name)}"/></td>
                         <td nowrap="nowrap" style="padding-left: 30px;   padding-bottom: 10px;"> <%-- <s:property value="#setDealerAudit.id"/> --%> </td>
                            <td nowrap="nowrap" style="padding-left: 30px;   padding-bottom: 10px;"><%--  <s:property value="id"/> --%></td>
                        </tr>
            <!--    NMHGSLMS-425 Labor Changes   -->       
                   <s:iterator value="individualLineItems" status="indItemStatus">                                   
                  	<tr>         
                  		<td nowrap="nowrap" style="padding-left: 30px;   padding-bottom: 10px;text-align:left;" class="numeric">     
                  		 <s:if test="task.claim.competitorModelBrand!=null" >           
                  		 <s:text name="message.competitor.model.claim.labor.jobcode"/> <%-- ( <s:text name="message.competitor.model.claim.labor.description"/>) --%>
                  		 </s:if>
                  		 <s:else>                  		                 		
                  		 <s:property value="serviceProcedureDefinition.code"/> ( <s:property value="serviceProcedureDefinition.description"/> )  
                  		 <s:hidden name="task.claim.payment.lineItemGroups[%{#status.index}].individualLineItems[%{#indItemStatus.index}].serviceProcedureDefinition" value="%{serviceProcedureDefinition.id}"></s:hidden>
                  		 </s:else>                   		            		
               <%--    		 <s:property value="item"/> ( <s:property value="description"/> )   
                  		 	<s:hidden name="task.claim.payment.lineItemGroups[%{#status.index}].individualLineItems[%{#indItemStatus.index}].item"></s:hidden>
                  		 	<s:hidden name="task.claim.payment.lineItemGroups[%{#status.index}].individualLineItems[%{#indItemStatus.index}].description"></s:hidden>   --%>         		
                  		   </td>
              			<td nowrap="nowrap" style="padding-left: 10px;   padding-bottom: 10px;text-align:right;" class="numeric"><s:property value="#setDealerAudit.individualLineItems[#indItemStatus.index].askedHrs"/> 
              				<s:hidden name="task.claim.payment.lineItemGroups[%{#status.index}].individualLineItems[%{#indItemStatus.index}].askedHrs"></s:hidden> </td>
              			<td nowrap="nowrap" style="padding-left: 10px;   padding-bottom: 10px;text-align:right;" class="numeric"><s:property value="#setDealerAudit.individualLineItems[#indItemStatus.index].baseAmount"/></td>
						<td nowrap="nowrap" style="padding-left: 10px;   padding-bottom: 10px;text-align:right;" class="numeric"><s:property value="baseAmount"/> </td>
 						<td nowrap="nowrap" style="padding-left: 2px;   padding-bottom: 10px;text-align:right;" class="numeric">  						
 						  <s:textfield  size="1" cssClass="numeric lineItems" name="task.claim.payment.lineItemGroups[%{#status.index}].individualLineItems[%{#indItemStatus.index}].acceptedHrs" value="%{acceptedHrs}" id="nonOem_Hrs_<s:property value='#indItemStatus.index'/>"/>
 						</td> 	  
                        <td nowrap="nowrap" style="padding-left: 10px;   padding-bottom: 10px;text-align:right;" class="numeric">
           				<div id="nonOemAmount_<s:property value="#indItemStatus.index"/>">
							<div id="percentage_processor_write_<s:property value="#status.index"/>_<s:property value="#indItemStatus.index"/>">
					       		<s:textfield size="8" cssClass="numeric lineItems" name="task.claim.payment.lineItemGroups[%{#status.index}].individualLineItems[%{#indItemStatus.index}].percentageAcceptance"
                                value="%{percentageAcceptance}" id="percentage_OemPart_<s:property value='#indItemStatus.index'/>" />%
                                </div>
                                <div id="flat_processor_write_<s:property value="#status.index"/>_<s:property value="#indItemStatus.index"/>" style="display:none">
                              <t:money id="flatOemPart_%{id}" 
	                         	name="task.claim.payment.lineItemGroups[%{#status.index}].individualLineItems[%{#indItemStatus.index}].acceptedAmount" 
	                         	value="%{acceptedAmount}" defaultSymbol="<s:property value='acceptedTotal.breachEncapsulationOfCurrency().getCurrencyCode()'/>"></t:money>
                            </div>
                            </div>
						</td>	
						<td nowrap="nowrap"	style="padding-left: 10px; padding-bottom: 10px; text-align: right;" class="numeric"><s:property value="acceptedAmount" />	</td>
						<s:if test="task.claim.payment.stateMandateActive && !task.claim.isGoodWillPolicy()">
							<td nowrap="nowrap"	style="padding-left: 10px; padding-bottom: 10px; text-align: right;" class="numeric"><s:property value="stateMandateAmount" />	</td>
						</s:if>				
						
                  </tr>                   
                              
                </s:iterator>
                <tr>
                <td nowrap="nowrap"	style="padding-left: 60px; padding-bottom: 10px; text-align: left;" class="numeric"><s:text name="label.common.rate"/>:</td>		
                <td nowrap="nowrap" style="padding-left: 10px;   padding-bottom: 10px; text-align: right;"> </td>		
                <td nowrap="nowrap"	style="padding-left: 10px; padding-bottom: 10px; text-align: right;" class="numeric"><s:if test="buConfigAMER"> <s:property value="#setDealerAudit.percentageApplicable"/>%=</s:if> <s:else>@</s:else> <s:property value="#setDealerAudit.rate"/>	</td>
						<td nowrap="nowrap"	style="padding-left: 10px; padding-bottom: 10px; text-align: right;" class="numeric"><s:if test="buConfigAMER"> <s:property value="percentageApplicable"/>%=</s:if> <s:else>@</s:else> <s:property value="rate"/></td>
						 <td nowrap="nowrap" style="padding-left: 10px;   padding-bottom: 10px; text-align: right;"> </td>
						  <td nowrap="nowrap" style="padding-left: 10px;   padding-bottom: 10px; text-align: right;"></td>
						  <td nowrap="nowrap" style="padding-left: 10px;   padding-bottom: 10px; text-align: right;"><s:if test="buConfigAMER"> <s:property value="percentageApplicable"/>%=</s:if> <s:else>@</s:else> <s:property value="rate"/></td>
						  <s:if test="task.claim.payment.stateMandateActive && !task.claim.isGoodWillPolicy()">
						   <td nowrap="nowrap" style="padding-left: 10px;   padding-bottom: 10px; text-align: right;"><s:property value="stateMandateRatePercentage"/>% = <s:property value="stateMandateRate"/></td>
						   </s:if>
						    
                </tr>
                        	<%-- <tr>
                            	<td nowrap="nowrap" style="padding-left: 30px;   padding-bottom: 10px;"><s:text name="%{getMessageKey(name)}"/></td>
                        		<td nowrap="nowrap" style="padding-left: 10px;   padding-bottom: 10px;text-align:right;" class="numeric">
  	                      			<span id="dealerAudit[<s:property value="#status.index"/>]">
  	                      				<s:property value="#setDealerAudit.baseAmount"/>
  	                      			</span>
  	                      			<s:if test="#setDealerAudit.rate != null">
	  	                      			<span dojoType="dijit.Tooltip" connectId="dealerAudit[<s:property value="#status.index"/>]">
											@<s:property value="#setDealerAudit.rate"/>
										</span>
										<br>{@<s:property value="#setDealerAudit.rate"/>}
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
										<br>{@<s:property value="rate"/>}
  	                      			</s:if>	                        		
                        		</td>
                       		</tr> --%>
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
                            
                 <%--   <tr>
                        <td nowrap="nowrap" style="padding-left: 10px;   padding-bottom: 10px;"><s:text name="%{getMessageKey(name)}"/></td>
                         <td nowrap="nowrap" style="padding-left: 10px;   padding-bottom: 10px;text-align:right;" class="numeric"></td>
                         <s:if test="name=='Oem Parts' || name=='Non Oem Parts' ||name=='Labor'">  
                         </s:if>
                         <s:else>
                        <td nowrap="nowrap" style="padding-left: 10px;   padding-bottom: 10px;text-align:right;" class="numeric">
	                    		<span id="dealerAudit[<s:property value="#status.index"/>]" >
	                    			<s:property value="#setDealerAudit.baseAmount"/>
	                    		</span>
	                    		<s:if test="#setDealerAudit.rate != null">
	                     			<span dojoType="dijit.Tooltip" connectId="dealerAudit[<s:property value="#status.index"/>]">
										@<s:property value="#setDealerAudit.rate"/>
									</span>
									<br>{@<s:property value="#setDealerAudit.rate"/>}
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
									<br>{@<s:property value="rate"/>}
	                    		</s:if>	                        		
	                   		</td>
	                   		</s:else>
                    	
                   		                    		
                    </tr>                 --%>
             
                  
                                      
               <%--       <s:if test="name=='Oem Parts'">      --%>           
                    
                    <s:iterator value="individualLineItems" status="indItemStatus">                                       
                 	 <tr>               
                  		<td nowrap="nowrap" style="padding-left: 30px;   padding-bottom: 10px;text-align:left;" class="numeric">
                  		 <s:if test="name=='Oem Parts'">                  		 
                  		 	<s:hidden name="task.claim.payment.lineItemGroups[%{#status.index}].individualLineItems[%{#indItemStatus.index}].brandItem" value="%{brandItem.id}"></s:hidden>  
                  			<s:property value="brandItem.itemNumber"/>-
                  			<s:property value="brandItem.item.description"/>
                  		</s:if>
                  		<s:elseif test="name=='Non Oem Parts'">        		
                  		<s:property value="nonOemPartReplaced"/>  
                  	<s:hidden name="task.claim.payment.lineItemGroups[%{#status.index}].individualLineItems[%{#indItemStatus.index}].nonOemPartReplaced"></s:hidden> 
                  		</s:elseif>            		 
                  		<%-- <s:hidden name="task.claim.payment.lineItemGroups[%{#status.index}].individualLineItems[%{#indItemStatus.index}].description"></s:hidden> --%></td>
              			<td nowrap="nowrap" style="padding-left: 10px;   padding-bottom: 10px;text-align:right;" class="numeric"><s:property value="#setDealerAudit.individualLineItems[#indItemStatus.index].askedQty"/> 
              			<s:hidden name="task.claim.payment.lineItemGroups[%{#status.index}].individualLineItems[%{#indItemStatus.index}].askedQty"></s:hidden></td>
              			<td nowrap="nowrap" style="padding-left: 10px;   padding-bottom: 10px;text-align:right;" class="numeric"><s:property value="#setDealerAudit.individualLineItems[#indItemStatus.index].baseAmount"/></td>
						<td nowrap="nowrap" style="padding-left: 10px;   padding-bottom: 10px;text-align:right;" class="numeric"><s:property value="baseAmount"/> </td>
 						<td nowrap="nowrap" style="padding-left: 2px;   padding-bottom: 10px;text-align:right;" class="numeric"> 
 						 <s:textfield  size="1"  name="task.claim.payment.lineItemGroups[%{#status.index}].individualLineItems[%{#indItemStatus.index}].acceptedQty" value="%{acceptedQty}" id="nonOem_Qty_<s:property value='#counter'/>"/> </td> 	  
                        <td nowrap="nowrap" style="padding-left: 10px;   padding-bottom: 10px;text-align:right;" class="numeric">
           				<div id="nonOemAmount_<s:property value="#indItemStatus.index"/>">
							<div id="percentage_processor_write_<s:property value="#status.index"/>_<s:property value="#indItemStatus.index"/>">
					       		<s:textfield size="8" cssClass="numeric lineItems" name="task.claim.payment.lineItemGroups[%{#status.index}].individualLineItems[%{#indItemStatus.index}].percentageAcceptance"
                                value="%{percentageAcceptance}" id="percentage_OemPart_<s:property value='#indItemStatus.index'/>" />%
                                </div>
                                <div id="flat_processor_write_<s:property value="#status.index"/>_<s:property value="#indItemStatus.index"/>" style="display:none">
                              <t:money id="flatOemPart_%{id}" 
	                         	name="task.claim.payment.lineItemGroups[%{#status.index}].individualLineItems[%{#indItemStatus.index}].acceptedAmount" 
	                         	value="%{acceptedAmount}" defaultSymbol="<s:property value='acceptedTotal.breachEncapsulationOfCurrency().getCurrencyCode()'/>"></t:money>
                            </div>
                            </div>
						</td>	
						<td nowrap="nowrap"	style="padding-left: 10px; padding-bottom: 10px; text-align: right;" class="numeric"><s:property value="acceptedAmount" />	</td>
						<s:if test="task.claim.payment.stateMandateActive&& !task.claim.isGoodWillPolicy()">
						<td nowrap="nowrap"	style="padding-left: 10px; padding-bottom: 10px; text-align: right;" class="numeric"><s:property value="stateMandateAmount" />	</td>
						</s:if>				
						
                  </tr>                                         
                 </s:iterator>
                    
                    <%-- </s:if>     --%>                  
                    
                    
                    
    </s:else>
                    <!-- Here, we have logic to display modifiers on the payment section
                    There is a possibility that modifiers have changed after dealer submission and while
                    processor is processing the claim, updated modifier need to be shown
                    Hence, we are iterating over Dealer as well as Latest Modifiers to make sure we show the updated
                    information on the payment section taking care of duplicates
                    -->
                    <!--  NMHGSLMS-425 Modifier code-->
                    <s:if test="!modifiers.empty">
                        <s:iterator value="modifiers" status="latestAuditStatus" id="modifier">
                        <tr>
                            <td nowrap="nowrap" style="padding-left: 30px;   padding-bottom: 10px;"><s:property value="paymentVariable.displayName"/>&nbsp;&nbsp;<s:property value="percentageConfigured"/>%	</td>
                             <td nowrap="nowrap" style="padding-left: 30px;   padding-bottom: 10px;"></td>
                            <td nowrap="nowrap" style="padding-left: 10px;   padding-bottom: 10px;text-align:right;" class="numeric">
                                <s:if test="#setDealerAudit.modifierMap[#modifier.name] != null">
           		        			<span id="dealerAuditMod_[<s:property value="#status.index"/>]_[<s:property value="#latestAuditStatus.index"/>]">
										<s:property value="#setDealerAudit.modifierMap[#modifier.name].value"/>
									</span>
  	                      			<span dojoType="dijit.Tooltip" connectId="dealerAuditMod_[<s:property value="#status.index"/>]_[<s:property value="#latestAuditStatus.index"/>]">
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
	                            	<span id="latestAuditMod_[<s:property value="#status.index"/>]_[<s:property value="#latestAuditStatus.index"/>]">
										<s:property value="value"/>
									</span>
	 	                      		<span dojoType="dijit.Tooltip" connectId="latestAuditMod_[<s:property value="#status.index"/>]_[<s:property value="#latestAuditStatus.index"/>]">
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
			<td nowrap="nowrap" style="padding-left: 10px;   padding-bottom: 10px;text-align:right;" class="numeric">
			<div id="percentage_processor_modifier_write_<s:property value='#status.index'/>">
			<%-- <s:property value="#latestAuditStatus.index"/> --%>
			<s:hidden name="task.claim.payment.lineItemGroups[%{#status.index}].modifiers[%{#latestAuditStatus.index}].name" value="%{name}"/>
			<s:hidden name="task.claim.payment.lineItemGroups[%{#status.index}].modifiers[%{#latestAuditStatus.index}].paymentVariable" value="%{paymentVariable}"/>
			<s:hidden name="task.claim.payment.lineItemGroups[%{#status.index}].modifiers[%{#latestAuditStatus.index}].level" value="%{level}"/>
			<s:hidden name="task.claim.payment.lineItemGroups[%{#status.index}].modifiers[%{#latestAuditStatus.index}].isFlatRate" value="%{isFlatRate}"/>
					       <s:textfield size="8" cssClass="numeric lineItems"
                                name="task.claim.payment.lineItemGroups[%{#status.index}].modifiers[%{#latestAuditStatus.index}].percentageAcceptance"
                                value="%{percentageAcceptance}" id="percentage_oemModifier_%{id}"/>%
                                </div>
                                <div id="flat_processor_modifier_write_<s:property value='#status.index'/>" style="display:none">
                              <t:money id="oemModifier_%{id}" 
	                         	name="task.claim.payment.lineItemGroups[%{#status.index}].modifiers[%{#latestAuditStatus.index}].acceptedCost" 
	                         	value="%{acceptedCost}" defaultSymbol="<s:property value='value.breachEncapsulationOfCurrency().getCurrencyCode()'/>"></t:money>
                            </div>
			</td>
			<td nowrap="nowrap" style="padding-left: 10px;   padding-bottom: 10px;text-align:right;" class="numeric"><s:property value="acceptedCost" /></td>
			 <s:if test="task.claim.payment.stateMandateActive && #lineItem.name!='Oem Parts' && !task.claim.isGoodWillPolicy()">    					
					<td nowrap="nowrap" style="padding-left: 10px;   padding-bottom: 10px;text-align:right;" class="numeric"><s:property value="stateMandateAmount"/></td>
			</s:if>					
                            
             </tr>
             <s:if test="task.claim.payment.stateMandateActive && #lineItem.name=='Oem Parts' && !task.claim.isGoodWillPolicy()">       
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
               <!--  Modifier code END-->
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
                    
                <%--     <s:if test="name=='Late Fee'||'Claim Amount'">                    
                  		  <s:set var="counterForDeductible" value="%{#counterForDeductible+1}"/>
                  	</s:if> --%>
       <%--        	<s:if test="#counterForDeductible==1">
                 	<s:if test="task.claim.payment.deductibleAmount!=null">
                 		<tr > 
                    		 <td nowrap="nowrap" style="padding-left: 30px;   padding-bottom: 10px;"> <s:text name="label.common.payment.deductable"/>:</td>
                     		<td nowrap="nowrap" style="padding-left: 30px;   padding-bottom: 10px;"></td>
                       		 <td nowrap="nowrap" style="padding-left: 30px;   padding-bottom: 10px;"></td>
                         	<td nowrap="nowrap" style="padding-left: 30px;   padding-bottom: 10px;"><s:property value="task.claim.payment.deductibleAmount"/></td>   
                         	<td nowrap="nowrap" style="padding-left: 30px;   padding-bottom: 10px;"></td>              	
                       		 <td nowrap="nowrap" style="padding-left: 30px;   padding-bottom: 10px;"></td>
                         	<td nowrap="nowrap" style="padding-left: 30px;   padding-bottom: 10px;text-align:right;"><s:property value="task.claim.payment.deductibleAmount"/></td>
                         	 <s:if test="task.claim.payment.stateMandateActive && !task.claim.isGoodWillPolicy()">  
                         	 <td nowrap="nowrap" style="padding-left: 30px;   padding-bottom: 10px;text-align:right;"><s:property value="task.claim.payment.deductibleAmount"/></td> 
                         	 </s:if>   
                         	</tr>                             
                 	</s:if>
                 	
                 </s:if> --%> 
                 
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
						  <s:if test="task.claim.payment.stateMandateActive && !task.claim.isGoodWillPolicy()">
						   <td nowrap="nowrap" style="padding-left: 10px;   padding-bottom: 10px; text-align: right;"><%-- <s:property value="#travelRate.stateMandateRatePercentage"/>% --%> {<s:if test="!buConfigAMER">@</s:if><s:property value="#travelRate.stateMandateRate"/>}</td>
						   </s:if>
						    
                		</tr>
                </s:if>
                </s:if>     
                 
                    <!-- Total code -->
                     <!-- Total code related to travel-->
                     <s:if test="name!='Others'">   
                                       
                      <tr id='totalItem_<s:property value="#status.index"/>'>   
                               
                     <s:if test="name=='Travel by Hours' || name=='Travel By Trip' ||name=='Additional Travel Hours'||name=='Item Freight And Duty' || name=='Handling Fee' ||name=='Transportation'||name=='Late Fee'||name=='Deductible'"> 
                         <td nowrap="nowrap" style="padding-left: 30px;   padding-bottom: 10px;"> <s:if test="name!='Travel By Trip'&&name!='Late Fee'&&name!='Deductible' "><s:text name="label.common.total"/> </s:if><s:text name="%{getMessageKey(name)}"/>:   <s:if test="name=='Late Fee' "><s:property value="percentageApplicable"/>%</s:if></td>
                     </s:if>
                     <s:else>             
                        <script type="text/javascript">
                      		dojo.addOnLoad(function(){                    	                     	                   	
                    			 var totalId='totalItem_'+<s:property value="#status.index"/>;                    	 
                    			 var totalElement = document.getElementById(totalId); 
									if (totalElement.className != "total") {								
										totalElement.className = ' total';
										}
                     		 });
						</script> 
                     <td nowrap="nowrap" style="padding-left: 60px;   padding-bottom: 10px;"><s:text name="label.common.total"/> <s:text name="%{getMessageKey(name)}"/>:</td>
                     </s:else>                       		 
                                      
                        	  <td nowrap="nowrap" style="padding-left: 10px;   padding-bottom: 10px;text-align:right;" class="numeric">
                        	  <!-- fix for SLMSPROD-1392 -->
                        	  	<s:if test="name=='Travel by Hours' || name=='Travel' ||name=='Additional Travel Hours'">
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
                          <s:if test="name!='Travel By Trip' ">
                          <s:property value="groupTotal"/></s:if>
                          </td>
                      
                       <s:if test="name=='Travel by Hours' ||name=='Additional Travel Hours'">
                       
                       <td nowrap="nowrap" style="padding-left: 2px;   padding-bottom: 10px;text-align:right;" class="numeric"> 
 							 <s:textfield  size="1"  name="task.claim.payment.lineItemGroups[%{#status.index}].acceptedQtyHrs" value="%{acceptedQtyHrs}" id="acceped_QTY_Travel_<s:property value='#status.index'/>"/>
 					  </td> 	   
                       </s:if>
                       <s:else>

                       <td nowrap="nowrap" style="padding-left: 10px;   padding-bottom: 10px;text-align:center;" class="numeric">
	                       <s:if test="name=='Travel'">
	                       		<s:property value="acceptedQtyHrs.replace('.',':')"/>
	                       </s:if>
	                       <s:else>
	                       		<s:property value="acceptedQtyHrs"/>
	                       </s:else>
                       </td>

                       </s:else>
                        	                       
                      
                        <td nowrap="nowrap" style="padding-left: 10px;   padding-bottom: 10px;text-align:right;" class="numeric">
                     
                     <s:if test="name=='Oem Parts' || name=='Non Oem Parts' ||name=='Labor' ||name=='Travel'||name=='Others'||name=='Handling Fee'||name=='Deductible'">
                                             
                           <div id="percentage_processor_write_<s:property value='#status.index'/>">
                           		 <s:if test="name!='Deductible'">
                                <s:property value="percentageAcceptance"/>%
                                </s:if>
                            </div>
                                <div id="flat_processor_write_<s:property value='#status.index'/>" style="display:none">
                                 <s:if test="name!='Deductible'">
                                   	   <s:property value="acceptedTotal"/>
                                  </s:if>
                            	</div>
                       </s:if>
                            <s:else>                        
                            
                              <div id="percentage_processor_write_<s:property value='#status.index'/>">
                               <s:if test="name=='Late Fee' ">
                           <s:textfield size="8" cssClass="numeric lineItems"
                                name="task.claim.payment.lineItemGroups[%{#status.index}].percentageAcceptance"
                                value="%{percentageAcceptance}" id="late_fee_percentage" onchange="refreshDecision('percentage')"/>%
                                </s:if>
                                <s:else>   
                                 	<s:if test="name!='Travel By Trip' ">                           
                          			 <s:textfield size="8" cssClass="numeric lineItems"
                               			 name="task.claim.payment.lineItemGroups[%{#status.index}].percentageAcceptance"
                               				 value="%{percentageAcceptance}" id="lineItemGroup_percentage_%{#status.index}"/>%
                               		</s:if>
                                </s:else>
                                 </div>
                                <div id="flat_processor_write_<s:property value='#status.index'/>" style="display:none">
                                  <s:if test="name=='Late Fee' ">                                
                                <t:money id="late_fee_flat" 
	                         	name="task.claim.payment.lineItemGroups[%{#status.index}].acceptedTotal" 
	                         	value="%{acceptedTotal}" defaultSymbol="<s:property value='acceptedTotal.breachEncapsulationOfCurrency().getCurrencyCode()'/>" onchange="refreshDecision('flat')"></t:money>
	                         	</s:if>
	                         	<s:else>
	                         	<s:if test="name!='Travel By Trip' ">   
	                         	 <t:money id="lineItemGroup_flat_%{#status.index}" 
	                         	name="task.claim.payment.lineItemGroups[%{#status.index}].acceptedTotal" 
	                         	value="%{acceptedTotal}" defaultSymbol="<s:property value='acceptedTotal.breachEncapsulationOfCurrency().getCurrencyCode()'/>"></t:money>
	                         	</s:if>
	                         	</s:else>
	                         	 </div>	                         	
                           
                           </s:else>
                         
                        </td>
                        
                        <td class="numeric" nowrap="nowrap" style="text-align:right">
                        <s:if test="name=='Travel By Trip' ">  
                        <s:property value="acceptedQtyHrs"/> 
                        </s:if>
                        <s:else>
                        	<s:property value="acceptedTotal"/>
                        	</s:else>
                        </td>
                         <s:if test="task.claim.payment.stateMandateActive && !task.claim.isGoodWillPolicy()">         
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
                     
            <%--         <s:if test="name=='Additional Travel Hours'">
                   			         <tr>
                			<td nowrap="nowrap"	style="padding-left: 60px; padding-bottom: 10px; text-align: left;" class="numeric"><s:text name="label.common.rate"/>:</td>		
                			<td nowrap="nowrap" style="padding-left: 10px;   padding-bottom: 10px; text-align: right;"> </td>		
               				 <td nowrap="nowrap"	style="padding-left: 10px; padding-bottom: 10px; text-align: right;" class="numeric">@<s:property value="#setDealerAudit.rate"/>	</td>
							<td nowrap="nowrap"	style="padding-left: 10px; padding-bottom: 10px; text-align: right;" class="numeric">@<s:property value="rate"/></td>
							<td nowrap="nowrap" style="padding-left: 10px;   padding-bottom: 10px; text-align: right;"> </td>
						 	 <td nowrap="nowrap" style="padding-left: 10px;   padding-bottom: 10px; text-align: right;"></td>
						  	<td nowrap="nowrap" style="padding-left: 10px;   padding-bottom: 10px; text-align: right;">@<s:property value="rate"/></td>
						  <s:if test="task.claim.payment.stateMandateActive && !task.claim.isGoodWillPolicy()">
						   <td nowrap="nowrap" style="padding-left: 10px;   padding-bottom: 10px; text-align: right;"><s:property value="stateMandateRatePercentage"/>% @<s:property value="stateMandateRate"/></td>
						   </s:if>
						    
                </tr>
                </s:if>      --%>          
                
                             
                </s:if>
             </s:iterator>
             <s:iterator value="task.claim.payment.lineItemGroups" status="status">
                 <s:set name="setDealerPayment" value="%{task.claim.getPaymentForDealerAudit()}"/>
                 <s:set name="setDealerAudit" value="#setDealerPayment.getLineItemGroup(name)"/>
                    <s:if test="name=='Claim Amount'">
                    <s:set name="claimAmountAudit" value="latestAudit"/>
                    <script type="text/javascript">
                        claimAmountRow = <s:property value="%{#status.index}"/>;
                    </script>
              <%--       <tr>
                        <td nowrap="nowrap" style="padding-left: 30px;   padding-bottom: 10px;"><s:text name="%{getMessageKey(name)}"/></td>
                        <td nowrap="nowrap" style="padding-left: 10px;   padding-bottom: 10px;text-align:right;" class="numeric"></td>
                        <td nowrap="nowrap" style="padding-left: 10px;   padding-bottom: 10px;text-align:right;" class="numeric"><s:property value="#setDealerAudit.baseAmount"/></td>
                        <td nowrap="nowrap" style="padding-left: 10px;   padding-bottom: 10px;text-align:right;" class="numeric"><s:property value="#claimAmountDisplayAudit.baseAmount"/></td>                        
                       
                    </tr> --%>
                     <!-- added for late fee -->
                          <s:if test="isLateFeeEnabled()">
				 			<s:set name="lateFeeAmount" value="calculateLateFees(#setDealerAudit.groupTotal)" />
				 		  </s:if>	
          			 <!-- added for late fee -->
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
		                        </tr>
	                        </s:if>
                        </s:iterator>
                    </s:if>
                    <tr class="total" >
                        <td nowrap="nowrap" style="padding-left: 10px;   padding-bottom: 10px;"><s:text name="label.common.total"/> <s:text name="%{getMessageKey(name)}"/></td>
                         <td nowrap="nowrap" style="padding-left: 10px;   padding-bottom: 10px;text-align:right;" class="numeric"></td>
                        <td nowrap="nowrap" style="padding-left: 10px;   padding-bottom: 10px;text-align:right;" class="numeric"><s:property value="#setDealerAudit.groupTotal"/></td>
                        <td nowrap="nowrap" style="padding-left: 10px;   padding-bottom: 10px;text-align:right;" class="numeric"><s:property value="groupTotal"/></td>
                        <td nowrap="nowrap" style="padding-left: 10px;   padding-bottom: 10px;text-align:right;" class="numeric"><%-- <s:property value="acceptedCpTotal"/> --%></td>
                        <td nowrap="nowrap" style="padding-left: 10px;   padding-bottom: 10px;text-align:right;" class="numeric" align="center" >
                           <div id="percentage_processor_write_<s:property value='#status.index'/>">
                           <%--  <s:textfield size="8" cssClass="numeric lineItems"
                                name="task.claim.payment.lineItemGroups[%{#status.index}].percentageAcceptance"
                                value="%{percentageAcceptance}" id="totalPercentageAcceptance"/><span id="globalPercent">%</span>
                            <s:checkbox name="totalPercentageAcceptanceChkBox" id="totalPercentageAcceptanceChkBox"/> --%>
                            <s:property value="percentageAcceptance"/>% 
                            </div>
                            <div id="flat_processor_write_<s:property value='#status.index'/>" style="display:none">
                         <%--  <t:money id="lineItemGroup_pre_%{#status.index}" 
	                         	name="task.claim.payment.lineItemGroups[%{#status.index}].acceptedTotal" 
	                         	value="%{acceptedTotal}" defaultSymbol="<s:property value='acceptedTotal.breachEncapsulationOfCurrency().symbol'/>"></t:money> --%>
	                           <s:property value="acceptedTotal"/>
                            </div>
                        </td>
                        <td class="numeric" nowrap="nowrap" style="text-align:right;">
                        <s:if test="task.claim.payment.totalAcceptanceChkbox">
                         <s:checkbox checked="%{task.claim.payment.totalAcceptanceChkbox}" name="task.claim.payment.totalAcceptanceChkbox" id="totalPercentageAcceptanceCheckBox" onchange="selectTotalCheckBox('warranty')" /><s:property value="acceptedTotal"/>
                       </s:if>
                       <s:else>
                       <s:checkbox name="task.claim.payment.totalAcceptanceChkbox" id="totalPercentageAcceptanceCheckBox" onchange="selectTotalCheckBox('warranty')" /><s:property value="acceptedTotal"/>
                       </s:else>
                       </td>
                        <s:if test="task.claim.payment.stateMandateActive && !task.claim.isGoodWillPolicy()">                        
                         <td class="numeric" nowrap="nowrap" style="text-align:right;"> 
                          <s:if test="task.claim.payment.totalAcceptStateMdtChkbox">
                          <s:checkbox checked="%{task.claim.payment.totalAcceptStateMdtChkbox}" name="task.claim.payment.totalAcceptStateMdtChkbox" id="totalPercentageAcceptanceChkBoxStateMandate" onchange="selectTotalCheckBox('statemandate')"/> <s:property value="groupTotalStateMandateAmount"/>
                          </s:if>
                          <s:else>
                           <s:checkbox name="task.claim.payment.totalAcceptStateMdtChkbox" id="totalPercentageAcceptanceChkBoxStateMandate" onchange="selectTotalCheckBox('statemandate')"/> <s:property value="groupTotalStateMandateAmount"/>
                          </s:else></td>
                         </s:if>     
                    </tr>
                                        
                     <script type="text/javascript">
                     
                     
                     
                     function selectTotalCheckBox(totalPayment)
                     {
                    	 
                    	 if(totalPayment=='warranty')
                    		 {
                    		 if(dojo.byId("totalPercentageAcceptanceChkBoxStateMandate")!=null)
                    			 {
                    		 	if(dojo.byId("totalPercentageAcceptanceChkBoxStateMandate").checked==true)
                     				{                     		
                     				dojo.byId("totalPercentageAcceptanceChkBoxStateMandate").checked=false;
                     				}
                    			 }
                    		 
                    		 }
                    	 else
                    		 {
                    	
                    			if(dojo.byId("totalPercentageAcceptanceCheckBox").checked==true)
                					{                					
                					dojo.byId("totalPercentageAcceptanceCheckBox").checked=false;
                					}
                    		 }
                     }
                     
                        	var lineItemGroupSize = <s:property value="task.claim.payment.lineItemGroups.size"/>;
                            dojo.addOnLoad(function() {
                            /*This code determines the percentage calculation for Line Item Group*/
                            var totalPercentageAcceptance = dojo.byId("totalPercentageAcceptance");
                            var globalPercent = dojo.byId("globalPercent");
                            totalPercentageAcceptance.setAttribute("readOnly", "readOnly");
                            var totalPercentageAcceptanceForCp=null;
                            var globalPercentForCp=null;
                            if (dojo.byId("totalPercentageAcceptanceForCp") != null) {
                                totalPercentageAcceptanceForCp = dojo.byId("totalPercentageAcceptanceForCp");
                                globalPercentForCp = dojo.byId("globalPercentForCp");
                            }
                            if (totalPercentageAcceptance.value == '' ) {
                                totalPercentageAcceptance.value = "100";
                            }
                            <s:if test="#isLineItemChanged">
                                dojo.html.hide(totalPercentageAcceptance);
                                dojo.html.hide(globalPercent);
                                totalPercentageAcceptance.value = "100";
                                dojo.byId("totalPercentageAcceptanceChkBox").checked=false;
                                for (var i = 0; i < lineItemGroupSize; i++) {
                                    if(claimAmountRow != i){
                                        dojo.byId("lineItemGroup_percentage_" + i).readOnly = false;
                                    }
                                }
                            </s:if>
                            <s:else>
                                dojo.html.show(totalPercentageAcceptance);
                                dojo.html.show(globalPercent);
                                dojo.byId("totalPercentageAcceptanceChkBox").checked=true;
                                totalPercentageAcceptance.readOnly = false;
                                for (var i = 0; i < lineItemGroupSize; i++) {
                                    if(claimAmountRow != i){
//                                         dojo.byId("lineItemGroup_percentage_" + i).readOnly = true;
                                    }
                                }
                            </s:else>
                                dojo.connect(dojo.byId("totalPercentageAcceptanceChkBox"), "onclick", function(event) {                                	 
                                    for (var i = 0; i < lineItemGroupSize; i++) {                                        
                                        if (event.target.checked) {
                                            if(dojo.byId("lineItemGroup_percentage_for_cp_" + i)){
                                                dojo.byId("lineItemGroup_percentage_for_cp_" + i).readOnly = true;
                                                dojo.byId("lineItemGroup_percentage_for_cp_" + i).value = "0";
                                            }
                                            totalPercentageAcceptance.readOnly = false;
                                            totalPercentageAcceptance.value = "100";                                            
                                            if(claimAmountRow != i){
                                                dojo.byId("lineItemGroup_percentage_" + i).value = totalPercentageAcceptance.value;
//                                                 dojo.byId("lineItemGroup_percentage_" + i).readOnly = true;
                                            }
                                            dojo.html.show(totalPercentageAcceptance);                                            
                                            dojo.html.show(globalPercent);
                                        } else {
                                            if(dojo.byId("lineItemGroup_percentage_for_cp_" + i)){
                                                dojo.byId("lineItemGroup_percentage_for_cp_" + i).readOnly = false;
                                                dojo.byId("lineItemGroup_percentage_for_cp_" + i).value = "0";
                                            }
                                            if(claimAmountRow != i){
                                                dojo.byId("lineItemGroup_percentage_" + i).readOnly = false;
                                                dojo.byId("lineItemGroup_percentage_" + i).value = "100";
                                            }
                                            totalPercentageAcceptance.readOnly = true;                                            
                                            totalPercentageAcceptance.value = "100";
                                            dojo.html.hide(totalPercentageAcceptance);
                                            dojo.html.hide(globalPercent);
                                        }
                                    }                                    
                                    if (event.target.checked) {
                                        if (dojo.byId("totalPercentageAcceptanceForCp") != null) {
                                        	
	                                        dojo.html.show(totalPercentageAcceptanceForCp);
	                                        dojo.html.show(globalPercentForCp);
	                                        
	                                        totalPercentageAcceptanceForCp.readOnly = false;
	                                        totalPercentageAcceptanceForCp.value="0";
                                        }
                                    }else{
                                        if (dojo.byId("totalPercentageAcceptanceForCp") != null) {
	                                        dojo.html.hide(totalPercentageAcceptanceForCp);
	                                        dojo.html.hide(globalPercentForCp);	                                        
	                                        totalPercentageAcceptanceForCp.readOnly = true;
	                                        totalPercentageAcceptanceForCp.value="0";
	                                        
                                        }
                                    }
                                });

                                /*This code determines the percentage calculation for CP share in Line Item Group*/
                                 if (dojo.byId("totalPercentageAcceptanceForCp") != null) {
                                    totalPercentageAcceptanceForCp.setAttribute("readOnly", "readOnly");
                                    if (totalPercentageAcceptanceForCp.value == '') {
                                        totalPercentageAcceptanceForCp.value = "0";
                                    }
                               		<s:if test="#isLineItemForCPChanged">
                                        for (var i = 0; i < lineItemGroupSize; i++) {
                                            if(claimAmountRow != i){
                                            dojo.byId("lineItemGroup_percentage_for_cp_" + i).readOnly = false;
                                            }
                                        }
                                        dojo.html.hide(totalPercentageAcceptanceForCp);
                                        dojo.html.hide(globalPercentForCp);
                                        
                                        totalPercentageAcceptanceForCp.value = "0";
                                    </s:if>
                                    <s:else>
                                    	dojo.html.show(totalPercentageAcceptanceForCp);
                                    	dojo.html.show(globalPercentForCp);
                                    	
                                    	totalPercentageAcceptanceForCp.readOnly = false;
                                        for (var i = 0; i < lineItemGroupSize; i++) {
                                            if(claimAmountRow != i){
                                            dojo.byId("lineItemGroup_percentage_for_cp_" + i).readOnly = true;
                                            }
                                        }
                                    </s:else>
                                }
                            });
                            function disableAccept(index) {
                            	dojo.connect(dojo.byId("lineItemGroup_percentage_for_cp_"+ index), "onblur", function(event) {
 									var acceptanceRadio = dojo.byId("acceptanceRadio");
 									var lineItemGroupSize = <s:property value="task.claim.payment.lineItemGroups.size"/>;
 									var isEnabled='false';
 									for (var i = 0; i < lineItemGroupSize; i++) { 
 										if(dojo.byId("lineItemGroup_percentage_for_cp_" + i)!=null){									
 										if((dojo.byId("lineItemGroup_percentage_for_cp_"+ i).value)!="" && 
 												parseFloat(dojo.byId("lineItemGroup_percentage_for_cp_"+ i).value)!="0"
 												&& isEnabled=='false'){
 													isEnabled='true';
 										}
 										}
 									}
 									if(isEnabled=='true'){
 										dojo.html.hide(acceptanceRadio);
 									}else{
 										dojo.html.show(acceptanceRadio);
 									}
 									
	   							});
                            }
                            function disableAcceptRadio() {
                            	dojo.connect(dojo.byId("totalPercentageAcceptanceForCp"), "onblur", function(event) {
 									var acceptanceRadio = dojo.byId("acceptanceRadio");
 									var isEnabled='false';
 										if((dojo.byId("totalPercentageAcceptanceForCp").value)!="" && 
 												parseFloat(dojo.byId("totalPercentageAcceptanceForCp").value)!="0"
 												&& isEnabled=='false'){
 													isEnabled='true';
 										}
 									if(isEnabled=='true'){
 										dojo.html.hide(acceptanceRadio);
 									}else{
 										dojo.html.show(acceptanceRadio);
 									}
	   							});

                            }
                          
                            function enableCPAcceptanceReason(percentageOrFlat){
                            
                            	var lineItemGroupSize = <s:property value="task.claim.payment.lineItemGroups.size"/>;
                            	dojo.html.hide(dojo.byId("settlementCodeLabel"));
                    			dojo.html.hide(dojo.byId("cpAcceptanceReason"));	
                            	for(var i = 0; i < lineItemGroupSize; i++){
                            		if(percentageOrFlat == 'percentage'){
                            			if((dojo.byId("lineItemGroup_percentage_for_cp_" + i) != null && dojo.byId("lineItemGroup_percentage_for_cp_" + i).value != 0)||(dojo.byId('totalPercentageAcceptanceForCp')&& dojo.byId('totalPercentageAcceptanceForCp').value!=0)){
                            				dojo.html.show(dojo.byId("settlementCodeLabel"));
                            				dojo.html.show(dojo.byId("cpAcceptanceReason"));
                            				break;
                            			}
                            		}else if(percentageOrFlat == 'flat'){
                            		
                            			if(dojo.byId("lineItemGroup_flat_for_cp" + i) != null && dojo.byId("lineItemGroup_flat_for_cp" + i).value != 0){
                            				dojo.html.show(dojo.byId("settlementCodeLabel"));
                            				dojo.html.show(dojo.byId("cpAcceptanceReason"));
                            				break;
                            			}
                            		}
                            	}
                            }
                            
                            function enableCPAcceptanceReasonOnLoad(){
                            	var lineItemGroupSize = <s:property value="task.claim.payment.lineItemGroups.size"/>;
                            	dojo.html.hide(dojo.byId("settlementCodeLabel"));
                        		dojo.html.hide(dojo.byId("cpAcceptanceReason"));
                            	for(var i = 0; i < lineItemGroupSize; i++){
                            		if((dojo.byId("lineItemGroup_percentage_for_cp_" + i) != null && dojo.byId("lineItemGroup_percentage_for_cp_" + i).value != 0) || (dojo.byId("lineItemGroup_flat_for_cp" + i) != null && dojo.byId("lineItemGroup_flat_for_cp" + i).value != 0)||(dojo.byId('totalPercentageAcceptanceForCp')&& dojo.byId('totalPercentageAcceptanceForCp').value!=0)){
                            			dojo.html.show(dojo.byId("settlementCodeLabel"));
                        				dojo.html.show(dojo.byId("cpAcceptanceReason"));
                        				break;
                            		} 
                            	}
                            }
                        </script>
                    <!-- added for late fee --> 
                <%--     <s:if test="isLateFeeEnabled()">   
	                    <tr>
			          		<td nowrap="nowrap" style="padding-left: 30px;   padding-bottom: 10px;"><s:text name="label.common.lateFee"/></td>
			          		<td nowrap="nowrap" style="padding-left: 30px;   padding-bottom: 10px;"></td>
			       			<td nowrap="nowrap" style="padding-left: 10px;   padding-bottom: 10px;text-align:right;" class="numeric"><s:property value="#lateFeeAmount"/></td>
			     		</tr>
			     		<tr class="total">
			        		<td nowrap="nowrap" style="padding-left: 10px;   padding-bottom: 10px;"><s:text name="label.common.total"/> <s:text name="%{getMessageKey(name)}"/></td>
			        		<td nowrap="nowrap" style="padding-left: 30px;   padding-bottom: 10px;"></td>
			        		<td nowrap="nowrap" style="padding-left: 10px;   padding-bottom: 10px;text-align:right;" class="numeric"><s:property value="computeClaimAmountAfterLateFee(#setDealerAudit.groupTotal,#lateFeeAmount)"/></td>
			     		</tr>
		     		</s:if> --%>
		   <!--   Total approved claim and cost amount code -->	             
	             <tr class="total">
		            <td nowrap="nowrap" style="padding-left: 60px;" colspan="2">
		                <s:text name="label.common.approvedClaimAmount"/>
		            </td>
		            <td nowrap="nowrap" style="padding-left: 60px;" class="numeric" style="padding-left: 10px;">
		                <%-- <s:property value="acceptedTotalForWnty"/> --%>
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
   </tbody>
  </table>
  </div>
  <div id="lowLeveltable_processor_write" style="display:none">
    <table id="percentage_payment" style="width:95%">
        <thead>
            <tr>
                <th style="text-align:left;" width="15%" class="labelStyle"><s:text name="label.common.category"/></th>
                <th style="text-align:right;" width="10%" class="labelStyle">&nbsp;&nbsp;&nbsp;&nbsp;<s:text name="label.claim.amountAsked" /></th>
                <th style="text-align:right;" width="20%" class="labelStyle">&nbsp;&nbsp;&nbsp;&nbsp;<s:text name="label.common.currentAmount" /></th>
                <th style="text-align:right;" width="15%" class="labelStyle">&nbsp;&nbsp;&nbsp;&nbsp;<s:text name="label.common.costPrice"/></th>
                <th style="text-align:right;" width="20%" class="labelStyle"><s:text name="label.newClaim.percentageAccepted.forWarranty"/></th>
                <th style="text-align:right;" width="20%" class="labelStyle"><s:text name="label.newClaim.acceptedAmount.for.warranty"/></th>
                <s:if test="isCPAdvisorEnabled()">
                <th style="text-align:center;" width="20%" class="labelStyle" id="percentage_cp_label"><s:text name="label.newClaim.percentageAcceptedForCP"/></th>
                <th style="text-align:center;" width="20%" class="labelStyle" id="flat_cp_label"><s:text name="label.newClaim.flatAmountAcceptedForCP"/></th>
                <th style="text-align:center;" width="20%" class="labelStyle"><s:text name="label.newClaim.acceptedAmount.for.cp"/></th>
                </s:if>
            </tr>
        </thead>
        <tbody>
            <s:iterator value="task.claim.payment.lineItemGroups" status="status">
                <s:set name="setDealerPayment" value="%{task.claim.getPaymentForDealerAudit()}"/>
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
                            <s:textfield size="8" cssClass="numeric lineItems"
                                name="task.claim.payment.lineItemGroups[%{#status.index}].percentageAcceptance"
                                value="%{percentageAcceptance}" id="lineItemGroup_percentage_%{#status.index}"/>%
                        </td>
                        <td class="numeric" nowrap="nowrap" style="text-align:right;"><s:property value="acceptedTotal"/></td>
                        
                    </tr>
                </s:if>
             </s:iterator>
             <s:iterator value="task.claim.payment.lineItemGroups" status="status">
                 <s:set name="setDealerPayment" value="%{task.claim.getPaymentForDealerAudit()}"/>
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
                            <s:textfield size="8" cssClass="numeric lineItems"
                                name="task.claim.payment.lineItemGroups[%{#status.index}].percentageAcceptance"
                                value="%{#setLatestAudit.percentageAcceptance}" id="totalPercentageAcceptance"/><span id="globalPercent">%</span>
                            <s:checkbox name="totalPercentageAcceptanceChkBox" id="totalPercentageAcceptanceChkBox"/>
                        </td>
                        <td class="numeric" nowrap="nowrap" style="text-align:right;"><s:property value="#setLatestAudit.acceptedTotal"/></td>
                      
                    </tr>
    					<script type="text/javascript">
                            dojo.addOnLoad(function() {
                            var lineItemGroupSize = <s:property value="task.claim.payment.lineItemGroups.size"/>;
                            /*This code determines the percentage calculation for Line Item Group*/
                            var totalPercentageAcceptance = dojo.byId("totalPercentageAcceptance");
                            var globalPercent = dojo.byId("globalPercent");
                            totalPercentageAcceptance.setAttribute("readOnly", "readOnly");
                            var totalPercentageAcceptanceForCp=null;
                            var globalPercentForCp=null;
                            if (dojo.byId("totalPercentageAcceptanceForCp") != null) {
                                totalPercentageAcceptanceForCp = dojo.byId("totalPercentageAcceptanceForCp");
                                globalPercentForCp = dojo.byId("globalPercentForCp");
                            }
                            if (totalPercentageAcceptance.value == '' ) {
                                totalPercentageAcceptance.value = "100";
                            }
                            <s:if test="#isLineItemChanged">
                                dojo.html.hide(totalPercentageAcceptance);
                                dojo.html.hide(globalPercent);
                                totalPercentageAcceptance.value = "100";
                                dojo.byId("totalPercentageAcceptanceChkBox").checked=false;
                                for (var i = 0; i < lineItemGroupSize; i++) {
                                    if(claimAmountRow != i){
                                        dojo.byId("lineItemGroup_percentage_" + i).readOnly = false;
                                    }
                                }
                            </s:if>
                            <s:else>
                                dojo.html.show(totalPercentageAcceptance);
                                dojo.html.show(globalPercent);
                                dojo.byId("totalPercentageAcceptanceChkBox").checked=true;
                                totalPercentageAcceptance.readOnly = false;
                                for (var i = 0; i < lineItemGroupSize; i++) {
                                    if(claimAmountRow != i){
//                                         dojo.byId("lineItemGroup_percentage_" + i).readOnly = true;
                                    }
                                }
                            </s:else>
                                dojo.connect(dojo.byId("totalPercentageAcceptanceChkBox"), "onclick", function(event) {
                                    for (var i = 0; i < lineItemGroupSize; i++) {
                                        if (event.target.checked) {
                                            if(dojo.byId("lineItemGroup_percentage_for_cp_" + i)){
                                                dojo.byId("lineItemGroup_percentage_for_cp_" + i).readOnly = true;
                                                dojo.byId("lineItemGroup_percentage_for_cp_" + i).value = "0";
                                            }
                                            totalPercentageAcceptance.readOnly = false;
                                            totalPercentageAcceptance.value = "100";
                                            if(claimAmountRow != i){
                                                dojo.byId("lineItemGroup_percentage_" + i).value = totalPercentageAcceptance.value;
//                                                 dojo.byId("lineItemGroup_percentage_" + i).readOnly = true;
                                            }
                                            dojo.html.show(totalPercentageAcceptance);
                                            dojo.html.show(globalPercent);
                                        } else {
                                            if(dojo.byId("lineItemGroup_percentage_for_cp_" + i)){
                                                dojo.byId("lineItemGroup_percentage_for_cp_" + i).readOnly = false;
                                                dojo.byId("lineItemGroup_percentage_for_cp_" + i).value = "0";
                                            }
                                            if(claimAmountRow != i){
                                                dojo.byId("lineItemGroup_percentage_" + i).readOnly = false;
                                                dojo.byId("lineItemGroup_percentage_" + i).value = "100";
                                            }
                                            totalPercentageAcceptance.readOnly = true;                                            
                                            totalPercentageAcceptance.value = "100";
                                            dojo.html.hide(totalPercentageAcceptance);
                                            dojo.html.hide(globalPercent);
                                        }
                                    }
                                    if (event.target.checked) {
                                        if (dojo.byId("totalPercentageAcceptanceForCp") != null) {
                                        dojo.html.show(totalPercentageAcceptanceForCp);
                                        dojo.html.show(globalPercentForCp);
                                        
                                        totalPercentageAcceptanceForCp.readOnly = false;
                                        totalPercentageAcceptanceForCp.value="0";
                                        }
                                    }else{
                                        if (dojo.byId("totalPercentageAcceptanceForCp") != null) {
                                        dojo.html.hide(totalPercentageAcceptanceForCp);
                                        dojo.html.hide(globalPercentForCp);
                                        totalPercentageAcceptanceForCp.readOnly = true;
                                        totalPercentageAcceptanceForCp.value="0";
                                        
                                        }
                                    }
                                });

                                /*This code determines the percentage calculation for CP share in Line Item Group*/
                                 if (dojo.byId("totalPercentageAcceptanceForCp") != null) {
                                    totalPercentageAcceptanceForCp.setAttribute("readOnly", "readOnly");
                                    if (totalPercentageAcceptanceForCp.value == '') {
                                        totalPercentageAcceptanceForCp.value = "0";
                                    }
                               		<s:if test="#isLineItemForCPChanged">
                                        for (var i = 0; i < lineItemGroupSize; i++) {
                                            if(claimAmountRow != i){
                                            dojo.byId("lineItemGroup_percentage_for_cp_" + i).readOnly = false;
                                            }
                                        }
                                        dojo.html.hide(totalPercentageAcceptanceForCp);
                                        dojo.html.hide(globalPercentForCp);
                                        
                                        totalPercentageAcceptanceForCp.value = "0";
                                    </s:if>
                                    <s:else>
                                    	dojo.html.show(totalPercentageAcceptanceForCp);
                                    	dojo.html.show(globalPercentForCp);
                                    	
                                    	totalPercentageAcceptanceForCp.readOnly = false;
                                        for (var i = 0; i < lineItemGroupSize; i++) {
                                            if(claimAmountRow != i){
                                            dojo.byId("lineItemGroup_percentage_for_cp_" + i).readOnly = true;
                                            }
                                        }
                                    </s:else>
                                }
                            });
                            function disableAccept(index) {
                            	dojo.connect(dojo.byId("lineItemGroup_percentage_for_cp_"+ index), "onblur", function(event) {
 									var acceptanceRadio = dojo.byId("acceptanceRadio");
 									if(!acceptanceRadio) 
 	 									return;
 									var lineItemGroupSize = <s:property value="task.claim.payment.lineItemGroups.size"/>;
 									var isEnabled='false';
 									for (var i = 0; i < lineItemGroupSize; i++) { 
 										if(dojo.byId("lineItemGroup_percentage_for_cp_" + i)!=null){									
 										if((dojo.byId("lineItemGroup_percentage_for_cp_"+ i).value)!="" && 
 												parseFloat(dojo.byId("lineItemGroup_percentage_for_cp_"+ i).value)!="0"
 												&& isEnabled=='false'){
 													isEnabled='true';
 										}
 										}
 									}
 									if(isEnabled=='true'){
 										dojo.html.hide(acceptanceRadio);
 									}else{
 										dojo.html.show(acceptanceRadio);
 									}
 									
	   							});
                            }
                            
                            function disableAcceptRadio() {
                            	dojo.connect(dojo.byId("totalPercentageAcceptanceForCp"), "onblur", function(event) {
 									var acceptanceRadio = dojo.byId("acceptanceRadio");
 									if(!acceptanceRadio) 
 	 									return;
 									var isEnabled='false';
 										if((dojo.byId("totalPercentageAcceptanceForCp").value)!="" && 
 												parseFloat(dojo.byId("totalPercentageAcceptanceForCp").value)!="0"
 												&& isEnabled=='false'){
 													isEnabled='true';
 										}
 									if(isEnabled=='true'){
 										dojo.html.hide(acceptanceRadio);
 									}else{
 										dojo.html.show(acceptanceRadio);
 									}
	   							});
                            }
                        </script>
             
                    </s:if>
             </s:iterator>

                </tbody>
                </table>
                </div>
</div>
<s:hidden name="task.claim.payment.flatAmountApplied" id="flatAmountApp"></s:hidden>
<s:hidden name="task.claim.payment.maxAmountSelected" id="isMaxAmountSelected"></s:hidden>
</s:if> 
</div>

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
<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="tda" uri="twmsDomainAware"%>

<table class="grid" cellspacing="0" cellpadding="0">
   	<s:iterator value="%{getAllTransitions(recoveryClaim)}" id="task" status="itr">
	    <tr>
         <s:if test="#task.equals('Dispute/Reject') && !disputeValid">
         </s:if>
         <s:else>
        	<td class="labelStyle" colspan="1" width="10%">
        	<s:if test="#task.equals('Dispute/Reject')">
        	 <s:text name="label.common.rejectOrDispute"/>
        	</s:if>
        	<s:else>
	            <s:property />
	            </s:else>
    	     </td>
	        <td class="labelStyle" width="2%">
	            <input type="radio" name="transition" id="transitionTaken_<s:property value='%{#itr.index}' />"  value='<s:property />' 
	            <s:if test="%{transition.equals(top)}">checked="checked"</s:if> class="processor_decesion"/>
	        </td>
            <td class="labelNormalTop" colspan="2" width="70%"> 
		        <s:if test="#task.equals('Dispute/Reject') || #task.equals('Reject')">
		        	<s:select id="rejectionReason" name="recoveryClaim.recoveryClaimRejectionReason" 
		         		cssClass="processor_decesion" value="%{recoveryClaim.recoveryClaimRejectionReason.code}"
		         		list="getLovsForClass('RecoveryClaimRejectionReason',recoveryClaim)" 
		         		listKey="code" listValue="description" headerKey="-1" 
		         		headerValue="%{getText('label.common.selectHeader')}"/>
		        </s:if>
	        	<s:else>
	        		<s:select id="acceptanceReason" name="recoveryClaim.recoveryClaimAcceptanceReason" 
		         		cssClass="processor_decesion" value="%{recoveryClaim.recoveryClaimAcceptanceReason.code}"
		         		list="getLovsForClass('RecoveryClaimAcceptanceReason',recoveryClaim)" 
		         		listKey="code" listValue="description" 
		         		headerKey="-1" headerValue="%{getText('label.common.selectHeader')}"/>  
	            </s:else>
	            <s:if test="#task.equals('Request For Part')">
	            	<script type="text/javascript">
		        		<s:if test="!isPartAvailableForReturn()">
		        			dojo.byId("transitionTaken_<s:property value='%{#itr.index}' />").disabled = true;
		        		</s:if>
		        	</script>
	            </s:if>
	        </td>
	         	<script type="text/javascript">
		dojo.addOnLoad(function(){
			var index=<s:property value="%{#itr.index}"/>;
		if((dojo.byId("transitionTaken_"+index).value=='Dispute/Reject' || dojo.byId("transitionTaken_"+index).value=='Reject')&& !(dojo.byId("transitionTaken_"+index).checked)){
			dijit.byId("rejectionReason").setDisabled(true);
		};
		if((dojo.byId("transitionTaken_"+index).value=='Accept')&& !(dojo.byId("transitionTaken_"+index).checked)){
			 dijit.byId("acceptanceReason").setDisabled(true);
		};
		});
		
		var index=<s:property value="%{#itr.index}"/>;
		if(dojo.byId("transitionTaken_"+index).value!='Accept'){
			dojo.connect(dojo.byId("transitionTaken_"+index),"onclick",function(){
				dijit.byId("acceptanceReason").setDisabled(true);
				dijit.byId("acceptanceReason").setDisplayedValue("--Select--");	
			});
		}else{
			dojo.connect(dojo.byId("transitionTaken_"+index),"onclick",function(){
				dijit.byId("acceptanceReason").setDisabled(false);
			});
		}
		
		if(dojo.byId("transitionTaken_"+index).value!='Dispute/Reject' && dojo.byId("transitionTaken_"+index).value!='Reject'){
			dojo.connect(dojo.byId("transitionTaken_"+index),"onclick",function(){
				dijit.byId("rejectionReason").setDisabled(true);
				dijit.byId("rejectionReason").setDisplayedValue("--Select--");	
			});
		}else{
			dojo.connect(dojo.byId("transitionTaken_"+index),"onclick",function(){
				dijit.byId("rejectionReason").setDisabled(false);
			});
		}
</script>
         </s:else>  
   	   </tr>
  </s:iterator>
</table>
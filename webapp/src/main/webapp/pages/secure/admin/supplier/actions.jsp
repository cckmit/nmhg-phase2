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
   
        
<table class="grid" cellspacing="0" celpadding="0">
   	<s:iterator value="%{getAllTransitions(recoveryClaim)}" id="task" status="itr">
    <tr>
        <td width="2%">
            <input type="radio" id="transitionTaken_<s:property value='%{#itr.index}' />" 
            	name="transitionTaken" value='<s:property/>' class="processor_decesion"  
            	<s:if test="%{transitionTaken.equals(top)}">checked="checked"</s:if> ></input>
            <script type="text/javascript">
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
            	    if(dojo.byId("transitionTaken_"+index).value!='Cannot Recover'){
            	    	dojo.connect(dojo.byId("transitionTaken_"+index),"onclick",function(){
            	    		dijit.byId("cannotRecoverReason").setDisabled(true);
            	    		dijit.byId("cannotRecoverReason").setDisplayedValue("--Select--");
            	    		dojo.style("checkBoxForPartReturnFromSupplier","display", "none");
            	    		dojo.byId("selectCheckOrNot").checked=false;
            	    	});
            	    }else{
            	    	dojo.connect(dojo.byId("transitionTaken_"+index),"onclick",function(){
            	    		dijit.byId("cannotRecoverReason").setDisabled(false);
            	    		dojo.style("checkBoxForPartReturnFromSupplier","display", "block");
            	    	});
            	    }
            	    if(dojo.byId("transitionTaken_"+index).value!='Transfer'){
            	    	dojo.connect(dojo.byId("transitionTaken_"+index),"onclick",function(){
            	    		dijit.byId("transferTo").setDisabled(true);
            	    		dijit.byId("transferTo").setDisplayedValue("--Select--");	
            	    	});
            	    }else{
            	    	dojo.connect(dojo.byId("transitionTaken_"+index),"onclick",function(){
            	    		dijit.byId("transferTo").setDisabled(false);
            	    	});
            	    }
                    dojo.addOnLoad(function(){
                    	var index=<s:property value="%{#itr.index}"/>;
                    if((dojo.byId("transitionTaken_"+index).value=='Transfer')&& !(dojo.byId("transitionTaken_"+index).checked)){
                    	dijit.byId("transferTo").setDisabled(true);
                   };
                   if((dojo.byId("transitionTaken_"+index).value=='Accept')&& !(dojo.byId("transitionTaken_"+index).checked)){
                  	 dijit.byId("acceptanceReason").setDisabled(true);
                  };
                  if((dojo.byId("transitionTaken_"+index).value=='Cannot Recover')&& !(dojo.byId("transitionTaken_"+index).checked)){
                   	 dijit.byId("cannotRecoverReason").setDisabled(true);
                   };
                   if(dojo.byId("transitionTaken_"+index).value=='Cannot Recover'&& dojo.byId("transitionTaken_"+index).checked && dojo.byId("checkBoxForPartReturnFromSupplier")){
                       	 dojo.style("checkBoxForPartReturnFromSupplier","display", "block");
                     };
                    });
	        </script>   
        </td>
        <td class="labelStyle" width="20%" nowrap="nowrap">
            <s:property/>
        </td>
        <td class="labelNormal" colspan="1">
           <s:if test="#task.equals('Accept')">
		         <s:select id="acceptanceReason" name="recoveryClaim.recoveryClaimAcceptanceReason" 
		         		cssClass="processor_decesion"  value="%{recoveryClaim.recoveryClaimAcceptanceReason.code}"
		         		list="getLovsForClass('RecoveryClaimAcceptanceReason',recoveryClaim)" 
		         		listKey="code" listValue="description" headerKey="" headerValue="%{getText('label.common.selectHeader')}"/>  
           </s:if>
           <s:elseif test="#task.equals('Transfer')">
           		 <s:select id="transferTo" name="transferToUser" 
		         		cssClass="processor_decesion" value="%{transferToUser.id.toString()}"
		         		list="availableRecoveryProcessors" 
		         		listKey="id" listValue="completeNameAndLogin" headerKey="-1" headerValue="%{getText('label.common.selectHeader')}"/>	
           </s:elseif>
           <s:elseif test="#task.equals('Cannot Recover')">
					<s:select id="cannotRecoverReason"
						name="recoveryClaim.recoveryClaimCannotRecoverReason"
						cssClass="processor_decision"
						value="%{recoveryClaim.recoveryClaimCannotRecoverReason.code}"
						list="getLovsForClass('RecoveryClaimCannotRecoverReason', recoveryClaim)"
						listKey="code" listValue="description" headerKey=""
						headerValue="%{getText('label.common.selectHeader')}" />
			
						<s:if test="taskName.equals('Supplier Response') && isRequestForPartBackFromSupplierCheckboxRequired()">
						   <div id="checkBoxForPartReturnFromSupplier" style="display:none;position:relative;left:190px;bottom:17px">
                            <s:checkbox
                                name="initiateReturnRequestFromSupplier" value="%{initiateReturnRequestFromSupplier}"
                                id="selectCheckOrNot"/>&nbsp;<s:text name="label.supplier.admin.part.return" />
                            </div>
                        </s:if>
				</s:elseif>
		   </td>
    </tr>
	</s:iterator>
</table>
<%@page contentType="text/html"%>
<%@ taglib prefix="sd" uri="/struts-dojo-tags"%>
<%@page pageEncoding="UTF-8"%>
<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="t" uri="twms"%>
<%@taglib prefix="u" uri="/ui-ext"%>
<%@taglib prefix="authz" uri="authz"%>

<html>
<head>
    <u:stylePicker fileName="yui/reset.css" common="true"/>
    <s:head theme="twms"/>
    <u:stylePicker fileName="common.css"/>
    <u:stylePicker fileName="form.css"/>
    <u:stylePicker fileName="base.css" />
     
     
    <script type="text/javascript">
  	  	dojo.require("dijit.layout.ContentPane");
      	dojo.require("dijit.layout.BorderContainer"); 

      	dojo.addOnLoad(function() {
          	<s:if test="claimSearchCriteria == null">
          	if(dojo.byId("ofClaimStatuscurrentState")) {
          		dojo.byId("ofClaimStatuscurrentState").checked=true;
          		dojo.byId("ofClaimStatusanyDateState").disabled=true;
          	}
          	if(dojo.byId("belongsToDealerGrouptrue")) {
          		dojo.byId("belongsToDealerGrouptrue").checked=true;
          	}
          	</s:if>
          	setOfClaimState();
                setCurrency();
      	});

		function setOfClaimState() {
			if(dojo.byId("ofDatedateLastModified") && dojo.byId("ofDatedateLastModified").checked) {
				dojo.byId("ofClaimStatusanyDateState").disabled=false;
			}else {
				dojo.byId("ofClaimStatuscurrentState").checked=true;
          		dojo.byId("ofClaimStatusanyDateState").disabled=true;
			}
		}
      	
      	function enableStates(checkbox){    	          	 
            var str = checkbox.id;
            size = str.length;
            var substring = str.slice(0,8);
            var count = str.slice(8,size);
      	 	for(i=0;i<count;i++){
      	 	    var temp = substring + i;
      	 	    var tcheckbox = dojo.byId(temp);
      	 	    if(checkbox.checked){
                     tcheckbox.checked=true;    	 	     
      	 	     } else {
      	 	          tcheckbox.checked=false;
      	 	     }
      	  	}
      	}
      	
		function enableText()
		{
			document.getElementById("savedQueryName").disabled=!document.getElementById("notATemporaryQuery").checked;
			if(document.getElementById("notATemporaryQuery").checked){
				document.getElementById("savedQueryName").value='';
			}
			return true;
		}
		
    </script>
<style>
.selectboxWidth{
width:164%;
}
.textareaWidth{
width:53%;
}
td{
padding-bottom:10px;
}
</style>
</head>
<u:body smudgeAlert="false">
	
  <div dojoType="dijit.layout.BorderContainer" style="width:99%;height: 100%; overflow-y:auto;" id="root" >         
      <div dojoType="dijit.layout.ContentPane" region="client" id="content">
  <div class="policy_section_div" style="width:100%">
     <u:actionResults/>
	<s:fielderror theme="xhtml" />
  <div id="dcap_pricing_title" class="section_header"  >
            <s:text name="title.claim.searchClaim"/>
      </div>
        <form action="validatePreDefinedClaimSearchFields.action" method="POST" onsubmit="setCurrency()">
	<s:hidden name="context" />
	<s:hidden name="queryId" />
	<s:hidden name="folderName"/>
	<s:hidden name="tableHeadData" />
		
		<table  cellspacing="0" cellpadding="0" class="grid"  style="margin:0px 5px 5px 5px;width:99%">
			
			<tr> 
		    <s:if test="(getLoggedInUser().businessUnits).size>1">
			 	 <td class="searchLabel labelStyle"><s:text name="label.common.businessUnit" />:</td>
			 	 <td nowrap="nowrap" colspan="2" >
		 	 	    <s:iterator value="businessUnits" status="buItr">
		 	 	        <s:if test="claimSearchCriteria.selectedBusinessUnits[#buItr.index] != null" >
		 	 	       	 	<input type="checkbox" 
			 				       name="claimSearchCriteria.selectedBusinessUnits[<s:property value="#buItr.index"/>]"
				 	 		        value="<s:property value="name" />" checked="true"/>
				 	 		        <s:property value="name"/>&nbsp;&nbsp;&nbsp;
			 				 		    
			 			 </s:if>
		 				 <s:else> 				 
			 				<input type="checkbox" 
			 				       name="claimSearchCriteria.selectedBusinessUnits[<s:property value="#buItr.index"/>]"
				 	 		       value="<s:property value="name" />" /> 
				 	 		       <s:property value="name"/>&nbsp;&nbsp;&nbsp;
			 			</s:else>
		 	 	   </s:iterator>	 
	   	 	 	 </td>
	   	 	 </s:if> 		   
	    </tr>
		
	
		<tr>
			<td class="searchLabel labelStyle"><s:text name="label.common.claimType" />:</td>
			<td colspan="2">
			   <s:iterator value="sortedClaimTypes" status="claimItr">
			      <s:if test="claimSearchCriteria.claimType[#claimItr.index] != null" >
			         <input type="checkbox" 
			 				name="claimSearchCriteria.claimType[<s:property value="#claimItr.index"/>]"
				 	 		value="<s:property value="sortedClaimTypes[#claimItr.index].name()" />" checked="true"/>
				 	 		<s:property value="getText(sortedClaimTypes[#claimItr.index].displayType)"/>&nbsp;&nbsp;&nbsp;
			      </s:if>
			      <s:else>
			          <input type="checkbox" 
			 				name="claimSearchCriteria.claimType[<s:property value="#claimItr.index"/>]"
			 				value="<s:property value="sortedClaimTypes[#claimItr.index].name()" />" />
				 	 		<s:property value="getText(sortedClaimTypes[#claimItr.index].displayType)"/>&nbsp;&nbsp;&nbsp;
			      </s:else>
			   </s:iterator>
			</td>
		</tr>
		<s:if test="isInternalUser()">
		
	
		
		<tr>
			<td class="searchLabel labelStyle" valign="top"><s:text name="label.claimSearch.users" />:</td>
			<td class="searchLabel" ><%-- <span id="userIds" tabindex="0"> --%><s:select name="claimSearchCriteria.userIds" list="preDefinedClaimsSearchFormData.userList"  
			    listKey="id" listValue="completeNameAndLogin" multiple="true" size="5" /><%-- </span> --%>
			   <%-- <span dojoType="dijit.Tooltip" connectId="userIds"><s:text
						name="claim.preDefineSearch.users.list" /></span> --%> </td>
		</tr>
		
	    <tr>
			<td class="searchLabel labelStyle" valign="top"><s:text name="label.claimSearch.assignTo" />:</td>
			<td class="searchLabel" ><%-- <span id="assignToUserIds" tabindex="0"> --%> <s:select name="claimSearchCriteria.assignToUserIds" list="preDefinedClaimsSearchFormData.assignToUserList"  
			    listKey="id" listValue="completeNameAndLogin" multiple="true" size="5" /><%-- </span> --%>
			   <%--  <span dojoType="dijit.Tooltip" connectId="assignToUserIds"><s:text
						name="claim.preDefineSearch.AssignTo.users.list" /></span> --%>
			    </td>
		</tr>
		
		<tr>
			<td class="searchLabel labelStyle"><s:text name="label.claim.dealerName" />:</td>
			<td class="searchLabel"><s:textfield
				name="claimSearchCriteria.dealerName"
				id="claimSearchCriteria.dealerName" /></td>
		</tr>
		
	
		<tr>
			<td class="searchLabel labelStyle"><s:text name="label.common.dealerNumber" />:</td>
			<td class="searchLabel"><s:textfield
				name="claimSearchCriteria.dealerNumber"
				id="claimSearchCriteria.dealerNumber" /></td>
		</tr>
<%--  
                <tr>
                    <td class="searchLabel labelStyle"><s:text name="label.common.dealerGroupLabel" />:</td>
                
				<td class="searchLabel" colspan="2">
                	<sd:autocompleter id='suggestOrgDealerGroups' href='suggest_org_dealer_groups.action' name='claimSearchCriteria.dealerGroup' showDownArrow='false' />
                </td>
                </tr>
--%>	
		<tr>
       	   <td class="searchLabel labelStyle"><s:text name="label.common.buildDate"/>:
       	   &nbsp;&nbsp;&nbsp;&nbsp;
       	    <s:text name="label.common.from"/>:</td>       	   
           <td class="labelStyle" width="5%"><sd:datetimepicker name='claimSearchCriteria.buildForm' id='buildFromDate' />
	       <s:text name="label.common.to"/>:
	       </td>
	       <td colspan="2">
               <sd:datetimepicker name='claimSearchCriteria.buildTo' id='buildToDate' />
	       </td>
	     </tr>
	
       	 <tr>
			<td class="searchLabel labelStyle" valign="top"><s:text	name="label.inventory.manufacturingSiteInventory" />:</td>
			<td><s:select name="claimSearchCriteria.manufacturingSite" 
				list="preDefinedClaimsSearchFormData.manufacturingSiteList"  cssClass="select"
				listKey="id" listValue="description" multiple="true" size="5" /></td>
		</tr>
		</s:if>	
		
	<s:if test="loggedInUserAnEnterpriseDealer">
    <tr>
        <td class="searchLabel labelStyle" width="20%" valign="top"><s:text name="label.common.childDealer"/>:</td>
        <td>
            <s:select name="claimSearchCriteria.childDealers" list="childDealerShip"
                      listKey="id" listValue="name" multiple="true" size="6" cssStyle="width:300px;">
            </s:select>
        </td>
    </tr>
    </s:if>
<tr>
    <td class="searchLabel labelStyle"><s:text name="columnTitle.common.productType"/>:</td>
    <td><s:select name="claimSearchCriteria.productType"
                  id="claimSearchCriteria.productType"
                  listKey="name" listValue="itemGroupDescription" list="productTypes" emptyOption="true"/></td>
   
</tr>
<tr>
    <td class="searchLabel labelStyle"><s:text name="columnTitle.common.products"/>:</td>
    <td><s:select name="claimSearchCriteria.productGroupCode"
                  id="claimSearchCriteria.products"
                  listKey="groupCode" listValue="groupCode" list="productCodes" emptyOption="true"/></td>
   
</tr>
<tr>
    <td class="searchLabel labelStyle"><s:text name="label.common.modelWithProductCode"/>:</td>
    <td><s:select name="claimSearchCriteria.modelNumber"
                  id="claimSearchCriteria.modelNumber"
                  listKey="name" listValue="itemGroupDescription" list="modelTypes" emptyOption="true"/></td>
    
</tr>
		<tr>
			<td class="searchLabel labelStyle"><s:text name="label.common.claimNumber" />:</td>
			<td class="searchLabel"><s:textfield
				name="claimSearchCriteria.claimNumber"
				id="claimSearchCriteria.claimNumber" /></td>
		</tr>
		
		<tr>
			<td class="searchLabel labelStyle"><s:text name="label.common.historicalClaimNumber" />:</td>
			<td class="searchLabel"><s:textfield
				name="claimSearchCriteria.historicalClaimNumber"
				id="claimSearchCriteria.historicalClaimNumber" /></td>
		</tr>
		
		
	
		
		<tr>
			<td class="searchLabel labelStyle" nowrap="nowrap"><s:text
				name="label.common.invoiceNumber" />:</td>
			<td class="searchLabel"><s:textfield
				name="claimSearchCriteria.invoiceNumber"
				id="claimSearchCriteria.invoiceNumber" /></td>
		</tr>
		
	
		<tr>
			<td class="searchLabel labelStyle" nowrap="nowrap"><s:text
				name="label.common.workOrderNumber" />:</td>
			<td class="searchLabel"><s:textfield
				name="claimSearchCriteria.workOrderNumber"
				id="claimSearchCriteria.workOrderNumber" /></td>
		</tr>
		 <s:if test="%{showAuthorizationNumber()}">
		<tr>
			<td class="searchLabel labelStyle" nowrap="nowrap"><s:text
				name="label.common.authNumber" />:</td>
			<td class="searchLabel"><s:textfield
				name="claimSearchCriteria.authNumber"
				id="claimSearchCriteria.authNumber" /></td>
		</tr>
		</s:if>
		
		<tr>
			<td class="searchLabel labelStyle"><s:text name="label.common.serialNumber" />:</td>
			<td class="searchLabel"><s:textfield
				name="claimSearchCriteria.serialNumber"
				id="claimSearchCriteria.serialNumber" /></td>
		</tr>
        
         <tr>
            <td class="searchLabel labelStyle"><s:text name="label.common.creditMemoNumber"/>:</td>
           	<td class="searchLabel"><s:textfield 
           	    name="claimSearchCriteria.creditMemoNumber" 
           	    id="claimSearchCriteria.creditMemoNumber"/></td>
        </tr>
	<s:if test="enableWarrantyOrderClaims()"> 
        <tr>
        			<td class="searchLabel labelStyle" nowrap="nowrap"><s:text name="label.viewClaim.warrantyOrderClaim" />:</td>
        			<td><s:radio id="claimSearchCriteria.warrantyOrder"
        				name="claimSearchCriteria.warrantyOrder"
        				list="#{'false':'No','true':'Yes'}" listKey="key" listValue="value"
        				theme="twms" /></td>
        </tr>
	</s:if>
		<tr>
			<td class="searchLabel labelStyle"><s:text
				name="label.integration.fromDate" />:</td>
			<td class="searchLabel"><sd:datetimepicker name='claimSearchCriteria.fromDate' id='claimSearchCriteria.fromDate' value='%{claimSearchCriteria.fromDate}' /></td>
			<td class="searchLabel"><s:radio name="claimSearchCriteria.ofDate" id="ofDate"
				list="#{'filedOnDate':'label.common.dateOfClaim'}" listKey="key"
				onclick="setOfClaimState()" listValue="%{getText(value)}" theme="twms" /></td>
		</tr>

		<tr>
			<td class="searchLabel labelStyle"><s:text name="label.integration.toDate" />:</td>
			<td class="searchLabel" nowrap="nowrap"><sd:datetimepicker name='claimSearchCriteria.toDate' id='claimSearchCriteria.toDate' value='%{claimSearchCriteria.toDate}' /></td>
			<td class="searchLabel" nowrap="nowrap"><s:radio name="claimSearchCriteria.ofDate" id="ofDate"
				list="#{'dateLastModified':'label.common.lastModified'}" listKey="key"
				onclick="setOfClaimState()" listValue="%{getText(value)}" theme="twms" /></td>
		</tr>
		<s:if test="isInternalUser()">
		<tr>
			<td class="searchLabel labelStyle"></td>
			<td class="searchLabel" nowrap="nowrap"></td>
			<td class="searchLabel" nowrap="nowrap"><s:radio name="claimSearchCriteria.ofDate" id="ofDate"
				list="#{'lastUpdatedOnDate':'label.claim.lastUpdatedDate'}" listKey="key"
				onclick="setOfClaimState()" listValue="%{getText(value)}" theme="twms" /></td>
		</tr>
		</s:if>
		
		<tr>
			<td class="searchLabel labelStyle" valign="top">
				<s:text name="label.predefinedSearch.claimStatus"/>:
			</td>
		</tr> 			
		<tr>
			<td class="searchLabel" nowrap="nowrap">
				<s:radio id="ofClaimStatus" name="claimSearchCriteria.ofClaimStatus"
				list="#{'currentState':'label.predefinedsearch.claimStatus.current'}" listKey="key"
				listValue="%{getText(value)}" theme="twms" />
			</td>
		</tr>
		<tr>
			<td class="searchLabel" nowrap="nowrap">
				<s:radio id="ofClaimStatus" name="claimSearchCriteria.ofClaimStatus"
				list="#{'anyDateState':'label.predefinedsearch.claimStatus.anyDate'}" listKey="key"
				listValue="%{getText(value)}" theme="twms" />
			</td>
		</tr>	

		<s:if test="isInternalUser()">
			<s:set name="stateMap" value="preDefinedClaimsSearchFormData.claimStatesForInternal" />
		</s:if>
		<s:elseif test="isClaimStatusShownToDealer()">		
			<s:if test="isAllOpenStatesShownToDealer()">
				<s:set name="stateMap" value="preDefinedClaimsSearchFormData.claimAllOpenStatesForExternal" />
			</s:if>
			<s:else>
				<s:set name="stateMap" value="preDefinedClaimsSearchFormData.claimStatesForExternal" />
			</s:else>			
		</s:elseif>
		<s:else>
			<s:set name="stateMap" value="preDefinedClaimsSearchFormData.state" />
		</s:else>

		<s:iterator value="stateMap" status="iter3" >
		<s:if test="value.size() == 1">
		<tr>
			<td  nowrap="nowrap">&nbsp;</td>
			<td class="searchLabel" nowrap="nowrap" style=" width:25%;" valign="top" >
			<s:iterator value="value" status ="iter4" >
				<s:checkbox name="claimSearchCriteria.selectedStates['%{top.name()}']" 
					id="state_%{#iter3.index}_%{#iter4.index}" />  	 			
				<s:set name="claimDisplayStatusExt" value="state"/>
				<s:if test="#claimDisplayStatusExt == 'draft'">
					Draft
				</s:if>
				<s:else>
				<!-- Fix for SLMSPROD-629  -->
					<%-- <s:if test="'All Open'==state">
						<s:property value="state"/>  (<s:text name="label.claimSearch.excludeStates"></s:text>)
					</s:if>					
					<s:else> --%>
						<s:property value="state"/>
					<%-- </s:else> --%>					
				</s:else>
				<br/>
			</s:iterator>
			</td>
		</tr>
		<tr><td></td></tr>
		</s:if>
		</s:iterator>
		<s:iterator value="stateMap" status="iter1" >
		<s:if test="value.size() > 1">
		<tr>
			<td  nowrap="nowrap">&nbsp;</td>
			<td class="searchLabel" valign="top" >
				<s:checkbox name="claimSearchCriteria.openClosedState['%{key}']"  
					onclick="enableStates(this)" id="state_%{#iter1.index}_%{value.size()}"/>	  	 		  
				<s:label value="%{key}" />	  	 		 
			</td>
			<td class="searchLabel" nowrap="nowrap" style=" width:25%;" valign="top" >
			<s:iterator value="value" status ="iter2" >
				<s:if test="top.name()=='IN_PROGRESS'">
					<s:checkbox name="claimSearchCriteria.inProgressState" 
						id="state_%{#iter1.index}_%{#iter2.index}" />
				</s:if>
				<s:else>
					<s:checkbox name="claimSearchCriteria.selectedStates['%{top.name()}']" 
						id="state_%{#iter1.index}_%{#iter2.index}" />
				</s:else>
				<s:set name="claimDisplayStatus" value="state"/>
				<s:if test="#claimDisplayStatus == 'on hold'">
					<s:text name="label.jbpm.task.on.hold"/>
				</s:if>
				<s:elseif test="#claimDisplayStatus == 'on hold for part return'" >
					<s:text name="label.jbpm.task.on.hold.part.return"/>
				</s:elseif>
				<s:else>
					<s:property value="state"/>
				</s:else>
				<br/>
			</s:iterator>
			</td>
		</tr>
		<tr><td></td></tr>
		</s:if>
		</s:iterator>
		<tr>
			<td style="padding:0">&nbsp;</td>
		</tr>
		
		<s:if test="isInternalUser()">
			<tr>
				<td class="searchLabel labelStyle">
					<s:text name="label.claimSearch.ruleFailure" />:			 
				</td>
				<td class="searchLabel" nowrap="nowrap" >
					<s:checkbox name="claimSearchCriteria.duplicateClaim" />
					<s:text name="label.claimSearch.duplicateClaim" />
				</td>
			</tr>
			<tr>
				<td class="searchLabel labelStyle">
					<s:text name="label.integration.returnPartStatus" />:			 
				</td>
				
				<s:iterator value="preDefinedClaimsSearchFormData.partReturnStatusesForInternal" status="partIter">
				<s:if test="#partIter.index != 0 && #partIter.index%3 == 0">
					</tr>
					<tr>
						<td class="searchLabel labelStyle"></td>
				</s:if>
				<td class="searchLabel" nowrap="nowrap" >
					<s:checkbox name="claimSearchCriteria.partStatusMap['%{top.name()}']"  />
					<s:property value="status"/>
				</td>
				</s:iterator>
			</tr>
		</s:if>
		<s:else>
			<tr>
				<td class="searchLabel labelStyle" nowrap="nowrap" valign="top">
					<s:text name="label.integration.returnPartStatus" />:			 
				</td>
				<s:iterator value="preDefinedClaimsSearchFormData.partReturnStatuses" status="iter1">
				<td class="searchLabel" nowrap="nowrap" valign="top" >
					<s:checkbox name="claimSearchCriteria.partStatusMap['%{key.name()}']"/>
					<s:property value="key.status" />
				</td>
				</s:iterator>
			</tr>  	 	
       </s:else>
       
	   	<tr>
			<td class="searchLabel labelStyle"></td>
			<td class="searchLabel" colspan="2" width="20%"><s:radio id="claimSearchCriteria.restrictSearch"
				name="claimSearchCriteria.restrictSearch"
				list="#{'true':'Restrict Search to claims within 3 years','false':
				'Search all claims'}" listKey="key" listValue="value"
				theme="twms"   /></td>
		</tr>
		
		<tr>
			<td class="searchLabel labelStyle" valign="top"><s:text
				name="label.claimSearch.settlementCodesForOnHold" />:</td>
			<td class="searchLabel" colspan="3"><s:select name="claimSearchCriteria.onHoldReason"
				list="preDefinedClaimsSearchFormData.onHoldReasonList"  
				 listKey="id" listValue="description" multiple="true" size="3"  cssClass="textareaWidth"/></td>
		</tr>
		<tr>
			<td class="searchLabel labelStyle" valign="top"><s:text
				name="label.claimSearch.settlementCodesForForwarded" />:</td>
			<td class="searchLabel" colspan="3"><s:select name="claimSearchCriteria.forwaredReason"
				list="preDefinedClaimsSearchFormData.forwardedReasonList"  
				 listKey="id" listValue="description" multiple="true" size="3"  cssClass="textareaWidth"/></td>
		</tr>
      
		<tr>
			<td class="searchLabel labelStyle" valign="top"><s:text
				name="label.viewClaim.acceptanceReason" />:</td>
			<td class="searchLabel" colspan="3"><s:select name="claimSearchCriteria.acceptanceReason"
				list="preDefinedClaimsSearchFormData.acceptanceReasonList"  
				 listKey="id" listValue="description" multiple="true" size="3"  cssClass="textareaWidth"/></td>
		</tr>
		
		<tr>
			<td class="searchLabel labelStyle" valign="top"><s:text
				name="label.viewClaim.rejectionReason" />:</td>
			<td class="searchLabel" colspan="3"><s:select name="claimSearchCriteria.rejectionReason"
				list="preDefinedClaimsSearchFormData.rejectionReasonList"  
				 listKey="id" listValue="description" multiple="true" size="3"  cssClass="textareaWidth"/></td>
		</tr>
		
<s:if test="isInternalUser()">
		<tr>
			<td class="searchLabel labelStyle" valign="top"><s:text
				name="label.claimSearch.accountabilityCodes" />:</td>
			<td colspan="3" ><s:select name="claimSearchCriteria.accountabilityCodeList" 
				list="preDefinedClaimsSearchFormData.accountabilityCodeList"  
				listKey="id" listValue="description" multiple="true" size="3" cssClass="textareaWidth"/></td>
		</tr>
		
		<tr>
			<td class="searchLabel labelStyle" nowrap="nowrap" valign="top"><s:text
				name="label.claimSearch.campaignCode" />:</td>
			<td colspan="3"><s:select name="claimSearchCriteria.campaignList"
				list="preDefinedClaimsSearchFormData.campaignList"
				listKey="id" listValue="code" multiple="true" size="3" cssClass="textareaWidth"/></td>
		</tr>
		<tr>
			<td class="searchLabel labelStyle" nowrap="nowrap" valign="top"><s:text
				name="label.claimSearch.dealerGroups" />:</td>
			<td colspan="3"><s:select name="claimSearchCriteria.dealerGroups"
				list="preDefinedClaimsSearchFormData.dealerGroups"
				listKey="id" listValue="name" multiple="true" size="3" cssClass="textareaWidth"/></td>
		</tr>
		<tr>
			<td class="searchLabel labelStyle" nowrap="nowrap" valign="top"><s:text
				name="label.claimSearch.partGroups" />:</td>
			<td colspan="3"><s:select name="claimSearchCriteria.partGroups"
				list="preDefinedClaimsSearchFormData.partGroups"
				listKey="id" listValue="name" multiple="true" size="3" cssClass="textareaWidth"/>
				</td>
		</tr>

		
		<tr>
			<td class="searchLabel labelStyle"><s:text
				name="label.claimSearch.resubmitted" />:</td>
			<td><s:radio id="claimSearchCriteria.resubmitted"
				name="claimSearchCriteria.resubmitted"
				list="#{'false':'No','true':'Yes'}" listKey="key" listValue="value"
				theme="twms" /></td>
		</tr>

		<tr>
		<td class="searchLabel labelStyle"><s:text
				name="label.claimSearch.manuallyReviewed" />:</td>
				<td><s:radio id="claimSearchCriteria.manuallyReviewed"
				name="claimSearchCriteria.manuallyReviewed"
				list="#{'false':'No','true':'Yes'}" listKey="key" listValue="value"
				theme="twms" /></td>
		</tr>
		<s:if test="displayClaimSearchBasedOnSMR()" >
		<tr>
			<td class="searchLabel labelStyle" nowrap="nowrap"><s:text name="label.viewClaim.smr" />:</td>
			<td><s:radio id="claimSearchCriteria.serviceManagerReview"
				name="claimSearchCriteria.serviceManagerReview"
				list="#{'false':'No','true':'Yes'}" listKey="key" listValue="value"
				theme="twms" /></td>
		</tr>
		</s:if>
	  <s:if test="(!isLoggedInUserADealer() || isDealerAllowedToFileNCRWith30Days()) && displayNCRandBT30DayNCROnClaimPage()">
      <tr>
			<td class="searchLabel labelStyle" nowrap="nowrap"><s:text
				name="label.include.ncr.claims" />:</td>
			<td><s:radio id="claimSearchCriteria.includeNCRClaims"
				name="claimSearchCriteria.includeNCRClaims"
				list="#{'false':'No','true':'Yes'}" listKey="key" listValue="value"
				theme="twms" /></td>
		</tr>
		</s:if>
		<tr>
			<td class="searchLabel labelStyle"><s:text name="label.common.causalPart" />:</td>
			<td class="searchLabel"><s:textfield
				name="claimSearchCriteria.causalPart" 
				id="claimSearchCriteria.causalPart" /></td>
		</tr>
		<%--Change for SLMS-776 adding date code --%>
		<tr>
			<td class="searchLabel labelStyle"><s:text name="label.common.dateCode" />:</td>
			<td class="searchLabel"><s:textfield
				name="claimSearchCriteria.dateCode"
				id="claimSearchCriteria.dateCode"/></td>
		</tr>  
		

		<tr>
			<td class="searchLabel labelStyle"><s:text name="label.common.faultCode" />:</td>
			<td class="searchLabel"><s:textfield
				name="claimSearchCriteria.faultCode"
				id="claimSearchCriteria.faultCode"/></td>
		</tr>                
  	 	</s:if>	
  	 	<s:else>
       	 	<authz:ifUserInRole roles="dealer"> 
	 <authz:ifUserNotInRole roles="processor">

	<tr>
	<td colspan="4">
     <table width="100%"   cellspacing="0" cellpadding="0" class="bgColor" style="margin-top:5px;border:0px">
	<tr>
	 <td colspan="2">
	 <div id="dcap_pricing_title" class="mainTitle" style="background:#F3FBFE">
           <s:text name="label.common.advancedSearch"/>
    </div>
	<div class="borderTable" style="background-color:#F3FBFE"></div>
	 </td>
	 </tr>
	 <s:if test="displayClaimSearchBasedOnSMR()" >
	 		<tr>
			<td class="searchLabel labelStyle"  width="21%" nowrap="nowrap"><s:text name="label.viewClaim.smr" />:</td>
			<td style="padding-top:2px;"><s:radio id="claimSearchCriteria.serviceManagerReview"
				name="claimSearchCriteria.serviceManagerReview"
				list="#{'false':'No','true':'Yes'}" listKey="key" listValue="value"
				theme="twms" /></td>
		</tr>
	</s:if>
		<tr>
			<td class="searchLabel labelStyle"><s:text name="label.common.causalPart" />:</td>
			<td class="searchLabel"><s:textfield
				name="claimSearchCriteria.causalPart" 
				id="claimSearchCriteria.causalPart" /></td>
		</tr>
		
		<%--Change for SLMS-776 adding date code --%>
		<tr>
			<td class="searchLabel labelStyle"><s:text name="label.common.dateCode" />:</td>
			<td class="searchLabel"><s:textfield
				name="claimSearchCriteria.dateCode"
				id="claimSearchCriteria.dateCode"/></td>
		</tr>  
		
		<tr>
			<td class="searchLabel labelStyle"><s:text name="label.common.faultCode" />:</td>
			<td class="searchLabel"><s:textfield
				name="claimSearchCriteria.faultCode"
				id="claimSearchCriteria.faultCode"/></td>
		</tr>
	</table>
	</td>
	</tr>
	</authz:ifUserNotInRole>
	</authz:ifUserInRole>
	</s:else>
        <tr>
			<td class="searchLabel labelStyle"><s:text name="label.multiCar.totalClaimAmount" />:</td>
                        <td class="searchLabel">
				<s:select emptyOption="true" id="totalAmountOperator" name="claimSearchCriteria.totalAmountOperator" list="totalAmountOperators" listKey="value" listValue="key" />
				</td>

                        <td class="searchLabel"><s:if test="loggedInUserAnInternalUser">
                                <s:select list="allCurrencies"
                                          listValue="currencyCode" emptyOption="true"
                                          name="dummyCurrency"
                                          value="%{claimSearchCriteria.totalAmountClaim.breachEncapsulationOfCurrency()}"
                                          id="dummyCurrency"/>
                            </s:if>
                            <s:else>
                                <s:hidden  id="dummyCurrency" value="%{loggedInUsersDealership.preferredCurrency}"/>
                                <s:property  value="loggedInUsersDealership.preferredCurrency"/>
                            </s:else>
                            <s:hidden  id="totalAmountClaim.currency" name="claimSearchCriteria.totalAmountClaim"/>
                            </td>
                        
                        <td class="searchLabel"><s:textfield
				name="claimSearchCriteria.totalAmountClaim"
                                value="%{claimSearchCriteria.totalAmountClaim.breachEncapsulationOfAmount()}"
				id="totalAmountClaim.amount"/></td>
		</tr>
<authz:ifUserInRole roles="processor">
<tr>
	<td class="searchLabel labelStyle"> 
		<s:text name="label.common.itaTruckClass"></s:text>
	</td>
	<td> 
		<s:select id="claimSearchCriteria.itaTruckClass" cssStyle="width:180px;" name="claimSearchCriteria.groupCodeForProductFamily" 
    			  list="listItaTruckClass()" 
    			  headerKey="" headerValue="%{getText('label.common.selectHeader')}" />     			  
	</td>
</tr>	
</authz:ifUserInRole>	

<tr>    
    <td class="searchLabel labelStyle"><s:text name="label.common.MarketingGroupCode"/>:</td>    
    <td>
        <s:textfield name="claimSearchCriteria.marketingGroupCode" id="marketingGroupCode"/>
    </td>
</tr>
	<tr>
	<td class="searchLabel labelStyle" align="right" style="padding-right:12px;"  ><s:text name="button.common.saveSearch" />
	<s:checkbox	cssClass="buttonGeneric" name="notATemporaryQuery" id="notATemporaryQuery"
							value="notATemporaryQuery" onclick="enableText()">
					</s:checkbox>
	</td>
	<td class="labelStyle" >
					<s:if test = "savedQueryName!=null" >
						<s:textfield name="savedQueryName" id="savedQueryName" value="%{savedQueryName}"></s:textfield>
					</s:if>
					<s:else>
						<s:textfield name="savedQueryName"  id="savedQueryName" disabled="true" value="Name of the Query" ></s:textfield>
					</s:else>
					</td>
		
	</td>
	<td >&nbsp;</td>
	</tr>
	<tr>
		<td width="17%"></td>
				<td class="label" valign="bottom">
					<s:reset label="reset" cssClass="buttonGeneric"></s:reset>
					<s:submit id="savedButton" cssClass="buttonGeneric" value="%{getText('button.common.search')}"  />
					
				</td>
	</tr>
	</table>
        </form>
	</div>
    </div>
  </div>
			
	
	</u:body>
    <script type="text/javascript">
        var amount=dojo.byId('totalAmountClaim.amount');
        var dummyCurrency=dojo.byId('dummyCurrency');
        var currency =dojo.byId('totalAmountClaim.currency');
        function setCurrency(){
            if(amount.value == ''){
                currency.value='';
            }else{
                currency.value=dummyCurrency.value;                
            }
            return true;
        }

    </script>
</html>

<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="t" uri="twms"%>
<%@taglib prefix="u" uri="/ui-ext"%>



<html>
<head>
    <meta http-equiv="Context-Type" content="text/html; charset=ISO-8859-1">    
    <s:head theme="twms"/>
    <u:stylePicker fileName="common.css"/>
    <u:stylePicker fileName="adminPayment.css"/>
    <u:stylePicker fileName="form.css"/>
    <u:stylePicker fileName="base.css"/>
    <u:stylePicker fileName="warrantyForm.css"/>    

</head>
<u:body>
<script type="text/javascript"> 
		dojo.require("dijit.layout.ContentPane");
		var claimSize='<s:property value="recoveryClaims.size"/>';
		var attrSize='<s:property value="attributeMapper.listOfAttributes.size"/>';
		dojo.addOnLoad(function(){			
		dojo.connect(dojo.byId("alinkclickable"),"onclick", function(){
		 var id=document.getElementById("domainPredicateId").value;
		 var url = "detail_view_search_expression.action";
		 url += "?id=" + id;
		 var contextName=document.getElementById("contextName").value;
		 url += "&context="+contextName;
		 var savedQueryId=document.getElementById("savedQueryId").value;
		 url += "&savedQueryId="+savedQueryId;
		 var isMultiClaimMaintenance=document.getElementById("isMultiRecClaimMaintainace").value;
		 url += "&isMultiRecClaimMaintainace="+isMultiClaimMaintenance;		 
		 parent.publishEvent("/tab/reload", {
		 	label: "Multi Claim Maintenance",
			url: url,
			decendentOf:"Home",
		 	tab: getTabHavingId(getTabDetailsForIframe().tabId)});		
		});
		dojo.connect(dojo.byId("masterCheckboxForClaims"),"onclick",function(){
			if(dojo.byId("masterCheckboxForClaims").checked){
				for(var i=0;i<claimSize;i++){				
				dojo.byId("claim_"+i).checked=true;
				}
			}else{
				for(var i=0;i<claimSize;i++){				
				dojo.byId("claim_"+i).checked=false;
				}
			}
		});
		dojo.connect(dojo.byId("masterCheckboxForAttributes"),"onclick",function(){
			if(dojo.byId("masterCheckboxForAttributes").checked){
				for(var i=0;i<attrSize;i++){				
				dojo.byId("listOfAttributes"+i).checked=true;
				}
			}else{
				for(var i=0;i<attrSize;i++){				
				dojo.byId("listOfAttributes"+i).checked=false;
				}
			}
		});
		}); 	   
    </script>

<div  style="height:100%;overflow:visible;overflow-Y:auto;width:100%;overflow-X:auto">
<u:actionResults/>

<s:if test="isMultiSupplierClaimsSelected()">
</s:if>


<s:form method="post" theme="twms" id="multipleClaimMaintenance" name="multipleClaimMaintenanceForm"
            		action="selectedRecClaims.action">
<s:hidden id="contextName" name="contextName"/>
<s:hidden id="savedQueryId" name="savedQueryId"/>
<s:hidden id="domainPredicateId" name="domainPredicateId"/>
<s:hidden id="isMultiClaimMaintenance" name="isMultiClaimMaintenance"/>
<s:hidden id="isMultiRecClaimMaintainace" name="isMultiRecClaimMaintainace"/>
<div class="policy_section_div">
<div class="alinkclickable">
<span id="alinkclickable"><s:text name="button.viewClaim.showSearchQuery" /> </span>
</div>

<div class="section_header" >
<s:text name="title.attributes.queryResults" />
</div>
<div style="overflow:auto;width:100%;">
<table  cellspacing="0" cellpadding="0" id="claim_details_table"  class="grid borderForTable" style="clear: both;margin:0px;">
    <thead>    
        <tr>        
            <th class="warColHeader" width="1%" style="padding: 0" align="center">
                <input type="checkbox" id="masterCheckboxForClaims" style="padding: 0"/>
            </th>        
            <th class="warColHeader" width="5%" class="non_editable"><s:text name="columnTitle.common.recClaimNo"/></th>
            <th class="warColHeader" width="10%" class="non_editable"><s:text name="label.common.model"/></th>
            <th class="warColHeader" width="5%"><s:text name="columnTitle.listContracts.contract_name"/></th>
            <th class="warColHeader" width="4%"><s:text name="columnTitle.partShipperPartsClaimed.supplier"/></th>
            <th class="warColHeader" width="5%"><s:text name="label.supplierNumber"/></th>
            <th class="warColHeader" width="10%"><s:text name="columnTitle.claimsSentToShipper.causal_part"/></th>
            <th class="warColHeader" width="10%"><s:text name="columnTitle.common.status"/></th>  
            <th class="warColHeader" width="10%"><s:text name="label.common.debitedAmount"/></th>
                                   
        </tr>
    </thead>
    <tbody>
    <s:if test="recoveryClaims.empty">
        <td align="center" colspan="13"><s:text name="No Claims Found" /></td>
    </s:if>
     <s:else>
     	
        <s:iterator value="recoveryClaims" status="claims"> 
        <s:hidden name="restoreRecClaimsList[%{#claims.index}]" value="%{id}"/>    
         <s:hidden name="recoveryClaims[%{#claims.index}]" value="%{id}"/>     
        <tr>        	
            <td align="center" valign="middle">
                <s:checkbox id="claim_%{#claims.index}"
                        name="recoveryClaims[%{#claims.index}].selected" value="%{selected}" />            
            </td>
            <td>
                <s:if test="documentNumber !=null && documentNumber.length() > 0" >
		                 	<s:property value="recoveryClaimNumber" />-<s:property
				        value="documentNumber" />
	                   	</s:if>
		         <s:else>
		                <s:property value="recoveryClaimNumber" />
		          </s:else>	  
            </td>
            <td>
                <s:property value="claim.claimedItems[0].itemReference.referredInventoryItem.ofType.model.name" />
            </td>
            <td>
                <s:property value="contract.name" />
            </td>
            <td>
                <s:property value="contract.supplier.name" />
            </td>
             <td>
                <s:property value="contract.supplier.supplierNumber" />
            </td>
            <td>
                <s:property value="claim.serviceInformation.causalPart.number" />
            </td>
            <td>
                <s:property value="recoveryClaimState.state" />
            </td> 
            <td>
                <s:property value="totalRecoveredCost" />
            </td>
        </tr>
       </s:iterator>
   </s:else>
   </tbody>
</table>
</div>
</div>

<div class="policy_section_div">
<div class="section_header" >
<s:text name="title.attributes.attributes"/>
</div>
<table  cellspacing="0" align="center" cellpadding="0" id="attribute_details_table" width="60%" class="grid" style="clear: both;">   
    
    <thead>    	
        <tr>        
            <th class="warColHeader" width="40%" style="padding: 0" align="center">
                <input type="checkbox" id="masterCheckboxForAttributes" style="padding: 0" />
            </th>        
            <th class="warColHeader" width="60%" class="non_editable"><s:text name="title.attributes.attributes"/></th>
        </tr>
    </thead>
    <tbody>        
    	<s:iterator value="attributeMapper.listOfAttributes" status="attributes">  
        <tr> 
        	<td align="center" valign="middle">            
                <s:checkbox id="listOfAttributes%{#attributes.index}" 
                        name="attributeMapper.listOfAttributes[%{#attributes.index}].selected"/>  
                        
            </td>       	
            <td>
            	<s:property value="name"/>
            </td>
        </tr>
        </s:iterator>
   </tbody>
</table>
</div>

<div dojoType="dijit.layout.ContentPane" layoutAlign="client" style="padding-bottom: 10px">
	<table class="buttons">
		<tr>
		<td>
		<center>
			 <s:submit value="%{getText('button.common.cancel')}" type="input" id="cancelButton"/>
                    <script type="text/javascript">
                        dojo.addOnLoad(function() {
                            dojo.connect(dojo.byId("cancelButton"), "onclick", closeMyTab);
                        });
                    </script>
			<s:submit id="Submit"  value="%{getText('button.common.submit')}" />
		</center>
		</td>
		</tr>
	</table>	
</div>

</s:form>
</u:body>


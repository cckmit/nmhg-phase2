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

<%@ taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="t" uri="twms"%>
<%@taglib prefix="u" uri="/ui-ext"%>


<script type="text/javascript">
    var rowCount =<s:property value = "inventoryItems.size" />;
    var claimDetail = '<s:property value="claim"/>'; 
    var idsInString='<s:property value="selectedItemsIds"/>'; 
    idsInString=idsInString.replace(/\s+/g,''); 
</script>

<script type="text/javascript" src="scripts/CheckBoxListControl.js"></script>
<script type="text/javascript" src="scripts/multiCar/multiCarInventorySelection.js"></script>

<s:form method="post" theme="twms" id="multiCarSearchResultsForm" name="searchMultipleCars"
        action="displayMultipleEquipInfo.action">
<a href="#redirectToTop" id="navigateToTopForErrors" ></a>
<a name="redirectToTop"></a>	
<s:hidden name="claimType" id="claim_type"/>
<div id="incompatibleInventoriesErrorSection" class="twmsActionResults" style="display:none">
	<div class="twmsActionResultsSectionWrapper twmsActionResultsErrors">
		<h4 class="twmsActionResultActionHead"><s:text name="error"/></h4>
		<ol>
			<li><s:text name="error.multiCar.incompatibleInventoriesSelected" /></li>
		</ol>
		<hr/>
	</div>
</div>
<input type="hidden" name ="claim" value="<s:property value="claim"/>"/>

<div style="overflow-Y:auto;overflow-X:none;height:365px;">
<table width="98%" class="repeat borderForTable">
    <thead>        
        <tr class="row_head">
        	<th width="4%" align="center" style="padding:0;margin:0"><input style="margin-left: 4px;" type="checkbox" id="masterCheckbox"/></th>
            <th width="16%"><s:text name="label.common.serialNumber"/></th>
            <th width="12%"><s:text name="label.common.model"/></th>
            <th width="8%"><s:text name="label.common.year"/></th>
            <th width="17%"><s:text name="customer.search.owner.name"/></th>
            <th width="13%"><s:text name="label.common.itemCondition"/></th> 
            <th width="13%"><s:text name="label.common.dealerNumber"/></th>                     
        </tr>
    </thead>
    <tbody>
    	<s:hidden name="selectedItemsIds" id="selectedIds"/>
    	<s:if test="inventoryItems.empty">
    	<tr >
    		<td colspan="7" align ="center">
    			<s:text name="label.multiCar.noInventoriesFound"/>
    		</td>
    	</tr>     
    	</s:if>
    	<s:else>
        <s:iterator value="inventoryItems" status="inventoryItems" id="inventoryList">
            <tr id="listCounter">
            	<td align="center" width="4%" nowrap="nowrap" valign="middle">
					<s:checkbox name="claim.claimedItems[%{#inventoryItems.index}].itemReference.referredInventoryItem"
						fieldValue="%{id}" id="%{#inventoryItems.index}" />
				</td>				
                <td width="15%"><s:property value="serialNumber" /></td>
                <td width="15%"><s:property value="ofType.model.name"/></td>
                <td width="8%"><s:property value="shipmentDate.breachEncapsulationOf_year()"/></td>                
                <td width="15%"><s:property value="%{getOwnedByName()}" /></td>               
                <td width="15%"><s:property value="conditionType.itemCondition" /></td>
                <td width="15%"><s:property value="owner.serviceProviderNumber" /></td>                  
            </tr>
        </s:iterator>
        </s:else>
    </tbody>
</table>
</div>



<table width="98%">
<tr>
<td><div><center><s:text name="search.multiCar.noOfPages"/>:<s:property value="totalpages"/></center></div></td>
<td>
<input type="text" name="enteredPageNo" id="enteredPageNo" />
<input type="button" value="<s:text name="label.common.go"/>" id="goButton" disabled="disabled" class="buttons"/>
</td>
</tr>
</table>
<script type="text/javascript">
 dojo.addOnLoad(function(){
 dojo.connect(dojo.byId("enteredPageNo"), "onblur", function() {
 	var pageNumber=dojo.byId("enteredPageNo").value;
 	var totalPages='<s:property value="totalpages"/>';
    			if(pageNumber!='' && parseInt(pageNumber)>=1 && parseInt(pageNumber)<=parseInt(totalPages)){
    				dojo.byId("goButton").disabled=false;
    			}
    		});
 dojo.connect(dojo.byId("goButton"),"onclick",function(){
 	getInventories(parseInt(dojo.byId("enteredPageNo").value)-1);
 	});
 });
</script>


<table width="98%">
<tr>
<td>
<div><center class="buttons">
<input type="button" value="<s:text name="label.common.previous"/>" id="previousButton" class="buttons"/></center>
</div>
</td>
<td>
<div>
<center>
<s:iterator value="pageNoList" status="pageCounter">
&nbsp;
<s:if test="pageNoList[#pageCounter.index] == (pageNo + 1)">
	<span id="page_<s:property value="%{intValue()-1}"/>">	
</s:if>	
<s:else>
	<span id="page_<s:property value="%{intValue()-1}"/>" style="cursor:pointer;text-decoration:underline">
</s:else>
<s:property value="%{intValue()}" />
<script type="text/javascript">
	dojo.addOnLoad(function(){	
		<s:if test="!previousButton">
			dojo.byId("previousButton").disabled=true;
		</s:if>
		<s:if test="!nextButton"> 
			dojo.byId("nextButton").disabled=true;
		</s:if>
		var counter = '<s:property value="%{intValue()}"/>'; 
		var index=parseInt(counter)-parseInt(1);
		var pageNo='<s:property value="pageNo"/>';
		if(index!=pageNo){
			dojo.connect(dojo.byId("page_"+index),"onclick",function(){
				getInventories(index);  
			});
		}	 
	});
</script>
</span>
</s:iterator>
</center>
</td>
<td>
<div><center class="buttons">
<input type="button" value="<s:text name="label.common.next"/>" id="nextButton" class="buttons"/></center>
</div>
</td>
</tr></table>

<script type="text/javascript">
function getInventories(index){
var typeOfClaim="<s:property value="claimType"/>";
var params={
	nextCounter:"<s:property value="nextCounter"/>",
	previousCounter:"<s:property value="previousCounter"/>",
	previousButton:"<s:property value="previousButton"/>",
	nextButton:"<s:property value="nextButton"/>",
	pageNo:index,
	dealerId:"<s:property value="dealerId"/>",
	selectedItemsIds:selectedItemsId,
	claim:"",
	claimType:typeOfClaim
};
<s:if test="claimType.equals('Campaign') || claimType.equals('Field Modification')">
params["campaignCode"]="<s:property value="campaignCode"/>";
</s:if>
<s:if test="multiCarSearch.customer.id!=null">
params["multiCarSearch.customer.id"]="<s:property value="multiCarSearch.customer.id"/>";
</s:if>
params["multiCarSearch.inventoryType"]="<s:property value="multiCarSearch.inventoryType"/>";
params["multiCarSearch.serialNumber"]="<s:property value="multiCarSearch.serialNumber"/>";
params["multiCarSearch.modelNumber"]="<s:property value="multiCarSearch.modelNumber"/>";
params["multiCarSearch.customer.companyName"]="<s:property value="multiCarSearch.customer.companyName"/>";
params["multiCarSearch.customer.corporateName"]="<s:property value="multiCarSearch.customer.corporateName"/>";
params["multiCarSearch.dealerNumber"]="<s:property value="multiCarSearch.dealerNumber"/>";
params["multiCarSearch.yearOfShipment"]="<s:property value="multiCarSearch.yearOfShipment"/>";
var url = "getInventoriesForPage.action?";
var targetContentPane=dijit.byId("searchResultTag");
targetContentPane.domNode.innerHTML="<div class='loadingLidThrobber'><div class='loadingLidThrobberContent'></div></div>";

twms.ajax.fireHtmlRequest(url, params, function(data) {
			var parentContentPane = dijit.byId("searchResultTag");
			parentContentPane.destroyDescendants();
			parentContentPane.setContent(data);				
		}
        );
}


function getInventoriesForNextButton(toUrl){
var typeOfClaim='<s:property value="claimType"/>';
var params={
	nextCounter:"<s:property value="nextCounter"/>",
	previousCounter:"<s:property value="previousCounter"/>",
	previousButton:"<s:property value="previousButton"/>",
	nextButton:"<s:property value="nextButton"/>",
	dealerId:"<s:property value="dealerId"/>",
	selectedItemsIds:selectedItemsId,
	claim:"",
	claimType:typeOfClaim
};
<s:if test="claimType.equals('Campaign')">
params["campaignCode"]="<s:property value="campaignCode"/>";
</s:if>

params["multiCarSearch.inventoryType"]="<s:property value="multiCarSearch.inventoryType"/>";
<s:if test="multiCarSearch.customer.id!=null">
params["multiCarSearch.customer.id"]="<s:property value="multiCarSearch.customer.id"/>";
</s:if>
params["multiCarSearch.serialNumber"]="<s:property value="multiCarSearch.serialNumber"/>";
params["multiCarSearch.modelNumber"]="<s:property value="multiCarSearch.modelNumber"/>";
params["multiCarSearch.customer.companyName"]="<s:property value="multiCarSearch.customer.companyName"/>";
params["multiCarSearch.customer.corporateName"]="<s:property value="multiCarSearch.customer.corporateName"/>";
params["multiCarSearch.dealerNumber"]="<s:property value="multiCarSearch.dealerNumber"/>";
params["multiCarSearch.yearOfShipment"]="<s:property value="multiCarSearch.yearOfShipment"/>";
var url = toUrl+".action?";
var targetContentPane=dijit.byId("searchResultTag");
targetContentPane.domNode.innerHTML="<div class='loadingLidThrobber'><div class='loadingLidThrobberContent'></div></div>";

twms.ajax.fireHtmlRequest(url, params, function(data) {
			var parentContentPane = dijit.byId("searchResultTag");
			parentContentPane.destroyDescendants();
			parentContentPane.setContent(data);				
		}
        );
}


</script>
<s:if test="inventoryItems.size != 0">
	<div  layoutAlign="bottom" style="padding-bottom: 3px; " id="submitContainer">
		<center class="buttons">
			
            
			<input type="button" value="<s:text name="button.common.select"/>"  id="btnSubmitInventories" disabled="true" />
		</center>
	</div>
</s:if>
</s:form>


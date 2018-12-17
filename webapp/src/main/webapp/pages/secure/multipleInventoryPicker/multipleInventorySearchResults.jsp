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

<!-- IMPORTANT! This script should always be placed before the inclusion of
 multipleInventorySelection.js. -->
<script type="text/javascript">
	dojo.require("twms.widget.DateTextBox");
	dojo.require("twms.widget.Tooltip"); 
	var selectedItems= [];
	var rowCount ="<s:property value = 'size' />";   
    var idsInString="<s:property value='selectedItemsIds'/>"; 
    idsInString=idsInString.replace(/\s+/g,'');
    if(idsInString!=null && !idsInString==""){
	    selectedItems=idsInString.split(",");
    } 
    function setSelectedInventories(){
    	selectedItemsCount = selectedItems.length;
   	 	for (var i=0; i< rowCount; i++) {
			var currentElement = dojo.byId(""+i);
          	var indexOf=dojo.indexOf(selectedItems,currentElement.value);
   			if(indexOf >= 0 && currentElement.disabled==false){
   				currentElement.checked=true;
   			}
   	 	}
   	}
   
</script>

<style type="text/css">
    input[type="checkbox"] {
        margin-top: 2px;
    }
</style>
<script type="text/javascript" src="scripts/multiInventoryPicker/multiInventoryCheckBoxListControl.js"></script>
<script type="text/javascript" src="scripts/multiInventoryPicker/multipleInventorySelection.js"></script>
<script type="text/javascript">
 dojo.addOnLoad(function() {
    	setSelectedInventories();
    	validateSubmission();
    });
</script>


<!-- The Form doesn't have any action defined, since that is set dynamically by the MultipleInventoryPicker widget
which uses this -->

<s:form method="post"  id="multiInventorySearchResultsForm" name="searchMultipleInventories">
<u:actionResults />
<div id="incompatibleInventoriesErrorSection" class="twmsActionResults" style="display:none">
	<div class="twmsActionResultsSectionWrapper twmsActionResultsErrors">
		<h4 class="twmsActionResultActionHead"><s:text name="label.common.errors"/></h4>
		<ol>
			<li><s:text name="error.multiCar.incompatibleInventoriesSelected" /></li>
		</ol>
		<hr/>
	</div>
</div>
<div style="overflow:auto; height:150px;">
<table  class="grid borderForTable" style="margin:5px;width:98%">
    <thead>        
        <tr>
        	<th class="warColHeader" width="2%" align="center" style="padding:0;margin:0">
                <input type="checkbox" id="masterCheckbox" style="border:none;" />
            </th>
            <th class="warColHeader" width="16%"><s:text name="label.common.serialNumber"/></th>
            <script>
            	if(dojo.byId("factoryOrderNumberSearch") && dojo.byId("factoryOrderNumberSearch").style.display!='none'){
        			dojo.html.show(dojo.byId("factoryOrderNumberHeader"));
       			 }
            </script>
            <th id="factoryOrderNumberHeader" class="warColHeader" width="25%" style="display:none"><s:text name="label.common.factoryOrderNumber"/></th>
            <th class="warColHeader" width="16%"><s:text name="label.common.model"/></th>
            <th class="warColHeader" width="8%"><s:text name="label.common.year"/></th>
            <th class="warColHeader" width="25%"><s:text name="customer.search.owner.name"/></th>
            <th class="warColHeader"><s:text name="label.common.itemCondition"/></th>
        </tr>
    </thead>
    <tbody>  
    	<s:property value = "" /> 	
    	<s:hidden name="selectedItemsIds" id="selectedIds"/>
    	<s:if test="inventoryItems.empty">
    	<tr >
    		<td colspan="7" align ="center">
    			<s:text name="label.multiCar.noInventoriesFound"/>
    		</td>
    	</tr>     
    	</s:if>
    	<s:else>    	
    	<!--  this is required to handle choosing one inventory from the search result scenario-->
    	<input type="hidden" name="size" value="<s:property value="size"/>" /> 
    	<s:if test="!sendInventoryId">       
    		<input type="hidden" name="inventoryItems" value="">
    	</s:if>        		
        <s:iterator value="inventoryItems" status="inventoryItems" id="inventoryList">
            <tr id="listCounter">
            	<td align="center" width="4%" nowrap="nowrap" valign="middle">
            	<s:if test="coverageAction.equals('MFGC')">
            	 	<input type="checkbox" name="inventoryItems" value="<s:property value="id" />" 
                           id="<s:property value="%{#inventoryItems.index}" />"  />
                </s:if>
                <s:else>
                	<input type="checkbox" name="inventoryItems" value="<s:property value="id" />" 
                           id="<s:property value="%{#inventoryItems.index}" />"  />
                </s:else>
                <s:if test="coverageAction.equals('MFWC')">
               
                <s:if test="%{!filterInventoriesWithAvailablePoliciesForUIDisplay(inventoryItems[#inventoryItems.index])}">
                   <script type="text/javascript">
                dojo.addOnLoad(function(){
                	var counter='<s:property value="%{#inventoryItems.index}" />';
                  dojo.byId(""+counter).disabled=true; 
                });
                
                 </script>
                </s:if>
                </s:if>
                <s:elseif test="coverageAction.equals('MFGC')">
                <s:if test="%{!filterInventoriesWithApplicableGWPolicyForUIDisplay(inventoryItems[#inventoryItems.index])}">
                <script type="text/javascript">
                
               dojo.addOnLoad(function(){
                	var counter='<s:property value="%{#inventoryItems.index}" />';
                	/* dojo.byId(""+counter).disabled=true; */
                });
              
                </script>
                </s:if>
                </s:elseif>
                <s:elseif test="coverageAction.equals('EXTWARPURCHASE')">
                <s:if test="%{!fetchAvailablePoliciesForUIDisplay(inventoryItems[#inventoryItems.index])}">
                <script type="text/javascript">
                dojo.addOnLoad(function(){
                	var counter='<s:property value="%{#inventoryItems.index}" />';
                	dojo.byId(""+counter).disabled=true;
                });
                
                </script>
                </s:if>
                </s:elseif>
                </td>
                <td width="16%"><s:property value="serialNumber" /></td>
                <td id="factoryOrderNumberColumn_<s:property value="%{#inventoryItems.index}" />" width="25%" style="display:none"><s:property value="factoryOrderNumber" /></td>
                <script>
            		if(dojo.byId("factoryOrderNumberSearch") && dojo.byId("factoryOrderNumberSearch").style.display!='none'){
        				dojo.html.show(dojo.byId("factoryOrderNumberColumn_"+<s:property value="%{#inventoryItems.index}" />));
       				 }
           		</script>
                <td width="16%"><s:property value="ofType.model.name"/></td>
                <td width="8%"><s:property value="shipmentDate.breachEncapsulationOf_year()"/></td>
                <td width="25%"><s:property value="%{getOwnedByName()}" /></td>
                <td>
                	<s:property value="conditionType.itemCondition" />
                </td>                
            </tr>
        </s:iterator>
        </s:else>
    </tbody>
</table>
<div>
<center>
<s:iterator value="pageNoList" status="pageCounter">
&nbsp;
<s:if test="pageNoList[#pageCounter.index] == (pageNo + 1)">
	<span id="page_<s:property value="%{#pageCounter.index}"/>"/>
</s:if>	
<s:else>
	<span id="page_<s:property value="%{#pageCounter.index}"/>" style="cursor:pointer;text-decoration:underline"/>
</s:else>
<s:property value="%{intValue()}" />
<script type="text/javascript">
	dojo.addOnLoad(function(){	
		var index = '<s:property value="%{#pageCounter.index}"/>';
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
</div>
</div>
<script type="text/javascript">
function getInventories(index){

var params={
	pageNo:index,
	selectedItemsIds:selectedItems
};
params["inventorySearchCriteria.dealerId"]="<s:property value="inventorySearchCriteria.dealerId"/>";
params["inventorySearchCriteria.dealerName"]="<s:property value="inventorySearchCriteria.dealerName"/>";
params["inventorySearchCriteria.dealerNumber"]="<s:property value="inventorySearchCriteria.dealerNumber"/>";
params["inventorySearchCriteria.productCode"]="<s:property value="inventorySearchCriteria.productCode"/>";
params["inventorySearchCriteria.serialNumber"]="<s:property value="inventorySearchCriteria.serialNumber"/>";
params["inventorySearchCriteria.customer.companyName"]="<s:property value="inventorySearchCriteria.customer.companyName"/>";
params["inventorySearchCriteria.customer.corporateName"]="<s:property value="inventorySearchCriteria.customer.corporateName"/>";
params["inventorySearchCriteria.itemModel"]="<s:property value="inventorySearchCriteria.itemModel"/>";
params["inventorySearchCriteria.modelNumber"]="<s:property value="inventorySearchCriteria.modelNumber"/>";
params["manageCoverageAction"]="<s:property value="manageCoverageAction"/>";

params["selectedGoodWillPolicy"]="<s:property value="selectedGoodWillPolicy" />";
params["inventorySearchCriteria.selectedBusinessUnits"]="<s:property value="inventorySearchCriteria.selectedBusinessUnits[0].toString()"/>";

<s:if test="coverageAction!=null">
params["coverageAction"]="<s:property value="coverageAction"/>";
</s:if>
<s:if test="inventorySearchCriteria.itemNumber!=null">
params["inventorySearchCriteria.itemNumber"]="<s:property value="inventorySearchCriteria.itemNumber"/>";
</s:if>
<s:if test="inventorySearchCriteria.inventoryType!=null">
params["inventorySearchCriteria.inventoryType.type"]="<s:property value="inventorySearchCriteria.inventoryType.type"/>";
</s:if>
<s:if test="inventorySearchCriteria.conditionTypeIs.itemCondition!=null">
params["inventorySearchCriteria.conditionTypeIs.itemCondition"]="<s:property value="inventorySearchCriteria.conditionTypeIs.itemCondition"/>";
</s:if>
<s:else>
params["inventorySearchCriteria.conditionTypeNot.itemCondition"]="<s:property value="inventorySearchCriteria.conditionTypeNot.itemCondition"/>";
</s:else>
var url = "getInventoryItemsForPage.action?";
<s:if test="coverageAction!=null && coverageAction.equals('MFWC')">
url="searchInventories_MFWC.action?"
</s:if>
<s:if test="coverageAction!=null && coverageAction.equals('MFGC')">
url="searchInventories_GWExtension.action?"
</s:if>
<s:if test="coverageAction!=null && coverageAction.equals('EXTWARPURCHASE')">
url="searchInventories_EWP.action?"
</s:if>
var targetContentPane=dijit.byId("searchResultsNode");
targetContentPane.destroyDescendants();
targetContentPane.domNode.innerHTML="<div class='loadingLidThrobber'><div class='loadingLidThrobberContent'></div></div>";

twms.ajax.fireHtmlRequest(url, params, function(data) {
			var parentContentPane = dijit.byId("searchResultsNode");
			parentContentPane.setContent(data);				
		}
        );
}
</script>
<s:if test="inventoryItems.size != 0">
	<div dojoType="dijit.layout.ContentPane" layoutAlign="bottom" style="padding-bottom: 3px; " id="submitContainer">
		<center class="buttons">
            <!-- Submission would be disabled by default. It would get enabled only when all the associated validation
             errors have been fixed. -->
            <s:submit value="%{getText('button.common.select')}" id="btnSubmitInventories" disabled="true" />
		</center>
	</div>
</s:if>

</s:form>



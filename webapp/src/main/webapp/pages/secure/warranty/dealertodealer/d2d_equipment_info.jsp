<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%@ taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="t" uri="twms"%>
<%@taglib prefix="u" uri="/ui-ext"%>
<u:actionResults/>
<script type="text/javascript">
var selectedItemsCount = 0;
dojo.addOnLoad(function(){		
	var numItems = '<s:property value="size"/>';
	dojo.require("twms.widget.Tooltip");	
	var validationTooltip; 
	validationTooltip = new twms.widget.Tooltip({
        showDelay: 100,
		connectId: ["performD2D"]
	});		    
	for (var i=0;i<numItems;i++) {		
		dojo.byId("item_"+i).checked=true;
		selectedItemsCount++;     		 
	}	
	submitButton = dojo.byId("performD2DButton");
	dojo.connect(dojo.byId("masterCheckBox"),"onclick",function(){
	 if(dojo.byId("masterCheckBox").checked) {
	    		selectedItemsCount = 0;    	    	
	    		for (var i=0;i<numItems;i++)
	    		{    	
	    			dojo.byId("item_"+i).checked=true;
	    			
	    		}
	    		  	
	    	} else {
	    		selectedItemsCount = numItems;
	    		for (var i=0;i<numItems;i++)
	    		{
	    			dojo.byId("item_"+i).checked=false; 
	    		    selectedItemsCount--;
	    		}
	    	}    	
	    	validateSubmission();
    	});
    	for (var i=0;i<numItems;i++) {
    		dojo.connect(dojo.byId("item_"+i), "onchange", function(event) {
	            if(event.target.checked) {
	                selectedItemsCount++;
	            } else {
	                selectedItemsCount--;
	            }
	            validateSubmission();
        	});
        }
   function validateSubmission() {    	  
			   var enableSubmission = true;
			   var message;			   
			   if(selectedItemsCount == 0) {
			       enableSubmission = false;
			       message = '<s:text name="error.retailMachineTransfer.selectedInventories"/>';
			   }
			   if(enableSubmission) {
			       submitButton.disabled=false;
			       validationTooltip.clearLabel();
			   } else {
			       submitButton.disabled=false;
			       validationTooltip.setLabel(message);
			   }
	   		 }
			dojo.connect(dojo.byId("performD2DButton"),"onclick",function(evt){submitConfForm(evt);});

	dojo.connect(dojo.byId("performD2DButton"),"onclick", function(){
		dojo.html.hide(dojo.byId("selectedInventoriesPane"));
		dojo.html.hide(dojo.byId("selectInventoriesLink"));
		dojo.html.show(dojo.byId("searchAgainLink")); 
		dojo.html.show(dojo.byId("selectedInventoriesTag"));				
		dijit.byId("selectedInventoriesTag").domNode.innerHTML="<div class='loadingLidThrobber'><div class='loadingLidThrobberContent'></div></div>";
       	});
  	dojo.connect(dojo.byId("searchAgainLink"),"onclick", function(){
   			dojo.html.show(dojo.byId("selectedInventoriesPane"));
   			dojo.html.show(dojo.byId("selectInventoriesLink"));
   			dojo.html.hide(dojo.byId("selectedInventoriesTag"));
			dojo.html.hide(dojo.byId("searchAgainLink"));
			dojo.html.hide(dojo.byId("searchedDealersDiv"));
			dojo.html.hide(dojo.byId("selectedDealerTag"));
			dojo.html.hide(dojo.byId("searchDealerAgainLink"));				
   		});
});

function submitConfForm(evt)
     {     	
     	dojo.stopEvent(evt);
     	dojo.xhrPost({
            url: "setSelectedInventoriesForD2D.action",
            form: dojo.byId("multipleInventoriesForm"),
            load:function(data, e) {            	
        		dijit.byId("selectedInventoriesTag").setContent(data);      	
        	}         
        });     
     }
</script>
<s:form method="post" theme="twms" id="multipleInventoriesForm" name="selectInventoriesForD2D" action="setSelectedInventoriesForD2D.action">
<table  cellspacing="0" cellpadding="0" id="equipment_details_table" width="96%" class="grid borderForTable" style="clear: both; margin:5px;">
    <thead>
        <tr>
            <th class="warColHeader" width="1%" align="center" style="padding:0;margin:0">
                <input type="checkbox" id="masterCheckBox" style="padding: 0" />
            </th>
            <th class="warColHeader" width="10%" class="non_editable"><s:text name="label.common.serialNumber"/></th>
            <th class="warColHeader" width="10%" class="non_editable"><s:text name="label.common.product"/></th>
            <th class="warColHeader" width="10%"><s:text name="label.common.model"/></th>
            <th class="warColHeader" width="10%"><s:text name="columnTitle.common.dealerName"/></th>
            <th class="warColHeader" width="10%"><s:text name="label.common.itemCondition"/></th>
            <s:if test="(getLoggedInUser().isInternalUser())">
            <th class="warColHeader" width="10%"><s:text name="label.retailMachineTransfer.buildDate"/></th>
            </s:if>
        </tr>
    </thead>
    <tbody>    
        <s:iterator value="selectedInventoryItems" status="selectedInventoryItemsIter">
        <input type="hidden" name="inventoryItems[<s:property value='%{#selectedInventoryItemsIter.index}'/>]" 
	      				 value='<s:property value="inventoryItem.id"/>'/>
        <tr>
            <td align="center" valign="middle">
                <s:checkbox id="item_%{#selectedInventoryItemsIter.index}"
                        name="selectedInventoryItems[%{#selectedInventoryItemsIter.index}].inventoryItem"
                       	fieldValue="%{inventoryItem.id}" />
            </td>
            <td>
                <u:openTab autoPickDecendentOf="true" id="equipment_%{inventoryItem.id}"
                tabLabel="Serial Number %{inventoryItem.serialNumber}"
                url="inventoryDetail.action?id=%{inventoryItem.id}">
                    <u style="cursor: pointer;">
                        <s:property value="inventoryItem.serialNumber" />
                    </u>
                </u:openTab>
            </td>
            <td>
                <s:property value="inventoryItem.ofType.product.name"/>
            </td>
            <td>
                <s:property value="inventoryItem.ofType.model.name" />
            </td>
            <td>
                <s:property value="inventoryItem.dealer.name" />
            </td>
            <td>
                <s:property value="inventoryItem.conditionType.itemCondition" />
            </td>             
            <s:if test="(getLoggedInUser().isInternalUser())">
            <td>
                <s:property value="inventoryItem.builtOn" />
            </td>
            </s:if>
           
        </tr>
       </s:iterator>     
   </tbody>
</table>
<div dojoType="dijit.layout.ContentPane" layoutAlign="bottom" style="padding-bottom: 3px;" id="performD2D">
<table width="100%">
<tr>
<td align="center" class="buttons">
  <s:submit id="performD2DButton"  value="%{getText('label.retailMachineTransfer.perform')}" />
</td>
</tr>
</table>	
</div>


</s:form>
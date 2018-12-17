<%--
  Created by IntelliJ IDEA.
  User: vikas.sasidharan
  Date: 20 Nov, 2007
  Time: 2:28:01 PM
--%>
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
		    submitButton = dojo.byId("performRMTButton");
		    validationTooltip = new twms.widget.Tooltip({
	        showDelay: 100,
	        connectId: ["performRMT"]
	        });
	    	for (var i=0;i<numItems;i++)
	    	{    	
	    		dojo.byId("item_"+i).checked=true;
	    		selectedItemsCount++;     		 
    		}
	    	validateSubmission();
	    	dojo.connect(dojo.byId("masterCheckboxForAllElements"),"onclick",function(){
	    	if(dojo.byId("masterCheckboxForAllElements").checked){
	            selectedItemsCount = numItems;
	            for (var j=0;j<numItems;j++){
	                dojo.byId("item_"+j).checked=true;	               
	            }
	    	}else{
	    	    selectedItemsCount = 0;
	    		for (var k=0;k<numItems;k++){
	    			dojo.byId("item_"+k).checked=false;
	    		}
	    	}    	
	    	validateSubmission();
	    	});
    		for (var i=0;i<numItems;i++)
    		{     	
    			dojo.connect(dojo.byId("item_"+i), "onchange", function(event) 
    			{
            		if(event.target.checked) 
            		{
                		selectedItemsCount++;
            		} 
            		else 
            		{
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
			       submitButton.disabled=true;
			       validationTooltip.setLabel(message);
			   }
	   		 }
        	new dojo.io.FormBind({         		
                    formNode: dojo.byId("multipleInventoriesForm"),
                    load: function(data, e) {
                        dijit.byId("selectedInventoriesTag").setContent(data);
                    },
                    error : function(error) {
                        dojo.body().style.cursor = "auto";
                    }
    			});			
				dojo.connect(dojo.byId("performRMTButton"),"onclick", function(){
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
</script>
<s:form method="post" theme="twms" id="multipleInventoriesForm" name="searchMultipleCars"
            		action="setSelectedInventoriesForRMT.action">
					
				
<table  cellspacing="0" cellpadding="0" id="equipment_details_table" width="95%" class="grid borderForTable" style="clear: both;">
    <thead>
        <tr>
            <th class="warColHeader" width="1%" align="center" style="padding:0;margin:0">
                <input type="checkbox" id="masterCheckboxForAllElements" style="padding: 0" />
            </th>
            <th class="warColHeader non_editable" width="10%"><s:text name="label.common.serialNumber"/></th>
            <th class="warColHeader non_editable" width="10%"><s:text name="label.common.product"/></th>
            <th class="warColHeader" width="10%"><s:text name="label.common.model"/></th>
            <th class="warColHeader" width="10%"><s:text name="columnTitle.common.dealerName"/></th>
            <th class="warColHeader" width="10%"><s:text name="label.common.itemCondition"/></th>
            <th class="warColHeader" width="10%"><s:text name="label.retailMachineTransfer.customerName"/></th>
            <th class="warColHeader" width="10%"><s:text name="label.warrantyAdmin.customerType"/></th>
            <th class="warColHeader" width="10%"><s:text name="label.retailMachineTransfer.buildDate"/></th>  
            <th class="warColHeader" width="10%"><s:text name="columnTitle.inventoryAction.delivery_date"/></th>        
        </tr>
    </thead>
    <tbody>    
    <s:if test="inventoryItemsForRMT.empty">
        <td align="center" colspan="10"><s:text name="label.warrantytransfer.noInventoriesSelected" /></td>
    </s:if>
    <s:else>
        <s:iterator value="inventoryItemsForRMT" status="inventoryItemsForRMT">
        <input type="hidden" name="inventoryItems[<s:property value='%{#inventoryItemsForRMT.index}'/>]" 
	      				 value='<s:property value="inventoryItem.id"/>'/>
        <tr>
            <td align="center" valign="middle">
                <s:checkbox id="item_%{#inventoryItemsForRMT.index}"
                        name="inventoryItemsForRMT[%{#inventoryItemsForRMT.index}].inventoryItem"
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
             <td>
                <s:property value="inventoryItem.ownedBy.name" />
            </td>
            <td>
                <s:property value="inventoryItem.ownedBy.type" />
            </td>
            <td>
                <s:property value="inventoryItem.builtOn" />
            </td>
            <td>
                <s:property value="inventoryItem.deliveryDate" />
            </td>
        </tr>
       </s:iterator>
   </s:else>   
   </tbody>
</table>

<div dojoType="dijit.layout.ContentPane" layoutAlign="bottom" style="padding-bottom: 3px;" id="performRMT">
<script type="text/javascript">
<s:if test="pageReadOnlyAdditional">
dojo.addOnLoad(function() {
    twms.util.makePageReadOnly("dishonourReadOnly");
});
</s:if>
</script>
<table width="100%">
<tr>
<td align="center" class="buttons">
  <s:submit id="performRMTButton"  value="%{getText('label.retailMachineTransfer.perform')}" />
</td>
</tr>
</table>	
</div>


</s:form>
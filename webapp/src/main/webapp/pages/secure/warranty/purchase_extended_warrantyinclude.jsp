<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%@ taglib prefix="sd" uri="/struts-dojo-tags"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="t" uri="twms"%>
<%@taglib prefix="u" uri="/ui-ext"%>
<script type="text/javascript" src="scripts/CheckBoxListControl.js"></script>
<script type="text/JavaScript">
dojo.require("twms.widget.Dialog");
</script>

<script>

	var fetched = 0;

	function hidePolicyFetchingLid() {
		var numItems = <s:property value="selectedInvItemsPolicies.size" />;
		if(fetched == numItems) {
			dijit.byId("extended_policy_fetching_lid").hide();
		}
	}

	function getOfferedPolicies(index) {
	    var invId = dojo.byId('item_'+index).value;
	    var purchaseDate = dijit.byId("purchaseDate").getDisplayedValue();
	    var params = {
	      "inventoryItems[0]" : dojo.string.escape("xhtml", invId),
	      "purchaseDate" : dojo.string.escape("xhtml", purchaseDate),
	      "inventoryIndex" : index
	    };

	    var policyDetailsPane = dijit.byId("ewpPolicyDetails_" + index);
	    var t ="selectedInvItemsPolicies_"+index;
	    dojo.query('[widgetid^=\"'+t+'\"]',dojo.byId("ewpPolicyDetails_" + index)).forEach(function(w){
		   dijit.byId(w.id).destroy();});
	    policyDetailsPane.domNode.innerHTML = "<center>Loading...</center>";
	    
	    twms.ajax.fireHtmlRequest("get_extended_warranty_policies.action", params,
	        function(data) {
	            policyDetailsPane.setContent(data);
	            fetched++;
	            hidePolicyFetchingLid();
	            delete data;
	        });
	
	    delete invId,purchaseDate;
	}
	 var selectionParams = {};

    dojo.addOnLoad(function() {        
    	setSelectedInventoryItem();  
    	if(dojo.byId("masterCheckbox_item"))
    	{
	    	var multiCheckBoxControl = new CheckBoxListControl(dojo.byId("masterCheckbox_item"));
			var numItems = <s:property value="selectedInvItemsPolicies.size" />;
	        for (var i = 0; i < numItems; i++) {
	            multiCheckBoxControl.addListElement(dojo.byId("item_" + i));
	        }
        }

    });
       
    function setSelectedInventoryItem(){   
    	var numItems = <s:property value="selectedInvItemsPolicies.size" />;
        for (var i = 0; i < numItems; i++) {  	     	
    	   selectionParams.selectedInventoryItem =  dojo.byId("item_" + i).value; 
        }    	      	
    } 
   
</script>   

<s:form action="confirmExtendedWarrantyPurchase" id="extendedWarrantyPurchase" validate="true">

				
					<s:if test="selectedInvItemsPolicies.size()">
                    <script type="text/javascript">
                        dojo.html.show(dojo.byId("ewpWarningSection"));
                    </script>
                        <div class="section_div" style="clear:both" >
						<div class="section_heading">
								<s:text name="label.extendedWarrantyPolicies" />
						</div>
						<table class="grid borderForTable"  cellspacing="0" cellpadding="0"  style="width:99%;margin:3px;"> 					
						<thead>
							<tr>
								<th class="warColHeader" align="center" width="2%">
									<input type="checkbox" id="masterCheckbox_item"/>
								</th>
						   		<th class="warColHeader" align="left" width="5%"><s:text name="label.common.serialNumber"/></th>
						   		<th class="warColHeader" align="left" width="3%"><s:text name="label.common.modelNumber"/></th>
						   		<th class="warColHeader" align="left" width="6%"><s:text name="columnTitle.listRegisteredWarranties.customer_name"/></th>
						   		<th class="warColHeader" align="left" width="6%"><s:text name="label.warrantyAdmin.customerType"/></th>
						   		<th class="warColHeader" align="left" width="78%"><s:text name="label.extendedwarrantyplans.purchase"/></th>
							</tr>
						</thead>				
						<tbody id="policy_list">	
							<s:iterator value = "selectedInvItemsPolicies" status="inventoryIterator">
								<tr>
									<td align="center">
									<s:checkbox name="selectedInvItemsPolicies[%{#inventoryIterator.index}].inventoryItem"
									     		fieldValue="%{inventoryItem.id}" id="item_%{#inventoryIterator.index}"/>
									<s:if test="availablePolicies.size() == 0">										
									    <script type="text/javascript">
									    dojo.addOnLoad ( function() {
						           			dojo.byId('item_<s:property value="#inventoryIterator.index"/>').checked=false;
						           		});
									    </script>
									</s:if>
									<s:hidden name="ewpDrivenByPurchaseDate" id="ewpDrivenByPurchaseDate_%{#inventoryIterator.index}"/>
									</td>
									<td><s:property value="inventoryItem.serialNumber"/></td>
									<td><s:property value="inventoryItem.ofType.model.name"/></td>
									<td nowrap="nowrap">
										<s:if test="inventoryItem.ownedBy.name == null">
			                    			<s:property value="inventoryItem.ownedBy.companyName" />
			                			</s:if>
			                			<s:else>
			                    			<s:property value="inventoryItem.ownedBy.name" />
			                			</s:else>
									</td>
									<td nowrap="nowrap">						
							            <s:if test="inventoryItem.dealer == inventoryItem.buyer">
			                    			<s:text name="label.common.dealer"/>
							            </s:if>
							            <s:else>
			                    			<s:text name="label.warrantyAdmin.endCustomer"/>
			            				</s:else>
									</td>
									<td>						
										<div dojoType="dojox.layout.ContentPane" executeScripts="true"
										scriptSeparation="false"
										id="ewpPolicyDetails_<s:property value="#inventoryIterator.index" />">
											<s:set name="invIndex1" value="#inventoryIterator.index"/>
											<s:set name="invIndex2" value="#inventoryIterator.index"/>
											<jsp:include page="ewp_policy_list.jsp" /></div>
								   </td>
								   
							 </tr>
					</s:iterator>					
				</tbody>
			</table>
		   	
		   <table  border="0" cellspacing="0" cellpadding="0"  width="100%" class="grid" style="padding-top:10px;">
		   <tr>
		    	<td colspan="2">
		    	</td>
		    </tr>		
		    <tr>		    
		    	<td nowrap="nowrap" class="labelStyle" width="20%">
		    		<s:text name="label.extendedwarrantyplan.dateOfPurchase"/>: </td>          		
	           		<td><sd:datetimepicker id='purchaseDate' name='purchaseDate' value='%{purchaseDate}' displayFormat='MM/dd/yyyy' onchange='refreshExtPolicies()' />
	           		<script type="text/javascript">
	           		function refreshExtPolicies() {		           		
	           			var numItems = <s:property value="selectedInvItemsPolicies.size" />;
	           			fetched = 0;
	           			var lidOpen = false;
	           			for (var i = 0; i < numItems; i++) {
		           			if(dojo.byId("ewpDrivenByPurchaseDate_"+i).value=="true") {
			           			if(!lidOpen) {
					           		dijit.byId("extended_policy_fetching_lid").show();
					           		lidOpen = true;
			           			}
	        	            	getOfferedPolicies(i);
		           			}else {
			           			fetched++;
		           			}
	        	        }
	           		}
	           		dojo.addOnLoad ( function() {
		           		if(dijit.byId("purchaseDate"))
	           			dijit.byId("purchaseDate").onChange=refreshExtPolicies;
	           		});
	           		</script>
	       		</td>
	       	</tr>    
	       	<tr>		    
		    	<td nowrap="nowrap" class="labelStyle">
		    		<s:text name="label.extendedwarrantyplan.orderNumber"/>:</td>	           		
	           		<td><s:textfield name="purchaseOrderNumber" />	           				           		
	       		</td>
	       	</tr>	 	
		    <tr>
		    	<td >
            		<label  class="labelStyle"><s:text name="label.common.comments"/>:</label></td>
            		<td><t:textarea  rows="3" cols="30"
                		name="purchaseComments" />
        		</td>
        	</tr>        	
		   </table>
		   </div>
           <div>
               <input type="checkbox" id="agreeTermsAndCondition"/>
               <s:text name="label.ewp.agreeTermsAndConditions"/>
               <script type="text/javascript">
               dojo.addOnLoad(function(){
                  dojo.connect(dojo.byId("agreeTermsAndCondition"),"onclick",function(){
                     if(dojo.byId("agreeTermsAndCondition").checked){
                         dojo.html.show(dojo.byId("extended_warranty_plan_submit"));
                     }else{
                         dojo.html.hide(dojo.byId("extended_warranty_plan_submit"));
                     }
                  });
               });
               </script>
           </div>
           <div id="submit" align="center" style="margin-top:10px;">
				<s:if test="selectedInvItemsPolicies.size() != 0">
				<s:submit id="extended_warranty_plan_submit"
				cssClass="buttonGeneric" value="%{getText('button.common.continue')}"/>&nbsp;
                    <script type="text/javascript">
                        dojo.html.hide(dojo.byId("extended_warranty_plan_submit"));
                    </script>
                </s:if>
                <input
				class="buttonGeneric" type="button" value='<s:text name="label.cancel"/>'
				onclick="closeCurrentTab()" />
			</div>				
		</s:if>
		<s:else>
			<s:text name="no.policies.available"/>
            <script type="text/javascript">
                dojo.html.hide(dojo.byId("ewpWarningSection"));
            </script>
        </s:else>
			
	  </s:form>
<div dojoType="twms.widget.Dialog"
     id="dialogBoxTermsCondition"
     title="<s:text name="label.common.termsAndConditions" />">
    <div dojoType="dijit.layout.LayoutContainer" style="height:280px;overflow: auto;">
        <div dojoType="dijit.layout.ContentPane" style="margin:5px;"> 

        </div>
    </div>

<div dojoType="twms.widget.Dialog" id="extended_policy_fetching_lid"
         bgColor="white" bgOpacity="0.5" toggle="fade" toggleDuration="250" style="height:190px;width:350px;"
         title="<s:text name="label.customReport.pleaseWait" />">
    <div class="dialogContent" dojoType="dijit.layout.LayoutContainer"
         style="background: #F3FBFE; width: 100%; height: 130px; border: 1px solid #EFEBF7">
        <div dojoType="dojox.layout.ContentPane">
            <div>
            <div class='loadingLidThrobber'>
                <div class='requestLidThrobberContent'></div>
            </div>
            <div  align="center" >
                  <s:text name="label.warranty.waitMessageForPolicy"/> 
            </div>
            </div>
        </div>
    </div>
</div>

</div>

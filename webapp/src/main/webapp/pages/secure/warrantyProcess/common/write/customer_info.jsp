<%--
  Created by IntelliJ IDEA.
  User: pradyot.rout
  Date: Sep 5, 2008
  Time: 1:08:20 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="t" uri="twms" %>
<%@ taglib prefix="u" uri="/ui-ext" %>
<%@ taglib prefix="authz" uri="authz" %>
<script type="text/JavaScript">
    dojo.require("twms.widget.Dialog");
</script>



	<s:hidden name="dealerName" id="dealerName"></s:hidden>
    <s:hidden name="dealerId" id="dealerId"></s:hidden>
    <table style="margin:5px 0px 5px 0px;" class="grid">
		<tr>
			<td width="10%" nowrap="nowrap" class="labelStyle"><s:text name="label.warrantyAdmin.customerType"/>:</td>
			<td width="27%" style="padding-left:50px;">
				<s:if test="%{isModifyDRorETR()}">
					<s:select list="customerTypes" name="addressBookType" disabled="true"
					listKey="key" listValue="value" headerKey="All" headerValue="--Select--" 
					value="addressBookType" id="addressBookType" />
				</s:if>
				<s:else>
					<s:select list="customerTypes" name="addressBookType" disabled="false"
					listKey="key" listValue="value" headerKey="All" headerValue="--Select--" 
					value="addressBookType" id="addressBookType" onchange="toggleCreateCustomer()"/>
				</s:else>
			</td>
			<td width="10%"><input id="name" type="text" name="name" /></td>
			<td><input type="button" class="searchButton"
				id="customerSearchButton" style="border: none;" /></td>
		</tr>
	</table>


<div id="addrDiv">
    <div dojoType="twms.widget.Dialog" id="DialogContent" title="<s:text name="title.common.customerInfo"/>:"
         style="width:90%;height: 90%">
        <div dojoType="dijit.layout.LayoutContainer" style="height:450px; width: 99%;">
            <div id="customerSearchResultTag" dojoType="dojox.layout.ContentPane" layoutAlign="client" executeScripts="true">
            </div>
        </div>
        <div class="buttonWrapperPrimary">
            <input type="button" name="Submit2" value="<s:text name='button.common.close' />" class="buttonGeneric"
                   onclick="javascript:dlg.hide()"/>
            <%--This feature of auto picking up the decendentOf attrubute should not be used.
 this is an odd case and the tag supports picking up of the attribute as the label of current tab
 but this is highly discouraged. specify the value explicitly whenever possible. --%>
            <s:if
				test="isLoggedInUserADealer() && addressBookType!='EndCustomer'">
				<input type="button" name="Submit3"
					value="<s:text name='home_jsp.fileMenu.createCustomer' />"
					class="buttonGeneric" onclick="createNewCustomer()"
					id="createCustomer" style="display: none;" />
			</s:if>
			<s:else>
				<input type="button" name="Submit3"
					value="<s:text name='home_jsp.fileMenu.createCustomer' />"
					class="buttonGeneric" onclick="createNewCustomer()"
					id="createCustomer" />
			</s:else>

            <script type="text/javascript">
            dojo.addOnLoad ( function() {
        		window.myById = dojo.hitch(dojo, dojo.byId);
        		var defaultCustomerBookType = dijit.byId("addressBookType");
        		defaultCustomerBookType.fireOnLoadOnChange=false;
 						dojo.connect(defaultCustomerBookType,"onChange",function(){
 							
 						checkForDemoOrDealerRental();
 			
 							 if((dojo.byId("dealerRentalAllowed").value!="true" && defaultCustomerBookType == "Dealer Rental") || defaultCustomerBookType.value == "Demo"){
 								 
 								 //dojo.html.show(dojo.byId("comp-addr"));
 	                            dojo.html.hide(dojo.byId("customerSearchButton"));
 	                            dojo.html.hide(dojo.byId("name")); 
 	                            
 	                            if(dojo.byId("warranty_operator_info"))
 	                            	dojo.html.hide(dojo.byId("warranty_operator_info"));
 	                           // dojo.html.show(dojo.byId("dealer-addr")); 
 	                          }
 	                         else{
 	                            dojo.html.show(dojo.byId("customerSearchButton")); 
 	                            dojo.html.show(dojo.byId("name"));
 	                            if(dojo.byId("warranty_operator_info"))
 	                            	dojo.html.show(dojo.byId("warranty_operator_info")); 
 	                          //dojo.html.hide(dojo.byId("dealer-addr")); 
 	                        //dojo.html.hide(dojo.byId("comp-addr"));
 	 						   
 	                          }
 						var indexList =  dojo.query("input[id $= 'indexFlag']"); 
 						var nameList =  dojo.query("input[id $= 'nameFlag']");  
 	 					for(var i=0;i<indexList.length;i++){ 	 	 					
                           	getAllPolicies(indexList[i].value,nameList[i].value);
                           	getDisclaimer(indexList[i].value,nameList[i].value);
                        }
 	 					try{
 	 	 					if(dojo.byId("modify_warranty")){
 	 	 						getAllPoliciesForEdit(0,0);
 	 	 					}
 	 					}
 	 					catch(e){
 	 						console.debug("Not for Modify Warranty" +e);
 	 					}
            		});
 						dojo.subscribe("/registration/customer/changed",function(data){
 	                       var indexList =  dojo.query("input[id $= 'indexFlag']"); 
 	                       var nameList =  dojo.query("input[id $= 'nameFlag']");
 	                	   for(var i=0;i<indexList.length;i++){
 	                    		getDisclaimer(indexList[i].value,nameList[i].value);
 	              		 	}
 	                   });
        	});
       	
                
                function createNewCustomer() {
                    dijit.byId("DialogContent").hide();
                    
		        //IE 7&8 HACK:
                    //Those stupid guys seems to have a problem with the Dialog hiding, before it hides the Dialog its called the open tab method??
                    //is called and that is making the tab hiding impossible, new tab is already here, Dialog seems to be hide to where situation?
                    //Any way for the time being it seems to be working
                    //PS: 500 milli second seems to be the standard time for every IE hack :)
                    if (dojo.isIE) {
                        setTimeout(function() {
                            _createCustomer();
                        }, 500);
                    } else {
                        _createCustomer();
                    }
                }
                function _createCustomer() {
                	var thisTabLabel = getMyTabLabel();
                    var urlForCreateCustomer = "show_customer.action";
                    var dealerName = dojo.byId("dealerName").value;
                    var dealerId = dojo.byId("dealerId").value;
 					var addBookType = dojo.byId("addressBookType").value;
                    
					if(addBookType != '--Select--') {
                    	
                    	/*
                    	 * Fix for SLMSPROD-874. addBookType is appropriately handled for dealer
                    	 * Even if dealer selects 'Dealer Rental' while filing DR, 'ENDCUSTOMER'
                    	 * will be sent in the URL as dealer cannot create Dealer Rental customers,
                    	 * only admin can do it. 
                    	 * This Fix also applies for the scenario when TTR is being filed by dealer
                    	 * or admin.
                    	 */
                    	
                    	var tmpAddBookType = addBookType;
                    	tmpAddBookType = tmpAddBookType.toUpperCase();
                    	
                    	if (tmpAddBookType == 'DEALER RENTAL') {
                    		tmpAddBookType = 'DEALERRENTAL'; // Removing the space as it trips the 'Create Customer' Page. It should be all caps as it is name of AddressBookType enum
                    	}
                    	
                    	<s:if test="isLoggedInUserADealer()">
	                		var forDealerAddBookType = tmpAddBookType;
	                	
	                		if (forDealerAddBookType == 'DEALERRENTAL') {
	                			forDealerAddBookType = 'ENDCUSTOMER'; // Dealers cannot created Dealer Rental customer, so resetting it
	                		}
	                		
	                		urlForCreateCustomer = "show_customer.action?dealerId=" + dealerId + "&dealerName=" + dealerName + "&addressBookAddressMappings[0].addressBook.type=" + forDealerAddBookType;
	                		
                		</s:if>
                		<s:else>
                			urlForCreateCustomer = "show_customer.action?dealerId=" + dealerId + "&dealerName=" + dealerName + "&addressBookAddressMappings[0].addressBook.type=" + tmpAddBookType;
    			    	</s:else>
                    }
                    else{
                        urlForCreateCustomer = "show_customer.action?dealerId=" + dealerId + "&dealerName=" + dealerName;	
                    }
 					
 					parent.publishEvent("/tab/open", {
                        label: "Create Customer",
                        url: urlForCreateCustomer,
                        decendentOf: thisTabLabel,
                        forceNewTab: true
                    });

                }
            </script>
        </div>
    </div>
    <div dojoType="dijit.layout.ContentPane" id="customerDetailsDiv">
        <s:if test="addressForTransfer!=null">
            <jsp:include flush="true" page="../read/customer_details.jsp"/>
        </s:if>
        <s:else>
            <s:hidden name="addressForTransfer" value="null" id="addressForTransfer"/>
        </s:else>
    </div>
</div>
<script type="text/javascript">
	dojo.addOnLoad(function(){
		checkForDemoOrDealerRental();
		
		//Call this method so that create-customer button is correctly enabled based on current loaded value of address book type
		//If it is draft DR, then the value of address book type will be different from '--Select--''
		toggleCreateCustomer();
	});

  		function checkForDemoOrDealerRental(){
  			var defaultCustomerBookType = dijit.byId("addressBookType");
  		  	if((dojo.byId("dealerRentalAllowed").value!="true" && defaultCustomerBookType == "Dealer Rental") /* || defaultCustomerBookType == "Demo" */){
            	hideAndDisableControlsForDemoOrDealerRental();
             } else {
            	showAndEnableControlsForDemoOrDealerRental();
             }
            if(dojo.byId("dealerRentalAllowed").value!="true" && defaultCustomerBookType=="Dealer Rental"){
            	if(dijit.byId("transactionTypes")){
                	dojo.byId("transactionTypes").value="Dealer Rental";
            	}
            	if(dijit.byId("market")){
                	dojo.byId("market").value="Rental";
            	}
            	if(dijit.byId("application")){
                	dojo.byId("application").value="Rental";
            	}
            }
          /*   if(defaultCustomerBookType=="Demo"){
            	if(dijit.byId("transactionTypes")){
               		dojo.byId("transactionTypes").value="Demo";
            	}
            	if(dijit.byId("market")){
               		dojo.byId("market").value="Demo";
            	}
            	if(dijit.byId("application")){
               		dojo.byId("application").value="Demo";
            	}
            } 
             else
            {*/
            	if(dijit.byId("transactionTypes")){
            		dojo.byId("transactionTypes").value="--Select--";
            	}
            	if(dijit.byId("market")){
            		dojo.byId("market").value="--Select--";
            	}
            	if(dijit.byId("application")){
            		dojo.byId("application").value="--Select--";
            	}
               
          }
  	  function hideAndDisableControlsForDemoOrDealerRental(){
  		  dojo.html.hide(dojo.byId("customerSearchButton"));
          dojo.html.hide(dojo.byId("name"));
          if(dojo.byId("operator_info"))
          	dojo.html.hide(dojo.byId("operator_info"));                                      
          dojo.html.show(dojo.byId("dealer-addr"));
          dojo.html.hide(dojo.byId("comp-addr"));
          dojo.html.hide(dojo.byId('<s:property value="qualifyId(\"marketingInformation\")" />'));
          dojo.html.hide(dojo.byId("majorComponents"));
          dojo.html.hide(dojo.byId("manageDocuments"));
          dojo.html.hide(dojo.byId("manageSupportDocs"));
          if(dijit.byId("salesMan")){
          dijit.byId("salesMan").set('disabled', true);
          }
          if(dijit.byId("market")){
           dijit.byId("market").set('disabled', true);
          }
          if(dijit.byId("firstTimeCustomer")){
          dijit.byId("firstTimeCustomer").set('disabled', true);
          }
          if(dijit.byId("application")){
          dijit.byId("application").set('disabled', true);
          }
          if(dijit.byId("firstTimeProductOwner")){
          dijit.byId("firstTimeProductOwner").set('disabled', true);
          }
          if(dijit.byId("ifPreviousOwner")){
          dijit.byId("ifPreviousOwner").set('disabled', true);
          }
          if(dijit.byId("competitorMake")){
          dijit.byId("competitorMake").set('disabled', true);
          }
          if(dijit.byId("tradeIn")){
          dijit.byId("tradeIn").set('disabled', true);
          }
          if(dijit.byId("competitionType")){
          dijit.byId("competitionType").set('disabled', true);
          }
          if(dijit.byId("competitorModel")){
          dijit.byId("competitorModel").set('disabled', true);
          }
          if(dijit.byId("contractCode")){
          dijit.byId("contractCode").set('disabled', true);
          }
          if(dijit.byId("maintenanceContract")){
          dijit.byId("maintenanceContract").set('disabled', true);
          }
          if(dijit.byId("industryCode")){
          dijit.byId("industryCode").set('disabled', true);
          }
          if(dijit.byId("installDate")){
              dijit.byId("installDate").set('disabled', true);
          }
          if(dojo.byId("checkboxInstallDate")){
              dojo.byId("checkboxInstallDate").disabled=true;
          }
          if(dijit.byId("transactionTypes")){
      	  	dijit.byId("transactionTypes").set('disabled', true);
          }
  	  }
  	  function showAndEnableControlsForDemoOrDealerRental(){
  		  dojo.html.show(dojo.byId("customerSearchButton")); 
          dojo.html.show(dojo.byId("name"));
          if(dojo.byId("operator_info"))
          	dojo.html.show(dojo.byId("operator_info")); 
          dojo.html.hide(dojo.byId("dealer-addr"));
          dojo.html.show(dojo.byId("comp-addr"));
          dojo.html.show(dojo.byId('<s:property value="qualifyId(\"marketingInformation\")" />')) ;
          dojo.html.show(dojo.byId("majorComponents"));
          dojo.html.show(dojo.byId("manageDocuments"));
          dojo.html.show(dojo.byId("manageSupportDocs"));
          if(dijit.byId("salesMan")){
          dijit.byId("salesMan").set('disabled', false);
          }
          if(dijit.byId("market")){
          dijit.byId("market").set('disabled', false);
          }
          if(dijit.byId("firstTimeCustomer")){
          dijit.byId("firstTimeCustomer").set('disabled', false);
          }
          if(dijit.byId("application")){
          dijit.byId("application").set('disabled', false);
          }
          if(dijit.byId("firstTimeProductOwner")){
          dijit.byId("firstTimeProductOwner").set('disabled', false);
          }
          if(dijit.byId("competitorMake")){
          dijit.byId("competitorMake").set('disabled', false);
          }
          if(dijit.byId("tradeIn")){
          dijit.byId("tradeIn").set('disabled', false);
          }
          if(dijit.byId("competitionType")){
          dijit.byId("competitionType").set('disabled', false);
          }
          if(dijit.byId("competitorModel")){          
          dijit.byId("competitorModel").set('disabled', false);
          }
          if(dijit.byId("contractCode")){
          dijit.byId("contractCode").set('disabled', false);
          }
          if(dijit.byId("maintenanceContract")){
          dijit.byId("maintenanceContract").set('disabled', false);
          }
          if(dijit.byId("industryCode")){
          dijit.byId("industryCode").set('disabled', false);
          }
          if(dijit.byId("installDate")){
           dijit.byId("installDate").set('disabled', false);
          }
          if(dojo.byId("checkboxInstallDate")){
           dojo.byId("checkboxInstallDate").disabled=false;
          }
          if(dijit.byId("transactionTypes")){
          	dijit.byId("transactionTypes").set('disabled', false);
          }
          if(dijit.byId("salesMan")){
          	dijit.byId("salesMan").set('disabled', false);
          }
  	  }
  	function toggleCreateCustomer(){
  		
		var addBookType = dojo.byId("addressBookType").value;
		
		<s:if test="isLoggedInUserADealer()">
		 	if(addBookType != '--Select--' ){
           		if(dojo.byId("createCustomer")){
        	   		dojo.byId("createCustomer").style.display = "";
           		}
        	}
	    	else {
	    		if(dojo.byId("createCustomer")){
            		dojo.byId("createCustomer").style.display = "none";
               	}		    	
	    	}
	    </s:if>
	    <s:else>
		    if(addBookType != '--Select--' ){
	       		if(dojo.byId("createCustomer")){
	    	   		dojo.byId("createCustomer").style.display = "";
	       		}
	    	}
		    else {
		    	if(dojo.byId("createCustomer")){
	    	   		dojo.byId("createCustomer").style.display = "none";
	       		}
		    }
	    </s:else>
	}
  	  
    </script>


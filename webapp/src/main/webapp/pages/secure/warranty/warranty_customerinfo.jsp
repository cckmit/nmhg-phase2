<%@taglib prefix="s" uri="/struts-tags" %>
<%@taglib prefix="t" uri="twms"%>
<%@taglib prefix="u" uri="/ui-ext"%>
<%@taglib prefix="authz" uri="authz"%>
<script type="text/JavaScript">
    dojo.require("twms.widget.Dialog");
</script>

<div id="waitForDisclaimer" style="display:none;">
    <div dojoType="twms.widget.Dialog" id="fetchingDisclamer"
         bgColor="white" bgOpacity="0.5" toggle="fade" toggleDuration="250"
         title="<s:text name="label.customReport.pleaseWait" />" style="width: 40%">
        <div class="dialogContent" dojoType="dijit.layout.LayoutContainer"
             style="background: #F3FBFE; width: 100%; height: 130px; border: 1px solid #EFEBF7">
            <div dojoType="dojox.layout.ContentPane">
                <div align="center"  style="padding-top: 20px">
                    <s:text name="Resetting disclaimer" />
                </div>
            </div>
        </div>
    </div>
</div>



<div id="warranty_customer_info" class="rela_Width">
  <div id="warranty_customer_info_title" class="section_heading">
    <s:text name="label.newCustomerInfo"/>
  </div>
  <div style="margin: 2px 7px 2px 7px;width:100%">
    <s:hidden name="dealerName" id="dealerName"></s:hidden>
    <s:hidden name="dealerId" id="dealerId"></s:hidden>
    <table width="100%" cellpadding="2" cellspacing="2" border="0">
      <tr>
        <td class="labelStyle" width="15%" nowrap="nowrap"><s:text name="label.warrantyAdmin.customerType" />
          :</td>
        <td width="25%"><s:if test="isModifyDRorETR()">
            <s:select list="customerTypes" name="addressBookType" disabled="true"
					listKey="key" listValue="value" headerKey="All" headerValue="--Select--" 
					value="addressBookType" id="addressBookType"/>
          </s:if>
          <s:else>
            <s:select list="customerTypes" name="addressBookType" disabled="false"
					listKey="key" listValue="value" headerKey="All" headerValue="--Select--" 
					value="addressBookType" id="addressBookType" onchange="toggleCreateCustomer()"/>
          </s:else>
        </td>
        <s:if test="canModifyDRorETR()">
          <td width="15%"><input id="name" type="text" name="name" /></td>
        </s:if>
        <s:else>
          <td width="15%"><input id="name" type="text" name="name" disabled="true"/></td>
        </s:else>
        <s:if test="canModifyDRorETR()">
          <td width="45%"><input type="button" class="searchButton"
					id="customerSearchButton" style="border: none;" /></td>

        </s:if>
      </tr>
    </table>
  </div>
  <div id="addrDiv">
      <div dojoType="twms.widget.Dialog" id="DialogContent" title="<s:text name="title.common.customerInfo"/>:" style="width:90%;height:90%;" >
    <div id="customerSearchResultTag" dojoType="dojox.layout.ContentPane" layoutAlign="client" executeScripts="true"
     style="padding-bottom: 3px;" > </div>
    <div class="buttonWrapperPrimary" dojoType="dojox.layout.ContentPane" layoutAlign="bottom">
      <input type="button" name="Submit2" value="<s:text name='button.common.close' />" class="buttonGeneric" onclick="javascript:dlg.hide()"/>
      <%--This feature of auto picking up the decendentOf attrubute should not be used. 
        this is an odd case and the tag supports picking up of the attribute as the label of current tab
        but this is highly discouraged. specify the value explicitly whenever possible. --%>      
        <s:if
				test="isLoggedInUserADealer() && (addressBookType!='EndCustomer' || addressBookType!='Dealer Rental' || addressBookType!='Demo' )">
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
/*           dojo.html.hide(dojo.byId("name")); 
          dojo.html.hide(dojo.byId("customerSearchButton")) */
          dojo.html.hide(dojo.byId("dealer-addr")); 
             if(dojo.byId("dealerRentalAllowed").value!="true" && '<s:property value = "addressBookType" />' == "Dealer Rental"){   
                      dojo.html.show(dojo.byId("dealer-addr")); 
                      if(dojo.byId("warranty_operator_info"))
                      	dojo.html.hide(dojo.byId("warranty_operator_info"));
                      }
                   dojo.connect(defaultCustomerBookType,"onChange",function(){
                	    checkForDemoOrDealerRental();
                        var indexList =  dojo.query("input[id $= 'indexFlag']"); 
                        var nameList =  dojo.query("input[id $= 'nameFlag']");
                        for(var i=0;i<indexList.length;i++){
                     		getAllPolicies(indexList[i].value,nameList[i].value);
                     		getDisclaimer(indexList[i].value,nameList[i].value);
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

       	
		    function createNewCustomer(){
		        dijit.byId("DialogContent").hide();
		        //IE 7&8 HACK:
		        //Those stupid guys seems to have a problem with the Dialog hiding, before it hides the Dialog its called the open tab method??
		        //is called and that is making the tab hiding impossible, new tab is already here, Dialog seems to be hide to where situation?
		        //Any way for the time being it seems to be working
		        //PS: 500 milli second seems to be the standard time for every IE hack :)
		        if(dojo.isIE){
		            setTimeout(function() {_createCustomer();},500);
		        }else{
		            _createCustomer();
		        }
			}
			function _createCustomer(){
			    var thisTabLabel = getMyTabLabel();
			    var urlForCreateCustomer="show_customer.action";
			    <authz:ifUserInRole roles="inventoryAdmin">
				    if(dojo.byId("warrantyId")){
				     	var warrantyId=dojo.byId("warrantyId").value;
				       
				    	urlForCreateCustomer="show_customer.action?warrantyId="+warrantyId;
				    }
				    else{
					    var dealerName=dojo.byId("dealerName").value;
				     	 
                        var dealerId=dojo.byId("dealerId").value;
                        var addBookType = dojo.byId("addressBookType").value;
                        addBookType = addBookType.toUpperCase();
                        if(addBookType!='--SELECT--'){
                            urlForCreateCustomer = "show_customer.action?dealerId=" + dealerId + "&dealerName=" + dealerName + "&addressBookAddressMappings[0].addressBook.type=" + addBookType;
                            }
                            else{
                            urlForCreateCustomer = "show_customer.action?dealerId=" + dealerId + "&dealerName=" + dealerName;	
                            }
                        
				    }			    	
			    </authz:ifUserInRole>
                <s:if test="isInternalUserModifying()">
                if (dojo.byId("warrantyId")) {
                    var warrantyId = dojo.byId("warrantyId").value;
                    urlForCreateCustomer = "show_customer.action?warrantyId=" + warrantyId;
                }
                else {
                    var dealerName = dojo.byId("dealerName").value;
                    var dealerId=dojo.byId("dealerId").value;
                    
                    var addBookType = dojo.byId("addressBookType").value;
                    addBookType = addBookType.toUpperCase();
                    if(addBookType!='--SELECT--'){
                    urlForCreateCustomer = "show_customer.action?dealerId=" + dealerId + "&dealerName=" + dealerName + "&addressBookAddressMappings[0].addressBook.type=" + addBookType;
                    }
                    else{
                    urlForCreateCustomer = "show_customer.action?dealerId=" + dealerId + "&dealerName=" + dealerName;	
                    }
                   }
                </s:if>
                <s:else>
                    var dealerName = dojo.byId("dealerName").value;
                    var dealerId=dojo.byId("dealerId").value;
                    var addBookType = dojo.byId("addressBookType").value;
                    addBookType = addBookType.toUpperCase();
                    if(addBookType!='--SELECT--'){
                    	
                    	/*
                    	 * Fix for SLMSPROD-874. addBookType is appropriately handled for dealer
                    	 * Even if dealer selects 'Dealer Rental' while filing DR, 'ENDCUSTOMER'
                    	 * will be sent in the URL as dealer cannot create Dealer Rental customers,
                    	 * only admin can do it. 
                    	 * This Fix also applies for the scenario when TTR is being filed by dealer
                    	 * or admin.
                    	 */
                    	
                    	var tmpAddBookType = addBookType;
                    	
                    	if (tmpAddBookType == 'Dealer Rental' || tmpAddBookType == 'DEALER RENTAL') {
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
               </s:else>
                parent.publishEvent("/tab/open", {
                    label: "Create Customer",
                    url: urlForCreateCustomer,
                    decendentOf: thisTabLabel,
                    forceNewTab: true
               });

			}
			
			function toggleCreateCustomer(){
				var addBookType = dojo.byId("addressBookType").value;
				<s:if test="isLoggedInUserADealer()"> 
			    	if(addBookType != '--Select--'){
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
			    	if(addBookType != '--Select--'){
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
    </div>
  </div>
    <div dojoType="dijit.layout.ContentPane" id="customerinfo">   
  <jsp:include flush="true" page="warranty_newcustomerinfo.jsp"/>
  </div>
</div>
</div>
<script type="text/javascript">
  		  dojo.addOnLoad(function(){
    			checkForDemoOrDealerRental();
    			toggleCreateCustomer();
    			
    			//Call this method so that create-customer button is correctly enabled based on current loaded value of address book type
    			//If it is draft DR, then the value of address book type will be different from '--Select--''
    			toggleCreateCustomer();
		});
  		  
  		function checkForDemoOrDealerRental(){
  			var defaultCustomerBookType = dijit.byId("addressBookType");
  		  	if((dojo.byId("dealerRentalAllowed").value!="true" && defaultCustomerBookType == "Dealer Rental" )/* || defaultCustomerBookType == "Demo" */){
            	hideAndDisableControlsForDemoOrDealerRental();
             } else {
            	showAndEnableControlsForDemoOrDealerRental();
             }
            if(dojo.byId("dealerRentalAllowed").value!="true" && defaultCustomerBookType =="Dealer Rental"){
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
           /*  if(defaultCustomerBookType =="Demo"){
            	if(dijit.byId("transactionTypes")){
                	dojo.byId("transactionTypes").value="Demo";
            	}
            	if(dijit.byId("market")){
                	dojo.byId("market").value="Demo";
            	}
            	if(dijit.byId("application")){
                	dojo.byId("application").value="Demo";
            	} 
            }*/
  	  }
  	  function hideAndDisableControlsForDemoOrDealerRental(){
  		  dojo.html.hide(dojo.byId("customerSearchButton"));
          dojo.html.hide(dojo.byId("name"));
          if(dojo.byId("warranty_operator_info"))
          	dojo.html.hide(dojo.byId("warranty_operator_info"));                                      
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
          if(dojo.byId("warranty_operator_info"))
          	dojo.html.show(dojo.byId("warranty_operator_info")); 
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
          if(dojo.byId("transactionTypes")){
          if(dojo.byId("transactionTypes").value == "Demo"){
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
          }
  	  }
    </script>
    
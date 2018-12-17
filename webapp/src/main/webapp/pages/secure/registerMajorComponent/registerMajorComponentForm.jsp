<%@ page contentType="text/html"%>
<%@ taglib prefix="sd" uri="/struts-dojo-tags"%>
<%@ page pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="t" uri="twms"%>
<%@ taglib prefix="u" uri="/ui-ext"%>
<%@taglib prefix="authz" uri="authz"%>

<%
	response.setHeader("Pragma", "no-cache");
	response.addHeader("Cache-Control", "must-revalidate");
	response.addHeader("Cache-Control", "no-cache");
	response.addHeader("Cache-Control", "no-store");
	response.setDateHeader("Expires", 0);
%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1" />
<u:stylePicker fileName="yui/reset.css" common="true" />
<s:head theme="twms" />
<u:stylePicker fileName="common.css" />
<u:stylePicker fileName="form.css" />
<u:stylePicker fileName="base.css" />
<u:stylePicker fileName="warrantyForm.css" />
<u:stylePicker fileName="adminPayment.css" />
<style>
hr.hrspace {
	margin: 5px 0;
}

.wdalign {
	width: 53%;
	width: 52% \9;
}
</style>
</head>
<script type="text/javascript">
      dojo.require("dijit.layout.ContentPane");
      dojo.require("dijit.layout.LayoutContainer");
      dojo.require("dojox.layout.ContentPane");   
      dojo.require("twms.widget.Dialog");   
      var dlg;     
     
      dojo.addOnLoad(function() {   
          dojo.html.hide(dojo.byId("loadingIndicationDiv"));  
    	  dojo.html.hide(dojo.byId("inventoryItemOwnerInfoDiv"));
    	  dojo.html.hide(dojo.byId("nonCertifiedInstallerDiv")); 
    	  dlg = dijit.byId("DialogContent");  

        <s:if test="!isDealer()">   		
			adjustUIForNameOrNumber(<s:property value="dealerNameSelected"/>);
			dijit.byId("dealerNameAutoComplete").textbox.value = "<s:property value="forDealer.name" />";
	    	dijit.byId("dealerNumberAutoComplete").textbox.value = "<s:property value="forDealer.serviceProviderNumber" />";
	   

  		 dojo.connect(dojo.byId("toggleToDealerNumber"), "onclick", function() {
  	  		dijit.byId("dealerNumberAutoComplete").setValue("");
  			dijit.byId("dealerNumberAutoComplete").setDisplayedValue("");
  			dijit.byId("dealerNameAutoComplete").setValue("");
	    	dijit.byId("dealerNameAutoComplete").setDisplayedValue("");				
			adjustUIForNameOrNumber(false);
		 }); 

	     dojo.connect(dojo.byId("toggleToDealerName"), "onclick", function()  {
	    	  dijit.byId("dealerNameAutoComplete").setValue("");
	    	  dijit.byId("dealerNameAutoComplete").setDisplayedValue("");
	    	  dijit.byId("dealerNumberAutoComplete").setValue("");
	  		  dijit.byId("dealerNumberAutoComplete").setDisplayedValue("");
			  adjustUIForNameOrNumber(true);
		 });
	     </s:if>	     
		 
	     dojo.connect(dijit.byId("dealerNameAutoComplete"), "onChange", function(value) {
	    	  dojo.html.show(dojo.byId("customerSearchButtonForNonCertifiedInstaller"));
	    	  dojo.html.show(dojo.byId("customerSearchButtonForEndCustomer"));
	     });

	     dojo.connect(dijit.byId("dealerNumberAutoComplete"), "onChange", function(value) {
	    	  dojo.html.show(dojo.byId("customerSearchButtonForNonCertifiedInstaller"));
	    	  dojo.html.show(dojo.byId("customerSearchButtonForEndCustomer"));
	     });

         if(dojo.byId("btnSubmit"))
    		{
    		    dojo.connect(dojo.byId("btnSubmit"), "onclick", function() {
    		        var form = dojo.byId("register_major_com");
    		        form.action = "register_major_com.action";
    	            dojo.byId("btnSubmit").disabled = true;
    	            dojo.byId("btnEdit").disabled = true;
    	            form.submit();
    		    });
    	    } 
        
  	     dojo.connect(dijit.byId("unitSerialNumber"), "onChange", function() {  	  	     
             if(dijit.byId("unitSerialNumber").getValue() != ''){
	    	    populateOwnerDetails(); 
             }	                           
		 });  	 
	    	  
	     dojo.connect(dojo.byId("customerSearchButtonForEndCustomer"), "onclick", function(){
	    	  getMatchingEndCustomers();    			
	  	 });	    	  		    	  
		 dojo.connect(dijit.byId("Part_itemNo"), "onChange", function() {       		     		  
	           populateDescriptionForPart();                
	     });     	
	     dojo.connect(dojo.byId("customerSearchButtonForNonCertifiedInstaller"), "onclick", function(){
	    	 getMatchingCustomers();    			
	      }); 
	      
          <s:if test="confirmMajorComRegistration">            
              dijit.byId("confirmMajorComRegistration").show();	
              dojo.connect(dojo.byId("btnEdit"), "onclick", function() {            	   
	              dijit.byId("confirmMajorComRegistration").hide();
	              <s:if test="inventoryItem.id != null">	              	                 
	                 populateOwnerDetails();
	              </s:if>	                           	              
	              populateDescriptionForPart();        
	          });
          </s:if> 
             
       }); 
      
      function populateOwnerDetails() {
    	  dojo.html.show(dojo.byId("loadingIndicationDiv"));     	  	   	  
    	  dojo.html.hide(dojo.byId("inventoryItemOwnerInfoDiv"));     	    	   
    	  twms.ajax.fireHtmlRequest("showOwnerInformation.action", {"inventoryItem.id": dijit.byId("unitSerialNumber").getValue()}, function(data) {     		    
            	dijit.byId("inventoryItemOwnerInfoDiv").setContent(data);  
                dojo.html.show(dojo.byId("inventoryItemOwnerInfoDiv")); 
                dojo.html.hide(dojo.byId("loadingIndicationDiv"));                 
            });
        }
      
      function populateDescriptionForPart() {   
    	  // Inside registerMajorComponentForm.jsp
    	  
    	 var currentPartNo = dijit.byId("Part_itemNo").getValue();
		 console.debug("Part Number read is " + "[" + currentPartNo + "]");
		 
		 if (currentPartNo) {
		 	twms.ajax.fireHtmlRequest("list_description_for_part.action", {"number": currentPartNo}, function(data) {
				dijit.byId("desc_for_part").setContent(eval(data)[0]);
		   	});
		 }
		 else { // Clear existing description from previous value as currently part number is blank
			 dijit.byId("desc_for_part").setContent("");
		 }   
      }
      
      function getMatchingCustomers(index){    	  
    	  var customerName = dojo.byId("name").value;    	  
    	  var params={};
    	  params.customerStartsWith=  customerName;
    	  params.customerType = "Company";
    	  params.addressBookType = dojo.byId("addressBookTypeForNonCertifiedInstallerId").value;
    	 
    	  <s:if test="!isDealer()">
	    	  if(dijit.byId("dealerNameAutoComplete").getDisplayedValue())
	    	     params.dealerName = dijit.byId("dealerNameAutoComplete").getDisplayedValue(); 
	    	  else
	    		  params.dealerId = dijit.byId("dealerNumberAutoComplete").getValue(); 	    	 
    	  </s:if> 

    	  if(index){
    	       params.pageNo=index;
    	    }  

    	  var customerSearchResultDiv = dijit.byId("customerSearchResultTag");
    	  customerSearchResultDiv.setContent("<div class='loadingLidThrobber'><div class='loadingLidThrobberContent'></div></div>");  	 
    	  twms.ajax.fireHtmlRequest("getMatching_nonCertifiedInstallers_for_majorCompReg.action",params,function(data) {    		    	
    				customerSearchResultDiv.destroyDescendants();    				
    				customerSearchResultDiv.setContent(data);    				
    				delete data, customerSearchResultDiv;  				
    			});
    	 delete customerName, params;
    	 dlg.show();  		
      }
      function getMatchingEndCustomers(index){    	  
    	  var customerName = dojo.byId("nameForEndCustomer").value;    	  
    	  var params={};
    	  params.customerStartsWith=  customerName;
    	  params.customerType = "Company";
    	  params.addressBookType = dojo.byId("addressBookTypeForEndCustomerId").value;

    	  <s:if test="!isDealer()">    	  
	    	  if(dijit.byId("dealerNameAutoComplete").getDisplayedValue()) {
	     	     params.dealerName = dijit.byId("dealerNameAutoComplete").getDisplayedValue();  }   	  
	     	  else{
	     	      params.dealerId = dijit.byId("dealerNumberAutoComplete").getValue(); }   
     	  </s:if> 

     	 if(index){
     	       params.pageNo=index;
     	    }   
    	    	 
    	  var customerSearchResultDiv = dijit.byId("customerSearchResultTag");
    	  customerSearchResultDiv.setContent("<div class='loadingLidThrobber'><div class='loadingLidThrobberContent'></div></div>");
    	  twms.ajax.fireHtmlRequest("getMatching_endCustomers_for_majorCompReg.action",params,function(data) {    		    	
    				customerSearchResultDiv.destroyDescendants();    				
    				customerSearchResultDiv.setContent(data);    				
    				delete data, customerSearchResultDiv;  				
    			});
    	 delete customerName, params;
    	 dlg.show();  		
      }
      function showAddressDetails(customerId, addressId){         
    		dlg.hide(); 
    		var params = {
    		    nonCertifiedInstaller:  customerId,
    		    nonCertifiedInstallerAddress: addressId        
    	    } 
    		if(dojo.byId("nonCertInstallerInfo")){
     		   dojo.byId("nonCertInstallerInfo").innerHTML = "";
     		   dojo.html.hide(dojo.byId("nonCertInstallerInfo"));  
    		}
    		var hiddenList = dojo.query("input[id ^= 'register_major_com_addressForTransferToNonCertifiedInstaller']");
		    for( var i = 0; i < hiddenList.length; i++) {
		        hiddenList[i].value = "";		        
            } 
		    var hiddenList = dojo.query("input[id ^= 'register_major_com_nonCertifiedInstaller']"); 
		    for( var i = 0; i < hiddenList.length; i++) {
		        hiddenList[i].value = "";		        
            }    		
    		var parentDiv = dojo.byId("nonCertifiedInstallerDiv");
    		parentDiv.innerHTML = "Loading...";
    	    params.customerType = "Company";
    	    twms.ajax.fireHtmlRequest("show_nonCertifiedInstallerDetails_for_majorCompReg.action", params, function(data) {     	    		    	 
    	    	parentDiv.innerHTML = data;
    	    	dojo.html.show(dojo.byId("nonCertifiedInstallerDiv"));    	    	
    			delete data, parentDiv;    			
    		});   	   
    	} 
      function showAddressDetailsForEndCustomer(customerId, addressId){         
  		dlg.hide(); 
  		var params = {
  		    endCustomer:  customerId,
  	        endCustomerAddress: addressId        
  	    }
  		if(dojo.byId("endCustomerInfo")){
 		   dojo.byId("endCustomerInfo").innerHTML = "";
 		   dojo.html.hide(dojo.byId("endCustomerInfo"));    
  		} 
  		var hiddenList = dojo.query("input[id ^= 'register_major_com_addressForTransferToendCustomer']");
	    for( var i = 0; i < hiddenList.length; i++) {
	        hiddenList[i].value = "";		        
        } 
	    var hiddenList = dojo.query("input[id ^= 'register_major_com_endCustomer']"); 
	    for( var i = 0; i < hiddenList.length; i++) {
	        hiddenList[i].value = "";		        
        }  		
  		var parentDiv = dojo.byId("inventoryItemOwnerInfoDiv");  		
  		parentDiv.innerHTML = "Loading...";
  		params.customerType = "Company";
  	    twms.ajax.fireHtmlRequest("show_endCustomerDetails_for_majorCompReg.action", params, function(data) {     	    		    	 
  	    	parentDiv.innerHTML = data;
  	    	dojo.html.show(dojo.byId("inventoryItemOwnerInfoDiv"));    	    	
  			delete data, parentDiv;    			
  		});   	   
  	} 
    function enableNonCertifiedText()
    {
       dijit.byId("addressBookTypeForNonCertifiedInstallerId").setDisabled(false);
       dijit.byId("certifiedDealerId").setDisplayedValue("");
       dojo.byId("certifiedDealerHidden").value = "";
       dijit.byId("certifiedDealerId").setDisabled(true);
    }
    function enableCertifiedText()
    {
    	 dijit.byId("certifiedDealerId").setDisabled(false);        
         dijit.byId("addressBookTypeForNonCertifiedInstallerId").setDisplayedValue("--Select--");  
         dijit.byId("addressBookTypeForNonCertifiedInstallerId").setDisabled(true);
         if(dojo.byId("nonCertInstallerInfo")){
        	 dojo.byId("nonCertInstallerInfo").innerHTML = ""; 
        	 dojo.html.hide(dojo.byId("nonCertifiedInstallerDiv"));        
         }        
    } 
    function enableUnitSNoText()
    {
    	 dijit.byId("addressBookTypeForEndCustomerId").setDisabled(false);
         dijit.byId("unitSerialNumber").setDisplayedValue("");
         dojo.byId("selectedInvItemHidden").value = "";
         dijit.byId("unitSerialNumber").setDisabled(true);  
         if(dojo.byId("ownerInfoDiv")){              
        	 dojo.byId("ownerInfoDiv").innerHTML = "";
   		     dojo.html.hide(dojo.byId("inventoryItemOwnerInfoDiv"));	    
         }                           
    } 
    function enableEndCustomerText()
    {
    	 dijit.byId("unitSerialNumber").setDisabled(false);  
         dijit.byId("addressBookTypeForEndCustomerId").setDisplayedValue("--Select--");  
         dijit.byId("addressBookTypeForEndCustomerId").setDisabled(true);
         dojo.byId("selectedInvItemHidden").value = "";  
         if(dojo.byId("endCustomerInfo")){
        	 dojo.byId("endCustomerInfo").innerHTML = ""; 
        	 dojo.html.hide(dojo.byId("inventoryItemOwnerInfoDiv"));	              
         }             
    }
    function adjustUIForNameOrNumber(/*boolean*/ isDealerName) {
		dojo.byId("dealerNameSelected").value = isDealerName;		
				
		dojo.html.setDisplay(dijit.byId("dealerNumberAutoComplete").domNode, !isDealerName);
		dijit.byId("dealerNumberAutoComplete").setDisabled(isDealerName);
		dojo.html.setDisplay(dojo.byId("dealerNumberText"), !isDealerName);
		dojo.html.setDisplay(dojo.byId("toggleToDealerName"), !isDealerName);
		dojo.html.setDisplay(dojo.byId("dealerNameText"), isDealerName); 
		
		dojo.html.setDisplay(dijit.byId("dealerNameAutoComplete").domNode, isDealerName);
		dijit.byId("dealerNameAutoComplete").setDisabled(!isDealerName);
		dojo.html.setDisplay(dojo.byId("toggleToDealerNumber"), isDealerName);

		dojo.html.hide(dojo.byId("customerSearchButtonForNonCertifiedInstaller"));
  	    dojo.html.hide(dojo.byId("customerSearchButtonForEndCustomer"));
	}
    </script>


<u:body>
	<s:form action="confirmMajorComRegistration" id="register_major_com"
		theme="twms" validate="true" method="POST">
		<div dojoType="dijit.layout.LayoutContainer" id="root">
            <div dojoType="dijit.layout.ContentPane" layoutalign="center">
			<div class="policy_section_div">

				<div class="section_header">
					<s:text name="label.majorComponent.componentRegister" />
				</div>
				<u:actionResults />
				<authz:ifUserInRole roles="admin,processor, enterpriseDealership">
					<table class="grid" cellpadding="0" cellspacing="0">
						<input type="hidden" name="dealerNameSelected"
							id="dealerNameSelected"
							value="<s:property value="dealerNameSelected"/>" />
						<tr>
							<td id="dealerNameText" class="labelStyle" width="23%"
								nowrap="nowrap"><s:text name="Dealer Name" />:</td>
							<td id="dealerNumberText" width="23%" nowrap="nowrap"
								class="labelStyle"><s:text name="Dealer Number" />:</td>
							<td><sd:autocompleter id='dealerNameAutoComplete' href='list_dealer_names_dealer_summary.action' name='forDealer' loadOnTextChange='true' showDownArrow='false' indicator='indicator' key='%{forDealer.id}' keyName='forDealer' value='%{forDealer.name}' /> <sd:autocompleter id='dealerNumberAutoComplete' href='list_dealer_numbers_dealer_summary.action' name='forDealer' loadOnTextChange='true' showDownArrow='false' indicator='indicator' key='%{forDealer.id}' keyName='forDealer' value='%{forDealer.dealerNumber}' /> <img
								style="display: none;" id="indicator" class="indicator"
								src="image/indicator.gif" alt="Loading..." />
							</td>
						</tr>
					</table>
					<div id="toggle" style="cursor: pointer;">
						<div id="toggleToDealerNumber" class="clickable">
							<s:text name="Specify Dealer Number" />
						</div>
						<div id="toggleToDealerName" class="clickable">
							<s:text name="Specify Dealer Name" />
						</div>
					</div>
					<hr class="hrspace" />
				</authz:ifUserInRole>

				<table cellspacing="0" cellpadding="0" class="grid" width="100%"
					border="0">
					<tr>
						<td class="labelStyle" width="23%" nowrap="nowrap"><s:text
								name="label.majorComponent.componentSerialNo" />:</td>
						<td width="20%"><s:textfield
								name="majorComponent.serialNumber"
								value="%{majorComponent.serialNumber}" cssStyle="width:120px;" />
						</td>
						<td colspan="2">&nbsp;</td>
					</tr>
					<tr>
						<td class="labelStyle" width="23%" nowrap="nowrap"><s:text
								name="label.majorComponent.componentPartNo" />:</td>
						<td class="labelStyle" width="20%"><sd:autocompleter id='Part_itemNo' delay='1000' href='list_major_components.action' name='majorComponent.ofType' value='%{majorComponent.ofType.number}' loadOnTextChange='true' showDownArrow='false' />
						</td>
						<td class="labelStyle" nowrap="nowrap" width="15%"><s:text
								name="label.common.description" />:</td>
						<td width="42%">
							<div dojoType="dojox.layout.ContentPane" id="desc_for_part"
								style="width: 100%; height: 100%;"></div>
						</td>
					</tr>
				</table>
				<hr class="hrspace" />
				<table cellspacing="0" cellpadding="0" class="grid" width="100%"
					border="0">
					<tr>
						<s:if test="applicationSettings.captureShipentDateForMajorComp">
							<td class="labelStyle" width="23%" nowrap="nowrap"><s:text
									name="label.common.shipmentDate" />:</td>
							<td width="77%"><sd:datetimepicker name='majorComponent.shipmentDate' value='%{majorComponent.shipmentDate}' id='shipmentDate' />
							</td>
						</s:if>
					</tr>
					<tr>
						<td class="labelStyle" width="23%" nowrap="nowrap"><s:text
								name="label.inventory.dateofDelivery" />:</td>
						<td width="77%"><sd:datetimepicker name='majorComponent.deliveryDate' value='%{majorComponent.deliveryDate}' id='deliveryDate' />
						</td>
					</tr>
				</table>
				<hr class="hrspace" />
				<table cellspacing="0" cellpadding="0" class="grid">
					<tr>
						<td width="20%" class="labelStyle" nowrap="nowrap">
							<table cellpadding="0" cellspacing="0" border="0">
								<tr>
									<td class="labelStyle" width="2%" nowrap="nowrap" align="left">
										<input id="certifiedInstaller" type="radio"
										name="certifiedInstaller" value="true"
										<s:if test='certifiedInstaller'> checked="checked" </s:if>
										onclick="enableCertifiedText()" align="left"
										style="padding: 0; border: 0" />
									</td>
									<td class="labelStyle" width="18%" nowrap="nowrap" align="left"><s:text
											name="label.majorComponent.certifiedInstaller" /></td>
								</tr>
							</table>
						</td>
						<td width="62%"><s:hidden name="certifiedDealer"
								value="%{certifiedDealer}" id="certifiedDealerHidden" /> <sd:autocompleter id='certifiedDealerId' href='list_certified_service_providers.action' name='certifiedDealer' value='%{certifiedDealer.name}' loadOnTextChange='true' showDownArrow='false' notifyTopics='inventoryItem/certifiedInstaller' /> <script
								type="text/javascript">
		                     dojo.addOnLoad(function() {       
		                         dojo.subscribe("inventoryItem/certifiedInstaller", null, function(data, type, request) {
		                            var selectedItemId = dojo.byId("certifiedDealerHidden").value; 		                                      
		                            if(selectedItemId == ''){
		                                dojo.byId("certifiedDealerHidden").value = dijit.byId("certifiedDealerId").getValue();                                
		                            }
		                         });
		                     });
		                 </script> <img style="display: none;" id="indicator"
							class="indicator" src="image/indicator.gif" alt="Loading..." />
						</td>
					</tr>
					<tr>
						<td width="20%" class="labelStyle" nowrap="nowrap">
							<table cellpadding="0" cellspacing="0" border="0">
								<tr>
									<td class="labelStyle" width="2%" nowrap="nowrap" align="left">
										<input id="nonCertifiedInstaller" type="radio"
										name="certifiedInstaller" value="false"
										onclick="enableNonCertifiedText()"
										<s:if test='!certifiedInstaller'> checked="checked" </s:if>
										align="left" style="padding: 0; border: 0">
									</td>
									<td class="labelStyle" width="18%" nowrap="nowrap" align="left"><s:text
											name="label.majorComponent.nonCertifiedInstaller" /></td>
								</tr>
							</table>
						</td>
						<td class="labelStyle" nowrap="nowrap">
							<table cellpadding="0" cellspacing="0" border="0">
								<tr>
									<td class="labelStyle" width="15%" nowrap="nowrap"><s:text
											name="label.warrantyAdmin.customerType" />:</td>
									<td width="10%"><s:hidden
											name="addressBookTypeForNonCertifiedInstaller"
											value="%{addressBookTypeForNonCertifiedInstaller}"
											id="nonCertifiedInstallerAddressBookType" /> <s:select
											list="customerTypes" listKey="key" listValue="value"
											headerKey="" headerValue="--Select--"
											value="addressBookTypeForNonCertifiedInstaller"
											id="addressBookTypeForNonCertifiedInstallerId" /> <script
											type="text/javascript">
                                      dojo.addOnLoad(function() { 
                                          if(dojo.byId("nonCertifiedInstaller").checked){                                              
                                        	  dijit.byId("addressBookTypeForNonCertifiedInstallerId").setDisabled(false);
                                          }
                                          else{                                        	  
                                        	  dijit.byId("addressBookTypeForNonCertifiedInstallerId").setDisabled(true);
                                          }
                                          dojo.connect(dijit.byId("addressBookTypeForNonCertifiedInstallerId"), "onChange", function(){
                                    		 setValue();    			
                              	    	  });
                                         function setValue()
                                         {     
                                            dojo.byId("nonCertifiedInstallerAddressBookType").value = dijit.byId("addressBookTypeForNonCertifiedInstallerId").getValue();       
                                        }	
                                        	
                                    });
                                   </script>
									</td>
									<td width="10%"><input id="name" type="text" name="name" />
									</td>
									<td><input type="button" class="searchButton"
										id="customerSearchButtonForNonCertifiedInstaller"
										style="border: none;" /></td>

								</tr>
							</table>
						</td>
					</tr>
					<tr>
						<td colspan="5">
							<div dojoType="dojox.layout.ContentPane"
								id="nonCertifiedInstallerDiv"
								style="width: 100%; height: 220px;"></div> <s:if
								test="!certifiedInstaller && addressForTransferToNonCertifiedInstaller!=null">
								<jsp:include flush="true"
									page="nonCertifiedInstaller_details_for_majorCompReg.jsp" />
							</s:if> <s:else>
								<s:hidden name="addressForTransferToNonCertifiedInstaller"
									value="null" id="addressForTransferToNonCertifiedInstaller" />
							</s:else>
						</td>
					</tr>
				</table>
				<div class="borderTable">&nbsp;</div>

				<table cellspacing="0" cellpadding="0" class="grid">
					<tr>
						<td width="20%" class="labelStyle" nowrap="nowrap">
							<table cellpadding="0" cellspacing="0" border="0">
								<tr>
									<td class="labelStyle" width="2%" nowrap="nowrap" align="left">
										<input id="ownerInfoForUnitSNo" type="radio"
										name="standAloneParts" value="false"
										onClick="enableEndCustomerText()"
										<s:if test='!standAloneParts'> checked="checked" </s:if>
										align="left" style="padding: 0; border: 0" />
									</td>
									<td class="labelStyle" width="18%" nowrap="nowrap" align="left"><s:text
											name="label.majorComponent.unitSerialNo" /></td>
								</tr>
							</table>
						</td>
						<td class="wdalign"><s:hidden name="selectedItem"
								value="%{inventoryItem.id}" id="selectedInvItemHidden" /> <sd:autocompleter id='unitSerialNumber' href='list_unit_serial_nos.action' name='inventoryItem' value='%{inventoryItem.serialNumber}' loadOnTextChange='true' showDownArrow='false' notifyTopics='invItemChanged/setUnitSerialNumber' keyName='inventoryItem' key='%{inventoryItem.id}' /> <script
								type="text/javascript">
		                     dojo.addOnLoad(function() {       
		                         dojo.subscribe("invItemChanged/setUnitSerialNumber", null, function(data, type, request) {
		                            var selectedInvId = dojo.byId("selectedInvItemHidden").value;		                                                                                        
		                            if(selectedInvId == ''){
		                                dojo.byId("selectedInvItemHidden").value = dijit.byId("unitSerialNumber").getValue();                                
		                            }
		                         });
		                     });
		                 </script> <img style="display: none;" id="indicator"
							class="indicator" src="image/indicator.gif" alt="Loading..." />
						</td>
					</tr>
					<tr>
						<td width="20%" class="labelStyle" nowrap="nowrap">
							<table cellpadding="0" cellspacing="0" border="0">
								<tr>
									<td class="labelStyle" width="2%" nowrap="nowrap" align="left">
										<input id="ownerInfoForWithOutUnitSNo" type="radio"
										name="standAloneParts" value="true"
										onClick="enableUnitSNoText()"
										<s:if test='standAloneParts'> checked="checked" </s:if>
										align="left" style="padding: 0; border: 0" />
									</td>
									<td class="labelStyle" width="18%" nowrap="nowrap" align="left"><s:text
											name="label.majorComponent.standAloneParts" /></td>
								</tr>
							</table>
						</td>
						<td class="labelStyle" nowrap="nowrap">
							<table cellpadding="0" cellspacing="0" border="0">
								<tr>
									<td class="labelStyle" width="15%" nowrap="nowrap"><s:text
											name="label.warrantyAdmin.customerType" />:</td>
									<td width="10%"><s:hidden
											name="addressBookTypeForEndCustomer"
											value="%{addressBookTypeForEndCustomer}"
											id="endCustomerAddressBookType" /> <s:select
											list="customerTypes" disabled="true" listKey="key"
											listValue="value" headerKey="" headerValue="--Select--"
											value="addressBookTypeForEndCustomer"
											id="addressBookTypeForEndCustomerId" /> <script
											type="text/javascript">
                                      dojo.addOnLoad(function() {
                                    	  if(dojo.byId("ownerInfoForWithOutUnitSNo").checked){                                              
                                        	  dijit.byId("addressBookTypeForEndCustomerId").setDisabled(false);
                                          }
                                          else{                                        	  
                                        	  dijit.byId("addressBookTypeForEndCustomerId").setDisabled(true);
                                          }  
                                         dojo.connect(dijit.byId("addressBookTypeForEndCustomerId"), "onChange", function(){
                                    		 setValue();    			
                              	    	  });
                                         function setValue()
                                         {     
                                            dojo.byId("endCustomerAddressBookType").value = dijit.byId("addressBookTypeForEndCustomerId").getValue();       
                                        }	
                                        	
                                    });
                                   </script>
									</td>
									<td width="10%"><input id="nameForEndCustomer" type="text"
										name="name" /></td>
									<td><input type="button" class="searchButton"
										id="customerSearchButtonForEndCustomer" style="border: none;" />
									</td>

								</tr>
							</table>
						</td>
					</tr>
					<tr>
						<td style="width: 100%; height: 100%;" colspan="2">
							<div dojoType="dojox.layout.ContentPane"
								id="inventoryItemOwnerInfoDiv"
								style="width: 100%; height: 220px;"></div> <s:if
								test="standAloneParts && addressForTransferToendCustomer != null">
								<jsp:include flush="true"
									page="endCustomer_details_for_majorCompReg.jsp" />
							</s:if> <s:else>
								<s:hidden name="addressForTransferToendCustomer" value="null"
									id="addressForTransferToendCustomer" />
							</s:else>

							<div id="loadingIndicationDiv" style="width: 100%; height: 88%;">
								<div class='loadingLidThrobber'>
									<div class='loadingLidThrobberContent'>Loading...</div>
								</div>
							</div>
						</td>
					</tr>



				</table>

				<div>
					<div id="submit" align="center">
						<s:submit name="button.common.save" cssClass="buttonGeneric" />
						<input id="cancel_btn" class="buttonGeneric" type="button"
							value="<s:text name='button.common.cancel'/>"
							onclick="javascript:closeTab(getTabHavingLabel(getMyTabLabel()));" />
					</div>
				</div>

				<div id="addrDiv">
					<div dojoType="twms.widget.Dialog" id="DialogContent"
						title="<s:text name="title.common.customerInfo"/>:"
						style="width: 85%; height: 75%;">
						<div id="customerSearchResultTag"
							dojoType="dojox.layout.ContentPane" layoutAlign="client"
							executeScripts="true"
							style="padding-bottom: 3px;">
						</div>
						<div class="buttonWrapperPrimary">
							<input type="button" name="Submit2"
								value="<s:text name='button.common.close' />"
								class="buttonGeneric" onClick="javascript:dlg.hide()" /> <input
								type="button" name="Submit3"
								value="<s:text name='home_jsp.fileMenu.createCustomer' />"
								class="buttonGeneric" onClick="createNewCustomer()"
								id="createCustomer" />
							<script type="text/javascript">
		    function createNewCustomer(){		        
		        dijit.byId("DialogContent").hide();		        
		        if(dojo.isIE){		        	 
		            setTimeout(function() {_createCustomer();},500);
		        }else{
		            _createCustomer();
		        }
			}
		    function _createCustomer(){		    	
			    var thisTabLabel = getMyTabLabel();
			    var dealerId;

			    <s:if test="!isDealer()">    	  
		    	  if(dijit.byId("dealerNameAutoComplete").getDisplayedValue()) {
		    		  dealerId = dijit.byId("dealerNameAutoComplete").getValue();  }   	  
		     	  else{
		     		 dealerId = dijit.byId("dealerNumberAutoComplete").getValue(); }   
	     	   </s:if> 
	     	   <s:else>	     	       
	     	         dealerId = '<s:property value="forDealer.id"/>';	     	       
	     	   </s:else> 
			    			   
	     	    var urlForCreateCustomer="show_customer.action?dealerId="+dealerId;			    
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
				</div>

				<div id="policiesList">
					<s:iterator value="availablePolicies" status="status">
						<s:hidden
							name="availablePolicies[%{#status.index}].policyDefinition"
							value="%{policyDefinition.id}" />
						<s:hidden name="availablePolicies[%{#status.index}].price"
							value="%{price.breachEncapsulationOfCurrency()}" />
						<s:hidden name="availablePolicies[%{#status.index}].price"
							value="%{price.breachEncapsulationOfAmount()}" />
					</s:iterator>
				</div>

			</div>
            </div>
        </div>
	</s:form>

		<div dojoType="twms.widget.Dialog" id="confirmMajorComRegistration"
			title="Confirm Registration" closable="false"
			style="width: 85%; height: 75%;">
				<div dojoType="dijit.layout.ContentPane" layoutalign="center"
					id="confirmRegistrationContent">
					<jsp:include page="register_majorCom_conf.jsp" />
				</div>
				<div dojoType="dijit.layout.ContentPane" id="actionButtons"
					layoutalign="bottom" style="padding: 10px 0 10px 0;">
					<center class="buttons">
						<s:submit value="%{getText('button.common.edit')}" id="btnEdit"
							type="button" />
						<s:submit value="%{getText('button.common.submit')}"
							id="btnSubmit" />
					</center>
				</div>
		</div>
</u:body>
</html>
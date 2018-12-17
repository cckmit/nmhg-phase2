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

<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="t" uri="twms" %>
<%@ taglib prefix="u" uri="/ui-ext" %>

<u:stylePicker fileName="multiCar.css" />


<script type="text/javascript">
    var isOwnerInfoEntered ="<s:property value="claim.ownerInformation.id"/>";
    
    var ownerNameLabel = "<s:text name="label.warrantyAdmin.ownerName"/>: ";
    var ownerCountryLabel = "<s:text name="label.common.country"/>:"; 
    var ownerStateLabel = "<s:text name="label.common.state"/>:";
    var ownerCityLabel = "<s:text name="label.common.city"/>:";
    var ownerZipLabel = "<s:text name="label.common.zipCode"/>:";
</script>
<script type="text/javascript" src="scripts/claim/nonSerializedOwnerInfo.js"></script>
<script type="text/javascript">
  dojo.addOnLoad(function() {
		if(isOwnerInfoEntered > 0)
	    {
	    	displayChosenAddress("<s:property value="claim.ownerInformation.belongsTo.name"/>",
	    						 "<s:property value="claim.ownerInformation.city"/>",
	    						 "<s:property value="claim.ownerInformation.state"/>",
	    						 "<s:property value="claim.ownerInformation.zipCode"/>",
	    						 "<s:property value="claim.ownerInformation.country"/>");
	    }
    });
</script>

<div id="nonSerializedOwnerInfo" dojoType="twms.widget.Dialog" title="<s:text name="label.matchRead.ownerInformation"/>" 
	bgColor="#FFF" bgOpacity="0.5" toggle="fade" toggleDuration="250" style="width:80%;">

			<span id="chosenAddress" >
			</span>
		  
		   

			<div id="claim_customer_info" >
			
			<div id="claim_customer_info_title" class="mainTitle" style="margin:10px 0px 10px 0px; ">
			 
				<s:text name="label.customer.searchCustomer"/>
			</div>
			<div class="borderTable">&nbsp;</div>
			<div style="padding: 2px; padding-left: 7px; padding-right: 10px; padding-bottom:10px;">	
			    	<input id="name" type="text" name="name"/> 
			    	<input type="button" class="searchButton" id="customerSearchButton" style="border:none;" />
			        <s:hidden id="forDealer" name="claim.forDealer.id" ></s:hidden>	
			      <s:hidden id="forBusinessUnit" name="claim.businessUnitInfo.name" ></s:hidden>	
			       
			                
			</div>

			<div id="addrDiv"> 
                <div dojoType="twms.widget.Dialog" id="CustomerDialogContent" bgColor="white" bgOpacity="0.5" toggle="fade" toggleDuration="250" style="width:90%;height:80%">
			<div id="customerSearchResultTag" dojoType="dojox.layout.ContentPane" layoutAlign="client" executeScripts="true"
			     style="padding-bottom: 3px;overflow:auto;width:100%;height:100%;">
			</div>
			<div class="buttonWrapperPrimary">
			        <input type="button" name="Submit2" value="<s:text name='button.common.close' />" class="buttonGeneric" onclick="javascript:closeCustomerDialog()"/>
			        <%--This feature of auto picking up the decendentOf attrubute should not be used. 
			        this is an odd case and the tag supports picking up of the attribute as the label of current tab
			        but this is highly discouraged. specify the value explicitly whenever possible. --%>
			           <s:if test="claim.forDealer.id == loggedInUser.belongsToOrganization.id">
				      			         
				         <input type="button" name="createCust" value="<s:text name='home_jsp.fileMenu.createCustomer' />" class="buttonGeneric" id="nonSerializedCreateCust"/>
				         
			           </s:if>
			           
			           <script type="text/javascript">
						dojo.addOnLoad(
						 function() {
						 var conElemnt= dojo.byId("nonSerializedCreateCust");
						if(conElemnt){
						  dojo.connect(conElemnt, "onmousedown", function(eventOld) {
						   var event= cloneEvenIfIe(eventOld);	
						   event.label = "Create Customer";
						   event.url = "show_customer.action?matchRead=true";
						    if(TWMS_UTILITY_JS) {
						     var tab = getFrameAttribute("TST_IS_PREVIEW") ? parent.getTabDetailsForIframe() : getTabDetailsForIframe();
						     event.decendentOf = getTabHavingId(tab.tabId).title;
						    }
						    event.forceNewTab = false;
						   top.requestTab(event);
						  });
						  }
						 }
						);
						</script>
			        
			</div>
			</div>
			</div>
			</div>
    	</div>

<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="t" uri="twms"%><%@taglib prefix="u" uri="/ui-ext"%>


<html xmlns="http://www.w3.org/1999/xhtml">

<head>
<s:head theme="twms"/>
<style  type="text/css">
.searchButton {
		vertical-align:middle;
		cursor:pointer;
		width:18px;
		overflow:visible;
		background:url(../../../image/searchIcon.gif);
		background-repeat:no-repeat;
		background-position:center;
}
</style>
    <u:stylePicker fileName="form.css"/>
    <u:stylePicker fileName="warrantyForm.css"/>
    <u:stylePicker fileName="common.css"/>
    <u:stylePicker fileName="base.css"/>
</head>
<u:body>
<script>
  dojo.require("twms.widget.Dialog");
  dojo.require("dijit.layout.LayoutContainer");
  dojo.require("dijit.layout.ContentPane");
  dojo.require("dojox.layout.ContentPane");
  function getMatchingCustomers(index) {
	var customerName = dojo.byId("name").value;
	var addressBookType = dojo.byId("addressBookType_dealer_company").value;
    var params={};
    if(index){
       params.pageNo=index;
    }
    params.customerStartsWith=  customerName;
    params.customerType= "Company";
    params.addressBookType= addressBookType;
    var customerSearchResultDiv = dijit.byId("customerSearchResultTag");
    customerSearchResultDiv.setContent("<div class='loadingLidThrobber'><div class='loadingLidThrobberContent'></div></div>");
	twms.ajax.fireHtmlRequest("search_customer.action",params,function(data) {
			customerSearchResultDiv.destroyDescendants();
			customerSearchResultDiv.setContent(data);	
			delete data, customerSearchResultDiv;
		});
	delete customerName,params;
	customerdlg.show();
 }
 
 function closeCustomerDialog(){
    customerdlg.hide();
}	

function showCustomerUpdate(customerId, addressId){
	var thisTabId = getTabDetailsForIframe().tabId;
	var thisTab = getTabHavingId(thisTabId);
	var customerUpdateUrl = "show_update_customer.action?customer="+customerId +
		"&selectedAddressId=" + addressId;
	customerUpdateUrl = customerUpdateUrl + "&customerType=Company";
	customerUpdateUrl = customerUpdateUrl + "&hideSelect=true";
	top.publishEvent("/tab/open", {label: "Update Customer", url: customerUpdateUrl, decendentOf: thisTab.title });
	delete customerUpdateUrl;
}


dojo.addOnLoad(function() {    
    dojo.connect(dojo.byId("customerSearchButton"), "onclick","getMatchingCustomers");
    customerdlg = dijit.byId("CustomerDialogContent");  
});

</script>

<div dojoType="dijit.layout.LayoutContainer"
	style="width: 100%; height: 100%; background: white;">
<div dojoType="dijit.layout.ContentPane" layoutAlign="client">

<div id="customer_info" class="section_div" style="width:99.6%">
<div id="customer_info_title" class="section_header"><s:text name="label.customer.searchCustomer"/></div>

<div style="margin:5px;width:100%" class="labelStyle">	
<tr>
 <td width="16%" class="labelStyle"><s:text name="label.common.addressBookType" /></td>
		     <td width="40%"><s:select label="Address Book Type" id="addressBookType_dealer_company"
					 name="addressBookAddressMappings[%{#newIndex}].addressBook.type"
					 list="addressBookTypesForDealer" required="true" 
					 theme="twms" /></td>
					 <td>
        <s:text name="label.customer.enterCustomerName"/>
    	<input id="name" type="text" name="name"/> 
    	<input type="button" class="searchButton" id="customerSearchButton" style="border:none;" />	
    	</td>
</tr>
</div>
<div id="addrDiv"> 
<div dojoType="twms.widget.Dialog" id="CustomerDialogContent" bgColor="white" bgOpacity="0.5" toggle="fade" toggleDuration="250" style="width:85%;height:75%">
<div id="customerSearchResultTag" dojoType="dojox.layout.ContentPane" layoutAlign="client" executeScripts="true"
     style="padding-bottom: 3px;overflow:auto;width:100%;height:100%">
</div>
<div class="buttonWrapperPrimary">
        <input type="button" name="Submit2" value="<s:text name='button.common.close' />" class="buttonGeneric" onClick="javascript:closeCustomerDialog()"/>
        <%--This feature of auto picking up the decendentOf attrubute should not be used. 
        this is an odd case and the tag supports picking up of the attribute as the label of current tab
        but this is highly discouraged. specify the value explicitly whenever possible. --%>
	         <u:openTab id="createCustomer" tagType="button" autoPickDecendentOf="true" tabLabel="Create Customer" url="show_customer.action?hideSelect=true" cssClass="buttonGeneric">
	        	  <s:text name="home_jsp.fileMenu.createCustomer" />
	         </u:openTab>     
</div>
</div>
</div>

</div>
</div>

</div>
</u:body>
</html>

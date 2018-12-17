<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="t" uri="twms"%>
<%@taglib prefix="u" uri="/ui-ext"%>
<%response.setHeader( "Pragma", "no-cache" );
response.addHeader( "Cache-Control", "must-revalidate" );
response.addHeader( "Cache-Control", "no-cache" );
response.addHeader( "Cache-Control", "no-store" );
response.setDateHeader("Expires", 0); %>

<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<s:head theme="twms" />
<style type="text/css">
.searchButton {
	vertical-align: middle;
	cursor: pointer;
	width: 18px;
	overflow: visible;
	background: url(../../../image/searchIcon.gif);
	background-repeat: no-repeat;
	background-position: center;
}
</style>
<u:stylePicker fileName="form.css" />
<u:stylePicker fileName="warrantyForm.css" />
<u:stylePicker fileName="common.css" />
<u:stylePicker fileName="base.css" />
</head>
<u:body>
<u:actionResults/>
<script>
  dojo.require("twms.widget.Dialog");
  dojo.require("dijit.layout.LayoutContainer");
  dojo.require("dijit.layout.ContentPane");
  dojo.require("dojox.layout.ContentPane");
  function getMatchingCustomers(index) {
	var customerName = dojo.byId("name").value;
    var params={};
    if(!isNaN(index)){
       params.pageNo=index;
    }else{
       params.pageNo=0;
    }
    params.customerStartsWith= dojo.string.escape("xhtml", customerName);
    params.customerType= "Company";
    params.selectedItemsIds = dojo.byId("selectedIds").value;
    var customerSearchResultDiv = dijit.byId("customerSearchResultTag");
    customerSearchResultDiv.setContent("<div class='loadingLidThrobber'><div class='loadingLidThrobberContent'></div></div>");
	twms.ajax.fireHtmlRequest("search_end_customer.action",params,function(data) {
			customerSearchResultDiv.destroyDescendants();
			customerSearchResultDiv.setContent(data);	
			delete data, customerSearchResultDiv;
		});
	delete customerName,params;
 }

dojo.addOnLoad(function() {    
    dojo.connect(dojo.byId("customerSearchButton"), "onclick","getMatchingCustomers");
});
</script>
	<div dojoType="dijit.layout.LayoutContainer" style="background: white;padding-bottom:5px;">
        <div dojoType="dijit.layout.ContentPane" layoutAlign="client">
            <s:form method="post" id="multiMergedCustomerSelection" name="searchMultipleMergedCustomers">
                <div class="section_div">
                    <div id="customer_merge_title" class="section_header">
                        <s:text name="accordion_jsp.accordionPane.mergeEndCustomer" />
                    </div>
                    <div style="margin-top:5px;">
                        <jsp:include flush="true" page="select_merge_customer.jsp" />
                    </div>
                </div>
                <div id="customer_info" class="section_div">
                    <div id="customer_info_title" class="section_header">
                        <s:text name="label.customer.searchCustomer" />
                    </div>
                    <div style="padding: 2px; padding-left: 7px; padding-right: 10px;">
                        <div class="labelStyle">
                            <s:text name="label.customer.enterCustomerName" /> 
                            <input id="name" type="text" name="customerStartsWith" value="<s:property value="customerStartsWith"/>" /> 
                            <input type="button" class="searchButton" id="customerSearchButton" style="border: none;" />
                        </div>
                    </div>
                    &nbsp; 
                    <div id="customerSearchResultTag" dojoType="dojox.layout.ContentPane" layoutAlign="client" executeScripts="true" style="padding-bottom:5px; overflow-x: hidden; overflow-y:auto;">
                        <jsp:include flush="true" page="end_customer_list.jsp" />
                    </div>
                </div>
            </s:form>	
        </div>
	</div>
</u:body>
</html>

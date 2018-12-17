<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="t" uri="twms"%>
<%@taglib prefix="u" uri="/ui-ext"%>
<%@ taglib prefix="authz" uri="authz" %>

<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<s:head theme="twms" />
	<u:stylePicker fileName="warrantyForm.css" />
	<u:stylePicker fileName="form.css" />
	<u:stylePicker fileName="common.css" />
	<u:stylePicker fileName="batterytestsheet.css" />
	<%@ include file="/i18N_javascript_vars.jsp"%>
	<script  type="text/javascript">
	    dojo.require("twms.widget.MultipleInventoryPicker");     				   
	    dojo.require("dojox.layout.ContentPane");
	    dojo.require("dijit.layout.ContentPane");    
	    dojo.require("twms.widget.Dialog");
	    dojo.require("dijit.layout.LayoutContainer");  
	    
	    dojo.addOnLoad(function() {
	        dojo.connect(dijit.byId("fleetInventoryUnScrap"), "onLoad", "bindInventorySelectionForm" );
	    });
	    
	    function bindInventorySelectionForm(){ 	
	        new dojo.io.FormBind({
	        	formNode: dojo.byId("manageFleetUnScrap"),
	        	load: function(data, e) {
	        		dijit.byId("fleetUnScrap_confirmation").show();
		        	var parentDiv = dojo.byId("fleetUnScrap_confirmation_div");
		        	parentDiv.innerHTML = data;        	
	        	}
	     	   });
	   		if(dojo.byId("fleetUnScrapCancel")) {
	 			dojo.connect(dojo.byId("fleetUnScrapCancel"),"onclick",function(){
				    dijit.byId("fleetUnScrap_confirmation").hide();
			    });
		      }
		 }				   
	</script>
</head>
<u:body >
	<u:actionResults />
	<div dojoType="dijit.layout.LayoutContainer"	style="width: 100%; height: 100%; background: white;">
		<div dojoType="dijit.layout.ContentPane" layoutAlign="client" executeScripts="true" >
			<div class="policy_section_div">
				<div class="section_header">
				    <s:text	name="title.fleetmanagement.inventoryUnScrap" />
				</div>
			
			<authz:ifNotPermitted resource="readOnlyAccesstoSLMS">
            <s:if test="(getLoggedInUser().businessUnits).size==1">
                <div dojoType="twms.widget.MultipleInventoryPicker"
                     searchHandlerUrl="searchInventories_fleetUnScrap.action"
                     useInnerHTML="false" selectionHandlerUrl="show_inventories_fleetUnScrap.action"
                     selectedItemsContentPane="fleetInventoryUnScrap" searchActionType="HIDEDTD"></div>
            </s:if>
            <s:elseif test="(getLoggedInUser().businessUnits).size>1">
                <div dojoType="twms.widget.MultipleInventoryPicker"
                     searchHandlerUrl="searchInventories_fleetUnScrap.action"
                     useInnerHTML="false" selectionHandlerUrl="show_inventories_fleetUnScrap.action"
                     isMultiLineUser="TRUE"
                     selectedItemsContentPane="fleetInventoryUnScrap" searchActionType="HIDEDTD"></div>
            </s:elseif>
            </authz:ifNotPermitted>
            </div>
			<div dojoType="dijit.layout.ContentPane" executeScripts="true"
				scriptSeparation="false" id="fleetInventoryUnScrap"></div>
		</div>
	</div>
    <div dojoType="twms.widget.Dialog" id="fleetUnScrap_confirmation" bgColor="white"
        bgOpacity="0.5" toggle="fade" toggleDuration="250" style="width: 90%;height: 50%;overflow: auto;">
        <div id="fleetUnScrap_confirmation_div" >
        </div>
    </div>
</u:body>
</html>
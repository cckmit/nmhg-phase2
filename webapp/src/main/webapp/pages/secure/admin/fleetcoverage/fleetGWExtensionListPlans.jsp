<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="t" uri="twms"%>
<%@taglib prefix="u" uri="/ui-ext"%>
<%@ taglib prefix="authz" uri="authz" %>


<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">    
    <s:head theme="twms"/>
    <u:stylePicker fileName="warrantyForm.css"/>
    <u:stylePicker fileName="form.css"/>
    <u:stylePicker fileName="common.css"/>
    <u:stylePicker fileName="batterytestsheet.css"/>
    <%@ include file="/i18N_javascript_vars.jsp" %>
      <script type="text/javascript" src="scripts/jscalendar/calendar.js"></script>
    <script type="text/javascript"
            src="scripts/jscalendar/lang/calendar-en.js"></script>
    <script type="text/javascript"
            src="scripts/jscalendar/calendar-setup.js"></script>

    <link href="scripts/jscalendar/calendar-brown.css" rel="stylesheet"
          type="text/css"> 
      
    <script>
     var selectionParams = {};
     dojo.addOnLoad(function() {
     	 setSelectedGoodWillPolicy();  
     })
     
     function setSelectedGoodWillPolicy(){     	     	
     	selectionParams.selectedGoodWillPolicy =  dojo.byId('goodwillpolicies').value;     	
     }     
     dojo.require("twms.widget.MultipleInventoryPicker");     				   
</script>
</head>
<u:body >
	<u:actionResults/>	
		<div dojoType="dijit.layout.LayoutContainer" style="width: 100%; height: 100%; background: white;">
			<div dojoType="dojox.layout.ContentPane" layoutAlign="client" >
				<div class="policy_section_div">
				<div class="section_header">
						<s:text name="title.fleetmanagement.goodWillExtension" />
				</div>
			
				<authz:ifNotPermitted resource="readOnlyAccesstoSLMS">
                <s:if test="(getLoggedInUser().businessUnits).size==1">
                    <div dojoType="twms.widget.MultipleInventoryPicker"
                         searchHandlerUrl="searchInventories_GWExtension.action"
                         useInnerHTML="false"
                         selectionHandlerUrl="show_inventories_GWPolicy.action"
                         searchActionType="HIDEDTD"
                         selectedItemsContentPane="fleetGWExtension"
                         selectionHandlerParams="selectionParams"
                         searchHandlerParams = "selectionParams" ></div>
                </s:if>
                <s:elseif test="(getLoggedInUser().businessUnits).size>1">
                    <div dojoType="twms.widget.MultipleInventoryPicker"
                         searchHandlerUrl="searchInventories_GWExtension.action"
                         useInnerHTML="false"
                         selectionHandlerUrl="show_inventories_GWPolicy.action"
                         searchActionType="HIDEDTD"
                         selectedItemsContentPane="fleetGWExtension"
                         selectionHandlerParams="selectionParams"
                         searchHandlerParams = "selectionParams" isMultiLineUser="TRUE" ></div>
                </s:elseif>
                </authz:ifNotPermitted>
                <div dojoType="dojox.layout.ContentPane" id = "fleetGWExtension"  style="margin-left:7px;" class="labelStyle">
					<s:text name="accordionLabel.manageBusinessRule.PolicyRules"/> : 
					<s:select id="goodwillpolicies" name="selectedGoodWillPlan" list="availableGoodWillPlans" listKey="id" listValue="description" onchange="setSelectedGoodWillPolicy()"/>		
				</div>				
			</div>				
		</div>	
		</div>	
			<div dojoType="twms.widget.Dialog" id="gwextension_confirmation" style="width:75%;height:75%" title="<s:text name="label.manageFleetCoverage.confirmation"/>:" >
<!--				<div dojoType="dijit.layout.LayoutContainer" style="background: #F3FBFE; border: 1px solid #EFEBF7">-->
<!--					<div dojoType="dijit.layout.ContentPane" style="width:100%;height:100%;overflow:auto;">-->
													
                        <div id="gwextension_confirmation_div" >
                        </div>
<!--                    </div>													-->
<!--                </div>					-->
            </div>
</u:body>
</html>




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
<%@ taglib prefix="sd" uri="/struts-dojo-tags"%>
<%@taglib prefix="t" uri="twms"%><%@taglib prefix="u" uri="/ui-ext"%>

<jsp:include page="/i18N_javascript_vars.jsp"/>

<style type="text/css">
    button#launchFaultCodeTree {
        background-image: url(image/searchIcon.gif);
        background-repeat: no-repeat;
        width : 16px;
        height : 16px;
        margin : 0px;
        padding : 0px;
        border : 0px;
    }
</style>


<html>
<head>
    <title><s:text name="label.complaints.newConsumerComplaintPage2"/></title>
    <s:head theme="twms"/>
    <u:stylePicker fileName="common.css"/>
    <u:stylePicker fileName="form.css"/>
    <u:stylePicker fileName="warrantyForm.css"/>

    <script type="text/javascript">
        dojo.addOnLoad(function() {
            top.publishEvent("/refresh/folderCount")
        });
        dojo.require("twms.widget.Select");
    </script>
    <script type="text/javascript" src="scripts/ServiceProcedureTree.js"></script>
</head>

<u:body>
<div dojoType="dijit.layout.ContentPane" layoutAlign="client">
<s:form method="post" theme="twms" validate="true" id="form"
        name="saveConsumerComplaint" action="saveConsumerComplaint.action">
	<u:actionResults/>
	
	<s:hidden name="vehicleInfoType" value="%{vehicleInfoType}" />
	<s:hidden name="vehicleInfoValue" value="%{vehicleInfoValue}" />
	<s:hidden name="model" value="%{model}" />
	<s:hidden name="year" value="%{year}" />
	<s:hidden name="complaintType" value="%{complaintType}" />			
	<div id="vehicle_info" class="section_div">
		<div id="vehicle_info_title" class="section_heading">
			<s:text name="title.complaints.vehicleInfo" />
		</div>
		<table class="form" cellpadding="0" cellspacing="0">
			<s:if test="vehicleInfoType.equals('serial') || vehicleInfoType.equals('item')">
				<tr>
					<s:if test="vehicleInfoType.equals('serial')">
						<td><label class="labelStyle"><s:text name="label.common.serialNumber" />:</label></td>
						<td><s:property value="vehicleInfoValue"/></td>
					</s:if>
					<td><label class="labelStyle"><s:text name="label.common.itemNumber" />:</label></td>
					<td><s:property value="itemReference.unserializedItem.alternateNumber"/></td>					
				</tr>						
				<tr>
					<td><label class="labelStyle"><s:text name="label.common.product" />:</label></td>
					<td><s:property value="itemReference.unserializedItem.make"/></td>
					<td><label class="labelStyle"><s:text name="label.common.model" />:</label></td>
					<td><s:property value="itemReference.unserializedItem.model.name"/></td>
					<td><label class="labelStyle"><s:text name="label.common.year" />:</label></td>
					<td><s:property value="itemReference.unserializedItem.year"/></td>						
				</tr>			
			</s:if>
			<s:else>				
				<tr>
					<td><label class="labelStyle"><s:text name="label.common.product" />:</label></td>
					<td><s:property value="vehicleInfoValue" /></td>
					<td><label class="labelStyle" ><s:text name="label.common.model" />:</label></td>
					<td><s:property value="model" /></td>
					<td><label class="labelStyle"><s:text name="label.common.year" />:</label></td>
					<td><s:property value="year" /></td>						
				</tr>			
			</s:else>
		</table>
	</div>	        
	<div id="incident_details" class="section_div">
		<div id="incident_details_info_title" class="section_heading">
			<s:text name="title.complaints.incidentDetails" />
		</div>        
		<table class="form" cellpadding="0" cellspacing="0">
			<tr>
				<td><label class="labelStyle"><s:text name="label.complaints.incidentDate" />:</label></td>
                <td><sd:datetimepicker name='complaint.incidentDate' value='%{complaint.incidentDate}' id='warrantyStartDate' /></td>
				<td><label class="labelStyle"><s:text name="label.complaints.incidentDescription" />:</label></td>
				<td><s:textfield name="complaint.incidentDescription" /></td>
			</tr>
			<tr>
				<td><label class="labelStyle"><s:text name="label.complaints.noOfFailures" />:</label></td>
				<td><s:textfield name="complaint.numberOfFailures" /></td>
				<td><label class="labelStyle"><s:text name="label.complaints.noOfDeaths" />:</label></td>
				<td><s:textfield name="complaint.numberOfDeaths" /></td>
			</tr>
			<tr>
				<td><label class="labelStyle"><s:text name="label.complaints.noOfPersonsInjured" />:</label></td>
				<td><s:textfield name="complaint.numberOfInjuredPersons" /></td>
				<td><label class="labelStyle"><s:text name="label.complaints.failedComponent" />:</label></td>
				<td><s:textfield name="complaint.failedComponent" /></td>
			</tr>
			<tr>
				<td><label class="labelStyle"><s:text name="label.complaints.wasThereFire" />:</label></td>
				<td ><s:checkbox name="complaint.isThereAFire" cssClass="checkbox"/></td>
				<td><label class="labelStyle"><s:text name="label.complaints.isPropertyDamaged" />:</label></td>
				<td ><s:checkbox name="complaint.isPropertyDamaged" cssClass="checkbox"/></td>
			</tr>
			<tr>
				<td><label class="labelStyle"><s:text name="label.complaints.wasThereCrash" />:</label></td>
				<td ><s:checkbox name="complaint.isThereACrash" cssClass="checkbox"/></td>
				<td><label class="labelStyle"><s:text name="label.complaints.reportedToPolice" />:</label></td>
				<td><s:checkbox name="complaint.hasReportedToPolice" cssClass="checkbox"/></td>
			</tr>
    <script type="text/javascript">
        var faultCodeTree = <s:property value="jsonFaultCodeTree" escape="false"/>;
        var faultSelected = "<s:property value="complaint.faultCodeRef.definition.code"/>";

        dojo.addOnLoad(function(){
            var treeWidget = dijit.byId("faultCodeTreeWidget");
            dojo.setSelectable(treeWidget.domNode, false);
            var faultCodeTreeManager = new tavant.twms.FaultCodeTreeManager(faultCodeTree, treeWidget, "faultCodeTree");
            var renderer = new tavant.twms.FaultCodeRenderer(dojo.byId("faultCode"), faultCodeTreeManager, faultSelected, spanTemplateText.markup);
            renderer.renderSelectedCode();
            var dialog = dijit.byId("browserForFaultCode");
            dojo.connect(dojo.byId("launchFaultCodeTree"), "onclick", function(event) {
                dojo.stopEvent(event);
                faultCodeTreeManager.renderTree();
                dialog.show();
            });
            dojo.connect(dojo.byId("clickToClose_faultCode"), "onclick", function() {
                dialog.hide();
            });
            dojo.connect(dojo.byId("addFaultCode"),"onclick", function() {
                renderer.renderSelectedCode();
                dialog.hide();
            });
        });
    </script>

    <u:jsVar varName="spanTemplateText">
        <span title="%{fullNameTooltip}">
            %{completeCode}
            <input type="hidden" value="%{faultCodeId}" name="complaint.faultCodeRef"/>
        </span>
    </u:jsVar>
    <div style="display: none">
        <div dojoType="twms.widget.Dialog" id="browserForFaultCode" bgColor="#FFF" bgOpacity="0.5" toggle="fade" toggleDuration="250">
            <div dojoType="dijit.layout.LayoutContainer" style="width:500px; height:400px; background: #F3FBFE; border: 1px solid #EFEBF7;
			 border-top : none !important; padding: 0px; margin: 0px;overflow:hidden">
                <div dojoType="dijit.layout.ContentPane" layoutAlign="top" style="height: 23px; background: url(image/menubg_new.gif) repeat-x;">
                    <span style="float: left; font-size: 9pt; display:inline" class="TitleBar">
                        Select Fault Code
                    </span>
                    <img id="clickToClose_faultCode" style="float: right; margin-top: 2px;padding-right:2px;" src="image/CloseRuleWizard.gif"/>
                </div>
                <div style="width:500px;height:280px;overflow:auto ">
				<div dojoType="dijit.layout.ContentPane" layoutAlign="client"  style="background:#F3FBFE;font-size: 9pt">
                    <div dojoType="dijit.Tree" id="faultCodeTreeWidget"></div>
                </div>
                
				</div>
				<div dojoType="dijit.layout.ContentPane" layoutALign="bottom" style="border-top:1px solid #EFEBF7">
                    <button id="addFaultCode"><s:text name="setFaultCode"/></button>
                </div>
            </div>
        </div>
    </div>
    <tr>
        <td>
            <label class="labelStyle"><s:text name="claim.failure.detailsHeader.faultCode"/></label>
            <button id="launchFaultCodeTree"/>
        </td>
        <td>
            <div id="faultCode"></div>
        </td>

        <td colspan="2"></td>
    </tr>
			
		</table>
	</div>
	<div id="customer_info" class="section_div">
		<div id="customer_info_title" class="section_heading">
			<s:text name="title.common.customerInfo" />
		</div>        
		<table class="form" cellpadding="0" cellspacing="0">
			<tr>
				<td><label class="labelStyle"><s:text name="label.complaints.firstName" />:</label></td>
				<td><s:textfield name="complaint.consumer.firstName" /></td>
				<td><label class="labelStyle"><s:text name="label.complaints.lastName" />:</label></td>
				<td><s:textfield name="complaint.consumer.lastName" /></td>
			</tr>
			<tr>
				<td><label class="labelStyle"><s:text name="label.common.address" />:</label></td>
				<td colspan="3"><t:textarea name="complaint.consumer.address.addressLine1" rows="3" cssStyle="width: 60%;"/></td>
			</tr>			
			<tr>
				<td><label class="labelStyle"><s:text name="label.common.city" />:</label></td>
				<td><s:textfield name="complaint.consumer.address.city" /></td>
				<td><label class="labelStyle"><s:text name="label.common.state" />:</label></td>
				<td>				
					<div id='stateTypeComponentDiv'>
						<span id='usComponentDiv'>
			            	<s:select name="complaint.consumer.address.state" list="usStates"
								size="60" listKey="stateCode" listValue="state" headerKey="-1"
                                headerValue="%{getText('label.common.selectHeader')}" />
						</span>
						<span id='canadaComponentDiv'>
			            	<s:select name="complaint.consumer.address.state" list="canadaStates"
								listKey="stateCode" listValue="state" headerKey="-1"
                                headerValue="%{getText('label.common.selectHeader')}" />
						</span>
						<span id='otherComponentDiv'>
							<s:textfield name="complaint.consumer.address.state" />
						</span>
					</div>				
				</td>
			</tr>
			<tr>
				<td><label class="labelStyle"><s:text name="label.common.zipCode" />:</label></td>
				<td><s:textfield name="complaint.consumer.address.zipCode" /></td>
				<td><label class="labelStyle"><s:text name="label.common.country" />:</label></td>
	            <td> 
	            	<select dojoType="twms.widget.Select" id="specifyType" name="complaint.consumer.address.country">
	                    <option value="USA"><s:text name="label.common.us" /></option>
	                    <option value="Canada"><s:text name="label.common.canada" /></option>
	                    <option value="Other"><s:text name="label.common.other" /></option>
	                </select>
	                <script type="text/javascript">
	                dojo.addOnLoad(function() {
						placeHoldingDiv = dojo.byId("stateTypeComponentDiv");			                	
	                	usComponent = dojo.byId("usComponentDiv");
	                	canadaComponent = dojo.byId("canadaComponentDiv");
	                	otherComponent=dojo.byId("otherComponentDiv");
						
						dojo.dom.removeNode(canadaComponent);
						dojo.dom.removeNode(otherComponent);
						
	                	dijit.byId("specifyType").onChange = function(value) {	                    	
	                    	if(value == "USA") {
	                    		dojo.dom.removeNode(canadaComponent);
	                    		dojo.dom.removeNode(otherComponent);
	                    		dojo.dom.insertAtIndex(usComponent, placeHoldingDiv, 0);
	                    	}else if(value == "Canada") {
	                    		dojo.dom.removeNode(usComponent);
	                    		dojo.dom.removeNode(otherComponent);
	                    		dojo.dom.insertAtIndex(canadaComponent, placeHoldingDiv, 0);
	                    	}else {
	                    		dojo.dom.removeNode(usComponent);
	                    		dojo.dom.removeNode(canadaComponent);
	                    		dojo.dom.insertAtIndex(otherComponent, placeHoldingDiv, 0);	                    	
	                    	}
	                    };
	                });
	                </script>					
				</td>
			</tr>
			<tr>
				<td><label class="labelStyle"><s:text name="label.common.phone" />:</label></td>
				<td><s:textfield name="complaint.consumer.address.phone" /></td>
			</tr>
		</table>
	</div>
    <div align="center" class="buttons spacingAtTop">
      
                    <s:submit value="%{getText('button.common.save')}" type="input" name="userAction"/>
               </div>
</s:form>
</div>
</u:body>
</html>
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


<html>
<head>
    <title><s:text name="label.complaints.newConsumerComplaintPage1"/></title>
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
</head>

<u:body>

<div dojoType="dijit.layout.ContentPane" layoutAlign="client">
<s:form method="post" theme="twms" validate="true" id="form"
        name="saveConsumerComplaint" action="showConsumerIncidentForm.action">
    <u:actionResults/>
	<div id="vehicle_info" class="section_div">
		<s:hidden name="complaintType" value="%{complaintType}" />
		<div id="vehicle_info_title" class="section_heading">
			<s:text name="title.complaints.vehicleInfo" />
		</div>
		<table class="form" cellpadding="0" cellspacing="0">
			<tr>
				<td class="non_editable">
					<label><s:text name="label.complaints.specify" />:</label>
	                <select dojoType="twms.widget.Select" id="specifyType" name="vehicleInfoType">
	                    <option value="serial"><s:text name="label.common.serialNumber" /></option>
	                    <option value="item"><s:text name="label.common.itemNumber" /></option>
	                    <option value="product"><s:text name="label.complaints.productModelYear" /></option>
	                </select>
	                <script type="text/javascript">
	                dojo.addOnLoad(function() {
						placeHoldingDiv = dojo.byId("specifyTypeComponent");			                	
	                	slNoComponent = dojo.byId("serialNumberComponent");
	                	itemComponent = dojo.byId("itemNumberComponent");
	                	prodComponent = dojo.byId("productComponent");
						
						dojo.dom.removeNode(itemComponent);
						dojo.dom.removeNode(prodComponent);
						
	                	dijit.byId("specifyType").onChange = function(value) {	                    	
	                    	if(value == "serial") {
	                    		dojo.dom.removeNode(itemComponent);
	                    		dojo.dom.removeNode(prodComponent);
	                    		dojo.dom.insertAtIndex(slNoComponent, placeHoldingDiv, 0);
	                    	}else if(value == "item") {
	                    		dojo.dom.removeNode(slNoComponent);
	                    		dojo.dom.removeNode(prodComponent);
	                    		dojo.dom.insertAtIndex(itemComponent, placeHoldingDiv, 0);
	                    	}else {
	                    		dojo.dom.removeNode(slNoComponent);
	                    		dojo.dom.removeNode(itemComponent);
	                    		dojo.dom.insertAtIndex(prodComponent, placeHoldingDiv, 0);	                    	
	                    	}
	                    };
	                });
	                </script>					
				</td>
			</tr>			
		</table>
		<div id='specifyTypeComponent'>
			<div id='serialNumberComponent'>
				<table class="form" cellpadding="0" cellspacing="0">
					<tr>
						<td><label><s:text name="label.common.serialNumber" />:</label></td>  
			            <td>
			                <sd:autocompleter id='serialNumber' href='list_claim_sl_nos.action' name='vehicleInfoValue' loadOnTextChange='true' loadMinimumCount='1' showDownArrow='false' indicator='indicatorSlNo' value='%{itemReference.referredInventoryItem.serialNumber}' />
			                <img style="display: none;" id="indicatorSlNo" class="indicator"
			                     src="image/indicator.gif" alt="Loading..."/>
			            </td>
			        </tr>
			    </table>         
			</div>
			<div id='itemNumberComponent'>
				<table class="form" cellpadding="0" cellspacing="0">
					<tr>
						<td><label><s:text name="label.common.itemNumber" />:</label></td>
			            <td>    
			                <sd:autocompleter id='itemNumber' href='list_claim_item_nos.action' name='vehicleInfoValue' loadOnTextChange='true' loadMinimumCount='1' showDownArrow='false' indicator='indicatorItemNo' value='%{itemReference.referredItem.alternateNumber' />			
			                <img style="display: none;" id="indicatorItemNo" class="indicator"
			                     src="image/indicator.gif" alt="Loading..."/>                    
			            </td>
			        </tr>
			    </table>         
			</div>
			<div id='productComponent'>
				<table class="form" cellpadding="0" cellspacing="0">
					<tr>
						<td><label><s:text name="label.common.product" />:</label></td>
						<td><s:textfield name="vehicleInfoValue" /></td>
						<td><label><s:text name="label.common.model" />:</label></td>
						<td><s:textfield name="model" /></td>
					</tr>
					<tr>	
						<td><label><s:text name="label.common.year" />:</label></td>
						<td><s:textfield name="year" /></td>						
					</tr>
				</table>			
			</div>
		</div>				
	</div>	  
    <table align="center" border="0" cellpadding="0" cellspacing="0" class="buttons" >
        <tbody>
            <tr>
                <td align="center">
                    <s:submit value="%{getText('button.common.continue')}" type="input" name="userAction"/>
                </td>
            </tr>
        </tbody>
    </table>
	      
</s:form>
</div>
</u:body>
</html>
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
    <title><s:text name="label.complaints.updateComplaintPage1"/></title>
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
<s:form method="post" theme="twms" validate="true" id="form"
        name="updateConsumerComplaint" action="update_complaint_page2.action">
    <u:actionResults/>
	<div id="vehicle_info" class="section_div">
		<s:hidden name="complaintType" value="%{complaintType}" />
		<div id="vehicle_info_title" class="section_heading">
			<s:text name="title.complaints.vehicleInfo" />
		</div>
		<table class="form" cellpadding="0" cellspacing="0">
			<tr>
				<td class="non_editable">
					<s:text name="label.complaints.specify" />:				
	                <select dojoType="twms.widget.Select" id="specifyType" name="vehicleInfoType">
				
						<s:if test="vehicleInfoType.equals('serial')" >
		                    <option value="serial" selected="selected"><s:text name="label.common.serialNumber" /></option>
		                    <option value="item"><s:text name="label.common.itemNumber" /></option>
		                    <option value="product"><s:text name="label.complaints.productModelYear" /></option>					
						</s:if>
						<s:elseif test="vehicleInfoType.equals('item')">
		                    <option value="serial"><s:text name="label.common.serialNumber" /></option>
		                    <option value="item"  selected="selected"><s:text name="label.common.itemNumber" /></option>
		                    <option value="product"><s:text name="label.complaints.productModelYear" /></option>										
						</s:elseif>
						<s:else>
		                    <option value="serial"><s:text name="label.common.serialNumber" /></option>
		                    <option value="item"><s:text name="label.common.itemNumber" /></option>
		                    <option value="product" selected="selected"><s:text name="label.complaints.productModelYear" /></option>															
						</s:else>
	                </select>	                
	                <script type="text/javascript">
	                dojo.addOnLoad(function() {
						placeHoldingDiv = dojo.byId("specifyTypeComponent");			                	
	                	slNoComponent = dojo.byId("serialNumberComponent");
	                	itemComponent = dojo.byId("itemNumberComponent");
	                	prodComponent=dojo.byId("productComponent");

						typeValue = "<s:property value="vehicleInfoType"/>";
						if(typeValue == "serial") {
							dojo.dom.removeNode(itemComponent);
							dojo.dom.removeNode(prodComponent);
						}else if(typeValue == "item") {
							dojo.dom.removeNode(slNoComponent);
							dojo.dom.removeNode(prodComponent);						
						}else {
							dojo.dom.removeNode(slNoComponent);
							dojo.dom.removeNode(itemComponent);												
						}
							
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
			<span id='serialNumberComponent'>
				<table class="form" cellpadding="0" cellspacing="0">
					<tr>
						<td class="non_editable"><s:text name="label.common.serialNumber" />:</td>  
			            <td>
			                <sd:autocompleter id='serialNumber' href='list_claim_sl_nos.action' name='vehicleInfoValue' loadOnTextChange='true' loadMinimumCount='1' showDownArrow='false' indicator='indicatorSlNo' value='%{vehicleInfoValue}' />
			                <img style="display: none;" id="indicatorSlNo" class="indicator"
			                     src="image/indicator.gif" alt="Loading..."/>
			            </td>
			        </tr>
			    </table>         
			</span>
			<span id='itemNumberComponent'>
				<table class="form" cellpadding="0" cellspacing="0">
					<tr>
						<td class="non_editable"><s:text name="label.common.itemNumber" />:</td>
			            <td>    
			                <sd:autocompleter id='itemNumber' href='list_claim_item_nos.action' name='vehicleInfoValue' loadOnTextChange='true' loadMinimumCount='1' showDownArrow='false' indicator='indicatorItemNo' value='%{vehicleInfoValue}' />			
			                <img style="display: none;" id="indicatorItemNo" class="indicator"
			                     src="image/indicator.gif" alt="Loading..."/>                    
			            </td>
			        </tr>
			    </table>         
			</span>
			<span id='productComponent'>
				<table class="form" cellpadding="0" cellspacing="0">
					<tr>
						<td class="non_editable"><s:text name="label.common.product" />:</td>
						<td><s:textfield name="vehicleInfoValue" /></td>
						<td class="non_editable"><s:text name="label.common.model" />:</td>
						<td><s:textfield name="model" /></td>
					</tr>
					<tr>	
						<td class="non_editable"><s:text name="label.common.year" />:</td>
						<td><s:textfield name="year" /></td>						
					</tr>
				</table>			
			</span>
		</div>				
	</div>
	<s:hidden name="complaint" value="%{complaint.id}" />	  
    <table align="center" border="0" cellpadding="0" cellspacing="0" class="buttons" border="1">
        <tbody>
            <tr>
                <td align="center">
                    <s:submit value="%{getText('button.common.continue')}" type="input" name="userAction"/>
                </td>
            </tr>
        </tbody>
    </table>
	      
</s:form>
</u:body>
</html>
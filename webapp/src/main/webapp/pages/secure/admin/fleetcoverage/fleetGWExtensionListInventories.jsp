<%@page contentType="text/html"%>
<%@ taglib prefix="sd" uri="/struts-dojo-tags"%>
<%@page pageEncoding="UTF-8"%>
<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="t" uri="twms"%>
<%@taglib prefix="u" uri="/ui-ext"%>

<u:stylePicker fileName="warrantyForm.css"/>
    <u:stylePicker fileName="form.css"/>
    <u:stylePicker fileName="common.css"/>
    <u:stylePicker fileName="adminPayment.css"/>
    <%@ include file="/i18N_javascript_vars.jsp" %>
      <script type="text/javascript" src="scripts/jscalendar/calendar.js"></script>
    <script type="text/javascript"
            src="scripts/jscalendar/lang/calendar-en.js"></script>
    <script type="text/javascript"
            src="scripts/jscalendar/calendar-setup.js"></script>

    <link href="scripts/jscalendar/calendar-brown.css" rel="stylesheet"
          type="text/css"> 
    <script type="text/javascript" src="scripts/CheckBoxListControl.js"></script>
<script>	 
     var selectionParams = {};     
     dojo.addOnLoad(function() {     	
		new dojo.io.FormBind({
        	formNode: dojo.byId("manageFleetCoverage"),
        	load: function(data, e) {
        		dijit.byId("gwextension_confirmation").show();
	        	var parentDiv = dojo.byId("gwextension_confirmation_div");
	        	parentDiv.innerHTML = data;        	
        	}
      	});
   		if(dojo.byId("gwExtensionCancel")) {
 			dojo.connect(dojo.byId("gwExtensionCancel"),"onclick",function(){
			dijit.byId("gwextension_confirmation").hide();
		 });
	    }	
	    
	    var multiCheckBoxControl = new CheckBoxListControl(dojo.byId("masterCheckbox_item"));
		var numItems = <s:property value="inventoryItemMappings.size" />;
        for (var i = 0; i < numItems; i++) {
            multiCheckBoxControl.addListElement(dojo.byId("item_" + i));
        }
     });
     
     function closeCurrentTab() {
            closeTab(getTabHavingId(getTabDetailsForIframe().tabId));
      }	
      
     dojo.require("twms.widget.MultipleInventoryPicker");     				   
</script>   

<s:form action="gwFleetExtensionConf"  id="manageFleetCoverage" validate="true">
<s:hidden name="selectedGoodWillPolicy" value="%{selectedGoodWillPolicy.id}"/>
	<s:if test="inventoryItems != null">
		<table border="0" cellspacing="0" cellpadding="0"  width="96%">
			<tr>
				<td width="20%"  class="labelStyle">	
					<s:text name="label.common.policyName"/>
				</td>
				<td>	
					<s:property value="selectedGoodWillPolicy.description"/>
				</td>
			</tr>
			<tr>
				<td width="20%" class="labelStyle">		
		    		<s:text name="label.common.policyCode"/>
		    	</td>
		    	<td>	
		    		<s:property value="selectedGoodWillPolicy.code"/>
		    	</td>
		    </tr>	
		</table>
		<div  style="clear:both" >
			<div class="mainTitle">
					<s:text name="title.fleetmanagement.goodWillExtension" />
			</div>		
			<table class="grid borderForTable" border="0" cellspacing="0" cellpadding="0" width="96%" style="margin:5px;"> 					
				<thead>
					    <tr>
							<th class="warColHeader" align="center" width="2%">
								<input type="checkbox" id="masterCheckbox_item" />
							</th>
					   		<th class="warColHeader" align="left"><s:text name="label.common.serialNumber"/></th>
					   		<th class="warColHeader" align="left"><s:text name="label.common.modelNumber"/></th>
					   		<th class="warColHeader" align="left"><s:text name="columnTitle.common.description"/></th>
					   		<th class="warColHeader" align="left"><s:text name="columnTitle.inventoryAction.delivery_date"/></th>
					   		<th class="warColHeader" align="left"><s:text name="label.common.hoursOnTruck"/></th>
						</tr>
					</thead>				
					<tbody id="policy_list">	
						<s:iterator value = "inventoryItemMappings" status="inventoryIterator">
							<tr>
								<td align="center">
									<s:checkbox name="inventoryItemMappings[%{#inventoryIterator.index}].inventoryItem"  
								     		fieldValue="%{inventoryItem.id}" id="item_%{#inventoryIterator.index}"/>   
								</td>
								<td><s:property value="inventoryItem.serialNumber"/></td>
								<td><s:property value="inventoryItem.ofType.model.name"/></td>
								<td><s:property value="inventoryItem.ofType.description"/></td>
								<td><s:property value="inventoryItem.deliveryDate"/></td>								
								<td><s:property value="inventoryItem.hoursOnMachine"/></td>
						 	</tr>
				        </s:iterator>					
					</tbody>
			</table>	   
	   <table  border="0" cellspacing="0" cellpadding="0" style="width:96%;" class="grid">
		   <tr>
		    	<td>
		    	</td>
		   </tr>
		   <tr>		    
		    	<td width="20%">		    		
		          		<label for="probably_cause" class="labelStyle"><s:text name="label.manageFleetCoverage.tillDate"/>:</label>	           		
		          			           				           		
		    	</td>
		    	<td><sd:datetimepicker name='manageCoverageDate' displayFormat='MM/dd/yyyy' /></td>
		  </tr>
		  <tr>		    
		    	<td width="20%">		    		
		          		<label for="probably_cause" class="labelStyle"><s:text name="label.common.hoursCovered"/>:</label>	           		
		          		          				           		
		    		</td>
		    		<td><input type="text" name="serviceHoursCovered" size="6"/></td>
		  		</tr>  
		  <tr>
		    	<td width="20%">
	           		<label for="probably_cause" class="labelStyle"><s:text name="label.common.comments"/>:</label>
	           		
	       		</td>
	       		<td><t:textarea  rows="3" cols="30"
	               		name="manageCoverageComments" /></td>
	       	</tr>	
	   </table>
	    </div>
	   <div id="submit" align="center" style="margin:5px;" class="spacingAtTop">
			<s:if test="inventoryItems.size() != 0">
			<s:submit id="manage_fleet_coverage_submit"
			cssClass="buttonGeneric" value="%{getText('button.common.continue')}"/>&nbsp;</s:if><input 
			class="buttonGeneric" type="button" value='<s:text name="label.cancel"/>'
			onclick="closeCurrentTab()" />
		</div>
	 		
	</s:if>	
</s:form>	

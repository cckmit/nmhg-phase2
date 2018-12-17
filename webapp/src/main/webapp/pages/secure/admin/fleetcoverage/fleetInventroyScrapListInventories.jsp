<%@page contentType="text/html"%>
<%@ taglib prefix="sd" uri="/struts-dojo-tags"%>
<%@page pageEncoding="UTF-8"%>
<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="t" uri="twms"%>
<%@taglib prefix="u" uri="/ui-ext"%>

<u:stylePicker fileName="warrantyForm.css"/>
<u:stylePicker fileName="form.css"/>
<u:stylePicker fileName="common.css"/>
<%@ include file="/i18N_javascript_vars.jsp" %>
<script>
     var selectionParams = {};
     
     dojo.addOnLoad(function() {
		new dojo.io.FormBind({
        	formNode: dojo.byId("manageFleetScrap"),
        	load: function(data, e) {
        		dijit.byId("fleetscrap_confirmation").show();
	        	var parentDiv = dojo.byId("fleetscrap_confirmation_div");
	        	parentDiv.innerHTML = data;        	
        	}
      	});
   		if(dojo.byId("fleetScrapCancel")) {
 			dojo.connect(dojo.byId("fleetScrapCancel"),"onclick",function(){
			dijit.byId("fleetscrap_confirmation").hide();
		    });
	      }	
    });
     
     function closeCurrentTab() {
            closeTab(getTabHavingId(getTabDetailsForIframe().tabId));
      }	
      
     dojo.require("twms.widget.MultipleInventoryPicker");     				   
</script> 

<s:form  id="manageFleetScrap" validate="true">
	<s:if test="scrapInventoryItems != null">		
		<div  style="clear:both" >
			<div class="policy_section_div">
			<div class="section_header">
				<s:text name="title.fleetmanagement.inventoryscrap" />
			</div>		
			<table class="grid borderForTable" border="0" cellspacing="0" cellpadding="0"  width="100%" align="center"> 					
				<thead>
				    <tr>
						<th class="warColHeader" align="center" style="padding:0;margin:0" width="2%">
							<input type="checkbox" id="masterCheckBox" />
						</th>
				   		<th class="warColHeader" align="left"><s:text name="label.common.serialNumber"/></th>
				   		<th class="warColHeader" align="left"><s:text name="label.common.modelNumber"/></th>
				   		<th class="warColHeader" align="left"><s:text name="columnTitle.common.description"/></th>
				   		<th class="warColHeader" align="left"><s:text name="label.managePolicy.itemCondition"/></th>
					</tr>
				</thead>				
				<tbody id="inventory_list">	
					<s:iterator value = "scrapInventoryItems" status="inventoryIterator">
						<tr>
							<td>
								<s:checkbox name="scrapInventoryItems[%{#inventoryIterator.index}]"  
							     		fieldValue="%{id}" id="item_%{#inventoryIterator.index}" />   
							</td>
							<td><s:property value="serialNumber"/></td>
							<td><s:property value="ofType.model.name"/></td>
							<td><s:property value="ofType.description" /></td>								
							<td><s:property value="conditionType.itemCondition" /></td>
					    </tr>
			        </s:iterator>					
				</tbody>
			</table>
	   	<br/>
	   <table  border="0" cellspacing="0" cellpadding="0"  width="100%" class="grid" style="padding-left:5px;">
	       <tr>
				<td width="20%">
					<s:label cssClass="labelStyle" value="%{getText('label.scrap.scrapDate')}"/> :
					
				</td>
				<td>
				<sd:datetimepicker name='scrapDate' value='%{scrapDate}' id='scrapDate' />
				</td>
			</tr>
	       <tr>
		    	<td width="20%">
	           		<label for="probably_cause" class="labelStyle"><s:text name="label.common.comments"/>:</label>
	           		
	       		</td>
	       		<td>
	       		<t:textarea  rows="3" cols="60"
	               		name="scrapComments" />
	       		</td>
	       	</tr>	
	   </table>
	   </div>
	   </div>
	   <div id="submit" align="center" style="margin-top:10px">
			<s:submit id="manage_fleet_scrap_submit" action="fleetScrapConfirmation" 
			         cssClass="buttonGeneric" value="%{getText('button.common.continue')}"/>&nbsp;
			<input class="buttonGeneric" type="button" value='<s:text name="label.cancel"/>'
			        onclick="closeCurrentTab()" />
		</div>
	</s:if>	
</s:form>


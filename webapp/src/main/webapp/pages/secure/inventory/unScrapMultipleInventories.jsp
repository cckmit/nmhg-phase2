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
<script type="text/javascript" src="scripts/CheckBoxListControl.js"></script>
<script>
 dojo.require("twms.widget.MultipleInventoryPicker");
 dojo.require("dojox.layout.ContentPane");   
 
  addOnContentPaneLoad(function() {  
       	if(dojo.byId("masterCheckbox_item")){		
        var multiCheckBoxControl = new CheckBoxListControl(dojo.byId("masterCheckbox_item"));
 		dojo.connect(dojo.byId("masterCheckbox_item"),"onclick",function(){
		    	if(dojo.byId("masterCheckbox_item").checked){		    				            
		            for (var i=0;i<numItems;i++){
		                dojo.byId("item_"+i).checked=true;		               
		            }
		    	} else {
		    		for (var j=0;j<numItems;j++){
		    			dojo.byId("item_"+j).checked=false;		    			
		    		}
		    	} 
		    });
		 }
    });   
</script>

<s:if test="scrapInventoryItems != null">		
		<div  style="clear:both" >
			<div class="section_header">
				<s:text name="title.fleetmanagement.inventoryUnScrap" />
			</div>		
			<table class="grid borderForTable" border="0" cellspacing="0" cellpadding="0"  width="100%"> 					
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
							<td align="center" style="padding:0;margin:0">
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
	   </div>	
	   <table  border="0" cellspacing="0" cellpadding="0"  width="100%">
	        <tr>
				<td>
				   <s:label value="%{getText('label.scrap.unScrapDate')}"/> :
				   <sd:datetimepicker name='unScrapDate' value='%{unScrapDate}' id='unScrapDate' />
			   </td>
			</tr>		     
		   <tr>
		    	<td>
	           		<label for="probably_cause"><s:text name="label.common.comments"/>:</label><br/>
	           		<t:textarea  rows="3" cols="60"
	               		name="scrapComments" />
	       		</td>
	       	</tr>	
	   </table>
	   <div id="submit" align="center">
			<s:submit id="manage_fleet_UnScrap_submit" action="fleetUnScrapConfirmation"
			         cssClass="buttonGeneric" value="%{getText('button.common.continue')}"/>&nbsp;
			<input class="buttonGeneric" type="button" value='<s:text name="label.cancel"/>'
			        onclick="closeCurrentTab()" />
		</div>
	</s:if>	
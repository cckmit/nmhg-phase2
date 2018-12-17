<%@page contentType="text/html"%>
<%@ taglib prefix="sd" uri="/struts-dojo-tags"%>
<%@page pageEncoding="UTF-8"%>
<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="t" uri="twms"%>
<%@taglib prefix="u" uri="/ui-ext"%>

<u:stylePicker fileName="warrantyForm.css"/>
<u:stylePicker fileName="form.css"/>
<u:stylePicker fileName="common.css"/>
<meta http-equiv="Context-Type" content="text/html; charset=ISO-8859-1" />
<%@ include file="/i18N_javascript_vars.jsp" %>
<script type="text/javascript">
     var selectionParams = {};
     dojo.require("twms.widget.MultipleInventoryPicker");   
     dojo.require("twms.widget.Dialog");  	
     function closeCurrentTab() {
            closeTab(getTabHavingId(getTabDetailsForIframe().tabId));
      }	
      
    /*  dojo.require("twms.widget.MultipleInventoryPicker");   
     dojo.require("twms.widget.Dialog");  		 */		   
</script> 

<s:form action="fleetUnScrapConfirmation"  id="manageFleetUnScrap" validate="true">
	<s:hidden name="isSearchForScrappedInventory" value="%{isSearchForScrappedInventory}"/>
	<s:if test="scrapInventoryItems != null">		
		<div  style="clear:both" >
		  <div class="policy_section_div">
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
							<td align="center">
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
	 </div> 
	 </div> 
	   <div id="submit" align="center">
			<s:submit id="manage_fleet_UnScrap_submit"
			         cssClass="buttonGeneric" value="%{getText('button.common.continue')}"/>&nbsp;
			   <input class="buttonGeneric" type="button" value='<s:text name="label.cancel"/>'
			        onclick="closeTab(getTabHavingId(getTabDetailsForIframe().tabId));" />    
			        
			<%--  <s:submit
			id="closeTab" value="Cancel" cssClass="buttonGeneric"  onclick="closeMyTab()"/> <script
			type="text/javascript">
			    dojo.addOnLoad(function() {
			        dojo.connect(dojo.byId("closeTab"), "onclick", function() {
			            closeMyTab();
			        });
			                 
			    });
			</script> --%>
		</div>
	</s:if>	
</s:form>


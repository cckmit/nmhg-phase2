<%@ page contentType="text/html;charset=UTF-8" language="java"%>

<%@ taglib prefix="sd" uri="/struts-dojo-tags"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="t" uri="twms"%>
<%@taglib prefix="u" uri="/ui-ext"%>

<style type="text/css">
.warrantyCoverageTrigger {
	background-image: url( image/writeWA.gif );
	width: 20px;
	height: 17px;
	background-repeat: no-repeat;
	margin-top: 5px;
	cursor: pointer;
}
</style>
<script type="text/javascript">
    dojo.require("dojox.layout.ContentPane");
    var slNos = [];
   
    </script>
<u:repeatTable id="warranty_units_table"
	cssClass="grid borderForTable" width="97%" cssStyle="border:0px;">
	<thead>
		<tr class="row_head">
			<th>
		
			<div dojoType="twms.widget.TitlePane"
				title="<s:text name="label.majorComponent.unitSerialNos"/>" id="unit_serial_nos"
				labelNodeClass="section_header" open="false" ></div>
		
			</th>
			<th width="9%"><u:repeatAdd id="unit_adder">
				<div class="repeat_add" />
			</u:repeatAdd></th>
		</tr>
	</thead>

	<u:repeatTemplate id="units_body" value="inventoryItemMappings">
		<tr index="#index">
			<td width="100%" height="100%">
			
         <table width="100%" cellpadding="0" cellspacing="0" border="0">
         <tr>
         <td width="100%">
         <div dojoType="dijit.layout.ContentPane" >
    	 <div dojoType="twms.widget.TitlePane"
         title="<s:text name="label.machineInfo"/>"
         id="equipment_information_#index" labelNodeClass="section_header" open="true">
         <table width="100%" cellspacing="0" cellpadding="0" border="0">
				<tbody>
					<tr>
						<td width="20%" class="labelStyle" nowrap="nowrap"><s:text
							name="label.common.serialNumber" />:</td>
						<td class="" width="37%">
							<s:hidden id="inventoryItemSize"
				value="%{inventoryItemMappings.size}" />				
						<s:hidden id="inventoryIemSN_#index" name="inventoryItemMappings[#index].inventoryItem" value="%{inventoryItemMappings[#index].id}"/> 						
						<sd:autocompleter id='serialNumber_#index' href='list_claim_sl_nos.action?selectedBusinessUnit=%{selectedBusinessUnit}' loadOnTextChange='true' loadMinimumCount='3' showDownArrow='false' indicator='indicator' value='%{inventoryItemMappings[#index].serialNumber}' notifyTopics='partInvItemChanged/setSerialNumber' />
					 <script type="text/javascript">
	           		dojo.addOnLoad(function(){				                   	
	            	dojo.connect(dijit.byId("serialNumber_"+#index),"onChange",function(){
	            	var serialNumber =dijit.byId("serialNumber_"+#index).getDisplayedValue();	            	            	      				
					var params={serialNumber:serialNumber};
					var url = "getDetailsForInventory.action?";						 
					twms.ajax.fireHtmlRequest(url, params, function(data) {						 
						var jsonString = eval(data);						 								     
						document.getElementById("product_"+#index).innerHTML = jsonString[0];
						document.getElementById("model_"+#index).innerHTML = jsonString[1];
						document.getElementById("shipment_"+#index).innerHTML = jsonString[2];
						document.getElementById("build_"+#index).innerHTML = jsonString[3];	
						document.getElementById("inventoryIemSN_"+#index).value = jsonString[4];														
						}); 
	            	});
	            });
	            </script>	     
					 </td>
						<td width="20%" class="labelStyle" nowrap="nowrap"><s:text
							name="label.common.model" />:</td>
						<td class="" width="35%"><span id="model_#index"> </span></td>

					</tr>
					
					<tr>
						<td width="20%" class="labelStyle" nowrap="nowrap"><s:text
							name="label.common.product" />:</td>
						<td class="" width="37%"><span id="product_#index"> </span></td>
						<td width="20%" class="labelStyle" nowrap="nowrap"><s:text
							name="label.common.shipmentDate" />:</td>
						<td class="" width="35%"><span id="shipment_#index"> </span></td>

					</tr>
					
					<tr>
						<td width="20%" class="labelStyle" nowrap="nowrap"><s:text
							name="label.common.buildDate" />:</td>
						<td class="" width="37%"><span id="build_#index"> </span></td>
						<td width="20%" class="labelStyle" nowrap="nowrap"><s:text
							name="label.deliveryDate" />:</td>
						<td class="" width="35%">
									<sd:datetimepicker name='inventoryItemMappings[#index].warrantyDeliveryDate' value='%{inventoryItemMappings[#index].warrantyDeliveryDate}' id='deliveryDate_#index' />
									<script type="text/javascript">
	           		dojo.addOnLoad(function(){		           	
	            	dojo.connect(dijit.byId("deliveryDate_"+#index),"onChange",function(){
	            		console.debug("Get policy"+#index);	            		
	            		getAllPolicies(#index);
	            	});
		            });

	           		</script>

								
								</td>

					</tr>
					<tr>
						<td width="20%" class="labelStyle" nowrap="nowrap"><s:text
							name="label.common.dateInstall" />:</td>
						<td class="" width="37%"><s:textfield
							value="" /></td>
						<td width="20%" class="labelStyle" nowrap="nowrap"><s:text
							name="label.common.hoursOnMachine" />:</td>
						<td class="" width="35%"><s:textfield
							name="inventoryItemMappings[#index].inventoryItem.hoursOnMachine" id="hoursOnMachine__#index" value="2" /></td>
							

					</tr>		
					<tr>
						<td class="" width="35%"><s:textfield
							name="inventoryItemMappings[#index].inventoryItem.hoursOnMachine" /></td>
					</tr>
					
					<tr>
						<td width="20%" class="labelStyle" nowrap="nowrap"><s:text
							name="label.common.oem" />:</td>
						<td class="" width="37%"><s:textfield
							value="" /></td>
						<td width="20%" class="labelStyle" nowrap="nowrap" colspan="2"></td>
							

					</tr>						
									
					
				</tbody>
			</table></div></div>
         </td>
         </tr>
				<tr>
					<td>
					<table width="100%">
						<div dojoType="twms.widget.TitlePane"
							title="<s:text name="label.common.warrantyCoverage"/>" id="coverage_#index"
							labelNodeClass="section_header" open="true">
						<div dojoType="dojox.layout.ContentPane" executeScripts="true"
							scriptSeparation="false" id="policyDetails_#index"
							style="height: auto;"></div>
						</div>
					</table>
					</td>
				</tr>

			</table>
			
			
			</td>
			<td><u:repeatDelete
						id="unit_deleter_#index">
						<div class="repeat_del" />
					</u:repeatDelete></td>
		</tr>
		
	</u:repeatTemplate>
</u:repeatTable>

<div id="policyFetchSection" style="display:none;">
    <div dojoType="twms.widget.Dialog" id="pop_up_for_policy_fetching"
         bgColor="white" bgOpacity="0.5" toggle="fade" toggleDuration="250"
         title="<s:text name="label.customReport.pleaseWait" />" style="width: 40%">
        <div class="dialogContent" dojoType="dijit.layout.LayoutContainer"
             style="background: #F3FBFE; width: 100%; height: 130px; border: 1px solid #EFEBF7">
            <div dojoType="dojox.layout.ContentPane">
                <div align="center"  style="padding-top: 20px">
                    <s:text name="label.warranty.waitMessageForPolicy" />
                </div>
            </div>
        </div>
    </div>
</div>
	
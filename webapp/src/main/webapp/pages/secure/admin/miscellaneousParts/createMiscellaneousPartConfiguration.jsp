<%@ page contentType="text/html"%>
<%@ taglib prefix="sd" uri="/struts-dojo-tags"%>
<%@ page pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="t" uri="twms"%>
<%@ taglib prefix="u" uri="/ui-ext"%>
<%
    response.setHeader("Pragma", "no-cache");
    response.addHeader("Cache-Control", "must-revalidate");
    response.addHeader("Cache-Control", "no-cache");
    response.addHeader("Cache-Control", "no-store");
    response.setDateHeader("Expires", 0);
%>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<s:head theme="twms" />
<title><s:text name="label.uom.mapping"/></title>
<u:stylePicker fileName="yui/reset.css" common="true" />
<u:stylePicker fileName="layout.css" common="true" />
<u:stylePicker fileName="common.css" />
<u:stylePicker fileName="form.css" />
<u:stylePicker fileName="adminPayment.css" />
<u:stylePicker fileName="base.css" />
<script type="text/javascript" src="scripts/RepeatTable.js"></script>
<script type="text/javascript" src="scripts/AdminToggle.js"></script>
<script type="text/javascript" src="scripts/adminAutocompleterValidation.js"></script>
    <script type="text/javascript">
  	  	dojo.require("dijit.layout.ContentPane");
      	dojo.require("dijit.layout.LayoutContainer"); 
      	
      	
      	function fillMiscPartDescription(index,data, type, request) {
		    if (type != "valuechanged") {
		        return;
		    }
		    twms.ajax.fireJavaScriptRequest("list_description_for_misc_part.action",{
		        number:data
		    },function(details) {
		    		if(dojo.byId("miscPartDescription_" + index)){
		           		dojo.byId("miscPartDescription_"+ index).innerHTML=details;
		           	}
		        }
		    );
	    }		 
      	
	</script>
	<style>
	.admin_entry_table tr th{
	background:#DCE9F7;
	height:30px;
	border:1px solid #EFEBF7;
	font-weight:700;
	color:#5577B4;
	text-align:center;
	}
	.admin_entry_table{
	margin:5px;
	}
	#mybody{
	border:1px solid #EFEBF7;
	}
	</style>
</head>
<u:body>
	<div dojoType="dijit.layout.LayoutContainer"
		style="width: 100%; height: 100%; background: white; overflow-y: auto;">
	<div dojoType="dijit.layout.ContentPane" layoutAlign="client">
	<s:form name="miscConfigForm" action="saveMiscellaneousPartConfiguration" theme="twms">
	<u:actionResults />			
	<s:hidden name="dealerGroupSelected" id="isDealerGroup" />
		<div class="policyRegn_section_div" style="width:99.5%">
		<div class="policy_section_heading">
		<s:text name="section.heading.Miscellaneoous.part.definition"/> 
		</div>
		
		<div class="mainTitle" style="margin:5px 0px 0px 0px;"><s:text name="section.heading.Miscellaneoous.part.criteria"/> </div>
		<div class="borderTable">&nbsp;</div>
		<table width="100%" border="0" cellspacing="0" cellpadding="0" class="policyRegn_table" style="margin-top:-10px;" >
				<tr>
					<td class="labelStyle"><s:text name="label.miscellaneousParts.configName"/> : </td>							
						<td><s:textfield name="miscItemCrit.configName" />
						</td>
				</tr>
				<tr>
					<td class="admin_data_table" width="20%" style="padding-bottom:10px;">
					<div id="dealerLabel"><s:text name="label.common.dealer" />:</div>
					<div id="dealerGroupLabel"><s:text
						name="label.common.dealerGroupLabel" />:</div>
					<div id="toggle" style="cursor: pointer;">
					<div id="toggleToDealerGroup" class="clickable"><s:text
						name="toggle.common.toDealerGroup" /></div>
					<div id="toggleToDealer" class="clickable"><s:text
						name="toggle.common.toDealer" /></div>
					</div>
					</td>
					<td width="80%" style="padding-bottom:10px;">
					<div id="dealer"><sd:autocompleter href='list_part_return_Dealers.action' id='dealerAutoComplete' name='miscItemCrit.serviceProvider' loadOnTextChange='true' loadMinimumCount='1' showDownArrow='false' value='%{miscItemCrit.serviceProvider.name}' autoComplete='false' /></div>
					<div id="dealerGroup"><sd:autocompleter href='list_part_return_DealerGroupsInDealerRates.action' id='dealerGroupAutoComplete' name='miscItemCrit.dealerGroup.name' loadOnTextChange='true' loadMinimumCount='1' showDownArrow='false' autoComplete='false' /></div>
					</td>
				</tr>
        		              
		</table>
		<div class="borderTable">&nbsp;</div>
		<div class="mainTitle" style="margin:0px 0px 0px 0px;"><s:text name="section.heading.Miscellaneoous.part.configuration"/> </div>
		
        <u:repeatTable id="myTable" cssClass="grid borderForTable" cellpadding="0" 
        cellspacing="0" width="98%" theme="simple" cssStyle="margin:5px;">
            <thead>
               <tr class="row_head">

					<th ><s:text name="label.miscPart.partNumber"/>  </th>
					<th ><s:text name="label.miscPart.partDescription"/>  </th>
					<th ><s:text name="label.miscPart.partPrice"/>  </th>
					<th ><s:text name="label.miscPart.partUom"/>  </th>
					<th ><s:text name="label.miscPart.partQuantity"/>  </th>
                    <th width="9%" >
                        <u:repeatAdd id="adder" theme="twms">
                            <img id="addPrice" src="image/addRow_new.gif" border="0" style="cursor: pointer; padding-right:4px; " title="<s:text name="label.miscellaneousParts.AddPart" />" />
                        </u:repeatAdd>
                    </th>
				</tr>
			</thead>	
			
            <u:repeatTemplate id="mybody" value="miscItemCrit.itemConfigs" index="myindex" theme="twms">
                <tr index="#myindex">								
						<td style="padding-left:5px; height:20px">							
				            	<sd:autocompleter id='miscItemCrit.itemConfigs_#myindex_miscellaneousItem_partNumber' href='list_miscellaneousItems.action' keyName='miscItemCrit.itemConfigs[#myindex].miscellaneousItem' name='miscItemCrit.itemConfigs[#myindex].miscellaneousItem' value='%{miscItemCrit.itemConfigs[#myindex].miscellaneousItem.partNumber}' loadOnTextChange='true' loadMinimumCount='0' showDownArrow='false' autoComplete='off' cssStyle='width:200px' cssClass='admin_selections showSuggestionsOnTop' value='%{miscItemCrit.itemConfigs[#myindex].miscellaneousItem.partNumber}' notifyTopics='/miscPart/changed/#myindex' />
	                    </td>								   		                 																				
						<td style="border:1px solid #EFEBF7;">
						         <span id="miscPartDescription_#myindex"/>
						</td>	
						
						<script type="text/javascript">            
				            dojo.addOnLoad(function() {			
							    dojo.subscribe("/miscPart/changed/#myindex", null, function(data, type, request) {
                	    			fillMiscPartDescription(#myindex, data, type,request);
				                }); 				                                          	
				            });
            		   </script>						
																		    		
						<td style="border:1px solid #EFEBF7;">
							<s:if test="miscItemCrit.itemConfigs[#myindex].miscItemRates != null">
								<s:iterator value="miscItemCrit.itemConfigs[#myindex].miscItemRates"
									status="iter">
		                        	<s:hidden name="miscItemCrit.itemConfigs[#myindex].miscItemRates[%{#iter.index}].currency" 
		                        				value="%{rate.breachEncapsulationOfCurrency().getCurrencyCode()}"/>
									<s:hidden name="miscItemCrit.itemConfigs[#myindex].miscItemRates[%{#iter.index}].currency" 
												value="0.00"/>
									<t:money id="rate_#myindex_%{#iter.index}" 
		                         	name="miscItemCrit.itemConfigs[#myindex].miscItemRates[%{#iter.index}].rate" 
		                         	value="%{rate}" defaultSymbol="$"></t:money><BR>
								</s:iterator><BR>		
							</s:if>
							<s:else>
								<s:iterator value="miscItems" status="iter">
		                        	<s:hidden name="miscItemCrit.itemConfigs[#myindex].miscItemRates[%{#iter.index}].currency" 
		                        				value="%{rate.breachEncapsulationOfCurrency().getCurrencyCode()}"/>
									<s:hidden name="miscItemCrit.itemConfigs[#myindex].miscItemRates[%{#iter.index}].currency" 
												value="0.00"/>
									<t:money id="rate_#myindex_%{#iter.index}" 
		                         	name="miscItemCrit.itemConfigs[#myindex].miscItemRates[%{#iter.index}].rate" 
		                         	value="%{rate}" defaultSymbol="$"></t:money><BR>
								</s:iterator><BR>
							</s:else>
						</td>
						<td style="border:1px solid #EFEBF7;">
								<s:select list="uomsList"  listKey="name" listValue="type" name="miscItemCrit.itemConfigs[#myindex].uom" 
								id="miscItemCrit.itemConfigs[#myindex]_uom" value ="%{miscItemCrit.itemConfigs[#myindex].uom.name}" theme="twms" cssStyle="width:100px;" />									
						</td>
						<td style="border:1px solid #EFEBF7;">
								<s:textfield name="miscItemCrit.itemConfigs[#myindex].tresholdQuantity"  
								id="miscItemCrit.itemConfigs[#myindex]_tresholdQuantity" cssStyle="width:100px;" />
						</td>
						<td style="border:1px solid #EFEBF7;">
	                        <u:repeatDelete id="deleter_#myindex" theme="twms">
	                            <img id="deletePrice" src="image/remove.gif" border="0" style="cursor: pointer; padding-right:4px;" title="<s:text name="label.miscellaneousParts.deletePart" />"/>
	                        </u:repeatDelete>
    	                </td>
				</tr>
			</u:repeatTemplate>	
		</u:repeatTable>
		</div>
		<div >
		<table width="100%" border="0" cellspacing="0" cellpadding="0" class="policyRegn_table">
				<tr>
			       <td align="center"><s:submit value="Submit" action="saveMiscellaneousPartConfiguration" cssClass="buttonGeneric"/> </td>					  			   
			    </tr>								  
		</table>
		</div>
	</s:form>
	<script type="text/javascript">
	    dojo.addOnLoad(function() {
	      <s:if test="dealerGroupSelected">
	          showDealerGroup();
	      </s:if>
	      <s:else>
	          showDealer();
	      </s:else>
	
	        dojo.connect(dojo.byId("toggleToDealer"), "onclick", function() {
	        	 dojo.byId("isDealerGroup").value = "false"; 
	            showDealer();
	        });
	        dojo.connect(dojo.byId("toggleToDealerGroup"), "onclick", function() {
	            dojo.byId("isDealerGroup").value = "true"; 
	            showDealerGroup();
	        });
	    });
	</script>				
	</div>
	</div>
</u:body>
</html>
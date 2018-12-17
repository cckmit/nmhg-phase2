<%@ page contentType="text/html"%>
<%@ taglib prefix="sd" uri="/struts-dojo-tags"%>
<%@ page pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="t" uri="twms"%>
<%@ taglib prefix="u" uri="/ui-ext"%>
<%@ taglib prefix="authz" uri="authz" %>

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
</head>
<u:body>
	<div dojoType="dijit.layout.LayoutContainer"
		style="width: 100%; height: 100%;overflow-y: auto;">
	<div dojoType="dijit.layout.ContentPane" layoutAlign="client" >
	<s:form name="miscConfigForm" action="updateMiscellaneousPartConfiguration" id="baseForm">
	<u:actionResults />				
		<div class="policy_section_div">
		<div class="policy_section_heading"><s:text name="section.heading.Miscellaneoous.part.definition"/> </div>
		
		<div class="mainTitle" style="margin-top:5px;"><s:text name="section.heading.Miscellaneoous.part.criteria"/> </div>
		<div class="borderTable">&nbsp;</div>
		<table width="100%" border="0" cellspacing="0" cellpadding="0" class="grid" style="margin-top:-10px;">
				<tr>
					<td nowrap="nowrap" width="20%" class="labelStyle"><s:text name="label.miscellaneousParts.configName"/> : </td>							
					<td><s:property value="miscItemCrit.configName" />
						<s:hidden name="miscItemCrit" value="%{miscItemCrit.id}"/>
					</td>
				</tr>
				<tr>
					<td class="labelStyle" nowrap="nowrap"><s:text name="label.miscellaneousParts.dealerOrDealerGroup"/> : </td>
					<s:if test="!(miscItemCrit.serviceProvider.name == null)" >							
					<td><s:property value="miscItemCrit.serviceProvider.name" />						
					</td>
					</s:if>
					<s:else>
					<td><s:property value="miscItemCrit.dealerGroup.name" />
					</td>
					</s:else>
			    </tr>
        		              
		</table>
			<br/>
		
		<div class="mainTitle"><s:text name="section.heading.Miscellaneoous.part.configuration"/> </div>
		
        <u:repeatTable id="myTable"  cssClass="grid bordeForTable" cellpadding="0" cssStyle="margin:4px;" cellspacing="0" width="100%" theme="simple">
            <thead>
               <tr class="title">

					<th class="colHeader"><s:text name="label.miscPart.partNumber"/>  </th>
					<th class="colHeader"><s:text name="label.miscPart.partDescription"/>  </th>
					<th class="colHeader"><s:text name="label.miscPart.partPrice"/>  </th>
					<th class="colHeader"><s:text name="label.miscPart.partUom"/>  </th>
					<th class="colHeader"><s:text name="label.miscPart.partQuantity"/>  </th>
                    <th width="9%" class="colHeader">
                        <u:repeatAdd id="adder" theme="twms">
                            <img id="addPrice" src="image/addRow_new.gif" border="0" style="cursor: pointer; padding-right:4px; " title="<s:text name="label.miscellaneousParts.AddPart" />" />
                        </u:repeatAdd>
                    </th>
				</tr>
			</thead>	
			
            <u:repeatTemplate id="mybody" value="miscItemCrit.itemConfigs" index="myindex" theme="twms">
                <tr index="#myindex">								
						<td style="padding-left:5px; height:20px;border:1px solid #EFEBF7;">													
				            	<sd:autocompleter id='miscItemCrit.itemConfigs_#myindex_miscellaneousItem_partNumber' href='list_miscellaneousItems.action' keyName='miscItemCrit.itemConfigs[#myindex].miscellaneousItem' name='miscItemCrit.itemConfigs[#myindex].miscellaneousItem' value='%{miscItemCrit.itemConfigs[#myindex].miscellaneousItem.partNumber}' disabled='miscItemCrit.itemConfigs[#myindex].id > 0' loadOnTextChange='true' loadMinimumCount='0' showDownArrow='false' autoComplete='off' cssStyle='width: 80%' cssClass='admin_selections showSuggestionsOnTop' value='%{miscItemCrit.itemConfigs[#myindex].miscellaneousItem.partNumber}' notifyTopics='/miscPart/changed/#myindex' />
	                    </td>
	                    <s:if test="miscItemCrit.itemConfigs[#myindex].id > 0">
		                    <s:hidden name="miscItemCrit.itemConfigs[#myindex].miscellaneousItem" value="%{miscItemCrit.itemConfigs[#myindex].miscellaneousItem.partNumber}"/>
	                    </s:if>								   		                 																				
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
									<t:money id="rate_#myindex_%{#iter.index}" 
		                         	name="miscItemCrit.itemConfigs[#myindex].miscItemRates[%{#iter.index}].rate" 
		                         	value="%{rate}" defaultSymbol="$"></t:money><BR>
								</s:iterator><BR>		
							</s:if>
							<s:else>
								<s:iterator value="miscItems" status="iter">
									<t:money id="rate_#myindex_%{#iter.index}" 
		                         	name="miscItemCrit.itemConfigs[#myindex].miscItemRates[%{#iter.index}].rate" 
		                         	value="%{rate}" defaultSymbol="$"></t:money><BR>
								</s:iterator><BR>
							</s:else>
						</td>
						<td style="border:1px solid #EFEBF7;">
								<s:select list="uomsList" listKey="name" listValue="type" theme="twms" name="miscItemCrit.itemConfigs[#myindex].uom" id="miscItemCrit.itemConfigs[#myindex]_uom"  value ="%{miscItemCrit.itemConfigs[#myindex].uom.name}"	/>
						</td>
						<td style="border:1px solid #EFEBF7;">
								<s:textfield name="miscItemCrit.itemConfigs[#myindex].tresholdQuantity"  id="miscItemCrit.itemConfigs[#myindex]_tresholdQuantity"/>
						</td>

						<td style="border:1px solid #EFEBF7;">	    
							 <u:repeatDelete id="deleter_#myindex" theme="twms">	                      		
	                             <img id="deletePrice" src="image/remove.gif" border="0" style="cursor: pointer;padding-right:4px;" title="<s:text name="label.miscellaneousParts.deletePart" />"/>	                            
	                        </u:repeatDelete>                   
	                        
	                        <script type="text/javascript"> 	                        
		                        <s:if test="miscItemCrit.itemConfigs[#myindex].id > 0  ">
		                        	dojo.byId("deleter_"+ #myindex).innerHTML = "";
		                        </s:if>
	                        </script>
	                        
	                       

    	                </td>
				</tr>
			</u:repeatTemplate>	
		</u:repeatTable>
	</div>
		
		<s:hidden name="deActivateMiscPartConfig" value="%{deActivateMiscPartConfig}" />
		<div align="center" class="spacingAtTop">
		    <input id="cancel_btn" class="buttonGeneric" type="button" value="<s:text name='button.common.cancel'/>" onclick="javascript:closeTab(getTabHavingLabel(getMyTabLabel()));" />		
			<s:submit value="%{getText('button.common.save')}" action="updateMiscellaneousPartConfiguration" cssClass="buttonGeneric"/> 	
			  <s:if test="deActivateMiscPartConfig">
                  <s:submit value="%{getText('button.common.deActivate')}" action="activateMiscellaneousPartConfiguration" cssClass="buttonGeneric"/> 
              </s:if>
              <s:else>
                  <s:submit value="%{getText('button.common.activate')}" action="activateMiscellaneousPartConfiguration" cssClass="buttonGeneric"/>
              </s:else>			
	    </div>		
	</s:form>
					
	</div>
	</div>
</u:body>
<authz:ifPermitted resource="warrantyAdminConfigureMiscellaneousPartsReadOnlyView">
	<script type="text/javascript">
	    dojo.addOnLoad(function() {
	        for ( var i = 0; i < dojo.query("input, button, textarea, select, text", dojo.byId('baseForm')).length; i++) {
	            dojo.query("input, button, textarea, select, text", dojo.byId('baseForm'))[i].disabled=true;
	        }
	    });
	</script>
</authz:ifPermitted>
</html>

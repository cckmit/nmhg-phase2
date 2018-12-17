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
<div style="width:100%; overflow:auto;">
<div id="partsDiv" style="width:100%; overflow-x:auto;">
		<table cellspacing="0" cellpadding="0" class="grid borderForTable">
	      <tr>
	        <th class="colHeader"></th>
		    <th  valign="middle" class="colHeader"><s:text name="columnTitle.partReturnConfiguration.part.serialNumber" /></th>
			<th  valign="middle" class="colHeader"><s:text name="columnTitle.partReturnConfiguration.partNumber" /></th>
		    <th  valign="middle" class="colHeader"><s:text name="columnTitle.common.description" /></th>
			<s:if test="isTaskDuePartsReceipt()  && isPartOffFeatureEnabled() && !partReturnsInitiatedBySupplier">
			    <th  valign="middle" class="colHeader"><s:text name="columnTitle.common.partOff.serialNumber" /></th>
			    <th  valign="middle" class="colHeader"><s:text name="columnTitle.common.partOff.partNumber" /></th>
		    <th  valign="middle" class="colHeader"><s:text name="columnTitle.common.partOff.description" /></th>
		    </s:if>
		    <s:elseif test="partReturnsInitiatedBySupplier">
		    	<th  valign="middle" class="colHeader"><s:text name="columnTitle.common.applicableContracts" /></th>
		    </s:elseif>
		    <th  valign="middle" class="colHeader"><s:text name="columnTitle.common.returnlocation" /></th>
		    <s:if test="isTaskDuePartsReceipt()">
		    <th  valign="middle" class="colHeader"><s:text name="columnTitle.common.receiptStatus" /></th>
			<authz:ifUserInRole roles="inspector"> 
			   	 <th  valign="middle" class="colHeader"><s:text name="label.partReturn.isInspect"/></th> 
			</authz:ifUserInRole>   
			</s:if>
		    <authz:ifUserInRole roles="inspector"> 
		    <th valign="middle" class="colHeader">
	        <table width="100%" border="0">
	        <tr>
	        	<th valign="middle" style="color:#5577B4; width:48%"><s:text name="columnTitle.partReturnConfiguration.action" /></th>
          		<th valign="middle" style="color:#5577B4;"><s:text name="label.partReturn.settlementCode" /></th>
          	</tr>
          	</table>
          	</th>
          	</authz:ifUserInRole>
	    </tr>
		 <s:iterator value="partReplacedBeans" status="partIterator" id="oEMPartReplacedBean">
 			<s:iterator value="partReturnTasks" status ="taskIterator">
		 		<tr class="
				     	 <s:if test="oemPartReplaced.activePartReturn.dueDays <= 5">tableDataYellowRowText</s:if>
				     	 <s:else>tableDataWhiteText</s:else>
			     		 ">
				<td>
				<input type="hidden"  name="partReplacedBeans[<s:property value="%{#partsCounter}"/>].oemPartReplaced" 
                value="<s:property value="partReplacedBeans[#partIterator.index].oemPartReplaced.id"/>"/>
				 <input type="hidden"
				 		     name="partReplacedBeans[<s:property value="%{#partsCounter}"/>].partReturnTasks[<s:property value="%{#taskIterator.index}"/>].task" 
						     value="<s:property value="task.id"/>"
								/>
				<s:checkbox 
					name="partReplacedBeans[%{#partsCounter}].partReturnTasks[%{#taskIterator.index}].selected"
	 		     value="!selected" 
	 		     id="%{#claimIterator.index}_%{#partIterator.index}_%{#taskIterator.index}"/> 
				</td>
				<td width="12%" style="padding-left:3px;"><s:property value="oemPartReplaced.itemReference.referredInventoryItem.serialNumber"/> </td> 
			  	<td width="12%" style="padding-left:3px;" ><s:property value="%{getOEMPartCrossRefForDisplay(oemPartReplaced.itemReference.referredItem, oemPartReplaced.oemDealerPartReplaced, true, claim.forDealer)}"/>  </td>
			  	<td width="10%" style="padding-left:3px;"><s:property value="%{getOEMPartCrossRefForDisplay(oemPartReplaced.itemReference.referredItem, oemPartReplaced.oemDealerPartReplaced, false, claim.forDealer)}"/> </td>
			  	<s:if test="isTaskDuePartsReceipt()">
			  		<s:if test=" isPartOffFeatureEnabled() && !#oEMPartReplacedBean.oemPartReplaced.partReturnInitiatedBySupplier">
				  	 	<td width> 				  	 	
				  	 	<s:if test="claimWithPartBeans[#claimIterator.index].claim.itemReference.referredInventoryItem != null && (claim.type.type == 'Machine' || (claim.type.type == 'Parts' && claim.partInstalled && claim.partItemReference.isSerialized()))">
				  	 	  <s:select list="claimWithPartBeans[#claimIterator.index].claim.itemReference.referredInventoryItem.composedOf" 
					  	 	 listValue="part.serialNumber" listKey="part.id"
					  	 	name='partReplacedBeans[%{#partsCounter}].partReturnTasks[%{#taskIterator.index}].partOffSerialNumber' size='1'
					  	 	id="partOffSerialNumber_%{#partsCounter}_%{#taskIterator.index}"
					  	 	 headerValue="" headerKey=""
					  	 	 cssStyle="width: 100px" onchange="updatePartOffNumber(this.value,'%{#partsCounter}'+'_'+'%{#taskIterator.index}')" 
					  	 	 disabled="%{isDisabledBasedOnPartsClaim(claim)}" > 
					  	 	 <script type="text/javascript">
				           		dojo.addOnLoad(function() {
						           	var partsCounterIndex = '<s:property value="#partsCounter"/>';
						        	var taskIteratorIndex = '<s:property value="#taskIterator.index"/>';
						        	var selectValue = '<s:property value="oemPartReplaced.itemReference.referredInventoryItem.id"/>';
						        	var index = partsCounterIndex+"_"+taskIteratorIndex;
						        	var selectId = "partOffSerialNumber_"+index;
						        	dojo.byId(selectId).value = selectValue;
						        	if(selectValue!="") {
										dojo.byId("partOffPartNumber_"+index).disabled = true;
									}
					            });
	           				</script>
					  	 	</s:select>
					  	 </s:if>
					  	 <s:elseif test="claim.type.type == 'Parts' && claim.partInstalled && claim.partItemReference.isSerialized() && !claim.itemReference.isSerialized()">
					  	 <s:select list="claimWithPartBeans[#claimIterator.index].claim.partItemReference.referredInventoryItem" 
					  	 	 listValue="serialNumber" listKey="id"
					  	 	name='partReplacedBeans[%{#partsCounter}].partReturnTasks[%{#taskIterator.index}].partOffSerialNumber' size='1'
					  	 	id="partOffSerialNumber_%{#partsCounter}_%{#taskIterator.index}"
					  	 	 headerValue="" headerKey=""
					  	 	 cssStyle="width: 100px" onchange="updatePartOffNumber(this.value,'%{#partsCounter}'+'_'+'%{#taskIterator.index}')">
					  	 	   <script type="text/javascript">
				           		dojo.addOnLoad(function() {
				           			var partsCounterIndex = '<s:property value="#partsCounter"/>';
						        	var taskIteratorIndex = '<s:property value="#taskIterator.index"/>';
						        	var selectValue = '<s:property value="oemPartReplaced.itemReference.referredInventoryItem.id"/>';
						        	var index = partsCounterIndex+"_"+taskIteratorIndex;
						        	var selectId = "partOffSerialNumber_"+index;
						        	dojo.byId(selectId).value = selectValue;
						        	if(selectValue!="") {
										dojo.byId("partOffPartNumber_"+index).disabled = true;
									}
					            });
	           				</script>
					  	 	</s:select>
					  	 </s:elseif>
					  	 <s:else>
					  	    &nbsp;  &nbsp;-
					  	 </s:else>
				  	   </td>
				  	   <td>
				  	   <s:if test="isDisabledBasedOnPartsClaim(claim)">
				  	   <sd:autocompleter id='partOffPartNumber_%{#partsCounter}_%{#taskIterator.index}' cssStyle='width:75px' href='list_oem_part_item_with_label_id.action?selectedBusinessUnit=%{claim.getBusinessUnitInfo()}' name='partReplacedBeans[%{#partsCounter}].partReturnTasks[%{#taskIterator.index}].partOffPartNumber' loadOnTextChange='true' showDownArrow='false' listenTopics='/partOff/initial/%{#partsCounter}_%{#taskIterator.index}' notifyTopics='/partOff/itemno/changed/%{#partsCounter}_%{#taskIterator.index}' disabled='true' />
		               </s:if>
		               <s:else>
		                 <sd:autocompleter id='partOffPartNumber_%{#partsCounter}_%{#taskIterator.index}' cssStyle='width:75px' href='list_oem_part_item_with_label_id.action?selectedBusinessUnit=%{claim.getBusinessUnitInfo()}' name='partReplacedBeans[%{#partsCounter}].partReturnTasks[%{#taskIterator.index}].partOffPartNumber' loadOnTextChange='true' showDownArrow='false' listenTopics='/partOff/initial/%{#partsCounter}_%{#taskIterator.index}' notifyTopics='/partOff/itemno/changed/%{#partsCounter}_%{#taskIterator.index}' />
		               
		               </s:else>
				  		</td>
				  		   <script type="text/javascript">
		                    dojo.addOnLoad(function(){
		                        dojo.publish("/partOff/initial/"+'<s:property value="%{#partsCounter}"/>' + "_" + '<s:property value="%{#taskIterator.index}"/>', [{
		                            addItem: {
		                                key: '<s:property value="%{oemPartReplaced.itemReference.referredItem.id}"/>',
		                                label: '<s:property value="%{oemPartReplaced.itemReference.referredItem.alternateNumber}"/>'
		                            }
		                        }]);
		                    });
	          			  </script>
				  		<td>
					  		<span id="partOff_description_<s:property value='%{#partsCounter}'/>_<s:property value='%{#taskIterator.index}'/>">
					  		</span>
				  		</td>
				  		<script type="text/javascript">
							dojo.addOnLoad(function() {
							    var index = '<s:property value="%{#partsCounter}"/>' + "_" + '<s:property value="%{#taskIterator.index}"/>';
								setPartOffDetails(index);
								  });
							  
							dojo.subscribe("/partOff/itemno/changed/"+'<s:property value="%{#partsCounter}"/>' + "_" + '<s:property value="%{#taskIterator.index}"/>',
				                     null, function(data, type, request) {
			  				   var partOffindex ='<s:property value="%{#partsCounter}"/>' + "_" + '<s:property value="%{#taskIterator.index}"/>';
			  				   fillPartOffDescription(partOffindex, data, type);
			                     });  
		
							  function   fillPartOffDescription(index,number,type){
							    if (type != "valuechanged") {
							        return;
							    }
							    twms.ajax.fireJsonRequest("get_partOff_details.action",{
							            number: number
							        }, function(details) {
								        dojo.byId("partOff_description_"+index).innerHTML=details[0];
							           	}
							        );
							  };
					
							 function setPartOffDetails(index){
					      		 fillPartOffDescription(index,dojo.byId("partOffPartNumber_"+index).value,"valuechanged");
							  };
					
							 function updatePartOffNumber(data,index){
								 var serialNumberCurrent=dojo.byId("partOffSerialNumber_"+index).value;
								 if(serialNumberCurrent==""){
									dojo.byId("partOffPartNumber_"+index).disabled=false;  
								 }else{
									dojo.byId("partOffPartNumber_"+index).disabled=true;  
								 }
								 twms.ajax.fireJsonRequest("get_partOff_details.action",{
                                        serialNumberId: data
                                        }, function(details) {
                                            dijit.byId("partOffPartNumber_"+index)._resetUsingLabelAndValue(details[0],details[1],true);
                                        }
                                    );
                             };
					</script>
			  		</s:if>
			  		<s:elseif test="#oEMPartReplacedBean.oemPartReplaced.partReturnInitiatedBySupplier">
						<td>
							<s:select
								list="#oEMPartReplacedBean.listOfApplicableContracts"
								listKey="id" listValue="name" headerKey="-1"
								headerValue="%{getText('label.common.selectHeader')}"
								id="applicableContracts_%{#taskIterator.index}"
								cssStyle="width:200px;" onchange="updateApplicableContractOptions()"
								name="partReplacedBeans[%{#partsCounter}].partReturnTasks[%{#taskIterator.index}].selectedContract">
							</s:select>
						</td>
						<script type="text/javascript">
							function updateApplicableContractOptions(){
								var contractsCount = new Object();
								<s:iterator value="applicableContracts">
									contractsCount['<s:property value="key.id"/>'] = '<s:property value="value"/>';
								</s:iterator>
								dojo.query("select[id^=applicableContracts_]",dojo.byId("partsDiv")).forEach(function(aSelectBox){
									if(aSelectBox.value != "-1"){
										var num = contractsCount[aSelectBox.value] -1;
										contractsCount[aSelectBox.value] = num;
										if(num == 0){
											dojo.query("select[id^=applicableContracts_]",dojo.byId("partsDiv")).forEach(function(otherSelectBox){
												if(otherSelectBox.value != aSelectBox.value){
													for(i=otherSelectBox.options.length-1;i>=0;i--)
													{
														if(otherSelectBox.options[i].value == aSelectBox.value)
															otherSelectBox.remove(i);
													}
												}
											})
											
										}
										
									}
								})
								
							}
						</script>
					</s:elseif>
		    	    <td>
				            <s:select list="%{getWareHouses(shipment.destination.code)}" 
				            	name='partReplacedBeans[%{#partsCounter}].partReturnTasks[%{#taskIterator.index}].warehouseLocation' size='1'
				            	 headerKey="" headerValue="%{getText('dropdown.partReturnConfiguration.location')}">
						    </s:select>
					   
				   </td>
				   <td><s:select name="partReplacedBeans[%{#partsCounter}].partReturnTasks[%{#taskIterator.index}].receiptStatus" list="receiptStatusList"
			                  required="true"   id ="receiptStatus_%{#partsCounter}_%{#taskIterator.index}" 
			                  cssStyle="width: 130px" value="%{receiptStatus.name()}"/>
		  		   </td>
				   <authz:ifUserInRole roles="inspector"> 
					    <td align="center"><s:checkbox id="inspected_%{#partsCounter}_%{#taskIterator.index}"  
					   			name="partReplacedBeans[%{#partsCounter}].partReturnTasks[%{#taskIterator.index}].toBeInspected"
		      						value="toBeInspected" >
		      						</s:checkbox>
		      			</td>
		      			<td>
				      			<div id="includeInspectionAction">
				      			<table>
				      			<jsp:include flush="true" page="tables/inspectionActionInclude_dropdown_form.jsp"/>
					        	</table>
					        	</div>
			       		</td>
						<script type="text/javascript">
		      			   dojo.addOnLoad(function() {
				      		    var index = '<s:property value="%{#partsCounter}"/>' + "_" + '<s:property value="%{#taskIterator.index}"/>';
			      		   		setInspectionValues(index);
			                    dojo.connect(dojo.byId("inspected_"+index), "onclick",function(evt){
	                                 setInspectionValues(index);
	                                 });
		      			   });
				       		
        				</script>
					</authz:ifUserInRole>
		  		</s:if>
		  		<s:else>
		  		    
	    	   		<td><s:property value = "getPartReturn().warehouseLocation"/></td>
	    	   		<td>
		      			<div id="includeInspectionAction">
		      			<table border="0" cellpadding="0" cellspacing="0" class="NoborderForTable" >
		      			<jsp:include flush="true" page="tables/inspectionActionInclude_dropdown_form.jsp"/>
			        	</table>
			        	</div>
	       		    </td>
	    	   		 
		  		</s:else>
			</tr>
		 	</s:iterator>
     	 <input type="hidden"  size="3" name="partReplacedBeans[<s:property value="%{#partsCounter}"/>].partReplacedId"  
      				value="<s:property value="oemPartReplaced.id"/>"/>
      	<input type="hidden" 
	 		     name="partReplacedBeans[<s:property value="%{#partsCounter}"/>].claim" 
			     value="<s:property value="claim.id"/>"/>
   		 <s:set name="partsCounter" value="%{#partsCounter + 1}"/>
  </s:iterator>
</table>
<script type="text/javascript">
	  function setInspectionValues(index)
	  {  
		  var inspectCheck=dojo.byId("inspected_"+index);
		  if(!inspectCheck.checked)
		  {
			  dojo.byId("inspectionStatus_"+index).setAttribute("disabled","disabled");
			  dijit.byId("acceptReasons_" + index).setDisabled(true);
			  dijit.byId("failureReasons_"+index).setDisabled(true);
		  }
		  else{
			  dojo.byId("inspectionStatus_"+index).removeAttribute("disabled");
			  dijit.byId("acceptReasons_"+index).setDisabled(false);
			  dijit.byId("failureReasons_"+index).setDisabled(false);
		  }
	  }
  </script>
  <div style="height:12px; height:30px\9;"></div>
</div>
</div>


<%@taglib prefix="u" uri="/ui-ext"%>
<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="authz" uri="authz"%>
<script type="text/javascript" src="scripts/CheckBoxListControl.js"></script>

<div class="mainTitle"
	style="border-bottom: 1px solid #E6EAEF; margin-bottom: 10px; padding-top: 10px; width: 99%;">
	<s:text name="title.partReturnConfiguration.partDetails" />
</div>

<div style="width: 100%; overflow: auto;">
	<table cellspacing="0" cellpadding="0" class="grid borderForTable">
		<tr>
			<th class="colHeader">
			<input id="selectAll_<s:property value="recoveryClaim.id" />" type="checkbox"
                    checked="checked"
                    value="checkbox" style="border:none"/>
              <script>
                  var multiCheckBox = dojo.byId("selectAll_<s:property value="recoveryClaim.id" />");
                  var multiCheckBoxControl = new CheckBoxListControl(multiCheckBox);

                  // this var is defined in parent jsp.
                 // __masterCheckBoxControls.push(multiCheckBoxControl);
              </script>
			</th>
			<th valign="middle" class="colHeader"><s:text
					name="columnTitle.partReturnConfiguration.partNumber" /></th>
			<th valign="middle" class="colHeader"><s:text
					name="columnTitle.common.description" /></th>
			<th valign="middle" class="colHeader" align="right"><s:text
					name="label.partReturn.shipped" /></th>
		    <th valign="middle" class="colHeader" align="right"><s:text
            		name="label.partReturn.cannotShip" /></th>
			<th valign="middle" class="colHeader" align="right"><s:text
					name="label.partReturn.received" /></th>
			<th valign="middle" class="colHeader" align="right"><s:text
					name="label.partReturn.notReceived" /></th>
			<th valign="middle" class="colHeader" align="right"><s:text
					name="label.partReturn.total" /></th>
			<th valign="middle" class="colHeader" align="right"><s:text
					name="label.partReturn.receive" /></th>
			<th valign="middle" class="colHeader" align="right"><s:text
					name="label.partReturn.didNotReceive" /></th>
			<th valign="middle" class="colHeader"><s:text
					name="label.partReturnConfiguration.location" /></th>
			<authz:ifUserInRole roles="inspector">
				<th valign="middle" class="colHeader"><s:text
						name="label.partReturn.isInspect" /></th>
				<th valign="middle" class="colHeader">
					<table>
						<tr>
							<th style="color: #5577B4"><s:text
									name="columnTitle.partReturnConfiguration.action" /></th>
							<th style="color: #5577B4"><s:text
									name="label.common.reason" /></th>
						</tr>
					</table>
				</th>
			</authz:ifUserInRole>
		</tr>
		 <s:hidden name="recoveryClaim.id" value="%{recoveryClaim.id}" />
		 <s:hidden name="recoveryClaim" value="%{recoveryClaim}" />
         <s:hidden name="id" value="%{recoveryClaim.id}" />
         <s:hidden name="transitionTaken"
                    			value='%{@tavant.twms.jbpm.WorkflowConstants@PART_RECEIVED_FROM_SUPPLIER}' />
		<s:iterator value="recoverablePartsBeans" status="partIterator">
		<s:set id="oEMPartReplaced" value="recoverablePart.oemPart" name="oEMPartReplaced"/>
        <s:iterator value="recoveryPartTaskBeans" status ="taskIterator">
           <input type="hidden"
              name="uiRecoverablePartsBeans[<s:property value="%{#partIterator.index}"/>].recoveryPartTaskBeans[<s:property value="%{#taskIterator.index}"/>].task"
              value="<s:property value="task.id"/>"/>
        </s:iterator>
         <input type="hidden" name="uiRecoverablePartsBeans[<s:property value="%{#partIterator.index}"/>].recoverablePart"
                      				value="<s:property value="recoverablePartsBeans[#partIterator.index].recoverablePart.id"/>"/>
		<tr class="tableDataWhiteText">
			<td align="center"><s:checkbox
					name="uiRecoverablePartsBeans[%{partsCounter}].selected" value="selected"  id="%{#partIterator.index}" />

			<script>
                 var selectElementId = "<s:property value="%{#partIterator.index}" />";
                          multiCheckBoxControl.addListElement(dojo.byId(selectElementId));
            </script>
		    </td>
			<td><s:property
					value="recoverablePart.oemPart.itemReference.unserializedItem.alternateNumber" /></td>
			<td><s:property
					value="recoverablePart.oemPart.itemReference.unserializedItem.description" />
			</td>
			<td><s:property value="shipped" /></td>
			<td><s:property value="cannotShip" /></td>
			<td><s:property value="received" /></td>
			<td><s:property value="notReceived" /></td>
			<td><s:property value="totalNoOfParts" /></td>
			<input type = "hidden" name="uiRecoverablePartsBeans[<s:property value="%{#partsCounter}"/>].shipped"
					value="<s:property value="shipped"/>" />
			<td><input type="text" size="3"
				name="uiRecoverablePartsBeans[<s:property value="%{#partsCounter}"/>].receive"
				value="<s:property value="receive"/>" /></td>
			<td><input type="text" size="3"
				name="uiRecoverablePartsBeans[<s:property value="%{#partsCounter}"/>].didNotReceive"
				value="<s:property value="didNotReceive"/>" /></td>
			<td><s:select list="%{getWareHouses(shipment.destination.code)}"
					name='uiRecoverablePartsBeans[%{#partsCounter}].warehouseLocation'
					size='1' headerKey=""
					headerValue="%{getText('dropdown.partReturnConfiguration.location')}">
				</s:select></td>
			<authz:ifUserInRole roles="inspector">
				<td align="center"><s:checkbox id="inspected_%{#partsCounter}"
						name="uiRecoverablePartsBeans[%{#partsCounter}].toBeInspected"
						value="toBeInspected">
					</s:checkbox></td>
				<td>
					<div id="includeInspectionAction">
						<table>
							<jsp:include flush="true"
								page="inspectionAction.jsp" />
						</table>
					</div>
				</td>
				<script type="text/javascript">
			      			   dojo.addOnLoad(function() {
			      			   setInspectionValue(<s:property value="%{#partsCounter}"/>);
			        		   dojo.connect(
				        		   dojo.byId("inspected_" + <s:property value="%{#partsCounter}"/>), "onchange",
				        		   function(evt)
				        		   {
				        			 setInspectionValue(<s:property value="%{#partsCounter}"/>);
				        		   }              
			        			);	      
			            		});
	        			</script>
			</authz:ifUserInRole>
			<s:set name="partsCounter" value="%{#partsCounter + 1}" />
		</tr>
		</s:iterator>
	</table>
</div>
<script type = "text/javascript">
function setInspectionValue(index)
{
	  var inspectCheck=dojo.byId("inspected_"+index);
	  if(!inspectCheck.checked)
	  {
		  dojo.byId("accept_"+index).setAttribute("disabled","disabled");
		  dojo.byId("reject_"+index).setAttribute("disabled","disabled");
		  dijit.byId("supplierPartAcceptanceReason_"+index).setDisabled(true);
		  dijit.byId("supplierPartRejectionReason_"+index).setDisabled(true);
		  dojo.byId("returnToDealer_"+index).setAttribute("disabled","disabled");
		  dojo.byId("scrap_"+index).setAttribute("disabled","disabled");
		  dojo.byId("returnToSupplier_"+index).setAttribute("disabled","disabled");
	  }
	  else{
		  dojo.byId("accept_"+index).removeAttribute("disabled");
		  dojo.byId("reject_"+index).removeAttribute("disabled");
		  dijit.byId("supplierPartAcceptanceReason_"+index).setDisabled(false);
		  dijit.byId("supplierPartRejectionReason_"+index).setDisabled(false);
		  dojo.byId("returnToDealer_"+index).removeAttribute("disabled");
		  dojo.byId("scrap_"+index).removeAttribute("disabled");
		  dojo.byId("returnToSupplier_"+index).removeAttribute("disabled");
	  }
}
</script>

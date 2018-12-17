<%@taglib prefix="s" uri="/struts-tags"%>
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
                    checked="checked" value="checkbox" style="border:none"/>
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
					name="label.partReturn.toBeShipped" /></th>
			<th valign="middle" class="colHeader" align="right"><s:text
					name="label.partReturn.shipped" /></th>
		    <th valign="middle" class="colHeader" align="right"><s:text
            		name="label.partReturn.cannotShip" /></th>
			<th valign="middle" class="colHeader" align="right"><s:text
					name="label.partReturn.received" /></th>
			<th valign="middle" class="colHeader" align="right"><s:text
					name="label.partReturn.inspected" /></th>
			<th valign="middle" class="colHeader" align="right"><s:text
					name="label.partReturn.total" /></th>
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
		</tr>
		 <s:hidden name="recoveryClaim.id" value="%{recoveryClaim.id}" />
         <s:hidden name="id" value="%{recoveryClaim.id}" />
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
			<td><s:property value="toBeShipped" /></td>
			<td><s:property value="shipped" /></td>
			<td><s:property value="cannotShip" /></td>
			<td><s:property value="received" /></td>
			<td><s:property value="inspected" /></td>
			<td><s:property value="totalNoOfParts" /></td>
			<input type = "hidden" name="uiRecoverablePartsBeans[<s:property value="%{#partsCounter}"/>].received"
					value="<s:property value="received"/>" />
			<td>
				<div id="includeInspectionAction">
					<table>
						<jsp:include flush="true" page="inspectionAction.jsp" />
					</table>
				</div>
			</td>
			<s:set name="partsCounter" value="%{#partsCounter + 1}" />
		</tr>
		</s:iterator>
	</table>
</div>

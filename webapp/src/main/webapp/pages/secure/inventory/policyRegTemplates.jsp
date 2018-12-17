<%@taglib prefix="t" uri="twms"%><%@taglib prefix="u" uri="/ui-ext"%>
<%@ taglib prefix="s" uri="/struts-tags" %>	

<script type="text/javascript">
    dojo.require("dijit.Tooltip");
    dojo.require("twms.widget.FileUploader");    
</script>

<s:hidden id="terminationStatus" name="terminatedStatus"/>
<s:hidden id="activationStatus" name="activeStatus"/>
<u:jsVar varName="editableTemplate">
	<tr index="${index}">
    <td align="left">
        <input type="hidden" name="registeredPolicies[${index}]" value="${id}"/>
        <input type="hidden" name="registeredPolicies[${index}].policyDefinition" value="${definitionId}"/>
        ${policyCode}
    </td>
			<td align="left">${policyName}</td>
			<td align="left">${type}</td>
			<td align="right">
			${startDate}
			<input type="hidden" name="registeredPolicies[${index}].policyAudits[${auditSize}].warrantyPeriod.fromDate" value="${startDate}"/>
			</td>
			<td align="center">
				<input type="text" name="registeredPolicies[${index}].policyAudits[${auditSize}].warrantyPeriod.tillDate" id="edit_end_date_${index}" size="11" maxlength="10" value="${endDate}"/>
				<img src="image/calendarIcon.gif" width="15" height="12" border="0" id="edit_end_date_Trigger_${index}" style="cursor: pointer;" vspace="3"/>
				<span dojoType="dijit.Tooltip" connectId="edit_end_date_Trigger_<s:property value="index"/>">
					<span class="admin_selections"><s:text name="choose.date"/></span>
				</span>
				<script type="text/javascript">
				    Calendar.setup({
				        inputField     :    "edit_end_date_${index}",
				        ifFormat       :    "<s:property value="jSCalendarDateFormatForLoggedInUser" />",
				        button         :    "edit_end_date_Trigger_${index}"
				    });
				</script>
				<script type="text/javascript">
	            		dojo.addOnLoad(function() {
	            			disableCheckBox(${index},${auditSize});
	            			toggleEndDateSetStatus(${index},${auditSize});
                		});
	        	</script>
				<br>
				<input type="checkbox" id="registeredPolicies[${index}].policyAudits[${auditSize}].status" name="registeredPolicies[${index}].policyAudits[${auditSize}].status"
								value="${currentPolicyStatus}"	onclick="toggleEndDateSetStatus(${index},${auditSize})"/>
				<div id = "action_${index}"></div>
				
			</td>
			<td align="center">${monthsCovered}</td>
			<s:if test="!(inventoryItem.serializedPart)">
			<td align="center"><input type="text" name="registeredPolicies[${index}].policyAudits[${auditSize}].serviceHoursCovered"
                                     size="25"  value="${hoursCovered}"/></td>
			</s:if>
			<td align="center">${policyStatusForDisplay}
			<input type="hidden" id="registeredPolicies[${index}].policyStatusInput" value="${policyStatus}"/>
			</td>
            <td align="left"><input type="text" name="registeredPolicies[${index}].policyAudits[${auditSize}].comments"
                                     size="10"  value="${comments}"/></td>
			<td align="center"></td>
			<td align="center">${purchaseOrdNumber}</td>
			<td align="center">${purchaseDate}</td>
			<td>
				<div id="edit_attachments_container_${index}" style="width:100%;height:100%;overflow:hidden">
				<u:uploadDocument id="edit_attachments_${index}" name="registeredPolicies[${index}].attachments" 
					trimFileNameDisplayTo="6" />
				</div>
			</td>
  </tr>
</u:jsVar>

<u:jsVar varName="readonlyTemplate">
	<tr index="${index}">
    <td align="left">
        <input type="hidden" name="registeredPolicies[${index}].policyDefinition" value="${definitionId}"/>
         <a href="#"><span id="${id}" class="link" onClick="getPolicyDetails('${id}');">${policyCode}</span></a>            
    </td>
			<td align="left">${policyName}</td>
			<td align="center">${type}</td>
			<td align="center">${startDate}</td>
			<td align="center">${endDate}</td>
			<td align="center">${monthsCovered}</td>
			<s:if test="!(inventoryItem.serializedPart)">
			<td align="center">${hoursCovered}</td>
			</s:if>
			<td align="center">${policyStatusForDisplay}</td>
        <s:if test="isDRDoneByLoggedInUser() || isUserInventoryFullView()">
            <td align="left">${comments}</td>
			<td align="center"><a href="#"><span id="${id}" class="link" onClick="getPolicyAudits('${id}');"><s:text name="home_jsp.menuBar.view"/></span></a></td> 
			<td align="center">${purchaseOrdNumber}</td>
			<td align="center">${purchaseDate}</td>
			<td>
				<div id="read_attachments_container_${index}">
				<u:uploadDocument id="read_attachments_${index}" name="registeredPolicies[${index}].attachments" 
					trimFileNameDisplayTo="6" disabled="true" />
				</div>
			</td>
		</s:if>
	</tr>
</u:jsVar>
	
<u:jsVar varName="editableTemplateForGoodwill">
	<tr index="${index}">
    <td align="left">
        <input type="hidden" name="registeredPolicies[${index}]" value="${id}"/>
        <input type="hidden" name="registeredPolicies[${index}].policyDefinition" value="${definitionId}"/>
        ${policyCode}
    </td>
			<td align="left">${policyName}</td>
			<td align="left">${type}</td>
			<td align="right">
			${startDate}
			</td>
			<td align="center">
				<input type="text" name="registeredPolicies[${index}].policyAudits[${auditSize}].warrantyPeriod.tillDate" id="edit_end_date_${index}" size="11" maxlength="10" value="${endDate}"/>
				<img src="image/calendarIcon.gif" width="15" height="12" border="0" id="edit_end_date_Trigger_${index}" style="cursor: pointer;" vspace="3"/>
				<span dojoType="dijit.Tooltip" connectId="edit_end_date_Trigger_<s:property value="index"/>">
					<span class="admin_selections"><s:text name="choose.date"/></span>
				</span>
				<script type="text/javascript">
				    Calendar.setup({
				        inputField     :    "edit_end_date_${index}",
				        ifFormat       :    "<s:property value="jSCalendarDateFormatForLoggedInUser" />",
				        button         :    "edit_end_date_Trigger_${index}"
				    });
				</script>
				<script type="text/javascript">
	            		dojo.addOnLoad(function() {
	            			disableCheckBox(${index},${auditSize});
	            			toggleEndDateSetStatus(${index},${auditSize});
                		});
	        	</script>
	        	<br>
	        	<input type="checkbox" id="registeredPolicies[${index}].policyAudits[${auditSize}].status" name="registeredPolicies[${index}].policyAudits[${auditSize}].status"
								value="${currentPolicyStatus}"	onclick="toggleEndDateSetStatus(${index},${auditSize})"/>
				<div id = "action_${index}"></div>
				
			</td>
			<td align="right">
				${monthsCovered}
			</td>
			<s:if test="!(inventoryItem.serializedPart)">
			<td align="right">
				<input type="text" name="registeredPolicies[${index}].policyAudits[${auditSize}].serviceHoursCovered" value="${hoursCovered}"/>
			</td>
			</s:if>
			<td align="left">${policyStatusForDisplay}</td><input type="hidden" id="registeredPolicies[${index}].policyStatusInput" value="${policyStatus}"/>
			<td align="left"><input type="text" name="registeredPolicies[${index}].policyAudits[${auditSize}].comments" size="25"  value="${comments}"/></td>
			<td align="left"></td>
			<td align="left"></td>
			<td align="left"></td>
	</tr>			
</u:jsVar>
 
<u:jsVar varName="readonlyTemplateForAdded">
	<tr index="${index}" style="color:red;">
    <td align="left">
        <input type="hidden" name="registeredPolicies[${index}].policyDefinition" value="${definitionId}"/>
        ${policyCode}
    </td>
		<td align="left">${policyName}</td>
		<td align="left">${type}</td>
		<td align="right">${startDate}</td>
		<td align="right">${endDate}</td>
		<td align="right">${monthsCovered}</td>
		<s:if test="!(inventoryItem.serializedPart)">
		<td align="right">${hoursCovered}</td>
		</s:if>
		<td align="left">${policyStatusForDisplay}</td>
		<td align="left">${comments}</td>
		<td align="left"></td>
		<td align="left">${purchaseOrdNumber}</td>
		<td align="left">${purchaseDate}</td>
			
	</tr>
</u:jsVar>

<u:jsVar varName="additionalTemplate">
	<tr index="${index}">
		<td align="center">
			<input id="check_${index}" type="checkbox" class="selectedDefinitionIds" name="selectedDefinitionIds" value="${definitionId}"/>
		</td>
    <td align="left">
        ${policyCode}
    </td>
		<td align="left">${policyName}</td>
		<td align="left">${type}</td>			
	</tr>	
</u:jsVar>

<div class="policyRegn_section_div" style="width:100%;">
    	<div class="admin_section_heading">
        	<s:text name="label.manageBusinessRule.history.sectionName"/>
    	</div>
    	<table width="100%" cellspacing="0" cellpadding="0" class="grid borderForTable" style="margin:5px;">
	        <tr>
	            <th width="10%" nowrap="nowrap" class="colHeader" style="border:1px solid #fff"><s:text name="columnTitle.manageBusinessRule.history.ruleDescription"/></th>
	            <th width="10%" nowrap="nowrap" class="colHeader" style="border:1px solid #fff"><s:text name="columnTitle.manageBusinessRule.history.result"/></th>
	            <th width="10%" nowrap="nowrap" class="colHeader" style="border:1px solid #fff"><s:text name="columnTitle.manageBusinessRule.history.comments"/></th>
	            <th width="10%" nowrap="nowrap" class="colHeader" style="border:1px solid #fff"><s:text name="columnTitle.manageBusinessRule.history.dateLastModified"/></th>
	            <th width="10%" nowrap="nowrap" class="colHeader" style="border:1px solid #fff"><s:text name="columnTitle.manageBusinessRule.history.modifiedBy"/></th>	            
	            <th width="10%" nowrap="nowrap" class="colHeader" style="border:1px solid #fff"><s:text name="columnTitle.manageBusinessRule.history.version"/></th>
	            <th width="10%" nowrap="nowrap" class="colHeader" style="border:1px solid #fff"><s:text name="columnTitle.manageBusinessRule.history.status"/></th>
	        </tr>
            <s:iterator value="rule.ruleAudits" status="iter">                        
	            <tr>
		            <td width="10%"><s:property value="name" /></td> 
		            <td width="10%"><s:property value="action.name" /></td>
		            <td width="10%"><s:property value="d.internalComments" /></td>
		            <td width="10%"><s:property value="createdOn" /></td>
		            <td width="10%"><s:property value="createdBy.completeNameAndLogin" /></td>
		            <td width="10%"><s:label value="%{#iter.index+1}" /></td>
		            <td width="10%"><s:property value="status" /></td>
	            </tr>
	         </s:iterator>  
    	</table>    	
</div>
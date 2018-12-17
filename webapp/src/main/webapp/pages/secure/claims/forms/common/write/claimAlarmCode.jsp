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
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="sd" uri="/struts-dojo-tags"%>
<%@taglib prefix="t" uri="twms"%>
<%@taglib prefix="u" uri="/ui-ext"%>

<div class="admin_section_div" style="margin: 5px; width: 99%">
<u:repeatTable id="myTable" cssClass="grid borderForTable"
	cellpadding="0" cellspacing="0" cssStyle="margin:5px;" theme="simple">
	<thead>
		<tr class="title">
			<th width="49%" class="colHeader"><s:text
				name="title.required.alarmCode" /></th>
			<th width="49%" class="colHeader"><s:text
				name="columnTitle.common.description" /></th>
			<th width="2%" class="colHeader"><u:repeatAdd id="alarmadder"
				theme="simple">
				<img id="addAlarmCodeId" src="image/addRow_new.gif" border="0"
					style="cursor: pointer; padding-right: 4px;"
					title="<s:text name="title.alarmcode.add" />" />
			</u:repeatAdd></th>
		</tr>
	</thead>
	<u:repeatTemplate id="alarmcode_body" value="task.claim.alarmCodes"
		index="myindex" theme="twms">
		<tr index="#myindex">
		   <script type="text/javascript">
					dojo.addOnLoad(function(){
						dojo.subscribe("/claim/alarmcode/changed/#myindex", null, function(data, type, request) {
							fillAlarmCodeDescription(#myindex, data, type);
				     	});  
					});
					</script>
			<td style="border: 1px solid #EFEBF7;">
			<s:hidden id="hiddenalarm_#myindex" name="task.claim.alarmCodes[#myindex]" />
			<s:if test="task.claim.itemReference.referredInventoryItem != null">
				<sd:autocompleter id='alarmCode_#myindex' showDownArrow='false' href='claim_list_alarmcode.action?itemGroup=%{task.claim.itemReference.referredInventoryItem.ofType.product}' cssStyle='width:410px' name='task.claim.alarmCodes[#myindex]' value='%{task.claim.alarmCodes[#myindex].code}' notifyTopics='/claim/alarmcode/changed/#myindex' loadOnTextChange='true' loadMinimumCount='1' />
			</s:if>
			<s:else>
				<sd:autocompleter id='alarmCode_#myindex' showDownArrow='false' href='claim_list_alarmcode.action?itemGroup=%{task.claim.itemReference.model.isPartOf.isPartOf}' cssStyle='width:410px' name='task.claim.alarmCodes[#myindex]' value='%{task.claim.alarmCodes[#myindex].code}' notifyTopics='/claim/alarmcode/changed/#myindex' loadOnTextChange='true' loadMinimumCount='1' />
			</s:else>
				</td>
				<td style="border: 1px solid #EFEBF7;"><span id="alarmcode_description_#myindex" > </span> 	</td>
			<td style="border: 1px solid #EFEBF7;"><u:repeatDelete
				id="deleter_#myindex" theme="simple">
				<div align="center"><img id="deletePrice" src="image/remove.gif" border="0"
					style="cursor: pointer;"
					title="<s:text name="title.alarmcode.delete" />" /></div>
			</u:repeatDelete></td>
		</tr>
	</u:repeatTemplate>
</u:repeatTable></div>
		<script type="text/javascript">
		
				  function  fillAlarmCodeDescription(index,number,type){
					if (type != "valuechanged") {
				        return;
				    }
				    twms.ajax.fireJavaScriptRequest("get_alarmcode_description.action",{
				            number: number
				        }, function(details) {
				        	var tr = findAlarmCodeRow(index);
				           	fillAlarmCodeForARow(tr, details, index);
				            delete tr;
				           	}
				        );
					}
		
					function findAlarmCodeRow(index) {
						    var tbody = dojo.byId("alarmcode_body");
						    var matches = dojo.query("> tr[index=" + index + "]", tbody);
						    return (matches.length == 1) ? matches[0] : null;
					}
			      	 
			      	function fillAlarmCodeForARow(tr, details, index,number) {
						if(tr != null){
							var descriptionField = document.getElementById("alarmcode_description_"+index);
					        descriptionField.innerHTML = details[0]; 
					        dojo.byId("hiddenalarm_"+index).value=details[1];
					    }
					 } 
		</script>


<%@taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="sd" uri="/struts-dojo-tags"%>
<%@taglib prefix="t" uri="twms"%>
<%@taglib prefix="u" uri="/ui-ext"%>
<%@taglib prefix="authz" uri="authz"%>
<%@ page contentType="html;charset=UTF-8" language="java"%>

<tr>
	<td>
		<s:label name="%{#nListName}.sequenceNumber"	id="%{qualifyId(\"Parts_sequenceNumber\")}" value="%{sequenceNumber}" />
	</td>
	<td>
		
		<s:if test="(part.source!=null)&&(part.source.toString().equalsIgnoreCase('INSTALLBASE') )">
				<s:label name="%{#nListName}.part.serialNumber"	id="%{qualifyId(\"Parts_serialNumber\")}" value="%{part.serialNumber}" />
		</s:if>
		<s:else>
				<s:textfield name="%{#nListName}.part.serialNumber"	id="%{qualifyId(\"Parts_serialNumber\")}" size="12" value="%{part.serialNumber}"/>
		</s:else>
	</td>
		
	<td>
		<s:if test="(part.source!=null)&&(part.source.toString().equalsIgnoreCase('INSTALLBASE') )">
			<s:textfield name="%{#nListName}.part.ofType" value="%{part.ofType.number}" id="%{qualifyId(\"Parts_itemNo\")}" disabled="true" />
		</s:if>
		<s:else>
	<s:div>
	        <sd:autocompleter delay='1000' href='list_major_components.action?selectedBusinessUnit=%{selectedBusinessUnit}' name='%{#nListName}.part.ofType' value='%{part.ofType.number}' loadOnTextChange='true' showDownArrow='false' notifyTopics='/oem_part/itemno/changed/#index' id='%{qualifyId("Parts_itemNo")}'/>
					<script type="text/javascript">
	           			dojo.addOnLoad(function(){		           	
	            		dojo.connect(dijit.byId('<s:property value="qualifyId(\"Parts_itemNo\")" />'),"onChange",function(){	            		            		
	            		fillPartDetails(this.id,'<s:property value="qualifyId(\"Part_Desc\")" />');
	            	});
		            });
	       			</script>
			</s:div>
		</s:else>
	</td>

	<td><s:textfield name="%{#nListName}.part.ofType.description"
		value="%{part.ofType.description}" id="%{qualifyId(\"Part_Desc\")}" disabled="true" /></td>

	<td>
		<s:if
		test="(part.source!=null)&&(part.source.toString().equalsIgnoreCase('INSTALLBASE') )">
			<s:label name="%{#nListName}.part.installationDate"
				value="%{part.installationDate}" />
		</s:if>
		<s:else>
			<sd:datetimepicker name='%{#nListName}.part.installationDate' value='%{part.installationDate}' id='%{qualifyId("part_install_date")}' />
		</s:else>
	</td>

	<s:if
		test="(part.source!=null)&&(part.source.toString().equalsIgnoreCase('INSTALLBASE') )">
		<td></td>
	</s:if>
	<s:else>
		<td width="5%" align="center"><s:hidden name="%{#nListName}" value="%{id}"
			id="%{qualifyId(\"inventoryItem_composedOf_id\")}" />			
		<div class="nList_delete" />
		</td>
		
	</s:else>


<script type="text/javascript">			
function fillPartDetails(id,descId) {	
    var number = dijit.byId(id).getDisplayedValue();   
    twms.ajax.fireJavaScriptRequest("list_description_for_part.action",{    		
            number: number
        }, function(details) {        	
            document.getElementById(descId).value=details;
        }
    );}	
</script>
</tr>

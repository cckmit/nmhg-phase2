<%@ page contentType="text/html;charset=UTF-8" language="java"%>

<%@ taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="t" uri="twms"%>
<%@taglib prefix="u" uri="/ui-ext"%>
<%@ taglib prefix="sd" uri="/struts-dojo-tags"%>
<tr id='%{qualifyId("installedPartRow")}'>
	<s:hidden name="%{#nListName}.isHussPartInstalled" id="installedPart_%{#mainIndex.index}_%{#subIndex.index}_isHussPartInstalled"
		value='%{isHussPartInstalled}' />

	<td width="10%" align="center" class="partReplacedClass"><sd:autocompleter size="15" cssStyle='margin:0 3px;width:99px;' id="%{qualifyId(\"installedHussmanPartNumber\")}"
			showDownArrow='false' required='true' notifyTopics='/installedPart/description/show/%{#nListIndex}'
			href='list_parts.action?selectedBusinessUnit=%{campaign.businessUnitInfo.name}' name='%{#nListName}.item' keyName='%{#nListName}.item'
			keyValue="%{item.id}" value='%{item.number}' /> <script type="text/javascript">
			 dojo.addOnLoad(function() {                                      	
                                        	var index = '<s:property value="%{#nListIndex}" />';
                                        	var  descId = '<s:property value="qualifyId(\"descriptionSpan_installedPartDescription\")" />'; 
                                        	var  installedPartId = '<s:property value="qualifyId(\"installedHussmanPartNumber\")" />'; 
                                            dojo.subscribe("/installedPart/description/show/"+index, null, function(number, type, request) {
                                                if (type != "valuechanged") {
                                                    return;
                                                }
                                                twms.ajax.fireJsonRequest("getUnserializedOemPartInfo.action", {
                                                	claimType: 'Campaign',
                                                    number: dijit.byId(installedPartId).getValue()
                                                }, function(details) {                                                	
                                                    dojo.byId(descId).innerHTML = details[0];
                                                }
                                                        );
                                            });
                                        });
                                    </script></td>
	<td width="10%" align="center" class="partReplacedClass"><s:textfield id="%{qualifyId(\"installedHussmanQuantity\")}" size="3"
			name="%{#nListName}.noOfUnits" />
	</td>
	<td width="45%" align="center" class="partReplacedClass"><span id='<s:property value="qualifyId(\"descriptionSpan_installedPartDescription\")" />'>
			<s:property value="campaign.hussPartsToReplace[%{#mainIndex.index}].installedParts[%{#subIndex.index}].item.description" /> </span>
	</td>
	<td width="15%" align="center" class="partReplacedClass"><input type="checkbox" id="<s:property value="qualifyId(\"installedShippedByOEM\")" />"
		name="<s:property value = 'nListName' />.shippedByOem" value = "true" /> <script type="text/javascript">
							            dojo.addOnLoad(function(){
							            	var installedShippedByOEM = '<s:property value="shippedByOem.booleanValue()"/>';
											var  installedShippedByOEMId = '<s:property value="qualifyId(\"installedShippedByOEM\")" />';
							            	if(installedShippedByOEM == 'true' && dojo.byId(installedShippedByOEMId))	{						            	
							            		dojo.byId(installedShippedByOEMId).checked = 'checked';		
							            	}else {
							            		dojo.byId(installedShippedByOEMId).checked = '';	
							            		}
							            });
						            </script>
	</td>
	<td width="5%" align="center" class="partReplacedClass"><s:hidden name="%{#nListName}" value="%{id}"
			id="%{qualifyId(\"installedPart_Id\")}" />
		<div class="nList_delete" />
	</td>
</tr>

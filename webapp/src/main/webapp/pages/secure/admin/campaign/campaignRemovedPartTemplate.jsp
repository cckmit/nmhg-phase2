<%@ page contentType="text/html;charset=UTF-8" language="java"%>

<%@ taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="t" uri="twms"%>
<%@taglib prefix="u" uri="/ui-ext"%>
<%@ taglib prefix="sd" uri="/struts-dojo-tags"%>

<tr id='%{qualifyId("removedPartRow")}'>
	<s:hidden name="%{#nListName}.isHussPartInstalled" id="removedPart_%{#mainIndex.index}_%{#subIndex.index}_isHussPartInstalled"
		value='%{isHussPartInstalled}' />

	<td width="10%" align="center" class="partReplacedClass"><sd:autocompleter size="15" cssStyle='margin:0 3px;width:99px;' id="%{qualifyId(\"removedHussmanPartNumber\")}"
			showDownArrow='false' required='true' notifyTopics='/replacedPart/description/show/%{#nListIndex}'
			href='list_parts.action?selectedBusinessUnit=%{campaign.businessUnitInfo.name}' name='%{#nListName}.item' keyName='%{#nListName}.item'
			keyValue="%{item.id}" value='%{item.number}' /> <script type="text/javascript">
			 dojo.addOnLoad(function() {                                      	
                                        	var index = '<s:property value="%{#nListIndex}" />';
                                        	var  removedPartId = '<s:property value="qualifyId(\"removedHussmanPartNumber\")" />'; 
                                            dojo.subscribe("/replacedPart/description/show/"+index, null, function(number, type, request) {
                                                if (type != "valuechanged") {
                                                    return;
                                                }
                                                twms.ajax.fireJsonRequest("getUnserializedOemPartInfo.action", {
                                                	claimType: 'Campaign',
                                                    number: dijit.byId(removedPartId).getValue()
                                                }, function(details) {
                                                	var  descId = '<s:property value="qualifyId(\"descriptionSpan_removedPartDescription\")" />'; 
                                                    dojo.byId(descId).innerHTML = details[0];
                                                }
                                                        );
                                            });
                                        });
                                    </script>
	</td>


	<td width="10%" align="center" class="partReplacedClass"><s:textfield id="%{qualifyId(\"removedHussmanQuantity\")}" size="3"
			name="%{#nListName}.noOfUnits" />
	</td>
	<td width="30%" align="center" class="partReplacedClass"><span id='<s:property value="qualifyId(\"descriptionSpan_removedPartDescription\")" />'> <s:property
				value="item.description" /> </span>
	</td>

	<td width="10%" align="center" class="partReplacedClass"><sd:autocompleter id="%{qualifyId(\"removedPartLocation\")}" size='2' cssStyle='margin:0 3px;width:99px;'
			href='list_part_return_Locations.action?selectedBusinessUnit=%{campaign.businessUnitInfo.name}' name='%{#nListName}.returnLocation' keyName='%{#nListName}.returnLocation'
			loadOnTextChange='true' showDownArrow='false' value='%{returnLocation.code}' keyValue="%{returnLocation.id}" listenTopics='/partReturn/returnLocation/%{#nListIndex}' />
	</td>
	<script type="text/javascript">
											dojo.addOnLoad(function() {
												var index = '<s:property value="%{#nListIndex}" />';
												var  locId = '<s:property value="qualifyId(\"removedPartLocation\")" />'; 
                                            dijit.byId(locId).store.includeSearchPrefixParamAlias=false;
							                        dojo.publish("/partReturn/returnLocation/"+index, [{
							                            addItem: {
							                                key: '<s:property value ="%{returnLocation.id}"/>',
							                                label: '<s:property value ="%{returnLocation.code}"/>'
							                            }
							                        }]);
							                    });
							                </script>

	<td width="20%" align="center" class="partReplacedClass"><s:select list="paymentConditions" id="%{qualifyId(\"paymentCondition\")}" cssStyle='margin:0 3px;width:120px;'
			name="%{#nListName}.paymentCondition" listKey="code" listValue="description" emptyOption="true" cssClass="hussmannPartReplaced"
			value="%{paymentCondition}">
		</s:select>
	</td>
	<td width="5%" align="center" class="partReplacedClass"><s:textfield size="3" id="%{qualifyId(\"dueDays\")}"
			name="%{#nListName}.dueDays"></s:textfield>
	</td>

	<td width="5%" align="center" class="partReplacedClass"><s:hidden name="%{#nListName}" value="%{id}"
			id="%{qualifyId(\"removedPart_Id\")}" />
		<div class="nList_delete" /></td>
</tr>

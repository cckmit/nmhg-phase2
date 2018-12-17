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

<%@page contentType="text/html"%>
<%@ taglib prefix="sd" uri="/struts-dojo-tags"%>
<%@page pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="t" uri="twms"%><%@taglib prefix="u" uri="/ui-ext"%>
<script type="text/javascript" src="scripts/CheckBoxListControl.js"></script>
 
				<div class="section_header"><s:text name="label.supplier.partDetails" /></div>
		
				<table width="100%" border="0" cellpadding="0" cellspacing="0"  class="borderForTable" align="center" style="margin:5px;width:99%">
					<tr>
						<td class="warColHeader" width="2%" class="warColHeader"><div align="center">
						 <input id="selectAll_<s:property value="claim.id" />" type="checkbox" 		          		 
         					checked="checked" value="checkbox" style="border:none"/>
	  					<script type="text/javascript">
			  				var masterCheckBox = new CheckBoxListControl(dojo.byId("selectAll_<s:property value="claim.id" />") );																		
						</script>
						</div>
						</td>
						<td valign="middle" nowrap="nowrap" class="warColHeader" width="10%"><s:text name="label.partNumber" /></td>
						<td valign="middle" nowrap="nowrap" class="warColHeader" width="14%"><s:text name="label.supplier.supplierPartNumber" /></td>
						<td valign="middle" nowrap="nowrap" class="warColHeader" width="6%" ><s:text name="label.common.quantity" /></td>
						<td valign="middle" nowrap="nowrap" class="warColHeader" width="25%" ><s:text name="label.common.description" /></td>
						<td valign="middle" nowrap="nowrap" class="warColHeader" width="10%" ><s:text name="label.supplier.commentSRA" /></td>
						<td valign="middle" nowrap="nowrap"
							class="warColHeader" width="10%" ><s:text name="label.supplier.recoveryAmount" /></td>
						<td nowrap="nowrap" class="warColHeader" width="15%" ><s:text name="label.supplier.location" /></td>
						<td nowrap="nowrap" class="warColHeader"><s:text name="label.supplier.dueDays" /></td>
					</tr>
					<s:iterator value="recoveredPartsToBeShipped" status="partStatus"
						id="recoveredPart">
						<tr>
							<td nowrap="nowrap" class="warColData"><div align="center">
							<input type="checkbox" id="cbox_<s:property value="%{#partStatus.index}"/>" name="recoveredPartsToBeShipped[<s:property value="%{#partStatus.index}"/>]"
								 checked ="checked" value="<s:property value="%{id}"/>"/>
								 <script type="text/javascript">		
			    					var selectElementId = "cbox_<s:property value="%{#partStatus.index}"/>";				
			    					var selectElement =	dojo.byId(selectElementId);
									masterCheckBox.addListElement(selectElement);
			  					</script>	</div>
							</td>
							<td nowrap="nowrap" class="warColData" ><s:property
								value="oemPart.itemReference.unserializedItem.number" /></td>
							<td nowrap="nowrap" class="warColData"><s:property
								value="supplierItem.number" /></td>
							<td class="warColData"><s:property
								value="quantity" /></td>
							<td class="warColData"><s:property
								value="oemPart.itemReference.unserializedItem.description" /></td>
							<td class="warColData"><s:property
								value="recoveryClaim.comments" />&nbsp;</td>
							<td class="warColData"><s:property
								value="recoveryClaim.totalRecoveredCost" />
								</td>
							<td class="warColData" >
						
							
							
							<sd:autocompleter id='supplierPart_location_%{#partStatus.index}' size='2' cssStyle='width:90%;' href='list_part_return_Locations.action' name='locations[%{#partStatus.index}]' listenTopics='/location/initial/%{#partStatus.index}' keyName='locations[%{#partStatus.index}]' loadOnTextChange='true' loadMinimumCount='1' showDownArrow='false' />
							
							<script type="text/javascript">
		                  		dojo.addOnLoad(function(){
			                        dojo.publish("/location/initial/<s:property value ='%{#partStatus.index}'/>", [{
		                            addItem: {
		                                key: '<s:property value="%{locations[#partStatus.index].id}"/>',
		                                label: '<s:property value="%{locations[#partStatus.index].code}"/>'
		                            }
		                        }]);
		                     });
	          			  </script>
							
							</td>
							<td class="warColData"><s:textfield	name='dueDays[%{#partStatus.index}]' size="3"/>
							
								</td>
						</tr>
						
					</s:iterator>
					<s:hidden name="dueDays"></s:hidden>
				</table>
				

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
<style>
	img#toolTip{cursor:pointer; margin-left:10px;}
</style>

<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="sd" uri="/struts-dojo-tags"%>
<%@taglib prefix="t" uri="twms"%><%@taglib prefix="u" uri="/ui-ext"%>

<div class="mainTitle" style="margin:10px 0px 0px 0px;"><s:text
	name="label.partReturnConfiguration.config" /></div>
	
<u:repeatTable id="myTable" cssClass="borderForTable" 
	 theme="twms" cellspacing="0" cellpadding="0" cssStyle="margin:5px;">
	<thead>
		<tr class="admin_table_header">
			<th class="colHeader"><s:text name="columnTitle.partReturnConfiguration.billingDate" />
				<img id="toolTip" src="image/alerts.gif" width="16" height="14" align="absmiddle" alt="" />
				<span dojoType="dijit.Tooltip" connectId="toolTip" label="<s:text name="label.partReturnConfiguration.applicateDate" />"></span>							
			</th>
			<th class="colHeader"><s:text
				name="columnTitle.partReturnConfiguration.paymentCondition" /></th>
			<th class="colHeader"><s:text
				name="columnTitle.partReturnConfiguration.isCausalPart" /></th>
			<th class="colHeader"><s:text name="columnTitle.partReturnConfiguration.returnLocation" /></th>
			<th class="colHeader"><s:text name="columnTitle.partReturnConfiguration.daysDue" /></th>
			<th class="colHeader"><s:text name="label.partReturnConfiguration.daysDue" /></th>
			<th class="colHeader"><s:text name="label.partReturn.RmaNo" /></th>
			<th class="colHeader"><s:text name="label.partReturnConfiguration.quantityReceived" /></th>
			<th class="colHeader" width="9%"><u:repeatAdd id="adder" theme="twms">
				<img id="addProductIcon" src="image/addRow_new.gif" border="0"
					style="cursor: pointer;"
					title="<s:text name="label.partReturnConfiguration.addConfiguration" />" />
			</u:repeatAdd></th>
		</tr>
	</thead>
	<u:repeatTemplate id="mybodyPRC" value="partReturnDefinition.configurations" index="myindex"
		theme="twms" >
		<tr index="#myindex">
			<td>
                <s:hidden name="partReturnDefinition.configurations[#myindex]"/>
            <table >
				<tr>
					<td ><s:text
						name="label.common.from" />:</td>
					<td valign="top"><sd:datetimepicker name='partReturnDefinition.configurations[#myindex].duration.fromDate' value='%{duration.fromDate}' id='startDate_#myindex' /></td>
				</tr>
				<tr>
					<td ><s:text
						name="label.common.to" />:</td>
					<td valign="top"><sd:datetimepicker name='partReturnDefinition.configurations[#myindex].duration.tillDate' value='%{duration.tillDate}' id='endDate_#myindex' /></td>
				</tr>
			</table>
			</td>
			<td valign="top"><s:select list="paymentConditions" cssStyle="width:165px;"
				name="partReturnDefinition.configurations[#myindex].paymentCondition"
				listKey="code" listValue="description" emptyOption="false" 
				value="%{paymentCondition.code}" theme="twms">
				</s:select>
			</td>
			<td valign="top"><s:radio
				name="partReturnDefinition.configurations[#myindex].causalPart" value="%{causalPart}" list="yesNo"
				listKey="key" listValue="value" theme="twms"/></td>
            <td valign="top">
                <sd:autocompleter id="locations_#myindex"
                                 href="list_part_return_Locations_for_PRC.action"
                                 name="partReturnDefinition.configurations[#myindex].returnLocation" loadOnTextChange="true"
                                 showDownArrow="false" 
                                 listenTopics="/setInitialValue/onLoad/#myindex" 
                                 cssClass="admin_selections"/>
                <script type="text/javascript">
                        dojo.addOnLoad(function(){
                        var indexCounter = #myindex;
                        dijit.byId("locations_" + indexCounter).store.includeSearchPrefixParamAlias=false;
                        dojo.publish("/setInitialValue/onLoad/#myindex", [{
                            addItem: {
                                key:'<s:property value="%{returnLocation.id}"/>',
                                label:'<s:property value="%{returnLocation.code}"/>'
                            }
                        }]);
                    });
                </script>
            </td>
			<td valign="top"><s:textfield
				name="partReturnDefinition.configurations[#myindex].dueDays" value="%{dueDays}" theme="twms" size="4"/></td>
			<td valign="top"><s:textfield
				name="partReturnDefinition.configurations[#myindex].maxQuantity" value="%{maxQuantity}" theme="twms" size="5"/></td>
		    <td valign="top">
                <s:textfield
                name="partReturnDefinition.configurations[#myindex].rmaNumber" value="%{rmaNumber}" theme="twms" size="5"/></td>
            </td>
			<td valign="top">
			 <s:if test="partReturnDefinition.id == null || partReturnDefinition.configurations[#myindex] == null">
			 	<s:property value="0"/>
			 </s:if>
			 <s:else> 
				<s:property value="quantityReceived"/>
			 </s:else>	
			<td valign="top"><u:repeatDelete id="deleter_#myindex"
				theme="twms">
				<img id="deleteConfiguration" src="image/remove.gif" border="0"
					style="cursor: pointer;"
					title="<s:text name="label.partReturnConfiguration.deleteConfiguration" />" />
			</u:repeatDelete></td>
		</tr>
	</u:repeatTemplate>
</u:repeatTable>

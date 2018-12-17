<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="t" uri="twms"%>
<%@taglib prefix="u" uri="/ui-ext"%>

<html>
<head>
	<u:stylePicker fileName="warrantyForm.css" />
</head>
<u:actionResults />
<script type="text/javascript">	 
    dojo.require("dijit.layout.ContentPane");
    dojo.require("dijit.layout.LayoutContainer");    				   
</script>
		<div dojoType="dijit.layout.ContentPane" layoutAlign="client" style="height:200px;overflow-y:auto;overflow-x:hidden;">
			<s:form	action="fleetInventoryUnScrap" theme="twms" validate="true">
				<div class="section_div">
					<table class="grid borderForTable" border="0" cellspacing="0" cellpadding="0" width="100%">
						<thead>
							<tr>
								<th class="warColHeader" align="center" style="padding:0;margin:0" width="2%">
									<input	type="checkbox" name="checkbox" value="checkbox"
										id="masterCheckBox" class="policy_checkboxes" disabled />
								</th>
								<th class="warColHeader" align="left">
								    <s:text	name="label.common.serialNumber" />
								</th>
								<th class="warColHeader" align="left">
								    <s:text	name="label.common.modelNumber" />
								</th>
								<th class="warColHeader" align="left">
								    <s:text	name="columnTitle.common.dealerName" />
								</th>
								<th class="warColHeader" align="left">
								    <s:text	name="label.managePolicy.itemCondition" />
								</th>
							</tr>
						</thead>
						<tbody id="UnScrapInventory_list">
							<s:iterator value="scrapInventoryItems" status="inventoryIterator">
								<tr>
									<td align="center">
										<s:hidden name="scrapInventoryItems[%{#inventoryIterator.index}]" 
										    value="%{id}" />
										<input type="checkbox" checked="checked" disabled="disabled"/>
									</td>
									<td><s:property value="serialNumber" /></td>
									<td><s:property value="ofType.model.name" /></td>
									<td><s:property value="dealer.name" /></td>
									<td><s:property value="conditionType.itemCondition" /></td>
								</tr>
							</s:iterator>
						</tbody>
					</table>
				</div>
				<table order="0" cellspacing="0" cellpadding="0" width="100%">
				    <tr>
						<td>
						   <s:label value="%{getText('label.scrap.unScrapDate')}"/> :
						   <s:property value="unScrapDate" />
						   <input type="hidden" name="unScrapDate" 
					                     value="<s:property value="unScrapDate"/>"/>
					    </td>
					</tr>          
					<tr>
						<td>
							<label for="probably_cause">
								<s:text	name="label.common.comments" />:
							</label>
							<br/>
							<s:property value="scrapComments" />
							<s:hidden name="scrapComments" />
						</td>
					</tr>
				</table>
				<div id="submit" align="center">
				    <s:submit cssClass="buttonGeneric" value="%{getText('button.common.submit')}" />
				</div>
			</s:form>
	</div>

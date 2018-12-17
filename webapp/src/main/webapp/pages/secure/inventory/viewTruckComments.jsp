<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<%@taglib prefix="s" uri="/struts-tags" %>
<%@taglib prefix="t" uri="twms"%>
<%@taglib prefix="u" uri="/ui-ext"%>
<html>
<head>
    <s:head theme="twms"/>
    <u:stylePicker fileName="common.css"/>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Truck Comments</title>
 <s:head theme="twms"/>
    <script type="text/javascript">
        dojo.require("dijit.layout.ContentPane");
        dojo.require("dijit.layout.BorderContainer");
        dojo.require("twms.widget.Dialog");
   	 dojo.require("twms.widget.TitlePane");
   	 function closeTab() {
         top.tabManager._closeAndCleanUpTab(getTabHavingId(getTabDetailsForIframe().tabId));
     }
    </script>
</head>
<u:body>

	<div dojoType="twms.widget.TitlePane"
				title="<s:text name="label.inventory.NEMISComments" />"
				labelNodeClass="section_header">
			<div style="width:100%; padding-bottom:10px;">
			<span class="labelStyle">
			<s:text name="columnTitle.common.machineSerialNo"/>: &nbsp
			</span>
			<u:openTab cssClass="link" url="inventoryDetail.action?id=%{inventoryItem.id}"
		        			id="invItem" tabLabel="Serial Number %{inventoryItem.serialNumber}"
	                		autoPickDecendentOf="true">
							<s:property value="inventoryItem.serialNumber" />							
			         	</u:openTab>
			         	<br>
			         	<br>
<table class="grid borderForTable"  cellpadding="0" cellspacing="0">
<thead>
<tr class="row_head">
		    <th width="10%"><s:text name="label.inventoryComment.commentSequence"/></th>
		    <th width="10%"><s:text name="label.userId"/></th>
		    <th width="10%"><s:text name="label.inventoryComment.dateOfComment"/></th>
		    <th width="40%"><s:text name="label.inventoryComment.comment"/></th>
</tr>

</thead>
<tbody>
<s:iterator value="inventoryItem.inventoryComments">
   			<tr>
				<td><s:text name="sequenceNumber" /></td>
				<td><s:text name="userId" /></td>
				<td><s:text name="dateOfComment" /></td>
				<td><s:text name="comment" /></td>
			</tr>
   		</s:iterator>
</tbody>
		</table>
</div>
		</div>
<div align="center">
		<input type="button"
			onclick="closeTab()" value="Close" class="buttonGeneric"/>
			</div> 
		
</u:body>
</html>
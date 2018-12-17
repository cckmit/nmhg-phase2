
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="t" uri="twms" %>
<%@ taglib prefix="u" uri="/ui-ext" %>
<script type="text/javascript">
dojo.addOnLoad(function(){
		dojo.connect(dojo.byId("closeDialogButton"),"onclick", function(){
				dijit.byId("policy_audits").hide();
		});
});
</script>
</br>
<td>
<s:text name="label.common.serialNumber"/>:
<s:property value="inventoryItem.serialNumber" />
</td>
<jsp:include flush="true" page="/pages/secure/inventory/inventory_warrantycoverages.jsp"/>
		
	

 
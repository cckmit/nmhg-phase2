<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="t" uri="twms"%>
<%@taglib prefix="u" uri="/ui-ext"%>
<html>
<head>
    <meta http-equiv="Context-Type" content="text/html; charset=ISO-8859-1">
    <s:head theme="twms"/>
    <u:stylePicker fileName="common.css"/>
    <u:stylePicker fileName="form.css"/>
    <u:stylePicker fileName="base.css" />
 </head>
  <u:actionResults/>
  
  <script type="text/javascript">
    dojo.require("dijit.layout.LayoutContainer");
</script>
<style>
td {
 padding-bottom:15px;
}
</style>
  
  
  
  
<u:body smudgeAlert="false">
	
	<div dojoType="dijit.layout.LayoutContainer" style="width: 100%; height: 100%;" id="root">	
	<form action="viewQuickSearchOfEquipmentVIN.action" method="POST" >
	<s:hidden name="context" />
	<s:hidden name="inventorySearchCriteria.inventoryType.type" value="RETAIL"/>
	<div style="margin:5px;" class="policy_section_div">
		<table   border="0" cellspacing="0" cellpadding="0" class="grid">
			<tr>
				<td algin="right">
					<s:submit cssClass="buttonGeneric" value="%{getText('button.common.continue')}"  />
				</td>
			</tr>
			 <tr style="height:4px;"><td></td></tr>
		</table>
		</div>
	</form>
	</div>
</u:body>
</html>
<%@ page contentType="text/html"%>
<%@ page pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="t" uri="twms"%>
<%@ taglib prefix="u" uri="/ui-ext"%>
<%@ taglib prefix="authz" uri="authz"%>

<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<s:head theme="twms" />
<title><s:text name="label.uom.mapping"/></title>
<u:stylePicker fileName="yui/reset.css" common="true" />
<u:stylePicker fileName="layout.css" common="true" />
<u:stylePicker fileName="common.css" />
<u:stylePicker fileName="form.css" />
<u:stylePicker fileName="adminPayment.css" />
<u:stylePicker fileName="base.css" />
    <script type="text/javascript">
  	  	dojo.require("dijit.layout.ContentPane");
      	dojo.require("dijit.layout.LayoutContainer"); 
	</script>
</head>
<u:body>
	<div dojoType="dijit.layout.LayoutContainer"
		style="width: 100%; height: 100%; background: white; overflow-y: auto;">
	<div dojoType="dijit.layout.ContentPane" layoutAlign="client">
	<u:actionResults />			
		<div class="policyRegn_section_div">
		<div class="policy_section_heading"><s:text name="label.uom.mapping"/> </div>
		
		<table width="100%" border="0" cellspacing="0" cellpadding="0" class="grid">
				<tr>						
					<td width="20%" nowrap="nowrap" class="labelStyle"><s:text name="label.uom.baseUom"/> : </td>
					<td><s:property value="uomMappings.baseUom" />
					</td>
				</tr>
				<tr>
					<td nowrap="nowrap" class="labelStyle" ><s:text name="label.uom.mappedUom"/> : </td>							
					<td><s:property value="uomMappings.mappedUom" /></td>
				</tr>
				<tr>
					<td nowrap="nowrap" class="labelStyle"><s:text name="label.uom.mappingFraction"/> : </td>							
					<td><s:property value="uomMappings.mappingFraction" /></td>
			    </tr>												  
		</table>
		</div>		
	</div>
	</div>
</u:body>
</html>
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
<%@page pageEncoding="UTF-8"%>
<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="t" uri="twms"%><%@taglib prefix="u" uri="/ui-ext"%>
<%@taglib prefix="authz" uri="authz"%>
<%
	response.setHeader("Pragma", "no-cache");
	response.addHeader("Cache-Control", "must-revalidate");
	response.addHeader("Cache-Control", "no-cache");
	response.addHeader("Cache-Control", "no-store");
	response.setDateHeader("Expires", 0);
%>
<u:stylePicker fileName="yui/reset.css" common="true" />
<u:stylePicker fileName="layout.css" common="true" />
<u:stylePicker fileName="base.css" />
<style type="text/css">
.header {
	height: 30px;
	line-height: 30px;
	font-family: Verdana;
	font-size: 10pt;
	font-weight: bold;
	color: #fff;
	background-color: #a8814b;
}

.ItemsHdr {
	height: 20px;
	line-height: 20px;
	font-family: Verdana;
	font-size: 7.5pt;
	font-weight: bold;
	color: #fff;
	background-color: #bda571;
	padding-left: 5px;
}

.ItemsLabels {
	height: 18px;
	line-height: 18px;
	font-family:Arial, Helvetica, sans-serif;
	font-size:9pt;
	font-weight: normal;
	color: #111111;
	padding-left: 5px;
	cursor: pointer;
}

.addRow {
	margin-top: -14px;
	height: 14px;
	text-align: right;
	padding-right: 17px;
}

.error {
	color: red;
}
</style>
<u:stylePicker fileName="detailDesign.css" />
<u:stylePicker fileName="common.css" />
<u:stylePicker fileName="claimForm.css" />


<script type="text/javascript"> var djConfig = {isDebug: true}; </script>
<script type="text/javascript" src="scripts/vendor/dojo-widget/dojo/dojo.js"></script>
<script type="text/javascript" src="scripts/vendor/dojo-widget/dijit/dijit.js"></script>
<script type="text/javascript" src="firebug/firebug.js"></script>
<script type="text/javascript">
	dojo.require("dijit.InlineEditBox");
	dojo.require("dijit.form.CheckBox");
</script>
<script type="text/javascript">
	function saveHandler(newValue, oldValue,name) {
		dojo.io.bind({
			url:'configvalue.action?id=' + name + "&value=" +  newValue,
			load: function(type, data, evt){			
			var jsonArray = dojo.toJson.evalJson(data);
			var sapnId = 'error_'+name;			
			if(data){
				dojo.byId(sapnId).innerHTML = jsonArray[0];
			}else{
				dojo.byId(sapnId).innerHTML =''; 
			}			
			/* Only error condition needs to be handled*/},
			error: function(type, error) {dojo.debug(error)},
			mimetype: "text/json"
		});
	};
	function cancelCall(){
	};
	function changeCall(newValue,oldValue){
	};		
	dojo.addOnLoad(function(){
		var editable = dojo.byId("editable");
		dojo.connect(editable, 'onSave', saveHandler);
		dojo.connect(editable,'onChange',cancelCall);
		dojo.connect(editable,'onCancel',changeCall);
	});
	
	function handleRadioChange(radioId){	
		for(var i=0;i<2;i++){
			if(dojo.byId('radio'+radioId+''+i).checked){
				dojo.io.bind({
				url:'configvalue.action?id=' + radioId + "&value=" + (dojo.byId('radio'+radioId+''+i).value),
				load: function(type, data, evt){ /* Only error condition needs to be handled*/},
				error: function(type, error) {dojo.debug(error)},
				mimetype: "text/json"
				});
			}
		} 
	}
	
	function handleCheckboxChange(){
		dojo.io.bind({
		url:'configvalue.action?id=' + this.id + "&value=" + (this.checked),
		load: function(type, data, evt){ /* Only error condition needs to be handled*/},
		error: function(type, error) {dojo.debug(error)},
		mimetype: "text/json"
		});
	}
</script>

<table width="100%" border="0" cellspacing="0" cellpadding="0"
	class="header">
	<tr>
		<td width="20%"><s:text name="label.businessUnit.configurations" /></td>
	</tr>
</table>
<br />
<div dojoType="LayoutContainer" layoutAlign="client"
	style="overflow-X: auto; overflow-Y: auto; width: 100%; height: 100%">

<s:iterator value="logicalGroups">

	<div dojoType="ContentPane"  style="width: 99%">
	<div dojoType="TitlePane" label="<s:property value="name"/>"
		labelNodeClass="section_header" id="claimHeaderPane" open="false">
	<table width="100%" border="0" cellspacing="0" cellpadding="0">
		<tr>
			<td width="49%" valign="top">
				<t:configDisplay param="configParam" cssClass="ItemsLabels"></t:configDisplay>
			</td>

		</tr>
	</table>
	</div>
	</div>
</s:iterator></div>
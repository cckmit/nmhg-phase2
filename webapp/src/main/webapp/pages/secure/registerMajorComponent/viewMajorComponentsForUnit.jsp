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

<%@ taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="t" uri="twms"%>
<%@taglib prefix="u" uri="/ui-ext"%>
<%@taglib prefix="authz" uri="authz"%> 	

<%response.setHeader( "Pragma", "no-cache" );
response.addHeader( "Cache-Control", "must-revalidate" );
response.addHeader( "Cache-Control", "no-cache" );
response.addHeader( "Cache-Control", "no-store" );
response.setDateHeader("Expires", 0); %>
<html>
 <head>   
    <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1" />
    <u:stylePicker fileName="yui/reset.css" common="true"/>
    <s:head theme="twms"/>
    <u:stylePicker fileName="common.css"/>
    <u:stylePicker fileName="form.css"/>
    <u:stylePicker fileName="base.css" />
    <u:stylePicker fileName="warrantyForm.css"/> 
    <style>
    	.btnGen{margin:0;}
    </style>      
 </head>
  <script type="text/javascript"> 
     dojo.require("dijit.layout.LayoutContainer");
  </script> 
  <%@include file="/i18N_javascript_vars.jsp"%> 
<u:body>
  
<div dojoType="dijit.layout.LayoutContainer" style="width: 100%; height: 100%; overflow-y:auto;" id="root">
  <div class="policy_section_div">
    <div class="section_header">
      <s:property value="inventoryItem.serialNumber" />-<s:text name="label.majorComponent.componentSerialNos"/>
   </div>
   <table width="98%" class="grid borderForTable" cellpadding="0" cellspacing="0" align="center" style="margin-top:10px;">
	<thead>
		<tr class="row_head">
			<th><s:text name="label.component.sequenceNumber" /></th>
			<th><s:text name="label.component.serialNumber" /></th>
			<th><s:text name="label.majorComponent.componentPartNo" /></th>
			<th><s:text name="columnTitle.common.componentDescription" /></th>
			<th><s:text name="label.majorComponent.componentManufacturer"/></th>
			<th><s:text name="columnTitle.common.componentStatus" /></th>
			<th><s:text name="label.installDate" /></th>
			<th><s:text name="label.viewClaim.history" /></th>
		</tr>
	</thead>
	<tbody>
	<s:if test="inventoryItemCompositon!=null && inventoryItemCompositon.size()>0">
		<s:iterator value="inventoryItemCompositon" status="status">
			<tr>
				<td><s:property value="sequenceNumber" /></td>
				
				<td><s:property value="part.serialNumber" /></td>

				<td><s:property value="%{part.ofType.getBrandItemNumber(part.brandType)}" /></td>
				<td><s:property value="serialTypeDescription" /></td>
				
				<td><s:property value="manufacturer" /></td>
				
				<td><s:property value="status" /></td>

				<td><s:property value="part.serialTypeDescription" /></td>

				<td><s:property value="part.installationDate" /></td>
		
				<td><s:a id="history_%{#status.index}" href="#"><s:text name="label.viewClaim.history"/> </s:a>
				    <script type="text/javascript">
							dojo.addOnLoad(function() {
								dojo.connect(dojo.byId("history_"+<s:property value="%{#status.index}"/>), "onclick", function(event){
									var url;
									url = "showAuditHistoryForMajorComponent.action?inventoryItem="+<s:property value="inventoryItem"/>+"&sequenceNumber="+<s:property value="sequenceNumber"/>;
									var thisTabLabel = getMyTabLabel();
				                    parent.publishEvent("/tab/open", {
									                    label: "Component Audit",
									                    url: url, 
									                    decendentOf: thisTabLabel,
									                    forceNewTab: true
				                                       });
				                });
							});	
                        </script>
                  </td>
			</tr>
		</s:iterator>
	</s:if>
	</tbody>
  </table>
  
    <div>	      
	   <div id="submit" align="center" class="submit_space">
		   <input id="cancel_btn" class="buttonGeneric" type="button" 
		        value="<s:text name='button.common.cancel'/>"
				onclick="javascript:closeTab(getTabHavingLabel(getMyTabLabel()));" />
	    </div>
    </div>
  </div>
</div>

</u:body>
</html>


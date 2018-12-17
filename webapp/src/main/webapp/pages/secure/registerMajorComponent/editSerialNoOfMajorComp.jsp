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
 </head>
  <script type="text/javascript"> 
     dojo.require("dijit.layout.LayoutContainer");
  </script> 
   
<u:body>
 <div dojoType="dijit.layout.LayoutContainer" style="width: 100%; height: 100%; overflow-y:auto;" id="root">
  <div class="policy_section_div">
    <div class="section_header">
       <s:property value="inventoryItemComposition.part.serialNumber" />-<s:text name="label.majorComponent.majorComponent"/>
   </div>
   <u:actionResults/> 
  <s:form action="updateMajorCompFromEditPage" method="POST" theme="twms"> 
  <s:hidden name="majorComponent" value="%{inventoryItemComposition.part.id}"></s:hidden>
  <s:hidden name="inventoryItemComposition" value="%{inventoryItemComposition.id}"></s:hidden>
      <table width="98%" class="grid borderForTable" cellpadding="0" cellspacing="0" align="center" style="margin-top:10px;">
	<thead>
		<tr class="row_head">
			<th><s:text name="columnTitle.common.serialNo" /></th>
			<th><s:text name="label.common.itemNumber" /></th>
			<th><s:text name="columnTitle.common.description" /></th>
			<th><s:text name="label.common.dateInstall" /></th>					
		</tr>
	</thead>
	<tbody>
	   <tr>	     
	      <td><s:textfield name="majorComponent.serialNumber" value="%{inventoryItemComposition.part.serialNumber}" /></td>
		  <td><s:property value="inventoryItemComposition.part.ofType.number" /></td>
		  <td><s:property value="inventoryItemComposition.part.ofType.description" /></td>
		  <td><s:property value="inventoryItemComposition.part.installationDate" /></td>
		 
	  </tr>	
	</tbody>
   </table>   
    <div id="submit" align="center" class="submit_space">
	       <input id="submit_btn" class="buttonGeneric" type="submit"
				value="<s:text name='button.common.save'/>" />
		   <input id="cancel_btn" class="buttonGeneric" type="button" 
		        value="<s:text name='button.common.cancel'/>"
				onclick="javascript:closeTab(getTabHavingLabel(getMyTabLabel()));" />        
	    </div>
 </s:form>	    
  </div>
  </div>	
</u:body>
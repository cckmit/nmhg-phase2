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
  <script type="text/javascript">  	  	
      	dojo.require("dijit.layout.LayoutContainer"); 
  </script>
<body>
<u:actionResults/>
 	<div dojoType="dijit.layout.LayoutContainer" style="width: 100%; height: 100%;" id="root">	
	<form action="viewMajorCompHistoryForQuickSearch.action?action=EQUIPMENT HISTORY" method="POST" >
	<div style="margin:5px;" class="policy_section_div">
	<div  class="section_header">        
       	<s:text name="label.majorComponent.quickMajorComponentSearch"/>
    </div>	
		<table   border="0" cellspacing="0" cellpadding="0" class="grid">
			<tr>
			 	 <td  class="labelStyle" width="1%" nowrap="nowrap">&nbsp;<s:text name="label.majorComponent.componentSerialNo" />:</td>
       	 	 	 <td class="searchLabel">
       	 	 	 	<s:textfield name="serialNumber" id="serialNumber" cssStyle="border:1px solid #AAAAAA"/>
       	 	 	 </td>				
			</tr>					
		</table>
		<div align="center" style="margin:10px 0;">
		  <s:submit cssClass="buttonGeneric" name="button.common.submit" />&nbsp;
		</div>
	</div>
	</form>
	<s:if test="inventoryItemsList!=null && inventoryItemsList.size()>1">
	  <div style="margin:5px;" class="policy_section_div">
		<div  class="section_header">        
	       	<s:text name="title.common.majorComponents"/>
	    </div>		
		<table width="98%" class="grid borderForTable" cellpadding="0" cellspacing="0">
		    <thead>
				<tr class="row_head">
					<th><s:text name="label.component.serialNumber" /></th>
					<th><s:text name="columnTitle.common.serialNo" /></th>
					<th><s:text name="label.common.partNumber" /></th>
					<th><s:text name="columnTitle.common.description" /></th>
					<th><s:text name="label.majorComponent.componentManufacturer"/></th>
					<th><s:text name="label.common.installationDate" /></th>
					<th><s:text name="columnTitle.common.warrantyStartDate" /></th>
					<th><s:text name="columnTitle.common.warrantyEndDate" /></th>
				</tr>
		    </thead>
		    <tbody>
		       <s:iterator value="inventoryItemsList" status="status">
				<tr>	
					<td>
						<u:openTab autoPickDecendentOf="true" id="major_Component_Details[%{id}]" cssClass="link"
	                           tabLabel="Major ComponentInfo %{serialNumber}" forceNewTab="true"
	                           url="majorComponentInventoryDetail.action?id=%{id}" catagory="majorComponents">
	                         <u style="cursor: pointer;">
	                        	<s:property value="serialNumber" />
	                    	</u>
	                    </u:openTab>
					</td>	
					<td><s:property value="%{getInventorySerialNumberForMajorComponent(id)}" /></td>	
					<td><s:property value="ofType.number" /></td>
					<td><s:property value="componentDescription" /></td>
					<td><s:property value="manufacturer" /></td>	
					<td><s:property value="installationDate" /></td>	
					<td><s:property value="wntyStartDate" /></td>					
					<td><s:property value="wntyEndDate" /></td>	
				</tr>
		     </s:iterator>		
		    </tbody>
		  </table>	   
	  </div>
	 </s:if>
	 
	</div>	
</body>
</html>     	
<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="t" uri="twms"%>
<%@taglib prefix="u" uri="/ui-ext"%>
<%response.setHeader( "Pragma", "no-cache" );
response.addHeader( "Cache-Control", "must-revalidate" );
response.addHeader( "Cache-Control", "no-cache" );
response.addHeader( "Cache-Control", "no-store" );
response.setDateHeader("Expires", 0); %>

<html>
	<head>
	    <meta http-equiv="Context-Type" content="text/html; charset=ISO-8859-1">
	    <s:head theme="twms"/>
	    <u:stylePicker fileName="common.css"/>
	    <u:stylePicker fileName="base.css"/>
	     
	</head>
	
	<style type="text/css">
  		.quickSearchDuplicateClass{
      		border:1px solid gray;
  		}
	</style>
	<u:actionResults/>
	
	<u:body>
		<s:form name="redirectDuplicateSerialNos">
			<table  width="100%" border="0" cellspacing="0" cellpadding="0" class="bgColor" colspan="6">
			<tr><div  class="section_header">        
        	  		<s:text name="label.common.quickInventorySearch"/>:<s:property value='%{action}' />
       			</div>	
      		 </tr>
      		 <tr>
				<s:text name="message.inventory.quickSearchDuplicate"/>      		 
      		 </tr>
      		 <tr>
      		 </tr>
				<tr>
					<td  width="20%" align="center" class="quickSearchDuplicateClass">
						<s:text name="label.common.serialNumber" />
					</td>
					<td  width="20%" align="center" class="quickSearchDuplicateClass">
						<s:text name="label.common.modelNumber" />
					</td>
					<td  width="20%" align="center" class="quickSearchDuplicateClass">
						<s:text name="columnTitle.common.itemNumber" />
					</td>
					<td  width="20%" align="center" class="quickSearchDuplicateClass">
						<s:text name="label.common.itemDescription" />
					</td>
	      		</tr>
	      		<s:hidden name="action"></s:hidden>
      		 	<s:iterator value="inventoryItemsList" status="list">
					<tr>
	      		 		<td  width="20%" align="center" class="quickSearchDuplicateClass">
		      		 		<u:openTab cssClass="link"
						url="%{actionName}"
						id="serialLink_%{#list.index}"
						tabLabel="Serial Number %{serialNumber}"
						autoPickDecendentOf="true">
						<s:property value="serialNumber" />
						<script type="text/javascript">
							  
						</script>
					</u:openTab>
			         	</td>
	      		 		<td  width="20%" align="center" class="quickSearchDuplicateClass">
	      		 			<s:property value="ofType.model.name" />
	      		 		</td>
	      		 		<td  width="20%" align="center" class="quickSearchDuplicateClass">
	      		 			<s:property value="ofType.number" />
	      		 		</td>
	      		 		<td  width="20%" align="center" class="quickSearchDuplicateClass">
	      		 			<s:property value="ofType.description" />
	      		 		</td>
	      		 	</tr>
		 		</s:iterator>
      		</table> 
		</s:form>
	</u:body>
</html>
 
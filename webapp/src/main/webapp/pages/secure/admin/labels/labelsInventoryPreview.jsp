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
 <u:actionResults/>
 <u:stylePicker fileName="inboxLikeButton.css"/>
  <script type="text/javascript">  	  	
     
   </script>
   
<u:body smudgeAlert="false">
	 <s:form name="labelSearchForm" id="labelSearchForm" >
	 <s:hidden name="labelType"/>
	 <div class="policy_section_div">
	 <div  class="section_header">        
        	  			<s:text name="label.common.inventoryLabels"/>
       				</div>
	 	<table  width="100%" border="0" cellspacing="0" cellpadding="0" class="grid borderForTable" align="center">
	 	
      		 <tr class="row_head">
      		 	<th>
      		 		<s:text name="columnTitle.common.serialNo"/>
      		 	</th>
      		 	<th>
      		 		<s:text name="label.inventory.inventoryType"/>
      		 	</th>
      		 </tr>
      		 <s:iterator  value="items" status="itr" id="localesItr">
      		<tr>
				<td >
				<u:openTab decendentOf="%{getText('home_jsp.tabs.home')}" cssClass="claims_folder folder"
			         id="equipmentHistory" tagType="a"
			         tabLabel="%{getText('label.inventory.equipmentHistory')}"
			         url="inventoryDetail.action?id=%{id}"
					 catagory="inventory">
		            <s:property value='serialNumber'/>
           		 </u:openTab>
				</td>
				<td class="label">
					<s:property value='type.type'/>
				</td>
				
	        </tr>
	        </s:iterator>
	      </table>
	      </div>
	  </s:form>
</u:body>      
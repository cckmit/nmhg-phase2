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
	 	<table width="100%" border="0" cellspacing="1" cellpadding="0" class="bgColor"  colspan="6">
	 		<tr >
	 			<td colspan="3">
	 				<div  class="section_header">        
        	  			<s:text name="label.common.warehouseLabels"/>
       				</div>
       			</td>		
      		 </tr>
      		 <tr class="admin_table_header">
      		 	<th class="labelStyle">
      		 		<s:text name="label.manageWarehouse.wareHouseName"/>
      		 	</th>
      		 	<th class="labelStyle">
      		 		<s:text name="label.manageWarehouse.businessName"/>
      		 	</th>
      		 </tr>
      		 <s:iterator  value="warehouses" status="itr" id="localesItr">
      		<tr>
				<td class="borderForTable"  >
					<s:property value="location.code"/>
				</td>
				<td class="borderForTable">
					 <s:property value="businessName"/> 
				</td>	
	        </tr>
	        </s:iterator>
	      </table>
	  </s:form>
</u:body>      
<%--
  Created by IntelliJ IDEA.
  User: pradyot.rout
  Date: Apr 29, 2009
  Time: 10:31:03 PM
  To change this template use File | Settings | File Templates.
--%>
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

<u:body>
	 <s:hidden name="labelType"/>
	 	<table  width="100%" border="0" cellspacing="0" cellpadding="0" class="bgColor" colspan="6">
	 		<tr>
	 			<td colspan="3">
	 				<div  class="section_header">
        	  			<s:text name="label.labels.modelLabels"/>
       				</div>
       			</td>
      		 </tr>
      		 <tr class="admin_table_header">
      		 	<th>
      		 		<s:text name="label.common.name"/>
      		 	</th>
      		 	<th>
      		 		<s:text name="label.common.labelVersion"/>
      		 	</th>
      		 </tr>
      		<s:iterator  value="models" status="itr" id="localesItr">
      		<tr>
				<td  class="label">
					<s:property value="name"/>
				</td>
				<td class="label">
					<s:property value="version"/>
				</td>
	        </tr>
	        </s:iterator>
	      </table>
</u:body>
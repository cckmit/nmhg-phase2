<%@ page contentType="text/html" %>
<%@ page pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="t" uri="twms" %>
<%@ taglib prefix="u" uri="/ui-ext" %>
<%@ taglib prefix="authz" uri="authz" %>
<%
    response.setHeader("Pragma", "no-cache");
    response.addHeader("Cache-Control", "must-revalidate");
    response.addHeader("Cache-Control", "no-cache");
    response.addHeader("Cache-Control", "no-store");
    response.setDateHeader("Expires", 0);
%>
<html>
  <head><title>Recovery Claim Offline Debit</title></head>
  	<table width="100%" border="0" cellspacing="0" cellpadding="0">
	      <tr>
	      	<td class="ItemsLabels">
				<img src="image/bullets_newDesign.gif" border="0" align="absmiddle"/>
	      		<u:openTab decendentOf="%{getText('home_jsp.tabs.home')}"
					id="recClaimDebitNoti" tagType="a" 
					tabLabel="Upload Debit Notification"
					url="import_claims.action" catagory="item">
					Upload Debit Notification
				 </u:openTab>
	      	</td>
	      </tr>	
	      <!--<tr>
	      	<td class="ItemsLabels">
	      		<span style="width:35px;">&nbsp;</span>
				
				<img src="image/bullets_newDesign.gif" border="0" align="absmiddle"/>
				<u:openTab decendentOf="%{getText('home_jsp.tabs.home')}"
					id="recClaimDebitExp" tagType="a" 
					tabLabel="Export Ready For Debit Claims"
					url="uploadDebitClaims.action" catagory="item">
					Export Ready For Debit Claims
				 </u:openTab>
	      	</td>
	      </tr>	
	--></table>
</html>
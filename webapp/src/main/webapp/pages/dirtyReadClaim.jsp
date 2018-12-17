<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@taglib prefix="u" uri="/ui-ext"%>
<%response.setHeader( "Pragma", "no-cache" );
response.addHeader( "Cache-Control", "must-revalidate" );
response.addHeader( "Cache-Control", "no-cache" );
response.addHeader( "Cache-Control", "no-store" );
response.setDateHeader("Expires", 0); %>

<html>
<head>
<meta http-equiv="Context-Type" content="text/html; charset=ISO-8859-1"/>
<title><s:text name="title.common.warranty"/></title>
<s:head theme="twms"/>
<u:stylePicker fileName="common.css"/>
<u:stylePicker fileName="detailDesign.css"/>
<u:stylePicker fileName="yui/reset.css" common="true"/>
<u:stylePicker fileName="common.css"/>
<u:stylePicker fileName="claimForm.css"/>
<u:stylePicker fileName="base.css"/>
</head>
<u:body>
<u:actionResults wipeMessages="false"/>
<s:form id="dirtyClaim">
      <div id="submitButton" style="position:relative;left:50%">
           <s:submit cssClass="buttonGeneric" id='claim_reprocess' value="%{getText('button.common.refresh')}" onclick="hideReprocessButton()"> </s:submit>
      </div>
</s:form>

<script type="text/javascript">
     function hideReprocessButton(){
         var frm = document.getElementById('dirtyClaim');
         frm.action="viewQuickClaimSearchDetail.action?claimNumber=<s:property value='task.claim.claimNumber'/>&context='ClaimSearches'";
         frm.submit();
     }
 </script>

 </u:body>
 </html>
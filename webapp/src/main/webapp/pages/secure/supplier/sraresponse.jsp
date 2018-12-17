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
<%@ taglib prefix="s" uri="/struts-tags" %>


<html>
  <head>
    <title>Supplier Recovery Admin Response</title>
    <script type="text/javascript" src="scripts/vendor/dojo-widget/dojo/dojo.js"></script>

    <script type="text/javascript">
      function updateMasterView(id) {
      	var tabDetails = getTabDetailsForIframe();
		var ids = id.split(",");
		setTimeout(function() {parent.hideCompletedRows(tabDetails.tabId, ids);}, 2000)
      }
    </script>
  </head>
  <body onLoad="updateMasterView('<s:property value='id'/>')">
  <p style="font-family: Verdana, Arial, Helvetica, sans-serif;font-size: 7.5pt;">
  	<s:if test="%{transitionTaken == 'Send To Supplier'}">
  		<s:text name="message.sra.response"/>	
  	</s:if>
  	<s:else>
  		<s:text name="message.sra.submit.response"/>
  	</s:else>
	
  </p>
  
  </body>
</html>


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
<%@taglib prefix="t" uri="twms"%><%@taglib prefix="u" uri="/ui-ext"%>
<html>
  <head>
    <u:stylePicker fileName="adminPayment.css"/>
    <s:head theme="twms"/>
  </head>
  <u:body>
    <div class="admin_section_div">
      <div class="admin_section_heading"><s:actionmessage theme="xhtml" /></div>
    </div>
    <script type="text/javascript">
      <s:if test="!actionMessages.isEmpty()">
          dojo.addOnLoad(function() {
		    manageTableRefresh("lrConfigTable");
        });
    </s:if>
    </script>
    
  </u:body>
</html>

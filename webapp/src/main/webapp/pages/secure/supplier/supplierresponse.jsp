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
<%@taglib prefix="t" uri="twms"%><%@taglib prefix="u" uri="/ui-ext"%>


<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title><s:text name="label.supplier.supplierResponse"/></title>
    <s:head theme="twms" />
    <script type="text/javascript">
      dojo.addOnLoad ( function() {
    	  if(_getParentIFrame()){ // this method should be called if the parent tab is a summarytable only and hence kept a check to verify if parent frame exists
  	      	manageRowHide("supplyRecoveryInboxTable", '<s:property value="id"/>');
    	  }
      });
    </script>
  </head>
  <u:body>
  	<u:actionResults/>
  </u:body>
</html>


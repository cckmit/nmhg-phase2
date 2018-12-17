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
    			<script type="text/javascript">
			      dojo.addOnLoad(function(){
					  var params = "#&serialNo=" + "<s:property value="focbean.serialNumber"/>" +"&orderNo=" + "<s:property value="focbean.orderNo" />" +"&requestType=" +"<s:property value="requestType"/>" + "&status=" + "<s:property value="status"/>";

					  var parentUrl = "<s:property value="ploc"/>";

					  console.debug(parentUrl + params);

					  parent.location = parentUrl + params;
			        });
			</script>
  </head>
  <u:body>
    	<div id="waitMsg" style="visibility:visible">
			<h2>Please Wait, Transaction is in progress...</h2>
		</div>
  </u:body>
</html>

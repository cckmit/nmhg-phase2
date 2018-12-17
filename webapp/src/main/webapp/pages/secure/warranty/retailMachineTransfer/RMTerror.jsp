<%@ taglib prefix="u" uri="/ui-ext" %>
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
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<script type="text/javascript">
dojo.addOnLoad(function(){
	var validationFailed = '<s:property value="hasValidationFailed"/>';
	if(!validationFailed){
		dojo.html.show(dojo.byId("selectedInventoriesTag"));
	}else{
		dojo.html.hide(dojo.byId("selectedInventoriesTag"));
		dojo.html.show(dojo.byId("errorSection"));		
	}
});
</script>
<u:actionResults />
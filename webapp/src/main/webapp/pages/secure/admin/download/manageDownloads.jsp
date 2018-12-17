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
<%--
@author: Jhulfikar Ali
--%>

<%@taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="u" uri="/ui-ext" %>
<%@ taglib prefix="authz" uri="authz" %>

<%@ include file="/i18N_javascript_vars.jsp" %>
<script type="text/javascript" src="scripts/vendor/dojo-widget/dojo/dojo.js"></script>
<u:stylePicker fileName="base.css"/>
<u:stylePicker fileName="adminPayment.css"/>
<u:stylePicker fileName="common.css"/>
<form name="baseForm" id="baseForm">

<div class="policy_section_div" style="width:100%">
	       <div class="section_header">
    		    <s:text name="label.downloadMgt.manageDownloads"/>
	        </div>
    <table  class="grid" style="width:100%" cellspacing="0" cellpadding="0">
       
      <tr>
      	<td class="errorMessage"><u:actionResults /></td>
      </tr>
      
      
      <tr>
    	<td class="labelBold" style="padding-left:3px;">&nbsp;<s:text name="label.downloadMgt.selectDownloadAndProceed" />
      	</td>
      </tr>
      
      <tr>
      	<td class="labelBold">
      		<s:select name="downloadContext" list="downloadList" listKey="key" listValue="value" emptyOption="false" theme="twms" />
      	</td>
      </tr>
 
    </table>
    
</div>

    	<div align="center" class="spacingAtTop">
    		&nbsp;<s:submit cssClass="buttonGeneric" value="%{getText('button.common.submit')}" type="input" action="downloadData" id="submitButton"/>
    	</div>

</form>
<authz:ifPermitted resource="reportsDownloadManagementReadOnlyView">
	<script type="text/javascript">
	    dojo.addOnLoad(function() {
	        for ( var i = 0; i < dojo.query("input, button, textarea, select, text", dojo.byId('baseForm')).length; i++) {
	            dojo.query("input, button, textarea, select, text", dojo.byId('baseForm'))[i].disabled=true;
	        }
	    });
	</script>
</authz:ifPermitted>
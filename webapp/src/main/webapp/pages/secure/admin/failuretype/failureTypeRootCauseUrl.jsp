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

<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="u" uri="/ui-ext" %>
<%@ include file="/i18N_javascript_vars.jsp" %>
<%@ taglib prefix="authz" uri="authz" %>
<script type="text/javascript" src="scripts/vendor/dojo-widget/dojo/dojo.js"></script>
<u:stylePicker fileName="base.css"/>
<u:stylePicker fileName="common.css"/>
<u:stylePicker fileName="adminPayment.css"/>

<form name="baseForm" id="baseForm">
    
    <div class="policy_section_div">
        <div class="section_header">
        		    <s:text name="title.rootCauseFailure.mangeRootCauseFailure"/>
		        </div>
		        <div class="mainTitle">
      		    <s:text name="label.rootCauseFailure.rootCauseFailure"/>
		        </div>
		        <div class="borderTable">&nbsp;</div> 
        <div style="margin-top:-10px;">
        <table  cellspacing="0" cellpadding="0" class="grid" style="width:100%">
           <tr class="errorMessage"><td colspan="2"><u:actionResults /></td></tr>
         <s:hidden name="model.id" />
         <s:hidden name="view" />
           <tr width="100%">
			   <td class="labelBold"><s:text name="label.rootCauseFailure.product"/>:</td>
		       <td class="label"><s:property value="model.isPartOf.name"/></td>
		   </tr>
	    
		   <tr>
			   <td class="labelBold"><s:text name="label.rootCauseFailure.model"/>:</td>
		       <td class="label"><s:property value="model.name"/></td>
	       </tr>
	      
		   <tr>
			   <td class="labelBold"><s:text name="label.rootCauseFailure.url"/>:</td>
		       <td class="label">
			       <s:if test="isDetailView()"><s:textfield id="urlElement" name="model.machineUrl" />&nbsp;
			       	<s:text name="label.rootCauseFailure.urlNote" />
			       	<script>
				       	dojo.addOnLoad( function() {
				       		var machineUrlElement = dojo.byId("urlElement");
				       		if (machineUrlElement.value=='')
				       			machineUrlElement.value = "http://";
				       	});
			       	</script>
			       </s:if>
			       <s:else><s:property value="model.machineUrl"/>
			       </s:else>
		       </td>
		   </tr>
	      
		  
		</table>
		</div>
	</div>
	 <div align="center" class="spacingAtTop">
		     <s:submit align="middle" cssClass="buttonGeneric" value="%{getText('button.common.save')}" action="update_model_url"></s:submit>
	</div>
<authz:ifPermitted resource="warrantyAdminMaintainFailureDetailofFailureURLReadOnlyView">
	<script type="text/javascript">
	    dojo.addOnLoad(function() {
	        for ( var i = 0; i < dojo.query("input, button, textarea, select", dojo.byId('baseForm')).length; i++) {
	            dojo.query("input, button, textarea, select", dojo.byId('baseForm'))[i].disabled=true;
	        }
	    });
	</script>
</authz:ifPermitted>
</form>

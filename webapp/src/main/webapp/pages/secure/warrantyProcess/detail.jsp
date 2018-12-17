<%--
  Created by IntelliJ IDEA.
  User: pradyot.rout
  Date: Sep 1, 2008
  Time: 4:39:01 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html" %>
<%@ page pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>	
<%@ taglib prefix="t" uri="twms" %>
<%@ taglib prefix="u" uri="/ui-ext" %>		
<%@ taglib prefix="authz" uri="authz" %>

<div id="baseDiv">
<s:if test="warranty.status.status=='Deleted' || warranty.status.status=='Forwarded' || warrantyTaskInstance.status.status=='Deleted'">
    <s:if test="%{warranty.filedBy.id.longValue()==getLoggedInUser().getId().longValue() ||
               warranty.forDealer.id.longValue()==getLoggedInUser().getBelongsToOrganization().getId().longValue()}">
        <jsp:include flush="true" page="../warrantyProcess/common/read/read_detail.jsp"/>
    </s:if>
</s:if>
<s:else>
    <jsp:include flush="true" page="../warrantyProcess/common/write/write_detail.jsp"/>
</s:else>
</div>
<authz:ifPermitted resource="dRApproval/TransferPendingforApprovalReadOnlyView">
	<script type="text/javascript">
	    dojo.addOnLoad(function() {
	        for ( var i = 0; i < dojo.query("input, button, textarea, select, text", dojo.byId('baseDiv')).length; i++) {
	            dojo.query("input, button, textarea, select, text", dojo.byId('baseDiv'))[i].disabled=true;
	        }
	    });
	</script>
</authz:ifPermitted>
<authz:ifPermitted resource="dRApproval/TransferRepliesReadOnlyView">
	<script type="text/javascript">
	    dojo.addOnLoad(function() {
	        for ( var i = 0; i < dojo.query("input, button, textarea, select, text", dojo.byId('baseDiv')).length; i++) {
	            dojo.query("input, button, textarea, select, text", dojo.byId('baseDiv'))[i].disabled=true;
	        }
	    });
	</script>
</authz:ifPermitted>
<authz:ifPermitted resource="dRApproval/TransferResubmittedReadOnlyView">
	<script type="text/javascript">
	    dojo.addOnLoad(function() {
	        for ( var i = 0; i < dojo.query("input, button, textarea, select, text", dojo.byId('baseDiv')).length; i++) {
	            dojo.query("input, button, textarea, select, text", dojo.byId('baseDiv'))[i].disabled=true;
	        }
	    });
	</script>
</authz:ifPermitted>
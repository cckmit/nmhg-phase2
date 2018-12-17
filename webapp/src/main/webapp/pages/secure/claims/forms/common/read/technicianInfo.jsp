<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="t" uri="twms" %>
<%@ taglib prefix="u" uri="/ui-ext" %>
<%@taglib prefix="authz" uri="authz"%>

<script type="text/javascript">
	dojo.addOnLoad(function(){
		dojo.connect(dojo.byId("show_technician_details"), "onclick", function() {
			var params = {};
	        twms.ajax.fireHtmlRequest("show_technician_information.action?dealerUserId=<s:property value="%{claim.serviceInformation.serviceDetail.technician.id}" />",
	                params, function(details) {
	        	dijit.byId("technicianDetails").show();
	            if (details) {
	            	dojo.byId("technician_info").innerHTML="";
	                dojo.byId("technician_info").innerHTML = details;
	            }
	        });
		}); 
	});
</script>

	<div id="technicianDetails" dojoType="twms.widget.Dialog" title="<s:text name="Technician Information"/>" 
			bgColor="#FFF" bgOpacity="0.5" toggle="fade" toggleDuration="250" style="width:70%;">
    		<div dojoType="dijit.layout.ContentPane" layoutAlign="top">            
    			<div id="technician_info"></div>
    		</div>
	</div>

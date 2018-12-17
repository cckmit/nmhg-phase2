<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="t" uri="twms"%>
<%@taglib prefix="u" uri="/ui-ext"%>

<script type="text/javascript">
    dojo.require("dojox.layout.ContentPane");
   	dojo.require("dijit.layout.ContentPane");   
	dojo.require("dijit.layout.LayoutContainer");
	dojo.require("twms.widget.Dialog"); 
</script>

<div dojoType="twms.widget.Dialog" id="validations" bgColor="white"
	style="width: 40%; height: 40%;"
	title="<s:text name="button.common.validate"/>">
	<div dojoType="dijit.layout.LayoutContainer"
		style="height: 60px; overflow-y: auto;">
		<div dojoType="dojox.layout.ContentPane" layoutAlign="client"
			id="validations_data" executeScripts="true" renderStyles="true">
		</div>
	</div>
<input id="reopen" class="buttonGeneric" type="button"
				value="<s:text name='button.commom.ok'/>" /> <input id="cancel_btn"
				class="buttonGeneric" type="button"
				value="<s:text name='button.common.cancel'/>"
				onclick="javascript:closeTab(getTabHavingLabel(getMyTabLabel()));" />

	<script type="text/javascript">
dojo.addOnLoad(function(){
	var reOpen = dojo.byId("reopen");
	dojo.connect(reOpen,"onclick",function(){
		var frm=document.getElementById('processorReopen');
		frm.action="claim_reopen_detail.action?claimId=<s:property value="id"/>";
		frm.submit(); 
	});
});
</script>
</div>

<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="t" uri="twms"%>
<%@taglib prefix="u" uri="/ui-ext"%>
<html>
<head>
    <meta http-equiv="Context-Type" content="text/html; charset=ISO-8859-1">    
    <s:head theme="twms"/>
    <u:stylePicker fileName="common.css"/>
    <u:stylePicker fileName="form.css"/>
    <u:stylePicker fileName="base.css"/>
    <u:stylePicker fileName="warrantyForm.css"/>    

</head>
<u:body >
<script type="text/javascript">
		dojo.require("dijit.layout.ContentPane");
        dojo.require("dijit.layout.LayoutContainer");
		dojo.require("dojox.layout.ContentPane");
		var dlg;
		dojo.addOnLoad(function(){			
			dojo.connect(dojo.byId("searchButton"),"onclick", function(){
				 var id=document.getElementById("domainPredicateId").value;
				 var url = "detail_view_search_expression.action";
				 url += "?id=" + id;
				 var contextName=document.getElementById("contextName").value;
				 url += "&context="+contextName;
				 var savedQueryId=document.getElementById("savedQueryId").value;
				 url += "&savedQueryId="+savedQueryId;
				 var selectedBusinessUnit=document.getElementById("selectedBusinessUnit").value;
				 url += "&selectedBusinessUnit="+selectedBusinessUnit;
				 var isTransferOrReProcess=document.getElementById("isTransferOrReProcess").value;
				 url += "&isTransferOrReProcess="+isTransferOrReProcess;		 
				 parent.publishEvent("/tab/reload", {
				 	label: "Multi Claim Maintenance",
					url: url,
					decendentOf:"Home",
				 	tab: getTabHavingId(getTabDetailsForIframe().tabId)});		
			});
			
	});
	function getMatchingClaims(index) {
		var params = {
		      "isTransferOrReProcess" : document.getElementById("isTransferOrReProcess").value,
		      "context" : document.getElementById("contextName").value,
		      "domainPredicateId" : document.getElementById("domainPredicateId").value,
		      "selectedBusinessUnit" : document.getElementById("selectedBusinessUnit").value,
		      "savedQueryId" : document.getElementById("savedQueryId").value,
		      "selectedClaimsId":selectedClaimsId
		};
		if(index){
	       params.pageNo=index;
        }
		var claimSearchResultDiv = dijit.byId("multiTransferReProcessContent");
	    claimSearchResultDiv.setContent("<div class='loadingLidThrobber'><div class='loadingLidThrobberContent'></div></div>");
		twms.ajax.fireHtmlRequest("multiTransferOrReProcessClaims.action",params,function(data) {
				claimSearchResultDiv.destroyDescendants();
				claimSearchResultDiv.setContent(data);
				delete data, claimSearchResultDiv;
			});
	
		delete params;
	} 	   
	</script>
<div dojoType="dijit.layout.LayoutContainer" style="width: 100%; height: 100%; overflow-x:auto;overflow-y:auto;" id="root">
<s:form method="post" theme="twms" id="multipleClaimMaintenance" name="multipleClaimMaintenanceForm">
<div dojoType="dijit.layout.ContentPane" layoutAlign="client"
		style="height:100%;width:100%;">
<u:actionResults/>
<div class="policy_section_div">
<div class="querySearchButton alinkclickable" style="padding:15px;">
<span id="searchButton" class="alinkclickable" style="vertical-align:middle"> <s:text name="button.viewClaim.showSearchQuery" /> </span>
</div>

<div class="section_header">
<s:text name="title.attributes.queryResults" />
</div>
<div id="multiTransferReProcessContent" dojoType="dojox.layout.ContentPane" executeScripts="true">
 <jsp:include page="multiTransferReProcessClaimResults.jsp" flush="true"/>
</div>
</div>
<s:if test="claims.size > 0">
	
<div class="policy_section_div">		
	<div class="section_header">
		<s:text name="title.viewClaim.actions"/>
	</div>
	
	<jsp:include page="multiClaimActions.jsp" flush="true"/>
	</div>
	
</s:if>
</div>
</s:form>
</div>
</u:body>


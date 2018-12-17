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
    <title><s:text name="title.newClaim.campaignClaim"/></title>
    <u:stylePicker fileName="yui/reset.css" common="true"/>
    <s:head theme="twms"/>
    <u:stylePicker fileName="common.css"/>
    <u:stylePicker fileName="form.css"/>
    <u:stylePicker fileName="claimForm.css"/>
    <script type="text/javascript">
      dojo.require("dijit.layout.ContentPane");
    </script>


    <script type="text/javascript" src="scripts/ClaimForm.js"></script>
    <script type="text/javascript" src="scripts/thirdParty/thirdPartySearch.js"></script>
    <script type="text/javascript">
        dojo.addOnLoad(function() {
            top.publishEvent("/refresh/folderCount")
        });
//        dojo.require("twms.widget.Select");
    </script>

    <style type="text/css">
        table.form {
  			margin-bottom: 5px;
            border: 1px solid #EFEBF7;
			background-color:#F3FBFE;
			margin-left:5px;
			width:99%;        }

        label {
            color: #000000;
            font-weight: 400;
        }
    </style>
</head>
	
<u:body>


	<u:actionResults />
	<div dojoType="dijit.layout.LayoutContainer" style="width: 100%; height: 100%; overflow:auto" >
	<div dojoType="dijit.layout.ContentPane" layoutAlign="client">

	<h3><s:text name="label.viewClaim.page1of2"/></h3>
	<s:form method="post" theme="twms" validate="true" id="form"
		name="saveMultiCampaignClaimDraft" action="saveMultiCampaignClaimDraft.action">
		<s:hidden name="claim.type.type" value="Campaign"/>
		<s:if test = "selectedBusinessUnit == null || selectedBusinessUnit.trim().length() == 0">
			<s:hidden name="selectedBusinessUnit" id="selectedBusinessUnit" value = "%{currentBusinessUnit.name}"/>
		</s:if>
		<s:else>
			<s:hidden name="selectedBusinessUnit" id="selectedBusinessUnit"/>
		</s:else>	
		<s:hidden name="claim.filedBy" value="%{loggedInUsersDealership}"/>
		<s:hidden name="dealer" value="%{loggedInUsersDealership}"/>
        <s:hidden name="claim.forDealer" value="%{claim.forDealer.id}" id="dealer"/>
		<s:hidden name="claimType" value="Campaign" />
		<s:hidden name="thirdPartyType" value="true" />
		<table class="form" cellpadding="0" cellspacing="0">
        	<tr>
	            <td width="22%">
	                <s:text name="label.claim.thirdParty"/>:
	            </td>
	            <td width="34%">
	               	<div id="selectedThirdPartyDisplayName"/>
	               		<s:property value="claim.forDealer.name"/>
	               	</div>
	              	<a id="thirdPartyAddLinkId" class="link" >
		            	<s:text name="label.viewClaim.thirdPartyAddLink"/>
					</a>
	           		<script type="text/javascript">
		                dojo.addOnLoad(function() {                	
		                    var form1 = dojo.byId("campaignThirdPartyForm");
		                    dojo.connect(dijit.byId("type"),"onChange",function(){
		                    	var claimType=dijit.byId("type").getValue();                    	
		                    	if( claimType == "Machine" ){
		                    		form1.action = "chooseThirdPartyClaimType.action";                             
		                            form1.submit();
		                    	}
		                    });
		                });
					</script>
	            </td>
				<td width="15%"><label for="type"><s:text name="label.common.claimType"/>:</label></td>
				<td>
				<s:select id="type" name="claimType" list="claimTypes" cssStyle="width:145px;"
					listKey="type" listValue="%{getText(displayType)}" disabled="true" />
                </td>
			</tr>
		</table>
		 <script type="text/javascript">
             dojo.connect(dojo.byId("thirdPartyAddLinkId"), "onclick",
            	function() {
            		dojo.publish("/thirdPartySearch/show");
				});
            
    	 </script>
		<jsp:include flush="true"
			page="../forms/common/write/multi_campaign_header.jsp" />

		<table align="center" border="0" cellpadding="0" cellspacing="0"
			class="buttons">
			<tbody>
				<tr>
                <td align="center">
                    <s:submit value="%{getText('button.common.continue')}" />
                </td>
            </tr>
			</tbody>
		</table>
		<s:hidden name="pageOne" value="true" />
	</s:form></div>
	<jsp:include flush="true" page="../forms/common/write/multiCarSearch.jsp"/>
	<jsp:include flush="true" page="../forms/common/write/thirdPartySearchPage.jsp"/>
</u:body>
</html>

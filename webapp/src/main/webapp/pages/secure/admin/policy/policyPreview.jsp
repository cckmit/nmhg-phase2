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
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="t" uri="twms"%>
<%@taglib prefix="u" uri="/ui-ext"%>


<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Context-Type" content="text/html; charset=ISO-8859-1"/>
    <title><s:text name="title.common.warranty"/></title>
    <s:head theme="twms"/>
    <script type="text/javascript" src="scripts/RuleSearch.js"></script>
    <script type="text/javascript">
       //start of changes as part of dojo migrations
       dojo.require("dijit.layout.LayoutContainer");
       dojo.require("dijit.layout.ContentPane");
       dojo.require("dijit.layout.TabContainer");
       dojo.require("twms.widget.Dialog"); 
       //end of  changes as part of dojo migrations
        <s:if test="!actionMessages.isEmpty()">
        dojo.addOnLoad(function() {
            manageTableRefresh("policyConfigTable");
        });
        </s:if>
    </script>
    <u:stylePicker fileName="base.css"/>
    <u:stylePicker fileName="layout.css" common="true"/>
    <u:stylePicker fileName="adminPayment.css"/>
    <style type="text/css">

        html, body {
            background: #FFF;
            font-family: Verdana, Arial, Helvetica, sans-serif;
            font-size: 8pt;
            height: 100%;
            width: 100%;
            padding: 0;
            margin: 0;
            overflow: hidden;
        }

        .preview_policy_tab {
            margin-right: 1px;
            background: #FFF;
        }

        .bodyText {
            color: #000000;

        }

        .bodyImportantText {
            color: #555555;
        }

        .tabesets {
            border-right: #EFEBF7 1px solid;
            border-top: #BBBBBB 1px solid; /*BORDER-LEFT: #FFFFFF 1px solid;*/
            border-bottom: #BBBBBB 1px solid;
            padding-left: 2px;
            padding-right: 2px;
            color: #636363;
            background-color: #F3FBFE;
        }

        .tabesets_Databottom {
            border-right: #EFEBF7 1px solid; /* BORDER-LEFT: #FFFFFF 1px solid; */
            border-bottom: #BBBBBB 1px solid;
            padding-left: 2px;
            padding-right: 2px;
            color: #000000;
            background-color: #ffffff;
        }

        .tabesetsSec {
            border-right: #EFEBF7 1px solid;
            border-top: #BBBBBB 1px solid; /*BORDER-LEFT: #FFFFFF 1px solid;*/
            border-bottom: #BBBBBB 1px solid;
            padding-left: 2px;
            padding-right: 2px;
            color: #636363;
            background-color: #F3FBFE;
        }
    </style>
</head>
<u:body>
<s:form action="update_policy_preview" method="post" validate="true" cssClass="maxSize">
<div dojoType="dijit.layout.LayoutContainer" class="maxSize">
	<div dojoType="dijit.layout.ContentPane" layoutAlign="top">
		<s:if test="%{policyDefinition.isInActive()}">
			<u:actionResults />
		</s:if>
		<s:else>
			<u:actionResults/>
		</s:else>		
	</div>
	<div class="preview_policy_tab" dojoType="dijit.layout.TabContainer" tabPosition="bottom" layoutAlign="client">
		<div dojoType="dijit.layout.ContentPane" title="<s:text name="label.managePolicy.policyDetails"/>"
			style="overflow-y:auto;">
			<jsp:include flush="true" page="policy_details.jsp"/>
			<jsp:include flush="true" page="policy_updatebuttons.jsp"/>
		</div>
		<div dojoType="dijit.layout.ContentPane" title="<s:text name="label.managePolicy.applicabilityTerms"/>"
			style="overflow-y:auto;">
			<jsp:include flush="true" page="applicability_terms.jsp"/>
			<jsp:include flush="true" page="policy_updatebuttons.jsp"/>
		</div>
		<div dojoType="dijit.layout.ContentPane" title="<s:text name="label.managePolicy.registrationTerms"/>"
			style="overflow-y:auto;">
			<jsp:include flush="true" page="policy_registrationTerms.jsp"/>
			<jsp:include flush="true" page="policy_updatebuttons.jsp"/>
		</div>
	</div>
</div>
</s:form>
</u:body>
</html>
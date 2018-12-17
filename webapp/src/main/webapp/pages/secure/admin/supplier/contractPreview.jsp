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
<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="t" uri="twms"%><%@taglib prefix="u" uri="/ui-ext"%>


<html>
<head>
    <meta http-equiv="Context-Type" content="text/html; charset=ISO-8859-1"/>
    <title>:: <s:text name="title.common.warranty" /> ::</title>
    <s:head theme="twms"/>
    <script type="text/javascript">
        dojo.require("dijit.layout.LayoutContainer");
        dojo.require("dijit.layout.TabContainer");
	    dojo.require("dijit.layout.ContentPane");
    </script>

    <u:stylePicker fileName="master.css"/>
    <u:stylePicker fileName="base.css"/>
     <u:stylePicker fileName="common.css"/>
    <u:stylePicker fileName="inventory.css"/>
    <u:stylePicker fileName="detailDesign.css"/>
    <style type="text/css">
        .labelBold {
            color: #545454;
            font-family: Arial, Helvetica, sans-serif;
            font-size: 9pt;
            font-weight: bold;
            font-style: normal;
            vertical-align: middle;
            padding-left: 5px;
            text-align: left;
            line-height: 20px;
            
        }
    </style>

</head>

<u:body>

<div dojoType="dijit.layout.LayoutContainer"
     style="width: 100%; height: 100%; margin: 0; padding: 0; overflow-X: hidden;">

	<div dojoType="dijit.layout.TabContainer" tabPosition="bottom" layoutAlign="client">
			<div dojoType="dijit.layout.ContentPane" title="<s:text name="label.contractAdmin.contractDetails"/>"
				labelNodeClass="label" containerNodeClass="content">
				<jsp:include flush="true" page="contractDetails.jsp"></jsp:include>
			</div>
			<div dojoType="dijit.layout.ContentPane" title="<s:text name="label.contractAdmin.compensationTerms"/>"
				labelNodeClass="label" containerNodeClass="content">
				<jsp:include flush="true" page="compensationCondition.jsp"></jsp:include>
			</div>
			<div dojoType="dijit.layout.ContentPane" title="<s:text name="label.contractAdmin.itemsCovered"/>"
				labelNodeClass="label" containerNodeClass="content">
				<jsp:include flush="true" page="itemsCovered.jsp"></jsp:include>
			</div>
			
	</div>	
</div>
</u:body>
</html>
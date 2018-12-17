<%--
  Created by IntelliJ IDEA.
  User: irdemo
  Date: Mar 24, 2010
  Time: 4:12:01 PM
  To change this template use File | Settings | File Templates.
--%>
<%--
  Created by IntelliJ IDEA.
  User: irdemo
  Date: Mar 24, 2010
  Time: 4:11:41 PM
  To change this template use File | Settings | File Templates.
--%>
<%@page contentType="text/html" %>
<%@page pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@taglib prefix="t" uri="twms" %>
<%@taglib prefix="u" uri="/ui-ext" %>
<%
    response.setHeader("Pragma", "no-cache");
    response.addHeader("Cache-Control", "must-revalidate");
    response.addHeader("Cache-Control", "no-cache");
    response.addHeader("Cache-Control", "no-store");
    response.setDateHeader("Expires", 0);
%>
<html>
<head>
    <s:head theme="twms"/>
    <title><s:text name="title.viewClaimFailureReports"/></title>
    <u:stylePicker fileName="yui/reset.css" common="true"/>
    <u:stylePicker fileName="common.css"/>
    <u:stylePicker fileName="form.css"/>
    <u:stylePicker fileName="claimForm.css"/>
    <u:stylePicker fileName="base.css"/>
    <u:stylePicker fileName="adminPayment.css"/>

    <script type="text/javascript">
        dojo.require("dijit.layout.LayoutContainer");
    dojo.require("dijit.layout.ContentPane");
    dojo.require("dijit.layout.TabContainer");
    </script>

</head>

<u:body>
    <div dojoType="dijit.layout.LayoutContainer">
    <div  dojoType="dijit.layout.TabContainer" tabPosition="bottom" layoutAlign="client">
            <div dojoType="dijit.layout.ContentPane" title="<s:text name="columnTitle.duePartsInspection.causalpart" />">
                <div class="scrollYNotX"><jsp:include page="failureReportForCausalPart.jsp"/></div>
            </div>
            <div dojoType="dijit.layout.ContentPane" title="<s:text name="label.inventory.oemInstalledParts" />">
                <div class="scrollYNotX"><jsp:include page="failureReportForInstalledParts.jsp"/></div>
            </div>
            <div dojoType="dijit.layout.ContentPane" title="<s:text name="label.inventory.oemReplacedParts" />">
               <div class="scrollYNotX"> <jsp:include page="failureReportForReplacedParts.jsp"/></div>
            </div>
        </div>
    </div>
</u:body>
</html>

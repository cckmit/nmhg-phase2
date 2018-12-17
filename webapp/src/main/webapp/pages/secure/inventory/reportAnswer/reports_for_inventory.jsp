<%--
  Created by IntelliJ IDEA.
  User: pradyot.rout
  Date: Jan 7, 2009
  Time: 12:45:55 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html" %>
<%@ page pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="t" uri="twms" %>
<%@ taglib prefix="u" uri="/ui-ext" %>
<html>
<head>
    <title>Custom Report List Page</title>
    <s:head theme="twms"/>
    <u:stylePicker fileName="common.css"/>
    <u:stylePicker fileName="base.css"/>
</head>
<script type="text/javascript">
    dojo.require("dijit.layout.LayoutContainer");
    dojo.require("dojox.layout.ContentPane");
</script>
<u:body>
    <s:form action="display_report" theme="twms" id="reportsForInventory">
        <s:hidden name="reportAnswer.forInventory" value="%{inventoryItem.id}"/>
        <s:hidden name="reportAnswer.customReport" id="customReportId"/>
    <div dojoType="dijit.layout.LayoutContainer" style="width: 100%; height: 100%;" id="root">
        <div dojoType="dijit.layout.ContentPane" layoutAlign="client">
            <div class="policy_section_div">
                <div class="section_header"><s:text name="label.inventory.customReports"/></div>
            <table class="grid" cellspacing="0" cellpadding="0">
            <s:iterator value="customReportList" status="reports">
                <tr><td>
                <a id="report_<s:property value="id"/>" class="link" >
                    <s:property value="name"/>
                </a>
                <script type="text/javascript">
                    dojo.addOnLoad(function() {
                        var reportId = <s:property value="id"/>;
                        dojo.connect(dojo.byId("report_" + reportId), "onclick", function() {
                            dojo.byId("customReportId").value = reportId;
                            dojo.byId("reportsForInventory").submit();
                        });
                    });
                </script>
                </td></tr>
            </s:iterator>
            </table>
                </div>
            </div>
        </div>
    </s:form>
</u:body>
</html>
<%--
  Created by IntelliJ IDEA.
  User: pradyot.rout
  Date: Dec 22, 2008
  Time: 11:54:43 AM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html" %>
<%@ page pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="t" uri="twms" %>
<%@ taglib prefix="u" uri="/ui-ext" %>
<html>
<head>
    <title>Custom Report Detail Page</title>
    <s:head theme="twms"/>
    <u:stylePicker fileName="common.css"/>
    <u:stylePicker fileName="form.css"/>
    <u:stylePicker fileName="claimForm.css"/>
    <u:stylePicker fileName="base.css"/>
</head>

<head><title>Custom Report Preview Page</title></head>
<script type="text/javascript">
    dojo.require("twms.widget.TitlePane");
    dojo.require("dijit.layout.LayoutContainer");
    dojo.require("dojox.layout.ContentPane");
    dojo.require("twms.widget.Dialog");
    var connects = {};
</script>
<u:body>
<div dojoType="dijit.layout.LayoutContainer">
    <div dojoType="dijit.layout.TabContainer" tabPosition="bottom" layoutAlign="client">

            <%-- This content pane paints the name and product types for this customReport --%>
        <div dojoType="dijit.layout.ContentPane" title="<s:text name="label.customReport.ReportDetails"/>"
             class="scrollYNotX" labelNodeClass="section_header" >
            <div class="policy_section_div">
                <div class="mainTitle"><s:text name="label.customReport.ReportDetail"/></div>
                <div class="borderTable">&nbsp;</div>
                <table width="96%" border="0" cellspacing="0" cellpadding="0" class="grid" style="margin-top:-10px;">
                    <tbody>
                        <tr>
                            <td width="20%" class="labelStyle">
                                <s:text name="label.customReport.reportName"/>
                            </td>
                            <td colspan="5">
                                <s:property value="customReport.name"/>svcsfvs
                            </td>
                            <td width="20%" class="labelStyle">
			                    <s:text name="label.lov.reportType"/>
			                </td>
			                <td colspan="5">
				                <s:property value="customReport.reportType.description"/>
			                </td>
                        </tr>
                    </tbody>
                </table>
                <div >
                    <div class="mainTitle">
                        <s:text name="label.common.productTypes"></s:text>
                    </div>
                    <div class="borderTable">&nbsp;</div>
                    <table width="96%" class="grid" border="0" cellspacing="0" cellpadding="0" style="margin-left:5px;margin-top:-10px;">
                        <tbody>
                            <s:iterator value="customReport.forItemGroups" status="itemGroups">
                                <tr>
                                    <s:property value="name"/>
                                </tr>
                            </s:iterator>
                        </tbody>
                    </table>
                </div>
                <div >
                    <div class="mainTitle">
                        <s:text name="label.common.inventoryType"></s:text>
                    </div>
                    <div class="borderTable">&nbsp;</div>
                    <table width="96%" border="0" cellspacing="0" class="grid" cellpadding="0" style="margin-left:5px;margin-top:-10px;">
                        <tbody>
                            <s:iterator value="customReport.forInventoryTypes" status="inventoryType">
                                <tr>
                                    <s:property value="type"/>
                                </tr>
                            </s:iterator>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>


            <%-- This content pane paints section and questions corresponding to each section in different tabs
         for this customReport --%>
        <s:iterator value="customReport.sections" status="section">
        <div dojoType="dijit.layout.ContentPane" title="<s:text name="label.customReport.section"/>:<s:property
                    value="name"/>" class="scrollYNotX">
            <div class="policy_section_div">
                <div class="section_header"><s:text name="label.customReport.section"/>:<s:property
                        value="name"/></div>
                <s:iterator value="questionnaire" status="question">
                        <div class="mainTitle">
                        <s:text name="label.customReport.question"/>:<s:property value="name"/></div>
                        <div class="borderTable">&nbsp;</div>
                            <table width="96%" border="0" cellspacing="0" cellpadding="0" class="grid" style="maring-top:-10px;">
                                <tbody>
                                    <tr>
                                        <td  nowrap="nowrap" class="labelStyle"><s:text name="label.customReport.questionName"/></td>
                                        <td><s:property value="name"/></td>
                                    </tr>
                                    <tr>
                                        <td nowrap="nowrap" class="labelStyle"><s:text name="label.customReport.questionOrder"/></td>
                                        <td><s:property value="order"/></td>
                                        <td nowrap="nowrap" class="labelStyle"><s:text name="label.customReport.mandatory"/></td>
                                        <td><s:property value="mandatory"/></td>
                                    </tr>
                                    <tr>
                                        <td nowrap="nowrap" class="labelStyle"><s:text name="label.customReport.answerType"/></td>
                                        <td><s:property value="answerType"/></td>
                                        <s:if test="answerType!='textbox">
                                            <td nowrap="nowrap" class="labelStyle"><s:text name="label.customReport.noOfOptions"/></td>
                                            <td><s:property value="answerOptions.size"/></td>
                                        </s:if>
                                    </tr>
                                    <s:iterator value="answerOptions" status="options">
                                        <tr>
                                            <td nowrap="nowrap" class="labelStyle"><s:text name="label.customReport.option"/>
                                                <s:property
                                                        value="order"/></td>
                                            <td><s:property value="answerOption"/></td>
                                        </tr>
                                    </s:iterator>
                                    <tr>
                                        <s:if test="defaultAnswer!=null">
                                            <td nowrap="nowrap" class="labelStyle"><s:text name="label.customReport.defaultValue"/></td>
                                            <td><s:property value="defaultAnswer.answerOption"/></td>
                                        </s:if>
                                    </tr>
                                </tbody>
                            </table>
                        </div>
                </s:iterator>
            </div>
            </div>
        </s:iterator>

        </div>
    </div>
    </u:body>
</html>
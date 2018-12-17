<%--
  Created by IntelliJ IDEA.
  User: pradyot.rout
  Date: Dec 22, 2008
  Time: 12:56:00 PM
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
    <u:stylePicker fileName="multiCar.css"/>
    <u:stylePicker fileName="MultipleInventoryPicker.css"/>
    <u:stylePicker fileName="inboxLikeButton.css"/>
</head>
<script type="text/javascript" src="scripts/domainUtility.js"></script>
<script type="text/javascript">
    dojo.require("twms.widget.TitlePane");
    dojo.require("dijit.layout.LayoutContainer");
    dojo.require("dojox.layout.ContentPane");
    dojo.require("twms.widget.Dialog");
    dojo.require("dijit.form.Button");
</script>
<u:body>
<div dojoType="dijit.layout.LayoutContainer" style="width: 100%; height: 100%; overflow-y:auto;" id="root">
    <div dojoType="dijit.layout.ContentPane" layoutAlign="client">
    <table><tr><td width="20%">
    <div class="inboxLikeButtonWrapper" style="float: left">
        <button dojoType="dijit.form.Button" id="i18nButton">
            <div>
                <div class="inboxLikeButtonWithoutPadding">
                <span class="inboxLikeButtonText">
                        <s:text name="label.report.internationalizeReport"/>
                 </span>
                </div>
            </div>
        </button>
    </div>
    </td></tr></table>
    <script type="text/javascript">
        dojo.addOnLoad(function() {
            dijit.byId("i18nButton").onClick = function() {
                var thisTabLabel = getMyTabLabel();
                var url = "internationalize_report.action?customReport=<s:property value='customReport.id'/>" ;
                parent.publishEvent("/tab/open", {label:thisTabLabel,
                    url: url,
                    decendentOf: thisTabLabel,
                    forceNewTab: true });
            }
        });
    </script>
        <div  width="96%">
            <div dojoType="twms.widget.TitlePane" title="<s:text name="label.customReport.ReportDetails"/>"
                 id="name_productType"
                 labelNodeClass="section_header" open="true" >
                <div  style="width:100%;">
                  <!--  <div class="section_header"><s:text name="label.customReport.ReportDetail"/></div>-->
						
                    <table style="width:96%;">
                        <tbody>
                            <tr>
                                <td width="20%" class="labelStyle">
                                    <s:text name="label.customReport.reportName"/>zxxzx
                                </td>
                                <td colspan="4">
                                    <s:property value="customReport.name"/>
                                </td>
                                <td width="20%" class="labelStyle">
				                    <s:text name="label.lov.reportType"/>
				                </td>
				                <td colspan="4">
					                <s:property value="customReport.reportType.description"/>
				                </td>
                            </tr>
                        </tbody>
                    </table>
					
					<div  id="separator"></div>
					
                    <div >
                        <div class="mainTitle">
                            <s:text name="label.customReport.productTypes"></s:text>
                        </div>
                        <div class="borderTable">&nbsp;</div>
						<div style="margin-top:-10px;">
                        <table border="0" cellspacing="0" cellpadding="0" class="grid" style="margin-left:5px;">
                            <tbody>
                                <s:iterator value="customReport.forItemGroups" status="itemGroups">
                                    <tr>
                                        <td calss="lableStyle"><s:property value="name"/></td>                                        
                                    </tr>
                                </s:iterator>
                            </tbody>
                        </table>
						</div>
                    </div>
				<div  id="separator"></div>
	                <div >
	                    <div class="mainTitle">
	                        <s:text name="label.common.inventoryType"></s:text>
	                    </div>
	                    <div class="borderTable">&nbsp;</div>
						<div style="margin-top:-10px;">
	                    <table border="0" cellspacing="0" cellpadding="0" class="grid" style="margin-left:5px;">
	                        <tbody>
	                            <s:iterator value="customReport.forInventoryTypes" status="inventoryType">
	                                <tr>
	                                    <td><s:property value="type"/></td>
	                                </tr>
	                            </s:iterator>
	                        </tbody>
	                    </table>
						</div>
                    </div>
                </div>
            </div>
        </div>
<div  id="separator"></div>
        <s:iterator value="customReport.sections" status="section">
            <div  width="96%">
                <div dojoType="twms.widget.TitlePane" title="<s:text name="label.customReport.section"/>:<s:property
                        value="name"/>"
                     labelNodeClass="section_header" open="true">
                    <s:iterator value="questionnaire" status="question">
                       
						
                            <div class="mainTitle">
                            <s:text name="label.customReport.question"/>:<s:property value="name"/></div>
                            <div class="borderTable">&nbsp;</div>
                                <table  border="0" cellspacing="0" cellpadding="0" style="margin-left:5px;margin-top:-10px;" class="grid">
                                    <tbody>
                                        <tr>
                                            <td nowrap="nowrap" class="labelStyle"><s:text name="label.customReport.questionName"/></td>
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
                          
							
                      
                    </s:iterator>
                </div>
                  </div>
            </s:iterator>
        </div>
    </div>
    </u:body>
</html>
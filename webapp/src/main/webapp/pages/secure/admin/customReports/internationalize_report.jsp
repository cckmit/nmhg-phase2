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
    dojo.require("dojox.layout.ContentPane");
    dojo.require("twms.widget.Dialog");
    dojo.require("dijit.form.Button");
</script>
<u:body>
<s:form action="submit_internationalize_report" method="POST" id="i18nReportForm">
   
        <div >
        
        <div dojoType="twms.widget.TitlePane" title="<s:text name="label.customReport.i18nReportName"/>"
             id="report_name" labelNodeClass="section_header" open="true" >
            <s:hidden name="customReport" value="%{customReport.id}"/>
            
            <table  width="96%" border="0" cellspacing="0" cellpadding="0" class="grid">
                <tbody>
                    <tr>
                        <td width="20%" class="labelStyle"><s:text name="label.customReport.reportName"/></td>
                        <td  style="padding-left:15px;"><s:property value="customReport.name"/></td>
                    </tr>
                   <s:iterator value="locales" status="reportLocaleIter">
                        <tr>
                            <td><s:property value="description"/></td>
                            <td><s:textfield
                                    name="customReport.i18nReportTexts[%{#reportLocaleIter.index}].description"
                                    value="%{customReport.i18nReportTexts[#reportLocaleIter.index].description}"/></td>
                            <s:hidden name="customReport.i18nReportTexts[%{#reportLocaleIter.index}].locale"
                                      value="%{locales[#reportLocaleIter.index].locale}"/>
                        </tr>
                    </s:iterator>
                </tbody>
            </table>
            </div>
       
        <s:iterator value="customReport.sections" status="sectionIter">
        
            <div dojoType="twms.widget.TitlePane"
                 title="<s:text name="label.customReport.i18nReportSectionName"/>"
                 labelNodeClass="section_header" open="true">
                <s:hidden name="customReport.sections[%{#sectionIter.index}]" value="%{id}"/>
                <table width="92%" border="0" cellspacing="0" cellpadding="0" class="grid">
                    <tbody>
                        <tr>
                            <td width="20%" class="labelStyle"><s:text name="label.customReport.sectionName"/></td>
                            <td  style="padding-left:15px;"><s:property value="name"/></td>
                        </tr>
                        <s:iterator value="locales" status="sectionLocaleIter">
                            <tr>
                                <td><s:property value="description"/></td>
                                <td><s:textfield
                                        name="customReport.sections[%{#sectionIter.index}].i18nReportSectionTexts[%{#sectionLocaleIter.index}].description"
                                        value="%{customReport.sections[#sectionIter.index].i18nReportSectionTexts[#sectionLocaleIter.index].description}"/></td>
                                <s:hidden name="customReport.sections[%{#sectionIter.index}].i18nReportSectionTexts[%{#sectionLocaleIter.index}].locale"
                                          value="%{locales[#sectionLocaleIter.index].locale}"/>
                            </tr>
                    </s:iterator>
                    </tbody>
                </table>
            <s:iterator value="questionnaire" status="questionIter">
            <div class="mainTitle">
            <s:text name="label.customReport.i18nReportQuestionName"/></div>
            <div class="borderTable">&nbsp;</div>
                <s:hidden name="customReport.sections[%{#sectionIter.index}].questionnaire[%{#questionIter.index}]" value="%{id}"/>
                <table width="92%" border="0" cellspacing="0" cellpadding="0" class="grid" style="margin-top:-10px;">
                    <tbody>
                        <tr>
                            <td nowrap="nowrap" width="20%" class="labelStyle"><s:text name="label.customReport.questionName"/></td>
                            <td  style="padding-left:15px;"><s:property value="name"/></td>
                        </tr>
                        <s:iterator value="locales" status="quesLocaleIter">
                            <tr>
                                <td><s:property value="description"/></td>
                                <td><s:textfield
                                        name="customReport.sections[%{#sectionIter.index}].questionnaire[%{#questionIter.index}].i18nQuestionTexts[%{#quesLocaleIter.index}].description"
                                        value="%{customReport.sections[#sectionIter.index].questionnaire[#questionIter.index].i18nQuestionTexts[#quesLocaleIter.index].description}"/></td>
                                <s:hidden name="customReport.sections[%{#sectionIter.index}].questionnaire[%{#questionIter.index}].i18nQuestionTexts[%{#quesLocaleIter.index}].locale"
                                          value="%{locales[#quesLocaleIter.index].locale}"/>
                            </tr>
                        </s:iterator>
                    </tbody>
                </table>
            <s:iterator value="answerOptions" status="optionIter">
                <div class="mainTitle">
                <s:text name="label.customReport.i18nReportAnsOptionName"/></div>
                <div class="borderTable">&nbsp;</div>
                    <s:hidden name="customReport.sections[%{#sectionIter.index}].questionnaire[%{#questionIter.index}].answerOptions[%{#optionIter.index}]" value="%{id}"/>
                    <table width="92%" border="0" cellspacing="0" cellpadding="0" class="grid" style="margin-top:-10px;">
                        <tbody>
                            <tr>
                                <td width="20%" nowrap="nowrap" class="labelStyle"><s:text name="label.customReport.option"/><s:property value="order"/></td>
                                <td><s:property value="answerOption"/></td>
                            </tr>
                            <s:iterator value="locales" status="optionLocaleIter">
                                <tr>
                                    <td><s:property value="description"/></td>
                                    <td><s:textfield
                                            name="customReport.sections[%{#sectionIter.index}].questionnaire[%{#questionIter.index}].answerOptions[%{#optionIter.index}].i18nAnswerOptionTexts[%{#optionLocaleIter.index}].description"
                                            value="%{customReport.sections[#sectionIter.index].questionnaire[#questionIter.index].answerOptions[#optionIter.index].i18nAnswerOptionTexts[#optionLocaleIter.index].description}"/></td>
                                    <s:hidden name="customReport.sections[%{#sectionIter.index}].questionnaire[%{#questionIter.index}].answerOptions[%{#optionIter.index}].i18nAnswerOptionTexts[%{#optionLocaleIter.index}].locale"
                                              value="%{locales[#optionLocaleIter.index].locale}"/>
                                </tr>
                            </s:iterator>
                        </tbody>
                    </table>
               
            </s:iterator>
        </s:iterator> 
    </div>
    </s:iterator>
    </div>
    <div align="center" class="spacingAtTop">
                    <input id="cancel_btn" class="buttonGeneric" type="button" value="<s:text name='button.common.cancel'/>"
                                    onclick="javascript:closeTab(getTabHavingLabel(getMyTabLabel()));"/>
                    <input  class="buttonGeneric" type="submit" value="<s:text name='button.common.save'/>"/>
    </div>

</s:form>
</u:body>
</html>
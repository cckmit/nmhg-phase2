<%--
  Created by IntelliJ IDEA.
  User: pradyot.rout
  Date: Dec 18, 2008
  Time: 6:03:39 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html" %>
<%@ page pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="t" uri="twms" %>
<%@ taglib prefix="u" uri="/ui-ext" %><head>
    <title>Create a section Page</title>
    <u:stylePicker fileName="common.css"/>
    <u:stylePicker fileName="form.css"/>
</head>
<script type="text/javascript">
    dojo.require("twms.widget.TitlePane");
    dojo.require("dijit.layout.LayoutContainer");
    dojo.require("dojox.layout.ContentPane");
    dojo.require("dijit.layout.ContentPane");
</script>
<s:form action="update_question" id="update_question_form_readOnly_%{id}">
<s:if test="errorMsgForUpdateQues">
    <u:actionResults/>
</s:if>
<s:set name="sectionId" value="%{id}"/>
<s:hidden name="section" value="%{id}"/>
<s:iterator value="questionnaire" status="questions">
    <s:set name="quesId" value="%{id}"/>
    <s:hidden name="section.questionnaire[%{#questions.index}]" value="%{id}"/>    
<div dojoType="twms.widget.TitlePane" title="<s:text name="label.customReport.question"/>:<s:property value="%{order}"/>"
         labelNodeClass="section_header" open="true">
        <table width="100%" border="0" cellspacing="0" cellpadding="0">
            <tbody>
                <tr>
                    <td><s:text name="label.customReport.questionName"/></td>
                    <td><s:textfield name="section.questionnaire[%{#questions.index}].name" value="%{name}" id="question_name_%{id}"/></td>
                </tr>
                <tr>
                    <td><s:text name="label.customReport.questionOrder"/></td>
                    <td><s:textfield name="section.questionnaire[%{#questions.index}].order" value="%{order}" id="question_order_%{id}"/></td>
                    <td><s:text name="label.customReport.mandatory"/></td>
                    <td align="center">
                        <s:checkbox name="section.questionnaire[%{#questions.index}].mandatory" 
                                    id="question_mandatory_%{id}" value="%{mandatory}"/>
                        <script type="text/javascript">
                            var isMandatory =<s:property value="mandatory"/>;
                            if (isMandatory) {
                                dojo.byId("question_mandatory_"+<s:property value="%{id}"/>).checked = true
                            }
                        </script>
                    </td>
                </tr>
                <tr>
                    <td><s:text name="label.customReport.answerType"/></td>
                    <td>
                        <s:select list="answerTypes" theme="twms" name="section.questionnaire[%{#questions.index}].answerType"
                                  id="selectedAnswerType_%{id}"
                                  value="%{answerType}" disabled="true"/>
                        <script type="text/javascript">
                            dojo.addOnLoad(function() {
                                var quesId =<s:property value="%{id}"/>;
                                dojo.connect(dijit.byId("selectedAnswerType_" + quesId), "onChange", function() {
                                    dojo.byId("question_noOfOptions_" + quesId).disabled =
                                    dijit.byId("selectedAnswerType_" + quesId).getDisplayedValue() == "textbox";
                                });
                            });
                        </script>
                    </td>
                    <td><s:text name="label.customReport.noOfOptions"/></td>
                    <td>
                        <s:textfield name="section.questionnaire[%{#questions.index}].noOfOptions" value="%{answerOptions.size}"
                                     id="question_noOfOptions_%{id}"/>
                    </td>

                </tr>
                <s:iterator value="answerOptions" status="options">
                    <tr>
                        <td><s:text name="label.customReport.option"/> <s:property value="%{order}"/> </td>
                        <td><s:textfield name="section.questionnaire[%{#questions.index}].answerOptions[%{#options.index}].answerOption"
                                         id="question_answer_option_%{#quesId}_%{order}"
                                value="%{answerOption}"/></td>
                        <s:hidden name="section.questionnaire[%{#questions.index}].answerOptions[%{#options.index}]" value="%{id}"/>
                    </tr>
                </s:iterator>
                <tr id="defaultValue_<s:property value="%{id}"/>">
                    <s:if test="defaultAnswer!=null ">
                    <td><s:text name="label.customReport.defaultValue"/></td>
                    <td><s:property value="defaultAnswer.answerOption"/></td>
                    </s:if>
                </tr>
                <tr id="optionList_<s:property value="%{id}"/>">
                    <td><s:text name="label.customReport.defaultAnswer"/></td>
                    <td>
                    <s:iterator value="answerOptions" status="optionList">
                    <input type="radio"
                           name="section.questionnaire[<s:property value="%{#questions.index}"/>].defaultAnswer"
                           value="<s:property value="%{id}"/>"
                           id="defaultAnswer_<s:property value="%{id}"/>"/>
                    <s:property value="%{order}"/>
                    </s:iterator>
                        <script type="text/javascript">
                           <s:if test="defaultAnswer!=null && defaultAnswer.order!=null">
                         //var defaultAnswerId=<s:property value="%{defaultAnswer.id}"/>;
                           var defaultAnswerDigitId=dojo.byId("defaultAnswer_"+<s:property value="%{defaultAnswer.id}"/>);
                           if(defaultAnswerDigitId){                           
                           	defaultAnswerDigitId.checked=true;
                           }
                            </s:if>
                        </script>
                    </td>
                </tr>
            </tbody>
        </table>
        <div align="right">
            <a id="update_question_link_<s:property value="%{id}"/>" class="link">
                <s:text name="label.customReport.updateQuestion"/>
            </a>
            <script type="text/javascript">
               dojo.addOnLoad(function() {
                    var questionId=<s:property value="%{id}"/>;
                    var optionCtr=<s:property value="%{answerOptions.size}"/>;
                   var sectionId = <s:property value="%{#sectionId}"/>;
                    dojo.connect(dojo.byId("update_question_link_"+questionId),"onclick",function(){
                        enableFields(questionId,optionCtr);
                        dojo.html.hide(dojo.byId("update_question_link_"+questionId));
                        dojo.html.hide(dojo.byId("create_question_link_"+sectionId));
                        dojo.html.show(dojo.byId("update_question_"+sectionId));
                        dojo.html.hide(dojo.byId("question_div_" + sectionId));
                        toggleSubmitButtons(true);
                    });
                 });
            </script>
        </div>
    </div>
    <script type="text/javascript">
       dojo.addOnLoad(function() {
            var questionId = '<s:property value="%{id}"/>';
            var optionCounter='<s:property value="%{answerOptions.size}"/>';
            readOnlyFields(questionId,optionCounter);
        });
    </script>
</s:iterator>
    <div align="center" >
        <input type="button"  class="buttonGeneric" value="<s:text name="button.common.continue"/>"
               id="update_question_<s:property value="%{id}"/>"/>
    </div>
</s:form>
<jsp:include page="newCreateQuestion.jsp" flush="true"/>
<script type="text/javascript">
    dojo.addOnLoad(function(){
        var sectionId = <s:property value="%{id}"/>;
        dojo.html.hide(dojo.byId("update_question_"+sectionId));
        dojo.html.show(dojo.byId("create_question_link_"+sectionId ));
        dojo.html.hide(dojo.byId("question_div_" + sectionId));
        dojo.connect(dojo.byId("update_question_"+sectionId), "onclick", function() {
            dijit.byId("waitForRequest").show();
            var addQuestionPane = dijit.byId("add_questions_div_"+sectionId);
            dojo.xhrPost({
                url: "update_question.action",
                form: dojo.byId("update_question_form_readOnly_"+sectionId),
                load:function(data) {
            	addQuestionPane.destroyDescendants();
                    addQuestionPane.setContent(data);
                    dijit.byId("waitForRequest").hide();
                }
            });
        });
    });
</script>
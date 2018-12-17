<%--
  Created by IntelliJ IDEA.
  User: pradyot.rout
  Date: Dec 11, 2008
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
<%--This has a form which saves the question alongwith the answer options and default answer
    right after click on CONTINUE>> button.--%>
<s:form action="update_question" id="update_question_form">
    <%-- This form basically sends the following parameters
             1)sectionId to section variable in action class
             2)All the question ids corresponding to this section
             3All the updated parameters related to the questions--%>
    <s:hidden name="section" value="%{section.id}"/>
    <s:if test="errorMsgForUpdateQues">
        <u:actionResults/>
    </s:if>
    <s:iterator value="section.questionnaire" status="questions">
        
<div dojoType="twms.widget.TitlePane" title="<s:property value="name"/>"
             labelNodeClass="section_header" open="true">
        <%--The different ids present in this page realted to each question are:
            1)Name:"question_name"
            2)Order:"question_order"
            3)Mandatory:"question_mandatory'
            4)AnswerType:"selectedAnswerType_eachQuestionId"
            5)<tr> which includes the answer options:"answerOptions_eachQuestionId"
            6)<tr> which includes the defaultAnswerOptions:"defaultAnswerOption_eachQuestionId"
            7)Id for eachdefaultAnswerOption:"defaultAnswer_eachAnswerId"
            --%>
            <s:hidden name="section.questionnaire[%{#questions.index}]" value="%{id}"/>
            <table width="100%" border="0" cellspacing="0" cellpadding="0">
                <tbody>
                    <tr>
                        <td><s:text name="label.customReport.questionName"/></td>
                        <td><s:textfield  name="section.questionnaire[%{#questions.index}].name"
                                          value="%{name}" id="question_name"/></td>
                    </tr>
                    <tr>
                        <td><s:text name="label.customReport.questionOrder"/></td>
                        <td><s:textfield name="section.questionnaire[%{#questions.index}].order"
                                         value="%{order}" id="question_order"/></td>
                        <td><s:text name="label.customReport.mandatory"/></td>
                        <td align="center">
                            <s:checkbox name="section.questionnaire[%{#questions.index}].mandatory"
                                        id="question_mandatory"/>
                            <script type="text/javascript">
                                var isMandatory =<s:property value="%{mandatory}"/>;
                                if (isMandatory) {
                                    dojo.byId("question_mandatory").checked = true
                                }
                            </script>
                        </td>
                    </tr>
                    <tr>
                        <td><s:text name="label.customReport.answerType"/></td>
                        <td>
                            <s:select list="answerTypes" theme="twms"
                                      name="section.questionnaire[%{#questions.index}].answerType"
                                      id="selectedAnswerType_%{id}"
                                      value="%{answerType}"/>
                            <%-- In case you are wondering where the "_container_" variable is defined, check the
                                documentation of the 'scriptHasHooks' property of dojox.layout.ContentPane. In few
                                words, by setting the 'scriptHasHooks' property to true, the ContentPane would
                                automatically replace instances of the variable name '_container_' with the value of
                                dijit.byId(this.id). In other words, _container_, would be replaced by a reference to
                                the ContentPane into which we are loading this page. --%>
                            <script type="text/javascript">
                                var sectionId = <s:property value="%{section.id}"/>;
                                /*The connect event with the answerType basically checks if the type is textbox type
                                  then disable the noOfOption field and hide answer options/default value if present and
                                  vice versa.
                                  Here the container is added because when this is set to contentPane as a response
                                  the onload is called.Since we cannot call addOnLoad for the dijits added to ContentPane.
                                  Every connect event is attached to the array which is the value for the key in
                                  map connects.The key is the eachSectionId*/
                                var connector = dojo.connect(_container_, "onLoad", function() {
                                    var quesId =<s:property value="%{id}"/>;
                                    dojo.byId("question_noOfOptions_" + quesId).disabled =
                                    dijit.byId("selectedAnswerType_" + quesId).getDisplayedValue() == "textbox";
                                    var connector = dojo.connect(dijit.byId("selectedAnswerType_" + quesId), "onChange", function() {
                                        var noOfOptionsField = dojo.byId("question_noOfOptions_" + quesId);
                                        var disable = dijit.byId("selectedAnswerType_" + quesId).getDisplayedValue() == "textbox";
                                        if (disable) {
                                            noOfOptionsField.disabled = true;
                                            dojo.html.hide(dojo.byId("answerOptions_" + quesId));
                                            dojo.html.hide(dojo.byId("defaultAnswerOption_" + quesId));
                                        } else {
                                            noOfOptionsField.disabled = false;
                                            dojo.html.show(dojo.byId("answerOptions_" + quesId));
                                            dojo.html.show(dojo.byId("defaultAnswerOption_" + quesId));
                                        }
                                    });
                                    connects[sectionId].push(connector);
                                });
                                connects[sectionId].push(connector);
                            </script>
                            
                        </td>
                        <td><s:text name="label.customReport.noOfOptions"/></td>
                        <td>
                            <s:textfield name="section.questionnaire[%{#questions.index}].noOfOptions"
                                         value="%{answerOptions.size}" id="question_noOfOptions_%{id}" disabled="true"/>
                        </td>

                    </tr>
                    <tr id="answerOptions_<s:property value="%{id}"/>">
                        <td colspan="2">
                            <table width="100%">
                                <s:iterator value="answerOptions" status="options">
                                    <tr>
                                        <td align="left"><s:text name="label.customReport.option"/> <s:property
                                                value="%{order}"/>:</td>
                                        <td align="left"><s:textfield
                                                name="section.questionnaire[%{#questions.index}].answerOptions[%{#options.index}].answerOption"
                                                value="%{answerOption}"/></td>
                                    </tr>
                                </s:iterator>
                            </table>
                        </td>
                    </tr>
                    <tr id="defaultAnswerOption_<s:property value="%{id}"/>">
                        <%--Default answer label is displayed only if the selected answer type
                            for this question is not textbox type --%>
                        <s:if test="answerOptions.size>0">
                            <td><s:text name="label.customReport.defaultAnswer"/></td>
                        </s:if>
                        <td>
                        <s:iterator value="answerOptions" status="optionList">
                            <input type="radio"
                                   name="section.questionnaire[<s:property value="%{#questions.index}"/>].defaultAnswer"
                                   value="<s:property value="%{id}"/>"
                                   id="defaultAnswer_<s:property value="%{id}"/>"/>
                            <s:property value="%{order}"/>
                        </s:iterator>
                            <script type="text/javascript">
                                <s:if test="defaultAnswer!=null">                              
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
        </div>
    </s:iterator>
    <div align="center">
        <input type="button" value="Continue" class="buttonGeneric" id="submit_question"/>
    </div>
</s:form>
<script type="text/javascript">
 /*This unLoad is added here only when this page is displayed as a INPUT page.
       unLoad is required as it removes the onLoad events to this ContentPane.*/
        dojo.connect(_container_, "onUnload", function() {
            var sectionId = <s:property value="%{section.id}"/>;
            var subConnects = connects[sectionId];
            var index=subConnects.length;
            while(index--){
            dojo.disconnect(subConnects[index]);
            }

            connects[sectionId]=[]; // clear array
        });
 /* This addOnLoad submits the form and sets the respons to the ContentPnae with id: add_questions_div_reportSectionId*/
        dojo.addOnLoad(function() {
        var sectionId = <s:property value="%{section.id}"/>;
        dojo.connect(dojo.byId("submit_question"), "onclick", function() {
            var addQuestionPane = dijit.byId("add_questions_div_" + sectionId);
            dijit.byId("waitForRequest").show();
            dojo.xhrPost({
                url: "update_question.action",
                form: dojo.byId("update_question_form"),
                load:function(data){
            	addQuestionPane.destroyDescendants();
                    dijit.byId("waitForRequest").hide();
                    addQuestionPane.setContent(data);
                }
            });
        });
    });
</script>


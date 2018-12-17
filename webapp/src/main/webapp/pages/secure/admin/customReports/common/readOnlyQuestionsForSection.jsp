<%--
  Created by IntelliJ IDEA.
  User: pradyot.rout
  Date: Dec 12, 2008
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
    var sectionId = <s:property value="%{section.id}"/>;
    function roEnableFields(quesId,optionSize){
        dojo.html.show(dojo.byId("optionList_"+quesId));
        //dojo.html.hide(dojo.byId("defaultAnswerOption_"+quesId));
        dojo.byId("question_name_"+quesId).readOnly=false;
        dojo.byId("question_order_"+quesId).readOnly=false;
        dojo.byId("question_mandatory_"+quesId).disabled=false;
        dijit.byId("selectedAnswerType_"+quesId).setDisabled(false);
        dojo.byId("question_noOfOptions_"+quesId).readOnly=false;
        for (var i=1;i<=optionSize;i++){
            dojo.byId("question_answer_option_"+quesId+"_"+i).readOnly=false;
        }
    }
    function roReadOnlyFields(quesId,optionSize){
        dojo.html.hide(dojo.byId("optionList_"+quesId));
        dojo.byId("question_name_"+quesId).readOnly=true;
        dojo.byId("question_order_"+quesId).readOnly=true;
        dojo.byId("question_mandatory_"+quesId).disabled=true;
        dijit.byId("selectedAnswerType_"+quesId).setDisabled(true);
        dojo.byId("question_noOfOptions_"+quesId).readOnly=true;
        for (var i=1;i<=optionSize;i++){
            dojo.byId("question_answer_option_"+quesId+"_"+i).readOnly=true;
        }
    }
</script>
<%-- This page has a form which updates the question/answeroptions/default values.--%>
<s:form action="update_question" id="update_question_form_readOnly">
<s:if test="errorMsgForUpdateQues">
    <u:actionResults/>
</s:if>
<%-- The parameters passed in this page are:
     1)Section id whose questions are updated
     2)Corresponding questionIds for this section
     3)Parameters for each question --%>
<s:hidden name="section" value="%{section.id}"/>
<s:iterator value="section.questionnaire" status="questions">
    <s:set name="quesId" value="%{id}"/>
    <s:hidden name="section.questionnaire[%{#questions.index}]" value="%{id}"/>
    
<div dojoType="twms.widget.TitlePane" title="<s:text name="label.customReport.question"/>:<s:property value="%{order}"/>"
         labelNodeClass="section_header" open="true">
        <table width="100%" border="0" cellspacing="0" cellpadding="0">
            <%-- The ids related to each question are:
                 1)Name:"question_name_eachQuestionId"
                 2)Order:"question_order_eachQuestionId"
                 3)Mandatory:"question_mandatory_eachQuestionId"
                 4)AnswerType:"selectedAnswerType_eachQuestionId"
                 5)No of options:"question_noOfOptions_eachQuestionId"
                 6)Answer option <tr>:"answerOptions_eachQuestionId"
                 7)Answeroptions:"question_answer_option_eachQuestionId_answerOrder
                 8)Default answer value <tr>:"defaultAnswerOption_eachQuestionId"
                 9)Default answer options <tr>:"optionList_eachQuestionId"
                 7)Default answer options:"defaultAnshwer_eacAnswerId"
                 --%>
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
                                    id="question_mandatory_%{id}" value="%{mandatory}" fieldValue="%{mandatory}"/>
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
                            /*The connect event with the answerType basically checks if the type is textbox type
                                  then disable the noOfOption field and hide answer options/default value if present and
                                  vice versa.
                                  Here the container is added because when this is set to contentPane as a response
                                  the onload is called.Since we cannot call addOnLoad for the dijits added to ContentPane.
                                  Every connect event is attached to the array which is the value for the key in
                                  map connects.The key is the eachSectionId*/
                            var connector = dojo.connect(_container_, "onLoad", function() {
                                var quesId =<s:property value="%{id}"/>;
                                var connector = dojo.connect(dijit.byId("selectedAnswerType_" + quesId), "onChange", function() {
                                    var noOfOptionsField = dojo.byId("question_noOfOptions_" + quesId);
                                    var disable = dijit.byId("selectedAnswerType_" + quesId).getDisplayedValue() == "textbox";
                                    if (disable) {
                                        noOfOptionsField.disabled = true;
                                        dojo.html.hide(dojo.byId("answerOptions_" + quesId));
                                        dojo.html.hide(dojo.byId("optionList_" + quesId));
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
                        <s:textfield name="section.questionnaire[%{#questions.index}].noOfOptions" value="%{answerOptions.size}"
                                     id="question_noOfOptions_%{id}" />
                    </td>

                </tr>
                <tr id="answerOptions_<s:property value="%{id}"/>">
                    <td colspan="2">
                        <table>
                            <tbody>
                                <s:iterator value="answerOptions" status="options">
                                    <tr>
                                        <td><s:text name="label.customReport.option"/> <s:property
                                                value="%{order}"/></td>
                                        <td><s:textfield
                                                name="section.questionnaire[%{#questions.index}].answerOptions[%{#options.index}].answerOption"
                                                id="question_answer_option_%{#quesId}_%{order}"/></td>
                                    </tr>
                                </s:iterator>
                            </tbody>
                        </table>
                    </td>
                </tr>
                <tr id="defaultAnswerOption_<s:property value="%{id}"/>">
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
                /* This onload event basically connects event to the update question link
                 the update link id : "update_question_link_eachQUestionId"
                 onclick of this link :
                 1)Enable all the fields
                 2)hide update link
                 3)hide add question link
                 4)Display update_question_eachSectionId button (i.e. continue button)
                 5)hide the add question page inside "question_div_eachSectionId"*/
                var connector = dojo.connect(_container_, "onLoad", function() {
                    var questionId=<s:property value="%{id}"/>;
                    var optionCtr=<s:property value="%{answerOptions.size}"/>;
                    var sectionId = <s:property value="%{section.id}"/>;
                    var connector = dojo.connect(dojo.byId("update_question_link_"+questionId),"onclick",function(){
                        roEnableFields(questionId,optionCtr);
                        dojo.html.hide(dojo.byId("update_question_link_"+questionId));
                        dojo.html.hide(dojo.byId("create_question_link_"+sectionId));
                        dojo.html.show(dojo.byId("update_question_"+ sectionId));
                        dojo.html.hide(dojo.byId("question_div_" + sectionId));
                        toggleSubmitButtons(true);
                    });
                    connects[sectionId].push(connector);
                 });
                connects[sectionId].push(connector);
            </script>
        </div>
    </div>
    <script type="text/javascript">
        /*this code basically makes all the field read only at onload of this page*/
        var connector = dojo.connect(_container_, "onLoad", function() {
            var questionId = <s:property value="%{id}"/>;
            var optionCounter=<s:property value="%{answerOptions.size}"/>;
            roReadOnlyFields(questionId,optionCounter);
        });
        connects[sectionId].push(connector);
    </script>
</s:iterator>
    <div align="center">
        <input type="button" value="Continue" class="buttonGeneric" id="update_question_<s:property value="%{section.id}"/>"/>
    </div>
</s:form>
<jsp:include page="createQuestion.jsp" flush="true"/>
<script type="text/javascript">
    dojo.connect(_container_, "onUnload", function() {
            var sectionId = <s:property value="%{section.id}"/>;
            var subConnects = connects[sectionId];
            var index=subConnects.length;
            while(index--){
            dojo.disconnect(subConnects[index]);
            }
            connects[sectionId]=[]; // clear array
        });
    /*This code basically submits this form and attaches th response under "add_questions_div_eachSectionId" div tag*/
    dojo.addOnLoad(function(){
        var sectionId = <s:property value="%{section.id}"/>;
        dojo.html.show(dojo.byId("create_section_link"));
        dojo.html.show(dojo.byId("create_question_link_"+sectionId ));
        dojo.html.hide(dojo.byId("question_div_" + sectionId));
        dojo.html.hide(dojo.byId("update_question_"+sectionId));
        toggleSubmitButtons(false);
        dojo.connect(dojo.byId("update_question_"+sectionId), "onclick", function() {
            dijit.byId("waitForRequest").show();
            var addQuestionPane = dijit.byId("add_questions_div_"+sectionId);
            dojo.xhrPost({
                url: "update_question.action",
                form: dojo.byId("update_question_form_readOnly"),
                load:function(data) {
            	addQuestionPane.destroyDescendants();
                    addQuestionPane.setContent(data);
                    dijit.byId("waitForRequest").hide();
                }
            });
        });
    });
</script>
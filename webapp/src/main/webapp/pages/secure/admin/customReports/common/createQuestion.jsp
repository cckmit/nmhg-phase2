<%--
  Created by IntelliJ IDEA.
  User: pradyot.rout
  Date: Dec 11, 2008
  Time: 3:23:50 PM
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
    dojo.require("twms.widget.Select");
    var sectionId = <s:property value="%{section.id}"/>;
</script>
<%-- This page is same as that of newCreateQuestion.jsp.
     The only difference is that here we have added container on load instead of addOnLoad.
     Since this page is loaded everytime whereas newCreateQuestion.jsp. is loaded only when the detail page is loaded
     for the first time.--%>
<%--This div tag contains the template to create questions for section.
    The div tag id is "question_div_eachSectionId".
    This has a form which saves the question right after click on GO button.--%>

<div id="question_div_<s:property value="%{section.id}"/>">
    <div dojoType="twms.widget.TitlePane"
         title="<s:text name="label.customReport.createQuestion"/>"
         labelNodeClass="section_header" open="true">
        <s:form action="create_question" id="create_question_form_%{section.id}">
        <%-- This check is to display error Messages only if the action is
             corresponding to create question --%>
        <s:if test="errorMsgForCreateQues">
            <u:actionResults/>
        </s:if>
            <%-- This form basically sends the following parameters
             1)sectionId to section variable in action class
             2)all the required parameters for question variable in action class
             The ids for question parameters are
             a)Name:"question_name_eachSectionId"
             b)Order:"question_order_eachSectionId"
             c)Mandatory:"question_mandatory_eachSectionId"
             d)AnswerType:"answerType_eachSectionId"
             e)NoOfOptions:"question_noOfOptions_eachSectionId"--%>
        <s:hidden name="section" value="%{section.id}"/>
        <table width="100%" border="0" cellspacing="0" cellpadding="0">
            <tbody>
                <tr>
                    <td><s:text name="label.customReport.questionName"/></td>
                    <td><s:textfield name="question.name" value="%{question.name}"
                                     id="question_name_%{section.id}"/></td>
                </tr>
                <tr>
                    <td><s:text name="label.customReport.questionOrder"/></td>
                    <td><s:textfield name="question.order" value="%{question.order}"
                                     id="question_order_%{section.id}"/></td>
                    <td><s:text name="label.customReport.mandatory"/></td>
                    <td align="center"><s:checkbox name="question.mandatory" id="question_mandatory_%{section.id}"/></td>
                </tr>
                <tr>
                    <td><s:text name="label.customReport.answerType"/></td>
                    <td>
                        <s:select list="answerTypes" theme="twms" name="question.answerType"
                                  id="answerType_%{section.id}"/>
                        <script type="text/javascript">
                            /* Here the container is added because when this is set to contentPane as a response
                            the onload is called.Since we cannot call addOnLoad for the dijits added to ContentPane.
                            Every connect event is attached to the array which is the value for the key in
                            map connects.The key is the eachSectionId*/
                            var connector = dojo.connect(_container_, "onLoad", function() {
                                var sectionId =<s:property value="%{section.id}"/>;
                                dojo.byId("question_noOfOptions_" + sectionId).disabled =
                                    dijit.byId("answerType_" + sectionId).getDisplayedValue() == "textbox";
                                var connector = dojo.connect(dijit.byId("answerType_" + sectionId), "onChange", function() {
                                    dojo.byId("question_noOfOptions_" + sectionId).disabled =
                                    dijit.byId("answerType_" + sectionId).getDisplayedValue() == "textbox";
                                });
                                connects[sectionId].push(connector);
                            });
                            connects[sectionId].push(connector);
                        </script>
                    </td>
                    <td><s:text name="label.customReport.noOfOptions"/></td>
                    <td>
                        <s:textfield name="question.noOfOptions" value="%{question.noOfOptions}"
                                     id="question_noOfOptions_%{section.id}" />
                    </td>
                </tr>
            </tbody>
        </table>
        <div align="center" >
            <%-- This button basically submits this form and
                  since this page included for each section the id has to be unique.
                  The id for this link is "createRowsForAnswers_eachSectionId"--%>
            <input type="button" value="Go" class="buttonGeneric"
                   id="createRowsForAnswers_<s:property value="%{section.id}"/>"/>
        </div>
    </div>
    </s:form>
</div>
<script type="text/javascript">
     var toBeDisplayed = '<s:property value="%{isErrorMsgForCreateQues()}"/>';
     /*This unLoad is added here only when this page is displayed as a INPUT page.
       unLoad is required as it removes the onLoad events to this ContentPane.*/
        if(toBeDisplayed=='true'){
            dojo.connect(_container_, "onUnload", function() {
            var sectionId = <s:property value="%{section.id}"/>;
            var subConnects = connects[sectionId];
            var index=subConnects.length;
            while(index--){
            dojo.disconnect(subConnects[index]);
            }
            connects[sectionId]=[]; // clear array
        });
        }
    /* This onLoad performs 3 main operations
       1)At the load of this page hide this page.
       2)If there is any error while creating question then :
            a)show this page
            b)hide createQuestion link and createSection link
       3)Onclick of the "create_question_link_reportSectionId" link :
            a)hide the createQuestion link
            b)show this page
            c)hide createSection link and create section page
            d)Save and publishButtons disabled.
            e)Reset all the fields of this page
       4)Onclick of the "createRowsForAnswers_eachSectionId" link :
            a)hide this page
            b)submit this form and add the response to "add_questions_div_reportSectionId"
            c)reset the fields
       All the connects event are added to the map connects
       */
    var connector = dojo.connect(_container_, "onLoad", function() {
        var sectionId = <s:property value="%{section.id}"/>;
        dojo.html.hide(dojo.byId("question_div_" + sectionId));
        var toBeDisplayed = '<s:property value="%{isErrorMsgForCreateQues()}"/>';
        if(toBeDisplayed=='true'){
           dojo.html.show(dojo.byId("question_div_" + sectionId));
           dojo.html.hide(dojo.byId("create_question_link_" + sectionId));
           dojo.html.hide(dojo.byId("create_section_link"));
        }
        var connector = dojo.connect(dojo.byId("create_question_link_" + sectionId), "onclick", function() {
            dojo.html.hide(dojo.byId("create_question_link_" + sectionId));
            dojo.html.show(dojo.byId("question_div_" + sectionId));
            dojo.html.hide(dojo.byId("create_section_link"));
            dojo.html.hide(dojo.byId("create_section_div"));
            dojo.byId("question_name_" + sectionId).value = "";
            dojo.byId("question_order_" + sectionId).value = "";
            dojo.byId("question_mandatory_" + sectionId).checked = false;
            dojo.byId("question_noOfOptions_" + sectionId).value = "";
            toggleSubmitButtons(true);
        });
        connects[sectionId].push(connector);
        var connector = dojo.connect(dojo.byId("createRowsForAnswers_" + sectionId), "onclick", function() {
            var sectionId = <s:property value="%{section.id}"/>;
            dojo.html.hide(dojo.byId("question_div_" + sectionId));
            var addQuestionPane = dijit.byId("add_questions_div_" + sectionId);
            dijit.byId("waitForRequest").show();
            dojo.xhrPost({
                url: "create_question.action",
                form: dojo.byId("create_question_form_" + sectionId),
                load:function(data) {
                    addQuestionPane.setContent(data);
                    dijit.byId("waitForRequest").hide();
                }
            });
            dojo.byId("question_name_" + sectionId).value = "";
            dojo.byId("question_order_" + sectionId).value = "";
            dojo.byId("question_mandatory_" + sectionId).checked = false;
            dojo.byId("question_noOfOptions_" + sectionId).value = "";
        });
        connects[sectionId].push(connector);
    });
    connects[sectionId].push(connector);
</script>
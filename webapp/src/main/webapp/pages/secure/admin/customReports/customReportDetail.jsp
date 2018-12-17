<%--
  Created by IntelliJ IDEA.
  User: pradyot.rout
  Date: Dec 10, 2008
  Time: 3:56:44 PM
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
</head>
<script type="text/javascript">
    dojo.require("twms.widget.TitlePane");
    dojo.require("dijit.layout.LayoutContainer");
    dojo.require("dojox.layout.ContentPane");
    dojo.require("twms.widget.Dialog");
    var connects = {};
</script>
<%--var connects is a map .It stores all the onLoad connect events corresponding to a particular section.
    If there is any eroor related to custom OnLoad just verify the onLoad connect event corresponding to a section--%>
<u:body >
    <div dojoType="dijit.layout.LayoutContainer" style="width: 100%; height: 100%;overflow-y:auto;overflow-x:hidden" id="root">
        <div dojoType="dijit.layout.ContentPane" layoutAlign="client">
            <u:actionResults wipeMessages="true"/>
            <%--This page includes the pop up section for please wait --%>
            <jsp:include page="common/waitForRequest.jsp"/>

            <%--This page includes the report name and product type section.--%>
            <div >
                <div dojoType="twms.widget.TitlePane" title="<s:text name="label.customReport.ReportDetail"/>"
                     id="name_productType"
                     labelNodeClass="section_header" open="true">
                    <jsp:include page="common/nameAndProductType.jsp" flush="true"/>
                </div>
            </div>

            <%--This page includes the create section for custom report
                The has action error checks if there is any error while creating a section
                then it hide the createSection link and displays createSection div tag--%>
            <div >
                <div dojoType="twms.widget.TitlePane" title="<s:text name="label.customReport.createSection"/>"
                     id="create_section"
                     labelNodeClass="section_header" open="true">
                    <jsp:include page="common/createSection.jsp" flush="true"/>
                    <s:if test="%{hasErrors()}">
                        <script type="text/javascript">
                            dojo.addOnLoad(function() {
                                dojo.html.show(dojo.byId("create_section_div"));
                                dojo.html.hide(dojo.byId("create_section_link"));
                            })
                        </script>
                    </s:if>
                    <s:else>
                        <script type="text/javascript">
                            dojo.addOnLoad(function(){
                               dojo.byId("section_name").value="";
                               dojo.byId("section_order").value=""; 
                            });
                        </script>
                    </s:else>
                </div>
            </div>

           <%--This part includes all the sections and corresponding questions for these sections.
               The titlePane id for each section is "report_section_sectionCounter"
               For each section connects array is initialized. The key for connects map object are the
               section Ids.--%>
            <s:iterator value="customReport.sections" status="sections">
                <script type="text/javascript">
                    var eachSectionId=<s:property value="%{id}"/>;
                    var sectionConnects=[];
                    connects[eachSectionId]=[];
                </script>
                <div >
                    <div dojoType="twms.widget.TitlePane" title="<s:text name="label.customReport.section"/>:<s:property value="name"/>"
                         id="report_section_<s:property value="%{#sections.index}"/>"
                         labelNodeClass="section_header" open="true">
                        <div align="right">
                            <s:hidden name="customReport.sections[%{#sections.index}]" value="%{id}"
                                      id="report_section_%{id}"/>

                            <%--This link is for each section to create questions
                                The id's for this link is "create_question_link_reportSectionId"--%>
                            <a id="create_question_link_<s:property value="%{id}"/>" class="link">
                                <s:text name="label.customReport.addQuestion"/>
                            </a>
                        </div>

                        <%--This part has all the questions corresponding to this section.
                            All the question creation/updation/addition are ajax response and
                            are set to this contenPane for each section separately.
                            The id of this section "add_questions_div_reportSectionId"--%>
                        <div dojoType="dojox.layout.ContentPane" layoutAlign="client"
                             scriptHasHooks="true" id="add_questions_div_<s:property value="%{id}"/>"
                             cleanContent="true">
                            <%-- If a section has already some questions then only we include
                                 createdQuestion.jsp or else newCreateQuestion.js is added.
                                 common/newCreateQuestion.jsp is added only once for a section.
                                 Eg.1)Section has no question then it is added.
                                     But after that every time common/createQuestion.jsp is added.
                                    2)Section has some question the common/createdQuestion.jsp is added
                                     and internally it adds newCreateQuestion.jsp--%>
                            <s:if test="questionnaire.size>0">
                                <jsp:include page="common/createdQuestion.jsp"/>
                            </s:if>
                            <s:else>
                                <jsp:include page="common/newCreateQuestion.jsp" flush="true"/>
                            </s:else>
                        </div>
                    </div>
                </div>
            </s:iterator>


    <!--This part has all the buttons corresponding to different actions in this detaill page  -->
    <div align="center">
        <input id="cancel_btn" class="buttonGeneric" type="button" value="<s:text name='button.common.cancel'/>"
               onclick="javascript:closeTab(getTabHavingLabel(getMyTabLabel()));"/>
        <s:if test="customReport==null || (customReport!=null && customReport.id==null)">
        <input  class="buttonGeneric" type="button" id="save_button" value="<s:text name='button.common.save'/>" disabled="true"/>
        </s:if>
        <s:else>
        <input  class="buttonGeneric" type="button" id="save_button" value="<s:text name='button.common.save'/>" />
        </s:else>
        <input class="buttonGeneric" type="button" id="publish_button" value="<s:text name='button.common.publish'/>"/>
        <script type="text/javascript">
            dojo.addOnLoad(function() {
                dojo.byId("publish_button").disabled=!<s:property value="%{canPublish()}"/> ;
                dojo.connect(dojo.byId("save_button"), "onclick", function() {
                    dojo.byId("customReportPublish").value = false;
                    removeContentFormsAndSubmit();
                });
                dojo.connect(dojo.byId("publish_button"), "onclick", function() {
                    dojo.byId("customReportPublish").value = true;
                    removeContentFormsAndSubmit();
                });
            });
        </script>
    </div>

    </div>
    </div>
    <script type="text/javascript">
        function removeContentFormsAndSubmit(){
             dojo.byId("update_custom_report_form").action="update_custom_report.action";
             dojo.byId("update_custom_report_form").submit();
        }
        function toggleSubmitButtons(value){
            dojo.byId("save_button").disabled=value;
            if(dojo.byId("publish_button")){
                dojo.byId("publish_button").disabled=value;
            }
        }
        /* We submit the update_custom_report_form form to retain the user input on the form and
         send the parameters from create_section form as hidden parameters
         */ 
        function submitCreateSectionForm() {
        var form = dojo.byId("update_custom_report_form");
        form.action="create_section.action";
        dojo.byId("customReportName").readOnly=true;
        form.submit();
    }
    
    function updateForm1(id){
    dojo.byId(id+'_hid').value=dojo.byId(id).value;
    }

    function enableFields(quesId,optionSize){    	
        dojo.html.show(dojo.byId("optionList_"+quesId));        
      //  dojo.html.hide(dojo.byId("defaultValue_"+quesId));
        dojo.byId("question_name_"+quesId).readOnly=false;
        dojo.byId("question_order_"+quesId).readOnly=false;
        dojo.byId("question_mandatory_"+quesId).disabled=false;
        dijit.byId("selectedAnswerType_"+quesId).setDisabled(false);
        dojo.byId("question_noOfOptions_"+quesId).readOnly=false;
        for (var i=1;i<=optionSize;i++){
            dojo.byId("question_answer_option_"+quesId+"_"+i).readOnly=false;
        }
    }
    function readOnlyFields(quesId,optionSize){
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
    
</u:body>
</html>
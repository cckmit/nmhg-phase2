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
        dojo.require("twms.widget.TitlePane");
        dojo.require("twms.widget.Dialog");
        dojo.require("dijit.layout.LayoutContainer");
        dojo.require("twms.widget.ValidationTextBox");
    </script>

</head>

<u:body>
    <div dojoType="dijit.layout.LayoutContainer" style="width: 100%; height: 100%;" id="root">
        <div dojoType="dijit.layout.ContentPane" layoutAlign="client">
            <u:actionResults/>
            <s:form method="post" theme="twms" validate="true" id="claim_failure_report_form"
                    name="claim_failure_report" action="claim_failure_report_submit.action">
                <s:hidden name="claim" value="%{claim.id}"/>
                <s:hidden name="id" value="%{id}"/>
                <s:if test="claim.serviceInformation.customReportAnswer!=null">
                    <jsp:include page="failureReportForCausalPart.jsp"/>
                </s:if>
                <jsp:include page="failureReportForInstalledParts.jsp"/>
                <jsp:include page="failureReportForReplacedParts.jsp"/>
                <table class="buttons">
                    <tr>
                        <td>
                            <center>
                                <t:button value="%{getText('button.common.cancel')}" id="cancelButton"
                                          label="%{getText('button.common.cancel')}"/>
                                <script type="text/javascript">	                                
                                    dojo.addOnLoad(function() {
                                        dojo.connect(dojo.byId("cancelButton"), "onclick", closeMyTab);
                                        if (dojo.byId("deleteButton")) {
	                                        dojo.connect(dojo.byId("deleteButton"), "onclick", function(){
	                                        	 var form = document.getElementById("claim_failure_report_form");
		   	                                	 form.action="claim_failure_report_delete.action";
		   	                        	         form.submit();            								
	            							});
                                        }
                                        dojo.connect(dojo.byId("submitButton"), "onclick", function(){
                                        	 var form = document.getElementById("claim_failure_report_form");
	   	                                	 form.action="claim_failure_report_submit.action";
	   	                                	 dojo.byId("submitButton").disabled = true;
	   	                        	         form.submit();             								
            							});
                                    });
                                </script>
                                <s:if test="!claim.state.state.equals('Forwarded')">
                                    <t:button value="%{getText('label.failureReport.deleteDraftClaim')}" id="deleteButton"
                                          label="%{getText('label.failureReport.deleteDraftClaim')}"/>                                   
                                </s:if>
                                
                                 <s:submit value="%{getText('button.common.save')}" type="input"
                                          action="claim_failure_report_save" id="saveButton"/>
                                 <t:button value="%{getText('button.common.submit')}" id="submitButton"
                                          label="%{getText('button.common.submit')}"/>
                                <script type="text/javascript">
                                    dojo.addOnLoad(function() {
                                        var submitButtons = [dojo.byId("cancelButton"),
                                            dojo.byId("saveButton"),dojo.byId("submitButton")];
                                        if (dojo.byId("deleteButton")) {
                                            submitButtons.push(dojo.byId("deleteButton"));
                                        }
                                        for (var i = 0; i < submitButtons.length; i++) {
                                            dojo.connect(submitButtons[i], "onclick", function(event) {
                                                for (var j = 0; j < submitButtons.length; j++) {
                                                    if(submitButtons[j].id != event.target.id){
                                                       submitButtons[j].disabled = true;
                                                    }
                                                }
                                            });
                                        }
                                    });
                                </script>
                            </center>
                        </td>
                    </tr>
                </table>
            </s:form>
        </div>
    </div>
</u:body>
</html>

<%--
  Created by IntelliJ IDEA.
  User: pradyot.rout
  Date: Jan 7, 2009
  Time: 4:23:03 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html" %>
<%@ taglib prefix="sd" uri="/struts-dojo-tags"%>
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
<s:if test="pageReadOnlyAdditional">
dojo.addOnLoad(function() {
    twms.util.makePageReadOnly("dishonourReadOnly");
});
</s:if>
    dojo.require("twms.widget.TitlePane");
    dojo.require("dijit.layout.LayoutContainer");
    dojo.require("dojox.layout.ContentPane");
    dojo.require("twms.widget.Dialog");
</script>
<u:body>
    <s:form name="reportSubmissionForm" action="submit_report_for_inventory" theme="twms" id="reportSubmissionFormId">
    <div dojoType="dijit.layout.LayoutContainer" style="width: 100%; height: 100%;overflow-y:auto;overflow-x:hidden" id="root">
        <div dojoType="dijit.layout.ContentPane" layoutAlign="client" style="overflow-x:hidden">
            <u:actionResults/>
            <s:hidden name="reportAnswer.forInventory" value="%{reportAnswer.forInventory.id}"/>
            <s:hidden name="reportAnswer.customReport" id="customReportId"/>
            <s:if test="reportAnswer.customReport.reportType.code=='COMMISSION'">
                        <div class="section">
                       <div class="section_header">
                             <s:text name="title.common.equipmenInfo"></s:text>
                       </div>
                        <table width="100%" border="0" cellspacing="0" cellpadding="0">
                            <tbody>
                                <tr>
                                    <td style="color:gray; font-weight:bold;">
                                        <s:text name="label.common.serialNumber"/>
                                    </td>
                                    <td>
                                       <s:property value="reportAnswer.forInventory.serialNumber" />
                                    </td>
                                    <td style="color:gray; font-weight:bold">
                                        <s:text name="label.common.model"/>
                                    </td>
                                    <td>
                                       <s:property value="reportAnswer.forInventory.ofType.model.name" />
                                    </td>
                                    <td style="color:gray; font-weight:bold">
                                        <s:text name="columnTitle.inventoryAction.item_number"/>
                                    </td>
                                    <td>
                                       <s:property value="reportAnswer.forInventory.ofType.number" />
                                    </td>
                                </tr>
                            </tbody>
                        </table>
                      </div>
                      <div class="section">
                       <div class="section_header">
                             <s:text name="lable.common.installationdetails"></s:text>
                       </div>
                        <table width="100%" border="0" cellspacing="0" cellpadding="0">
                            <tbody>
                                <tr>
                                   <td style="color:gray; font-weight:bold">
                                        <s:text name="columnTitle.inventoryAction.delivery_date"/>
                                    </td>
                                    <td>
                                        <s:property value="reportAnswer.forInventory.deliveryDate" />
                                    </td>
                                    <td style="color:gray; font-weight:bold">
                                        <s:text name="label.common.dateInstall"/>
                                   </td>
                                    <td>
                                    <s:if test="reportAnswer.installationDate != null && (!installationDateIsAfter)">
                                           		<s:hidden name ="reportAnswer.installationDate" />
                                                <s:property value="reportAnswer.installationDate"/>
                                         </s:if>
                                         <s:else>
                                                <sd:datetimepicker name='reportAnswer.installationDate' value='%{reportAnswer.forInventory.deliveryDate}' />
                                         </s:else>
                                   </td>
                                   <td>
                                   	<div dojoType="dojox.layout.ContentPane" executeScripts="true"
                                                            scriptSeparation="false" id="attachments">                                                          
                                    	<a id="addOrRemoveAttachments" class="clickable"> 
                                                     <span id="commonAttachLink">
                                                        
                                                         <s:if test="@tavant.twms.web.TWMSWebConstants@SUBMITTED.equals(reportAnswer.status)"> <s:text name="label.common.viewAttachment" />  </s:if>     
                                                         <s:else><s:text name="label.common.addAttachment" /></s:else>                                                  
                                                     </span>
                                        </a> 
                                        <script type="text/javascript">
	                                    dojo.addOnLoad(function() {
	                                        dojo.connect(dojo.byId("addOrRemoveAttachments"), "onclick", showFileAttachDialog);
	                                        function showFileAttachDialog(evt) {
	                                            dojo.stopEvent(evt);
	                                            dijit.byId("add_or_remove_attachment").show();
	                                            if (!evt.target._attachmentWizardLoaded) {
	                                                evt.target._attachmentWizardLoaded = true;
	                                                dijit.byId("add_or_remove_attachment_div").setContent('Loading...');
	                                                dojo.xhrPost({
	                                                    url: "show_attached_files.action",
	                                                    form: dojo.byId("reportSubmissionFormId"),
	                                                    load:function(data) {
	                                                       dijit.byId("add_or_remove_attachment_div").setContent(data);
	                                                    }
	                                                });
	                                            }
	                                        }
	                                    });                     
                                   		   </script>
                                      </div>
                                    </td>
                                </tr>
                            </tbody>
                        </table>
                      </div>
               </s:if>
               <s:else>
                  <table>
                        <tbody>
                             <tr>
                                <td>
                                     <div executeScripts="true"
                                             scriptSeparation="false" id="attachments">
                                                   <a id="addOrRemoveAttachments" class="clickable"> 
                                                         <span id="commonAttachLink">
                                                          <s:if test="@tavant.twms.web.TWMSWebConstants@SUBMITTED.toString().equals(reportAnswer.status)"><s:text name="label.common.viewAttachment" /></s:if>     
                                                         <s:else><s:text name="label.common.addAttachment" /></s:else>                                                            
                                                          </span>
                                                    </a> 
                                                   <script type="text/javascript">                         
                                                     dojo.addOnLoad(function() {
                                                         dojo.connect(dojo.byId("addOrRemoveAttachments"),"onclick",showFileAttachDialog);
                                                          function showFileAttachDialog(evt){
                                                               dojo.stopEvent(evt);
                                                                var dlg = dijit.byId("add_or_remove_attachment");
                                                                 dlg.show();
                                                                   if(!dlg._attachmentWizardLoaded) {
                                                                      dlg._attachmentWizardLoaded = true;
                                                                      dijit.byId("add_or_remove_attachment_div").setContent('Loading...');
                                                                      dojo.xhrPost({
                                                                      url: "show_attached_files.action",
                                                                      form: dojo.byId("reportSubmissionFormId"),
                                                                      load:function(data) {
                                                                       dijit.byId("add_or_remove_attachment_div").setContent(data);
                                                                       }
                                                                     });
                                                                   }
                                                               }
                                                           });  
                                            </script>
                                          </div>
                                   </td>
                                 </tr>
                        </tbody>
                  </table>
                </s:else>
                <div id="dialogBoxContainer" style="display: none; overflow: auto">
                        <div dojoType="twms.widget.Dialog" id="add_or_remove_attachment"
                              bgColor="white" bgOpacity="0.5" toggle="fade" toggleDuration="250"
                              title="Supporting Documents" style="width: 50%; height: 400px">
                        <div class="dialogContent" dojoType="dijit.layout.LayoutContainer"
                              style="background: #F3FBFE; width: 100%; height: 400px; border: 1px solid #EFEBF7">
                              <div dojoType="dojox.layout.ContentPane"
                                    id="add_or_remove_attachment_div" executeScripts="true"></div>
                              </div>
                        </div>
                  </div>
                  <div executeScripts="true" >
                  <jsp:include flush="true" page="../inventory_fileupload_dialog.jsp" />
            </div>
            <s:iterator value="reportAnswer.attachments" status="attachments">
                 <s:hidden name="reportAnswer.attachments[%{#attachments.index}]" 
                        value="%{reportAnswer.attachments[#attachments.index].id}"/>
            </s:iterator>
                <s:iterator value="reportAnswer.customReport.sections" status="section">
                <div class="section" style="width:99%;">
                <div dojoType="twms.widget.TitlePane" title="<s:property value="name"/>"
                     labelNodeClass="section_header" open="true">
                    <s:iterator value="%{getReportAnswersForSection(id)}" status="formAnswers">
                      <s:hidden name="reportAnswer.formAnswers[%{order}].question" value="%{question.id}"/>
                      <s:hidden name="reportAnswer.formAnswers[%{order}].order" value="%{order}"/>
                      <s:hidden name="reportAnswer.formAnswers[%{order}].section" value="%{section.id}"/>  
                              <div style="width: 100%; overflow-y:auto;overflow-x:auto">
                           <table class="grid" style="padding-top:0;" border="0" cellspacing="0" cellpadding="0">
                              <tbody>
                              		<s:if test="questionnaire[#formAnswers.index].preInstructions!=null">
                              		  <s:if test="questionnaire[#formAnswers.index].preInstructions.instructions!=null">
						                <tr>						                
						                    <td class="labelStyle" style="vertical-align:top;" align="justify"><s:text name="label.question.preInstructions"/> :
						                               <s:property value="questionnaire[#formAnswers.index].preInstructions.instructions"/>
						                    </td>
						                </tr>
						              </s:if>
						              <s:if test="questionnaire[#formAnswers.index].preInstructions.attachment!=null">
						                 <tr> 
						                    <td> <img src="<s:url value="downloadDocument.action?docId=%{questionnaire[#formAnswers.index].preInstructions.attachment.id}"/>"></td>
						                 </tr>   
						              </s:if>						                    					                  
            						</s:if>
	                                 <tr> 	                                 		
		                                 		<td width="100%" style="padding-left:8px;" align="justify"><s:property value="question.name"/> (
			                                     <s:if test="question.mandatory">
			                                     	<s:text name="label.customReportAnswer.mandatory"/>
			                                     </s:if>
			                                     <s:else>
			                                    	 <s:text name="label.customReportAnswer.nonMandatory"/>
			                                     </s:else>
		                                     		)
		                                    	</td>
		                           	</tr>
	                                 		<tr>
                                            <td>
                                                 <s:if test="@tavant.twms.domain.customReports.ReportFormAnswerTypes@SMALL_TEXT.equals(question.answerType)">
	                                                  <s:textfield
	                                                  name="reportAnswer.formAnswers[%{order}].answerValue"
	                                                  value="%{answerValue}" disabled="reportAnswer.status.equals('submitted')"/>
                                                 </s:if>
                                                 <s:if test="@tavant.twms.domain.customReports.ReportFormAnswerTypes@LARGE_TEXT.equals(question.answerType)">
                                                      <s:textarea name="text"  name="reportAnswer.formAnswers[%{order}].answerValue"
                                                      value="%{answerValue}" cssStyle="width:250px;"  disabled="reportAnswer.status.equals('submitted')"/>
                                                 </s:if>                                                
                                                 <s:if test="@tavant.twms.domain.customReports.ReportFormAnswerTypes@SINGLE_SELECT.equals(question.answerType)"> 
	                                                 <t:multiSelectWithImage docId="documentId" inputType="radio" action="downloadDocument"  cssStyle="vertical"
	                                				 list="question.answerOptions" listValue="answerOption" listKey="id"   name="reportAnswer.formAnswers[%{order}].answerOptions[0]"
	                                				 value="%{reportAnswer.formAnswers[order].answerOptions[0].id}" disabled="reportAnswer.status.equals('submitted')" />
                                                 </s:if>
                                                 <s:if test="@tavant.twms.domain.customReports.ReportFormAnswerTypes@SINGLE_SELECT_LIST.equals(question.answerType)">
                                                	 <s:select list="question.answerOptions" listKey="id" listValue="answerOption"
                                					  name="reportAnswer.formAnswers[%{order}].answerOptions[0]"
                                					  value="%{reportAnswer.formAnswers[order].answerOptions[0].id.toString()}" 
                                					  disabled="reportAnswer.status.equals('submitted')"/>
                                                 </s:if>
                                                <s:if test="@tavant.twms.domain.customReports.ReportFormAnswerTypes@MULTI_SELECT_LIST.equals(question.answerType)">
                                                <s:if test="reportAnswer.status.equals('submitted')">
	                                                 <s:select multiple="true" list="question.answerOptions" listKey="id"
							                                  listValue="answerOption" theme="simple" cssStyle="width:200px"
							                                  name="reportAnswer.formAnswers[%{order}].answerOptions"
							                                  value="%{reportAnswer.formAnswers[order].answerOptions.{id.toString()}}" disabled="disabled" />
                                                </s:if>
                                                <s:else>
                                                 	<s:select multiple="true" list="question.answerOptions" listKey="id"
						                                  listValue="answerOption" theme="simple" cssStyle="width:200px"
						                                  name="reportAnswer.formAnswers[%{order}].answerOptions"
						                                  value="%{reportAnswer.formAnswers[order].answerOptions.{id.toString()}}" />
                                                </s:else>
                                                     
                                                </s:if>
                                                <s:if test="@tavant.twms.domain.customReports.ReportFormAnswerTypes@MULTI_SELECT.equals(question.answerType)">
                                                	 <t:multiSelectWithImage
                              						  docId="documentId" inputType="checkbox" action="downloadDocument"
                               						  cssStyle="vertical" list="question.answerOptions" listValue="answerOption" listKey="id"
                              						  name="reportAnswer.formAnswers[%{order}].answerOptions"
                               						  value="%{reportAnswer.formAnswers[order].answerOptions.{id}}" disabled="reportAnswer.status.equals('submitted')" />
                                             	</s:if>                                             	
	                                           <s:if test="@tavant.twms.domain.customReports.ReportFormAnswerTypes@NUMBER.equals(question.answerType)">	                                           
		                                           <s:if test="@tavant.twms.web.TWMSWebConstants@SUBMITTED.equals(reportAnswer.status)">
													   <input type="text"
		  													name="reportAnswer.formAnswers[<s:property value="%{order}" />].answerValue"
															value="<s:property value="%{reportAnswer.formAnswers[order].answerValue}"/>" disabled="true" />
												   </s:if>
												   <s:else>
												   		<input type="text"
		  													name="reportAnswer.formAnswers[<s:property value="%{order}" />].answerValue"
															value="<s:property value="%{reportAnswer.formAnswers[order].answerValue}"/>"  />
												   </s:else>			
	                                           </s:if>
	                                           
                                              <s:if test="@tavant.twms.domain.customReports.ReportFormAnswerTypes@DATE.equals(question.answerType)">
                                              <s:if test="reportAnswer.status.equals('submitted')">
                                                 <s:textfield name="reportAnswer.formAnswers[%{order}].answerValue" value="%{answerValue}" disabled="true"></s:textfield>
                                              </s:if>
                                              <s:else>
                                             	  <sd:datetimepicker name='reportAnswer.formAnswers[%{order}].answerValue' value='%{answerValue}' />
                                              </s:else>
                                                            
                                               </s:if>
                                           </td>
                                           </tr>
                                          <s:if test="questionnaire[#formAnswers.index].postInstructions!=null">
		                              		  <s:if test="questionnaire[#formAnswers.index].postInstructions.instructions!=null">
								                <tr>						                
								                     <td class="labelStyle" style="vertical-align:top;" align="justify"><s:text name="label.question.postInstructions"/> :
							                               <s:property value="questionnaire[#formAnswers.index].postInstructions.instructions"/>
								                    </td>
								                </tr>
								              </s:if>
								             <s:if test="questionnaire[#formAnswers.index].postInstructions.attachment!=null">
								                 <tr> 
								                    <td><img src="<s:url value="downloadDocument.action?docId=%{questionnaire[#formAnswers.index].postInstructions.attachment.id}"/>"></td>
								                 </tr>   
								              </s:if>						                    					                  
            						     </s:if> 
                                    </tbody>
                                </table>
                        </div>
                    </s:iterator>
                </div>

            </div>
            </s:iterator>
            <s:if test="reportAnswer==null || reportAnswer.id==null">
                  <div align="center">
                      <input id="cancel_btn" class="buttonGeneric" type="button" value="<s:text name='button.common.cancel'/>"
                      onclick="javascript:closeTab(getTabHavingLabel(getMyTabLabel()));"/>
                      <s:submit value="%{getText('button.common.save')}" cssClass="buttonGeneric" action="save_reports"/>
                      <s:submit value="%{getText('button.common.submit')}" cssClass="buttonGeneric"/>
                  </div>
            </s:if>
            <s:else>
               <div align="center">
                      <input id="cancel_btn" class="buttonGeneric" type="button" value="<s:text name='button.common.cancel'/>"
                             onclick="javascript:closeTab(getTabHavingLabel(getMyTabLabel()));"/>
                      <s:hidden name="reportAnswer" value="%{reportAnswer.id}"/>
                     <s:if test ="reportAnswer.status.equals('draft')"> 
                      <s:submit value="%{getText('button.common.save')}" cssClass="buttonGeneric" action="save_reports"/>
                      <s:submit value="%{getText('button.common.submit')}" cssClass="buttonGeneric"/>      
                     </s:if>
                  </div>
            </s:else>
        </div>

    </div>

    </s:form>

</u:body>

</html>



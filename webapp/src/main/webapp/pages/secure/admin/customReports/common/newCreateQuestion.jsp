<%--
  Created by IntelliJ IDEA.
  User: amritha.k
  Date: 15 Feb, 2010
  Time: 10:53:36 AM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html"%>
<%@ page pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="t" uri="twms"%>
<%@ taglib prefix="u" uri="/ui-ext"%>

<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    
    <s:head theme="twms" />
    <title><s:text name="Failure Report"/></title>
    <u:stylePicker fileName="common.css"/>
    <u:stylePicker fileName="form.css"/>
    <u:stylePicker fileName="claimForm.css"/>
    <u:stylePicker fileName="multiCar.css"/>
   <u:stylePicker fileName="base.css" />
     <script type="text/javascript">
    	dojo.require("twms.widget.TitlePane");
        dojo.require("dijit.layout.LayoutContainer");
        dojo.require("dijit.layout.ContentPane");
        dojo.require("twms.widget.FileUploader");
    </script>
    <script type="text/javascript" src="scripts/adminAutocompleterValidation.js"></script>
    <script type="text/javascript">
    
    function addQuestionToSection(addNextQuestion) {
     var form = dojo.byId("questionReport");
     if(addNextQuestion){
    	 form.action="add_question_to_Section.action";
     }else{
    	 form.action="add_question_to_Section_andExit.action";
     }
        form.submit();
    }

    dojo.addOnLoad(function(){
         var id = '<s:property value = "%{questionnaire.id}"/>';
         if(id==''){
        	  var value = <s:property value="%{section.questionnaire.size}"/>;
	          dojo.byId("questionnaire_order_id").value=value+1;
        	  	
         }else{
        	 dojo.byId("questionnaire_order_id").value='<s:property value = "%{questionnaire.order}"/>';
         }
    });
    </script>
</head>
<u:body >
  <div dojoType="dijit.layout.LayoutContainer" style="width: 100%; height: 99%; overflow-y: auto;">
     <div dojoType="dijit.layout.ContentPane" layoutAlign="client">
        <u:actionResults wipeMessages="true"/>
        <s:form id="questionReport" action="add_question_toSection" theme="twms"  method="POST">
        	<div dojoType="twms.widget.TitlePane" title="<s:text name="label.customReport.section.question"/>"  id="question"
                     labelNodeClass="section_header" open="true">
            	<s:if test="%{customReport.published}">
                       <div style="width:98%"><jsp:include page="newCreateQuestion_readOnly.jsp"></jsp:include></div>
                 </s:if>
                 <s:else>
                     <s:hidden name="section" value="%{section.id}"/>
                     <s:hidden name="questionnaire.order"  id="questionnaire_order_id"/>
                     <s:hidden name="questionnaire" value="%{questionnaire.id}"/>
					 <div style="width:98%">
 <table width="100%" border="0" cellspacing="4" cellpadding="4">
  <tr>
    <td valign="top" class="labelStyle" width="20%"><s:text name="label.section.questionnaire.text"/>:</td>
    <td colspan="3" valign="top"><t:textarea  rows="3" cols="40" id="questionnaire_name" value="%{questionnaire.name}" name="questionnaire.name" maxLength="4000" > 
				                	</t:textarea></td>
  </tr>
<tr>
 <td valign="top" class="labelStyle"  width="20%"><s:text name="label.section.questionnaire.preInstructions.instructions"/>:</td>
    <td valign="top" width="20%"><t:textarea rows="3" cols="40"  id="questionnaire_preInstructions" value="%{questionnaire.preInstructions.instructions}" name="questionnaire.preInstructions.instructions" maxLength="4000"> 
				                	</t:textarea></td>
<td class="labelStyle" width="17%"><s:text name="label.section.questionnaire.instruction.image"/>:</td>
<td><u:uploadDocument name="questionnaire.preInstructions.attachment"  singleFileUpload="true" /></td>
</tr>
<tr>
<td  class="labelStyle"><s:text name="label.section.questionnaire.answerType"/>:</td>
<td colspan="3"><s:select list="answerTypes"  id="questionnaire_answerType" listKey="key" listValue="value" name="questionnaire.answerType" value="questionnaire.answerType.name"></s:select></td>
</tr>
<tr>
<td colspan="4">
<table width="100%" border="0" cellspacing="0" cellpadding="0">
  <tr>
    <td width="17%">&nbsp;</td>

    <td  class="labelStyle" width="23%"><s:text name="label.section.questionnaire.includeOtherOption"/></td>
    <td  width="3%"><s:checkbox name="questionnaire.includeOtherAsAnOption" id="questionnaire_includeOtherAsAnOption" value="%{questionnaire.includeOtherAsAnOption}" > 
				                 	</s:checkbox></td>
	 <td width="5%">&nbsp;</td>

    <td  width="13%" class="labelStyle" ><s:text name="label.section.questionnaire.mandatory"/></td>
    <td> <s:checkbox name="questionnaire.mandatory" id="questionnaire_mandatory" value="%{questionnaire.mandatory}" > 
					                 </s:checkbox></td>
  </tr>
</table>
</td>
</tr>
<tr>
<td class="labelStyle"><s:text name="label.section.questionnaire.postInstructions.instructions" />:</td>
<td><t:textarea rows="3" cols="40"  id="questionnaire_postInstructions" value="%{questionnaire.postInstructions.instructions}" name="questionnaire.postInstructions.instructions" maxLength="4000"> 
					                	</t:textarea></td>
<td class="labelStyle"><s:text name="label.section.questionnaire.instruction.image"/>:</td>
<td><u:uploadDocument name="questionnaire.postInstructions.attachment" singleFileUpload="true" /></td>
</tr>
<tr>
<td class="labelStyle"><s:text name="label.section.questionnaire.answeroption"/>:</td>
<td colspan="3"></td>

</tr>



		                     <tr>
			                     <u:repeatTable id="questionnaire_answeroptons" cssClass="grid borderForTable" cellpadding="0"
		                             cellspacing="0" width="98%" theme="simple" cssStyle="margin:5px;">
			                        <thead>
			                            <tr class="row_head">
			                                <th width="30%"><s:text name="label.section.questionnaire.answer.text"/></th>
			                                <th width="30%"><s:text name="label.section.questionnaire.answer.image"/></th>
			                                <th width="20%"><s:text name="label.section.questionnaire.answer.default"/></th>
			                                <th width="20%" >
			                                 <u:repeatAdd id="adder" theme="twms">
			                                    <img id="addAnswerOptions" src="image/addRow_new.gif" border="0" style="cursor: pointer; padding-right:4px; " title="<s:text name="label.customReport.addAnswerOption" />" />
			                                 </u:repeatAdd>
			                                </th>
			                            </tr>
			                        </thead>
			                         <u:repeatTemplate id="answerOptions" value="questionnaire.answerOptions" index="answerOptionsIndex" theme="twms">
			                          	<tr index="#answerOptionsIndex" style="border: 1px solid #EFEBF7">
			                           		<s:hidden name ="questionnaire.answerOptions[#answerOptionsIndex]" value="%{questionnaire.answerOptions[#answerOptionsIndex].id}"/>
					                        <s:hidden name ="questionnaire.answerOptions[#answerOptionsIndex].order" id="answer_option_order_#answerOptionsIndex"/>
			                             <s:if test="%{!isOtherOption()}">
				      					<script type="text/javascript">                      
										    dojo.addOnLoad(function(){
										         dojo.byId("answer_option_order_#answerOptionsIndex").value=#answerOptionsIndex+1;
										    });
			   					 		</script>
				                            <td width="30%">
					                            <s:textfield id="question_answer_answeroption" name="questionnaire.answerOptions[#answerOptionsIndex].answerOption" value="%{questionnaire.answerOptions[#answerOptionsIndex].answerOption}">
					                            </s:textfield>
				                            </td>
				                            <td width="30%">
				                            	<u:uploadDocument name="questionnaire.answerOptions[#answerOptionsIndex].attachment" singleFileUpload="true" />
				                            </td>
					                        <td width="20%">
					                              <s:checkbox name="questionnaire.answerOptions[#answerOptionsIndex].isDefault" 
					                              value="%{isDefault}" > 
					               				  </s:checkbox>
					                        </td>
				                           	<td align="center" width="20%">
				                                 <u:repeatDelete id="questionnaire_answeroptons_#answerOptionsIndex" theme="twms">
				                                    <img id="delete" src="image/remove.gif" border="0" style="cursor: pointer;" title="<s:text name="label.common.deleteEntry" />"/>
												</u:repeatDelete>
											</td>
			                             </s:if>
			                            </tr>
			                          </u:repeatTemplate>
		                        </u:repeatTable>
		                     </tr>
	                    </tbody>
                 	</table>
					</div>
        	     </s:else>
        	</div>
          </s:form>
          <br>
          <div align="center">
	           <s:if test="%{customReport.published}">
	                   <input id="cancel_btn" class="buttonGeneric" type="button" value="<s:text name='button.common.cancel'/>"
	                             onclick="javascript:closeTab(getTabHavingLabel(getMyTabLabel()));"/>   
	             </s:if>
	             <s:else>
	             		   <input  class="buttonGeneric" type="button" id="save_button" value="<s:text name='button.common.save'/>"  onclick="addQuestionToSection(false)" />
	                        <s:if test="questionnaire.id !=null"></s:if>
	                        <s:else>
	                        	<input class="buttonGeneric" type="button" id="publish_button" value="<s:text name='button.save.andAdd.questions'/>"  onclick="addQuestionToSection(true)"/>
	                        </s:else>
	                         <input id="cancel_btn" class="buttonGeneric" type="button" value="<s:text name='button.common.cancel'/>"
	                             onclick="javascript:closeTab(getTabHavingLabel(getMyTabLabel()));"/>
	             </s:else>
           </div>
       </div>
 	</div>
</u:body>
</html>
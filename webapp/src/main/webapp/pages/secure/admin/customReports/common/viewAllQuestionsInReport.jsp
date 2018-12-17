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
    <u:stylePicker fileName="ruleGroup.css"/>
    <u:stylePicker fileName="form.css"/>
    <u:stylePicker fileName="claimForm.css"/>
    <u:stylePicker fileName="multiCar.css"/>
    <u:stylePicker fileName="base.css" />
    <script type="text/javascript" src="scripts/prioritizeQuestions.js"></script>
    <script type="text/javascript">
    	dojo.require("twms.widget.TitlePane");
    	  dojo.require("dojo.dnd.Source");
    </script>
</head>
<u:body>
    <s:form theme="twms" validate="true" id="questionPriorityForm">
	  		<s:hidden name="customReport"/>
	     	<div dojoType="dijit.layout.ContentPane" layoutAlign="client">
	        	<u:actionResults wipeMessages="true"/>
	          	<s:iterator value="customReport.sections" status="sectionIndex">
		             <s:hidden id="sectionId_%{#sectionIndex.index}" value="%{id}"/>
		             <s:hidden id="finalSection_%{#sectionIndex.index}" name="sectionCount" value="%{customReport.sections[#sectionIndex.index].questionnaire.size()}"/>
		             <script type="text/javascript">		             	
		            	 adjustOrderAfterDragDrop("questionPriorityForm", "questionsInSection_","<s:property value='%{#sectionIndex.index}'/>");
		             </script>
			   		<s:if test="%{customReport.published}">
			              <div dojoType="twms.widget.TitlePane" title="<s:property value="%{name}"/>" id="<s:property value="%{#sectionIndex.index}"/>"
				                     labelNodeClass="section_header" open="true">
					             <div id="questionsInSection_<s:property value='%{#sectionIndex.index}'/>">
							          <s:iterator value="questionnaire" status="questionIterator">
									       <div class="ruleNameSection">
										       <s:hidden id="questionOrder_%{#sectionIndex.index}_%{#questionIterator.index}"  
										                 name="customReport.sections[%{#sectionIndex.index}].questionnaire[%{#questionIterator.index}].order" />
										       <s:property value="name"/>
										       <s:hidden id="questionName_%{#sectionIndex.index}_%{#questionIterator.index}" value="%{name}"/>
										                  <s:hidden id="questionId_%{#sectionIndex.index}_%{#questionIterator.index}" value="%{id}"/>
										       <div align="right">
											       <a id="modifyQuestion_<s:property value='%{#sectionIndex.index}'/>_<s:property value='%{#questionIterator.index}'/>" class="link"
											               	  onclick="modifyQuestion('<s:property value="%{#sectionIndex.index}"/>','<s:property value="%{#questionIterator.index}"/>')" >
											               	        	 <s:text name="label.customReport.viewQuestion"/>
				                           			</a>
										       </div>
									       </div>
							         </s:iterator>
				             	 </div>
				          </div>
	             	</s:if>
	          	    <s:else>
				         <div dojoType="twms.widget.TitlePane" title="<s:property value="%{name}"/> 
				         (<s:text name="label.customReport.reorderQuestions"/>)" id="<s:property value='%{#sectionIndex.index}'/>"
				                     labelNodeClass="section_header" open="true">
					          <div id="questionsInSection_<s:property value='%{#sectionIndex.index}'/>">
					               <div dojoType="dojo.dnd.Source" class="container" >
					                <s:if test="questionnaire==null || (questionnaire !=null && questionnaire .size()==0)">
					                  <s:text name="label.section.questions.none"/>
					                </s:if>
					                <s:else>					                
					                 <s:iterator value="questionnaire" status="questionIterator">
								                <div class="dojoDndItem">
									                <div class="ruleNameSection">
										                <s:hidden id="questionOrder_%{#sectionIndex.index}_%{#questionIterator.index}"  
										                 name="customReport.sections[%{#sectionIndex.index}].questionnaire[%{#questionIterator.index}].order" />
										                <s:property value="name"/>
										                <s:hidden id="questionName_%{#sectionIndex.index}_%{#questionIterator.index}" value="%{name}"/>
										                  <s:hidden id="questionId_%{#sectionIndex.index}_%{#questionIterator.index}" value="%{id}"/>
									               	</div>
									               	<div align="right">
											               	  <a id="modifyQuestion_<s:property value='%{#sectionIndex.index}'/>_<s:property value='%{#questionIterator.index}'/>" class="link"
											               	  onclick="modifyQuestion('<s:property value="%{#sectionIndex.index}"/>','<s:property value="%{#questionIterator.index}"/>')" >
											               	      		 <s:text name="label.customReport.modifyQuestion"/>
				                               			 			
				                           					  </a>
											        </div>
								               </div>
							             </s:iterator>
					                </s:else>
							            
					                </div>
				              </div>
				          </div>
		         	  </s:else>
	           </s:iterator>
		       <div align="center">
					       <s:if test="%{!customReport.published}">
									        <s:submit value="%{getText('button.common.update')}" cssClass="buttonGeneric"
								                              action="update_question_order"/>
						   </s:if>
					       <input id="cancel_btn" class="buttonGeneric" type="button" value="<s:text name='button.common.cancel'/>"
					                             onclick="javascript:closeTab(getTabHavingLabel(getMyTabLabel()));"/>
		        </div>
				<script type="text/javascript">
						 function   modifyQuestion(sectionIndex,questionIndex){
							var questionName = dojo.byId("questionName_"+sectionIndex+"_"+questionIndex).value;
							var questionId = dojo.byId("questionId_"+sectionIndex+"_"+questionIndex).value;
							var reportId = '<s:property value="customReport.id"/>';
			        		var sectionId = dojo.byId("sectionId_"+sectionIndex).value;
			        		var actionURL = "add_question.action?questionnaire="+questionId+"&customReport="+reportId+"&section="+sectionId;
			        		var thisTabLabel = getMyTabLabel();
			        	    parent.publishEvent("/tab/open", {label: questionName, url: actionURL, decendentOf : thisTabLabel});
							};
				</script>
		 	 </div>
	</s:form>
</u:body>
</html>
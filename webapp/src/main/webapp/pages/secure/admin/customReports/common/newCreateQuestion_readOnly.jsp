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
   <s:hidden name="questionnaire.order"  id="questionnaire_order_id"/>
	<table width="100%" border="0" cellspacing="0" cellpadding="0" class="grid">
	    <tbody>
			<tr>
				<td  class="labelStyle" nowrap="nowrap" width="20%">
					<s:text name="label.section.questionnaire.text"/>
				</td>
				<td  class="labelStyle" nowrap="nowrap" width="20%">
					&nbsp;&nbsp;
					<s:textarea rows="3" cols="40"  id="questionnaire_name" value="%{questionnaire.name}" name="questionnaire.name" readonly="true" > 
					</s:textarea>
				</td>
			</tr>
			<tr>
				<td  class="labelStyle" nowrap="nowrap" width="20%">
					<s:text name="label.section.questionnaire.preInstructions.instructions"/>
				</td>
				<td  class="labelStyle" nowrap="nowrap" width="20%">
					&nbsp;&nbsp;
					<s:textarea rows="3" cols="40"  id="questionnaire_preInstructions" value="%{questionnaire.preInstructions.instructions}" readonly="true" 
					name="questionnaire.preInstructions.instructions" > 
					</s:textarea>
				</td>
				<td  class="labelStyle" nowrap="nowrap" width="20%">
					<s:text name="label.section.questionnaire.answer.uploadedimage"/>
				</td>
				<td  nowrap="nowrap" width="20%">
					<s:if test="questionnaire.preInstructions.attachment==null">
						  <s:text name="label.common.none"/>
					 </s:if>
					 <s:else>
					 	<u:uploadDocument name="questionnaire.preInstructions.attachment"  disabled="true" singleFileUpload="true" />
					 </s:else>
				</td>
			</tr>
			<tr>
				<td  class="labelStyle" nowrap="nowrap" width="20%">
					<s:text name="label.section.questionnaire.answerType"/>
				</td>
				<td  class="labelStyle" nowrap="nowrap" width="22%" style="padding-left:10px;">
					<s:select list="answerTypes"  id="questionnaire_answerType" disabled="true" listKey="key" listValue="value" name="questionnaire.answerType" value="questionnaire.answerType.name"></s:select>
				</td>
			</tr>
			<tr>
				<td  class="labelStyle" nowrap="nowrap" width="20%"><s:text name="label.section.questionnaire.includeOtherOption"/>
				</td>
				<td width="22%">&nbsp;&nbsp;
					<s:if test="questionnaire.includeOtherAsAnOption">
				   	 <s:text name="label.yes"/>
					</s:if>
					<s:else>
				 	 <s:text name="label.no"/>
					</s:else>
				</td>
				<td  class="labelStyle" nowrap="nowrap" width="20%"><s:text name="label.section.questionnaire.mandatory"/>
				</td>
				<td>
				<s:if test="questionnaire.mandatory">
				 <s:text name="label.yes"/>
				</s:if>
				<s:else>
				  <s:text name="label.no"/>
				</s:else>
				</td>
		    </tr>
			<tr>
				<td  class="labelStyle" nowrap="nowrap" width="20%">
					<s:text name="label.section.questionnaire.postInstructions.instructions" />
				</td>
				<td  class="labelStyle" nowrap="nowrap" width="20%">
					&nbsp;&nbsp;
					<s:textarea rows="3" cols="40"  readonly="true" id="questionnaire_postInstructions" value="%{questionnaire.postInstructions.instructions}" name="questionnaire.postInstructions.instructions" > 
					</s:textarea>
				</td>
				<td  class="labelStyle" nowrap="nowrap" width="20%">
					<s:text name="label.section.questionnaire.answer.uploadedimage"/>
				</td>
				<td  nowrap="nowrap" width="20%">
				 	 <s:if test="questionnaire.postInstructions.attachment==null">
						           <s:text name="label.common.none"/>
					 </s:if>
					 <s:else>
						<u:uploadDocument name="questionnaire.postInstructions.attachment" disabled="true" singleFileUpload="true" />
					 </s:else>
				</td>
			</tr>
			<tr>
				<td class="labelStyle">
					<s:text name="label.section.questionnaire.answeroption"/>
				</td>
			</tr>
			<tr>
				<u:repeatTable id="questionnaire_answeroptons" cssClass="grid borderForTable" cellpadding="0"
			   		 cellspacing="0" width="98%" theme="simple" cssStyle="margin:5px;">
				    <thead>
					    <tr class="row_head">
						    <th width="30%"><s:text name="label.section.questionnaire.answer.text"/></th>
						    <th width="30%"><s:text name="label.section.questionnaire.answer.uploadedimage"/></th>
						    <th width="20%"><s:text name="label.section.questionnaire.answer.default"/></th>
						   
						    </th>
					    </tr>
				    </thead>
				    <u:repeatTemplate id="answerOptions" value="questionnaire.answerOptions" index="answerOptionsIndex" theme="twms">
					    <tr index="#answerOptionsIndex" style="border: 1px solid #EFEBF7">
						    <script type="text/javascript">                      
							dojo.addOnLoad(function(){
							dojo.byId("answer_option_order_#answerOptionsIndex").value=#answerOptionsIndex+1;
							});
						   	</script>
					   		 <s:hidden name ="questionnaire.answerOptions[#answerOptionsIndex].order" id="answer_option_order_#answerOptionsIndex"/>
						    <s:if test="%{!isOtherOption()}">
						    <td width="30%">
							    <s:textfield  readonly="true" id="question_answer_answeroption" name="questionnaire.answerOptions[#answerOptionsIndex].answerOption" value="%{questionnaire.answerOptions[#answerOptionsIndex].answerOption}">
							    </s:textfield>
						    </td>
						    <td width="30%">
						        <s:if test="questionnaire.answerOptions[#answerOptionsIndex].attachment==null">
						           <s:text name="label.common.none"/>
						         </s:if>
						         <s:else>
						         	 <u:uploadDocument name="questionnaire.answerOptions[#answerOptionsIndex].attachment" disabled= "true" singleFileUpload="true" />
						         </s:else>
						    </td>
							 <td width="20%">
							 	<s:if test="questionnaire.answerOptions[#answerOptionsIndex].isDefault" >
							 	 <s:text name="label.yes"/>
							 	</s:if>
							 	<s:else>
							 	 <s:text name="label.no"/>
							 	</s:else> 	
							 </td>
							 </s:if>
					    </tr>
				    </u:repeatTemplate>
				</u:repeatTable>
			</tr>
	</tbody>
</table>

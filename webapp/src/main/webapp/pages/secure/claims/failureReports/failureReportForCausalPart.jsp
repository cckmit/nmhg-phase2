<%--
  Created by IntelliJ IDEA.
  User: irdemo
  Date: Mar 25, 2010
  Time: 9:10:20 PM
  To change this template use File | Settings | File Templates.
--%>
<head>
    <script type="text/javascript">
        dojo.require("twms.widget.TitlePane");      
    </script>
</head>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sd" uri="/struts-dojo-tags"%>
<%@ taglib prefix="t" uri="twms" %>

<div dojoType="twms.widget.TitlePane"
     title="<s:text name="label.common.causalPartNumber"/>: <s:property value="claim.serviceInformation.causalPart.number"/>"
     labelNodeClass="section_header">
    <table class="grid" cellspacing="0" cellpadding="0">
        <tr>
            <td class="labelStyle" nowrap="nowrap" width="20%"><s:text name="label.common.causalPartDescription"/> :</td>
            <td width="20%" class="labelStyle"><s:property value="claim.serviceInformation.causalPart.description"/></td>
        </tr>
        <s:hidden name="claim.serviceInformation.customReportAnswer.status"
                  value="DRAFT"/>
        <s:hidden name="claim.serviceInformation.customReportAnswer.customReport"
                  value="%{claim.serviceInformation.customReportAnswer.customReport.id}"/>
        <s:iterator value="claim.serviceInformation.customReportAnswer.formAnswers"
                    status="causalPartReportAnswers">
            <tr>
                <td class="labelStyle" nowrap="nowrap" width="25%"><s:text name="label.customReport.sectionName"/></td>
                <td width="75%">
                    <s:property value="section.name"/>
                </td>
            </tr>
            <s:hidden
                    name="claim.serviceInformation.customReportAnswer.formAnswers[%{#causalPartReportAnswers.index}].order"
                    value="%{order}"/>
            <s:hidden
                    name="claim.serviceInformation.customReportAnswer.formAnswers[%{#causalPartReportAnswers.index}].question"
                    value="%{question.id}"/>
            <s:hidden
                    name="claim.serviceInformation.customReportAnswer.formAnswers[%{#causalPartReportAnswers.index}].section"
                    value="%{section.id}"/>
            <s:hidden name="claim.serviceInformation.customReportAnswer.formAnswers[%{#causalPartReportAnswers.index}]"
                      value="%{claim.serviceInformation.customReportAnswer.formAnswers[#causalPartReportAnswers.index].id}"/>
                      
             <s:if test="question.preInstructions!=null">
		       <s:if test="question.preInstructions.instructions!=null">
				 <tr>						                
					<td class="labelStyle" width="25%" style="vertical-align:top;"><s:text name="label.question.preInstructions"/> :</td>
                    <td width="75%" style="vertical-align:top;">
                               <s:property value="question.preInstructions.instructions"/>
					</td>
				 </tr>
				</s:if>
				</s:if >
					</table>
					<s:if test="question.preInstructions!=null">
				 <s:if test="question.preInstructions.attachment!=null">
					<tr> 
						<td> <img src="<s:url value="downloadDocument.action?docId=%{question.preInstructions.attachment.id}"/>"></td>
					</tr>   
				</s:if>						                    					                  
            </s:if> 
            <table class="grid" cellspacing="0" cellpadding="0">
            <tr>
                <td class="labelStyle" width="25%" style="vertical-align:top;"><s:property value="question.name"/> :</td>
                <td width="75%" style="vertical-align:top;">
                    <s:if test="@tavant.twms.domain.customReports.ReportFormAnswerTypes@SMALL_TEXT.equals(question.answerType)">                    
                        <s:textfield
                                name="claim.serviceInformation.customReportAnswer.formAnswers[%{#causalPartReportAnswers.index}].answerValue"/>
                    </s:if>
                    <s:if test="@tavant.twms.domain.customReports.ReportFormAnswerTypes@NUMBER.equals(question.answerType)">                   
                        <input type="text"
                                   name="claim.serviceInformation.customReportAnswer.formAnswers[<s:property value="%{#causalPartReportAnswers.index}" />].answerValue"
                                   value="<s:property value="%{claim.serviceInformation.customReportAnswer.formAnswers[#causalPartReportAnswers.index].answerValue}"/>"/>
                    </s:if>
                    <s:if test="@tavant.twms.domain.customReports.ReportFormAnswerTypes@LARGE_TEXT.equals(question.answerType)">
                        <t:textarea
                                name="claim.serviceInformation.customReportAnswer.formAnswers[%{#causalPartReportAnswers.index}].answerValue" maxLength="500"/>
                    </s:if>
                    <s:if test="@tavant.twms.domain.customReports.ReportFormAnswerTypes@SINGLE_SELECT.equals(question.answerType)">
                        <t:multiSelectWithImage
                                docId="documentId" inputType="radio" action="downloadDocument"
                                cssStyle="vertical"
                                list="question.answerOptions" listValue="answerOption" listKey="id"
                                name="claim.serviceInformation.customReportAnswer.formAnswers[%{#causalPartReportAnswers.index}].answerOptions[0]"
                                value="%{claim.serviceInformation.customReportAnswer.formAnswers[#causalPartReportAnswers.index].answerOptions[0].id}"/>
                    </s:if>
                    <s:if test="@tavant.twms.domain.customReports.ReportFormAnswerTypes@MULTI_SELECT.equals(question.answerType)">
                        <t:multiSelectWithImage
                                docId="documentId" inputType="checkbox" action="downloadDocument"
                                cssStyle="vertical"
                                list="question.answerOptions" listValue="answerOption" listKey="id"
                                name="claim.serviceInformation.customReportAnswer.formAnswers[%{#causalPartReportAnswers.index}].answerOptions"
                                value="%{claim.serviceInformation.customReportAnswer.formAnswers[#causalPartReportAnswers.index].answerOptions.{id}}"/>
                    </s:if>
                    <s:if test="@tavant.twms.domain.customReports.ReportFormAnswerTypes@SINGLE_SELECT_LIST.equals(question.answerType)">
                        <s:select list="question.answerOptions" listKey="id" listValue="answerOption"
                                  name="claim.serviceInformation.customReportAnswer.formAnswers[%{#causalPartReportAnswers.index}].answerOptions[0]"
                                  value="%{claim.serviceInformation.customReportAnswer.formAnswers[#causalPartReportAnswers.index].answerOptions[0].id.toString()}"/>
                    </s:if>
                    <s:if test="@tavant.twms.domain.customReports.ReportFormAnswerTypes@MULTI_SELECT_LIST.equals(question.answerType)">
                        <s:select multiple="true" list="question.answerOptions" listKey="id"
                                  listValue="answerOption" theme="simple" cssStyle="width:200px"
                                  name="claim.serviceInformation.customReportAnswer.formAnswers[%{#causalPartReportAnswers.index}].answerOptions"
                                  value="%{claim.serviceInformation.customReportAnswer.formAnswers[#causalPartReportAnswers.index].answerOptions.{id.toString()}}"/>
                    </s:if>
                    <s:if test="@tavant.twms.domain.customReports.ReportFormAnswerTypes@DATE.equals(question.answerType)">
                        <sd:datetimepicker name='claim.serviceInformation.customReportAnswer.formAnswers[%{#causalPartReportAnswers.index}].answerDate' value='%{claim.serviceInformation.customReportAnswer.formAnswers[#causalPartReportAnswers.index].answerDate}' />
                    </s:if>
                </td>
            </tr>
            <s:if test="question.postInstructions!=null">
		       <s:if test="question.postInstructions.instructions!=null">
				 <tr>						                
					<td class="labelStyle" width="20%"><s:text name="label.question.postInstructions"/> :</td>
                    <td width="80%">
                        <s:property value="question.postInstructions.instructions"/>
					</td>
				 </tr>
				</s:if>
				</s:if>
				</table>
				<s:if test="question.postInstructions!=null">
				 <s:if test="question.postInstructions.attachment!=null">
					<tr> 
						<td> <img src="<s:url value="downloadDocument.action?docId=%{question.postInstructions.attachment.id}"/>"></td>
					</tr>   
				</s:if>						                    					                  
            </s:if>   
             <table class="grid" cellspacing="0" cellpadding="0">         
            <tr><td colspan="2" class="borderTable"></td></tr>
        </s:iterator>
    </table>
</div>


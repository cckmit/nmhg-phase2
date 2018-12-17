<%--
  Created by IntelliJ IDEA.
  User: irdemo
  Date: Mar 25, 2010
  Time: 3:43:01 PM
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
<s:iterator value="installedParts" status="installedPartStatus">
<s:if test="customReportAnswer!=null">
<s:set name="partNumberForTitlePane" value="item.number"/>
<s:if test="serialNumber!=null">
    <s:set name="installedPartSerialNumberForTitlePane" value="serialNumber"/>
</s:if>

<div dojoType="twms.widget.TitlePane"
     title="
     <s:if test="%{#installedPartSerialNumberForTitlePane!=null}">
     <s:text name="label.newClaim.partSerialNo"/>: <s:property value="%{#installedPartSerialNumberForTitlePane}"/>&nbsp&nbsp&nbsp&nbsp
     </s:if>
     <s:text name="label.common.partNumber"/>: <s:property value="#partNumberForTitlePane"/>
     "
     labelNodeClass="section_header">
    <table class="grid" cellspacing="0" cellpadding="0">
        <tr>
            <td class="labelStyle" nowrap="nowrap" width="20%"><s:text name="label.batteryTestSheet.partDesc"/> :</td>
            <td width="20%" class="labelStyle"><s:property value="item.description"/></td>
        </tr>
        <s:hidden name="installedParts[%{#installedPartStatus.index}].customReportAnswer.status"
                  value="DRAFT"/>
         <s:hidden name="installedParts[%{#installedPartStatus.index}].customReportAnswer.customReport"
                  value="%{installedParts[#installedPartStatus.index].customReportAnswer.customReport.id}"/>
        <s:iterator value="customReportAnswer.formAnswers" status="installedPartAnswers">
            <tr>
                <td class="labelStyle" nowrap="nowrap" width="25%">
                    <s:text name="label.customReport.sectionName"/>
                </td>
                <td width="75%"><s:property value="section.name"/></td>
            </tr>
            <s:hidden
                    name="installedParts[%{#installedPartStatus.index}].customReportAnswer.formAnswers[%{#installedPartAnswers.index}].order"
                    value="%{order}"/>
            <s:hidden
                    name="installedParts[%{#installedPartStatus.index}].customReportAnswer.formAnswers[%{#installedPartAnswers.index}].question"
                    value="%{question.id}"/>
            <s:hidden
                    name="installedParts[%{#installedPartStatus.index}].customReportAnswer.formAnswers[%{#installedPartAnswers.index}].section"
                    value="%{section.id}"/>
            <s:hidden
                    name="installedParts[%{#installedPartStatus.index}].customReportAnswer.formAnswers[%{#installedPartAnswers}]"
                    value="%{installedParts[#installedPartStatus.index].customReportAnswer.formAnswers[#installedPartAnswers.index].id}"/>
                    
            <s:if test="question.preInstructions!=null">
		       <s:if test="question.preInstructions.instructions!=null">
				 <tr>						                
					<td class="labelStyle" width="25%" style="vertical-align:top;"><s:text name="label.question.preInstructions"/> :</td>
                    <td width="75%" style="vertical-align:top;">
                               <s:property value="question.preInstructions.instructions"/>
					</td>
				 </tr>
				</s:if>
				</s:if>
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
                                name="installedParts[%{#installedPartStatus.index}].customReportAnswer.formAnswers[%{#installedPartAnswers.index}].answerValue"/>
                    </s:if>                    
                    <s:if test="@tavant.twms.domain.customReports.ReportFormAnswerTypes@NUMBER.equals(question.answerType)">                   
                        <input type="text"
                               name="installedParts[<s:property value="%{#installedPartStatus.index}"/>].customReportAnswer.formAnswers[<s:property value="%{#installedPartAnswers.index}"/>].answerValue"
                               value="<s:property value="%{installedParts[#installedPartStatus.index].customReportAnswer.formAnswers[#installedPartAnswers.index].answerValue}"/>"/>
                    </s:if>
                    <s:if test="@tavant.twms.domain.customReports.ReportFormAnswerTypes@LARGE_TEXT.equals(question.answerType)">
                        <t:textarea
                                name="installedParts[%{#installedPartStatus.index}].customReportAnswer.formAnswers[%{#installedPartAnswers.index}].answerValue" maxLength="500"/>
                    </s:if>
                    <s:if test="@tavant.twms.domain.customReports.ReportFormAnswerTypes@SINGLE_SELECT.equals(question.answerType)">
                        <t:multiSelectWithImage
                                docId="documentId" inputType="radio" action="downloadDocument"
                                cssStyle="vertical"
                                list="question.answerOptions" listValue="answerOption" listKey="id"
                                name="installedParts[%{#installedPartStatus.index}].customReportAnswer.formAnswers[%{#installedPartAnswers.index}].answerOptions[0]"
                                value="%{installedParts[#installedPartStatus.index].customReportAnswer.formAnswers[#installedPartAnswers.index].answerOptions[0].id}"/>
                    </s:if>
                    <s:if test="@tavant.twms.domain.customReports.ReportFormAnswerTypes@MULTI_SELECT.equals(question.answerType)">
                        <t:multiSelectWithImage
                                docId="documentId" inputType="checkbox" action="downloadDocument"
                                cssStyle="vertical"
                                list="question.answerOptions" listValue="answerOption" listKey="id"
                                name="installedParts[%{#installedPartStatus.index}].customReportAnswer.formAnswers[%{#installedPartAnswers.index}].answerOptions"
                                value="%{installedParts[#installedPartStatus.index].customReportAnswer.formAnswers[#installedPartAnswers.index].answerOptions.{id}}"/>
                    </s:if>
                    <s:if test="@tavant.twms.domain.customReports.ReportFormAnswerTypes@SINGLE_SELECT_LIST.equals(question.answerType)">
                        <s:select list="question.answerOptions" listKey="id" listValue="answerOption"
                                  name="installedParts[%{#installedPartStatus.index}].customReportAnswer.formAnswers[%{#installedPartAnswers.index}].answerOptions[0]"
                                  value="%{installedParts[#installedPartStatus.index].customReportAnswer.formAnswers[#installedPartAnswers.index].answerOptions[0].id.toString()}"/>
                    </s:if>
                    <s:if test="@tavant.twms.domain.customReports.ReportFormAnswerTypes@MULTI_SELECT_LIST.equals(question.answerType)">
                        <s:select multiple="true" list="question.answerOptions" listKey="id"
                                  listValue="answerOption"
                                  theme="simple" cssStyle="width:200px"
                                  name="installedParts[%{#installedPartStatus.index}].customReportAnswer.formAnswers[%{#installedPartAnswers.index}].answerOptions"
                                  value="%{installedParts[#installedPartStatus.index].customReportAnswer.formAnswers[#installedPartAnswers.index].answerOptions.{id.toString()}}"/>
                    </s:if>
                    <s:if test="@tavant.twms.domain.customReports.ReportFormAnswerTypes@DATE.equals(question.answerType)"> 
					<sd:datetimepicker
                                name="installedParts[%{#installedPartStatus.index}].customReportAnswer.formAnswers[%{#installedPartAnswers.index}].answerDate"
                                value="%{installedParts[#installedPartStatus.index].customReportAnswer.formAnswers[#installedPartAnswers.index].answerDate}"/>
                    </s:if>
                   </td>
            </tr>
            <s:if test="question.postInstructions!=null">
		       <s:if test="question.postInstructions.instructions!=null">
				 <tr>						                
					<td class="labelStyle" width="25%" style="vertical-align:top;"><s:text name="label.question.postInstructions"/> :</td>
                    <td width="75%" style="vertical-align:top;">
                        <s:property value="question.postInstructions.instructions"/>
					</td>
				 </tr>
				</s:if>
				</s:if>
				</table>
					<s:if test="question.postInstructions!=null" > 
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
</s:if>
</s:iterator>
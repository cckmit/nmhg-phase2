<%--
  Created by IntelliJ IDEA.
  User: irdemo
  Date: Apr 21, 2010
  Time: 6:54:22 PM
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
    <title><s:text name="title.viewFailureReports"/></title>
    <u:stylePicker fileName="yui/reset.css" common="true"/>
    <u:stylePicker fileName="commonPrintReport.css"/>
    <u:stylePicker fileName="form.css"/>
    <u:stylePicker fileName="claimForm.css"/>
    <u:stylePicker fileName="base.css"/>
    <script type="text/javascript">
        dojo.require("twms.widget.TitlePane");
        dojo.require("dijit.layout.LayoutContainer");
    </script>
</head>
<u:body>
<table width="100%" class="grid" style="border:1px solid black; background-color:white;">
    <tr width="100%" style="padding:0px">
        <td width="100%" style="padding:0px">
            <table width="100%" style="border:1px solid black;text-transform:uppercase; background-color:#F3FBFE">
                <tr>
                    <td class="labelStyle">Claim Details</td>
                </tr>
            </table>
        </td>
    </tr>
    <tr >
        <td width="100%">
            <table class="grid" style="background-color:white;">
                <tr>
                    <td class="labelStyle" nowrap="nowrap" width="20%"><s:text name="label.common.claimNumber"/>:</td>
                    <td width="30%"><s:property value="claim.claimNumber"/></td>
                    <td class="labelStyle" nowrap="nowrap" width="20%"><s:text name="label.failueReport.dealerCode"/>:
                    </td>
                    <td width="30%"><s:property value="claim.forDealerShip.serviceProviderNumber"/></td>
                </tr>
                <tr>
                    <td class="labelStyle" nowrap="nowrap" width="20%"><s:text
                            name="label.failueReport.unitSerialNumberUnit"/>:
                    </td>
                    <td width="30%">
                        <s:if test="claim.itemReference.referredInventoryItem!=null">
                            <s:property value="claim.itemReference.referredInventoryItem.serialNumber"/>
                        </s:if>
                        <s:else>
                            -
                        </s:else>
                    </td>
                    <td class="labelStyle" nowrap="nowrap" width="20%"><s:text name="label.failueReport.unitModel"/>:
                    </td>
                    <td width="30%">
                        <s:if test="claim.itemReference.referredInventoryItem!=null">
                            <s:property value="claim.itemReference.referredInventoryItem.ofType.model.name"/>
                        </s:if>
                        <s:elseif test="claim.itemReference.model!=null">
                            <s:property value="claim.itemReference.model.name"/>
                        </s:elseif>
                        <s:else>
                            -
                        </s:else>
                    </td>
                </tr>
                <tr>
                    <td class="labelStyle" nowrap="nowrap" width="20%"><s:text name="label.failueReport.completedDate"/>:</td>
                    <td><s:property value="customReportAnswer.d.updatedOn"/></td>
					
				</tr>
				 <tr>
			            <td class="labelStyle" nowrap="nowrap" width="20%"><s:text name="label.failueReport.RelatedParts"/>:</td>
			            <td><s:iterator value="itemsApplicableOnReport" status="itemStatus"><s:property value="number"/><s:if 
			            		test="itemsApplicableOnReport.size()>1 && !#itemStatus.last"> | </s:if></s:iterator></td>
				</tr>
            </table>
        </td>
    </tr>
</table>
<br/><br/>
<table width="100%" class="grid" style="border:1px solid black; background-color:white">
<tr width="100%" style="padding:0px">
        <td width="100%" style="padding:0px">
            <table width="100%" style="border:1px solid black;text-transform:uppercase;  background-color:#F3FBFE">
                <tr>
                    <td class="labelStyle">
                        <s:if test="inventoryItem!=null">
        <s:text name="label.newClaim.partSerialNo"/>: <s:property value="%{inventoryItem.serialNumber}"/>&nbsp&nbsp&nbsp&nbsp
     </s:if>
     <s:elseif test="unSzdSlNo!=null">
        <s:text name="label.newClaim.partSerialNo"/>: <s:property value="%{unSzdSlNo}"/>&nbsp&nbsp&nbsp&nbsp
     </s:elseif>
     <s:text name="label.common.partNumber"/>: <s:property value="%{item.number}"/>
                    </td>            
                </tr>
            </table>
        </td>
    </tr>
   
    <tr >
        <td>
            <table class="grid" cellspacing="0" cellpadding="0" style="background-color:white;">           
                <s:iterator value="customReportAnswer.formAnswers" status="reportAnswers">
                    <tr>
                        <td class="labelStyle" nowrap="nowrap" width="25%"><s:text
                                name="label.customReport.sectionName"/></td>
                        <td width="75%">
                            <s:property value="section.name"/>
                        </td>
                    </tr>
                    <s:if test="question.preInstructions!=null">
				       <s:if test="question.preInstructions.instructions!=null">
				       <table class="grid" cellspacing="0" cellpadding="0">
						 <tr>						                
							 <td class="labelStyle" width="25%" style="vertical-align:top;"><s:text name="label.question.preInstructions"/> :</td>
		                     <td width="75%" style="vertical-align:top;">
		                        <s:property value="question.preInstructions.instructions"/>
							</td>
						 </tr>
						  </table>
						</s:if>
						</s:if>
						 <s:if test="question.preInstructions!=null">
						 <s:if test="question.preInstructions.attachment!=null">
						  <table class="grid" cellspacing="0" cellpadding="0">
							<tr> 
								<td> <img src="<s:url value="downloadDocument.action?docId=%{question.preInstructions.attachment.id}"/>"></td>
							</tr> 
							</table>  
						</s:if>						                    					                  
                    </s:if>   
                    <tr>
                    <table class="grid" cellspacing="0" cellpadding="0">
                        <td class="labelStyle" width="25%" style="vertical-align:top;"><s:property value="question.name"/> :</td>
                        <td width="75%" style="vertical-align:top;">
                            <s:if test="@tavant.twms.domain.customReports.ReportFormAnswerTypes@SMALL_TEXT.equals(question.answerType)">
                                <s:property value="answerValue"/>
                            </s:if>
                            <s:if test="@tavant.twms.domain.customReports.ReportFormAnswerTypes@NUMBER.equals(question.answerType)">
                                <s:property value="answerValue"/>
                            </s:if>
                            <s:if test="@tavant.twms.domain.customReports.ReportFormAnswerTypes@LARGE_TEXT.equals(question.answerType)">
                                <s:property value="answerValue"/>
                            </s:if>
                            <s:if test="@tavant.twms.domain.customReports.ReportFormAnswerTypes@SINGLE_SELECT.equals(question.answerType)">
                                <s:property value="answerOptions[0].answerOption"/>
                                <s:if test="answerOptions[0].attachment!=null">
                                    <img src="<s:url value="downloadDocument.action?docId=%{answerOptions[0].attachment.id}"/>">
                                </s:if>
                            </s:if>
                            <s:if test="@tavant.twms.domain.customReports.ReportFormAnswerTypes@MULTI_SELECT.equals(question.answerType)">
                                <s:iterator value="answerOptions">
                                    <s:property value="answerOption"/>
                                    <s:if test="attachment!=null">
                                        <img src="<s:url value="downloadDocument.action?docId=%{attachment.id}"/>">
                                    </s:if>
                                    <br/>
                                </s:iterator>
                            </s:if>
                            <s:if test="@tavant.twms.domain.customReports.ReportFormAnswerTypes@SINGLE_SELECT_LIST.equals(question.answerType)">
                                <s:property value="answerOptions[0].answerOption"/>
                                <s:if test="answerOptions[0].attachment!=null">
                                    <img src="<s:url value="downloadDocument.action?docId=%{answerOptions[0].attachment.id}"/>">
                                </s:if>
                            </s:if>
                            <s:if test="@tavant.twms.domain.customReports.ReportFormAnswerTypes@MULTI_SELECT_LIST.equals(question.answerType)">
                                <s:iterator value="answerOptions">
                                    <s:property value="answerOption"/>
                                    <s:if test="attachment!=null">
                                        <img src="<s:url value="downloadDocument.action?docId=%{attachment.id}"/>">
                                    </s:if>
                                    <br/>
                                </s:iterator>
                            </s:if>
                            <s:if test="@tavant.twms.domain.customReports.ReportFormAnswerTypes@DATE.equals(question.answerType)">
                                <s:property value="answerDate"/>
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
						  <s:if test="question.postInstructions!=null">
						 <s:if test="question.postInstructions.attachment!=null">
							<tr> 
							<table>
								<td> <img src="<s:url value="downloadDocument.action?docId=%{question.postInstructions.attachment.id}"/>"></td>
								</table>
							</tr>   
						</s:if>						                    					                  
                    </s:if>                 
                    <tr>
                        <table class="grid" cellspacing="0" cellpadding="0">   
                    </tr>
                </s:iterator>
            </table>
        </td>
    </tr>
</table>
<br></br>
<!--	Need to put a pageBreak here-->
	
	<div class="buttonWrapperPrimary"><input type="button"
		name="Submit2232" value='<s:text name="button.common.cancel" />'
		class="buttonGeneric" onClick="window.close()" /> <input
		type="button" name="Submit2231"
		value="<s:text name="button.common.print"/>" class="buttonGeneric"
		onclick="window.print()"/></div>
</u:body>
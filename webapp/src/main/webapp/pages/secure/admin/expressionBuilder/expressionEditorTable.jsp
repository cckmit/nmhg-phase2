<%@ taglib prefix="s" uri="/struts-tags" %>
<div style="margin-left:3px">
<table  border="0" cellspacing="0" cellpadding="0"
        style="border:none;width:98%;">
        <tr><td>&nbsp;</td></tr>
    <tr>
        <td height="19" colspan="2" nowrap="nowrap" class="label">
            <%-- START ReadOnly-vs-Normal Rendering... --%>
            <s:if test="readOnly">
                <s:text name="label.manageBusinessCondition.conditionDescription"/>
            </s:if>
            <s:else>
                <s:text name="label.manageBusinessCondition.conditionName"/>
            </s:else>
            <%-- END ReadOnly-vs-Normal Rendering... --%>
        </td>
        
    </tr>
    <tr>
        <td width="70%" height="19" class="labelNormal">
            <%-- START ReadOnly-vs-Normal Rendering... --%>
            <s:if test="readOnly">
                <s:property value="domainPredicate.name"/>
            </s:if>
            <s:else>
                <s:textfield id="expressionNameField" name="domainPredicate.name" cssClass="txtField"  />
            </s:else>
            <%-- END ReadOnly-vs-Normal Rendering... --%>
        </td>
        <td width="30%"></td>
    </tr>
    <tr>
        <td height="3" colspan="2"></td>
    </tr>
</table>

<table width="100%" border="0" cellspacing="0" cellpadding="0" style="border:none">
    <tr>
        <td class="mainTitle" style="padding-top:10px;padding-bottom:10px">
            <%-- START ReadOnly-vs-Normal Rendering... --%>
            <s:if test="readOnly">
                <s:text name="label.manageBusinessCondition.businessCondition"/>
            </s:if>
            <s:else>
                <s:text name="label.manageBusinessCondition.createBusinessCondition"/>
            </s:else>
            <%-- END ReadOnly-vs-Normal Rendering... --%>
        </td>
    </tr>
	<tr><td class="borderTable">&nbsp;</td></tr>
	<s:if test="!readOnly || invertPredicate ">
    <tr>
        <td >
            <%-- START ReadOnly-vs-Normal Rendering... --%>          
            
            <s:if test="!readOnly">            	
                <s:checkbox name="invertPredicate" cssStyle="border:none" />
            </s:if>
            <%-- END ReadOnly-vs-Normal Rendering... --%>
            <span class="invertPredicate">
                <s:text name="label.manageBusinessCondition.invertPredicate" />
            </span>
        </td>
    </tr>
	</s:if>
    <tr>
        <td style="padding-top:10px;">
            <table class="grid" border="0" cellspacing="0" cellpadding="0">
                <tr>
                    <td style="color:#000000;padding-left:5px;">
                        <select style="width: 250px;" id="operator"/>
                    </td>
                    <td width="9%">
                        <%-- START ReadOnly-vs-Normal Rendering... --%>
                        <s:if test="!readOnly">
                            <div class="addButton" id="addButton"
                                 title="<s:text name="button.manageBusinessCondition.addNewExpressionRow"/>"/>
                        </s:if>
                        <%-- END ReadOnly-vs-Normal Rendering... --%>
                    </td>
                </tr>
                <tr>
                <td></td>
                <td align="right" width="9%">
								<div class="addButton" id="addButtonReadOnly" style="display:none;"
									title="<s:text name="button.manageBusinessCondition.addNewExpressionRow"/>" />
							</td>
				</tr>
					<tr>
                    <td class="nodeEditorContainer" colspan="2">
                        <table width="100%" border="0">
                            <tbody id="nodeEditorBody" class="nodeEditorBody">
                            </tbody>
                        </table>
                    </td>
                </tr>
            </table>
        </td>
    </tr>
</table>
</div>
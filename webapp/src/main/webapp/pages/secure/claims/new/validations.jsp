<%@ taglib prefix="s" uri="/struts-tags" %>
<%@taglib prefix="t" uri="twms"%><%@taglib prefix="u" uri="/ui-ext"%>


<div dojoType="dijit.layout.ContentPane" id="validations">
    <s:if test="messages.errors.size() > 0">
        <%-- TODO: Using an OL here had CSS issues --%>
        <ul id="errors" title="Errors">
            <s:iterator value="messages.errors" status="s">
                <li><s:property value="#s.count"/>. <s:property/></li>
            </s:iterator>
        </ul>
    </s:if>
    <s:if test="messages.warnings.size() > 0">
        <ul id="warnings" title="Warnings">
            <s:iterator value="messages.warnings" status="s">
                <li><s:property value="#s.count"/>. <s:property/></li>
            </s:iterator>
        </ul>
    </s:if>
</div>

<div dojoType="dijit.layout.ContentPane" id="applicable_policy">
<table width="50%">
    <tr>
        <td class="non_editable"><s:text name="label.newClaim.applicablePolicy"/>:</td>
        <td colspan="3"><s:property value="claim.applicablePolicy.code"/></td>
    </tr>
</table>
</div>


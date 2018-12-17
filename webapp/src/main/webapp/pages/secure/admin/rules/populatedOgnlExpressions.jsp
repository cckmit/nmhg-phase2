<%@ taglib prefix="s" uri="/struts-tags" %>
<html>
<head/>
<body >
    <table border="1" cellspacing="0">
        <s:iterator value="domainRules">
            <tr>
                <td>
                    <s:property value="ruleNumber"/>
                </td>
                <td>
                    <s:property value="businessUnitInfo.name"/>
                </td>
                <td>
                    <s:property value="ognlExpression"/>
                </td>
            </tr>
        </s:iterator>
    </table>
</body>
</html>
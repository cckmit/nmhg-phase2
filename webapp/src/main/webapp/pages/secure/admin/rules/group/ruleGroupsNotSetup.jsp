<%--
  Created by IntelliJ IDEA.
  User: vikas.sasidharan
  Date: October 8, 2008
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%@ taglib prefix="s" uri="/struts-tags" %>
<%@taglib prefix="t" uri="twms"%><%@taglib prefix="u" uri="/ui-ext"%>

<html>
  <head>
      <title>Rule Groups not setup!</title>
      <s:head theme="twms" />

      <u:stylePicker fileName="common.css"/>

  </head>
  <u:body>
    <u:actionResults />
    <s:form name="baseForm" id="baseForm" method="POST">
        <s:hidden name="context"/>
        <div id="actions" style="margin-left: 20px">
            <s:submit key="button.manageBusinessRule.createRuleGroup" cssClass="buttonGeneric"
                      action="setup_rule_group_creation" />
            <jsp:include page="../../../common/closeTab.jsp" />
        </div>
    </s:form>
  </u:body>
</html>
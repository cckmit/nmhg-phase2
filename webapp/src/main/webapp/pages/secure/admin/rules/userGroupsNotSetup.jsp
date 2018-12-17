<%--
  Created by IntelliJ IDEA.
  User: vikas.sasidharan
  Date: Jul 26, 2007
  Time: 3:36:49 PM
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%@ taglib prefix="s" uri="/struts-tags" %>
<%@taglib prefix="t" uri="twms"%><%@taglib prefix="u" uri="/ui-ext"%>

<html>
  <head>
      <title><s:text name="title.manageBusinessCondition.userGroupsNotSetup"/></title>
      <s:head theme="twms" />

      <u:stylePicker fileName="common.css"/>

      <script type="text/javascript">
        dojo.addOnLoad(function() {
            dojo.connect(dojo.byId("cancel"), "onclick", function() {
                closeTab(getTabHavingId(getTabDetailsForIframe().tabId));    
            });
        });
      </script>
  </head>
  <u:body>
    <u:actionResults />
    <s:form>
        <div id="actions" style="margin-left: 20px">
            <s:submit key="button.manageBusinessCondition.setupUserGroups"
                      cssClass="buttonGeneric" action="show_user_schemes" />
            <button id="cancel" class="buttonGeneric">
                <s:text name="button.common.cancel" />
            </button>
        </div>
    </s:form>
  </u:body>
</html>
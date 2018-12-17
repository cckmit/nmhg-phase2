<%@ page contentType="text/html" %>
<%@ taglib prefix="sd" uri="/struts-dojo-tags"%>
<%@ page pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="t" uri="twms" %>
<%@ taglib prefix="u" uri="/ui-ext" %>
<%@ taglib prefix="authz" uri="authz" %>
<%
	response.setHeader("Pragma", "no-cache");
	response.addHeader("Cache-Control", "must-revalidate");
	response.addHeader("Cache-Control", "no-cache");
	response.addHeader("Cache-Control", "no-store");
	response.setDateHeader("Expires", 0);
%>
 <s:select id="application" cssStyle="width:180px;"
                  name="marketingInformation.application"
                  list="listOfMarketApplications"
                  headerKey="null" headerValue="%{getText('label.common.selectHeader')}"
                  value="%{marketingInformation.application.id.toString()}"
                  listKey="id" listValue="title"/> 
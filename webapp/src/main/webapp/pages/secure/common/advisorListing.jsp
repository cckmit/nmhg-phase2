<%--
  Created by IntelliJ IDEA.
  User: irdemo
  Date: Nov 5, 2009
  Time: 5:52:45 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@taglib prefix="t" uri="twms"%>
<%@taglib prefix="u" uri="/ui-ext"%>
<%@page pageEncoding="UTF-8" %>
<%response.setHeader( "Pragma", "no-cache" );
response.addHeader( "Cache-Control", "must-revalidate" );
response.addHeader( "Cache-Control", "no-cache" );
response.addHeader( "Cache-Control", "no-store" );
response.setDateHeader("Expires", 0); %>
<html>
<u:stylePicker fileName="yui/reset.css" common="true"/>
    <s:head theme="twms"/>
    <u:stylePicker fileName="common.css"/>
    <u:stylePicker fileName="form.css"/>
    <u:stylePicker fileName="claimForm.css"/>
    <u:stylePicker fileName="base.css"/>
    <u:stylePicker fileName="multiCar.css"/>
  <head><title>Simple jsp page</title></head>
  <u:body >


<div dojoType="dijit.layout.LayoutContainer" style="width: 100%; height: 100%; overflow:auto" >
<div dojoType="dijit.layout.ContentPane" layoutAlign="client">
    <s:iterator value="claimWithAdvisors">
        <table class="grid">
            <tr>
                <td><s:property value="%{key}" /></td>
                <td><s:property value="%{value}"/></td>
            </tr>
        </table>

    </s:iterator>
    </div></div></u:body>
</html>
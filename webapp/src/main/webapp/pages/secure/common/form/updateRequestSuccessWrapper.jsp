<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
        "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<%--

   Copyright (c)2006 Tavant Technologies
   All Rights Reserved.

   This software is furnished under a license and may be used and copied
   only  in  accordance  with  the  terms  of such  license and with the
   inclusion of the above copyright notice. This software or  any  other
   copies thereof may not be provided or otherwise made available to any
   other person. No title to and ownership of  the  software  is  hereby
   transferred.

   The information in this software is subject to change without  notice
   and  should  not be  construed as a commitment  by Tavant Technologies.

--%>
<%@ page import="tavant.twms.web.actions.CRUDFormAction" %>
<%@ page import="tavant.twms.annotations.form.ActionType" %>
<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="t" uri="twms"%><%@taglib prefix="u" uri="/ui-ext"%>
<%response.setHeader( "Pragma", "no-cache" );
response.addHeader( "Cache-Control", "must-revalidate" );
response.addHeader( "Cache-Control", "no-cache" );
response.addHeader( "Cache-Control", "no-store" );
response.setDateHeader("Expires", 0); %>

<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
    <title><s:text name="%{pageTitle}"/></title>
    <s:head theme="twms"/>
    <u:stylePicker fileName="yui/reset.css" common="true"/>
    <u:stylePicker fileName="base.css"/>
    <u:stylePicker fileName="common.css"/>
</head>
<u:body >
    <div dojoType="dijit.layout.LayoutContainer" style="width: 100%; height: 100%">
        <div dojoType="dijit.layout.ContentPane" layoutAlign="client">
        <jsp:include page="common/actionResults.jsp"/>
            <s:form action="%{updateSubmitAction}" validate="%{ajaxValidatable}" theme="twms" method="POST">
                <jsp:include page="<%=request.getAttribute(CRUDFormAction.SUCCESS_WRAPPER_ATTRIBUTE).toString() %>"/>
                <div class="formButtonContainer">
                    <jsp:include page="common/cancelButton.jsp"/>
                    <% request.setAttribute(CRUDFormAction.DELETE_BUTTON_ACTION,
                                request.getAttribute(ActionType.DELETE_SUBMIT.toString()));%>
                    <jsp:include page="common/deleteButton.jsp"/>
                    <% request.setAttribute(CRUDFormAction.DEACTIVATE_BUTTON_ACTION,
                            request.getAttribute(ActionType.DEACTIVATE_SUBMIT.toString()));%>
                    <jsp:include page="common/deactivateButton.jsp"/>
                    <% request.setAttribute(CRUDFormAction.ACTIVATE_BUTTON_ACTION,
                            request.getAttribute(ActionType.ACTIVATE_SUBMIT.toString()));%>
                    <jsp:include page="common/activateButton.jsp"/>
                    <% request.setAttribute(CRUDFormAction.SAVE_BUTTON_ACTION,
                            request.getAttribute(ActionType.UPDATE_SUBMIT.toString()));%>
                    <jsp:include page="common/saveButton.jsp"/>
                </div>
            </s:form>
        </div>
    </div>
</u:body>
</html>
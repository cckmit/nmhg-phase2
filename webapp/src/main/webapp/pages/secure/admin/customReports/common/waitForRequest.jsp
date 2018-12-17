<%--
  Created by IntelliJ IDEA.
  User: pradyot.rout
  Date: Dec 19, 2008
  Time: 11:32:38 AM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html" %>
<%@ page pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="t" uri="twms" %>
<%@ taglib prefix="u" uri="/ui-ext" %>

<div dojoType="twms.widget.Dialog" id="waitForRequest"
     title="<s:text name="label.customReport.pleaseWait" />">
    <div dojoType="dijit.layout.LayoutContainer"
         style="background: #F3FBFE; border: 1px solid #EFEBF7;overflow: auto;">
        <div dojoType="dojox.layout.ContentPane" layoutAlign="top">
            <div class='loadingLidThrobber'>
                <div class='requestLidThrobberContent'>
                   <s:text name="label.customReport.waitMessage"/>
                </div>
            </div>
        </div>
    </div>
</div>


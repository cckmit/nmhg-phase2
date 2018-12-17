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

<%@ taglib prefix="s" uri="/struts-tags" %>
<%@taglib prefix="t" uri="twms"%><%@taglib prefix="u" uri="/ui-ext"%>

<s:if test="messages.hasErrors() || messages.hasWarnings()">
    <div id="validation_messages">
        <s:if test="messages.hasErrors()">
            <div id="validation_errors" class="validation_wrapper">
                <h4><s:text name="label.common.errors"/>:</h4>
                <ol>
                    <s:iterator value="messages.errors">
                        <li><s:property/></li>
                    </s:iterator>
                </ol>
                <hr/>
            </div>
        </s:if>

        <s:if test="messages.hasWarnings()">
            <div id="validation_warnings" class="validation_wrapper">
                <h4><s:text name="message.common.warning"/>:</h4>
                <ol>
                    <s:iterator value="messages.warnings">
                        <li><s:property/></li>
                    </s:iterator>
                </ol>
                <hr/>
            </div>
        </s:if>
    </div>
</s:if>

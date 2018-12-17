<%@ page contentType="text/html" %>
<%@ page pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="t" uri="twms" %>
<%@ taglib prefix="u" uri="/ui-ext" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">

<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <title>
        <s:text name="accordion_jsp.accordionPane.dealerUserMgmt"/>
    </title>
    <s:head theme="twms"/>
    <u:stylePicker fileName="form.css"/>
    <u:stylePicker fileName="warrantyForm.css"/>
    <u:stylePicker fileName="common.css"/>
    <u:stylePicker fileName="base.css"/>

</head>

<u:body>
    <script type="text/javascript">
        dojo.require("dijit.layout.LayoutContainer");
    </script>
    <div dojoType="dijit.layout.LayoutContainer"
         style="width: 100%; height: 100%; background: white; overflow: auto"
         align="center">
        <s:if test="userCreated">
            <h3><s:text name="label.dealerUser.success"/></h3>
        </s:if>
        <s:else>
            <h3><s:text name="label.dealerUser.update.success"/></h3>    
        </s:else>

        <div id="submit" align="center">
            <input id="cancel_btn" class="buttonGeneric" type="button" value="<s:text name='button.common.close'/>"
                   onclick="javascript:closeTab(getTabHavingLabel(getMyTabLabel()));"/>
        </div>
    </div>
</u:body>
</html>			
		
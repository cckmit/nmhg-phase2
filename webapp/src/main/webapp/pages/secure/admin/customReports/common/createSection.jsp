<%--
  Created by IntelliJ IDEA.
  User: pradyot.rout
  Date: Dec 11, 2008
  Time: 12:59:42 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html" %>
<%@ page pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="t" uri="twms" %>
<%@ taglib prefix="u" uri="/ui-ext" %><head>
    <title>Create a section Page</title>
    <u:stylePicker fileName="common.css"/>
    <u:stylePicker fileName="form.css"/>
</head>
<script type="text/javascript">
    dojo.require("twms.widget.TitlePane");
    dojo.require("dijit.layout.LayoutContainer");
    dojo.require("dojox.layout.ContentPane");
    dojo.require("dijit.layout.ContentPane")
</script>

<a id="create_section_link" class="link">
    <s:text name="label.customReport.createSection"/>
</a>

<div id="create_section_div" style="display:none">
    <s:form action="create_section" id="createSectionForm">
        <s:if test="customReport.id!=null">
            <s:hidden name="customReport" value="%{customReport.id}" />
        </s:if>
        <div dojoType="dojox.layout.ContentPane" layoutAlign="client"
             executeScripts="true" id="create_section_content">
            <table width="100%" border="0" cellspacing="0" cellpadding="0" class="grid">
                <tbody>
                    <tr>
                        <td class="labelStyle" width="20%" nowrap="nowrap"><s:text name="label.customReport.sectionName"/></td>
                        <td><s:textfield name="section.name" value="%{section.name}" id="section_name"
                        onblur="updateForm1('section_name')"/></td>
                    </tr>
                    <tr>
                        <td class="labelStyle" width="20%" nowrap="nowrap"><s:text name="label.customReport.sectionOrder"/></td>
                        <td><s:textfield name="section.order" value="%{section.order}" id="section_order" 
                        onblur="updateForm1('section_order')" size="3" maxLength="3"/></td>
                    </tr>
                </tbody>
            </table>
        </div>
        <div align="center" style="margin-top:10px;margin-bottom:10px;">
            <input class="buttonGeneric" type="button" value="<s:text name='button.common.continue'/>" onclick="submitCreateSectionForm()"/>
        </div>
    </s:form>
</div>
<script type="text/javascript">
    dojo.addOnLoad(function() {
        dojo.connect(dojo.byId("create_section_link"), "onclick", function() {
            dojo.html.hide(dojo.byId("create_section_link"));
            dojo.html.show(dojo.byId("create_section_div"));
            toggleSubmitButtons(true);
        });
    });
 </script>
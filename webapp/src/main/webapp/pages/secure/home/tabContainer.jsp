<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="t" uri="twms"%><%@taglib prefix="u" uri="/ui-ext"%>
<%@taglib prefix="authz" uri="authz"%>
<%response.setHeader( "Pragma", "no-cache" );
response.addHeader( "Cache-Control", "must-revalidate" );
response.addHeader( "Cache-Control", "no-cache" );
response.addHeader( "Cache-Control", "no-store" );
response.setDateHeader("Expires", 0); %>

<script type="text/javascript">
    dojo.require("twms.widget.TabContainer");
    dojo.require("dojo.parser");
</script>
<%--twms.widget.TabContainer is required in Tabs.js... and that is included in the home page...--%>

<div dojoType="twms.widget.TabContainer" id="tabPane" tabPosition="top"
     region="center"  sizeShare="72" class="welcomeBGForTabContainer">

     <div dojoType="dijit.layout.ContentPane" closable="false" selected="true" title="<s:text name="home_jsp.tabs.home"/>">
        <!-- Dummy IFrame used to avoid the "dropdown selects ignore z-index in IE" issue. This
             IFrame should have a z-index value higher than 0, since that is the z-index value
             we set on the hidden tabs.
        -->
        <iframe frameborder="0" style="position: absolute; width: 100%; height:100%; z-index: -1">
        </iframe>
        <jsp:include flush="true" page="main.jsp" />
     </div>
</div>
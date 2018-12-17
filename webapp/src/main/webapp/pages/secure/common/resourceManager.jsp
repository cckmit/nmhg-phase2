<%@taglib prefix="t" uri="twms"%><%@taglib prefix="u" uri="/ui-ext"%>
<%response.setHeader( "Pragma", "no-cache" );
response.addHeader( "Cache-Control", "must-revalidate" );
response.addHeader( "Cache-Control", "no-cache" );
response.addHeader( "Cache-Control", "no-store" );
response.setDateHeader("Expires", 0); %>
<script type="text/javascript">
    dojo.require("twms.widget.Select");
    var RESOURCE_MANAGER_CSS_CLASSES = {
        COMPONENT_HOLDER : "commonResource_component",
        ACCEPT_BUTTON : "commonResource_accept",
        RESET_BUTTON : "commonResource_reset"
    };
</script>
<script type="text/javascript" src="scripts/ui-ext/common/ResourceManager.js"></script>
<style type="text/css">
    img.commonResourceButton {
       margin-left : 2px; 
    }
    span.commonResourceWrapper {
        vertical-align: bottom;
    }
    span.commonResourceRoot {
        height : 15px;
        padding : 1px;
    }
</style>
<u:jsVar varName="commonResource_componentWrapper">
    <span class="commonResourceRoot">
        <span class="commonResource_component"></span>
        <span class="commonResourceWrapper"><%--TODO : i18N me!!!--%>
            <img src="image/correctIcon.gif" title="Accept" class="commonResource_accept commonResourceButton"/>
            <img src="image/crossIcon.gif" title="Reset" class="commonResource_reset commonResourceButton"/>
        </span>
    </span>
</u:jsVar>
<body class="claro">
<!-- This inline lid is used only by those pages which don't open within a tab, for eg. the Home page.
        We use an inline lid since:
    i)  we obviously cant use dojo coz it wont even be loaded yet!
    ii) We want to reduce as much JS code for the Lid, since that would increase the appearance time of the lid.
-->
<div class="startupLid" id="startupLid" style="display:none">
    <div class="startupLidIndication">
        <div class="startupLidMessage">Please Wait...</div>
    </div>
</div>
<script type="text/javascript">
    var docBody = document.body;
    // backup original overflow value.
    var bodyStyle = docBody.style;
    docBody._originalOverflow = bodyStyle.overflow ? bodyStyle.overflow : "";
    bodyStyle.overflow = "hidden";

    var __iframe = window.frameElement;
    var __tabLidActive = __iframe && twms.util.hasLid(__iframe.parentNode);

    if(__tabLidActive) {
        __iframe.__twmsPageLoaded = false;
    } else {
        <#-- we are not inside an iframe, or we are in some flow where the tab manager is not in the picture. Hence,
                show the lid ourselves. -->
        dojo.html.show(dojo.byId("startupLid"));
    }
    
<#if parameters.smudgeAlert>
    // smudge alerts...
    window.alert = function() {};
</#if>
</script>
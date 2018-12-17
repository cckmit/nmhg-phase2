<script type="text/javascript">
    <#if parameters.type?if_exists == "date">
        dojo.require("twms.widget.DateTextBox");
    <#else>
        dojo.require("twms.widget.TimeTextBox");
    </#if>
</script>
<div <#rt/>
  <#if parameters.type?if_exists == "date">
    dojoType="twms.widget.DateTextBox" <#rt/>
  <#else>
    dojoType="twms.widget.TimeTextBox" <#rt/>
  </#if>
  <#if parameters.id?if_exists != "">
    id="${parameters.id?html}" <#rt/>
  </#if>
  <#if parameters.name?if_exists != "">
    name="${parameters.name?html}" <#rt/>
  </#if>
  constraints="{datePattern:'${stack.findValue("dateFormatForLoggedInUser")}'}" <#rt/>
  <#if parameters.toggleType?if_exists != "">
    containerToggle="${parameters.toggleType?html}" <#rt/>
  </#if>
  <#if parameters.required?exists>
    required="${parameters.required?string?html}"<#rt/>
  </#if>
  <#if parameters.toggleDuration?exists>
    containerToggleDuration="${parameters.toggleDuration?string?html}" <#rt/>
  </#if>
  <#if parameters.nameValue?if_exists != "">
    formattedValue="${parameters.nameValue?html}"
  </#if>
  <#include "/${parameters.templateDir}/simple/scripting-events.ftl" />
>
</div>

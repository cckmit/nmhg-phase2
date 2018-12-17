<#include "/${parameters.templateDir}/twms/twms_common_setHasErrors.ftl" />
<#if hasFieldErrors>
    <#if parameters.cssClass?exists>
        ${tag.addParameter('cssClass', "erroneousField ${parameters.cssClass}")}
    <#else>
        ${tag.addParameter('cssClass', "erroneousField")}
    </#if>
</#if>
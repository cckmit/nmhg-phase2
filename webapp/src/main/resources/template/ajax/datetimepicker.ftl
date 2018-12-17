<!--<#include "/${parameters.templateDir}/twms/twms_common_setHasErrors.ftl" />-->
<#include "/${parameters.templateDir}/simple/datetimepicker.ftl" />
<#if parameters.required?default(false)>
    <span class="required">*</span><#t/>
</#if>
<#if hasFieldErrors>
<!--<#include "/${parameters.templateDir}/twms/twms_common_staticErrorMessageFragment.ftl" />-->
</#if>
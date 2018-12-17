<#if parameters.required?default(false)>
    <span class="required">*</span><#t/>
</#if>

<#if hasFieldErrors>
<#include "/${parameters.templateDir}/twms/twms_common_staticErrorMessageFragment.ftl" />
<script type="text/javascript">
dojo.addOnLoad(function() { dojo.byId("${parameters.id?html}").touched = true; });
</script>
</#if>
<#if parameters.validate?exists>
<script type="text/javascript" src="${base}/scripts/patched/struts/ajax/validationClient.js"></script>
<script type="text/javascript" src="${base}/dwr/interface/validator.js"></script>
<script type="text/javascript" src="${base}/dwr/engine.js"></script>
<script type="text/javascript" src="${base}/scripts/patched/struts/ajax/validation.js"></script>
<script type="text/javascript" src="${base}/tagsupport/validation.js"></script>
</#if>
<form<#rt/>
<#if parameters.namespace?exists>
 namespace="${parameters.namespace?html}"<#rt/>
</#if>
<#if parameters.id?exists>
 id="${parameters.id?html}"<#rt/>
</#if>
<#if parameters.name?exists>
 name="${parameters.name?html}"<#rt/>
</#if>
<#if parameters.action?exists>
 action="${parameters.action?html}"<#rt/>
</#if>
<#if parameters.target?exists>
 target="${parameters.target?html}"<#rt/>
</#if>
<#if parameters.method?exists>
 method="${parameters.method?html}"<#rt/>
<#else>
 method="post"<#rt/>
</#if>
<#if parameters.enctype?exists>
 enctype="${parameters.enctype?html}"<#rt/>
</#if>
<#if parameters.cssClass?exists>
 class="${parameters.cssClass?html}"<#rt/>
</#if>
<#if parameters.acceptcharset?exists>
 accept-charset="${parameters.acceptcharset?html}"<#rt/>
</#if>
<#if parameters.cssStyle?exists>
 style="${parameters.cssStyle?html}"<#rt/>
</#if>
 ${tag.addParameter("ajaxSubmit", "true")}
>
<script type="text/javascript">
var __validationErrorIcon = "${base}/tagsupport/alerts.gif";
</script>
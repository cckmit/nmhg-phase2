<script type="text/javascript">
dojo.addOnLoad(function() {
 var ${parameters.id?html}_showHide = dojo.byId("${parameters.id?html}");
 <#if parameters.showOn?exists>
  dojo.subscribe("${parameters.showOn}", this, function() {
  <#if (parameters.tagType == "tr")>
   ${parameters.id?html}_showHide.style.display = "table-row";
  <#elseif (parameters.tagType == "td")>
   ${parameters.id?html}_showHide.style.display = "table-cell";
  <#else>
   ${parameters.id?html}_showHide.style.display = "block";
  </#if>
   <#if parameters.publishOnShow?exists>
    dojo.publish("${parameters.publishOnShow}", [{target: ${parameters.id?html}_showHide, type: "show"}]);
   </#if>
  });
 </#if>
 <#if parameters.hideOn?exists>
  dojo.subscribe("${parameters.hideOn}", this, function() {
   ${parameters.id?html}_showHide.style.display = "none";
   <#if parameters.publishOnHide?exists>
    dojo.publish("${parameters.publishOnHide}", [{target: ${parameters.id?html}_showHide, type: "hide"}]);
   </#if>
  });
 </#if>
});
</script>
<<#rt/>
<#if parameters.tagType?exists>
${parameters.tagType}<#rt/>
<#else>
div<#rt/>
</#if>
 id="${parameters.id?html}"<#rt/>
<#if parameters.cssClass?exists>
 class="${parameters.cssClass?html}"<#rt/>
</#if>
<#if (parameters.visibleInitially?exists && (parameters.visibleInitially == false))>
 style="display: none;"<#rt/>
<#else>
 <#if (parameters.tagType == "tr")>
   style="display: table-row;"<#rt/>
  <#elseif (parameters.tagType == "td")>
   style="display: table-cell;"<#rt/>
  <#else>
   style="display: block;"<#rt/>
  </#if>
</#if>
>
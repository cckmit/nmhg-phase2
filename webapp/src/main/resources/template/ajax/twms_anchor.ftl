<#if ((parameters.onclick?exists) || (parameters.publishOnClick?exists))>
<script type="text/javascript">
dojo.addOnLoad(
 function() {
  dojo.connect(dojo.byId("${parameters.id?html}"), "onclick", function(event) {
   <#if parameters.onclick?exists>
    ${parameters.onclick}(event);
   </#if>
   <#if parameters.publishOnClick?exists>
    dojo.publish("${parameters.publishOnClick}", [event]);
   </#if>
   <#if parameters.url?exists>
    window.location.href="${parameters.url?html}";
   </#if>
  });
 }
);
</script>
</#if>
<${parameters.tagType} id="${parameters.id?html}"<#rt/>
<#if parameters.cssClass?exists>
 class="${parameters.cssClass}"<#rt/>
</#if>
><#rt/>
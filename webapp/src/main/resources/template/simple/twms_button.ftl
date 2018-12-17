<#if ((parameters.onclick?exists || parameters.publishOnClick?exists) && !(parameters.readOnly?exists && parameters.readOnly == true))>
<script type="text/javascript">
 dojo.addOnLoad(function() {
  dojo.connect(dojo.byId("${parameters.id?html}"),"onclick",function(event) {
   <#if parameters.onclick?exists>
    ${parameters.onclick}(event);
   </#if>
   <#if parameters.publishOnClick?exists>
    event.id = "${parameters.id?html}";
    dojo.publish("${parameters.publishOnClick}",[event]);
   </#if>
  });
 });
</script>
</#if>
<#if !(parameters.readOnly?exists && (parameters.readOnly == true))>
<button<#rt/>
<#if parameters.type?exists>
 type="${parameters.type?html}"<#rt/>
</#if>
<#if parameters.id?exists>
 id="${parameters.id?html}"<#rt/>
</#if>
<#if parameters.name?exists>
 name="${parameters.name?html}"<#rt/>
</#if>
<#if parameters.value?exists>
 value="${parameters.value?html}"<#rt/>
<#elseif parameters.nameValue?exists>
 value="${parameters.nameValue?html}"<#rt/>
</#if>
<#if parameters.cssClass?exists>
 class="${parameters.cssClass?html}"<#rt/>
</#if>
><#rt/>
<#if parameters.label?exists>
<@s.text name="${parameters.label}"/><#rt/>
</#if>
</button>
</#if>
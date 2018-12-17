<script type="text/javascript">
<#if (parameters.onchange?exists || parameters.publishOnChange?exists)>
 var ${parameters.id?html}_record = "";
 function ${parameters.id?html}_compairer(event) {
  if(${parameters.id?html}_record != event.target.value) {
   ${parameters.id?html}_record = event.target.value;
   <#if parameters.onchange?exists>
    ${parameters.onchange?html}(event);
   </#if>
   <#if parameters.publishOnChange?exists>
    event.id = "${parameters.id?html}";
    dojo.publish("${parameters.publishOnChange}", [event]);
   </#if>
  }
 }
</#if>
<#if (parameters.onchange?exists || parameters.publishOnChange?exists)>
 dojo.addOnLoad(function() {
  ${parameters.id?html}_domNode = dojo.byId("${parameters.id?html}");
  ${parameters.id?html}_record = ${parameters.id?html}_domNode.value;
  bindOnChange(${parameters.id?html}_domNode, ${parameters.id?html}_compairer, new Array("onkeyup", "timebased")); 
 });
</#if>
</script>
<input type="hidden"<#rt/>
 name="${parameters.name?default("")?html}"<#rt/>
<#if parameters.id?exists>
 id="${parameters.id?html}"<#rt/>
</#if>
<#if parameters.nameValue?exists>
 value="<@s.property value="parameters.nameValue"/>"<#rt/>
</#if>
/>